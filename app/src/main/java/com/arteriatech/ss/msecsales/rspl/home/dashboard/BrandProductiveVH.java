package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class BrandProductiveVH extends RecyclerView.ViewHolder {
    ImageView ivBrandStatus;
    TextView tv_productive,tv_nug_value,tv_total_value;
    ProgressBar pbCount;
    public BrandProductiveVH(View itemView) {
        super(itemView);
        tv_productive = (TextView) itemView.findViewById(R.id.tv_productive);
        tv_nug_value = (TextView)itemView.findViewById(R.id.tv_nug_value);
        tv_total_value = (TextView)itemView.findViewById(R.id.tv_total_value);
        ivBrandStatus = (ImageView)itemView.findViewById(R.id.ivBrandStatus);
        pbCount = (ProgressBar)itemView.findViewById(R.id.pbCount);
    }
}
