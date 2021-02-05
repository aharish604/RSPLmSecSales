package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 02-12-2017.
 */

public class OutStndingVH extends RecyclerView.ViewHolder {
    public final ImageView ivDelvStatus;
    public final TextView tvMaterialDesc, tvQty, tvAmount;

    public OutStndingVH(View viewItem, Context mContext) {
        super(viewItem);
        ivDelvStatus = (ImageView)viewItem.findViewById(R.id.beatDistance);
        tvMaterialDesc = (TextView)viewItem.findViewById(R.id.tvMaterialDesc);
        tvQty = (TextView)viewItem.findViewById(R.id.expenseAmount);
        tvAmount = (TextView)viewItem.findViewById(R.id.textViewExpenseTypeDesc);
    }
}
