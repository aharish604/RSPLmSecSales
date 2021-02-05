package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 16-05-2018.
 */

class DBStockDetailVH extends RecyclerView.ViewHolder {
    TextView tvMaterialDesc, tvQty, tvAmount, tvBrand, tvMfd, tvMrp;

    public DBStockDetailVH(View view) {
        super(view);
        tvMaterialDesc = (TextView)view.findViewById(R.id.tvMaterialDesc);
        tvQty = (TextView)view.findViewById(R.id.expenseAmount);
        tvAmount = (TextView)view.findViewById(R.id.textViewExpenseTypeDesc);
        tvBrand = (TextView)view.findViewById(R.id.tvBrand);
        tvMfd = (TextView)view.findViewById(R.id.tvMfd);
        tvMrp = (TextView)view.findViewById(R.id.tvMrp);

    }
}
