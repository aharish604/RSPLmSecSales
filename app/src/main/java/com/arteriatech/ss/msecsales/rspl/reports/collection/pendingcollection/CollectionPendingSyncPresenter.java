package com.arteriatech.ss.msecsales.rspl.reports.collection.pendingcollection;

import android.app.Activity;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.reports.collection.header.CollectionListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;


/**
 * Created by e10847 on 07-12-2017.
 */

public class CollectionPendingSyncPresenter implements ICollectionPendingSyncPresenter, UIListener, MessageWithBooleanCallBack {
    private Activity context = null;
    private String CPGUID = "";
    private ArrayList<CollectionHistoryBean> collHeaderArrayList = new ArrayList<>();
    private ICollectionPendingSyncView collView = null;
    private ArrayList<String> pendingCollectionList = null;
    private boolean isErrorFromBackend = false;
    private int penROReqCount = 0;
    private int pendingROVal = 0;
    private int isFromWhere = 0;
    private String[] tempRODevList = null;
    private boolean dialogCancelled = false;
    private String concatCollectionStr = "";
    private GUID refguid =null;

    public CollectionPendingSyncPresenter(Activity context, String CPGUID, ICollectionPendingSyncView salesOrderPendingSyncView) {
        this.context = context;
        this.CPGUID = CPGUID;
        this.collView = salesOrderPendingSyncView;
        this.collHeaderArrayList = new ArrayList<>();
        this.pendingCollectionList = new ArrayList<>();
    }

    @Override
    public void connectToOfflineDB(ICollectionPendingSyncView.SalesOrderResponse<CollectionHistoryBean> salesOrderResponse) {
        GetCollListAsyncTask salesOrderAsyncTask = new GetCollListAsyncTask(salesOrderResponse);
        salesOrderAsyncTask.execute();
    }

    @Override
    public void onSync() {
        onSyncSOrder();
    }

