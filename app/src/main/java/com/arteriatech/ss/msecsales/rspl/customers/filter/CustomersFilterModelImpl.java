package com.arteriatech.ss.msecsales.rspl.customers.filter;

import android.content.Context;


import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;

import java.util.ArrayList;

/**
 * Created by e10860 on 12/2/2017.
 */

public class CustomersFilterModelImpl implements CustomersFilterModel {

    private Context mContext;
    private CustomersFilterView filterView;
    private ArrayList<RetailerBean>RoutePlanCustomersList=null;

    public CustomersFilterModelImpl(Context mContext, CustomersFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
        this.RoutePlanCustomersList= new ArrayList<>();
    }
    @Override
    public void onStart() {


//        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.INVST + "'";
       /* try {
            RoutePlanCustomersList.addAll();
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (filterView!=null) {
            filterView.displayList(invoicePaymentStatusArrayList);
        }*/


    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
