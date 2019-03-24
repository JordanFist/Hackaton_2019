package com.google.location.nearby.apps.rockpaperscissors;

import android.graphics.Point;

class SafeArea extends AbstractPointOfInterest{

    /*
     *   compare 2 point
     *   arguments:
     *   return:
     */
    int survivors;
    SafeArea(Point p, String s, int nb){
        super(p, s);
        survivors = nb;
    }

    int cmp_pointofinterest(AbstractPointOfInterest A, AbstractPointOfInterest B){
        return 0;
    }

    String parameters(){
        return String.valueOf(survivors);
    }
}
