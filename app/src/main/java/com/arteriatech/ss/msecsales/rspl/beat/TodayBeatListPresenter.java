package com.arteriatech.ss.msecsales.rspl.beat;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

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
import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by e10847 on 07-12-2017.
 */

public class TodayBeatListPresenter implements IBeatListListPresenter, UIListener {
    ArrayList<String> alAssignColl = null;
    String routeQry = null, routeName = "";
    private Activity context = null;
    private ArrayList<RetailerBean> BeatArrayList = null;
    private ArrayList<RetailerBean> alRSCHList = null;
    private ArrayList<RetailerBean> searchBeanArrayList = null;
    private IBeatListView IBeatListView = null;
    private String searchText = "";
    private long refreshTime = 0;
    private boolean isTodayBeat = false;
    private HashMap<String, String> cpGrp4Desc = null;
    private HashMap<String, String> mapInvCPList = null;
    private ArrayList<HashSet<String>> alVisitStatus = new ArrayList<>();
    private String mCPGUID = "";
    private GUID refguid =null;
    private BeatOpeningSummaryBean beatOpeningSummaryBean = null;

    public TodayBeatListPresenter(Activity context, IBeatListView IBeatListView, boolean isTodayBeat, String mCPGUID) {
        this.context = context;
        this.IBeatListView = IBeatListView;
        this.BeatArrayList = new ArrayList<>();
        this.isTodayBeat = isTodayBeat;
        this.mCPGUID = mCPGUID;
        this.searchBeanArrayList = new ArrayList<>();
    }

    @Override
    public void connectToOfflineDB() {
        requestData();
    }

