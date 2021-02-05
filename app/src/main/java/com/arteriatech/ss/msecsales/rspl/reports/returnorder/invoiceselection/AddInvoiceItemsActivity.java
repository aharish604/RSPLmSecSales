package com.arteriatech.ss.msecsales.rspl.reports.returnorder.invoiceselection;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.create.ROFilterDialogFragment;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class AddInvoiceItemsActivity extends AppCompatActivity implements AddInvoiceItemCreateView, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterInterface<ReturnOrderBean>, ROFilterDialogFragment.OnFragmentFilterListener {
    // data members
    public final static int RETURN_ORDER_RESULT_ID = 867;
    private Context mContext = this;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private AddInvoiceItemCreatePresenterImpl presenter;
    private SimpleRecyclerViewAdapter<ReturnOrderBean> simpleRecyclerViewAdapter;
    private Toolbar mToolbar;
    private ArrayList<ReturnOrderBean> roCreateBean = null;
    private String stockOwner;
    private LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;
    MaterialDesignSpinner spDMSDivision;
    // data members
    private String mStrBundleRetID = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "";
    private ROCreateBean rocreatebean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ro);


        // fetching data sent by other component through bundle
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
        }
        // adding details from bundle to competitors bean class
        rocreatebean = new ROCreateBean();
        rocreatebean.setCPGUID(mStrBundleCPGUID);
        rocreatebean.setCPGUID32(mStrBundleCPGUID32);
        rocreatebean.setCPNo(mStrBundleRetID);
        rocreatebean.setCPName(mStrBundleRetName);
        rocreatebean.setCPUID(mStrBundleRetailerUID);
        rocreatebean.setComingFrom(mStrComingFrom);
        rocreatebean.setComingFromInv(Constants.YES);
        initializeUI();
    }

    /**
     * @desc for initialization of UI components
     */
    private void initializeUI() {
        spDMSDivision = (MaterialDesignSpinner) findViewById(R.id.spDMSDivision);
        spDMSDivision.setVisibility(View.GONE);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        ConstantsUtils.setProgressColor(mContext, swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        // setting toolbar title
        ConstantsUtils.initActionBarView(this, mToolbar, true, getString(R.string.title_ro_create), 0);
        // Initializing recycler view
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<ReturnOrderBean>(mContext, R.layout.recycleview_inv_mat_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        displayRefreshTime(SyncUtils.getCollectionSyncTime(getApplicationContext(), Constants.SSINVOICES));
        // Initializing presenter(MVP)
        presenter = new AddInvoiceItemCreatePresenterImpl(mContext, this, true, AddInvoiceItemsActivity.this, rocreatebean,beatGUID,parentId);
        if (!Constants.restartApp(AddInvoiceItemsActivity.this)) {
//            presenter.getInvoiceList();
            presenter.getCPSPRelationDivisions();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showProgressDialog(String message) {

    }

    @Override
    public void hideProgressDialog() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void conformationDialog(String message, int from) {

    }

    @Override
    public void showMessage(String message, boolean isSimpleDialog) {
        ConstantsUtils.displayLongToast(AddInvoiceItemsActivity.this,message);
    }

    @Override
    public void searchResult(ArrayList<ReturnOrderBean> skuSearchList) {
        simpleRecyclerViewAdapter.refreshAdapter(skuSearchList);
    }

    @Override
    public void displayList(ArrayList<ReturnOrderBean> displayList) {

        if (displayList != null) {
            simpleRecyclerViewAdapter.refreshAdapter(displayList);
        }
    }

    @Override
    public void showProgressDialog() {
        swipeRefresh.setRefreshing(true);
    }

    DMSDivisionBean dmsDivisionBean = null;
    @Override
    public void displayDMSDivision(final ArrayList<DMSDivisionBean> alDMSDiv) {
        presenter.getInvoiceList("");

       /* ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(mContext, R.layout.custom_textview,
                R.id.tvItemValue, alDMSDiv) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivision, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivision.setAdapter(adapterShipToList);
        spDMSDivision.showFloatingLabel();
        spDMSDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dmsDivisionBean = alDMSDiv.get(position);
                if(dmsDivisionBean!=null) {
                    if(!dmsDivisionBean.getDMSDivisionID().equalsIgnoreCase("")) {
                        presenter.getInvoiceList(dmsDivisionBean.getDMSDivisionID());
                    }else {
                        displayList(new ArrayList<ReturnOrderBean>());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, AddInvoiceItemsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayRefreshTime(String refreshTime) {
        String lastRefresh = "";
        if (!TextUtils.isEmpty(refreshTime)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + refreshTime;
        }
        if (lastRefresh != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onItemClick(ReturnOrderBean roCreateBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new AddInvoiceCreateVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final ReturnOrderBean roCreateBean) {
        ((AddInvoiceCreateVH) viewHolder).textViewMatName.setText(roCreateBean.getMaterialDesc() + " (" + roCreateBean.getMaterialNo() + ")");
        ((AddInvoiceCreateVH) viewHolder).tvInvNumber.setText(roCreateBean.getInvoiceNo());
        ((AddInvoiceCreateVH) viewHolder).textViewInvoiceDate.setText(roCreateBean.getInvoiceDate());
        ((AddInvoiceCreateVH) viewHolder).cbMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                roCreateBean.setSelected(isChecked);
            }
        });
        ((AddInvoiceCreateVH) viewHolder).cbMaterial.setChecked(roCreateBean.getSelected());

        if(roCreateBean.getZZIsBomMaterial().equalsIgnoreCase("X")){
            ((AddInvoiceCreateVH) viewHolder).cbMaterial.setEnabled(false);
            ((AddInvoiceCreateVH) viewHolder).cbMaterial.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next_save, menu);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.menu_save);
        menu.removeItem(R.id.menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                //next step
                if (ConstantsUtils.isAutomaticTimeZone(AddInvoiceItemsActivity.this)) {
                    presenter.sendResultToOtherActivity();
                } else {
                    ConstantsUtils.showAutoDateSetDialog(AddInvoiceItemsActivity.this);
                }
                break;
        }
        return true;
    }

    @Override
    public void onFragmentFilterInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        presenter.onFragmentInteraction(brand, brandName, category, categoryName, creskuGrp, creskuGrpName);
    }

    @Override
    public void onBackPressed() {
        UtilConstants.dialogBoxWithCallBack(AddInvoiceItemsActivity.this, "", getString(R.string.alert_exit_create_ro), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    finish();
                }
            }
        });


    }
}