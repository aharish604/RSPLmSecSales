package com.arteriatech.ss.msecsales.rspl.retailertrends;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class RetailerTrendVH extends RecyclerView.ViewHolder {


    TextView tvTrendsSKUGrp, tvM1Value, tvM2Value, tvM3Value, tvLsttreMValue, tvLysmaValue, tvCMTargValue, tvCMTDValue, tvBTDValue, tvACHDValue, tvLYGrthValue, tvM1Header, tvM2Header, tvM3Header;

    public RetailerTrendVH(View itemView) {
        super(itemView);
        tvM1Header = (TextView) itemView.findViewById(R.id.tvM1Header);
        tvM2Header = (TextView) itemView.findViewById(R.id.tvM2Header);
        tvM3Header = (TextView) itemView.findViewById(R.id.tvM3Header);
        tvTrendsSKUGrp = (TextView) itemView.findViewById(R.id.tvTrendsSKUGrp);
        tvM1Value = (TextView) itemView.findViewById(R.id.tvM1Value);
        tvM2Value = (TextView) itemView.findViewById(R.id.tvM2Value);
        tvM3Value = (TextView) itemView.findViewById(R.id.tvM3Value);
        tvLsttreMValue = (TextView) itemView.findViewById(R.id.tvLsttreMValue);
        tvLysmaValue = (TextView) itemView.findViewById(R.id.tvLysmaValue);
        tvCMTargValue = (TextView) itemView.findViewById(R.id.tvCMTargValue);
        tvCMTDValue = (TextView) itemView.findViewById(R.id.tvCMTDValue);
        tvBTDValue = (TextView) itemView.findViewById(R.id.tvBTDValue);
        tvACHDValue = (TextView) itemView.findViewById(R.id.tvACHDValue);
        tvLYGrthValue = (TextView) itemView.findViewById(R.id.tvLYGrthValue);
    }
}
