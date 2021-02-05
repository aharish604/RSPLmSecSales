package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.arteriatech.ss.msecsales.rspl.R;

public class ExpenseImageHeaderViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivThumb;

    public ExpenseImageHeaderViewHolder(View itemView) {
        super(itemView);
        ivThumb = (ImageView) itemView.findViewById(R.id.imageView);
        itemView.setClickable(true);
    }
}
