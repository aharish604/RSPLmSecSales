package com.arteriatech.ss.msecsales.rspl.retailertrends;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.MyPerformanceBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RetailerTrends extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,AdapterInterface<MyPerformanceBean>, RetailerTrendView{

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView no_record_found;
    private Toolbar toolbar;
    private RetailerTrendsPresenterImpl presenter;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private SimpleRecyclerViewAdapter<MyPerformanceBean> recyclerViewAdapter;
    private String m1Header = "";
    private String m2Header = "";
    private String m3Header = "";
    TextView tv_RetailerName,tv_RetailerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_trends);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }


        if (!Constants.restartApp(RetailerTrends.this)) {
            initializeUI(this);
        }
    }
    public void initializeUI(Context context) {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_trends), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.MONTH, -1);
        m1Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        mCalendar.add(Calendar.MONTH, -1);
        m2Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        mCalendar.add(Calendar.MONTH, -1);
        m3Header = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<MyPerformanceBean>(this, R.layout.trends_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        presenter = new RetailerTrendsPresenterImpl(RetailerTrends.this,RetailerTrends.this, mStrBundleCPGUID);
        presenter.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        initScroll();
    }

    @Override
    public void onRefresh() {
         presenter.refresh();

    }

    @Override
    public void showProgress() {

        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);

    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(RetailerTrends.this,msg );

    }

    @Override
    public void displayList(ArrayList<MyPerformanceBean> alRetTrends) {
        recyclerViewAdapter.refreshAdapter(alRetTrends);
        // displayRetTrendsValues(alRetTrends);
    }

    @Override
    public void displayLstSyncTime(String lastSeenDateFormat) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.po_last_refreshed) + " " + lastSeenDateFormat);
        }
    }

    @Override
    public void onItemClick(MyPerformanceBean myPerformanceBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new RetailerTrendVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyPerformanceBean myPerformanceBean) {
        ((RetailerTrendVH) viewHolder).tvM1Header.setText(m3Header);
        ((RetailerTrendVH) viewHolder).tvM2Header.setText(m2Header);
        ((RetailerTrendVH) viewHolder).tvM3Header.setText(m1Header);
        ((RetailerTrendVH) viewHolder).tvM1Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth1PrevPerf()));
        ((RetailerTrendVH) viewHolder).tvM2Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth2PrevPerf()));
        ((RetailerTrendVH) viewHolder).tvM3Value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMonth3PrevPerf()));
        ((RetailerTrendVH) viewHolder).tvLsttreMValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAvgLstThreeMonth()));
        ((RetailerTrendVH) viewHolder).tvLysmaValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtLMTD()));
        ((RetailerTrendVH) viewHolder).tvCMTargValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getCMTarget()));
        ((RetailerTrendVH) viewHolder).tvCMTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAmtMTD()));
        ((RetailerTrendVH) viewHolder).tvBTDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getBalToDo()));
        ((RetailerTrendVH) viewHolder).tvACHDValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getAchivedPer()));
        ((RetailerTrendVH) viewHolder).tvLYGrthValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(myPerformanceBean.getGrPer()));
       ((RetailerTrendVH) viewHolder).tvTrendsSKUGrp.setText(getString(R.string.po_details_display_value, myPerformanceBean.getMaterialGroupDesc(), myPerformanceBean.getUOM()));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_distributor_trend, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_info:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                RetailerTrendDialogFragment dbFilterDialogFragment = new RetailerTrendDialogFragment();
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
