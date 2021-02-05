package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.text.Editable;
import android.text.TextWatcher;

import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;

public class ROBatchTextWatcher implements TextWatcher {
    private ReturnOrderBean returnOrderBean = null;

    public ROBatchTextWatcher() {

    }

    public void updateTextWatcher(ReturnOrderBean returnOrderBean) {
        this.returnOrderBean = returnOrderBean;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (returnOrderBean != null) {
            returnOrderBean.setReturnBatchNumber(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
