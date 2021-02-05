package com.arteriatech.ss.msecsales.rspl.retailerStockEntry.add;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.arteriatech.ss.msecsales.rspl.R;

public class RetailerStockVH extends RecyclerView.ViewHolder {
    public CheckBox checkBoxMaterial;
    public RetailerStockVH(View itemView) {
        super(itemView);
        checkBoxMaterial = (CheckBox)itemView.findViewById(R.id.checkBoxMaterial);
    }
}
