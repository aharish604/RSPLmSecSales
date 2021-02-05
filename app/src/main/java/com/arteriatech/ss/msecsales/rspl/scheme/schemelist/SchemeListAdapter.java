package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeItemListBean;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeListBean;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeSalesAreaBean;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeSlabBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 27-03-2017.
 */

class SchemeListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<SchemeListBean> schemeListBeanArrayList;

    public SchemeListAdapter(Context mContext, ArrayList<SchemeListBean> schemeListBeanArrayList) {
        this.mContext = mContext;
        this.schemeListBeanArrayList = schemeListBeanArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheme_list_item_header, parent, false);
//        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheme_list_item_header_mvp, parent, false);
        viewHolder = new ViewHolderHeader(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       /* final SchemeListBean schemeListBean = schemeListBeanArrayList.get(position);
        final ViewHolderHeader viewHolderHeader = ((ViewHolderHeader) holder);

        viewHolderHeader.tvSchemeName.setText(schemeListBean.getSchemeName() + " (" + schemeListBean.getSchemeId() + ")");
//        viewHolderHeader.tvSchemeValidTo.setText(schemeListBean.getValidFrom()+"VALID TO: " +schemeListBean.getValidDate());
        viewHolderHeader.tvSchemeValidTo.setText(schemeListBean.getValidFrom() + " - " + schemeListBean.getValidDate());
        viewHolderHeader.tvSchemeType.setText(schemeListBean.getSchemeDesc());
        viewHolderHeader.tvApplicableFor.setText(schemeListBean.getSchemeApplicableFor());
        viewHolderHeader.tv_on_sales_of.setText(schemeListBean.getSchemeOnSaleOF());
        viewHolderHeader.tv_scheme_slab_per.setText(schemeListBean.getSchemeSlabRule());

        Drawable delvStatusImg = SOUtils.displaySchemeSlabStatusImage(schemeListBean.getSlabRuleID(), mContext);
        if (delvStatusImg != null) {
            viewHolderHeader.ivSchemeSlabType.setImageDrawable(delvStatusImg);
        }
        if (schemeListBean.isExpand()) {
            viewHolderHeader.btExpand.setText("COLLAPSE");
            viewHolderHeader.llMoreInfo.setVisibility(View.VISIBLE);
        } else {
            viewHolderHeader.btExpand.setText("EXPAND");
            viewHolderHeader.llMoreInfo.setVisibility(View.GONE);
        }
        viewHolderHeader.btExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderHeader.llMoreInfo.getVisibility() == View.VISIBLE) {
                    viewHolderHeader.llMoreInfo.setVisibility(View.GONE);
                    schemeListBean.setExpand(false);
                    viewHolderHeader.btExpand.setText("EXPAND");
                } else {
                    viewHolderHeader.llMoreInfo.setVisibility(View.VISIBLE);
                    schemeListBean.setExpand(true);
                    viewHolderHeader.btExpand.setText("COLLAPSE");
                }
            }
        });

        //sale of
        try {
            viewHolderHeader.llItemDetailsList.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schemeListBean.getItemListBeanArrayList() != null) {
            for (SchemeItemListBean schemeItemListBean : schemeListBean.getItemListBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sale_of, null, false);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
                tvSchemeDesc.setText(schemeItemListBean.getOnSalesDesc());
                if (!schemeItemListBean.getUOM().equalsIgnoreCase(""))
                    tvSchemeTo.setText(schemeItemListBean.getItemMin() + " " + schemeItemListBean.getUOM());
                else
                    tvSchemeTo.setText(schemeItemListBean.getItemMin());
                viewHolderHeader.llItemDetailsList.addView(salesAreaLayout);
            }
        }

        //slab
        if (schemeListBean.getSchemeSlabBeanArrayList() != null) {
            boolean isFirstTime = true;
            try {
                viewHolderHeader.llSchemeSlab.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (SchemeSlabBean schemeSlabBean : schemeListBean.getSchemeSlabBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sub_header, null, false);
                TextView tvSchemeName = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_name);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
//                if (isFirstTime) {
//                    tvSchemeName.setText(schemeListBean.getSlabTitle());
                tvSchemeName.setText(schemeSlabBean.getToQty());
//                    isFirstTime = false;
//                }
                tvSchemeDesc.setText(schemeSlabBean.getMaterialDesc());
                tvSchemeTo.setText(schemeSlabBean.getPayoutAmount() + " " + schemeSlabBean.getFreeQtyUOM());
                viewHolderHeader.llSchemeSlab.addView(salesAreaLayout);
            }
        }*/


        SchemeListBean schemeListBean = schemeListBeanArrayList.get(position);
        ViewHolderHeader viewHolderHeader = ((ViewHolderHeader) holder);

        viewHolderHeader.tvSchemeNameTitle.setText(schemeListBean.getSchemeNameTitle());
        viewHolderHeader.tvSchemeName.setText(schemeListBean.getSchemeName() + " (" + schemeListBean.getSchemeId() + ")");
        viewHolderHeader.tvSchemeValidTo.setText(schemeListBean.getValidDate());
        viewHolderHeader.tvSchemeValidFrom.setText(schemeListBean.getValidFrom());
        viewHolderHeader.tvSchemeType.setText(schemeListBean.getSchemeTypeName());
        viewHolderHeader.tvSchemeTypeDesc.setText(schemeListBean.getSchemeDesc());
//        viewHolderHeader.tvSchemeSalesOfDesc.setText(schemeListBean.getOnSaleOfCatDesc());
        viewHolderHeader.tvSchemeBenefitDesc.setText(schemeListBean.getSlabRuleDesc());
        viewHolderHeader.tvSchemeBenefitTo.setText(schemeListBean.getSlabRuleType());
        viewHolderHeader.tvSchemeSlabs.setText(schemeListBean.getSlabTitle());
        if (schemeListBean.getSalesAreaBeanArrayList() != null) {
            boolean isFirstTime = true;
            try {
                viewHolderHeader.llSalesAreaView.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (SchemeSalesAreaBean schemeSalesAreaBean : schemeListBean.getSalesAreaBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_item_applicable, null, false);
//                TextView tvSchemeName = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_name);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                View vLines = (View) salesAreaLayout.findViewById(R.id.view_line);
                if (isFirstTime) {
                    vLines.setVisibility(View.GONE);
                    isFirstTime = false;
                } else {
                    vLines.setVisibility(View.VISIBLE);
                }
                tvSchemeDesc.setText(schemeSalesAreaBean.getFinalGroupDesc());
                viewHolderHeader.llSalesAreaView.addView(salesAreaLayout);
            }
        }
        try {
            viewHolderHeader.llItemDetailsList.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schemeListBean.getItemListBeanArrayList() != null) {
            for (SchemeItemListBean schemeItemListBean : schemeListBean.getItemListBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sale_of, null, false);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
                tvSchemeDesc.setText(schemeItemListBean.getOnSalesDesc());
                if (!schemeItemListBean.getUOM().equalsIgnoreCase(""))
                    tvSchemeTo.setText(schemeItemListBean.getItemMin() + " " + schemeItemListBean.getUOM());
                else
                    tvSchemeTo.setText(schemeItemListBean.getItemMin());
                viewHolderHeader.llItemDetailsList.addView(salesAreaLayout);
            }
        }
        if (schemeListBean.getSchemeSlabBeanArrayList() != null) {
            boolean isFirstTime = true;
            try {
                viewHolderHeader.llSchemeSlab.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (SchemeSlabBean schemeSlabBean : schemeListBean.getSchemeSlabBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sub_header, null, false);
                TextView tvSchemeName = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_name);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
//                if (isFirstTime) {
//                    tvSchemeName.setText(schemeListBean.getSlabTitle());
                tvSchemeName.setText(schemeSlabBean.getToQty());
//                    isFirstTime = false;
//                }
                tvSchemeDesc.setText(schemeSlabBean.getMaterialDesc());
                tvSchemeTo.setText(schemeSlabBean.getPayoutAmount()+" "+schemeSlabBean.getFreeQtyUOM());
                viewHolderHeader.llSchemeSlab.addView(salesAreaLayout);
            }
        }

    }

    @Override
    public int getItemCount() {
        return schemeListBeanArrayList.size();
    }
}
