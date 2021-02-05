package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.WindowDisplayActivity;

import java.util.ArrayList;

public class WindowDisplayListActivity extends AppCompatActivity implements WindowDisplayListView, AdapterInterface<SchemeBean>, SwipeRefreshLayout.OnRefreshListener{

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelWinDispType = "";
    private String mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    private String mStrComingFrom = "";
    private String beatGUID = "";
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView no_record_found;
    private TextView textInfo;
    private SimpleRecyclerViewAdapter<SchemeBean> recyclerViewAdapter;
    Toolbar toolbar;
    WindowDisplayListPresenter windowDisplayListPresenter;
    private String[][] arrWinDispType = null;
    private int numberOfDays = 0;
    private String[][] mArrayDistributors = null;
    Context context=this;
    SchemeBean schemeBean=null;
    private KeyboardView keyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_display_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_window_display_list), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
       }
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        textInfo = (TextView)findViewById(R.id.textInfo);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<SchemeBean>(this, R.layout.window_list_items, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        windowDisplayListPresenter=new WindowDisplayListPresenterImpl(this,this,mStrBundleCPGUID,mStrBundleCPGUID32);
        windowDisplayListPresenter.getWindowDispType();
        windowDisplayListPresenter.getDataFromOfflineDB();
    }
    @Override
    public void displayList(ArrayList<SchemeBean> schemeModelArrayList) {
        if (schemeModelArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            no_record_found.setVisibility(View.VISIBLE);
        }
        else
        recyclerViewAdapter.refreshAdapter(schemeModelArrayList);
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
        ConstantsUtils.displayLongToast(WindowDisplayListActivity.this,msg );
    }
    @Override
    public void navToWindowDisplay(boolean status,String values) {
        if (status && schemeBean != null) {
            Intent sampleCollection = new Intent(WindowDisplayListActivity.this, WindowDisplayActivity.class);
            sampleCollection.putExtra(Constants.CPNo, mStrBundleRetID);
            sampleCollection.putExtra(Constants.CPUID, mStrBundleRetailerUID);
            sampleCollection.putExtra(Constants.RetailerName, mStrBundleRetName);
            sampleCollection.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
            sampleCollection.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
            sampleCollection.putExtra(Constants.comingFrom, mStrComingFrom);
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_GUID, schemeBean.getSchemeGUID());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_NAME, schemeBean.getSchemeDesc());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_IS_SECONDTIME, schemeBean.isSecondTime());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_TYPE_ID, schemeBean.getSchemeTypeID());
            sampleCollection.putExtra(Constants.EXTRA_INVOICE_DATE, values);
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_ID, schemeBean.getSchemeID());
            startActivity(sampleCollection);

        } else {
            Constants.dialogBoxWithButton(WindowDisplayListActivity.this, "", getString(R.string.window_display_not_valid), getString(R.string.ok), "", null);
        }
    }
    @Override
    public void onItemClick(SchemeBean schemeBean, View view, int i) {

        windowDisplayListPresenter.validateWindowDisplay(schemeBean.getSchemeGUID(),schemeBean);
        this.schemeBean=schemeBean;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new WindowDisplayVH(view);

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i, SchemeBean schemeBean) {

        ((WindowDisplayVH) holder).tvSchemeName.setText(schemeBean.getSchemeDesc());
        ((WindowDisplayVH) holder).schem_id.setText(schemeBean.getSchemeID());
        if (schemeBean.isSecondTime())
            ((WindowDisplayVH) holder).llStatusColor.setBackgroundColor(context.getResources().getColor(R.color.YELLOW));
        else
            ((WindowDisplayVH) holder).llStatusColor.setBackgroundColor(context.getResources().getColor(R.color.RED));

    }
    @Override
    public void onRefresh() {
        windowDisplayListPresenter.refreshList();
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



}
