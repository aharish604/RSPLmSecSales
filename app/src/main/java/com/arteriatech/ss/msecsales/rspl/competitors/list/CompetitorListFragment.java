package com.arteriatech.ss.msecsales.rspl.competitors.list;

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
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;

import java.util.ArrayList;


public class CompetitorListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AdapterInterface<CompetitorBean>, CompetitorListView {

    private Bundle bundle = null;
    private CompetitorListPresenterImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<CompetitorBean> simpleRecyclerViewAdapter;
    private String lastRefresh = "";
    private String syncType = "";
    private String comingFrom;

    public CompetitorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        bundle = this.getArguments();
        if (bundle != null) {
            comingFrom = bundle.getString(Constants.comingFrom);
            syncType = bundle.getString(Constants.SyncType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_competitor_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    /**
     * @param view
     * @desc initializing views
     */
    private void initViews(View view) {
        try {
            swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
            ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
            swipeRefresh.setOnRefreshListener(this);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<CompetitorBean>(getActivity(), R.layout.competitor_list_items, this, recyclerView, noRecordFound);
            recyclerView.setAdapter(simpleRecyclerViewAdapter);

            presenter = new CompetitorListPresenterImpl(getActivity(), getContext(), this, bundle);
            if (!Constants.restartApp(getActivity())) {
                presenter.onStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void displayList(ArrayList<CompetitorBean> displayList) {
        simpleRecyclerViewAdapter.refreshAdapter(displayList);
    }

    @Override
    public void onRefreshView() {
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLSTSyncTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.CompetitorInfos));
    }

    @Override
    public void displayLSTSyncTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + time;
        }

        ((CompetitorListActivity) getActivity()).displaySubTitle(lastRefresh);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        if (comingFrom.equalsIgnoreCase(Constants.Device)) {
            presenter.onUploadData();
        } else {
            presenter.onRefresh();
        }
    }

    @Override
    public void onItemClick(CompetitorBean competitorBean, View view, int i) {
        presenter.onItemClick(competitorBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CompetitorListVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, CompetitorBean competitorBean) {
        ((CompetitorListVH) viewHolder).tvCompetitorName.setText(competitorBean.getCompanyName());
        ((CompetitorListVH) viewHolder).tvDate.setText(competitorBean.getUpdatedOn());
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
