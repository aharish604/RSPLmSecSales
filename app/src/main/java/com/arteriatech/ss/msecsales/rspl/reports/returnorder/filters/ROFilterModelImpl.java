package com.arteriatech.ss.msecsales.rspl.reports.returnorder.filters;

import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-10-2017.
 */

public class ROFilterModelImpl implements ROFilterModel {
    private Context mContext;
    private ROFilterView filterView = null;

    public ROFilterModelImpl(Context mContext, ROFilterView filterView) {
        this.mContext = mContext;
        this.filterView = filterView;
    }

    @Override
    public void onStart() {
        final ArrayList<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
        final ArrayList<ConfigTypesetTypesBean> configTypesetDeliveryList = new ArrayList<>();

        String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.StatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSRO+"' &$orderby="+ Constants.ID+"%20asc";
        try {
            configTypesetDeliveryList.addAll(OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.ALL));
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
