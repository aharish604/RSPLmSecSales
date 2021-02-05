package com.arteriatech.ss.msecsales.rspl.beat;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

/**
 * Created by e10860 on 4/24/2018.
 */

public class OtherBeatListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterInterface<RetailerBean>, IBeatListView {
    boolean isTodayBeat = false;
    private SimpleRecyclerViewAdapter<RetailerBean> beatListRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView textViewNoDataFound;
    private TodayBeatListPresenter presenter;
    private int itemResource = 0;
    private String lastRefresh = "";
    private MaterialDesignSpinner spBeat;
    private TextView tvBeat;
    private int selectedPos = 0;
    private String mCPGUID = "";
    private String mBeatGuid = "";
    private TextView tvHeader;
    private LinearLayout llBeatOpening,llRCount;
    private LinearLayoutManager linearLayoutManager;
    private TextView tv_total_ret_val,tv_Visited_ret_val,tv_non_visited_ret_val,tv_prod_ret_val,tv_non_prod_ret_val,tv_no_order_ret_val,tvLastVistdate;


    public OtherBeatListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExt = getArguments();
        if (bundleExt != null) {
            isTodayBeat = bundleExt.getBoolean(Constants.BeatPlan, false);
            mCPGUID = bundleExt.getString(Constants.CPGUID, "");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_beat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI(view);
    }

    void initializeUI(View view) {
        textViewNoDataFound = (TextView) view.findViewById(R.id.no_record_found);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        tvBeat = (TextView) view.findViewById(R.id.tvBeat);
        spBeat = (MaterialDesignSpinner) view.findViewById(R.id.spBeat);
        tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        tv_total_ret_val = (TextView) view.findViewById(R.id.tv_total_ret_val);
        tv_Visited_ret_val = (TextView) view.findViewById(R.id.tv_Visited_ret_val);
        tv_non_visited_ret_val = (TextView) view.findViewById(R.id.tv_non_visited_ret_val);
        tv_prod_ret_val = (TextView) view.findViewById(R.id.tv_prod_ret_val);
        tv_non_prod_ret_val = (TextView) view.findViewById(R.id.tv_non_prod_ret_val);
        tv_no_order_ret_val = (TextView) view.findViewById(R.id.tv_no_order_ret_val);
        tvLastVistdate = (TextView) view.findViewById(R.id.tvLastVistdate);
        llBeatOpening = (LinearLayout) view.findViewById(R.id.llBeatOpening);
        llRCount = (LinearLayout) view.findViewById(R.id.llRCount);
        swipeRefresh.setOnRefreshListener(this);
        presenter = new TodayBeatListPresenter(getActivity(), this, false, mCPGUID);
        initializeRecyclerView();
        onRefreshView();

    }


