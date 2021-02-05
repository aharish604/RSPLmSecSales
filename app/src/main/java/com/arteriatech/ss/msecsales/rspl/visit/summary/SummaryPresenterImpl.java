package com.arteriatech.ss.msecsales.rspl.visit.summary;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MyPerformanceBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOItemBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.github.mikephil.charting.data.BarEntry;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by e10769 on 12-May-18.
 */

public class SummaryPresenterImpl implements SummaryPresenter {

    private Context mContext;
    private SummaryView summaryView = null;
    private String mStrBundleCPGUID = "";
    private String mStrParentId = "";
    private String mStrBundleBeatGUID = "";
    private String mStrOpenOrderNo = "";
    private String mStrOpenOrderDate = "";
    private String mStrAvgInvValue = "";
    private String mStrCurrency = "";
    private String mStrVisitDate = "";
    private ArrayList<MyPerformanceBean> preformanceList = new ArrayList<>();
    private BigDecimal bigDecimalAmtMTD=null;

    public SummaryPresenterImpl(Context mContext, SummaryView summaryView, String mStrBundleCPGUID, String mStrBundleBeatGUID, String mStrParentId) {
        this.mContext = mContext;
        this.summaryView = summaryView;
        this.mStrBundleBeatGUID = mStrBundleBeatGUID;
        this.mStrBundleCPGUID = mStrBundleCPGUID;
        this.mStrParentId = mStrParentId;
    }

