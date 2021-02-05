package com.arteriatech.ss.msecsales.rspl.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class FeedbackActivity extends AppCompatActivity implements FeedbackView{

    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    String beatGUID = "";
    String parentId = "";
    MaterialDesignSpinner spFeedBackType,spProductRelated;
    EditText etRemarks,etOthers;
    TextInputLayout tiRemarks,tiOthers;
    FeedbackPresenterImpl presenter;
    FeedbackBean feedbackBean =new FeedbackBean();
    ProgressDialog progressDialog=null;
    private TextView tvRetailerName, tvRetailerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Bundle bundleExtras = getIntent().getExtras();

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
        mContext = FeedbackActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_feed_back_create), 0);
        initUI();
        feedbackBean = new FeedbackBean();
        feedbackBean.setCPGUID(mStrBundleCPGUID32);
        feedbackBean.setCPNo(mStrBundleRetID);
        feedbackBean.setCPNo(mStrBundleRetID);
        feedbackBean.setParentId(parentId);
        presenter = new FeedbackPresenterImpl(FeedbackActivity.this, this, true,FeedbackActivity.this,feedbackBean);
        if (!Constants.restartApp(FeedbackActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI(){
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        spFeedBackType = (MaterialDesignSpinner) findViewById(R.id.spFeedBackType);
        spProductRelated = (MaterialDesignSpinner) findViewById(R.id.spProductRelated);
        etOthers = (EditText) findViewById(R.id.etOthers);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        tiOthers = (TextInputLayout) findViewById(R.id.tiOthers);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        tiOthers.setVisibility(View.GONE);
        spProductRelated.setVisibility(View.GONE);
        // set retailer name
        tvRetailerName.setText(mStrBundleRetName);
        // set retailer id
        tvRetailerID.setText(mStrBundleRetID);
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(250);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(FilterArray);
        etOthers.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});

        etOthers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiOthers.setErrorEnabled(false);
                feedbackBean.setOthers(s.toString());
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
                feedbackBean.setRemarks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(FeedbackActivity.this, message);
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
        UtilConstants.dialogBoxWithCallBack(FeedbackActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
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
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void displayByFeedback(final ArrayList<ValueHelpBean> feedbackType) {
        ArrayAdapter<ValueHelpBean> adapterShipToList = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, feedbackType) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spFeedBackType, position, getContext());
                return v;
            }
        };

        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spFeedBackType.setAdapter(adapterShipToList);
        spFeedBackType.showFloatingLabel();
        spFeedBackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedbackBean.setFeedbackType(feedbackType.get(position).getID());
                feedbackBean.setFeedbackTypeDesc(feedbackType.get(position).getDescription());
                feedbackBean.setParentID(feedbackType.get(position).getParentID());
                if(feedbackType.get(position).getID().equalsIgnoreCase(Constants.str_06)){
                    tiOthers.setVisibility(View.VISIBLE);
                }else{
                    tiOthers.setVisibility(View.GONE);
                }

                if(feedbackType.get(position).getID().equalsIgnoreCase(Constants.str_05)){
                    spProductRelated.setVisibility(View.VISIBLE);
                    presenter.getProductRelInfo(feedbackBean.getFeedbackType());
                }else{
                    spProductRelated.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayByProductRelInfo(final ArrayList<ValueHelpBean> alProdRelInfo) {
        ArrayAdapter<ValueHelpBean> adapterShipToList = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alProdRelInfo) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spProductRelated, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spProductRelated.setAdapter(adapterShipToList);
        spProductRelated.showFloatingLabel();
        spProductRelated.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedbackBean.setProdRelID(alProdRelInfo.get(position).getID());
                feedbackBean.setProdRelDesc(alProdRelInfo.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void errorFeedBackType(String message) {
        if (spFeedBackType.getVisibility() == View.VISIBLE)
            spFeedBackType.setError(message);
    }

    @Override
    public void errorProductRelated(String message) {
        if (spProductRelated.getVisibility() == View.VISIBLE)
            spProductRelated.setError(message);
    }

    @Override
    public void errorRemarks(String message) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(message);
    }

    @Override
    public void errorOthers(String message) {
        tiOthers.setErrorEnabled(true);
        tiOthers.setError(message);
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(FeedbackActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
                //next step
                if (ConstantsUtils.isAutomaticTimeZone(FeedbackActivity.this)) {
                    if (presenter.validateFields(feedbackBean)) {
                        presenter.onAsignData("","","",feedbackBean);
                    }
                } else {
                    ConstantsUtils.showAutoDateSetDialog(FeedbackActivity.this);
                }
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_feedback).setCancelable(false)
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
