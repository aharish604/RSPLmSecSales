package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.sync.SyncHistoryDB;
import com.arteriatech.mutils.sync.SyncHistoryModel;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.ITrafficSpeedListener;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.TrafficSpeedMeasurer;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.Utils;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncSelectionActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SyncHistoryInfoFragment extends Fragment implements UIListener, View.OnClickListener, CollectionSyncInterface {

    ArrayList<String> tempCPList = new ArrayList<>();
    int updateCancelSOCount = 0;
    int cancelSoPos = 0;
    int mIntPendingCollVal = 0;
    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    String[][] invKeyValues;
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    String endPointURL = "";
    String appConnID = "";
    String syncType = "";
    boolean onlineStoreOpen = false;
    PendingCountAdapter pendingCountAdapter;
    private RecyclerView recycler_view_His, rvSyncTime;
    private int pendingCount = 0;
    private boolean mBoolIsNetWorkNotAval = false;
    private boolean mBoolIsReqResAval = false;
    private boolean isBatchReqs = false;
    private boolean tokenFlag = false;
    private int penReqCount = 0;
    private ProgressDialog syncProgDialog = null;
    private boolean dialogCancelled = false;
    private int mError = 0;
    private List<PendingCountBean> pendingCountBeanList = new ArrayList<>();
    private ArrayList<SyncHistoryModel> syncHistoryModelList = new ArrayList<>();
    private ImageView ivUploadDownload, ivSyncAll;
    private TextView tvPendingCount, tvPendingStatus;
    private NestedScrollView nestedScroll;
    private LinearLayout cvUpdatePending;
    private SimpleRecyclerViewAdapter<SyncHistoryModel> simpleUpdateHistoryAdapter;
    private boolean isClickable = false;
    private GUID refguid =null;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private int networkErrorCount=0,networkError=0;
    Thread networkThread;
    private boolean isMonitoringStopped = false;
    public SyncHistoryInfoFragment() {
        // Required empty public constructor
    }

    private static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();
        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        return size;
    }

    public static ArrayList<Object> getPendingCollList(Context mContext, boolean isFromAutoSync) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Feedbacks;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SecondarySOCreate;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.FinancialPostings;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SampleDisbursement;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CPList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.ROList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new SyncSelectionActivity.ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }

        return objectsArrayList;

    }

    public static ArrayList<String> getRefreshList(Context context) {
        ArrayList<String> alAssignColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.ChannelPartners + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ChannelPartners);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CPDMSDivisions + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CPDMSDivisions);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Visits);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CompetitorInfos);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.VisitActivities);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.SchemeCPDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Claims + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Claims);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.ClaimDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ClaimDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.ClaimDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ClaimDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CPStockItems + "?$filter= sap.islocal() ")) {
                alAssignColl.addAll(SyncUtils.getRetailerStock());
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPs + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.SchemeCPs);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.Complaints + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Complaints);
                alAssignColl.add(Constants.ComplaintDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.ExpenseDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Expenses);
                alAssignColl.add(Constants.ExpenseItemDetails);
                alAssignColl.add(Constants.ExpenseDocuments);
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            if (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) >= 25 && Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys)) {
                if (OfflineManager.getVisitStatusForCustomer(Constants.SyncHistorys + Constants.isLocalFilterQry)) {
                    alAssignColl.add(Constants.SyncHistorys);
                }
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
        return alAssignColl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sync_history_info, container, false);
        recycler_view_His = view.findViewById(R.id.recycler_view_His);
        rvSyncTime = view.findViewById(R.id.rvSyncTime);
        ivUploadDownload = view.findViewById(R.id.ivUploadDownload);
        tvPendingStatus = view.findViewById(R.id.tvPendingStatus);
        cvUpdatePending = view.findViewById(R.id.cvUpdatePending);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        ivSyncAll = view.findViewById(R.id.ivSyncAll);
        ivUploadDownload.setOnClickListener(SyncHistoryInfoFragment.this);
        ivSyncAll.setOnClickListener(SyncHistoryInfoFragment.this);
        recycler_view_His.setHasFixedSize(false);
        rvSyncTime.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        ViewCompat.setNestedScrollingEnabled(recycler_view_His, false);
        recycler_view_His.setLayoutManager(linearLayoutManager);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        ViewCompat.setNestedScrollingEnabled(rvSyncTime, false);
        rvSyncTime.setLayoutManager(linearLayoutManager);
        pendingCountAdapter = new PendingCountAdapter(pendingCountBeanList, getActivity(), this);
        recycler_view_His.setAdapter(pendingCountAdapter);
        setSyncTimeAdapter();
