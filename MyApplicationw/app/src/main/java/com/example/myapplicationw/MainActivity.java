package com.example.myapplicationw;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
  implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap ownMapboxMap;
    private GeoJsonSource geoJsonSourceWater;
    private GeoJsonSource geoJsonSourceFood;
    private GeoJsonSource geoJsonSourceSafe;
    private GeoJsonSource geoJsonSourceDanger;
    private List<Feature> featDanger = new ArrayList<>();
    private List<Feature> featFood = new ArrayList<>();
    private List<Feature> featSafe = new ArrayList<>();
    private List<Feature> featWater = new ArrayList<>();

    SymbolLayer symbolLayer;
    SymbolLayer symbolLayerSafe;
    SymbolLayer symbolLayerDanger;
    SymbolLayer symbolLayerWater;
    SymbolLayer symbolLayerFood;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private boolean isFABOpen;
    private int idLayer = 0;

    boolean dangerMrk = false;
    boolean safeMrk = false;
    boolean waterMrk = false;
    boolean foodMrk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 =  findViewById(R.id.fab1);
        fab2 =  findViewById(R.id.fab2);
        fab3 =  findViewById(R.id.fab3);
        fab4 =  findViewById(R.id.fab4);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangerButtonAct();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                safeButtonAct();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodButtonAct();
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waterButtonAct();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        mapView.getMapAsync(this);


    }

    public void dangerButtonAct() {
        dangerMrk = true;
        closeFABMenu();
    }

    public void safeButtonAct() {
        safeMrk = true;
        closeFABMenu();
    }

    public void foodButtonAct() {
        foodMrk = true;
        closeFABMenu();
    }

    public void waterButtonAct() {
        waterMrk = true;
        closeFABMenu();
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        ownMapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {


            @Override
            public void onStyleLoaded(@NonNull Style style) {

            }

        });
        ownMapboxMap.addOnMapClickListener(MainActivity.this);

    }

    void setUp(String idname, LatLng point, List<Feature> feats, int s) {
        Style style = ownMapboxMap.getStyle();
        style.addImage(idname + "-icon-id",
          BitmapFactory.decodeResource(
            getResources(), s));
        feats.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(),point.getLatitude())));
        if (s == R.drawable.redicon) {
            geoJsonSourceDanger = new GeoJsonSource(idname + "-id",
              FeatureCollection.fromFeatures(feats));
            style.addSource(geoJsonSourceDanger);
            symbolLayerDanger = new SymbolLayer("layer-id-" + idname, idname + "-id");
            symbolLayerDanger.withProperties(
              PropertyFactory.iconImage(idname + "-icon-id")
            );
            style.addLayer(symbolLayerDanger);
        } else if (s == R.drawable.yellowicon) {
            geoJsonSourceFood = new GeoJsonSource(idname + "-id",
              FeatureCollection.fromFeatures(feats));
            style.addSource(geoJsonSourceFood);
            symbolLayerFood = new SymbolLayer("layer-id-" + idname, idname + "-id");
            symbolLayerFood.withProperties(
              PropertyFactory.iconImage(idname + "-icon-id")
            );
            style.addLayer(symbolLayerFood);
        } else if (s == R.drawable.greenicon) {
            geoJsonSourceSafe = new GeoJsonSource(idname + "-id",
              FeatureCollection.fromFeatures(feats));
            style.addSource(geoJsonSourceSafe);
            symbolLayerSafe = new SymbolLayer("layer-id-" + idname, idname + "-id");
            symbolLayerSafe.withProperties(
              PropertyFactory.iconImage(idname + "-icon-id")
            );
            style.addLayer(symbolLayerSafe);
        } else if (s == R.drawable.blueicon) {
            geoJsonSourceWater = new GeoJsonSource(idname + "-id",
              FeatureCollection.fromFeatures(feats));
            style.addSource(geoJsonSourceWater);
            symbolLayerWater = new SymbolLayer("layer-id-" + idname, idname + "-id");
            symbolLayerWater.withProperties(
              PropertyFactory.iconImage(idname + "-icon-id")
            );
            style.addLayer(symbolLayerWater);
        }
    }

    private void addFeat(LatLng point, List<Feature> feats, GeoJsonSource geoJsonSource) {
        feats.add(Feature.fromGeometry(
          Point.fromLngLat(point.getLongitude(), point.getLatitude())));
        geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(feats));
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (dangerMrk || safeMrk || foodMrk || waterMrk) {
            System.out.println("" + point.getLongitude() + "," + point.getLatitude());
            Style style = ownMapboxMap.getStyle();
            idLayer ++;
            // Add the marker image to map
            if (dangerMrk) {
                if (symbolLayerDanger != null) {
                    style.addImage("danger-icon-id",
                      BitmapFactory.decodeResource(
                        getResources(), R.drawable.redicon));
                    symbolLayerDanger.withProperties(
                      PropertyFactory.iconImage("danger-icon-id")
                    );
                    addFeat(point, featDanger, geoJsonSourceDanger);
                } else {
                    setUp("danger", point, featDanger, R.drawable.redicon);
                }
            } else if (safeMrk) {
                if (symbolLayerSafe != null) {
                    style.addImage("safe-icon-id",
                      BitmapFactory.decodeResource(
                        getResources(), R.drawable.greenicon));
                    symbolLayerSafe.withProperties(
                      PropertyFactory.iconImage("safe-icon-id")
                    );
                    addFeat(point, featSafe, geoJsonSourceSafe);
                } else {
                    setUp("safe", point, featSafe, R.drawable.greenicon);
                }
            } else if (foodMrk) {
                if (symbolLayerFood != null) {
                    style.addImage("food-icon-id",
                      BitmapFactory.decodeResource(
                        getResources(), R.drawable.yellowicon));
                    symbolLayerFood.withProperties(
                      PropertyFactory.iconImage("food-icon-id")
                    );
                    addFeat(point, featFood, geoJsonSourceFood);
                } else {
                    setUp("food", point, featFood, R.drawable.yellowicon);
                }
            } else {
                if (symbolLayerWater != null) {
                    style.addImage("water-icon-id",
                      BitmapFactory.decodeResource(
                        getResources(), R.drawable.blueicon));
                    symbolLayerWater.withProperties(
                      PropertyFactory.iconImage("water-icon-id")
                    );
                    addFeat(point, featWater, geoJsonSourceWater);
                } else {
                    setUp("water", point, featWater, R.drawable.blueicon);
                }
            }

            dangerMrk = safeMrk = foodMrk = waterMrk = false;
        }
        return true;
    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
