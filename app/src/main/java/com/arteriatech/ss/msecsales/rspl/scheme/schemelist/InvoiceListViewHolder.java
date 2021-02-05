package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 29-06-2017.
 */

public class InvoiceListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewMaterialName,textViewQuantity,textViewInvoiceNumber,textViewInvoiceDate,textViewInvoiceAmount;
    public ImageView imageViewInvoiceStatus;


    public InvoiceListViewHolder(View itemView) {
        super(itemView);
        textViewMaterialName = (TextView) itemView.findViewById(R.id.textViewExpenseTypeDesc);
        textViewQuantity = (TextView) itemView.findViewById(R.id.expenseAmount);
        textViewInvoiceNumber = (TextView) itemView.findViewById(R.id.textViewExpenseNumber);
        textViewInvoiceDate = (TextView) itemView.findViewById(R.id.textViewExpenseTypeDesc);
        textViewInvoiceAmount = (TextView) itemView.findViewById(R.id.textViewInvoiceAmount);
        imageViewInvoiceStatus = (ImageView) itemView.findViewById(R.id.beatDistance);

    }
}
