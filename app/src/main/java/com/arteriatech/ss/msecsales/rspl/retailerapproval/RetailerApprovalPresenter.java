package com.arteriatech.ss.msecsales.rspl.retailerapproval;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.filter.CustomersFilterActivity;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.store.GetOnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.arteriatech.ss.msecsales.rspl.store.OfflineManager.TAG;

/**
 * Created by e10847 on 19-12-2017.
 */

public class RetailerApprovalPresenter implements ICustomerPresenter, UIListener, GetOnlineODataInterface {
    public static boolean isRefresh = false;
    public ArrayList<RetailerBean> retailerArrayList, searchBeanArrayList;
    ArrayList<String> alAssignColl = null;
    boolean isValidData = true;
    RetailerBean retailerBean = null;
    private int comingFrom = 0;
    private Context context;
    private ICustomerViewPresenter iCustomerViewPresenter;
    private Activity activity;
    private String visitType = "", customerNumber = "", filterType = "", statusId = "", statusName = "", delvStatusId = "", delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private String searchText = "";
    private String salesDistrictId = "";
    private String mStrCustName = "";
    private String beatGuid = "";
    private String cpGuid = "";
    private ArrayList<RetailerBean> alRSCHList = null;
    private int countPerRequest = 50;


    public RetailerApprovalPresenter(Context context, Activity activity, ICustomerViewPresenter iCustomerViewPresenter, String visitType, @NonNull String customerNumber, String salesDistrictId, String cpGuid) {
        this.context = context;
        this.iCustomerViewPresenter = iCustomerViewPresenter;
        this.retailerArrayList = new ArrayList<>();
//        this.cpGrp4Desc = new HashMap<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.visitType = visitType;
        this.activity = activity;
        this.customerNumber = customerNumber;
        this.salesDistrictId = salesDistrictId;
        this.cpGuid = cpGuid;
    }


    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.hideProgressDialog();
        }
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                            context, R.style.MyTheme);
                    builder.setMessage(context.getString(R.string.msg_error_occured_during_sync))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });

                    builder.show();
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            //   Constants.updateLastSyncTimeToTable(context, alAssignColl, Constants.DownLoad);
            Constants.isSync = false;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                    }
                }
            });
        } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    @Override
    public void onFilter() {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.openFilter(filterType, statusId, delvStatusId);
        }
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText);
        }
    }

    @Override
    public void onRefresh() {
        onRefreshRetailerList();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS);
        statusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS_NAME);
        delvStatusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS);
        delvStatusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
