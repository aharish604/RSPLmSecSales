package com.arteriatech.ss.msecsales.rspl.visitsummaryreport;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 20-12-2017.
 */

 public class VisitSummaryReportVH extends RecyclerView.ViewHolder {
    public TextView tv_bp_name, tv_total_beat_val, tv_Visited_beat_val, tv_non_beat_val, tv_total_ret_val, tv_visited_ret_val, tv_non_visited_ret_val;

    public VisitSummaryReportVH(View viewItem) {
        super(viewItem);
        tv_bp_name = (TextView)viewItem.findViewById(R.id.tv_bp_name);
        tv_total_beat_val = (TextView)viewItem.findViewById(R.id.tv_total_beat_val);
        tv_Visited_beat_val = (TextView)viewItem.findViewById(R.id.tv_Visited_beat_val);
        tv_non_beat_val = (TextView)viewItem.findViewById(R.id.tv_non_beat_val);
        tv_total_ret_val = (TextView)viewItem.findViewById(R.id.tv_total_ret_val);
        tv_visited_ret_val = (TextView)viewItem.findViewById(R.id.tv_visited_ret_val);
        tv_non_visited_ret_val = (TextView)viewItem.findViewById(R.id.tv_non_visited_ret_val);


    }
}
