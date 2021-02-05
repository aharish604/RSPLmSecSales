package com.arteriatech.ss.msecsales.rspl.expenselist;

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
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplainListActivity;

public class ExpenseListActivity extends AppCompatActivity {


    ViewPagerTabAdapter viewPagerAdapter;
    ViewPager viewpagerExpenseList;
    TabLayout tabLayoutExpenseListHeader;
    ExpenseListHistoryFragment expenseListHistoryFragment;
    ExpenseListPendingSync expenseListPendingSync;

    public static boolean mBoolRefreshDone=false;
    Toolbar toolbar;
    private String TAG= ComplainListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);
        toolbar=(Toolbar) findViewById(R.id.toolbar);

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_expense_list),0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeView();


    }
    private void initializeView() {
        viewpagerExpenseList =(ViewPager)findViewById(R.id.viewpagerComplaintsHeader);
        tabLayoutExpenseListHeader =(TabLayout)findViewById(R.id.tabLayoutComplintsHeader);
        initializeTabLayout();
    }
    private void initializeTabLayout(){
        setupViewPager(viewpagerExpenseList);
        tabLayoutExpenseListHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutExpenseListHeader.setupWithViewPager(viewpagerExpenseList);
        viewpagerExpenseList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: "+position);
                expenseListHistoryFragment.getExpenseList();

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        expenseListHistoryFragment =new ExpenseListHistoryFragment();
      //  Bundle bundle1 = getIntent().getExtras();
        //bundle1.putString(Constants.comingFrom, Constants.Device);
        //expenseListHistoryFragment.setArguments(bundle1);

        expenseListPendingSync = new ExpenseListPendingSync();
      //  Bundle bundle2 = getIntent().getExtras();
        //bundle2.putString(Constants.comingFrom, Constants.NonDevice);
        //expenseListPendingSync.setArguments(bundle2);

        viewPagerAdapter.addFrag(expenseListHistoryFragment, getString(R.string.lbl_history));
        viewPagerAdapter.addFrag(expenseListPendingSync,getString(R.string.lbl_so_device_order_list));
        viewPager.setAdapter(viewPagerAdapter);
    }
    public void setActionBarSubTitle(String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTitle);
        }
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
