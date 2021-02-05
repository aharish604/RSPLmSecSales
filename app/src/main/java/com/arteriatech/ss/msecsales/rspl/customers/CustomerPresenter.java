package com.arteriatech.ss.msecsales.rspl.customers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

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
import com.arteriatech.ss.msecsales.rspl.customers.filter.CustomersFilterActivity;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPHeaderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPRoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.WeekDetailsList;
import com.arteriatech.ss.msecsales.rspl.mbo.WeekHeaderList;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by e10847 on 19-12-2017.
 */

public class CustomerPresenter implements ICustomerPresenter, UIListener {
    public ArrayList<RetailerBean> retailerArrayList, searchBeanArrayList;
    ArrayList<RetailerBean> arrayListnew = new ArrayList<>();

    ArrayList<String> alAssignColl = null;
    boolean isValidData = true;
    private Context context;
    private ICustomerViewPresenter iCustomerViewPresenter;
    private Activity activity;
    private String visitType = "", customerNumber = "", filterType = "", statusId = "", statusName = "", delvStatusId = "", delvStatusName = "";
    private boolean isErrorFromBackend = false;
    private String searchText = "";
    private String salesDistrictId = "";
    private String mStrCustName = "";
    private String beatGuid = "";
    private BeatOpeningSummaryBean beatOpeningSummaryBean = null;
    private String cpGuid = "";
    private ArrayList<RetailerBean> alRSCHList = null;
    private int countPerRequest = 50;
    private String  allBeatQuery = "";
//    private HashMap<String,String> cpGrp4Desc = null;
    private GUID refguid =null;
    private boolean stopLazyAsyncTask = false;
    private LazyProspectCustomerAsyncTask lazyListAsynTask = null;


    public CustomerPresenter(Context context, Activity activity, ICustomerViewPresenter iCustomerViewPresenter, String visitType, @NonNull String customerNumber, String salesDistrictId, String cpGuid) {
        this.context = context;
        this.iCustomerViewPresenter = iCustomerViewPresenter;
        this.retailerArrayList = new ArrayList<>();
//        this.cpGrp4Desc = new HashMap<>();
        this.searchBeanArrayList = new ArrayList<>();
        this.visitType = visitType;
        this.activity = activity;
        this.customerNumber = customerNumber;
        this.salesDistrictId = salesDistrictId;
        this.cpGuid = cpGuid;
    }


    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.hideProgressDialog();
        }
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                }
            }else if (i == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.displayMsg(context.getString(R.string.msg_error_occured_during_sync));
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Retailers_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        getOtherBeatList();
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
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                getOtherBeatList();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, activity, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    @Override
    public void onFilter() {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.openFilter(filterType, statusId, delvStatusId);
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
    public void onRefresh() {
        onRefreshRetailerList();
    }

    @Override
    public void startFilter(int requestCode, int resultCode, Intent data) {
        filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
        statusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS);
        statusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS_NAME);
        delvStatusId = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS);
        delvStatusName = data.getStringExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
