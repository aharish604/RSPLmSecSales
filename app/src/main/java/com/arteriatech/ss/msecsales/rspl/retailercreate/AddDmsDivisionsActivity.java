package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerClassificationBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

/**
 * Created by e10526 on 03-08-2018.
 *
 */

public class AddDmsDivisionsActivity extends AppCompatActivity implements RetailerCreateView {

    private Toolbar toolbar;
    private Context mContext;
    MaterialDesignSpinner spDMSDivision,spDMSDivGrpOne,spDMSDivGrpFour,spDMSDivGrpFive,spDMSDivGrpThree,spDMSDivGrpTwo;
    TextInputLayout tiDiscountPercentage,tiCreditLimit,tiCreditPeriod,tiNoOfBills;
    EditText etDiscountPercentage,etCreditLimit,etCreditPeriod,etNoOfBills;
    RetailerCreatePresenterImpl presenter;
    RetailerCreateBean retailerCreateBean =new RetailerCreateBean();
    RetailerClassificationBean retailerClassificationBean =new RetailerClassificationBean();
    ProgressDialog progressDialog=null;
    private SearchView mSearchView;
    private String mStrDistId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dms_division);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrDistId = extras.getString(Constants.CPGUID);
            retailerCreateBean = (RetailerCreateBean) extras.getSerializable(Constants.RetailerList);
        }

        initUI();
        if(retailerCreateBean==null){
            retailerCreateBean = new RetailerCreateBean();
        }

        presenter = new RetailerCreatePresenterImpl(AddDmsDivisionsActivity.this, this, true, AddDmsDivisionsActivity.this, retailerCreateBean);
        if (!Constants.restartApp(AddDmsDivisionsActivity.this)) {
            presenter.onReqDMSDivsion(mStrDistId);
        }
    }

    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = AddDmsDivisionsActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.lbl_step_tax_classfication));

        spDMSDivision = (MaterialDesignSpinner) findViewById(R.id.spDMSDivision);
        spDMSDivGrpOne = (MaterialDesignSpinner) findViewById(R.id.spDMSDivGrpOne);
        spDMSDivGrpFour = (MaterialDesignSpinner) findViewById(R.id.spDMSDivGrpFour);
        spDMSDivGrpFive = (MaterialDesignSpinner) findViewById(R.id.spDMSDivGrpFive);
        spDMSDivGrpTwo = (MaterialDesignSpinner) findViewById(R.id.spDMSDivGrpTwo);
        spDMSDivGrpThree = (MaterialDesignSpinner) findViewById(R.id.spDMSDivGrpThree);

        tiDiscountPercentage = (TextInputLayout) findViewById(R.id.tiDiscountPercentage);
        tiCreditLimit = (TextInputLayout) findViewById(R.id.tiCreditLimit);
        tiCreditPeriod = (TextInputLayout) findViewById(R.id.tiCreditPeriod);
        tiNoOfBills = (TextInputLayout) findViewById(R.id.tiNoOfBills);
        etDiscountPercentage = (EditText) findViewById(R.id.etDiscountPercentage);
        etCreditLimit = (EditText) findViewById(R.id.etCreditLimit);
        etCreditPeriod = (EditText) findViewById(R.id.etCreditPeriod);
        etNoOfBills = (EditText) findViewById(R.id.etNoOfBills);
        tiDiscountPercentage.setVisibility(View.GONE);
        tiCreditLimit.setVisibility(View.GONE);
        tiCreditPeriod.setVisibility(View.GONE);
        tiNoOfBills.setVisibility(View.GONE);

        UtilConstants.editTextDecimalFormat(etDiscountPercentage, 3, 3);
        UtilConstants.editTextDecimalFormat(etCreditLimit, 11, 2);
        editTextOntextChangeLisners();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(AddDmsDivisionsActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(mContext,message);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(AddDmsDivisionsActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
//                    redirectActivity();
                }
            }
        });
    }

    @Override
    public void displayCPType(final ArrayList<ValueHelpBean> cpType,final ArrayList<ValueHelpBean> alCountry,final ArrayList<ValueHelpBean> alState,final ArrayList<RoutePlanBean> routePlanBeanArrayList,final String defaultStateID) {
    }

    @Override
    public void displayWeeklyOff(final ArrayList<ValueHelpBean> alWeeklyOff,final ArrayList<ValueHelpBean> alTaxRegStatus) {

    }

    @Override
    public void displayDMSDivision(final ArrayList<DMSDivisionBean> alDMSDiv, final ArrayList<ValueHelpBean> alGrpOne,final ArrayList<ValueHelpBean> alGrpFour,final  ArrayList<ValueHelpBean> alGrpFive) {
        ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alDMSDiv) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivision, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivision.setAdapter(adapterShipToList);
        spDMSDivision.showFloatingLabel();
        spDMSDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setDMSDivision(alDMSDiv.get(position).getDMSDivisionID());
                retailerClassificationBean.setDMSDivisionDesc(alDMSDiv.get(position).getDmsDivsionDesc());
                retailerClassificationBean.setPartnerMgrNo(alDMSDiv.get(position).getSPNo());
                retailerClassificationBean.setPartnerMgrGUID(alDMSDiv.get(position).getSPGUID());
                retailerClassificationBean.setParentID(alDMSDiv.get(position).getCPGUID());
                retailerClassificationBean.setParentTypeID(alDMSDiv.get(position).getCPTypeID());
                retailerClassificationBean.setParentName(alDMSDiv.get(position).getCPName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterGrpOne = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alGrpOne) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivGrpOne, position, getContext());
                return v;
            }
        };
        adapterGrpOne.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivGrpOne.setAdapter(adapterGrpOne);
        spDMSDivGrpOne.showFloatingLabel();
        spDMSDivGrpOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setGroup1(alGrpOne.get(position).getID());
                retailerClassificationBean.setGroup1Desc(alGrpOne.get(position).getDescription());

                if(!retailerClassificationBean.getGroup1().equalsIgnoreCase("")){
                    presenter.onReqGrp2byGrpOne(retailerClassificationBean.getGroup1());
                }else{
                    retailerClassificationBean.setGroup2("");
                    retailerClassificationBean.setGroup2Desc("");
                    retailerClassificationBean.setGroup3("");
                    retailerClassificationBean.setGroup3Desc("");
                    displayGrpTwoValue(new ArrayList<ValueHelpBean>());
                    displayGrpThreeValue(new ArrayList<ValueHelpBean>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterGrpFour = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alGrpFour) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivGrpFour, position, getContext());
                return v;
            }
        };
        adapterGrpFour.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivGrpFour.setAdapter(adapterGrpFour);
        spDMSDivGrpFour.showFloatingLabel();
        spDMSDivGrpFour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setGroup4(alGrpFour.get(position).getID());
                retailerClassificationBean.setGroup4Desc(alGrpFour.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterGrpFive = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alGrpFive) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivGrpFive, position, getContext());
                return v;
            }
        };
        adapterGrpFive.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivGrpFive.setAdapter(adapterGrpFive);
        spDMSDivGrpFive.showFloatingLabel();
        spDMSDivGrpFive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setGroup5(alGrpFive.get(position).getID());
                retailerClassificationBean.setGroup5Desc(alGrpFive.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayGrpTwoValue(final ArrayList<ValueHelpBean> alGrpTwo) {
        ArrayAdapter<ValueHelpBean> adapterGrpTwo = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alGrpTwo) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivGrpTwo, position, getContext());
                return v;
            }
        };
        adapterGrpTwo.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivGrpTwo.setAdapter(adapterGrpTwo);
        spDMSDivGrpTwo.showFloatingLabel();
        spDMSDivGrpTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setGroup2(alGrpTwo.get(position).getID());
                retailerClassificationBean.setGroup2Desc(alGrpTwo.get(position).getDescription());

                if(!retailerClassificationBean.getGroup2().equalsIgnoreCase("")){
                    presenter.onReqGrp3byGrpTwo(retailerClassificationBean.getGroup2());
                }else{
                    retailerClassificationBean.setGroup3("");
                    retailerClassificationBean.setGroup3Desc("");
                    displayGrpThreeValue(new ArrayList<ValueHelpBean>());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayGrpThreeValue(final ArrayList<ValueHelpBean> alGrpThree) {
        ArrayAdapter<ValueHelpBean> adapterGrpThree = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alGrpThree) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivGrpThree, position, getContext());
                return v;
            }
        };
        adapterGrpThree.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivGrpThree.setAdapter(adapterGrpThree);
        spDMSDivGrpThree.showFloatingLabel();
        spDMSDivGrpThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerClassificationBean.setGroup3(alGrpThree.get(position).getID());
                retailerClassificationBean.setGroup3Desc(alGrpThree.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void errorRetailerType(String message) {
    }

    @Override
    public void errorOutletName(String message) {

    }

    @Override
    public void errorOwnerName(String message) {

    }

    @Override
    public void errorAddressOne(String message) {

    }

    @Override
    public void errorLandMArk(String message) {

    }

    @Override
    public void errorCountry(String message) {

    }

    @Override
    public void errorState(String message) {

    }

    @Override
    public void errorRouteName(String message) {

    }

    @Override
    public void errorPostlcode(String message) {

    }

    @Override
    public void errorMobileOne(String message) {

    }

    @Override
    public void errorMobileTwo(String message) {

    }

    @Override
    public void errorID(String message) {

    }

    @Override
    public void errorBussnessID(String message) {

    }

    @Override
    public void errorTelNo(String message) {

    }

    @Override
    public void errorFaxNo(String message) {

    }

    @Override
    public void errorEmailId(String message) {

    }

    @Override
    public void errorAnniversary(String message) {

    }

    @Override
    public void errorRemarks(String message) {
//        tiOutletName.setErrorEnabled(true);
//        tiOutletName.setError(message);
    }

    @Override
    public void errorOthers(String message) {
//        tiOwnerName.setErrorEnabled(true);
//        tiOwnerName.setError(message);
    }

    @Override
    public void errorDMSDiv(String message) {
        if (spDMSDivision.getVisibility() == View.VISIBLE)
            spDMSDivision.setError(message);
    }

    @Override
    public void errorGroupOne(String message) {
        if (spDMSDivGrpOne.getVisibility() == View.VISIBLE)
            spDMSDivGrpOne.setError(message);
    }

    @Override
    public void errorGroupTwo(String message) {
        if (spDMSDivGrpTwo.getVisibility() == View.VISIBLE)
            spDMSDivGrpTwo.setError(message);
    }

    @Override
    public void errorGroupThree(String message) {
        if (spDMSDivGrpThree.getVisibility() == View.VISIBLE)
            spDMSDivGrpThree.setError(message);
    }

    @Override
    public void errorGroupFour(String message) {
        if (spDMSDivGrpFour.getVisibility() == View.VISIBLE)
            spDMSDivGrpFour.setError(message);
    }

    @Override
    public void errorGroupFive(String message) {
        if (spDMSDivGrpFive.getVisibility() == View.VISIBLE)
            spDMSDivGrpFive.setError(message);
    }

    @Override
    public void errorDiscountPercentage(String message) {
        tiDiscountPercentage.setErrorEnabled(true);
        tiDiscountPercentage.setError(message);
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(AddDmsDivisionsActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        inflater.inflate(R.menu.menu_add_create_ret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply:
                if (retailerClassificationBean!=null) {
                   if(retailerClassificationBean.getCreditBills()==null ||  retailerClassificationBean.getCreditBills().equalsIgnoreCase("")){
                       retailerClassificationBean.setCreditBills("0");
                   }
                    if(retailerClassificationBean.getCreditDays()==null || retailerClassificationBean.getCreditDays().equalsIgnoreCase("")){
                        retailerClassificationBean.setCreditDays("0");
                    }
                    if (presenter.validateDMSDivFields(retailerClassificationBean)) {
                        setResult(RetailerCreateClassificationActivity.INTENT_RESULT_RETAILER_CREATE, new Intent().putExtra(Constants.CPDMSDivisions, retailerClassificationBean));
                        finish();
                    }

                } else {
                    Toast.makeText(this, "please select valid DMS Division", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void editTextOntextChangeLisners() {
        etDiscountPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDiscountPercentage.setErrorEnabled(false);
                retailerClassificationBean.setDiscountPer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCreditLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerClassificationBean.setCreditLimit(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCreditPeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerClassificationBean.setCreditDays(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNoOfBills.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerClassificationBean.setCreditBills(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
