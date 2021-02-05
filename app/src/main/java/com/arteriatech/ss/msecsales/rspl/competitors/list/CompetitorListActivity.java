package com.arteriatech.ss.msecsales.rspl.competitors.list;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class CompetitorListActivity extends AppCompatActivity {
    // data members
    private ViewPager viewpagerHeader;
    private TabLayout tabLayoutHeader;
    private Toolbar toolbar;
    private ViewPagerTabAdapter viewPagerAdapter;
    private CompetitorListFragment competitorHistoryListFragment = null;
    private CompetitorListFragment competitorPendingListFragment = null;
    public static boolean mBoolRefreshDone = false;
    TextView tv_RetailerName,tv_RetailerID;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private Bundle bundleExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitor_list);
        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
        }
        if (!Constants.restartApp(CompetitorListActivity.this)) {
            initViews();
        }
    }

    /**
     * @desc initializing views
     */
    private void initViews() {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        viewpagerHeader = (ViewPager) findViewById(R.id.viewpagerHeader);
        tabLayoutHeader = (TabLayout) findViewById(R.id.tabLayoutHeader);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_competitor_list), 0);

        initializeTabLayout();
    }

    /**
     * @desc Initializing tab layout
     */
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
                    if (competitorHistoryListFragment != null && mBoolRefreshDone) {
                        mBoolRefreshDone = false;
                        competitorHistoryListFragment.onRefreshView();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * @param viewpager
     * @desc Set up fragments into adapter
     */
    private void setupViewPager(ViewPager viewpager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        competitorHistoryListFragment = new CompetitorListFragment();
        competitorPendingListFragment = new CompetitorListFragment();
        Bundle bundle1 = getIntent().getExtras();
        bundle1.putString(Constants.comingFrom, Constants.NonDevice);
        bundle1.putString(Constants.SyncType, Constants.CI_PD_sync);
        Bundle bundle2 = getIntent().getExtras();
        bundle2.putString(Constants.comingFrom, Constants.Device);
        bundle2.putString(Constants.SyncType, Constants.CIPostPD_sync);
        competitorHistoryListFragment.setArguments(bundle1);
        competitorPendingListFragment.setArguments(bundle2);
        viewPagerAdapter.addFrag(competitorHistoryListFragment, getString(R.string.lbl_history));
        viewPagerAdapter.addFrag(competitorPendingListFragment, getString(R.string.lbl_so_device_order_list));
        viewpager.setAdapter(viewPagerAdapter);
    }

    public void displaySubTitle(String subTxt) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTxt);
        }
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
}
