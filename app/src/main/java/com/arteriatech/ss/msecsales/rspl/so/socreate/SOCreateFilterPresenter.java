package com.arteriatech.ss.msecsales.rspl.so.socreate;

import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;

import java.util.List;

/**
 * Created by e10526 on 18-05-2018.
 */

public interface SOCreateFilterPresenter {
    void onDestroy();
    void onStart(DmsDivQryBean dmsDivisionBean,String mStrBrandID);
    void getBrandList(String catID, List<String> dividionID);



}
