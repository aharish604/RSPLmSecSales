package com.arteriatech.ss.msecsales.rspl.feedback.list;


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
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackListFragment extends Fragment implements FeedBackListView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<FeedbackBean> {
    // data members
    private Bundle bundle = null;
    private String comingFrom = "";
    private String syncType = "";
    private FeedBackListPresenterImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<FeedbackBean> simpleRecyclerViewAdapter;
    private String lastRefresh = "";

    // constructor
    public FeedBackListFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_back_list, container, false);
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
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<FeedbackBean>(getActivity(), R.layout.feed_back_list_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        presenter = new FeedBackListPresenterImpl(getActivity(), getContext(), this, bundle);
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
    public void displayList(ArrayList<FeedbackBean> displayList) {
        simpleRecyclerViewAdapter.refreshAdapter(displayList);
    }

    @Override
    public void onRefreshView() {
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLSTSyncTime(SyncUtils.getCollectionSyncTime(getContext(), Constants.Feedbacks));
    }

    @Override
    public void displayLSTSyncTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + time;
        }
        ((FeedbackListActivity) getActivity()).displaySubTitle(lastRefresh);
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
    public void onItemClick(FeedbackBean feedbackBean, View view, int i) {
        presenter.onItemClick(feedbackBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new FeedBackListVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, FeedbackBean feedbackBean) {
        ((FeedBackListVH) viewHolder).tvFeedBackNo.setText(feedbackBean.getFeedbackNo());
        ((FeedBackListVH) viewHolder).tvFeedBackType.setText(feedbackBean.getFeedbackTypeDesc());
        ((FeedBackListVH) viewHolder).tvDate.setText(feedbackBean.getFeedbackDate());
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
