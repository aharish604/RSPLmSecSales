package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;

public class PendingCountViewHolder extends RecyclerView.ViewHolder {
    //
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

        public TextView tvPendingStatus;
        public TextView tvPendingCount;
        public TextView tvEntityName;
        public TextView tvSyncTime;
        public ImageView ivUploadDownload;

        public PendingCountViewHolder(View itemView) {
            super(itemView);
            tvPendingStatus = (TextView)itemView.findViewById(R.id.tvPendingStatus);
            tvPendingCount = (TextView)itemView.findViewById(R.id.tvPendingCount);
            tvEntityName = (TextView)itemView.findViewById(R.id.tvEntityName);
            tvSyncTime = (TextView)itemView.findViewById(R.id.tvSyncTime);
            ivUploadDownload = (ImageView)itemView.findViewById(R.id.ivUploadDownload);
        }


}
