package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.arteriatech.ss.msecsales.rspl.R;

public class AddROCreateVH extends RecyclerView.ViewHolder {
    CheckBox cbMaterial;

    public AddROCreateVH(View view) {
        super(view);
        cbMaterial = (CheckBox) view.findViewById(R.id.cbMaterial);
    }
}
