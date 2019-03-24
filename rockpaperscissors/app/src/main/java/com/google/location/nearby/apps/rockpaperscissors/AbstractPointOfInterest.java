package com.google.location.nearby.apps.rockpaperscissors;


import android.graphics.Point;

import java.lang.String;

abstract class AbstractPointOfInterest{
    private Point _localisation;
    private String _type;

    AbstractPointOfInterest(Point p, String s){
        _localisation = p;
        _type = s;
    }

    abstract String parameters();
    abstract int cmp_pointofinterest(AbstractPointOfInterest A, AbstractPointOfInterest B);

    Point getlocalisation(){
        return _localisation;
    }
    void setlocalisation(Point loc){
        _localisation = loc;
    }
    String gettype(){
        return _type;
    }
    void settype(String type){
        _type = type;
    }
}
