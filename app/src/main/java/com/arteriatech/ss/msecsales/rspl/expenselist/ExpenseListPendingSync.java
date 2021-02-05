package com.arteriatech.ss.msecsales.rspl.expenselist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplaintListAdapter;
import com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails.ExpenseListDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;

import java.util.ArrayList;


public class ExpenseListPendingSync extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterInterface<ExpenseListBean>, ExpenseListView, ExpenseListView.ExpenseListResponse {

    Context context;
    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swiperefreshlayout;
    ComplaintListAdapter adapter;
    String CPGUID = "";
    String CPUID = "";
    String mcpNo = "";
    String nRetailerName = "";
    String comingFrom;
    Bundle bundleExt;
    TextView no_record_found;
    SimpleRecyclerViewAdapter<ExpenseListBean> recyclerViewAdapter = null;
    ExpensePendingSyncPresenterImpl expensePendingSyncPresenter;
    private String lastRefresh = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleExt = getArguments();
        if (bundleExt != null) {

//            mcpNo = bundleExt.getString(Constants.CPNo, "");
//            CPGUID = bundleExt.getString(Constants.CPGUID, "");
//            CPUID = bundleExt.getString(Constants.CPUID, "");
//            nRetailerName = bundleExt.getString(Constants.RetailerName, "");
//            comingFrom = bundleExt.getString(Constants.comingFrom);
//            isInvoiceItemsEnabled = bundleExt.getBoolean(Constants.isInvoiceItemsEnabled);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        context = container.getContext();
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        swiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(context, R.layout.expense_list_list_item, this, swiperefreshlayout, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        swiperefreshlayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getExpenseList();

    }

    private void getExpenseList() {
        expensePendingSyncPresenter = new ExpensePendingSyncPresenterImpl(getActivity(), this, getActivity());
        expensePendingSyncPresenter.start(this);

    }

    @Override
    public void onItemClick(ExpenseListBean expenseListBean, View view, int i) {
        getExpenseDetailsList(expenseListBean);
    }

    private void getExpenseDetailsList(ExpenseListBean expenseListBean) {
        Intent intent = new Intent(getActivity(), ExpenseListDetailsActivity.class);
        intent.putExtra(Constants.ExpenseListBean, expenseListBean);
        intent.putExtra(Constants.EXTRA_IS_OFFLINE,true);
        startActivity(intent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ExpenseListVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ExpenseListBean expenseListBean) {
        ((ExpenseListVH) viewHolder).textViewExpenseDate.setText(expenseListBean.getDate());
        ((ExpenseListVH) viewHolder).textViewExpenseNumber.setText(expenseListBean.getExpenseNo());
        ((ExpenseListVH) viewHolder).textViewAmount.setText(UtilConstants.getCurrencyFormat(expenseListBean.getCurrency(), expenseListBean.getAmount()));
        ((ExpenseListVH) viewHolder).textViewExpenseTypeDesc.setText(expenseListBean.getExpenseTypeDesc());

//        if (isInvoiceItemsEnabled) {
//            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
//            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
//            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setText(invoiceBean.getMaterialDesc());
//            try {
//                ((InvoiceListViewHolder) viewHolder).textViewQuantity.setText(ConstantsUtils.checkNoUOMZero(invoiceBean.getUOM(), invoiceBean.getQuantity()) + " " + invoiceBean.getUOM());
//            } catch (OfflineODataStoreException e) {
//                e.printStackTrace();
//            }
//        } else {
//            ((InvoiceListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.GONE);
//            ((InvoiceListViewHolder) viewHolder).textViewQuantity.setVisibility(View.GONE);
//        }
//        Drawable delvStatusImg = SOUtils.displayInvoiceStatusImage(invoiceBean.getInvoiceStatus(), invoiceBean.getDueDateStatus(), context);
//        if (delvStatusImg != null) {
//            ((InvoiceListViewHolder) viewHolder).imageViewInvoiceStatus.setImageDrawable(delvStatusImg);
//        }
    }


    public void loadProgressBar() {
        swiperefreshlayout.setRefreshing(true);
    }

    public void hideProgressBar() {
        swiperefreshlayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {

        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void syncSucces() {
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.Expenses));
    }

    @Override
    public void reloadata() {
        expensePendingSyncPresenter.start(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sync, menu);
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
//        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
//        mSearchView.setSearchableInfo(searchInfo);
//        mSearchView.setQueryHint(getString(R.string.lbl_expense_description_search));
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.menu_sync:
                onRefresh();
                return true;
            case R.id.filter:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        expensePendingSyncPresenter.syncExpenseList();
    }

    public void displayRefreshTime(String refreshTime) {
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        ((ExpenseListActivity) getActivity()).setActionBarSubTitle(lastRefresh);
    }

    @Override
    public void displayExpenseList(ArrayList<ExpenseListBean> expenseListBeans) {
        recyclerViewAdapter.refreshAdapter(expenseListBeans);
    }

    @Override
    public void displayExpenseSearchList(ArrayList<ExpenseListBean> expenseListBeans) {

    }

    @Override
    public void success(ArrayList success) {

    }

    @Override
    public void error(String message) {

    }
}