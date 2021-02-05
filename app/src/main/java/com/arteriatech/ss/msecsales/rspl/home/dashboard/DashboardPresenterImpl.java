package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import android.app.Activity;
import android.content.Context;
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
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.retailerapproval.RetailerApprovalListActivity;
import com.arteriatech.ss.msecsales.rspl.store.GetOnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.store.OpenOnlineManagerStore;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10769 on 13-Apr-18.
 */

public class DashboardPresenterImpl implements DashboardPresenter, UIListener, GetOnlineODataInterface {

    ArrayList<String> alAssignColl = null;
    Map<String, Double> mapMonthAchived = new HashMap<>();
    Map<String, Double> mapMonthTarget = new HashMap<>();
    HashSet<String> kpiNames = new HashSet<>();
    DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    private Context context;
    private DashboardView iDaySummViewPresenter = null;
    private Activity activity;
    private ArrayList<MyTargetsBean> alTargets = new ArrayList<>();
    private ArrayList<VisitedBeatBean> alVisitBeat = new ArrayList<>();
    private ArrayList<BrandProductiveBean> alBrandDeatils = new ArrayList<>();
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
    private ArrayList<MyTargetsBean> alKpiList = new ArrayList<>();
    private ArrayList<MyTargetsBean> alMyTargets = new ArrayList<>();
    private Map<String, MyTargetsBean> mapMyTargetVal = new HashMap<>();
    private int mtpCount = 0, soCount = 0, contractApprovalCount = 0, creditlimitCount = 0;
    private List<SalesOrderBean> salesOrderHeaderArrayList;

    private int totalRequest = 0;
    private int currentRequest = 0;
    private String totalRETCount = "";
    public static boolean isReloadRETApproval = false;
    public static boolean isRefreshedAttendance = false;
    private final SharedPreferences sharedPreferences;
    private GUID refguid =null;

    public DashboardPresenterImpl(Context context, DashboardView iDaySummViewPresenter, Activity activity) {
        this.context = context;
        this.iDaySummViewPresenter = iDaySummViewPresenter;
        this.alTargets = new ArrayList<>();
        this.searchTargetAL = new ArrayList<>();
        this.headerTable = new Hashtable<>();
        this.activity = activity;
        this.salesOrderHeaderArrayList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    @Override
    public void onStart() {
        // if (!entities.isEmpty()) {
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showProgressDialog();
            iDaySummViewPresenter.showProgress();
        }
        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode) && !isRefreshedAttendance) {
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.showAttendancePB();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String mStrSPGUID = Constants.getSPGUID();
                    if (!TextUtils.isEmpty(mStrSPGUID)) {
                        String prvDayQry = Constants.Attendances + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                        if(Constants.writeDebug){
                            LogManager.writeLogDebug("Dahsboard refresh : Attendances URL : "+prvDayQry);
                        }
                        String mStrAttendanceId = "";
                        try {
                            mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(mStrAttendanceId)) {
                            if (UtilConstants.isNetworkAvailable(context)) {
                                try {
                                    refguid = GUID.newRandom();
                                    SyncUtils.updatingSyncStartTime(context,Constants.Dashboard_sync,Constants.StartSync,refguid.toString().toUpperCase());
                                    OfflineManager.refreshStoreSync(context, new UIListener() {
                                        @Override
                                        public void onRequestError(int operation, Exception e) {
                                            ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
                                            isRefreshedAttendance=true;
                                            if(Constants.writeDebug){
                                                LogManager.writeLogDebug("Dahsboard refresh : Attendances loading offline ");
                                            }
                                            refreshAttendanceUI();
                                        }

                                        @Override
                                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                            ArrayList<String> alCollList = new ArrayList<>();
                                            alCollList.add(Constants.Attendances);
                                            alCollList.add(Constants.ConfigTypsetTypeValues);
                                            Constants.updateLastSyncTimeToTable(context,alCollList,Constants.Dashboard_sync,refguid.toString().toUpperCase());
                                            isRefreshedAttendance=true;
                                            if(Constants.writeDebug){
                                                LogManager.writeLogDebug("Dahsboard refresh : Attendances loading Online ");
                                            }
                                            refreshAttendanceUI();
                                        }
                                    }, Constants.Fresh, Constants.Attendances);
                                } catch (OfflineODataStoreException e) {
                                    refreshAttendanceUI();
                                    e.printStackTrace();
                                    if(Constants.writeDebug){
                                        LogManager.writeLogDebug("Dahsboard refresh : Attendances error : "+e.getMessage());
                                    }
                                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                                }
                            } else {
                                isRefreshedAttendance=true;
                                refreshAttendanceUI();
                            }
                        } else {
                            refreshAttendanceUI();
                        }
                        Log.d("Test","Attendence");
                    }
                }
            }).start();
        }
        if (sharedPreferences.getString(Constants.isRetailerApprovalKey, "").equalsIgnoreCase(Constants.isRetailerApprovalTcode)) {
            getDataFromOnline();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getDataFromDB();
                    ((Activity) activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iDaySummViewPresenter != null) {
                                iDaySummViewPresenter.hideProgressDialog();
                                iDaySummViewPresenter.displayList(alTargets,alVisitBeat,alBrandDeatils);
                            }
                        }
                    });
                }
            }).start();
        }

