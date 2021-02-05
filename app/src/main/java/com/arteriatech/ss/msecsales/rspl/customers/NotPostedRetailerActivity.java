package com.arteriatech.ss.msecsales.rspl.customers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;

import java.util.ArrayList;
import java.util.Set;

public class NotPostedRetailerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<RetailerBean>, NotPostedRetailerViewPresenter {

    SimpleRecyclerViewTypeAdapter<RetailerBean> recyclerViewAdapter = null;
    TextView no_record_found;
    SearchView mSearchView;
    SwipeRefreshLayout swipeRefresh;
    NotPostedRetailerPresenterImpl postedRetailerPresenter;
    ArrayList<String> cpList;
    Toolbar toolbar;
    private RecyclerView rvNotPostedRetailer;
    private LinearLayoutManager layoutManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_posted_retailer);
//        if (getIntent() != null) {
//            cpList = getIntent().getExtras().getStringArrayList(Constants.duplicateCPList);
//        }


        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        rvNotPostedRetailer = findViewById(R.id.rv_not_posted_retailer);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        layoutManager = new LinearLayoutManager(this);
        swipeRefresh.setOnRefreshListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOT_POSTED_RETAILERS, 0);
        Set<String> set = sharedPreferences.getStringSet(Constants.duplicateCPList, null);
        if(set != null && set.size()>0 ) {
            cpList = new ArrayList<>(set);
        }
        initializeRecyclerViewItems(layoutManager);
        initializeObjects(this);
        ConstantsUtils.initActionBarView(this, toolbar, true, "Retailers with error", 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onItemClick(RetailerBean retailerBean, View view, int i) {
        onItemClickIntent(retailerBean);
    }

    @Override
    public int getItemViewType(int i, ArrayList<RetailerBean> arrayList) {
        return arrayList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CustomerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i, final RetailerBean retailerBean, ArrayList<RetailerBean> arrayList) {
        if (viewHolder instanceof CustomerListViewHolder) {
            ((CustomerListViewHolder) viewHolder).tvRetailerName.setText(retailerBean.getRetailerName());
            ((CustomerListViewHolder) viewHolder).tv_retailer_mob_no.setText(retailerBean.getCustomerId());
            ((CustomerListViewHolder) viewHolder).tvGrp3.setText(retailerBean.getGroup3Desc());
            ((CustomerListViewHolder) viewHolder).ivMobileNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(retailerBean.getMobile1())) {
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (retailerBean.getMobile1())));
                        startActivity(dialIntent);
                    }
                }
            });
            String cityVal, state = "";

            cityVal = retailerBean.getCity();
            state = retailerBean.getState();

            String disticVal;

            if (!retailerBean.getDistrictDesc().equalsIgnoreCase("") && !retailerBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerBean.getDistrictDesc() + " " + retailerBean.getPostalCode();
            } else if (!retailerBean.getDistrictDesc().equalsIgnoreCase("") && retailerBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerBean.getDistrictDesc();
            } else if (retailerBean.getDistrictDesc().equalsIgnoreCase("") && !retailerBean.getPostalCode().equalsIgnoreCase("")) {
                disticVal = retailerBean.getDistrictDesc();
            } else {
                disticVal = "";
            }

            String addressVa = "";
            if (retailerBean.getAddress1() != null && !retailerBean.getAddress1().equalsIgnoreCase("")) {
                addressVa = retailerBean.getAddress1();
            }

            if (retailerBean.getAddress2() != null && !retailerBean.getAddress2().equalsIgnoreCase("")) {
                addressVa = addressVa + "," + retailerBean.getAddress2();
            }

            if (retailerBean.getAddress3() != null && !retailerBean.getAddress3().equalsIgnoreCase("")) {
                addressVa = addressVa + "," + retailerBean.getAddress3();
            }
            if (retailerBean.getAddress4() != null && !retailerBean.getAddress4().equalsIgnoreCase("")) {
                addressVa = addressVa + "," + retailerBean.getAddress3();
            }
            if (!cityVal.equalsIgnoreCase("") && !state.equalsIgnoreCase("")) {
                ((CustomerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + state + "\n" + cityVal + "\n" + disticVal));
            } else {
                ((CustomerListViewHolder) viewHolder).tv_address2.setText(this.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + disticVal));
            }

            if (retailerBean.getRetailerName().length() > 0)
                ((CustomerListViewHolder) viewHolder).tvName.setText(String.valueOf(retailerBean.getRetailerName().trim().charAt(0)).toUpperCase());
            ((CustomerListViewHolder) viewHolder).mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CustomerListViewHolder) viewHolder).detailsLayout.getVisibility() == View.VISIBLE) {
                        ((CustomerListViewHolder) viewHolder).detailsLayout.setVisibility(View.GONE);
                    } else {
                        ((CustomerListViewHolder) viewHolder).detailsLayout.setVisibility(View.VISIBLE);
                        if (layoutManager.findLastVisibleItemPosition() == i) {
                            rvNotPostedRetailer.post(new Runnable() {
                                @Override
                                public void run() {
                                    rvNotPostedRetailer.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition());
                                }
                            });
                        }
                    }
                }
            });

            ((CustomerListViewHolder) viewHolder).detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickIntent(retailerBean);
                }
            });

            ((CustomerListViewHolder) viewHolder).tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickIntent(retailerBean);
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOT_POSTED_RETAILERS, 0);
            String error = sharedPreferences.getString(retailerBean.getCustomerId(), "");
            if (!error.isEmpty()) {
                ((CustomerListViewHolder) viewHolder).tvError.setVisibility(View.VISIBLE);
                ((CustomerListViewHolder) viewHolder).tvError.setText(error);
            } else {
                ((CustomerListViewHolder) viewHolder).tvError.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void initializeRecyclerViewItems(LinearLayoutManager linearLayoutManager) {
        rvNotPostedRetailer.setHasFixedSize(true);
        rvNotPostedRetailer.setLayoutManager(layoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(this, R.layout.snippet_customer, this, rvNotPostedRetailer, no_record_found);
        rvNotPostedRetailer.setAdapter(recyclerViewAdapter);
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
    public void initializeObjects(Context context) {
        postedRetailerPresenter = new NotPostedRetailerPresenterImpl(NotPostedRetailerActivity.this, this, this);
        postedRetailerPresenter.getCPList(cpList);
    }

    @Override
    public void displayDuplicateCPList(ArrayList retailerBeans) {
        recyclerViewAdapter.refreshAdapter(retailerBeans);
    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(this, message);
    }

    @Override
    public void reload() {
        postedRetailerPresenter.getCPList(cpList);
    }


    private void onItemClickIntent(RetailerBean customerBean) {
        if (!customerBean.isTitle()) {
            if (ConstantsUtils.isAutomaticTimeZone(NotPostedRetailerActivity.this)) {
                Intent intentRetailerDetails = new Intent(NotPostedRetailerActivity.this, CustomerDetailsActivity.class);
                intentRetailerDetails.putExtra(Constants.RetailerName, customerBean.getRetailerName());
                intentRetailerDetails.putExtra(Constants.PostalCode, customerBean.getPostalCode());
                intentRetailerDetails.putExtra(Constants.CPNo, customerBean.getCustomerId());
                intentRetailerDetails.putExtra(Constants.CPUID, customerBean.getUID());
                intentRetailerDetails.putExtra(Constants.CPGUID32, customerBean.getCpGuidStringFormat());
                intentRetailerDetails.putExtra(Constants.comingFrom, "NotPostedRetailer");
                intentRetailerDetails.putExtra(Constants.Address, customerBean.getAddress1());
                intentRetailerDetails.putExtra("MobileNo", customerBean.getMobile1());
                intentRetailerDetails.putExtra(Constants.NotPostedRetailer, customerBean);
                intentRetailerDetails.putExtra(Constants.CPGUID, customerBean.getCPGUID());
                intentRetailerDetails.putExtra(Constants.OtherRouteGUID, customerBean.getRouteGuid36());
                Constants.VisitNavigationFrom = "";
                startActivity(intentRetailerDetails);
            } else {
                ConstantsUtils.showAutoDateSetDialog(NotPostedRetailerActivity.this);
            }
        }
    }

    @Override
    public void onRefresh() {

        postedRetailerPresenter.onSyncSOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.FLAG) {
            Constants.FLAG = false;
            postedRetailerPresenter.getCPList(cpList);
            swipeRefresh.setOnRefreshListener(this);
        }
    }
}
