package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 29-06-2017.
 */

public class OutstandingListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewMaterialName,textViewQuantity,textViewInvoiceNumber,textViewInvoiceDate,textViewInvoiceAmount,textViewOutAmt;
    public TextView tvDueDays;


    public OutstandingListViewHolder(View itemView) {
        super(itemView);
        textViewMaterialName = (TextView) itemView.findViewById(R.id.textViewExpenseTypeDesc);
        textViewQuantity = (TextView) itemView.findViewById(R.id.expenseAmount);
        textViewInvoiceNumber = (TextView) itemView.findViewById(R.id.textViewExpenseNumber);
        textViewInvoiceDate = (TextView) itemView.findViewById(R.id.textViewInvoiceDate);
        textViewInvoiceAmount = (TextView) itemView.findViewById(R.id.textViewInvoiceAmount);
        tvDueDays = (TextView) itemView.findViewById(R.id.beatDistance);
        textViewOutAmt = (TextView) itemView.findViewById(R.id.textViewOutAmt);

    }
}
