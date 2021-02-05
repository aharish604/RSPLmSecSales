package com.arteriatech.ss.msecsales.rspl.visit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.NotPostedRetailerActivity;
import com.arteriatech.ss.msecsales.rspl.home.dashboard.DashboardPresenterImpl;
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.interfaces.CustomDialogCallBack;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.retailerapproval.RetailerApprovalListActivity;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.ui.fabTnsfmgToolBar.FABToolbarLayout;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataParserException;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Set;

@SuppressLint("NewApi")
public class AddressFragment extends Fragment implements View.OnClickListener, AsyncTaskCallBack, UIListener {
    public FABToolbarLayout fabtoolbarContainer;
    String mStrCPGUID = "", mStrCustomerID = "", mStrRetName = "";
    ODataPropMap oDataProperties;
    ODataProperty oDataProperty;
    ImageView iv_mail, iv_sms, iv_call, iv_location, ivGSTStatus;
    Button ivLocationCapture;
    LinearLayout ll_location_layout, ll_tax_status, ll_owner_name;
    String mDistributorName = "", mContactNum = "", mRetCategory = "", mClassification = "", selCPTypeDesc,
            mWeeklyOff = "", mDOB = "", mAnniversary = "", mStrCPTypeId = "", mStrFirstAddress = "", partnerGUID = "", mStrLandmark = "";
    ODataGuid mCpGuid = null;
    String address = "", mobNo = "", comeFrom = "";
    TextView tvDistName, tvOwnerName, tvAddress, tvRetailerCategory, tvClassification, tvWeeklyOff, tvLocationVal, tvLandmark, tvGSTNo, tvGSTText;
    RetailerBean retailerBean = new RetailerBean();
    ProgressDialog progressDialog = null;
    Context mContext;
    String mStrInstanceId;
    private String mStrEmailID = "", mStrOwnerName = "";
    private View myInflatedView = null;
    private double mDouLatVal = 0.0, mDouLongVal = 0.0;
    private String strAddress = "";
    private FloatingActionButton fabtoolbar;
    private ScrollView nestedScroll;
    private String mStrGSTIN = "", mStrGSTREGStatus = "";
    private ProgressDialog pdLoadDialog;
    private String mStrID = "";
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private boolean isOnline = false;
    private LinearLayout ll_apr_rej/*,ll_apr_rej_fab*/;
    private View tvReject;
    //    private View tvRejectFab;
    private View tvApprove;
    //    private View tvApproveFab;
    private LinearLayout ll_address_area;

    public static AddressFragment newInstance(String mStrRetId, String mStrRetName, String mStrCpGuid, String address, String mobileNo, String comefrom, RetailerBean retailerBean, String insatnceId) {

        AddressFragment addressFragment = new AddressFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID, mStrCpGuid);
        bundle.putString(Constants.RetName, mStrRetName);
        bundle.putString(Constants.CPNo, mStrRetId);
        bundle.putString("Address", address);
        bundle.putString("MobNo", mobileNo);
        bundle.putString("ComeFrom", comefrom);
        bundle.putString("InstanceID", insatnceId);
        bundle.putParcelable("RetailerBean", retailerBean);
        addressFragment.setArguments(bundle);
        return addressFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mContext = container.getContext();
        mStrCPGUID = getArguments().getString(Constants.CPGUID);
        mStrCustomerID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetName);
        myInflatedView = inflater.inflate(R.layout.fragment_address_lay, container, false);
        address = getArguments().getString("Address");
        mobNo = getArguments().getString("MobNo");
        comeFrom = getArguments().getString("ComeFrom");
        mStrInstanceId = getArguments().getString("InstanceID");
        if (!comeFrom.equalsIgnoreCase("NotPostedRetailer")) {
            retailerBean = getArguments().getParcelable("RetailerBean");
        } else {
            retailerBean = getArguments().getParcelable(Constants.NotPostedRetailer);
        }

        iv_call = (ImageView) myInflatedView.findViewById(R.id.call);
        iv_call.setOnClickListener(this);

        iv_sms = (ImageView) myInflatedView.findViewById(R.id.sms);
        iv_sms.setOnClickListener(this);

        iv_mail = (ImageView) myInflatedView.findViewById(R.id.mail);
        iv_mail.setOnClickListener(this);

        ImageView iv_whatsApp = (ImageView) myInflatedView.findViewById(R.id.whats_app);
        iv_whatsApp.setOnClickListener(this);

