package com.arteriatech.ss.msecsales.rspl.expenselist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

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
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplaintListModel;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class ExpenseListHistoryPresenterImpl implements  ExpenseListHistoryPresenter, UIListener{
    Context context;
    ArrayList<ExpenseListBean>expenseListBeansList=new ArrayList<>();
    ArrayList<ExpenseListBean> expenseListSearchList = new ArrayList<>();
    boolean soSearchStatus = false;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private int mError = 0;
    Activity activity;
    private boolean isErrorFromBackend = false;
    ExpenseListView expenseListView;
    private GUID refguid =null;
    ExpenseListHistoryPresenterImpl(Context context,ExpenseListView expenseListView,Activity activity)
    {
        this.context=context;
        if(expenseListView instanceof ExpenseListView)
        {
            this.expenseListView=expenseListView;
            this.activity=activity;

        }
    }
    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(expenseListView!=null)
                        {
                            expenseListView.loadProgressBar();
                        }
                    }
                });
                try {
                    expenseListBeansList.clear();
                    String qry = Constants.Expenses;
                    expenseListBeansList.addAll(OfflineManager.getExpenseList(qry,context));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(expenseListView!=null)
                        {
                            expenseListView.displayExpenseList(expenseListBeansList);
                            expenseListView.syncSucces();
                            expenseListView.hideProgressBar();

                        }
                    }
                });
            }
        }).start();
    }
    @Override
    public void onSearch(String searchText) {
        expenseListSearchList.clear();
        if (expenseListBeansList != null) {
            if (TextUtils.isEmpty(searchText)) {
                expenseListSearchList.addAll(expenseListBeansList);
            } else {
                for (ExpenseListBean item : expenseListBeansList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getExpenseNo().contains(searchText);
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        expenseListSearchList.add(item);
                }
            }
        }
        if (expenseListView != null) {
            expenseListView.displayExpenseSearchList(expenseListSearchList);
        }
    }
    @Override
    public void refreshExpenseList() {
        if (expenseListView != null) {
        }
        mError = 0;
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getExpenseListCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (expenseListView != null) {
                    expenseListView.hideProgressBar();
                    expenseListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                expenseListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }
    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                    Constants.isSync = false;
                    if (i == Operation.OfflineRefresh.getValue()) {
                        LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
                        if (expenseListView != null) {
                            expenseListView.hideProgressBar();
                            expenseListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }


                    }else if (i == Operation.GetStoreOpen.getValue()) {
                        Constants.isSync = false;
                        if (expenseListView != null) {
                            expenseListView.hideProgressBar();
                            expenseListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }
                    }
            }
        } else if (errorBean.isStoreFailed()) {
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
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            Constants.displayMsgReqError(errorBean.getErrorCode(), context);
        }
    }
    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                start();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(context, false);
            if (expenseListView != null) {
                expenseListView.hideProgressBar();
                start();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }

    }
}


