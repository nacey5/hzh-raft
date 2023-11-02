package com.hzh.domain.node.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName SeekableFile
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 12:20
 * @Version 0.0.1
 **/
public interface SeekableFile {

    long position() throws IOException;

    void seek(long position) throws IOException;

    void writeInt(int i) throws IOException;

    void writeLong(long l) throws IOException;

    void write(byte[] b) throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    int read(byte[] b) throws IOException;

    long size() throws IOException;

    void truncate(long size) throws IOException;

    InputStream inputStream(long start) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
