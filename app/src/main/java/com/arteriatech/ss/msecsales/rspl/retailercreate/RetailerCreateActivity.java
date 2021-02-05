package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.home.MainActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.UserCustomersBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class RetailerCreateActivity extends AppCompatActivity implements RetailerCreateView {

    private Toolbar toolbar;
    private Context mContext;
    TextView tvLocationVal;
    ImageView iv_location;
    MaterialDesignSpinner spRetailerType,spCountry,spRetState,spBeatName,spDistributor;
    EditText etOwnerName,etOutletName,et_address,etAddressTwo,etAddressThree,etAddressFour,etLandMark,etDistrict,etTown,etCity,
            etPinCode,etIDOne,etIDTwo,etBussIDOne,etBussIDTwo,etCPUID,etMailID,etMobile,etMobileTwo,etTelphone,etFax;
    EditText etDOB,etAnniversary;
    TextInputLayout tiOwnerName,tiOutletName,tiDOB,tiAnniversary,tiAddress,tiAddressTwo,tiAddressThree,tiAddressFour,
            tiLandMark,tiDistrict,tiCity,tiTown,tiPinCode,tiMobile,tiFax,tiMobileTwo,tiTelphone,timailid,tiBussIDTwo,tiCPUID,tiIDOne,tiBussIDOne;
    RetailerCreatePresenterImpl presenter;
    RetailerCreateBean retailerCreateBean =new RetailerCreateBean();
    ProgressDialog progressDialog=null;
    private Switch switchIskeyCP;
    RetailetLatLongCapture retailetLatLongCapture = null;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mYearAnnversary = 0;
    private int mMonthAnnversary = 0;
    private int mDayAnnversary = 0;
    private String dateOfBirth = "",dateOfAnnversary="";
    private static final int DATE_DIALOG_ID = 0;
    private static final int DATE_DIALOG_ID_ANNVERSARY = 1;
    private ProgressDialog pdLoadDialog;
    private TextInputLayout tiDistName=null;
    private EditText etDistNameVal =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_create);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = RetailerCreateActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.lbl_step_one_basic_data));
        initUI();
        retailerCreateBean = new RetailerCreateBean();
        presenter = new RetailerCreatePresenterImpl(RetailerCreateActivity.this, this, true, RetailerCreateActivity.this, retailerCreateBean);
        if (!Constants.restartApp(RetailerCreateActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI(){
        spRetailerType = (MaterialDesignSpinner) findViewById(R.id.spRetailerType);
        spCountry = (MaterialDesignSpinner) findViewById(R.id.spRetCountry);
        spRetState = (MaterialDesignSpinner) findViewById(R.id.spRetState);
        spBeatName = (MaterialDesignSpinner) findViewById(R.id.spBeatName);
        spDistributor = (MaterialDesignSpinner) findViewById(R.id.spDistributor);

        etOutletName = (EditText) findViewById(R.id.etOutletName);
        etOwnerName = (EditText) findViewById(R.id.etOwnerName);
        tiOutletName = (TextInputLayout) findViewById(R.id.tiOutletName);
        tiOwnerName = (TextInputLayout) findViewById(R.id.tiOwnerName);
        tiDistName = (TextInputLayout) findViewById(R.id.tiDistName);

        tiAddress = (TextInputLayout) findViewById(R.id.tiAddress);
        tiAddressTwo = (TextInputLayout) findViewById(R.id.tiAddressTwo);
        tiAddressThree = (TextInputLayout) findViewById(R.id.tiAddressThree);
        tiAddressFour = (TextInputLayout) findViewById(R.id.tiAddressFour);
        tiLandMark = (TextInputLayout) findViewById(R.id.tiLandMark);
        tiDistrict = (TextInputLayout) findViewById(R.id.tiDistrict);
        tiCity = (TextInputLayout) findViewById(R.id.tiCity);
        tiTown = (TextInputLayout) findViewById(R.id.tiTown);
        tiPinCode = (TextInputLayout) findViewById(R.id.tiPinCode);
        tiMobile = (TextInputLayout) findViewById(R.id.tiMobile);
        tiMobileTwo = (TextInputLayout) findViewById(R.id.tiMobileTwo);
        tiTelphone = (TextInputLayout) findViewById(R.id.tiTelphone);
        tiFax = (TextInputLayout) findViewById(R.id.tiFax);
        timailid = (TextInputLayout) findViewById(R.id.timailid);
        switchIskeyCP = (Switch) findViewById(R.id.switchIskeyCP);
        etDOB = (EditText) findViewById(R.id.etDOB);
        etDistNameVal = (EditText) findViewById(R.id.etDistNameVal);
        etAnniversary = (EditText) findViewById(R.id.etAnniversary);
        tiAnniversary = (TextInputLayout) findViewById(R.id.tiAnniversary);
        tiDOB = (TextInputLayout) findViewById(R.id.tiDOB);
        tiBussIDTwo = (TextInputLayout) findViewById(R.id.tiBussIDTwo);
        tiCPUID = (TextInputLayout) findViewById(R.id.tiCPUID);
        tiIDOne = (TextInputLayout) findViewById(R.id.tiIDOne);
        tiBussIDOne = (TextInputLayout) findViewById(R.id.tiBussIDOne);

        tvLocationVal = (TextView) findViewById(R.id.tvLocationVal);
        iv_location = (ImageView) findViewById(R.id.iv_location);

//        etOwnerName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        etOwnerName.setFilters(Constants.getAlphabetOnly(40));
//        etOutletName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        etOutletName.setFilters(Constants.getAlphabetOnly(40));
        et_address = (EditText) findViewById(R.id.etAddress);
        etAddressTwo = (EditText) findViewById(R.id.etAddressTwo);
        etAddressThree = (EditText) findViewById(R.id.etAddressThree);
        etAddressFour = (EditText) findViewById(R.id.etAddressFour);
        etLandMark = (EditText) findViewById(R.id.etLandMark);
        etDistrict = (EditText) findViewById(R.id.etDistrict);
        etCity = (EditText) findViewById(R.id.etCity);
        etTown = (EditText) findViewById(R.id.etTown);
        etPinCode = (EditText) findViewById(R.id.etPinCode);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etMobileTwo = (EditText) findViewById(R.id.etMobileTwo);
        etTelphone = (EditText) findViewById(R.id.etTelphone);
        etFax = (EditText) findViewById(R.id.etFax);
        etMailID = (EditText) findViewById(R.id.etMailID);
        etIDOne = (EditText) findViewById(R.id.etIDOne);
        etIDTwo = (EditText) findViewById(R.id.etIDTwo);
        etBussIDOne = (EditText) findViewById(R.id.etBussIDOne);
        etBussIDTwo = (EditText) findViewById(R.id.etBussIDTwo);
        etCPUID = (EditText) findViewById(R.id.etCPUID);
//        et_address.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        et_address.setFilters(Constants.getAlphabetOnly(35));
//        etAddressTwo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        etAddressTwo.setFilters(Constants.getAlphabetOnly(35));
//        etAddressThree.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        etAddressThree.setFilters(Constants.getAlphabetOnly(35));
//        etAddressFour.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        etAddressFour.setFilters(Constants.getAlphabetOnly(35));
//        etLandMark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        etLandMark.setFilters(Constants.getAlphabetOnly(30));
//        etDistrict.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        etDistrict.setFilters(Constants.getAlphabetOnly(40));
//        etCity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        etCity.setFilters(Constants.getAlphabetOnly(40));
//        etTown.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        etTown.setFilters(Constants.getAlphabetOnly(40));
//        etIDOne.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        etIDTwo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        etBussIDOne.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        etBussIDTwo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        etCPUID.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        etMailID.setFilters(new InputFilter[]{new InputFilter.LengthFilter(45)});
        tvLocationVal.setText("0.00, 0.00");
        tiBussIDTwo.setVisibility(View.GONE);
        tiCPUID.setVisibility(View.GONE);
        tiCity.setVisibility(View.GONE);
        tiDistrict.setVisibility(View.GONE);

        InputFilter[] voterIDFilter = new InputFilter[2];
        voterIDFilter[0] = new InputFilter.LengthFilter(12);
        voterIDFilter[1] = Constants.getNumberAlphabet();
        etIDTwo.setFilters(voterIDFilter);

        InputFilter[] panIDFilter = new InputFilter[2];
        panIDFilter[0] = new InputFilter.LengthFilter(10);
        panIDFilter[1] = Constants.getNumberAlphabetCaps();
        etBussIDOne.setFilters(panIDFilter);
        switchIskeyCP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                retailerCreateBean.setKeyCP(isChecked);

            }
        });
        try {
            RetailetLatLongCapture.retailetLatLongCapture = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        onPermissionLatLong();
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onPermissionLatLong();
                setLatLonValues();

            }
        });

        editTextOntextChangeLisners();

        final Calendar calDob = Calendar.getInstance();
        mYear = calDob.get(Calendar.YEAR);
        mMonth = calDob.get(Calendar.MONTH);
        mDay = calDob.get(Calendar.DAY_OF_MONTH);

        final Calendar calAnnversary = Calendar.getInstance();
        mYearAnnversary = calAnnversary.get(Calendar.YEAR);
        mMonthAnnversary = calAnnversary.get(Calendar.MONTH);
        mDayAnnversary = calAnnversary.get(Calendar.DAY_OF_MONTH);

        tiDOB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID).show();
            }
        });
        etDOB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID).show();
            }
        });
        tiAnniversary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID_ANNVERSARY).show();
            }
        });
        etAnniversary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID_ANNVERSARY).show();
            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(RetailerCreateActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null&&progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(mContext,message);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(RetailerCreateActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
//                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity(){
        Intent intentNavPrevScreen = new Intent(this, MainActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void displayCPType(final ArrayList<ValueHelpBean> cpType, final ArrayList<ValueHelpBean> alCountry, final ArrayList<ValueHelpBean> alState, final ArrayList<RoutePlanBean> alBeat,final String defaultStateID) {
        ArrayAdapter<ValueHelpBean> adapterRetType = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, cpType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRetailerType, position, getContext());
                return v;
            }
        };

        adapterRetType.setDropDownViewResource(R.layout.spinnerinside);
        spRetailerType.setAdapter(adapterRetType);
        spRetailerType.showFloatingLabel();
        spRetailerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setCPTypeID(cpType.get(position).getID());
                retailerCreateBean.setCPTypeDesc(cpType.get(position).getDescription());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterCounry = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alCountry) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCountry, position, getContext());
                return v;
            }
        };


        adapterCounry.setDropDownViewResource(R.layout.spinnerinside);
        spCountry.setAdapter(adapterCounry);
        spCountry.showFloatingLabel();
        spCountry.setClickable(false);
        spCountry.setEnabled(false);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setCountry(alCountry.get(position).getID());
                retailerCreateBean.setCountryName(alCountry.get(position).getDescription());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        ArrayAdapter<ValueHelpBean> adapterState = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alState) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRetState, position, getContext());
                return v;
            }
        };

        adapterState.setDropDownViewResource(R.layout.spinnerinside);
        spRetState.setAdapter(adapterState);
        spRetState.showFloatingLabel();
        spRetState.setEnabled(false);
       /* try {
            if(alState!=null && alState.size()>0){
                for (int i = 0; i < alState.size(); i++) {
                    if (defaultStateID.equalsIgnoreCase(alState.get(i).getID())) {
                        spRetState.setSelection(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        spRetState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setStateID(alState.get(position).getID());
                retailerCreateBean.setStateDesc(alState.get(position).getDescription());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<RoutePlanBean> adapterRouteName = new ArrayAdapter<RoutePlanBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alBeat) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBeatName, position, getContext());
                return v;
            }
        };

        adapterRouteName.setDropDownViewResource(R.layout.spinnerinside);
        spBeatName.setAdapter(adapterRouteName);
        spBeatName.showFloatingLabel();
        spBeatName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setRouteDesc(alBeat.get(position).getDescription());
                retailerCreateBean.setRouteSchGUID(alBeat.get(position).getRschGuid());
                retailerCreateBean.setRouteID(alBeat.get(position).getRoutId());
                retailerCreateBean.setRouteDist(alBeat.get(position).getCPGUID());
                //retailerCreateBean.setRouteDistName(alBeat.get(position).getDistName());
                if(retailerCreateBean.getRouteID().equalsIgnoreCase("")){
                    spDistributor.setVisibility(View.GONE);
                    tiDistName.setVisibility(View.GONE);
                }else{
                    tiDistName.setVisibility(View.GONE);
                  //  etDistNameVal.setText(retailerCreateBean.getRouteDistName());
                    spDistributor.setVisibility(View.VISIBLE);
                    showdistributorSelection(alBeat.get(position).getDistributors(),alState);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showdistributorSelection(final List<UserCustomersBean> list, final ArrayList<ValueHelpBean> alState) {

        ArrayAdapter<UserCustomersBean> adapterState = new ArrayAdapter<UserCustomersBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue,list ) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDistributor, position, getContext());
                return v;
            }
        };

        adapterState.setDropDownViewResource(R.layout.spinnerinside);
        spDistributor.setAdapter(adapterState);
        spDistributor.showFloatingLabel();
       /* try {
            if(alState!=null && alState.size()>0){
                for (int i = 0; i < alState.size(); i++) {
                    if (defaultStateID.equalsIgnoreCase(alState.get(i).getID())) {
                        spRetState.setSelection(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           /*     retailerCreateBean.setStateID(list.get(position).getID());
                retailerCreateBean.setStateDesc(list.get(position).getDescription());*/
                retailerCreateBean.setRouteDistName(list.get(position).getDistibutorName());
                try {
                    if(alState!=null && alState.size()>0){
                        for (int i = 0; i < alState.size(); i++) {
                            if (list.get(position).getStateID().equalsIgnoreCase(alState.get(i).getID())) {
                                spRetState.setSelection(i);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    public void displayWeeklyOff(final ArrayList<ValueHelpBean> alProdRelInfo,final ArrayList<ValueHelpBean> altaxStatus) {

    }

    @Override
    public void displayDMSDivision(ArrayList<DMSDivisionBean> alWeeklyOff, final ArrayList<ValueHelpBean> alGrpOne,ArrayList<ValueHelpBean> alGrpFour,ArrayList<ValueHelpBean> alGrpFive) {

    }

    @Override
    public void displayGrpTwoValue(ArrayList<ValueHelpBean> alGrpTwo) {

    }

    @Override
    public void displayGrpThreeValue(ArrayList<ValueHelpBean> alGrpThree) {

    }

    @Override
    public void errorRetailerType(String message) {
        if (spRetailerType.getVisibility() == View.VISIBLE) {
            spRetailerType.setError(message);

//            spRetailerType.setFocusable(true);
//            spRetailerType.setFocusableInTouchMode(true);
//            spRetailerType.requestFocus();
        }
    }

    @Override
    public void errorOutletName(String message) {
        tiOutletName.setErrorEnabled(true);
        tiOutletName.setError(message);

//        View focusView = etOutletName;
//        focusView.requestFocus();
    }

    @Override
    public void errorOwnerName(String message) {
        tiOwnerName.setErrorEnabled(true);
        tiOwnerName.setError(message);

//        View focusView = etOwnerName;
//        focusView.requestFocus();
    }

    @Override
    public void errorAddressOne(String message) {
        tiAddress.setErrorEnabled(true);
        tiAddress.setError(message);

//        View focusView = et_address;
//        focusView.requestFocus();
    }

    @Override
    public void errorLandMArk(String message) {
        tiLandMark.setErrorEnabled(true);
        tiLandMark.setError(message);

//        View focusView = etLandMark;
//        focusView.requestFocus();
    }

    @Override
    public void errorCountry(String message) {
        if (spCountry.getVisibility() == View.VISIBLE) {
            spCountry.setError(message);

//            spCountry.setFocusable(true);
//            spCountry.setFocusableInTouchMode(true);
//            spCountry.requestFocus();
        }
    }

    @Override
    public void errorState(String message) {
        if (spRetState.getVisibility() == View.VISIBLE) {
            spRetState.setError(message);

//            spRetState.setFocusable(true);
//            spRetState.setFocusableInTouchMode(true);
//            spRetState.requestFocus();
        }
    }

    @Override
    public void errorRouteName(String message) {
        if (spBeatName.getVisibility() == View.VISIBLE) {
            spBeatName.setError(message);


//            spBeatName.setFocusable(true);
//            spBeatName.setFocusableInTouchMode(true);
//            spBeatName.requestFocus();
        }
    }

    @Override
    public void errorPostlcode(String message) {
        tiPinCode.setErrorEnabled(true);
        tiPinCode.setError(message);

//        View focusView = etPinCode;
//        focusView.requestFocus();
    }

    @Override
    public void errorMobileOne(String message) {
        tiMobile.setErrorEnabled(true);
        tiMobile.setError(message);

//        View focusView = etMobile;
//        focusView.requestFocus();
    }

    @Override
    public void errorMobileTwo(String message) {
        tiMobileTwo.setErrorEnabled(true);
        tiMobileTwo.setError(message);

//        View focusView = etMobileTwo;
//        focusView.requestFocus();
    }

    @Override
    public void errorID(String message) {
        tiIDOne.setErrorEnabled(true);
        tiIDOne.setError(message);

//        View focusView = etIDOne;
//        focusView.requestFocus();
    }

    @Override
    public void errorBussnessID(String message) {
        tiBussIDOne.setErrorEnabled(true);
        tiBussIDOne.setError(message);

    }

    @Override
    public void errorTelNo(String message) {
        tiTelphone.setErrorEnabled(true);
        tiTelphone.setError(message);

//        View focusView = etTelphone;
//        focusView.requestFocus();
    }

    @Override
    public void errorFaxNo(String message) {
        tiFax.setErrorEnabled(true);
        tiFax.setError(message);

//        View focusView = etFax;
//        focusView.requestFocus();
    }

    @Override
    public void errorEmailId(String message) {
        timailid.setErrorEnabled(true);
        timailid.setError(message);

//        View focusView = etMailID;
//        focusView.requestFocus();
    }

    @Override
    public void errorAnniversary(String message) {
        tiAnniversary.setErrorEnabled(true);
        tiAnniversary.setError(message);

//        View focusView = etAnniversary;
//        focusView.requestFocus();
    }

    @Override
    public void errorRemarks(String message) {

    }

    @Override
    public void errorOthers(String message) {

    }

    @Override
    public void errorDMSDiv(String message) {

    }

    @Override
    public void errorGroupOne(String message) {

    }

    @Override
    public void errorGroupTwo(String message) {

    }

    @Override
    public void errorGroupThree(String message) {

    }

    @Override
    public void errorGroupFour(String message) {

    }

    @Override
    public void errorGroupFive(String message) {

    }

    @Override
    public void errorDiscountPercentage(String message) {

    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(RetailerCreateActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    presenter.onSaveData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next_save, menu);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.menu_save);
        menu.removeItem(R.id.menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                //next step
                if (ConstantsUtils.isAutomaticTimeZone(RetailerCreateActivity.this)) {
                  /*  if(spBeatName!=null) {
                        spBeatName.setFocusable(false);
                        spBeatName.setFocusableInTouchMode(false);
                    }

                    if(spRetailerType!=null) {
                        spRetailerType.setFocusable(false);
                        spRetailerType.setFocusableInTouchMode(false);
                    }

                    if(spCountry!=null) {
                        spCountry.setFocusable(false);
                        spCountry.setFocusableInTouchMode(false);
                    }

                    if(spRetState!=null) {
                        spRetState.setFocusable(false);
                        spRetState.setFocusableInTouchMode(false);
                    }*/

                    if (presenter.validateFields(retailerCreateBean)) {
                        Intent intentRetailerDetails = new Intent(this, RetailerCreateSalesDataActivity.class);
                        intentRetailerDetails.putExtra(Constants.RetailerList, retailerCreateBean);
                        startActivity(intentRetailerDetails);
                    }else{
                        ConstantsUtils.displayShortToast(RetailerCreateActivity.this, getString(R.string.alert_please_fill_mandatoryor_fields));
                    }
                } else {
                    ConstantsUtils.showAutoDateSetDialog(RetailerCreateActivity.this);
                }
                break;
        }
        return true;
    }


    @SuppressLint("NewApi")
    private Dialog onDatePickerDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(this, mDateSetListener,
                        mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());
                return datePicker;
            case DATE_DIALOG_ID_ANNVERSARY:
                DatePickerDialog datePickerAnnversary = new DatePickerDialog(this, mDateSetAnnversaryListener,
                        mYearAnnversary, mMonthAnnversary, mDayAnnversary);
                Calendar cal = Calendar.getInstance();
                Date newDateAnnv = cal.getTime();
                datePickerAnnversary.getDatePicker().setMaxDate(newDateAnnv.getTime());
                return datePickerAnnversary;
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonth + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDay;
            if (mDay < 10)
                day = "0" + mDay;
            dateOfBirth = mYear + "-" + mon + "-" + day;
            retailerCreateBean.setDOB(dateOfBirth);
            retailerCreateBean.setDOBTemp(day+"/"+mon+"/"+mYear);

            String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDay)
                    .append("/").append(UtilConstants.MONTHS_NUMBER[mMonth])
                    .append("").append("/").append(mYear)));

            etDOB.setText(convertDateFormat);
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateSetAnnversaryListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYearAnnversary = year;
            mMonthAnnversary = monthOfYear;
            mDayAnnversary = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonthAnnversary + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDayAnnversary;
            if (mDayAnnversary < 10)
                day = "0" + mDayAnnversary;
            dateOfAnnversary = mYearAnnversary + "-" + mon + "-" + day;
            retailerCreateBean.setAnniversary(dateOfAnnversary);

            retailerCreateBean.setAnniversaryTemp(day+"/"+mon+"/"+mYearAnnversary);

            String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDayAnnversary)
                    .append("/").append(UtilConstants.MONTHS_NUMBER[mMonthAnnversary])
                    .append("").append("/").append(mYearAnnversary)));

            tiAnniversary.setErrorEnabled(false);
            etAnniversary.setText(convertDateFormat);
        }
    };
    private void editTextOntextChangeLisners(){
        etOwnerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiOwnerName.setErrorEnabled(false);
                retailerCreateBean.setOwnerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etOutletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiAddress.setErrorEnabled(false);
                retailerCreateBean.setAddress1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddressTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiAddressTwo.setErrorEnabled(false);
                retailerCreateBean.setAddress2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddressThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setAddress3(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddressFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setAddress4(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setTownDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setCityDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDistrict.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tiOutletName.setErrorEnabled(false);
                retailerCreateBean.setDistrictDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiPinCode.setErrorEnabled(false);
                retailerCreateBean.setPostalCode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etLandMark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiLandMark.setErrorEnabled(false);
                retailerCreateBean.setLandmark(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etIDOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiIDOne.setErrorEnabled(false);
                retailerCreateBean.setID1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etIDTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setID2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBussIDOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiBussIDOne.setErrorEnabled(false);
                retailerCreateBean.setBusinessID1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBussIDTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setBusinessID2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCPUID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setCPUID(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiMobile.setErrorEnabled(false);
                retailerCreateBean.setMobile1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMobileTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiMobileTwo.setErrorEnabled(false);
                retailerCreateBean.setMobile2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTelphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiTelphone.setErrorEnabled(false);
                retailerCreateBean.setLandline(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiFax.setErrorEnabled(false);
                retailerCreateBean.setFaxNo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timailid.setErrorEnabled(false);
                retailerCreateBean.setEmailID(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RetailerCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_retailer).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        redirectActivity();
                        if(retailetLatLongCapture!=null){
                            retailetLatLongCapture.onDestoryListern();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void onPermissionLatLong() {
        pdLoadDialog = Constants.showProgressDialog(RetailerCreateActivity.this, "", getString(R.string.app_loading));
        LocationUtils.checkLocationPermission(RetailerCreateActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
//                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }else {
                    closeProgressDialog();
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

    private void locationPerGranted() {
//        pdLoadDialog = Constants.showProgressDialog(RetailerCreateActivity.this, "", getString(R.string.gps_progress));
        retailetLatLongCapture = RetailetLatLongCapture.getInstance(this);
        closeProgressDialog();
    }

    private void setLatLonValues() {
//       retailerCreateBean.setLongitude(UtilConstants.longitude+"");
//        retailerCreateBean.setLatitude(UtilConstants.latitude+"");
        Location location = null;
        if(retailetLatLongCapture!= null){
            location = retailetLatLongCapture.onChangeLoc();
        }

        if (location != null) {
            retailerCreateBean.setLongitude(UtilConstants.round(location.getLongitude(), 12) + "");
            retailerCreateBean.setLatitude(UtilConstants.round(location.getLatitude(), 12) + "");
            tvLocationVal.setText(""+location.getLatitude() + "" + ", " + location.getLongitude()+ "");
        } else {
            onPermissionLatLong();
        }
    }
}
