package com.hzh.domain.node.file;

import java.io.*;

/**
 * @ClassName RandomAccessFileAdapter
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 12:20
 * @Version 0.0.1
 **/
public class RandomAccessFileAdapter implements SeekableFile{

    private static final String DEFAULT_MODE = "rw";

    private final File file;
    private final RandomAccessFile randomAccessFile;

    public RandomAccessFileAdapter(File file) throws FileNotFoundException {
        this(file, DEFAULT_MODE);
    }

    public RandomAccessFileAdapter(File file, String mode) throws FileNotFoundException {
        this.file = file;
        randomAccessFile = new RandomAccessFile(file, mode);
    }
    @Override
    public long position() throws IOException {
        return 0;
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public void writeInt(int i) throws IOException {
        randomAccessFile.writeInt(i);
    }

    @Override
    public void writeLong(long l) throws IOException {
        randomAccessFile.writeLong(l);
    }

    @Override
    public void write(byte[] b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public int readInt() throws IOException {
        return randomAccessFile.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return randomAccessFile.readLong();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public long size() throws IOException {
        return randomAccessFile.length();
    }

    @Override
    public void truncate(long size) throws IOException {
        randomAccessFile.setLength(size);
    }

    @Override
    public InputStream inputStream(long start) throws IOException {
        FileInputStream input = new FileInputStream(file);
        if (start > 0) {
            input.skip(start);
        }
        return input;
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