//



    }

    private void refreshAttendanceUI() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null)
                    iDaySummViewPresenter.hideAttendancePB();
            }
        });

    }

    private void getDataFromOnline() {
        Log.d("Test","getDataFromOnline");
        if(!Constants.IsOnlineStoreStarted) {
            Log.d("Test","IsOnlineStoreStarted"+"--"+"false");
            Log.d("Test","IsOnlineStoreStarted"+"--"+"afterInstance");
            /*OnlineODataStore store = openListener.getStore();*/
            Log.d("Test","IsOnlineStoreStarted"+"--"+"after_getstore");
//            if (store != null) {
            Log.d("Test","IsOnlineStoreStarted"+"--"+"after_getstore_not_null");
            getRETApprovalCount();
            displayUIList();
//            } else {
//                Log.d("Test","IsOnlineStoreStarted"+"--"+"after_getstore_null");
//                openOnlineStore();
//                displayUIList();
//            }
        }else{
            displayUIList();
            Log.d("Test","IsOnlineStoreStarted"+"--"+"true");
        }
    }

    private void openOnlineStore() {
        //optional store open
        if (UtilConstants.isNetworkAvailable(activity)) {
            new OpenOnlineManagerStore(activity, new AsyncTaskCallBack() {
                @Override
                public void onStatus(boolean status, String values) {
                    if (status) {
                        getRETApprovalCount();
                    } else {
                        if (iDaySummViewPresenter != null)
                            iDaySummViewPresenter.showMessage(values);
                    }
                    displayUIList();
                }
            }).execute();
        }
    }
    private void getRETApprovalCount() {
        if (UtilConstants.isNetworkAvailable(activity)) {
            getCountRequest();
        }
    }
    public void getCountRequest() {
        totalRequest = 0;
        currentRequest = 0;
//        totalRETCount = "";
//        totalSOCount = "";

        if (!TextUtils.isEmpty(totalRETCount)) {
            if (!isReloadRETApproval) {
                if (!TextUtils.isEmpty(RetailerApprovalListActivity.RetApprovalTotalCount))
                    totalRETCount = RetailerApprovalListActivity.RetApprovalTotalCount;  // Eanble after approval is done
                refreshUI("");
            } else {
                if (isReloadRETApproval) {
                    totalRETCount = "";
                    requestRET();
                    isReloadRETApproval = false;
                }
            }

        } else {
            requestRET();
        }

    }

    public void requestRET() {
        if (sharedPreferences.getString(Constants.isRetailerApprovalKey, "").equalsIgnoreCase(Constants.isRetailerApprovalTcode)) {  //
            totalRequest++;
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.hideProgressDialog();
                iDaySummViewPresenter.showMTPProgress();
            }

            OnlineManager.doOnlineGetRequest(Constants.Tasks + "/?$select=InstanceID&$filter=" + Constants.EntityType + "+eq+'CP'", context, event -> {
                if (event.getResponseStatusCode() == 200) {
                    String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                    Log.d("OnlineManager", "getUserRollInfo: " + responseBody + " " + event.getResponseStatusCode());
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(responseBody);
                        JSONObject dObject = jsonObj.getJSONObject("d");
                        JSONArray resultArray = dObject.getJSONArray("results");
                        totalRETCount = String.valueOf(resultArray.length());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iDaySummViewPresenter != null) {
                                    iDaySummViewPresenter.hideMTPProgress();
                                    iDaySummViewPresenter.disPlayMTPCount(totalRETCount);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        refreshUI(responseBody);
                    }
                } else {
                    String errorMsg="";
                    try {
                        errorMsg = Constants.getErrorMessage(event,context);
                        refreshUI(errorMsg);
                        LogManager.writeLogError(errorMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        refreshUI(e.getMessage());
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
                refreshUI(errormessage);
            });
        }
    }


    @Override
    public void onFilter() {

        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.openFilter(startDate, endDate, filterType, statusId, delvStatusId);
        }

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
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.searchResult(searchTargetAL);
        }
    }

    @Override
    public void onRefresh() {
        alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Targets);
        alAssignColl.add(Constants.TargetItems);
        alAssignColl.add(Constants.KPISet);
        alAssignColl.add(Constants.KPIItems);
        alAssignColl.add(Constants.SSSOs);
        alAssignColl.add(Constants.SSSoItemDetails);
        alAssignColl.add(Constants.Attendances);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        getTargets(alAssignColl);
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
//        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
//        statusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS);
//        statusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_STATUS_NAME);
//        delvStatusId = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS);
//        delvStatusName = data.getStringExtra(BehaviourFilterActivity.EXTRA_BEHAVIOUR_DELV_STATUS_NAME);
        displayFilterType();
    }

    private void getDataFromDB() {
        getSystemKPI();
        getMyTargetsList();
        alTargets = new ArrayList<>();
        String mStrSpGuid = "", mStrCurrency = "";
        Constants.alTodayBeatRet.clear();
        Constants.alRetailersGuid36.clear();
        Constants.alVisitedGuid36.clear();
        Constants.alRouteGuid36.clear();
        Constants.alVisitGuid36.clear();
        Constants.visitRetailersMap.clear();

        String targetCustomers = Constants.getVisitTargetForToday();
        String actualVistedCustomers = Constants.getVisitedRetailerCount();
        String totalVisiedCount = Constants.getVisitedCount();
        dmsDivQryBean = Constants.getDMSDIV("");
//        int mIntBalVisit = Constants.getBalanceVisit(Constants.alTodayBeatRet);
        deletePostedSOData();
        String yourOrderValue = "0.0";
        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), Constants.alTodayBeatRet);
        if (sharedPreferences.getString(Constants.isTCPCEnabled, "").equalsIgnoreCase(Constants.isTCPCTcode))
            yourOrderValue = Constants.getyourOrderValue(context, UtilConstants.getNewDate());