//        requestSOList(startDate, endDate);
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
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.setFilterDate(statusDesc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAsyncTask(String beatGuid) {
        this.beatGuid = beatGuid;
        retailerArrayList.clear();
        Log.e("Adhoc_Visit","AsyncTaskBefore");
       // new ProspectCustomerAsyncTask(countPerRequest, 0).execute();
        if(!TextUtils.isEmpty(beatGuid)) {
            stopLazyAsyncTask = true;
            new ProspectCustomerAsyncTask(countPerRequest, 0).execute();
           /* if(lazyListAsynTask!=null && lazyListAsynTask.getStatus() == AsyncTask.Status.RUNNING){
                lazyListAsynTask.cancel(true);
            }*/
        }
        else {
            countPerRequest=1;
            stopLazyAsyncTask = false;
            if(alRSCHList!=null && alRSCHList.size()>1){
                allBeatQuery = alRSCHList.get(countPerRequest).getRschGuid();
            }
            lazyListAsynTask = new LazyProspectCustomerAsyncTask(countPerRequest, 0);
            lazyListAsynTask.execute();
        }


    }

    @Override
    public void sendResult(final MTPHeaderBean mtpResultHeaderBean, final MTPHeaderBean mtpHeaderBean, final boolean isAsmLogin) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<MTPRoutePlanBean> mtpRoutePlanList = new ArrayList<>();
                    if (!retailerArrayList.isEmpty()) {
                        for (RetailerBean customerBean : retailerArrayList) {
                            if (customerBean.isChecked()) {
                                MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                                mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                                mtpRoutePlanBean.setActivityDec(mtpResultHeaderBean.getActivityDec());
                                mtpRoutePlanBean.setActivityId(mtpResultHeaderBean.getActivityID());
                                if (isAsmLogin) {
                                    mtpRoutePlanBean.setSalesDistrict(customerBean.getCustomerId());
                                    mtpRoutePlanBean.setSalesDistrictDesc(customerBean.getCustomerName());
                                } else {
                                    mtpRoutePlanBean.setCustomerNo(customerBean.getCustomerId());
                                    mtpRoutePlanBean.setCustomerName(customerBean.getCustomerName());
                                    mtpRoutePlanBean.setAddress(customerBean.getAddress1());
                                    mtpRoutePlanBean.setPostalCode(customerBean.getPostalCode());
                                    mtpRoutePlanBean.setMobile1(customerBean.getMobile1());
                                }
                                mtpRoutePlanBean.setRouteSchGUID(mtpHeaderBean.getRouteSchGUID());
                                mtpRoutePlanBean.setRouteSchPlanGUID(customerBean.getRouteSchPlanGUID());
                                mtpRoutePlanList.add(mtpRoutePlanBean);
                            }
                        }
                    }
                    if (mtpRoutePlanList.isEmpty() && !mtpResultHeaderBean.getActivityID().equalsIgnoreCase("01")) {
                        MTPRoutePlanBean mtpRoutePlanBean = new MTPRoutePlanBean();
                        mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                        mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                        mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                        mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                        mtpRoutePlanBean.setActivityDec(mtpResultHeaderBean.getActivityDec());
                        mtpRoutePlanBean.setActivityId(mtpResultHeaderBean.getActivityID());
                        if (!mtpHeaderBean.getMTPRoutePlanBeanArrayList().isEmpty())
                            mtpRoutePlanBean.setRouteSchPlanGUID(mtpHeaderBean.getMTPRoutePlanBeanArrayList().get(0).getRouteSchPlanGUID());
                        mtpRoutePlanList.add(mtpRoutePlanBean);
                    }
                    if (!mtpRoutePlanList.isEmpty()) {
                        if (isAsmLogin) {
                            if (mtpRoutePlanList.size() == 1) {
                                mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc());
                            } else {
                                mtpResultHeaderBean.setSalesDistrictDisc(mtpRoutePlanList.get(0).getSalesDistrictDesc() + "...");
                            }
                            mtpResultHeaderBean.setSalesDistrict(mtpRoutePlanList.get(0).getSalesDistrict());
                        } else {
                            if (mtpRoutePlanList.size() == 1) {
                                mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName());
                            } else {
                                mtpResultHeaderBean.setCustomerName(mtpRoutePlanList.get(0).getCustomerName() + "...");
                            }
                            mtpResultHeaderBean.setCustomerNo(mtpRoutePlanList.get(0).getCustomerNo());
                        }
                    }

                    mtpResultHeaderBean.setMTPRoutePlanBeanArrayList(mtpRoutePlanList);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mtpRoutePlanList.isEmpty()) {
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.displayMsg("Select customer");
                                }
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.EXTRA_BEAN, mtpResultHeaderBean);
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.sendSelectedItem(intent);
                                }
                            }
                        }
                    });

                }
            }).start();

        }
    }

    @Override
    public void loadMTPCustomerList(final ArrayList<MTPRoutePlanBean> mtpRoutePlanBeanArrayList, final boolean isAsmLogin) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (isAsmLogin) {
                        String routeQry = Constants.CustomerSalesAreas + "?$select=SalesDistrictID,SalesDistrictDesc";
                        retailerArrayList = OfflineManager.getCustomerSalesAreaList(routeQry, mtpRoutePlanBeanArrayList);
                    } else {
                        String routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=" + Constants.RetailerName + "%20asc";
                        retailerArrayList = OfflineManager.getCustomerList(routeQry, mtpRoutePlanBeanArrayList);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.searchResult(retailerArrayList);
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Searc query to update the retailerList
     *
     * @param searchText
     */
    private void onSearchQuery(String searchText) {
        Log.e("Adhoc_Visit","onSearchQuery");
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        boolean isCity = false;
        if (retailerArrayList != null) {
            if (TextUtils.isEmpty(searchText)) {
                for (RetailerBean retailerBean : retailerArrayList) {
                    ArrayList<RetailerBean> tempArr = retailerBean.getItemList();
//                    retailerBean.setItemList(new ArrayList<RetailerBean>());
                    retailerBean.setRetailerCount(String.valueOf(tempArr.size()));
                    if(beatOpeningSummaryBean!=null){
                        retailerBean.setBeatOpeningSummaryBean(beatOpeningSummaryBean);
                    }
                    searchBeanArrayList.add(retailerBean);
                    searchBeanArrayList.addAll(tempArr);
                }
            } else {
                try {
                    for (RetailerBean item : retailerArrayList) {
                        ArrayList<RetailerBean> tempRetailerList = new ArrayList<>();
                        for (RetailerBean retailerBean : item.getItemList()) {
                            if (!TextUtils.isEmpty(searchText)) {
                                isCustomerID = retailerBean.getCPNo().toLowerCase().contains(searchText.toLowerCase());
                                isCustomerName = retailerBean.getRetailerName().toLowerCase().contains(searchText.toLowerCase());
                                isCity = retailerBean.getCity().toLowerCase().contains(searchText.toLowerCase());
                            } else {
                                isCustomerID = true;
                                isCustomerName = true;
                                isCity = true;
                            }
                            if (isCustomerID || isCustomerName || isCity) {
                                tempRetailerList.add(retailerBean);
                            }
                        }
                        if (tempRetailerList.size()>0) {
                            item.setRetailerCount(String.valueOf(tempRetailerList.size()));
                            searchBeanArrayList.add(item);
                            searchBeanArrayList.addAll(tempRetailerList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (iCustomerViewPresenter != null) {
            Log.e("Adhoc_Visit","searchResult");
            iCustomerViewPresenter.searchResult(searchBeanArrayList);
        }

    }
    private void lazyonSearchQuery(String searchText) {
        Log.e("Adhoc_Visit","onSearchQuery");
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean isCustomerID = false;
        boolean isCustomerName = false;
        boolean isCity = false;
        if (arrayListnew != null) {
            retailerArrayList.addAll(arrayListnew);
            if (TextUtils.isEmpty(searchText)) {
                for (RetailerBean retailerBean : retailerArrayList) {
                    ArrayList<RetailerBean> tempArr = retailerBean.getItemList();
//                    retailerBean.setItemList(new ArrayList<RetailerBean>());
                    retailerBean.setRetailerCount(String.valueOf(tempArr.size()));
                    if(beatOpeningSummaryBean!=null){
                        retailerBean.setBeatOpeningSummaryBean(beatOpeningSummaryBean);
                    }
                    searchBeanArrayList.add(retailerBean);
                    searchBeanArrayList.addAll(tempArr);
                }
            } else {
                try {
                    for (RetailerBean item : retailerArrayList) {
                        ArrayList<RetailerBean> tempRetailerList = new ArrayList<>();
                        for (RetailerBean retailerBean : item.getItemList()) {
                            if (!TextUtils.isEmpty(searchText)) {
                                isCustomerID = retailerBean.getCPNo().toLowerCase().contains(searchText.toLowerCase());
                                isCustomerName = retailerBean.getRetailerName().toLowerCase().contains(searchText.toLowerCase());
                                isCity = retailerBean.getCity().toLowerCase().contains(searchText.toLowerCase());
                            } else {
                                isCustomerID = true;
                                isCustomerName = true;
                                isCity = true;
                            }
                            if (isCustomerID || isCustomerName || isCity) {
                                tempRetailerList.add(retailerBean);
                            }
                        }
                        if (tempRetailerList.size()>0) {
                            item.setRetailerCount(String.valueOf(tempRetailerList.size()));
                            searchBeanArrayList.add(item);
                            searchBeanArrayList.addAll(tempRetailerList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (iCustomerViewPresenter != null) {
            Log.e("Adhoc_Visit","searchResult");
            iCustomerViewPresenter.searchResult(searchBeanArrayList);
        }

    }

    /**
     * refreshing the RetailerList Online
     */
    private void onRefreshRetailerList() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getFOS());
            alAssignColl.addAll(SyncUtils.getBeatCollection());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                alAssignColl.add(Constants.VisitSummarySet);
            }
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.hideProgressDialog();
                    iCustomerViewPresenter.displayMsg(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    // progressDialog = Constants.showProgressDialog(context, "", context.getString(R.string.msg_sync_progress_msg_plz_wait));
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Retailers_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (iCustomerViewPresenter != null) {
                        iCustomerViewPresenter.hideProgressDialog();
                        iCustomerViewPresenter.displayMsg(e.getMessage());
                    }
                }
            }
        } else {
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                iCustomerViewPresenter.displayMsg(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void loadRTGSCustomerList(final ArrayList<WeekDetailsList> rtgsBeanArrayList) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String routeQry = Constants.Customers + "?$select=CustomerNo,Name,Address1,Address2,Address3,District,City,PostalCode,Mobile1,Currency &$orderby=Name asc";

                    try {
                        retailerArrayList = OfflineManager.getCustomerListRTGS(routeQry, rtgsBeanArrayList);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iCustomerViewPresenter != null) {
                                iCustomerViewPresenter.searchResult(retailerArrayList);
                                iCustomerViewPresenter.hideProgressDialog();
                            }
                        }
                    });
                }
            }).start();
        }

    }

    @Override
    public void loadRTGSList(final ArrayList<WeekDetailsList> rtgsBeanArrayList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<RetailerBean> retailerArrayList = new ArrayList<>();
                if (rtgsBeanArrayList != null && rtgsBeanArrayList.size() > 0) {
                    for (WeekDetailsList weekHeaderList : rtgsBeanArrayList) {
                        RetailerBean customerBean = new RetailerBean("");
                        customerBean.setRouteSchPlanGUID(weekHeaderList.getCollectionPlanItemGUID());
                        customerBean.setAmount(weekHeaderList.getPlannedValue());
                        customerBean.setRemarks(weekHeaderList.getRemarks());
                        customerBean.setCustomerId(weekHeaderList.getcPNo());
                        customerBean.setCustomerName(weekHeaderList.getcPName());
                        customerBean.setRouteSchPlanGUID(weekHeaderList.getCollectionPlanItemGUID());
                        customerBean.setCollPlanHeaderGUID(weekHeaderList.getCollectionPlanGUID());
                        customerBean.setCustomerType(weekHeaderList.getcPType());
                        retailerArrayList.add(customerBean);
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.searchResult(retailerArrayList);
                            iCustomerViewPresenter.hideProgressDialog();
                        }
                    }
                });

            }
        }).start();
    }

    @Override
    public void getOtherBeatList() {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mRouteFilterQry = "";
                    if (!TextUtils.isEmpty(cpGuid)) {
                        mRouteFilterQry = mRouteFilterQry + "?$filter=" + Constants.CPGUID + " eq '" + cpGuid + "' and ApprovalStatus eq '03' and StatusID eq '01'";
                    }
                    Log.e("Adhoc_Visit","QueryFormation");

                    String routeQry = Constants.RouteSchedules + mRouteFilterQry;
                    alRSCHList = OfflineManager.getBeatListWithAll(routeQry);
                    Log.e("Adhoc_Visit","Listpopulated");

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iCustomerViewPresenter != null) {
                            iCustomerViewPresenter.displayBeat(alRSCHList);
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    public void sendResultRTGS(final WeekHeaderList mtpResultHeaderBean, final WeekHeaderList mtpHeaderBean, final ArrayList<RetailerBean> selCustomers) {
        if (iCustomerViewPresenter != null) {
            iCustomerViewPresenter.showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<WeekDetailsList> rtgsWeekItemList = new ArrayList<>();
                    if (!selCustomers.isEmpty()) {
                        isValidData = true;
                        mStrCustName = "";
                        double mTotalAmt = 0.0;
                        for (RetailerBean customerBean : selCustomers) {
                            double mdoubAmt = 0.0;
                            try {
                                mdoubAmt = Double.parseDouble(customerBean.getAmount());
                            } catch (NumberFormatException e) {
                                mdoubAmt = 0.0;
                                e.printStackTrace();
                            }
                            if (mdoubAmt > 0) {
                                mTotalAmt = mTotalAmt + mdoubAmt;
                                WeekDetailsList mtpRoutePlanBean = new WeekDetailsList();
                                mtpRoutePlanBean.setVisitDate(mtpHeaderBean.getFullDate());
                                mtpRoutePlanBean.setDate(mtpHeaderBean.getDate());
                                mtpRoutePlanBean.setDay(mtpHeaderBean.getDay());
                                mtpRoutePlanBean.setRemarks(mtpResultHeaderBean.getRemarks());
                                mtpRoutePlanBean.setcPNo(customerBean.getCustomerId());
                                mtpRoutePlanBean.setcPName(customerBean.getCustomerName());
                                mtpRoutePlanBean.setcPType(customerBean.getCustomerType());
                                mtpRoutePlanBean.setPlannedValue(customerBean.getAmount());
                                mtpRoutePlanBean.setRemarks(customerBean.getRemarks());
                                mtpRoutePlanBean.setCollectionPlanGUID(mtpHeaderBean.getCollectionPlanGUID());
                                mtpRoutePlanBean.setCollectionPlanItemGUID(customerBean.getRouteSchPlanGUID());
                                rtgsWeekItemList.add(mtpRoutePlanBean);
                            } else {
                                mStrCustName = customerBean.getCustomerName();
                                isValidData = false;
                                break;
                            }

                        }
                        if (isValidData) {
                            if (!rtgsWeekItemList.isEmpty()) {
                                if (rtgsWeekItemList.size() == 1) {
                                    mtpResultHeaderBean.setName(rtgsWeekItemList.get(0).getCPName());
                                } else {
                                    mtpResultHeaderBean.setName(rtgsWeekItemList.get(0).getCPName() + "...");
                                }
                                mtpResultHeaderBean.setCustNo(rtgsWeekItemList.get(0).getCPNo());
                                mtpResultHeaderBean.setTotalAmount(mTotalAmt + "");
                            }
                        }
                    }
                    mtpResultHeaderBean.setWeekDetailsLists(rtgsWeekItemList);


                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (selCustomers.isEmpty()) {
                                if (iCustomerViewPresenter != null) {
                                    iCustomerViewPresenter.hideProgressDialog();
                                    iCustomerViewPresenter.displayMsg("Select atleast one customer");
                                }
                            } else {
                                if (isValidData) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.EXTRA_BEAN, mtpResultHeaderBean);
                                    if (iCustomerViewPresenter != null) {
                                        iCustomerViewPresenter.hideProgressDialog();
                                        iCustomerViewPresenter.sendSelectedItem(intent);
                                    }
                                } else {
                                    if (iCustomerViewPresenter != null) {
                                        iCustomerViewPresenter.hideProgressDialog();
                                        iCustomerViewPresenter.displayMsg("Enter Amount for " + mStrCustName + "");
                                    }
                                }

                            }
                        }
                    });

                }
            }).start();

        }
    }

    private class ProspectCustomerAsyncTask extends AsyncTask<Void, Void, Void> {
        private int top = 0;
        private int skip = 0;
//        private ArrayList<RetailerBean> retailerTempArrayList = new ArrayList<>();
//        private HashMap<String,String> cpGrp4DescTemp = new HashMap<>();

        ProspectCustomerAsyncTask(int top, int skip) {
            this.top = top;
            this.skip = skip;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (skip == 0) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                beatOpeningSummaryBean = null;
                String retailerQry = "";
                String mStrSPGUID = Constants.getSPGUID();
                String qryOrder = " &$orderby=RouteID asc";
                ArrayList<String> divisionList = null;
                try {
                    divisionList = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String stringDivision = "";
                if(divisionList!=null && !divisionList.isEmpty()) {
                    for (int i = 0; i < divisionList.size();i++){
                        if(i==divisionList.size()-1) {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i)+"'";
                        }else {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i) + "' or ";
                        }
                    }
                }
                Log.e("Adhoc_Visit","CPDMSQueryForm");

                if(!TextUtils.isEmpty(stringDivision)) {
                    if (beatGuid != null && !beatGuid.equalsIgnoreCase("")) {
//                        retailerQry = Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + beatGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and DMSDivision ne ''";
                        retailerQry = Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + beatGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ("+stringDivision+")";
                    } else {
//                        retailerQry = Constants.CPDMSDivisions + "?$filter=StatusID eq '01' and ApprvlStatusID eq '03' and DMSDivision ne ''";
                        retailerQry = Constants.CPDMSDivisions + "?$filter=StatusID eq '01' and ApprvlStatusID eq '03' and ("+stringDivision+")";
                    }
                    if (!TextUtils.isEmpty(cpGuid)) {
                        retailerQry = retailerQry + " and ParentID eq '" + cpGuid + "'";
                    }
                    if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                        retailerQry = retailerQry + " and " + Constants.PartnerMgrGUID + " eq guid'" + mStrSPGUID.toUpperCase() + "'";
                    }
                    retailerQry = retailerQry + qryOrder;
                }
