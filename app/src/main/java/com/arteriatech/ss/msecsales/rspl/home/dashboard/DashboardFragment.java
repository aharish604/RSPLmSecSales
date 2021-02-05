package com.arteriatech.ss.msecsales.rspl.home.dashboard;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.attendance.CreateAttendanceActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.home.MainActivity;
import com.arteriatech.ss.msecsales.rspl.interfaces.CustomDialogCallBackWithCode;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RemarkReasonBean;
import com.arteriatech.ss.msecsales.rspl.notvistedretailers.NotVisitedRetailersActivity;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.retailerapproval.RetailerApprovalListActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.ui.TextProgressBar;
import com.github.mikephil.charting.charts.PieChart;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        UIListener, DashboardView , DashboardView.DaySummResponse,AdapterViewInterface<VisitedBeatBean>{


    private CardView cvAttendance, cv_sales_view, cv_tlsd_view, cv_bill_cut_view, cv_eco_view;
    private String mStrPreviousDate = "", mStrAttendanceId = "";
    private boolean mBooleanStartFalg = false, mBooleanEndFlag = false, mBooleanCompleteFlag = false;
    public static boolean mProgressFalg = false;
    private String mStrSPGUID = "";
    private ProgressDialog pdLoadDialog = null;
    private ProgressBar pbAttendance = null;
    private String mStrPopUpText = "";
    private String mStrOtherRetailerGuid = "";
    private ODataGuid mStrVisitId = null;
    private String[][] delList = null;
    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false, mBooleanDayResetDialog = false;
    private boolean wantToCloseDialog = false;
    private String mStrVisitEndRemarks = "";
    private String mStrReasonCode = "";
    private String mStrReasonDesc = "";
    private ImageView iv_day_start_action;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout llDaySummaryMain;
    private LinearLayout ll_beat_list;
    private RecyclerView rv_beat;
    private RecyclerView rv_brand;
    private boolean refreshingFlag = false;
    SimpleRecyclerViewTypeAdapter<VisitedBeatBean> recyclerViewAdapterBeat = null;
    SimpleRecyclerViewTypeAdapter<BrandProductiveBean> recyclerViewAdapterBrand = null;
    DashboardPresenterImpl presenter;
    ArrayList<MyTargetsBean> customerBeanBeenFilterArrayList;
    ArrayList<MyTargetsBean> mapTargetVal = null;
    ArrayList<VisitedBeatBean> mapBeatList = new ArrayList<>();
    ArrayList<BrandProductiveBean> mapBrandList = new ArrayList<>();
    LinearLayout llFlowLayout;
    LinearLayout ll_brand_list;


    TextView no_record_found, tv_no_of_outlets, tv_days_percentage, tv_order_val,tv_your_order_val,
            tv_tar_sal_val, tv_ach_sal_val, tv_tar_tlsd_val, tv_ach_tlsd_val,
            tv_tar_bill_val, tv_ach_bill_val, tv_tar_eco_val, tv_ach_eco_val, tv_day_start_text,tv_tc_vs_pc;
    PieChart pieChart_sales_val, pieChart_tlsd,/*pieChart_outlets,*/
            pieChart_billcut, pieChart_eco;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sha;
    private TextView tvAlertCount;
    private TextView tv_tc,tv_NPC,tv_pc;
    private CardView cv_visit;
    private ArrayList<RemarkReasonBean> reasonCodedesc = new ArrayList<>();
    private ProgressBar pbRetAplCount;
    private CardView cvRetApprovalView;
    private CardView cv_beat,cv_your_order_val;
    private TextView tvRetApprovalCount;
    private TextProgressBar pbSalesPer, pbBillCut, pbTLSDPer, pbECOPer;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("ToSeePerformance","onCreateView");
        return inflater.inflate(R.layout.fragment_dashboard, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ToSeePerformance","onViewCreated");
        cvAttendance = (CardView) view.findViewById(R.id.cv_attendance);
        pbAttendance = (ProgressBar) view.findViewById(R.id.pbAttendance);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_targets);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setDistanceToTriggerSync(ConstantsUtils.SWIPE_REFRESH_DISTANCE);
        tv_day_start_text = (TextView) view.findViewById(R.id.tv_day_start_text);
        iv_day_start_action = (ImageView) view.findViewById(R.id.iv_day_start_action);
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        cvAttendance.setOnClickListener(this);
        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            cvAttendance.setVisibility(View.VISIBLE);
        } else {
            cvAttendance.setVisibility(View.GONE);
        }

         try{
             sha=getActivity().getSharedPreferences(Constants.LOGPREFS_NAME, 0);
             if (sha.getBoolean("writeDBGLog", false)) {
                 Constants.writeDebug = sha.getBoolean("writeDBGLog", false);
             }

         }catch (Exception ex){

         }


        llFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        ll_brand_list = (LinearLayout) view.findViewById(R.id.ll_brand_list);
        tv_no_of_outlets = (TextView) view.findViewById(R.id.tv_no_of_outlets);
        tv_tc_vs_pc = (TextView) view.findViewById(R.id.tv_tc_vs_pc);
        tv_order_val = (TextView) view.findViewById(R.id.tv_order_val);
        tv_your_order_val = (TextView) view.findViewById(R.id.tv_your_order_val);
        cv_sales_view = (CardView) view.findViewById(R.id.cv_sales_view);
        cv_tlsd_view = (CardView) view.findViewById(R.id.cv_tlsd_view);
        cv_bill_cut_view = (CardView) view.findViewById(R.id.cv_bill_cut_view);
        cv_eco_view = (CardView) view.findViewById(R.id.cv_eco_view);

        cvRetApprovalView = (CardView) view.findViewById(R.id.cv_retailer_approval_view);
        cv_beat = (CardView) view.findViewById(R.id.cv_beat);
        cv_your_order_val = (CardView) view.findViewById(R.id.cv_your_order_val);
        cvRetApprovalView.setOnClickListener(this);
        tvRetApprovalCount = (TextView) view.findViewById(R.id.tv_mtp_approval_count);
        pbRetAplCount = (ProgressBar) view.findViewById(R.id.pbMTPAplCount);

        cv_visit = (CardView) view.findViewById(R.id.cv_visit);
        cv_visit.setOnClickListener(this);

        pbSalesPer = (TextProgressBar) view.findViewById(R.id.pbSalesPer);
        pbBillCut = (TextProgressBar) view.findViewById(R.id.pbBillCut);
        pbTLSDPer = (TextProgressBar) view.findViewById(R.id.pbTLSDPer);
        pbECOPer = (TextProgressBar) view.findViewById(R.id.pbECOPer);

        tv_day_start_text = (TextView) view.findViewById(R.id.tv_day_start_text);
        iv_day_start_action = (ImageView) view.findViewById(R.id.iv_day_start_action);

        tv_tar_sal_val = (TextView) view.findViewById(R.id.tv_tar_sal_val);
        tv_ach_sal_val = (TextView) view.findViewById(R.id.tv_ach_sal_val);
        tv_tc = (TextView) view.findViewById(R.id.tv_tc);
        tv_NPC = (TextView) view.findViewById(R.id.tv_NPC);
        tv_pc = (TextView) view.findViewById(R.id.tv_pc);

        tv_tar_tlsd_val = (TextView) view.findViewById(R.id.tv_tar_tlsd_val);
        tv_ach_tlsd_val = (TextView) view.findViewById(R.id.tv_ach_tlsd_val);

        tv_tar_bill_val = (TextView) view.findViewById(R.id.tv_tar_bill_val);
        tv_ach_bill_val = (TextView) view.findViewById(R.id.tv_ach_bill_val);

        tv_tar_eco_val = (TextView) view.findViewById(R.id.tv_tar_eco_val);
        tv_ach_eco_val = (TextView) view.findViewById(R.id.tv_ach_eco_val);


        tv_days_percentage = (TextView) view.findViewById(R.id.tv_days_percentage);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);

        pieChart_sales_val = (PieChart) view.findViewById(R.id.pieChart_sales_val);
        pieChart_tlsd = (PieChart) view.findViewById(R.id.pieChart_tlsd);
        pieChart_billcut = (PieChart) view.findViewById(R.id.pieChart_bill_cut);
        pieChart_eco = (PieChart) view.findViewById(R.id.pieChart_eco);
        ConstantsUtils.setProgressColor(getActivity(), swipeRefresh);
        llDaySummaryMain = (LinearLayout) view.findViewById(R.id.llDaySummaryMain);
        ll_beat_list = (LinearLayout) view.findViewById(R.id.ll_beat_list);
        rv_beat = (RecyclerView) view.findViewById(R.id.rv_beat);
        rv_brand = (RecyclerView) view.findViewById(R.id.rv_brand);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d("ToSeePerformance","initializeObjects");

        initializeObjects(getActivity());
        initializeClickListeners();
        initializeRecyclerVeiwBeat();
        initializeRecyclerVeiwBrand();
    }

    private void initializeRecyclerVeiwBrand() {
        rv_brand.setHasFixedSize(true);
        rv_brand.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewAdapterBrand = new SimpleRecyclerViewTypeAdapter<>(getActivity(), R.layout.ll_brand_list, new AdapterViewInterface<BrandProductiveBean>() {
            @Override
            public void onItemClick(BrandProductiveBean brandProductiveBean, View view, int i) {

            }

            @Override
            public int getItemViewType(int i, ArrayList arrayList) {
                return 0;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new BrandProductiveVH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, BrandProductiveBean brandProductiveBean, ArrayList<BrandProductiveBean> arrayList) {
                try {
                    if(brandProductiveBean.getBrandID().equalsIgnoreCase("01")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.cake);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 42;
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setScaleType(ImageView.ScaleType.FIT_XY);
                    }else if(brandProductiveBean.getBrandID().equalsIgnoreCase("02")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.powder);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 42;
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setScaleType(ImageView.ScaleType.FIT_XY);
                    }else if(brandProductiveBean.getBrandID().equalsIgnoreCase("03")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.venu_cream_bar);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 32;
                    }else if(brandProductiveBean.getBrandID().equalsIgnoreCase("04")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.ghadi_machine_washash);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 42;
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setScaleType(ImageView.ScaleType.FIT_XY);
                    } else if(brandProductiveBean.getBrandID().equalsIgnoreCase("05")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.glori_soap);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 42;
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setScaleType(ImageView.ScaleType.FIT_XY);
                    }else if(brandProductiveBean.getBrandID().equalsIgnoreCase("06")){
                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setImageResource(R.drawable.handwash);
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.getLayoutParams().height = 42;
//                        ((BrandProductiveVH)viewHolder).ivBrandStatus.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (brandProductiveBean.isShowProgress()){
                        ((BrandProductiveVH) viewHolder).pbCount.setVisibility(View.VISIBLE);
                        ((BrandProductiveVH) viewHolder).tv_nug_value.setVisibility(View.GONE);
                    }else {
                        ((BrandProductiveVH) viewHolder).pbCount.setVisibility(View.GONE);
                        ((BrandProductiveVH) viewHolder).tv_nug_value.setVisibility(View.VISIBLE);
                        //                        ((BrandProductiveVH) viewHolder).tv_nug_value.setText(brandProductiveBean.getQuantity());
                        if(Integer.parseInt(brandProductiveBean.getPcValue())!=0 && Integer.parseInt(brandProductiveBean.getBagValue())!=0) {
                            ((BrandProductiveVH) viewHolder).tv_nug_value.setText(brandProductiveBean.getBagValue() + " BAG / "+brandProductiveBean.getPcValue() + " PC" );
                        }else if(Integer.parseInt(brandProductiveBean.getPcValue())!=0 && Integer.parseInt(brandProductiveBean.getCarValue())!=0) {
                            ((BrandProductiveVH) viewHolder).tv_nug_value.setText( brandProductiveBean.getCarValue() + " CAR / "+brandProductiveBean.getPcValue() + " PC");
                        }else if(Integer.parseInt(brandProductiveBean.getPcValue())!=0) {
                            ((BrandProductiveVH) viewHolder).tv_nug_value.setText(brandProductiveBean.getPcValue() + " PC");
                        }else if(Integer.parseInt(brandProductiveBean.getBagValue())!=0) {
                            ((BrandProductiveVH) viewHolder).tv_nug_value.setText(brandProductiveBean.getBagValue() + " BAG");
                        }else if(Integer.parseInt(brandProductiveBean.getCarValue())!=0) {
                            ((BrandProductiveVH) viewHolder).tv_nug_value.setText(brandProductiveBean.getCarValue() + " CAR");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    //                    ((BrandProductiveVH)viewHolder).tv_productive.setText(brandProductiveBean.getMaterialItemDesc());
                    ((BrandProductiveVH)viewHolder).tv_productive.setText(brandProductiveBean.getRetailerCount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*try {
                    ((BrandProductiveVH)viewHolder).tv_nug_value.setText(brandProductiveBean.getQuantity());
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                try {
                    ((BrandProductiveVH)viewHolder).tv_total_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(brandProductiveBean.getToatlPrice()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, rv_beat, no_record_found);
        rv_brand.setLayoutManager(manager);
        rv_brand.setAdapter(recyclerViewAdapterBrand);
    }

    private void initializeRecyclerVeiwBeat() {
        rv_beat.setHasFixedSize(true);
        rv_beat.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewAdapterBeat = new SimpleRecyclerViewTypeAdapter<>(getActivity(), R.layout.ll_beat_list, this, rv_brand, no_record_found);
        rv_beat.setLayoutManager(manager);
        rv_beat.setAdapter(recyclerViewAdapterBeat);
    }

    /*@Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
        setAttendenceUI();
        setDaySummary();
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_attendance:
                onDaystart();
                break;
            case R.id.cv_visit:
                MainActivity.callBeatPlan(getContext());
                break;
            case R.id.cv_retailer_approval_view:
                Intent intent=new Intent(getActivity(), RetailerApprovalListActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void onDaystart() {
        setAttendenceUI();
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

        LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                        onDayStartOrEnd();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                    }
                }
            }
        });
    }

    private void onDayStartOrEnd() {
        if (mBooleanEndFlag) {
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                getNonVisitedDealers(UtilConstants.getNewDate());
            } else {
                getNonVisitedDealers(mStrPreviousDate);
            }
        } else {
            attendanceFunctionality(iv_day_start_action, tv_day_start_text);
        }
    }

    private void attendanceFunctionality(final ImageView ivIcon, final TextView tvIconName) {
        if (mBooleanEndFlag) {
            String message;
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                //For Today
                mStrOtherRetailerGuid = "";
                final String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01' and "+Constants.CPTypeID+" eq '"+Constants.str_02+"' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                final String[][] otherRetDetails = {new String[2]};
                Thread thread =null;
                try {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                otherRetDetails[0] = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                                ConstantsUtils.printErrorLog(e.getMessage());
                            }
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (thread!=null&&thread.isAlive()){
                        thread.interrupt();
                    }
                }

                final String[] finalOtherRetDetails = otherRetDetails[0];

                mStrOtherRetailerGuid = finalOtherRetDetails[1];
                if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                         /*
                                         display alert dialog for visit started but not ended retailer
                                          */
                    AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);

                    alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0][0]))
                            .setCancelable(false)
                            .setPositiveButton(
                                    R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();

                                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                            Constants.getLocation(getActivity(), new LocationInterface() {
                                                @Override
                                                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                    closeProgressDialog();
                                                    if (status) {
                                                        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                            boolean isVisitActivities = false;
                                                            try {
                                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                            } catch (OfflineODataStoreException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                            if (isVisitActivities) {
                                                                onSaveVisitClose();
                                                            } else {
                                                                wantToCloseDialog = false;
                                                                onAlertDialogForVisitDayEndRemarks();
                                                            }
                                                        } else {
                                                            ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    });
                    alertDialogVisitEnd.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }

                            });

                    alertDialogVisitEnd.show();
                } else {
//                    delList = checkNonVisitedRetailers(UtilConstants.getNewDate());

                    String alrtConfMsg = "", alrtNegtiveMsg = "";

                    if (delList == null) {


                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.yes);
                        alrtNegtiveMsg = getString(R.string.no);

                    } else {
                        message = getString(R.string.msg_confirm_day_end);
                        alrtConfMsg = getString(R.string.ok);
                        alrtNegtiveMsg = getString(R.string.cancel);
                    }

                                         /*
                                           display alert dialog for Day end or non visited retailers
                                         */

                    AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                            getActivity(), R.style.MyTheme);
                    alertDialogDayEnd.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(
                                    alrtConfMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                            if (delList == null) {
                                                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                Constants.getLocation(getActivity(), new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        closeProgressDialog();
                                                        if (status) {
                                                            if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                mBooleanEndFlag = false;
                                                                tvIconName
                                                                        .setText(R.string.tv_complete);
                                                                mBooleanStartFalg = true;
                                                                mBooleanCompleteFlag = true;
                                                                mStrPopUpText = getString(R.string.msg_update_end);
                                                                mBooleanDayStartDialog = false;
                                                                mBooleanDayEndDialog = true;
                                                                mBooleanDayResetDialog = false;
                                                                onSaveClose();
                                                            } else {
                                                                ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                    Intent intentNavEndRemarksScreen = new Intent(getActivity(), NotVisitedRetailersActivity.class);
                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.Today);
                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, UtilConstants.getNewDate());
                                                    startActivity(intentNavEndRemarksScreen);
                                                } else {
                                                    ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                }
                                            }
                                        }

                                    })
                            .setNegativeButton(alrtNegtiveMsg,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                        }

                                    });
                    alertDialogDayEnd.show();
                }

            } else {
                message = getString(R.string.msg_previous_day_end);

                                    /*
                                     *
                                      *  display alert dialog for previous day is not ended.
                                     */
                AlertDialog.Builder alertDialogPreviousDay = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogPreviousDay.setMessage(
                        message)
                        .setCancelable(false)
                        .setPositiveButton(
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();

                                        mStrOtherRetailerGuid = "";

                                        final String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                                                "and StartDate eq datetime'" + mStrPreviousDate + "'and " + Constants.StatusID + " eq '01' and "+Constants.CPTypeID+" eq '"+Constants.str_02+"' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                                        final String[][] otherRetDetails = {new String[2]};
                                        Thread thread =null;
                                        try {
                                            thread=new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        otherRetDetails[0] = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                                                    } catch (OfflineODataStoreException e) {
                                                        e.printStackTrace();
                                                        ConstantsUtils.printErrorLog(e.getMessage());
                                                    }
                                                }
                                            });
                                            thread.start();
                                            try {
                                                thread.join();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            if (thread!=null&&thread.isAlive()){
                                                thread.interrupt();
                                            }
                                        }

                                        final String[] finalOtherRetDetails = otherRetDetails[0];

                                        mStrOtherRetailerGuid = finalOtherRetDetails[1];
                                        if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                                                    /*
                                                                     * display alert dialog for visit started but not ended retailer
                                                                     */
                                            AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);

                                            alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0][0]))
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            R.string.yes,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                                        @Override
                                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                            closeProgressDialog();
                                                                            if (status) {
                                                                                if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                                    boolean isVisitActivities = false;
                                                                                    try {
                                                                                        isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                                                    } catch (OfflineODataStoreException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                                                    if (isVisitActivities) {
                                                                                        onSaveVisitClose();
                                                                                    } else {
                                                                                        wantToCloseDialog = false;
                                                                                        onAlertDialogForVisitDayEndRemarks();
                                                                                    }
                                                                                } else {
                                                                                    ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                                }
                                                                            }
                                                                        }
                                                                    });


                                                                }
                                                            });
                                            alertDialogVisitEnd.setNegativeButton(R.string.no,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                            dialog.cancel();
                                                        }

                                                    });

                                            alertDialogVisitEnd.show();
                                        } else {
                                            String msg = "";
//                                            delList = checkNonVisitedRetailers(mStrPreviousDate);

                                            String alrtConfMsg = "", alrtNegtiveMsg = "";

                                            if (delList == null) {
                                                msg = getString(R.string.msg_confirm_day_end);
                                                alrtConfMsg = getString(R.string.yes);
                                                alrtNegtiveMsg = getString(R.string.no);
                                            } else {
                                                msg = getString(R.string.msg_remarks_pending_visit);
                                                alrtConfMsg = getString(R.string.ok);
                                                alrtNegtiveMsg = getString(R.string.cancel);
                                            }

                                                                     /*

                                                                      display alert dialog for Day end  and non visited retailers
                                                                       */
                                            AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                                                    getActivity(), R.style.MyTheme);
                                            alertDialogDayEnd.setMessage(msg)
                                                    .setCancelable(false)
                                                    .setPositiveButton(
                                                            alrtConfMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                    if (delList == null) {
                                                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                                                            @Override
                                                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                                closeProgressDialog();
                                                                                if (status) {
                                                                                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                                        mStrPopUpText = getString(R.string.msg_update_previous_day_end);
                                                                                        mBooleanDayStartDialog = false;
                                                                                        mBooleanDayEndDialog = true;
                                                                                        mBooleanDayResetDialog = false;

                                                                                        onSaveClose();
                                                                                        mBooleanEndFlag = false;
                                                                                        tvIconName
                                                                                                .setText(R.string.tv_start);
                                                                                        mBooleanStartFalg = false;
                                                                                        mBooleanCompleteFlag = false;
                                                                                        ivIcon.setImageResource(R.drawable.stop);
                                                                                    } else {
                                                                                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                                    }
                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                                            Intent intentNavEndRemarksScreen = new Intent(getActivity(), NotVisitedRetailersActivity.class);
                                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.PreviousDay);
                                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, mStrPreviousDate);
                                                                            startActivity(intentNavEndRemarksScreen);
                                                                        } else {
                                                                            ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                                        }
                                                                    }
                                                                }

                                                            })
                                                    .setNegativeButton(alrtNegtiveMsg,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int id) {
                                                                    dialog.cancel();
                                                                }

                                                            });
                                            alertDialogDayEnd.show();
                                        }
                                    }

                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogPreviousDay.show();
            }


        } else {

            if (!mBooleanStartFalg) {

                Intent intentNavPrevScreen = new Intent(getActivity(), CreateAttendanceActivity.class);
                startActivity(intentNavPrevScreen);

            }

            if (mBooleanCompleteFlag) {

                                    /*
                                    display alert dialog for Day end reset
                                     */
                AlertDialog.Builder alertDialogDayEndReset = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                alertDialogDayEndReset.setMessage(
                        getString(R.string.msg_reset_day_end))
                        .setCancelable(false)
                        .setPositiveButton(
                                //commenting
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                closeProgressDialog();
                                                if (status) {
                                                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                                                        ivIcon.setImageResource(R.drawable.stop);
                                                        tvIconName
                                                                .setText(R.string.tv_end);
                                                        mBooleanEndFlag = true;
                                                        mBooleanCompleteFlag = false;
                                                        mBooleanStartFalg = true;


                                                        mBooleanDayStartDialog = false;
                                                        mBooleanDayEndDialog = false;
                                                        mBooleanDayResetDialog = true;
                                                        onCloseUpdate();
                                                    } else {
                                                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }

                                });
                alertDialogDayEndReset.show();

            }

        }
    }

    /*resets day*/
    private void onCloseUpdate() {
        mStrPopUpText = getString(R.string.msg_resetting_day_end);
        try {
            new ResettingDate().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

    }

    /*Ends day*/
    private void onSaveClose() {
        try {
            new ClosingDate().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }

    }

    private void onAlertDialogForVisitDayEndRemarks() {
        mStrReasonCode = "";
        mStrReasonDesc = "";
        reasonCodedesc=Constants.getReasonValues(reasonCodedesc);
        ConstantsUtils.showVisitRemarksDialog(getActivity(), new CustomDialogCallBackWithCode() {
            @Override
            public void cancelDialogCallBack(boolean userClicked, String ids, String description,String reasonCode,String reasonDesc) {
                mStrVisitEndRemarks = description;
                mStrReasonCode = reasonCode;
                mStrReasonDesc = reasonDesc;
                if (userClicked) {
                    if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
                        wantToCloseDialog = false;
                        onSaveVisitClose();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(getActivity());
                    }
                } else {

                }
            }
        }, getString(R.string.alert_plz_enter_remarks),reasonCodedesc);

        /*










        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        int MAX_LENGTH = 255;

        final EditText etVisitEndRemarks = new EditText(getActivity());

        if (wantToCloseDialog) {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);

        } else {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
        }

        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (wantToCloseDialog) {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                    wantToCloseDialog = false;
                } else {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");
        etVisitEndRemarks.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etVisitEndRemarks.setLayoutParams(lp);
        alertDialogVisitEndRemarks.setView(etVisitEndRemarks);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                        if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                            wantToCloseDialog = true;
                            onAlertDialogForVisitDayEndRemarks();
                        } else {
                            wantToCloseDialog = false;
                            onSaveVisitClose();
                        }
                    }
                });

        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });
        AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();*/

    }
    private void onSaveVisitClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    private void getNonVisitedDealers(String strDate) {
        try {
            new GetNonVistedRetailers().execute(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[][] checkNonVisitedRetailers(String selctedDate) {
        String retList[][] = null;
        try {

            String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + selctedDate + "'";
            String mGetRouteQry = OfflineManager.getRouteQry(routeQry);

            if (!mGetRouteQry.equalsIgnoreCase("")) {
                mGetRouteQry = Constants.RouteSchedulePlans + "?$filter=" + mGetRouteQry;
                retList = OfflineManager.getNotVisitedRetailerList(mGetRouteQry, UtilConstants.getNewDate());
            } else {
                retList = null;
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
        return retList;
    }
    private void closeProgressDialog() {
        try {
            if (pdLoadDialog != null)
                pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private void setAttendenceUI() {
        if (TextUtils.isEmpty(mStrSPGUID)) {
            mStrSPGUID = Constants.getSPGUID();
        }
        if (!TextUtils.isEmpty(mStrSPGUID)) {
//            cvAttendance.setVisibility(View.VISIBLE);
            Constants.MapEntityVal.clear();
            mStrPreviousDate = "";
            mStrAttendanceId = "";
            mBooleanStartFalg = false;
            mBooleanEndFlag = false;
            mBooleanCompleteFlag = false;
            Thread thread=null;
            final String prvDayQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate ne datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            try {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                            ConstantsUtils.printErrorLog(e.getMessage());
                        }
                        if (!mStrAttendanceId.equalsIgnoreCase("")) {
                            mStrPreviousDate = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                        } else {
                            mStrPreviousDate = "";
                        }
                        String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                        try {
                            mStrAttendanceId = OfflineManager.getAttendance(dayEndqry);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                            ConstantsUtils.printErrorLog(e.getMessage());
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }finally {
                if (thread!=null&&thread.isAlive()){
                    thread.interrupt();
                    Log.e("T","INTERUPTED");
                }else{
                    Log.e("T","ENDED");
                }
            }

            final String[] startDateStr = new String[1];
            final String[] endDateStr = new String[1];
            if (getActivity()!=null) {
                if (Constants.MapEntityVal.isEmpty()) {
                    final String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                    try {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                                } catch (OfflineODataStoreException e) {
                                    e.printStackTrace();
                                    ConstantsUtils.printErrorLog(e.getMessage());
                                }

                                startDateStr[0] = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                                endDateStr[0] = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.EndDate));

                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        if (thread!=null&&thread.isAlive()){
                            thread.interrupt();
                            Log.e("T","INTERUPTED");
                        }else{
                            Log.e("T","ENDED");
                        }
                    }

                    if (startDateStr[0].equalsIgnoreCase(UtilConstants.getNewDate()) && endDateStr[0].equalsIgnoreCase(UtilConstants.getNewDate())) {
                        iv_day_start_action.setImageResource(R.drawable.stop);
                        tv_day_start_text.setText(getActivity().getString(R.string.tv_complete));
                        mBooleanCompleteFlag = true;
                        mBooleanEndFlag = false;
                        mBooleanStartFalg = true;
                    } else {
                        iv_day_start_action.setImageResource(R.drawable.start);
                        tv_day_start_text.setText(getActivity().getString(R.string.menu_start));
                    }

                } else {
                    if (Constants.MapEntityVal.get(Constants.EndDate) == null) {
                        iv_day_start_action.setImageResource(R.drawable.stop);
                        tv_day_start_text.setText(getActivity().getString(R.string.tv_end));
                        mBooleanEndFlag = true;
                    } else {
                        iv_day_start_action.setImageResource(R.drawable.start);
                        tv_day_start_text.setText(getActivity().getString(R.string.menu_start));
                    }
                }
            }
        } else {
//            cvAttendance.setVisibility(View.GONE);
        }

    }

    private void setUI() {
        setAttendenceUI();
        setDaySummary();

    }

    private void setDaySummary(){
        if (mapTargetVal != null && mapTargetVal.size() > 0) {
            HashSet<String> mKpiNames = null;
            try {
                mKpiNames = mapTargetVal.get(mapTargetVal.size() - 1).getKpiNames();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mKpiNames != null && mKpiNames.size() > 0) {

                String salesKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ";

                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(salesKPI)) {
                        cv_sales_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_sales_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                String tlsdKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' and " + Constants.CalculationBase + " eq '04'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(tlsdKPI)) {
                        cv_tlsd_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_tlsd_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String billCutKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '07' and " + Constants.CalculationBase + " eq '05'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(billCutKPI)) {
                        cv_bill_cut_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_bill_cut_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String ecoKPI = Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '04' and " + Constants.CalculationBase + " eq '05'";
                try {
                    if (OfflineManager.getVisitActivityStatusForVisit(ecoKPI)) {
                        cv_eco_view.setVisibility(View.VISIBLE);
                    } else {
                        cv_eco_view.setVisibility(View.GONE);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else {
                cv_sales_view.setVisibility(View.GONE);
                cv_tlsd_view.setVisibility(View.GONE);
                cv_bill_cut_view.setVisibility(View.GONE);
                cv_eco_view.setVisibility(View.GONE);
            }
            for (MyTargetsBean myTargetsBean : mapTargetVal) {
                try {
                    if (myTargetsBean.getKPIName().equalsIgnoreCase("Visits")) {
                        tv_no_of_outlets.setText(myTargetsBean.getMTDA() + " / " + myTargetsBean.getMonthTarget());
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),
//                            pieChart_outlets,getActivity(),6,myTargetsBean.getMTDA()+"/"+myTargetsBean.getMonthTarget());
                        tv_order_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getBTD()));
                        tv_your_order_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getYourOrderValue()));
                        tv_tc_vs_pc.setText(myTargetsBean.getTcVSPC());
                    } else if (myTargetsBean.getKPIName().contains("Sales")) {
                        tv_tar_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMonthTarget()));
                        tv_ach_sal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMTDA()));
                        pbSalesPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                        pbSalesPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                        Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_sales_val, getActivity(), 8,
//                                UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                    } else if (myTargetsBean.getKPIName().contains("ECO")) {
                        tv_tar_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                        tv_ach_eco_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                        pbECOPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                        pbECOPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                        Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_eco, getActivity(), 8,
//                                UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                    } else if (myTargetsBean.getKPIName().contains("Bill")) {
                        tv_tar_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                        tv_ach_bill_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                        pbBillCut.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                        pbBillCut.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                        Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_billcut, getActivity(), 8,
//                                UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                    } else if (myTargetsBean.getKPIName().contains("TLSD")) {
                        tv_tar_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMonthTarget()));
                        tv_ach_tlsd_val.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getMTDA()));
                        pbTLSDPer.setProgress(Integer.parseInt(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage())));
                        pbTLSDPer.setText(UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
//                        Constants.displayPieChart(myTargetsBean.getAchivedPercentage(), pieChart_tlsd, getActivity(), 8,
//                                UtilConstants.trimQtyDecimalPlace(myTargetsBean.getAchivedPercentage()) + "%");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        ErrorBean errorBean = Constants.getErrorCode(operation, e, getContext());
        if (errorBean.hasNoError()) {
            Toast.makeText(getActivity(), getString(R.string.err_odata_unexpected, e.getMessage()),
                    Toast.LENGTH_LONG).show();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
            else if (mBooleanDayResetDialog) {
                mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);
            }
            if (mStrPopUpText.equalsIgnoreCase("")) {
                try {
                    mStrPopUpText = errorBean.getErrorMsg();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (operation == Operation.Create.getValue()) {
                closeProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                closeProgressDialog();

                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                closeProgressDialog();

                displayPopUpMsg();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                try {

                    closeProgressDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure), getActivity());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            closeProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.CreateOperation).equalsIgnoreCase("4")) {
                closeProgressDialog();

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(DashboardFragment.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.UpdateOperation).equalsIgnoreCase("4")) {

                closeProgressDialog();
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                try {
                    if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                        mStrPopUpText = getString(R.string.visit_ended);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(DashboardFragment.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.ReadOperation).equalsIgnoreCase("4")) {
                closeProgressDialog();

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                else if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                else if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                else if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {

                    String allCollection = "";
//                    if (mBooleanDayStartDialog) {
//                        allCollection = Constants.Attendances + "," + Constants.SPStockItems + "," + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SFINVOICES + "," + Constants.SSInvoiceItemDetails
//                                + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
//                                + "," + Constants.FinancialPostingItemDetails
//                                + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
//                    } else {
                    allCollection = Constants.Attendances;
//                    }

                    new RefreshAsyncTask(getActivity(), allCollection, this).execute();
//                    OfflineManager.refreshRequests(getActivity(), allCollection, DaySummaryFragment.this);
                }
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            closeProgressDialog();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.dialog_day_started);
            else if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.dialog_day_ended);
            else if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.dialog_day_reset);

            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue()) {
//            new NotificationSetClass(getContext());
            try {
                OfflineManager.getAuthorizations(getActivity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {

                closeProgressDialog();
//                onRefresh();
                //setAppointmentNotification();
            } catch (Exception e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }

        }

    }

    private void displayPopUpMsg() {
        UtilConstants.showAlert(mStrPopUpText, getActivity());
        setAttendenceUI();
    }

    @Override
    public void hideAttendancePB() {
        pbAttendance.setVisibility(ProgressBar.GONE);
        iv_day_start_action.setVisibility(View.VISIBLE);
        setAttendenceUI();
    }

    @Override
    public void showAttendancePB() {
        iv_day_start_action.setVisibility(View.GONE);
        pbAttendance.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void initializeUI(Context context) {

    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects(Context context) {
        mapTargetVal = new ArrayList<>();
        customerBeanBeenFilterArrayList = new ArrayList<>();
        presenter = new DashboardPresenterImpl(getActivity(), this, getActivity());
        presenter.onStart();
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {

    }

    private boolean isUnauthorized = false;
    @Override
    public void showMessage(String message) {

        if(message.contains("HTTP Status 401 ? Unauthorized") || message.contains("invalid authentication")){

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog();
                    }
                });
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME,0);
                String loginUser=sharedPreferences.getString("username","");
                String login_pwd=sharedPreferences.getString("password","");
                UtilConstants.getPasswordStatus(Configuration.IDPURL, loginUser, login_pwd, Configuration.APP_ID, new UtilConstants.PasswordStatusCallback() {
                    @Override
                    public void passwordStatus(final JSONObject jsonObject) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                                if(!isUnauthorized) {
                                    isUnauthorized = true;
                                    Constants.passwordStatusErrorMessage(getActivity(), jsonObject, loginUser);
                                }
                            }
                        });
                    }
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                });
                e.printStackTrace();
            }
