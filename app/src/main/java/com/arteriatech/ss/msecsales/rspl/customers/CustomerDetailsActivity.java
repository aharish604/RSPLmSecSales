package com.arteriatech.ss.msecsales.rspl.customers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.RetailerDetailPagetTabAdapter;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.common.MSFAApplication;
import com.arteriatech.ss.msecsales.rspl.interfaces.CustomDialogCallBackWithCode;
import com.arteriatech.ss.msecsales.rspl.mbo.FragmentWithTitleBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RemarkReasonBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.visit.AddressFragment;
import com.arteriatech.ss.msecsales.rspl.visit.ReportsFragment;
import com.arteriatech.ss.msecsales.rspl.visit.VisitFragment;
import com.arteriatech.ss.msecsales.rspl.visit.summary.SummaryFragment;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.google.android.material.tabs.TabLayout.GRAVITY_FILL;

/**
 * Created by ${e10526} on ${16-11-2016}.
 */
@SuppressLint("NewApi")
public class CustomerDetailsActivity extends AppCompatActivity implements View.OnClickListener, UIListener {
    String mStrCPTypeId = "";
    ImageView iv_visit_status;
    Map<String, String> startParameterMap;
    String address = "", mobNo = "";
    TabLayout tabLayout;
    private boolean isClickable = false;
    /*
                 Enter remarks in visit table if activity is not done.

                */
    boolean wantToCloseDialog = false;
    boolean mBoolBackBtnPressed = false;
    RetailerBean retailerBean;
    RetailerBean notPostedRetailer;
    AddressFragment addressFragment = null;
    private String mStrCustomerName = "";
    private String mStrUID = "";
    private String mStrCustomerId = "";
    private String mStrBundleCpGuid = "";
    private String mStrComingFrom = "";
    private String mStrRouteGuid = "";
    private String mStrRouteName = "";
    private String mStrCurrency = "";
    private String mStrPopUpText = "";
    private String mStrCustNo = "";
    private String mStrOtherRetailerGuid = "";
    //new
    private String mStrVisitEndRemarks = "";
    private String mStrReasonCode = "";
    private String distubutorID = "";
    private String mStrBeatGUID = "";
    private String mStrParentId = "";
    private String mStrReasonDesc = "";
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private ODataGuid mStrVisitId = null;
    private boolean mBooleanVisitStarted = false;
    private ProgressDialog pdLoadDialog;
    private boolean mBooleanNavPrvVisitClosed = false;
    private boolean mBooleanSaveStart = false;
    private boolean mBooleanVisitStartDialog = false, mBooleanVisitEndDialog = false;
    private String mStrVisitCatId = "";
    //This is our viewPager
    private ViewPager viewPager;
    private boolean mBoolMsg = false;
    private String mStrComingFromBeatPlanProspective = "";
    private String mStrSPGUID = "";
    private ArrayList<RemarkReasonBean> reasonCodedesc = new ArrayList<>();
    private String mStrBundleInstanceId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)

        setContentView(R.layout.activity_retailer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Constants.mApplication = (MSFAApplication) getApplication();

        TextView tvCustomerID = (TextView) findViewById(R.id.tv_RetailerID);
        TextView tvCustomerName = (TextView) findViewById(R.id.tv_RetailerName);

        startParameterMap = new HashMap<String, String>();

        iv_visit_status = (ImageView) findViewById(R.id.iv_visit_status);
        iv_visit_status.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrCustomerName = extras.getString(Constants.RetailerName);
            mStrUID = extras.getString(Constants.CPUID);
            mStrCustomerId = extras.getString(Constants.CPNo);
            mStrComingFrom = extras.getString(Constants.comingFrom);
            try {
                address = extras.getString(Constants.Address);
                mobNo = extras.getString(Constants.MobileNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mStrBundleInstanceId = extras.getString(Constants.InstanceID) != null ? extras.getString(Constants.InstanceID) : "";
            mStrBundleCpGuid = extras.getString(Constants.CPGUID) != null ? extras.getString(Constants.CPGUID) : "";
            mStrRouteName = extras.getString(Constants.OtherRouteName) != null ? extras.getString(Constants.OtherRouteName) : "";
            mStrRouteGuid = extras.getString(Constants.OtherRouteGUID) != null ? extras.getString(Constants.OtherRouteGUID) : "";
            distubutorID = extras.getString(Constants.DistubutorID) != null ? extras.getString(Constants.DistubutorID) : "";
            mStrBeatGUID = extras.getString(Constants.BeatGUID) != null ? extras.getString(Constants.BeatGUID) : "";
            mStrParentId = extras.getString(Constants.ParentId) != null ? extras.getString(Constants.ParentId) : "";
            mStrVisitCatId = extras.getString(Constants.VisitCatID);
            mStrCurrency = extras.getString(Constants.Currency);
//            retailerBean = (RetailerBean) extras.getSerializable(Constants.RetailerApprovalList);
            if (!mStrComingFrom.equalsIgnoreCase("NotPostedRetailer")) {
                retailerBean = (RetailerBean) extras.getParcelable(Constants.RetailerApprovalList);
            }else {
                retailerBean = (RetailerBean) extras.getParcelable(Constants.NotPostedRetailer);
            }
            Constants.VisitNavigationFrom = mStrComingFrom;

        }
        mStrSPGUID = Constants.getSPGUID();

        if (!mStrComingFrom.equalsIgnoreCase("NotPostedRetailer")) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_retailer_details), 0);
        }else {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_not_retailer_details), 0);
        }

        mStrCustNo = mStrCustomerId;
        tvCustomerName.setText(mStrCustomerName);

        if (!TextUtils.isEmpty(mStrCustomerId)) {
            tvCustomerID.setText(mStrCustomerId);

        }
        if (!Constants.restartApp(CustomerDetailsActivity.this)) {
            if (mStrComingFrom.equals(Constants.AdhocList) || mStrComingFrom.equals(Constants.BeatPlan)) {
                displayVisitIcon();
            } else {
                mBooleanVisitStarted = true;
                iv_visit_status.setVisibility(View.GONE);
            }
            tabIntilize();
            reasonCodedesc = Constants.getReasonValues(reasonCodedesc);
        }
    }

    /*
        Display Visit Status Icon
    */
    private void displayVisitIcon() {
       /* if (!mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
            Constants.Route_Plan_Key = "";
        }*/
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) || mStrComingFrom.equals(Constants.BeatPlan)) {
            iv_visit_status.setVisibility(View.VISIBLE);
        } else {
            iv_visit_status.setVisibility(View.GONE);
        }

        mBooleanVisitStartDialog = false;
        mBooleanVisitEndDialog = false;
        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "'" +
                "and " + Constants.CPGUID + " eq '" + mStrBundleCpGuid.replace("-", "") + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";


        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                "and " + Constants.CPGUID + " eq '" + mStrBundleCpGuid.replace("-", "") + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";

        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                iv_visit_status.setImageResource(R.drawable.stop);
                mBooleanVisitStarted = true;
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done_black_24dp);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();

                String qry = Constants.Visits + "?$filter=EndDate eq null and " + Constants.CPGUID + " eq '" +  mStrBundleCpGuid.replace("-", "") + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";

                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    /*
       Navigate to Previous List Screens
   */
    private void NavigateToListScreen() {
        try {
            Log.d("VistSartorStop","onVisitClosingAction");
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
          Dismiss Progress Dialog

      */
    private void dismissProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Display error message
*/
    @Override
    public void onRequestError(int operation, Exception exception) {
        isClickable=false;
        dismissProgressDialog();
        String err_msg = "";
        try {
            if (operation == Operation.Create.getValue()) {
                try {
                    err_msg = getString(R.string.err_msg_concat, getString(R.string.lbl_visit_start), exception.getMessage());
                } catch (Exception ex) {
                    err_msg = getString(R.string.err_visit_start);
                }
                UtilConstants.showAlert(err_msg, CustomerDetailsActivity.this);
            } else if (operation == Operation.Update.getValue()) {
                try {
                    err_msg = getString(R.string.err_msg_concat, getString(R.string.lbl_visit_end), exception.getMessage());
                } catch (Exception ex) {
                    err_msg = getString(R.string.err_visit_end);
                }
                UtilConstants.showAlert(err_msg, CustomerDetailsActivity.this);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Toast.makeText(CustomerDetailsActivity.this, getString(R.string.err_odata_unexpected, exception.getMessage()),
                Toast.LENGTH_LONG).show();


    }

    /*
     Display Success message
 */
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        isClickable=false;
        if (operation == Operation.Create.getValue()) {
            alertPopupMessage();
        } else if (operation == Operation.Update.getValue()) {
//            Constants.TodayActualVisitRetailersCount =  Constants.getTodayActualVisitedRetCount("");
            alertPopupMessage();
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            dismissProgressDialog();

            if (mBooleanNavPrvVisitClosed) {
                NavigateToListScreen();
            } else {
                if (mBooleanSaveStart) {
                }
            }
        }
    }

    /*
    Display Alert message regarding visit started or visit ended.
    */
    private void alertPopupMessage() {
        try {
            dismissProgressDialog();
            if (mBooleanVisitStartDialog) {
                mStrPopUpText = getString(R.string.visit_started);
                Constants.writeDebugLog(CustomerDetailsActivity.this, mStrPopUpText);
            }
            if (mBooleanVisitEndDialog) {
                mStrPopUpText = getString(R.string.visit_ended);
                Constants.writeDebugLog(CustomerDetailsActivity.this, mStrPopUpText);
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CustomerDetailsActivity.this, R.style.MyTheme);
            builder.setMessage(mStrPopUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {
                                        Dialog.cancel();

                                        if (mBooleanNavPrvVisitClosed) {
                                            NavigateToListScreen();
                                        } else {
                                            if (mBooleanSaveStart) {
                                                mBooleanVisitStarted = true;
                                                setupViewPagerWithVisit();
                                                tabLayout.setupWithViewPager(viewPager);
                                                viewPager.setCurrentItem(2);
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    private void onSaveClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            e.printStackTrace();
            ConstantsUtils.printErrorLog(e.getMessage());
        }
    }

    private void onSaveStart() {
        mStrPopUpText = getString(R.string.marking_visit_start_plz_wait);
        try {
            String cpId = UtilConstants.removeLeadingZeros(mStrCustomerId);
            startParameterMap.put(Constants.CPNo, cpId);
            startParameterMap.put(Constants.CPName, mStrCustomerName);
            startParameterMap.put(Constants.CPTypeID, Constants.str_02);
            startParameterMap.put(Constants.VisitCatID, mStrVisitCatId);
            startParameterMap.put(Constants.StatusID, "01");
            startParameterMap.put(Constants.PlannedDate, null);
            startParameterMap.put(Constants.PlannedStartTime, null);
            startParameterMap.put(Constants.PlannedEndTime, null);
            startParameterMap.put(Constants.VisitTypeID, "");
            startParameterMap.put(Constants.VisitTypeDesc, "");
            startParameterMap.put(Constants.Remarks, "");
            startParameterMap.put(Constants.BeatGUID, mStrBeatGUID);
            startParameterMap.put(Constants.VisitSeq, "");
            try {
                String deviceID  = Settings.Secure.getString(CustomerDetailsActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                startParameterMap.put(Constants.ZZDEVICE_ID, deviceID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                startParameterMap.put(Constants.ZZPARENT, mStrParentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            startParameterMap.put(Constants.VisitSeq,mStrVisitSeqNo);
            startParameterMap.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());


            Constants.createVisit(startParameterMap, mStrBundleCpGuid.replace("-", ""), CustomerDetailsActivity.this, this);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_visit_status:
                if(!isClickable) {
                    isClickable = true;
                    checkBeatApproved();
                }
                break;
        }
    }

    private void checkBeatApproved() {
        if (!TextUtils.isEmpty(ConstantsUtils.checkBeatApprove(CustomerDetailsActivity.this))) {
            if (!TextUtils.isEmpty(mStrRouteGuid)) {
                String qryForTodaysBeat = Constants.RouteSchedules + "?$filter=" + Constants.RouteSchGUID + " eq guid'" + mStrRouteGuid.toUpperCase() + "' and ApprovalStatus eq '03'";
                List<RetailerBean> routeSchList = null;
                try {
                    routeSchList = OfflineManager.getOtherBeatList(qryForTodaysBeat, true);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (routeSchList != null && routeSchList.size() > 0) {
                    checkVisitStartLocPermission();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.visit_beat_not_aprvd), Toast.LENGTH_LONG).show();
                    isClickable =false;
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.visit_beat_empty), Toast.LENGTH_LONG).show();
                isClickable =false;
            }
        } else {
            checkVisitStartLocPermission();
        }
    }

    private void checkVisitStartLocPermission() {
        pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(CustomerDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                        onVisitAction();
                    } else {
                        ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                        isClickable =false;
                    }

                }else {
                    isClickable=false;
                }
            }
        });
    }

    private void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, false);
        viewPager.setCurrentItem(pageIndex, false);
    }

    private void onVisitAction() {
        mBoolBackBtnPressed = false;
        Thread thread =null;
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) || mStrComingFrom.equals(Constants.BeatPlan)) {

            Constants.MapEntityVal.clear();

            final String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCpGuid.replace("-", "").toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' " +
                    "and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";
            ;

            Constants.writeDebugLog(CustomerDetailsActivity.this,"Visits  getting visit details: query-"+qry);
            try {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            mStrVisitId = OfflineManager.getVisitDetails(qry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
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
            if (!Constants.MapEntityVal.isEmpty()) {
                mBooleanVisitStarted = true;
            } else {
                mBooleanVisitStarted = false;
            }


            mBooleanSaveStart = false;
            mBooleanNavPrvVisitClosed = false;
            Constants.MapEntityVal.clear();

            final String[] attdIdStr = {""};
            final String attnQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

            try {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            attdIdStr[0] = OfflineManager.getAttendance(attnQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
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
                isClickable =false;
            } finally {
                if (thread!=null&&thread.isAlive()){
                    thread.interrupt();
                }
            }

            if (!attdIdStr[0].equalsIgnoreCase("")) {
                if (!mBooleanVisitStarted) {
                    mStrOtherRetailerGuid = "";


                    //new 28112016
//                    String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null and CPGUID ne '" + mStrBundleCpGuid.replace("-", "").toUpperCase() + "' " +
//                            "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                    String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                            "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";

                            Constants.writeDebugLog(CustomerDetailsActivity.this,"Visit Start getting not ended retailer list: query-"+otherRetVisitQuery);

                    String[] otherRetDetails = new String[2];
                    try {
                        otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }

                    if (otherRetDetails[0] == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                CustomerDetailsActivity.this, R.style.MyTheme);

                        builder.setMessage(R.string.alert_start_visit)
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
                                                pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.gps_progress));
                                                Constants.getLocation(CustomerDetailsActivity.this, new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        dismissProgressDialog();
                                                        if (status) {
                                                            mBooleanSaveStart = true;
                                                            mBooleanVisitStartDialog = true;
                                                            mBooleanVisitEndDialog = false;
                                                            onSaveStart();
                                                            iv_visit_status.setImageResource(R.drawable.stop);
                                                        }else {
                                                            isClickable=false;
                                                        }
                                                    }
                                                });

                                            }
                                        });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        isClickable=false;
                                        onRefreshVisitIcon();
                                    }

                                });

                        builder.show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                CustomerDetailsActivity.this, R.style.MyTheme);
                        final String[] finalOtherRetDetails = otherRetDetails;

                        builder.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
                                                pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.gps_progress));
                                                Constants.getLocation(CustomerDetailsActivity.this, new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        dismissProgressDialog();
                                                        if (status) {
                                                            mStrOtherRetailerGuid = finalOtherRetDetails[1];

                                                            boolean isVisitActivities = false;
                                                            try {
                                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                            } catch (OfflineODataStoreException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                            if (isVisitActivities) {
                                                                mBooleanVisitEndDialog = true;
                                                                onSaveClose();
                                                            } else {
                                                                wantToCloseDialog = false;
                                                                onAlertDialogForVisitDayEndRemarks();
                                                            }
                                                        }else {
                                                            isClickable=false;
                                                        }
                                                    }
                                                });

                                            }
                                        });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        isClickable=false;
                                        onRefreshVisitIcon();
                                    }

                                });

                        builder.show();

                    }
                } else {
                    mBoolMsg = false;
                    onVisitClosingAction();
                }
            } else {
                isClickable=false;
                attdIdStr[0] = "";
                String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
                try {
                    attdIdStr[0] = OfflineManager.getAttendance(dayEndqry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    ConstantsUtils.printErrorLog(e.getMessage());
                    isClickable =false;
                }
                if (!TextUtils.isEmpty(attdIdStr[0])) {
                    Toast.makeText(getApplicationContext(), getString(R.string.attend_close_prev_day), Toast.LENGTH_LONG).show();
                    isClickable =false;
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.alert_plz_start_day), Toast.LENGTH_LONG).show();
                    isClickable =false;
                }
            }

        }
    }




 /*
            Refresh Visit Status Icon
           */

    private void onVisitClosingAction() {
        Log.d("VistSartorStop","onVisitClosingAction");

        if (mStrComingFrom.equalsIgnoreCase(Constants.RetailerList)) {
            isClickable=false;
            NavigateToListScreen();
        } else {

            mStrVisitId = null;

            String visitQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" +
                    mStrBundleCpGuid.replace("-", "") + "' and StartDate eq datetime'" +
                    UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "' and "+ Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            Constants.writeDebugLog(CustomerDetailsActivity.this,"Visits closing action: query-"+visitQry);


            try {
                mStrVisitId = OfflineManager.getVisitDetails(visitQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            if (mStrVisitId != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CustomerDetailsActivity.this, R.style.MyTheme);

                String mStrEndDilaog = "", mStrPostive = "", mStrNegative = "";

                if (mBoolMsg) {
                    mStrEndDilaog = getString(R.string.alert_visit_pause);
                    mStrPostive = getString(R.string.mark_now);
                    mStrNegative = getString(R.string.later);
                } else {
                    mStrEndDilaog = getString(R.string.alert_end_visit);
                    mStrPostive = getString(R.string.yes);
                    mStrNegative = getString(R.string.no);
                }


                builder.setMessage(mStrEndDilaog)
                        .setCancelable(false)
                        .setPositiveButton(mStrPostive,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.gps_progress));
                                        Constants.getLocation(CustomerDetailsActivity.this, new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                dismissProgressDialog();
                                                if (status) {

                                                    boolean isVisitActivities = false;
                                                    try {
                                                        isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrVisitId.guidAsString36() + "'");
                                                    } catch (OfflineODataStoreException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (isVisitActivities) {
                                                        mBooleanNavPrvVisitClosed = true;
                                                        mBooleanVisitStartDialog = false;
                                                        mBooleanVisitEndDialog = true;
                                                        iv_visit_status.setImageResource(R.drawable.start);
                                                        onSaveClose();
                                                    } else {
                                                        wantToCloseDialog = false;
                                                        onAlertDialogForVisitDayEndRemarks();
                                                    }

                                                }else {
                                                    isClickable=false;
                                                }
                                            }
                                        });
                                    }
                                });
                builder.setNegativeButton(mStrNegative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                isClickable=false;
                                Log.d("VistSartorStop","setNegativeButton");
                                NavigateToListScreen();

                            }

                        });


                builder.show();
            } else {
                isClickable=false;
                NavigateToListScreen();
            }
        }
    }

    private void onRefreshVisitIcon() {
        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and CPGUID eq '" + mStrBundleCpGuid.replace("-", "").toUpperCase() + "' and " + Constants.StatusID + " eq '01' and "+ Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done_black_24dp);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();
                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCpGuid.replace("-", "").toUpperCase() + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' and "+ Constants.SPGUID + " eq guid'" + mStrSPGUID + "' and " + Constants.BeatGUID + " eq guid'" + mStrBeatGUID + "'";
                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }


    /*
              Set up fragments into adapter

             */

    /*
               Initialize Tab
              */
    private void tabIntilize() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addressFragment.closeFabLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        if (mStrComingFrom.equalsIgnoreCase("NotPostedRetailer")) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setPadding(16,0,0,0);
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        if (!mStrComingFrom.equalsIgnoreCase("NotPostedRetailer")) {
            SummaryFragment summaryFragment = new SummaryFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
            bundle1.putString(Constants.RetailerName, mStrCustomerName);
            bundle1.putString(Constants.CPNo, mStrCustomerId);
            bundle1.putString(Constants.CPUID, mStrUID);
            bundle1.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
            bundle1.putString(Constants.BeatGUID, mStrBeatGUID);
            bundle1.putString(Constants.ParentId, mStrParentId);
            summaryFragment.setArguments(bundle1);
            if (!mStrComingFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                adapter.addFrag(summaryFragment, getString(R.string.summary_title));
            }


            Bundle bundle = new Bundle();
            bundle.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
            bundle.putString(Constants.RetailerName, mStrCustomerName);
            bundle.putString(Constants.CPNo, mStrCustomerId);
            bundle.putString(Constants.CPUID, mStrUID);
            bundle.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
            bundle.putString(Constants.BeatGUID, mStrBeatGUID);
            bundle.putString(Constants.ParentId, mStrParentId);
            addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid, address, "", mStrComingFrom, retailerBean, mStrBundleInstanceId);

            ReportsFragment reportsFragment = new ReportsFragment();
            reportsFragment.setArguments(bundle);
            adapter.addFrag(addressFragment, Constants.Address);

            if (mBooleanVisitStarted) {
                if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) || mStrComingFrom.equals(Constants.BeatPlan)) {

                    VisitFragment visitFragment = new VisitFragment();

                    Bundle bundleVisit = new Bundle();
                    bundleVisit.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
                    bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
                    bundleVisit.putString(Constants.CPNo, mStrCustomerId);
                    bundleVisit.putString(Constants.CPUID, mStrUID);
                    bundleVisit.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
                    bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
                    bundleVisit.putString(Constants.DistubutorID, distubutorID);
                    bundleVisit.putString(Constants.BeatGUID, mStrBeatGUID);
                    bundleVisit.putString(Constants.ParentId, mStrParentId);
                    visitFragment.setArguments(bundleVisit);
                    adapter.addFrag(visitFragment, Constants.Visit);
                }
            }

            if (!mStrComingFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                adapter.addFrag(reportsFragment, Constants.Reports);
            }
        } else {
            addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid, address, "", mStrComingFrom, retailerBean, mStrBundleInstanceId);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.NotPostedRetailer, retailerBean);
            bundle.putString("ComeFrom", "NotPostedRetailer");
            addressFragment.setArguments(bundle);
            adapter.addFrag(addressFragment, Constants.Address);

        }

        viewPager.setAdapter(adapter);