    private void requestData() {
        if (IBeatListView != null) {
            IBeatListView.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
                    alRSCHList = OfflineManager.getTodayRoutes1(routeQry, mCPGUID);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (IBeatListView != null) {
                            IBeatListView.hideProgressDialog();
                            IBeatListView.displayBeatList(alRSCHList);
                        }
                    }
                });
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        IBeatListView = null;
    }

    @Override
    public void onResume() {
        if (IBeatListView != null) {
            if (refreshTime != 0)
                IBeatListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.RoutePlans));
        }
    }

    @Override
    public void onFilter() {
    }

    @Override
    public void onSearch(String searchText) {
        if (!this.searchText.equalsIgnoreCase(searchText)) {
            this.searchText = searchText;
            onSearchs(searchText);
        }
    }

    private void onSearchs(String searchText) {
        try{
        this.searchText = searchText;
        searchBeanArrayList.clear();
        boolean soSearchStatus = false;
        HashSet<String> setVisitPartially = new HashSet<>();
        HashSet<String> setVisitClosed = new HashSet<>();
        if (BeatArrayList != null) {
            try {
                if (alVisitStatus!=null&&!alVisitStatus.isEmpty()) {
                    setVisitClosed = alVisitStatus.get(0) != null ? alVisitStatus.get(0) : new HashSet<String>();
                    setVisitPartially = alVisitStatus.get(1) != null ? alVisitStatus.get(1) : new HashSet<String>();
                }
            } catch (Exception e) {
                setVisitClosed = new HashSet<>();
                setVisitPartially = new HashSet<>();
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(searchText)) {
                for (RetailerBean item : BeatArrayList) {
                    try {
                        item.setGroup3Desc(cpGrp4Desc.get(item.getCPGUID()));
                    } catch (Exception e) {
                        item.setGroup3Desc("");
                        e.printStackTrace();
                    }

                    try {
                        if(mapInvCPList.containsKey(item.getCPGUID().toUpperCase())){
                            item.setInvoiceAval("X");
                        }else{
                            item.setInvoiceAval("");
                        }

                    } catch (Exception e) {
                        item.setInvoiceAval("");
                        e.printStackTrace();
                    }

                    try {
                        if (setVisitPartially.contains(item.getCpGuidStringFormat())) {
                            item.setVisitStatus(Constants.str_02);
                        } else if (setVisitClosed.contains(item.getCpGuidStringFormat())) {
                            item.setVisitStatus(Constants.str_03);
                        } else {
                            item.setVisitStatus(Constants.str_01);
                        }
                    } catch (Exception e) {
                        item.setVisitStatus(Constants.str_01);
                        e.printStackTrace();
                    }
                    searchBeanArrayList.add(item);
                }
            } else {
                for (RetailerBean item : BeatArrayList) {

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = item.getRetailerName().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        soSearchStatus = true;
                    }
                    if (soSearchStatus) {
                        try {
                            item.setGroup3Desc(cpGrp4Desc.get(item.getCPGUID()));
                        } catch (Exception e) {
                            item.setGroup3Desc("");
                            e.printStackTrace();
                        }

                        try {
                            if(mapInvCPList.containsKey(item.getCPGUID().toUpperCase())){
                                item.setInvoiceAval("X");
                            }else{
                                item.setInvoiceAval("");
                            }

                        } catch (Exception e) {
                            item.setInvoiceAval("");
                            e.printStackTrace();
                        }

                        try {
                            if (setVisitPartially.contains(item.getCpGuidStringFormat())) {
                                item.setVisitStatus(Constants.str_02);
                            } else if (setVisitClosed.contains(item.getCpGuidStringFormat())) {
                                item.setVisitStatus(Constants.str_03);
                            } else {
                                item.setVisitStatus(Constants.str_01);
                            }
                        } catch (Exception e) {
                            item.setVisitStatus(Constants.str_01);
                            e.printStackTrace();
                        }
                        searchBeanArrayList.add(item);
                    }
                }
            }
        }
        if (IBeatListView != null) {
            IBeatListView.searchResult(searchBeanArrayList);
        }
    }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        onRefreshSOrder();
    }


    @Override
    public void getRefreshTime() {
        if (IBeatListView != null) {
//            if (refreshTime != 0)
            IBeatListView.displayRefreshTime(SyncUtils.getCollectionSyncTime(context, Constants.RoutePlans));
        }
    }

    @Override
    public void loadBeatList(RetailerBean retailerBean) {
        new GetBeatListAsyncTask(true, retailerBean).execute();
    }

    @Override
    public void getOtherBeatList() {
        otherBeatData();
    }

    private void otherBeatData() {
        if (IBeatListView != null) {
            IBeatListView.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mRouteFilterQry = "";
                    if (!TextUtils.isEmpty(mCPGUID)) {
                        mRouteFilterQry = mRouteFilterQry + " ?$filter=" + Constants.CPGUID + " eq '" + mCPGUID + "' & ApprovalStatus eq '03' & StatusID eq '01'";
                    }
                    String routeQry = Constants.RouteSchedules + mRouteFilterQry;
                    alRSCHList = OfflineManager.getRetailerListForOtherRoute1(routeQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (IBeatListView != null) {
                            IBeatListView.hideProgressDialog();
                            IBeatListView.displayBeatList(alRSCHList);
                        }
                    }
                });
            }
        }).start();
    }


    private void onRefreshSOrder() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.addAll(SyncUtils.getBeatCollection());
            alAssignColl.addAll(SyncUtils.getFOS());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                alAssignColl.add(Constants.VisitSummarySet);
            }
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (IBeatListView != null) {
                    IBeatListView.hideProgressDialog();
                    IBeatListView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Beat_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (IBeatListView != null) {
                IBeatListView.hideProgressDialog();
                IBeatListView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, context);
        if (errorBean.hasNoError()) {
            if (!Constants.isStoreClosed) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Beat_sync,refguid.toString().toUpperCase());
                    Constants.isSync = false;
                    if (!Constants.isStoreClosed) {
                        if (IBeatListView != null) {
                            IBeatListView.hideProgressDialog();
                            IBeatListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                        }


                    } else {
                        if (IBeatListView != null) {
                            IBeatListView.hideProgressDialog();
                            IBeatListView.showMessage(context.getString(R.string.msg_sync_terminated));
                        }
                    }
                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    if (IBeatListView != null) {
                        IBeatListView.hideProgressDialog();
                        IBeatListView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                    }
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (IBeatListView != null) {
                    IBeatListView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (IBeatListView != null) {
                    IBeatListView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (IBeatListView != null) {
                IBeatListView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Beat_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ConstantsUtils.startAutoSync(context,false);
            if (IBeatListView != null) {
                IBeatListView.hideProgressDialog();
                if (isTodayBeat)
                    IBeatListView.onRefreshView();
                else
                    IBeatListView.onRefreshView();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            Constants.isSync = false;
            try {
                OfflineManager.getAuthorizations(context);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(context, Constants.Sync_All);
            ConstantsUtils.startAutoSync(context,false);
            if (IBeatListView != null) {
                IBeatListView.hideProgressDialog();
                if (isTodayBeat)
                    IBeatListView.onRefreshView();
                else
                    IBeatListView.onRefreshView();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }


    /**
     * Get SOs on Background Thread
     */
    public class GetBeatListAsyncTask extends AsyncTask<Void, Void, ArrayList<RetailerBean>> {
        boolean isTodayBeat = false;
        RetailerBean retailerBean = null;

        public GetBeatListAsyncTask(boolean isTodayBeat, RetailerBean retailerBean) {
            this.isTodayBeat = isTodayBeat;
            this.retailerBean = retailerBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (IBeatListView != null)
                IBeatListView.showProgressDialog();
        }

        @Override
        protected ArrayList<RetailerBean> doInBackground(Void... params) {
            cpGrp4Desc = new HashMap<>();
           // String mStrSPGUID = Constants.getSPGUID();
           // BeatArrayList.clear();
            BeatArrayList = new ArrayList<>();
            beatOpeningSummaryBean = null;
           /* try {
                if (retailerBean != null) {
                    String routeSchopeVal = retailerBean.getRoutSchScope();
                    Constants.Route_Plan_Key = retailerBean.getRoutePlanKey();
                    routeName = retailerBean.getRouteDesc();
                    Constants.Route_Schudle_GUID = retailerBean.getRschGuid();
                    if (routeSchopeVal.equalsIgnoreCase("000001") || routeSchopeVal.equalsIgnoreCase("OtherBeat")) {
                        // Get the list of retailers from RouteSchedulePlans
                        String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'"
                                + retailerBean.getRschGuid().toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";
                        // Prepare Today's beat Retailer Query
                        String mCPGuidQry = OfflineManager.getBeatList(qryForTodaysBeat);
                        // Get Today's Retailer Details
                        if (!mCPGuidQry.equalsIgnoreCase("")) {
                            List<RetailerBean> listRetailers = OfflineManager.getTodayBeatRetailer(mCPGuidQry, Constants.mMapCPSeqNo);
                            BeatArrayList = (ArrayList<RetailerBean>) listRetailers;

                            cpGrp4Desc = OfflineManager.getCPGrp3Desc(BeatArrayList, mStrSPGUID);
                            mapInvCPList = OfflineManager.getSSInvSoldID(BeatArrayList);

                            alVisitStatus = OfflineManager.getCPVisitStatusByTodayBeatRetailers(BeatArrayList,mStrSPGUID);
                        }
                    } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                        // Get the list of retailers from RoutePlans
                    }
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }*/

            try {
                String retailerQry = "";
                String mStrSPGUID = Constants.getSPGUID();
                String stringDivision = "";
                ArrayList<String> divisionList = null;
                try {
                    divisionList = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(divisionList!=null && !divisionList.isEmpty()) {
                    for (int i = 0; i < divisionList.size();i++){
                        if(i==divisionList.size()-1) {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i)+"'";
                        }else {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i) + "' or ";
                        }
                    }
                }
                String beatGuid = "";
                beatGuid = retailerBean.getRschGuid();

                String qryOrder = " &$orderby=RouteID asc";
                if (beatGuid != null && !beatGuid.equalsIgnoreCase("")) {
                    retailerQry = Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + beatGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ("+stringDivision+")";
                } else {
                    retailerQry = Constants.CPDMSDivisions + "?$filter=StatusID eq '01' and ApprvlStatusID eq '03' and ("+stringDivision+")";
                }
                if (!TextUtils.isEmpty(mCPGUID)) {
                    retailerQry = retailerQry + " and ParentID eq '" + mCPGUID + "'";
                }
                if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                    retailerQry = retailerQry +" and "+ Constants.PartnerMgrGUID + " eq guid'" + mStrSPGUID.toUpperCase() + "'";
                }
                retailerQry = retailerQry + qryOrder;
//                retailerQry = Constants.ChannelPartners + " ?$orderby=Name asc";
//                if (!TextUtils.isEmpty(retailerQry)) {
//                    retailerQry = Constants.ChannelPartners+ "?$skip=" + skip + "&$top=" + top + "&$orderby=Name asc";
//                }
               /* if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_TSO_04)) {
                    OnlineStoreListener openListener = OnlineStoreListener.getInstance();
                    OnlineODataStore store = openListener.getStore();
                    boolean isOnlineStoreOpened = false;
                    if (store != null) {
                        isOnlineStoreOpened = true;
                    } else {
                        isOnlineStoreOpened = OnlineManager.openOnlineStore(context,false);
                    }
                    if (isOnlineStoreOpened) {
                        BeatArrayList = OnlineManager.getRetailerList(retailerQry);
                        BeatArrayList = BeatArrayList.get(0).getItemList();
                    }
                }*/ //else {
                    BeatArrayList = OfflineManager.getRetailerList(retailerQry);
                    BeatArrayList = BeatArrayList.get(0).getItemList();
              //  }
                String cpguidQry="";
                String cpguidBeatGuidQry="";
                if(BeatArrayList!=null && !BeatArrayList.isEmpty()){
                    for(RetailerBean retailerData : BeatArrayList) {
                        if (cpguidQry.length() == 0) {
                            cpguidQry += " guid'" + retailerData.getCPGUID() + "'";
                        } else {
                            cpguidQry += " or " + Constants.CPGUID + " eq guid'" + retailerData.getCPGUID() + "'";
                        }

                        if(cpguidBeatGuidQry.length()==0){
                            cpguidBeatGuidQry +="("+Constants.CPGUID + " eq '" + retailerData.getCpGuidStringFormat().toUpperCase() + "' and "+Constants.BeatGUID + " eq guid'" + retailerData.getRouteGuid36() + "')";
                        }else {
                            cpguidBeatGuidQry += " or " + "("+Constants.CPGUID + " eq '" + retailerData.getCpGuidStringFormat().toUpperCase() + "' and "+Constants.BeatGUID + " eq guid'" + retailerData.getRouteGuid36() + "')";
                        }
                    }
                }
                List<RetailerBean> listRetailers = OfflineManager.getTodayBeatRetailer(cpguidQry, Constants.mMapCPSeqNo);
                BeatArrayList = (ArrayList<RetailerBean>) listRetailers;

                cpGrp4Desc = OfflineManager.getCPGrp3Desc(BeatArrayList, mStrSPGUID,context);
                mapInvCPList = OfflineManager.getSSInvSoldID(BeatArrayList);

//                alVisitStatus = OfflineManager.getCPVisitStatusByTodayBeatRetailers(BeatArrayList,mStrSPGUID);
                alVisitStatus = OfflineManager.getCPVisitStatusByTodayBeatRetailers(cpguidBeatGuidQry,mStrSPGUID);
                if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                    String beatOpeningQry = Constants.VisitSummarySet + "?$filter=" + Constants.RschGUID + " eq guid'" + retailerBean.getRschGuid() + "'" + " and " + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "'";
                    try {
                        beatOpeningSummaryBean = OfflineManager.getBeatOpeningDetails(beatOpeningQry);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }else {
                    beatOpeningSummaryBean = new BeatOpeningSummaryBean();
                }


//                cpGrp4DescTemp = OfflineManager.getCPGrp3Desc(retailerTempArrayList,mStrSPGUID);
//                cpGrp4Desc.putAll(cpGrp4DescTemp);
//                retailerArrayList.addAll(retailerTempArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return BeatArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<RetailerBean> BeatListBeenArrayList) {
            super.onPostExecute(BeatListBeenArrayList);
            if (IBeatListView != null) {
                if (BeatListBeenArrayList != null) {
                    onSearchs(searchText);
                }
                IBeatListView.success();
                IBeatListView.beatOpeningDetails(beatOpeningSummaryBean);
                IBeatListView.hideProgressDialog();
            }
        }
    }

}