//           Constants.customAlertDialogWithScrollError(getContext(),message,getActivity());
        }else {
            ConstantsUtils.displayLongToast(getContext(), message);
        }
    }

    @Override
    public void dialogMessage(String message, String msgType) {

    }

    @Override
    public void success(ArrayList success) {
        this.mapTargetVal = success;
//        recyclerViewAdapter.refreshAdapter(success);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void error(String message) {
        swipeRefresh.setRefreshing(false);

    }


    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList<MyTargetsBean> customerBeanArrayList) {
//        recyclerViewAdapter.refreshAdapter(customerBeanArrayList);
    }

    @Override
    public void onResume() {
        super.onResume();
        isUnauthorized = false;
    }

    @Override
    public void onRefresh() {
        try {
            refreshingFlag = true;
            isUnauthorized = false;
            presenter.requestRET();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFilterDate(String filterType) {

    }


    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {

    }

    @Override
    public void TargetSync() {
        try {
            sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            sha=getActivity().getSharedPreferences(Constants.LOGPREFS_NAME, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            presenter.onStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOfflineDataFirst(){
        presenter.onLoadOfflinedata();

    }

    @Override
    public void displayRefreshTime(String refreshTime) {

    }

    @Override
    public void displayList(ArrayList<MyTargetsBean> alTargets, ArrayList<VisitedBeatBean> alVisitList,ArrayList<BrandProductiveBean> alBrandList) {
        llDaySummaryMain.setVisibility(View.VISIBLE);
        mapBeatList.clear();
        mapBrandList.clear();
        mapTargetVal.addAll(alTargets);
        mapBeatList.addAll(alVisitList);
        mapBrandList.addAll(alBrandList);

        if (sharedPreferences.getString(Constants.isStartCloseEnabled, "").equalsIgnoreCase(Constants.isStartCloseTcode)) {
            cvAttendance.setVisibility(View.VISIBLE);
        } else {
            cvAttendance.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isBeatEnabled, "").equalsIgnoreCase(Constants.isBeatTcode)) {
            ll_beat_list.setVisibility(View.VISIBLE);
        } else {
            ll_beat_list.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isTCPCEnabled, "").equalsIgnoreCase(Constants.isTCPCTcode)) {
            cv_beat.setVisibility(View.VISIBLE);
            cv_your_order_val.setVisibility(View.VISIBLE);
        } else {
            cv_beat.setVisibility(View.GONE);
            cv_your_order_val.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isBrandEnabled, "").equalsIgnoreCase(Constants.isBrandTcode)) {
            ll_brand_list.setVisibility(View.VISIBLE);
        } else {
            ll_brand_list.setVisibility(View.GONE);
        }

        if (sharedPreferences.getString(Constants.isRetailerApprovalKey, "").equalsIgnoreCase(Constants.isRetailerApprovalTcode)) {
            cvRetApprovalView.setVisibility(View.VISIBLE);
        } else {
            cvRetApprovalView.setVisibility(View.GONE);
        }
        setBeatList();
        setUI();

    }

    @Override
    public void displayBrandList(ArrayList<BrandProductiveBean> alBrandList) {
        mapBrandList.addAll(alBrandList);

        if (mapBrandList != null && mapBrandList.size() > 0) {
            for (BrandProductiveBean showProgress : mapBrandList) {
                showProgress.setShowProgress(false);
            }
            if (recyclerViewAdapterBrand != null)
                ll_brand_list.setVisibility(View.VISIBLE);
            recyclerViewAdapterBrand.refreshAdapter(mapBrandList);
        } else {
            ll_brand_list.setVisibility(View.GONE);
        }
    }


    private void setBeatList() {
        for (MyTargetsBean myTargetsBean : mapTargetVal) {
            try {
                if (myTargetsBean.getKPIName().equalsIgnoreCase("Visits")) {
//                    Constants.displayPieChart(myTargetsBean.getAchivedPercentage(),
//                            pieChart_outlets,getActivity(),6,myTargetsBean.getMTDA()+"/"+myTargetsBean.getMonthTarget());
                    try {
                        String tcPCNPC =myTargetsBean.getTcVSPC();
                        String[] slipyString = tcPCNPC.split("/");
                        tv_tc.setText(slipyString[0]);
                        tv_pc.setText(slipyString[1]);
                        tv_NPC.setText(slipyString[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(mapBeatList != null && mapBeatList.size()>0){
            rv_beat.setVisibility(View.VISIBLE);
            recyclerViewAdapterBeat.refreshAdapter(mapBeatList);
        }else {
            rv_beat.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString(Constants.isBrandEnabled, "").equalsIgnoreCase(Constants.isBrandTcode)) {
            if (mapBrandList != null && mapBrandList.size() > 0) {
                for (BrandProductiveBean showProgress : mapBrandList) {
                    showProgress.setShowProgress(false);
                }
                if (recyclerViewAdapterBrand != null)
                    ll_brand_list.setVisibility(View.VISIBLE);
                    recyclerViewAdapterBrand.refreshAdapter(mapBrandList);
            } else {
                ll_brand_list.setVisibility(View.GONE);
            }
        }else {
            ll_brand_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void disPlayMTPCount(String count) {
        tvRetApprovalCount.setText(count);
        if(refreshingFlag) {
            RetailerApprovalListActivity.RetApprovalTotalCount = count;
            refreshingFlag = false;
            presenter.onRefresh();
        }
    }

    @Override
    public void disPlaySOCount(String count) {

    }

    @Override
    public void disPlayContractApprovalCount(String count) {

    }

    @Override
    public void displayPendingDispatch(String count) {

    }

    @Override
    public void displayCreditLimitCount(String count) {

    }

    @Override
    public void showMTPProgress() {
        tvRetApprovalCount.setVisibility(View.GONE);
        pbRetAplCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {
        if(mapBrandList != null && mapBrandList.size()>0) {
            if(mProgressFalg) {
                for (BrandProductiveBean showProgress : mapBrandList) {
                    showProgress.setShowProgress(true);
                }
                mProgressFalg = false;
            }
            if (recyclerViewAdapterBrand != null) {
                ll_brand_list.setVisibility(View.VISIBLE);
                recyclerViewAdapterBrand.refreshAdapter(mapBrandList);
            }
        }
    }

    @Override
    public void hideMTPProgress() {
        tvRetApprovalCount.setVisibility(View.VISIBLE);
        pbRetAplCount.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(VisitedBeatBean visitedBeatBean, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<VisitedBeatBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new VisitBestVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, VisitedBeatBean visitedBeatBean, ArrayList<VisitedBeatBean> arrayList) {
        try {

            if (visitedBeatBean.isVisitedStatus()) {
                ((VisitBestVH) viewHolder).status.setBackgroundColor(getResources().getColor(R.color.GREEN));
            } else {
                ((VisitBestVH) viewHolder).status.setBackgroundColor(getResources().getColor(R.color.ORANGE));
            }

            ((VisitBestVH)viewHolder).tv_beat_name.setText(visitedBeatBean.getBeatName());
            try {
                ((VisitBestVH)viewHolder).tv_total_retail.setText(visitedBeatBean.getTotalBeatRetailers());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                ((VisitBestVH)viewHolder).tv_Visited.setText(visitedBeatBean.getTotalVisitedRetailers());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                ((VisitBestVH)viewHolder).tv_not_visited.setText(String.valueOf(Integer.valueOf(visitedBeatBean.getTotalBeatRetailers()) - Integer.valueOf(visitedBeatBean.getTotalVisitedRetailers())));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class GetNonVistedRetailers extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String mStrDate = params[0];
            try {
                Thread.sleep(1000);
//                delList = checkNonVisitedRetailers(mStrDate);
                delList = Constants.getDealer(mStrDate);

            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closeProgressDialog();
            attendanceFunctionality(iv_day_start_action, tv_day_start_text);
        }
    }

    /*AsyncTask to Close Attendance for day*/
    private class ClosingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String qry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                Hashtable hashTableAttendanceValues;

                if (Constants.MapEntityVal.size() > 0) {


                    hashTableAttendanceValues = new Hashtable();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                    String loginIdVal = sharedPreferences.getString(UtilRegistrationActivity.KEY_username, "");
                    //noinspection unchecked
                    if (!TextUtils.isEmpty(loginIdVal)) {
                        hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                    }
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.AttendanceGUID) != null) {
                        hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                    }
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.StartDate) != null) {
                        hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                    }
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.StartTime) != null) {
                        hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                    }
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.StartLat) != null) {
                        hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                    }
                    //noinspection unchecked
                    if (Constants.MapEntityVal.get(Constants.StartLong) != null) {
                        hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                    }
                    try {
                        //noinspection unchecked
                        hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                        //noinspection unchecked
                        hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //noinspection unchecked
                    try {
                        hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    hashTableAttendanceValues.put(Constants.SPGUID, mStrSPGUID);

                    if (Constants.MapEntityVal.get(Constants.SetResourcePath) != null) {
                        hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));
                    }

                    if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                        hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                    } else {
                        hashTableAttendanceValues.put(Constants.Etag, "");
                    }

                    hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                    final Calendar calCurrentTime = Calendar.getInstance();
                    int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                    int minute = calCurrentTime.get(Calendar.MINUTE);
                    int second = calCurrentTime.get(Calendar.SECOND);
                    ODataDuration oDataDuration = null;
                    try {
                        oDataDuration = new ODataDurationDefaultImpl();
                        oDataDuration.setHours(hourOfDay);
                        oDataDuration.setMinutes(minute);
                        oDataDuration.setSeconds(BigDecimal.valueOf(second));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //noinspection unchecked
                    if (oDataDuration != null) {
                        hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);
                    }

                    //noinspection unchecked

                    SharedPreferences sharedPreferencesVal = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferencesVal.edit();
                    editor.putInt("VisitSeqId", 0);
                    editor.apply();

                    try {
                        //noinspection unchecked
                        OfflineManager.updateAttendance(hashTableAttendanceValues, DashboardFragment.this,getActivity());
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                }else {
                    if(pdLoadDialog!=null && pdLoadDialog.isShowing()){
                        pdLoadDialog.dismiss();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*AsyncTask to reset attendance for day*/
    private class ResettingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                Hashtable hashTableAttendanceValues;


                if (Constants.MapEntityVal.size() > 0) {


                    hashTableAttendanceValues = new Hashtable();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                    String loginIdVal = sharedPreferences.getString(UtilRegistrationActivity.KEY_username, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLat, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndLong, "");
                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndDate, "");

                    hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                    hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                    hashTableAttendanceValues.put(Constants.SPGUID, mStrSPGUID);

                    hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));

                    if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                        hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                    } else {
                        hashTableAttendanceValues.put(Constants.Etag, "");
                    }

                    final Calendar calCurrentTime = Calendar.getInstance();
                    int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                    int minute = calCurrentTime.get(Calendar.MINUTE);
                    int second = calCurrentTime.get(Calendar.SECOND);
                    ODataDuration oDataDuration = null;
                    try {
                        oDataDuration = new ODataDurationDefaultImpl();
                        oDataDuration.setHours(hourOfDay);
                        oDataDuration.setMinutes(minute);
                        oDataDuration.setSeconds(BigDecimal.valueOf(second));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //noinspection unchecked
                    hashTableAttendanceValues.put(Constants.EndTime, "");

                    try {
                        //noinspection unchecked
                        OfflineManager.resetAttendanceEntity(hashTableAttendanceValues, DashboardFragment.this,getActivity());
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                        ConstantsUtils.printErrorLog(e.getMessage());
                    }
                }else {
                    if(pdLoadDialog!=null && pdLoadDialog.isShowing()){
                        pdLoadDialog.dismiss();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*
     Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);

                Hashtable table = new Hashtable();

                try {

                    if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                    }
                    ODataEntity visitEntity;
                    visitEntity = OfflineManager.getVisitDetailsByKey(mStrVisitId);

                    if (visitEntity != null) {
                        ODataPropMap oDataProperties = visitEntity.getProperties();
                        oDataProperties = visitEntity.getProperties();
                        ODataProperty oDataProperty = oDataProperties.get(Constants.StartLat);
                        //noinspection unchecked
                        table.put(Constants.StartLat, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.StartLong);
                        //noinspection unchecked
                        table.put(Constants.StartLong, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.STARTDATE);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.STARTTIME);
                        //noinspection unchecked
                        table.put(Constants.STARTTIME, oDataProperty.getValue());

                        try {
                            //noinspection unchecked
                            table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude,12)));
                            //noinspection unchecked
                            table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude,12)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        //noinspection unchecked
                        table.put(Constants.Remarks, mStrVisitEndRemarks);
                        try {
                            table.put(Constants.REASON, mStrReasonCode);
                        } catch (Exception e) {
                            table.put(Constants.REASON, "");
                            e.printStackTrace();
                        }


                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.SPGUID);
                        ODataGuid mSPGUID=null;
                        try {
                            mSPGUID = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.SPGUID, mSPGUID.guidAsString36().toUpperCase());
                        } catch (Exception e) {
                            table.put(Constants.SPGUID, Constants.getSPGUID());
                        }

                        try {
                            oDataProperty = oDataProperties.get(Constants.ZZDEVICE_ID);
                            table.put(Constants.ZZDEVICE_ID, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            oDataProperty = oDataProperties.get(Constants.ZZPARENT);
                            table.put(Constants.ZZPARENT, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.ROUTEPLANKEY);

                        //noinspection unchecked
                        if (oDataProperty.getValue() == null) {
                            table.put(Constants.ROUTEPLANKEY, "");
                        } else {
                            ODataGuid mRouteGuid = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.ROUTEPLANKEY, mRouteGuid.guidAsString36().toUpperCase());
                        }


                        oDataProperty = oDataProperties.get(Constants.StatusID);
                        table.put(Constants.StatusID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate,  oDataProperty!=null?oDataProperty.getValue():null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate,  "");
                        }


                        oDataProperty = oDataProperties.get(Constants.VisitSeq);
                        try {
                            table.put(Constants.VisitSeq, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());


                        oDataProperty = oDataProperties.get(Constants.BeatGUID);
                        ODataGuid mBeatGUID = null;
                        try {
                            mBeatGUID = (ODataGuid) oDataProperty.getValue();
                            table.put(Constants.BeatGUID, mBeatGUID.guidAsString36().toUpperCase());
                        } catch (Exception e) {
//                            table.put(Constants.BeatGUID, Constants.getSPGUID());
                        }
                        
                        table.put(Constants.ENDTIME, UtilConstants.getOdataDuration());

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        }
                        oDataProperty = oDataProperties.get(Constants.CreatedOn);
                        //noinspection unchecked
                        try {
                            table.put(Constants.CreatedOn, oDataProperty.getValue() == null ? UtilConstants.convertDateFormat(UtilConstants.getNewDateTimeFormat()) : oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CreatedBy);
                        //noinspection unchecked
                        try {
                            table.put(Constants.CreatedBy, oDataProperty.getValue() == null ? loginIdVal : oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, DashboardFragment.this,getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
                ConstantsUtils.printErrorLog(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_mainmenu, menu);
        MenuItem menuItem = menu.findItem(R.id.itemAlerrt);
        menuItem.setVisible(true);
        View badgeLayout = menuItem.getActionView();
        RelativeLayout alertMain = (RelativeLayout) badgeLayout.findViewById(R.id.alertMain);
        alertMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAlerts();
            }
        });
        tvAlertCount = (TextView) badgeLayout.findViewById(R.id.tvAlertCount);
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.app_name), false, true);
        setAlertCount();
    }
    private void callAlerts() {
        Intent intent = new Intent(getActivity(), AlertsActivity.class);
        startActivity(intent);
    }
    public void setAlertCount() {
        if (tvAlertCount != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
            tvAlertCount.setText(String.valueOf(sharedPreferences.getInt(Constants.ALERTS_COUNT, 0)));
        }
    }*/
}
