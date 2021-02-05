package com.arteriatech.ss.msecsales.rspl.reports.returnorder.filters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.util.ArrayList;

public class ROFilterActivity extends AppCompatActivity implements ROFilterView, DateFilterFragment.OnFragmentInteractionListener {

    public static final String EXTRA_SO_STATUS = "extraStatus";
    public static final String EXTRA_SO_STATUS_NAME = "extraStatusName";
    public static final String EXTRA_DELV_STATUS = "extraDelvStatus";
    public static final String EXTRA_DELV_STATUS_NAME = "extraDelvStatusName";

    private Toolbar toolbar;
    private DateFilterFragment dateFilterFragment;
    private ROFilterModelImpl filterModel;
    private ProgressDialog progressDialog = null;
    private LinearLayout rgDelvStatus;
    private String oldDelvStatus = "";
    private String oldStatus = "";
    private String newStatusId = "";
    private String newDelvStatusId = "";
    private String newDelvStatusName = "";
    private String newStatusName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.ro_filter), 0);
        //date filter fragment open
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            oldDelvStatus = bundle.getString(EXTRA_DELV_STATUS, "");
            oldStatus = bundle.getString(EXTRA_SO_STATUS, "");
            dateFilterFragment = new DateFilterFragment();
        }
        initUI();

        //other filter type
        filterModel = new ROFilterModelImpl(ROFilterActivity.this, this);
        if (!Constants.restartApp(ROFilterActivity.this)) {
            filterModel.onStart();
        }
    }

    private void initUI() {
//        rgStatus = (LinearLayout) findViewById(R.id.rgStatus);
        rgDelvStatus = (LinearLayout) findViewById(R.id.rgDelvStatus);
    }


    @Override
    public void onFragmentInteraction(String startDate, String endDate, String filterType) {
        Intent intent = new Intent();
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_SO_STATUS, newStatusId);
        intent.putExtra(EXTRA_SO_STATUS_NAME, newStatusName);
        intent.putExtra(EXTRA_DELV_STATUS, newDelvStatusId);
        intent.putExtra(EXTRA_DELV_STATUS_NAME, newDelvStatusName);
        setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_apply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.apply:
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SO_STATUS, newStatusId);
                intent.putExtra(EXTRA_SO_STATUS_NAME, newStatusName);
                intent.putExtra(EXTRA_DELV_STATUS, newDelvStatusId);
                intent.putExtra(EXTRA_DELV_STATUS_NAME, newDelvStatusName);
                setResult(ConstantsUtils.ACTIVITY_RESULT_FILTER, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayList(ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeen, ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList) {
        displaySelvStatusList(ROFilterActivity.this, configTypesetDeliveryList);
    }

    private void displaySelvStatusList(Context mContext, ArrayList<ConfigTypesetTypesBean> configTypesetTypesList) {
        try {
            rgDelvStatus.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RadioGroup radioGroupHeader = new RadioGroup(this);
        radioGroupHeader.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        radioGroupHeader.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton != null) {
                    newDelvStatusId = radioButton.getTag(R.id.delv_id_value).toString();
                    newDelvStatusName = radioButton.getTag(R.id.delv_name_value).toString();
                }
            }
        });
        rgDelvStatus.addView(radioGroupHeader);
        Drawable img=null;
        if (configTypesetTypesList != null) {
            if (configTypesetTypesList.size() > 0) {
                for (int i = 0; i < configTypesetTypesList.size(); i++) {
                    ConfigTypesetTypesBean configTypesetTypesBean = configTypesetTypesList.get(i);
                    RadioButton radioButtonView = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.radio_button_item, null, false);
                    radioButtonView.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setId(i + 10000);
                    radioButtonView.setTag(R.id.delv_id_value, configTypesetTypesBean.getTypes());
                    radioButtonView.setTag(R.id.delv_name_value, configTypesetTypesBean.getTypesName());
                    radioButtonView.setText(configTypesetTypesBean.getTypesName());
                    img = SOUtils.displayStatusImage(configTypesetTypesBean.getTypes(),"",mContext);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        radioButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
                    } else {
                        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    }
                    radioGroupHeader.addView(radioButtonView);
                    if (oldDelvStatus.equalsIgnoreCase(configTypesetTypesBean.getTypes())) {
                        radioButtonView.setChecked(true);
                    }
                }
            }
        }
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(ROFilterActivity.this, message);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = ConstantsUtils.showProgressDialog(ROFilterActivity.this, getString(R.string.app_loading));
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        filterModel.onDestroy();
        super.onDestroy();
    }
}
