package com.arteriatech.ss.msecsales.rspl.visit.summary;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.common.MyAxisValueFormatter;
import com.arteriatech.ss.msecsales.rspl.ui.AxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment implements SummaryView, SwipeRefreshLayout.OnRefreshListener {

    private SummaryPresenterImpl presenter;
    private BarChart chart;
    private TextView tvTargetValue;
    private String mStrBundleCPGUID = "";
    private String mStrBundleBeatGUID = "";
    private String mStrParentId = "";
    private TextView tvOpenOrderValue;
    private TextView tvVisitDate;
    private TextView tvVisitDateVal,tvInvoiceValue,tvTitle_AvgInvoice;
    private TextView tvOpenOrderNo,tvLastOrderDate;
    private LinearLayout ll_visit_date;
    private TextView tcCMTitle;
    private SwipeRefreshLayout swipeRefresh;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
            mStrBundleBeatGUID = getArguments().getString(Constants.BeatGUID,"");
            mStrParentId = getArguments().getString(Constants.ParentId,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = (BarChart) view.findViewById(R.id.bar_chart);
        tvTargetValue = (TextView) view.findViewById(R.id.tvTargetValue);
        tvOpenOrderValue = (TextView) view.findViewById(R.id.tvOpenOrderValue);
        tvVisitDate = (TextView) view.findViewById(R.id.tvVisitDate);
        tvVisitDateVal = (TextView) view.findViewById(R.id.tvVisitDateVal);
        tvInvoiceValue = (TextView) view.findViewById(R.id.tvInvoiceValue);
        tvTitle_AvgInvoice = (TextView) view.findViewById(R.id.tvTitle_AvgInvoice);
        tvOpenOrderNo = (TextView) view.findViewById(R.id.tvLastOrderNo);
        tvLastOrderDate = (TextView) view.findViewById(R.id.tvLastOrderDate);
        ll_visit_date = (LinearLayout) view.findViewById(R.id.ll_visit_date);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        ConstantsUtils.setProgressColor(getContext(),swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        tcCMTitle = (TextView) view.findViewById(R.id.tcCMTitle);
        chart.setNoDataText(getString(R.string.no_data));
//        chart.setNoDataTextColor(getResources().getColor(R.color.sub_text_view));
        presenter = new SummaryPresenterImpl(getContext(), this, mStrBundleCPGUID,mStrBundleBeatGUID,mStrParentId);
        presenter.onStart();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
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

    }

    @Override
    public void displayValue(ArrayList<BarEntry> barEntryArrayList, ArrayList<String> labels, String cmTarget,String crtMth,String mStrCurrency) {
        setChat(chart, barEntryArrayList, labels,mStrCurrency);
        tvTargetValue.setText(cmTarget);
        tcCMTitle.setText(crtMth);
    }

    @Override
    public void displayOpenOrder(String openOrder,String mStrVisitDate,String mStrOrderNo,String mStrOrderDate,String mAvgInvValue,String mStrCurrency) {
        tvOpenOrderValue.setText(openOrder);

        try {
            tvInvoiceValue.setText(Constants.getCurrencySymbol(mStrCurrency,mAvgInvValue));
        } catch (Exception e) {
            tvInvoiceValue.setText(Constants.getCurrencySymbol("INR","0.00"));
            e.printStackTrace();
        }


        if(mStrOrderNo.equalsIgnoreCase("")){
            tvOpenOrderNo.setText("NA");
            tvLastOrderDate.setText("NA");
        }else{
            tvOpenOrderNo.setText(mStrOrderNo);
            tvLastOrderDate.setText(mStrOrderDate);
        }
        if(mStrVisitDate.equalsIgnoreCase("")){
            ll_visit_date.setVisibility(View.GONE);
        }else{
            ll_visit_date.setVisibility(View.VISIBLE);
        }


        tvVisitDateVal.setText(mStrVisitDate);
    }

    private void setChat(BarChart mChart, ArrayList<BarEntry> barEntryArrayList, ArrayList<String> labels,String mStrCurrency) {
        Typeface typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        XAxis xl = mChart.getXAxis();
        xl.setValueFormatter(new AxisValueFormatter(labels));
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(typeface);
        xl.setTextSize(12f);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);

        YAxis yl = mChart.getAxisLeft();
        yl.setEnabled(false);
        yl.setTypeface(typeface);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = mChart.getAxisRight();
        yr.setEnabled(false);
        yr.setEnabled(false);
        yr.setTypeface(typeface);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        BarDataSet set1 = new BarDataSet(barEntryArrayList, getString(R.string.summary_trend_chart));
        set1.setDrawIcons(false);
        set1.setValueTextSize(12f);// set text size above the bar
        set1.setColor(ContextCompat.getColor(getActivity(), R.color.secondaryColor));
        set1.setDrawValues(true);   //to hide values on top of bar

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        BarData data = new BarData(dataSets);
        data.setValueFormatter(new MyAxisValueFormatter());
        data.setValueTypeface(typeface);
        mChart.setData(data);
        mChart.setTouchEnabled(false);
        mChart.setExtraRightOffset(60f);
        mChart.animateXY(1500, 1500);
        if(getMobileDim(getActivity())>1280){
            chart.setViewPortOffsets(7f, 30f, 0f, 55f);
        }else{
            chart.setViewPortOffsets(7f, 20f, 0f, 35f);
        }

        mChart.invalidate();
    }

    @Override
    public void onRefresh() {
        presenter.onStart();
    }

    private static double getMobileDim(Activity mAct){
        int height = 0;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mAct.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
        } catch (Exception e) {
            height = 0;
            e.printStackTrace();
        }

        return height;
    }
}
