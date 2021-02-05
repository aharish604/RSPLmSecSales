package com.arteriatech.ss.msecsales.rspl.home.nav;


import com.arteriatech.ss.msecsales.rspl.mbo.SalesPersonBean;

import java.util.List;

public interface MenuPresenter {

    List<SalesPersonBean> setSideMenuData();

    void onActivityCreated();

}
