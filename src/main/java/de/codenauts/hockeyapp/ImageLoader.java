// Based on code from https://github.com/thest1/LazyList

package de.codenauts.hockeyapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {
  final private static int REQUIRED_SIZE = 72;
  final private static int STUB_ID = 0;

  private ImageLoaderThread imageLoaderThread = new ImageLoaderThread();
  private ImageQueue imageQueue = new ImageQueue();
  private FileCache fileCache;
  private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
  private MemoryCache memoryCache = new MemoryCache();

  public ImageLoader(Context context) {
    fileCache = new FileCache(context);
    imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
  }

  public void displayImage(String url, Activity activity, ImageView imageView) {
    imageViews.put(imageView, url);
    Bitmap bitmap = memoryCache.get(url);
    if (bitmap != null) {
      imageView.setImageBitmap(bitmap);
    }
    else {
      queueImage(url, activity, imageView);
      imageView.setImageResource(STUB_ID);
    }    
  }

  private void queueImage(String url, Activity activity, ImageView imageView) {
    imageQueue.remove(imageView);
    
    ImageToLoad image = new ImageToLoad(url, imageView);
    synchronized (imageQueue.imagesToLoad) {
      imageQueue.imagesToLoad.push(image);
      imageQueue.imagesToLoad.notifyAll();
    }

    if (imageLoaderThread.getState() == Thread.State.NEW) {
      imageLoaderThread.start();
    }
  }

  private Bitmap getBitmap(String url) {
    File file = fileCache.getFile(url);

    Bitmap cachedBitmap = decodeFile(file);
    if (cachedBitmap != null) {
      return cachedBitmap;
    }

    try {
      Bitmap bitmap = null;
      URL imageUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();

      InputStream inputStream = connection.getInputStream();
      OutputStream outputStream = new FileOutputStream(file);
      
      CopyStream(inputStream, outputStream);
      
      inputStream.close();
      outputStream.close();
      
      bitmap = decodeFile(file);
      return bitmap;
    } 
    catch (Exception e) {
      return null;
    }
  }

  private Bitmap decodeFile(File file) {
    try {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      
      BitmapFactory.decodeStream(new FileInputStream(file), null, options);

      int width = options.outWidth; 
      int height = options.outHeight;
      int scale = 1;
      while (true) {
        if ((width / 2 < REQUIRED_SIZE) || (height / 2 < REQUIRED_SIZE)) {
          break;
        }
        width /= 2;
        height /= 2;
        scale *= 2;
      }

      BitmapFactory.Options outOptions = new BitmapFactory.Options();
      outOptions.inSampleSize = scale;
      
      return BitmapFactory.decodeStream(new FileInputStream(file), null, outOptions);
    } 
    catch (FileNotFoundException e) {
    }
    
    return null;
  }

  public void stopThread() {
    imageLoaderThread.interrupt();
  }

  private class ImageToLoad {
    public String url;
    public ImageView imageView;
    
    public ImageToLoad(String url, ImageView imageView) {
      this.url = url; 
      this.imageView = imageView;
    }
  }

  private class ImageQueue {
    private Stack<ImageToLoad> imagesToLoad = new Stack<ImageToLoad>();

    public void remove(ImageView image) {
      for (int index = 0; index < imagesToLoad.size();) {
        if (imagesToLoad.get(index).imageView == image) {
          imagesToLoad.remove(index);
        }
        else {
          ++index;
        }
      }
    }
  }

  private class ImageLoaderThread extends Thread {
    public void run() {
      try {
        while (true) {
          if (imageQueue.imagesToLoad.size() == 0) {
            synchronized (imageQueue.imagesToLoad) {
              imageQueue.imagesToLoad.wait();
            }
          }
          
          if(imageQueue.imagesToLoad.size() != 0) {
            ImageToLoad imageToLoad;
            synchronized(imageQueue.imagesToLoad) {
              imageToLoad = imageQueue.imagesToLoad.pop();
            }
            
            Bitmap bitmap = getBitmap(imageToLoad.url);
            memoryCache.put(imageToLoad.url, bitmap);
            String tag = imageViews.get(imageToLoad.imageView);
            if ((tag != null) && (tag.equals(imageToLoad.url))) {
              BitmapDisplayer displayer = new BitmapDisplayer(bitmap, imageToLoad.imageView);
              Activity activity = (Activity)imageToLoad.imageView.getContext();
              activity.runOnUiThread(displayer);
            }
          }
          if (Thread.interrupted()) {
            break;
          }
        }
      } 
      catch (InterruptedException e) {
      }
    }
  }

  class BitmapDisplayer implements Runnable {
    Bitmap bitmap;
    ImageView imageView;
    
    public BitmapDisplayer(Bitmap bitmap, ImageView imageView) { 
      this.bitmap = bitmap;
      this.imageView = imageView;
    }
    
    public void run() {
      if (bitmap!=null) {
        imageView.setImageBitmap(bitmap);
      }
      else {
        imageView.setImageResource(STUB_ID);
      }
    }
  }

  public void clearCache() {
    memoryCache.clear();
    fileCache.clear();
  }

  public static void CopyStream(InputStream inputStream, OutputStream outputStream) {
    final int bufferSize = 1024;
    try {
      byte[] bytes = new byte[bufferSize];
      for (;;) {
        int count=inputStream.read(bytes, 0, bufferSize);
        if (count == -1) {
          break;
        }
        outputStream.write(bytes, 0, count);
      }
    }
    catch (Exception e) {
    }
  }
}
