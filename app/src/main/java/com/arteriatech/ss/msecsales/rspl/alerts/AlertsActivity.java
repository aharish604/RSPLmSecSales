package com.arteriatech.ss.msecsales.rspl.alerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory.AlertHistoryFragment;
import com.arteriatech.ss.msecsales.rspl.alerts.alertsList.AlertConfirmFragment;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.home.MainActivity;


/**
 * Created by e10860 on 3/29/2018.
 */

public class AlertsActivity extends AppCompatActivity {

    ViewPagerTabAdapter adapter;
    Toolbar toolbar = null;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ActionBar supportActionBar = null;
    private AlertConfirmFragment confirmFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.alerts), 0);
        if (!Constants.restartApp(AlertsActivity.this)) {
            setTab();
            setAlertsCountZero();
        }
    }


    private void setTab() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager() {
        adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        confirmFragment = new AlertConfirmFragment();
        adapter.addFrag(confirmFragment, getString(R.string.alert_list));
        AlertHistoryFragment historyFragment = new AlertHistoryFragment();
        adapter.addFrag(historyFragment, getString(R.string.alerts_history));
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                if (currentFragment instanceof AlertHistoryFragment) {
                    ConstantsUtils.initActionBarView(AlertsActivity.this, toolbar, true, getString(R.string.alerts), 0);
//                    confirmFragment.clearSelections();
//                    confirmFragment.hideMenu();
                    ((AlertHistoryFragment) currentFragment).callPresenter();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            /*case android.R.id.home:
                finish();
                break;*/
        }
        return false;
    }

    @Override
    public void onBackPressed() {
       /* if(confirmFragment.getSelectedItemCount()>0){
            confirmFragment.clearSelections();
            confirmFragment.hideMenu();
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.alerts), 0);
        }else{*/
//            MainActivity.isRefresh = true;
//            MainActivity.fromAlerts= true;
        Intent intent = new Intent(AlertsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        }

    }

    //Return current fragment on basis of Position
    public Fragment getFragment(int pos) {
        return adapter.getItem(pos);
    }

    public void setActionBarTitle(String title) {
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
    }

    private void setAlertsCountZero() {
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.BirthdayAlertsCount, 0);
        editor.putInt(Constants.TextAlertsCount, 0);
        editor.putInt(Constants.AppointmentAlertsCount, 0);
        editor.commit();
    }


}
