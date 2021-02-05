package com.arteriatech.ss.msecsales.rspl.reports.collection.header;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.filter.SOFilterActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterInterface<CollectionHistoryBean>, ICollectionListView {
    private SimpleRecyclerViewAdapter<CollectionHistoryBean> collRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private String customerNumber, customerName, CPGUID, CPUID;
    private TextView textViewNoDataFound;
    private CollectionListPresenter presenter;
    private FlowLayout flowLayout;
    private LinearLayout linearLayoutFlowLayout;
    private String tabStatus = "";
    private int itemResource = 0;
    private String lastRefresh = "";

    public CollectionListFragment() {
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
        textViewNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        linearLayoutFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);

        flowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefresh.setOnRefreshListener(this);
        presenter = new CollectionListPresenter(CPGUID,getActivity(), customerNumber,customerName, this,view);
        initializeRecyclerView();
        presenter.connectToOfflineDB();

    }


    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
            itemResource = R.layout.coll_list_item;
        collRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), itemResource, this, recyclerView, textViewNoDataFound);
        recyclerView.setAdapter(collRecyclerViewAdapter);
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
        dateFilter.setVisible(false);
      /*  if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }*/
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_coll_doc_num_search));

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
       /* collRecyclerViewAdapter.refreshAdapter(salesOrderBeenArrayList);
        swipeRefresh.setRefreshing(false);*/
        presenter.onRefresh();
    }


    @Override
    public void success() {
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.FinancialPostings));
    }

    private void refreshAdapter(ArrayList<CollectionHistoryBean> success) {
        collRecyclerViewAdapter.refreshAdapter(success);
    }

    @Override
    public void error(String message) {

    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getContext(),message);
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
    public void searchResult(ArrayList<CollectionHistoryBean> collHistBean) {
        collRecyclerViewAdapter.refreshAdapter(collHistBean);
    }


    @Override
    public void displayRefreshTime(String refreshTime) {
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        ((CollectionListActivity) getActivity()).setActionBarSubTitle(lastRefresh);
    }

    @Override
    public void openSODetail(CollectionHistoryBean collectionHistoryBean) {
        collectionHistoryBean.setRetID(customerNumber);
        collectionHistoryBean.setRetName(customerName);
        Intent intent = new Intent(getActivity(), CollectionListDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_SO_DETAIL, collectionHistoryBean);
        startActivity(intent);
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
        Intent intent = new Intent(getContext(), SOFilterActivity.class);
        intent.putExtra(SOFilterActivity.EXTRA_SO_STATUS, status);
        intent.putExtra(SOFilterActivity.EXTRA_DELV_STATUS, delvStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if(filterType!=null && !filterType.equalsIgnoreCase("")){
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            }else{
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
            refreshAdapter(new ArrayList<CollectionHistoryBean>());
            presenter.startFilter(requestCode, resultCode, data);

        }
    }
    public void onRefreshView(){
        presenter.connectToOfflineDB();
    }
    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }


    /**
     * RecyclcerView Click listener
     */
    @Override
    public void onItemClick(CollectionHistoryBean collectionHistoryBean, View view, int i) {
        presenter.getDetails(collectionHistoryBean);

    }

    /**
     * recyclerView Resource
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CollectionListViewHolder(view);
    }

    /**
     * recyclerView OnBindViewHolder
     *
     * @param collectionHistoryBean
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CollectionHistoryBean collectionHistoryBean) {

        if(Double.parseDouble(!collectionHistoryBean.getDueDays().equalsIgnoreCase("")?collectionHistoryBean.getDueDays():"0")>1){
            ((CollectionListViewHolder) viewHolder).tvDueDays.setText(collectionHistoryBean.getDueDays() + " " + getString(R.string.days));
        }else{
            ((CollectionListViewHolder) viewHolder).tvDueDays.setText(collectionHistoryBean.getDueDays() + " " + getString(R.string.day));
        }

        ((CollectionListViewHolder) viewHolder).tvDueDays.setTextColor(ContextCompat.getColor(getActivity(), R.color.GREEN));
//       ((CollectionListViewHolder) viewHolder).textViewOrderID.setText(collectionHistoryBean.getFIPDocNo());
       ((CollectionListViewHolder) viewHolder).textViewCollType.setText(collectionHistoryBean.getReferenceTypeDesc());
       ((CollectionListViewHolder) viewHolder).textViewCollNo.setText(collectionHistoryBean.getFIPDocNo());
        ((CollectionListViewHolder) viewHolder).textViewPaymentMode.setText(collectionHistoryBean.getPaymentModeDesc());
        ((CollectionListViewHolder) viewHolder).textViewCollDate.setText(collectionHistoryBean.getFIPDate());
        ((CollectionListViewHolder) viewHolder).textViewCollAmt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(),collectionHistoryBean.getAmount()));
//        ((CollectionListViewHolder) viewHolder).textViewCollAmt.setText(Constants.getCurrencyPattren(collectionHistoryBean.getCurrency(),UtilConstants.removeLeadingZero(collectionHistoryBean.getAmount())));

    }
}
