package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;


public class RetailerStkReviewActivity extends AppCompatActivity implements RetailerStockCrtView, AdapterInterface<RetailerStockBean>, SearchFilterInterface, View.OnClickListener, RetailerStkCrtQtyTxtWtchrInterface {

    LinearLayout linearLayoutFlowLayout;
    private RetailerStockCrtPresenterImpl presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<RetailerStockBean> simpleRecyclerViewAdapter;
    private boolean isSessionRequired = false;
    private Toolbar toolbar;
    private SearchView mSearchView = null;
    private boolean isKeyBoardOpen = false;
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "", mStrCPGUID = "", beatGUID = "", parentID = "";
    private ArrayList<RetailerStockBean> alDealerStockList = new ArrayList<>();
    private String searchHint = "";
    private TextView tvRetailerName, tvRetailerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            alDealerStockList = (ArrayList<RetailerStockBean>) bundle.getSerializable(Constants.EXTRA_STOCK_BEAN);
            mstrCustomerNo = bundle.getString(Constants.CPNo);
            mStrCustomerName = bundle.getString(Constants.RetailerName);
            mStrUID = bundle.getString(Constants.CPUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
            searchHint = bundle.getString(Constants.EXTRA_SEARCH_HINT);
            mStrCPGUID = bundle.getString(Constants.CPGUID);
            beatGUID = bundle.getString(Constants.BeatGUID);
            parentID = bundle.getString(Constants.ParentID);
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.retailer_stock_entry_title), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.retailer_stock_sub_review));

        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        // set retailer name
        tvRetailerName.setText(mStrCustomerName);
        // set retailer id
        tvRetailerID.setText(mstrCustomerNo);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<RetailerStockBean>(RetailerStkReviewActivity.this, R.layout.retailer_stock_review_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, RetailerStkReviewActivity.this)) {
                    isKeyBoardOpen = true;
                } else {
                    isKeyBoardOpen = false;
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });
        presenter = new RetailerStockCrtPresenterImpl(RetailerStkReviewActivity.this, this, mStrComingFrom, isSessionRequired, alDealerStockList, mstrCustomerNo, true, mStrCustomerName, mStrCPGUID32, mStrCPGUID);
        presenter.onStart();
    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(RetailerStkReviewActivity.this);
        }
    }


    @Override
    public void displayList(ArrayList<RetailerStockBean> reviewStockList, String searchHint) {
        refreshAdapter(reviewStockList);
    }

    @Override
    public void displaySearchList(ArrayList<RetailerStockBean> retailerStkList) {
        refreshAdapter(retailerStkList);
    }

    private void refreshAdapter(ArrayList<RetailerStockBean> retailerStkList) {
        simpleRecyclerViewAdapter.refreshAdapter(retailerStkList);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(RetailerStkReviewActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayShortToast(RetailerStkReviewActivity.this, message);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {

    }


    @Override
    public void onItemClick(RetailerStockBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new StockMaterialReviewVH(viewItem, new RetailerStockCrtQtyTxtWtchr());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final RetailerStockBean retailerStkBean) {
        ((StockMaterialReviewVH) holder).tvMatDesc.setText(retailerStkBean.getOrderMaterialGroupDesc());
        ((StockMaterialReviewVH) holder).tvLandingPrice.setText(retailerStkBean.getEnterdQty() + " " + retailerStkBean.getEnterdUOM());

    }

    @Override
    public void onTextChange(String charSequence, RetailerStockBean retailerStkBean, RecyclerView.ViewHolder holder) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retailer_stock, menu);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.add);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(searchHint);
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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                UtilConstants.dialogBoxWithCallBack(RetailerStkReviewActivity.this, "", getString(R.string.retailer_stock_save_conformation_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (b) {
                            presenter.saveItem(recyclerView, alDealerStockList);
                        }
                    }
                });
//                presenter.saveItem(recyclerView, alDealerStockList);
                return true;
            case R.id.filter:
                presenter.onFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean applyConditionToAdd(Object o) {
        if (mSearchView != null)
            return presenter.onSearch(mSearchView.getQuery().toString(), o);
        else
            return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if (filterType != null && !filterType.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateUpdateSuccess() {
        Intent intent = new Intent(RetailerStkReviewActivity.this, CustomerDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CPNo, mstrCustomerNo);
        intent.putExtra(Constants.RetailerName, mStrCustomerName);
        intent.putExtra(Constants.CPUID, mStrUID);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.BeatGUID, beatGUID);
        intent.putExtra(Constants.ParentId, parentID);
        startActivity(intent);
    }
}
