package com.arteriatech.ss.msecsales.rspl.merchandising;

import android.graphics.Bitmap;

import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 25-Apr-18.
 */

public interface MerchandisingCreateView {
    void showProgress();

    void hideProgress();

    void displayMsg(String msg);

    void displayMerchList(ArrayList<ValueHelpBean> alMerchTypeList);

    void displayImages(Bitmap bitMap, byte[] imageInByte);

    void displayMerchTypeError(String error);

    void requestPermission();

    void displayRemarkError(String eror);
}
