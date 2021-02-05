package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;

public class AddROActivity extends AppCompatActivity implements AddROCreateView, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterInterface<ReturnOrderBean>, ROFilterDialogFragment.OnFragmentFilterListener {
    // data members
    public final static int RETURN_ORDER_RESULT_ID = 867;
    private Context mContext = this;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private ArrayList<ReturnOrderBean> finalSendResult = new ArrayList<>();
    private AddROCreatePresenterImpl presenter;
    private SimpleRecyclerViewAdapter<ReturnOrderBean> simpleRecyclerViewAdapter;
    private Toolbar mToolbar;
    private ArrayList<ReturnOrderBean> roCreateBean = null;
    private String stockOwner;
    private LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ro);

        Intent intent = getIntent();

        if (intent != null) {
            roCreateBean = intent.getParcelableArrayListExtra(Constants.ItemList);
            stockOwner = intent.getStringExtra(Constants.StockOwner);
        }

        initializeUI();
    }

    /**
     * @desc for initialization of UI components
     */
    private void initializeUI() {
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
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<ReturnOrderBean>(mContext, R.layout.add_ro_list_items, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        // Initializing presenter(MVP)
        presenter = new AddROCreatePresenterImpl(mContext, this, true, AddROActivity.this, roCreateBean);
        if (!Constants.restartApp(AddROActivity.this)) {
            presenter.onStart();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showProgressDialog(String message) {
        swipeRefresh.setRefreshing(true);
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, AddROActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onItemClick(ReturnOrderBean roCreateBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new AddROCreateVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, final ReturnOrderBean roCreateBean) {
        ((AddROCreateVH) viewHolder).cbMaterial.setText(roCreateBean.getMaterialDesc() + " (" + roCreateBean.getMaterialNo() + ")");
        ((AddROCreateVH) viewHolder).cbMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                roCreateBean.setSelected(isChecked);
            }
        });
        ((AddROCreateVH) viewHolder).cbMaterial.setChecked(roCreateBean.getSelected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ro_add, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.ro_material_search_hint));
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.apply:
                sendResultToOtherActivity(AddROActivity.this);
                return true;
            case R.id.filter:
                Bundle bundle = presenter.openFilter();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                ROFilterDialogFragment dbFilterDialogFragment = new ROFilterDialogFragment();
                dbFilterDialogFragment.setArguments(bundle);
//                dbFilterDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog);
                dbFilterDialogFragment.show(ft, "dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void sendResultToOtherActivity(final Context mContext) {
        finalSendResult.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ReturnOrderBean returnOrderBean : roCreateBean) {
                    if (returnOrderBean.getSelected()) {
                        finalSendResult.add(returnOrderBean);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalSendResult.size() > 0) {
                            Intent intent = new Intent(mContext, ROCreateActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, finalSendResult);
                            setResult(RETURN_ORDER_RESULT_ID, intent);
                            finish();
                        } else {
                            UtilConstants.showAlert(getString(R.string.validation_sel_atlest_one_material), mContext);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFragmentFilterInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        presenter.onFragmentInteraction(brand, brandName, category, categoryName, creskuGrp, creskuGrpName);
    }
}
