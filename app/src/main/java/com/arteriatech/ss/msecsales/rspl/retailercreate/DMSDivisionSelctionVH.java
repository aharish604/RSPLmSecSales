package com.arteriatech.ss.msecsales.rspl.retailercreate;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10526 on 03-08-2018.
 *
 */

public class DMSDivisionSelctionVH extends RecyclerView.ViewHolder {
    ImageView ivLeft, ivRight,iv_expand_div_val;
    LinearLayout viewForeground;
    RelativeLayout viewBackground;
    TextView tvDmsDivisionDesc,tvCreditLimit,tvDiscountPer,tvNoOfDays,textViewNoOfBills;
    TextView tvGroupTwoVal,tvGroupOneVal,tvGroupThreeVal,tvGroupFourVal,tvGroupFiveVal;
    LinearLayout detailsLayout,ll_line_div;

    public DMSDivisionSelctionVH(View viewItem) {
        super(viewItem);
        tvDmsDivisionDesc = (TextView) viewItem.findViewById(R.id.tvDmsDivisionDesc);
        tvCreditLimit = (TextView) viewItem.findViewById(R.id.tvCreditLimit);
        tvDiscountPer = (TextView) viewItem.findViewById(R.id.tvDiscountPer);
        tvNoOfDays = (TextView) viewItem.findViewById(R.id.tvNoOfDays);
        textViewNoOfBills = (TextView) viewItem.findViewById(R.id.textViewNoOfBills);

        tvGroupOneVal = (TextView) viewItem.findViewById(R.id.tvGroupOneVal);
        tvGroupTwoVal = (TextView) viewItem.findViewById(R.id.tvGroupTwoVal);
        tvGroupThreeVal = (TextView) viewItem.findViewById(R.id.tvGroupThreeVal);
        tvGroupFourVal = (TextView) viewItem.findViewById(R.id.tvGroupFourVal);
        tvGroupFiveVal = (TextView) viewItem.findViewById(R.id.tvGroupFiveVal);

        detailsLayout = (LinearLayout) itemView.findViewById(R.id.detailsLayout);
        ll_line_div = (LinearLayout) itemView.findViewById(R.id.ll_line_div);

        viewBackground = (RelativeLayout) viewItem.findViewById(R.id.view_background);
        viewForeground = (LinearLayout) viewItem.findViewById(R.id.view_foreground);
        ivLeft = (ImageView) viewItem.findViewById(R.id.ivLeft);
        ivRight = (ImageView) viewItem.findViewById(R.id.ivRight);
        iv_expand_div_val = (ImageView) viewItem.findViewById(R.id.iv_expand_div_val);
        setIsRecyclable(false);

    }
}