    void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        itemResource = R.layout.snippet_beat_retailer;
        beatListRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), itemResource, this, recyclerView, textViewNoDataFound);
        recyclerView.setAdapter(beatListRecyclerViewAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        try {
            menu.clear();
            inflater.inflate(R.menu.menu_search, menu);
            SearchManager searchManager = null;
            SearchableInfo searchInfo = null;
            try {
                if (getActivity()!=null) {
                    searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                }
                searchInfo = null;
                if (getActivity()!=null&&searchManager!=null) {
                    searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
            MenuItem dateFilter = menu.findItem(R.id.filter);
            dateFilter.setVisible(false);
            mSearchView.setSearchableInfo(searchInfo);
            mSearchView.setQueryHint(getString(R.string.lbl_search_by_retailer_name));

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
             try {
            if(presenter==null){
                presenter = new TodayBeatListPresenter(getActivity(), this, false, mCPGUID);
                presenter.onSearch("");
                presenter.getRefreshTime();
            }else {
                presenter.onSearch("");
                presenter.getRefreshTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.RoutePlans));
    }

    private void refreshAdapter(ArrayList<RetailerBean> success) {
        beatListRecyclerViewAdapter.refreshAdapter(success);
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getContext(), message);
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
    public void searchResult(ArrayList<RetailerBean> retailerBean) {
        beatListRecyclerViewAdapter.refreshAdapter(retailerBean);

        if (llRCount.getVisibility() == View.VISIBLE) {
            if (retailerBean.size() <= 1) {
                tvHeader.setText(getString(R.string.ret_count, String.valueOf(retailerBean.size())));
            } else {
                tvHeader.setText(getString(R.string.rets_count, String.valueOf(retailerBean.size())));
            }
            if (retailerBean.size() > 0) {
                tvHeader.setVisibility(View.VISIBLE);
            } else {
                tvHeader.setVisibility(View.GONE);
            }
        }
        if (retailerBean.size() > 0) {
            llBeatOpening.setVisibility(View.VISIBLE);
        } else {
            llBeatOpening.setVisibility(View.GONE);
        }
        /*if(beatOpeningSummaryBean==null)tv_total_ret_val.setText(String.valueOf(retailerBean.size()));*/
    }

    @Override
    public void displayBeatList(final ArrayList<RetailerBean> retailerBeans) {

        try {
            llBeatOpening.setVisibility(View.GONE);
            if (retailerBeans.size() == 1) {
                tvBeat.setVisibility(View.VISIBLE);
                spBeat.setVisibility(View.GONE);
                tvHeader.setVisibility(View.VISIBLE);
                llRCount.setVisibility(View.VISIBLE);
                llBeatOpening.setVisibility(View.VISIBLE);
                tvBeat.setText(retailerBeans.get(0).getRouteDesc());
                try {
                    mBeatGuid = retailerBeans.get(0).getRschGuid();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                presenter.loadBeatList(retailerBeans.get(0));
            } else if (retailerBeans.size() > 1) {
                tvBeat.setVisibility(View.GONE);
                spBeat.setVisibility(View.VISIBLE);
                tvHeader.setVisibility(View.VISIBLE);
                llRCount.setVisibility(View.VISIBLE);
                llBeatOpening.setVisibility(View.VISIBLE);
                ArrayAdapter<RetailerBean> adapterShipToList = new ArrayAdapter<RetailerBean>(getContext(), R.layout.custom_textview,
                        R.id.tvItemValue, retailerBeans) {
                    @Override
                    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                        final View v = super.getDropDownView(position, convertView, parent);
                        ConstantsUtils.selectedView(v, spBeat, position, getContext());
                        return v;
                    }
                };
                adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
                if (retailerBeans.size() > selectedPos) {
                    spBeat.setSelection(selectedPos);
                }
                spBeat.setAdapter(adapterShipToList);
                spBeat.showFloatingLabel();
                spBeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedPos = position;
                        try {
                            mBeatGuid = retailerBeans.get(position).getRschGuid();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        presenter.loadBeatList(retailerBeans.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                tvBeat.setVisibility(View.GONE);
                spBeat.setVisibility(View.GONE);
                tvHeader.setVisibility(View.GONE);
                llRCount.setVisibility(View.GONE);
                llBeatOpening.setVisibility(View.GONE);
                presenter.loadBeatList(null);
            }
        } catch (Exception e) {
            tvBeat.setVisibility(View.GONE);
            spBeat.setVisibility(View.GONE);
            tvHeader.setVisibility(View.GONE);
            llRCount.setVisibility(View.GONE);
            llBeatOpening.setVisibility(View.GONE);
            presenter.loadBeatList(null);
            e.printStackTrace();
        }
    }


    @Override
    public void displayRefreshTime(String refreshTime) {
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (getActivity() instanceof OtherBeatActivity)
            ((OtherBeatActivity) getActivity()).setActionBarSubTitle(lastRefresh);
        else if (getActivity() instanceof BeatListActivity)
            ((BeatListActivity) getActivity()).setActionBarSubTitle(lastRefresh);
    }

    @Override
    public void onRefreshView() {
        try {
            if (ConstantsUtils.isAutomaticTimeZone(getContext())) {
                presenter.getOtherBeatList();
            } else {
                try {
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", getContext().getString(R.string.autodate_change_msg), getContext().getString(R.string.autodate_change_btn), "", false, new com.arteriatech.mutils.interfaces.DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), ConstantsUtils.DATE_SETTINGS_REQUEST_CODE);
                        }
                    });
                } catch (Exception e) {
                    LogManager.writeLogError("Other Beat List Frag onRefreshView() :"+e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            LogManager.writeLogError("Other Beat List Frag onRefreshView() :"+e.toString());
            e.printStackTrace();
        }
    }
    BeatOpeningSummaryBean beatOpeningSummaryBean=null;
    @Override
    public void beatOpeningDetails(BeatOpeningSummaryBean beatOpeningSummaryBean) {
        this.beatOpeningSummaryBean = beatOpeningSummaryBean;
//        llBeatOpening.setVisibility(View.VISIBLE);
        if(beatOpeningSummaryBean == null){
            beatOpeningSummaryBean = new BeatOpeningSummaryBean();
        }
        if(beatOpeningSummaryBean!=null){
            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getNonProdNoOrder())) {
                tv_no_order_ret_val.setText(beatOpeningSummaryBean.getNonProdNoOrder());
            }else {
                tv_no_order_ret_val.setText("0");
            }

            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getProductive())) {
                tv_prod_ret_val.setText(beatOpeningSummaryBean.getProductive());
            }else {
                tv_prod_ret_val.setText("0");
            }

            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getNonProductive())) {
                tv_non_prod_ret_val.setText(beatOpeningSummaryBean.getNonProductive());
            }else {
                tv_non_prod_ret_val.setText("0");
            }

            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getVisitedRetailers())) {
                tv_Visited_ret_val.setText(beatOpeningSummaryBean.getVisitedRetailers());
            }else {
                tv_Visited_ret_val.setText("0");
            }

            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getTotalRetailers())) {
                tv_total_ret_val.setText(beatOpeningSummaryBean.getTotalRetailers());
            }else {
                tv_total_ret_val.setText("0");
            }

            tvLastVistdate.setText("Last Visit : "+beatOpeningSummaryBean.getVisitDate());
            tvLastVistdate.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(beatOpeningSummaryBean.getVisitedRetailers()) && !TextUtils.isEmpty(beatOpeningSummaryBean.getTotalRetailers())){
                int non_visit = 0;
                non_visit = Integer.parseInt(beatOpeningSummaryBean.getTotalRetailers())-Integer.parseInt(beatOpeningSummaryBean.getVisitedRetailers());
                tv_non_visited_ret_val.setText(""+non_visit);
            }else {
                tv_non_visited_ret_val.setText("0");
            }
        }else {
            tvLastVistdate.setVisibility(View.VISIBLE);
            tvLastVistdate.setText("Last Visit : ");
//            llBeatOpening.setVisibility(View.GONE);
        }
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
    public void onItemClick(RetailerBean salesOrderBean, View view, int i) {

//        presenter.getDetails(salesOrderBean.getSSROGUID());

    }

    /**
     * recyclerView Resource
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new BeatPlanListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int postion, final RetailerBean customerBean) {

        double mDouLatVal = customerBean.getLatVal();
        double mDouLongVal = customerBean.getLongVal();
        if (mDouLatVal == 0.0 || mDouLongVal == 0.0) {
            ((BeatPlanListViewHolder) viewHolder).iv_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.RED), PorterDuff.Mode.SRC_IN);
        } else {
            ((BeatPlanListViewHolder) viewHolder).iv_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.GREEN), PorterDuff.Mode.SRC_IN);
        }
        if (customerBean.getVisitStatus().equalsIgnoreCase(Constants.str_01)) {
            ((BeatPlanListViewHolder) viewHolder).cl_visit_status.setBackgroundColor(getResources().getColor(R.color.RED));
        } else if (customerBean.getVisitStatus().equalsIgnoreCase(Constants.str_02)) {
            ((BeatPlanListViewHolder) viewHolder).cl_visit_status.setBackgroundColor(getResources().getColor(R.color.YELLOW));
        } else {
            ((BeatPlanListViewHolder) viewHolder).cl_visit_status.setBackgroundColor(getResources().getColor(R.color.GREEN));
        }
        if (customerBean.getInvoiceAval().equalsIgnoreCase("")) {
            ((BeatPlanListViewHolder) viewHolder).tvRetailerName.setTextColor(getResources().getColor(R.color.RED));
        }else{
            ((BeatPlanListViewHolder) viewHolder).tvRetailerName.setTextColor(getResources().getColor(R.color.BLACK));
        }

        if(customerBean.getZZVisitFlag()!=null && customerBean.getZZVisitFlag().equalsIgnoreCase("X")){
            ((BeatPlanListViewHolder) viewHolder).cl_visith_status.setBackgroundColor(getResources().getColor(R.color.WHITE));
        }else{
            ((BeatPlanListViewHolder) viewHolder).cl_visith_status.setBackgroundColor(getResources().getColor(R.color.RED));

        }

        ((BeatPlanListViewHolder) viewHolder).tvRetailerName.setText(customerBean.getRetailerName());
        ((BeatPlanListViewHolder) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId());
        ((BeatPlanListViewHolder) viewHolder).tvGrp3.setText(customerBean.getGroup3Desc());
        ((BeatPlanListViewHolder) viewHolder).ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(customerBean.getMobileNumber())) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (customerBean.getMobileNumber())));
                    startActivity(dialIntent);
                }
            }
        });
        String cityVal, state = "";

        cityVal = customerBean.getCity();
        state = customerBean.getState();

        String disticVal;

        if (!customerBean.getDistrictDesc().equalsIgnoreCase("") && !customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getDistrictDesc() + " " + customerBean.getPostalCode();
        } else if (!customerBean.getDistrictDesc().equalsIgnoreCase("") && customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getDistrictDesc();
        } else if (customerBean.getDistrictDesc().equalsIgnoreCase("") && !customerBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = customerBean.getDistrictDesc();
        } else {
            disticVal = "";
        }

        String addressVa = "";
        if (customerBean.getAddress1() != null && !customerBean.getAddress1().equalsIgnoreCase("")) {
            addressVa = customerBean.getAddress1();
        }

        if (customerBean.getAddress2() != null && !customerBean.getAddress2().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress2();
        }

        if (customerBean.getAddress3() != null && !customerBean.getAddress3().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress3();
        }
        if (customerBean.getAddress4() != null && !customerBean.getAddress4().equalsIgnoreCase("")) {
            addressVa = addressVa + "," + customerBean.getAddress3();
        }
        if (!cityVal.equalsIgnoreCase("") && !state.equalsIgnoreCase("")) {
            ((BeatPlanListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + state + "\n" + cityVal + "\n" + disticVal));
        } else {
            ((BeatPlanListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + disticVal));
        }

        if (customerBean.getRetailerName().length() > 0)
            ((BeatPlanListViewHolder) viewHolder).tvName.setText(String.valueOf(customerBean.getRetailerName().trim().charAt(0)).toUpperCase());
        ((BeatPlanListViewHolder) viewHolder).mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BeatPlanListViewHolder) viewHolder).detailsLayout.getVisibility() == View.VISIBLE) {
                    ((BeatPlanListViewHolder) viewHolder).detailsLayout.setVisibility(View.GONE);
                } else {
                    ((BeatPlanListViewHolder) viewHolder).detailsLayout.setVisibility(View.VISIBLE);
                    if (linearLayoutManager.findLastVisibleItemPosition() == postion) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition());
                            }
                        });
                    }
                }
            }
        });
        ((BeatPlanListViewHolder) viewHolder).detailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.LAST_SEL_VAL = postion;
                onItemClickIntent(customerBean);
            }
        });
        ((BeatPlanListViewHolder) viewHolder).tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickIntent(customerBean);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateParticularRow();
    }

    private void updateParticularRow() {
        int updateIndex = 3;
//        data.set(updateIndex, newValue);
        beatListRecyclerViewAdapter.notifyItemChanged(Constants.LAST_SEL_VAL);
    }

    private void onItemClickIntent(RetailerBean retBean) {
        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
            Intent intentRetailerDetails = new Intent(getActivity(), CustomerDetailsActivity.class);
            intentRetailerDetails.putExtra(Constants.RetailerName, retBean.getRetailerName());
            intentRetailerDetails.putExtra(Constants.PostalCode, retBean.getPostalCode());
            intentRetailerDetails.putExtra(Constants.CPNo, retBean.getCPNo());
            intentRetailerDetails.putExtra(Constants.CPUID, retBean.getUID());
            intentRetailerDetails.putExtra(Constants.CPGUID32, retBean.getCpGuidStringFormat());
            intentRetailerDetails.putExtra(Constants.BeatGUID, mBeatGuid);
            intentRetailerDetails.putExtra(Constants.ParentId, OfflineManager.getParentID(Constants.CPDMSDivisions + "?$select=" + Constants.ParentID + " &$filter="
                    + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(retBean.getCpGuidStringFormat()).toUpperCase() +"' and "+Constants.RouteGUID+" eq guid'"+mBeatGuid+"'"));
            intentRetailerDetails.putExtra(Constants.Address, retBean.getAddress1());
            intentRetailerDetails.putExtra("MobileNo", retBean.getMobile1());

            intentRetailerDetails.putExtra(Constants.VisitCatID, Constants.OtherBeatVisitCatID);
            if (retBean.getCurrency() != null) {
                intentRetailerDetails.putExtra(Constants.Currency, retBean.getCurrency());
            } else {
                intentRetailerDetails.putExtra(Constants.Currency, "");
            }
            intentRetailerDetails.putExtra(Constants.comingFrom, Constants.AdhocList);
            intentRetailerDetails.putExtra(Constants.CPGUID, retBean.getCPGUID());
            Constants.Route_Plan_No = retBean.getRouteID();
            Constants.Route_Plan_Desc = retBean.getRouteDesc();
            Constants.VisitNavigationFrom = "";
            startActivity(intentRetailerDetails);
        } else {
            ConstantsUtils.showAutoDateSetDialog(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsUtils.DATE_SETTINGS_REQUEST_CODE) {
            onRefreshView();
        }
    }
}