//                retailerQry = Constants.ChannelPartners + " ?$orderby=Name asc";
//                if (!TextUtils.isEmpty(retailerQry)) {
//                    retailerQry = Constants.ChannelPartners+ "?$skip=" + skip + "&$top=" + top + "&$orderby=Name asc";
//                }
                Log.e("Adhoc_Visit","CPDMSQueryFormed");
                Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                retailerArrayList = OfflineManager.getRetailerList(retailerQry);
                Log.e("Adhoc_Visit","RetailerDatapopulated");

                /*if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_TSO_04)) {
                    Log.e("Adhoc_Visit","IFTso_OpeningOnlineStore");

                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    boolean isOnlineStoreOpened = false;
                    if (store != null) {
                        isOnlineStoreOpened = true;
                    } else {
                        isOnlineStoreOpened = OnlineManager.openOnlineStore(context,false);
                    }
                    Log.e("Adhoc_Visit","IFTso_OpeningOnlineStore"+isOnlineStoreOpened);
                    if (isOnlineStoreOpened) {
                        Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                        retailerArrayList = OnlineManager.getRetailerList(retailerQry);
                        Log.e("Adhoc_Visit","RetailerDatapopulated");
                    }
                } else {
                    Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                    retailerArrayList = OfflineManager.getRetailerList(retailerQry);
                    Log.e("Adhoc_Visit","RetailerDatapopulated");
                }*/
                if(beatGuid!=null && !beatGuid.equalsIgnoreCase("")) {
                    if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                        String beatOpeningQry = Constants.VisitSummarySet + "?$filter=" + Constants.RschGUID + " eq guid'" + beatGuid.toLowerCase() + "'" + " and " + Constants.SPGUID + " eq guid'" + Constants.getSPGUID().toLowerCase() + "'";
                        try {
                            beatOpeningSummaryBean = OfflineManager.getBeatOpeningDetails(beatOpeningQry);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }else {
                        beatOpeningSummaryBean = new BeatOpeningSummaryBean();
                    }
                }
                Log.e("Adhoc_Visit","Going to loop retailerlist");
                ArrayList<RetailerBean> retailerArrayListTemp = new ArrayList<>();
                if(retailerArrayList!=null && !retailerArrayList.isEmpty()){
                    for(RetailerBean retailerData : retailerArrayList){
                        String cpguidQry="";
                        if(retailerData.getItemList()!=null && !retailerData.getItemList().isEmpty()){
                            String routeGuid = "";
                            for(RetailerBean retailerItemData : retailerData.getItemList()){
                                routeGuid = retailerItemData.getRouteGuid36();
                                if (cpguidQry.length() == 0)
                                    cpguidQry += " guid'" + retailerItemData.getCPGUID() + "'";
                                else
                                    cpguidQry += " or " + Constants.CPGUID + " eq guid'" + retailerItemData.getCPGUID() + "'";
                            }
                            List<RetailerBean> listRetailers = OfflineManager.getTodayBeatListRetailer(cpguidQry, Constants.mMapCPSeqNo,routeGuid);
                            retailerData.setItemList((ArrayList<RetailerBean>) listRetailers);
                            retailerArrayListTemp.add(retailerData);
                        }
                    }
                }
                Log.e("Adhoc_Visit","looped retailerlist");

                retailerArrayList = retailerArrayListTemp;
