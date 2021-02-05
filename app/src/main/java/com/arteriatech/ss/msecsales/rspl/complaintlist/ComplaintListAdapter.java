package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;

import java.util.ArrayList;

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.ViewHolder>{

    Context context;
    Bundle bundleUserDetails;
    ArrayList<ComplaintListModel> alComplaintListModel;
    public ComplaintListAdapter(Context context, ArrayList<ComplaintListModel> alComplaintListModel, Bundle bundleUserDetails) {
        this.alComplaintListModel = alComplaintListModel;
        this.context = context;
        this.bundleUserDetails = bundleUserDetails;
    }
    @Override
    public ComplaintListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaintlist_detail, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ComplaintListAdapter.ViewHolder holder, int position) {
        try {

            final ComplaintListModel complaintListModel = alComplaintListModel.get(position);
            String complaintNo=complaintListModel.getComplaintNo();
            String complaintnoWithoutPreceedingZeros= UtilConstants.removeLeadingZeros(complaintNo);
            holder.complaintno.setText(complaintnoWithoutPreceedingZeros);
            holder.complaintType.setText(complaintListModel.getComplainCategoryDesc());
            holder.date.setText(complaintListModel.getComplaintDate());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context,ComplaintListDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.COMPLAINTLISTMODEL,complaintListModel);
                    intent.putExtra("UserDetial",bundleUserDetails);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e){
            e.printStackTrace();}
    }

    @Override
    public int getItemCount() {

        return alComplaintListModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView date;
        TextView complaintType;
        TextView complaintno;
        ConstraintLayout layout;


        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            complaintType =(TextView) itemView.findViewById(R.id.complaintType);
            complaintno = (TextView)itemView.findViewById(R.id.complaintNumber);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout);
            layout.setClickable(true);
        }
    }
}