//        pendingCountBeanList = getRecordInfo();
        initRecyclerView();
        ConstantsUtils.focusOnView(nestedScroll);
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);

        return view;
    }

    private void setSyncTimeAdapter() {
        simpleUpdateHistoryAdapter = new SimpleRecyclerViewAdapter<SyncHistoryModel>(getActivity(), R.layout.item_history_time, new AdapterInterface<SyncHistoryModel>() {
            @Override
            public void onItemClick(SyncHistoryModel o, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new HistoryTimeVH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SyncHistoryModel o) {
                ((HistoryTimeVH) viewHolder).tvEntityName.setText(o.getCollections());
                ((HistoryTimeVH) viewHolder).tvSyncTime.setText(o.getTimeStamp());
            }
        }, null, null);
        rvSyncTime.setAdapter(simpleUpdateHistoryAdapter);
    }

    private void initRecyclerView() {
        pendingCountBeanList.clear();
        pendingCountBeanList.addAll(getRecordInfo(getActivity()));
        pendingCountAdapter.notifyDataSetChanged();

        simpleUpdateHistoryAdapter.refreshAdapter(syncHistoryModelList);
        tvPendingCount.setText(String.valueOf(pendingCount));
        if (pendingCount > 0) {
            cvUpdatePending.setVisibility(View.VISIBLE);
            tvPendingStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.RejectedColor));
        } else {
            cvUpdatePending.setVisibility(View.GONE);
            tvPendingStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ApprovedColor));
        }
    }

    private List<PendingCountBean> getRecordInfo(Context mContext) {
        pendingCount = 0;
        syncHistoryModelList.clear();
        syncHistoryModelList.addAll((new SyncHistoryDB(mContext)).getAllRecord());
        Collections.sort(syncHistoryModelList, new Comparator<SyncHistoryModel>() {
            public int compare(SyncHistoryModel one, SyncHistoryModel other) {
                return one.getCollections().compareTo(other.getCollections());
            }
        });
        PendingCountBean countBean = null;
        int count = 0;
        List<PendingCountBean> pendingCountBeans = new ArrayList();
        List<PendingCountBean> temppendingList = new ArrayList();
        List<PendingCountBean> tempNonpendingList = new ArrayList();
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        ArrayList<String> alTempList = new ArrayList<>();
        ArrayList<String> alCollectionList = null;
        for (int k = 0; k < syncHistoryModelList.size(); k++) {
            alCollectionList = new ArrayList<>();
            SyncHistoryModel historyModel = syncHistoryModelList.get(k);
            try {

                if ((historyModel.getCollections().equalsIgnoreCase(Constants.Attendances)) && !alTempList.contains(Constants.Attendances)) {
                    count = 0;
                    alTempList.add("Attendances");
                    alCollectionList.add(Constants.Attendances);
                    countBean = new PendingCountBean();

                    count = OfflineManager.getPendingCount(Constants.Attendances + "?$filter= sap.islocal() ");
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(Constants.Attendances);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(Constants.Attendances);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase("FinancialPostings") || historyModel.getCollections().equalsIgnoreCase("FinancialPostingItemDetails")) && !alTempList.contains("Collections")) {
                    count = 0;

                    alTempList.add("Collections");
                    alCollectionList.addAll(SyncUtils.getFIPCollection());
                    countBean = new PendingCountBean();
                    set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                        countBean.setCollection("Collections");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection("Collections");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase("ChannelPartners") /*|| historyModel.getCollections().equalsIgnoreCase("CPDMSDivisons")*/) && !alTempList.contains("Retailers")) {
                    count = 0;

                    alTempList.add("Retailers");
                    alCollectionList.addAll(SyncUtils.getFOS());

                    set = sharedPreferences.getStringSet(Constants.CPList, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                    }
                    if (OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ") > 0) {
                        count = count + OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ");
                        pendingCount = pendingCount + count;
                    }
                    countBean = new PendingCountBean();

                    if(count>0){
                        countBean.setCollection("Retailers");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection("Retailers");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }



                } else if ((historyModel.getCollections().equalsIgnoreCase("SSSOs") || historyModel.getCollections().equalsIgnoreCase("SSSOItemDetails")) && !alTempList.contains("Sales Order")) {
                    count = 0;

                    alTempList.add("Sales Order");
                    alCollectionList.addAll(SyncUtils.getSOsCollection());
                    countBean = new PendingCountBean();
                    set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                        countBean.setCollection("Sales Order");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection("Sales Order");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }



                } else if ((historyModel.getCollections().equalsIgnoreCase("Visits") /*|| historyModel.getCollections().equalsIgnoreCase("VisitActivities")*/) && !alTempList.contains("Visits")) {
                    count = 0;

                    alTempList.add("Visits");
                    alCollectionList.add(Constants.Visits);
                    alCollectionList.add(Constants.VisitActivities);
                    if( Arrays.asList(Constants.getDefinigReq(getActivity())).contains(Constants.VisitSummarySet)) {
                        alCollectionList.add(Constants.VisitSummarySet);
                    }
                    countBean = new PendingCountBean();

                    count = OfflineManager.getPendingCount(Constants.Visits + "?$filter= sap.islocal() ") + OfflineManager.getPendingCount(Constants.VisitActivities + "?$filter= sap.islocal() ");
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(Constants.Visits);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(Constants.Visits);
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }



                } else if (historyModel.getCollections().equalsIgnoreCase(Constants.Alerts)) {
                    count = 0;

                    alTempList.add(Constants.Alerts);
                    alCollectionList.add(Constants.Alerts);
                    countBean = new PendingCountBean();
                    countBean.setCollection(Constants.Alerts);
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if (historyModel.getCollections().equalsIgnoreCase(Constants.UserProfileAuthSet)) {
                    count = 0;

                    alTempList.add("Authorization");
                    alCollectionList.add(Constants.UserProfileAuthSet);
                    countBean = new PendingCountBean();
                    countBean.setCollection("Authorization");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.RoutePlans) || historyModel.getCollections().equalsIgnoreCase(Constants.RouteSchedules) || historyModel.getCollections().equalsIgnoreCase(Constants.RouteSchedulePlans)) && !alTempList.contains(mContext.getString(R.string.lbl_beat_paln))) {
                    count = 0;

                    alTempList.add(mContext.getString(R.string.lbl_beat_paln));
                    alCollectionList.addAll(SyncUtils.getBeatCollection());
                    countBean = new PendingCountBean();
                    countBean.setCollection(mContext.getString(R.string.lbl_beat_paln));
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.CPStockItems) || historyModel.getCollections().equalsIgnoreCase(Constants.Brands)) && !alTempList.contains(mContext.getString(R.string.db_stocks))) {
                    count = 0;

                    alTempList.add(mContext.getString(R.string.db_stocks));
                    alCollectionList.addAll(SyncUtils.getDBStockCollection());
                    countBean = new PendingCountBean();
                    countBean.setCollection(mContext.getString(R.string.db_stocks));
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Documents)) && !alTempList.contains(mContext.getString(R.string.digital_product_title))) {
                    count = 0;

                    alTempList.add(mContext.getString(R.string.digital_product_title));
                    alCollectionList.addAll(SyncUtils.getVisualAid());
                    countBean = new PendingCountBean();
                    countBean.setCollection(mContext.getString(R.string.digital_product_title));
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Expenses)) && !alTempList.contains(mContext.getString(R.string.title_expense))) {
                    count = 0;
                    set = sharedPreferences.getStringSet(Constants.Expenses, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                    }
                    count = OfflineManager.getPendingCount(Constants.ExpenseDocuments + "?$filter= sap.islocal() ") + count;

                    alTempList.add(mContext.getString(R.string.title_expense));
                    alCollectionList.addAll(SyncUtils.getExpenseListCollection());
                    countBean = new PendingCountBean();
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(mContext.getString(R.string.title_expense));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(mContext.getString(R.string.title_expense));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Feedbacks)) && !alTempList.contains(mContext.getString(R.string.title_feed_back))) {
                    count = 0;
                    alTempList.add(mContext.getString(R.string.title_feed_back));
                    alCollectionList.addAll(SyncUtils.getFeedBack());
                    countBean = new PendingCountBean();
                    set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                    }

                    if(count>0){
                        countBean.setCollection(mContext.getString(R.string.title_feed_back));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(mContext.getString(R.string.title_feed_back));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }


                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SSInvoiceItemDetails)) && !alTempList.contains(mContext.getString(R.string.sample_disbursement_title))) {
                    count = 0;

                    set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                    }

                    alTempList.add(mContext.getString(R.string.sample_disbursement_title));
                    alCollectionList.add(Constants.SSInvoiceItemDetails);
                    alCollectionList.add(Constants.SSINVOICES);
                    countBean = new PendingCountBean();

                    if(count>0){
                        countBean.setCollection(mContext.getString(R.string.sample_disbursement_title));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(mContext.getString(R.string.sample_disbursement_title));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SSINVOICES)) && !alTempList.contains(mContext.getString(R.string.title_invoice_History))) {
                    count = 0;

                    alTempList.add(mContext.getString(R.string.title_invoice_History));
                    alCollectionList.addAll(SyncUtils.getInvoice());
                    countBean = new PendingCountBean();
                    countBean.setCollection(mContext.getString(R.string.title_invoice_History));
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.MerchReviews)) && !alTempList.contains(mContext.getString(R.string.lbl_merchndising_list))) {
                    count = 0;
                    count = OfflineManager.getPendingCount(Constants.MerchReviews + "?$filter= sap.islocal() ") + count;

                    alTempList.add(mContext.getString(R.string.lbl_merchndising_list));
                    alCollectionList.addAll(SyncUtils.getMerchandising());
                    countBean = new PendingCountBean();
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection(mContext.getString(R.string.lbl_merchndising_list));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(mContext.getString(R.string.lbl_merchndising_list));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Schemes)) && !alTempList.contains(mContext.getString(R.string.title_schemes))) {
                    count = 0;

                    alTempList.add(mContext.getString(R.string.title_schemes));
                    alCollectionList.addAll(SyncUtils.getSchemes());
                    countBean = new PendingCountBean();
                    countBean.setCollection(mContext.getString(R.string.title_schemes));
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SSROs)) && !alTempList.contains(mContext.getString(R.string.title_return_order_list))) {
                    count = 0;

                    set = sharedPreferences.getStringSet(Constants.ROList, null);
                    if (set != null && !set.isEmpty()) {
                        count = set.size();
                        pendingCount = pendingCount + count;
                    }

                    alTempList.add(mContext.getString(R.string.title_return_order_list));
                    alCollectionList.addAll(SyncUtils.getROsCollection());
                    countBean = new PendingCountBean();
                    if(count>0){
                        countBean.setCollection(mContext.getString(R.string.title_return_order_list));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    }else {
                        countBean.setCollection(mContext.getString(R.string.title_return_order_list));
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }

                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.Targets)) && !alTempList.contains("Targets")) {
                    count = 0;

                    alTempList.add("Targets");
                    alCollectionList.addAll(SyncUtils.getTargets());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Targets");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                } else if ((historyModel.getCollections().equalsIgnoreCase(Constants.ValueHelps)) && !alTempList.contains("Value Helps")) {
                    count = 0;

                    alTempList.add("Value Helps");
                    alCollectionList.addAll(SyncUtils.getValueHelps());
                    countBean = new PendingCountBean();
                    countBean.setCollection("Value Helps");
                    countBean.setCount(count);
                    countBean.setSyncTime(historyModel.getTimeStamp());
                    countBean.setAlCollectionList(alCollectionList);
                    tempNonpendingList.add(countBean);
                }else if ((historyModel.getCollections().equalsIgnoreCase(Constants.SyncHistorys)) && !alTempList.contains(Constants.SyncHistorys)) {
                    count = 0;
                    alTempList.add("SyncHistorys");
                    alCollectionList.add(Constants.SyncHistorys);
                    countBean = new PendingCountBean();
                    count = OfflineManager.getPendingCount(Constants.SyncHistorys + "?$filter= sap.islocal() ");
                    if (count > 0) {
                        pendingCount = pendingCount + count;
                        countBean.setCollection("Sync Historys");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        temppendingList.add(countBean);
                    } else {
                        countBean.setCollection("Sync Historys");
                        countBean.setCount(count);
                        countBean.setSyncTime(historyModel.getTimeStamp());
                        countBean.setAlCollectionList(alCollectionList);
                        tempNonpendingList.add(countBean);
                    }
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(tempNonpendingList, new Comparator<PendingCountBean>() {
            public int compare(PendingCountBean one, PendingCountBean other) {
                return one.getCollection().compareTo(other.getCollection());
            }
        });
        Collections.sort(temppendingList, new Comparator<PendingCountBean>() {
            public int compare(PendingCountBean one, PendingCountBean other) {
                return one.getCollection().compareTo(other.getCollection());
            }
        });

        pendingCountBeans.addAll(temppendingList);
        pendingCountBeans.addAll(tempNonpendingList);

        return pendingCountBeans;
    }
    private void startNetworkMonitoring(){
        mTrafficSpeedMeasurer.startMeasuring();
        isMonitoringStopped = true;
        checkNetwork(getContext(), new OnNetworkInfoListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onNetworkFailureListener(boolean isFailed) {
                if (isFailed) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(SyncSelectionActivity.this, "Network dropped / unable to connect.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        },false,0);

    }
    @Override
    public  void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    public  void onResume() {
        super.onResume();
        isClickable=false;
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);

    }

    @Override
    public  void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }


    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                    String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                    if(upStream<=0 || downStream<=0)
                        networkErrorCount++;
                    else
                        networkErrorCount=0;

                    if((upStream!=0 && upStream<1)|| (downStream!=0 && downStream<1))
                        networkError++;
                    else
                        networkError=0;

                    if(networkErrorCount>=3){
                        networkErrorCount=0;
                        isMonitoringStopped = false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    onBackPressed();
                                }
                            }
                        });
                    }else if(networkError>=3){
                        networkError=0;
                        isMonitoringStopped = false;
                        mTrafficSpeedMeasurer.stopMeasuring();
                        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                        UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to low network bandwidth", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    onBackPressed();
                                }
                            }
                        });
                    }

                    Log.d("Network_Bandwidth","Values"+upStreamSpeed+"--"+downStreamSpeed);
                }
            });
        }
    };

    private static boolean isActiveNetwork(Context context){
        return isConnected(context);
    }
    private static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        boolean isMobile = false;
        boolean isConnected = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        if (isConnected) {
            if (isWiFi) {
                return isConnectedToThisServer();
            }
            if (isMobile) {
                return isConnectedToThisServer();
            }
        } else {
            return false;
        }
        return false;
    }
    private static boolean isConnectedToThisServer() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public interface OnNetworkInfoListener{
        void onNetworkFailureListener(boolean isFailed);
    }
    private static boolean isNetworkStopped;
    public  void checkNetwork(final Context context, final OnNetworkInfoListener networkInfoListener, boolean isInterupted, final int delayInSec){
        if (!isInterupted) {
            isNetworkStopped=false;
            networkThread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    check(context, networkInfoListener,delayInSec);
                }
            });
            networkThread.start();
        }else{
            isNetworkStopped =true;
        }
    }
    private  void check(Context context,OnNetworkInfoListener networkInfoListener, int delayInSec){
        if (!isNetworkStopped) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isError = isActiveNetwork(context);
            if (!isError) {
                networkErrorCount++;
                if(networkErrorCount>=3){
                    networkError=0;
                    isMonitoringStopped = false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            if (b) {
                                onBackPressed();
                            }
                        }
                    });
                }

                Log.e("CHECKING NETWORK", "NETWORK ERROR");

                if (networkInfoListener != null && isMonitoringStopped) {
                    networkInfoListener.onNetworkFailureListener(true);
                    check(context, networkInfoListener,delayInSec);
                }
            } else {
                if (networkInfoListener != null && isMonitoringStopped) {
                    Log.e("CHECKING NETWORK", "NETWORK ACTIVE");
                    networkInfoListener.onNetworkFailureListener(false);
                    check(context, networkInfoListener, delayInSec);
                }
            }
        }
    }
    private void onUpdateSync() {
        setEmpty();
        Constants.Entity_Set.clear();
        Constants.AL_ERROR_MSG.clear();
        tempCPList.clear();
        mBoolIsNetWorkNotAval = false;
        isBatchReqs = false;
        mBoolIsReqResAval = true;
        updateCancelSOCount = 0;
        cancelSoPos = 0;
        try {
            try {
                removeFromDatavault(getActivity());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            checkPendingReqIsAval();

            if (OfflineManager.offlineStore!=null && OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
                if (getActivity()!=null) {
                    UtilConstants.showAlert(getString(R.string.no_req_to_update_sap), getActivity());
                }
            } else {
                if (Constants.iSAutoSync || Constants.isBackGroundSync) {
                    if (getActivity()!=null) {
                        if(Constants.iSAutoSync) {
                            UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), getActivity());
                        }else if(Constants.isBackGroundSync){
                            UtilConstants.showAlert(getString(R.string.alert_backgrounf_sync_is_progress), getActivity());
                        }
                    }
                }else {
                    if (mIntPendingCollVal > 0) {

                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                            alAssignColl.add(Constants.ConfigTypsetTypeValues);

                        if (getActivity()!=null) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                startNetworkMonitoring();
                                Constants.isSync = true;
                                refguid = GUID.newRandom();
                                SyncUtils.updatingSyncStartTime(getActivity(),Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
                                onPostOnlineData();
                            } else {
                                UtilConstants.showAlert(getString(R.string.no_network_conn), getActivity());
                            }
                        }
                    } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        if (getActivity()!=null) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                startNetworkMonitoring();
                                refguid = GUID.newRandom();
                            	SyncUtils.updatingSyncStartTime(getActivity(),Constants.UpLoad,Constants.StartSync,refguid.toString().toUpperCase());
				                onPostOfflineData();
                            } else {
                                UtilConstants.showAlert(getString(R.string.no_network_conn), getActivity());
                            }
                        }
                    }
                }
            }
        } catch (ODataException e) {
            e.printStackTrace();
        }
    }

    private void checkPendingReqIsAval() {
        try {
            mIntPendingCollVal = 0;
            invKeyValues = null;
            ArrayList<Object> objectArrayList = null;
            if (getActivity()!=null) {
                objectArrayList = getPendingCollList(getActivity(), false);
            }
            if (objectArrayList!=null&&!objectArrayList.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayList.get(0);
                invKeyValues = (String[][]) objectArrayList.get(1);
            }

            penReqCount = 0;


            alAssignColl.clear();
            alFlushColl.clear();
            concatCollectionStr = "";
            concatFlushCollStr = "";
            ArrayList<String> allAssignColl = null;
            if (getActivity()!=null) {
                allAssignColl = getRefreshList(getActivity());
            }
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEmpty() {
        if (getActivity()!=null) {
            HashSet companySet = new HashSet();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.NOT_POSTED_RETAILERS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.duplicateCPList, companySet);
            editor.apply();
        }
    }

    private void onPostOnlineData() {
        try {
            new PostingDataValutData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        isClickable=false;
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, getActivity());
        try {
            if (errorBean.hasNoError()) {

                if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                    if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                        tempCPList.add(invKeyValues[penReqCount][0]);
                        if (getActivity()!=null) {
                            Constants.removeFromSharKey(invKeyValues[penReqCount][0], errorBean.getErrorMsg(), getActivity(), false);
                        }
                    }
                }

                mError++;
                penReqCount++;
                mBoolIsReqResAval = true;


                if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                    try {
                        if (getActivity()!=null) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                                    try {
                                        new AsyncPostOfflineData().execute();
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                } else {
                                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                            alAssignColl.add(Constants.ConfigTypsetTypeValues);

                                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                        new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                                    } else {
                                        Constants.isSync = false;
                                        closingProgressDialog();
                                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                                    }
                                }
                            } else {
                                Constants.isSync = false;
                                closingProgressDialog();
                                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                            }
                        }


                    } catch (ODataException e3) {
                        e3.printStackTrace();
                    }
                }
                if (operation == Operation.OfflineFlush.getValue()) {
                    if (getActivity()!=null) {
                        if (UtilConstants.isNetworkAvailable(getActivity())) {
                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                            concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                            LogManager.writeLogInfo(concatCollectionStr + " refresh started");
                            new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                        } else {
                            Constants.isSync = false;
                            closingProgressDialog();
                            UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                        }
                    }

                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    LogManager.writeLogError("Error : " + exception.getMessage());
                    Constants.isSync = false;
                    String mErrorMsg = "";
                    if (Constants.AL_ERROR_MSG.size() > 0) {
                        mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                    }
                    closingProgressDialog();
                    if (getActivity()!=null) {
                        if (mErrorMsg.equalsIgnoreCase("")) {
                            if(errorBean.getErrorMsg().contains("invalid authentication")||errorBean.getErrorMsg().contains("HTTP Status 401 ? Unauthorized")){
                                try {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                                    String loginUser=sharedPreferences.getString("username","");
                                    String login_pwd=sharedPreferences.getString("password","");
                                    UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                        @Override
                                        public void passwordStatus(final JSONObject jsonObject) {

                                            if(!getActivity().isFinishing()) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Constants.passwordStatusErrorMessage(getActivity(), jsonObject, loginUser);
                                                    }
                                                });
                                            }

                                        }
                                    });
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
//                                Constants.customAlertDialogWithScrollError(getActivity(), errorBean.getErrorMsg(), getActivity());
                            }else {
                                UtilConstants.showAlert(errorBean.getErrorMsg(), getActivity());
                            }
                        } else {
                            if(errorBean.getErrorMsg().contains("invalid authentication")||errorBean.getErrorMsg().contains("HTTP Status 401 ? Unauthorized")){
                                try {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                                    String loginUser=sharedPreferences.getString("username","");
                                    String login_pwd=sharedPreferences.getString("password","");
                                    UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                                        @Override
                                        public void passwordStatus(final JSONObject jsonObject) {

                                            if(!getActivity().isFinishing()) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Constants.passwordStatusErrorMessage(getActivity(), jsonObject, loginUser);
                                                    }
                                                });
                                            }

                                        }
                                    });
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
//                                Constants.customAlertDialogWithScrollError(getActivity(), errorBean.getErrorMsg(), getActivity());
                            }else {
                                Constants.customAlertDialogWithScrollError(getActivity(), mErrorMsg, getActivity());
                            }

                        }
                    }

                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    closingProgressDialog();
                    if (getActivity()!=null) {
                        UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                                getActivity());
                    }
                }
            } else {
                mBoolIsReqResAval = true;
                mBoolIsNetWorkNotAval = true;
                Constants.isSync = false;
                if (errorBean.isStoreFailed()) {
                    OfflineManager.offlineStore = null;
                    OfflineManager.options = null;
                    try {
                        if (!OfflineManager.isOfflineStoreOpen()) {
                            closingProgressDialog();
                            try {
                                new OpenOfflineStore().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            closingProgressDialog();
                            if (getActivity()!=null) {
                                Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                            }
                        }
                    } catch (Exception e) {
                        closingProgressDialog();
                        if (getActivity()!=null) {
                            Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                        }
                        e.printStackTrace();
                    }
                } else {
                    closingProgressDialog();
                    if (getActivity()!=null) {
                        Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                    }
                }


            }
        } catch (Throwable e) {
            mBoolIsReqResAval = true;
            mBoolIsNetWorkNotAval = true;
            Constants.isSync = false;
            closingProgressDialog();
            if (getActivity()!=null) {
                Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
            }
        }

    }

    public void removeFromDatavault(Context context){
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove SO orders from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove SO orders from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreate, deviceNo, false);
                        Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreateTemp, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove SO orders from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreate, deviceNo, false);
                            Constants.removeDataValtFromSharedPref(context, Constants.SecondarySOCreateTemp, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove Feedbacks from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.Feedbacks, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove Feedbacks from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.Feedbacks, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove FinancialPostings from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.FinancialPostings, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove FinancialPostings from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.FinancialPostings, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove SampleDisbursement from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.SampleDisbursement, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove SampleDisbursement from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.SampleDisbursement, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove CPList from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.CPList, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove CPList from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.CPList, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove ROList from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.ROList, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove ROList from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.ROList, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                String store = null, deviceNo = "";
                deviceNo = itr.next().toString();
                try {
                    store = ConstantsUtils.getFromDataVault(deviceNo, context);
//                    LogManager.writeLogInfo("Remove Feedbacks from Datavault");
                    if (store == null) {
                        LogManager.writeLogInfo("Remove Expenses from Datavault key value null");
                        Constants.removeDataValtFromSharedPref(context, Constants.Expenses, deviceNo, false);
                    }else {
                        if (TextUtils.isEmpty(store)) {
                            LogManager.writeLogInfo("Remove Expenses from Datavault key value empty");
                            Constants.removeDataValtFromSharedPref(context, Constants.Expenses, deviceNo, false);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        isClickable=false;
        try {
            if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
                mBoolIsReqResAval = true;

                /*
                 if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FinancialPostings)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.FinancialPostings, invKeyValues[penReqCount][0]);
                }
                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
                } else */
               /* if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SecondarySOCreate)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SecondarySOCreate, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Feedbacks)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Feedbacks, invKeyValues[penReqCount][0]);
                }*/ /*else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.InvList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.ROList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.ROList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
                }else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Expenses, invKeyValues[penReqCount][0]);
                }else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CPList, invKeyValues[penReqCount][0]);
                }*/
                if (getActivity()!=null) {
                    Constants.removeDataValtFromSharedPref(getActivity(), invKeyValues[penReqCount][1], invKeyValues[penReqCount][0], false);
                }
                ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0], "",getActivity());
                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SecondarySOCreate)) {
                    Constants.removeDataValtFromSharedPref(getActivity(),Constants.SecondarySOCreateTemp, invKeyValues[penReqCount][0]+"_temp", false);
                    ConstantsUtils.storeInDataVault(invKeyValues[penReqCount][0]+"_temp", "",getActivity());
                }

                penReqCount++;
            }
            if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        if (getActivity()!=null) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                try {
                                    new AsyncPostOfflineData().execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Constants.isSync = false;
                                closingProgressDialog();
                                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                            }
                        }
                    } else {
                        if (getActivity()!=null) {
                            if (UtilConstants.isNetworkAvailable(getActivity())) {
                                if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                    alAssignColl.add(Constants.ConfigTypsetTypeValues);

                                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                            } else {
                                Constants.isSync = false;
                                closingProgressDialog();
                                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                            }
                        }
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } else if (operation == Operation.OfflineFlush.getValue()) {
                if (getActivity()!=null) {
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                            alAssignColl.add(Constants.ConfigTypsetTypeValues);
                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                        new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                    } else {
                        Constants.isSync = false;
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                    }
                }


            } else if (operation == Operation.OfflineRefresh.getValue()) {

                if (alAssignColl.contains(Constants.UserProfileAuthSet)) {
                    try {
                        OfflineManager.getAuthorizations(getActivity());
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }

                if (alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
                    Constants.alTodayBeatRet.clear();
    //                Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
    //                Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
                }
                if (alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners)) {
                    try {
                        if (getActivity()!=null) {
                            Constants.setBirthdayListToDataValut(getActivity());
                            Constants.setBirthDayRecordsToDataValut(getActivity());
                            Constants.setAppointmentNotification(getActivity());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
    //            if(alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
    //                Constants.loadingTodayAchived(SyncSelectionActivity.this,Constants.alTodayBeatRet);
    //            }

                if (alAssignColl.contains(Constants.MerchReviews)) {
                    Constants.deleteDeviceMerchansisingFromDataVault(getActivity());
                }
                if (alAssignColl.contains(Constants.Alerts)) {
                    Constants.setAlertsRecordsToDataValut(getActivity());
                }
                // Staring MustSell Code Snippet

    //            if(tempCPList.size()>0){
    //                OfflineManager.getCPListFromDataValt(SyncSelectionActivity.this,tempCPList);
    //            }

                if (tempCPList.size() > 0) {
                    HashSet companySet = new HashSet(tempCPList);
                    if (getActivity()!=null) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.NOT_POSTED_RETAILERS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet(Constants.duplicateCPList, companySet);
                        editor.apply();
                    }
                } else {
                    if (getActivity()!=null) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.NOT_POSTED_RETAILERS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
                String mErrorMsg = "";
                if (Constants.AL_ERROR_MSG.size() > 0) {
                    mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                }


                Constants.isSync = false;
                final String finalMErrorMsg = mErrorMsg;
                Constants.updateLastSyncTimeToTable(getActivity(), alAssignColl, syncType, new RefreshListInterface() {
                    @Override
                    public void refreshList() {
                        closingProgressDialog();
                        if (mError == 0) {
                            ConstantsUtils.startAutoSync(getActivity(), false);
                            UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {
                                    AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, getActivity(), BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
                                }
                            });
                            isMonitoringStopped=false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                        } else {
                            if (finalMErrorMsg.equalsIgnoreCase("")) {
                                UtilConstants.showAlert(getString(R.string.error_occured_during_post), getActivity());
                            } else {
                                Constants.customAlertDialogWithScrollError(getActivity(), finalMErrorMsg, getActivity());
                            }
                        }
                    }
                },refguid.toString().toUpperCase());

            } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
    //                Constants.ReIntilizeStore =false;
                Constants.isSync = false;
                try {
                    OfflineManager.getAuthorizations(getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    Constants.setSyncTime(getActivity(), Constants.Sync_All);
                }
                setStoreOpenUI();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setStoreOpenUI() {
        closingProgressDialog();
        UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUploadDownload:
                if (UtilConstants.isNetworkAvailable(getActivity())) {
                    if (!Constants.isPullDownSync&&!Constants.isBackGroundSync&&!Constants.iSAutoSync) {
                        if (!isClickable) {
                            isClickable = true;
                            syncType = Constants.UpLoad;
                            onUpdateSync();
                        }
                    }else{
                        if (Constants.iSAutoSync){
                            showAlert(getString(R.string.alert_auto_sync_is_progress));
                        }else{
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        }
                    }
                }else{
                    showAlert(getString(R.string.no_network_conn));
                }
                break;
            case R.id.ivSyncAll:
                onAllSync();
                break;
        }
    }

    private void onBackPressed() {
        getActivity().finish();
    }

    private void closingProgressDialog() {
        try {
            syncProgDialog.dismiss();
            syncProgDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        initRecyclerView();
        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();*/
    }

    private void readValuesFromDataVault() {
        if (mIntPendingCollVal > 0) {
            for (int k = 0; k < invKeyValues.length; k++) {

                while (!mBoolIsReqResAval) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mBoolIsNetWorkNotAval) {
                    break;
                }
                mBoolIsReqResAval = false;

                String store = null;
                try {
                    store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(),getActivity());
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                //Fetch object from data vault
                try {

                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    dbHeadTable = new Hashtable();
                    arrtable = new ArrayList<>();

                    if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedbacks)) {
                        // preparing entity pending
                        if (!alAssignColl.contains(Constants.Feedbacks)) {
                            alAssignColl.add(Constants.Feedbacks);
                            alAssignColl.add(Constants.FeedbackItemDetails);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getFeedbackJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);

                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Feedbacks, SyncHistoryInfoFragment.this,  getActivity());
                    } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        if (!alAssignColl.contains(Constants.SSSOs)) {
                            alAssignColl.add(Constants.SSSoItemDetails);
                            alAssignColl.add(Constants.SSSOs);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getSOHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSSOs,  SyncHistoryInfoFragment.this, getActivity());
                    } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.FinancialPostings)) {

                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        if (!alAssignColl.contains(Constants.FinancialPostings)) {
                            alAssignColl.add(Constants.FinancialPostings);
                            alAssignColl.add(Constants.FinancialPostingItemDetails);
                        }
                        if (!alAssignColl.contains(Constants.SSOutstandingInvoices)) {
                            alAssignColl.add(Constants.SSOutstandingInvoiceItemDetails);
                            alAssignColl.add(Constants.SSOutstandingInvoices);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getCollHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.FinancialPostings, SyncHistoryInfoFragment.this, getActivity());
                    }else if(fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)){
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getSSInvoiceJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSINVOICES, SyncHistoryInfoFragment.this, getActivity());
                    }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                        // preparing entity pending
                        if (!alAssignColl.contains(Constants.ChannelPartners)) {
                            alAssignColl.add(Constants.ChannelPartners);
                            alAssignColl.add(Constants.CPDMSDivisions);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getCPHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.ChannelPartners, SyncHistoryInfoFragment.this, getActivity());
                    } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                        if (!alAssignColl.contains(Constants.SSROs)) {
                            alAssignColl.add(Constants.SSROItemDetails);
                            alAssignColl.add(Constants.SSROs);
                        }
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getROHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSROs, SyncHistoryInfoFragment.this, getActivity());
                    } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                        if (!alAssignColl.contains(Constants.Expenses)) {
                            alAssignColl.add(Constants.ExpenseItemDetails);
                            alAssignColl.add(Constants.Expenses);
                        }
                        Constants.REPEATABLE_REQUEST_ID="";
                        JSONObject dbHeadTable = Constants.getExpenseHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                        OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Expenses, SyncHistoryInfoFragment.this, getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mBoolIsReqResAval = true;
                }

            }
        }
    }

    private void onPostOfflineData() {
        Constants.isSync = true;
        try {
            new AsyncPostOfflineData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayProgressDialog() {
        try {
            syncProgDialog
                    .show();
            syncProgDialog
                    .setCancelable(true);
            syncProgDialog
                    .setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogCancelled = false;
    }

    @Override
    public void onUploadDownload(boolean isUpload, PendingCountBean countBean, String syncType) {
        this.syncType = "";
        alAssignColl.clear();
        Constants.AL_ERROR_MSG.clear();
        Constants.Entity_Set.clear();
        Constants.isSync = true;
        dialogCancelled = false;
        this.syncType = syncType;
        Constants.isStoreClosed = false;
        mError = 0;
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                if (Constants.iSAutoSync){
                    showAlert(getString(R.string.alert_auto_sync_is_progress));
                }else{
                    showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                }
            }else{
                if(!isClickable) {
                    isClickable = true;
                    alAssignColl.addAll(countBean.getAlCollectionList());
                    if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                        alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
                    syncProgDialog.setMessage(getString(R.string.app_loading));
                    syncProgDialog.setCancelable(false);
                    syncProgDialog.show();
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(getActivity(),Constants.DownLoad,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(getActivity(), concatCollectionStr, this).execute();
                }
            }
        } else {
            ConstantsUtils.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isClickable = false;
                    dialog.cancel();
                }
            });
        }
    }



    private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.addAll(SyncUtils.getAllSyncValue(getActivity()));
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
    }

    private void onAllSync() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            onSyncAll();
        } else {
            UtilConstants.showAlert(getActivity().getString(R.string.no_network_conn), getActivity());
        }
    }

    private void onSyncAll() {
        try {
            Constants.AL_ERROR_MSG.clear();
            Constants.Entity_Set.clear();
            Constants.isSync = true;
            dialogCancelled = false;
            Constants.isStoreClosed = false;
            mError = 0;
            new LoadingData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
//            Constants.printLogInfo("check store is opened or not");
            if (!OfflineManager.isOfflineStoreOpen()) {
//                Constants.printLogInfo("check store is failed");
                try {
                    OfflineManager.openOfflineStore(getActivity(), SyncHistoryInfoFragment.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                Constants.isStoreClosed = false;
                assignCollToArrayList();
//                Constants.printLogInfo("check store is opened");
                try {
                    OfflineManager.refreshStoreSync(getActivity().getApplicationContext(), SyncHistoryInfoFragment.this, Constants.ALL, "");
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public class PostingDataValutData extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (syncProgDialog == null) {
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
           /* }else{
                syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            }*/
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                tokenFlag = false;

                Constants.x_csrf_token = "";
                Constants.ErrorCode = 0;
                Constants.ErrorNo = 0;
                Constants.ErrorName = "";
                Constants.IsOnlineStoreFailed = false;
                Constants.IsOnlineStoreStarted = false;
                mBoolIsReqResAval = true;
                LogManager.writeLogInfo("Posting data to backend started");
                try {
                    onlineStoreOpen = OnlineManager.openOnlineStore(getActivity(), true);
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                    Constants.printLog("Get online store ended with error(1) " + e.getMessage());
                }
                if (onlineStoreOpen) {
                    readValuesFromDataVault();
                } else {
                    return onlineStoreOpen;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return onlineStoreOpen;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                closingProgressDialog();
                syncProgDialog = null;

                if (!onlineStoreOpen) {
                    if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity());

                    } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code) {
                            String errorMessage = "Authorization failed,Your Password is expired. To change password go to Setting and click on Change Password";
                            UtilConstants.showAlert(errorMessage, getActivity());
                        } else {
                            UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), getActivity());
                        }

                    } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity());
                    } else {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), getActivity());
                    }
                } else if (!tokenFlag) {
                    Constants.displayMsgINet(Constants.ErrorNo_Get_Token, getActivity());
                } else if (Constants.x_csrf_token == null || Constants.x_csrf_token.equalsIgnoreCase("")) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, -2 + ""), getActivity());
                }
            }
        }
    }

    public class AsyncPostOfflineData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (syncProgDialog == null) {
                syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            }
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    displayProgressDialog();

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            dialogCancelled = false;
                            OfflineManager.flushQueuedRequests(SyncHistoryInfoFragment.this, concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*private List<SyncHistoryModel> getRecordInfo() {
        List<SyncHistoryModel> syncHistoryModelList = (new SyncHistoryDB(this.getActivity())).getAllRecord();
        List<SyncHistoryModel> duplicateSyncHistoryModelList = new ArrayList();
        ArrayList<String> alEntity = new ArrayList<>();
        for (int k=0; k<syncHistoryModelList.size(); k++){
            SyncHistoryModel historyModel = syncHistoryModelList.get(k);

            if((historyModel.getCollections().equalsIgnoreCase("RoutePlans") || historyModel.getCollections().equalsIgnoreCase("RouteSchedulePlans") || historyModel.getCollections().equalsIgnoreCase("RouteSchedules")) && !alEntity.contains("Beat")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Beat");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Beat");
            }else if((historyModel.getCollections().equalsIgnoreCase("ChannelPartners") || historyModel.getCollections().equalsIgnoreCase("CPDMSDivisons") ) && !alEntity.contains("Retailers")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Retailers");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Retailers");

            }else if((historyModel.getCollections().equalsIgnoreCase("SSSOs")  || historyModel.getCollections().equalsIgnoreCase("SSSOItemDetails")) && !alEntity.contains("Sales Order")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Sales Order");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Sales Order");
            }else if((historyModel.getCollections().equalsIgnoreCase("FinancialPostings") || historyModel.getCollections().equalsIgnoreCase("FinancialPostingItemDetails")) && !alEntity.contains("Collections")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Collections");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Collections");
            }else if((historyModel.getCollections().equalsIgnoreCase("Visits") || historyModel.getCollections().equalsIgnoreCase("VisitActivities")) && !alEntity.contains("Visits")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Visits");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Visits");
            }else if((historyModel.getCollections().equalsIgnoreCase("Attendances")) && !alEntity.contains("Attendances")){
                SyncHistoryModel model = new SyncHistoryModel();
                model.setCollections("Attendances");
                model.setTimeStamp(historyModel.getTimeStamp());
                duplicateSyncHistoryModelList.add(model);
                alEntity.add("Attendances");
            }
        }

        Collections.sort(duplicateSyncHistoryModelList, new Comparator<SyncHistoryModel>() {
            @Override
            public int compare(SyncHistoryModel historyModel, SyncHistoryModel historyMode2) {
                return historyModel.getCollections().compareTo(historyMode2.getCollections());
            }
        } );
        return duplicateSyncHistoryModelList;
    }*/
    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.app_loading));
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            OfflineManager.openOfflineStore(getActivity(), SyncHistoryInfoFragment.this);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private void showAlert(String message){
        ConstantsUtils.showAlert(message, getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }
}
