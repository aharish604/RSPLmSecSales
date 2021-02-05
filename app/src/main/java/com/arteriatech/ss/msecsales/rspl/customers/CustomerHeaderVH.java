package com.arteriatech.ss.msecsales.rspl.customers;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 29-05-2018.
 */

class CustomerHeaderVH extends RecyclerView.ViewHolder {
    public TextView tvHeader,tvLastVistdate;
    public TextView tv_total_ret_val,tv_Visited_ret_val,tv_non_visited_ret_val,tv_prod_ret_val,tv_non_prod_ret_val,tv_no_order_ret_val;
    LinearLayout llBeatOpening;
    public CustomerHeaderVH(View itemView) {
        super(itemView);
        tvHeader = (TextView) itemView.findViewById(R.id.tvHeader);
        tvLastVistdate = (TextView) itemView.findViewById(R.id.tvLastVistdate);
        tv_total_ret_val = (TextView) itemView.findViewById(R.id.tv_total_ret_val);
        tv_Visited_ret_val = (TextView) itemView.findViewById(R.id.tv_Visited_ret_val);
        tv_non_visited_ret_val = (TextView) itemView.findViewById(R.id.tv_non_visited_ret_val);
        tv_prod_ret_val = (TextView) itemView.findViewById(R.id.tv_prod_ret_val);
        tv_non_prod_ret_val = (TextView) itemView.findViewById(R.id.tv_non_prod_ret_val);
        tv_no_order_ret_val = (TextView) itemView.findViewById(R.id.tv_no_order_ret_val);
        llBeatOpening = (LinearLayout) itemView.findViewById(R.id.llBeatOpening);
//        tvCount = (TextView) itemView.findViewById(R.id.tvCount);
    }
}
