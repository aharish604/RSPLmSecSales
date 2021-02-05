package com.arteriatech.ss.msecsales.rspl.expenselist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class ExpenseListVH extends RecyclerView.ViewHolder {
    public TextView textViewExpenseTypeDesc, textViewAmount, textViewExpenseNumber, textViewExpenseDate;



    public ExpenseListVH(View itemView) {
        super(itemView);
        textViewExpenseTypeDesc = (TextView) itemView.findViewById(R.id.textViewExpenseTypeDesc);
        textViewAmount = (TextView) itemView.findViewById(R.id.expenseAmount);
        textViewExpenseNumber = (TextView) itemView.findViewById(R.id.textViewExpenseNumber);
        textViewExpenseDate = (TextView) itemView.findViewById(R.id.textViewExpenseDate);


    }
}