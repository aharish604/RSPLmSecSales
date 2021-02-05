package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;
import com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingFilter.OutstandingFilterActivity;
import com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingDetails.OutstandingDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

/**
 * Created by e10604 on 25/1/2018.
 *
 */

public class OutstandingListActivity extends AppCompatActivity implements OutStndingInvoiceListView,SwipeRefreshLayout.OnRefreshListener,AdapterInterface<OutstandingBean> {

    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<OutstandingBean> recyclerViewAdapter=null;
    private FlowLayout flowLayout;
    LinearLayout linearLayoutFlowLayout;

    // variables
    ArrayList<OutstandingBean> invoiceBeanArrayList =null;
    ArrayList<OutstandingBean>invoiceBeenFilterArrayList =new ArrayList<>();
    ArrayList<OutstandingBean> invoiceBeanArrayListTemp =new ArrayList<>();
    OutstndingListPresenter presenter;
    private Bundle bundleExtras;
    private String mStrBundleRetID = "",mStrBundleCPGUID="",mStrBundleCPGUID36="",mStrBundleParentID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private boolean isInvoiceItemsEnabled=true;
    OutstandingBean invoiceListBean;
    TextView tv_RetailerName,tv_RetailerID,textViewOutAmt,tvNetAmount;
    private String outStandingStatus = "",outStandingStatusName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_list_mvp);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleCPGUID36 = bundleExtras.getString(Constants.CPGUID);
            mStrBundleParentID = bundleExtras.getString(Constants.ParentId);
            isInvoiceItemsEnabled = bundleExtras.getBoolean(Constants.isInvoiceItemsEnabled);
        }

        initializeUI(this);
    }

    @Override
    public void initializeUI(Context context) {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        textViewOutAmt = (TextView) findViewById(R.id.textViewOutAmt);
        tvNetAmount = (TextView) findViewById(R.id.tvNetAmount);
        swipeRefresh =(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        recyclerView =(RecyclerView) findViewById(R.id.recyclerView);
        no_record_found =(TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_outstanding),0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeClickListeners();
        initializeObjects(this);
        initializeRecyclerViewItems(new LinearLayoutManager(this));

    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void initializeObjects(Context context) {
        invoiceBeanArrayList = new ArrayList<>();
        presenter= new OutstndingListPresenter(this,this,this,mStrBundleRetID,mStrBundleCPGUID,mStrBundleCPGUID36,mStrBundleParentID);
        if (isInvoiceItemsEnabled){
            invoiceBeanArrayList =  presenter.getInvoiceItemsList();
        }else{
            invoiceBeanArrayList =  presenter.getInvoiceList();
        }
        invoiceBeanArrayListTemp.clear();
        invoiceBeanArrayListTemp.addAll(invoiceBeanArrayList);
        presenter.calTotalAmount(invoiceBeanArrayList);
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.SSOutstandingInvoices));
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_view_out_list,this,recyclerView,no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(invoiceBeanArrayList);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_inv_no_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query,invoiceBeanArrayListTemp);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText,invoiceBeanArrayListTemp);
                return false;
            }
        });
        presenter.onSearch("",invoiceBeanArrayListTemp);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                presenter.onFilter();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getApplicationContext(), message);
    }

    @Override
    public void dialogMessage(String message, String msgType) {

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
    public void openFilter(String startDate, String endDate, String filterType,String status,String grStatus) {
        Intent intent = new Intent(this, OutstandingFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(OutstandingFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(OutstandingFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void searchResult(ArrayList<OutstandingBean> reqBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(reqBeanArrayList);
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
    public void invoiceDetails(ArrayList<OutstandingBean> alItemList) {
        this.invoiceListBean.setAlItemList(alItemList);
        Intent toInvoiceHisdetails = new Intent(this, OutstandingDetailsActivity.class);
        toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
        toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
        toInvoiceHisdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
        toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        toInvoiceHisdetails.putExtra(Constants.INVOICE_ITEM, invoiceListBean);
        startActivity(toInvoiceHisdetails);
    }

    @Override
    public void invoiceListFresh() {
        try {
            invoiceBeanArrayList.clear();
            invoiceBeenFilterArrayList.clear();
            if (isInvoiceItemsEnabled){
                invoiceBeanArrayList = presenter.getInvoiceItemsList();
            }else{
                invoiceBeanArrayList = presenter.getInvoiceList();
            }
            invoiceBeanArrayListTemp.clear();
            invoiceBeanArrayListTemp.addAll(invoiceBeanArrayList);
            if (!TextUtils.isEmpty(outStandingStatusName)&&!outStandingStatusName.equalsIgnoreCase(Constants.ALL)) {

            switch (outStandingStatus) {
                case "01": {
                    for (OutstandingBean item : invoiceBeanArrayList) {
                        if (ConstantsUtils.getBillAge(item) >= 0 && ConstantsUtils.getBillAge(item) <= 30)
                            invoiceBeenFilterArrayList.add(item);
                    }
                }
                break;

                case "02": {
                    for (OutstandingBean item : invoiceBeanArrayList) {
                        if (ConstantsUtils.getBillAge(item) > 30 && ConstantsUtils.getBillAge(item) <= 60)
                            invoiceBeenFilterArrayList.add(item);
                    }
                }
                break;

                case "03": {
                    for (OutstandingBean item : invoiceBeanArrayList) {
                        if (ConstantsUtils.getBillAge(item) > 60 && ConstantsUtils.getBillAge(item) <= 90)
                            invoiceBeenFilterArrayList.add(item);
                    }
                }
                break;

                case "04": {
                    for (OutstandingBean item : invoiceBeanArrayList) {
                        if (ConstantsUtils.getBillAge(item) > 90)
                            invoiceBeenFilterArrayList.add(item);
                    }
                }
                break;
            }
            presenter.calTotalAmount(invoiceBeenFilterArrayList);
            recyclerViewAdapter.refreshAdapter(invoiceBeenFilterArrayList);
            linearLayoutFlowLayout.setVisibility(View.VISIBLE);
        }else{
                presenter.calTotalAmount(invoiceBeanArrayList);
                displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.SSOutstandingInvoices));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }



    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh!=null)
        getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void displayTotalValue(String totalOutValue,String totalNetValue,String currency) {
        textViewOutAmt.setText(UtilConstants.getCurrencyFormat(currency,totalOutValue));
        tvNetAmount.setText(UtilConstants.getCurrencyFormat(currency,totalNetValue));
    }

    @Override
    public void onRefresh() {
        try {
            presenter.onRefresh();
        } catch (Throwable e) {
            e.printStackTrace();
        }
      //  recyclerViewAdapter.refreshAdapter(invoiceBeanArrayList);
    }

    @Override
    public void onItemClick(OutstandingBean reqBean, View view, int i) {
        this.invoiceListBean = reqBean;
        presenter.getInvoiceDetails(reqBean.getInvoiceGuid());

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new OutstandingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, OutstandingBean invoiceBean) {
        ((OutstandingListViewHolder) viewHolder).textViewInvoiceAmount.setText(UtilConstants.getCurrencyFormat(invoiceBean.getCurrency(),invoiceBean.getInvoiceAmount()));
        ((OutstandingListViewHolder) viewHolder).textViewInvoiceNumber.setText(invoiceBean.getInvoiceNo());
        ((OutstandingListViewHolder) viewHolder).textViewInvoiceDate.setText(invoiceBean.getInvoiceDate());
        //((OutstandingListViewHolder) viewHolder).textViewOutAmt.setText(UtilConstants.getCurrencyFormat(invoiceBean.getCurrency(),invoiceBean.getInvoiceBalanceAmount()));

        if (isInvoiceItemsEnabled){
            ((OutstandingListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
            ((OutstandingListViewHolder) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
            ((OutstandingListViewHolder) viewHolder).textViewMaterialName.setText(invoiceBean.getMaterialDesc());
            try {
                ((OutstandingListViewHolder) viewHolder).textViewQuantity.setText(ConstantsUtils.checkNoUOMZero(invoiceBean.getUOM(),invoiceBean.getQuantity())+ " " + invoiceBean.getUOM());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }else{
            ((OutstandingListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.GONE);
            ((OutstandingListViewHolder) viewHolder).textViewQuantity.setVisibility(View.GONE);
          }

        if(Double.parseDouble(!invoiceBean.getDueDays().equalsIgnoreCase("")?invoiceBean.getDueDays():"0")>1){
            ((OutstandingListViewHolder) viewHolder).tvDueDays.setText(invoiceBean.getDueDays() + " " + getString(R.string.days));
        }else{
            ((OutstandingListViewHolder) viewHolder).tvDueDays.setText(invoiceBean.getDueDays() + " " + getString(R.string.day));
        }
        ((OutstandingListViewHolder) viewHolder).tvDueDays.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.RED));

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            String filterType = data.getStringExtra(DateFilterFragment.EXTRA_DEFAULT);
            String startDate = data.getStringExtra(DateFilterFragment.EXTRA_START_DATE);
            String endDate = data.getStringExtra(DateFilterFragment.EXTRA_END_DATE);
            String soStatus = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_STATUS);
            String statusName = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_STATUS_NAME);
            String delvStatus = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_GR_STATUS);
            String delvStatusName = data.getStringExtra(OutstandingFilterActivity.EXTRA_INVOICE_GR_STATUS_NAME);
            invoiceBeenFilterArrayList.clear();
            invoiceBeanArrayListTemp.clear();
            outStandingStatus = soStatus;
            outStandingStatusName = statusName;
            if (!statusName.equalsIgnoreCase(Constants.ALL)) {

                switch (soStatus) {
                    case "01": {
                        for (OutstandingBean item : invoiceBeanArrayList) {
                            if (ConstantsUtils.getBillAge(item) >= 0 && ConstantsUtils.getBillAge(item) <= 30)
                                invoiceBeenFilterArrayList.add(item);
                        }
                    }
                    break;

                    case "02": {
                        for (OutstandingBean item : invoiceBeanArrayList) {
                            if (ConstantsUtils.getBillAge(item) > 30 && ConstantsUtils.getBillAge(item) <= 60)
                                invoiceBeenFilterArrayList.add(item);
                        }
                    }
                    break;

                    case "03": {
                        for (OutstandingBean item : invoiceBeanArrayList) {
                            if (ConstantsUtils.getBillAge(item) > 60 && ConstantsUtils.getBillAge(item) <= 90)
                                invoiceBeenFilterArrayList.add(item);
                        }
                    }
                    break;

                    case "04": {
                        for (OutstandingBean item : invoiceBeanArrayList) {
                            if (ConstantsUtils.getBillAge(item) > 90)
                                invoiceBeenFilterArrayList.add(item);
                        }
                    }
                    break;
                }
                invoiceBeanArrayListTemp.addAll(invoiceBeenFilterArrayList);
                presenter.calTotalAmount(invoiceBeenFilterArrayList);
                recyclerViewAdapter.refreshAdapter(invoiceBeenFilterArrayList);
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }else{
                outStandingStatus = "";
                outStandingStatusName = "";
                invoiceBeanArrayListTemp.addAll(invoiceBeanArrayList);
                presenter.calTotalAmount(invoiceBeanArrayList);
                linearLayoutFlowLayout.setVisibility(View.GONE);
                recyclerViewAdapter.refreshAdapter(invoiceBeanArrayList);
            }

            presenter.startFilter(requestCode, resultCode, data);

        }
    }


}