//        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
        viewPager.setCurrentItem(0);
//        Constants.ComingFromCreateSenarios = "";
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPagerWithVisit() {
        viewPager.setAdapter(null);
        ArrayList<FragmentWithTitleBean> fragmentWithTitleBeanArrayList = new ArrayList<>();

        SummaryFragment summaryFragment = new SummaryFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
        bundle1.putString(Constants.RetailerName, mStrCustomerName);
        bundle1.putString(Constants.CPNo, mStrCustomerId);
        bundle1.putString(Constants.CPUID, mStrUID);
        bundle1.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
        bundle1.putString(Constants.BeatGUID, mStrBeatGUID);
        bundle1.putString(Constants.ParentId, mStrParentId);
        summaryFragment.setArguments(bundle1);
        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(summaryFragment, getString(R.string.summary_title)));


        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
        bundle.putString(Constants.BeatGUID, mStrBeatGUID);
        bundle.putString(Constants.ParentId, mStrParentId);

        addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid, address, "", mStrComingFrom, retailerBean, mStrBundleInstanceId);
        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);

//        SummaryFragment summaryFragment = new SummaryFragment();
//        summaryFragment.setArguments(bundle);

        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(addressFragment, Constants.Address));

        VisitFragment visitFragment = new VisitFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID32, mStrBundleCpGuid.replace("-", "").toUpperCase());
        bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
        bundleVisit.putString(Constants.CPNo, mStrCustomerId);
        bundleVisit.putString(Constants.CPUID, mStrUID);
        bundleVisit.putString(Constants.CPGUID, mStrBundleCpGuid.toUpperCase());
        bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
        bundleVisit.putString(Constants.DistubutorID, distubutorID);
        bundleVisit.putString(Constants.BeatGUID, mStrBeatGUID);
        bundleVisit.putString(Constants.ParentId, mStrParentId);
        visitFragment.setArguments(bundleVisit);
        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(visitFragment, Constants.Visit));

        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(reportsFragment, Constants.Reports));

