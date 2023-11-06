package com.hzh.domain.log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName LogGeneration
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/6 13:03
 * @Version 0.0.1
 **/
public class LogGeneration extends AbstractLogDir implements Comparable<LogGeneration> {
    private static final Pattern DIR_NAME_PATTERN = Pattern.compile("log-(\\d+)");
    private final int lastIncludedIndex;

    LogGeneration(File baseDir, int lastIncludedIndex) {
        super(new File(baseDir, generateDirName(lastIncludedIndex)));
        this.lastIncludedIndex = lastIncludedIndex;
    }

    LogGeneration(File dir) {
        super(dir);
        Matcher matcher = DIR_NAME_PATTERN.matcher(dir.getName());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("not a directory name of log generation, [" + dir.getName() + "]");
        }
        lastIncludedIndex = Integer.parseInt(matcher.group(1));
    }

    private static String generateDirName(int lastIncludedIndex) {
        return "log-" + lastIncludedIndex;
    }


    public int getLastIncludedIndex() {
        return lastIncludedIndex;
    }

    @Override
    public int compareTo(LogGeneration o) {
        return Integer.compare(lastIncludedIndex, o.lastIncludedIndex);
    }
}
