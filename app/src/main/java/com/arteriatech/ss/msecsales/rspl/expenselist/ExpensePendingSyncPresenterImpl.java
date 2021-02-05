package com.arteriatech.ss.msecsales.rspl.expenselist;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.FlushDataAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class ExpensePendingSyncPresenterImpl implements ExpensePendingSyncPresenter,UIListener,MessageWithBooleanCallBack {
    Context context;
    ExpenseListView expenseListView;
    Activity activity;
    ExpenseListView.ExpenseListResponse<ExpenseListBean> expenseListBeanExpenseListResponse;
    ArrayList<ExpenseListBean> expenseListBeansList = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> pendingCollectionList = null;
    private int pendingROVal = 0;
    private String[] tempRODevList = null;
    private int penROReqCount = 0;
    private int isFromWhere = 0;
    private String concatCollectionStr = "";
    private boolean isErrorFromBackend = false;
    private int mError = 0;
    private GUID refguid =null;
    public ExpensePendingSyncPresenterImpl(Context context, ExpenseListView expenseListView, Activity activity) {
        this.context = context;
        if (expenseListView instanceof ExpenseListView) {
            this.expenseListView = expenseListView;
            this.activity = activity;
        }
        pendingCollectionList=new ArrayList<>();
    }
    @Override
    public void start(final ExpenseListView.ExpenseListResponse<ExpenseListBean> expenseListBeanExpenseListResponse) {
        this.expenseListBeanExpenseListResponse = expenseListBeanExpenseListResponse;
        if (expenseListView != null) {
            expenseListView.loadProgressBar();
        }
        expenseListBeansList.clear();
        try {
                    ExpensePendingSyncPresenterImpl.GetExpenseListAsyncTask salesOrderAsyncTask = new ExpensePendingSyncPresenterImpl.GetExpenseListAsyncTask(expenseListBeanExpenseListResponse);
                    salesOrderAsyncTask.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
    @Override
    public void syncExpenseList() {

        isErrorFromBackend = false;
        isFromWhere = 4;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        try {
            expenseListBeansList.clear();
            expenseListBeansList =  OfflineManager.getExpenseListFromDataValt(context);
            if (!expenseListBeansList.isEmpty()) {
                if (Constants.iSAutoSync) {
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.addAll(SyncUtils.getExpenseListCollection());
                        pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                        pendingROVal = 0;
                        if (tempRODevList != null) {
                            tempRODevList = null;
                            penROReqCount = 0;
                        }

                        if (expenseListBeansList != null && expenseListBeansList.size() > 0) {
                            tempRODevList = new String[expenseListBeansList.size()];

                            for (ExpenseListBean expenseListBean : expenseListBeansList) {
                                tempRODevList[pendingROVal] = expenseListBean.getDeviceNo();
                                pendingROVal++;
                            }
                            if (expenseListView != null) {
                                expenseListView.loadProgressBar();
                            }
                            refguid = GUID.newRandom();
                            SyncUtils.updatingSyncStartTime(context,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());

                            new SyncFromDataValtAsyncTask(context, tempRODevList, this, this).execute();
                        }
                    } else {
                        if (expenseListView != null) {
                            expenseListView.hideProgressBar();
                            expenseListView.showMessage(context.getString(R.string.no_network_conn));
                        }
                    }
                }
            } else if (expenseListView != null) {
                expenseListView.hideProgressBar();
                expenseListView.showMessage(context.getString(R.string.no_req_to_update_sap));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
            }
        }


    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (operation == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.reloadata();
                        if (!Constants.isStoreClosed) {
                            expenseListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        } else {
                            expenseListView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }


            } else {
                Constants.mBoolIsReqResAval = true;
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    new RefreshAsyncTask(context, Constants.Visits, this).execute();
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.reloadata();
                        UtilConstants.showAlert(context.getString(R.string.msg_error_occured_during_sync), context);
                    }
                }else{
                    Constants.isSync = false;
                    LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.reloadata();
                        UtilConstants.showAlert(context.getString(R.string.msg_error_occured_during_sync), context);
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (expenseListView != null) {
                    expenseListView.loadProgressBar();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (expenseListView != null) {
                    expenseListView.hideProgressBar();
                    expenseListView.reloadata();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                expenseListView.reloadata();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }

    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue() && pendingROVal > 0) {
            Constants.mBoolIsReqResAval = true;

            Constants.removeDataValtFromSharedPref(context, Constants.Expenses, tempRODevList[penROReqCount], false);
            try {
                ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount],"",context);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            penROReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
            if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                if (UtilConstants.isNetworkAvailable(context)) {
                    try {
                        ArrayList<String> alFlushColl = new ArrayList<>();
                        alFlushColl.add(Constants.ExpenseDocuments);
                        pendingCollectionList.add(Constants.ExpenseDocuments);
                        new FlushDataAsyncTask(this, alFlushColl).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (expenseListView != null) {
                        expenseListView.hideProgressBar();
                        expenseListView.showMessage(context.getString(R.string.data_conn_lost_during_sync));
                    }
                }
            }else {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
            new RefreshAsyncTask(context, concatCollectionStr, this).execute();
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.DownLoad,refguid.toString().toUpperCase());

            SalesOrderHeaderListActivity.mBoolRefreshDone = true;
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                String msg = "";
                ConstantsUtils.startAutoSync(context, false);
                if (expenseListView != null) {
                    if (!isErrorFromBackend) {
                        expenseListView.showMessage(context.getString(R.string.msg_sync_successfully_completed));
                        expenseListView.reloadata();
                    } else {
                        expenseListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        expenseListView.reloadata();
                    }
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            Constants.mBoolIsReqResAval = true;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(context, false);
            if (expenseListView != null) {
                expenseListView.reloadata();
                expenseListView.hideProgressBar();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {

        if (!clickedStatus) {
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                UtilConstants.showAlert(errorMsg, context);
            }
        }
    }

    public class GetExpenseListAsyncTask extends AsyncTask<Void, Void, ArrayList<ExpenseListBean>> {

        ExpenseListView.ExpenseListResponse<ExpenseListBean> expenseListBeanExpenseListResponse;
        public GetExpenseListAsyncTask(ExpenseListView.ExpenseListResponse<ExpenseListBean> expenseListBeanExpenseListResponse) {
            this.expenseListBeanExpenseListResponse = expenseListBeanExpenseListResponse;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (expenseListView != null) {
                expenseListView.loadProgressBar();
            }
        }
        @Override
        protected ArrayList<ExpenseListBean> doInBackground(Void... params) {
            expenseListBeansList.clear();
            getSampleDisbursementList();
            return expenseListBeansList;
        }
        @Override
        protected void onPostExecute(ArrayList<ExpenseListBean> expenseListBean) {
            super.onPostExecute(expenseListBean);
            if (expenseListView != null) {
                expenseListView.displayExpenseList(expenseListBean);
                expenseListView.hideProgressBar();
            }
        }
    }
    private void getSampleDisbursementList() {
        try {
            expenseListBeansList.addAll(OfflineManager.getExpenseListFromDataValt(context));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
}

