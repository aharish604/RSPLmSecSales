package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;

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
 * Created by e10526 on 02-08-2018.
 *
 */

public class RetailerCreateSalesDataActivity extends AppCompatActivity implements RetailerCreateView {

    private Toolbar toolbar;
    private Context mContext;
    MaterialDesignSpinner spWeekOff,spRetClassfication;
    EditText etZone,etVatNo,etTaxOne,etTaxTwo,etTaxThree,etTaxFour;
    TextInputLayout tiZone,tiVatNo,tiTaxOne,tiTaxTwo,tiTaxthree,tiTaxFour;
    RetailerCreatePresenterImpl presenter;
    RetailerCreateBean retailerCreateBean =new RetailerCreateBean();
    ProgressDialog progressDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_create_sales_data);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            retailerCreateBean = (RetailerCreateBean) extras.getSerializable(Constants.RetailerList);
        }

        initUI();
        if(retailerCreateBean==null){
            retailerCreateBean = new RetailerCreateBean();
        }

        presenter = new RetailerCreatePresenterImpl(RetailerCreateSalesDataActivity.this, this, true, RetailerCreateSalesDataActivity.this, retailerCreateBean);
        if (!Constants.restartApp(RetailerCreateSalesDataActivity.this)) {
            presenter.onReqSalesData();
        }
    }

    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = RetailerCreateSalesDataActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.lbl_step_two_sales_data));

        spWeekOff = (MaterialDesignSpinner) findViewById(R.id.spWeekOff);
        spRetClassfication = (MaterialDesignSpinner) findViewById(R.id.spRetClassfication);

        tiZone = (TextInputLayout) findViewById(R.id.tiZone);
        tiVatNo = (TextInputLayout) findViewById(R.id.tiVatNo);
        tiTaxOne = (TextInputLayout) findViewById(R.id.tiTaxOne);
        tiTaxTwo = (TextInputLayout) findViewById(R.id.tiTaxTwo);
        tiTaxthree = (TextInputLayout) findViewById(R.id.tiTaxthree);
        tiTaxFour = (TextInputLayout) findViewById(R.id.tiTaxFour);
        etZone = (EditText) findViewById(R.id.etZone);
        etVatNo = (EditText) findViewById(R.id.etVatNo);
        etTaxOne = (EditText) findViewById(R.id.etTaxOne);
        etTaxTwo = (EditText) findViewById(R.id.etTaxTwo);
        etTaxThree = (EditText) findViewById(R.id.etTaxThree);
        etTaxFour = (EditText) findViewById(R.id.etTaxFour);
        etZone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        etVatNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
//        etTaxOne.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        InputFilter[] etTaxOneFilter = new InputFilter[2];
        etTaxOneFilter[0] = new InputFilter.LengthFilter(16);
        etTaxOneFilter[1] = Constants.getNumberAlphabet();
        etTaxOne.setFilters(etTaxOneFilter);
//        etTaxOne.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etTaxTwo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etTaxThree.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etTaxFour.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        tiZone.setVisibility(View.GONE);
        tiVatNo.setVisibility(View.GONE);
        tiTaxTwo.setVisibility(View.GONE);
        tiTaxthree.setVisibility(View.GONE);
        tiTaxFour.setVisibility(View.GONE);
        editTextOntextChangeLisners();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(RetailerCreateSalesDataActivity.this, message);
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
        UtilConstants.dialogBoxWithCallBack(RetailerCreateSalesDataActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
//                    redirectActivity();
                }
            }
        });
    }


    @Override
    public void displayCPType(final ArrayList<ValueHelpBean> cpType,final ArrayList<ValueHelpBean> alCountry,final ArrayList<ValueHelpBean> alState,final ArrayList<RoutePlanBean> routePlanBeanArrayList,String defaultStateID) {
    }

    @Override
    public void displayWeeklyOff(final ArrayList<ValueHelpBean> alWeeklyOff,final ArrayList<ValueHelpBean> alTaxStatus) {
        ArrayAdapter<ValueHelpBean> adapterShipToList = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alWeeklyOff) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spWeekOff, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spWeekOff.setAdapter(adapterShipToList);
        spWeekOff.showFloatingLabel();
        spWeekOff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setWeeklyOff(alWeeklyOff.get(position).getID());
                retailerCreateBean.setWeeklyOffDesc(alWeeklyOff.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterTaxClaSatatus = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alTaxStatus) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRetClassfication, position, getContext());
                return v;
            }
        };
        adapterTaxClaSatatus.setDropDownViewResource(R.layout.spinnerinside);
        spRetClassfication.setAdapter(adapterTaxClaSatatus);
        spRetClassfication.showFloatingLabel();
        spRetClassfication.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retailerCreateBean.setTaxRegStatus(alTaxStatus.get(position).getID());
                retailerCreateBean.setTaxRegStatusDesc(alTaxStatus.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayDMSDivision(ArrayList<DMSDivisionBean> alDmsDiv, final ArrayList<ValueHelpBean> alGrpOne,ArrayList<ValueHelpBean> alGrpFour,ArrayList<ValueHelpBean> alGrpFive) {

    }

    @Override
    public void displayGrpTwoValue(ArrayList<ValueHelpBean> alGrpTwo) {

    }

    @Override
    public void displayGrpThreeValue(ArrayList<ValueHelpBean> alGrpThree) {

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
        if (spWeekOff.getVisibility() == View.VISIBLE)
            spWeekOff.setError(message);
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
        UtilConstants.dialogBoxWithCallBack(RetailerCreateSalesDataActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
                if (ConstantsUtils.isAutomaticTimeZone(RetailerCreateSalesDataActivity.this)) {

                    retailerCreateBean.setAlRetClassfication(new ArrayList<RetailerClassificationBean>());
                    Intent intentRetailerDetails = new Intent(this, RetailerCreateClassificationActivity.class);
                    intentRetailerDetails.putExtra(Constants.RetailerList, retailerCreateBean);
                    startActivity(intentRetailerDetails);

                } else {
                    ConstantsUtils.showAutoDateSetDialog(RetailerCreateSalesDataActivity.this);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editTextOntextChangeLisners() {
        etZone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setZoneDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etVatNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setVATNo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etTaxOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setTax1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTaxTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setTax2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTaxThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setTax3(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTaxFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retailerCreateBean.setTax4(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
