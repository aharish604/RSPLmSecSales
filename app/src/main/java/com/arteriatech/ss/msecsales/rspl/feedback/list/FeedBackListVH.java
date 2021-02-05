package com.arteriatech.ss.msecsales.rspl.feedback.list;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

class FeedBackListVH extends RecyclerView.ViewHolder {
    TextView tvFeedBackNo, tvFeedBackType, tvDate;

    public FeedBackListVH(View view) {
        super(view);
        tvFeedBackType = (TextView) view.findViewById(R.id.tvFeedBackType);
        tvFeedBackNo = (TextView) view.findViewById(R.id.tvFeedBackNo);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
    }
}
