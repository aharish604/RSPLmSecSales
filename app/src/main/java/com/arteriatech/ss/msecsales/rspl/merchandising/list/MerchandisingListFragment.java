package com.arteriatech.ss.msecsales.rspl.merchandising.list;


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
import com.arteriatech.ss.msecsales.rspl.mbo.MerchandisingBean;
import com.arteriatech.ss.msecsales.rspl.reports.merchandising.MerchandisingListActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchandisingListFragment extends Fragment implements MerchandisingListView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<MerchandisingBean> {


    private MerchandisingListPresenterImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<MerchandisingBean> simpleRecyclerViewAdapter;
    private Bundle bundle;
    private String comingFrom = "";
    private String lastRefresh = "";
    private String syncType = "";

    public MerchandisingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bundle = this.getArguments();
        if (bundle != null) {
            comingFrom = bundle.getString(Constants.comingFrom);
            try {
                syncType = bundle.getString(Constants.SyncType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_merchandising_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), R.layout.merchandising_list_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        presenter = new MerchandisingListPresenterImpl(getActivity(), this, bundle);
        presenter.onStart();
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
    public void showMessage(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void displayList(ArrayList<MerchandisingBean> alMercBean) {
        simpleRecyclerViewAdapter.refreshAdapter(alMercBean);
    }

    @Override
    public void onRefreshView()
    {
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLSTSyncTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.MerchReviews));
    }

    @Override
    public void displayLSTSyncTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + time;
        }
        ((MerchandisingListActivity) getActivity()).displaySubTitle(lastRefresh);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        // !Constants.isSync
        if (!Constants.iSAutoSync && !Constants.isSync) {
            if (comingFrom.equalsIgnoreCase(Constants.Device)) {
                presenter.onUploadData();
            } else {
                presenter.onRefresh();
            }
        } else {
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), getActivity());
            } else {
                UtilConstants.showAlert(getString(R.string.sync_in_progress), getActivity());
            }
        }


    }

    @Override
    public void onItemClick(MerchandisingBean merchandisingBean, View view, int i) {
        presenter.onItemClick(merchandisingBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new MerchandisingVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, MerchandisingBean merchandisingBean) {
        ((MerchandisingVH) viewHolder).tvMerchType.setText(merchandisingBean.getMerchReviewTypeDesc());
        ((MerchandisingVH) viewHolder).tvtvMerchDate.setText(merchandisingBean.getMerchReviewDate());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (comingFrom.equalsIgnoreCase(Constants.Device)) {
            inflater.inflate(R.menu.menu_sync_back, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:


                    onRefresh();

                return true;
            case R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