//        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(summaryFragment, Constants.Summary));

        RetailerDetailPagetTabAdapter visitAdapter = new RetailerDetailPagetTabAdapter(getSupportFragmentManager(), fragmentWithTitleBeanArrayList);
        viewPager.setAdapter(visitAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


//        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
        viewPager.setCurrentItem(2);
//        Constants.ComingFromCreateSenarios = "";
    }

    private void onAlertDialogForVisitDayEndRemarks() {
        try {
            mStrReasonCode = "";
            mStrReasonDesc = "";
            ConstantsUtils.showVisitRemarksDialog(CustomerDetailsActivity.this, new CustomDialogCallBackWithCode() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description, String reasonCode, String reasonDesc) {
                    mStrVisitEndRemarks = description;
                    mStrReasonCode = reasonCode;
                    mStrReasonDesc = reasonDesc;

                    if (userClicked) {
                        if (ConstantsUtils.isAutomaticTimeZone(CustomerDetailsActivity.this)) {
                            if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
                                mBooleanNavPrvVisitClosed = true;
                            else
                                mBooleanNavPrvVisitClosed = false;

                            mBooleanVisitStartDialog = false;
                            mBooleanVisitEndDialog = true;

                            wantToCloseDialog = false;
                            onSaveClose();
                            onRefreshVisitIcon();
                        } else {
                            ConstantsUtils.showAutoDateSetDialog(CustomerDetailsActivity.this);
                        }
                    }else {
                        isClickable=false;
                    }
                }
            }, getString(R.string.alert_plz_enter_remarks), reasonCodedesc);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        Log.d("VistSartorStop","backpressed");
        mBoolMsg = true;
        mBoolBackBtnPressed = true;
        checkVisitEndLocPermission();
    }

    private void checkVisitEndLocPermission() {
        pdLoadDialog = Constants.showProgressDialog(CustomerDetailsActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(CustomerDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    onVisitClosingAction();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(CustomerDetailsActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                if (mBoolBackBtnPressed) {
                                    onVisitClosingAction();
                                } else {
                                    onVisitAction();
                                }
                            }
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBoolBackBtnPressed) {
                    onVisitClosingAction();
                } else {
                    onVisitAction();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CustomerDetailsActivity.this, R.style.ProgressDialogTheme);
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
                        oDataProperties = visitEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.StartLat);
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

                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        //noinspection unchecked
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                        try {
                            //noinspection unchecked
                            table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                            //noinspection unchecked
                            table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        //noinspection unchecked
                        if(mStrVisitId==null){
                            if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                            }
                        }else {
                            table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        }
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
                        ODataGuid mSPGUID = null;
                        try {
                            mSPGUID = (ODataGuid) oDataProperty.getValue();
                            table.put(Constants.SPGUID, mSPGUID.guidAsString36().toUpperCase());
                        } catch (Exception e) {
                            table.put(Constants.SPGUID, Constants.getSPGUID());
                        }

                        oDataProperty = oDataProperties.get(Constants.BeatGUID);
                        ODataGuid mBeatGUID = null;
                        try {
                            mBeatGUID = (ODataGuid) oDataProperty.getValue();
                            table.put(Constants.BeatGUID, mBeatGUID.guidAsString36().toUpperCase());
                        } catch (Exception e) {
//                            table.put(Constants.BeatGUID, Constants.getSPGUID());
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

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());


                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitSeq);
                            table.put(Constants.VisitSeq, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());

                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate, oDataProperty != null ? oDataProperty.getValue() : null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate, "");
                        }

                        table.put(Constants.ENDTIME, UtilConstants.getOdataDuration());

                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        } else {
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
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, CustomerDetailsActivity.this,CustomerDetailsActivity.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdLoadDialog!=null&&pdLoadDialog.isShowing()){
            pdLoadDialog.dismiss();
        }
    }
}
