package com.arteriatech.ss.msecsales.rspl.sync;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.ITrafficSpeedListener;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.TrafficSpeedMeasurer;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.Utils;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.RefreshListInterface;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.SyncSelectionViewActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class shows selection type of sync icons in grid manner.
 */

@SuppressLint("NewApi")
public class SyncSelectionActivity extends AppCompatActivity implements UIListener, OnlineODataInterface {
    GridView grid_main;
    String iconName[] = {"All Download", "Download", "Upload", "Sync History"};
    int OriginalStatus[] = {1, 1, 1, 1};
    int TempStatus[] = {1, 1, 1, 1};
    String[][] invKeyValues;
    ArrayList<String> tempCPList = new ArrayList<>();
    int mIntPendingCollVal = 0;

    ProgressDialog syncProgDialog;
    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    int[] cancelSOCount = new int[0];
    int updateCancelSOCount = 0;
    int cancelSoPos = 0;
    String endPointURL = "";
    String syncHistoryType = "";
    String appConnID = "";
    int mError = 0;
    boolean onlineStoreOpen = false;
    private boolean dialogCancelled = false;
    private int penReqCount = 0;
    private boolean mBoolIsNetWorkNotAval = false;
    private boolean mBoolIsReqResAval = false;
    private boolean isBatchReqs = false;
    private boolean tokenFlag = false;
    private boolean isClickable = false;
    private GUID refguid =null;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private int networkErrorCount=0,networkError=0;
    Thread networkThread;
    private boolean isMonitoringStopped = false;
    private LoadingData loadingAsyncTask = null;
    private boolean isNetwrokErrAlert = false;
    private boolean isRefreshDB = false;


    public static ArrayList<String> getRefreshList(final Context context) {
        final ArrayList<String> alAssignColl = new ArrayList<>();
        Thread thread =null;
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (OfflineManager.getVisitStatusForCustomer(Constants.ChannelPartners + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.ChannelPartners);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.CPDMSDivisions + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.CPDMSDivisions);
                        }

