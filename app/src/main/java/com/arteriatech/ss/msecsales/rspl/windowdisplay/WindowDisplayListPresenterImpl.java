package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

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
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class WindowDisplayListPresenterImpl implements WindowDisplayListPresenter, AsyncTaskCallBack, UIListener {

    Context context;
    Activity activtiy;
    String mStrBundleCPGUID;
    String mStrBundleCPGUID32;
    WindowDisplayListView windowDisplayListView;
    ArrayList<String> alAssignColl;
    private String[][] arrWinDispType = null;
    private int numberOfDays = 0;
    private String[][] mArrayDistributors = null;
    private ArrayList<SchemeBean> schemeModelArrayList = new ArrayList<>();
    private ProgressDialog prgressDialog = null;
    private boolean isErrorFromBackend = false;
    public WindowDisplayListPresenterImpl(Context context, Activity activity, String mStrBundleCPGUID, String mStrBundleCPGUID32) {
        this.context = context;
        this.activtiy = activity;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrBundleCPGUID32 = mStrBundleCPGUID32;
        if (context instanceof WindowDisplayListView) {
            windowDisplayListView = (WindowDisplayListView) context;
        }
    }
    @Override
    public void getWindowDispType() {
        String id = "";
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.RegSchemeCat + "'";
            arrWinDispType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        String qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                Constants.SC + "' and " + Constants.Types + " eq '" + Constants.WDSPINVDTR + "' ";
        try {
            String mStrDaysBefore = OfflineManager.getConfigValue(qryStr);
            numberOfDays = Integer.parseInt(mStrDaysBefore);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID,context);
    }
    @Override
    public void getDataFromOfflineDB() {
        try
        {
            schemeModelArrayList.clear();
            if (windowDisplayListView != null) {
                windowDisplayListView.showProgress();
            }
            DmsDivQryBean dmsDivQryBean = Constants.getDMSDIV("");
            schemeModelArrayList = OfflineManager.getSchemeWindowDisplay(arrWinDispType[0][1], mStrBundleCPGUID32.toUpperCase(),
                    mArrayDistributors[4][0], mArrayDistributors[5][0], mArrayDistributors[8][0], schemeModelArrayList, dmsDivQryBean.getDMSDivisionQry(), dmsDivQryBean.getDMSDivisionIDQry());

            //schemeModelArrayList = OfflineManager.getSchemeWindowDisplay("000002", mStrBundleCPGUID32.toUpperCase(),
            //     mArrayDistributors[4][0], mArrayDistributors[5][0], mArrayDistributors[8][0], schemeModelArrayList,dmsDivQryBean.getDMSDivisionQry(),dmsDivQryBean.getDMSDivisionIDQry());

            windowDisplayListView.displayList(schemeModelArrayList);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + " window display no data from RegSchemeCat " + e.getMessage());
        }
        if (windowDisplayListView != null) {
            windowDisplayListView.hideProgress();
        }
    }
    @Override
    public void validateWindowDisplay(String schemeGUid, SchemeBean schemeBean) {
        prgressDialog = Constants.showProgressDialog(context, "", "Please wait...");
        new ValidateWindowDisplayAsyncTask(this, schemeGUid, numberOfDays, mStrBundleCPGUID).execute();

    }
    @Override
    public void refreshList() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getSchemes());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (windowDisplayListView != null) {
                    windowDisplayListView.hideProgress();
                    windowDisplayListView.displayMsg(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (windowDisplayListView != null) {
                windowDisplayListView.hideProgress();
                windowDisplayListView.displayMsg(context.getString(R.string.no_network_conn));
            }
        }
    }
    @Override
    public void onStatus(boolean status, String values) {
        if (prgressDialog != null) {
            Constants.hideProgressDialog(prgressDialog);
        }
        if (windowDisplayListView != null) {
            windowDisplayListView.navToWindowDisplay(status, values);
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (!Constants.isStoreClosed) {
                if (i == Operation.OfflineRefresh.getValue()) {

                    Constants.isSync = false;
                    if (windowDisplayListView != null) {
                        windowDisplayListView.hideProgress();
                            windowDisplayListView.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }else if (i == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (windowDisplayListView != null) {
                        windowDisplayListView.hideProgress();
                        windowDisplayListView.displayMsg(context.getString(R.string.msg_offline_store_failure));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (windowDisplayListView != null) {
                    windowDisplayListView.showProgress();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (windowDisplayListView != null) {
                    windowDisplayListView.hideProgress();
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
          //  Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad);
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (windowDisplayListView != null) {
                windowDisplayListView.hideProgress();
                connectToOfflineDBRefresh();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activtiy, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
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
            if (windowDisplayListView != null) {
                windowDisplayListView.hideProgress();
                connectToOfflineDBRefresh();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activtiy, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }

    }
    public void connectToOfflineDBRefresh() {

        if (windowDisplayListView != null) {
            windowDisplayListView.showProgress();
        }
        try {
            getWindowDispType();
            getDataFromOfflineDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (windowDisplayListView != null) {
            windowDisplayListView.hideProgress();
        }

    }
}
