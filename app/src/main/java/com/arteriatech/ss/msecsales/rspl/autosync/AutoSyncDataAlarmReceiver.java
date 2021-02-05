package com.arteriatech.ss.msecsales.rspl.autosync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by E10953 on 11-10-2019.
 */


public class AutoSyncDataAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "AutoSyncDataAlarmReceiver triggered");
        try {
            Intent i = new Intent(context, AutoSynDataService.class);
            i.putExtra("foo", "alarm!!");
            // i.putExtra("receiver", MySimpleReceiver.setupServiceReceiver(context));
            AutoSynDataService.enqueueWork(context, i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}