package com.arteriatech.ss.msecsales.rspl.beat;

import com.arteriatech.ss.msecsales.rspl.mbo.BeatOpeningSummaryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 29-06-2017.
 */

public interface IBeatListView {
    void success();

    void showMessage(String message);

    void showProgressDialog();

    void hideProgressDialog();

    void searchResult(ArrayList<RetailerBean> salesOrderBeen);

    void displayBeatList(ArrayList<RetailerBean> salesOrderBeen);

    void displayRefreshTime(String refreshTime);

    void onRefreshView();
    void beatOpeningDetails(BeatOpeningSummaryBean beatOpeningSummaryBean);
}
