package com.arteriatech.ss.msecsales.rspl.retailerapproval;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 29-05-2018.
 */

class CustomerHeaderVH extends RecyclerView.ViewHolder {
    public TextView tvHeader;

    public CustomerHeaderVH(View itemView) {
        super(itemView);
        tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
//        tvCount = (TextView) itemView.findViewById(R.id.tvCount);
    }
}
