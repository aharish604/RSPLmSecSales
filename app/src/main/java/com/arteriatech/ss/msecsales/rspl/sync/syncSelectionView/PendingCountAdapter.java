package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;

import java.util.List;

public class PendingCountAdapter extends RecyclerView.Adapter<PendingCountViewHolder> {
    private List<PendingCountBean> pendingCountBeansList;
    private Context context;
    private CollectionSyncInterface collectionSyncInterface=null;
    private String syncType = "";

    public PendingCountAdapter(List<PendingCountBean> pendingCountBeansList, Context context, CollectionSyncInterface collectionSyncInterface) {
        this.pendingCountBeansList = pendingCountBeansList;
        this.context = context;
        this.collectionSyncInterface=collectionSyncInterface;
    }

    public PendingCountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pendingcount_list, parent, false);
        return new PendingCountViewHolder(view);
    }

    public void onBindViewHolder(PendingCountViewHolder holder, int position) {
        final PendingCountBean countBean = (PendingCountBean) pendingCountBeansList.get(position);
        if(countBean.getCount()>0){
            holder.tvPendingStatus.setBackgroundColor(context.getResources().getColor(R.color.RejectedColor));
        }else {
            holder.tvPendingStatus.setBackgroundColor(context.getResources().getColor(R.color.ApprovedColor));
        }
        holder.tvEntityName.setText(countBean.getCollection());
        holder.tvPendingCount.setText(""+countBean.getCount());
        holder.tvSyncTime.setText(""+countBean.getSyncTime());
//        holder.ivUploadDownload.setText("UPLOAD");
        holder.ivUploadDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collectionSyncInterface!=null){
                    syncType = Constants.DownLoad;
                    collectionSyncInterface.onUploadDownload(false,countBean,syncType);
                }
            }
        });
    }

    public int getItemCount() {
        return pendingCountBeansList.size();
    }
}
