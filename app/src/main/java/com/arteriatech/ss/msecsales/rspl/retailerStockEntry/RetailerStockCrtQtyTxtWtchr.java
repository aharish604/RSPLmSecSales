package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;


public class RetailerStockCrtQtyTxtWtchr implements TextWatcher {
    private RetailerStockBean stockBean = null;
    private RecyclerView.ViewHolder holder = null;
    private RetailerStkCrtQtyTxtWtchrInterface createQtyTextWatcherInterface = null;

    public RetailerStockCrtQtyTxtWtchr() {

    }

    public void updateTextWatcher(RetailerStockBean stockBean, RecyclerView.ViewHolder holder, RetailerStkCrtQtyTxtWtchrInterface createQtyTextWatcherInterface) {
        this.stockBean = stockBean;
        this.holder = holder;
        this.createQtyTextWatcherInterface = createQtyTextWatcherInterface;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (stockBean != null) {
            stockBean.setEnterdQty(s.toString());
        }
        if (createQtyTextWatcherInterface != null) {
            createQtyTextWatcherInterface.onTextChange(s.toString(), stockBean, holder);
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

}
