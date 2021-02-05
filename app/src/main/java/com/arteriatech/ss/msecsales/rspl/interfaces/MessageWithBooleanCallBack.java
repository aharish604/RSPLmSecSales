package com.arteriatech.ss.msecsales.rspl.interfaces;


import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;

/**
 * Created by e10526 on 6/27/2017.
 */

public interface MessageWithBooleanCallBack {
    void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean);
}
