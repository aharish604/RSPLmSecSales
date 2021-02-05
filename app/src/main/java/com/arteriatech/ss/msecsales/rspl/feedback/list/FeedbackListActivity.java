package com.arteriatech.ss.msecsales.rspl.feedback.list;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class FeedbackListActivity extends AppCompatActivity {

    public static boolean mBoolRefreshDone = false;
    private ViewPager viewpagerHeader;
    private TabLayout tabLayoutHeader;
    private Toolbar toolbar;
    private ViewPagerTabAdapter viewPagerAdapter;
    private FeedBackListFragment feedbckHeaderListFragment = null;
    private FeedBackListFragment deviceFeedbckListFragment = null;
    TextView tv_RetailerName,tv_RetailerID;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private Bundle bundleExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
        }
        if (!Constants.restartApp(FeedbackListActivity.this)) {
            initializeUI();
        }
    }

    void initializeUI() {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        viewpagerHeader = (ViewPager) findViewById(R.id.viewpagerHeader);
        tabLayoutHeader = (TabLayout) findViewById(R.id.tabLayoutHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_feed_back), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeTabLayout();
    }

    private void initializeTabLayout() {
        setupViewPager(viewpagerHeader);
        tabLayoutHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutHeader.setupWithViewPager(viewpagerHeader);
        viewpagerHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (feedbckHeaderListFragment != null && mBoolRefreshDone) {
                        mBoolRefreshDone = false;
                        feedbckHeaderListFragment.onRefreshView();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        feedbckHeaderListFragment = new FeedBackListFragment();
        deviceFeedbckListFragment = new FeedBackListFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putString(Constants.comingFrom, Constants.NonDevice);
        bundle.putString(Constants.SyncType, Constants.FB_PD_sync);
        Bundle bundle1 = getIntent().getExtras();
        bundle1.putString(Constants.comingFrom, Constants.Device);
        bundle1.putString(Constants.SyncType, Constants.FBPostPD_sync);
        feedbckHeaderListFragment.setArguments(bundle);
        deviceFeedbckListFragment.setArguments(bundle1);
        viewPagerAdapter.addFrag(feedbckHeaderListFragment, getString(R.string.lbl_history));
        viewPagerAdapter.addFrag(deviceFeedbckListFragment, getString(R.string.lbl_so_device_order_list));
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displaySubTitle(String subTxt) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTxt);
        }
    }
}