//        String mStrDeviceTLSD = Constants.getDeviceTLSD(Constants.alRetailers, Constants.SecondarySOCreate, context);
//        String mStrDeviceUniqueBillCut = Constants.getDeviceBillCut(Constants.alRetailers, Constants.SecondarySOCreate, context);
//        String mStrDeviceTLSDOffLine = Constants.getDeviceTLSDOffline("");
        /*String mStrVisitCount = null;
        try {
            mStrVisitCount = "0";
            ArrayList<RetailerBean> alVisitedRet = Constants.getVisitedRetFromVisit();
            if(alVisitedRet!=null && alVisitedRet.size()>0){
                mStrVisitCount = alVisitedRet.size()+"";
            }else{
                mStrVisitCount = "0";
            }
        } catch (Exception e) {
            mStrVisitCount = "0";
            e.printStackTrace();
        }*/

            String mStrSOCount = Constants.getOrderCountFromSSSOs(context, UtilConstants.getNewDate());
            String mStrVisitedSOCount = Constants.getVisitedOrderCount(context, UtilConstants.getNewDate());
            try {
                if (sharedPreferences.getString(Constants.isBeatEnabled, "").equalsIgnoreCase(Constants.isBeatTcode)) {
                    alVisitBeat = Constants.getBeatCount(context);
                }
                Log.d("TC/PC","start");
                if (sharedPreferences.getString(Constants.isBrandEnabled, "").equalsIgnoreCase(Constants.isBrandTcode)) {
                    alBrandDeatils = Constants.getBrandDetails(context);
                }
                Log.d("TC/PC","after_looping_data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Double mCalTLSDVal = 0.0;
//        try {
//            mCalTLSDVal = Double.parseDouble(mStrDeviceTLSDOffLine) + Double.parseDouble(mStrDeviceTLSD);
//        } catch (NumberFormatException e) {
//            mCalTLSDVal = 0.0;
//            e.printStackTrace();
//        }

        kpiNames = new HashSet<>();
//        alTargets = getValuesFromMap(mapMyTargetVal, totalOrderValue, mCalTLSDVal + "", mStrDeviceUniqueBillCut);

        MyTargetsBean myTargetsBean = new MyTargetsBean();
        myTargetsBean.setMonthTarget(targetCustomers);
        myTargetsBean.setMTDA(actualVistedCustomers);
        double mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
        myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
        myTargetsBean.setBTD(totalOrderValue);
        myTargetsBean.setYourOrderValue(yourOrderValue);
        myTargetsBean.setKPIName("Visits");
        myTargetsBean.setKpiNames(kpiNames);
//        myTargetsBean.setTcVSPC(actualVistedCustomers+" / "+mStrSOCount);
        int nonProCall;
        try {
            nonProCall = 0;
            if(!TextUtils.isEmpty(totalVisiedCount) && !TextUtils.isEmpty(mStrVisitedSOCount)){
                nonProCall = Integer.parseInt(totalVisiedCount) - Integer.parseInt(mStrVisitedSOCount);
            }
        } finally {

        }
        try {
            myTargetsBean.setTcVSPC(totalVisiedCount+"/"+mStrVisitedSOCount + "/"+nonProCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
        alTargets.add(myTargetsBean);
        Log.d("Test","getTargets");
    }

    private void deletePostedSOData() {
       /* try {
            Set<String> soRefNos = sharedPreferences.getStringSet(Constants.soRefrenceNoToRemove, null);
            Set<String> valuestoremove = new HashSet<String>();
            valuestoremove = soRefNos;
            if(soRefNos!=null && soRefNos.size()>0) {
                for(String docNo:soRefNos) {
                    Constants.removeDeviceDocNoFromSharedPref(context, Constants.SecondarySOCreateTemp, docNo, sharedPreferences, false);
                    try {
                        valuestoremove.remove(docNo);
                    }catch (Throwable e){
                        e.printStackTrace();
                    }
                }
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putStringSet(Constants.soRefrenceNoToRemove, valuestoremove);
                spEditor.commit();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Constants.isBackGroundSync = false;
            Constants.isSync = false;
        }*/

        try {
            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.SecondarySOCreateTemp, null);
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    String store = null, deviceNo = "";
                    try {
                        deviceNo = itr.next().toString();
                        store = ConstantsUtils.getFromDataVault(deviceNo,context);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    try {
                        if(store!=null) {
                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            String ssoGuid = fetchJsonHeaderObject.getString(Constants.SSSOGuid);
                            String query = Constants.SSSOs + "?$filter=" + Constants.SSSOGuid + " eq guid'" + ssoGuid + "'";
                            try {
                                if (OfflineManager.isSoPresent(context, query)) {
                                    Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreateTemp, deviceNo, false);
                                    try {
                                        ConstantsUtils.storeInDataVault(deviceNo, "", context);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    /*public ArrayList<MyTargetsBean> getTargetsFromOfflineDB() {
        getSystemKPI();
        getMyTargetsList();
        alTargets = new ArrayList<>();
        Constants.alTodayBeatCustomers.clear();
        String actualVistedCustomers = Constants.getVisitedRetailerCount();
        ArrayList<String> setVisitedCustomers = Constants.getTodayVisitedCustomers();
        String targetCustomers = Constants.getVisitTargetForToday(context);
        String totalOrderValue = Constants.getTotalOrderValue(context, UtilConstants.getNewDate(), setVisitedCustomers);
        String mStrDeviceTLSDOffLine = Constants.getDeviceTLSD("");
        String mStrDeviceTLSD = Constants.getDeviceTLSDDataVault(Constants.alCustomers, context);

        Double mCalTLSDVal = 0.0;
        try {
            mCalTLSDVal = Double.parseDouble(mStrDeviceTLSDOffLine) + Double.parseDouble(mStrDeviceTLSD);
        } catch (NumberFormatException e) {
            mCalTLSDVal = 0.0;
            e.printStackTrace();
        }
        String mStrDeviceUniqueBillCut = Constants.getUniqueBillCut();
        kpiNames = new HashSet<>();
        alTargets = getValuesFromMap(mapMyTargetVal, totalOrderValue, mCalTLSDVal + "",mStrDeviceUniqueBillCut);

        MyTargetsBean myTargetsBean = new MyTargetsBean();

        myTargetsBean = new MyTargetsBean();
        myTargetsBean.setMonthTarget(targetCustomers);
        myTargetsBean.setMTDA(actualVistedCustomers);
        double mDouAchivedPercentage = OfflineManager.getAchivedPer(targetCustomers, actualVistedCustomers);
        myTargetsBean.setAchivedPercentage(mDouAchivedPercentage + "");
        myTargetsBean.setBTD(totalOrderValue);
        myTargetsBean.setKPIName("Visits");
        myTargetsBean.setKpiNames(kpiNames);
        alTargets.add(myTargetsBean);

        return alTargets;

    }*/

    private ArrayList<MyTargetsBean> getValuesFromMap(Map<String, MyTargetsBean> mapMyTargetVal, String orderVal, String mStrTlSD, String mStrUniqueBillCut) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        int remaingDays = numDays - day;

        ArrayList<MyTargetsBean> alTargets = new ArrayList<>();
        if (!mapMyTargetVal.isEmpty()) {
            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                myTargetsBean.setMonthTarget(mapMonthTarget.get(key).toString());
                kpiNames.add(myTargetsBean.getKPIName());  // adding kpi names
                myTargetsBean.setKpiNames(kpiNames);
                String kpiQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " +
                        "" + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' " +
                        "and " + Constants.CalculationBase + " eq '02' and " + Constants.KPICode + " eq '" + myTargetsBean.getKPICode() + "'";

                String tlsdQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " +
                        "" + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' " +
                        "and " + Constants.CalculationBase + " eq '04' and " + Constants.KPICode + " eq '" + myTargetsBean.getKPICode() + "'";

                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(kpiQry)) {
                        myTargetsBean.setMTDA(orderVal);
                    } else if (OfflineManager.getVisitActivityStatusForVisit(tlsdQry)) {
                        myTargetsBean.setMTDA(mStrTlSD);
                    } else {
                        myTargetsBean.setMTDA(mapMonthAchived.get(key).toString());
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(), myTargetsBean.getMTDA());
                double dayTarget = OfflineManager.getDayTarget(BTDPer + "", remaingDays + "");

                if (dayTarget < 0) {
                    dayTarget = 0.0;
                }

                myTargetsBean.setMonthTarget(dayTarget + "");
                myTargetsBean.setBTD(BTDPer + "");
                myTargetsBean.setAchivedPercentage(achivedPer + "");
                alTargets.add(mapMyTargetVal.get(key));
            }
        }
        if (!kpiNames.contains("Sales")){
            MyTargetsBean myTargetsBean = new MyTargetsBean();
            myTargetsBean.setMonthTarget("0");
            myTargetsBean.setMTDA("0");
            myTargetsBean.setAchivedPercentage("0");
            myTargetsBean.setKPIName("Sales");
            alTargets.add(myTargetsBean);
        }
        return alTargets;
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI() {
        try {
            if (alKpiList != null) {
                alKpiList.clear();
            }
            String mStrMyStockQry;
            mStrMyStockQry = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "'  ";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry, "");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

//    private void getPendingDispatch() {  //and DelvStatus eq 'A'
//        String qry = Constants.SOs + "?$filter=Status eq 'B' ";
//        try {
//            salesOrderHeaderArrayList = OfflineManager.getSecondarySalesOrderList(context, qry, "", "00");
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }
//    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList != null && alKpiList.size() > 0) {
                if (alMyTargets != null) {
                    alMyTargets.clear();
                }
                alMyTargets = OfflineManager.getMyTargets(alKpiList, Constants.getSPGUID());
            }
            mapMonthTarget.clear();
            mapMonthAchived.clear();
            mapMyTargetVal.clear();
            mapMyTargetVal = getALMyTargetList(alMyTargets);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    // sum of actual and target quantity/Value based on kpi code and assign to map table
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
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int i, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(i, exception, context);
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            Constants.isSync = false;
            if (i == Operation.OfflineRefresh.getValue()) {
                responseFailed(null, 0, 0, "", context.getString(R.string.msg_error_occured_during_sync), null);
            }else{
                responseFailed(null, 0, 0, "", context.getString(R.string.msg_error_occured_during_sync), null);
            }
        }else if (errorBean.isStoreFailed()) {
            String concatCollectionStr = "";
            if (UtilConstants.isNetworkAvailable(context)) {
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(SyncUtils.getTargets());
                Constants.isSync = true;
                new RefreshAsyncTask(context, concatCollectionStr, this).execute();
            } else {
                Constants.isSync = false;
                responseFailed(null, 0, 0, "", context.getString(R.string.msg_error_occured_during_sync), null);
            }
        } else{
            Constants.isSync = false;
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Dashboard_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (iDaySummViewPresenter != null) {
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                iDaySummViewPresenter.hideProgressDialog();
                onStart();
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Sync_All,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context, false);
            if (iDaySummViewPresenter != null) {
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                iDaySummViewPresenter.hideProgressDialog();
                onStart();
            }
        }
    }

    /**
     * sync Dealer Behaviour online
     *
     * @param collectionName
     */
    private void getTargets(@NonNull ArrayList<String> collectionName) {
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(collectionName);
            if (Constants.iSAutoSync) {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.hideProgressDialog();

                    if(Constants.writeDebug){
                        LogManager.writeLogDebug("Dashboard refresh : "+context.getString(R.string.alert_auto_sync_is_progress));
                    }
                    iDaySummViewPresenter.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.showProgressDialog();
                }
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Dashboard_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    if(Constants.writeDebug){
                        LogManager.writeLogDebug("Dashboard refresh : Started : "+concatCollectionStr);
                    }
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (iDaySummViewPresenter != null) {
                iDaySummViewPresenter.hideProgressDialog();
                iDaySummViewPresenter.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, final List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                if (entities != null) {
                    totalRETCount = String.valueOf(entities.size());
                    if(Constants.writeDebug){
                        LogManager.writeLogDebug("Dashboard refresh Success: RETCount "+ totalRETCount);
                    }
//                    MTPApprovalPresenter.mtpTotalCount = totalRETCount;  // Todo enable after approval details
                }
                Log.d("Test","totalRETCount");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideMTPProgress();
                            iDaySummViewPresenter.disPlayMTPCount(totalRETCount);
                        }
                    }
                });
                currentRequest++;
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, final String errorMsg, Bundle bundle) {
        currentRequest++;
        if (currentRequest == totalRequest) {
            if(Constants.writeDebug){
                LogManager.writeLogDebug("Dashboard refresh Error: "+ errorMsg);
            }
            refreshUI(errorMsg);
        }
    }
    private void displayUIList() {
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();
                Log.d("Test","displayUIList");
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();
                            Log.d("TC/PC","before_sending_toUI");
                            iDaySummViewPresenter.displayList(alTargets,alVisitBeat,alBrandDeatils);
                        }
                    }
                });
            }
        }).start();
    }

    private void refreshUI(final String errorMsg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (iDaySummViewPresenter != null) {
                    iDaySummViewPresenter.hideMTPProgress();
                    iDaySummViewPresenter.disPlayMTPCount(totalRETCount);
                    if (!TextUtils.isEmpty(errorMsg)) {
                        iDaySummViewPresenter.showMessage(errorMsg);
                    }
                }
            }
        });
    }

    public void onLoadOfflinedata() {
        if (iDaySummViewPresenter != null) {
            iDaySummViewPresenter.showProgressDialog();
//            iDaySummViewPresenter.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();
                Log.d("Test","displayUIList");
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iDaySummViewPresenter != null) {
                            iDaySummViewPresenter.hideProgressDialog();
                            iDaySummViewPresenter.displayList(alTargets,alVisitBeat,alBrandDeatils);
                        }
                    }
                });
            }
        }).start();
    }
}
