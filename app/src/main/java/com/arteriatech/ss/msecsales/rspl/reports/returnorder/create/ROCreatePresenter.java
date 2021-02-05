package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.content.Intent;
import android.os.Bundle;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface ROCreatePresenter {
    void onStart();

    void onDestroy();

    boolean validateFields();

    boolean isValidMargin(String margin);

    void onAsignData(CompetitorBean competitorBean);

    void onSaveData();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void removeItem(ReturnOrderBean retailerStkBean);

    void onSearch(String query);
}
