

package com.glob3mobile.glasses;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;


public class Utils {

   String TAG = "Utils:";


   /**
    * 
    * @param bmp
    *           input bitmap
    * @param contrast
    *           0..10 1 is default
    * @param brightness
    *           -255..255 0 is default
    * @return new bitmap
    */
   public static Bitmap changeBitmapContrastBrightness(final Bitmap bmp,
                                                       final float contrast,
                                                       final float brightness) {
      final ColorMatrix cm = new ColorMatrix(new float[] { contrast, 0, 0, 0, brightness, 0, contrast, 0, 0, brightness, 0, 0,
               contrast, 0, brightness, 0, 0, 0, 1, 0 });

      final Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

      final Canvas canvas = new Canvas(ret);

      final Paint paint = new Paint();
      paint.setColorFilter(new ColorMatrixColorFilter(cm));
      canvas.drawBitmap(bmp, 0, 0, paint);

      return ret;
   }


   public String getJsonResponse(final String url) {
      final StringBuilder builder = new StringBuilder();

      final HttpGet httpGet = new HttpGet(url);
      final HttpParams httpParameters = new BasicHttpParams();
      final int timeoutConnection = 60000;
      HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
      final HttpClient client = new DefaultHttpClient(httpParameters);
      try {
         final HttpResponse response = client.execute(httpGet);

         final StatusLine statusLine = response.getStatusLine();
         final int statusCode = statusLine.getStatusCode();
         if (statusCode == 200) {
            final HttpEntity entity = response.getEntity();
            final InputStream content = entity.getContent();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
               builder.append(line);
            }
         }
         else {
            Log.e(TAG, "Failed to download file");
         }
      }
      catch (final ClientProtocolException e) {
         Log.e(TAG, e.toString());
      }
      catch (final IOException e) {
         Log.e(TAG, e.toString());
      }
      return builder.toString();
   }


   public static Bitmap DownloadFullFromUrl(final String imageFullURL) {
      Bitmap bm = null;
      try {

         Log.d("URL;", "" + imageFullURL);

         final URL url = new URL(imageFullURL);
         final URLConnection ucon = url.openConnection();
         final InputStream is = ucon.getInputStream();
         final BufferedInputStream bis = new BufferedInputStream(is);
         final ByteArrayBuffer baf = new ByteArrayBuffer(50);
         int current = 0;
         Log.i("baf;", "" + baf.length());
         while ((current = bis.read()) != -1) {
            baf.append((byte) current);
         }

         Log.i("baf;", "" + baf.length());
         bm = BitmapFactory.decodeByteArray(baf.toByteArray(), 0, baf.toByteArray().length);
         Log.i("Bitmap;", "" + bm.getByteCount());
      }
      catch (final IOException e) {
         Log.d("ImageManager", "Error: " + e);
      }
      return bm;
   }
}
