package com.arteriatech.ss.msecsales.rspl.SampleDisbursementList;

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
import androidx.appcompat.widget.Toolbar;
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
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplaintListAdapter;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplaintListModel;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter.InvoiceFilterActivity;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invoiceDetails.InvoiceDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

public class SampleDisbursementListFragment  extends Fragment implements SampleDisbursementListsView,SampleDisbursementListsView.SampleDisbursemwntResponse, SwipeRefreshLayout.OnRefreshListener,AdapterInterface<InvoiceListBean> {
    Context context;
    Toolbar toolbar;
    SampleDisbursementListPresenterImpl sampleDisbursementListPresenter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swiperefreshlayout;
    ComplaintListAdapter adapter;
    String CPGUID="";
    String CPGUID32="";
    String CPUID="";
    String mcpNo="";
    String nRetailerName="";
    String comingFrom;
    Bundle bundleExt;
    TextView no_record_found;
    SimpleRecyclerViewAdapter<InvoiceListBean> recyclerViewAdapter = null;
    private boolean isInvoiceItemsEnabled = true;
    private String lastRefresh = "";
    LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;
    ArrayList<InvoiceListBean>sampledisbursesyncList=new ArrayList<>();
    private String syncType;
    private String parentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleExt = getArguments();
        if (bundleExt != null) {
            mcpNo = bundleExt.getString(Constants.CPNo, "");
            CPGUID = bundleExt.getString(Constants.CPGUID, "");
            CPGUID32 = bundleExt.getString(Constants.CPGUID32, "");
            CPUID = bundleExt.getString(Constants.CPUID, "");
            nRetailerName = bundleExt.getString(Constants.RetailerName, "");
            comingFrom = bundleExt.getString(Constants.comingFrom);
            isInvoiceItemsEnabled = bundleExt.getBoolean(Constants.isInvoiceItemsEnabled);
            syncType = bundleExt.getString(Constants.SyncType);
            parentId = bundleExt.getString(Constants.ParentId);
        }
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_list, container, false);
        context=container.getContext();
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        linearLayoutFlowLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) view.findViewById(R.id.llFlowLayout);
        swiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(context, R.layout.sample_disbursement_list_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        swiperefreshlayout.setOnRefreshListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSampleDisbursementList();
    }

    public void getSampleDisbursementList() {
        sampleDisbursementListPresenter=new SampleDisbursementListPresenterImpl(context, this,isInvoiceItemsEnabled,bundleExt,mcpNo,CPGUID,getActivity(),parentId);
        sampleDisbursementListPresenter.start(this);
    }


    @Override
    public void onItemClick(InvoiceListBean invoiceBean, View view, int i) {
        sampleDisbursementListPresenter.getInvoiceDetails(invoiceBean);

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SampleDisbursementListVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, InvoiceListBean invoiceBean) {
        ((SampleDisbursementListVH) viewHolder).textViewInvoiceDate.setText(invoiceBean.getInvoiceDate());
        ((SampleDisbursementListVH) viewHolder).textViewInvoiceNumber.setText(invoiceBean.getInvoiceNo());
        ((SampleDisbursementListVH) viewHolder).textViewInvoiceAmount.setText(UtilConstants.getCurrencyFormat(invoiceBean.getCurrency(), invoiceBean.getInvoiceAmount()));

        if (isInvoiceItemsEnabled) {
            ((SampleDisbursementListVH) viewHolder).textViewMaterialName.setVisibility(View.VISIBLE);
            ((SampleDisbursementListVH) viewHolder).textViewQuantity.setVisibility(View.VISIBLE);
            ((SampleDisbursementListVH) viewHolder).textViewMaterialName.setText(invoiceBean.getMaterialDesc());
            try {
                ((SampleDisbursementListVH) viewHolder).textViewQuantity.setText(ConstantsUtils.checkNoUOMZero(invoiceBean.getUOM(), invoiceBean.getQuantity()) + " " + invoiceBean.getUOM());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else {
            ((SampleDisbursementListVH) viewHolder).textViewMaterialName.setVisibility(View.GONE);
            ((SampleDisbursementListVH) viewHolder).textViewQuantity.setVisibility(View.GONE);
        }
        Drawable delvStatusImg = SOUtils.displayInvoiceStatusImage(invoiceBean.getInvoiceStatus(), invoiceBean.getDueDateStatus(), context);
        if (delvStatusImg != null) {
            ((SampleDisbursementListVH) viewHolder).imageViewInvoiceStatus.setImageDrawable(delvStatusImg);
        }
    }
    @Override
    public void setSampleDisbursementListDatatoAdapter(ArrayList<InvoiceListBean> reqBeanArrayList) {
        recyclerViewAdapter.refreshAdapter(reqBeanArrayList);
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.SSINVOICES));
    }
    public void loadProgressBar() {
        swiperefreshlayout.setRefreshing(true);
    }
    public void hideProgressBar() {
        swiperefreshlayout.setRefreshing(false);
    }
    @Override
    public void setSearchedSampleDisbursementListDatatoAdapter(ArrayList<ComplaintListModel> listModels) {

    }
    @Override
    public void showMessage(String msg) {

        ConstantsUtils.displayLongToast(getContext(), msg);
    }
    @Override
    public void openfilter(String startDate, String endDate, String filterType, String status, String grStatus) {
        Intent intent = new Intent(context, InvoiceFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(DateFilterFragment.EXTRA_START_DATE, startDate);
        intent.putExtra(DateFilterFragment.EXTRA_END_DATE, endDate);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(InvoiceFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }
    @Override
    public void searchResult(ArrayList<InvoiceListBean> reqBeanArrayList) {
        if(reqBeanArrayList==null)
        {
            no_record_found.setVisibility(View.VISIBLE);
        }
        else {
            recyclerViewAdapter.refreshAdapter(reqBeanArrayList);
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void invoiceDetails(InvoiceListBean invoiceListBean) {
        Intent toInvoiceHisdetails = new Intent(context, InvoiceDetailsActivity.class);
        toInvoiceHisdetails.putExtra(Constants.CPNo, mcpNo);
        toInvoiceHisdetails.putExtra(Constants.RetailerName, nRetailerName);
        toInvoiceHisdetails.putExtra(Constants.CPUID, CPUID);
        toInvoiceHisdetails.putExtra(Constants.CPGUID, CPGUID);
        toInvoiceHisdetails.putExtra(Constants.INVOICE_ITEM, invoiceListBean);
        toInvoiceHisdetails.putExtra(Constants.comingFrom, Constants.SampleDisbursement);
        startActivity(toInvoiceHisdetails);

    }
    @Override
    public void syncSuccess() {
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.SSINVOICES));
    }
    @Override
    public void reloadData() {
        if (sampledisbursesyncList != null) {
            this.sampledisbursesyncList.clear();
        }
        sampleDisbursementListPresenter.connectToOfflineDB(this);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(comingFrom.equalsIgnoreCase(Constants.Device)) {
            inflater.inflate(R.menu.menu_sync, menu);
        }else
            {
            inflater.inflate(R.menu.menu_search, menu);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
            SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
            mSearchView.setSearchableInfo(searchInfo);
            mSearchView.setQueryHint(getString(R.string.lbl_sample_disbursement_search));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    sampleDisbursementListPresenter.onSearch(query);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    sampleDisbursementListPresenter.onSearch(newText);
                    return false;
                }
            });
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                onRefresh();
                return true;
                case R.id.filter:
                    sampleDisbursementListPresenter.onFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onRefresh() {
        if (comingFrom.equalsIgnoreCase(Constants.Device)) {

            sampleDisbursementListPresenter.sync();
        } else {

            sampleDisbursementListPresenter.refresh();

        }
    }

    public void displayRefreshTime(String refreshTime) {
        try {
            if (!TextUtils.isEmpty(refreshTime)) {
                lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
            }
            ((SampleDisbursementListActivity) getActivity()).setActionBarSubTitle(lastRefresh);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sampleDisbursementListPresenter.startFilter(requestCode, resultCode, data);
    }

    @Override
    public void success(ArrayList reqBeanArrayList) {
            recyclerViewAdapter.refreshAdapter(reqBeanArrayList);


    }

    @Override
    public void error(String message) {

    }
}
