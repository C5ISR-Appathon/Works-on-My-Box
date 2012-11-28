package com.geocent.codeathon.teamlocator;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class TeamMap extends MapActivity
{
    private MapController mapController;
    private MapView mapView;
    private TeamOverlay itemizedOverlay;
    private MyLocationOverlay myLocationOverlay;

    public void onCreate( Bundle savedInstanceState ) {
        Log.d( "SMA", "---->DEBUG: onCreate" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_team_map );
        
        // Configure the map
        mapView = (MapView) findViewById( R.id.mapview );
        mapView.setBuiltInZoomControls( true );
        mapView.setSatellite( true );
        mapController = mapView.getController();
        mapController.setZoom( 14 );
        
        
        myLocationOverlay = new MyLocationOverlay( this, mapView );
        mapView.getOverlays().add( myLocationOverlay );
        
        myLocationOverlay.runOnFirstFix( new Runnable() {
            public void run() {
                mapView.getController().animateTo( myLocationOverlay.getMyLocation() );
            }
        });
        
        Drawable drawable = this.getResources().getDrawable( R.drawable.point );
        itemizedOverlay = new TeamOverlay( drawable, this );
        createMarker();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    private void createMarker() {
        GeoPoint p = mapView.getMapCenter();
        OverlayItem overlayItem = new OverlayItem( p, "", "" );
        itemizedOverlay.addOverlay( overlayItem );
        if( itemizedOverlay.size() > 0 ) {
            mapView.getOverlays().add( itemizedOverlay );
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableCompass();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableCompass();
    }
    
}
