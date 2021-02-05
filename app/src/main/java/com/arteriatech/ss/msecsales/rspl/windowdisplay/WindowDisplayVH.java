package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class WindowDisplayVH extends RecyclerView.ViewHolder {

    public final TextView tvSchemeName;
    public final TextView schem_id;
    public final LinearLayout llStatusColor;


    public WindowDisplayVH(View view) {
        super(view);
            tvSchemeName = (TextView)view.findViewById(R.id.schem_name);
        schem_id = (TextView)view.findViewById(R.id.schem_id);
            llStatusColor = (LinearLayout)view.findViewById(R.id.ll_status_color);
    }
}
