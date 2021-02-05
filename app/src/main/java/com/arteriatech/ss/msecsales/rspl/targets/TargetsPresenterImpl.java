package com.arteriatech.ss.msecsales.rspl.targets;

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
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by e10893 on 25-01-2018.
 */

public class TargetsPresenterImpl implements TargetPresenter, UIListener {

    ArrayList<String> alAssignColl = null;
    Map<String, Double> mapMonthAchived = new HashMap<>();
    Map<String, Double> mapMonthTarget = new HashMap<>();
    private Context context;
    private TargetViewPresenter iTargetViewPresenter = null;
    private Activity activity;
    private ArrayList<MyTargetsBean> alTargets;
    private ArrayList<MyTargetsBean> searchTargetAL;
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
    private ArrayList<MyTargetsBean> alKpiList = null;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private Map<String, MyTargetsBean> mapMyTargetVal = new HashMap<>();
    private GUID refguid =null;

    public TargetsPresenterImpl(Context context, TargetViewPresenter iTargetViewPresenter, Activity activity) {
        this.context = context;
        this.iTargetViewPresenter = iTargetViewPresenter;
        this.alTargets = new ArrayList<>();
        this.searchTargetAL = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
    }


    @Override
    public void onFilter() {

        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
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

    public void getDataFromOffline() {
       /* if (iTargetViewPresenter != null) {
            iTargetViewPresenter.showProgressDialog();

        }
        getDataFromDB();

        iTargetViewPresenter.hideProgressDialog();

        iTargetViewPresenter.displayList(alTargets);*/


        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.showProgressDialog();

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iTargetViewPresenter != null) {
                            iTargetViewPresenter.hideProgressDialog();

                            iTargetViewPresenter.displayList(alTargets);
                        }
                    }
                });
            }
        }).start();
    }

    private void getDataFromDB() {
        alTargets = new ArrayList<>();
        String mStrTotalOrderVal = Constants.getTotalOrderValueByCurrentMonth(Constants.getFirstDateOfCurrentMonth(), "", "");
        getSystemKPI();
        getMyTargetsList();

        alTargets = getValuesFromMap(mapMyTargetVal, mStrTotalOrderVal);
    }

    private void onSearchQuery(String searchText) {
        this.searchText = searchText;
        searchTargetAL.clear();
        boolean soTypeStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (alTargets != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchTargetAL.addAll(alTargets);
            } else {
                for (MyTargetsBean item : alTargets) {
                    soTypeStatus = false;
                    soDelStatus = false;
                    soSearchStatus = false;

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getKPIName().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus)
                        searchTargetAL.add(item);
                }
            }
        }
        if (iTargetViewPresenter != null) {
            iTargetViewPresenter.searchResult(searchTargetAL);
        }
    }

    @Override
    public void onRefresh() {
        alAssignColl = new ArrayList<>();
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


    private ArrayList<MyTargetsBean> getValuesFromMap(Map<String, MyTargetsBean> mapMyTargetVal, String totalOrderVal) {
        ArrayList<MyTargetsBean> alTargets = new ArrayList<>();
        if (!mapMyTargetVal.isEmpty()) {
            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                myTargetsBean.setMonthTarget(mapMonthTarget.get(key).toString());

                Double morderVal = 0.0;
                double btdVal = 0;
                double achivedPer = 0;
                try {
                    if (myTargetsBean.getKPIName().contains("Sales")) {
                        morderVal = Double.parseDouble(totalOrderVal);
                    }
                    myTargetsBean.setMTDA((mapMonthAchived.get(key) + morderVal) + "");
                    btdVal = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), (mapMonthAchived.get(key) + morderVal) + "");
                    achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), (mapMonthAchived.get(key) + morderVal) + "");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                myTargetsBean.setBTD(btdVal + "");
                myTargetsBean.setAchivedPercentage(achivedPer + "");

                alTargets.add(mapMyTargetVal.get(key));
            }
        }
        return alTargets;
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI() {
        try {
            String mStrMyStockQry;
            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "'  ";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry, "");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
//            alMyTargets.clear();
            if (alKpiList != null && alKpiList.size() > 0) {
                alMyTargets = OfflineManager.getMyTargets(alKpiList, Constants.getSPGUID(Constants.SPGUID));
            }
          /*  mapMyTargetVal.clear();
            mapMonthAchived.clear();
            mapMonthTarget.clear();*/
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /**
     * sum of actual and target quantity/Value based on kpi code and assign to map table
     *
     * @param alMyTargets
     * @return
     */
    private Map<String, MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets) {
        Map<String, MyTargetsBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyTargetsBean bean : alMyTargets)
                if (mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) + mapMonthTarget.get(bean.getKPICode());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getKPICode());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                } else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA());
                    double mDoubAchivedPer = Double.parseDouble(bean.getAchivedPercentage());
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(), bean);
                }
        }


        return mapMyTargetBean;
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
            if (iTargetViewPresenter != null) {
                iTargetViewPresenter.setFilterDate(statusDesc);
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

                    Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
                    Constants.isSync = false;
                    if (iTargetViewPresenter != null) {
                        iTargetViewPresenter.hideProgressDialog();
                        iTargetViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                } else if (i == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (iTargetViewPresenter != null) {
                        iTargetViewPresenter.hideProgressDialog();
                        iTargetViewPresenter.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
           /* if (progressDialog != null) {
                Constants.hideProgressDialog(progressDialog);
            }*/
            if (iTargetViewPresenter != null) {
                iTargetViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (!Constants.isStoreClosed) {
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
                Constants.isSync = false;
                ConstantsUtils.startAutoSync(context, false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iTargetViewPresenter != null) {
                            getDataFromDB();
                            iTargetViewPresenter.hideProgressDialog();
                            iTargetViewPresenter.displayList(alTargets);
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
                ConstantsUtils.startAutoSync(context, false);
                if (iTargetViewPresenter != null) {
                    iTargetViewPresenter.hideProgressDialog();
                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                }
                onStart();
            }

        }
    }

    /**
     * sync Targets
     *
     * @param collectionName
     */
    private void getTargets(@NonNull ArrayList<String> collectionName) {
//        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
//            alAssignColl.clear();
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(collectionName);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(context.getString(R.string.alert_auto_sync_is_progress), context);
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
            UtilConstants.showAlert(context.getString(R.string.no_network_conn), context);
        }
    }
}
