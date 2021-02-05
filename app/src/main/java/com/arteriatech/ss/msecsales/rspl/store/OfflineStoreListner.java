package com.arteriatech.ss.msecsales.rspl.store;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.offline.ODataOfflineStore;
import com.sap.smp.client.odata.offline.ODataOfflineStoreListener;
import com.sap.smp.client.odata.offline.ODataOfflineStoreNotification;
import com.sap.smp.client.odata.offline.ODataOfflineStoreState;

public class OfflineStoreListner implements ODataOfflineStoreListener {

    private UIListener uiListener;
    private int operation;

    private final int SUCCESS = 0;
    private final int ERROR = -1;

    private Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SUCCESS) {
                // Notify the Activity the is complete
                String key = (String) msg.obj;
                try {
                    uiListener.onRequestSuccess(operation, key);
                } catch (ODataException e) {
                    e.printStackTrace();
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ERROR) {
                Exception e = (Exception) msg.obj;
                uiListener.onRequestError(operation, e);
            }
        }
    };


    public OfflineStoreListner(UIListener uiListener) {
        super();
        this.operation = Operation.GetStoreOpen.getValue();
        this.uiListener = uiListener;
    }

    /*****************
     * Utils Methods
     *****************/


    /**
     * Notify the OnlineUIListener that the request was successful.
     */
    protected void notifySuccessToListener(String key) {
        Message msg = uiHandler.obtainMessage();
        msg.what = SUCCESS;
        msg.obj = key;
        uiHandler.sendMessage(msg);

    }

    /**
     * Notify the OnlineUIListener that the request has an error.
     *
     * @param exception an Exception that denotes the error that occurred.
     */
    protected void notifyErrorToListener(Exception exception) {
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e(Constants.OfflineStoreOpenFailed+" :", exception);
        LogManager.writeLogError(Constants.OfflineStoreOpenedFailed+" : "+ exception.getMessage());

    }



    @Override
    public void offlineStoreStateChanged(ODataOfflineStore oDataOfflineStore, ODataOfflineStoreState oDataOfflineStoreState) {
//        OfflineManager.setStoreState(oDataOfflineStoreState.name());
        UtilOfflineManager.setStoreState(oDataOfflineStoreState.name());
       TraceLog.d(Constants.OfflineStoreStateChanged+" :" + oDataOfflineStoreState.toString());
    }

    @Override
    public void offlineStoreOpenFailed(ODataOfflineStore oDataOfflineStore, ODataException e) {
        TraceLog.e(Constants.OfflineStoreOpenFailed+" :", e);
        notifyErrorToListener(e);

    }

    @Override
    public void offlineStoreOpenFinished(ODataOfflineStore oDataOfflineStore) {
        TraceLog.d(Constants.OfflineStoreOpenFinished+" :" + oDataOfflineStore.toString());
        notifySuccessToListener(null);

    }

    @Override
    public void offlineStoreNotification(ODataOfflineStore oDataOfflineStore, ODataOfflineStoreNotification oDataOfflineStoreNotification) {

    }
}
