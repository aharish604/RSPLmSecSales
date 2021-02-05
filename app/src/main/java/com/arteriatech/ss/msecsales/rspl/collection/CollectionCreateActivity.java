package com.arteriatech.ss.msecsales.rspl.collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.InvoiceBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class CollectionCreateActivity extends AppCompatActivity implements CollectionCreateView ,
        View.OnClickListener,DatePickerDialog.OnDateSetListener,KeyboardView.OnKeyboardActionListener{

    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    String beatGUID = "";
    String parentId = "";
    MaterialDesignSpinner spRefType,spPaymentMode,spBankName;
    EditText etRemarks,etCollAmt,etChequeDate,etUTRName, etBranchName;
    TextView tvOutstandingValue,tvCollDateValue,tvAdavnceAmountValue;
    TextInputLayout tiRemarks,tiCollAmount,tiBranchName,tiUtrName,tiChequeDate;
    CollectionCreatePresenterImpl presenter;
    CollectionBean collBean =new CollectionBean();
    ProgressDialog progressDialog=null;
    MenuItem menu_save, menu_next;
    private boolean mBooleanCollectionWithReference = false;
    private DatePickerDialog dialog;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    TextView tv_RetailerName,tv_RetailerID;
    LinearLayout llAdvanceLayout;
    KeyboardView keyboardView;
    Keyboard keyboard;
    private InputFilter[] uTRFilter;
    private InputFilter[] cardNumbFilter;
    private InputFilter[] chequeFilter;
    MaterialDesignSpinner spDMSDivision;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_create);
        Bundle bundleExtras = getIntent().getExtras();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = CollectionCreateActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_coll_create), 0);
        initUI();
        collBean = new CollectionBean();
        collBean.setCPGUID(mStrBundleCPGUID);
        collBean.setCPGUID32(mStrBundleCPGUID32);
        collBean.setCPNo(mStrBundleRetID);
        collBean.setCPName(mStrBundleRetName);
        collBean.setCpUID(mStrBundleRetailerUID);
        collBean.setComingFrom(mStrComingFrom);
        collBean.setParentID(parentId);
