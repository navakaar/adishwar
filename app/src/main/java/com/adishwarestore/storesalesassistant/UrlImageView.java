package com.adishwarestore.storesalesassistant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dhiraj on 03-Jan-17.
 */

public class UrlImageView extends ImageView {

    private static class UrlLoadingTask extends AsyncTask<URL, Void, Bitmap> {
        private final ImageView updateView;
        private boolean         isCancelled = false;
        private boolean         filenotfound = false;
        private String          imgType = "";
        private InputStream urlInputStream;


        private UrlLoadingTask(ImageView updateView) {
            this.updateView = updateView;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Log.d(getClass().getName(), "params[0] = " + params[0].toString());
            try {
                URLConnection con = params[0].openConnection();
                if (params[0].toString().contains("EMPLOYEE")) {
                    imgType = "EMPLOYEE";
                } else if (params[0].toString().contains("TELEVISION")) {
                    imgType = "TELEVISION";
                } else if (params[0].toString().contains("REFRIGERATOR")) {
                    imgType = "REFRIGERATOR";
                } else if (params[0].toString().contains("MICROWAVE")) {
                    imgType = "MICROWAVE";
                } else {
                    imgType = "OTHERS";
                }
                // can use some more params, i.e. caching directory etc
                con.setUseCaches(true);
                this.urlInputStream = con.getInputStream();
                return BitmapFactory.decodeStream(urlInputStream);
            } catch (FileNotFoundException e) {
                Log.d(getClass().getName(), "FILENOTFOUND EXCEPTION IS CAUGHT!!");
                filenotfound = true;
                return null;
            }
            catch (IOException e) {
                Log.w(UrlImageView.class.getName(), "failed to load image from " + params[0], e);
                return null;
            } finally {
                if (this.urlInputStream != null) {
                    try {
                        this.urlInputStream.close();
                    } catch (IOException e) {
                        ; // swallow
                    } finally {
                        this.urlInputStream = null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null && this.filenotfound) {
                Log.d(getClass().getName(), "Result is NULL & FileNoteFound is TRUE");
                if (imgType.equals("EMPLOYEE"))
                    result = BitmapFactory.decodeResource(this.updateView.getResources(), R.drawable.img_staff);
                else if (imgType.equals("TELEVISION"))
                    result = BitmapFactory.decodeResource(this.updateView.getResources(), R.drawable.img_tv);
                else if (imgType.equals("REFRIGERATOR"))
                    result = BitmapFactory.decodeResource(this.updateView.getResources(), R.drawable.img_fridge);
                else if (imgType.equals("MICROWAVE"))
                    result = BitmapFactory.decodeResource(this.updateView.getResources(), R.drawable.img_mwoven);
                else
                    result = BitmapFactory.decodeResource(this.updateView.getResources(), R.drawable.img_ac);

            }
            if (!this.isCancelled) {
                // hope that call is thread-safe
                this.updateView.setImageBitmap(result);
            }
        }

        /*
         * just remember that we were cancelled, no synchronization necessary
         */
        @Override
        protected void onCancelled() {
            this.isCancelled = true;
            try {
                if (this.urlInputStream != null) {
                    try {
                        this.urlInputStream.close();
                    } catch (IOException e) {
                        ;// swallow
                    } finally {
                        this.urlInputStream = null;
                    }
                }
            } finally {
                super.onCancelled();
            }
        }
    }

    /*
     * track loading task to cancel it
     */
    private AsyncTask<URL, Void, Bitmap> currentLoadingTask;
    /*
     * just for sync
     */
    private Object                       loadingMonitor = new Object();

    public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlImageView(Context context) {
        super(context);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        cancelLoading();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        cancelLoading();
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        cancelLoading();
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(Uri uri) {
        cancelLoading();
        super.setImageURI(uri);
    }

    /**
     * loads image from given url
     *
     * @param url
     */
    public void setImageURL(URL url) {
        synchronized (loadingMonitor) {
            cancelLoading();
            this.currentLoadingTask = new UrlLoadingTask(this).execute(url);
        }
    }

    /**
     * cancels pending image loading
     */
    public void cancelLoading() {
        synchronized (loadingMonitor) {
            if (this.currentLoadingTask != null) {
                this.currentLoadingTask.cancel(true);
                this.currentLoadingTask = null;
            }
        }
    }
}
