package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class WindowImageDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<ExpenseImageBean> imageBeanArrayList;
    private int ITEM_TYPE_HEADER=1;
    private int ITEM_TYPE_NORMAL=2;
    OnLongClickInterFace onClickInterface;

    public WindowImageDisplayAdapter(Context mContext, ArrayList<ExpenseImageBean> imageBeanList, OnLongClickInterFace onClickInterface) {
        this.mContext=mContext;
        this.onClickInterface=onClickInterface;
        this.imageBeanArrayList=imageBeanList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_window_image_item, null);
            return new ExpenseImageViewHolder(normalView);
        }
        else {
            View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_window_header_item, null);
            return new ExpenseImageHeaderViewHolder(headerRow);
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ExpenseImageViewHolder) {
            final ExpenseImageBean expenseImageBean = imageBeanArrayList.get(position);
            if(!expenseImageBean.isImageFromMedia()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(expenseImageBean.getImagePath(), options);
                ((ExpenseImageViewHolder) holder).ivThumb.setImageBitmap(bitmap);
                ((ExpenseImageViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(expenseImageBean.getImagePath());
                            Bitmap bm = BitmapFactory.decodeStream(fis);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
                            byte[] imgBytes = baos.toByteArray();
                            ConstantsUtils.openImageInDialogBox(mContext,imgBytes);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                });
                ((ExpenseImageViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(onClickInterface!=null){
                            onClickInterface.onLongClickInterFace(v,position,((ExpenseImageViewHolder) holder).ivThumb);
                        }
                        return true;
                    }
                });
            }else {
                try {
                    final byte[] imgByteArray = OfflineManager.getImageList(expenseImageBean.getImagePath());
                    ((ExpenseImageViewHolder)holder).ivThumb.setImageBitmap(BitmapFactory.decodeByteArray(imgByteArray, 0,
                            imgByteArray.length));
                    ((ExpenseImageViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConstantsUtils.openImageInDialogBox(mContext,imgByteArray);
                        }
                    });
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        }else if(holder instanceof ExpenseImageHeaderViewHolder){
            ((ExpenseImageHeaderViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickInterface!=null){
                        onClickInterface.onItemClick(v,position);
                    }
                    else
                    {
                        Log.d("View not Inialize","View not Inialize");
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        ExpenseImageBean expenseImageBean = imageBeanArrayList.get(position);
        if (expenseImageBean.getImagePath().equals("")) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }


    @Override
    public int getItemCount() {
        return imageBeanArrayList.size();
    }

}
