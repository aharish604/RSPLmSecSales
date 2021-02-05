package com.arteriatech.ss.msecsales.rspl.competitors;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class CompetitorActivity extends AppCompatActivity implements CompetitorView, KeyboardView.OnKeyboardActionListener {

    //data members
    private Toolbar toolbar;
    private Context mContext = this;
    private String mStrBundleRetID = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    EditText etProductName, etMrp, etRetailerMargin, etSchemeName, etConsumerOffer, etTradeOffer, etRemarks, etShelfLife, etWhlLandingPrice, etRetailerLandingPrice;
    TextInputLayout tiProductName, tiMrp, tiRetailerMargin, tiSchemeName, tiConsumerOffer, tiTradeOffer, tiRemarks, tiShelfLife, tiWhlLandingPrice, tiRetailerLandingPrice;
    CompetitorPresenterImpl presenter;
    CompetitorBean competitorBean = null;
    ProgressDialog progressDialog = null;
    MaterialDesignSpinner spCompetitorName, spSchemeLaunched;
    private Keyboard keyboard;
    private KeyboardView keyboardView;
    private EditText focusEditText;
    private TextView tvRetailerName, tvRetailerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_create_actvity);

        // fetching data sent by other component through bundle
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
        }

        // adding details from bundle to competitors bean class
        competitorBean = new CompetitorBean();
        competitorBean.setCPGUID(mStrBundleCPGUID);
        competitorBean.setCPGUID32(mStrBundleCPGUID32);
        competitorBean.setCPNo(mStrBundleRetID);
        competitorBean.setCPName(mStrBundleRetName);
        competitorBean.setCpUID(mStrBundleRetailerUID);

        // Initializing presenter(MVP)
        presenter = new CompetitorPresenterImpl(CompetitorActivity.this, this, true, CompetitorActivity.this, competitorBean);

        if (!Constants.restartApp(CompetitorActivity.this)) {
            presenter.onStart();
        }

        initializeKeyboardDependencies();
        initView();
    }

    /**
     * @desc To initialize views
     */
    private void initView() {
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        spCompetitorName = (MaterialDesignSpinner) findViewById(R.id.spCompetitorName);
        spSchemeLaunched = (MaterialDesignSpinner) findViewById(R.id.spSchemeLaunched);
        etProductName = (EditText) findViewById(R.id.etProductName);
        etMrp = (EditText) findViewById(R.id.etMRM);
        etRetailerMargin = (EditText) findViewById(R.id.etRetailerMargin);
        etSchemeName = (EditText) findViewById(R.id.etSchemeName);
        etConsumerOffer = (EditText) findViewById(R.id.etConsumerOffer);
        etTradeOffer = (EditText) findViewById(R.id.etTradeOffer);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        etShelfLife = (EditText) findViewById(R.id.etShelfLife);
        etWhlLandingPrice = (EditText) findViewById(R.id.etWholesalerLandingPrice);
        etRetailerLandingPrice = (EditText) findViewById(R.id.etRetailerLandingPrice);
        tiProductName = (TextInputLayout) findViewById(R.id.tiProduct);
        tiSchemeName = (TextInputLayout) findViewById(R.id.tiSchemeName);
        tiRetailerMargin = (TextInputLayout) findViewById(R.id.tiRetailerMargin);
        tiMrp = (TextInputLayout) findViewById(R.id.tiMRP);
        tiConsumerOffer = (TextInputLayout) findViewById(R.id.tiConsumerOffer);
        tiTradeOffer = (TextInputLayout) findViewById(R.id.tiTradeOffer);
        tiShelfLife = (TextInputLayout) findViewById(R.id.tiShelfLife);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        tiRetailerLandingPrice = (TextInputLayout) findViewById(R.id.tiRetailerLandingPrice);
        tiWhlLandingPrice = (TextInputLayout) findViewById(R.id.tiWholesalerLandingPrice);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // setting toolbar title
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_competitor_create), 0);

        setUI();
    }

    /**
     * @desc to setup UI
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setUI() {
        // set retailer name
        tvRetailerName.setText(mStrBundleRetName);
        // set retailer id
        tvRetailerID.setText(mStrBundleRetID);

        // restricting decimals (Ex: 123.12  1234.23  23436641.2, 34.22)
        UtilConstants.editTextDecimalFormat(etMrp, 13, 2);
        UtilConstants.editTextDecimalFormat(etRetailerMargin, 3, 2);
        UtilConstants.editTextDecimalFormat(etRetailerLandingPrice, 13, 2);
        UtilConstants.editTextDecimalFormat(etWhlLandingPrice, 13, 2);

        // Adding TextChangeListener to the edit text views
        etProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiProductName.setErrorEnabled(false);
                competitorBean.setMaterialDesc(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMrp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiMrp.setErrorEnabled(false);
                competitorBean.setMRP(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRetailerMargin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiRetailerMargin.setErrorEnabled(false);
                competitorBean.setMargin(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTradeOffer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiTradeOffer.setErrorEnabled(false);
                competitorBean.setTradeOffer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConsumerOffer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiConsumerOffer.setErrorEnabled(false);
                competitorBean.setConsumerOffer(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiRemarks.setErrorEnabled(false);
                competitorBean.setRemarks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etShelfLife.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiShelfLife.setErrorEnabled(false);
                competitorBean.setShelfLife(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etWhlLandingPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiWhlLandingPrice.setErrorEnabled(false);
                competitorBean.setWholesalerLandingPrice(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRetailerLandingPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiRetailerLandingPrice.setErrorEnabled(false);
                competitorBean.setRetailerLandingPrice(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSchemeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiSchemeName.setErrorEnabled(false);
                competitorBean.setSchemeName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // adding on focus change listener to edit texts
        etMrp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = etMrp;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        etRetailerMargin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = etRetailerMargin;
                    Constants.showCustomKeyboard(v, keyboardView, mContext);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        etRetailerLandingPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = etRetailerLandingPrice;
                    Constants.showCustomKeyboard(v, keyboardView, mContext);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        etWhlLandingPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = etWhlLandingPrice;
                    Constants.showCustomKeyboard(v, keyboardView, mContext);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        etShelfLife.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = etShelfLife;
                    Constants.showCustomKeyboard(v, keyboardView, mContext);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        // adding onTouch listeners to edit texts
        etMrp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, mContext);
                Constants.setCursorPostion(etMrp, v, event);
                return true;
            }
        });

        etRetailerMargin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, mContext);
                Constants.setCursorPostion(etRetailerMargin, v, event);
                return true;
            }
        });

        etRetailerLandingPrice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, mContext);
                Constants.setCursorPostion(etRetailerLandingPrice, v, event);
                return true;
            }
        });

        etWhlLandingPrice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, mContext);
                Constants.setCursorPostion(etWhlLandingPrice, v, event);
                return true;
            }
        });

        etShelfLife.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, mContext);
                Constants.setCursorPostion(etShelfLife, v, event);
                return true;
            }
        });
    }

    /**
     * @desc to enable custom keyboard when we click on edit text
     */
    private void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(CompetitorActivity.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(CompetitorActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        if (!isSimpleDialog) {
       //     String finalStr = message + " " + mStrBundleRetName + ". " + getResources().getString(R.string.msg_do_you_want_to_capture_another_information);
            String finalStr = message + " " + mStrBundleRetName;
            navigationPopup(finalStr);
        }
    }

    /**
     * @desc decides where to launch after success of competitors creation
     */
    private void navigationPopup(String finalStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorActivity.this, R.style.MyTheme);
        builder.setMessage(finalStr).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       //emptyAllViews();
                        redirectActivity();
                    }
                });
