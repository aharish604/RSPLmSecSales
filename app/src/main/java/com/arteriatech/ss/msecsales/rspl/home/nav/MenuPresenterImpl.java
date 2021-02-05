package com.arteriatech.ss.msecsales.rspl.home.nav;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.registration.MainMenuBean;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesPersonBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 1/31/2018.
 */

public class MenuPresenterImpl implements MenuPresenter, MenuModel.OnFinishedListener {

    private Context mContext;
    private List<SalesPersonBean> salesPersonBeanList = new ArrayList<>();
    private MenuModelImpl model;
    private int viewType;
    private MainMenuView mainMenuView;

    MenuPresenterImpl(Context mContext, MenuModelImpl model, int viewType, MainMenuView mainMenuView) {
        this.mContext = mContext;
        this.model = model;
        this.viewType = viewType;
        this.mainMenuView = mainMenuView;
    }

    @Override
    public List<SalesPersonBean> setSideMenuData() {
        String mStrSPGUID = Constants.getSPGUID();
        String qry = Constants.UserSalesPersons + "?$filter=" + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
        try {
            salesPersonBeanList = OfflineManager.getSalesPerson(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return salesPersonBeanList;
    }

    @Override
    public void onActivityCreated() {
        if (model != null) {
            model.findItems(mContext, this, viewType);
        }
    }


    @Override
    public void onFinished(final ArrayList<MainMenuBean> mainMenuBeenList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<SalesPersonBean> salesPersonList = setSideMenuData();
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainMenuView != null)
                            mainMenuView.onItemLoaded(mainMenuBeenList, salesPersonList);
                    }
                });
            }
        }).start();

    }
}
