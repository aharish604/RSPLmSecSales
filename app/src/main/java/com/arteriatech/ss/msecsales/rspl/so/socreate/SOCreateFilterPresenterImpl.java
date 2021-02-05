package com.arteriatech.ss.msecsales.rspl.so.socreate;

import android.app.Activity;
import android.content.Context;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.List;

/**
 * Created by e10526 on 18-05-2018.
 *
 */

public class SOCreateFilterPresenterImpl implements SOCreateFilterPresenter{
    private Context context;
    private SOCreateView soCreateView=null;
    // Below hard code values
    String[][] skuType = {{"00", "01"}, {"All", "Must Sell"}};
    String[][] brandArrvalues=null, catArrValues=null;
    private DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    private String mStrSelBrand="",mStrSelCatType="";

    public SOCreateFilterPresenterImpl(Context context,SOCreateView soCreateView){
        this.soCreateView = soCreateView;
        this.context = context;
    }
    @Override
    public void onDestroy() {
        soCreateView= null;
    }

    @Override
    public void onStart(DmsDivQryBean dmsDivQryBean,String brandID) {
        this.dmsDivQryBean = dmsDivQryBean;
        this.mStrSelBrand = brandID;
        getCategory();
    }

    private void getCategory(){
     /*   if (soCreateView != null) {
            soCreateView.showProgressDialog();
        }*/

        try {
            String mStrMatCatQry = "";
            if (/*mStrSelBrand.equalsIgnoreCase(Constants.str_00) ||*/ mStrSelBrand.equalsIgnoreCase("")) {
                mStrMatCatQry = Constants.MaterialCategories + "?$orderby=" + Constants.MaterialCategoryDesc + " &$filter="+dmsDivQryBean.getDMSDivisionQry()+" ";
                catArrValues = OfflineManager.getMaterialCategries(mStrMatCatQry);
            } else {
                mStrMatCatQry = Constants.BrandsCategories + "?$orderby=" + Constants.MaterialCategoryDesc + " &$filter=" + Constants.BrandID + " eq '" + mStrSelBrand + "' and "+dmsDivQryBean.getDMSDivisionQry()+" ";
                catArrValues = OfflineManager.getCatgeriesBrandsLink(mStrMatCatQry, Constants.MaterialCategoryID, Constants.MaterialCategoryDesc);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (soCreateView != null) {
//                    soCreateView.hideProgressDialog();
                    if (catArrValues!=null) {
                        soCreateView.displayCat(catArrValues);
                    }else {
                        soCreateView.displayMessage("");
                    }
//                    soCreateView.divisionList(finalDistListDms);
                }
            }
        });

    }
    @Override
    public void getBrandList(String mStrSelCatType, List<String> dividionIDs) {
       /* if (soCreateView != null) {
            soCreateView.showProgressDialog();
        }*/
        try {
            String mStrBrandqry = "";
            if (/*mStrSelCatType.equalsIgnoreCase(Constants.str_00) ||*/ mStrSelCatType.equalsIgnoreCase("")) {
//                mStrBrandqry = Constants.Brands + "?$orderby=" + Constants.BrandDesc + " &$filter="+dmsDivQryBean.getDMSDivisionQry()+"";

                String divisionQry = "";
                if(dividionIDs!=null && !dividionIDs.isEmpty()) {
                    for (int i = 0; i < dividionIDs.size();i++){
                        if(i==dividionIDs.size()-1) {
                            divisionQry = divisionQry + "DMSDivision eq '" + dividionIDs.get(i)+"'";
                        }else {
                            divisionQry = divisionQry + "DMSDivision eq '" + dividionIDs.get(i) + "' or ";
                        }
                    }
                }

              //  String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + soCreateBean.getCPNo() + "' and ("+divisionQry+")";

                mStrBrandqry = Constants.Brands + "?$orderby=" + Constants.BrandDesc + " &$filter="+divisionQry+" and "+dmsDivQryBean.getDMSDivisionQry()+"";
               // mStrBrandqry = Constants.Brands + "?$orderby=" + Constants.BrandDesc + " &$filter="+Constants.DMSDivision+" eq '"+dividionID+"' and "+dmsDivQryBean.getDMSDivisionQry()+"";
                brandArrvalues = OfflineManager.getBrands(mStrBrandqry);
            } else {
                mStrBrandqry = Constants.BrandsCategories + "?$orderby=" + Constants.BrandDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + mStrSelCatType + "' and "+dmsDivQryBean.getDMSDivisionQry()+" ";
                brandArrvalues = OfflineManager.getCatgeriesBrandsLink(mStrBrandqry, Constants.BrandID, Constants.BrandDesc);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (soCreateView != null) {
                    if (catArrValues!=null) {
                        soCreateView.displayBrands(brandArrvalues);
                    }else {
                        soCreateView.displayMessage("");
                    }
//                    soCreateView.hideProgressDialog();

//                    soCreateView.divisionList(finalDistListDms);
                }
            }
        });

    }
}