//                cpGrp4DescTemp = OfflineManager.getCPGrp3Desc(retailerTempArrayList,mStrSPGUID);
//                cpGrp4Desc.putAll(cpGrp4DescTemp);
//                retailerArrayList.addAll(retailerTempArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onSearchQuery(searchText);
           /* if (top == retailerTempArrayList.size()) {
                int skipVal = skip + top;
                new ProspectCustomerAsyncTask(countPerRequest, skipVal).execute();
            } else {*/
            if (iCustomerViewPresenter != null) {
                iCustomerViewPresenter.hideProgressDialog();
                iCustomerViewPresenter.beatOpeningDetails(beatOpeningSummaryBean);
            }
//            }

        }
    }

    private class LazyProspectCustomerAsyncTask extends AsyncTask<Void, Void, Void> {
        private int top = 0;
        private int skip = 0;
//        private ArrayList<RetailerBean> retailerTempArrayList = new ArrayList<>();
//        private HashMap<String,String> cpGrp4DescTemp = new HashMap<>();

        LazyProspectCustomerAsyncTask(int top, int skip) {
            this.top = top;
            this.skip = skip;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (countPerRequest == 0) {
                if (iCustomerViewPresenter != null) {
                    iCustomerViewPresenter.showProgressDialog();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                beatOpeningSummaryBean = null;
                String retailerQry = "";
                String mStrSPGUID = Constants.getSPGUID();
                String qryOrder = " &$orderby=RouteID asc";
                ArrayList<String> divisionList = null;
                try {
                    divisionList = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String stringDivision = "";
                if(divisionList!=null && !divisionList.isEmpty()) {
                    for (int i = 0; i < divisionList.size();i++){
                        if(i==divisionList.size()-1) {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i)+"'";
                        }else {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i) + "' or ";
                        }
                    }
                }
                Log.e("Adhoc_Visit","CPDMSQueryForm");

                if(!TextUtils.isEmpty(stringDivision)) {
                    retailerQry = Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + allBeatQuery + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ("+stringDivision+")";


                    if (!TextUtils.isEmpty(cpGuid)) {
                        retailerQry = retailerQry + " and ParentID eq '" + cpGuid + "'";
                    }
                    if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                        retailerQry = retailerQry + " and " + Constants.PartnerMgrGUID + " eq guid'" + mStrSPGUID.toUpperCase() + "'";
                    }
                  //  retailerQry = retailerQry+"&$skip=77&$top=20";
                    retailerQry = retailerQry + qryOrder;
                }
                Log.e("Adhoc_Visit","CPDMSQueryFormed");

              /*  if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_TSO_04)) {
                    Log.e("Adhoc_Visit","IFTso_OpeningOnlineStore");

                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    boolean isOnlineStoreOpened = false;
                    if (store != null) {
                        isOnlineStoreOpened = true;
                    } else {
                        isOnlineStoreOpened = OnlineManager.openOnlineStore(context,false);
                    }
                    Log.e("Adhoc_Visit","IFTso_OpeningOnlineStore"+isOnlineStoreOpened);
                    if (isOnlineStoreOpened) {
                        Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                        retailerArrayList = OnlineManager.getRetailerList(retailerQry);
                        Log.e("Adhoc_Visit","RetailerDatapopulated");
                    }
                } else {
                    Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                    arrayListnew = OfflineManager.getRetailerList(retailerQry);
                    Log.e("Adhoc_Visit","RetailerDatapopulated");
                }*/
                Log.e("Adhoc_Visit","GettingRetailerDataBefore");
                arrayListnew = OfflineManager.getRetailerList(retailerQry);
                Log.e("Adhoc_Visit","RetailerDatapopulated");
                Log.e("Adhoc_Visit","Going to loop retailerlist");
                ArrayList<RetailerBean> retailerArrayListTemp = new ArrayList<>();
                if(arrayListnew!=null && !arrayListnew.isEmpty()){
                    for(RetailerBean retailerData : arrayListnew){
                        String cpguidQry="";
                        if(retailerData.getItemList()!=null && !retailerData.getItemList().isEmpty()){
                            String routeGuid = "";
                            for(RetailerBean retailerItemData : retailerData.getItemList()){
                                routeGuid = retailerItemData.getRouteGuid36();
                                if (cpguidQry.length() == 0)
                                    cpguidQry += " guid'" + retailerItemData.getCPGUID() + "'";
                                else
                                    cpguidQry += " or " + Constants.CPGUID + " eq guid'" + retailerItemData.getCPGUID() + "'";
                            }
                            List<RetailerBean> listRetailers = OfflineManager.getTodayBeatListRetailer(cpguidQry, Constants.mMapCPSeqNo,routeGuid);
                            retailerData.setItemList((ArrayList<RetailerBean>) listRetailers);
                            retailerArrayListTemp.add(retailerData);
                        }
                    }
                }
                Log.e("Adhoc_Visit","looped retailerlist");

                arrayListnew = retailerArrayListTemp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!stopLazyAsyncTask) {
                lazyonSearchQuery(searchText);
            }

            if (iCustomerViewPresenter != null && countPerRequest==1) {
                iCustomerViewPresenter.hideProgressDialog();
            }
            if(countPerRequest<alRSCHList.size()-1 &&!stopLazyAsyncTask){
                countPerRequest++;
                allBeatQuery = alRSCHList.get(countPerRequest).getRschGuid();
                lazyListAsynTask.cancel(true);
                lazyListAsynTask = new LazyProspectCustomerAsyncTask(countPerRequest, 0);
                lazyListAsynTask.execute();

            }
//            }

        }
    }

}
