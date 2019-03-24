package com.google.location.nearby.apps.rockpaperscissors;

import android.graphics.Point;

class WaterSpot extends AbstractPointOfInterest{

    /*
     *   compare 2 point
     *   arguments:
     *   return:
     */
    int waterLevel;

    WaterSpot(Point p, String s, int nb){
        super(p, s);
        waterLevel = nb;
    }

    int cmp_pointofinterest(AbstractPointOfInterest A, AbstractPointOfInterest B){
        return 0;
    }

    String parameters(){
        return String.valueOf(waterLevel);
    }
}

