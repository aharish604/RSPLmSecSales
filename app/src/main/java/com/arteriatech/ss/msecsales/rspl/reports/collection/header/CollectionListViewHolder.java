package com.arteriatech.ss.msecsales.rspl.reports.collection.header;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 29-06-2017.
 */

public class CollectionListViewHolder extends RecyclerView.ViewHolder {
    public TextView /*textViewOrderID,*/textViewCollDate,textViewCollNo,textViewCollAmt,textViewMaterialName,textViewCollType,textViewPaymentMode,tvDueDays;
    public CollectionListViewHolder(View itemView) {
        super(itemView);
//        textViewOrderID = (TextView) itemView.findViewById(R.id.tv_order_id);
        textViewCollDate = (TextView) itemView.findViewById(R.id.tv_inv_date);
        textViewCollNo = (TextView) itemView.findViewById(R.id.tv_coll_no);
        textViewCollAmt = (TextView) itemView.findViewById(R.id.tv_coll_value);
        textViewCollType= (TextView) itemView.findViewById(R.id.tv_coll_type);
        textViewPaymentMode= (TextView) itemView.findViewById(R.id.tv_payment_mode);
        tvDueDays= (TextView) itemView.findViewById(R.id.imgStatus);
    }
}
