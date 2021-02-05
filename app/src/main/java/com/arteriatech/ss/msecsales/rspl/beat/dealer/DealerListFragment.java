package com.arteriatech.ss.msecsales.rspl.beat.dealer;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.beat.OtherBeatActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerListActivity;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerListViewHolder;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DealerListFragment extends Fragment implements DealerView, SwipeRefreshLayout.OnRefreshListener, AdapterViewInterface<DMSDivisionBean> {


    private TextView noRecordFound;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private SimpleRecyclerViewTypeAdapter<DMSDivisionBean> recyclerViewAdapter;
    private DealerPresenterImpl presenter;
    private String comingFrom = "";

    public DealerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            comingFrom = bundle.getString(Constants.comingFrom,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dealer_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);

        swipeRefresh.setOnRefreshListener(this);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new SimpleRecyclerViewTypeAdapter<>(getContext(), R.layout.snippet_customer, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);

        presenter = new DealerPresenterImpl(getContext(), this);
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
    public void displayMessage(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void displayDistList(ArrayList<DMSDivisionBean> distListDms) {
        if (ConstantsUtils.getRollInformation(getContext()).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
            if (distListDms.size() <= 1) {
                if (!distListDms.isEmpty())
                    redirectCustomerList(distListDms.get(0));
                else
                    redirectCustomerList(new DMSDivisionBean());
                getActivity().finish();
            } else {
                recyclerViewAdapter.refreshAdapter(distListDms);
            }
        } else {
            recyclerViewAdapter.refreshAdapter(distListDms);
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    private void redirectCustomerList(DMSDivisionBean dmsDivisionBean) {
        Constants.Route_Plan_No = "";
        Constants.Route_Plan_Desc = "";
        Constants.Route_Plan_Key = "";
        Constants.mSetTodayRouteSch.clear();
        if (comingFrom.equalsIgnoreCase(Constants.BeatPlan)) {
            Intent intent = new Intent(getContext(), OtherBeatActivity.class);
            intent.putExtra(Constants.BeatPlan, true);
            intent.putExtra(Constants.EXTRA_CPGUID, dmsDivisionBean.getDistributorGuid());
            startActivity(intent);
        } else {
            Log.e("Adhoc_Visit","OnItemClick");
            Intent retList = new Intent(getContext(), CustomerListActivity.class);
            retList.putExtra(Constants.comingFrom, comingFrom);
            retList.putExtra(Constants.EXTRA_CPGUID, dmsDivisionBean.getDistributorGuid());
            startActivity(retList);
        }

    }

    @Override
    public void onItemClick(DMSDivisionBean dmsDivisionBean, View view, int i) {
        redirectCustomerList(dmsDivisionBean);
    }


    @Override
    public int getItemViewType(int i, ArrayList<DMSDivisionBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CustomerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DMSDivisionBean dmsDivisionBean, ArrayList<DMSDivisionBean> arrayList) {
        ((CustomerListViewHolder) viewHolder).tvRetailerName.setText(dmsDivisionBean.getDistributorName());
        ((CustomerListViewHolder) viewHolder).tv_retailer_mob_no.setText(dmsDivisionBean.getDistributorId());
        ((CustomerListViewHolder) viewHolder).tvGrp3.setVisibility(View.GONE);
        ((CustomerListViewHolder) viewHolder).ivMobileNo.setVisibility(View.GONE);
        if (dmsDivisionBean.getDistributorName().length() > 0)
            ((CustomerListViewHolder) viewHolder).tvName.setText(String.valueOf(dmsDivisionBean.getDistributorName().trim().charAt(0)).toUpperCase());

    }
}
