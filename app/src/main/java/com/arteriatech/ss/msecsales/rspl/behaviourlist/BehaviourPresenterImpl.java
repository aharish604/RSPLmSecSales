package com.arteriatech.ss.msecsales.rspl.behaviourlist;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by e10893 on 25-01-2018.
 */

public class BehaviourPresenterImpl implements IBehaviourListPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    private Context context;
    private Activity activity;
    private ArrayList<RetailerBean> CustomerBeanArrayList;
    private ArrayList<RetailerBean> searchBeanArrayList;
    private Hashtable<String, String> headerTable;
    private String SPGUID = "";
    private String searchText = "";
    private String CPGUID = "", CPUID = "", cpNo = "", cpName = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private GUID refguid =null;

    private IBehaviourListView view;

    public BehaviourPresenterImpl(Context context, IBehaviourListView view, Activity activity) {
        this.context = context;
        this.view = view;
        this.CustomerBeanArrayList = new ArrayList<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
    }


    @Override
    public void onFilter() {
/*
        if (view != null) {
            view.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }*/

    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchQuery(searchText);
        }
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (CustomerBeanArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(CustomerBeanArrayList);
            } else {
                for (RetailerBean item : CustomerBeanArrayList) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getNameNumber().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (view != null) {
            view.searchResult(searchBeanArrayList);
          /*  else
                view.updateFragment(searchBeanArrayList,simpleRecyclerViewAdapter);*/
        }
    }


    @Override
    public void onRefresh(String statusID) {
        this.statusId = statusID;
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            if (view != null) {
                view.showProgressDialog();
            }
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getBehaviorList());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (view != null) {
                    view.hideProgressDialog();
                    view.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Behav_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (view != null) {
                view.hideProgressDialog();
                view.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

  /*  @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS);
        statusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME);
        delvStatusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS);
        delvStatusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS_NAME);
        loadAsyncTask(statusId);
        displayFilterType();
    }*/

    @Override
    public void loadAsyncTask(String statusId) {
        if (view != null) {
            view.showProgressDialog();
        }
        new CustomerAsyncTask().execute(statusId);
    }

    @Override
    public void onDestroy() {
        view = null;
    }


    /**
     * @desc get Campaign HeaderList from OfflineDB
     */
    private ArrayList<RetailerBean> getBehaviourList(String statusId) {
        String query = "";
        if (!TextUtils.isEmpty(statusId)) {
            if ("000004".equals(statusId)) {
                query = Constants.SPChannelEvaluationList + "?$filter=" + Constants.EvaluationTypeID + " eq '" + statusId + "' and "+Constants.SPGUID+" eq guid'"+Constants.getSPGUID(Constants.SPGUID) + "' &$orderby=CPName asc";
            } else if("000002".equals(statusId)){
                query = Constants.SPChannelEvaluationList + "?$filter=" + Constants.EvaluationTypeID + " eq '" + statusId + "' and "+Constants.SPGUID+" eq guid'"+Constants.getSPGUID(Constants.SPGUID) + "' &$orderby=PurchaseAmount asc";
            } else {
                query = Constants.SPChannelEvaluationList + "?$filter=" + Constants.EvaluationTypeID + " eq '" + statusId + "' and "+Constants.SPGUID+" eq guid'"+Constants.getSPGUID(Constants.SPGUID) + "' &$orderby=PurchaseAmount desc";
            }
        } else {
            query = Constants.SPChannelEvaluationList/*+"?$filter=ApplicationID eq 'SF'"*/;

        }

        CustomerBeanArrayList = new ArrayList<RetailerBean>();
        try {
            CustomerBeanArrayList.addAll(OfflineManager.getBehavoiurList(query,statusId));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return CustomerBeanArrayList;
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
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (i == Operation.OfflineRefresh.getValue()) {
                if (view != null) {
                    view.hideProgressDialog();
                    view.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }else if (i == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (view != null) {
                    view.hideProgressDialog();
                    view.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (view != null) {
                    view.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (view != null) {
                    view.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            String mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(), context, false);
            if (view != null) {
                view.hideProgressDialog();
                view.showMessage(mStrPopUpText);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Behav_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (view != null) {
                            view.hideProgressDialog();
                            view.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.SPChannelEvaluationList));
                            loadAsyncTask(statusId);
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
                Constants.setSyncTime(context,Constants.Sync_All);
                ConstantsUtils.startAutoSync(context, false);
                if (view != null) {
                    view.hideProgressDialog();
                    loadAsyncTask(statusId);
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }


    private class CustomerAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (view != null)
                view.showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... params) {
            String identifier = params[0];
            getBehaviourList(identifier);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (view != null) {
                view.hideProgressDialog();
                onSearchQuery(searchText);
            }
        }
    }
}
