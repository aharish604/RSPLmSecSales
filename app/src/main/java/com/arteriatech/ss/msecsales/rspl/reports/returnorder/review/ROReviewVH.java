package com.arteriatech.ss.msecsales.rspl.reports.returnorder.review;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

class ROReviewVH extends RecyclerView.ViewHolder {
    public final TextView tvMaterialDesc, tvReason, tvQty, tvMrp, tvBatch,tvInvQty,tvInvNo;

    public ROReviewVH(View view) {
        super(view);
        tvMaterialDesc = (TextView)view.findViewById(R.id.tvMaterialDesc);
        tvReason = (TextView)view.findViewById(R.id.tvReason);
        tvQty = (TextView)view.findViewById(R.id.tvQty);
        tvMrp = (TextView)view.findViewById(R.id.tvMRP);
        tvBatch = (TextView)view.findViewById(R.id.tvBatch);
        tvInvQty = (TextView) view.findViewById(R.id.tvInvQty);
        tvInvNo = (TextView) view.findViewById(R.id.tvInvNo);
    }
}
