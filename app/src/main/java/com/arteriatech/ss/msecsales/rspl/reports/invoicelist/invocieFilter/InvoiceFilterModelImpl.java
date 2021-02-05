package com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invocieFilter;

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

public class InvoiceFilterModelImpl implements InvoiceFilterModel {

    private Context mContext;
    private InvoiceFilterView filterView;
    public static final String STATUS_POSTED = "10";
    public static final String STATUS_CONFIRMED = "11";
    public static final String STATUS_PARTIALLY_PAID = "12";
    public static final String STATUS_COMPLETELY_PAID = "13";
    public static final String STATUS_COMPLETELY_RETURN = "15";
    public static final String STATUS_PARTIALLY_RETURN = "16";
    public static final String STATUS_CANCELED = "14";

    public InvoiceFilterModelImpl(Context mContext, InvoiceFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }
    @Override
    public void onStart() {

        ArrayList<ConfigTypesetTypesBean> invoicePaymentStatusArrayList = new ArrayList<>();
//        ArrayList<ConfigTypesetTypesBean> invoiceStatusArrayList = new ArrayList<>();


//        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.INVST + "'";
       /* String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.PaymentStatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSInvoice+"' &$orderby="+ Constants.ID+"%20asc";
        try {
            invoicePaymentStatusArrayList.addAll(OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
      /*  mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.StatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSInvoice+"' &$orderby="+ Constants.ID+"%20asc";
        try {
            invoiceStatusArrayList.addAll(OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
        /**
         *  Posted + Open       =   Posted
            Confirmed + Open	=   Confirmed 
            Partialy processed  =	Partially Paid
            closed	            =   Completely paid 
            Cancelled           =  	Cancelled
         */
        ConfigTypesetTypesBean configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes("");
        configTypesetTypesBean.setTypesName("All");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_POSTED);
        configTypesetTypesBean.setTypesName("Posted");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_CONFIRMED);
        configTypesetTypesBean.setTypesName("Confirmed");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_PARTIALLY_PAID);
        configTypesetTypesBean.setTypesName("Partially Paid");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_COMPLETELY_PAID);
        configTypesetTypesBean.setTypesName("Completely Paid");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_COMPLETELY_RETURN);
        configTypesetTypesBean.setTypesName("Completely Return");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_PARTIALLY_RETURN);
        configTypesetTypesBean.setTypesName("Partially Return");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);

        configTypesetTypesBean = new ConfigTypesetTypesBean();
        configTypesetTypesBean.setTypes(STATUS_CANCELED);
        configTypesetTypesBean.setTypesName("Cancelled");
        invoicePaymentStatusArrayList.add(configTypesetTypesBean);


        if (filterView!=null) {
            filterView.displayList(invoicePaymentStatusArrayList,null);
        }


    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
