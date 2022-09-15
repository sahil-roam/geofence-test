package com.local.geofencetest

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    lateinit var geofenceClient: GeofencingClient
    lateinit var addGeofenceButton: Button
    lateinit var removeGeofenceButton: Button
    lateinit var geofencingRequest: GeofencingRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestBackgroundPermission()
        registerLocalBroadcast()
        registerGeofence()

        addGeofenceButton = findViewById(R.id.add_geofence)
        removeGeofenceButton = findViewById(R.id.remove_geofence)

        addGeofenceButton.setOnClickListener {
            addGeofence()
        }

        removeGeofenceButton.setOnClickListener {
            removeGeofence()
        }

    }


    fun requestBackgroundPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                1001
            )
        }
    }



    fun getPendingIntent(): PendingIntent{
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE) //fix
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun registerGeofence(){
        geofenceClient = LocationServices.getGeofencingClient(applicationContext)
        var geofence: Geofence = Geofence.Builder()
            .setRequestId("request_key")
            .setCircularRegion(28.4421046, 77.0516732, 200f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(1000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .build()

        geofencingRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()

        Log.e("TAG", "registerGeofence: registered")
    }

    fun addGeofence(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("TAG", "registerGeofence: return")
            return
        }
        geofenceClient.addGeofences(geofencingRequest, getPendingIntent()).run {
            addOnSuccessListener {
                Log.e("TAG", "registerGeofence: geofence added")
            }
            addOnFailureListener {
                Log.e("TAG", "registerGeofence: geofence not added")
            }
        }


    }

    fun removeGeofence(){
        geofenceClient.removeGeofences(getPendingIntent()).run {
            addOnSuccessListener {
                Log.e("TAG", "registerGeofence: geofence removed")
            }
            addOnFailureListener {
                Log.e("TAG", "registerGeofence: geofence not removed")
            }
        }
    }

    fun registerLocalBroadcast(){

        var broadcastReceiver = (object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action.equals("MY_GEOFENCE")){
                    Log.e("TAG", "onReceive: received local broadcast")
                }
            }
        })

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(broadcastReceiver, IntentFilter("MY_GEOFENCE"))

    }




}