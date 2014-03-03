

package com.glob3mobile.glasses;

import org.glob3.mobile.generated.ILogger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;


public class WikipediaViewActivity
         extends
            Activity {
   protected PowerManager.WakeLock wakelock;
   private GestureDetector         mGestureDetector;
   private WebView                 _wb;

   private class MyWebViewClient
            extends
               WebViewClient {

      @Override
      //show the web page in webview but not in web browser
      public boolean shouldOverrideUrlLoading(final WebView view,
                                              final String url) {
         view.loadUrl(url);
         return true;
      }
   }


   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      //      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      //      this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "G3M Google Glasses");
      //      wakelock.acquire();
      setContentView(R.layout.wp_layout);

      mGestureDetector = createGestureDetector(getApplicationContext());


      _wb = (WebView) findViewById(R.id.webview);
      _wb.getSettings().setJavaScriptEnabled(true);
      _wb.loadUrl(getIntent().getStringExtra("url"));
      _wb.setWebViewClient(new MyWebViewClient());

      _wb.setOnTouchListener(new View.OnTouchListener() {

         @Override
         public boolean onTouch(final View v,
                                final MotionEvent event) {

            mGestureDetector.onMotionEvent(event);
            return true;
         }
      });
   }


   @Override
   public boolean onGenericMotionEvent(final MotionEvent event) {
      mGestureDetector.onMotionEvent(event);
      return true;
   }


   private GestureDetector createGestureDetector(final Context context) {
      final GestureDetector gestureDetector = new GestureDetector(context);
      //Create a base listener for generic gestures
      gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
         @Override
         public boolean onGesture(final Gesture gesture) {
            if (gesture == Gesture.TAP) {

               return true;
            }
            else if (gesture == Gesture.TWO_TAP) {
               // do something on two finger tap
               return true;
            }
            else if (gesture == Gesture.SWIPE_RIGHT) {
               // do something on right (forward) swipe
               return true;
            }
            else if (gesture == Gesture.SWIPE_LEFT) {
               // do something on left (backwards) swipe
               return true;
            }
            return false;
         }


      });
      gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
         @Override
         public void onFingerCountChanged(final int previousCount,
                                          final int currentCount) {
            // do something on finger count changes
         }
      });
      gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
         @Override
         public boolean onScroll(final float displacement,
                                 final float delta,
                                 final float velocity) {
            // do something on scrolling
            _wb.setScrollY(_wb.getScrollY() + ((int) displacement / 5));

            ILogger.instance().logError("Scrolling the view?");

            return true;
         }
      });
      return gestureDetector;
   }


}
