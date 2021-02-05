package com.arteriatech.ss.msecsales.rspl.beat;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.beat.dealer.DealerListFragment;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class BeatListActivity extends AppCompatActivity {
    public static boolean mBoolRefreshDone = false;
    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerSalesOrderHeader;
    TabLayout tabLayoutSalesOrderHeader;
    TodayBeatListFragment todayBeatFragment = null;
    DealerListFragment dealerListFragment = null;
    Toolbar toolbar;
    private String TAG = BeatListActivity.class.getSimpleName();
    private String cpGuid = "";
    private OtherBeatListFragment otherBeatFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_list);
        if (!Constants.restartApp(BeatListActivity.this)) {
            initializeUI();
        }
    }

    void initializeUI() {
        cpGuid = getIntent().getStringExtra(Constants.EXTRA_CPGUID);
        viewpagerSalesOrderHeader = (ViewPager) findViewById(R.id.viewpagerBeatHeader);
        tabLayoutSalesOrderHeader = (TabLayout) findViewById(R.id.tabLayoutBeatHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_beat_paln), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeTabLayout();
    }

    private void initializeTabLayout() {
        setupViewPager(viewpagerSalesOrderHeader);
        tabLayoutSalesOrderHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutSalesOrderHeader.setupWithViewPager(viewpagerSalesOrderHeader);
        viewpagerSalesOrderHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                if (position == 0) {
                    if (todayBeatFragment != null && mBoolRefreshDone) {
                        mBoolRefreshDone = false;
                        todayBeatFragment.onRefreshView();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        int position = viewpagerSalesOrderHeader.getCurrentItem();
        if (position == 0) {
            if (todayBeatFragment != null) {
                todayBeatFragment.onRefreshView();
            }
        } else if (position == 1) {
            if (otherBeatFragment != null) {
                otherBeatFragment.onRefreshView();
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        todayBeatFragment = new TodayBeatListFragment();

        Bundle bundle = getIntent().getExtras();
        bundle.putString(Constants.CPGUID, cpGuid);
        todayBeatFragment.setArguments(bundle);
        viewPagerAdapter.addFrag(todayBeatFragment, getString(R.string.lbl_today_beats));
        if (ConstantsUtils.getRollInformation(BeatListActivity.this).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
            otherBeatFragment = new OtherBeatListFragment();
            otherBeatFragment.setArguments(bundle);
            viewPagerAdapter.addFrag(otherBeatFragment, getString(R.string.lbl_other_beats));
        } else {
            dealerListFragment = new DealerListFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putString(Constants.comingFrom, Constants.BeatPlan);
            dealerListFragment.setArguments(bundle1);
            viewPagerAdapter.addFrag(dealerListFragment, getString(R.string.lbl_other_beats));
        }
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
