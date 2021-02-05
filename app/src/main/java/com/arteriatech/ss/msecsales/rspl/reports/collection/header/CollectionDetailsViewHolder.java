package com.arteriatech.ss.msecsales.rspl.reports.collection.header;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 02-12-2017.
 */

public class CollectionDetailsViewHolder extends RecyclerView.ViewHolder {
    public final ImageView iv_coll_status;
    public final TextView tvBillNo, tvBillDate, tvInvAmt;
    public ConstraintLayout clItem,clOrder;
    public TextView tv_coll_det_paid_amt,tv_coll_total_paid_amt,tv_coll_det_bal_amt_ex, tvPayable, tvDiscountPer, tvDiscountPerAmt, tvNetPayable;

    public CollectionDetailsViewHolder(View viewItem, Context mContext) {
        super(viewItem);
        iv_coll_status = (ImageView)viewItem.findViewById(R.id.iv_coll_status);
        tvBillNo = (TextView)viewItem.findViewById(R.id.tvBillNo);
        tvBillDate = (TextView)viewItem.findViewById(R.id.tvBillDate);
        tvInvAmt = (TextView)viewItem.findViewById(R.id.tvInvAmt);
        tv_coll_det_paid_amt = (TextView)viewItem.findViewById(R.id.tv_coll_det_paid_amt_ex);
        tv_coll_total_paid_amt = (TextView)viewItem.findViewById(R.id.tv_coll_total_paid_amt);
        tv_coll_det_bal_amt_ex = (TextView)viewItem.findViewById(R.id.tv_coll_det_bal_amt_ex);
        tvPayable = (TextView)viewItem.findViewById(R.id.tv_coll_det_payable);
        tvDiscountPer = (TextView)viewItem.findViewById(R.id.tvDiscountPer);
        tvDiscountPerAmt = (TextView)viewItem.findViewById(R.id.tv_coll_det_perAmt);
        tvNetPayable = (TextView)viewItem.findViewById(R.id.tv_coll_det_net_payable);
    }
}
