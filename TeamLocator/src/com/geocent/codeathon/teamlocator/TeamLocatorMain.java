/**
 * 
 */
package com.geocent.codeathon.teamlocator;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

/**
 * @author rnolen
 *
 */
public class TeamLocatorMain extends Activity
{
    private LocationManager locationManager;
    private LocationUpdateHandler updateListener;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.activity_team_map, menu );
        return true;
    }

    public void registerMember( View view ) {
        // TODO: call service here
        
        // Start getting location updates for this member
        updateListener = new LocationUpdateHandler();
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 10000, 0, updateListener );
        locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 10000, 0, updateListener );

        Intent intent = new Intent( this, TeamMap.class );
        startActivity( intent );
        
    }

    @Override
    public void onStop() {
        locationManager.removeUpdates( updateListener );
        super.onStop();
    }

    public class LocationUpdateHandler implements LocationListener {
        private static final int TWO_MINUTES = 1000 * 60 * 2;
        private Location lastLocation;
        
        @Override
        public void onLocationChanged( Location location ) {
            Log.d( "SMA", "---->DEBUG:  onLocationChanged fired for provider: " + location.getProvider() +", lat=" + location.getLatitude() +", long=" + location.getLongitude() );
            if( isBetterLocation( location, lastLocation ) ) {
                lastLocation = location;
                // TODO: call the service here to pass this location up
            }
        }

        @Override
        public void onProviderDisabled( String provider ) {
            Log.d( "SMA", "---->DEBUG:  onProviderDisabled" );
        }

        @Override
        public void onProviderEnabled( String provider ) {
            Log.d( "SMA", "---->DEBUG:  onProviderEnabled" );
        }

        @Override
        public void onStatusChanged( String provider, int status, Bundle extras ) {
            Log.d( "SMA", "---->DEBUG:  onStatusChanged" );
        }
        
        /** Determines whether one Location reading is better than the current Location fix
         * @param location  The new Location that you want to evaluate
         * @param currentBestLocation  The current Location fix, to which you want to compare the new one
         */
        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }
    
           // Check whether the new location fix is newer or older
           long timeDelta = location.getTime() - currentBestLocation.getTime();
           boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
           boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
           boolean isNewer = timeDelta > 0;
    
           // If it's been more than two minutes since the current location, use the new location
           // because the user has likely moved
           if (isSignificantlyNewer) {
               return true;
           // If the new location is more than two minutes older, it must be worse
           } else if (isSignificantlyOlder) {
               return false;
           }
    
           // Check whether the new location fix is more or less accurate
           int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
           boolean isLessAccurate = accuracyDelta > 0;
           boolean isMoreAccurate = accuracyDelta < 0;
           boolean isSignificantlyLessAccurate = accuracyDelta > 200;
    
           // Check if the old and new location are from the same provider
           boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
    
           // Determine location quality using a combination of timeliness and accuracy
           if (isMoreAccurate) {
               return true;
           } else if (isNewer && !isLessAccurate) {
               return true;
           } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
               return true;
           }
           return false;
       }

       /** Checks whether two providers are the same */
       private boolean isSameProvider(String provider1, String provider2) {
           if (provider1 == null) {
             return provider2 == null;
           }
           return provider1.equals(provider2);
       }    
   }
    
}
