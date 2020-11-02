package com.gpstrackers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.BreakIterator;
import java.util.Locale;


public class MainActivity extends Activity implements LocationListener {

    LocationManager locationManager;
    String provider;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);
//        Log.d("miFiltro","provider "+provider);
        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 2000, 1, this);
//            Log.d("miFiltro","providerrr "+provider);
            Log.d("miFiltro", "location latitude start " + location.getLatitude() + " longitude " + location.getLongitude());


            if (location != null) {
                onLocationChanged(location);
            } else {
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
        LocationRequest locationRequest;
        final LocationCallback[] locationCallback = new LocationCallback[1];
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.d("miFiltro", "location fusedLocationClient " + location);
                        if (location != null) {
                            // Logic to handle location object
                            onLocationChanged(location);

                        } else {
                            Toast.makeText(getBaseContext(), "Location can't be retrieved fusedLocationClient", Toast.LENGTH_SHORT).show();
                            LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationRequest.setInterval(20 * 1000);
                            locationCallback[0] = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) {
                                        return;
                                    }
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            double wayLatitude = location.getLatitude();
                                            double wayLongitude = location.getLongitude();
                                            BreakIterator txtLocation = null;
                                            txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                                        }
                                    }
                                }
                            };
                            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback[0], Looper.getMainLooper());
                        }
                    }
                });


    }


    @Override
    public void onLocationChanged(Location location) {
        // Getting reference to TextView tv_longitude
        TextView tvLongitude = (TextView)findViewById(R.id.tv_longitude);

        // Getting reference to TextView tv_latitude
        TextView tvLatitude = (TextView)findViewById(R.id.tv_latitude);
        Log.d("miFiltro","location latitude onLocationChanged "+location.getLatitude()+" longitude "+location.getLongitude());

        // Setting Current Longitude
        tvLongitude.setText("Longitude:" + location.getLongitude());

        // Setting Current Latitude
        tvLatitude.setText("Latitude:" + location.getLatitude() );
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}