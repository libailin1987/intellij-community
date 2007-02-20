package com.intellij.localvcs;

import com.intellij.util.io.PagedMemoryMappedFile;
import com.intellij.util.io.RandomAccessPagedDataInput;
import com.intellij.util.io.RecordDataOutput;

import java.io.File;
import java.io.IOException;

// todo get rid of exception declarations

// todo what about checking consistency?
public class ContentStorage implements IContentStorage {
  private PagedMemoryMappedFile myStore;

  public ContentStorage(File f) throws IOException {
    myStore = new PagedMemoryMappedFile(f);
  }

  public void close() {
    myStore.dispose();
  }

  public void save() {
    myStore.immediateForce();
  }

  public int store(byte[] content) throws IOException {
    RecordDataOutput r = myStore.createRecord();
    r.writeInt(content.length);
    r.write(content);
    r.close();
    return r.getRecordId();
  }

  public byte[] load(int id) throws IOException {
    RandomAccessPagedDataInput r = myStore.getReader(id);
    byte[] buffer = new byte[r.readInt()];
    r.readFully(buffer);
    r.close();
    return buffer;
  }

  public void remove(int id) {
    myStore.delete(id);
  }

  public boolean isRemoved(int id) {
    return myStore.isPageFree(id);
  }
}