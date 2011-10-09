// Based on code from https://github.com/thest1/LazyList

package de.codenauts.hockeyapp;

import java.io.File;

import android.content.Context;

public class FileCache {
  private File cacheDir;

  public FileCache(Context context){
    cacheDir = context.getCacheDir();
    
    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    }
  }

  public File getFile(String url) {
    String filename = String.valueOf(url.hashCode());
    File file = new File(cacheDir, filename);
    return file;
  }

  public void clear() {
    File[] files = cacheDir.listFiles();
    for (File file : files) {
      file.delete();
    }
  }
}
