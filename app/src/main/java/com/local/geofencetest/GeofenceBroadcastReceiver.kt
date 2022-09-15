package com.local.geofencetest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        val geofencingEvent = GeofencingEvent.fromIntent(p1!!)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("TAG", errorMessage)
            return
        }

        LocalBroadcastManager.getInstance(p0!!).sendBroadcast(Intent("MY_GEOFENCE"))

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.e("TAG", "onReceive: transition enter")
        }

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            Log.e("TAG", "onReceive: transition exit")
        }

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            Log.e("TAG", "onReceive: transition dwell")
        }


    }

}