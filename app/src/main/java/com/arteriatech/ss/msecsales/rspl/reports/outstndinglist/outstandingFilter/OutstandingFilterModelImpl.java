package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingFilter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 05/05/2017.
 *
 */

public class OutstandingFilterModelImpl implements OutStndingInvFilterModel {

    private Context mContext;
    private OutStndingInvFilterView filterView;

    public OutstandingFilterModelImpl(Context mContext, OutStndingInvFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }
    @Override
    public void onStart() {

        ArrayList<ConfigTypesetTypesBean> invoicePaymentStatusArrayList = new ArrayList<>();
        ConfigTypesetTypesBean configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("");
        configTypesetTypesBean.setTypesName("All");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("01");
        configTypesetTypesBean.setTypesName("0 - 30 Days");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("02");
        configTypesetTypesBean.setTypesName("31 - 60 Days");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("03");
        configTypesetTypesBean.setTypesName("61 - 90 Days");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("04");
        configTypesetTypesBean.setTypesName("> 90 Days");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

/*
//        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.INVST + "'";
        String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.PaymentStatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSInvoice+"' &$orderby="+ Constants.ID+"%20asc";
        try {
            invoicePaymentStatusArrayList.addAll(OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
        if (filterView!=null) {
            filterView.displayList(invoicePaymentStatusArrayList,null);
        }


    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
