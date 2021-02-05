package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.ui.EditextClearButton;


class StockMultiMaterialVH extends RecyclerView.ViewHolder {
    ImageView ivLeft, ivRight;
    ConstraintLayout viewForeground;
    RelativeLayout viewBackground;
    //    ImageView ivSelectedImg;
    TextView tvMatDesc, tvLandingPrice, tvUom, tvAsOnDate;
    LinearLayout clView;
    EditextClearButton etQty;
    Button btAdd;//, tvPlus, tvMinus;
    RetailerStockCrtQtyTxtWtchr qtyTextWatcher;

    public StockMultiMaterialVH(View viewItem, RetailerStockCrtQtyTxtWtchr soCreateQtyTextWatcher) {
        super(viewItem);
//        ivSelectedImg = (ImageView) viewItem.findViewById(R.id.ivSelectedImg);
        tvMatDesc = (TextView) viewItem.findViewById(R.id.tvMatDesc);
        tvLandingPrice = (TextView) viewItem.findViewById(R.id.tvLandingPrice);
//        tvMatId = (TextView) viewItem.findViewById(R.id.tvMatId);
//        tvPlus = (Button) viewItem.findViewById(R.id.tvPlus);
//        tvQty = (TextView) viewItem.findViewById(R.id.tvQty);
//        tvMinus = (Button) viewItem.findViewById(R.id.tvMinus);
        tvUom = (TextView) viewItem.findViewById(R.id.tvUom);
        tvAsOnDate = (TextView) viewItem.findViewById(R.id.tvAsOnDate);
        etQty = (EditextClearButton) viewItem.findViewById(R.id.etQty);
        btAdd = (Button) viewItem.findViewById(R.id.btAdd);
        viewBackground = (RelativeLayout) viewItem.findViewById(R.id.view_background);
        viewForeground = (ConstraintLayout) viewItem.findViewById(R.id.view_foreground);
        ivLeft = (ImageView) viewItem.findViewById(R.id.ivLeft);
        ivRight = (ImageView) viewItem.findViewById(R.id.ivRight);

//        etQty.setClearButtonMode();
//        tilQty = (TextInputLayout)viewItem.findViewById(R.id.tilQty);
//        clViewMT = (ConstraintLayout) viewItem.findViewById(R.id.clViewMT);
//        clViewPC = (LinearLayout) viewItem.findViewById(R.id.clViewPC);
        clView = (LinearLayout) viewItem.findViewById(R.id.clView);
        this.qtyTextWatcher = soCreateQtyTextWatcher;
        etQty.addTextChangedListener(soCreateQtyTextWatcher);
        setIsRecyclable(false);

    }
}
