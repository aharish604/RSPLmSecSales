package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.os.Bundle;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;

public interface AddROCreatePresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(CompetitorBean competitorBean);

    boolean isValidMargin(String margin);

    void onAsignData(CompetitorBean competitorBean);

    void onSaveData();

    void onSearch(String query);

    void onFragmentInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName);

    Bundle openFilter();
}
