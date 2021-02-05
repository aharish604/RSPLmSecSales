package com.arteriatech.ss.msecsales.rspl.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.interfaces.FragmentCallbackInterface;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.SupportActivity;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.support.SecuritySettingActivity;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.alerts.AlertsActivity;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.beat.BeatListActivity;
import com.arteriatech.ss.msecsales.rspl.behaviourlist.BehaviourListActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DealerStockListActivity;
import com.arteriatech.ss.msecsales.rspl.digitalProduct.DigitalProductActivity;
import com.arteriatech.ss.msecsales.rspl.expense.ExpenseEntryActivity;
import com.arteriatech.ss.msecsales.rspl.expenselist.ExpenseListActivity;
import com.arteriatech.ss.msecsales.rspl.home.dashboard.DashboardFragment;
import com.arteriatech.ss.msecsales.rspl.home.nav.NavigationMenuFragment;
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.retailercreate.RetailerCreateActivity;
import com.arteriatech.ss.msecsales.rspl.scheme.schemelist.SchemeListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncSelectionActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.targets.TargetsActivity;
import com.arteriatech.ss.msecsales.rspl.visitsummary.VisitSummaryActivity;
import com.arteriatech.ss.msecsales.rspl.visitsummaryreport.VisitSummaryReportActivity;
import com.arteriatech.ss.msecsales.rspl.visualaid.VisualAidActivity;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;
import com.sybase.persistence.PrivateDataVault;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements UIListener, View.OnClickListener, FragmentCallbackInterface {

    public static Context mContext;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };
    private static int REQUEST_SUPPORT = 350;
    boolean flagforexportDB = true;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private FrameLayout flBackDrop;
    private SharedPreferences sharedPerf;
    private ActionBar supportActionBar = null;
    private boolean isFromRegistration = false;
    private RegistrationModel registrationModel = null;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String pDialogStr = "";
    private ProgressDialog pdLoadDialog = null;
    private boolean isDialogBoxShowing = false;
    private NavigationMenuFragment mainMenuFragment = null;
    private DashboardFragment dashBoardFragment = null;
    private TextView tvAlertCount;
//    private TextView tv_toolbar;
    private FrameLayout vAlertRedCircle = null;
    private boolean isRefreshDB = false;
    private GUID refguid =null;