    @Override
    public void onStart() {
        if (summaryView != null) {
            summaryView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    preformanceList = OfflineManager.getRetTrendsList(Constants.Performances + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID.replace("-", "").toUpperCase() + "' " +
                            "and " + Constants.PerformanceTypeID + " eq '000002'", mStrBundleCPGUID.replace("-", "").toUpperCase());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                String configDateFormat = ConstantsUtils.getConfigTypeDateFormat(mContext);
//                String query = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID + "' " + "and " + Constants.Status + " eq '000001' &$orderby=" + Constants.OrderDate + " %20desc";
                String tempmStrParentId= "";
                if(!TextUtils.isEmpty(mStrParentId)){
                    tempmStrParentId= String.valueOf(Integer.parseInt(mStrParentId));
                }


                ArrayList<String> divisionUserAuthAL=new ArrayList<>();
                try {
                    divisionUserAuthAL = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String strUserAuthDivisionID = "";
                if(divisionUserAuthAL!=null && !divisionUserAuthAL.isEmpty()) {
                    for (int i = 0; i < divisionUserAuthAL.size();i++){
                        if(i==divisionUserAuthAL.size()-1) {
                            strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i)+"'";
                        }else {
                            strUserAuthDivisionID = strUserAuthDivisionID + "DMSDivision eq '" + divisionUserAuthAL.get(i) + "' or ";
                        }
                    }
                }

                List<ODataEntity> entities = null;
                if(!TextUtils.isEmpty(strUserAuthDivisionID)) {
                    String mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID + "' and ("+strUserAuthDivisionID+")";


                    try {
                        entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
               ArrayList<DMSDivisionBean> alDmsDivision=new ArrayList<>();
                try {
//            alDmsDivision.addAll(OfflineManager.getDistributorsDmsDivision(entities));
                    alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivisionwithoutNone(entities));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String strDmsDivision = "";
                if(alDmsDivision!=null && !alDmsDivision.isEmpty()) {
                    for (int i = 0; i < alDmsDivision.size();i++){
                        if(i==alDmsDivision.size()-1) {
                            strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID()+"'";
                        }else {
                            strDmsDivision = strDmsDivision + "DmsDivision eq '" + alDmsDivision.get(i).getDMSDivisionID() + "' or ";
                        }
                    }
                }


                ArrayList<SalesOrderBean> alSOList = new ArrayList<>();
                if(!TextUtils.isEmpty(strDmsDivision)) {

                    String query = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID + "' " + "and " + Constants.FromCPGUID + " eq '" + tempmStrParentId + "' and ("+strDmsDivision + ") and " + Constants.Status + " eq '000001' &$orderby=" + Constants.OrderDate + " %20desc";
                    try {
                        alSOList = OfflineManager.getSOList(query, configDateFormat, mContext);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }

               /* String invQuery = Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID + "' " + "and " + Constants.StatusID + " ne '02'";

                try {
                    mStrAvgInvValue = OfflineManager.getAvgInvValue(invQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/

               /* List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                String queryVistDate = Constants.Visits + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID.replace("-","").toUpperCase()+"' and "+Constants.BeatGUID + " eq guid'" + mStrBundleBeatGUID.toUpperCase() + "' &$orderby=" + Constants.VisitDate + "%20desc &$top=1";

                try {
                    mStrVisitDate = OfflineManager.getDateByColumnName(queryVistDate,Constants.VisitDate,configDateFormat,mContext);
                } catch (OfflineODataStoreException e) {
                    mStrVisitDate= "";
                    e.printStackTrace();
                }
//                String queryOrderNo = Constants.SSSOs + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrBundleCPGUID + "' " + "and " + Constants.Status + " eq '000001' ";

//                mStrTypeVal = Constants.getConfigTypeIndicator(Constants.ConfigTypsetTypeValues,
//                        Constants.TypeValue, Constants.Types, Constants.DAYSFLT, Constants.Typeset, Constants.MSEC);

                mStrCurrency = Constants.getCurrencyFromSP();
                BigDecimal m1 = new BigDecimal(0);
                BigDecimal m2 = new BigDecimal(0);
                BigDecimal m3 = new BigDecimal(0);
                bigDecimalAmtMTD = new BigDecimal(0);

                BigDecimal invM1 = new BigDecimal(0);
                BigDecimal invM2 = new BigDecimal(0);
                BigDecimal invM3 = new BigDecimal(0);

                BigDecimal cmTarget = new BigDecimal(0);
                try {
                    if(preformanceList!=null && preformanceList.size()>0){
                        for (MyPerformanceBean myPerformanceBean : preformanceList) {
                            if (myPerformanceBean.getReportOnID().equalsIgnoreCase("01")) {
                                m1 = m1.add(new BigDecimal(myPerformanceBean.getQtyMonth1PrevPerf()));
                                m2 = m2.add(new BigDecimal(myPerformanceBean.getQtyMonth2PrevPerf()));
                                m3 = m3.add(new BigDecimal(myPerformanceBean.getQtyMonth3PrevPerf()));

                            } else if (myPerformanceBean.getReportOnID().equalsIgnoreCase("02")) {
                                cmTarget = cmTarget.add(new BigDecimal(myPerformanceBean.getCMTarget()));

                                m1 = m1.add(new BigDecimal(myPerformanceBean.getAmtMonth1PrevPerf()));
                                m2 = m2.add(new BigDecimal(myPerformanceBean.getAmtMonth2PrevPerf()));
                                m3 = m3.add(new BigDecimal(myPerformanceBean.getAmtMonth3PrevPerf()));

                            }
                            try {
                                bigDecimalAmtMTD= bigDecimalAmtMTD.add(new BigDecimal(myPerformanceBean.getAmtMTD()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            invM1 = invM1.add(new BigDecimal(myPerformanceBean.getAmtMonth1PrevPerf()));
                            invM2 = invM2.add(new BigDecimal(myPerformanceBean.getAmtMonth2PrevPerf()));
                            invM3 = invM3.add(new BigDecimal(myPerformanceBean.getAmtMonth3PrevPerf()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Double avgLastThreeMont = 0.0;
                try {
                    avgLastThreeMont = (invM1.doubleValue() + invM2.doubleValue() + invM3.doubleValue())  / 3;
                } catch (Exception e) {
                    avgLastThreeMont = 0.00;
                }

                if (Double.isNaN(avgLastThreeMont) || Double.isInfinite(avgLastThreeMont)) {
                    avgLastThreeMont = 0.00;
                }

                mStrAvgInvValue = avgLastThreeMont+"";

                final ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
                final ArrayList<String> labels = new ArrayList<>();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MONTH, -1);
                barEntryArrayList.add(new BarEntry(0, Float.valueOf(String.valueOf(m1))));
                labels.add(c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                barEntryArrayList.add(new BarEntry(1, Float.valueOf(String.valueOf(m2))));
                c.add(Calendar.MONTH, -1);
                labels.add(c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                barEntryArrayList.add(new BarEntry(2, Float.valueOf(String.valueOf(m3))));
                c.add(Calendar.MONTH, -1);
                labels.add(c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                String totalOpenOrder = "0";
                c = Calendar.getInstance();
                if (alSOList != null && alSOList.size()>0) {
                    totalOpenOrder = String.valueOf(alSOList.size());
                }else {
                    totalOpenOrder = "0";
                }
                try {
                    if(alSOList!=null && alSOList.size()>0){
                        mStrOpenOrderNo = alSOList.get(0).getOrderNo();
                        mStrOpenOrderDate = alSOList.get(0).getOrderDate();
                    }else {
                        mStrOpenOrderNo = "";
                        mStrOpenOrderDate = "";
                    }
                } catch (Exception e) {
                    mStrOpenOrderNo = "";
                    e.printStackTrace();
                }

                final BigDecimal finalCmTarget = cmTarget;
                final String finalTotalOpenOrder = totalOpenOrder;
                final Calendar finalC = c;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (summaryView != null) {
                            summaryView.hideProgress();
//                            summaryView.displayValue(barEntryArrayList, labels, ConstantsUtils.commaSeparator(String.valueOf(finalCmTarget), ""), finalC.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),mStrCurrency);
                            summaryView.displayValue(barEntryArrayList, labels, ConstantsUtils.commaSeparatorWithoutZerosAfterDot(String.valueOf(bigDecimalAmtMTD), mStrCurrency), finalC.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),mStrCurrency);
                            summaryView.displayOpenOrder(finalTotalOpenOrder,mStrVisitDate,mStrOpenOrderNo,mStrOpenOrderDate,mStrAvgInvValue,mStrCurrency);
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public void onDestroy() {
        summaryView = null;
    }
}
