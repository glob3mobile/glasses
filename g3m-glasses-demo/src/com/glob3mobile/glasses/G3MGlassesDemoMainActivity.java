

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
import org.glob3.mobile.generated.MapBoxLayer;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarkTouchListener;
import org.glob3.mobile.generated.MarkUserData;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.MercatorUtils;
import org.glob3.mobile.generated.PeriodicalTask;
import org.glob3.mobile.generated.Quality;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.Touch;
import org.glob3.mobile.generated.TouchEvent;
import org.glob3.mobile.generated.TouchEventType;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.Vector2I;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.glob3mobile.glasses.OrientationManager.OnChangedListener;


public class G3MGlassesDemoMainActivity
         extends
            Activity
         implements
            G3MGlassesDemoListener {

   public class WpUserData
            extends
               MarkUserData {

      private final String _wpURL;


      public WpUserData(final String wpURL) {
         this._wpURL = wpURL;
      }


      public String getWpURL() {
         return _wpURL;
      }

   }


   private G3MWidget_Android       _g3mWidget;
   MarksRenderer                   _iconRenderer = new MarksRenderer(false);
   protected PowerManager.WakeLock wakelock;
   private OrientationManager      _om;
   Geodetic2D                      _lastPosition = null;


   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);


      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      this.wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "G3M Google Glasses");
      wakelock.acquire();
      setContentView(R.layout.activity_main);


      final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      _om = new OrientationManager(sensorManager, locationManager);
      _om.start();

      final LinearLayout layout = (LinearLayout) findViewById(R.id.glob3);


      final G3MBuilder_Android builder = new G3MBuilder_Android(this);


      final MapBoxLayer mboxAerialLayer = new MapBoxLayer("examples.map-m0t0lrpu", TimeInterval.fromDays(30), true, 2);
      mboxAerialLayer.setTitle("Map Box Aerial");
      mboxAerialLayer.setEnable(true);

      final LayerSet layerSet = new LayerSet();
      layerSet.addLayer(mboxAerialLayer);

      builder.getPlanetRendererBuilder().setLayerSet(layerSet);

      final HUDRenderer hudRenderer = new HUDRenderer();
      final HUDQuadWidget xImg = new HUDQuadWidget(new DownloaderImageBuilder(new URL("file:///x.png")), new HUDRelativePosition(
               0.5f, HUDRelativePosition.Anchor.VIEWPORT_WIDTH, HUDRelativePosition.Align.CENTER), new HUDRelativePosition(0.5f,
               HUDRelativePosition.Anchor.VIEWPORT_HEIGTH, HUDRelativePosition.Align.MIDDLE), new HUDRelativeSize(0.25f,
               HUDRelativeSize.Reference.VIEWPORT_MIN_AXIS), new HUDRelativeSize(0.125f,
               HUDRelativeSize.Reference.VIEWPORT_MIN_AXIS));

      hudRenderer.addWidget(xImg);
      builder.setHUDRenderer(hudRenderer);


      builder.addRenderer(_iconRenderer);

      //  final Geodetic2D position = new Geodetic2D(Angle.fromDegrees(39.15), Angle.fromDegrees(-77.6));


      //  builder.getPlanetRendererBuilder().setRenderDebug(true);
      builder.getPlanetRendererBuilder().setQuality(Quality.QUALITY_LOW);
      builder.setBackgroundColor(Color.fromRGBA255(135, 206, 235, 255));

      builder.addPeriodicalTask(getDataRetrievementPeriodicalTask());
      //   builder.getPlanetRendererBuilder().setRenderTileMeshes(false);

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


      layout.addView(_g3mWidget);
   }


   @Override
   public boolean onKeyDown(final int keycode,
                            final KeyEvent event) {


      ILogger.instance().logError("Event:" + event.toString());

      if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {

         final Touch t = new Touch(new Vector2I(320, 180), new Vector2I(320, 180));
         final TouchEvent te = TouchEvent.create(TouchEventType.DownUp, t);
         _g3mWidget.queueEvent(new Runnable() {
            @Override
            public void run() {
               _g3mWidget.getG3MWidget().onTouchEvent(te);
            }
         });


         ILogger.instance().logError("CLICK");
         return true;
      }
      else if (keycode == KeyEvent.KEYCODE_BACK) {
         ILogger.instance().logError("killing the app");
         stopService(new Intent(this, G3MGlassesDemoService.class));
         this.wakelock.release();
         System.exit(0);
         return true;
      }
      return false;

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


               WikipediaDataRetriever.getNearbyWikipediaArticles(context, currentLatitude, currentLongitude,
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


   //   @Override
   //   final protected void onResume() {
   //      super.onResume();
   //      ILogger.instance().logError("Resume");
   //
   //
   //      ILogger.instance().logInfo("Resume");
   //      _g3mWidget.getG3MContext().getThreadUtils().invokeInRendererThread(new GTask() {
   //         @Override
   //         public void run(final G3MContext context) {
   //            _g3mWidget.onResume();
   //         }
   //      }, true);
   //
   //   }


   @Override
   final protected void onPause() {
      super.onPause();

      if (isFinishing()) {
         ILogger.instance().logError("killing the app");
         stopService(new Intent(this, G3MGlassesDemoService.class));
         this.wakelock.release();
         System.exit(0);
      }

      //      ILogger.instance().logInfo("On Pause: is finishing:" + isFinishing());
      //      if (isFinishing()) {
      //
      //         _g3mWidget.getG3MContext().getThreadUtils().invokeInRendererThread(new GTask() {
      //            @Override
      //            public void run(final G3MContext context) {
      //               _g3mWidget.onDestroy();
      //               //     System.exit(0);
      //            }
      //         }, true);
      //
      //
      //      }
      //      else {
      //
      //         _g3mWidget.getG3MContext().getThreadUtils().invokeInRendererThread(new GTask() {
      //            @Override
      //            public void run(final G3MContext context) {
      //               _g3mWidget.onPause();
      //            }
      //         }, true);
      //
      //
      //      }

   }


   @Override
   final protected void onDestroy() {
      ILogger.instance().logInfo("destroy");
      super.onDestroy();
      stopService(new Intent(this, G3MGlassesDemoService.class));
      System.exit(0);

   }


   @Override
   public void onWikipediaArticlesRetrieved(final ArrayList<WikipediaArticle> articles) {

      ILogger.instance().logInfo("Retrieved " + articles.size() + " from geonames service");

      for (final WikipediaArticle article : articles) {

         final Mark m = new Mark(article.getTitle(),//
                  new URL("http://icons.iconarchive.com/icons/sykonist/popular-sites/256/Wikipedia-icon.png", false), //
                  new Geodetic3D(article.getPosition()._latitude, article.getPosition()._longitude, 0), //
                  AltitudeMode.RELATIVE_TO_GROUND, 0);

         m.setUserData(new WpUserData(article.getWikipediaURL()));

         _iconRenderer.setMarkTouchListener(new MarkTouchListener() {

            @Override
            public boolean touchedMark(final Mark mark) {
               ILogger.instance().logError(
                        "Marker positioned in:" + mark.getPosition().description() + ", URL:"
                                 + ((WpUserData) mark.getUserData()).getWpURL());

               runOnUiThread(new Runnable() {

                  @Override
                  public void run() {

                     final Intent intent = new Intent(G3MGlassesDemoMainActivity.this, WikipediaViewActivity.class);
                     intent.putExtra("url", "http://" + ((WpUserData) mark.getUserData()).getWpURL());
                     G3MGlassesDemoMainActivity.this.startActivity(intent);
                  }
               });


               return false;
            }
         }, true);

         _iconRenderer.addMark(m);

      }


   }
}
