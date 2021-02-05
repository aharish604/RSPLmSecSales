package com.arteriatech.ss.msecsales.rspl.visitsummaryreport;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;

import java.util.ArrayList;

public class VisitSummaryReportActivity extends AppCompatActivity implements VisitSummaryReportView , AdapterInterface<VisitSummaryReportBean>, SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    VisitSummaryReportPresentImpl summaryPresent = null;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    SwipeRefreshLayout swipeRefresh;
    private SimpleRecyclerViewAdapter<VisitSummaryReportBean> simpleRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary_report);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.llb_visit_sum), 0);
        intit();
    }

    private void intit() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<VisitSummaryReportBean>(this, R.layout.visit_summary_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        summaryPresent = new VisitSummaryReportPresentImpl(this,this);
        summaryPresent.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dialogMessage(String message) {

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
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void displayList(ArrayList<VisitSummaryReportBean> list) {
        try {
            displayRefreshTime(SyncUtils.getCollectionSyncTime(this, Constants.VisitSummarySet));
        } catch (Exception e) {
            e.printStackTrace();
        }
        simpleRecyclerViewAdapter.refreshAdapter(list);
    }

    @Override
    public void onRefresh() {
        summaryPresent.onRefresh();
    }

    @Override
    public void onItemClick(VisitSummaryReportBean attendanceSummaryBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new VisitSummaryReportVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, VisitSummaryReportBean attendanceSummaryBean) {

        ((VisitSummaryReportVH)viewHolder).tv_bp_name.setText(attendanceSummaryBean.getSPName());
        ((VisitSummaryReportVH)viewHolder).tv_total_beat_val.setText(attendanceSummaryBean.getTotalBeat());
        ((VisitSummaryReportVH)viewHolder).tv_total_ret_val.setText(attendanceSummaryBean.getTotalRetailers());
        int visitRetailer=0;
        int totalRetailer=0;

        if(!TextUtils.isEmpty(attendanceSummaryBean.getTotalRetailers())){
            totalRetailer = totalRetailer+Integer.parseInt(attendanceSummaryBean.getTotalRetailers());
        }
        if(!TextUtils.isEmpty(attendanceSummaryBean.getVisitedRetailer())){
            visitRetailer = visitRetailer + Integer.parseInt(attendanceSummaryBean.getVisitedRetailer());
        }

       /* if(!TextUtils.isEmpty(attendanceSummaryBean.getTodayVisitedRetailer())){
            visitRetailer = visitRetailer + Integer.parseInt(attendanceSummaryBean.getTodayVisitedRetailer());
        }

        if(visitRetailer>totalRetailer){
            visitRetailer = totalRetailer;
        }*/
        ((VisitSummaryReportVH)viewHolder).tv_visited_ret_val.setText(""+visitRetailer);
        int visitBeat=0;

        if(!TextUtils.isEmpty(attendanceSummaryBean.getVisitedBeat())){
            visitBeat = visitBeat + Integer.parseInt(attendanceSummaryBean.getVisitedBeat());
        }

        /*if(!TextUtils.isEmpty(attendanceSummaryBean.getTodayVisitedBeat())){
            visitBeat = visitBeat + Integer.parseInt(attendanceSummaryBean.getTodayVisitedBeat());
        }*/
        ((VisitSummaryReportVH)viewHolder).tv_Visited_beat_val.setText(""+visitBeat);
        int notVisitedBeat = 0;
        if(!TextUtils.isEmpty(attendanceSummaryBean.getTotalBeat())){
            notVisitedBeat = Integer.parseInt(attendanceSummaryBean.getTotalBeat()) -visitBeat;
        }else {
            notVisitedBeat = 0;
        }

        int notVisitedRet = 0;
        if(!TextUtils.isEmpty(attendanceSummaryBean.getTotalRetailers())){
            notVisitedRet = Integer.parseInt(attendanceSummaryBean.getTotalRetailers()) -visitRetailer;
        }else {
            notVisitedRet = 0;
        }

        ((VisitSummaryReportVH)viewHolder).tv_non_beat_val.setText(""+notVisitedBeat);
        ((VisitSummaryReportVH)viewHolder).tv_non_visited_ret_val.setText(""+notVisitedRet);
    }
}
