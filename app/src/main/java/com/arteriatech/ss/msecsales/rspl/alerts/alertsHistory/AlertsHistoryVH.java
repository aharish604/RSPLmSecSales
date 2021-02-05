package com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10860 on 3/29/2018.
 */

public class AlertsHistoryVH extends RecyclerView.ViewHolder {
    public final ImageView ivLeft;
    public final ImageView ivRight;
    //    public final ImageView ivMobileNo;
    public final TextView tvRetailerOwner;
    public final ImageView ivDOB;
    public final ImageView ivAnversiry;
    public final ImageView ivAppointment;
    public TextView tvAlertMsg, iconText;
    //    public ConstraintLayout viewForeground;
    public CardView viewForeground;
    public View view;
    public RelativeLayout iconContainer, iconBack;

    public AlertsHistoryVH(View itemView) {
        super(itemView);
        view = itemView;
        tvAlertMsg = (TextView) itemView.findViewById(R.id.AlertMsg);
//        tvCreatedOn = (TextView) itemView.findViewById(R.id.AlertCreatedOn);
//        viewForeground = (ConstraintLayout) itemView.findViewById(R.id.cvOrderDetails);
        viewForeground = (CardView) itemView.findViewById(R.id.cvOrderDetails);
        ivLeft = (ImageView) itemView.findViewById(R.id.ivLeft);
        ivRight = (ImageView) itemView.findViewById(R.id.ivRight);
//        ll_alerts_list_sel = (LinearLayout) itemView.findViewById(R.id.ll_alerts_list_sel);

        iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
        iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
//        iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
        iconText = (TextView) view.findViewById(R.id.icon_text);
//        imgProfile = (ImageView) view.findViewById(R.id.icon_profile);

//        ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        tvRetailerOwner = (TextView) view.findViewById(R.id.tv_retailer_owner_name);
        ivDOB = (ImageView) view.findViewById(R.id.iv_dob_icon);
        ivAnversiry = (ImageView) view.findViewById(R.id.iv_anversiry_icon);
        ivAppointment = (ImageView) view.findViewById(R.id.iv_appointment_icon);
    }
}
