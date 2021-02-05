package com.arteriatech.ss.msecsales.rspl.behaviourlist;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10860 on 4/17/2018.
 */

public class UnbilledVH extends RecyclerView.ViewHolder   {
        public TextView tvRetailerName, tv_retailer_mob_no, tvRetailerCatTypeDesc, tv_status_color, tv_down_color, tv_address2;
        public ImageView ivMobileNo, iv_expand_icon;
        public ConstraintLayout detailsLayout, mainLayout;
        public TextView tvName;


        public UnbilledVH(View itemView) {
            super(itemView);
            tvRetailerName = (TextView) itemView.findViewById(R.id.tv_RetailerName);
            ivMobileNo = (ImageView) itemView.findViewById(R.id.iv_mobile);
            tv_retailer_mob_no = (TextView) itemView.findViewById(R.id.tv_retailer_mob_no);
            detailsLayout = (ConstraintLayout) itemView.findViewById(R.id.detailsLayout);
            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.mainLayout);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tv_address2 = (TextView) itemView.findViewById(R.id.tv_address2);
        }
}
