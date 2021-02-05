package com.arteriatech.ss.msecsales.rspl.common;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.Toast;

import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.ApplicationLifecycleHandler;
import com.arteriatech.ss.msecsales.rspl.autosync.NetworkChangeReceiver;
import com.arteriatech.ss.msecsales.rspl.service.InitialRegistrationIntentService;
import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.sybase.persistence.PrivateDataVault;

/**
 * Created by e10769 on 12-Apr-18.
 */

public class MSFAApplication extends Application {
    private boolean isFinished = false;
    private String serviceErrorMsg = "";
    private BroadcastReceiver openStoreReceiver = null;
    public static String ACTION_SERVICE_KEY = "actionService";
    public static String EXTRA_FINISHED_KEY = "isFinished";
    public static String EXTRA_ERROR_KEY = "serviceErrorMsg";
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
            registerActivityLifecycleCallbacks(handler);
            registerComponentCallbacks(handler);
            try {
                PrivateDataVault.init(MSFAApplication.this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                registerReceiver(new NetworkChangeReceiver(),new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                registerReceiver(new NetworkChangeReceiver(),new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
                @Override
                public void onAppNotResponding(final ANRError error) {
                    // Handle the error. For example, log it to HockeyApp:
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Your UI code here
                            Toast.makeText(MSFAApplication.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            LogManager.writeLogInfo(error.getMessage());
                        }
                    });
                }
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void startService(Context mContext, boolean isFromReg) {
        isFinished = false;
        serviceErrorMsg = "";
        InitialRegistrationIntentService.startInitialRegService(mContext, isFromReg);
    }

    public boolean isServiceFinished() {
        return isFinished;
    }

    public String getServiceError() {
        return serviceErrorMsg;
    }

    public void setBroadCastReceiver(BroadcastReceiver broadCastReceiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SERVICE_KEY);
        this.openStoreReceiver = broadCastReceiver;
        registerReceiver(openStoreReceiver, intentFilter);
    }

    public void unRegisterReceiver() {
        try {
            if (openStoreReceiver != null) {
                unregisterReceiver(openStoreReceiver);
                openStoreReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setServiceFinished(boolean isFinished, String serviceErrorMsg) {
        this.isFinished = isFinished;
        this.serviceErrorMsg = serviceErrorMsg;
        if (openStoreReceiver != null) {
            Intent intent = new Intent(ACTION_SERVICE_KEY);
            intent.putExtra(EXTRA_ERROR_KEY, serviceErrorMsg);
            intent.putExtra(EXTRA_FINISHED_KEY, isFinished);
            sendBroadcast(intent);
        }
    }
}
