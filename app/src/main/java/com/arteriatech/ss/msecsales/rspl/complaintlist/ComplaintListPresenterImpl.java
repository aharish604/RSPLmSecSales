package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.FlushDataAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class ComplaintListPresenterImpl implements ComplaintListPresenter, UIListener {
    ArrayList<ComplaintListModel> alComplintsList = new ArrayList<>();
    ArrayList<ComplaintListModel> alComplaintsSearchList = new ArrayList<>();
    boolean soSearchStatus = false;
    Context context;
    ComplaintListView complaintListView;
    private int mError = 0;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private String comingFrom = "";
    private String mCPNO = "";
    private String syncType = "";
    private String ParentId = "";
    private GUID refguid =null;


    public ComplaintListPresenterImpl(Context context, ComplaintListView complaintListView, Bundle bundle, String mCPNO) {
        this.context = context;
        this.mCPNO = mCPNO;
        this.complaintListView = complaintListView;
        comingFrom = bundle.getString(Constants.comingFrom);
        syncType = bundle.getString(Constants.SyncType);
        ParentId = bundle.getString(Constants.ParentId);
    }

    @Override
    public void searchComplaint(String searchText) {
        alComplaintsSearchList.clear();
        if (alComplintsList != null) {
            if (TextUtils.isEmpty(searchText)) {
                alComplaintsSearchList.addAll(alComplintsList);
            } else {
                for (ComplaintListModel item : alComplintsList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getComplaintNo().contains(searchText);
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        alComplaintsSearchList.add(item);
                }
            }
        }
        if (complaintListView != null) {
            complaintListView.setSearchedComplaintListDatatoAdapter(alComplaintsSearchList);
        }
    }

    @Override
    public void onUploadData() {

        try {
            mError = 0;
            if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && alComplintsList.size() == 0) {
                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    complaintListView.showMessage(context.getString(R.string.no_req_to_update_merchant));
                }
            } else {
                if (Constants.iSAutoSync) {
                    if (complaintListView != null) {
                        complaintListView.hideProgressBar();
                        complaintListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                    }
                } else {
                    getRefreshList();

                    if (UtilConstants.isNetworkAvailable(context)) {
                        if (complaintListView != null) {
                            complaintListView.loadProgressBar();
                        }
                        Constants.isSync = true;
                        try {
                            refguid = GUID.newRandom();
                            SyncUtils.updatingSyncStartTime(context,Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                            new FlushDataAsyncTask(this,alFlushColl).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (complaintListView != null) {
                            complaintListView.hideProgressBar();
                            complaintListView.showMessage(context.getString(R.string.no_network_conn));
                        }
                    }
                }
            }

        } catch (ODataException e) {
            e.printStackTrace();
            if (complaintListView != null) {
                complaintListView.hideProgressBar();
            }
        }
    }

    @Override
    public void onStart(final String mcpNo) {
        if (complaintListView != null) {
            complaintListView.loadProgressBar();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    alComplintsList.clear();
                    if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                        alComplintsList.addAll(OfflineManager.getComplaintList(Constants.Complaints + " " + Constants.isLocalFilterQry + " " +
                                "and " + Constants.CPNo + " eq '" + mcpNo + "' "+
                                "and " + Constants.ParentNo + " eq '" + ParentId + "' "));
                    } else {
                        String tempParentID = "";
                        if(!TextUtils.isEmpty(ParentId)){
                            tempParentID = String.valueOf(Integer.parseInt(ParentId));
                        }
                        alComplintsList.addAll(OfflineManager.getComplaintList(Constants.Complaints + " " + Constants.isNonLocalFilterQry + " " +
                                "and " + Constants.CPNo + " eq '" + mcpNo + "' "+
                                "and " + Constants.ParentNo + " eq '" + tempParentID + "' "));
                    }

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (complaintListView != null) {
                            complaintListView.hideProgressBar();
                            complaintListView.setComplaintListDatatoAdapter(alComplintsList);
//                            complaintListView.displayLSTSyncTime(SyncUtils.getCollectionSyncTime(context, Constants.MerchReviews));
                        }
                    }
                });
            }
        }).start();
    }

    private void getRefreshList() {
        alAssignColl.clear();
        alFlushColl.clear();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.Complaints + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Complaints);
                alFlushColl.add(Constants.Complaints);
                alFlushColl.add(Constants.ComplaintDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.VisitActivities);
                alFlushColl.add(Constants.VisitActivities);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                String concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                Constants.Entity_Set.clear();
                Constants.AL_ERROR_MSG.clear();
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            OfflineManager.flushQueuedRequests(ComplaintListPresenterImpl.this, concatFlushCollStr);
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

    @Override
    public void onRequestError(int operation, Exception exception) {
        final ErrorBean errorBean = Constants.getErrorCode(operation, exception, context);
        if (errorBean.hasNoError()) {
            mError++;
            if (operation == Operation.OfflineFlush.getValue()) {
                Constants.isSync = false;
                ComplainListActivity.mBoolRefreshDone = true;

                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    Constants.displayErrorDialog(context, errorBean.getErrorMsg());
                }

            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                ComplainListActivity.mBoolRefreshDone = true;

                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    Constants.displayErrorDialog(context, errorBean.getErrorMsg());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    complaintListView.showMessage(context.getString(R.string.msg_offline_store_failure));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;

                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (complaintListView != null) {
                complaintListView.hideProgressBar();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }
    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                String concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                ComplainListActivity.mBoolRefreshDone = true;
                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    complaintListView.showMessage(context.getString(R.string.no_network_conn));
                }
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,syncType,refguid.toString().toUpperCase());
//            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.MerchReviews, Constants.TimeStamp, this));
//            closingProgressDialog();

            Constants.isSync = false;
            ComplainListActivity.mBoolRefreshDone = true;
            ConstantsUtils.startAutoSync(context, false);
            if (complaintListView != null) {
                complaintListView.hideProgressBar();
                if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                    if (mError == 0) {
                        complaintListView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                    } else {
                        complaintListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
//                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                onStart(mCPNO);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            Constants.setSyncTime(context, Constants.Sync_All);
            if (complaintListView != null) {
                complaintListView.hideProgressBar();
                complaintListView.showMessage(context.getString(R.string.msg_offline_store_success));
//                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                onStart(mCPNO);
            }
        }
    }
    @Override
    public void onRefresh() {
        if (complaintListView != null) {
            complaintListView.showMessage("");
        }
        mError = 0;
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getComplintsList());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (complaintListView != null) {
                    complaintListView.hideProgressBar();
                    complaintListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    SyncUtils.updatingSyncStartTime(context,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (complaintListView != null) {
                complaintListView.hideProgressBar();
                complaintListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }
}