//        iv_appointment = (ImageView) myInflatedView.findViewById(R.id.appointment);
//        iv_appointment.setOnClickListener(this);
        iv_location = (ImageView) myInflatedView.findViewById(R.id.iv_location);

        onInitUI();

        if (!comeFrom.equalsIgnoreCase("NotPostedRetailer")) {
            iv_location.setOnClickListener(this);
            onRetailerDetails();
        } else {
            onNotPostedRetailDetails();
        }

        if (comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
            ll_apr_rej.setVisibility(View.VISIBLE);
//            ll_apr_rej_fab.setVisibility(View.VISIBLE);
            setValuesToUIRetApproval();
        } else {
            ll_apr_rej.setVisibility(View.GONE);
//            ll_apr_rej_fab.setVisibility(View.GONE);
            setValuesToUI();
        }
        return myInflatedView;
    }

    /*
     *  This method initialize UI
     */
    private void onInitUI() {
        tvDistName = (TextView) myInflatedView.findViewById(R.id.tvDistName);
        tvOwnerName = (TextView) myInflatedView.findViewById(R.id.tvOwnerName);
        tvAddress = (TextView) myInflatedView.findViewById(R.id.tvAddress);
        tvLandmark = (TextView) myInflatedView.findViewById(R.id.tvLandmark);
//        ll_address_area = (LinearLayout) myInflatedView.findViewById(R.id.ll_address_area);

        tvGSTNo = (TextView) myInflatedView.findViewById(R.id.tvGSTNo);
        tvGSTText = (TextView) myInflatedView.findViewById(R.id.tvGSTText);
        tvLocationVal = (TextView) myInflatedView.findViewById(R.id.tvLocationVal);
        tvLocationVal.setOnClickListener(this);

        ivGSTStatus = (ImageView) myInflatedView.findViewById(R.id.ivGSTStatus);
        ivLocationCapture = (Button) myInflatedView.findViewById(R.id.ivLocationCapture);
        ivLocationCapture.setOnClickListener(this);

        ll_location_layout = (LinearLayout) myInflatedView.findViewById(R.id.ll_location_layout);
        ll_tax_status = (LinearLayout) myInflatedView.findViewById(R.id.ll_tax_status);
        ll_owner_name = (LinearLayout) myInflatedView.findViewById(R.id.ll_owner_name);

        tvRetailerCategory = (TextView) myInflatedView.findViewById(R.id.tv_ret_category);
        tvClassification = (TextView) myInflatedView.findViewById(R.id.tv_classification);
        tvWeeklyOff = (TextView) myInflatedView.findViewById(R.id.tv_weekly_off);
        fabtoolbar = (FloatingActionButton) myInflatedView.findViewById(R.id.fabtoolbar);
        nestedScroll = (ScrollView) myInflatedView.findViewById(R.id.nestedScroll);
        fabtoolbar.setOnClickListener(this);
        fabtoolbarContainer = (FABToolbarLayout) myInflatedView.findViewById(R.id.fabtoolbarContainer);
        ll_apr_rej = (LinearLayout) myInflatedView.findViewById(R.id.ll_apr_rej);
//        ll_apr_rej_fab = (LinearLayout) myInflatedView.findViewById(R.id.ll_apr_rej_fab);
        tvReject = myInflatedView.findViewById(R.id.tvReject);
//        tvRejectFab = myInflatedView.findViewById(R.id.tvRejectFab);
        tvReject.setOnClickListener(this);
//        tvRejectFab.setOnClickListener(this);
        tvApprove = myInflatedView.findViewById(R.id.tvApprove);
//        tvApproveFab = myInflatedView.findViewById(R.id.tvApproveFab);
        tvApprove.setOnClickListener(this);
//        tvApproveFab.setOnClickListener(this);
        nestedScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fabtoolbarContainer.hide();
                return false;
            }
        });

    }

    /*
     * This method set values to UI
     */
    private void setValuesToUI() {
       /* if (!TextUtils.isEmpty(mStrOwnerName)) {
            if (!TextUtils.isEmpty(mDistributorName))
                mDistributorName = mDistributorName + "\n" + mStrOwnerName;
            else
                mDistributorName = mStrOwnerName;
        }*/

        tvDistName.setText(mDistributorName);
        tvOwnerName.setText(mStrOwnerName);
        if (!TextUtils.isEmpty(mStrOwnerName)) {
            ll_owner_name.setVisibility(View.VISIBLE);
        } else {
            ll_owner_name.setVisibility(View.GONE);
        }
        tvAddress.setText(strAddress);
        tvRetailerCategory.setText(selCPTypeDesc);
        tvClassification.setText(mClassification);
        tvWeeklyOff.setText(mWeeklyOff);
        if (!mStrLandmark.equalsIgnoreCase("")) {
            ll_location_layout.setVisibility(View.VISIBLE);
            tvLandmark.setText(mStrLandmark);
        } else {
            ll_location_layout.setVisibility(View.GONE);
        }

        tvGSTNo.setText(mStrGSTIN);
        try {
            if (mStrGSTREGStatus.equalsIgnoreCase(Constants.TaxRegStatusNotDone)
                    || mStrGSTREGStatus.equalsIgnoreCase("")) {
                tvGSTText.setTextColor(getResources().getColor(R.color.RejectedColor));
            } else {
                tvGSTText.setTextColor(getResources().getColor(R.color.ClosedColor));
            }
        } catch (Exception e) {
            tvGSTText.setTextColor(getResources().getColor(R.color.RejectedColor));
            e.printStackTrace();
        }


       /* if(mStrGSTIN.equalsIgnoreCase("")){
            ll_tax_status.setVisibility(View.GONE);
        }else{
            ll_tax_status.setVisibility(View.VISIBLE);
        }*/
        if (mDouLatVal == 0.0 && mDouLongVal == 0.0) {
            tvLocationVal.setVisibility(View.INVISIBLE);
//            ivLocationCapture.setVisibility(View.VISIBLE);
        } else {
//            ivLocationCapture.setVisibility(View.GONE);
            SpannableString content = new SpannableString(mDouLatVal + ", " + mDouLongVal);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvLocationVal.setText(content);
        }
        Drawable collStatus = SOUtils.displayTaxRegStatusImage(mDouLatVal, mDouLongVal, getActivity());
        iv_location.setImageDrawable(collStatus);

        ivLocationCapture.setVisibility(View.GONE);

    }

    private void setValuesToUIRetApproval() {


       /* if (!TextUtils.isEmpty(mStrOwnerName)) {
            if (!TextUtils.isEmpty(mDistributorName))
                mDistributorName = mDistributorName + "\n" + mStrOwnerName;
            else
                mDistributorName = mStrOwnerName;
        }*/

        tvDistName.setText(retailerBean.getParentName());  // TODO clarfication req
        if (!TextUtils.isEmpty(retailerBean.getOwnerName())) {
            ll_owner_name.setVisibility(View.VISIBLE);
            tvOwnerName.setText(retailerBean.getOwnerName());
        } else {
            ll_owner_name.setVisibility(View.GONE);
        }
        tvAddress.setText(retailerBean.getAddress1());

        tvRetailerCategory.setText(retailerBean.getCPTypeDesc());   // TODO clarfication req
        tvClassification.setText(retailerBean.getClassification());   // TODO clarfication req

        tvWeeklyOff.setText(retailerBean.getWeeklyOffDesc());
        if (!retailerBean.getLandMark().equalsIgnoreCase("")) {
            ll_location_layout.setVisibility(View.VISIBLE);
            tvLandmark.setText(retailerBean.getLandMark());
        } else {
            ll_location_layout.setVisibility(View.GONE);
        }
        tvGSTNo.setText(retailerBean.getTax1());
        try {
            if (mStrGSTREGStatus.equalsIgnoreCase(Constants.TaxRegStatusNotDone) || mStrGSTREGStatus.equalsIgnoreCase("")) {
                tvGSTText.setTextColor(getResources().getColor(R.color.RejectedColor));
            } else {
                tvGSTText.setTextColor(getResources().getColor(R.color.ClosedColor));
            }
        } catch (Exception e) {
            tvGSTText.setTextColor(getResources().getColor(R.color.RejectedColor));
            e.printStackTrace();
        }


       /* if(mStrGSTIN.equalsIgnoreCase("")){
            ll_tax_status.setVisibility(View.GONE);
        }else{
            ll_tax_status.setVisibility(View.VISIBLE);
        }*/
        if (retailerBean.getLatVal() == 0.0 && retailerBean.getLongVal() == 0.0) {
            tvLocationVal.setVisibility(View.INVISIBLE);
//            ivLocationCapture.setVisibility(View.VISIBLE);
        } else {
//            ivLocationCapture.setVisibility(View.GONE);
            SpannableString content = new SpannableString(String.valueOf(retailerBean.getLatVal()) + ", " + String.valueOf(retailerBean.getLongVal()));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvLocationVal.setText(content);
            //tvLocationVal.setText(String.valueOf(retailerBean.getLatVal()) + ", " + String.valueOf(retailerBean.getLongVal()));
        }

        Drawable collStatus = SOUtils.displayTaxRegStatusImage(retailerBean.getLatVal(), retailerBean.getLongVal(), getActivity());
        iv_location.setImageDrawable(collStatus);
        ivLocationCapture.setVisibility(View.GONE);
    }

    private void onNotPostedRetailDetails() {
        String strJson = retailerBean.getFetchJsonHeaderObject();
        try {
            JSONObject fetchJsonHeaderObject = new JSONObject(strJson);
            mDistributorName = fetchJsonHeaderObject.getString(Constants.ParentName);
            mStrOwnerName = fetchJsonHeaderObject.getString(Constants.OwnerName);
            mDouLatVal = fetchJsonHeaderObject.getDouble(Constants.Latitude);
            mDouLongVal = fetchJsonHeaderObject.getDouble(Constants.Longitude);
            strAddress = Constants.getAddress(fetchJsonHeaderObject);
            mContactNum = fetchJsonHeaderObject.getString(Constants.MobileNo);
            mStrEmailID = fetchJsonHeaderObject.getString(Constants.EmailID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        selCPTypeDesc = retailerBean.getGroup3Desc();
        mClassification = retailerBean.getGroup4Desc();
        mWeeklyOff = retailerBean.getWeeklyOffDesc();

    }


    private void onRetailerDetails() {
        try {
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            if (retilerEntity != null) {
                oDataProperties = retilerEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.EmailID);
                mStrEmailID = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.ParentName);
                mDistributorName = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.PartnerMgrGUID);

                try {
                    ODataGuid mParGUID = (ODataGuid) oDataProperty.getValue();
                    partnerGUID = mParGUID.guidAsString36();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (mDistributorName != null && !mDistributorName.equalsIgnoreCase("")) {

                    } else {
                        mDistributorName = Constants.getDistNameFromCPDMSDIV(mStrCPGUID.toUpperCase(), partnerGUID.toUpperCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                oDataProperty = oDataProperties.get(Constants.MobileNo);
                mContactNum = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.CPTypeID);
                mRetCategory = (String) oDataProperty.getValue();

                oDataProperty = oDataProperties.get(Constants.WeeklyOffDesc);
                mWeeklyOff = (String) oDataProperty.getValue();
                oDataProperty = oDataProperties.get(Constants.DOB);
//            mDOB = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
                mDOB = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());
                oDataProperty = oDataProperties.get(Constants.Anniversary);
//            mAnniversary = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
                mAnniversary = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());
                oDataProperty = oDataProperties.get(Constants.CPTypeID);
                mStrCPTypeId = (String) oDataProperty.getValue();

                oDataProperty = oDataProperties.get(Constants.CPGUID);
                mCpGuid = (ODataGuid) oDataProperty.getValue();

                selCPTypeDesc = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.Group3Desc + " &$filter="
                        + Constants.CPGUID + " eq guid'" + mCpGuid.guidAsString36().toUpperCase() + "'", Constants.Group3Desc);
                mClassification = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.Group4Desc + " &$filter="
                        + Constants.CPGUID + " eq guid'" + mCpGuid.guidAsString36().toUpperCase() + "'", Constants.Group4Desc);

                strAddress = SOUtils.getCustomerDetailsAddressValue(oDataProperties);
                oDataProperty = oDataProperties.get(Constants.OwnerName);
                mStrOwnerName = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

                oDataProperty = oDataProperties.get(Constants.Latitude);
                BigDecimal mDecimalLatitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

                try {
                    if (mDecimalLatitude != null) {
                        mDouLatVal = mDecimalLatitude.doubleValue();
                    } else {
                        mDouLatVal = 0.0;
                    }
                } catch (Exception e) {
                    mDouLatVal = 0.0;
                }

                oDataProperty = oDataProperties.get(Constants.Longitude);
                BigDecimal mDecimalLongitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

                try {
                    if (mDecimalLongitude != null) {
                        mDouLongVal = mDecimalLongitude.doubleValue();
                    } else {
                        mDouLongVal = 0.0;
                    }
                } catch (Exception e) {
                    mDouLongVal = 0.0;
                }

                oDataProperty = oDataProperties.get(Constants.Landmark);
                mStrLandmark = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

                try {
                    oDataProperty = oDataProperties.get(Constants.Tax1);
                    mStrGSTIN = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

                    oDataProperty = oDataProperties.get(Constants.TaxRegStatus);
                    mStrGSTREGStatus = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
                } catch (Exception e) {
                    mStrGSTIN = "";
                    mStrGSTREGStatus = "";
                }
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.call:
                if (comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                    onCallRetailerApproval();
                } else {
                    onCall();
                }
                fabtoolbarContainer.hide();
                break;
            case R.id.sms:
                if (comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                    onSMSRetailerApproval();
                } else {
                    onSMS();
                }
                fabtoolbarContainer.hide();
                break;
            case R.id.mail:
                if (comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                    onMailRetailerApproval();
                } else {
                    onMail();
                }
                fabtoolbarContainer.hide();
                break;
           /* case R.id.tv_email_id:
                onMail();
                break;*/
            case R.id.whats_app:
                if (comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
                    whatsAppCallRetailerApproval();
                } else {
                    whatsAppCall();
                }
                fabtoolbarContainer.hide();
                break;
            case R.id.fabtoolbar:
                fabtoolbarContainer.show();
                break;
            case R.id.ivLocationCapture:
                if (mDouLatVal == 0.0 && mDouLongVal == 0.0) {
                    setLatLongVal();
                } else {
                    openMAP();
                }
             //   setLatLongVal();
                break;
            case R.id.iv_location:
                if (mDouLatVal == 0.0 && mDouLongVal == 0.0) {
                    setLatLongVal();
                } else {
                    openMAP();
                }
               // setLatLongVal();
                break;
            case R.id.tvLocationVal:
               /* if (mDouLatVal == 0.0 && mDouLongVal == 0.0) {
                    setLatLongVal();
                } else {
                    openMAP();
                }*/
                 setLatLongVal();
                break;
            case R.id.tvApprove:
                fabtoolbarContainer.hide();
                approveCredit();
                break;
            case R.id.tvReject:
                fabtoolbarContainer.hide();
                rejectCredit();
                break;
          /*  case R.id.tvApproveFab:
                fabtoolbarContainer.hide();
                approveCredit();
                break;
            case R.id.tvRejectFab:
                fabtoolbarContainer.hide();
                rejectCredit();
                break;*/

        }
    }

    private void openMAP() {
        String strUri = "http://maps.google.com/maps?q=loc:" + mDouLatVal + "," + mDouLongVal + " (" + mStrRetName + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    /*
     * TODO This method make a whats up call.
     */
    private void whatsAppCall() {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
            sendIntent.putExtra(Constants.jid, Constants.getCountryCode(mContext) + mContactNum + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            UtilConstants.showAlert(getString(R.string.alert_whatsapp_not_installed), getActivity());
        }
    }

    /*
     * TODO This method make a whats up call.
     */
//    private void whatsAppCall() {
//        PackageManager packageManager = mContext.getPackageManager();
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        try {
//            String url = "https://api.whatsapp.com/send?phone="+ Constants.getCountryCode(mContext)+mContactNum +"&text=" + URLEncoder.encode("", "UTF-8");
//            i.setPackage("com.whatsapp");
//            i.setData(Uri.parse(url));
//            if (i.resolveActivity(packageManager) != null) {
//                this.startActivity(i);
//            }
//        } catch (Exception e){
//            UtilConstants.showAlert(getString(R.string.alert_whatsapp_not_installed), getActivity());
//            e.printStackTrace();
//        }
//    }
    private void whatsAppCallRetailerApproval() {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
//            sendIntent.putExtra(Constants.jid, PhoneNumberUtils.stripSeparators(retailerBean.
//
// Number()) + Constants.whatsapp_domainname);
            sendIntent.putExtra(Constants.jid, Constants.getCountryCode(mContext) + retailerBean.getMobileNumber() + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            UtilConstants.showAlert(getString(R.string.alert_whatsapp_not_installed), getActivity());
        }
    }

   /* private void whatsAppCallRetailerApproval() {
        PackageManager packageManager = mContext.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone="+ Constants.getCountryCode(mContext)+retailerBean.getMobileNumber() +"&text=" + URLEncoder.encode("", "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                this.startActivity(i);
            }
        } catch (Exception e){
            UtilConstants.showAlert(getString(R.string.alert_whatsapp_not_installed), getActivity());
            e.printStackTrace();
        }
    }*/

    /*
     * TODO This method make a sms.
     */
    private void onSMS() {
        if (!mContactNum.equalsIgnoreCase("")) {
            Uri smsUri = Uri.parse(Constants.sms_txt + mContactNum);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        } else {
            UtilConstants.showAlert(getString(R.string.alert_mobile_no_maintend), getActivity());

        }
    }

    private void onSMSRetailerApproval() {
        if (!retailerBean.getMobileNumber().equalsIgnoreCase("")) {
            Uri smsUri = Uri.parse(Constants.sms_txt + retailerBean.getMobileNumber());
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        } else {
            UtilConstants.showAlert(getString(R.string.alert_mobile_no_maintend), getActivity());

        }
    }

    /*
     * TODO This method make a email.
     */
    private void onMail() {
        try {
            if (!mStrEmailID.equalsIgnoreCase("")) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] emailList = {mStrEmailID};
                email.putExtra(Intent.EXTRA_EMAIL, emailList);
                email.setType(Constants.plain_text);
                startActivity(Intent.createChooser(email, Constants.send_email));
            } else {
                UtilConstants.showAlert(getString(R.string.alert_mail_id_not_maintend), getActivity());
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void onMailRetailerApproval() {
        try {
            if (!retailerBean.getEmailId().equalsIgnoreCase("")) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] emailList = {retailerBean.getEmailId()};
                email.putExtra(Intent.EXTRA_EMAIL, emailList);
                email.setType(Constants.plain_text);
                startActivity(Intent
                        .createChooser(email, Constants.send_email));
            } else {
                UtilConstants.showAlert(getString(R.string.alert_mail_id_not_maintend), getActivity());
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*
     * TODO This method make a call.
     */
    private void onCall() {
        try {
            if (!mContactNum.equalsIgnoreCase("")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(Constants.tel_txt + (mContactNum)));
                startActivity(dialIntent);
            } else {
                UtilConstants.showAlert(getString(R.string.alert_mobile_no_maintend), getActivity());
            }
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void onCallRetailerApproval() {
        try {
            if (!retailerBean.getMobileNumber().equalsIgnoreCase("")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(Constants.tel_txt + (retailerBean.getMobileNumber())));
                startActivity(dialIntent);
            } else {
                UtilConstants.showAlert(getString(R.string.alert_mobile_no_maintend), getActivity());
            }
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void setLatLongVal() {
        if (!comeFrom.equalsIgnoreCase(Constants.RetailerApprovalList)) {
            String mStrAlertMsg = getString(R.string.update_lat_long_retailer);
            alertMSGLatAndLong(mStrAlertMsg);
        }
    }

    private void onPermissionLatLong() {
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void alertMSGLatAndLong(String mAlertMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        builder.setMessage(mAlertMsg).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onPermissionLatLong();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
        Constants.getLocation(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    setLatLonValues();
                    updateLatAndLonToChannelPartner();
                }
            }
        });
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void setLatLonValues() {
        mDouLongVal = UtilConstants.longitude;
        mDouLatVal = UtilConstants.latitude;
        SpannableString content = new SpannableString(UtilConstants.latitude + "" + ", " + UtilConstants.longitude + "");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvLocationVal.setText(content);
      //  tvLocationVal.setText(UtilConstants.latitude + "" + ", " + UtilConstants.longitude + "");
        tvLocationVal.setVisibility(View.VISIBLE);
        iv_location.setVisibility(View.VISIBLE);
        ivLocationCapture.setVisibility(View.GONE);

        Drawable collStatus = SOUtils.displayTaxRegStatusImage(mDouLatVal, mDouLongVal, getActivity());
        iv_location.setImageDrawable(collStatus);
    }

    private void updateLatAndLonToChannelPartner() {
        try {
            Hashtable table = new Hashtable();
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = null;
            try {
                retilerEntity = OfflineManager.getRetDetails(retDetgry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            try {
                oDataProperties = retilerEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.CPGUID);
                table.put(Constants.CPGUID, mStrCPGUID.toUpperCase());
                //noinspection unchecked
                try {
                    oDataProperty = oDataProperties.get(Constants.Anniversary);
                    //noinspection unchecked
                    table.put(Constants.Anniversary, oDataProperty.getValue());
                    oDataProperty = oDataProperties.get(Constants.DOB);
                    //noinspection unchecked
                    table.put(Constants.DOB, oDataProperty.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                oDataProperty = oDataProperties.get(Constants.MobileNo);
                table.put(Constants.MobileNo, (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "");
                table.put(Constants.OutletName, (String) oDataProperties.get(Constants.OutletName).getValue() != null ? (String) oDataProperties.get(Constants.OutletName).getValue() : "");
                table.put(Constants.OwnerName, (String) oDataProperties.get(Constants.OwnerName).getValue() != null ? (String) oDataProperties.get(Constants.OwnerName).getValue() : "");
                table.put(Constants.RetailerProfile, (String) oDataProperties.get(Constants.RetailerProfile).getValue() != null ? (String) oDataProperties.get(Constants.RetailerProfile).getValue() : "");
                table.put(Constants.Group2, (String) oDataProperties.get(Constants.Group2).getValue() != null ? (String) oDataProperties.get(Constants.Group2).getValue() : "");
                table.put(Constants.PAN, (String) oDataProperties.get(Constants.PAN).getValue() != null ? (String) oDataProperties.get(Constants.PAN).getValue() : "");
                table.put(Constants.VATNo, (String) oDataProperties.get(Constants.VATNo).getValue() != null ? (String) oDataProperties.get(Constants.VATNo).getValue() : "");
                table.put(Constants.EmailID, (String) oDataProperties.get(Constants.EmailID).getValue() != null ? (String) oDataProperties.get(Constants.EmailID).getValue() : "");
                table.put(Constants.MobileNo, (String) oDataProperties.get(Constants.MobileNo).getValue() != null ? (String) oDataProperties.get(Constants.MobileNo).getValue() : "");
                table.put(Constants.PostalCode, (String) oDataProperties.get(Constants.PostalCode).getValue() != null ? (String) oDataProperties.get(Constants.PostalCode).getValue() : "");
                table.put(Constants.Landmark, (String) oDataProperties.get(Constants.Landmark).getValue() != null ? (String) oDataProperties.get(Constants.Landmark).getValue() : "");
                table.put(Constants.Address1, (String) oDataProperties.get(Constants.Address1).getValue() != null ? (String) oDataProperties.get(Constants.Address1).getValue() : "");
                table.put(Constants.CPTypeID, (String) oDataProperties.get(Constants.CPTypeID).getValue() != null ? (String) oDataProperties.get(Constants.CPTypeID).getValue() : "");
                table.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue() != null ? oDataProperties.get(Constants.Latitude).getValue() : 0.0);
                table.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue() != null ? oDataProperties.get(Constants.Longitude).getValue() : 0.0);
                table.put(Constants.ParentID, (String) oDataProperties.get(Constants.ParentID).getValue() != null ? (String) oDataProperties.get(Constants.ParentID).getValue() : "");
                table.put(Constants.ParentTypeID, (String) oDataProperties.get(Constants.ParentTypeID).getValue() != null ? (String) oDataProperties.get(Constants.ParentTypeID).getValue() : "");
                table.put(Constants.ParentName, (String) oDataProperties.get(Constants.ParentName).getValue() != null ? (String) oDataProperties.get(Constants.ParentName).getValue() : "");
                table.put(Constants.StateID, (String) oDataProperties.get(Constants.StateID).getValue() != null ? (String) oDataProperties.get(Constants.StateID).getValue() : "");
                table.put(Constants.PartnerMgrGUID, (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() != null ? (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() : "");
                table.put(Constants.CityDesc, (String) oDataProperties.get(Constants.CityDesc).getValue() != null ? (String) oDataProperties.get(Constants.CityDesc).getValue() : "");
                table.put(Constants.CityID, (String) oDataProperties.get(Constants.CityID).getValue() != null ? (String) oDataProperties.get(Constants.CityID).getValue() : "");
                table.put(Constants.DistrictDesc, (String) oDataProperties.get(Constants.DistrictDesc).getValue() != null ? (String) oDataProperties.get(Constants.DistrictDesc).getValue() : "");
                table.put(Constants.DistrictID, (String) oDataProperties.get(Constants.DistrictID).getValue() != null ? (String) oDataProperties.get(Constants.DistrictID).getValue() : "");
                table.put(Constants.CPNo, (String) oDataProperties.get(Constants.CPNo).getValue() != null ? (String) oDataProperties.get(Constants.CPNo).getValue() : "");
                table.put(Constants.CPUID, (String) oDataProperties.get(Constants.CPUID).getValue() != null ? (String) oDataProperties.get(Constants.CPUID).getValue() : "");
                table.put(Constants.StatusID, (String) oDataProperties.get(Constants.StatusID).getValue() != null ? (String) oDataProperties.get(Constants.StatusID).getValue() : "");
                table.put(Constants.ApprvlStatusID, (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() != null ? (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() : "");
                table.put(Constants.WeeklyOff, (String) oDataProperties.get(Constants.WeeklyOff).getValue() != null ? (String) oDataProperties.get(Constants.WeeklyOff).getValue() : "");
                table.put(Constants.Tax1, (String) oDataProperties.get(Constants.Tax1).getValue() != null ? (String) oDataProperties.get(Constants.Tax1).getValue() : "");
                table.put(Constants.TaxRegStatus, (String) oDataProperties.get(Constants.TaxRegStatus).getValue() != null ? (String) oDataProperties.get(Constants.TaxRegStatus).getValue() : "");
                table.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                table.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                table.put(Constants.IsLatLongUpdate, Constants.X);


                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                table.put(Constants.LOGINID, loginIdVal);

                table.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')");
                if (retilerEntity.getEtag() != null) {
                    table.put(Constants.Etag, retilerEntity.getEtag());
                }
                table.put(Constants.comingFrom, "");
                table.put(Constants.CreatedBy, oDataProperties.get(Constants.CreatedBy).getValue() != null ? (String) oDataProperties.get(Constants.CreatedBy).getValue() : "");
                table.put(Constants.CreatedOn, oDataProperties.get(Constants.CreatedOn).getValue() != null ? oDataProperties.get(Constants.CreatedOn).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }


            if (retilerEntity != null) {
                try {
                    OfflineManager.updateRetilerBatchReq(table, Constants.Latitude, retilerEntity.getProperties());
                } catch (ODataParserException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

//        UtilConstants.showAlert(getString(R.string.str_lat_long_update),getActivity());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (!comeFrom.equalsIgnoreCase("NotPostedRetailer")) {
            inflater.inflate(R.menu.menu_accept_reject, menu);
            menu.removeItem(R.id.menu_accept);
            menu.removeItem(R.id.menu_reject);
        } else {
            inflater.inflate(R.menu.not_postd_retailer_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accept:
                approveCredit();
//                    Log.d("hell","hell");
                return true;
            case R.id.menu_reject:
                rejectCredit();
                return true;
            case R.id.edit_NPR:
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.not_save_retail_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (b) {
                            Constants.FLAG = true;
                            FragmentTransaction changeFragment = getActivity().getSupportFragmentManager().beginTransaction();
                            ChangeMobileNoFragment changeMobileNoFragment = new ChangeMobileNoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("FetchJsonHeaderObject",retailerBean.getFetchJsonHeaderObject());
                            bundle.putString("DOCID",retailerBean.getCustomerId());
                            changeMobileNoFragment.setArguments(bundle);
                            changeMobileNoFragment.show(changeFragment, "dialog");
                        }
                    }
                });
                return true;


            case R.id.delete_NPR:
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.delete_retail_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (b) {
                            Constants.removeDataValtFromSharedPref(getActivity(), Constants.CPList, retailerBean.getCustomerId(),false);
                            Constants.removeDataValtFromSharedPref(getActivity(), Constants.NOT_POSTED_RETAILERS, retailerBean.getCustomerId(),false);
                            ConstantsUtils.storeInDataVault(retailerBean.getCustomerId(), "",getActivity());
                            Constants.removeFromSharKey(retailerBean.getCustomerId(),"",getActivity(),true);
                            Constants.FLAG = true;
                            getActivity().finish();
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNotPostedRetailerActivity() {
        ArrayList<String> alCPList = null;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.NOT_POSTED_RETAILERS,0);
        try {
            Set<String> set = sharedPreferences.getStringSet(Constants.duplicateCPList,null);
            alCPList = new ArrayList<>(set);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getActivity(), NotPostedRetailerActivity.class);
        intent.putExtra(Constants.duplicateCPList,alCPList);
        startActivity(intent);
        getActivity().finish();
    }

    private void rejectCredit() {
        if (!TextUtils.isEmpty(mStrInstanceId)) {
            SOUtils.showCommentsDialog(getActivity(), new CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        mStrID = Constants.RejectStatus;
                        approveOrder(mStrInstanceId, Constants.RejectStatus, mStrCPGUID, description + "");
                    }
                }
            }, getString(R.string.reject_title_comments));
        }
    }

    private void approveCredit() {
        if (!TextUtils.isEmpty(mStrInstanceId)) {
            SOUtils.showCommentsDialog(getActivity(), new CustomDialogCallBack() {
                @Override
                public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                    if (userClicked) {
                        mStrID = Constants.ApprovalStatus01;
                        approveOrder(mStrInstanceId, Constants.ApprovalStatus01, mStrCPGUID, description + "");
                    }
                }
            }, getString(R.string.approve_title_comments));
        }
    }

    /*approve order*/
    private void approveOrder(String mStrInstanceId, String desisionKey, String soNo, String comments) {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            RetailerApprovalListActivity.isRefreshList = true;
            ConstantsUtils.APPROVALERRORMSG = "";
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            masterHeaderTable.clear();
            masterHeaderTable.put(Constants.InstanceID, mStrInstanceId);
            masterHeaderTable.put(Constants.EntityType, "CP");
            masterHeaderTable.put(Constants.DecisionKey, desisionKey);
            masterHeaderTable.put(Constants.LoginID, loginIdVal);
            masterHeaderTable.put(Constants.EntityKey, soNo);
            masterHeaderTable.put(Constants.Comments, comments);

            JSONObject headerObject = new JSONObject();
            try {
                headerObject.putOpt(Constants.InstanceID, masterHeaderTable.get(Constants.InstanceID));
                headerObject.putOpt(Constants.EntityType, masterHeaderTable.get(Constants.EntityType));
                headerObject.putOpt(Constants.DecisionKey, masterHeaderTable.get(Constants.DecisionKey));
                headerObject.putOpt(Constants.LoginID, masterHeaderTable.get(Constants.LoginID));
                headerObject.putOpt(Constants.EntityKey, masterHeaderTable.get(Constants.EntityKey));
                headerObject.putOpt(Constants.Comments, masterHeaderTable.get(Constants.Comments));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String qry = Constants.Tasks + "(InstanceID='" + headerObject.optString(Constants.InstanceID) + "',EntityType='CP')";
            progressDialog = ConstantsUtils.showProgressDialog(mContext, "Update data please wait...");
//            new DirecySyncAsyncTask(mContext, AddressFragment.this, AddressFragment.this, masterHeaderTable, null, 2).execute();
            OnlineManager.updateEntity("",headerObject.toString(),qry, AddressFragment.this,getActivity());
            DashboardPresenterImpl.isReloadRETApproval = true;
        } else {
            ConstantsUtils.displayLongToast(getActivity(), getString(R.string.no_network_conn));
        }
    }

    @Override
    public void onStatus(boolean status, String values) {

    }

    @Override
    public void onRequestError(int i, Exception e) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String statusMsg = getString(R.string.retailer_details_msg_approved);
        if (!TextUtils.isEmpty(ConstantsUtils.APPROVALERRORMSG)) {
            statusMsg = ConstantsUtils.APPROVALERRORMSG;
        } else if (mStrID.equalsIgnoreCase(Constants.RejectStatus))
            statusMsg = getString(R.string.retailerapproval_details_msg_rejected);
        UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.ret_apprvol_failed, statusMsg), getString(R.string.ok), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {

            }
        });
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        String statusMsg = getString(R.string.retailer_details_msg_approved);
        if (mStrID.equalsIgnoreCase(Constants.RejectStatus))
            statusMsg = getString(R.string.retailerapproval_details_msg_rejected);
        UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.retailer_apprvol_success, statusMsg), getString(R.string.ok), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                onListScreen();
            }
        });
    }

    private void onListScreen() {
        DashboardPresenterImpl.isReloadRETApproval = true;
        getActivity().finish();
    }

    public void closeFabLayout() {
        try {
            fabtoolbarContainer.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
