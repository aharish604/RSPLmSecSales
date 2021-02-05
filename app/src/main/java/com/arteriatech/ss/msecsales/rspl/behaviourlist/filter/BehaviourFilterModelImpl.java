package com.arteriatech.ss.msecsales.rspl.behaviourlist.filter;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.filter.SOFilterModel;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10893 on 25-01-2018.
 */


public class BehaviourFilterModelImpl implements SOFilterModel {
    private Context mContext;
    private IBehaviourFilterView filterView = null;

    public BehaviourFilterModelImpl(Context mContext, IBehaviourFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart() {
//        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType + " eq 'Evaluation' ";
        String mStrConfigQry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.EVLTYP + "'";

        final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList = new ArrayList<>();
        try {
            configTypesetTypesBeanArrayList.addAll(OfflineManager.getStatusConfig(mStrConfigQry, Constants.ALL));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        if (filterView!=null) {
            filterView.displayList(configTypesetTypesBeanArrayList, configTypesetDeliveryList);
        }

    }

    @Override
    public void onDestroy() {
        filterView=null;
    }
}