//        collBean.setBeatGuid(beatGUID);
        presenter = new CollectionCreatePresenterImpl(CollectionCreateActivity.this, this, true, CollectionCreateActivity.this, collBean);
        if (!Constants.restartApp(CollectionCreateActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI(){
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        spRefType = (MaterialDesignSpinner) findViewById(R.id.spRefType);
        spPaymentMode = (MaterialDesignSpinner) findViewById(R.id.spPaymentMode);
        spBankName = (MaterialDesignSpinner) findViewById(R.id.spBankName);
        etCollAmt = (EditText) findViewById(R.id.etCollAmt);
        etBranchName = (EditText) findViewById(R.id.etBranchName);
        etChequeDate = (EditText) findViewById(R.id.etChequeDate);
        tiCollAmount = (TextInputLayout) findViewById(R.id.tiCollAmount);
        tvAdavnceAmountValue = (TextView) findViewById(R.id.tvAdavnceAmountValue);
        llAdvanceLayout = (LinearLayout) findViewById(R.id.llAdvanceLayout);
        spDMSDivision = (MaterialDesignSpinner) findViewById(R.id.spDMSDivision);

        tvCollDateValue = (TextView) findViewById(R.id.tvCollDateValue);
        tvOutstandingValue = (TextView) findViewById(R.id.tvOutstandingValue);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);

        tiBranchName = (TextInputLayout) findViewById(R.id.tiBranchName);
        tiChequeDate = (TextInputLayout) findViewById(R.id.tiChequeDate);
        tiUtrName = (TextInputLayout) findViewById(R.id.tiUtrName);
        etUTRName = (EditText) findViewById(R.id.etUTRName);

        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        UtilConstants.editTextDecimalFormat(etCollAmt, 13, 2);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dialog = new DatePickerDialog(mContext, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        initializeKeyboardDependencies();
        etChequeDate.setOnClickListener(this);
        tiChequeDate.setOnClickListener(this);
//        setCollDate();

        InputFilter[] remarksFilter = new InputFilter[2];
        remarksFilter[0] = new InputFilter.LengthFilter(250);
        remarksFilter[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(remarksFilter);

        uTRFilter = new InputFilter[2];
        uTRFilter[0] = new InputFilter.LengthFilter(16);//22
        uTRFilter[1] = Constants.getNumberAlphabet();

        cardNumbFilter = new InputFilter[2];
        cardNumbFilter[0] = new InputFilter.LengthFilter(16);//19
        cardNumbFilter[1] = Constants.getNumberOnly();

        chequeFilter = new InputFilter[2];
        chequeFilter[0] = new InputFilter.LengthFilter(6);
        chequeFilter[1] = Constants.getNumberOnly();

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiRemarks.setErrorEnabled(false);
                collBean.setRemarks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCollAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiCollAmount.setErrorEnabled(false);
                collBean.setAmount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCollAmt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.requestFocus();
                etCollAmt.setFocusable(true);
                etCollAmt.setFocusableInTouchMode(true);
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                showCustomKeyboard(view);
                Constants.setCursorPostion(etCollAmt,view,motionEvent);
                return true;
            }
        });
        etCollAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                {

                    showCustomKeyboard(view);
                }

                else
                {
                    hideCustomKeyboard();
                }

            }
        });


        etUTRName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiUtrName.setErrorEnabled(false);
                collBean.setUTRNo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etBranchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiBranchName.setErrorEnabled(false);
                collBean.setBranchName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void initializeKeyboardDependencies()
    {
        keyboardView = (KeyboardView)findViewById(R.id.keyboard);
        keyboard = new Keyboard(CollectionCreateActivity.this, R.xml.ll_dot_key_board);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(CollectionCreateActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(CollectionCreateActivity.this,message);
    }

    @Override
    public void displayOutstandingAmount(String ousStndingValue) {
        if(!TextUtils.isEmpty(ousStndingValue)) {
            displayOutStndingValue(ousStndingValue);
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(CollectionCreateActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    @Override
    public void errorInvoiceScreen(String message) {
        UtilConstants.showAlert(message, CollectionCreateActivity.this);
    }

    @Override
    public void errorCollScreen(String message) {
        UtilConstants.showAlert(message, CollectionCreateActivity.this);
    }

    private void redirectActivity(){
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
       /* if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }*/
        startActivity(intentNavPrevScreen);
    }
    DMSDivisionBean dmsDivisionBean = null;
    @Override
    public void displayByCollectionData(final ArrayList<ValueHelpBean> alCollType,
                                        final ArrayList<ValueHelpBean> alPaymentMode,
                                        final ArrayList<ValueHelpBean> alBankNames,
                                        String outstandingData, final ArrayList<DMSDivisionBean> alDMSDiv) {

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
                dmsDivisionBean = alDMSDiv.get(position);
                if(dmsDivisionBean!=null) {
                    if(!dmsDivisionBean.getDMSDivisionID().equalsIgnoreCase("")) {
                        presenter.getOutStandingAmount(dmsDivisionBean.getDMSDivisionID());
                        collBean.setDivision(dmsDivisionBean.getDMSDivisionID());
                    }else {
                        collBean.setDivision("");
                        displayOutstandingAmount("0");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterCollType = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alCollType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRefType, position, getContext());
                return v;
            }
        };
        adapterCollType.setDropDownViewResource(R.layout.spinnerinside);
        spRefType.setAdapter(adapterCollType);
        spRefType.showFloatingLabel();
        spRefType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collBean.setRefTypeID(alCollType.get(position).getID());
                collBean.setRefTypeDesc(alCollType.get(position).getDescription());
                if (collBean.getRefTypeID().equalsIgnoreCase(Constants.str_01) || collBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
                    mBooleanCollectionWithReference = false;
                    menu_save.setVisible(false);
                    menu_next.setVisible(true);
                }else{
                    mBooleanCollectionWithReference = true;
                    menu_save.setVisible(true);
                    menu_next.setVisible(false);
                }

                if (collBean.getRefTypeID().equalsIgnoreCase(Constants.str_01) || collBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
                    if( collBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)){
                        llAdvanceLayout.setVisibility(View.VISIBLE);
                        tiCollAmount.setVisibility(View.GONE);
                        spPaymentMode.setVisibility(View.GONE);
                        tvAdavnceAmountValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(collBean.getAdvanceAmount() + "")+" "+collBean.getCurrency());
                        if(collBean.getAdvanceAmount()<=0){
                            menu_next.setVisible(false);
                        }
                    }else{
//                        etCollAmt.setText("");
                        llAdvanceLayout.setVisibility(View.GONE);
                        tiCollAmount.setVisibility(View.VISIBLE);
                        spPaymentMode.setVisibility(View.VISIBLE);
                    }

                } else {
                    llAdvanceLayout.setVisibility(View.GONE);
                    tiCollAmount.setVisibility(View.VISIBLE);
                    spPaymentMode.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterPaymentMode = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alPaymentMode) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spPaymentMode, position, getContext());
                return v;
            }
        };
        adapterPaymentMode.setDropDownViewResource(R.layout.spinnerinside);
        spPaymentMode.setAdapter(adapterPaymentMode);
        spPaymentMode.showFloatingLabel();
        spPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collBean.setPaymentModeID(alPaymentMode.get(position).getID());
                collBean.setPaymentModeDesc(alPaymentMode.get(position).getDescription());

                if (collBean.getPaymentModeID().equalsIgnoreCase("03")) {
                    tiUtrName.setHint(getString(R.string.lbl_utr_no));
                    spBankName.setSelection(0);
                    spBankName.setVisibility(View.VISIBLE);
                    tiBranchName.setVisibility(View.VISIBLE);
                    tiUtrName.setVisibility(View.VISIBLE);
                    tiChequeDate.setVisibility(View.VISIBLE);
                    etUTRName.setText("");
                    etUTRName.setInputType(InputType.TYPE_CLASS_TEXT);
                    etUTRName.setFilters(uTRFilter);
                }else if (collBean.getPaymentModeID().equalsIgnoreCase("02")){
                    tiUtrName.setHint(getString(R.string.coll_header_crt_crd));
                    spBankName.setVisibility(View.VISIBLE);
                    tiBranchName.setVisibility(View.VISIBLE);
                    tiUtrName.setVisibility(View.VISIBLE);
                    tiChequeDate.setVisibility(View.VISIBLE);
                    etUTRName.setText("");
                    etUTRName.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etUTRName.setFilters(cardNumbFilter);
                }else if (collBean.getPaymentModeID().equalsIgnoreCase("01")){
                    tiUtrName.setHint(getString(R.string.coll_header_crt_cheque));
                    spBankName.setVisibility(View.VISIBLE);
                    tiBranchName.setVisibility(View.VISIBLE);
                    tiUtrName.setVisibility(View.VISIBLE);
                    tiChequeDate.setVisibility(View.VISIBLE);
                    etUTRName.setText("");
                    etUTRName.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etUTRName.setFilters(chequeFilter);
                }else  {
                    spBankName.setVisibility(View.GONE);
                    tiBranchName.setVisibility(View.GONE);
                    tiUtrName.setVisibility(View.GONE);
                    tiChequeDate.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        displayBankNames(alBankNames);
        displayOutStndingValue(outstandingData);
        setCollDate();
    }

    @Override
    public void displayInvoiceData(ArrayList<InvoiceBean> alInvList) {

    }

    private void displayOutStndingValue(String ousStndingValue){
        collBean.setOutstandingAmount(ousStndingValue);
        tvOutstandingValue.setText(Constants.getCurrencySymbol(collBean.getCurrency(),ousStndingValue));
    }

    private void displayBankNames(final ArrayList<ValueHelpBean> alBankNames){
        ArrayAdapter<ValueHelpBean> adapterPaymentMode = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alBankNames) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBankName, position, getContext());
                return v;
            }
        };
        adapterPaymentMode.setDropDownViewResource(R.layout.spinnerinside);
        spBankName.setAdapter(adapterPaymentMode);
        spBankName.showFloatingLabel();
        spBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collBean.setBankID(alBankNames.get(position).getID());
                collBean.setBankName(alBankNames.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void errorRefType(String message) {
        if (spRefType.getVisibility() == View.VISIBLE)
            spRefType.setError(message);
    }

    @Override
    public void errorPaymentMode(String message) {
        if (spPaymentMode.getVisibility() == View.VISIBLE)
            spPaymentMode.setError(message);
    }

    @Override
    public void errorBankName(String message) {
        if (spBankName.getVisibility() == View.VISIBLE)
            spBankName.setError(message);
    }

    @Override
    public void errorDivision(String message) {
        if (spDMSDivision.getVisibility() == View.VISIBLE)
            spDMSDivision.setError(message);
    }

    @Override
    public void errorRemarks(String message) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(message);
    }

    @Override
    public void errorChequeDate(String message) {
        tiChequeDate.setErrorEnabled(true);
        tiChequeDate.setError(message);
    }

    @Override
    public void errorUTRNoOrChequeDD(String message) {
        tiUtrName.setErrorEnabled(true);
        tiUtrName.setError(message);
    }

    @Override
    public void errorAmount(String message) {
        tiCollAmount.setErrorEnabled(true);
        tiCollAmount.setError(message);
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(CollectionCreateActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu_save = menu.findItem(R.id.menu_collection_save);
        menu_next = menu.findItem(R.id.menu_collection_next);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mBooleanCollectionWithReference) {
            menu_save.setVisible(true);
            menu_next.setVisible(false);
        } else {
            menu_save.setVisible(false);
            menu_next.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                onSave();
                break;
            case R.id.menu_collection_next:
                onNext();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if(isCustomKeyboardVisible())
        {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_collection).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onNavigateToRetDetilsActivity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
        }

    }
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    private void onNavigateToRetDetilsActivity() {
        Intent intentNavPrevScreen = new Intent(CollectionCreateActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);

//        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        startActivity(intentNavPrevScreen);
    }
    private void onSave(){
        //next step
        if (ConstantsUtils.isAutomaticTimeZone(CollectionCreateActivity.this)) {
            if (presenter.validateFields(collBean,collBean.getRefTypeID())) {
                presenter.onAsignData("",collBean.getRefTypeID(),collBean,"01");

            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(CollectionCreateActivity.this);
        }
    }

    private void onNext(){
        hideCustomKeyboard();
        //next step
        if (ConstantsUtils.isAutomaticTimeZone(CollectionCreateActivity.this)) {
            int invoiceCount=0;
            if (presenter.validateFields(collBean,collBean.getRefTypeID())) {
                String mStrInvQry = Constants.SSOutstandingInvoices + "?$orderby=" + Constants.InvoiceNo + " asc&$filter=SoldToID eq '" + collBean.getCPNo() + "' and (StatusID eq '03' or StatusID eq '04' or StatusID eq '05') and "+Constants.DMSDivisionID+" eq '"+collBean.getDivision()+"'";
                try {
                    invoiceCount = OfflineManager.getInvoiceListCount(mStrInvQry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (collBean.getPaymentModeID().equalsIgnoreCase("04")) {
                    collBean.setBankID("");
                    collBean.setBankName("");
                    collBean.setInstrumentNo("");
                    collBean.setUTRNo("");
                    collBean.setChequeDate("");
                    collBean.setBranchName("");
                    collBean.setInstrumentDate("");
                }
                if(invoiceCount>0) {
                    Intent intent = new Intent(mContext, InvoiceSelectionActivity.class);
                    intent.putExtra(Constants.EXTRA_SO_DETAIL, collBean);
                    intent.putExtra(Constants.BeatGUID, beatGUID);
                    intent.putExtra(Constants.ParentId, parentId);
                    if (collBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
                        intent.putExtra(Constants.InvoiceAmount, collBean.getAdvanceAmount());
                        intent.putExtra(Constants.PaymentMode, "");
                    } else {
                        intent.putExtra(Constants.InvoiceAmount, Double.parseDouble(collBean.getAmount()));
                        intent.putExtra(Constants.PaymentMode, collBean.getPaymentModeID());
                    }
                    startActivity(intent);
                }else {
                    Toast.makeText(CollectionCreateActivity.this, getResources().getString(R.string.invoice_select), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(CollectionCreateActivity.this);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etChequeDate:
                openCallender();
                break;
            case R.id.tiChequeDate:
                openCallender();
                break;
        }

    }

    private void openCallender() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        //Get last three months date
        Calendar calendarPast = Calendar.getInstance();
        calendarPast.add(Calendar.MONTH, -3);
        //Get Next three months date
        Calendar calendarFuture = Calendar.getInstance();
        calendarFuture.add(Calendar.MONTH, 3);
        // Cheque Date allow past and future three months only
        dialog.getDatePicker().setMinDate(calendarPast.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(calendarFuture.getTimeInMillis());

        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        setDateToView(mYear, mMonth, mDay);
    }

    private void setDateToView(int mYear, int mMonth, int mDay) {
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
        collBean.setChequeDate(mYear + "-" + mon + "-" + day);
        tiChequeDate.setErrorEnabled(false);
        String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDay).append("/")
                .append(UtilConstants.MONTHS_NUMBER[mMonth]).append("/").append("").append(mYear)));
        etChequeDate.setText(convertDateFormat);
    }
    private int mYearCurrent, mMonthCurrent, mDayCurrent, mntCurrent;
    private String monCurrent = "", dayCurrent = "", mStrCurrentDate = "";
    private void setCollDate(){
        final Calendar calCollectionDate = Calendar.getInstance();
        mYearCurrent = calCollectionDate.get(Calendar.YEAR);
        mMonthCurrent = calCollectionDate.get(Calendar.MONTH);
        mDayCurrent = calCollectionDate.get(Calendar.DAY_OF_MONTH);

        mntCurrent = mMonthCurrent + 1;
        if (mntCurrent < 10)
            monCurrent = "0" + mntCurrent;
        else
            monCurrent = "" + mntCurrent;
        dayCurrent = "" + mDayCurrent;
        if (mDayCurrent < 10)
            dayCurrent = "0" + mDayCurrent;

        collBean.setCollDate(mYearCurrent + "-" + monCurrent + "-" + dayCurrent);
        String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDayCurrent)
                .append("/").append(UtilConstants.MONTHS_NUMBER[mMonthCurrent])
                .append("").append("/").append(mYearCurrent)));
        tvCollDateValue.setText(convertDateFormat);
    }
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }
    public void showCustomKeyboard( View v) {

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if( v!=null ){
            ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode)
        {
            case 81:
                //Plus
                Constants.incrementTextValues(etCollAmt, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(etCollAmt, Constants.Y);
                break;
            case 1:
                //changeEditTextFocus(0);
                break;
            case 2:
                // changeEditTextFocus(1);
                break;
            case 56:
                if(!checkAlreadyDotIsThere())
                {
                    KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                    dispatchKeyEvent(event);
                }

                break;

            default:
                //default numbers
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;
        }


    }

    private Boolean checkAlreadyDotIsThere() {

        String textValue = etCollAmt.getText().toString();
        if(textValue.contains("."))
        {
            return true;
        }
        else
            return false;
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
