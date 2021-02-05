package com.arteriatech.ss.msecsales.rspl.complaintcreate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.SelfDisplayFragment;

import java.util.ArrayList;

public class CompCreateImageActivity extends AppCompatActivity implements ComplaintCreateView {
    Toolbar toolbar;
    Context context;
    private ComplaintCreateBean complainteCreateBean = null;
    private ComplaintCreatePresenterImpl presenter;
    private String mStrBundleRetID="";
    private String mStrBundleRetName="";
    private String mStrBundleCPGUID="";
    private String beatGUID="";
    private String ParentId="";
    private String mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID="";
    private String mStrComingFrom="";
    private ProgressDialog progressDialog=null;
    TextView tv_RetailerName;
    TextView tv_RetailerID;
    private boolean isClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_create_image);
        initializeView();
        Intent intent = getIntent();
        if (intent != null) {
            complainteCreateBean = (ComplaintCreateBean) intent.getSerializableExtra(Constants.EXTRA_BEAN);
            beatGUID = getIntent().getExtras().getString(Constants.BeatGUID);
            ParentId = getIntent().getExtras().getString(Constants.ParentId);
        }
        if (complainteCreateBean == null) {
            complainteCreateBean = new ComplaintCreateBean();
        }else {
            mStrBundleRetID = complainteCreateBean.getCPNo();
            mStrBundleRetName = complainteCreateBean.getmName();//bundleComplaintCreation.getString(Constants.RetailerName);
            mStrBundleCPGUID =complainteCreateBean.getCPGUID();// bundleComplaintCreation.getString(Constants.CPGUID);
            mStrBundleCPGUID32 =complainteCreateBean.getCPGUID32();// bundleComplaintCreation.getString(Constants.CPGUID);
            //mStrBundleCPGUID32 = complainteCreateBean.getCbundleComplaintCreation.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = complainteCreateBean.getmCPUID();//bundleComplaintCreation.getString(Constants.CPUID);
            mStrComingFrom =complainteCreateBean.getComingFrom();// bundleComplaintCreation.getString(Constants.comingFrom);
        }
        tv_RetailerName.setText(mStrBundleRetName);
        tv_RetailerID.setText(mStrBundleRetID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = CompCreateImageActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_complai_create), 0);

        presenter = new ComplaintCreatePresenterImpl(CompCreateImageActivity.this, this, true, CompCreateImageActivity.this, complainteCreateBean,ParentId);
        presenter.onImageStart();
        openImageFragment();
    }

    private void initializeView() {
        tv_RetailerName=(TextView)findViewById(R.id.tv_RetailerName);
        tv_RetailerID=(TextView)findViewById(R.id.tv_RetailerID);

    }


    private void openImageFragment() {
        Fragment fragment = new SelfDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsUtils.EXTRA_FROM, 1);
        bundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, true);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_item_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(CompCreateImageActivity.this, message);
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
    public void displayComplaintCategoryType(ArrayList<ValueHelpBean> feedbackType) {

    }

    @Override
    public void displayComplaintType(String[][] complaintType) {

    }

    @Override
    public void displayOrderedMaterialGroup(String[][] orderBy) {

    }

    @Override
    public void displayMaterialItemDescription(ArrayList<ComplaintCreateBeanUOMandDescription> orderBy) {

    }

    @Override
    public void navigateRetailer() {
        redirectActivity();
    }

    @Override
    public void errorCategoryType(String message) {

    }

    @Override
    public void errorComplaintType(String message) {

    }

    @Override
    public void errorOrderType(String message) {

    }

    @Override
    public void errorItemDetail(String message) {

    }

    @Override
    public void errorRemarks(String message) {

    }

    @Override
    public void errorQuantity(String message) {

    }

    @Override
    public void errorDate(String message) {

    }

    @Override
    public void errorBatch(String message) {

    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(CompCreateImageActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                isClickable=false;
                if (b) {
                    presenter.onSaveData();
                }
            }
        });
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(CompCreateImageActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
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
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, ParentId);
        startActivity(intentNavPrevScreen);
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
                if (!isClickable) {
                    isClickable = true;
                    presenter.onAsignData("", complainteCreateBean);
                }
                break;
        }
        return true;
    }
}
