package com.hzh.domain.node.file;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ByteArraySeekableFileTest {
    @Test
    public void testWriteAndReadInt() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        int expected = 42;
        file.writeInt(expected);
        file.seek(0);
        int actual = file.readInt();
        assertEquals(expected, actual);
    }

    @Test
    public void testWriteAndReadLong() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        long expected = 1234567890L;
        file.writeLong(expected);
        file.seek(0);
        long actual = file.readLong();
        assertEquals(expected, actual);
    }

    @Test
    public void testWriteAndReadByteArray() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        byte[] expected = { 1, 2, 3, 4, 5 };
        file.write(expected);
        file.seek(0);
        byte[] actual = new byte[5];
        file.read(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testSeekAndPosition() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        int position = (int) file.position();
        assertEquals(0, position);

        file.writeLong(123);
        position = (int) file.position();
        assertEquals(8, position);

        file.seek(4);
        position = (int) file.position();
        assertEquals(4, position);
    }

    @Test
    public void testSizeAndTruncate() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        long size = file.size();
        assertEquals(0, size);

        file.writeLong(123);
        size = file.size();
        assertEquals(8, size);

        file.truncate(4);
        size = file.size();
        assertEquals(4, size);
    }

    @Test
    public void testInputStream() throws IOException {
        ByteArraySeekableFile file = new ByteArraySeekableFile();
        byte[] expected = { 1, 2, 3, 4, 5 };
        file.write(expected);

        ByteArrayInputStream inputStream = (ByteArrayInputStream) file.inputStream(1);
        byte[] actual = new byte[4];
        int bytesRead = inputStream.read(actual);
        assertArrayEquals(new byte[]{2, 3, 4, 5}, actual);
        assertEquals(4, bytesRead);
    }

}
