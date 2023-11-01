package com.hzh.domain.node.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @ClassName SeekableFile
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 17:14
 * @Version 0.0.1
 **/
public class SeekableFile extends RandomAccessFile {
    public SeekableFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    public SeekableFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }
}
