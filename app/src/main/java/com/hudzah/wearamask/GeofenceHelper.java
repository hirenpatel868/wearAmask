package com.hudzah.wearamask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeofenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;

    private int REQUEST_CODE = 2607;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(List<Geofence> geofences){

        Log.d(TAG, "getGeofencingRequest: Geofences builder wih the geofences list of, " + geofences.toString());

        return new GeofencingRequest.Builder()
                .addGeofences(geofences)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_DWELL) // Dont set as both, rather change dynamically
                .build();
    }

    public Geofence getGeofence(String ID, LatLng latLng, int radius, int transitionTypes){

        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(2000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            Log.d(TAG, "getPendingIntent: return old geofence intent");
            return pendingIntent;
        }

        Log.d(TAG, "getPendingIntent: pending intent called");
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public String getErrorString(Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";

                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";

                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";

            }
        }

        return e.getLocalizedMessage();
    }

}
