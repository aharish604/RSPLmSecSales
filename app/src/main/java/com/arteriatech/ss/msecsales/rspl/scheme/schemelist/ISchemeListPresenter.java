package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import android.content.Intent;

import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeListBean;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface ISchemeListPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    ArrayList<SchemeListBean>  getSchemeList();
}
