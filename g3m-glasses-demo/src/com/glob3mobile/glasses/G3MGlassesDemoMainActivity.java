

package com.glob3mobile.glasses;

import java.util.ArrayList;

import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.Color;
import org.glob3.mobile.generated.DownloaderImageBuilder;
import org.glob3.mobile.generated.G3MContext;
import org.glob3.mobile.generated.GTask;
import org.glob3.mobile.generated.Geodetic2D;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.HUDQuadWidget;
import org.glob3.mobile.generated.HUDRelativePosition;
import org.glob3.mobile.generated.HUDRelativeSize;
import org.glob3.mobile.generated.HUDRenderer;
import org.glob3.mobile.generated.ILogger;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarkTouchListener;
import org.glob3.mobile.generated.MarkUserData;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.MercatorUtils;
import org.glob3.mobile.generated.PeriodicalTask;
import org.glob3.mobile.generated.Quality;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.Touch;
import org.glob3.mobile.generated.TouchEvent;
import org.glob3.mobile.generated.TouchEventType;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.URLTemplateLayer;
import org.glob3.mobile.generated.Vector2I;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glob3mobile.glasses.OrientationManager.OnChangedListener;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;


public class G3MGlassesDemoMainActivity
         extends
            Activity
         implements
            G3MGlassesDemoListener {

   public class ItemUserData
            extends
               MarkUserData {

      private String _wpURL;
      private String _googlePlaceReference;


      public String getWpURL() {
         return _wpURL;
      }


      public String getGooglePlaceReference() {
         return _googlePlaceReference;
      }


      public void setGooglePlaceReference(final String googlePlaceReference) {
         _googlePlaceReference = googlePlaceReference;
      }


      public void setWpURL(final String wpURL) {
         _wpURL = wpURL;
      }

   }


   private G3MWidget_Android       _g3mWidget;
   MarksRenderer                   _iconRenderer = new MarksRenderer(false);
   protected PowerManager.WakeLock wakelock;
   private OrientationManager      _om;
   Geodetic2D                      _lastPosition = null;
   private LinearLayout            _layout;


   private GestureDetector         mGestureDetector;


   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      mGestureDetector = createGestureDetector(getApplicationContext());

      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "G3M Google Glasses");
      wakelock.acquire();
      setContentView(R.layout.activity_main);


      final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      _om = new OrientationManager(sensorManager, locationManager);
      _om.start();

      _layout = (LinearLayout) findViewById(R.id.glob3);


      final G3MBuilder_Android builder = new G3MBuilder_Android(this);


      builder.getPlanetRendererBuilder().setDefaultTileBackGroundImage(
               new DownloaderImageBuilder(new URL("file:///_TEXTURE_GREEN_DARK.png")));
      builder.getPlanetRendererBuilder().setForceFirstLevelTilesRenderOnStart(false);

      final URLTemplateLayer greyLayer = URLTemplateLayer.newWGS84("file:///___TEXTURE_MAGENTA.png", Sector.FULL_SPHERE, false,
               0, 6, TimeInterval.zero());


      //      final MapBoxLayer mboxAerialLayer = new MapBoxLayer("examples.map-m0t0lrpu", TimeInterval.fromDays(30), true, 2);
      //      mboxAerialLayer.setTitle("Map Box Aerial");
      //      mboxAerialLayer.setEnable(true);

      final LayerSet layerSet = new LayerSet();
      layerSet.addLayer(greyLayer);

      builder.getPlanetRendererBuilder().setLayerSet(layerSet);
      //      builder.getPlanetRendererBuilder().setRenderDebug(true);
      //      builder.getPlanetRendererBuilder().setRenderTileMeshes(false);

      final HUDRenderer hudRenderer = new HUDRenderer();
      final HUDQuadWidget xImg = new HUDQuadWidget(new DownloaderImageBuilder(new URL("file:///x.png")), new HUDRelativePosition(
               0.5f, HUDRelativePosition.Anchor.VIEWPORT_WIDTH, HUDRelativePosition.Align.CENTER), new HUDRelativePosition(0.5f,
               HUDRelativePosition.Anchor.VIEWPORT_HEIGTH, HUDRelativePosition.Align.MIDDLE), new HUDRelativeSize(0.25f,
               HUDRelativeSize.Reference.VIEWPORT_MIN_AXIS), new HUDRelativeSize(0.125f,
               HUDRelativeSize.Reference.VIEWPORT_MIN_AXIS));

      hudRenderer.addWidget(xImg);
      builder.setHUDRenderer(hudRenderer);


      builder.addRenderer(_iconRenderer);

      builder.getPlanetRendererBuilder().setQuality(Quality.QUALITY_LOW);
      builder.setBackgroundColor(Color.fromRGBA255(135, 206, 235, 255));

      builder.addPeriodicalTask(getDataRetrievementPeriodicalTask());

      _g3mWidget = builder.createWidget();

      _g3mWidget.setCameraPosition(new Geodetic3D(Angle.fromDegrees(_om.getLocation().getLatitude()),
               Angle.fromDegrees(_om.getLocation().getLongitude()), 10));


      _om.addOnChangedListener(new OnChangedListener() {

         @Override
         public void onOrientationChanged(final OrientationManager orientationManager) {

            _g3mWidget.getG3MWidget().getNextCamera().setFOV(Angle.nan(), Angle.fromDegrees(14));
            _g3mWidget.setCameraPitch(Angle.fromDegrees(-orientationManager.getPitch()));
            _g3mWidget.setCameraHeading(Angle.fromDegrees(-orientationManager.getHeading()));
            _g3mWidget.setCameraRoll(Angle.fromDegrees(orientationManager.getRoll()));
         }


         @Override
         public void onLocationChanged(final OrientationManager orientationManager) {

            _g3mWidget.setCameraPosition(new Geodetic3D(Angle.fromDegrees(_om.getLocation().getLatitude()),
                     Angle.fromDegrees(_om.getLocation().getLongitude()), 10));

         }


         @Override
         public void onAccuracyChanged(final OrientationManager orientationManager) {
            // TODO Auto-generated method stub

         }
      });


      _layout.addView(_g3mWidget);
   }


   @Override
   public boolean onGenericMotionEvent(final MotionEvent event) {
      if (mGestureDetector != null) {
         return mGestureDetector.onMotionEvent(event);
      }
      return false;
   }


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {

      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main_menu, menu);
      return true;
   }


   private PeriodicalTask getDataRetrievementPeriodicalTask() {

      final PeriodicalTask task = new PeriodicalTask(TimeInterval.fromMilliseconds(30), new GTask() {
         @Override
         public void run(final G3MContext context) {


            final double currentLatitude = _om.getLocation().getLatitude();
            final double currentLongitude = _om.getLocation().getLongitude();

            final Geodetic2D currentPosition = new Geodetic2D(Angle.fromDegrees(currentLatitude),
                     Angle.fromDegrees(currentLongitude));

            if ((_lastPosition == null) || (getDistance(_lastPosition, currentPosition) > 500.0)) {

               runOnUiThread(new Runnable() {

                  @Override
                  public void run() {
                     ((TextView) findViewById(R.id.data)).setVisibility(View.VISIBLE);
                  }
               });

               //               POIDataRetriever.getNearbyWikipediaArticles(context, currentLatitude, currentLongitude,
               //                        G3MGlassesDemoMainActivity.this);

               POIDataRetriever.getNearbyPlaces(context, currentLatitude, currentLongitude, "food",
                        G3MGlassesDemoMainActivity.this);


               _lastPosition = currentPosition;
            }

         }


         private double getDistance(final Geodetic2D lastPosition,
                                    final Geodetic2D currentPosition) {
            final double lastPositionLon = MercatorUtils.longitudeToMeters(lastPosition._longitude);
            final double lastPositionLat = MercatorUtils.longitudeToMeters(lastPosition._latitude);
            final double currentPositionLon = MercatorUtils.longitudeToMeters(currentPosition._longitude);
            final double currentPositionLat = MercatorUtils.longitudeToMeters(currentPosition._latitude);
            return Math.sqrt(Math.pow(2, (lastPositionLat - currentPositionLat))
                             + Math.pow(2, (lastPositionLon - currentPositionLon)));
         }

      });
      return task;
   }


   @Override
   final protected void onResume() {
      super.onResume();
      ILogger.instance().logError("Resume");
      wakelock.acquire();
   }


   @Override
   final protected void onPause() {
      super.onPause();

      if (isFinishing()) {
         ILogger.instance().logError("killing the app");
         this.wakelock.release();
         System.exit(0);
      }

   }


   @Override
   final protected void onDestroy() {
      ILogger.instance().logInfo("destroy");
      super.onDestroy();
      finish();
   }


   private GestureDetector createGestureDetector(final Context context) {
      final GestureDetector gestureDetector = new GestureDetector(context);
      //Create a base listener for generic gestures
      gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
         @Override
         public boolean onGesture(final Gesture gesture) {
            if (gesture == Gesture.TWO_TAP) {
               Log.d(G3MGlassesDemoMainActivity.class.toString(), "two taps options menu");
               openOptionsMenu();
               return true;
            }
            else if (gesture == Gesture.TAP) {
               final AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
               audio.playSoundEffect(Sounds.DISALLOWED);
               Log.d(G3MGlassesDemoMainActivity.class.toString(), "one tap");
               final Touch t = new Touch(new Vector2I(320, 180), new Vector2I(320, 180));
               final TouchEvent te = TouchEvent.create(TouchEventType.DownUp, t);
               _g3mWidget.queueEvent(new Runnable() {
                  @Override
                  public void run() {
                     _g3mWidget.getG3MWidget().onTouchEvent(te);
                  }
               });

               return true;
            }
            return false;
         }


      });

      return gestureDetector;

   }


   @Override
   public void onWikipediaPOIsRetrieved(final ArrayList<WikipediaArticle> articles) {
      ILogger.instance().logInfo("Retrieved " + articles.size() + " from geonames service");

      runOnUiThread(new Runnable() {

         @Override
         public void run() {
            ((TextView) findViewById(R.id.data)).setVisibility(View.INVISIBLE);
         }
      });


      for (final WikipediaArticle article : articles) {


         final Mark m = new Mark(article.getTitle(),//
                  new URL("http://icons.iconarchive.com/icons/sykonist/popular-sites/256/Wikipedia-icon.png", false), //
                  new Geodetic3D(article.getPosition()._latitude, article.getPosition()._longitude, 0), //
                  AltitudeMode.RELATIVE_TO_GROUND, 0);

         final ItemUserData iud = new ItemUserData();
         iud.setWpURL(article.getWikipediaURL());
         m.setUserData(iud);

         _iconRenderer.setMarkTouchListener(new MarkTouchListener() {

            @Override
            public boolean touchedMark(final Mark mark) {
               ILogger.instance().logInfo(
                        "Marker positioned in:" + mark.getPosition().description() + ", URL:"
                                 + ((ItemUserData) mark.getUserData()).getWpURL());

               final AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
               audio.playSoundEffect(Sounds.SUCCESS);

               runOnUiThread(new Runnable() {

                  @Override
                  public void run() {

                     final Intent intent = new Intent(G3MGlassesDemoMainActivity.this, WikipediaViewActivity.class);
                     intent.putExtra("url", "http://" + ((ItemUserData) mark.getUserData()).getWpURL());
                     G3MGlassesDemoMainActivity.this.startActivity(intent);
                  }
               });


               return false;
            }
         }, true);

         _iconRenderer.addMark(m);

      }
   }


   @Override
   public void onGooglePlacePOIsRetrieved(final ArrayList<GooglePlaceItem> items) {
      ILogger.instance().logInfo("Retrieved " + items.size() + " from google play service");

      runOnUiThread(new Runnable() {

         @Override
         public void run() {
            ((TextView) findViewById(R.id.data)).setVisibility(View.INVISIBLE);
         }
      });


      for (final GooglePlaceItem item : items) {


         final Mark m = new Mark(item.getTitle(),//
                  new URL(item.getUrlIcon(), false), //
                  new Geodetic3D(item.getPosition()._latitude, item.getPosition()._longitude, 0), //
                  AltitudeMode.RELATIVE_TO_GROUND, 0);

         //  m.setUserData(new WpUserData(item.getWikipediaURL()));

         _iconRenderer.setMarkTouchListener(new MarkTouchListener() {

            @Override
            public boolean touchedMark(final Mark mark) {
               ILogger.instance().logInfo(
                        "Marker positioned in:" + mark.getPosition().description() + ", URL:"
                                 + ((ItemUserData) mark.getUserData()).getGooglePlaceReference());

               final AudioManager audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
               audio.playSoundEffect(Sounds.SUCCESS);

               runOnUiThread(new Runnable() {

                  @Override
                  public void run() {

                     Log.d("TAP:", "On the place");

                     //Show a target//
                     //                     final Intent intent = new Intent(G3MGlassesDemoMainActivity.this, WikipediaViewActivity.class);
                     //                     intent.putExtra("url", "http://" + ((WpUserData) mark.getUserData()).getWpURL());
                     //                     G3MGlassesDemoMainActivity.this.startActivity(intent);
                  }
               });


               return false;
            }
         }, true);

         _iconRenderer.addMark(m);

      }
   }
}
