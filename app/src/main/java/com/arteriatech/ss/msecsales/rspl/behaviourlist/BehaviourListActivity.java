package com.arteriatech.ss.msecsales.rspl.behaviourlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;

import java.util.ArrayList;


/**
 * Created by e10526 on 03-02-2017.
 */
public class BehaviourListActivity extends AppCompatActivity implements AdapterInterface<RetailerBean> {


    // android components
    RecyclerView recyclerView;
    TextView no_record_found;
    Toolbar toolbar;
    SimpleRecyclerViewAdapter<RetailerBean> recyclerViewAdapter = null;
    LinearLayout llFlowLayout;
    // variables
    BehaviourPresenterImpl presenter;
    ArrayList<RetailerBean> retailerBeanBeenFilterArrayList;
    private FlowLayout flowLayout;
    private ViewPager viewpagerHeader;
    private TabLayout tabLayoutHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behaviour_list);
        if (!Constants.restartApp(BehaviourListActivity.this)) {
            initializeUI(this);
        }
    }

    /**
     * @param context
     * @desc to initialize views
     */
    public void initializeUI(Context context) {
        llFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewpagerHeader = (ViewPager) findViewById(R.id.viewpagerHeader);
        tabLayoutHeader = (TabLayout) findViewById(R.id.tabLayoutHeader);

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_behaviour_list), 0);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeTabLayout();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }


    @Override
    public void onItemClick(RetailerBean customerBean, View view, int i) {

        /* Intent intentReqDetails = new Intent(FeedbackListActivity.this, FeedBackDetailsActivity.class);
        intentReqDetails.putExtra(Constants.FeedBackGUID, feedbackBean.getFeedbackGUID());
        intentReqDetails.putExtra(Constants.FeedbackNo, feedbackBean.getFeedbackNo());
        intentReqDetails.putExtra(Constants.FeedbackTypeDesc, feedbackBean.getFeedbackDescription());
        intentReqDetails.putExtra("from", "FeedBack");
        startActivity(intentReqDetails);*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new BehaviourListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, RetailerBean customerBean) {
        ((BehaviourListViewHolder) viewHolder).textViewRetailerName.setText(customerBean.getRetailerName());
        ((BehaviourListViewHolder) viewHolder).textViewCustomerID.setText(customerBean.getCustomerId());
        ((BehaviourListViewHolder) viewHolder).textViewMtdValue.setText(customerBean.getPurchaseQty() + " " + customerBean.getUOM());
    }

    private void initializeTabLayout() {
        setupViewPager(viewpagerHeader);
        tabLayoutHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayoutHeader.setupWithViewPager(viewpagerHeader);
    }

    private void setupViewPager(ViewPager viewPager) {
        retailerBeanBeenFilterArrayList = new ArrayList<>();
        String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.EntityType+" eq 'Evaluation' &$orderby=" + Constants.ID+"";
        final ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        try {
            final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = (ArrayList<ConfigTypesetTypesBean>) OfflineManager.getStatusConfig(mStrConfigQry, Constants.ALL);
            for (int i = 0; i < configTypesetTypesBeanArrayList.size(); i++) {
                    Bundle bundle = new Bundle();
                    bundle.putString("behaviourStatusID", configTypesetTypesBeanArrayList.get(i).getTypes());
                    bundle.putInt("behaviourPosition", i);
                    if ("000004".equals(configTypesetTypesBeanArrayList.get(i).getTypes())) {
                        UnbilledFragment unbilledFragment = new UnbilledFragment();
                        unbilledFragment.setArguments(bundle);
                        String title = configTypesetTypesBeanArrayList.get(i).getTypeName();
                        adapter.addFrag(unbilledFragment, title);
                    } else {
                        BehaviourFragment behaviourFragment = new BehaviourFragment();
                        behaviourFragment.setArguments(bundle);
                        String title = configTypesetTypesBeanArrayList.get(i).getTypeName();
                        adapter.addFrag(behaviourFragment, title);
                    }
            }

            viewpagerHeader.setAdapter(adapter);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    public void setActionTitle(String lastRefresh) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(lastRefresh);
    }
}
