package com.arteriatech.ss.msecsales.rspl.home.dashboard;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class VisitBestVH extends RecyclerView.ViewHolder {

    TextView tv_beat_name,tv_Visited,tv_not_visited,tv_total_retail;
    LinearLayout status;
    public VisitBestVH(View itemView) {
        super(itemView);
        tv_beat_name = (TextView) itemView.findViewById(R.id.tv_beat_name);
        tv_Visited = (TextView) itemView.findViewById(R.id.tv_Visited);
        tv_not_visited = (TextView) itemView.findViewById(R.id.tv_not_visited);
        tv_total_retail = (TextView) itemView.findViewById(R.id.tv_total_retail);
        status = (LinearLayout) itemView.findViewById(R.id.status);
    }
}
