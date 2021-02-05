package com.arteriatech.ss.msecsales.rspl.reports.invoicelist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invoiceDetails.InvoiceDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

/**
 * Created by e10604 on 25/1/2018.
 */

public class InvoiceListActivity extends AppCompatActivity implements IInvoiceListViewPresenter, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<InvoiceListBean> {

    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<InvoiceListBean> recyclerViewAdapter = null;
    LinearLayout linearLayoutFlowLayout;
    // variables
    InvoiceListPresenter presenter;
    private FlowLayout flowLayout;
    private Bundle bundleExtras;
    private String mStrBundleRetID = "", mStrBundleCPGUID = "",mStrBundleCPGUID36="",mStrBeatGuid,mStrParentId;
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private boolean isInvoiceItemsEnabled = true;
    TextView tv_RetailerName,tv_RetailerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list_mvp);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleCPGUID36 = bundleExtras.getString(Constants.CPGUID);
            try {
                mStrBeatGuid = bundleExtras.getString(Constants.BeatGUID,"");
            } catch (Exception e) {
                mStrBeatGuid="";
                e.printStackTrace();
            }
            try {
                mStrParentId = bundleExtras.getString(Constants.ParentId,"");
            } catch (Exception e) {
                mStrBeatGuid="";
                e.printStackTrace();
            }
            isInvoiceItemsEnabled = bundleExtras.getBoolean(Constants.isInvoiceItemsEnabled);
        }
        if (!Constants.restartApp(InvoiceListActivity.this)) {
            initializeUI(this);
        }
    }

    @Override
    public void initializeUI(Context context) {
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_invoice_History), 0);
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
        presenter = new InvoiceListPresenter(this, this, this, mStrBundleRetID, mStrBundleCPGUID,mStrBundleCPGUID36,mStrParentId);
        if (!Constants.restartApp(InvoiceListActivity.this)) {
            invoiceListFresh();
        }
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.recycler_view_invoice_list, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
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
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText);
                return false;
            }
        });
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
    public void openFilter(String startDate, String endDate, String filterType, String status, String grStatus) {
        Intent intent = new Intent(this, InvoiceFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void searchResult(ArrayList<InvoiceListBean> reqBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(reqBeanArrayList);
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if (filterType != null && !filterType.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invoiceDetails(InvoiceListBean invoiceListBean) {
        Intent toInvoiceHisdetails = new Intent(this, InvoiceDetailsActivity.class);
        toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
        toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
        toInvoiceHisdetails.putExtra(Constants.CPUID, mStrBundleRetUID);
        toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        toInvoiceHisdetails.putExtra(Constants.INVOICE_ITEM, invoiceListBean);
        toInvoiceHisdetails.putExtra(Constants.comingFrom, Constants.InvoiceList);
        startActivity(toInvoiceHisdetails);
    }

    @Override
    public void invoiceListFresh() {
        try {
            if (isInvoiceItemsEnabled) {
                presenter.getInvoiceItemsList();
            } else {
                presenter.getInvoiceList();
            }
            displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.SSINVOICES));
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
        if (lastRefresh != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void onRefresh() {
            presenter.onRefresh();
    }

    @Override
    public void onItemClick(InvoiceListBean reqBean, View view, int i) {
        presenter.getInvoiceDetails(reqBean);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new InvoiceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, InvoiceListBean invoiceBean) {
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceDate.setText(invoiceBean.getInvoiceDate());
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceNumber.setText(invoiceBean.getInvoiceNo());
        ((InvoiceListViewHolder) viewHolder).textViewInvoiceAmount.setText(UtilConstants.getCurrencyFormat(invoiceBean.getCurrency(), invoiceBean.getInvoiceAmount()));

        if (isInvoiceItemsEnabled) {
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setText(invoiceBean.getMaterialDesc());
            try {
                ((InvoiceListViewHolder) viewHolder).textViewQuantity.setText(ConstantsUtils.checkNoUOMZero(invoiceBean.getUOM(), invoiceBean.getQuantity()) + " " + invoiceBean.getUOM());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else {
            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.GONE);
            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.GONE);
        }
        Drawable delvStatusImg = SOUtils.displayInvoiceStatusImage(invoiceBean.getInvoiceStatus(), invoiceBean.getDueDateStatus(), this);
        if (delvStatusImg != null) {
            ((InvoiceListViewHolder) viewHolder).imageViewInvoiceStatus.setImageDrawable(delvStatusImg);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.startFilter(requestCode, resultCode, data);
    }
}
