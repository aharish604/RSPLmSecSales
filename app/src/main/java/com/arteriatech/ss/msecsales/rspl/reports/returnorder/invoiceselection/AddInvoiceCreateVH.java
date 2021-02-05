package com.arteriatech.ss.msecsales.rspl.reports.returnorder.invoiceselection;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class AddInvoiceCreateVH extends RecyclerView.ViewHolder {
    CheckBox cbMaterial;
    TextView tvInvNumber,textViewMatName,textViewInvoiceDate;

    public AddInvoiceCreateVH(View view) {
        super(view);
        cbMaterial = (CheckBox) view.findViewById(R.id.cbMaterial);
        tvInvNumber = (TextView) view.findViewById(R.id.textViewInvoiceNumber);
        textViewMatName = (TextView) view.findViewById(R.id.textViewMatName);
        textViewInvoiceDate = (TextView) view.findViewById(R.id.textViewInvoiceDate);
    }
}
