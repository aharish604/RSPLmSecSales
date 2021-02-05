package com.arteriatech.ss.msecsales.rspl.reports.collection.pendingcollection;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;
import com.arteriatech.ss.msecsales.rspl.reports.collection.header.CollectionListDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.reports.collection.header.CollectionListViewHolder;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionPendingSyncFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, AdapterInterface<CollectionHistoryBean>, ICollectionPendingSyncView, ICollectionPendingSyncView.SalesOrderResponse<CollectionHistoryBean> {
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView textViewNoDataFound;
    SimpleRecyclerViewAdapter<CollectionHistoryBean> salesOrderRecyclerViewAdapter;
    ArrayList<CollectionHistoryBean> salesOrderBeenArrayList;
    String customerNumber, customerName, CPGUID, CPUID;
    CollectionPendingSyncPresenter presenter;

    public CollectionPendingSyncFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_order_header_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundleExt = getArguments();
        if (bundleExt != null) {
            customerNumber = bundleExt.getString(Constants.CPNo, "");
            customerName = bundleExt.getString(Constants.RetailerName, "");
            CPGUID = bundleExt.getString(Constants.CPGUID, "");
        }
        setHasOptionsMenu(true);
        initializeUI(view);
    }

    void initializeUI(View view) {
        salesOrderBeenArrayList = new ArrayList<>();
        textViewNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefresh.setOnRefreshListener(this);
        presenter = new CollectionPendingSyncPresenter(getActivity(), CPGUID, this);
        presenter.connectToOfflineDB(this);
        initializeRecyclerView();

    }

    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        int itemResource = R.layout.coll_list_item;
        salesOrderRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), itemResource, this, recyclerView, textViewNoDataFound);
        recyclerView.setAdapter(salesOrderRecyclerViewAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_sync_back, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                presenter.onSync();
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
        presenter.onSync();
    }

    @Override
    public void openCollDetail(CollectionHistoryBean collectionHistoryBean) {
        collectionHistoryBean.setRetID(customerNumber);
        collectionHistoryBean.setRetName(customerName);
        Intent intent = new Intent(getActivity(), CollectionListDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_SO_DETAIL, collectionHistoryBean);
        startActivity(intent);
    }
    @Override
    public void onItemClick(CollectionHistoryBean collectionHistoryBean, View view, int i) {
        presenter.getDetails(collectionHistoryBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CollectionListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CollectionHistoryBean collectionHistoryBean) {

        if(Double.parseDouble(!collectionHistoryBean.getDueDays().equalsIgnoreCase("")?collectionHistoryBean.getDueDays():"0")>1){
            ((CollectionListViewHolder) viewHolder).tvDueDays.setText(collectionHistoryBean.getDueDays() + " " + getString(R.string.days));
        }else{
            ((CollectionListViewHolder) viewHolder).tvDueDays.setText(collectionHistoryBean.getDueDays() + " " + getString(R.string.day));
        }

        ((CollectionListViewHolder) viewHolder).tvDueDays.setTextColor(ContextCompat.getColor(getActivity(), R.color.GREEN));
        ((CollectionListViewHolder) viewHolder).textViewCollType.setText(collectionHistoryBean.getReferenceTypeDesc());
        ((CollectionListViewHolder) viewHolder).textViewCollNo.setText(collectionHistoryBean.getFIPDocNo());
        ((CollectionListViewHolder) viewHolder).textViewPaymentMode.setText(collectionHistoryBean.getPaymentModeDesc());
        ((CollectionListViewHolder) viewHolder).textViewCollDate.setText(collectionHistoryBean.getFIPDate());
        ((CollectionListViewHolder) viewHolder).textViewCollAmt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(),collectionHistoryBean.getAmount()));
    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void onReloadData() {
        presenter.connectToOfflineDB(this);
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showMessaage(String message) {
        ConstantsUtils.displayLongToast(getContext(),message);
    }

    @Override
    public void success(ArrayList<CollectionHistoryBean> success) {
        this.salesOrderBeenArrayList = success;
        if (salesOrderRecyclerViewAdapter != null) {
            salesOrderRecyclerViewAdapter.refreshAdapter(success);
        }
    }

    @Override
    public void error(String message) {
        ConstantsUtils.displayLongToast(getContext(),message);
    }

    @Override
    public void onDestroyView() {
         presenter.onDestroy();
        super.onDestroyView();
    }


}
