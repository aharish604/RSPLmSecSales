package com.arteriatech.ss.msecsales.rspl.beat;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 02-12-2017.
 */

public class SODetailsViewHolder extends RecyclerView.ViewHolder {
    public final ImageView ivDelvStatus;
    public final TextView tvMaterialDesc, tvQty, tvAmount;
    public ConstraintLayout clItem,clOrder;
    public TextView tvOrderQtyValue,tvDelverQtyValue,tvPendingQtyValue;

    public SODetailsViewHolder(View viewItem, Context mContext) {
        super(viewItem);
        ivDelvStatus = (ImageView)viewItem.findViewById(R.id.beatDistance);
        tvMaterialDesc = (TextView)viewItem.findViewById(R.id.tvMaterialDesc);
        tvQty = (TextView)viewItem.findViewById(R.id.expenseAmount);
        tvAmount = (TextView)viewItem.findViewById(R.id.textViewExpenseTypeDesc);
        clItem = (ConstraintLayout) viewItem.findViewById(R.id.clItem);
        clOrder = (ConstraintLayout) viewItem.findViewById(R.id.clOrder);
        tvDelverQtyValue= (TextView) viewItem.findViewById(R.id.tvDelverQtyValue);
        tvPendingQtyValue= (TextView) viewItem.findViewById(R.id.tvPendingQtyValue);
    }
}
