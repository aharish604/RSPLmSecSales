package com.arteriatech.ss.msecsales.rspl.visitsummary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
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
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.VisitSummaryBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by e10893 on 25-01-2018.
 */

public class VisitSummaryPresenterImpl implements VisitSummaryPresenter, UIListener {

    ArrayList<String> alAssignColl = new ArrayList<>();
    DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    MyTargetsBean salesKpi = null, tlsdKPI = null;
    private Context context;
    private VisitSummaryViewPresenter iVisSummViewPresenter = null;
    private Activity activity;
    private ArrayList<VisitSummaryBean> alVisitSummary;
    private ArrayList<VisitSummaryBean> searchTargetAL;
    private String searchText = "";
    private String startDate = "";
    private String endDate = "";
    private String filterType = "";
    private String delvStatusId = "";
    private String statusId = "";
    private String statusName = "";
    private String delvStatusName = "";
    String mStrVisitStartTime = "",mStrVisitEndTime="";
    private GUID refguid =null;

    public VisitSummaryPresenterImpl(Context context, VisitSummaryViewPresenter iTargetViewPresenter, Activity activity) {
        this.context = context;
        this.iVisSummViewPresenter = iTargetViewPresenter;
        this.alVisitSummary = new ArrayList<>();
        this.searchTargetAL = new ArrayList<>();
        this.activity = activity;
    }


    @Override
    public void onFilter() {

        if (iVisSummViewPresenter != null) {
            iVisSummViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
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
    public void onStart() {
        getDataFromOffline();
    }

    @Override
    public void onDestoy() {
        iVisSummViewPresenter=null;
    }

    public void getDataFromOffline() {
        if (iVisSummViewPresenter != null) {
            iVisSummViewPresenter.showProgressDialog();

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();

                Collections.sort(alVisitSummary, new Comparator<VisitSummaryBean>() {
                    @Override
                    public int compare(VisitSummaryBean o1, VisitSummaryBean o2) {
                        return o1.getRetailerName().compareTo(o2.getRetailerName());
                    }
                });

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iVisSummViewPresenter != null) {
                            iVisSummViewPresenter.hideProgressDialog();
                            iVisSummViewPresenter.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.Targets));
                            iVisSummViewPresenter.displayList(alVisitSummary,mStrVisitStartTime,mStrVisitEndTime);
                        }
                    }
                });
            }
        }).start();


    }

    private void getDataFromDB() {

        alVisitSummary = new ArrayList<>();

        dmsDivQryBean = Constants.getDMSDIV("");
        getSystemKPI();
        ArrayList<RetailerBean> alVisitedRet = null;
        alVisitedRet = Constants.getVisitedRetFromVisit();
        getVisitStartEndTime();
        getVisitSummaryValues(alVisitedRet);

    }

    private void getVisitStartEndTime(){

        try {

            ArrayList<String> alVisittimes =  OfflineManager.getVisitTime(Constants.Visits+"?$filter = "
                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and "+Constants.ENDDATE+" ne null and "
                    +Constants.SPGUID+" eq guid'"+Constants.getSPGUID()+"' and "+
                    "("+Constants.VisitCatID+" eq '01' or "+Constants.VisitCatID+" eq '02')",Constants.EndTime,Constants.StartTime);
            if(alVisittimes!=null && alVisittimes.size()>1){
                try {
                    mStrVisitStartTime = alVisittimes.get(0);
                    mStrVisitEndTime = alVisittimes.get(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                mStrVisitStartTime = "00:00";
                mStrVisitEndTime = "00:00";
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchTargetAL.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (alVisitSummary != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchTargetAL.addAll(alVisitSummary);
            } else {
                for (VisitSummaryBean item : alVisitSummary) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getRetailerName().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        searchTargetAL.add(item);
                }
            }
        }
        if (iVisSummViewPresenter != null) {
            iVisSummViewPresenter.searchResult(searchTargetAL);
        }
    }

    @Override
    public void onRefresh() {
        alAssignColl.clear();
        alAssignColl.add(Constants.Targets);
        alAssignColl.add(Constants.TargetItems);
        alAssignColl.add(Constants.KPISet);
        alAssignColl.add(Constants.KPIItems);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        getTargets(alAssignColl);
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
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
            if (iVisSummViewPresenter != null) {
                iVisSummViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (errorBean.hasNoError()) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.VisitMTD_sync,refguid.toString().toUpperCase());
                Constants.isSync = false;
                if (!Constants.isStoreClosed) {
                    if (iVisSummViewPresenter != null) {
                        iVisSummViewPresenter.hideProgressDialog();
                        iVisSummViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }

                } else {
                    if (iVisSummViewPresenter != null) {
                        iVisSummViewPresenter.hideProgressDialog();
                        iVisSummViewPresenter.showMessage(context.getString(R.string.msg_sync_terminated));
                    }
                }
            } else if (i == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (iVisSummViewPresenter != null) {
                    iVisSummViewPresenter.hideProgressDialog();
                    iVisSummViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iVisSummViewPresenter != null) {
                    iVisSummViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iVisSummViewPresenter != null) {
                    iVisSummViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
           /* if (progressDialog != null) {
                Constants.hideProgressDialog(progressDialog);
            }*/
            if (iVisSummViewPresenter != null) {
                iVisSummViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.VisitMTD_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (iVisSummViewPresenter != null) {
                iVisSummViewPresenter.hideProgressDialog();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                getDataFromOffline();
            }
        } else if (i == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(context, false);
            if (iVisSummViewPresenter != null) {
                iVisSummViewPresenter.hideProgressDialog();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                getDataFromOffline();
            }
        }

    }

    /**
     * sync Targets
     *
     * @param collectionName
     */
    private void getTargets(@NonNull ArrayList<String> collectionName) {
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(collectionName);
            if (Constants.iSAutoSync) {
                if (iVisSummViewPresenter != null) {
                    iVisSummViewPresenter.hideProgressDialog();
                    iVisSummViewPresenter.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.VisitMTD_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (iVisSummViewPresenter != null) {
                iVisSummViewPresenter.hideProgressDialog();
                iVisSummViewPresenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    private void getSystemKPI() {
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ", dmsDivQryBean.getCVGValueQry());


            tlsdKPI = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' and " + Constants.CalculationBase + " eq '04'", dmsDivQryBean.getCVGValueQry());

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void getVisitSummaryValues(ArrayList<RetailerBean> alTodayRetailers) {
        try {
            if (alTodayRetailers != null && alTodayRetailers.size() > 0) {
                alVisitSummary = OfflineManager.getVisitSummaryVal(context, alTodayRetailers,
                        salesKpi, tlsdKPI, Constants.getSOOrderType(), dmsDivQryBean.getDMSDivisionSSInvQry());
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }
}
