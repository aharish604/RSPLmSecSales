package com.arteriatech.ss.msecsales.rspl.customers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotPostedRetailerPresenterImpl implements NotPostedRetailerPresenter, UIListener, MessageWithBooleanCallBack {
    private Context context;
    private NotPostedRetailerViewPresenter retailerViewPresenter;
    private Activity activity;
    private ArrayList<RetailerBean> alRetailerBeans;
    private ArrayList<String> notPostedRetailerArrayList = new ArrayList<>();
    private ArrayList<String> tempCPList = new ArrayList<>();
    private ArrayList<String> pendingCollectionList = new ArrayList<>();
    private String[] tempRetailer;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private String concatCollectionStr = "";
    private boolean isErrorFromBackend = false;
    private String error = "";
    private GUID refguid =null;

    public NotPostedRetailerPresenterImpl(Context context, Activity activity, NotPostedRetailerViewPresenter retailerViewPresenter) {
        this.context = context;
        this.retailerViewPresenter = retailerViewPresenter;
        this.activity = activity;
    }

    @Override
    public void getCPList(ArrayList<String> cpList) {
        if (retailerViewPresenter != null) {
            retailerViewPresenter.showProgressDialog();
        }

        try {
            alRetailerBeans = OfflineManager.getCPListFromDataValt(context, cpList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (retailerViewPresenter != null) {
                    retailerViewPresenter.displayDuplicateCPList(alRetailerBeans);
                    retailerViewPresenter.hideProgressDialog();
                }
            }
        });

    }

    @Override
    public void onSyncSOrder() {
        error = "";
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        isErrorFromBackend = false;
        tempCPList.clear();
        try {
            notPostedRetailerArrayList.clear();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NOT_POSTED_RETAILERS, 0);
            Set<String> set = sharedPreferences.getStringSet(Constants.duplicateCPList, null);
            notPostedRetailerArrayList = new ArrayList<>(set);
            tempRetailer = new String[notPostedRetailerArrayList.size()];
            pendingROVal = 0;
            penROReqCount = 0;
            if (!notPostedRetailerArrayList.isEmpty()) {
                if (Constants.iSAutoSync) {
                    if (retailerViewPresenter != null) {
                        retailerViewPresenter.hideProgressDialog();
                        retailerViewPresenter.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.addAll(SyncUtils.getFOS());
                        if (retailerViewPresenter != null) {
                            retailerViewPresenter.showProgressDialog();
                        }
                        for (int k = 0; k < notPostedRetailerArrayList.size(); k++) {
                            tempRetailer[k] = notPostedRetailerArrayList.get(k);
                            pendingROVal++;
                        }
                        new SyncFromDataValtAsyncTask(context, tempRetailer, this, this).execute();
                    } else {
                        if (retailerViewPresenter != null) {
                            retailerViewPresenter.hideProgressDialog();
                            retailerViewPresenter.showMessage(context.getString(R.string.no_network_conn));
                        }
                    }
                }
            } else if (retailerViewPresenter != null) {
                retailerViewPresenter.hideProgressDialog();
                retailerViewPresenter.showMessage(context.getString(R.string.no_req_to_update_sap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (retailerViewPresenter != null) {
                retailerViewPresenter.hideProgressDialog();
            }
        }
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            Constants.mBoolIsReqResAval = true;
            if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                if (!isErrorFromBackend) {
                    isErrorFromBackend = true;
                }

//                if (tempRetailer[penROReqCount].equalsIgnoreCase(Constants.CPList)) {
                tempCPList.add(tempRetailer[penROReqCount]);
                HashSet companySet = new HashSet(tempCPList);
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NOT_POSTED_RETAILERS, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(Constants.duplicateCPList, companySet);
                editor.commit();
                Constants.removeFromSharKey(tempRetailer[penROReqCount],errorBean.getErrorMsg(),context,false);
                penROReqCount++;
//                }
            }


            error = error + "\n" + errorBean.getErrorMsg();

            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                if (retailerViewPresenter != null) {
                    retailerViewPresenter.hideProgressDialog();
                    ConstantsUtils.startAutoSync(context, false);
                    if (retailerViewPresenter != null) {
                        if(!TextUtils.isEmpty(error)) {
                            retailerViewPresenter.showMessage(error);
                        }else {
                            retailerViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                        retailerViewPresenter.reload();
                    }
                }
            }
            if (operation == Operation.OfflineRefresh.getValue()) {
                if (retailerViewPresenter != null) {
                    retailerViewPresenter.hideProgressDialog();
                    ConstantsUtils.startAutoSync(context, false);
                    if (retailerViewPresenter != null) {
                        retailerViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        retailerViewPresenter.reload();
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (retailerViewPresenter != null) {
                    retailerViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (retailerViewPresenter != null) {
                    retailerViewPresenter.hideProgressDialog();
                    retailerViewPresenter.reload();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (retailerViewPresenter != null) {
                retailerViewPresenter.hideProgressDialog();
                retailerViewPresenter.reload();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }


    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue() && pendingROVal > 0) {
            Constants.mBoolIsReqResAval = true;
            Constants.removeDataValtFromSharedPref(context, Constants.CPList, tempRetailer[penROReqCount], false);
            Constants.removeDataValtFromSharedPref(context, Constants.NOT_POSTED_RETAILERS, tempRetailer[penROReqCount], false);
            ConstantsUtils.storeInDataVault(tempRetailer[penROReqCount], "",context);
            Constants.removeFromSharKey( tempRetailer[penROReqCount],"",context,true);
            penROReqCount++;


        }
        if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
            new RefreshAsyncTask(context, concatCollectionStr, this).execute();
        } else if (operation == Operation.OfflineRefresh.getValue()) {
       //     Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.DownLoad);
            SalesOrderHeaderListActivity.mBoolRefreshDone = true;
            if (retailerViewPresenter != null) {
                retailerViewPresenter.hideProgressDialog();
                String msg = "";
                ConstantsUtils.startAutoSync(context, false);
                if (retailerViewPresenter != null) {
                    if (!isErrorFromBackend) {
                        retailerViewPresenter.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                        retailerViewPresenter.reload();
                    } else {
                        retailerViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        retailerViewPresenter.reload();
                    }
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }


    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (retailerViewPresenter != null) {
                retailerViewPresenter.hideProgressDialog();
                UtilConstants.showAlert(errorMsg, context);
            }
        }
    }
}
