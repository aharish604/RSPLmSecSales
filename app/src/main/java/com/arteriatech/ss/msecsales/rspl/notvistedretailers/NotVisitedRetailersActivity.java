package com.arteriatech.ss.msecsales.rspl.notvistedretailers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RemarkReasonBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by e10526 on 21-04-2018.
 */

public class NotVisitedRetailersActivity extends AppCompatActivity implements NotVisitedRetailersView {

    MaterialDesignSpinner spRetailer, spReason;
    EditText etRemarks;
    TextInputLayout tiRemarks;
    NotVisitedRetailerPresenterImpl presenter;
    ProgressDialog progressDialog = null;
    String closingDayType = "", closingDate = "", selReson = "", selDealer, selDelName = "", selDelGuid = "",
            selRoutePlanKey = "", selDelGuid36 = "";
    MenuItem menu_save, menu_next;
    private Toolbar toolbar;
    private String[][] alRetailer = null;
    private Context mContext;
    private int currentRetailerId = 0;
    private Boolean nextRetailer = false;
    private String selectedReasonDesc = "";
    private String selectedReasonCode = "";
    private ProgressDialog pdLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_visted_retailers);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            closingDayType = bundleExtras.getString(Constants.ClosingeDayType);
            closingDate = bundleExtras.getString(Constants.ClosingeDay);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = NotVisitedRetailersActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_not_visited_retailer), 0);
        initUI();
        presenter = new NotVisitedRetailerPresenterImpl(NotVisitedRetailersActivity.this, this, true,
                NotVisitedRetailersActivity.this, closingDate);
        if (!Constants.restartApp(NotVisitedRetailersActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI() {
        spRetailer = (MaterialDesignSpinner) findViewById(R.id.spRetailer);
        spReason = (MaterialDesignSpinner) findViewById(R.id.spReason);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(250);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(FilterArray);

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
                        String mStrRemarks = "";
                        try {
                            mStrRemarks = s.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mStrRemarks = "";
                        }
                        alRetailer[currentRetailerId][8] = mStrRemarks;
                        if (mStrRemarks.equalsIgnoreCase("")) {
                            mStrRemarks = selectedReasonDesc;
                        } else {
                            mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                        }
                        alRetailer[currentRetailerId][5] = mStrRemarks;
                    } else if (selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
                        String mStrRemarks = "";
                        try {
                            mStrRemarks = s.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mStrRemarks = "";
                        }
                        alRetailer[currentRetailerId][8] = mStrRemarks;
                        if (mStrRemarks.equalsIgnoreCase("")) {
                            mStrRemarks = selectedReasonDesc;
                        } else {
                            mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                        }
                        alRetailer[currentRetailerId][5] = mStrRemarks;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tiRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(NotVisitedRetailersActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        try {
            menu_save.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
    }

    @Override
    public void displayMessageAndNavigateToPreviosScreen(String message) {
        try {
            menu_save.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(NotVisitedRetailersActivity.this, R.style.MyTheme);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.isSync = false;
                                    onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    @Override
    public void remarksNotEnterdSpecificRetailer(String message) {
        try {
            menu_save.setEnabled(true);
            menuVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UtilConstants.showAlert(message, NotVisitedRetailersActivity.this);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(NotVisitedRetailersActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void menuVisible(boolean isNextEnable) {
        nextRetailer = isNextEnable;
        menuVisible();
    }


    @Override
    public void displayByDealers(final String[][] alRetailer, final ArrayList<RemarkReasonBean> selReason) {
        //displaying retailer name and number in spinner
        this.alRetailer = alRetailer;
        setDealers(selReason);
    }

    public void displayRetailerReason(final ArrayList<RemarkReasonBean> reasonCodedesc) {
        ArrayAdapter<RemarkReasonBean> reasonadapter = new ArrayAdapter<RemarkReasonBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, reasonCodedesc) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spReason, position, getContext());
                return v;
            }
        };
        spReason.setAdapter(reasonadapter);
        for (int i = 0; i < reasonCodedesc.size(); i++) {
            if (selReson.equalsIgnoreCase(reasonCodedesc.get(i).getReasonCode())) {
                spReason.setSelection(i);
                break;
            }
        }

        reasonadapter.setDropDownViewResource(R.layout.spinnerinside);
        spReason.setAdapter(reasonadapter);
        spReason.showFloatingLabel();
        spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReasonDesc = reasonCodedesc.get(position).getReasonDesc();
                selectedReasonCode = reasonCodedesc.get(position).getReasonCode();
//                spReason.setBackgroundResource(R.drawable.spinner_bg);

                if (!selectedReasonCode.equalsIgnoreCase("00") && !selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
                    alRetailer[currentRetailerId][7] = selectedReasonCode;

                    String mStrRemarks = "";
                    if (etRemarks.getText() != null) {
                        try {
                            mStrRemarks = etRemarks.getText().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mStrRemarks = "";
                        }
                        alRetailer[currentRetailerId][8] = mStrRemarks;
                        if (mStrRemarks.equalsIgnoreCase("")) {
                            mStrRemarks = selectedReasonDesc;
                        } else {
                            mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                        }
                    } else {
                        alRetailer[currentRetailerId][8] = "";
                        mStrRemarks = selectedReasonDesc;
                    }
                    alRetailer[currentRetailerId][5] = mStrRemarks;

                } else if (selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {
                    alRetailer[currentRetailerId][7] = selectedReasonCode;

                    String mStrRemarks = "";
                    if (etRemarks.getText() != null) {
                        try {
                            mStrRemarks = etRemarks.getText().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mStrRemarks = "";
                        }
                        alRetailer[currentRetailerId][8] = mStrRemarks;
                        if (mStrRemarks.equalsIgnoreCase("")) {
                            mStrRemarks = selectedReasonDesc;
                        } else {
                            mStrRemarks = selectedReasonDesc + " " + mStrRemarks;
                        }
                    } else {
                        mStrRemarks = "";
                        alRetailer[currentRetailerId][8] = mStrRemarks;
                    }
                    alRetailer[currentRetailerId][5] = mStrRemarks;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setDealers(final ArrayList<RemarkReasonBean> selReason) {


        if (alRetailer == null) {
            alRetailer = new String[1][9];
            alRetailer[0][0] = "";
            alRetailer[0][1] = "";
            alRetailer[0][2] = "";
            alRetailer[0][3] = "";
            alRetailer[0][4] = "";
            alRetailer[0][5] = "";
            alRetailer[0][6] = "";
            alRetailer[0][7] = "";
            alRetailer[0][8] = "";
        }

        //displaying retailer name and number in spinner
        String[] dealerList = new String[0];
        try {
            dealerList = new String[alRetailer.length];
            for (int i = 0; i < alRetailer.length; i++) {
                dealerList[i] = alRetailer[i][2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, dealerList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spRetailer, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spRetailer.setAdapter(adapterShipToList);
        spRetailer.showFloatingLabel();
        spRetailer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentRetailerId = position;
                selDealer = alRetailer[position][0];
                selDelName = alRetailer[position][2];
                selDelGuid = alRetailer[position][3];
                selRoutePlanKey = alRetailer[position][4];
                try {
                    etRemarks.setText(alRetailer[position][8]);
                } catch (Exception e) {
                    etRemarks.setText("");
                    e.printStackTrace();
                }
                selDelGuid36 = alRetailer[position][6];
                try {
                    selReson = alRetailer[position][7] != null ? alRetailer[position][7] : "";
                } catch (Exception e) {
                    e.printStackTrace();
                    selReson = "";
                }
                displayRetailerReason(selReason);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        if (nextRetailer) {
            if (currentRetailerId <= alRetailer.length) {
                currentRetailerId = currentRetailerId + 1;
                spRetailer.setSelection(currentRetailerId);
                etRemarks.setText("");
            }

        }
        int remarksIncompleted = 0;
        for (int i = 0; i < alRetailer.length; i++) {
            if (alRetailer[i][7].equals("")) {
                remarksIncompleted++;

            }
        }
        if (remarksIncompleted > 1) {
            nextRetailer = true;
        } else {
            nextRetailer = false;
        }
        menuVisible();


    }

    private void menuVisible() {
        try {
            if (!nextRetailer) {
                menu_save.setVisible(true);
                menu_next.setVisible(false);
            } else {
                menu_save.setVisible(false);
                menu_next.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorFeedBackType(String message) {
        if (spRetailer.getVisibility() == View.VISIBLE)
            spRetailer.setError(message);
    }

    @Override
    public void errorReason(String message) {
        if (spReason.getVisibility() == View.VISIBLE)
            spReason.setError(message);
    }

    @Override
    public void errorRemarks(String message) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(message);
    }

    @Override
    public void errorOthers(String message) {
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(NotVisitedRetailersActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu_save = menu.findItem(R.id.menu_collection_save);
        menu_next = menu.findItem(R.id.menu_collection_next);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!nextRetailer) {
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

            case R.id.menu_collection_next:
                onNext();
                break;
            case R.id.menu_collection_save:
                menu_save.setEnabled(false);
                onSave();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSave() {
        Constants.MapEntityVal.clear();
        final String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null ";
        Thread thread =null;
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OfflineManager.getAttendance(dayEndqry);
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
            if (Constants.isEndateAndEndTimeValid(UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate)), Constants.MapEntityVal.get(Constants.StartTime) + "")) {
                validDateTime();
            } else {
                // display error pop up
                menu_save.setEnabled(true);
                String mStrDate = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) Constants.MapEntityVal.get(Constants.StartDate));
                UtilConstants.showAlert(getString(R.string.msg_end_date_should_be_greterthan_startdate, mStrDate), NotVisitedRetailersActivity.this);
            }
        } else {
            validDateTime();
        }

    }

    private void validDateTime() {
        pdLoadDialog = Constants.showProgressDialog(NotVisitedRetailersActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(NotVisitedRetailersActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }else {
                    menu_save.setEnabled(true);
                }
            }
        });
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(NotVisitedRetailersActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(NotVisitedRetailersActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    presenter.checkValidation(alRetailer);
                }else {
                    menu_save.setEnabled(true);
                }
            }
        });
    }

    private void closeProgressDialog() {
        try {
            if(pdLoadDialog!=null && pdLoadDialog.isShowing()) {
                pdLoadDialog.dismiss();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(NotVisitedRetailersActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                locationPerGranted();
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
                locationPerGranted();
            }
        }
    }

    private void onNext() {
        presenter.checkRemarksValidation(currentRetailerId, selectedReasonCode, selectedReasonDesc, etRemarks);
    }
}
