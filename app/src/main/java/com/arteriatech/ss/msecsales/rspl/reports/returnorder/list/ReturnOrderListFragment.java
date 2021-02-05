package com.arteriatech.ss.msecsales.rspl.reports.returnorder.list;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.SOListBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.filters.ROFilterActivity;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

public class ReturnOrderListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterInterface<SalesOrderBean>, ReturnOrderListView {
    boolean isMaterialEnabled = false;
    private SimpleRecyclerViewAdapter<SalesOrderBean> salesOrderRecyclerViewAdapter;
    private ArrayList<SalesOrderBean> salesOrderBeenArrayList = new ArrayList<>();
    private ArrayList<SalesOrderBean> salesOrderBeenFilterArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private String customerNumber, customerName, CPGUID, CPUID;
    private TextView textViewNoDataFound;
    private ReturnOrderListPresenterImpl presenter;
    private FlowLayout flowLayout;
    private LinearLayout linearLayoutFlowLayout;
    private String tabStatus = "";
    private int itemResource = 0;
    private String lastRefresh = "";
    private String syncType = "";
    private String beatGuid = "";
    private String parentId = "";

    public ReturnOrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExt = getArguments();
        if (bundleExt != null) {
            customerNumber = bundleExt.getString(Constants.CPNo, "");
            customerName = bundleExt.getString(Constants.RetailerName, "");
            CPGUID = bundleExt.getString(Constants.CPGUID, "");
            try {
                syncType = bundleExt.getString(Constants.SyncType, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                beatGuid = bundleExt.getString(Constants.BeatGUID, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                parentId = bundleExt.getString(Constants.ParentId, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            isMaterialEnabled = bundleExt.getBoolean(Constants.isMaterialEnabled, false);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales_order_header_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
    }

    void initializeUI(View view) {
        salesOrderBeenArrayList = new ArrayList<>();
        textViewNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        linearLayoutFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);

        flowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefresh.setOnRefreshListener(this);
        presenter = new ReturnOrderListPresenterImpl(CPGUID, getActivity(), customerNumber, parentId,this, isMaterialEnabled, view,syncType);
        initializeRecyclerView();
        presenter.connectToOfflineDB();

    }


    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (isMaterialEnabled) {
            itemResource = R.layout.so_list_single_item1;
        } else {
            itemResource = R.layout.so_list_item;
        }
        salesOrderRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), itemResource, this, recyclerView, textViewNoDataFound);
        recyclerView.setAdapter(salesOrderRecyclerViewAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_ro_doc_num_search));

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
        presenter.getRefreshTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                presenter.onFilter();
                return true;
            case R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }


    @Override
    public void success() {
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.SSROs));
    }

    private void refreshAdapter(ArrayList<SalesOrderBean> success) {
        salesOrderRecyclerViewAdapter.refreshAdapter(success);
    }

    @Override
    public void error(String message) {

    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getContext(), message);
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
    public void searchResult(ArrayList<SalesOrderBean> salesOrderBeen) {
        salesOrderRecyclerViewAdapter.refreshAdapter(salesOrderBeen);
    }


    @Override
    public void displayRefreshTime(String refreshTime) {
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        ((ReturnOrderListActivity) getActivity()).setActionBarSubTitle(lastRefresh);
    }

    @Override
    public void openRODetail(SOListBean soListBean) {
        Intent intent = new Intent(getActivity(), ReturnOrderDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_SO_DETAIL, soListBean);
        startActivity(intent);
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(getContext(), ROFilterActivity.class);
        intent.putExtra(ROFilterActivity.EXTRA_SO_STATUS, status);
        intent.putExtra(ROFilterActivity.EXTRA_DELV_STATUS, delvStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            refreshAdapter(new ArrayList<SalesOrderBean>());
            presenter.startFilter(requestCode, resultCode, data);

        }
    }

    public void onRefreshView() {
        presenter.connectToOfflineDB();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }


    /**
     * @desc Recycler View Click listener
     */
    @Override
    public void onItemClick(SalesOrderBean salesOrderBean, View view, int i) {
        presenter.getDetails(salesOrderBean.getSSROGUID());

    }

    /**
     * @desc recyclerView Resource
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ReturnOrderHeaderVH(view);
    }

    /**
     * @desc recyclerView OnBindViewHolder
     * @param salesOrderBean
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SalesOrderBean salesOrderBean) {
        ((ReturnOrderHeaderVH) viewHolder).textViewOrderID.setText(salesOrderBean.getOrderNo());
        ((ReturnOrderHeaderVH) viewHolder).textViewOrderDate.setText(salesOrderBean.getOrderDate());
        ((ReturnOrderHeaderVH) viewHolder).textViewSalesOrderValue.setText(UtilConstants.getCurrencyFormat(salesOrderBean.getCurrency(), salesOrderBean.getNetAmount()));
        if (isMaterialEnabled) {
            ((ReturnOrderHeaderVH) viewHolder).textViewMaterialName.setText(salesOrderBean.getMaterialDesc());
            ((ReturnOrderHeaderVH) viewHolder).textViewQuantity.setText(salesOrderBean.getQAQty() + " " + salesOrderBean.getUom());
            ((ReturnOrderHeaderVH) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
            ((ReturnOrderHeaderVH) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
            ((ReturnOrderHeaderVH) viewHolder).textViewSalesOrderValue.setText(UtilConstants.getCurrencyFormat(salesOrderBean.getCurrency(), salesOrderBean.getNetAmount()));
        }

        Drawable delvStatusImg = SOUtils.displayStatusImage(salesOrderBean.getStatusID(), salesOrderBean.getDelvStatus(), getContext());
        if (delvStatusImg != null) {
            ((ReturnOrderHeaderVH) viewHolder).imageViewDeliveryStatus.setImageDrawable(delvStatusImg);
        }
    }
}