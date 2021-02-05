package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.arteriatech.ss.msecsales.rspl.R;

/**
 * Created by e10769 on 04-07-2017.
 */

public class SyncSelectionVH extends RecyclerView.ViewHolder {
    public CheckBox cbCollection;
    public SyncSelectionVH(View itemView) {
        super(itemView);
        cbCollection = (CheckBox)itemView.findViewById(R.id.cbCollection);
    }
}
