package com.arteriatech.ss.msecsales.rspl.retailerapproval;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.customers.filter.CustomersFilterActivity;
import com.arteriatech.ss.msecsales.rspl.datefilter.DateFilterFragment;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;


public class RetailerApprovalListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<RetailerBean>, ICustomerViewPresenter {
    // android components
    SwipeRefreshLayout swipeRefresh;
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewTypeAdapter<RetailerBean> recyclerViewAdapter = null;
    LinearLayout linearLayoutFlowLayout;
    SearchView mSearchView;
    // variables
    RetailerApprovalPresenter presenter;
    ArrayList<RetailerBean> retailerArrayList = null;
    String customerNumber = "", visitType = "", beatType = "";
    private FlowLayout flowLayout;
    private String comingFrom = Constants.RetailerList;
    private String salesDistrictId;

    private MaterialDesignSpinner spBeat;
    private TextView tvBeat;
    private String mStrBeatGuid = "";
    private LinearLayoutManager layoutManager = null;
    private String cpGuid = "";
    String CPGUID;
    String CPGUIDString;
    String RetailerName;
    String CPNO;
    String CPUI;
    String InstanceId;
    public static String RetApprovalTotalCount = "";
    public static boolean isRefreshList=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_customer_list);
        initializeUI(this);
    }

    /**
     * Initialing UI
     */
    @Override
    public void initializeUI(Context context) {
//      ConstantsUtils.setProgressColor(this, swipeRefresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        spBeat = (MaterialDesignSpinner) findViewById(R.id.spBeat);
        tvBeat = (TextView) findViewById(R.id.tvBeat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loadData();
        initializeClickListeners();
        layoutManager = new LinearLayoutManager(this);
        initializeRecyclerViewItems(layoutManager);
        initializeObjects(this);

    }

    /**
     * Initialing ClickListeners
     */
    @Override
    public void initializeClickListeners() {
        swipeRefresh.setOnRefreshListener(this);
    }

    /**
     * Initialing Objects
     */
    @Override
    public void initializeObjects(Context context) {
        retailerArrayList = new ArrayList<>();
        presenter = new RetailerApprovalPresenter(this, this, this, visitType, customerNumber, salesDistrictId, cpGuid);
        if (!Constants.restartApp(RetailerApprovalListActivity.this)) {
            presenter.loadAsyncTask("");
        }
        displayRefreshTime(SyncUtils.getCollectionSyncTime(this, Constants.ChannelPartners));

    }

    /**
     * Initialing RecyclerView
     */
    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(this, R.layout.snippet_customer, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void showProgressDialog() {
        try {
            swipeRefresh.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideProgressDialog() {
        try {
            swipeRefresh.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getting data from Offline DB and populating to RecyclerView
     */
    @Override
    public void onRefreshData() {
        retailerArrayList = presenter.retailerArrayList;
        recyclerViewAdapter.refreshAdapter(retailerArrayList);
    }

    /**
     * Sync Customers Online to get Latest data
     */
    @Override
    public void customersListSync() {
        try {
            presenter.loadAsyncTask(mStrBeatGuid);
            displayRefreshTime(SyncUtils.getCollectionSyncTime(this, Constants.ChannelPartners));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Opening Filter Activity
     */
    @Override
    public void openFilter(String filterType, String status, String grStatus) {
        Intent intent = new Intent(this, CustomersFilterActivity.class);
        intent.putExtra(DateFilterFragment.EXTRA_DEFAULT, filterType);
        intent.putExtra(CustomersFilterActivity.EXTRA_INVOICE_STATUS, status);
        intent.putExtra(CustomersFilterActivity.EXTRA_INVOICE_GR_STATUS, grStatus);
        startActivityForResult(intent, ConstantsUtils.ACTIVITY_RESULT_FILTER);
    }

    /**
     * Setting FlowLayout Filter Data
     */
    @Override
    public void setFilterDate(String filterType) {
        try {
            String[] filterTypeArr = filterType.split(", ");
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displaying Last Refresh time and setting to Toolbar
     */
    @Override
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
//        if (!comingFrom.equalsIgnoreCase(Constants.MTPList)) {
//            if (lastRefresh != null)
//                getSupportActionBar().setSubtitle(lastRefresh);
//        }
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(RetailerApprovalListActivity.this, msg);
    }

    @Override
    public void sendSelectedItem(Intent intent) {

    }

    @Override
    public void retailerDetails(RetailerBean retailerBean) {
        if (ConstantsUtils.isAutomaticTimeZone(RetailerApprovalListActivity.this)) {


            Intent intentRetailerDetails = new Intent(RetailerApprovalListActivity.this, CustomerDetailsActivity.class);
            intentRetailerDetails.putExtra(Constants.RetailerApprovalList, retailerBean);
            intentRetailerDetails.putExtra(Constants.comingFrom, Constants.RetailerApprovalList);
            intentRetailerDetails.putExtra(Constants.RetailerName, RetailerName);
                intentRetailerDetails.putExtra(Constants.CPNo, CPNO);
                intentRetailerDetails.putExtra(Constants.CPUID, CPUI);
                intentRetailerDetails.putExtra(Constants.CPGUID32, CPGUIDString);
                intentRetailerDetails.putExtra(Constants.CPGUID, CPGUID);
                intentRetailerDetails.putExtra(Constants.OtherRouteGUID, "");
                intentRetailerDetails.putExtra(Constants.InstanceID, InstanceId);

            Constants.VisitNavigationFrom = "";
            startActivity(intentRetailerDetails);
        }
        else {
                ConstantsUtils.showAutoDateSetDialog(RetailerApprovalListActivity.this);
            }
    }

    @Override
    public void displayBeat(final ArrayList retailerBeans) {

        final ArrayList<RetailerBean> alRouteList = retailerBeans;
        mStrBeatGuid = "";
        try {
            if (retailerBeans.size() == 1) {
                tvBeat.setVisibility(View.VISIBLE);
                spBeat.setVisibility(View.GONE);
                tvBeat.setText(alRouteList.get(0).getRouteDesc());
                mStrBeatGuid = alRouteList.get(0).getRschGuid();
                presenter.loadAsyncTask(mStrBeatGuid);
            } else if (retailerBeans.size() > 1) {
                tvBeat.setVisibility(View.GONE);
                spBeat.setVisibility(View.VISIBLE);
                ArrayAdapter<RetailerBean> adapterShipToList = new ArrayAdapter<RetailerBean>(RetailerApprovalListActivity.this, R.layout.custom_textview,
                        R.id.tvItemValue, retailerBeans) {
                    @Override
                    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                        final View v = super.getDropDownView(position, convertView, parent);
                        ConstantsUtils.selectedView(v, spBeat, position, getContext());
                        return v;
                    }
                };
                adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
                spBeat.setAdapter(adapterShipToList);
                spBeat.showFloatingLabel();
                spBeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mStrBeatGuid = alRouteList.get(position).getRschGuid();
                        presenter.loadAsyncTask(mStrBeatGuid);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                tvBeat.setVisibility(View.GONE);
                spBeat.setVisibility(View.GONE);
                mStrBeatGuid = "";
                presenter.loadAsyncTask(mStrBeatGuid);
            }
        } catch (Exception e) {
            tvBeat.setVisibility(View.GONE);
            spBeat.setVisibility(View.GONE);
            mStrBeatGuid = "";
            presenter.loadAsyncTask(mStrBeatGuid);
            e.printStackTrace();
        }
    }

    /**
     * Getting Search Data
     */
    @Override
    public void searchResult(ArrayList retailerSearchList) {
        try {
            recyclerViewAdapter.refreshAdapter(retailerSearchList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading all UI components and Intent Data
     */
    private void loadData() {
        customerNumber = getIntent().getStringExtra(Constants.CPNo);
        visitType = getIntent().getStringExtra(Constants.VisitType);
        beatType = getIntent().getStringExtra(Constants.BeatType);
        comingFrom = getIntent().getStringExtra(Constants.comingFrom);
        salesDistrictId = getIntent().getStringExtra(Constants.SalesDistrictID);
        cpGuid = getIntent().getStringExtra(Constants.EXTRA_CPGUID);
        String salesDistrictTitle = getIntent().getStringExtra(Constants.EXTRA_TITLE);
        String title = getString(R.string.lbl_retailer_approval);
        ConstantsUtils.initActionBarView(this, toolbar, true, title, 0);
    }

    /**
     * On Swipe Refresh
     */
    @Override
    public void onRefresh() {
        presenter.loadAsyncTask("");
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (isRefreshList) {
            presenter.loadAsyncTask("");
            recyclerView.setVisibility(View.GONE);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefreshList) {
            presenter.loadAsyncTask("");
            recyclerView.setVisibility(View.GONE);
            isRefreshList = false;
        }
    }

    @Override
    public void onItemClick(RetailerBean customerBean, View view, int i) {
        onItemClickIntent(customerBean);
    }

    @Override
    public int getItemViewType(int i, ArrayList<RetailerBean> arrayList) {
        RetailerBean retailerBean = arrayList.get(i);
        if (retailerBean.isTitle()) {
            return 0;
        } else {
            return 1;
        }
    }

    private void onItemClickIntent(RetailerBean customerBean) {
        if (!customerBean.isTitle()) {
            RetailerName= customerBean.getRetailerName();
            CPNO= customerBean.getCPNo();
            CPUI=customerBean.getUID();
            CPGUIDString= customerBean.getCpGuidStringFormat();
            CPGUID=customerBean.getCPGUID();
            InstanceId=customerBean.getInstanceId();
            presenter.getRetailerDetails(customerBean);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        if (i == 1) {
            return new RetailerListViewHolder(view);
        } else {
            View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_header_item, viewGroup, false);
            return new CustomerHeaderVH(viewItem);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i, final RetailerBean customerBean, final ArrayList<RetailerBean> arrayList) {
        if (viewHolder instanceof CustomerHeaderVH) {
            if (mStrBeatGuid.equalsIgnoreCase("")) {
                ((CustomerHeaderVH) viewHolder).tvHeader.setVisibility(View.VISIBLE);
                ((CustomerHeaderVH) viewHolder).tvHeader.setText(customerBean.getRouteDesc());
//                ((CustomerHeaderVH) viewHolder).tvCount.setText("("+customerBean.getRetailerCount()+")");

                int retCount = 0;

                try {
                    retCount = Integer.parseInt(customerBean.getRetailerCount());
                } catch (NumberFormatException e) {
                    retCount = 0;
                    e.printStackTrace();
                }
                if (retCount <= 1) {
                    ((CustomerHeaderVH) viewHolder).tvHeader.setText(getString(R.string.ret_count_with_beat_name, customerBean.getRouteDesc(), customerBean.getRetailerCount()));
                } else {
                    ((CustomerHeaderVH) viewHolder).tvHeader.setText(getString(R.string.rets_count_with_beat_name, customerBean.getRouteDesc(), customerBean.getRetailerCount()));
                }

            } else {
                ((CustomerHeaderVH) viewHolder).tvHeader.setVisibility(View.VISIBLE);
                if (arrayList.size() - 1 <= 1) {
                    ((CustomerHeaderVH) viewHolder).tvHeader.setText(getString(R.string.ret_count, String.valueOf(arrayList.size() - 1)));
                } else {
                    ((CustomerHeaderVH) viewHolder).tvHeader.setText(getString(R.string.rets_count, String.valueOf(arrayList.size() - 1)));
                }
            }
        } else if (viewHolder instanceof RetailerListViewHolder) {
            ((RetailerListViewHolder) viewHolder).tvRetailerName.setText(customerBean.getRetailerName());
            ((RetailerListViewHolder) viewHolder).tv_retailer_mob_no.setText(customerBean.getCustomerId());
            ((RetailerListViewHolder) viewHolder).tvGrp3.setText(customerBean.getGroup3Desc());
            ((RetailerListViewHolder) viewHolder).ivMobileNo.setOnClickListener(new View.OnClickListener() {
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
                ((RetailerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + state + "\n" + cityVal + "\n" + disticVal));
            } else {
                ((RetailerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + disticVal));
            }

            if (customerBean.getRetailerName().length() > 0)
                ((RetailerListViewHolder) viewHolder).tvName.setText(String.valueOf(customerBean.getRetailerName().trim().charAt(0)).toUpperCase());


            ((RetailerListViewHolder) viewHolder).detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickIntent(customerBean);
                }
            });

            ((RetailerListViewHolder) viewHolder).tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickIntent(customerBean);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
     /*   if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {*/
        dateFilter.setVisible(false);
        //}
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_cust_name_search));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                //   presenter.onFilter();
                return true;
            case android.R.id.home:
                if (mSearchView != null) {
                    if (!mSearchView.isIconified()) {
                        mSearchView.setIconified(true);
                    } else {
                        finish();
                    }
                }
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}


