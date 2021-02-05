package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.retailerStockEntry.add.RetailerStockVH;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

public class AddSampleDisbursementActivity extends AppCompatActivity implements AdapterInterface<RetailerStockBean>, AddSampleDisbursementView, SwipeRefreshLayout.OnRefreshListener,AddSampleDisbursermentDialogFragment.AddSampleDisbursermentDialogFragmentListener{

    public static final int SD_RESULT_ID = 6778;
    private Toolbar toolbar;
    private TextView no_record_found;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    private TextView textViewNoRecordFound;
    private SearchView mSearchView;
    private ArrayList<RetailerStockBean> materialArrayList, selectedArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> dealerStockBeanArrayList = null;
    private SimpleRecyclerViewAdapter<RetailerStockBean> recyclerViewAdapter;
    private AddSampleDisbursementPresenterImple presenter;
    private String stockOwner = "";
    private String searchHint = "";
    private String mStrCPGUID32 = "";
    private String divisionID = "";
    String parentBrandId,parentBrandName;
    String OrderTypeID;
    private ArrayList<RetailerStockBean> retailerStockBeanPopupArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> filterStockBeanPopupArrayLis = new ArrayList<>();
    private ArrayList<RetailerStockBean> filterTempStockBeanPopupArrayLis = new ArrayList<>();

