package com.arteriatech.ss.msecsales.rspl.reports.returnorder.invoiceselection;

import android.content.Context;
import android.os.Bundle;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;

public interface AddInvoiceItemCreatePresenter {
    void onStart();
    void getInvoiceList(String dividionID);

    void onDestroy();

    boolean validateFields(ROCreateBean competitorBean);

    boolean isValidMargin(String margin);

    void onAsignData(CompetitorBean competitorBean);

    void onSaveData();

    void onSearch(String query);

    void onFragmentInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName);

    Bundle openFilter();

    void sendResultToOtherActivity();
}
