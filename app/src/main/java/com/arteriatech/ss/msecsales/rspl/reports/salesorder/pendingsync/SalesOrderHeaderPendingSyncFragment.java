package com.arteriatech.ss.msecsales.rspl.reports.salesorder.pendingsync;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListViewHolder;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderListDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesOrderHeaderPendingSyncFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, AdapterInterface<SalesOrderBean>, ISalesOrderPendingSyncView, ISalesOrderPendingSyncView.SalesOrderResponse<SalesOrderBean> {
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView textViewNoDataFound;
    SimpleRecyclerViewAdapter<SalesOrderBean> salesOrderRecyclerViewAdapter;
    ArrayList<SalesOrderBean> salesOrderBeenArrayList;
    String customerNumber, customerName, CPGUID, CPUID,BeatGUID,mStrParentId;
    SalesOrderHeaderPendingSyncPresenter presenter;
    private boolean isClickable =false;
    public SalesOrderHeaderPendingSyncFragment() {
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
            CPGUID = bundleExt.getString(Constants.CPGUID, "");
            BeatGUID = bundleExt.getString(Constants.BeatGUID, "");
            mStrParentId = bundleExt.getString(Constants.ParentId, "");
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
        presenter = new SalesOrderHeaderPendingSyncPresenter(getActivity(), CPGUID, mStrParentId,this, view);
        presenter.connectToOfflineDB(this);
        initializeRecyclerView();

    }

    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        int itemResource = R.layout.so_list_item;
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
                if (!isClickable) {
                    isClickable=true;
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                        if (!Constants.isPullDownSync&&!Constants.iSAutoSync&&!Constants.isBackGroundSync) {
                            presenter.onSync();
                        }else{
                            UtilConstants.showAlert(getString(R.string.sync_in_progress),getActivity());
                            isClickable=false;
                        }
                    }else{
                        UtilConstants.showAlert(getString(R.string.no_network_conn),getActivity());
                        isClickable=false;
                    }
                }
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
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            if (!Constants.isPullDownSync&&!Constants.iSAutoSync&&!Constants.isBackGroundSync) {
                presenter.onSync();
            }else{
                hideProgressDialog();
                if (Constants.isBackGroundSync){
                    UtilConstants.showAlert(getString(R.string.alert_backgrounf_sync_is_progress),getActivity());
                }else if (Constants.iSAutoSync){
                    UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),getActivity());
                }else{
                    UtilConstants.showAlert(getString(R.string.sync_in_progress),getActivity());
                }
            }
        }else{
            hideProgressDialog();
            UtilConstants.showAlert(getString(R.string.no_network_conn),getActivity());
        }
    }

    @Override
    public void onItemClick(SalesOrderBean salesOrderBean, View view, int i) {

        Intent intent = new Intent(getContext(), SalesOrderListDetailsActivity.class);
        intent.putExtra(Constants.CPGUID, CPGUID);
        intent.putExtra(Constants.CPNo, customerNumber);
        intent.putExtra(Constants.RetailerName, customerName);
        intent.putExtra(Constants.CPUID, CPUID);
//        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.EXTRA_TAB_POS, 0);
        intent.putExtra(Constants.DeviceNo, salesOrderBean.getDeviceNo());
        intent.putExtra(Constants.EXTRA_SSRO_GUID, salesOrderBean.getSSROGUID());
        intent.putExtra(Constants.EXTRA_ORDER_DATE, salesOrderBean.getOrderDate());
        intent.putExtra(Constants.EXTRA_ORDER_IDS, salesOrderBean.getOrderNo());
        intent.putExtra(Constants.EXTRA_ORDER_AMOUNT, salesOrderBean.getTotalAmt());
        intent.putExtra(Constants.EXTRA_ORDER_SATUS, salesOrderBean.getDelvStatus());
        intent.putExtra(Constants.EXTRA_ORDER_CURRENCY, salesOrderBean.getCurrency());
        intent.putExtra(Constants.ORDER_TYPE, salesOrderBean.getOrderType());
        intent.putExtra(Constants.ORDER_TYPE_DESC, "Sales Order");
        intent.putExtra(Constants.SALESAREA, salesOrderBean.getSalesArea());
        intent.putExtra(Constants.SALESAREA_DESC, salesOrderBean.getSalesAreaDesc());
        intent.putExtra(Constants.SOLDTO, salesOrderBean.getSoldTo());
        intent.putExtra(Constants.SOLDTONAME, salesOrderBean.getSoldToName());
        intent.putExtra(Constants.SHIPPINTPOINT, salesOrderBean.getShippingTypeID());
        intent.putExtra(Constants.SHIPPINTPOINTDESC, salesOrderBean.getShippingTypeDesc());
        intent.putExtra(Constants.SHIPTO, salesOrderBean.getShipTo());
        intent.putExtra(Constants.SHIPTONAME, salesOrderBean.getShipToName());
        intent.putExtra(Constants.FORWARDINGAGENT, salesOrderBean.getForwardingAgent());
        intent.putExtra(Constants.FORWARDINGAGENTNAME, salesOrderBean.getForwardingAgentName());
        intent.putExtra(Constants.PLANT, salesOrderBean.getPlant());
        intent.putExtra(Constants.PLANTDESC, salesOrderBean.getPlantDesc());
        intent.putExtra(Constants.INCOTERM1, salesOrderBean.getIncoTerm1());
        intent.putExtra(Constants.INCOTERM1DESC, salesOrderBean.getIncoterm1Desc());
        intent.putExtra(Constants.INCOTERM2, salesOrderBean.getIncoterm2());
        intent.putExtra(Constants.SALESDISTRICT, salesOrderBean.getSalesDistrict());
        intent.putExtra(Constants.SALESDISTRICTDESC, salesOrderBean.getSalesDistrictDesc());
        intent.putExtra(Constants.SplProcessing, salesOrderBean.getSplProcessing());
        intent.putExtra(Constants.SplProcessingDesc, salesOrderBean.getSplProcessingDesc());
        intent.putExtra(Constants.Payterm, salesOrderBean.getPaymentTerm());
        intent.putExtra(Constants.PaytermDesc, salesOrderBean.getPaytermDesc());
        intent.putExtra(Constants.ROUTE, salesOrderBean.getRoute());
        intent.putExtra(Constants.ROUTEDESC, salesOrderBean.getRouteDesc());
        intent.putExtra(Constants.MEANSOFTRANSPORT, salesOrderBean.getMeansOfTranstyp());
        intent.putExtra(Constants.MEANSOFTRANSPORTDESC, salesOrderBean.getMeansOfTranstypDesc());
        intent.putExtra(Constants.STORAGELOC, salesOrderBean.getStorLoc());
        intent.putExtra(Constants.CUSTOMERPO, salesOrderBean.getPONo());
        intent.putExtra(Constants.CUSTOMERPODATE, salesOrderBean.getPODate());
        intent.putExtra(Constants.Remarks, salesOrderBean.getRemarks());

        intent.putExtra(Constants.SalesGroup, salesOrderBean.getRemarks());
        intent.putExtra(Constants.SalesGrpDesc, salesOrderBean.getRemarks());
        intent.putExtra(Constants.SalesOffice, salesOrderBean.getRemarks());
        intent.putExtra(Constants.SalesOffDesc, salesOrderBean.getRemarks());
        intent.putExtra(Constants.DmsDivision, salesOrderBean.getDmsDivision());
        intent.putExtra(Constants.DmsDivisionDesc, salesOrderBean.getDmsDivisionDesc());
        intent.putExtra("SALES_ORDER_BEAN_OBJECT", salesOrderBean);
        startActivity(intent);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SalesOrderHeaderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SalesOrderBean salesOrderBean) {
        try {
            ((SalesOrderHeaderListViewHolder) viewHolder).textViewOrderID.setText(salesOrderBean.getOrderNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (salesOrderBean.getOrderDate() != null)
            try {
                ((SalesOrderHeaderListViewHolder) viewHolder).textViewOrderDate.setText(salesOrderBean.getOrderDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            ((SalesOrderHeaderListViewHolder) viewHolder).textViewSalesOrderValue.setText(UtilConstants.getCurrencyFormat(salesOrderBean.getCurrency(), salesOrderBean.getNetAmount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (TextUtils.isEmpty(salesOrderBean.getStatusID())) {
                Drawable drawable = SOUtils.getSODefaultDrawable(getContext());
                if (drawable != null)
                    ((SalesOrderHeaderListViewHolder) viewHolder).imageViewDeliveryStatus.setImageDrawable(drawable);
            } else {
               /* Drawable delvStatusImg = SOUtils.displayStatusImage(salesOrderBean.getDelvStatus(), getContext());
                if (delvStatusImg != null) {
                    ((CollectionListViewHolder) viewHolder).imageViewDeliveryStatus.setImageDrawable(delvStatusImg);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if (false) {

        } else {
            ((CollectionListViewHolder) viewHolder).textViewMaterialName.setVisibility(View.GONE);
            ((CollectionListViewHolder) viewHolder).textMaiViewQuantity.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void onReloadData() {
        try {
            Constants.isPullDownSync=false;
            isClickable=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (salesOrderBeenArrayList != null) {
            this.salesOrderBeenArrayList.clear();

        }
        presenter.connectToOfflineDB(this);

//        onRefresh();
    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getContext(), message);
    }

    @Override
    public void success(ArrayList<SalesOrderBean> success) {
        this.salesOrderBeenArrayList = success;
        if (salesOrderRecyclerViewAdapter != null) {
            salesOrderRecyclerViewAdapter.refreshAdapter(success);
        }
    }

    @Override
    public void error(String message) {

    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
}
