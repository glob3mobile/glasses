

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


public class WikipediaDataRetriever {

   public WikipediaDataRetriever() {
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


            listener.onWikipediaArticlesRetrieved(articles);

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
