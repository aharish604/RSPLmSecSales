package com.arteriatech.ss.msecsales.rspl.behaviourlist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10893 on 25-01-2018.
 */

public class BehaviourListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewRetailerName,textViewCustomerID,textViewMtdValue;

    public BehaviourListViewHolder(View itemView) {
        super(itemView);
        textViewRetailerName = (TextView) itemView.findViewById(R.id.textViewRetailerName);
        textViewCustomerID = (TextView) itemView.findViewById(R.id.textViewCustomerID);
        textViewMtdValue = (TextView) itemView.findViewById(R.id.textViewMTDValue);

    }
}
