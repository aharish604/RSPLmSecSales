package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeListBean;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

/**
 * Created by e10526 on 25/7/2018.
 *
 */

public class SchemeListActivity extends AppCompatActivity implements ISchemeListViewPresenter, SwipeRefreshLayout.OnRefreshListener {

    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    LinearLayout linearLayoutFlowLayout;
    // variables
    SchemeListPresenter presenter;
    private FlowLayout flowLayout;
    private ArrayList<SchemeListBean> schemeListBeanArrayList = new ArrayList<>();
    private SchemeListAdapter schemeListAdapter;
    private String schemeIds = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            schemeIds = bundle.getString(Constants.EXTRA_SCHEME_GUID, "");
        }
        initializeUI(this);
    }

    @Override
    public void initializeUI(Context context) {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(this, swipeRefresh);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_schemes), 0);
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
        presenter = new SchemeListPresenter(this, this, this,  schemeIds);
        if (!Constants.restartApp(SchemeListActivity.this)) {
            schemeListFresh();
        }
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        schemeListAdapter = new SchemeListAdapter(SchemeListActivity.this, schemeListBeanArrayList);
        recyclerView.setAdapter(schemeListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_scheme, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }
        dateFilter.setVisible(false);
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_scheme_search));
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
          /*  case R.id.menu_expand:
                for (SchemeListBean schemeListBean :schemeListBeanArrayList){
                    schemeListBean.setExpand(true);
                }
                try {
                    schemeListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_collapse:
                for (SchemeListBean schemeListBean :schemeListBeanArrayList){
                    schemeListBean.setExpand(false);
                }
                try {
                    schemeListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;*/
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
    public void searchResult(ArrayList<SchemeListBean> reqBeanArrayList) {
        schemeListBeanArrayList.clear();
        schemeListBeanArrayList.addAll(reqBeanArrayList);
        try {
            schemeListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schemeListBeanArrayList.isEmpty()) {
            no_record_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            no_record_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
    public void schemeListFresh() {
        try {
                presenter.getSchemeList();
            displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.Schemes));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.startFilter(requestCode, resultCode, data);
    }
}
