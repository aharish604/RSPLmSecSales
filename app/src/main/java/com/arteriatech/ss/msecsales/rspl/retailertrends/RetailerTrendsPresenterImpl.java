package com.arteriatech.ss.msecsales.rspl.retailertrends;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyPerformanceBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RetailerTrendsPresenterImpl implements RetailerTrendsPresenter, UIListener {

    private String mStrBundleCPGUID;
    Context context;
    private ArrayList<MyPerformanceBean> alMyStock = new ArrayList<>();
    private ArrayList<MyPerformanceBean> alStockList = new ArrayList<>();
    RetailerTrendView retailerTrendView;
    ArrayList<String> alAssignColl = null;
    private boolean isErrorFromBackend = false;
    Map<String, MyPerformanceBean> mapTrends = new HashMap<>();
    Activity activity;
    private GUID refguid =null;
    public RetailerTrendsPresenterImpl(Context context, Activity activity, String mStrBundleCPGUID) {
        this.context = context;
        if (context instanceof RetailerTrendView) {
            retailerTrendView = (RetailerTrendView) context;
        }
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.activity = activity;
    }
    @Override
    public void start() {
        getRetailerTrendList();
    }
    @Override
    public void refresh() {
        onRefreshSOrder();
    }
    @Override
    public void destroy() {
        retailerTrendView = null;

    }
    @SuppressLint("StaticFieldLeak")
    private void getRetailerTrendList() {
        final String mStrMyStockQry = Constants.Performances + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID.toUpperCase() + "' " +
                "and " + Constants.PerformanceTypeID + " eq '000002' ";
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (retailerTrendView != null) {
                    retailerTrendView.showProgress();
                }
            }
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    alMyStock =new ArrayList<>();
                    alStockList =new ArrayList<>();
                    alMyStock = OfflineManager.getMyPerfomnceList(mStrMyStockQry);
                    mapTrends = getALMyTargetList(alMyStock);
                    alStockList = getValuesFromMap(mapTrends);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

               /* if (alMyStock != null && alMyStock.size() > 0) {
                    Collections.sort(alMyStock, new Comparator<MyPerformanceBean>() {
                        public int compare(MyPerformanceBean one, MyPerformanceBean other) {
                            BigInteger i1 = null;
                            BigInteger i2 = null;
                            try {
                                i1 = new BigInteger(one.getAmtMTD());
                            } catch (NumberFormatException e) {
                            }
                            try {
                                i2 = new BigInteger(other.getAmtMTD());
                            } catch (NumberFormatException e) {
                            }
                            if (i1 != null && i2 != null) {
                                return i1.compareTo(i2);
                            } else {
                                return one.getAmtMTD().compareTo(other.getAmtMTD());
                            }
                        }
                    });
                }*/
                if (retailerTrendView != null) {
                    retailerTrendView.displayList(alStockList);
                    retailerTrendView.displayLstSyncTime(SyncUtils.getCollectionSyncTime(context, Constants.Performances));

                }
                if (retailerTrendView != null) {
                    retailerTrendView.hideProgress();
                }
            }
        }.execute();

    }
    private void onRefreshSOrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.add(Constants.Performances);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                if (retailerTrendView != null) {
                    retailerTrendView.hideProgress();
                    retailerTrendView.displayMsg(context.getString(R.string.alert_auto_sync_is_progress));
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
            if (retailerTrendView != null) {
                retailerTrendView.hideProgress();
                retailerTrendView.displayMsg(context.getString(R.string.no_network_conn));
            }
        }
    }
    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
                Constants.isSync = false;
                if (retailerTrendView != null) {
                    retailerTrendView.hideProgress();
                    retailerTrendView.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (retailerTrendView != null) {
                    retailerTrendView.hideProgress();
                    retailerTrendView.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (retailerTrendView != null) {
                    retailerTrendView.showProgress();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (retailerTrendView != null) {
                    retailerTrendView.hideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (retailerTrendView != null) {
                retailerTrendView.hideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }
    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.DownLoad,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (retailerTrendView != null) {
                retailerTrendView.hideProgress();
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
            if (retailerTrendView != null) {
                retailerTrendView.hideProgress();
                start();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }

    }

    private Map<String, MyPerformanceBean> getALMyTargetList(ArrayList<MyPerformanceBean> alMyTargets) {
        Map<String, MyPerformanceBean> mapMyTargetBean = new HashMap<>();
        if (alMyTargets != null && alMyTargets.size() > 0) {
            for (MyPerformanceBean bean : alMyTargets)
                if (mapMyTargetBean.containsKey(bean.getMaterialGroupID())) {

                    double AmtMonth1PrevPerf = Double.parseDouble(bean.getAmtMonth1PrevPerf()) + Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getAmtMonth1PrevPerf());
                    double AmtMonth2PrevPerf = Double.parseDouble(bean.getAmtMonth2PrevPerf())+ Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getAmtMonth2PrevPerf());
                    double AmtMonth3PrevPerf = Double.parseDouble(bean.getAmtMonth3PrevPerf())+ Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getAmtMonth3PrevPerf());
                    double mDoubAmtLMTD = Double.parseDouble(bean.getAmtLMTD())+ Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getAmtLMTD());
                    double mDoubAmtMTD = Double.parseDouble(bean.getAmtMTD())+ Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getAmtMTD());
                    double mDoubGRPER = Double.parseDouble(bean.getGrPer())+ Double.parseDouble(mapMyTargetBean.get(bean.getMaterialGroupID()).getGrPer());

                    bean.setAmtMonth1PrevPerf(AmtMonth1PrevPerf+"");
                    bean.setAmtMonth2PrevPerf(AmtMonth2PrevPerf+"");
                    bean.setAmtMonth3PrevPerf(AmtMonth3PrevPerf+"");
                    bean.setAmtLMTD(mDoubAmtLMTD+"");
                    bean.setAmtMTD(mDoubAmtMTD+"");
                    bean.setGrPer(mDoubGRPER+"");

                    mapMyTargetBean.put(bean.getMaterialGroupID(), bean);
                } else {

                    mapMyTargetBean.put(bean.getMaterialGroupID(), bean);
                }
        }


        return mapMyTargetBean;
    }

    private ArrayList<MyPerformanceBean> getValuesFromMap(Map<String, MyPerformanceBean> mapMyTargetVal) {
        ArrayList<MyPerformanceBean> alTargets = new ArrayList<>();
        if (!mapMyTargetVal.isEmpty()) {
            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MyPerformanceBean myPerformanceBean = mapMyTargetVal.get(key);
                Double mAmtOne  = 0.00,mdoubSumOf =0.00,mAmtTwo=0.00,mAmtThree=0.00;
                try {
                    mAmtOne = Double.parseDouble(myPerformanceBean.getAmtMonth1PrevPerf());
                    mAmtOne = ConstantsUtils.decimalRoundOff(new BigDecimal(mAmtOne), 3).doubleValue();
                } catch (NumberFormatException e) {
                    mAmtOne = 0.00;
                    e.printStackTrace();
                }
                try {
                    mAmtTwo = Double.parseDouble(myPerformanceBean.getAmtMonth2PrevPerf());
                    mAmtTwo = ConstantsUtils.decimalRoundOff(new BigDecimal(mAmtTwo), 3).doubleValue();
                } catch (NumberFormatException e) {
                    mAmtTwo =0.00;
                    e.printStackTrace();
                }

                try {
                    mAmtThree = Double.parseDouble(myPerformanceBean.getAmtMonth3PrevPerf());
                    mAmtThree = ConstantsUtils.decimalRoundOff(new BigDecimal(mAmtThree), 3).doubleValue();
                } catch (NumberFormatException e) {
                    mAmtThree =0.00;
                    e.printStackTrace();
                }

                Double avgLastThreeMont = 0.0;
                try {
                    avgLastThreeMont = (mAmtOne+mAmtTwo+mAmtThree) / 3;
                } catch (Exception e) {
                    avgLastThreeMont = 0.0;
                }

                if (Double.isNaN(avgLastThreeMont) || Double.isInfinite(avgLastThreeMont)) {
                    avgLastThreeMont = 0.0;
                }

                avgLastThreeMont = ConstantsUtils.decimalRoundOff(new BigDecimal(avgLastThreeMont), 3).doubleValue();

                myPerformanceBean.setAvgLstThreeMonth(avgLastThreeMont.toString());

                alTargets.add(myPerformanceBean);
            }
        }
        Collections.sort(alTargets, new Comparator<MyPerformanceBean>() {
            public int compare(MyPerformanceBean one, MyPerformanceBean other) {
                BigInteger i1 = null;
                BigInteger i2 = null;
                try {
                    i1 = new BigInteger(one.getMaterialGroupDesc());
                } catch (NumberFormatException e) {
                }

                try {
                    i2 = new BigInteger(other.getMaterialGroupDesc());
                } catch (NumberFormatException e) {
                }
                if (i1 != null && i2 != null) {
                    return i1.compareTo(i2);
                } else {
                    return one.getMaterialGroupDesc().compareTo(other.getMaterialGroupDesc());
                }
            }
        });
        return alTargets;
    }

}
