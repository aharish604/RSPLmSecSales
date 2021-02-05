package com.arteriatech.ss.msecsales.rspl.retailerStockEntry.add;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.retailerStockEntry.RetailerStkCrtActivity;

import java.util.ArrayList;



public class RetailerAddStockActivity extends AppCompatActivity implements RetailerAddStockViewPresenter, AdapterInterface<RetailerStockBean> {

    private RecyclerView recyclerView;
    private TextView textViewNoRecordFound;
    private Toolbar toolbar;
    private SearchView mSearchView;
    private ArrayList<RetailerStockBean> materialArrayList, selectedArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> dealerStockBeanArrayList = null;
    private SimpleRecyclerViewAdapter<RetailerStockBean> recyclerViewAdapter;
    private RetailerAddStockPresenter presenter;
    private String stockOwner = "";
    private String searchHint = "";
    private String mStrCPGUID32 = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_stock);
        initializeUI();
    }

    @Override
    public void initializeUI() {
        LinearLayout llincludelayout = (LinearLayout) findViewById(R.id.llincludelayout);
        llincludelayout.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.retailer_stock_entry_title), 0);
        Intent intent = getIntent();
        if (intent != null) {
            dealerStockBeanArrayList = (ArrayList<RetailerStockBean>) intent.getSerializableExtra(Constants.EXTRA_STOCK_BEAN);
            stockOwner = intent.getStringExtra(Constants.EXTRA_STOCK_OWNER);
            searchHint = intent.getStringExtra(Constants.EXTRA_SEARCH_HINT);
            mStrCPGUID32 = intent.getStringExtra(Constants.CPGUID32);
        }
        String subTitle = getString(R.string.retailer_crs_sku_sel);
        if (searchHint.equalsIgnoreCase(getString(R.string.lbl_Search_by_skugroupdesc))) {
            subTitle = getString(R.string.retailer_sku_sel);
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(subTitle);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        textViewNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        initializeClickListeners();
        initializeRecyclerViewAdapter(new LinearLayoutManager(this));
        initializeObjects();
    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects() {
        try {
            materialArrayList = new ArrayList<>();
            presenter = new RetailerAddStockPresenter(this, this, this, dealerStockBeanArrayList, stockOwner, mStrCPGUID32);
            presenter.loadMaterialData();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.stock_select_item, this, recyclerView, textViewNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showMessage(String message, int status) {
        ConstantsUtils.displayLongToast(RetailerAddStockActivity.this, message);
    }

    @SuppressWarnings("all")
    @Override
    public void refreshAdapter(ArrayList<?> arrayList) {
        materialArrayList = (ArrayList<RetailerStockBean>) arrayList;
        recyclerViewAdapter.refreshAdapter((ArrayList<RetailerStockBean>) materialArrayList);
    }

    @SuppressWarnings("all")
    @Override
    public void loadIntentData(Intent intent) {

    }

    @Override
    public void searchResult(ArrayList<RetailerStockBean> searchBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(searchBeanArrayList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(RetailerStockBean dealerStockBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new RetailerStockVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final RetailerStockBean stocksInfoBean) {
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setText(stocksInfoBean.getOrderMaterialGroupDesc() + " (" + stocksInfoBean.getOrderMaterialGroupID() + ")");
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedArrayList.contains(stocksInfoBean)) {
                        stocksInfoBean.setChecked(true);
                        selectedArrayList.add(stocksInfoBean);
                    }
                } else {
                    if (selectedArrayList.contains(stocksInfoBean)) {
                        stocksInfoBean.setChecked(false);
                        selectedArrayList.remove(stocksInfoBean);
                    }
                }
            }
        });
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setChecked(stocksInfoBean.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stock_add, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
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
            case R.id.apply:
                if (!selectedArrayList.isEmpty()) {
                    setResult(RetailerStkCrtActivity.INTENT_RESULT_STOCK_CREATE, new Intent().putExtra(Constants.EXTRA_STOCK_BEAN, selectedArrayList));
                    finish();
                } else {
                    Toast.makeText(this, "please select sku", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
            case R.id.menu_search_item:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
