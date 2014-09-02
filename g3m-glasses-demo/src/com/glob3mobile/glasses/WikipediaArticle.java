

package com.glob3mobile.glasses;


public class WikipediaArticle
extends
G3MGlassItem {

   String _summary;
   String _wikipediaURL;
   double _lon;
   double _lat;


   public WikipediaArticle() {
      // TODO Auto-generated constructor stub
   }


   public String getSummary() {
      return _summary;
   }


   public void setSummary(final String summary) {
      _summary = summary;
   }


   public String getWikipediaURL() {
      return _wikipediaURL;
   }


   public void setWikipediaURL(final String wikipediaURL) {
      this._wikipediaURL = wikipediaURL;
   }


   public double getLon() {
      return _lon;
   }


   public void setLon(final double lon) {
      this._lon = lon;
   }


   public double getLat() {
      return _lat;
   }


   public void setLat(final double lat) {
      this._lat = lat;
   }


}
