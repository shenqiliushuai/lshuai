package com.les.ls.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 随机读写 添加缓存 高速读取写入工具类 参考IBM技术文章
 *
 * @author lshuai
 */
public class BufferedRandomAccessFileOptimize extends RandomAccessFile {
    /**
     * 常用缓冲字节长度
     */
    private static final int
            BUFFER_BIT_LENGTH_1024 = 1024,
            BUFFER_BIT_LENGTH_512 = 512,
            BUFFER_BIT_LENGTH_128 = 128,
            BUFFER_BIT_LENGTH_64 = 64,
            BUFFER_BIT_LENGTH_8 = 8;
    /**
     * RandomAccessFile 读取模式
     */
    private static final String
            R = "r",
            RW = "rw",
            RWS = "rws",
            RWD = "rwd";
    /**
     * 缓冲数组
     */
    protected byte[] buffer;
    /**
     * 缓冲区数组大小
     */
    protected int bufferBitLength;
    /**
     * 缓冲区大小（缓冲数组2倍）
     */
    protected int bufferSize;
    /**
     * 缓冲指针位置
     */
    protected long bufferMask;
    /**
     * 缓冲区中是否存在脏数据
     */
    protected boolean bufferDirty;
    /**
     * 缓冲区已使用大小
     */
    protected int bufferUsedSize;
    /**
     * 当前读取文件指针
     */
    protected long currentPosition;
    /**
     * 缓冲区开始位置
     */
    protected long bufferStartPosition;
    /**
     * 缓冲区结束位置
     */
    protected long bufferEndPosition;
    /**
     * 文件结束指针
     */
    protected long fileEndPosition;
    /**
     * 文件名
     */
    protected String filename;
    /**
     * 文件大小（byte）
     */
    protected long initFileLength;

    public BufferedRandomAccessFileOptimize(String name, String mode, int bufferBitLength) throws IOException {
        super(name, mode);
        this.init(name, bufferBitLength);
    }

    private void init(String name, int bufferBitLength) throws IOException {
        if (bufferBitLength < 0) {
            throw new IllegalArgumentException("缓冲区设置错误！");
        }
        this.filename = name;
        this.initFileLength = super.length();
        this.fileEndPosition = this.initFileLength - 1;
        this.currentPosition = super.getFilePointer();
        this.bufferBitLength = bufferBitLength;
        this.bufferSize = 1 << bufferBitLength;
        this.buffer = new byte[this.bufferSize];
        this.bufferMask = ~this.bufferSize;
        this.bufferDirty = false;
        this.bufferUsedSize = 0;
        this.bufferStartPosition = -1;
        this.bufferEndPosition = -1;
    }

    public long length() {
        return Math.max(this.fileEndPosition + 1, this.initFileLength);
    }

    public long getFilePointer() {
        return this.currentPosition;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }


    /**
     * 读取
     *
     * @param bytes 数据被读入的缓冲区
     * @param off   数组param bytes中写入数据的起始偏移量
     * @param len   读取的最大字节数
     * @return 读取到缓冲区的总字节数，如果没有更多的数据或者已经到达文件的末尾则返回 -1。
     * @throws IOException 如果发生I/O错误
     */
    public int read(byte[] bytes, int off, int len) throws IOException {
        long readendpos = this.currentPosition + len - 1;
        if (readendpos <= this.bufferEndPosition && readendpos <= this.fileEndPosition) {
            //读入缓冲区
            System.arraycopy(this.buffer, (int) (this.currentPosition - this.bufferStartPosition), bytes, off, len);
        } else {
            if (readendpos > this.fileEndPosition) {
                // 如果读取的数组大小大于缓冲区大小 读取文件中的bytes[]部分
                len = (int) (this.length() - this.currentPosition + 1);
            }
            super.seek(this.currentPosition);
            len = super.read(bytes, off, len);
            readendpos = this.currentPosition + len - 1;
        }
        this.seek(readendpos + 1);
        return len;
    }

    /**
     * 从指定的字节数组param bytes中写入param len字节，从这个文件的偏移量param off开始
     *
     * @param bytes 数据
     * @param off   数组param bytes中写入数据的起始偏移量
     * @param len   要写入的字节数
     * @throws IOException 如果发生I/O错误
     */
    public void write(byte[] bytes, int off, int len) throws IOException {
        long writeEndPosition = this.currentPosition + len - 1;
        if (writeEndPosition <= this.bufferEndPosition) {
            //写入缓冲区
            System.arraycopy(bytes, off, this.buffer, (int) (this.currentPosition - this.bufferStartPosition), len);
            this.bufferDirty = true;
            this.bufferUsedSize = (int) (writeEndPosition - this.bufferStartPosition + 1);
        } else {
            //如果当前写入的数据bytes[]不在当前缓存中
            super.seek(this.currentPosition);
            super.write(bytes, off, len);
        }
        if (writeEndPosition > this.fileEndPosition) {
            this.fileEndPosition = writeEndPosition;
        }
        this.seek(writeEndPosition + 1);
    }

