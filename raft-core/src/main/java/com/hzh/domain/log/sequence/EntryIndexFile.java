package com.hzh.domain.log.sequence;

import com.hzh.domain.node.file.RandomAccessFileAdapter;
import com.hzh.domain.node.file.SeekableFile;
import lombok.Data;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName EntrylndexFile
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 13:48
 * @Version 0.0.1
 **/
@Data
public class EntryIndexFile implements Iterable<EntryIndexItem> {

    private static final long OFFSET_MAX_ENTRY_INDEX = Integer.BYTES;

    private static final int LENGTH_ENTRY_INDEX_ITEM = 16;

    private final SeekableFile seekableFile;
    private int entryIndexCount;
    private int minEntryIndex;
    private int maxEntryIndex;
    private Map<Integer, EntryIndexItem> entryIndexMap = new HashMap<>();

    public EntryIndexFile(File file) throws IOException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntryIndexFile(SeekableFile seekableFile) throws IOException {
        this.seekableFile = seekableFile;
        load();
    }

    //load all metadata
    private void load() throws IOException {
        if (seekableFile.size() == 0L) {
            entryIndexCount = 0;
            return;
        }
        minEntryIndex = seekableFile.readInt();
        maxEntryIndex = seekableFile.readInt();
        updateEntryIndexCount();
        // load every sequence
        long offset;
        int kind;
        int term;
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            offset = seekableFile.readLong();
            kind = seekableFile.readInt();
            term = seekableFile.readInt();
            entryIndexMap.put(i, new EntryIndexItem(i, offset, kind, term));
        }
    }

    //update all log sequence
    private void updateEntryIndexCount() {
        entryIndexCount = maxEntryIndex - minEntryIndex + 1;
    }

    public void appendEntryIndex(int index, long offset, int kind, int term) throws IOException {
        if (seekableFile.size() == 0l) {
            // if file is null,then write to the minEntryIndex
            seekableFile.writeInt(index);
            minEntryIndex = index;
        } else {
            //check the index
            if (index != maxEntryIndex + 1) {
                throw new IllegalArgumentException("index must be " + (maxEntryIndex + 1) + ",but was " + index);
            }
            //skip the minEntryIndex
            seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        }
        //write to then maxEntryIndex
        seekableFile.writeInt(index);
        maxEntryIndex = index;
        updateEntryIndexCount();
        //remove to the end of the file
        seekableFile.seek(getOffsetOfEntryIndexItem(index));
        seekableFile.writeLong(offset);
        seekableFile.writeInt(kind);
        seekableFile.writeInt(term);
        entryIndexMap.put(index, new EntryIndexItem(index, offset, kind, term));
    }

    //get the specific log's offset
    private long getOffsetOfEntryIndexItem(int index) throws IOException {
        return (index - minEntryIndex) * LENGTH_ENTRY_INDEX_ITEM + Integer.BYTES * 2;
    }

    public void clear() throws IOException {
        seekableFile.truncate(0l);
        entryIndexCount = 0;
        entryIndexMap.clear();
    }

    //remove the index after data
    public void removeAfter(int newMaxEntryIndex) throws IOException {
        if (isEmpty() || newMaxEntryIndex >= maxEntryIndex) {
            return;
        }
        if (newMaxEntryIndex < minEntryIndex) {
            clear();
            return;
        }
        //change the maxEntryIndex
        seekableFile.seek(OFFSET_MAX_ENTRY_INDEX);
        seekableFile.writeInt(newMaxEntryIndex);
        //cropped text
        seekableFile.truncate(getOffsetOfEntryIndexItem(newMaxEntryIndex + 1));
        //remove the meta from memory
        for (int i = newMaxEntryIndex + 1; i <= maxEntryIndex; i++) {
            entryIndexMap.remove(i);
        }
        maxEntryIndex = newMaxEntryIndex;
        entryIndexCount = newMaxEntryIndex - minEntryIndex + 1;
    }

    public boolean isEmpty() {
        return entryIndexCount == 0;
    }

    @Override
    public Iterator<EntryIndexItem> iterator() {
        if (isEmpty()){
            return Collections.emptyIterator();
        }
        return new EntryIndexIterator(entryIndexCount,minEntryIndex);
    }


    @Nonnull
    public EntryIndexItem get(int entryIndex) {
        checkEmpty();
        if (entryIndex < minEntryIndex || entryIndex > maxEntryIndex) {
            throw new IllegalArgumentException("index < min or index > max");
        }
        return entryIndexMap.get(entryIndex);
    }

    private void checkEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("no entry index");
        }
    }

    public long getOffset(int entryIndex) {
        return get(entryIndex).getOffset();
    }


   private class EntryIndexIterator implements Iterator<EntryIndexItem> {
        private final int entryIndexCount;

        private int currentEntryIndex;

        private int minEntryIndex;
        private int maxEntryIndex;

        private Map<Integer, EntryIndexItem> entryIndexMap = new HashMap<>();


    public EntryIndexIterator(int entryIndexCount, int currentEntryIndex) {
            this.entryIndexCount = entryIndexCount;
            this.currentEntryIndex = currentEntryIndex;
        }

        @Override
        public boolean hasNext() {
            checkModification();
            return currentEntryIndex <= maxEntryIndex;
        }

        //check Modification or not
        private void checkModification() {
            if (this.entryIndexCount != EntryIndexFile.this.entryIndexCount) {
                throw new IllegalStateException("entry index count changed");
            }
        }

        @Override
        public EntryIndexItem next() {
            checkModification();
            return entryIndexMap.get(currentEntryIndex);
        }
    }




}
