package com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.alerts.AlertsActivity;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 3/29/2018.
 */

public class AlertHistoryFragment extends Fragment implements AlertsView, AdapterInterface<BirthdaysBean>, SwipeRefreshLayout.OnRefreshListener {

    public static final String Alerts_list = "1";
    public static final String Alerts_History = "2";
    private SimpleRecyclerViewAdapter<BirthdaysBean> simpleRecyclerViewAdapter;
    private Context context;
    private ProgressDialog progressDialog;
    private RecyclerView rvAlertHistory;
    private TextView tvNoRecord;
    private AlertsPresenterImple alertsPresenterImple;
    //    private SwipeRefreshLayout swipeRefresh;
    private boolean hasLoadedOnce = false;


    public AlertHistoryFragment() {
        /*
        empty constructor is required
         */
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewID(view);
    }

    private void setViewID(View view) {
        ((AlertsActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.alerts));

        rvAlertHistory = (RecyclerView) view.findViewById(R.id.rvAlertHistory);
        tvNoRecord = (TextView) view.findViewById(R.id.no_record_found);
//        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(this);
//        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAlertHistory.setLayoutManager(linearLayoutManager);
        rvAlertHistory.setNestedScrollingEnabled(false);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getContext(), R.layout.snippet_alerts_history, this, rvAlertHistory, tvNoRecord);
        rvAlertHistory.setAdapter(simpleRecyclerViewAdapter);
        //callPresenter();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !hasLoadedOnce) {
                hasLoadedOnce = true;
                //  callPresenter();
            }
        }
    }


    public void callPresenter() {
        alertsPresenterImple = new AlertsPresenterImple(getActivity(), false, this);
        alertsPresenterImple.alertsCallHistory();
    }

    @Override
    public void onShowProgress(String msg) {
//        progressDialog = ConstantsUtils.showProgressDialog(getContext(), msg);
//        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void onHideProgress() {
//        if (progressDialog != null)
//            progressDialog.dismiss();
//        swipeRefresh.setRefreshing(false);

    }

    @Override
    public void showMessage(String errorMsg) {
        ConstantsUtils.displayShortToast(getContext(), errorMsg);
    }

    public void showData(List<BirthdaysBean> historyBeanList) {
        simpleRecyclerViewAdapter.refreshAdapter((ArrayList<BirthdaysBean>) historyBeanList);
    }

    @Override
    public void onItemClick(BirthdaysBean alertsHistoryBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new AlertsHistoryVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i, BirthdaysBean alertsHistoryBean) {
       /* ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getAlertText());
        ((AlertsHistoryVH) holder).tvCreatedOn.setText(getString(R.string.createdon) + " "+alertsHistoryBean.getCreatedOn());

        // displaying the first letter of From in icon text
        ((AlertsHistoryVH)holder).iconContainer.setVisibility(View.GONE);
        ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getAlertText().substring(0, 1));*/

//        applyProfilePicture((AlertsHistoryVH) holder,alertsHistoryBean);
        ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getRetailerName());
        ((AlertsHistoryVH) holder).tvRetailerOwner.setText(alertsHistoryBean.getOwnerName());
        if (!TextUtils.isEmpty(alertsHistoryBean.getRetailerName()))
            ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getRetailerName().substring(0, 1));
        if (!"".equalsIgnoreCase(alertsHistoryBean.getAlertGUID())) {
            ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getAlertText());
            ((AlertsHistoryVH) holder).tvRetailerOwner.setText("");
            if (!TextUtils.isEmpty(alertsHistoryBean.getAlertText()))
                ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getAlertText().substring(0, 1));
        }
        if (alertsHistoryBean.getAppointmentAlert()) {
            String startTime = UtilConstants.convertTimeOnly(alertsHistoryBean.getAppointmentTime()).substring(0, 5);
            String endTime = UtilConstants.convertTimeOnly(alertsHistoryBean.getAppointmentEndTime()).substring(0, 5);
            ((AlertsHistoryVH) holder).tvRetailerOwner.setText(alertsHistoryBean.getAppointmentType() + " "
                    + startTime + " "
                    + endTime);
            ((AlertsHistoryVH) holder).ivAppointment.setImageResource(R.drawable.ic_calender_24dp);
        }
        if (!alertsHistoryBean.getDOB().equalsIgnoreCase("") && ConstantsUtils.showImage(alertsHistoryBean.getDOB())) {//alertsHistoryBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivDOB.setImageResource(R.drawable.ic_cake_black_24dp);
        } else {
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        }

        if (!alertsHistoryBean.getAnniversary().equalsIgnoreCase("") && ConstantsUtils.showImage(alertsHistoryBean.getAnniversary())) {//alertsHistoryBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivAnversiry.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.GONE);
        }


        if (alertsHistoryBean.getAppointmentAlert()) {
            ((AlertsHistoryVH) holder).ivAppointment.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivAppointment.setImageResource(R.drawable.ic_calender_24dp);
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.GONE);
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        } else {
            ((AlertsHistoryVH) holder).ivAppointment.setVisibility(View.GONE);
        }
        if (!"".equalsIgnoreCase(alertsHistoryBean.getAlertGUID())) {
//            ((AlertsHistoryVH) holder).ivMobileNo.setVisibility(View.INVISIBLE);
            ((AlertsHistoryVH) holder).ivAnversiry.setImageResource(R.drawable.ic_notifications_black_24dp);
            ((AlertsHistoryVH) holder).tvRetailerOwner.setVisibility(View.GONE);
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        }
    }


    @Override
    public void onRefresh() {
        alertsPresenterImple.alertsCallHistory();
    }

    public void updateFragment() {
        alertsPresenterImple.alertsCallHistory();
    }



   /* private void applyProfilePicture(AlertsHistoryVH holder, AlertsHistoryBean message) {
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(R.color.secondaryDarkColor);
        holder.iconText.setVisibility(View.VISIBLE);
    }*/
}