//                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        redirectActivity();
//                    }
//
//                });
        builder.show();
    }

    /**
     * @desc to empty all views if user wants to enter competitors info again
     */
    private void emptyAllViews() {
        spCompetitorName.setSelection(0);
        spSchemeLaunched.setSelection(0);
        etProductName.setText("");
        etMrp.setText("");
        etRetailerMargin.setText("");
        etSchemeName.setText("");
        etConsumerOffer.setText("");
        etTradeOffer.setText("");
        etRemarks.setText("");
        etShelfLife.setText("");
        etWhlLandingPrice.setText("");
        etRetailerLandingPrice.setText("");
    }

    @Override
    public void errorMessage(String message) {
        UtilConstants.dialogBoxWithCallBack(CompetitorActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                redirectActivity();
            }
        });
    }

    /**
     * @desc to invoke customer details activity after success of save
     */
    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(mContext, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
        intentNavPrevScreen.putExtra(Constants.comingFrom, Constants.AdhocList);
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void displayByCompetitor(final ArrayList<ValueHelpBean> compName) {
        ArrayAdapter<ValueHelpBean> adapterShipToList = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, compName) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCompetitorName, position, getContext());
                return v;
            }
        };

        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spCompetitorName.setAdapter(adapterShipToList);
        spCompetitorName.showFloatingLabel();
        spCompetitorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                competitorBean.setCompanyName(compName.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayByScheme(final ArrayList<String> schemeName) {
        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, schemeName) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSchemeLaunched, position, getContext());
                return v;
            }
        };

        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spSchemeLaunched.setAdapter(adapterShipToList);
        spSchemeLaunched.showFloatingLabel();
        spSchemeLaunched.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                competitorBean.setSchemeLaunched(schemeName.get(position));
                if (schemeName.get(position).equalsIgnoreCase(Constants.YES)) {
                    tiSchemeName.setVisibility(View.VISIBLE);
                } else {
                    tiSchemeName.setVisibility(View.GONE);
                    etSchemeName.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * @desc to display error messages
     */
    @Override
    public void errorProductName(String message) {
        tiProductName.setErrorEnabled(true);
        tiProductName.setError(message);
    }

    @Override
    public void errorRetailerMargin(String message) {
        tiRetailerMargin.setErrorEnabled(true);
        tiRetailerMargin.setError(message);
    }

    @Override
    public void errorCompetitorName(String message) {
        if (spCompetitorName.getVisibility() == View.VISIBLE)
            spCompetitorName.setError(message);
    }

    @Override
    public void errorMrp(String message) {
        tiMrp.setErrorEnabled(true);
        tiMrp.setError(message);
    }

    @Override
    public void errorRemarks(String message) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(message);
    }

    @Override
    public void errorSchemeLaunched(String message) {
        if (spSchemeLaunched.getVisibility() == View.VISIBLE)
            spSchemeLaunched.setError(message);
    }

    @Override
    public void errorSchemeName(String message) {
        if (tiSchemeName.getVisibility() == View.VISIBLE)
            tiSchemeName.setError(message);
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(CompetitorActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        inflater.inflate(R.menu.menu_back_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                onSave();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_competitor).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            redirectActivity();
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

    /**
     * @desc It will check weather server keyboard is in visible state or in invisible state
     */
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * @desc To hide custom key board
     */
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    /**
     * @desc to create competitor data
     */
    private void onSave() {
        try {
            if (ConstantsUtils.isAutomaticTimeZone(CompetitorActivity.this)) {
                if (presenter.validateFields(competitorBean)) {
                    if (presenter.isValidMargin(competitorBean.getMargin())) {
                        presenter.onAsignData(competitorBean);
                    } else {
                        errorRetailerMargin(mContext.getResources().getString(R.string.lbl_enter_valid_value_retailer_margin));
                    }
                }
            } else {
                ConstantsUtils.showAutoDateSetDialog(CompetitorActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(focusEditText, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.Y);
                break;
            case 1:
                changeCursor(0, focusEditText);
                break;
            case 2:
                changeCursor(1, focusEditText);
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;

            default:
                //default numbers
                KeyEvent events = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(events);
                break;
        }
    }

    private void changeCursor(int fromTop, EditText selectedEditText) {
        switch (selectedEditText.getId()) {
            case R.id.etMRM:
                if (fromTop == 1) {
                    etRetailerMargin.requestFocus();
                }
                break;
            case R.id.etRetailerMargin:
                if (fromTop == 1) {
                    etRetailerLandingPrice.requestFocus();
                } else {
                    etMrp.requestFocus();
                }
                break;
            case R.id.etRetailerLandingPrice:
                if (fromTop == 1) {
                    etWhlLandingPrice.requestFocus();
                } else {
                    etRetailerMargin.requestFocus();
                }
                break;
            case R.id.etWholesalerLandingPrice:
                if (fromTop == 1) {
                    etShelfLife.requestFocus();
                } else {
                    etRetailerLandingPrice.requestFocus();
                }
                break;
            case R.id.etShelfLife:
                if (fromTop == 1) {
                } else {
                    etWhlLandingPrice.requestFocus();
                }
                break;
        }
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
