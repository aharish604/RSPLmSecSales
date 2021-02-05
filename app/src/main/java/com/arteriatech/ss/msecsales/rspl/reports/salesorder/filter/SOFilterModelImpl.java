package com.arteriatech.ss.msecsales.rspl.reports.salesorder.filter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public class SOFilterModelImpl implements SOFilterModel {
    private Context mContext;
    private SOFilterView filterView = null;

    public SOFilterModelImpl(Context mContext, SOFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart() {
        final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList = new ArrayList<>();
        /*try {
            configTypesetTypesBeanArrayList.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
      /*  mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.DELVST + "' &$orderby = Types asc";
        try {
            configTypesetDeliveryList.addAll(OfflineManager.getConfigTypesetTypes(mStrConfigQry));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/


        String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.StatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSSO+"' &$orderby="+ Constants.ID+"%20asc";
        try {
            configTypesetDeliveryList.addAll(OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        ConfigTypesetTypesBean configTypesetTypesBean = new ConfigTypesetTypesBean();
//        configTypesetTypesBean.setTypes("000001");
//        configTypesetTypesBean.setTypesName("Open");
//        configTypesetDeliveryList.add(configTypesetTypesBean);
//        configTypesetTypesBean = new ConfigTypesetTypesBean();
//        configTypesetTypesBean.setTypes("000002");
//        configTypesetTypesBean.setTypesName("Partially Processed");
//        configTypesetDeliveryList.add(configTypesetTypesBean);
//        configTypesetTypesBean = new ConfigTypesetTypesBean();
//        configTypesetTypesBean.setTypes("000003");
//        configTypesetTypesBean.setTypesName("Bill Generated");
//        configTypesetDeliveryList.add(configTypesetTypesBean);
//        configTypesetTypesBean = new ConfigTypesetTypesBean();
//        configTypesetTypesBean.setTypes("000004");
//        configTypesetTypesBean.setTypesName("Rejected");
//        configTypesetDeliveryList.add(configTypesetTypesBean);
        if (filterView!=null) {
            filterView.displayList(configTypesetTypesBeanArrayList, configTypesetDeliveryList);
        }

    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}
