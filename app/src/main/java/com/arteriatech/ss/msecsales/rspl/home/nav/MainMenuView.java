package com.arteriatech.ss.msecsales.rspl.home.nav;

import com.arteriatech.mutils.registration.MainMenuBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesPersonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 11-05-2017.
 */

public interface MainMenuView {
    void onItemLoaded(ArrayList<MainMenuBean> mainMenuBeenList, List<SalesPersonBean> salesPersonList);

    void showMessage(String message);

    void onRefresh();

}
