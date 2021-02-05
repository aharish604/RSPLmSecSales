package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

/**
 * Created by e10860 on 4/20/2018.
 */

public class DealerStockListActivity extends AppCompatActivity implements IDealerStockListViewPresenter, AdapterInterface<DBStockBean>, SwipeRefreshLayout.OnRefreshListener, DBFilterDialogFragment.OnFragmentFilterListener {
    // android components
    RecyclerView recyclerView;
    TextView textViewNoRecordFound;
    Toolbar toolbar;
    SearchView mSearchView;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<DBStockBean> dealerStockBeanArrayList;
    SimpleRecyclerViewAdapter<DBStockBean> recyclerViewAdapter;
    DealerStockListPresenter presenter;
    private String stockType = "";
    private LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_stock);
        initializeUI();
    }

    @Override
    public void initializeUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.db_stocks), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        textViewNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
//        spDistributor = (MaterialDesignSpinner) findViewById(R.id.spDistributor);
//        spCategory = (MaterialDesignSpinner) findViewById(R.id.spCategory);
//        spBrand = (MaterialDesignSpinner) findViewById(R.id.spBrand);
//        spCRSSKU = (MaterialDesignSpinner) findViewById(R.id.spCRSSKU);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);

        ConstantsUtils.setProgressColor(DealerStockListActivity.this, swipeRefresh);
        initializeClickListeners();
        loadIntentData(getIntent());
        initializeRecyclerViewAdapter(new LinearLayoutManager(this));
        initializeObjects();
    }

    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void initializeObjects() {
        try {
            dealerStockBeanArrayList = new ArrayList<>();
            presenter = new DealerStockListPresenter(this, this, this);
            if (!Constants.restartApp(DealerStockListActivity.this)) {
                presenter.loadDistributor();
            }
//            presenter.loadMaterialData();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.dealer_stock_item, this, recyclerView, textViewNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
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
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("all")
    @Override
    public void refreshAdapter(final ArrayList<?> arrayList, String stockType) {
        this.stockType = stockType;
        recyclerViewAdapter.refreshAdapter((ArrayList<DBStockBean>) arrayList);
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle( getString(R.string.po_last_refreshed) + " " +refreshTime);
        }
    }

    @SuppressWarnings("all")
    @Override
    public void loadIntentData(Intent intent) {
//        dealerStockBeanArrayList = (ArrayList<DBStockBean>) intent.getSerializableExtra(Constants.INTENT_EXTRA_DEALER_STOCK_BEAN);

    }

    @Override
    public void searchResult(ArrayList<DBStockBean> searchBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(searchBeanArrayList);
    }

    @Override
    public void brandList(final ArrayList<BrandBean> arrBrand) {
        /*ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(DealerStockListActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, arrBrand[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBrand, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spBrand.setAdapter(adapterShipToList);
        spBrand.showFloatingLabel();
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBrandId = arrBrand[0][position];
                presenter.loadCategory(strDivisionQry, strBrandId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    @Override
    public void divisionList(final ArrayList<DMSDivisionBean> finalDistListDms) {
        /*ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(DealerStockListActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, finalDistListDms) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDistributor, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spDistributor.setAdapter(adapterShipToList);
        spDistributor.showFloatingLabel();
        spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strDivisionQry = finalDistListDms.get(position).getDMSDivisionQuery();
                presenter.loadBrands(strDivisionQry, finalDistListDms.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        if (!finalDistListDms.isEmpty()) {
//            presenter.loadBrands(strDivisionQry, finalDistListDms.get(0));
//            if (finalDistListDms.size()==1){
//                spDistributor.setVisibility(View.GONE);
//            }
            presenter.initialLoad("", finalDistListDms.get(0));
        }
    }

    @Override
    public void categoryList(final String[][] arrCategory) {
        /*ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(DealerStockListActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, arrCategory[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCategory, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spCategory.setAdapter(adapterShipToList);
        spCategory.showFloatingLabel();
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategoryId = arrCategory[0][position];
                presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    @Override
    public void crsSKUList(final ArrayList<SKUGroupBean> arrCrsSku) {
        /*ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(DealerStockListActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, arrCrsSku[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCRSSKU, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spCRSSKU.setAdapter(adapterShipToList);
        spCRSSKU.showFloatingLabel();
        spCRSSKU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                strCategoryId = arrCrsSku[0][position];
                presenter.loadMaterialData(arrCrsSku[0][position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, DealerStockListActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onItemClick(DBStockBean dealerStockBean, View view, int i) {
        presenter.onItemClick(dealerStockBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DealerMaterialStockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final DBStockBean stocksInfoBean) {
        try {
            if (stockType.equalsIgnoreCase(Constants.str_01)) {
                ((DealerMaterialStockViewHolder) viewHolder).textViewSKUGroup.setText(stocksInfoBean.getMaterialDesc());
            } else {
                ((DealerMaterialStockViewHolder) viewHolder).textViewSKUGroup.setText(stocksInfoBean.getOrderMaterialGroupDesc());
            }
            ((DealerMaterialStockViewHolder) viewHolder).textViewDBStock.setText(stocksInfoBean.getQAQty() + " " + stocksInfoBean.getUom());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.search_stock));
        MenuItem dateFilter = menu.findItem(R.id.filter);
        dateFilter.setVisible(true);
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
            case R.id.filter:
                Bundle bundle = presenter.openFilter();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DBFilterDialogFragment dbFilterDialogFragment = new DBFilterDialogFragment();
                dbFilterDialogFragment.setArguments(bundle);
//                dbFilterDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog);
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onFragmentFilterInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        presenter.onFragmentInteraction(dmsDivisionBean, distributor, divisionName, brand, brandName, category, categoryName, creskuGrp, creskuGrpName);
    }
}
