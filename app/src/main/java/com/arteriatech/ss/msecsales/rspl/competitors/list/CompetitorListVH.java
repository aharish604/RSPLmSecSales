package com.arteriatech.ss.msecsales.rspl.competitors.list;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class CompetitorListVH extends RecyclerView.ViewHolder {
    TextView tvCompetitorName, tvDate;

    public CompetitorListVH(View view) {
        super(view);
        tvCompetitorName = (TextView) view.findViewById(R.id.tvCompetitorName);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
    }
}
