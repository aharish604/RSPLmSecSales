package com.arteriatech.ss.msecsales.rspl.visualaid;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class VisualAidVH extends RecyclerView.ViewHolder {

    public ImageView ivThumbNail, ivDataFormat;
    public TextView tvName, tvDate;

    public VisualAidVH(View view) {
        super(view);
        ivThumbNail = (ImageView) view.findViewById(R.id.ivThumbNail);
        ivDataFormat = (ImageView) view.findViewById(R.id.ivDataFormat);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
    }
}
