package com.arteriatech.ss.msecsales.rspl.visitsummary;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10893 on 25-01-2018.
 */

public class VisitSummaryViewHolder extends RecyclerView.ViewHolder {
    public TextView tvRetName,tv_time_taken_value,tv_order_value,tv_day_target_value;
    public TextView tv_today_tlsd,tv_tlsd_till_date_val,tv_month_tgt,tv_ach_mtd,tv_mtd_per;

    public VisitSummaryViewHolder(View itemView) {
        super(itemView);
        tvRetName = (TextView) itemView.findViewById(R.id.tvRetName);
        tv_time_taken_value = (TextView) itemView.findViewById(R.id.tv_time_taken_value);
        tv_order_value = (TextView) itemView.findViewById(R.id.tv_order_value);
        tv_day_target_value = (TextView) itemView.findViewById(R.id.tv_day_target_value);
        tv_today_tlsd = (TextView) itemView.findViewById(R.id.tv_today_tlsd);
        tv_tlsd_till_date_val = (TextView) itemView.findViewById(R.id.tv_tlsd_till_date_val);
        tv_month_tgt = (TextView) itemView.findViewById(R.id.tv_month_tgt);
        tv_ach_mtd = (TextView) itemView.findViewById(R.id.tv_ach_mtd);
        tv_mtd_per = (TextView) itemView.findViewById(R.id.tv_mtd_per);
    }
}
