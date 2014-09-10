package util;

/**
 * Created by juanc.jimenez on 26/08/14.
 */

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

//THis class is for handling lruBitmapCache
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
    //We handling the cachesize...
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); //4MB indeeed
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    //This COnstructor ensure the cache size is updating
    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    //We are setting the size in kilobytes of our cache
    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
