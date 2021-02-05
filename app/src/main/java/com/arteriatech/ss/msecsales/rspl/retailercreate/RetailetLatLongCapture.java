package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.arteriatech.mutils.location.LocationServiceInterface;
import com.arteriatech.mutils.location.LocationUsingGoogleAPI;
import com.arteriatech.mutils.location.LocationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class RetailetLatLongCapture implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final long INTERVAL = 10000L;
    private static final long FASTEST_INTERVAL = 5000L;
    private static String TAG = LocationUsingGoogleAPI.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private static GoogleApiClient mGoogleApiClient = null;
    private Activity mContext;
    private LocationServiceInterface locationInterface = null;
    private Handler handler1 = new Handler();
    private Runnable runnable1 = null;
    private Handler handler = new Handler();
    private Runnable runnable = null;
    private boolean locationChanged = false;
    private boolean gotExactLocation = false;
    private Location altLocation = null;
    private float oldAccuracy = 1000.0F;
    private int totalAttempt = 1;
    private int currentAttempt = 1;
    public static Location location = null;
    private LocationInterfaceCapture locationInterface1 = null;
    public static RetailetLatLongCapture retailetLatLongCapture = null;
    public static RetailetLatLongCapture getInstance(Activity context)
    {
        if (retailetLatLongCapture == null) {
            retailetLatLongCapture = new RetailetLatLongCapture(context);
        }

        return retailetLatLongCapture;
    }

    private RetailetLatLongCapture(Activity mContext) {
        this.mContext = mContext;
//        this.locationInterface = locationInterface;
        this.initiLocationService(mContext);
    }

    private void initiLocationService(Context mContext) {
        if (!LocationUtils.isGPSEnabled(mContext)) {
            this.setError(500, "location disabled");
        } else {
            boolean gApiOk = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == 0;
            if (gApiOk) {
                this.createLocationRequest();
                this.mGoogleApiClient = (new GoogleApiClient.Builder(mContext)).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
                Log.d(TAG, "onStart fired ..............");
                this.mGoogleApiClient.connect();
            } else {
                this.setError(501, "Please install google play service");
            }
        }

    }

    private void setError(int errorCode, String msg) {
        try {
            Log.d(TAG, "err=>" + msg);
            this.disConnect();
            if (this.totalAttempt > this.currentAttempt) {
                if (errorCode == 508) {
                    this.setError(errorCode, msg, this.currentAttempt);
                    ++this.currentAttempt;
                    this.initiLocationService(this.mContext);
                } else {
                    this.currentAttempt = this.totalAttempt;
                    this.setError(errorCode, msg, this.currentAttempt);
                    this.locationInterface = null;
                }
            } else {
                this.setError(errorCode, msg, this.currentAttempt);
                this.locationInterface = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void setError(int errorCode, String msg, int currentAttempt) {
        if (this.locationInterface != null) {
            this.locationInterface.location(false, (Location)null, msg, errorCode, currentAttempt);
        }

    }

    private void setSuccess(Location location) {
        if (this.locationInterface != null && location != null) {
            this.locationInterface.location(true, location, "", 200, this.currentAttempt);
            this.locationInterface = null;
        } else if (this.locationInterface != null) {
            this.locationInterface.location(false, (Location)null, "Not able to get location ", 400, this.currentAttempt);
            this.locationInterface = null;
        }

        this.disConnect();
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (this.mGoogleApiClient != null) {
            Log.d(TAG, "onConnected - isConnected ...............: " + this.mGoogleApiClient.isConnected());
            this.startLocationUpdates();
        }

    }

    public void onConnectionSuspended(int i) {
        this.setError(504, "connection suspended");
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.setError(502, "connection failed");
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged..............");
        this.locationChanged = true;
        this.gotExactLocation = true;
        if(location!= null){
            this.location = location;
        }
        /*((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationInterface1.onLocationChangeCapture(location);
            }
        });*/
//        this.setSuccess(location);
    }

    private void disConnect() {
        try {
            if (mGoogleApiClient != null) {
                Log.d(TAG, "onStop fired ..............");
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
                mGoogleApiClient = null;
            }

            if (this.runnable1 != null) {
                this.handler1.removeCallbacks(this.runnable1);
                this.runnable1 = null;
            }

            if (this.runnable != null) {
                this.handler.removeCallbacks(this.runnable);
                this.runnable = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    protected void createLocationRequest() {
        new LocationRequest();
        this.mLocationRequest = LocationRequest.create();
        this.mLocationRequest.setInterval(10000L);
        this.mLocationRequest.setFastestInterval(5000L);
        this.mLocationRequest.setPriority(100);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            this.setError(503, "Please grant permission for Location in app settings");
        } else {
            if (this.mGoogleApiClient != null) {
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, this);
                Log.d(TAG, "Location update started ..............: ");
                if (this.runnable != null) {
                    this.handler.removeCallbacks(this.runnable);
                    this.runnable = null;
                }

                Log.d(TAG, "runnable started");
                this.runnable = new Runnable() {
                    public void run() {
                        if (!RetailetLatLongCapture.this.locationChanged) {
                            RetailetLatLongCapture.this.setError(508, RetailetLatLongCapture.this.mContext.getString(com.arteriatech.mutils.R.string.unable_to_get_location));
                        }

                    }
                };
                this.handler.postDelayed(this.runnable, 30000L);
            }

        }
    }

    public static Location onChangeLoc(){
        return location;
    }

    public static void onDestoryListern(){
        if(mGoogleApiClient!= null){
            mGoogleApiClient.disconnect();
//            mGoogleApiClient = null;
        }
        if(retailetLatLongCapture!= null){
            retailetLatLongCapture = null;
        }
    }
}
