package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class ComplainListActivity extends AppCompatActivity {

    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerCompListHeader;
    TabLayout tabLayoutCompListHeader;
    ComplaintListFragment complaintListFragmentSync;
    ComplaintListFragment complaintListFragmentHist;
    public static boolean mBoolRefreshDone=false;
    Toolbar toolbar;
    private String TAG= ComplainListActivity.class.getSimpleName();
    TextView tv_RetailerName,tv_RetailerID;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private Bundle bundleExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_list);
        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
        }
        if (!Constants.restartApp(ComplainListActivity.this)) {
            initializeUI();
        }

    }
    void initializeUI(){
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        viewpagerCompListHeader =(ViewPager)findViewById(R.id.viewpagerComplaintsHeader);
        tabLayoutCompListHeader =(TabLayout)findViewById(R.id.tabLayoutComplintsHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_complaints),0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeTabLayout();
    }
    private void initializeTabLayout(){
        setupViewPager(viewpagerCompListHeader);
        tabLayoutCompListHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutCompListHeader.setupWithViewPager(viewpagerCompListHeader);
        viewpagerCompListHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: "+position);

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        complaintListFragmentSync =new ComplaintListFragment();
        Bundle bundle1 = getIntent().getExtras();
        bundle1.putString(Constants.comingFrom, Constants.Device);
        bundle1.putString(Constants.SyncType, Constants.UpLoad);
        complaintListFragmentSync.setArguments(bundle1);

        complaintListFragmentHist = new ComplaintListFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putString(Constants.comingFrom, Constants.NonDevice);
        bundle.putString(Constants.SyncType, Constants.DownLoad);
        complaintListFragmentHist.setArguments(bundle);

        viewPagerAdapter.addFrag(complaintListFragmentHist, getString(R.string.lbl_history));

        viewPagerAdapter.addFrag(complaintListFragmentSync,getString(R.string.lbl_so_device_order_list));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
