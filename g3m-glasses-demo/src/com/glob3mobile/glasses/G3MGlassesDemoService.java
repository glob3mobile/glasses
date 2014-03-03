

package com.glob3mobile.glasses;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;


public class G3MGlassesDemoService
         extends
            Service {

   private static final String LIVE_CARD_TAG = "G3MDemo";


   private TimelineManager     mTimelineManager;
   private LiveCard            mLiveCard;


   public G3MGlassesDemoService() {
      // TODO Auto-generated constructor stub
   }


   @Override
   public void onCreate() {
      super.onCreate();
      mTimelineManager = TimelineManager.from(this);
   }


   @Override
   public int onStartCommand(final Intent intent,
                             final int flags,
                             final int startId) {

      Log.e("Service killin app", "Starting the service");

      if (mLiveCard == null) {
         //create live card *New in XE12 createLiveCard replaced getLiveCard from XE11*
         mLiveCard = mTimelineManager.createLiveCard(LIVE_CARD_TAG);

         //set the views of the card from a xml file 
         mLiveCard.setViews(new RemoteViews(this.getPackageName(), R.layout.card_layout));

         //sets the menu of the card 
         final Intent glob3Intent = new Intent(this, G3MGlassesDemoMainActivity.class);
         glob3Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(glob3Intent);
         //mLiveCard.setAction(PendingIntent.getActivity(this, 0, menu, 0));

         //publish the card to the timeline 
         //Set publish mode to reveal *New in XE12 replaced setNonSilent method from XE11*
         mLiveCard.publish(LiveCard.PublishMode.REVEAL);
      }
      else {
         // TODO(alainv): Jump to the LiveCard when API is available.
      }

      return START_STICKY;
   }


   @Override
   public void onDestroy() {
      //remove the card from
      if ((mLiveCard != null) && mLiveCard.isPublished()) {
         mLiveCard.unpublish();
         mLiveCard = null;
      }
      Log.e("Service killin app", "System.exit(0)");
      System.exit(0);
      super.onDestroy();

   }


   @Override
   public IBinder onBind(final Intent arg0) {
      // TODO Auto-generated method stub
      return null;
   }

}
