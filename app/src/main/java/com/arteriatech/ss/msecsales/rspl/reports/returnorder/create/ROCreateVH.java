package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

class ROCreateVH extends RecyclerView.ViewHolder {
    public final TextView itemDbstkSkuDesc,tvMRP,tvInvQty,tvInvNo;
    public final MaterialDesignSpinner spSelectReason, spUomType;
    public final EditText etQty, etBatchNumber;
    public final ImageView ivLeft, ivRight;
    public ROBatchTextWatcher roBatchTextWatcher;
    public ROQtyTextWatcher roQtyTextWatcher;
    public LinearLayout viewForeground;
    public TextInputLayout tv_cust_batch_number;

    public ROCreateVH(View viewGroup, ROBatchTextWatcher roBatchTextWatcher, ROQtyTextWatcher roQtyTextWatcher) {
        super(viewGroup);
        itemDbstkSkuDesc = (TextView) viewGroup.findViewById(R.id.item_dbstk_sku_desc);
        tv_cust_batch_number = (TextInputLayout) viewGroup.findViewById(R.id.tv_cust_batch_number);
        tvMRP = (TextView) viewGroup.findViewById(R.id.tvMRP);
        tvInvQty = (TextView) viewGroup.findViewById(R.id.tvInvQty);
        tvInvNo = (TextView) viewGroup.findViewById(R.id.tvInvNo);
        spSelectReason = (MaterialDesignSpinner) viewGroup.findViewById(R.id.sp_select_reason);
        etQty = (EditText) viewGroup.findViewById(R.id.edit_quantity);
        etBatchNumber = (EditText) viewGroup.findViewById(R.id.edit_batch_number);
        viewForeground = (LinearLayout) viewGroup.findViewById(R.id.view_foreground);
        this.roBatchTextWatcher=roBatchTextWatcher;
        this.roQtyTextWatcher=roQtyTextWatcher;
        etQty.addTextChangedListener(roQtyTextWatcher);
        etBatchNumber.addTextChangedListener(roBatchTextWatcher);
        ivLeft = (ImageView) viewGroup.findViewById(R.id.ivLeft);
        ivRight = (ImageView) viewGroup.findViewById(R.id.ivRight);
        spUomType = (MaterialDesignSpinner) viewGroup.findViewById(R.id.spUomType);
    }
}
