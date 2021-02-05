package com.arteriatech.ss.msecsales.rspl.visitsummary;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.VisitSummaryBean;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;


/**
 * Created by e10526 on 03-02-2017.
 */
public class VisitSummaryActivity extends AppCompatActivity implements VisitSummaryViewPresenter, VisitSummaryViewPresenter.TargetResponse, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<VisitSummaryBean> {


    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found,tv_visit_start_time,tv_visit_end_time;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<VisitSummaryBean> recyclerViewAdapter = null;
    LinearLayout llFlowLayout;
    VisitSummaryPresenterImpl presenter;
    private FlowLayout flowLayout;
    SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);
        initializeUI(this);
    }


    @Override
    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_targets);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_targets);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        tv_visit_start_time = (TextView) findViewById(R.id.tv_visit_start_time);
        tv_visit_end_time = (TextView) findViewById(R.id.tv_visit_end_time);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_visit_summary), 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeClickListeners();
        initializeRecyclerViewItems(new LinearLayoutManager(this));
        initializeObjects(this);
    }


    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    public void initializeObjects(Context context) {
        presenter = new VisitSummaryPresenterImpl(this, this, this);
        if (!Constants.restartApp(VisitSummaryActivity.this)) {
            presenter.onStart();
        }
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.visit_summary_line_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(VisitSummaryActivity.this, message);
    }

    @Override
    public void dialogMessage(String message, String msgType) {

    }

    /**
     * Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }
            if (lastRefresh != null)
                getSupportActionBar().setSubtitle(lastRefresh);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void success(ArrayList success) {
    }

    @Override
    public void error(String message) {

    }


    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void searchResult(ArrayList<VisitSummaryBean> customerBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(customerBeanArrayList);
    }


    @Override
    public void onRefresh() {
        try {
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(VisitSummaryBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new VisitSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, VisitSummaryBean customerBean) {
        ((VisitSummaryViewHolder) viewHolder).tvRetName.setText(customerBean.getRetailerName());
        ((VisitSummaryViewHolder) viewHolder).tv_time_taken_value.setText(customerBean.getTimeTaken());
        ((VisitSummaryViewHolder) viewHolder).tv_order_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getOrderValue()));
        ((VisitSummaryViewHolder) viewHolder).tv_day_target_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getDayTarget()));
        ((VisitSummaryViewHolder) viewHolder).tv_today_tlsd.setText(customerBean.getTodayTlsd());
        ((VisitSummaryViewHolder) viewHolder).tv_tlsd_till_date_val.setText(customerBean.getTlsdTilldate());
        ((VisitSummaryViewHolder) viewHolder).tv_month_tgt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMonthTarget()));
        ((VisitSummaryViewHolder) viewHolder).tv_ach_mtd.setText(customerBean.getAchMTD());
        ((VisitSummaryViewHolder) viewHolder).tv_mtd_per.setText(customerBean.getMTDPer());

    }


    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
    }

    @Override
    public void TargetSync() {
        presenter.onStart();
    }

    @Override
    public void displayList(ArrayList<VisitSummaryBean> alTargets,String mStrVisitStartTime,String mStrVisitEndTime) {
        tv_visit_end_time.setText(Constants.get12HoursFromat(mStrVisitEndTime).toUpperCase());
        tv_visit_start_time.setText(Constants.get12HoursFromat(mStrVisitStartTime).toUpperCase());
        refreshAdapter(alTargets);
    }


    private void refreshAdapter(ArrayList<VisitSummaryBean> alTargets) {
        recyclerViewAdapter.refreshAdapter(alTargets);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_info, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_cust_name_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText);
                return false;
            }
        });
        presenter.onSearch("");
        return true;
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
                VisitSummaryInfoFragment dbFilterDialogFragment = new VisitSummaryInfoFragment();
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestoy();
        super.onDestroy();
    }
}