//        requestSOList(startDate, endDate);
        displayFilterType();
    }

    private void displayFilterType() {
        try {
            String statusDesc = "";
            if (!TextUtils.isEmpty(statusId)) {
                statusDesc = ", " + statusName;
            }
            if (!TextUtils.isEmpty(delvStatusId)) {
                statusDesc = statusDesc + ", " + delvStatusName;
            }
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAsyncTask(String beatGuid) {
        this.beatGuid = beatGuid;
        retailerArrayList.clear();
        openStore();
    }

    @Override
    public void getRetailerDetails(final RetailerBean retailerBean) {

        String qry = Constants.ChannelPartners + "(guid'" + retailerBean.getCPGUID() + "')?$expand=CPDMSDivisions";
        OnlineManager.doOnlineGetRequest(qry, context, event -> {
            if (event.getResponseStatusCode() == 200) {
                String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(responseBody);
                    JSONObject dObject = jsonObj.getJSONObject("d");
//                    JSONArray resultArray = dObject.getJSONArray("results");
                    RetailerBean retailerBean1= new RetailerBean();
                    try {
                        retailerBean1 = OnlineManager.getRetailerApprovalDetails(dObject, retailerBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    RetailerBean finalRetailerBean = retailerBean1;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iCustomerViewPresenter != null) {
                                iCustomerViewPresenter.hideProgressDialog();
                            }
                            iCustomerViewPresenter.retailerDetails(finalRetailerBean);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayErrorMsg(responseBody);
                }
            } else {
                String errorMsg="";
                try {
                    errorMsg = Constants.getErrorMessage(event,context);
                    displayErrorMsg(errorMsg);
                    LogManager.writeLogError(errorMsg);
                } catch (Throwable e) {
                    e.printStackTrace();
                    displayErrorMsg(e.getMessage());
                    LogManager.writeLogError(e.getMessage());
                }
            }
        }, iError -> {
            iError.printStackTrace();
            String errormessage = "";
            errormessage = ConstantsUtils.geterrormessageForInternetlost(iError.getMessage(),context);
            if(TextUtils.isEmpty(errormessage)){
                errormessage = iError.getMessage();
            }
            displayErrorMsg(errormessage);
        });

    }


    /**
     * Searc query to update the retailerList
     *
     * @param searchText
     */
    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        boolean isCity = false;
        if (retailerArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                for (RetailerBean retailerBean : retailerArrayList) {
                    ArrayList<RetailerBean> tempArr = retailerBean.getItemList();
                    retailerBean.setRetailerCount(String.valueOf(tempArr.size()));
                    searchBeanArrayList.add(retailerBean);
                    searchBeanArrayList.addAll(tempArr);
                }
            } else {

                try {
                    for (RetailerBean item : retailerArrayList) {
                        ArrayList<RetailerBean> tempRetailerList = new ArrayList<>();
                        for (RetailerBean retailerBean : item.getItemList()) {
                            if (!TextUtils.isEmpty(searchText)) {
                                isCustomerID = retailerBean.getCPNo().toLowerCase().contains(searchText.toLowerCase());
                                isCustomerName = retailerBean.getRetailerName().toLowerCase().contains(searchText.toLowerCase());
                                isCity = retailerBean.getCity().toLowerCase().contains(searchText.toLowerCase());
                            } else {
                                isCustomerID = true;
                                isCustomerName = true;
                                isCity = true;
                            }
                            if (isCustomerID || isCustomerName || isCity) {
                                tempRetailerList.add(retailerBean);
                            }
                        }
                        if (tempRetailerList.size() > 0) {
                            item.setRetailerCount(String.valueOf(tempRetailerList.size()));
                            searchBeanArrayList.add(item);
                            searchBeanArrayList.addAll(tempRetailerList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.searchResult(searchBeanArrayList);
        }
    }

    /**
     * refreshing the RetailerList Online
     */
    private void onRefreshRetailerList() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getFOS());
            alAssignColl.addAll(SyncUtils.getBeatCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.displayMsg(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    // progressDialog = Constants.showProgressDialog(context, "", context.getString(R.string.msg_sync_progress_msg_plz_wait));
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        iCustomerViewPresenter.displayMsg(e.getMessage());
                    }
                }
            }
        } else {
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                iCustomerViewPresenter.displayMsg(context.getString(R.string.no_network_conn));
            }
        }
    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {

            case 1:
                try {
                    RetailerApprovalListActivity.RetApprovalTotalCount = String.valueOf(entities.size());
                    retailerArrayList = OfflineManager.getRetailerApprovalList(entities);
                    Log.d(TAG, "Ret Approval List Loaded");

                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                        onSearchQuery(searchText);
                    }
                });
                break;
            case 2:

                try {
                    retailerBean = OnlineManager.getRetailerApprovalDetails(oDataRequestExecution, retailerBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                        iCustomerViewPresenter.retailerDetails(retailerBean);
                    }
                });
                break;
        }


    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, String errorMsg, Bundle bundle) {
        try {
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                if (errorMsg.contains("HTTP Status 401 ? Unauthorized") || errorMsg.contains("invalid authentication")) {
                    try {
                        SharedPreferences sharedPreferences =context.getSharedPreferences(Constants.PREFS_NAME,0);
                        String loginUser=sharedPreferences.getString("username","");
                        String login_pwd=sharedPreferences.getString("password","");
                        UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                            @Override
                            public void passwordStatus(final JSONObject jsonObject) {


                                ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Constants.passwordStatusErrorMessage(context, jsonObject, loginUser);
                                        }
                                    });
                                }


                        });
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
//                    Constants.customAlertDialogWithScrollError(context, errorMsg, activity);
                } else {
                    ConstantsUtils.displayErrorDialog(context, errorMsg);
                }
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openStore() {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
        }
