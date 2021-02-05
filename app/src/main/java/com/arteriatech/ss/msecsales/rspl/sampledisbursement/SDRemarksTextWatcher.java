package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

public class SDRemarksTextWatcher implements TextWatcher {


    private RetailerStockBean stockBean = null;
    private RecyclerView.ViewHolder holder = null;

    public SDRemarksTextWatcher() {

    }

    public void updateTextWatcher(RetailerStockBean stockBean, RecyclerView.ViewHolder holder) {
        this.stockBean = stockBean;
        this.holder = holder;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (stockBean != null) {
            stockBean.setRemarks(s.toString());
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
       /* if (!TextUtils.isEmpty(s.toString())) {
            String text = Double.toString(Math.abs(Double.parseDouble(s.toString())));
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;
            int charLength = integerPlaces + 1 + decimalPlaces;
            if (charLength<=13 && decimalPlaces >=3) {
                if (holder instanceof SOMultiMaterialVH) {
                    ((SOMultiMaterialVH) holder).etQty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charLength)});
                }
            }
        }*/
    }

//    private int position;
//    private List<RetailerStockBean> retailerStockBeanList;
//    private EditText remarks;
//
//    public SDRemarksTextWatcher(List<RetailerStockBean> retailerStockBeanList) {
//        this.retailerStockBeanList = retailerStockBeanList;
//    }
//
//    public void updatePosition(int position, EditText remarks) {
//        this.position = position;
//        this.remarks = remarks;
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//        retailerStockBeanList.get(position).setRemarks(charSequence.toString());
//        if (!charSequence.toString().isEmpty()) {
//           // remarks.setBackgroundResource(R.drawable.edittext);
//        }
//    }
//
//    @Override
//    public void afterTextChanged(Editable editable) {
//    }
}
