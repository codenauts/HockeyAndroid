// Based on code from https://github.com/thest1/LazyList

package de.codenauts.hockeyapp;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class MemoryCache {
  private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

  public Bitmap get(String id) {
    if (!cache.containsKey(id)) {
      return null;
    }
    SoftReference<Bitmap> reference = cache.get(id);
    return reference.get();
  }

  public void put(String id, Bitmap bitmap) {
    cache.put(id, new SoftReference<Bitmap>(bitmap));
  }

  public void clear() {
    cache.clear();
  }
}
