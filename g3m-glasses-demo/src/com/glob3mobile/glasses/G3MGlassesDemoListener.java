

package com.glob3mobile.glasses;

import java.util.ArrayList;


public interface G3MGlassesDemoListener {
   void onWikipediaPOIsRetrieved(ArrayList<WikipediaArticle> articles);


   void onGooglePlacePOIsRetrieved(ArrayList<GooglePlaceItem> articles);
}
