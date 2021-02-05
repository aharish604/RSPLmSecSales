package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class ExpenseListDetailseVH extends RecyclerView.ViewHolder {
    public TextView beatDistance, expenseAmount, location, convey, expenseItemType;
    public ImageView imageViewInvoiceStatus;

    public ExpenseListDetailseVH(View itemView) {
        super(itemView);
        beatDistance = (TextView) itemView.findViewById(R.id.beatDistance);
        expenseAmount = (TextView) itemView.findViewById(R.id.expenseAmount);
        location = (TextView) itemView.findViewById(R.id.location);
        convey = (TextView) itemView.findViewById(R.id.textViewExpenseTypeDesc);
        expenseItemType = (TextView) itemView.findViewById(R.id.textViewExpenseNumber);

    }
}