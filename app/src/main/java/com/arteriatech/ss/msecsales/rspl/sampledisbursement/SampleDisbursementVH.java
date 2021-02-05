package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.ui.CustomAutoComplete;
import com.arteriatech.ss.msecsales.rspl.ui.EditextClearButton;

public class SampleDisbursementVH extends RecyclerView.ViewHolder{
    public  TextView tvMaterialDesc;
    public  ImageButton ibDelete;
    public Button btAdd;
    public  EditText etRemarks;
    public TextView tvMaterailName, tvDBStock;
    public EditextClearButton edMaterialQty;
    public SampleDisbursementRetailerStockCrtQtyTxtWtchr qtyTextWatcher;
    public SDRemarksTextWatcher remarksTextWatcher;
    ConstraintLayout viewForeground;
    ImageView ivLeft, ivRight;
    public CustomAutoComplete autUOM=null;
    public SampleDisbursementVH(View itemView,SampleDisbursementRetailerStockCrtQtyTxtWtchr qtyTextWatcher,SDRemarksTextWatcher remarksTextWatcher) {
        super(itemView);
        tvMaterailName = (TextView) itemView.findViewById(R.id.tv_material_name);
        tvDBStock = (TextView) itemView.findViewById(R.id.tv_db_stk);
        //tvMaterialDesc = (TextView) itemView.findViewById(R.id.tv_material_desc);
        edMaterialQty = (EditextClearButton) itemView.findViewById(R.id.et_material_qty);
        etRemarks = (EditText) itemView.findViewById(R.id.et_remarks);
        viewForeground = (ConstraintLayout) itemView.findViewById(R.id.view_foreground);
        btAdd = (Button) itemView.findViewById(R.id.btAdd);
        ivLeft = (ImageView) itemView.findViewById(R.id.ivLeft);
        ivRight = (ImageView) itemView.findViewById(R.id.ivRight);
        autUOM = (CustomAutoComplete) itemView.findViewById(R.id.autUOM);

        //    ibDelete = (ImageButton) itemView.findViewById(R.id.ib_delete_item);

        this.qtyTextWatcher = qtyTextWatcher;
        this.remarksTextWatcher = remarksTextWatcher;
        edMaterialQty.addTextChangedListener(qtyTextWatcher);
        etRemarks.addTextChangedListener(remarksTextWatcher);
    }
}
