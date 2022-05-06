package com.les.ls.test;

import com.les.ls.utils.DateUtils;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {

    // 获取逻辑处理器数量,我的电脑是8
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        System.out.println(NCPU);
        long[] array = new long[200000000];
        for (int i = 0; i < array.length; i++) {
            array[i] = random();
        }
        long expectedSum = 0;
        Instant start =  Instant.now();
        // 创建2000个随机数组成的数组:
        for (long l : array) {
            expectedSum += l;
        }
        Instant end =  Instant.now();
        long time =  DateUtils.timeInterval(start,end);
        System.out.println("Expected sum: " + expectedSum + "time:" + time);
        // fork/join:
        ForkJoinTask<Long> task = new SumTask(array, 0, array.length);
        long startTime = System.currentTimeMillis();
        Long result = new ForkJoinPool(NCPU).submit(task).get();
        long endTime = System.currentTimeMillis();
        System.out.println("Fork/join sum: " + result + " in " + (endTime - startTime) + " ms.");
    }

    static Random random = new Random(0);

    static long random() {
        return random.nextInt(10000);
    }
}

class SumTask extends RecursiveTask<Long> {

    static final int THRESHOLD = 100000000;
    long[] array;
    int start;
    int end;

    SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // 如果任务足够小,直接计算:
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += this.array[i];
                // 故意放慢计算速度:
                /*try {
                    //TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                }*/
            }
            System.out.println("return sum " + sum);
            System.out.println(" start = " + start + " end = " + end );
            return sum;
        }
        // 任务太大,一分为二:
        int middle = (end + start) / 2;
        System.out.println(String.format("split %d~%d ==> %d~%d, %d~%d", start, end, start, middle, middle, end));
        SumTask subtask1 = new SumTask(this.array, start, middle);
        SumTask subtask2 = new SumTask(this.array, middle, end);
        invokeAll(subtask1, subtask2);
        Long subresult1 = subtask1.join();
        Long subresult2 = subtask2.join();
        Long result = subresult1 + subresult2;
        System.out.println("result = " + subresult1 + " + " + subresult2 + " ==> " + result);
        return result;
    }
}
