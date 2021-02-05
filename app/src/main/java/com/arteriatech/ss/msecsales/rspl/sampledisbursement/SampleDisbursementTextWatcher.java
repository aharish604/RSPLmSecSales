package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.List;

public class SampleDisbursementTextWatcher implements TextWatcher {
    private int position;
    private List<RetailerStockBean> retailerStockBeanList;
    private TextWatcherInterface textWatcherInterface = null;
    private EditText qtyEditText;

    public SampleDisbursementTextWatcher(List<RetailerStockBean> retailerStockBeanList, TextWatcherInterface textWatcherInterface) {
        this.retailerStockBeanList = retailerStockBeanList;
        this.textWatcherInterface = textWatcherInterface;
    }

    public void updatePosition(int position, EditText qtyEditText) {
        this.position = position;
        this.qtyEditText = qtyEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        retailerStockBeanList.get(position).setQAQty(charSequence.toString());
        if (textWatcherInterface != null) {
            textWatcherInterface.textChane(charSequence + "", position);
        }
        if (!charSequence.toString().isEmpty()) {
            qtyEditText.setBackgroundResource(R.drawable.edittext);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
