package com.arteriatech.ss.msecsales.rspl.beat.dealer;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by e10769 on 05-06-2018.
 */

public class DealerPresenterImpl implements DealerPresenter {
    private Context mContext;
    private DealerView dealerView=null;
    private ArrayList<DMSDivisionBean> distListDms=new ArrayList<>();

    public DealerPresenterImpl(Context mContext, DealerView dealerView){
        this.mContext=mContext;
        this.dealerView=dealerView;
    }
    @Override
    public void onStart() {
        if (dealerView!=null){
            dealerView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mStrDistQry = Constants.CPSPRelations + "?$select=CPNo,DMSDivisionID,CPGUID,CPName,DMSDivisionID,CPTypeID &$orderby=CPName asc";
                try {
                    distListDms = OfflineManager.getDistributorsDms(mStrDistQry);
                    if (distListDms != null && distListDms.size() > 0) {
                        Collections.sort(distListDms, new Comparator<DMSDivisionBean>() {
                            public int compare(DMSDivisionBean one, DMSDivisionBean other) {
                                return one.getDistributorName().compareTo(other.getDistributorName());
                            }
                        });

                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dealerView!=null){
                            dealerView.hideProgress();
                            dealerView.displayDistList(distListDms);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        dealerView=null;
    }
}