    /**
     * 设置文件指针的偏移量，从文件指针开始测量文件，在它发生下一个读或写。
     * 偏移量可能是设置在文件的末尾以外。
     * 设置结束后的偏移量不改变文件的长度。
     * 文件长度将仅在偏移量设置到超过结束后写入更改的文件。
     *
     * @param pos position
     * @throws IOException IOException
     */
    public void seek(long pos) throws IOException {
        if ((pos < this.bufferStartPosition) || (pos > this.bufferEndPosition)) {
            this.flushBuffer();
            if ((pos >= 0) && (pos <= this.fileEndPosition) && (this.fileEndPosition != 0)) {
                this.bufferStartPosition = pos & this.bufferMask;
                this.bufferUsedSize = this.fillBuffer();
            } else if (((pos == 0) && (this.fileEndPosition == 0)) || (pos == this.fileEndPosition + 1)) {
                this.bufferStartPosition = pos;
                this.bufferUsedSize = 0;
            }
            this.bufferEndPosition = this.bufferStartPosition + this.bufferSize - 1;
        }
        this.currentPosition = pos;
    }

    /**
     * 刷新缓冲区
     *
     * @throws IOException IOException
     */
    private void flushBuffer() throws IOException {
        if (this.bufferDirty) {
            if (super.getFilePointer() != this.bufferStartPosition) {
                super.seek(this.bufferStartPosition);
            }
            super.write(this.buffer, 0, this.bufferUsedSize);
            this.bufferDirty = false;
        }
    }

    /**
     * 填充缓冲区
     *
     * @return 缓冲区已使用大小
     * @throws IOException IOException
     */
    private int fillBuffer() throws IOException {
        super.seek(this.bufferStartPosition);
        this.bufferDirty = false;
        return super.read(this.buffer);
    }

    public void close() throws IOException {
        this.flushBuffer();
        super.close();
    }

    public static void main(String[] args) throws InterruptedException {
        Instant readStart = Instant.now();
        long fileLength = 0;
        List<byte[]> list = new ArrayList<>();
        try (BufferedRandomAccessFileOptimize bufferedRandomAccessFileOptimize =
                     new BufferedRandomAccessFileOptimize("I:\\SoapUI-5.4.0.zip", R, BUFFER_BIT_LENGTH_1024)) {
            fileLength = bufferedRandomAccessFileOptimize.initFileLength;
            byte[] bytes = new byte[BUFFER_BIT_LENGTH_1024];
            while (bufferedRandomAccessFileOptimize.read(bytes) != -1) {
                list.add(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Instant readEnd = Instant.now();
        System.out.printf("file size %s (byte) read speed %s ms ...%n", fileLength, Duration.between(readStart, readEnd).toMillis());

        Thread.sleep(5000);

        Instant writeStart = Instant.now();
        try (BufferedRandomAccessFileOptimize bufferedRandomAccessFileOptimize =
                     new BufferedRandomAccessFileOptimize("I:\\SoapUI-5.4.0.zip.optimize.1", RW, BUFFER_BIT_LENGTH_1024)) {
            for (byte[] bytes : list) {
                bufferedRandomAccessFileOptimize.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Instant writeEnd = Instant.now();
        System.out.printf("file size %s (byte) write speed %s ms ...%n", fileLength, Duration.between(writeStart, writeEnd).toMillis());

        Thread.sleep(5000);

        Instant readWriteStart = Instant.now();
        try (BufferedRandomAccessFileOptimize readObject =
                     new BufferedRandomAccessFileOptimize("I:\\SoapUI-5.4.0.zip", R, BUFFER_BIT_LENGTH_1024);
             BufferedRandomAccessFileOptimize writeObject =
                     new BufferedRandomAccessFileOptimize("I:\\SoapUI-5.4.0.zip.optimize.2", RW, BUFFER_BIT_LENGTH_1024)) {
            fileLength = readObject.initFileLength;
            int readcount;
            byte[] bytes = new byte[BUFFER_BIT_LENGTH_1024];
            while ((readcount = readObject.read(bytes)) != -1) {
                writeObject.write(bytes, 0, readcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Instant readWriteEnd = Instant.now();
        System.out.printf("file size %s (byte) read and write speed %s ms ...%n", fileLength, Duration.between(readWriteStart, readWriteEnd).toMillis());
    }
}
