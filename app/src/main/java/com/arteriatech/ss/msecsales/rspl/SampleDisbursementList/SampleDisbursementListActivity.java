package com.arteriatech.ss.msecsales.rspl.SampleDisbursementList;

import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplainListActivity;

public class SampleDisbursementListActivity extends AppCompatActivity {

    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerCompListHeader;
    TabLayout tabLayoutCompListHeader;
    SampleDisbursementListFragment sampleDisbursementListFragmentSync;
    SampleDisbursementListFragment sampleDisbursementListFragmentHis;
    public static boolean mBoolRefreshDone=false;
    Toolbar toolbar;
    private String TAG= ComplainListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_disbursement_list);
        initializeUI();
    }
    void initializeUI(){
        viewpagerCompListHeader =(ViewPager)findViewById(R.id.viewpagerComplaintsHeader);
        tabLayoutCompListHeader =(TabLayout)findViewById(R.id.tabLayoutComplintsHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sample_disbursement),0);
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
                Log.d(TAG, "PageScrolled: "+position);

            }
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: "+position);
                if(sampleDisbursementListFragmentHis!=null){
                sampleDisbursementListFragmentHis.getSampleDisbursementList();
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "PageScrolled statechnge: "+state);

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        sampleDisbursementListFragmentSync =new SampleDisbursementListFragment();
        Bundle bundle1 = getIntent().getExtras();
        bundle1.putString(Constants.comingFrom, Constants.Device);
        bundle1.putString(Constants.SyncType, Constants.SDPostPD_sync);
        sampleDisbursementListFragmentSync.setArguments(bundle1);

        sampleDisbursementListFragmentHis = new SampleDisbursementListFragment();
        Bundle bundle2 = getIntent().getExtras();
        bundle2.putString(Constants.comingFrom, Constants.NonDevice);
        bundle2.putString(Constants.SyncType, Constants.SD_PD_sync);
        sampleDisbursementListFragmentHis.setArguments(bundle2);


        viewPagerAdapter.addFrag(sampleDisbursementListFragmentHis, getString(R.string.lbl_history));
        viewPagerAdapter.addFrag(sampleDisbursementListFragmentSync,getString(R.string.lbl_so_device_order_list));
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
    public void setActionBarSubTitle(String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTitle);
        }
    }

}
