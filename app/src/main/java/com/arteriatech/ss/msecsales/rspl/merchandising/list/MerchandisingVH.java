package com.arteriatech.ss.msecsales.rspl.merchandising.list;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

class MerchandisingVH extends RecyclerView.ViewHolder {
    public TextView tvMerchType, tvtvMerchDate;

    public MerchandisingVH(View view) {
        super(view);
        tvMerchType = (TextView)view.findViewById(R.id.tvMerchType);
        tvtvMerchDate = (TextView)view.findViewById(R.id.tvtvMerchDate);
    }
}
