package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SyncSelectionViewActivity extends AppCompatActivity implements SyncSelectionViewInterface, UIListener {

    private boolean dialogCancelled = false;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ProgressDialog syncProgDialog = null;
    private GUID refguid =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_selection_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sync_menu), 0);
        try {
            SharedPreferences mSharedPrefs = SyncSelectionViewActivity.this.getSharedPreferences(Constants.LOGPREFS_NAME, 0);
            if (mSharedPrefs.getBoolean("writeDBGLog", false)) {
                Constants.writeDebug = mSharedPrefs.getBoolean("writeDBGLog", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Fragment fragment = new SyncSelectionViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_BEAN_LIST, SyncUtils.getSyncSelectionView(SyncSelectionViewActivity.this));
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSelectedCollection(ArrayList<String> selectedList) {
        alAssignColl.clear();
        if (Constants.iSAutoSync) {
            ConstantsUtils.displayLongToast(SyncSelectionViewActivity.this, getString(R.string.alert_auto_sync_is_progress));
        } else {
            if (UtilConstants.isNetworkAvailable(SyncSelectionViewActivity.this)) {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    alAssignColl.addAll(selectedList);
                    new AsyncSyncData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ConstantsUtils.displayLongToast(SyncSelectionViewActivity.this, getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void closeProgrDialog() {
        closingProgressDialog();

    }

    @Override
    protected void onDestroy() {
//        allSyncPresenter.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onRequestError(int operation, Exception e) {
        String strErrorMsg=e.toString();
        ErrorBean errorBean = Constants.getErrorCode(operation, e, SyncSelectionViewActivity.this);
        if(Constants.writeDebug){
            LogManager.writeLogDebug("Download Sync : Failed"+ e.getLocalizedMessage());
        }
        if (errorBean.hasNoError()) {
            if (dialogCancelled == false && !Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
//                    updatingSyncTime(Constants.DownLoad);
                    if (strErrorMsg.contains("invalid authentication")||strErrorMsg.contains("HTTP Status 401 ? Unauthorized")) {
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
                                                closingProgressDialog();
                                                Constants.passwordStatusErrorMessage(SyncSelectionViewActivity.this, jsonObject, loginUser);
                                            }
                                        });
                                    }

                                }
                            });
                        } catch (Throwable e1) {
                            e1.printStackTrace();
                        }
//                        inValidPasswordDialog(strErrorMsg);
                    } else {
                        Constants.isSync = false;
                        SyncUtils.updatingSyncTime(SyncSelectionViewActivity.this, alAssignColl, Constants.DownLoad,refguid.toString().toUpperCase(), new RefreshListInterface() {
                            @Override
                            public void refreshList() {
                                closingProgressDialog();
                                syncCompletedWithErrorDialog();
                            }
                        });
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    closingProgressDialog();
                    Constants.isSync = false;
                    syncCompletedWithErrorDialog();
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                closingProgressDialog();
                Constants.isSync = true;
                dialogCancelled = false;
                new AsyncSyncData().execute();
            } else {
                Constants.isSync = false;
                closingProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionViewActivity.this);
            }
        } else {
            Constants.isSync = false;
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionViewActivity.this);
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (dialogCancelled == false && !Constants.isStoreClosed) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
//                Constants.setBirthdayListToDataValut(SyncSelectViewActivity.this);
//                setAppointmentNotification();
//                updatingSyncTime(Constants.DownLoad);
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(SyncSelectionViewActivity.this,false);
                if(alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners) ) {
                    Constants.setBirthdayListToDataValut(SyncSelectionViewActivity.this);
                    Constants.setBirthDayRecordsToDataValut(SyncSelectionViewActivity.this);
                    Constants.setAppointmentNotification(SyncSelectionViewActivity.this);
                }

                if(alAssignColl.contains(Constants.Alerts)) {
                    Constants.setAlertsRecordsToDataValut(SyncSelectionViewActivity.this);
                }
                SyncUtils.updatingSyncTime(SyncSelectionViewActivity.this, alAssignColl, Constants.DownLoad,refguid.toString().toUpperCase(), new RefreshListInterface() {
                    @Override
                    public void refreshList() {
                        closingProgressDialog();
                        syncCompletedDialog();
                    }
                });
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(SyncSelectionViewActivity.this, Constants.Sync_All);

                if(alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners) ) {
                    Constants.setBirthdayListToDataValut(SyncSelectionViewActivity.this);
                    Constants.setBirthDayRecordsToDataValut(SyncSelectionViewActivity.this);
                    Constants.setAppointmentNotification(SyncSelectionViewActivity.this);
                }

                if(alAssignColl.contains(Constants.Alerts)) {
                    Constants.setAlertsRecordsToDataValut(SyncSelectionViewActivity.this);
                }
                ConstantsUtils.startAutoSync(SyncSelectionViewActivity.this,false);
                closingProgressDialog();
                if(Constants.writeDebug){
                    LogManager.writeLogDebug("Download Sync : Completed");
                }
                syncCompletedDialog();
            }
        }
    }

    private void updatingSyncTime(String syncType) {
        SyncUtils.updatingSyncTime(SyncSelectionViewActivity.this, alAssignColl,syncType,refguid.toString().toUpperCase(),null);
    }

    private void closingProgressDialog() {
        try {
            if(syncProgDialog!=null)
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void syncCompletedWithErrorDialog() {
        UtilConstants.dialogBoxWithCallBack(SyncSelectionViewActivity.this, "", getString(R.string.msg_error_occured_during_sync), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                onBackPressed();
            }
        });
    }

    private void inValidPasswordDialog(String mErrTxt) {
        LayoutInflater inflater = LayoutInflater.from(SyncSelectionViewActivity.this);
        View view = inflater.inflate(R.layout.custom_dialog_scroll, null);
        TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);
        final TextView tvdetailmsg = (TextView) view.findViewById(R.id.tv_detail_msg);

        if (mErrTxt.contains("invalid authentication") || mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
            textview.setText(Constants.PasswordExpiredMsg);
            tvdetailmsg.setText(mErrTxt);
        } else {
            textview.setText(mErrTxt);
        }
            final AlertDialog dialog = new AlertDialog.Builder(SyncSelectionViewActivity.this)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                    .setNeutralButton("Show Details", null)
                    .setNegativeButton("Settings", null)
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                           onBackPressed();
                        }
                    });

                    final Button mesg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                    mesg.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something


                            if (mesg.getText().equals("Hide Details")) {
                                tvdetailmsg.setVisibility(View.GONE);
                                mesg.setText("Show Details");
                            } else {
                                tvdetailmsg.setVisibility(View.VISIBLE);
                                mesg.setText("Hide Details");
                            }


                            // dialog.dismiss();
                        }
                    });
                    Button change = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    change.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                            RegistrationModel<Serializable> registrationModel = new RegistrationModel<>();
                            Intent intent = new Intent(SyncSelectionViewActivity.this, com.arteriatech.mutils.support.SecuritySettingActivity.class);
                            registrationModel.setExtenndPwdReq(true);
                            registrationModel.setUpdateAsPortalPwdReq(true);
                            registrationModel.setIDPURL(Configuration.IDPURL);
                            registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                            registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                            intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                            //context.startActivityForResult(intent, 350);
                            startActivity(intent);
                            // dialog.dismiss();
                        }
                    });

                }
            });
            dialog.show();

    }
    private void syncCompletedDialog() {
        UtilConstants.dialogBoxWithCallBack(SyncSelectionViewActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, SyncSelectionViewActivity.this, BuildConfig.APPLICATION_ID, true, Constants.APP_UPGRADE_TYPESET_VALUE))
                    onBackPressed();
            }
        });
    }
    public class AsyncSyncData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionViewActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionViewActivity.this, R.style.MyTheme);
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
                                                    SyncUtils.updatingSyncStartTime(SyncSelectionViewActivity.this,Constants.download_cancel_sync,Constants.EndSync,refguid.toString().toUpperCase());

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
        protected Void doInBackground(Void... params) {
            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                alAssignColl.add(Constants.ConfigTypsetTypeValues);
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(SyncSelectionViewActivity.this, SyncSelectionViewActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                try {
                    String concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(SyncSelectionViewActivity.this,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectionViewActivity.this, Constants.Fresh, concatCollectionStr);
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
}
