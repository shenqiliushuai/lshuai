package com.les.ls.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FutureTest {

    static ExecutorService executorService = new ThreadPoolExecutor(
            2,
            2,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(),
            new CustomThreadPoolError());

    public static void main(String[] args) throws Exception {
        List<TestInterface> list = new ArrayList<>();
        TestInterface impl1 = new TestImpl1();
        TestInterface impl2 = new TestImpl2();
        list.add(impl1);
        list.add(impl2);
        List<Future<List<String>>> taskResult = new ArrayList<>();
        CountDownLatch downLatch = new CountDownLatch(list.size());
        for (TestInterface testInterface : list) {
            try {
                Future<List<String>> listFuture = executorService.submit(new CustomThread(testInterface));
                taskResult.add(listFuture);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                downLatch.countDown();
            }
        }
        downLatch.await();
        for (Future<List<String>> future : taskResult) {
            List<String> strings = null;
            try {
                strings = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            strings.forEach(System.out::println);
        }
        executorService.shutdownNow();
    }


}

class CustomThread implements Callable<List<String>> {
    private TestInterface testInterface;

    CustomThread(TestInterface testInterface) {
        this.testInterface = testInterface;
    }

    @Override
    public List<String> call() throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new CustomThreadError() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                super.uncaughtException(t, e);
                if (e.getClass() == ArithmeticException.class) {
                    System.out.println("忽略！");
                }
            }
        });
        return testInterface.test();
    }
}

abstract class CustomThreadError implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getId() + "发生了异常！");
    }
}


class CustomThreadPoolError implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(r.getClass().getName() + "发生了异常！触发线程拒绝策略...");
    }
}

interface TestInterface {
    List<String> test();
}

class TestImpl1 implements TestInterface {
    @Override
    public List<String> test() {
        List<String> list = new ArrayList<>();
        list.add(this.getClass().getName());
        return list;
    }
}

class TestImpl2 implements TestInterface {
    @Override
    public List<String> test() {
        List<String> list = new ArrayList<>();
        list.add(this.getClass().getName());
        int a = 1 / 0;
        return list;
    }
}