                        if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.Attendances);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.Visits);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.CompetitorInfos);
                        }

                        if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.MerchReviews);
                            alAssignColl.add(Constants.MerchReviewImages);
                        }

                        if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.VisitActivities);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPDocuments + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.SchemeCPDocuments);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.Claims + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.Claims);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.ClaimDocuments + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.ClaimDocuments);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.ClaimDocuments + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.ClaimDocuments);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.CPStockItems + "?$filter= sap.islocal() ")) {
                            alAssignColl.addAll(SyncUtils.getRetailerStock());
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPs + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.SchemeCPs);
                        }

                        if (OfflineManager.getVisitStatusForCustomer(Constants.Complaints + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.Complaints);
                            alAssignColl.add(Constants.ComplaintDocuments);
                        }
                        if (OfflineManager.getVisitStatusForCustomer(Constants.ExpenseDocuments + "?$filter= sap.islocal() ")) {
                            alAssignColl.add(Constants.Expenses);
                            alAssignColl.add(Constants.ExpenseItemDetails);
                            alAssignColl.add(Constants.ExpenseDocuments);
                        }
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                        if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 25 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys)) {
                            if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + Constants.isLocalFilterQry)) {
                                alAssignColl.add(Constants.SyncHistorys);
                            }
                        }

                /*if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + "?$filter= sap.islocal() ")) {
                    alAssignColl.add(Constants.SyncHistorys);
                }*/

                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError("Error : " + e.getMessage());
                    }

                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (thread!=null&&thread.isAlive()){
                try {
                    thread.interrupt();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return alAssignColl;
    }

    public static ArrayList<Object> getPendingCollList(Context mContext, boolean isFromAutoSync) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Feedbacks;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SecondarySOCreate;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.FinancialPostings;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SampleDisbursement;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CPList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.ROList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }

        return objectsArrayList;

    }

    private static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();
        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        return size;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        // ActionBarView.initActionBarView(this, true, getString(R.string.syncmenu));
        setContentView(R.layout.activity_sync_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sync_menu), 0);
        if (!Constants.restartApp(SyncSelectionActivity.this)) {
            SharedPreferences sharedPreferences = SyncSelectionActivity.this.getSharedPreferences(Constants.LOGPREFS_NAME, 0);
            if (sharedPreferences.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
            }
            onInitUI();
            setIconVisiblity();
            setValuesToUI();
        }
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);


    }

    /*
     * This method initialize UI
     */
    private void onInitUI() {
        grid_main = (GridView) findViewById(R.id.GridView01);

    }

    /*
  This method set values to UI
  */
    private void setValuesToUI() {
        grid_main.setAdapter(new SyncAdapter(this));
    }

    private void setIconVisiblity() {
        OriginalStatus[0] = 1;
        OriginalStatus[1] = 1;
        OriginalStatus[2] = 1;
        OriginalStatus[3] = 1;
        int countStatus = 0;
        int len = OriginalStatus.length;
        for (int countOriginalStaus = 0; countOriginalStaus < len; countOriginalStaus++) {
            if (OriginalStatus[countOriginalStaus] == 1) {
                TempStatus[countStatus] = countOriginalStaus;
                countStatus++;
            }
        }
    }

    private void checkPendingReqIsAval() {
        try {
            mIntPendingCollVal = 0;
            invKeyValues = null;
            ArrayList<Object> objectArrayList = getPendingCollList(SyncSelectionActivity.this, false);
            if (!objectArrayList.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayList.get(0);
                invKeyValues = (String[][]) objectArrayList.get(1);
            }

            penReqCount = 0;


            alAssignColl.clear();
            alFlushColl.clear();
            concatCollectionStr = "";
            concatFlushCollStr = "";
            ArrayList<String> allAssignColl = getRefreshList(SyncSelectionActivity.this);
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setEmpty(){
        HashSet companySet = new HashSet();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOT_POSTED_RETAILERS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(Constants.duplicateCPList, companySet);
        editor.commit();
    }

    /**
     * This method update pending requests.
     */

    private void startNetworkMonitoring(){
        mTrafficSpeedMeasurer.startMeasuring();
        isMonitoringStopped = true;
        checkNetwork(this, new OnNetworkInfoListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onNetworkFailureListener(boolean isFailed) {
                if (isFailed) {
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(SyncSelectionActivity.this, "Network dropped / unable to connect.", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }
        },false,0);

    }

    public void removeFromDatavault(Context context){
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove SO orders from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove SO orders from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreate, deviceNo, false);
                        Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreateTemp, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove SO orders from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreate, deviceNo, false);
                            Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreateTemp, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove Feedbacks from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.Feedbacks, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove Feedbacks from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.Feedbacks, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove FinancialPostings from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.FinancialPostings, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove FinancialPostings from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.FinancialPostings, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove SampleDisbursement from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.SampleDisbursement, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove SampleDisbursement from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.SampleDisbursement, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove CPList from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.CPList, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove CPList from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.CPList, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove ROList from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.ROList, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove ROList from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.ROList, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove Expenses from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.Expenses, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove Expenses from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.Expenses, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void onUpdateSync() {
        try {
            setEmpty();
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            tempCPList.clear();
            mBoolIsNetWorkNotAval = false;
            isBatchReqs = false;
            mBoolIsReqResAval = true;
            updateCancelSOCount = 0;
            cancelSoPos = 0;
            try {
                try {
                    removeFromDatavault(this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                checkPendingReqIsAval();

                if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
                    showAlert(getString(R.string.no_req_to_update_sap));
                } else {
                    if (Constants.iSAutoSync||Constants.isBackGroundSync||Constants.isPullDownSync) {
                        if (Constants.iSAutoSync) {
                            showAlert(getString(R.string.alert_auto_sync_is_progress));
                        }else{
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        }
                    }else {
                        if (mIntPendingCollVal > 0) {

                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);

                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                startNetworkMonitoring();

                                Constants.isSync = true;
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : Started");
                                onPostOnlineData();
                            } else {
 if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + getString(R.string.no_network_conn));
                                showAlert(getString(R.string.no_network_conn));
                            }
                        } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                startNetworkMonitoring();
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                                onPostOfflineData();
                            } else {
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("Upload Sync : " + getString(R.string.no_network_conn));
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    }
                }
            } catch (ODataException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onPostOfflineData() {
        Constants.isSync = true;
        try {
            new AsyncPostOfflineData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostOnlineData() {
        try {
            new PostingDataValutData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSyncAll() {
        try {
            startNetworkMonitoring();
            Constants.AL_ERROR_MSG.clear();
            Constants.Entity_Set.clear();
            Constants.isSync = true;
            dialogCancelled = false;
            Constants.isStoreClosed = false;
            loadingAsyncTask = new LoadingData();
            loadingAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method calls sync all collections for the selected "All" icon
     */
    private void onAllSync() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            try {
                if(!OfflineManager.getSyncStatus(Constants.Sync_All)) {
                    syncAll();
                }else {
                    syncAlreadyDoneorNotMsg();
                }
            } catch (OfflineODataStoreException e) {
                syncAll();
                e.printStackTrace();
            }

        } else {
            showAlert(getString(R.string.no_network_conn));
        }
    }

    private void syncAlreadyDoneorNotMsg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.all_sync_confirmation)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                syncAll();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                isClickable = false;
                            }
                        });


        builder.show();
    }

    private void syncAll(){
        if (Constants.iSAutoSync) {
            showAlert(getString(R.string.alert_auto_sync_is_progress));
        }else {
            onSyncAll();
        }
    }
    /**
     * This method calls fresh sync for the selected "Fresh" icon
     */
    private void onFreshSync() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            try {
                if(!OfflineManager.getSyncStatus(Constants.DownLoad)) {
                    Intent intent = new Intent(this, SyncSelectionViewActivity.class);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
                    builder.setMessage(R.string.download_sync_confirmation)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(SyncSelectionActivity.this, SyncSelectionViewActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                            .setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            isClickable = false;
                                        }
                                    });


                    builder.show();
                }
            } catch (OfflineODataStoreException e) {
                Intent intent = new Intent(SyncSelectionActivity.this, SyncSelectionViewActivity.class);
                startActivity(intent);
                e.printStackTrace();
            }
        } else {
            showAlert(getString(R.string.no_network_conn));
        }
    }

    private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.addAll(SyncUtils.getAllSyncValue(SyncSelectionActivity.this));
        for (int i = 0; i < alAssignColl.size(); i++) {

            if(Constants.writeDebug)
                LogManager.writeLogDebug("All Sync Starts:" + alAssignColl.get(i));
        }
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
    }

    private void displayProgressDialog() {
        try {
            syncProgDialog
                    .show();
            syncProgDialog
                    .setCancelable(true);
            syncProgDialog
                    .setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogCancelled = false;
    }

    private void setAppointmentNotification() {
//        new NotificationSetClass(this);//TODO need to enable

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickable=false;
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }
    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            if (SyncSelectionActivity.this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                        String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                        if (upStream <= 0 || downStream <= 0)
                            networkErrorCount++;
                        else
                            networkErrorCount = 0;

                        if ((upStream != 0 && upStream < 1) || (downStream != 0 && downStream < 1))
                            networkError++;
                        else
                            networkError = 0;

                        if (networkErrorCount >= 3) {
                            networkErrorCount = 0;
                            isNetwrokErrAlert = true;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            if (loadingAsyncTask != null)
                                loadingAsyncTask.cancel(true);
                            closingProgressDialog();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                        @Override
                                        public void clickedStatus(boolean b) {
                                            if (b) {
                                                isNetwrokErrAlert = false;
                                                if(refguid==null){
                                                    refguid = GUID.newRandom();
                                                }
                                                if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }
                                                onBackPressed();
                                            } else {
                                                isNetwrokErrAlert = false;
                                            }
                                        }
                                    });
                                }
                            });


                        } else if (networkError >= 3) {
                            networkError = 0;
                            isNetwrokErrAlert = true;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            if (loadingAsyncTask != null)
                                loadingAsyncTask.cancel(true);
                            closingProgressDialog();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                        @Override
                                        public void clickedStatus(boolean b) {
                                            if (b) {
                                                isNetwrokErrAlert = false;
                                                if(refguid==null){
                                                    refguid = GUID.newRandom();
                                                }
                                                if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                }
                                                onBackPressed();
                                            } else {
                                                isNetwrokErrAlert = false;
                                            }
                                        }
                                    });
                                }
                            });


                        }

                        Log.d("Network_Bandwidth", "Values" + upStreamSpeed + "--" + downStreamSpeed);
                    }
                });
            }
        }
    };

    private static boolean isActiveNetwork(Context context){
        return isConnected(context);
    }
    private static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        boolean isMobile = false;
        boolean isConnected = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        if (isConnected) {
            if (isWiFi) {
                return isConnectedToThisServer();
            }
            if (isMobile) {
                return isConnectedToThisServer();
            }
        } else {
            return false;
        }
        return false;
    }
    private static boolean isConnectedToThisServer() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public interface OnNetworkInfoListener{
        void onNetworkFailureListener(boolean isFailed);
    }
    private static boolean isNetworkStopped;
    public  void checkNetwork(final Context context, final OnNetworkInfoListener networkInfoListener, boolean isInterupted, final int delayInSec){
        if (!isInterupted) {
            isNetworkStopped=false;
            networkThread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    check(context, networkInfoListener,delayInSec);
                }
            });
            networkThread.start();
        }else{
            isNetworkStopped =true;
        }
    }
    private  void check(Context context,OnNetworkInfoListener networkInfoListener, int delayInSec){
        if (!isNetworkStopped) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isError = isActiveNetwork(context);
            if (!isError) {
                networkErrorCount++;
                if(networkErrorCount>=3){
                    networkErrorCount=0;
                    isNetwrokErrAlert=true;
                    isMonitoringStopped = false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    if(loadingAsyncTask!=null)
                        loadingAsyncTask.cancel(true);
                    closingProgressDialog();
                    if (SyncSelectionActivity.this != null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", "Sync can't perform due to network drop", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                    @Override
                                    public void clickedStatus(boolean b) {
                                        if (b) {
                                            isNetwrokErrAlert = false;
                                            if(refguid==null){
                                                refguid = GUID.newRandom();
                                            }
                                            if(syncHistoryType.equalsIgnoreCase(Constants.UpLoad)){
                                                SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.upload_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                            }else if(syncHistoryType.equalsIgnoreCase(Constants.Sync_All)){
                                                SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.download_all_net_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                            }
                                            onBackPressed();
                                        } else {
                                            isNetwrokErrAlert = false;
                                        }
                                    }
                                });

                            }
                        });
                    }

                }

                Log.e("CHECKING NETWORK", "NETWORK ERROR");

                if (networkInfoListener != null && isMonitoringStopped) {
                    networkInfoListener.onNetworkFailureListener(true);
                    check(context, networkInfoListener,delayInSec);
                }
            } else {
                if (networkInfoListener != null && isMonitoringStopped) {
                    Log.e("CHECKING NETWORK", "NETWORK ACTIVE");
                    networkInfoListener.onNetworkFailureListener(false);
                    check(context, networkInfoListener, delayInSec);
                }
            }
        }
    }



    @Override
    public void onRequestError(int operation, Exception exception) {
        isClickable=false;
        final ErrorBean errorBean = Constants.getErrorCode(operation, exception, SyncSelectionActivity.this);
        LogManager.writeLogDebug("Sync Failed : " + operation + ":" + exception.getLocalizedMessage());
        try {
            SharedPreferences mSharedPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
            String customeNEO = Constants.checkUnknownNetworkerror(errorBean.getErrorMsg(), SyncSelectionActivity.this);

            if (!TextUtils.isEmpty(errorBean.getErrorMsg()) && errorBean.getErrorMsg().contains("10348") && !isRefreshDB) {
                isRefreshDB = true;
                if (mSharedPrefs.getBoolean("flagTointializeDB", false)) {
                    closingProgressDialog();
                    syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
                    syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
                    syncProgDialog.setCancelable(true);
                    syncProgDialog.setCanceledOnTouchOutside(false);
                    syncProgDialog.show();
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        Constants.isSync = true;
                        Constants.closeStore(SyncSelectionActivity.this);
                        new RefreshAsyncTask(SyncSelectionActivity.this, "", this).execute();
                    }
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    try {
                        editor.putBoolean("flagTointializeDB", false);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    SharedPreferences.Editor editor = mSharedPrefs.edit();
                    try {
                        editor.putBoolean("flagTointializeDB", true);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.error_10348) + "Kindly Authenticate again", "OK", "", false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            Constants.iSAutoSync = false;
                            UpdatePendingRequest.instance = null;
                            if (OfflineManager.offlineStore != null) {
                                try {
                                    OfflineManager.offlineStore.closeStore();
                                } catch (ODataException e) {
                                    e.printStackTrace();
                                }
                            }
                            OfflineManager.offlineStore = null;
                            OfflineManager.options = null;
                            android.os.Process.killProcess(android.os.Process.myPid());
                                finishAffinity();
                            System.exit(0);

                        }
                    });

                }
            } else {
                if (customeNEO.equalsIgnoreCase("")) {
                if (errorBean.hasNoError()) {

                    if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                        if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                            tempCPList.add(invKeyValues[penReqCount][0]);
                            Constants.removeFromSharKey(invKeyValues[penReqCount][0], errorBean.getErrorMsg(), this, false);
                        }
                    }

                    mError++;
                    penReqCount++;
                    mBoolIsReqResAval = true;


                    if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                        try {
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                                    try {
                                        new AsyncPostOfflineData().execute();
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                } else {
                                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                            alAssignColl.add(Constants.ConfigTypsetTypeValues);

                                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                        new RefreshAsyncTask(SyncSelectionActivity.this, concatCollectionStr, this).execute();
                                    } else {
                                        Constants.isSync = false;
                                        closingProgressDialog();
                                        showAlert(getString(R.string.data_conn_lost_during_sync));
                                    }
                                }
                            } else {
                                Constants.isSync = false;
                                closingProgressDialog();
                                showAlert(getString(R.string.data_conn_lost_during_sync));
                            }


                        } catch (ODataException e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (operation == Operation.OfflineFlush.getValue()) {
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                            concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                            LogManager.writeLogInfo(concatCollectionStr + " refresh started");
                            new RefreshAsyncTask(SyncSelectionActivity.this, concatCollectionStr, this).execute();
                        } else {
                            Constants.isSync = false;
                            closingProgressDialog();
                            showAlert(getString(R.string.data_conn_lost_during_sync));
                        }

                    } else if (operation == Operation.OfflineRefresh.getValue()) {
                        LogManager.writeLogError("Error : " + exception.getMessage());
                        Constants.isSync = false;
                        String mErrorMsg = "";
                        if (Constants.AL_ERROR_MSG.size() > 0) {
                            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                        }
                        final String finalMErrorMsg = mErrorMsg;
                        Constants.updateLastSyncTimeToTable(SyncSelectionActivity.this, alAssignColl, syncHistoryType, new RefreshListInterface() {
                            @Override
                            public void refreshList() {
                                closingProgressDialog();
                                if (finalMErrorMsg.equalsIgnoreCase("")) {
                                    if(errorBean.getErrorMsg().contains("invalid authentication")||errorBean.getErrorMsg().contains("HTTP Status 401 ? Unauthorized")){

                                        try {
                                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
                                            String loginUser=sharedPreferences.getString("username","");
                                            String login_pwd=sharedPreferences.getString("password","");
                                            UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                @Override
                                                public void passwordStatus(final JSONObject jsonObject) {

                                                    if(!isFinishing()) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Constants.passwordStatusErrorMessage(SyncSelectionActivity.this, jsonObject, loginUser);
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
//                                        Constants.customAlertDialogWithScrollError(SyncSelectionActivity.this, errorBean.getErrorMsg(), SyncSelectionActivity.this);
                                    } else {
                                        showAlert(errorBean.getErrorMsg());
                                    }

                                } else {
                                    if(errorBean.getErrorMsg().contains("invalid authentication")||errorBean.getErrorMsg().contains("HTTP Status 401 ? Unauthorized")){

                                        try {
                                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
                                            String loginUser=sharedPreferences.getString("username","");
                                            String login_pwd=sharedPreferences.getString("password","");
                                            UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                                @Override
                                                public void passwordStatus(final JSONObject jsonObject) {

                                                    if(!isFinishing()) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Constants.passwordStatusErrorMessage(SyncSelectionActivity.this, jsonObject, loginUser);
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
//                                        Constants.customAlertDialogWithScrollError(SyncSelectionActivity.this, errorBean.getErrorMsg(), SyncSelectionActivity.this);
                                    } else {
                                        Constants.customAlertDialogWithScrollError(SyncSelectionActivity.this, finalMErrorMsg, SyncSelectionActivity.this);
                                    }
                                }
                            }
                        }, refguid.toString().toUpperCase());

                    } else if (operation == Operation.GetStoreOpen.getValue()) {
                        Constants.isSync = false;
                        closingProgressDialog();
                        showAlert(getString(R.string.msg_offline_store_failure));
                    }
                } else {
                    mBoolIsReqResAval = true;
                    mBoolIsNetWorkNotAval = true;
                    Constants.isSync = false;
                    if (errorBean.isStoreFailed()) {
                        OfflineManager.offlineStore = null;
                        OfflineManager.options = null;
                        try {
                            if (!OfflineManager.isOfflineStoreOpen()) {
                                closingProgressDialog();
                                try {
                                    new OpenOfflineStore().execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                closingProgressDialog();
                                Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                            }
                        } catch (Exception e) {
                            closingProgressDialog();
                            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                            e.printStackTrace();
                        }
                    } else {
                        closingProgressDialog();
                        Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                    }


                }
            }else{
                    closingProgressDialog();
                    UtilConstants.showAlert(customeNEO, SyncSelectionActivity.this);

                }
        }
        } catch (Exception e) {
            mBoolIsReqResAval = true;
            mBoolIsNetWorkNotAval = true;
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
        }

    }

    @Override
    public void onRequestSuccess(int operation, String key) throws OfflineODataStoreException {

        isClickable=false;
        if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
            mBoolIsReqResAval = true;
            if(Constants.writeDebug)
                LogManager.writeLogDebug("Upload Sync Create Success");
            /*
             if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FinancialPostings)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.FinancialPostings, invKeyValues[penReqCount][0]);
            }
            if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
            } else */
           /* if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SecondarySOCreate)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SecondarySOCreate, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Feedbacks)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Feedbacks, invKeyValues[penReqCount][0]);
            }*/ /*else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.InvList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.ROList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.ROList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
            }else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Expenses, invKeyValues[penReqCount][0]);
            }else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CPList, invKeyValues[penReqCount][0]);
            }*/
            Constants.removeDataValtFromSharedPref(SyncSelectionActivity.this, invKeyValues[penReqCount][1], invKeyValues[penReqCount][0], false);
            ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",this);
            if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SecondarySOCreate)) {
                Constants.removeDataValtFromSharedPref(SyncSelectionActivity.this,Constants.SecondarySOCreateTemp, invKeyValues[penReqCount][0]+"_temp", false);
                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0]+"_temp", "",this);
            }


            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        try {
                            new AsyncPostOfflineData().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Constants.isSync = false;
                        closingProgressDialog();
                        showAlert(getString(R.string.data_conn_lost_during_sync));
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                            alAssignColl.add(Constants.ConfigTypsetTypeValues);

                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                        if(Constants.writeDebug) {
                            LogManager.writeLogDebug("Upload Sync flush collections posting : " + concatCollectionStr);
                        }
                        new RefreshAsyncTask(SyncSelectionActivity.this, concatCollectionStr, this).execute();
                    } else {
                        Constants.isSync = false;
                        closingProgressDialog();
                        showAlert(getString(R.string.data_conn_lost_during_sync));
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                    alAssignColl.add(Constants.ConfigTypsetTypeValues);
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                if(Constants.writeDebug) {
                    LogManager.writeLogDebug("Upload Sync Offline flush collections posting : " + concatCollectionStr);
                }
                new RefreshAsyncTask(SyncSelectionActivity.this, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                closingProgressDialog();
                showAlert(getString(R.string.data_conn_lost_during_sync));
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {

            if (alAssignColl.contains(Constants.UserProfileAuthSet)) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

            if (alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
                Constants.alTodayBeatRet.clear();
//                Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
//                Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
            }
            if(alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners) ) {
                Constants.setBirthdayListToDataValut(SyncSelectionActivity.this);
                Constants.setBirthDayRecordsToDataValut(SyncSelectionActivity.this);
                Constants.setAppointmentNotification(SyncSelectionActivity.this);
            }
//            if(alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
//                Constants.loadingTodayAchived(SyncSelectionActivity.this,Constants.alTodayBeatRet);
//            }

            if(alAssignColl.contains(Constants.MerchReviews)) {
                Constants.deleteDeviceMerchansisingFromDataVault(SyncSelectionActivity.this);
            }
            if(alAssignColl.contains(Constants.Alerts)) {
                Constants.setAlertsRecordsToDataValut(SyncSelectionActivity.this);
            }
            // Staring MustSell Code Snippet

//            if(tempCPList.size()>0){
//                OfflineManager.getCPListFromDataValt(SyncSelectionActivity.this,tempCPList);
//            }

            if(tempCPList.size()>0){
                HashSet companySet = new HashSet(tempCPList);
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOT_POSTED_RETAILERS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(Constants.duplicateCPList, companySet);
                editor.commit();
            }else {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOT_POSTED_RETAILERS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
            }

            String mErrorMsg = "";
            if (Constants.AL_ERROR_MSG.size() > 0) {
                mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
            }


            Constants.isSync = false;
            final String finalMErrorMsg = mErrorMsg;
            Constants.updateLastSyncTimeToTable(SyncSelectionActivity.this, alAssignColl, syncHistoryType, new RefreshListInterface() {
                @Override
                public void refreshList() {
                    closingProgressDialog();

                    if (mError == 0) {
                        ConstantsUtils.startAutoSync(SyncSelectionActivity.this, false);
                        if (Constants.writeDebug)
                            LogManager.writeLogDebug("Sync : Completed");
                        UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, SyncSelectionActivity.this, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                            }
                        });
                        isMonitoringStopped=false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    } else if(!isNetwrokErrAlert) {
                        if (finalMErrorMsg.equalsIgnoreCase("")) {
                            if (Constants.writeDebug)
                                LogManager.writeLogDebug("Sync : "+finalMErrorMsg+ "Failed");
                            showAlert(getString(R.string.error_occured_during_post));
                        } else {
                            Constants.customAlertDialogWithScrollError(SyncSelectionActivity.this, finalMErrorMsg,SyncSelectionActivity.this);
                        }
                    }
                }
            },refguid.toString().toUpperCase());

        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
