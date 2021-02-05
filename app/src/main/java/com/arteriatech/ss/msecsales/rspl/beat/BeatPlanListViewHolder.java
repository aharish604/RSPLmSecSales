package com.arteriatech.ss.msecsales.rspl.beat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 29-06-2017.
 */

public class BeatPlanListViewHolder extends RecyclerView.ViewHolder {
    public TextView tvRetailerName, tv_retailer_mob_no, tvRetailerCatTypeDesc, tv_status_color, tv_down_color, tv_address2;
    public ImageView ivMobileNo, iv_expand_icon,iv_location;
    public ConstraintLayout detailsLayout, mainLayout,cl_visit_status;
    public TextView tvName, tvGrp3;
    public ConstraintLayout cl_visith_status;



    public BeatPlanListViewHolder(View itemView) {
        super(itemView);
        tvRetailerName = (TextView) itemView.findViewById(R.id.tv_RetailerName);
        ivMobileNo = (ImageView) itemView.findViewById(R.id.iv_mobile);
        iv_location = (ImageView) itemView.findViewById(R.id.iv_location);
        tv_retailer_mob_no = (TextView) itemView.findViewById(R.id.tv_retailer_mob_no);
        detailsLayout = (ConstraintLayout) itemView.findViewById(R.id.detailsLayout);
        cl_visit_status = (ConstraintLayout) itemView.findViewById(R.id.cl_visit_status);
        cl_visith_status = (ConstraintLayout) itemView.findViewById(R.id.cl_visith_status);
        mainLayout = (ConstraintLayout) itemView.findViewById(R.id.mainLayout);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvGrp3 = (TextView) itemView.findViewById(R.id.tvGrp3);
        tv_address2 = (TextView) itemView.findViewById(R.id.tv_address2);
    }
}
