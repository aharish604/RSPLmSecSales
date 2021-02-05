package com.arteriatech.ss.msecsales.rspl.reports.invoicelist;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by e10847 on 19-12-2017.
 */

public interface IInvoiceListPresenter {
    void onFilter();
    void onSearch(String searchText);
    void onRefresh();
    void startFilter(int requestCode, int resultCode, Intent data);
    ArrayList<InvoiceListBean>  getInvoiceList();
}
