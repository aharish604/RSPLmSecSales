package com.arteriatech.ss.msecsales.rspl.targets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
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
import com.arteriatech.ss.msecsales.rspl.mbo.MyTargetsBean;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;
import com.arteriatech.ss.msecsales.rspl.ui.TextProgressBar;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by e10526 on 03-02-2017.
 *
 */
public class TargetsActivity extends AppCompatActivity implements TargetViewPresenter,TargetViewPresenter.TargetResponse,SwipeRefreshLayout.OnRefreshListener,AdapterInterface<MyTargetsBean> {


    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found, tvremaingdays,tv_days_percentage;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<MyTargetsBean> recyclerViewAdapter=null;
    LinearLayout llFlowLayout;
    private FlowLayout flowLayout;
    // variables
    ArrayList<MyTargetsBean> mapTargetVal =null;
    TargetsPresenterImpl presenter;
    ArrayList<MyTargetsBean> customerBeanBeenFilterArrayList;
    private TextProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targets);
        initializeUI(this);
    }


    private void getNoOfdays(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        int remaingDays = numDays - day;

        double mDoubPer = 0;
        try {
            mDoubPer = Double.parseDouble(day+"")/Double.parseDouble(numDays+"")*100;
        } catch (Exception e) {
            mDoubPer=0.0;
            e.printStackTrace();
        }
        String percentageValue = UtilConstants.removeDecimalPoints(mDoubPer+"");
        tvremaingdays.setText(remaingDays+" "+getString(R.string.lbl_days));
        tv_days_percentage.setText(percentageValue+" %");
        progressBar.setProgress(Integer.parseInt(percentageValue));
        progressBar.setText(percentageValue+" %");

    }
    @Override
    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout)findViewById(R.id.llFilterLayout);
        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh_targets);
        tvremaingdays =(TextView)findViewById(R.id.tv_remaing_days);
        tv_days_percentage =(TextView)findViewById(R.id.tv_days_percentage);
        recyclerView =(RecyclerView) findViewById(R.id.recycler_view_targets);
        no_record_found =(TextView) findViewById(R.id.no_record_found);
        progressBar =(TextProgressBar)findViewById(R.id.progressBar);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_mytargets),0);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.Targets));
        getNoOfdays();
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
        mapTargetVal = new ArrayList<>();
        customerBeanBeenFilterArrayList = new ArrayList<>();
        presenter= new TargetsPresenterImpl(this,this,this);
        if (!Constants.restartApp(TargetsActivity.this)) {
            presenter.onStart();
        }
//        mapTargetVal = presenter.getTargetsFromOfflineDB();
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_targets_list_item,this,recyclerView,no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        if(mapTargetVal!=null && mapTargetVal.size()>0) {
            recyclerViewAdapter.refreshAdapter(mapTargetVal);
        }
    }


    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(TargetsActivity.this, message);
    }

    @Override
    public void dialogMessage(String message, String msgType) {

    }
    /**
     *  Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        try {
            String lastRefresh = "";
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }
            if (lastRefresh!=null)
                getSupportActionBar().setSubtitle(lastRefresh);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    @Override
    public void success(ArrayList success) {
        this.mapTargetVal =success;
        recyclerViewAdapter.refreshAdapter(success);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void error(String message) {
        swipeRefresh.setRefreshing(false);

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
    public void searchResult(ArrayList<MyTargetsBean> customerBeanArrayList) {
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
    public void onItemClick(MyTargetsBean customerBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new TargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MyTargetsBean customerBean) {
        ((TargetViewHolder)viewHolder).tv_kpi_name.setText(customerBean.getKPIName());
        ((TargetViewHolder)viewHolder).tv_target_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMonthTarget()));
        ((TargetViewHolder)viewHolder).tv_achieved_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getMTDA()));
        ((TargetViewHolder)viewHolder).tv_bal_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(customerBean.getBTD()));

//        displayPieChart(customerBean.getAchivedPercentage(),((TargetViewHolder)viewHolder).pieChart_target);

        Constants.displayPieChart(customerBean.getAchivedPercentage(), ((TargetViewHolder)viewHolder).pieChart_target,
                TargetsActivity.this, 8,
                UtilConstants.trimQtyDecimalPlace(customerBean.getAchivedPercentage()) + "%");
    }


    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
    }

    @Override
    public void TargetSync() {
        presenter.onStart();
    }
    @Override
    public void displayList(ArrayList<MyTargetsBean> alTargets) {
        refreshAdapter(alTargets);
    }


    private void refreshAdapter(ArrayList<MyTargetsBean> alTargets) {
        if(alTargets!=null && alTargets.size()>0) {
            recyclerViewAdapter.refreshAdapter(alTargets);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
