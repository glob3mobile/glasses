

package com.glob3mobile.glasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;


public class GooglePlaceActivity
         extends
            Activity {


   String                  TAG = GooglePlaceActivity.this.toString();

   private GestureDetector mGestureDetector;

   private String          _reference;

   private String          _vicinity;

   private String          _latlon;


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      final MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.item_menu, menu);
      return true;
   }


   private GestureDetector createGestureDetector(final Context context) {
      final GestureDetector gestureDetector = new GestureDetector(context);
      //Create a base listener for generic gestures
      gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
         @Override
         public boolean onGesture(final Gesture gesture) {
            if (gesture == Gesture.TWO_TAP) {
               Log.d(G3MGlassesDemoMainActivity.class.toString(), "two taps options menu");

               return true;
            }
            else if (gesture == Gesture.TAP) {
               final AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
               audio.playSoundEffect(Sounds.DISALLOWED);
               openOptionsMenu();
            }
            return false;
         }


      });

      return gestureDetector;

   }


   @Override
   public boolean onGenericMotionEvent(final MotionEvent event) {
      if (mGestureDetector != null) {
         return mGestureDetector.onMotionEvent(event);
      }
      return false;
   }


   @Override
   public boolean onOptionsItemSelected(final MenuItem item) {
      // Handle item selection 
      switch (item.getItemId()) {
         case R.id.directions:
            Log.i(TAG, "Go to directions");
            Log.i(TAG, "google.navigation:ll=" + _latlon + "&title=" + _vicinity);
            final Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:ll=" + _latlon + "&title="
                                                                              + _vicinity));
            startActivity(navIntent);
            return true;
         case R.id.more:
            Log.i(TAG, "More");
            GooglePlaceDetailsRetriever.getDetails(_reference, GooglePlaceActivity.this);
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }


   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.google_place_layout);

      mGestureDetector = createGestureDetector(getApplicationContext());

      _reference = getIntent().getExtras().getString("url");
      _vicinity = getIntent().getExtras().getString("vicinity");
      _latlon = getIntent().getExtras().getString("latlon");

      final String title = getIntent().getExtras().getString("title");
      final String photoReference = getIntent().getExtras().getString("photo");
      final int distance = Integer.valueOf(getIntent().getExtras().getString("distance"));

      Log.i(TAG, title + "," + photoReference + "," + distance);


      final RelativeLayout root = (RelativeLayout) findViewById(R.id.root_google_place_activity);


      final Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {


            final String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?photoreference=" + photoReference
                                    + "&sensor=false&maxheight=360&maxwidth=640&key=AIzaSyA_5m4agh4i8voQ8fdEaeOgkpb1EJt0_OI";
            Log.i(TAG, urlPhoto);

            final Bitmap bitmap = Utils.DownloadFullFromUrl(urlPhoto);

            GooglePlaceActivity.this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  String distanceText = "";
                  if (distance < 1000) {
                     distanceText = distance + "m";
                  }
                  else {
                     distanceText = (distance / 1000) + "km";
                  }
                  root.addView(new CardBuilder(GooglePlaceActivity.this, CardBuilder.Layout.CAPTION).setText(title).setFootnote(
                           distanceText).addImage(bitmap).getView());
               }
            });
         }
      });
      thread.start();


   }
}