    private LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sample_disbursement);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);


        swipeRefresh.setOnRefreshListener(this);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sample_disbursement_title), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (!Constants.restartApp(AddSampleDisbursementActivity.this)) {
            Intent intent = getIntent();
            if (intent != null) {
                ArrayList<RetailerStockBean> returnOrderBeanArrayList = (ArrayList<RetailerStockBean>) intent.getSerializableExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                retailerStockBeanPopupArrayList.addAll(returnOrderBeanArrayList);
                filterTempStockBeanPopupArrayLis.addAll(returnOrderBeanArrayList);
                divisionID=intent.getExtras().getString(Constants.DivisionID,"");
            }
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            presenter = new AddSampleDisbursementPresenterImple(AddSampleDisbursementActivity.this, retailerStockBeanPopupArrayList, AddSampleDisbursementActivity.this);
            recyclerViewAdapter = new SimpleRecyclerViewAdapter(this, R.layout.stock_select_item, this, recyclerView, no_record_found);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.refreshAdapter(retailerStockBeanPopupArrayList);


        }
    }

    @Override
    public void onItemClick(RetailerStockBean retailerStockBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new RetailerStockVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final RetailerStockBean retailerStockBean) {
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setText(retailerStockBean.getMaterialDesc() + " (" + retailerStockBean.getMaterialNo() + ")");
        if("0".equalsIgnoreCase(retailerStockBean.getUnrestrictedQty())){
            ((RetailerStockVH) viewHolder).checkBoxMaterial.setEnabled(false);
            ((RetailerStockVH) viewHolder).checkBoxMaterial.setTextColor(getResources().getColor(R.color.dashboard_title_color));

        }else{
            ((RetailerStockVH) viewHolder).checkBoxMaterial.setEnabled(true);
            ((RetailerStockVH) viewHolder).checkBoxMaterial.setTextColor(getResources().getColor(R.color.BLACK));
        }
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedArrayList.contains(retailerStockBean)) {
                        retailerStockBean.setChecked(true);
                        retailerStockBean.setSelected(true);
                        selectedArrayList.add(retailerStockBean);
                    }
                } else {
                    if (selectedArrayList.contains(retailerStockBean)) {
                        retailerStockBean.setChecked(false);
                        retailerStockBean.setSelected(false);
                        selectedArrayList.remove(retailerStockBean);
                    }
                }
            }
        });
        ((RetailerStockVH) viewHolder).checkBoxMaterial.setChecked(retailerStockBean.isChecked());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_sample_disbursement, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search_item);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_sample_disbursement_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.filter(query,filterStockBeanPopupArrayLis);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.filter(newText,filterTempStockBeanPopupArrayLis);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add:
                presenter.sendResultToOtherActivity(selectedArrayList);
                return true;
            case R.id.menu_save:
                return true;
            case R.id.menu_filter:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                AddSampleDisbursermentDialogFragment dbFilterDialogFragment = new AddSampleDisbursermentDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DivisionID,divisionID);
                bundle.putString(Constants.BrandID,parentBrandId);
                bundle.putString(Constants.BrandName,parentBrandName);
                dbFilterDialogFragment.setArguments(bundle);
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        ConstantsUtils.displayLongToast(AddSampleDisbursementActivity.this, msg);
    }
    @Override
    public void displayList(ArrayList<RetailerStockBean> list) {
        recyclerViewAdapter.refreshAdapter(list);
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, AddSampleDisbursementActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
    }
    @Override
    public void sendIds(AddSampleDisbursementModel addSampleDisbursementModel) {
        parentBrandId = addSampleDisbursementModel.getBrandId();
        parentBrandName = addSampleDisbursementModel.getBrandDescription();
        OrderTypeID = addSampleDisbursementModel.getOrderMaterialID();

        bundle = new Bundle();
        bundle.putString(Constants.BrandName, addSampleDisbursementModel.getBrandDescription());
        bundle.putString(Constants.BrandID, addSampleDisbursementModel.getBrandId());
        bundle.putString(Constants.OrderMaterialGroupDesc, addSampleDisbursementModel.getOrderMaterialDesc());
        bundle.putString(Constants.OrderMaterialGroupID, addSampleDisbursementModel.getOrderMaterialID());
        filterStockBeanPopupArrayLis.clear();
        /*if((addSampleDisbursementModel.getBrandId().equalsIgnoreCase(Constants.None))&&(addSampleDisbursementModel.getOrderMaterialID().equalsIgnoreCase(Constants.None)))
        {
            recyclerViewAdapter.refreshAdapter(retailerStockBeanPopupArrayList);
        }
        else  if((addSampleDisbursementModel.getBrandId().equalsIgnoreCase(Constants.None))&&(!addSampleDisbursementModel.getOrderMaterialID().equalsIgnoreCase(Constants.None))){
            for (int i = 0; i < retailerStockBeanPopupArrayList.size(); i++) {
                if (retailerStockBeanPopupArrayList.get(i).getOrderMaterialGroupID().equals(OrderTypeID)) {
                    filterStockBeanPopupArrayLis.add(retailerStockBeanPopupArrayList.get(i));
                }
            }
            recyclerViewAdapter.refreshAdapter(filterStockBeanPopupArrayLis);
        }else if((!addSampleDisbursementModel.getBrandId().equalsIgnoreCase(Constants.None))&&(addSampleDisbursementModel.getOrderMaterialID().equalsIgnoreCase(Constants.None))){
            for (int i = 0; i < retailerStockBeanPopupArrayList.size(); i++) {

                if (retailerStockBeanPopupArrayList.get(i).getBrandId().equals(parentBrandId)) {
                    filterStockBeanPopupArrayLis.add(retailerStockBeanPopupArrayList.get(i));
                }
            }
            recyclerViewAdapter.refreshAdapter(filterStockBeanPopupArrayLis);
        }else{
            for (int i = 0; i < retailerStockBeanPopupArrayList.size(); i++) {
                if (retailerStockBeanPopupArrayList.get(i).getBrandId().equals(parentBrandId) && retailerStockBeanPopupArrayList.get(i).getOrderMaterialGroupID().equals(OrderTypeID)) {
                    filterStockBeanPopupArrayLis.add(retailerStockBeanPopupArrayList.get(i));
                }
            }*/
        filterTempStockBeanPopupArrayLis.clear();
        if ((addSampleDisbursementModel.getBrandId().equalsIgnoreCase(Constants.None))) {
            filterTempStockBeanPopupArrayLis.addAll(retailerStockBeanPopupArrayList);
            recyclerViewAdapter.refreshAdapter(retailerStockBeanPopupArrayList);
        } else if ((!addSampleDisbursementModel.getBrandId().equalsIgnoreCase(Constants.None))) {
            for (int i = 0; i < retailerStockBeanPopupArrayList.size(); i++) {
                if (retailerStockBeanPopupArrayList.get(i).getBrandId().equals(parentBrandId)) {
                    filterStockBeanPopupArrayLis.add(retailerStockBeanPopupArrayList.get(i));
                }
            }
            filterTempStockBeanPopupArrayLis.addAll(filterStockBeanPopupArrayLis);
            recyclerViewAdapter.refreshAdapter(filterStockBeanPopupArrayLis);
        }
        presenter.onFragmentInteraction(null, "", "", addSampleDisbursementModel.getBrandId(), addSampleDisbursementModel.getBrandDescription(), addSampleDisbursementModel.getOrderMaterialID(), addSampleDisbursementModel.getOrderMaterialDesc(), "", "");
    }
}