//    private LinearLayout ll_badge;

    public static void verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

            if (storage != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED || camera != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        1
                );
            }
        }
    }

    public static boolean exportDB(Context context) {

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String PACKAGE_NAME;
        PACKAGE_NAME = context.getPackageName();
        String currentDBPath = Constants.offlineDBPath;
        String currentrqDBPath = Constants.offlineReqDBPath;

        String backupDBPath = Constants.backupDBPath;
        String backuprqDBPath = Constants.backuprqDBPath;

        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        File currentrqDB = new File(data, currentrqDBPath);
        File backuprqDB = new File(sd, backuprqDBPath);
        try {
            // Exporting Offline DB
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            // Exporting Offline rq DB
            source = new FileInputStream(currentrqDB).getChannel();
            destination = new FileOutputStream(backuprqDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            return true;
        } catch (IOException e) {
            LogManager.writeLogError(e.getMessage());
            return false;
        }
    }

    /*Import Offline DB into application*/
    public static boolean importDB(Context mContext, UIListener uiListener) {
        if (OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.closeOfflineStore();
                LogManager.writeLogError(mContext.getString(R.string.msg_sync_terminated));
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(mContext.getString(R.string.error_during_offline_close) + e.getMessage());
            }
        }

        File isd = Environment.getExternalStorageDirectory();
        File idata = Environment.getDataDirectory();
        FileChannel isource = null;
        FileChannel idestination = null;
        File ibackupDB = new File(idata, Constants.icurrentUDBPath);
        File icurrentDB = new File(isd, Constants.ibackupUDBPath);

        File ibackupRqDB = new File(idata, Constants.icurrentRqDBPath);
        File icurrentRqDB = new File(isd, Constants.ibackupRqDBPath);
        try {
            isource = new FileInputStream(icurrentDB).getChannel();
            idestination = new FileOutputStream(ibackupDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource = new FileInputStream(icurrentRqDB).getChannel();
            idestination = new FileOutputStream(ibackupRqDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource.close();
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(mContext, uiListener);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    public static int exportDataVault(Context mContext) {
        boolean flagforlog = false;
        try {
            FileWriter fileWriter = null;
            String jsonData = null;
            try {
                jsonData = Constants.makePendingDataToJsonString(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError("exportDataVault() : " + e.getMessage());
                jsonData = "";
            }
            if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
                fileWriter = new FileWriter(Environment.getExternalStorageDirectory()
                        + "/" + Constants.DataVaultFileName + "");
                fileWriter.write(jsonData);
                fileWriter.close();
//                flagforlog = true;
                return 3;
            } else {
//                Constants.ExportDataFailedErrorMsg = "No Pending Requests Available";
//                flagforlog = false;
                return 2;
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogManager.writeLogError("exportDataVault() (IOException) : " + e.getMessage());
            return 1;
        }
    }

    public static int exportDataVaultData(Context mContext) {
        boolean flagforlog = false;
        try {
            FileWriter fileWriter = null;
            String jsonData = null;
            try {
                jsonData = makePendingDataToJsonString(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError("exportDataVault() : " + e.getMessage());
                jsonData = "";
            }
            if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
                fileWriter = new FileWriter(Environment.getExternalStorageDirectory()
                        + "/" + Constants.DataVaultFileName + "");
                fileWriter.write(jsonData);
                fileWriter.close();
//                flagforlog = true;
                return 3;
            } else {
//                Constants.ExportDataFailedErrorMsg = "No Pending Requests Available";
//                flagforlog = false;
                return 2;
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogManager.writeLogError("exportDataVault() (IOException) : " + e.getMessage());
            return 1;
        }
    }
    public static String makePendingDataToJsonString(Context context) {
        String mStrJson = "";
        ArrayList<Object> objectArrayList = getPendingCollList(context, false);
        if (!objectArrayList.isEmpty()) {
            String[][] invKeyValues = (String[][]) objectArrayList.get(1);
            JSONArray jsonArray = new JSONArray();
            for (int k = 0; k < invKeyValues.length; k++) {
                JSONObject jsonObject = new JSONObject();
                String store = "";
                try {
                    store = getValueFromDataVault(invKeyValues[k][0].toString(), context,Constants.EncryptKey);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    // Add the values to the jsonObject
                    jsonObject.put(Constants.KeyNo, invKeyValues[k][0]);
                    jsonObject.put(Constants.KeyType, invKeyValues[k][1]);
                    jsonObject.put(Constants.KeyValue, store);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(Constants.DataVaultData, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mStrJson = jsonObj.toString();
        }
        return mStrJson;
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

        set = sharedPreferences.getStringSet(Constants.SecondarySOCreateTemp, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SecondarySOCreateTemp;
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
            Arrays.sort(invKeyValues, new SyncSelectionActivity.ArrayComarator());
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
        set = sharedPreferences.getStringSet(Constants.SecondarySOCreateTemp, null);
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

    public static String getValueFromDataVault(String key, Context mContext, String password) {
        String store = null;

        try {
            PrivateDataVault privateDataVault = PrivateDataVault.getVault(Configuration.APP_ID);
            if (privateDataVault != null) {
                if (privateDataVault.isLocked()) {
                    privateDataVault.unlock(null);
                }

                byte[] byteValue = privateDataVault.getValue(key);
                if (byteValue != null) {
                   store = new String(byteValue, "UTF-8");
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return store;
    }

    public static void callBeatPlan(Context mContext) {
        Intent intent = new Intent(mContext, BeatListActivity.class);
        intent.putExtra(Constants.comingFrom, Constants.BeatPlan);
        mContext.startActivity(intent);
    }
    private String intialSyncTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoActionBar);
        setContentView(R.layout.activity_home_page);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            isFromRegistration = bundleExtras.getBoolean(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, false);
            registrationModel = (RegistrationModel) bundleExtras.getSerializable(UtilConstants.RegIntentKey);
        }
        mContext = MainActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tv_toolbar = (TextView) findViewById(R.id.tv_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        flBackDrop = (FrameLayout) findViewById(R.id.flBackDrop);
        ConstantsUtils.initActionBarView(this, toolbar, false, getString(R.string.lbl_main_menu), 0);
        sharedPerf = getSharedPreferences(Constants.PREFS_NAME, 0);
        if (isFromRegistration) {
            pDialogStr = getString(R.string.preparing_app);
            intialSyncTime = UtilConstants.getSyncHistoryddmmyyyyTime();

        } else {
            pDialogStr = getString(R.string.app_loading);
        }
        if (getSupportActionBar() != null) {
            supportActionBar = getSupportActionBar();
            supportActionBar.setIcon(R.mipmap.ic_action_bar_logo);
        }

        Log.d("ToSeePerformance","MainActivity");
        if (!Constants.restartApp(MainActivity.this)) {
            disableCollapse();
            verifyStoragePermissions(this);
            setDrawer();

            openInitialStore(MainActivity.this);
        }
    }
    private void openInitialStore(Context mContext) {
//        new OpenOfflineStoreAsync(false).execute();
        if (sharedPerf.getBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false)) {
            pdLoadDialog = new ProgressDialog(MainActivity.this, R.style.UtilsDialogTheme);
            pdLoadDialog.setMessage(pDialogStr);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }else if ((sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) != Constants.NewDefingRequestVersion) ||
                (sharedPerf.getInt(Constants.INTIALIZEDB, 0) != Constants.IntializeDBVersion)) {
            showDialogMessage();
        }
        ConstantsUtils.getRollId(MainActivity.this, new AsyncTaskCallBack() {
            @Override
            public void onStatus(boolean status, String values) {
                if (status) {
                    new OpenOfflineStoreAsync(false).execute();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pdLoadDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ConstantsUtils.displayLongToast(MainActivity.this, values);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError("onRequestError :" + e.getMessage());
        ErrorBean errorBean = Constants.getErrorCode(operation, e, MainActivity.this);
        String customeNEO = Constants.checkUnknownNetworkerror(errorBean.getErrorMsg(), MainActivity.this);

        if (!TextUtils.isEmpty(errorBean.getErrorMsg()) && errorBean.getErrorMsg().contains("10340") && !isRefreshDB) {
            if (pdLoadDialog != null && pdLoadDialog.isShowing())
                pdLoadDialog.dismiss();
            pDialogStr = "Updating applicaiton please wait";
            pdLoadDialog = new ProgressDialog(MainActivity.this, R.style.UtilsDialogTheme);
            pdLoadDialog.setMessage(pDialogStr);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
            isRefreshDB = true;

            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                Constants.isSync = true;
                Constants.closeStore(MainActivity.this);
                new RefreshAsyncTask(MainActivity.this, "", this).execute();
            }

        /*    if (OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.closeOfflineStore();
                    LogManager.writeLogError(getString(R.string.msg_sync_terminated));
                } catch (OfflineODataStoreException ez) {
                    LogManager.writeLogError(Constants.error_during_offline_close + ez.getMessage());
                }
            }

            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(MainMenu.this, uiListener);
                } catch (OfflineODataStoreException ec) {
                    ec.printStackTrace();
                }
            }*/
        }else{
        if (customeNEO.equalsIgnoreCase("")) {
            if (errorBean.hasNoError()) {
                Toast.makeText(MainActivity.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                        Toast.LENGTH_LONG).show();
                String mStrPopUpText = "";
                try {
                    mStrPopUpText = errorBean.getErrorMsg();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//            }
                if (TextUtils.isEmpty(mStrPopUpText)) {
                    mStrPopUpText = getString(R.string.alert_sync_cannot_be_performed);
                }
                if (operation == Operation.OfflineRefresh.getValue()) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    displayPopUpMsg(mStrPopUpText);
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    try {
                        try {
                            pdLoadDialog.dismiss();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        displayPopUpMsg(mStrPopUpText);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            } else if (errorBean.isStoreFailed()) {
//            LogManager.writeLogError("onRequestError isStoreFailed:" + errorBean.getErrorMsg());
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    Constants.isSync = true;
                    new RefreshAsyncTask(MainActivity.this, "", this).execute();
                } else {
                    Constants.isSync = false;
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    Constants.displayMsgReqError(errorBean.getErrorCode(), MainActivity.this);
                }
            } else {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Constants.displayMsgReqError(errorBean.getErrorCode(), MainActivity.this);
            }
        } else {
            /*if(customeNEO.contains("10348")){
                    if (OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.closeOfflineStore();
                    LogManager.writeLogError(getString(R.string.msg_sync_terminated));
                } catch (OfflineODataStoreException ez) {
                  //  LogManager.writeLogError(Constants.error_during_offline_close + ez.getMessage());
                }
            }

            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(MainActivity.this, new UIListener() {
                        @Override
                        public void onRequestError(int i, Exception e) {
                            Log.d("error","offline");
                        }

                        @Override
                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                            Log.d("success","offline");
                        }
                    });
                } catch (OfflineODataStoreException ec) {
                    ec.printStackTrace();
                }
            }
            }*/
            UtilConstants.showAlert(customeNEO, MainActivity.this);
        }
    }

    }

    private void displayPopUpMsg(String mStrPopUpText) {
        if (!isDialogBoxShowing) {
            UtilConstants.dialogBoxWithCallBack(MainActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    isDialogBoxShowing = false;
                }
            });
            isDialogBoxShowing = true;
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                pdLoadDialog.dismiss();
//                setUI(true,false);
                setUI();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (operation == Operation.GetStoreOpen.getValue()) {
            /*try {
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, MainActivity.this, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConstantsUtils.startAutoSync(MainActivity.this, true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (sharedPerf.getBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false)) {
                        SharedPreferences.Editor editor = sharedPerf.edit();
                        editor.putBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false);
                        editor.apply();
                        try {
                            OfflineManager.getAuthorizations(getApplicationContext());
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        SyncUtils.updateAllSyncHistory(MainActivity.this,Constants.Sync_All);
                        Constants.setBirthDayRecordsToDataValut(MainActivity.this);
                        Constants.setAlertsRecordsToDataValut(MainActivity.this);
                        refreshUI();
                    } else {
                        try {
                            OfflineManager.getAuthorizations(MainActivity.this);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
//                        Constants.setBirthDayRecordsToDataValut(MainActivity.this);
                        String mStrSPGUID = Constants.getSPGUID();
                        String prvDayQry = Constants.Attendances + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                        String mStrAttendanceId = "";
                        try {
                            mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(mStrAttendanceId)) {
                            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                try {
                                    OfflineManager.refreshStoreSync(mContext, new UIListener() {
                                        @Override
                                        public void onRequestError(int operation, Exception e) {
                                            ErrorBean errorBean = Constants.getErrorCode(operation, e, MainActivity.this);
                                            if (errorBean.hasNoError()) {
                                                refreshUI();
                                            }else{
                                                refreshUI();
                                                Constants.displayMsgReqError(errorBean.getErrorCode(), MainActivity.this);
                                            }
                                        }

                                        @Override
                                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                            refreshUI();
                                        }
                                    }, Constants.Fresh, Constants.Attendances);
                                } catch (OfflineODataStoreException e) {
                                    e.printStackTrace();
                                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                                }
                            } else {
                                refreshUI();
                            }
                        } else {
                            refreshUI();
                        }

                    }
                    Constants.setSyncTime(MainActivity.this, Constants.Sync_All);

                }
            }).start();*/
            if (sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) == Constants.NewDefingRequestVersion) {
                if (sharedPerf.getInt(Constants.INTIALIZEDB, 0) == Constants.IntializeDBVersion) {
                    refreshStore();
                } else {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        Constants.isSync = true;
                        increaseSharedPerfVal(Constants.INTIALIZEDB, Constants.IntializeDBVersion);
                        Constants.closeStore(MainActivity.this);
                        new RefreshAsyncTask(MainActivity.this, "", this).execute();
                    } else {
                        refreshStore();
                    }
                }
            } else {
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    if (OfflineManager.offlineStore!=null && !OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            OfflineManager.flushQueuedRequests(new UIListener() {
                                @Override
                                public void onRequestError(int i, Exception e) {
                                    refreshStore();
                                }

                                @Override
                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                    increaseSharedPerfVal(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                                    Constants.closeStore(MainActivity.this);
                                    new RefreshAsyncTask(MainActivity.this, "", MainActivity.this).execute();
                                }
                            }, "");
                        } catch (OfflineODataStoreException e) {
                            refreshStore();
                            e.printStackTrace();
                        }
                    } else {
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            Constants.isSync = true;
                            increaseSharedPerfVal(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                            Constants.closeStore(MainActivity.this);
                            new RefreshAsyncTask(MainActivity.this, "", this).execute();
                        } else {
                            refreshStore();
                        }
                    }
                } else {
                    refreshStore();
                }
            }
        }
    }

    private void increaseSharedPerfVal(String versionCode, int version) {
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            editor.putInt(versionCode, version);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setSharedPerfVal() {
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            if (!sharedPerf.contains(Constants.CURRENT_VERSION_CODE)) {
                editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    private void storeLoginIdToSp() {
        String loginId = "";
        loginId = OfflineManager.getLoginID("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27");
        if(!TextUtils.isEmpty(loginId)) {
            SharedPreferences.Editor userNameEditor = sharedPerf.edit();
            userNameEditor.putString(Constants.username, loginId.toUpperCase());
            userNameEditor.putString(Constants.usernameExtra, loginId.toUpperCase());
            userNameEditor.apply();
        }else{
            LogManager.writeLogError("MainActivity Oflline query for UserAuthSet login Id returns null or empty"+":"+loginId);
        }
    }

    private void getUserPartnerDetails() {
        String userPartnerID = sharedPerf.getString(Constants.USERPARTNERTYPE,"");
        try {
            if(TextUtils.isEmpty(userPartnerID)){
                if (UtilConstants.isNetworkAvailable(MainActivity.this)) {
                    String userLoginId = sharedPerf.getString("username", "");
                    ConstantsUtils.getUserPartnerDetails(MainActivity.this,userLoginId);
                }else {
                    String parentID = "";
                    String strSPGUID = Constants.getSPGUID(Constants.SPGUID);
                    if (!TextUtils.isEmpty(strSPGUID)) {
                        String StrSPGUID32 = strSPGUID.replaceAll("-", "");
                        try {
                            if(Arrays.asList(Constants.getDefinigReq(MainActivity.this)).contains(Constants.UserPartners)) {
                                parentID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                editor.putString(Constants.USERPARTNERTYPE, parentID);
                                editor.apply();
                            }
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void refreshStore() {
        try {
            AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, MainActivity.this, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String userLoginID = sharedPerf.getString("username", "");
            if(isFromRegistration) {
                storeLoginIdToSp();
            }else if(TextUtils.isEmpty(userLoginID)){
                storeLoginIdToSp();
            }


            /*try {
                getUserPartnerDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            String firstTime = "";
            String endTime = "";
            String autoSyncTime = "";
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            firstTime = sharedPreferences.getString("FIRSTAUTOSYNCTIME", "");
            autoSyncTime = sharedPreferences.getString("AUTOSYNCTIME", "");
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                endTime = sdf.format(cal.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

            long diffTime = 0;
            long finalTime = 0;
            if (!TextUtils.isEmpty(firstTime) && !TextUtils.isEmpty(endTime)) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date1 = format.parse(firstTime);
                Date date2 = format.parse(endTime);
                diffTime = date2.getTime() - date1.getTime();
                finalTime = diffTime / (60 * 1000) % 60;
            }
            int intFirstTime = (int) finalTime;
            int intAutoSyncTime = 0;
            if (!TextUtils.isEmpty(autoSyncTime)) {
                intAutoSyncTime = Integer.parseInt(autoSyncTime);
            }

            if (!TextUtils.isEmpty(autoSyncTime)) {
                if (intFirstTime >= intAutoSyncTime) {
                    try {
                       // UpdatePendingRequest.getInstance(null).callScheduleFirstLoginSync();
                    } catch (Exception e) {
                        LogManager.writeLogError("UpdatePendingRequest : " + e.toString());
                        e.printStackTrace();
                    }
                    ConstantsUtils.startAutoSync(MainActivity.this, true);
                }
            } else {
                ConstantsUtils.startAutoSync(MainActivity.this, true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        ConstantsUtils.startAutoSync(MainActivity.this, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                refguid = GUID.newRandom();
                if (sharedPerf.getBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false)) {
                    SharedPreferences.Editor editor = sharedPerf.edit();
                    editor.putBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false);
                    editor.apply();
                    try {
                        OfflineManager.getAuthorizations(getApplicationContext());
                        OfflineManager.getLogAuthorizations(getApplicationContext());
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                    SyncUtils.updatingInitialSyncStartTime(MainActivity.this,Constants.Sync_All,Constants.StartSync,refguid.toString().toUpperCase(),intialSyncTime);
                    SyncUtils.updateAllSyncHistory(MainActivity.this, Constants.Initial_sync,refguid.toString().toUpperCase());
                    Constants.setBirthDayRecordsToDataValut(MainActivity.this);
                    Constants.setAlertsRecordsToDataValut(MainActivity.this);
                    refreshUI(true);
                } else {
                    try {
                        OfflineManager.getAuthorizations(MainActivity.this);
                        OfflineManager.getLogAuthorizations(getApplicationContext());
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
//                        Constants.setBirthDayRecordsToDataValut(MainActivity.this);
                        refreshUI(true);

                }
                Constants.setSyncTime(MainActivity.this, Constants.Sync_All);
                boolean datavault = PrivateDataVault.vaultExists(Configuration.APP_ID);
                if(!sharedPerf.getBoolean(Constants.DataVaultUpdate,false)) {
                    if (datavault) {
                        LogManager.writeLogInfo("Vault is exists");
                        datavaultData();
//            Toast.makeText(mContext, "Vault is exists", Toast.LENGTH_SHORT).show();
                    } else {
                        LogManager.writeLogInfo("Vault is not exists");
//            Toast.makeText(mContext, "Vault is not exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    private void refreshUI(final boolean offlineOpened) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
//                    setUI(true,offlineOpened);
                    setUI();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
            }
        });
    }

    private void closeNavDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT, true);
    }

    private void openFragment(Fragment mainMenuFragment) {
        disableCollapse();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, mainMenuFragment, mainMenuFragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();
        Log.d("ToSeePerformance","commitAllowingStateLoss");


    }

    @Override
    public void fragmentCallBack(String s, Bundle bundles) {
        closeNavDrawer();
        Bundle bundle;
        if (bundles != null) {
            final int pos = bundles.getInt(Constants.EXTRA_POS, 0);
            Fragment fragment = null;
            Intent intent = null;
            switch (pos) {
                case 1:
                    if (ConstantsUtils.isAutomaticTimeZone(MainActivity.this)) {
                        fragment = new DashboardFragment();
                        dashBoardFragment = (DashboardFragment) fragment;
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainActivity.this);
                    }
                    break;
                case 2:
                    ConstantsUtils.onDealerList(this, Constants.AdhocList);
                    break;
                case 3:
                    onTargetActivity();
                    break;
                case 4:
                    if (ConstantsUtils.isAutomaticTimeZone(MainActivity.this)) {
                        callBeatPlan(MainActivity.this);
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(MainActivity.this);
                    }
                    break;
                case 5:
                    callBehavior();
                    break;
                case 6:
                    startActivity(new Intent(this, DealerStockListActivity.class));
                    break;
                case 9:
                    ConstantsUtils.onDealerList(this, Constants.RetailerList);
//                    ConstantsUtils.onCustomerList(this, Constants.RetailerList);
                    break;
                case 10:
                    intent = new Intent(MainActivity.this, VisualAidActivity.class);
                    break;
                case 11:
                    intent = new Intent(MainActivity.this, DigitalProductActivity.class);
                    break;
                case 12:
                    intent = new Intent(MainActivity.this, VisitSummaryActivity.class);
                    break;
                case 13:
                    intent = new Intent(MainActivity.this, SchemeListActivity.class);
                    break;
                case 14:
                    intent = new Intent(MainActivity.this, RetailerCreateActivity.class);
                    break;
                case 16:
                    intent = new Intent(MainActivity.this, SyncSelectionActivity.class);
                    break;
                case 20:
                    intent = new Intent(MainActivity.this, ExpenseEntryActivity.class);
                    break;
                case 21:
                    intent = new Intent(MainActivity.this, ExpenseListActivity.class);
                    break;

                case 22:
                    intent = new Intent(MainActivity.this, VisitSummaryReportActivity.class);
                    startActivity(intent);
                    break;

                case 19:
                    openSupport();
                    break;
                case 18:
                    if (registrationModel != null) {
                        registrationModel.setExtenndPwdReq(true);
                        registrationModel.setUpdateAsPortalPwdReq(true);
                        registrationModel.setIDPURL(Configuration.IDPURL);
                        registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                        registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                        intent = new Intent(this, com.arteriatech.mutils.support.SecuritySettingActivity.class);
                        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                    }
                    break;
            }
            if (fragment != null) {
                Handler handler = new Handler();
                final Fragment finalFragment = fragment;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openFragment(finalFragment);
                       /* if (pos != 1)
                            setActionBarTitle(title, false, false);
                        else//home fragment
                            setActionBarTitle(getString(R.string.app_name), false, true);*/
                    }
                }, 300);

            } else if (intent != null) {
                Handler handler = new Handler();
                final Intent finalIntent = intent;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(finalIntent);
//                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }, 300);
            }
        }

    }

    private void callBehavior() {
        Intent intent = new Intent(this, BehaviourListActivity.class);
        startActivity(intent);
    }

    private void onTargetActivity() {
        Intent behaviour = new Intent(MainActivity.this, TargetsActivity.class);
        startActivity(behaviour);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        setUI(false,false);
        setUI();
    }

    private void setUI(/*boolean call,boolean isOfflineOpened*/) {
        if (supportActionBar != null) {
            String mStrSPGUID = Constants.getSPGUID();
            String titleName = "";
            String date = "";
            String sliptDate = "";
            String qry = Constants.UserSalesPersons + "?$filter=" + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            try {
                titleName = OfflineManager.getSPName(qry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(titleName)){
                titleName = "Main Menu";
            }

            try {
                String pattern = "dd-MMM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                 date = simpleDateFormat.format(new Date());
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            supportActionBar.setTitle(""+titleName);
            supportActionBar.setSubtitle(""+date);
            if(titleName.length()>=0 && titleName.length()<=15){
                ((TextView)toolbar.getChildAt(0)).setTextSize(20);
            }else if(titleName.length()>=15 && titleName.length()<=20){
                ((TextView)toolbar.getChildAt(0)).setTextSize(16);
            }else if(titleName.length()>=20 && titleName.length()<=25){
                ((TextView)toolbar.getChildAt(0)).setTextSize(11);
            }else if(titleName.length()>25 ){
                ((TextView)toolbar.getChildAt(0)).setTextSize(11);
            }
//            tv_toolbar.setText(titleName);

//            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        }
        if (mainMenuFragment != null)
            mainMenuFragment.onRefresh();
       /* if (dashBoardFragment != null)
            dashBoardFragment.onRefresh();*/
        if (dashBoardFragment != null) {
            dashBoardFragment.TargetSync();
           /* if(isOfflineOpened)
                dashBoardFragment.loadOfflineDataFirst();*/
        }
        setAlertCount();

    }

    public void disableCollapse() {
        flBackDrop.setVisibility(View.GONE);
        collapsingToolbar.setTitleEnabled(false);
//        collapsingToolbar.setBackground(ContextCompat.getDrawable(MainActivity.this,R.color.primaryColor));
    }

    public void setActionBarTitle(String title, boolean showBackButton, boolean showAppIcon) {
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        }
    }

    private void setDrawer() {
        Log.d("ToSeePerformance","setDrawer");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mainMenuFragment = new NavigationMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_COME_FROM, 1);
       /* if (!presenter.setSideMenuData().isEmpty()) {
            bundle.putSerializable(Constants.EXTRA_SALES_PERSON, presenter.setSideMenuData().get(0));
        }*/
        mainMenuFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.ll_container, mainMenuFragment);
        fragmentTransaction.commitAllowingStateLoss();
        dashBoardFragment = new DashboardFragment();
        Log.d("ToSeePerformance","openFragment");
        openFragment(dashBoardFragment);
    }

    @Override
    public void onBackPressed() {
        UtilConstants.dialogBoxWithCallBack(MainActivity.this, "", getString(R.string.do_u_want_exit_app), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    Constants.IsOnlineStoreStarted = false;
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                    finishAndRemoveTask();
//                    finishAffinity();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.itemAlerrt);
        menuItem.setVisible(true);
        View badgeLayout = menuItem.getActionView();
        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callAlerts();
                onOptionsItemSelected(menuItem);
            }
        });
        tvAlertCount = (TextView) badgeLayout.findViewById(R.id.tvAlertCount);
        vAlertRedCircle = (FrameLayout) badgeLayout.findViewById(R.id.view_alert_red_circle);
        setAlertCount();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mainmenu_export:
                if (ConstantsUtils.checkStoragePermission(MainActivity.this)) {
                    exportOfflineDB(MainActivity.this);
                }
                return true;
            case R.id.menu_mainmenu_import:
                if (ConstantsUtils.checkStoragePermission(MainActivity.this)) {
                    importOfflineDB(MainActivity.this);
                }
                return true;
            case R.id.menu_exportdatavault:
                alertPOPUPExportDataVaultData();
                return true;
            case R.id.menu_importdatavault:
                alertPOPUPImportDataVaultData();
                return true;
            case R.id.menu_settings:
                if (registrationModel != null) {

                    if(registrationModel.getUserName().equalsIgnoreCase(""))
                    {   SharedPreferences sharedPerf = this.getSharedPreferences(Constants.PREFS_NAME, 0);
                        String userName = sharedPerf.getString("username", "");
                        registrationModel.setUserName(userName);
                    }

                    registrationModel.setExtenndPwdReq(true);
                    registrationModel.setUpdateAsPortalPwdReq(true);
                    registrationModel.setIDPURL(Configuration.IDPURL);
                    registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                    registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                    Intent intent = new Intent(this, SecuritySettingActivity.class);
                    intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_support:
                openSupport();
//                callAlerts();
                return true;

            case R.id.screenShot:
                takeScreenshot();
//                callAlerts();
                return true;
            case R.id.itemAlerrt:
                callAlerts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSupport() {
        if (registrationModel != null) {
            registrationModel.setDisplayDBReInitMenu(true);
            registrationModel.setDisplayImportMenu(false);
            registrationModel.setDisplayExportMenu(true);
            registrationModel.setDisplayExportDataMenu(true);
            registrationModel.setDataVaultPassword(Constants.EncryptKey);
            registrationModel.setDisplayImportDataMenu(true);
            registrationModel.setAlEntityNames(Constants.getEntityNames());
            Intent intent = new Intent(this, SupportActivity.class);
            intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
            startActivityForResult(intent, REQUEST_SUPPORT);
        }
    }

    private void alertPOPUPExportDataVaultData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_export_data_to_device).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (ConstantsUtils.checkStoragePermission(MainActivity.this)) {
                            exportDatavaultData(MainActivity.this);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void alertPOPUPImportDataVaultData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_import_data_to_device).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (ConstantsUtils.checkStoragePermission(MainActivity.this)) {
                            importDatavaultData(MainActivity.this);
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void exportDatavaultData(final Context mContext) {
        pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.export_datavault_data_to_storage));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int isExportData = exportDataVault(mContext);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.dismiss();
                            String msg = mContext.getString(R.string.export_datavault_to_sdcard_error_occurred);
                            if (isExportData == 3) {//success
                                msg = mContext.getString(R.string.export_datavault_to_sdcard_finish);
                                Constants.removeDataValtFromSharedPref(mContext, "", "", true);
                            } else if (isExportData == 2) {
                                msg = "No Pending Requests Available";
                            }
                            ConstantsUtils.displayLongToast(mContext, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void datavaultData(){
        pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.update_app_message));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final int isExportData = exportDataVaultData(MainActivity.this);
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            pdLoadDialog.dismiss();
                            String msg = mContext.getString(R.string.export_datavault_to_sdcard_error_occurred);
                            if (isExportData == 3) {//success
                                msg = getString(R.string.export_datavault_to_sdcard_finish);
                                Constants.removeDataValtFromSharedPref(mContext, "", "", true);
                                importDatavault(MainActivity.this);
                            } else if (isExportData == 2) {
                                pdLoadDialog.dismiss();
                                msg = "No Pending Requests Available";
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                try {
                                    editor.putBoolean(Constants.DataVaultUpdate, true);
                                    editor.apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                pdLoadDialog.dismiss();
                            }
                            LogManager.writeLogError(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void importDatavaultData(final Context mContext) {
        pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.import_datavault_data_from_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                String message = getString(R.string.import_datavault_from_sdcard_finish);

                try {
                    boolean isFileExists = Constants.isFileExits(Constants.DataVaultFileName);
                    if (isFileExists) {
                        String datavaultData = Constants.getTextFileData(Constants.DataVaultFileName);
                        Constants.setJsonStringDataToDataVault(datavaultData, mContext);
                    } else {
                        message = getString(R.string.file_not_exist);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError("importDatavaultData() (InterruptedException): " + e.getMessage());
                    message = getString(R.string.import_datavault_from_sdcard_error_occurred);
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    public void run() {
                        pdLoadDialog.cancel();
                        ConstantsUtils.displayLongToast(mContext, finalMessage);
                    }
                });
            }
        }).start();

    }

    private void importDatavault(final Context mContext) {
        new Thread(new Runnable() {
            public void run() {
                String message = getString(R.string.import_datavault_from_sdcard_finish);

                try {
                    boolean isFileExists = Constants.isFileExits(Constants.DataVaultFileName);
                    if (isFileExists) {
                        String datavaultData = Constants.getTextFileData(Constants.DataVaultFileName);
                        Constants.setJsonStringDataToDataVault(datavaultData, mContext);
                    } else {
                        message = getString(R.string.file_not_exist);
                    }
                    SharedPreferences.Editor editor = sharedPerf.edit();
                    try {
                        editor.putBoolean(Constants.DataVaultUpdate, true);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError("importDatavaultData() (InterruptedException): " + e.getMessage());
                    message = getString(R.string.import_datavault_from_sdcard_error_occurred);
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    public void run() {
                        pdLoadDialog.cancel();
                        LogManager.writeLogInfo(finalMessage);
                        setUI();
//                        ConstantsUtils.displayLongToast(mContext, finalMessage);
                    }
                });
            }
        }).start();

    }
    private void callAlerts() {
        Intent intent = new Intent(MainActivity.this, AlertsActivity.class);
        startActivity(intent);
    }

    public void setAlertCount() {
        if (tvAlertCount != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            int alertCount = sharedPreferences.getInt(Constants.BirthdayAlertsCount, 0) +
                    sharedPreferences.getInt(Constants.TextAlertsCount, 0) +
                    sharedPreferences.getInt(Constants.AppointmentAlertsCount, 0);
            if (alertCount > 0) {
                if (alertCount > 99)
                    tvAlertCount.setText(String.valueOf(99) + "+");
                else
                    tvAlertCount.setText(String.valueOf(alertCount));
                vAlertRedCircle.setVisibility(View.VISIBLE);
            } else {
                vAlertRedCircle.setVisibility(View.GONE);
            }
        }
    }

    private void exportOfflineDB(final Context mContext) {
        pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.export_databse_to_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean isExportDB = exportDB(mContext);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.cancel();
                            String msg = mContext.getString(R.string.export_databse_to_sdcard_error_occurred);
                            if (isExportDB) {//success
                                msg = mContext.getString(R.string.export_databse_to_sdcard_finish);
                            }
                            ConstantsUtils.displayLongToast(mContext, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void importOfflineDB(final Context mContext) {
        pdLoadDialog = new ProgressDialog(mContext, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.import_databse_from_sdcard));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean isImportDB = importDB(mContext, MainActivity.this);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pdLoadDialog.cancel();
                            String msg = mContext.getString(R.string.import_databse_from_sdcard_error_occurred);
                            if (isImportDB) {//success
                                msg = mContext.getString(R.string.import_databse_from_sdcard_finish);
                            }
                            ConstantsUtils.displayLongToast(mContext, msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SUPPORT && resultCode == SupportActivity.OFFLINE_DB_REINITIALIZE) {
            new OpenOfflineStore().execute();
        } else if (requestCode == REQUEST_SUPPORT && resultCode == SupportActivity.OFFLINE_DB_IMPORT) {
            importOfflineDB(MainActivity.this);
        }
    }

    private void closeStore() {
        try {
            OfflineManager.closeOfflineStore(MainActivity.this, OfflineManager.options);
            LogManager.writeLogInfo(getString(R.string.store_removed));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(getString(R.string.error_during_offline_close) + e.getMessage());
        }
    }

    private void showDialogMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    pdLoadDialog = new ProgressDialog(MainActivity.this, R.style.UtilsDialogTheme);
                    pdLoadDialog.setMessage(getString(R.string.update_app_message));
                    pdLoadDialog.setCancelable(false);
                    pdLoadDialog.show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e1.getMessage());
                }
            }
        });
        }


    private class OpenOfflineStoreAsync extends AsyncTask<Void, String, String> {
        boolean isStoreOpened = false;
        boolean readRollId = false;
        ErrorBean errorBean = null;

        OpenOfflineStoreAsync(boolean readRollId) {
            this.readRollId = readRollId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if (sharedPerf.getBoolean(UtilRegistrationActivity.KEY_isFirstTimeReg, false)) {
                pdLoadDialog = new ProgressDialog(MainActivity.this, R.style.UtilsDialogTheme);
                pdLoadDialog.setMessage(pDialogStr);
                pdLoadDialog.setCancelable(false);
                pdLoadDialog.show();
            }else if ((sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) != Constants.NewDefingRequestVersion) ||
                    (sharedPerf.getInt(Constants.INTIALIZEDB, 0) != Constants.IntializeDBVersion)) {
                showDialogMessage();
            }*/
        }

        @Override
        protected String doInBackground(Void... params) {
            if (isFromRegistration) {
                pDialogStr = getString(R.string.preparing_app);
            }
            String errorMsg = "";
            SyncUtils.checkAndCreateDB(MainActivity.this);

            try {
//                String rollId = ConstantsUtils.getRollId(MainActivity.this);
//                if (!TextUtils.isEmpty(rollId)) {
                   /* String loginID = sharedPerf.getString(Constants.USERROLELOGINID,"");
                    if(TextUtils.isEmpty(loginID)){
                        String userLoginId = sharedPerf.getString("username", "");
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.USERROLELOGINID, userLoginId);
                        editor.apply();
                        loginID = sharedPerf.getString(Constants.USERROLELOGINID,"");
                    }

                    String userParntenrID = ConstantsUtils.getUserPartnerDetails(MainActivity.this,loginID);
                    if(!TextUtils.isEmpty(userParntenrID)){*/
                if (OfflineManager.offlineStore != null) {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            OfflineManager.openOfflineStore(MainActivity.this, MainActivity.this);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    } else {
                        isStoreOpened = true;
                    }
                } else {
                    try {
                        OfflineManager.openOfflineStore(MainActivity.this, MainActivity.this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }
                    /*}else {
                        return getString(R.string.unknown_error);
                    }*/
//                } else {
//                    return getString(R.string.unknown_error);
//                }
            }/* catch (OnlineODataStoreException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            } catch (ODataParserException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            } catch (ODataNetworkException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            } catch (ODataContractViolationException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            }*/ catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            }
            return errorMsg;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogManager.writeLogError(Constants.error_txt + result);
                ConstantsUtils.displayLongToast(MainActivity.this, result);
//                Constants.displayMsgReqError(result, MainActivity.this);
            } else if (errorBean != null) {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                LogManager.writeLogError(Constants.error_txt + "Not able to get roll information");
                Constants.displayMsgReqError(errorBean.getErrorCode(), MainActivity.this);
            } else if (isStoreOpened) {
                try {
                    if (pdLoadDialog!=null&&pdLoadDialog.isShowing()) {
                        pdLoadDialog.dismiss();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    String firstTime = "";
                    String endTime = "";
                    String autoSyncTime = "";
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    firstTime = sharedPreferences.getString("FIRSTAUTOSYNCTIME", "");
                    autoSyncTime = sharedPreferences.getString("AUTOSYNCTIME", "");
                    try {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        endTime = sdf.format(cal.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    long diffTime = 0;
                    long finalTime = 0;
                    if (!TextUtils.isEmpty(firstTime) && !TextUtils.isEmpty(endTime)) {
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = format.parse(firstTime);
                        Date date2 = format.parse(endTime);
                        diffTime = date2.getTime() - date1.getTime();
                        finalTime = diffTime / (60 * 1000) % 60;
                    }
                    int intFirstTime = (int) finalTime;
                    int intAutoSyncTime = 0;
                    if (!TextUtils.isEmpty(autoSyncTime)) {
                        intAutoSyncTime = Integer.parseInt(autoSyncTime);
                    }

                    if (!TextUtils.isEmpty(autoSyncTime)) {
                        if (intFirstTime >= intAutoSyncTime) {
                            try {
                                // UpdatePendingRequest.getInstance(null).callScheduleFirstLoginSync();
                            } catch (Exception e) {
                                LogManager.writeLogError("UpdatePendingRequest : " + e.toString());
                                e.printStackTrace();
                            }
                            ConstantsUtils.startAutoSync(MainActivity.this, true);
                        }
                    } else {
                        ConstantsUtils.startAutoSync(MainActivity.this, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
//                setUI(false,false);
                boolean datavault = PrivateDataVault.vaultExists(Configuration.APP_ID);
                if(!sharedPerf.getBoolean(Constants.DataVaultUpdate,false)) {
                    if (datavault) {
                        LogManager.writeLogInfo("Vault is exists");
                        datavaultData();
//            Toast.makeText(mContext, "Vault is exists", Toast.LENGTH_SHORT).show();
                    } else {
                        LogManager.writeLogInfo("Vault is not exists");
//            Toast.makeText(mContext, "Vault is not exists", Toast.LENGTH_SHORT).show();
                    }
                }
                setUI();
            }
        }
    }

    /*
     *
     * AsyncTask for opening offline store
     *
     */
    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(MainActivity.this, R.style.UtilsDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            closeStore();
            try {
                OfflineManager.openOfflineStore(MainActivity.this, MainActivity.this);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void takeScreenshot() {
        /*Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);*/

        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String doc = System.currentTimeMillis()+"";
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + doc + ".jpg";

            // create bitmap screen capture
            View viewScreenShort = getWindow().getDecorView().getRootView();
            viewScreenShort.setDrawingCacheEnabled(true);

            Bitmap bitmap = Bitmap.createBitmap(viewScreenShort.getDrawingCache());
            viewScreenShort.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        Uri screenshotUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + imageFile);

            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
            startActivity(Intent.createChooser(sharingIntent, "Share image using"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
