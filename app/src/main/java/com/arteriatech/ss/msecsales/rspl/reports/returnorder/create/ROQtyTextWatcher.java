package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.text.Editable;
import android.text.TextWatcher;

import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;

public class ROQtyTextWatcher implements TextWatcher {
    private ReturnOrderBean returnOrderBean = null;

    public ROQtyTextWatcher() {

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
            returnOrderBean.setReturnQty(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
