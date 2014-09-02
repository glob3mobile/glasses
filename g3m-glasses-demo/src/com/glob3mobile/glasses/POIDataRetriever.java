

package com.glob3mobile.glasses;

import java.util.ArrayList;

import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.G3MContext;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.IBufferDownloadListener;
import org.glob3.mobile.generated.IByteBuffer;
import org.glob3.mobile.generated.IDownloader;
import org.glob3.mobile.generated.IJSONParser;
import org.glob3.mobile.generated.JSONArray;
import org.glob3.mobile.generated.JSONBaseObject;
import org.glob3.mobile.generated.JSONObject;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.specific.JSONParser_Android;


public class POIDataRetriever {

   public POIDataRetriever() {
   }


   public static void getNearbyPlaces(final G3MContext context,
                                      final double lat,
                                      final double lon,
                                      final String types,
                                      final G3MGlassesDemoListener listener) {

      final IDownloader downloaderLocation = context.getDownloader();

      final IBufferDownloadListener listenerLocation = new IBufferDownloadListener() {

         @Override
         public void onDownload(final URL url,
                                final IByteBuffer buffer,
                                final boolean expired) {

            final String response = buffer.getAsString();
            final IJSONParser parser = new JSONParser_Android();
            final JSONBaseObject jsonObject = parser.parse(response);
            final JSONObject object = jsonObject.asObject();
            final JSONArray results = object.getAsArray("results");

            final ArrayList<GooglePlaceItem> gpitemArray = new ArrayList<GooglePlaceItem>();

            for (int i = 0; i < results.size(); i++) {

               final GooglePlaceItem item = new GooglePlaceItem();

               final JSONObject result = results.getAsObject(i);
               final JSONObject geometry = result.getAsObject("geometry");
               final JSONObject location = geometry.getAsObject("location");

               final double latItem = location.getAsNumber("lat", 0.0);
               final double lonItem = location.getAsNumber("lng", 0.0);

               final Geodetic3D position = new Geodetic3D(Angle.fromDegrees(latItem), Angle.fromDegrees(lonItem), 0.0);
               item.setPosition(position);

               final String urlIcon = result.getAsString("icon", "");
               final String name = result.getAsString("name", "");
               final String reference = result.getAsString("reference", "");
               item.setTitle(name);
               item.setReference(reference);
               item.setUrlIcon(urlIcon);
               gpitemArray.add(item);
            }

            listener.onGooglePlacePOIsRetrieved(gpitemArray);

         }


         @Override
         public void onError(final URL url) {
            // TODO Auto-generated method stub

         }


         @Override
         public void onCancel(final URL url) {
            // TODO Auto-generated method stub

         }


         @Override
         public void onCanceledDownload(final URL url,
                                        final IByteBuffer buffer,
                                        final boolean expired) {
            // TODO Auto-generated method stub

         }
      };


      downloaderLocation.requestBuffer(new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat
               + "," + lon + "&types=" + types
               + "&radius=500&sensor=false&key=AIzaSyA_5m4agh4i8voQ8fdEaeOgkpb1EJt0_OI", false),
               0, TimeInterval.fromHours(1), false, listenerLocation, false);


   }


   public static void getNearbyWikipediaArticles(final G3MContext context,
                                                 final double lat,
                                                 final double lon,
                                                 final G3MGlassesDemoListener listener) {


      final IDownloader downloaderLocation = context.getDownloader();

      final IBufferDownloadListener listenerLocation = new IBufferDownloadListener() {

         @Override
         public void onDownload(final URL url,
                                final IByteBuffer buffer,
                                final boolean expired) {

            final String response = buffer.getAsString();
            final IJSONParser parser = new JSONParser_Android();
            final JSONBaseObject jsonObject = parser.parse(response);
            final JSONObject object = jsonObject.asObject();
            final JSONArray articlesJson = object.getAsArray("geonames");

            final ArrayList<WikipediaArticle> articles = new ArrayList<WikipediaArticle>();

            for (int i = 0; i < articlesJson.size(); i++) {

               final JSONObject articleJson = articlesJson.getAsObject(i);
               final WikipediaArticle wa = new WikipediaArticle();

               final double latArticle = articleJson.getAsNumber("lat", 0.0);
               final double lonArticle = articleJson.getAsNumber("lng", 0.0);
               final double elevationArticle = articleJson.getAsNumber("elevation", 0.0);
               final Geodetic3D position = new Geodetic3D(Angle.fromDegrees(latArticle), Angle.fromDegrees(lonArticle),
                        elevationArticle);


               wa.setLat(latArticle);
               wa.setLon(lonArticle);
               wa.setPosition(position);
               wa.setSummary(articleJson.getAsString("summary", "no summary avalaible"));
               wa.setTitle(articleJson.getAsString("title", "title is not avalaible"));
               wa.setWikipediaURL(articleJson.getAsString("wikipediaUrl", "wikipediaUrl is not avalaible"));

               articles.add(wa);

            }


            listener.onWikipediaPOIsRetrieved(articles);

         }


         @Override
         public void onError(final URL url) {
            // TODO Auto-generated method stub

         }


         @Override
         public void onCancel(final URL url) {
            // TODO Auto-generated method stub

         }


         @Override
         public void onCanceledDownload(final URL url,
                                        final IByteBuffer buffer,
                                        final boolean expired) {
            // TODO Auto-generated method stub

         }

      };

      downloaderLocation.requestBuffer(new URL("http://api.geonames.org/findNearbyWikipediaJSON?lat=" + lat + "&lng=" + lon
                                               + "9&username=mdelacalle&radius=20", false), 0, TimeInterval.fromHours(1), false,
               listenerLocation, false);


   }
}
