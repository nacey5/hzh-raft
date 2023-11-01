package com.hzh.domain.node.file;

import com.hzh.domain.node.NodeId;
import com.hzh.domain.node.NodeStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 4 bytes. uπen tTerm o
 * 4 bytes, votedFor (node lD) length.
 * Variable length, vo tedFor content.
 *
 * @ClassName FileNodeStore
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 17:12
 * @Version 0.0.1
 **/
public class FileNodeStore implements NodeStore {
    //fileName
    public static final String FILE_NAME = "node.bin";
    private static final long OFFSET_TERM = 0;
    private static final long OFFSET_VOTED_FOR = 4;
    private final SeekableFile seekableFile;
    private int term = 0;
    private NodeId votedFor = null;

    public FileNodeStore(File file) {
        try {
            if (!file.exists()) {
                //todo 这里的创建文件应该有一点点问题
                Files.createFile(file.toPath());
            }
            seekableFile = new RandomAccessFileAdapter(file);
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    public FileNodeStore(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
        try {
            initializeOrLoad();
        } catch (IOException e) {
            throw new NodeStoreException(e);
        }
    }

    private void initializeOrLoad() throws IOException {
        if (seekableFile.size() == 0) {
            //init
            //term，4+votedFor,4=8
            seekableFile.truncate(8L);
            seekableFile.seek(0);
            seekableFile.writeInt(0);//term
            seekableFile.writeInt(0);//votedFor length
        } else {
            //load
            //read the term
            term = seekableFile.readInt();
            //read the votedFor
            int length = seekableFile.readInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                seekableFile.read(bytes);
                votedFor = new NodeId(new String(bytes));
            }
        }
    }


    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        try {
            //aim to the term
            seekableFile.seek(OFFSET_TERM);
            seekableFile.writeInt(term);
        }catch (IOException e){
            throw new NodeStoreException(e);
        }
        this.term=term;
    }

    @Override
    public void setVoteFor(NodeId voteFor) {
        try {
           seekableFile.seek(OFFSET_VOTED_FOR);
           //votedFor is null
            if (voteFor==null){
                seekableFile.writeInt(0);
                seekableFile.truncate(8L);
            }else {
                byte[] bytes = voteFor.getValue().getBytes();
                seekableFile.writeInt(bytes.length);
                seekableFile.write(bytes);
            }
        }catch (IOException e){
            throw new NodeStoreException(e);
        }
        this.votedFor=voteFor;
    }

    @Override
    public NodeId getVoteFor() {
        return votedFor;
    }

    @Override
    public void close() {
        try {
            seekableFile.close();
        }catch (IOException e){
            throw new NodeStoreException(e);
        }
    }
}
