package com.arteriatech.ss.msecsales.rspl.visitsummaryreport;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
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
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VisitSummaryReportPresentImpl implements VisitSummaryReportPresenterView, UIListener {
    private Context context;
    private VisitSummaryReportView summaryView;
    private ArrayList<VisitSummaryReportBean> visitSummaryReportBeans = new ArrayList<>();

    private String searchText="";

    public VisitSummaryReportPresentImpl(Context context, VisitSummaryReportView summaryView) {
        this.context = context;
        this.summaryView = summaryView;
    }

    @Override
    public void onStart() {
        visitSummaryReportBeans.clear();
        if(summaryView!=null){
            summaryView.showProgressDialog();
        }
        new AsyncTaskAttendanceSummary().execute();
    }

    public void onSearch(String searchText) {
        /*this.searchText=searchText;
        attendanceSummarySearchList.clear();
        if (attFinalList != null) {
            if (TextUtils.isEmpty(searchText)) {
                attendanceSummarySearchList.addAll(attFinalList);
            } else {
                for (AttendanceSummaryBean item : attFinalList) {
                    if (item.getCreatedBy().toLowerCase().contains(searchText.toLowerCase()) || item.getSPName().toLowerCase().contains(searchText.toLowerCase())) {
                        attendanceSummarySearchList.add(item);
                    }
                }
            }
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (summaryView != null) {
                    summaryView.displayList(attendanceSummarySearchList);
                }
            }
        });*/

    }

    @Override
    public void onRefresh() {
        /*if (summaryView != null) {
            summaryView.showProgressDialog();
        }*/
        onRefreshVisitSummaryList();
    }
    ArrayList<String> alAssignColl = null;
    private GUID refguid =null;
    private void onRefreshVisitSummaryList() {
        alAssignColl = new ArrayList<>();
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(context)) {
            alAssignColl.clear();
            alAssignColl.add(Constants.CPDMSDivisions);
            alAssignColl.add(Constants.RouteSchedules);

            if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                alAssignColl.add(Constants.VisitSummarySet);
            }
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (summaryView != null) {
                    summaryView.hideProgressDialog();
                    summaryView.showMessage(context.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(context,Constants.Retailers_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(context, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (summaryView != null) {
                        summaryView.hideProgressDialog();
                        summaryView.showMessage(e.getMessage());
                    }
                }
            }
        } else {
            if (summaryView != null) {
                summaryView.hideProgressDialog();
                summaryView.showMessage(context.getString(R.string.no_network_conn));
            }
        }
    }

    private ArrayList<DMSDivisionBean> distListDms=new ArrayList<>();
    public static HashMap<String, String>beatDetailMap = new HashMap<>();
    private boolean isErrorFromBackend = false;
    @Override
    public void onRequestError(int i, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(i, e, context);
        if (summaryView != null) {
            summaryView.hideProgressDialog();
        }
        if (errorBean.hasNoError()) {
            isErrorFromBackend = true;
            if (i == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (summaryView != null) {
                    summaryView.hideProgressDialog();
                    summaryView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }else if (i == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (summaryView != null) {
                    summaryView.hideProgressDialog();
                    summaryView.showMessage(context.getString(R.string.msg_error_occured_during_sync));
                }
            }

        } else if (errorBean.isStoreFailed()) {
            if (UtilConstants.isNetworkAvailable(context)) {
                Constants.isSync = true;
                if (summaryView != null) {
                    summaryView.showProgressDialog();
                }
                new RefreshAsyncTask(context, "", this).execute();
            } else {
                Constants.isSync = false;
                if (summaryView != null) {
                    summaryView.hideProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), context);
                }
            }
        } else {
            Constants.isSync = false;
            if (summaryView != null) {
                summaryView.hideProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), context);
            }
        }
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (i == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(context, alAssignColl,Constants.Retailers_sync,refguid.toString().toUpperCase());
            Constants.isSync = false;
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (summaryView != null) {
                        summaryView.hideProgressDialog();
                        onStart();
                        AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, (Activity)context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
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
            if (summaryView != null) {
                summaryView.hideProgressDialog();
                onStart();
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, (Activity)context, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }

    // get Visit Summary list report for current date
    private class AsyncTaskAttendanceSummary extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //get all BP Details from CPSPRelations
            String mStrDistQry = Constants.CPSPRelations + "?$select=CPNo,DMSDivisionID,CPGUID,CPName,DMSDivisionID,CPTypeID &$orderby=CPName asc";
            try {
                distListDms = OfflineManager.getDistributorsDms(mStrDistQry);
                if (distListDms != null && distListDms.size() > 0) {
                    Collections.sort(distListDms, new Comparator<DMSDivisionBean>() {
                        public int compare(DMSDivisionBean one, DMSDivisionBean other) {
                            return one.getDistributorName().compareTo(other.getDistributorName());
                        }
                    });
                }
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
                /*String vstQry = Constants.Visits + "?$filter=" + Constants.VisitDate + " eq datetime'"+ UtilConstants.getNewDate()+"'" + " and " + Constants.SPGUID + " eq guid'"+Constants.getSPGUID()+"' &$select=CPGUID,BeatGUID,CPNo,VisitGUID";

                ArrayList<BeatOpeningSummaryBean>  visitDetailMap = new ArrayList<>();
                try {
                    visitDetailMap = OfflineManager.getTodayDateVisitDetails(vstQry);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                ArrayList<RoutePlanBean> routePlanBeans = new ArrayList<>();
                int totalRetailers=0;
                VisitSummaryReportBean visitSummaryReportBean = null;
                BeatOpeningSummaryBean beatOpeningSummaryBean = null;
                for(int i =0;i<distListDms.size();i++){
                    visitSummaryReportBean = new VisitSummaryReportBean();
                    beatOpeningSummaryBean = new BeatOpeningSummaryBean();
                    totalRetailers=0;
                    String reschQry = Constants.RouteSchedules+"?$filter="+Constants.CPGUID+" eq '"+distListDms.get(i).getDistributorGuid()+"' and ApprovalStatus eq '03' and StatusID eq '01' &$select=RouteSchGUID,CPGUID";
                    routePlanBeans = OfflineManager.getTotalBeatList(reschQry);
                    visitSummaryReportBean.setTotalBeat(""+routePlanBeans.size());
                    visitSummaryReportBean.setSPName(""+distListDms.get(i).getDistributorName());
                    visitSummaryReportBean.setSPNo(""+distListDms.get(i).getDistributorGuid());
//                    String retailerQry = getRouteSchGuidQry(routePlanBeans,Constants.RouteGUID);
//                    String visitSmyGuidQry = getRouteSchGuidQry(routePlanBeans,Constants.RschGUID);
                    String retailerQry="";
                    String visitSmyGuidQry="";
                    for (int j=0;j<routePlanBeans.size();j++){
                        if(j==routePlanBeans.size()-1){
                            retailerQry = retailerQry + Constants.RouteGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"'";
//                            retailerQry = retailerQry + Constants.RouteSchGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"'";
                            visitSmyGuidQry = visitSmyGuidQry + Constants.RschGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"'";
                        }else {
                            retailerQry = retailerQry + Constants.RouteGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"' or ";
//                            retailerQry = retailerQry + Constants.RouteSchGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"' or ";
                            visitSmyGuidQry = visitSmyGuidQry + Constants.RschGUID + " eq guid'"+routePlanBeans.get(j).getRschGuid()+"' or ";
                        }
                    }
                    if(!TextUtils.isEmpty(retailerQry)){
                        String routePlanQry = Constants.CPDMSDivisions + "?$filter=(" +retailerQry + ") and StatusID eq '01' and ApprvlStatusID eq '03' and (" + stringDivision + ") and ParentID eq '"+distListDms.get(i).getDistributorGuid()+"'  &$select=CPGUID,RouteGUID,ParentID";
//                        String routePlanQry = Constants.RouteSchedulePlans+"?$filter=(" +retailerQry + ") &$select=VisitCPGUID";

//                        List<ODataEntity> dataEntities = OfflineManager.getEntities(routePlanQry);
                        ArrayList<RetailerBean> totalRetailersList =  OfflineManager.getTotalRetailerList(routePlanQry);
                        totalRetailers = totalRetailers+totalRetailersList.size();
//                        totalRetailers = totalRetailers+dataEntities.size();
                    }else {
                        totalRetailers = totalRetailers +0;
                    }

                    if(!TextUtils.isEmpty(visitSmyGuidQry)) {
                        if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
                            String visitSumQry = Constants.VisitSummarySet + "?$filter=" + Constants.VisitDate +
                                    " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and " + Constants.VisitDate + " le datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "' and (" + visitSmyGuidQry +") and "+Constants.ParntNo+ " eq '"+distListDms.get(i).getDistributorGuid()+"' &$select=VisitedRetailersMTD,RschGUID";

                            beatOpeningSummaryBean = OfflineManager.getVisitListDetails(visitSumQry);
                        }
                    }
                    if(beatOpeningSummaryBean!=null){
                        visitSummaryReportBean.setVisitedRetailer(""+beatOpeningSummaryBean.getVisitedRetailers());
                        visitSummaryReportBean.setVisitedBeat(""+beatOpeningSummaryBean.getVisitedBeats());
                        visitSummaryReportBean.setBeatGuidList(beatOpeningSummaryBean.getBeatList());
                    }
                    /*int tdyVisitedBeat = 0;
                    int tdyVisitedRetailer = 0;
                    ArrayList<String> beatList = visitSummaryReportBean.getBeatGuidList();

                    try {
                        for (BeatOpeningSummaryBean beatVisitList : visitDetailMap) {
                            if(beatDetailMap!=null && beatDetailMap.size()>0) {
                                String distID = beatDetailMap.get(beatVisitList.getBeatGUID().toLowerCase());
                                if(distID !=null &&distID.equalsIgnoreCase(distListDms.get(i).getDistributorGuid())) {
                                    if (beatList.size() > 0) {
                                        if (!beatList.contains(beatVisitList.getBeatGUID().toLowerCase())) {
                                            if(!TextUtils.isEmpty(beatVisitList.getVisitedRetailers())){
                                                tdyVisitedRetailer = tdyVisitedRetailer + Integer.parseInt(beatVisitList.getVisitedRetailers());
                                            }else {
                                                tdyVisitedRetailer = tdyVisitedRetailer +0;
                                            }
                                            tdyVisitedBeat = tdyVisitedBeat + 1;
                                            beatList.add(beatVisitList.getBeatGUID());
                                        } else {
                                            tdyVisitedBeat = tdyVisitedBeat + 0;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }*/
                    visitSummaryReportBean.setTotalRetailers(""+totalRetailers);
//                    visitSummaryReportBean.setTodayVisitedBeat(""+tdyVisitedBeat);
//                    visitSummaryReportBean.setTodayVisitedRetailer(""+tdyVisitedRetailer);
                    visitSummaryReportBeans.add(visitSummaryReportBean);
                }

                Collections.sort(visitSummaryReportBeans, new Comparator<VisitSummaryReportBean>() {
                    @Override
                    public int compare(VisitSummaryReportBean o1, VisitSummaryReportBean o2) {
                        return o1.getSPName().compareTo(o2.getSPName());
                    }
                });
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(summaryView!=null){
                        summaryView.hideProgressDialog();
                        summaryView.displayList(visitSummaryReportBeans);
                    }
                }
            });

        }
    }

    private String getRouteSchGuidQry(ArrayList<RoutePlanBean> routePlanBeans,String property){
        String qry="";
        beatDetailMap.clear();
        for (int i=0;i<routePlanBeans.size();i++){
            beatDetailMap.put(routePlanBeans.get(i).getRschGuid(),routePlanBeans.get(i).getCPGUID());
            if(i==routePlanBeans.size()-1){
                qry = qry + property + " eq guid'"+routePlanBeans.get(i).getRschGuid()+"'";
            }else {
                qry = qry + property + " eq guid'"+routePlanBeans.get(i).getRschGuid()+"' or ";
            }
        }
        return qry;
    }
}
