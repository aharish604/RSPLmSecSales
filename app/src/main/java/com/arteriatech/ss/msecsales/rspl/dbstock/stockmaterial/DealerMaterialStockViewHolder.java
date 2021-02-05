package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


@SuppressWarnings("all")
public class DealerMaterialStockViewHolder extends RecyclerView.ViewHolder {
   TextView textViewSKUGroup,textViewDBStock;
    public DealerMaterialStockViewHolder(View itemView) {
        super(itemView);
        textViewSKUGroup = (TextView) itemView.findViewById(R.id.textViewSKUGroup);
        textViewDBStock = (TextView) itemView.findViewById(R.id.textViewDBStock);
    }
}