    @Override
    public void getDetails(final CollectionHistoryBean collectionHistoryBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<CollectionHistoryBean> alCollHisDetails = new ArrayList<>();
                try {
                    alCollHisDetails.addAll(OfflineManager.getCollDetailsListFromDataValt(context, collectionHistoryBean.getDeviceNo()));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                final ArrayList<CollectionHistoryBean> finalAlCollHisDetails = alCollHisDetails;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (collView != null) {
                            collectionHistoryBean.setAlCollItemList(finalAlCollHisDetails);
                            collView.openCollDetail(collectionHistoryBean);

                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        collView = null;
    }

    @Override
    public void onRequestError(int i, Exception e) {
        onError(i, e);
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        onSuccess(i, s);
    }

    /**
     * get SOs Pending Sync List from DataVault
     */
    private void getCollList() {
        try {
            collHeaderArrayList.addAll(OfflineManager.getDevCollHisList(context, CPGUID));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSyncSOrder() {
        isErrorFromBackend = false;
        isFromWhere = 4;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        try {
            collHeaderArrayList.clear();
            collHeaderArrayList = (ArrayList<CollectionHistoryBean>) OfflineManager.getDevCollHisList(context, CPGUID);
            if (!collHeaderArrayList.isEmpty()) {
                    if (UtilConstants.isNetworkAvailable(context)) {
                        pendingCollectionList.clear();
                        pendingCollectionList.addAll(SyncUtils.getFIPCollection());
                        pendingCollectionList.addAll(SyncUtils.getInvoice());
                        pendingCollectionList.add(Constants.ConfigTypsetTypeValues);
                        pendingROVal = 0;
                        if (tempRODevList != null) {
                            tempRODevList = null;
                            penROReqCount = 0;
                        }

                        if (collHeaderArrayList != null && collHeaderArrayList.size() > 0) {
                            tempRODevList = new String[collHeaderArrayList.size()];

                            for (CollectionHistoryBean SalesOrderBean : collHeaderArrayList) {
                                tempRODevList[pendingROVal] = SalesOrderBean.getDeviceNo();
                                pendingROVal++;
                            }
                            if (Constants.iSAutoSync) {
                                if (collView != null) {
                                    collView.hideProgressDialog();
                                    collView.showMessaage(context.getString(R.string.alert_auto_sync_is_progress));
                                }
                            } else {
                                if (collView != null) {
                                    collView.showProgressDialog();
                                }
                                Constants.isSync = true;
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(context,Constants.CollectionPostPD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                                new SyncFromDataValtAsyncTask(context, tempRODevList, this, this).execute();
                            }
                        }
                    } else {
                        if (collView != null) {
                            collView.hideProgressDialog();
                            collView.showMessaage(context.getString(R.string.no_network_conn));
                        }
                    }
                } else {
                if (collView != null) {
                    collView.hideProgressDialog();
                    collView.showMessaage(context.getString(R.string.no_req_to_update_sap));
                }
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            if (collView != null) {
                collView.hideProgressDialog();
                collView.showMessaage(e.getMessage());
            }
        }
    }

    public void onError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    if (collView != null) {
                        collView.hideProgressDialog();
                        collView.onReloadData();
                        collView.showMessaage(context.getString(R.string.msg_error_occured_during_sync));
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
                    if (collView != null) {
                        collView.hideProgressDialog();
                        collView.onReloadData();
                        collView.showMessaage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (collView != null) {
                        collView.hideProgressDialog();
                        collView.showMessaage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (collView != null) {
                    collView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (collView != null) {
                    collView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.mBoolIsReqResAval = true;
            Constants.isSync = false;
            if (collView != null) {
                collView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    public void onSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
            Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.CollectionPostPD_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (collView != null) {
                collView.hideProgressDialog();
                collView.onReloadData();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
        if (isFromWhere == 4) {
            if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                Constants.mBoolIsReqResAval = true;
                Constants.removeDataValtFromSharedPref(context, Constants.FinancialPostings, tempRODevList[penROReqCount], false);
                try {
                    ConstantsUtils.storeInDataVault(tempRODevList[penROReqCount], "",context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                penROReqCount++;
            }
            if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(pendingCollectionList);
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, pendingCollectionList,Constants.CollectionPostPD_sync,refguid.toString().toUpperCase());
                String msg = "";
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                if (collView != null) {
                    collView.hideProgressDialog();
                    if (!isErrorFromBackend) {
                        CollectionListActivity.mBoolRefreshDone = true;
                        collView.showMessaage(context.getString(R.string.msg_sync_successfully_completed));
                        collView.onReloadData();
                    } else {
                        collView.showMessaage(context.getString(R.string.msg_error_occured_during_sync));
                        CollectionListActivity.mBoolRefreshDone = true;
                        collView.onReloadData();
                    }
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
                Constants.isSync = false;
                Constants.mBoolIsReqResAval = true;
                try {
                    OfflineManager.getAuthorizations(context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(context,Constants.Sync_All);
                ConstantsUtils.startAutoSync(context, false);
                if (collView != null) {
                    collView.onReloadData();
                    collView.hideProgressDialog();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
            }
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String err_msg, ErrorBean errorBean) {
        if (!clickedStatus) {
            if (collView != null) {
                collView.hideProgressDialog();
                collView.showMessaage(err_msg);
            }
        }
    }

    /**
     * Get SOs on Background Thread
     */
    public class GetCollListAsyncTask extends AsyncTask<Void, Void, ArrayList<CollectionHistoryBean>> {
        ICollectionPendingSyncView.SalesOrderResponse<CollectionHistoryBean> salesOrderResponse;

        public GetCollListAsyncTask(ICollectionPendingSyncView.SalesOrderResponse<CollectionHistoryBean> salesOrderResponse) {
            this.salesOrderResponse = salesOrderResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (collView != null) {
                collView.showProgressDialog();
            }
        }

        @Override
        protected ArrayList<CollectionHistoryBean> doInBackground(Void... params) {
            collHeaderArrayList.clear();
            getCollList();
            return collHeaderArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<CollectionHistoryBean> salesOrderBeenArrayList) {
            super.onPostExecute(salesOrderBeenArrayList);
            if (collView != null) {
                salesOrderResponse.success(salesOrderBeenArrayList);
                collView.hideProgressDialog();
            }
        }
    }
}