//                Constants.ReIntilizeStore =false;
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(SyncSelectionActivity.this, Constants.Sync_All);
            setStoreOpenUI();
        }
    }

    private void setStoreOpenUI() {
        closingProgressDialog();
        showAlert(getString(R.string.msg_offline_store_success));
    }

    private void closingProgressDialog() {
        try {
            if(syncProgDialog!=null)
                syncProgDialog.dismiss();
            syncProgDialog=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onSyncHist() {
        Intent intent = new Intent(this, SyncHistoryActivity.class);
        intent.putExtra(Constants.comingFrom,"SyncHist");
        startActivity(intent);
    }

    private void onSyncHistColl() {
        Intent intent = new Intent(this, SyncHistoryActivity.class);
        intent.putExtra(Constants.comingFrom,"SyncHistColl");
        startActivity(intent);
    }

    private void onPendingCount() {
        Intent intent = new Intent(this, SyncHistoryActivity.class);
        intent.putExtra(Constants.comingFrom,"PendingCount");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        isClickable=false;
//        String type = bundle != null ? bundle.getString(Constants.BUNDLE_RESOURCE_PATH) : "";
//        Log.d("responseSuccess", "responseSuccess: " + type);
       /* if (!isBatchReqs) {
            switch (type) {
                case Constants.RouteSchedules:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
                case Constants.CollectionPlan:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
            }
            isBatchReqs = true;
        }*/
    }

    @Override
    public void responseFailed(final ODataRequestExecution request, String s, Bundle bundle) {
        isClickable=false;
        Log.d("SyncError", "responseFailed: " + s);
        /*if (!isBatchReqs) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TraceLog.scoped(this).d(Constants.RequestFailed);
                    if (request != null && request.getResponse() != null) {
                        ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                        if (payload != null && payload instanceof ODataError) {
                            ODataError oError = (ODataError) payload;
                            TraceLog.d(Constants.RequestFailed_status_message + oError.getMessage());
                            try {
                                ODataRequestParamSingle oDataResponseSingle = (ODataRequestParamSingleDefaultImpl) request.getRequest();
                                ODataEntity oDataEntity = (ODataEntity) oDataResponseSingle.getPayload();
                                Constants.Entity_Set.add(oDataEntity.getResourcePath());
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                            LogManager.writeLogError(Constants.Error + " :" + oError.getMessage());
                            Constants.AL_ERROR_MSG.add(oError.getMessage());
                            onRequestError(Operation.Update.getValue(), new OnlineODataStoreException(oError.getMessage()));
                            return;
                        }
                    }
                    onRequestError(Operation.Update.getValue(), null);
                }
            });
            isBatchReqs = true;
        }*/
    }

    private void readValuesFromDataVault() {
        if (mIntPendingCollVal > 0) {
            for (int k = 0; k < invKeyValues.length; k++) {

                while (!mBoolIsReqResAval) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mBoolIsNetWorkNotAval) {
                    break;
                }
                mBoolIsReqResAval = false;

                String store = null;
                try {
                    store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),this);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                //Fetch object from data vault
                try {

                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    dbHeadTable = new Hashtable();
                    arrtable = new ArrayList<>();
                    if (Constants.writeDebug) {
                        LogManager.writeLogDebug("Upload Sync Collection Name:" + fetchJsonHeaderObject.getString(Constants.entityType));
                    }
                    if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedbacks)) {
                        // preparing entity pending
                        if (!alAssignColl.contains(Constants.Feedbacks)) {
                            alAssignColl.add(Constants.Feedbacks);
                            alAssignColl.add(Constants.FeedbackItemDetails);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getFeedbackJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Feedbacks, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }  else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        if (!alAssignColl.contains(Constants.SSSOs)) {
                            alAssignColl.add(Constants.SSSoItemDetails);
                            alAssignColl.add(Constants.SSSOs);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getSOHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSSOs, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }  else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.FinancialPostings)) {

                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        if (!alAssignColl.contains(Constants.FinancialPostings)) {
                            alAssignColl.add(Constants.FinancialPostings);
                            alAssignColl.add(Constants.FinancialPostingItemDetails);
                        }
                        if (!alAssignColl.contains(Constants.SSOutstandingInvoices)) {
                            alAssignColl.add(Constants.SSOutstandingInvoiceItemDetails);
                            alAssignColl.add(Constants.SSOutstandingInvoices);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getCollHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.FinancialPostings, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }else if(fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)){
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getSSInvoiceJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSINVOICES, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                        // preparing entity pending
                        if (!alAssignColl.contains(Constants.ChannelPartners)) {
                            alAssignColl.add(Constants.ChannelPartners);
                            alAssignColl.add(Constants.CPDMSDivisions);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getCPHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.ChannelPartners, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                        if (!alAssignColl.contains(Constants.SSROs)) {
                            alAssignColl.add(Constants.SSROItemDetails);
                            alAssignColl.add(Constants.SSROs);
                        }
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getROHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSROs, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                        if (!alAssignColl.contains(Constants.Expenses)) {
                            alAssignColl.add(Constants.ExpenseItemDetails);
                            alAssignColl.add(Constants.Expenses);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getExpenseHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Expenses, SyncSelectionActivity.this, SyncSelectionActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mBoolIsReqResAval = true;
                }

            }
        }
    }

    public static class ArrayComarator implements Comparator<String[]> {

        @Override
        public int compare(String s1[], String s2[]) {
            BigInteger i1 = null;
            BigInteger i2 = null;
            try {
                i1 = new BigInteger(s1[0]);
            } catch (NumberFormatException e) {
            }

            try {
                i2 = new BigInteger(s2[0]);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return s1[0].compareTo(s2[0]);
            }
        }

    }

    /**
     * This adapter show arrange icons and text in grid view manner.
     */
    public class SyncAdapter extends BaseAdapter {
        Context mContext;

        SyncAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int OriginalStatu : OriginalStatus) {
                if (OriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int iconposition = TempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                view = li.inflate(R.layout.retailer_menu_inside, null);
                view.requestFocus();
                TextView tvIconName = (TextView) view.findViewById(R.id.icon_text);
//                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(iconName[iconposition]);
                ImageView ivIcon = (ImageView) view.findViewById(R.id.ib_must_sell);
                if (iconposition == 0) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            syncHistoryType = Constants.Sync_All;

                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                                    if (Constants.iSAutoSync){
                                        showAlert(getString(R.string.alert_auto_sync_is_progress));
                                    }else{
                                        showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                                    }
                                }else{
                                    if(!isClickable) {
                                        isClickable = true;
                                        onAllSync();
                                    }
                                }
                            }else{
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    });
                } else if (iconposition == 1) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (!isClickable) {
                                isClickable = true;
                                onFreshSync();
                            }
                        }
                    });
                } else if (iconposition == 2) {
                    ivIcon.setImageResource(R.drawable.ic_sync_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            syncHistoryType = Constants.UpLoad;
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if(!isClickable) {
                                    isClickable = true;
                                    onUpdateSync();
                                }
                            }else{
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    });
                } else if (iconposition == 3) {
                    ivIcon.setImageResource(R.drawable.ic_history_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
//                            onSyncHist();
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                if(!isClickable) {
                                    isClickable = true;
                                    onSyncHistColl();
                                }
                            }else{
                                showAlert(getString(R.string.no_network_conn));
                            }
                        }
                    });
                }/*else if (iconposition == 4) {
                    ivIcon.setImageResource(R.drawable.ic_history_black_24dp);
                    ivIcon.setColorFilter(ContextCompat.getColor(SyncSelectionActivity.this, R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            onSyncHistColl();
                        }
                    });
                }*/
                view.setId(position);
            } else {
                view = convertView;
            }
            return view;
        }

    }

    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    if(loadingAsyncTask!=null)
                                                    loadingAsyncTask.cancel(true);

                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.download_all_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