//        if (store == null) {
        Log.e("Main", "opening store for user profile ");
        try {
//            if (store != null) {
            requestRetApprovalList();
           /* } else {
                new OpenOnlineManagerStore(context, new AsyncTaskCallBack() {
                    @Override
                    public void onStatus(boolean status, String values) {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                        if (status) {

                            requestRetApprovalList();
                        } else {
                            ConstantsUtils.displayShortToast(context, values);
                        }
                    }
                }).execute();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void requestRetApprovalList() {
        if (UtilConstants.isNetworkAvailable(context)) {
//                new LoadData().execute();
            Log.d(TAG, " Before Getting SO List");
            getDataFromDB(context);
            isRefresh = false;
        } else {
            ConstantsUtils.dialogBoxWithButton(context, "", context.getString(R.string.no_network_conn), context.getString(R.string.ok), "", null);
        }


    }


    private void getDataFromDB(Context mContext) {
        String qry = "";
        iCustomerViewPresenter.showProgressDialog();
        /*Bundle bundle = new Bundle();

        if (comingFrom == 1) {//Contract approval
            bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CONTRACT'");
            if (Constants.writeDebug) {
                LogManager.writeLogDebug("Dashboard refresh : Contract Approval URL : " + Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CONTRACT'");
            }
        } else {//SO approval
            bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CP'");
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
            bundle.putInt(Constants.BUNDLE_OPERATION, Operation.GetRequest.getValue());
            bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, true);
            bundle.putBoolean(Constants.BUNDLE_SESSION_URL_REQUIRED, true);
            if (Constants.writeDebug) {
                LogManager.writeLogDebug("Dashboard refresh : SO Approval URL : " + Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CP'");
            }
        }

        try {
            OnlineManager.requestOnline(this, bundle, mContext);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt1 + " : " + e.getMessage());
            e.printStackTrace();
            iCustomerViewPresenter.hideProgressDialog();

        }*/
        if (comingFrom == 1)//Contract approval
            qry = Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CONTRACT'";
        else //SO approval
            qry = Constants.Tasks + "/?$select=InstanceID,EntityKey,EntityDate1,EntityKeyID,EntityKeyDesc,EntityCurrency,EntityValue1,EntityAttribute1,EntityType,PriorityNumber&$filter=" + Constants.EntityType + "+eq+'CP'";
        OnlineManager.doOnlineGetRequest(qry, context, event -> {
            if (event.getResponseStatusCode() == 200) {
                String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(responseBody);
                    JSONObject dObject = jsonObj.getJSONObject("d");
                    JSONArray resultArray = dObject.getJSONArray("results");
                    try {
                        RetailerApprovalListActivity.RetApprovalTotalCount = String.valueOf(resultArray.length());
                        retailerArrayList = OfflineManager.getRetailerApprovalListJSON(resultArray);
                        Log.d(TAG, "Ret Approval List Loaded");

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogManager.writeLogError(Constants.error_txt + " : " + e.getMessage());
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iCustomerViewPresenter != null) {
                                iCustomerViewPresenter.hideProgressDialog();
                            }
                            onSearchQuery(searchText);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayErrorMsg(responseBody);
                }
            } else {
                String errorMsg="";
                try {
                    errorMsg = Constants.getErrorMessage(event,context);
                    displayErrorMsg(errorMsg);
                    LogManager.writeLogError(errorMsg);
                } catch (Throwable e) {
                    e.printStackTrace();
                    displayErrorMsg(e.getMessage());
                    LogManager.writeLogError(e.getMessage());
                }
            }
        }, iError -> {
            iError.printStackTrace();
            String errormessage = "";
            errormessage = ConstantsUtils.geterrormessageForInternetlost(iError.getMessage(),context);
            if(TextUtils.isEmpty(errormessage)){
                errormessage = iError.getMessage();
            }
            displayErrorMsg(errormessage);
        });
    }

    private void displayErrorMsg(String errorMsg) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.hideProgressDialog();
            ConstantsUtils.displayErrorDialog(context, errorMsg);
        }
    }
}
