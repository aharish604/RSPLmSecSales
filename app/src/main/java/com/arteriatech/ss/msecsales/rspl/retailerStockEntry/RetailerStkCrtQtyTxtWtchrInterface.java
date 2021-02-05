package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import androidx.recyclerview.widget.RecyclerView;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;



public interface RetailerStkCrtQtyTxtWtchrInterface {
    void onTextChange(String charSequence, RetailerStockBean stockBean, RecyclerView.ViewHolder holder);
}