//            Constants.printLogInfo("check store is opened or not");
            if (!OfflineManager.isOfflineStoreOpen()) {
//                Constants.printLogInfo("check store is failed");
                try {
                    OfflineManager.openOfflineStore(SyncSelectionActivity.this, SyncSelectionActivity.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                Constants.isStoreClosed = false;
                assignCollToArrayList();
//                Constants.printLogInfo("check store is opened");
                try {
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.Sync_All,Constants.StartSync,refguid.toString().toUpperCase());
                    OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectionActivity.this, Constants.ALL, "");
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (syncProgDialog == null) {
                syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            }
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.upload_cancel_sync,Constants.StartSync,refguid.toString().toUpperCase());
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            dialogCancelled = false;
                            OfflineManager.flushQueuedRequests(SyncSelectionActivity.this, concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class PostingDataValutData extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (syncProgDialog == null) {
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
           /* }else{
                syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            }*/
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    if(refguid==null){
                                                        refguid = GUID.newRandom();
                                                    }
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionActivity.this,Constants.upload_cancel_sync,Constants.StartSync,refguid.toString().toUpperCase());

                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                tokenFlag = false;

                Constants.x_csrf_token = "";
                Constants.ErrorCode = 0;
                Constants.ErrorNo = 0;
                Constants.ErrorName = "";
                Constants.IsOnlineStoreFailed = false;
                Constants.IsOnlineStoreStarted =false;
                mBoolIsReqResAval = true;
                if (Constants.writeDebug)
                    LogManager.writeLogDebug("Upload Sync : Posting data to backend started" + getString(R.string.no_network_conn));

                try {
                    onlineStoreOpen = OnlineManager.openOnlineStore(SyncSelectionActivity.this,true);
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                    Constants.printLog("Get online store ended with error(1) " + e.getMessage());
                }
                if (onlineStoreOpen) {
                    readValuesFromDataVault();
                } else {
                    return onlineStoreOpen;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return onlineStoreOpen;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                closingProgressDialog();
                syncProgDialog = null;

                if (!onlineStoreOpen) {
                    if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));

                    } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        if(Constants.ErrorNo == Constants.UnAuthorized_Error_Code){
                           String errorMessage = Constants.PasswordExpiredMsg;
                            showAlert(errorMessage);
                        }else{
                            showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""));
                        }

                    } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                        showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));
                    } else {
                        showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""));
                    }
                } else if (!tokenFlag) {
                    Constants.displayMsgINet(Constants.ErrorNo_Get_Token, SyncSelectionActivity.this);
                } else if (Constants.x_csrf_token == null || Constants.x_csrf_token.equalsIgnoreCase("")) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, -2 + ""), SyncSelectionActivity.this);
                    showAlert(getString(R.string.data_conn_lost_during_sync_error_code, -2 + ""));
                }
            }
        }
    }

    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.app_loading));
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            OfflineManager.openOfflineStore(SyncSelectionActivity.this, SyncSelectionActivity.this);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private void showAlert(String message){
        ConstantsUtils.showAlert(message, SyncSelectionActivity.this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }
}
