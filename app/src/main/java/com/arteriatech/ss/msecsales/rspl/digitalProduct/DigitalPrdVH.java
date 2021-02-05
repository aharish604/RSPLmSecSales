package com.arteriatech.ss.msecsales.rspl.digitalProduct;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

class DigitalPrdVH extends RecyclerView.ViewHolder {
    public ImageView ivThumbNail;
    public TextView tvName;

    public DigitalPrdVH(View view) {
        super(view);
        ivThumbNail= (ImageView)view.findViewById(R.id.ivThumbNail);
        tvName= (TextView)view.findViewById(R.id.tvName);
    }
}
