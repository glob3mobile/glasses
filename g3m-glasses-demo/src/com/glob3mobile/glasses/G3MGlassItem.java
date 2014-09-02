

package com.glob3mobile.glasses;

import org.glob3.mobile.generated.Geodetic3D;


public class G3MGlassItem {


   private Geodetic3D _position;
   private String     _title;
   private String     _URL;


   public Geodetic3D getPosition() {
      return _position;
   }


   public void setPosition(final Geodetic3D position) {
      _position = position;
   }


   public String getTitle() {
      return _title;
   }


   public void setTitle(final String title) {
      _title = title;
   }


   public String getURL() {
      return _URL;
   }


   public void setURL(final String uRL) {
      _URL = uRL;
   }

}
