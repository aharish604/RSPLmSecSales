package com.arteriatech.ss.msecsales.rspl.collection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

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
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by e10526 on 29-05-2018.
 */

public class InvoiceSelectionActivity extends AppCompatActivity implements CollectionCreateView, KeyboardView.OnKeyboardActionListener {
    private static int lastSelectedEditText = 0;
    private static EditText[] newInvoiceEdit = null;
    public HashMap<String, String> mapCheckedStateHashMap = new HashMap<String, String>();
    CollectionCreatePresenterImpl presenter;
    CollectionBean collBean = null;
    ProgressDialog progressDialog = null;
    String beatGUID="";
    String parentId="";
    ScrollView sv_invoice_list;
    KeyboardView keyboardView;
    Keyboard keyboard;
    double mDoubleBundleTotalInvAmt = 0.0, mDoubleTotalInvSum = 0.0, mDoubleTempOutAmt = 0.0, mDoubleTempTotalAmt = 0.0;
    TextView tv_RetailerName, tv_RetailerID;
    TextView tv_balance_amount, tv_coll_amount, tv_out_amount;
    double mDouOutAmt = 0.0, mDouCollectedAmt = 0.0, mDouBalnceAmt = 0.0;
    private Toolbar toolbar;
    private Context mContext;
    private ArrayList<InvoiceBean> alInvoiceList = new ArrayList<>();
    private ArrayList<InvoiceBean> selectedInvoice = new ArrayList<>();
    private double mDoubleTotInvAmt = 0.0;
    private EditText[] newInvoicePercnt = null;
    private EditText[] newInvoicePercntAmt = null;
    private TextView[] tvActualCollected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_sel_screen);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            collBean = (CollectionBean) bundleExtras.getSerializable(Constants.EXTRA_SO_DETAIL);
            mDoubleBundleTotalInvAmt = bundleExtras.getDouble(Constants.InvoiceAmount);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
            mDoubleTempOutAmt = mDoubleBundleTotalInvAmt;
        }
        if (!collBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
            collBean.setBundleTotalInvAmt(mDoubleBundleTotalInvAmt + "");
        }
        initUI();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = InvoiceSelectionActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_out_standing_bills), 0);
        presenter = new CollectionCreatePresenterImpl(InvoiceSelectionActivity.this, this, true, InvoiceSelectionActivity.this, collBean);
        presenter.getInvoices(collBean.getDivision());
    }

    private void initUI() {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        tv_balance_amount = (TextView) findViewById(R.id.tv_balance_amount);
        tv_coll_amount = (TextView) findViewById(R.id.tv_coll_amount);
        tv_out_amount = (TextView) findViewById(R.id.tv_out_amount);
        sv_invoice_list = (ScrollView) findViewById(R.id.sv_invoice_list);
        tv_RetailerID.setText(collBean.getCPNo());
        tv_RetailerName.setText(collBean.getCPName());
        setOutAmountToUI();
        initializeKeyboardDependencies();
    }

    private void setOutAmountToUI() {
        try {
            mDouOutAmt = Double.parseDouble(collBean.getOutstandingAmount());
        } catch (NumberFormatException e) {
            mDouOutAmt = 0.0;
            e.printStackTrace();
        }
        try {
            mDouCollectedAmt = mDoubleBundleTotalInvAmt;
        } catch (Exception e) {
            mDouCollectedAmt = 0.0;
            e.printStackTrace();
        }
        try {
            mDouBalnceAmt = mDouOutAmt - mDouCollectedAmt;
        } catch (Exception e) {
            mDouBalnceAmt = 0.0;
            e.printStackTrace();
        }
        tv_out_amount.setText(Constants.getCurrencySymbol(collBean.getCurrency(), mDouOutAmt + ""));
        tv_coll_amount.setText(Constants.getCurrencySymbol(collBean.getCurrency(), mDouCollectedAmt + ""));
        tv_balance_amount.setText(Constants.getCurrencySymbol(collBean.getCurrency(), mDouBalnceAmt + ""));
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceSelectionActivity.this, message);
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
    public void displayOutstandingAmount(String ousStndingValue) {

    }

    @Override
    public void displayByCollectionData(ArrayList<ValueHelpBean> alColltype,
                                        ArrayList<ValueHelpBean> alPaymentMode,
                                        ArrayList<ValueHelpBean> alBankName,
                                        String outstandingData,ArrayList<DMSDivisionBean> alDMSDiv) {

    }

    @Override
    public void displayInvoiceData(ArrayList<InvoiceBean> alInvList) {
        alInvoiceList.addAll(alInvList);
        displayInvoiceValues();
    }

    @Override
    public void errorRefType(String message) {

    }

    @Override
    public void errorPaymentMode(String message) {

    }

    @Override
    public void errorBankName(String message) {

    }

    @Override
    public void errorDivision(String message) {

    }

    @Override
    public void errorRemarks(String message) {

    }

    @Override
    public void errorChequeDate(String message) {

    }

    @Override
    public void errorUTRNoOrChequeDD(String message) {

    }

    @Override
    public void errorAmount(String message) {

    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(InvoiceSelectionActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    presenter.onSaveData();
                }
            }
        });
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(InvoiceSelectionActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, collBean.getCPNo());
        intentNavPrevScreen.putExtra(Constants.RetailerName, collBean.getCPName());
        intentNavPrevScreen.putExtra(Constants.CPUID, collBean.getCpUID());
        intentNavPrevScreen.putExtra(Constants.comingFrom, collBean.getComingFrom());
        intentNavPrevScreen.putExtra(Constants.CPGUID, collBean.getCPGUID());
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
       /* if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }*/
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void errorInvoiceScreen(String message) {
        UtilConstants.showAlert(message, InvoiceSelectionActivity.this);
    }

    @Override
    public void errorCollScreen(String message) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu.findItem(R.id.menu_collection_next).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                onSave();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSave() {
        if (ConstantsUtils.isAutomaticTimeZone(InvoiceSelectionActivity.this)) {
            if (!presenter.validateOutstanding(selectedInvoice, collBean)) {
                presenter.onAsignData("", collBean.getRefTypeID(), collBean, "02");
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(InvoiceSelectionActivity.this);
        }
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(InvoiceSelectionActivity.this, R.xml.ll_up_down_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void displayInvoiceValues() {
        @SuppressLint("InflateParams")
        TableLayout tlInvoiceList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llInvoiceList;
        String configDateFormat = ConstantsUtils.getConfigTypeDateFormat(mContext);
        if (!alInvoiceList.isEmpty()
                && alInvoiceList.size() > 0) {

            newInvoiceEdit = new EditText[alInvoiceList.size()];
            newInvoicePercnt = new EditText[alInvoiceList.size()];
            newInvoicePercntAmt = new EditText[alInvoiceList.size()];
            tvActualCollected = new TextView[alInvoiceList.size()];

            for (int i = 0; i < alInvoiceList.size(); i++) {
                final int selvalue = i;
                final InvoiceBean newbean = alInvoiceList.get(i);
                llInvoiceList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_inv_sel_item,
                                null, false);

                TextView tvInvNo = (TextView) llInvoiceList.findViewById(R.id.tv_invoice_number);
                TextView tvDueDays = (TextView) llInvoiceList.findViewById(R.id.tv_due_days);
                final LinearLayout llStatusColor = (LinearLayout) llInvoiceList.findViewById(R.id.llStatusColor);
                tvInvNo.setText(alInvoiceList.get(i).getInvoiceNo());

                if (alInvoiceList.get(i).getDeviceInvStatus().equalsIgnoreCase("")) {
                    tvInvNo.setTextColor(getResources().getColor(R.color.icon_text_blue));
                } else {
                    tvInvNo.setTextColor(getResources().getColor(R.color.InvStatusRed));
                }

                if (Double.parseDouble(!alInvoiceList.get(i).getDueDays().equalsIgnoreCase("") ? alInvoiceList.get(i).getDueDays() : "0") > 1) {
                    tvDueDays.setText(alInvoiceList.get(i).getDueDays() + " " + getString(R.string.days));
                } else {
                    tvDueDays.setText(alInvoiceList.get(i).getDueDays() + " " + getString(R.string.day));
                }
                String convertDateFormat = "";
                if (!TextUtils.isEmpty(alInvoiceList.get(i).getInvoiceDate())) {
                    convertDateFormat = UtilConstants.convertDateIntoDeviceFormat(mContext, alInvoiceList.get(i).getInvoiceDate(), configDateFormat);
                }
                ((TextView) llInvoiceList.findViewById(R.id.tvBillDate)).setText(convertDateFormat);

                ((TextView) llInvoiceList.findViewById(R.id.tv_coll_det_paid_amt_ex))
                        .setText(Constants.getCurrencySymbol(alInvoiceList.get(i).getCurrency(), alInvoiceList.get(i).getPaidAmount()));
//                        .setText(UtilConstants.getCurrencySymbol(alInvoiceList.get(i).getCurrency())+" "+UtilConstants.removeLeadingZerowithTwoDecimal("0.00"));

                ((TextView) llInvoiceList.findViewById(R.id.tv_invoice_amt))
                        .setText(Constants.getCurrencySymbol(alInvoiceList.get(i).getCurrency(), alInvoiceList.get(i).getInvoiceAmount()));

                ((TextView) llInvoiceList.findViewById(R.id.tv_pending_amt_ex))
                        .setText(Constants.getCurrencySymbol(alInvoiceList.get(i).getCurrency(), alInvoiceList.get(i).getInvoiceOutstanding()));

                //new 05122016
                final TextView tvBalAmt = (TextView) llInvoiceList.findViewById(R.id.tv_bal_amt);
                final TextView tvPercentageAmount = (TextView) llInvoiceList.findViewById(R.id.tvPercentageAmount);
                tvBalAmt.setText(Constants.getCurrencySymbol(alInvoiceList.get(i).getCurrency(), alInvoiceList.get(i).getInvoiceOutstanding()));

                newInvoiceEdit[i] = (EditText) llInvoiceList.findViewById(R.id.ed_invoice_inv_amount);
                newInvoicePercnt[i] = (EditText) llInvoiceList.findViewById(R.id.ed_invoice_percentage);
                newInvoicePercntAmt[i] = (EditText) llInvoiceList.findViewById(R.id.ed_invoice_percentage_amount);
                tvActualCollected[i] = (TextView) llInvoiceList.findViewById(R.id.tvActualCollected);

                newInvoiceEdit[i].setCursorVisible(true);
                newInvoiceEdit[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            lastSelectedEditText = selvalue;
                            showCustomKeyboard(v);
                        } else {
                            lastSelectedEditText = selvalue;
                            hideCustomKeyboard();
                        }

                    }
                });
                newInvoiceEdit[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedEditText = selvalue;
                        v.requestFocus();
                        showCustomKeyboard(v);
                        Constants.setCursorPostion(newInvoiceEdit[selvalue], v, event);
                        return true;
                    }
                });


                mapCheckedStateHashMap.put(newbean.getInvoiceGUID(), newbean.getInputInvAmount());

                if (newbean.isItemSelected()) {
                    newInvoiceEdit[selvalue].setText(newbean.getInputInvAmount());

                    newbean.setItemSelected(true);
                    selectedInvoice.add(newbean);

                }

                if (mDoubleTempOutAmt > 0) {
                    if (mDoubleTempOutAmt >= Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding()) && mDoubleTempTotalAmt != mDoubleBundleTotalInvAmt) {

                        mDoubleTempOutAmt = mDoubleTempOutAmt - Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding());

                        mDoubleTempTotalAmt = mDoubleTempTotalAmt + Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding());

                        newInvoiceEdit[selvalue].setText(UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                        newbean.setItemSelected(true);
                        newbean.setInputInvAmount(UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                        mapCheckedStateHashMap.put(newbean.getInvoiceGUID(), UtilConstants.removeLeadingZero(alInvoiceList.get(i).getInvoiceOutstanding()));

                    } else if (mDoubleTempOutAmt <= Double.parseDouble(alInvoiceList.get(i).getInvoiceOutstanding()) && mDoubleTempTotalAmt != mDoubleBundleTotalInvAmt) {

                        mDoubleTempTotalAmt = mDoubleTempTotalAmt + mDoubleTempOutAmt;

                        newInvoiceEdit[selvalue].setText(UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                        newbean.setItemSelected(true);
                        newbean.setInputInvAmount(UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                        mapCheckedStateHashMap.put(newbean.getInvoiceGUID(), UtilConstants.removeLeadingZero(mDoubleTempOutAmt + ""));
                    } else {
                        newbean.setItemSelected(false);
                    }
                } else {
                    newbean.setItemSelected(false);
                }
                updateBalAmount(newbean.getInvoiceGUID(), tvBalAmt, newbean, llStatusColor);

                selectedInvoice.add(newbean);
                //percentage
                displayPercentage(newbean, selvalue, alInvoiceList.get(i).getCurrency(), tvPercentageAmount);

                UtilConstants.editTextDecimalFormat(newInvoiceEdit[selvalue], 13, 2);
                newInvoiceEdit[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence source, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String s1 = s.toString();

                        if (s1.equals(".")) {
                            s1 = "0.";
                        }

                        if (!s1.equalsIgnoreCase(""))
                            newInvoiceEdit[selvalue].setBackgroundResource(R.drawable.edittext);
                        if (selectedInvoice.contains(newbean)) {
                            if (!s1.equalsIgnoreCase("")) {
                                newbean.setItemSelected(true);
                            } else {
                                newbean.setItemSelected(false);
                            }
                            newbean.setInputInvAmount(s1);
                        }

                        if (mapCheckedStateHashMap.containsKey(newbean.getInvoiceGUID())) {
                            mapCheckedStateHashMap.put(newbean.getInvoiceGUID(), s1);
                        }
                        sumOfInvAmt();
                        updateBalAmount(newbean.getInvoiceGUID(), tvBalAmt, newbean, llStatusColor);

                        //percentage
                        displayPercentage(newbean, selvalue, newbean.getCurrency(), tvPercentageAmount);

                    }
                });

                newInvoicePercnt[i].setCursorVisible(true);
                newInvoicePercnt[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            showCustomKeyboard(v);
                        } else {
                            hideCustomKeyboard();
                        }

                    }
                });
                newInvoicePercnt[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.requestFocus();
                        showCustomKeyboard(v);
                        Constants.setCursorPostion(newInvoicePercnt[selvalue], v, event);
                        return true;
                    }
                });
                UtilConstants.editTextDecimalFormat(newInvoicePercnt[selvalue], 3, 3);
                newInvoicePercnt[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String percentage = String.valueOf(charSequence);
                        newbean.setDiscountPer(percentage);
                        newbean.setDiscountEnteredAmt("");
                        newbean.setDiscountAmt("");
                        try {
                            if (!TextUtils.isEmpty(percentage)) {
                                if (!TextUtils.isEmpty(newbean.getInputInvAmount())) {
                                    BigDecimal invoiceAmt = new BigDecimal(newbean.getInputInvAmount());
                                    BigDecimal percent = new BigDecimal(percentage);
                                    BigDecimal percentAmt = (invoiceAmt.divide(new BigDecimal("100"))).multiply(percent);
                                    newbean.setDiscountAmt(String.valueOf(percentAmt));
                                    tvPercentageAmount.setText(Constants.getCurrencySymbol(newbean.getCurrency(), String.valueOf(percentAmt)));
                                    tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(newbean.getCurrency(), String.valueOf(invoiceAmt.subtract(percentAmt))));
                                }
                                tvPercentageAmount.setVisibility(View.VISIBLE);
                                newInvoicePercntAmt[selvalue].setVisibility(View.GONE);
                            } else {
                                tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(newbean.getCurrency(), newbean.getInputInvAmount()));
                                tvPercentageAmount.setVisibility(View.GONE);
//                                newInvoicePercntAmt[selvalue].setVisibility(View.VISIBLE);
                                newInvoicePercntAmt[selvalue].setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                newInvoicePercntAmt[i].setCursorVisible(true);
                newInvoicePercntAmt[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            showCustomKeyboard(v);
                        } else {
                            hideCustomKeyboard();
                        }

                    }
                });
                newInvoicePercntAmt[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.requestFocus();
                        showCustomKeyboard(v);
                        Constants.setCursorPostion(newInvoicePercntAmt[selvalue], v, event);
                        return true;
                    }
                });
                UtilConstants.editTextDecimalFormat(newInvoicePercntAmt[selvalue], 13, 2);
                newInvoicePercntAmt[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String percentage = String.valueOf(charSequence);
                        newbean.setDiscountEnteredAmt(percentage);
                        newbean.setDiscountPer("");
                        try {
                            if (!TextUtils.isEmpty(percentage)) {
                                if (!TextUtils.isEmpty(newbean.getInputInvAmount())) {
                                    BigDecimal invoiceAmt = new BigDecimal(newbean.getInputInvAmount());
                                    BigDecimal percent = new BigDecimal(percentage);
//                                    BigDecimal percentAmt = (invoiceAmt.subtract(percent));
//                                    tvPercentageAmount.setText(String.valueOf(percentAmt));
                                    tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(newbean.getCurrency(), String.valueOf(invoiceAmt.subtract(percent))));
                                }
                                newInvoicePercnt[selvalue].setVisibility(View.INVISIBLE);
                            } else {
                                tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(newbean.getCurrency(), newbean.getInputInvAmount()));
                                newInvoicePercnt[selvalue].setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvPercentageAmount.setVisibility(View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                tlInvoiceList.addView(llInvoiceList);
            }


        } else {

            llInvoiceList = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null, false);

            tlInvoiceList.addView(llInvoiceList);
        }

        sumOfInvAmt();

        sv_invoice_list.addView(tlInvoiceList);
        sv_invoice_list.requestLayout();

    }

    private void displayPercentage(InvoiceBean newbean, int selvalue, String currency, TextView tvPercentageAmount) {
        try {
            if (!TextUtils.isEmpty(newbean.getInputInvAmount())) {
                BigDecimal invoiceAmt = new BigDecimal(newbean.getInvoiceAmount());
                if (invoiceAmt.compareTo(new BigDecimal(newbean.getInputInvAmount())) == 0) {
                    newInvoicePercnt[selvalue].setVisibility(View.VISIBLE);
//                    newInvoicePercntAmt[selvalue].setVisibility(View.VISIBLE);
                    newInvoicePercntAmt[selvalue].setVisibility(View.INVISIBLE);
                    tvPercentageAmount.setVisibility(View.GONE);
                    tvPercentageAmount.setText("");
                    newInvoicePercnt[selvalue].setText("");
                    newInvoicePercntAmt[selvalue].setText("");
                    tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(currency, newbean.getInputInvAmount()));
                } else {
                    newInvoicePercnt[selvalue].setVisibility(View.INVISIBLE);
                    newInvoicePercntAmt[selvalue].setVisibility(View.INVISIBLE);
                    tvPercentageAmount.setVisibility(View.GONE);
                    tvPercentageAmount.setText("");
                    tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(currency, newbean.getInputInvAmount()));
                }
            } else {
                newInvoicePercnt[selvalue].setVisibility(View.INVISIBLE);
                newInvoicePercntAmt[selvalue].setVisibility(View.INVISIBLE);
                tvPercentageAmount.setVisibility(View.GONE);
                tvPercentageAmount.setText("");
                tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(currency,newbean.getInputInvAmount()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            newInvoicePercnt[selvalue].setVisibility(View.INVISIBLE);
            newInvoicePercntAmt[selvalue].setVisibility(View.INVISIBLE);
            tvPercentageAmount.setVisibility(View.GONE);
            tvPercentageAmount.setText("");
            tvActualCollected[selvalue].setText(Constants.getCurrencySymbol(currency, newbean.getInputInvAmount()));
        }
    }

    private void sumOfInvAmt() {
        double mDouInvPrice = 0.0;
        if (!mapCheckedStateHashMap.isEmpty()) {
            Iterator mapSelctedValues = mapCheckedStateHashMap.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                Double invPrice = null;
                try {
                    invPrice = Double.parseDouble(mapCheckedStateHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapCheckedStateHashMap.get(Key));
                } catch (NumberFormatException e) {
                    invPrice = 0.0;
                }
                mDouInvPrice = mDouInvPrice + invPrice;
            }
        }
        mDoubleTotInvAmt = mDouInvPrice;
    }

    private void updateBalAmount(String key, TextView textView, InvoiceBean invoiceBean, LinearLayout iv_coll_status) {
        Double invPrice = null;
        try {
            invPrice = Double.parseDouble(mapCheckedStateHashMap.get(key).equalsIgnoreCase("") ? "0" : mapCheckedStateHashMap.get(key));
        } catch (NumberFormatException e) {
            invPrice = 0.0;
        }
        Double invOutStanding = 0.0;
        try {
            invOutStanding = Double.parseDouble(invoiceBean.getInvoiceOutstanding());
        } catch (NumberFormatException e) {
            invOutStanding = 0.0;
        }
        Double totalBalAmount = null;
        try {
            totalBalAmount = invOutStanding - invPrice;
        } catch (Exception e) {
            totalBalAmount = 0.0;
            e.printStackTrace();
        }
        textView.setText(Constants.getCurrencySymbol(invoiceBean.getCurrency(), totalBalAmount + ""));


        int collStatus = SOUtils.displayCollectionStatusColor(totalBalAmount + "", InvoiceSelectionActivity.this);

        iv_coll_status.setBackgroundColor(collStatus);
    }

    @Override
    public void onBackPressed() {

        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            finish();
        }

    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = alInvoiceList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (newInvoiceEdit[lastSelectedEditText] != null)
                    newInvoiceEdit[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (newInvoiceEdit[lastSelectedEditText - 1] != null)
                    newInvoiceEdit[lastSelectedEditText - 1].requestFocus();
            }

        }

    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        return super.onKeyLongPress(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //Handle what you want in long press.
            super.onKeyLongPress(keyCode, event);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(newInvoiceEdit[lastSelectedEditText], Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(newInvoiceEdit[lastSelectedEditText], Constants.Y);
                break;
            case 1:
                changeEditTextFocus(0);
                break;
            case 2:
                changeEditTextFocus(1);
                break;
            case 56:
                if (!checkAlreadyDotIsThere()) {
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


    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public void showCustomKeyboard(View v) {

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if (v != null) {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    private Boolean checkAlreadyDotIsThere() {
        ArrayList<EditText> myEditTextList = new ArrayList<EditText>();

        for (int i = 0; i < sv_invoice_list.getChildCount(); i++)
            if (sv_invoice_list.getChildAt(i) instanceof EditText) {
                myEditTextList.add((EditText) sv_invoice_list.getChildAt(i));
                if (myEditTextList.get(i).hasFocus()) {
                    String textValue = myEditTextList.get(i).getText().toString();
                    if (textValue.contains(".")) {
                        return true;
                    } else
                        return false;
                }
            }
        return false;
    }
}
