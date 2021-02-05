package com.arteriatech.ss.msecsales.rspl.scheme.schemelist;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;


/**
 * Created by e10769 on 27-03-2017.
 */

public class ViewHolderHeader extends RecyclerView.ViewHolder {
    public final TextView tvSchemeNameTitle,tvSchemeName,tvSchemeValidTo,tvSchemeType,
            tvSchemeTypeDesc,tvSchemeBenefitDesc;
    public final TableLayout llSalesAreaView;
    public final TableLayout llItemDetailsList;
    public final TableLayout llSchemeSlab;
    public final TextView tvSchemeBenefitTo;
    public final TextView tvSchemeSlabs;
    public final TextView tvSchemeValidFrom;

   /* public final TextView tvSchemeName,tvSchemeValidTo,tvSchemeType,
            tvApplicableFor,tv_scheme_slab_per,tv_on_sales_of;
    public final ImageView ivSchemeSlabType;
    public final LinearLayout llMoreInfo;
    public final Button btExpand;*/

    public ViewHolderHeader(View itemView) {
        super(itemView);
        tvSchemeNameTitle = (TextView) itemView.findViewById(R.id.tv_scheme_name_title);
        tvSchemeName = (TextView) itemView.findViewById(R.id.tv_scheme_name);
        tvSchemeValidTo = (TextView) itemView.findViewById(R.id.tv_scheme_valid_to);
        tvSchemeValidFrom = (TextView) itemView.findViewById(R.id.tv_scheme_from_date);
        tvSchemeType = (TextView) itemView.findViewById(R.id.tv_scheme_type);
        tvSchemeTypeDesc = (TextView) itemView.findViewById(R.id.tv_scheme_type_desc);
//        tvSchemeSalesOfDesc = (TextView) itemView.findViewById(R.id.tv_scheme_sales_of_desc);
        tvSchemeBenefitDesc = (TextView) itemView.findViewById(R.id.tv_scheme_benefit_desc);
        tvSchemeBenefitTo = (TextView) itemView.findViewById(R.id.tv_scheme_benefit_to);
        tvSchemeSlabs = (TextView) itemView.findViewById(R.id.tv_scheme_slabs);
        llSalesAreaView = (TableLayout) itemView.findViewById(R.id.ll_sales_area);
        llItemDetailsList = (TableLayout) itemView.findViewById(R.id.ll_scheme_item_details_list);
        llSchemeSlab = (TableLayout) itemView.findViewById(R.id.ll_scheme_slabs);


        /*tvSchemeName = (TextView) itemView.findViewById(R.id.tv_scheme_name);
        tvSchemeValidTo = (TextView) itemView.findViewById(R.id.tv_scheme_valid_to);
        tvSchemeType = (TextView) itemView.findViewById(R.id.tv_scheme_type);
        tvApplicableFor = (TextView) itemView.findViewById(R.id.tvApplicableFor);
        tv_on_sales_of = (TextView) itemView.findViewById(R.id.tv_on_sales_of);
        tv_scheme_slab_per = (TextView) itemView.findViewById(R.id.tv_scheme_slab_per);
        ivSchemeSlabType = (ImageView) itemView.findViewById(R.id.ivSchemeSlabType);
        llItemDetailsList = (TableLayout) itemView.findViewById(R.id.ll_scheme_item_details_list);
        llMoreInfo = (LinearLayout) itemView.findViewById(R.id.llMoreInfo);
        btExpand = (Button) itemView.findViewById(R.id.btExpand);*/


    }
}
