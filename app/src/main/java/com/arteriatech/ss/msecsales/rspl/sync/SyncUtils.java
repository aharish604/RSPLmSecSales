package com.arteriatech.ss.msecsales.rspl.sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.sync.SyncHistoryDB;
import com.arteriatech.mutils.sync.SyncHistoryModel;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.RefreshListInterface;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.SyncSelectionViewBean;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestChangeSet;
import com.sap.smp.client.odata.store.ODataRequestParamBatch;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.impl.ODataRequestChangeSetDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamBatchDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by e10769 on 04-07-2017.
 */

public class SyncUtils {

    public static ArrayList<SyncSelectionViewBean> getSyncSelectionView(Context mContext) {
        ArrayList<SyncSelectionViewBean> syncSelectionViewBeanArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        SyncSelectionViewBean syncSelectionViewBean;

        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName(mContext.getString(R.string.alerts));
        syncSelectionViewBean.setCollectionName(SyncUtils.getAlerts());
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);

        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName("Attendance");
        syncSelectionViewBean.setCollectionName(SyncUtils.getAttendanceCollection());
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);

        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName("Authorization");
        syncSelectionViewBean.setCollectionName(SyncUtils.getAuthorizationCollection());
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);


        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName(mContext.getString(R.string.lbl_beat_paln));
        syncSelectionViewBean.setCollectionName(SyncUtils.getBeatCollection());
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);

        if (sharedPreferences.getString(Constants.isBehaviourEnabled, "").equalsIgnoreCase(Constants.isBehaviourTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName("Behavior");
            syncSelectionViewBean.setCollectionName(SyncUtils.getBehaviorList());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }


//        syncSelectionViewBean = new SyncSelectionViewBean();
//        syncSelectionViewBean.setChecked(false);
//        syncSelectionViewBean.setDisplayName("Customers");
//        syncSelectionViewBean.setCollectionName(SyncUtils.getCustomersCollection());
//        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        if (sharedPreferences.getString(Constants.isDBStockEnabled, "").equalsIgnoreCase(Constants.isDBStockTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.db_stocks));
            syncSelectionViewBean.setCollectionName(SyncUtils.getDBStockCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isCollCreateEnabledKey, "").equalsIgnoreCase(Constants.isCollCreateTcode) || sharedPreferences.getString(Constants.isCollListKey, "").equalsIgnoreCase(Constants.isCollListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_coll_history));
            syncSelectionViewBean.setCollectionName(SyncUtils.getFIPCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }
        if (sharedPreferences.getString(Constants.isDigitalProductEntryEnabled, "").equalsIgnoreCase(Constants.isDigitalProductEntryTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.digital_product_title));
            syncSelectionViewBean.setCollectionName(SyncUtils.getVisualAid());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }
        if (sharedPreferences.getString(Constants.isExpenseEntryKey, "").equalsIgnoreCase(Constants.isExpenseEntryTcode) || sharedPreferences.getString(Constants.isExpenseListKey, "").equalsIgnoreCase(Constants.isExpenseListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_expense));
            syncSelectionViewBean.setCollectionName(SyncUtils.getExpenseListCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }


        if (sharedPreferences.getString(Constants.isFeedBackListKey, "").equalsIgnoreCase(Constants.isFeedBackListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_feed_back));
            syncSelectionViewBean.setCollectionName(SyncUtils.getFeedBack());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isSecondaryInvoiceListKey, "").equalsIgnoreCase(Constants.isSecondaryInvoiceListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_invoice_History));
            syncSelectionViewBean.setCollectionName(SyncUtils.getInvoice());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isMerchReviewListKey, "").equalsIgnoreCase(Constants.isMerchReviewListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.lbl_merchndising_list));
            syncSelectionViewBean.setCollectionName(SyncUtils.getMerchandising());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

      /*  if (sharedPreferences.getString(Constants.isOutstandingListKey, "").equalsIgnoreCase(Constants.isOutstandingListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_outstanding));
            syncSelectionViewBean.setCollectionName(SyncUtils.getOutStandings());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }*/

        if (sharedPreferences.getString(Constants.isRetailerListEnabled, "").equalsIgnoreCase(Constants.isRetailerListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName("Retailers");
            syncSelectionViewBean.setCollectionName(SyncUtils.getFOS());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isRetailerStockKey, "").equalsIgnoreCase(Constants.isRetailerStockTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.retailer_stock_entry_title));
            syncSelectionViewBean.setCollectionName(SyncUtils.getRetailerStock());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isSecondarySalesListKey, "").equalsIgnoreCase(Constants.isSecondarySalesListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_sales_order));
            syncSelectionViewBean.setCollectionName(SyncUtils.getSOsCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isSchemeEnabled, "").equalsIgnoreCase(Constants.isSchemeTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_schemes));
            syncSelectionViewBean.setCollectionName(SyncUtils.getSchemes());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isReturnOrderListKey, "").equalsIgnoreCase(Constants.isReturnOrderListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.title_return_order_list));
            syncSelectionViewBean.setCollectionName(SyncUtils.getROsCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isMyTargetsEnabled, "").equalsIgnoreCase(Constants.isMyTargetsTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName("Targets");
            syncSelectionViewBean.setCollectionName(SyncUtils.getTargets());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }

        if (sharedPreferences.getString(Constants.isVisualAidEnabled, "").equalsIgnoreCase(Constants.isVisualAidTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName(mContext.getString(R.string.visual_aidl_title));
            syncSelectionViewBean.setCollectionName(SyncUtils.getVisualAid());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }


        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName("Value Helps");
        syncSelectionViewBean.setCollectionName(SyncUtils.getValueHelps());
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);

        syncSelectionViewBean = new SyncSelectionViewBean();
        syncSelectionViewBean.setChecked(false);
        syncSelectionViewBean.setDisplayName("Visits");
        syncSelectionViewBean.setCollectionName(SyncUtils.getVisit(mContext));
        syncSelectionViewBeanArrayList.add(syncSelectionViewBean);

       /* if (sharedPreferences.getString(Constants.isSOCreateKey, "").equalsIgnoreCase(Constants.isSOCreateTcode) || sharedPreferences.getString(Constants.isSOCreateCCKey, "").equalsIgnoreCase(Constants.isSOCreateCCTcode) || sharedPreferences.getString(Constants.isSOListKey, "").equalsIgnoreCase(Constants.isSOListTcode) || sharedPreferences.getString(Constants.isSOListCCKey, "").equalsIgnoreCase(Constants.isSOListCCTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName("SOs");
            syncSelectionViewBean.setCollectionName(SyncUtils.getSOsCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }*/

       /* if (sharedPreferences.getString(Constants.isInvoiceListKey, "").equalsIgnoreCase(Constants.isInvoiceListTcode)) {
            syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setDisplayName("Invoice");
            syncSelectionViewBean.setCollectionName(SyncUtils.getInvoiceCollection());
            syncSelectionViewBeanArrayList.add(syncSelectionViewBean);
        }*/


        return syncSelectionViewBeanArrayList;
    }

    public static ArrayList<String> getBehaviorList() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.SPChannelEvaluationList);
        return arrayList;
    }

    public static ArrayList<String> getTargets() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Targets);
        alAssignColl.add(Constants.TargetItems);
        alAssignColl.add(Constants.KPISet);
        alAssignColl.add(Constants.KPIItems);
        return alAssignColl;
    }

    public static ArrayList<String> getVisualAid() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Documents);
        return alAssignColl;
    }

    public static ArrayList<String> getMerchandising() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.MerchReviews);
        alAssignColl.add(Constants.MerchReviewImages);
        return alAssignColl;
    }

    public static ArrayList<String> getOutStandings() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.SSOutstandingInvoices);
        alAssignColl.add(Constants.SSOutstandingInvoiceItemDetails);
        return alAssignColl;
    }

    public static ArrayList<String> getFeedBack() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.Feedbacks);
        alAssignColl.add(Constants.FeedbackItemDetails);
        return alAssignColl;
    }

    public static ArrayList<String> getCompetitors() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.CompetitorInfos);
        return alAssignColl;
    }

    public static ArrayList<String> getInvoice() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        alAssignColl.add(Constants.SSINVOICES);
        alAssignColl.add(Constants.SSInvoiceItemDetails);
        return alAssignColl;
    }

    public static ArrayList<String> getSOsCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.SSSoItemDetails);
        arrayList.add(Constants.SSSOs);
        return arrayList;
    }

    public static ArrayList<String> getROsCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.SSROItemDetails);
        arrayList.add(Constants.SSROs);
        return arrayList;
    }

    public static ArrayList<String> getValueHelps() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.ValueHelps);
        arrayList.add(Constants.ConfigTypsetTypeValues);
        arrayList.add(Constants.ConfigTypesetTypes);
        // arrayList.add(Constants.ExpenseConfigs);
        return arrayList;
    }

    public static ArrayList<String> getAuthorizationCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.UserProfileAuthSet);
        return arrayList;
    }

    public static ArrayList<String> getBeatCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.RoutePlans);
        arrayList.add(Constants.RouteSchedules);
        arrayList.add(Constants.RouteSchedulePlans);
        return arrayList;
    }

    public static ArrayList<String> getFOS() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.ChannelPartners);
        arrayList.add(Constants.CPDMSDivisions);
        arrayList.add(Constants.CPSPRelations);
        arrayList.add(Constants.SalesPersons);
        return arrayList;
    }

    public static ArrayList<String> getRetailerStock() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.CPStockItems);
        arrayList.add(Constants.CPStockItemSnos);
        return arrayList;
    }

    public static ArrayList<String> getAlerts() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.ChannelPartners);
        arrayList.add(Constants.Visits);
        arrayList.add(Constants.Alerts);
        return arrayList;
    }

    public static ArrayList<String> getVisit(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.Visits);
        arrayList.add(Constants.VisitActivities);
        if( Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.VisitSummarySet)) {
            arrayList.add(Constants.VisitSummarySet);
        }
        return arrayList;
    }

    public static ArrayList<String> getAttendanceCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.Attendances);
        return arrayList;
    }

    public static ArrayList<String> getCustomersCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.Customers);
//        arrayList.add(Constants.UserCustomers);
        return arrayList;
    }

    public static ArrayList<String> getInvoiceCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add(Constants.Invoices);
//        arrayList.add(Constants.InvoiceItemDetails);
        return arrayList;
    }

    public static ArrayList<String> getDBStockCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.CPStockItems);
        arrayList.add(Constants.CPStockItemSnos);
        arrayList.add(Constants.Brands);
        arrayList.add(Constants.BrandsCategories);
        arrayList.add(Constants.MaterialCategories);
        arrayList.add(Constants.OrderMaterialGroups);
        return arrayList;
    }

    public static ArrayList<String> getFIPCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.FinancialPostingItemDetails);
        arrayList.add(Constants.FinancialPostings);
        arrayList.add(Constants.SSOutstandingInvoices);
        arrayList.add(Constants.SSOutstandingInvoiceItemDetails);
        return arrayList;
    }

    public static ArrayList<String> getExpenseListCollection() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.Expenses);
        arrayList.add(Constants.ExpenseItemDetails);
        arrayList.add(Constants.ExpenseDocuments);
        return arrayList;
    }

    public static ArrayList<String> getSchemes() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Constants.Schemes);
        arrayList.add(Constants.SchemeItemDetails);
        arrayList.add(Constants.SchemeSlabs);
        arrayList.add(Constants.SchemeGeographies);
        arrayList.add(Constants.SchemeCPs);
        arrayList.add(Constants.SchemeSalesAreas);
        arrayList.add(Constants.SchemeCostObjects);
        arrayList.add(Constants.SchemeFreeMatGrpMaterials);
        arrayList.add(Constants.SchemeCPDocuments);
        return arrayList;
    }

    /*public static ArrayList<MainMenuBean> getSyncMenu(Context mContext) {
        ArrayList<MainMenuBean> syncMenuList = new ArrayList<>();
        MainMenuBean mainMenuBean;
//        if (sharedPreferences.getString(Constants.isAllSyncKey, "").equalsIgnoreCase(Constants.isAllSyncTcode)) {
        mainMenuBean = new MainMenuBean();
        mainMenuBean.setMenuName(mContext.getString(R.string.sync_all));
        mainMenuBean.setId(1);
        mainMenuBean.setMenuImage(R.drawable.ic_sync);
        syncMenuList.add(mainMenuBean);
//        }
//        if (sharedPreferences.getString(Constants.isFreshSyncKey, "").equalsIgnoreCase(Constants.isFreshSyncTcode)) {
        mainMenuBean = new MainMenuBean();
        mainMenuBean.setMenuName(mContext.getString(R.string.sync_fresh));
        mainMenuBean.setId(2);
        mainMenuBean.setMenuImage(R.drawable.ic_sync);
        syncMenuList.add(mainMenuBean);
//        }
//        if (sharedPreferences.getString(Constants.isUpdateSyncKey, "").equalsIgnoreCase(Constants.isUpdateSyncTcode)) {
        mainMenuBean = new MainMenuBean();
        mainMenuBean.setMenuName(mContext.getString(R.string.sync_update_system));
        mainMenuBean.setId(3);
        mainMenuBean.setMenuImage(R.drawable.ic_sync);
        syncMenuList.add(mainMenuBean);
//        }
//        if (sharedPreferences.getString(Constants.isSyncHistKey, "").equalsIgnoreCase(Constants.isSyncHistTcode)) {
        mainMenuBean = new MainMenuBean();
        mainMenuBean.setMenuName(mContext.getString(R.string.sync_history));
        mainMenuBean.setId(4);
        mainMenuBean.setMenuImage(R.drawable.ic_sync_history);
        syncMenuList.add(mainMenuBean);
//        }
        return syncMenuList;
    }*/

    public static String getCollectionSyncTime(Context mContext, String collectionName) {
       if(mContext!=null){
        SyncHistoryDB syncHistoryDB = new SyncHistoryDB(mContext);
        List<SyncHistoryModel> syncHistoryModelList = syncHistoryDB.getSyncTimeBySpecificColl(SyncHistoryDB.COL_COLLECTION, collectionName);
        if (!syncHistoryModelList.isEmpty()) {
            long timeMillSec = ConstantsUtils.getMilliSeconds(syncHistoryModelList.get(0).getTimeStamp());
            return UtilConstants.getLastSeenDateFormat(mContext, timeMillSec);
        }
        }
        return "";
    }

    /*get all sync value from defining req*/
    public static ArrayList<String> getAllSyncValue(Context mContext) {
        ArrayList<String> alAssignColl = new ArrayList<>();
        String[] DEFINGREQARRAY = Constants.getDefinigReq(mContext);
        for (String collectionName : DEFINGREQARRAY) {
            if (collectionName.contains("?")) {
                String splitCollName[] = collectionName.split("\\?");
                collectionName = splitCollName[0];
            }
            alAssignColl.add(collectionName);
        }
        return alAssignColl;
    }

    public static void initialInsert(Context context) {
        String[] definingReqArray = Constants.getDefinigReq(context);
        SyncHistoryDB syncHistoryDB = new SyncHistoryDB(context);
        syncHistoryDB.deleteAll();
        for (String aDefiningReqArray : definingReqArray) {
            String colName = aDefiningReqArray;
            if (colName.contains("?$")) {
                String splitCollName[] = colName.split("\\?");
                colName = splitCollName[0];
            }
            try {
                syncHistoryDB.createRecord(new SyncHistoryModel("", colName, ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static List<SyncHistoryModel> getOneRecord(SyncHistoryDB syncHistoryDB) {
        List<SyncHistoryModel> syncHistoryModelList = new ArrayList();
        try {
            SQLiteDatabase db = syncHistoryDB.getReadableDatabase();
            String selectQuery = "select * from syncHistory limit 1";
            Cursor cursor = db.rawQuery(selectQuery, (String[]) null);
            if (cursor.moveToFirst()) {
                do {
                    SyncHistoryModel syncHistoryModel = new SyncHistoryModel();
                    syncHistoryModel.setCollections(cursor.getString(1));
                    syncHistoryModel.setTimeStamp(cursor.getString(2));
                    syncHistoryModelList.add(syncHistoryModel);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncHistoryModelList;
    }

    public static void checkAndCreateDB(Context mContext) {
        SyncHistoryDB syncHistoryDB = new SyncHistoryDB(mContext);
        if (getOneRecord(syncHistoryDB).isEmpty()) {
            initialInsert(mContext);
        }
    }

    public static void createSyncHistory(String collectionName, String syncTime, String syncType, String StrSPGUID32, String parternTypeID, String loginId,String refGuid,Context context) {
        try {
            Thread.sleep(100);

            if (collectionName.equalsIgnoreCase("ConfigTypsetTypeValues") && syncType.equals(Constants.UpLoad)) {
                syncType = Constants.DownLoad;
            }

            GUID guid = GUID.newRandom();
            Hashtable hashtable = new Hashtable();
            if(!TextUtils.isEmpty(collectionName) && (collectionName.contains("End") || collectionName.contains("Cancel")))
                hashtable.put(Constants.SyncHisGuid, guid);
            else
                hashtable.put(Constants.SyncHisGuid, refGuid);
            if (!collectionName.equals("") && collectionName != null) {
                hashtable.put(Constants.Collection, collectionName);
            }
            hashtable.put(Constants.SyncApplication, BuildConfig.APPLICATION_ID);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
            String time = "";
            String strDate = "";
            try {
                Date date = dateFormat.parse(syncTime);
                strDate = dateFormat.format(date);
                time = timeFormat.format(date.parse(syncTime));

            } catch (ParseException ex) {
                ex.printStackTrace();
                Log.v("Exception", ex.getLocalizedMessage());
            }
            ODataDuration startDuration = null;
            try {
                if (!time.isEmpty()) {
                    startDuration = Constants.getTimeAsODataDurationConvertion(time);
                    hashtable.put(Constants.SyncTime, startDuration);
                } else {
                    hashtable.put(Constants.SyncTime, startDuration);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            hashtable.put(Constants.SyncDate, strDate);

            hashtable.put(Constants.SyncType, syncType);

            hashtable.put(Constants.PartnerId, StrSPGUID32);
            hashtable.put(Constants.PartnerType, parternTypeID);
            hashtable.put(Constants.LoginId, loginId);
            hashtable.put(Constants.RefGUID, refGuid);
//            hashtable.put(Constants.Remarks,getDeviceName() + " (" + mapTable.get(Constants.AppVisibility) + ")");
            OfflineManager.CreateSyncHistroy(hashtable,context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Hashtable createSyncHistoryBatch(String collectionName, String syncTime, String syncType, String StrSPGUID32, String parternTypeID, String loginId,String refGuid) {
        Hashtable hashtable = new Hashtable();
        try {
            Thread.sleep(100);

            if (collectionName.equalsIgnoreCase("ConfigTypsetTypeValues") && syncType.equals(Constants.UpLoad)) {
                syncType = Constants.DownLoad;
            }

            GUID guid = GUID.newRandom();
            hashtable.put(Constants.SyncHisGuid, guid.toString().toUpperCase());
            if (!collectionName.equals("") && collectionName != null) {
                hashtable.put(Constants.Collection, collectionName);
            }
            hashtable.put(Constants.SyncApplication, BuildConfig.APPLICATION_ID);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
            String time = "";
            String strDate = "";
            try {
                Date date = dateFormat.parse(syncTime);
                strDate = dateFormat.format(date);
                time = timeFormat.format(date.parse(syncTime));

            } catch (ParseException ex) {
                ex.printStackTrace();
                Log.v("Exception", ex.getLocalizedMessage());
            }
            ODataDuration startDuration = null;
            try {
                if (!time.isEmpty()) {
                    startDuration = Constants.getTimeAsODataDurationConvertion(time);
                    hashtable.put(Constants.SyncTime, startDuration);
                } else {
                    hashtable.put(Constants.SyncTime, startDuration);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            hashtable.put(Constants.SyncDate, strDate);

            hashtable.put(Constants.SyncType, syncType);

            hashtable.put(Constants.PartnerId, StrSPGUID32);
            hashtable.put(Constants.PartnerType, parternTypeID);
            hashtable.put(Constants.LoginId, loginId);
            hashtable.put(Constants.RefGUID,refGuid );
//            hashtable.put(Constants.Remarks,getDeviceName() + " (" + mapTable.get(Constants.AppVisibility) + ")");
//            OfflineManager.CreateSyncHistroy(hashtable);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashtable;
    }

    public static void updateAllSyncHistory(final Context context, final String syncType,final String refGuid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncHistoryDB syncHistoryDB = new SyncHistoryDB(context);
                String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                String strSPGUID = Constants.getSPGUID();
                String StrSPGUID32 = "";
                String parternTypeID = "";

                if (!TextUtils.isEmpty(strSPGUID)) {
                    StrSPGUID32 = strSPGUID.replaceAll("-", "");
                    try {
                        parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }

                boolean checkSyncHistoryColl = getSyncHistoryColl(context);
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginId = sharedPreferences.getString("username", "");
//                parternTypeID = sharedPreferences.getString(Constants.USERPARTNERTYPE,"");;
                String[] definingReqArray = Constants.getDefinigReq(context);
                final ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                for (int incReq =0; incReq<definingReqArray.length;incReq++) {
                    String collection = definingReqArray[incReq];
                    if (collection.contains("?$")) {
                        String splitCollName[] = collection.split("\\?");
                        collection = splitCollName[0];
                    }
                    if (!syncHistoryDB.getSyncTimeBySpecificColl(SyncHistoryDB.COL_COLLECTION, collection).isEmpty()) {
                        syncHistoryDB.updateRecord(collection, syncTime);
                    } else {
                        syncHistoryDB.createRecord(new SyncHistoryModel("", collection, syncTime));
                    }
                    try {
                /*String collectionQry = Constants.SyncHistorys + "?$filter=" +Constants.Collection + " eq'" + collection +"'";
                ODataEntity entity = OfflineManager.checkCollectionIsExist(collectionQry);
                if(entity == null) {
                    SyncUtils.createSyncHistory(collection,syncTime);
                }else {*/
                        //                        ODataEntity oDataEntity = OfflineManager.getSyncHistroyByCollection(collection);
                        if(checkSyncHistoryColl) {
//                            SyncUtils.createSyncHistory(collection, syncTime, syncType, StrSPGUID32, parternTypeID, loginId);
                            Hashtable hashtable = SyncUtils.createSyncHistoryBatch(collection, syncTime, syncType, StrSPGUID32, parternTypeID, loginId,refGuid);
                            ODataEntity channelPartnerEntity = null;
                            try {
                                channelPartnerEntity = OfflineManager.createSyncHistroyEntity(hashtable);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            int id = incReq+1;
                            String contentId = String.valueOf(id);
                            ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                            // Create change set
                            batchItem.setPayload(channelPartnerEntity);
                            batchItem.setMode(ODataRequestParamSingle.Mode.Create);

                            batchItem.setResourcePath(Constants.SyncHistroy);
                            batchItem.setContentID(contentId);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("OfflineOData.RemoveAfterUpload", "true");
                            batchItem.setOptions(map);

                            Map<String, String> createHeaders = new HashMap<String, String>();
                            createHeaders.put("OfflineOData.RemoveAfterUpload", "true");
                            batchItem.getCustomHeaders().putAll(createHeaders);

                            ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                            changeSetItem.add(batchItem);
                            try {
                                requestParamBatch.add(changeSetItem);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }
                        }
//                }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                try {
                    OfflineManager.offlineStore.executeRequest(requestParamBatch);
                } catch (Exception e) {
                    try {
                        throw new OfflineODataStoreException(e);
                    } catch (OfflineODataStoreException e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    SyncUtils.updatingSyncStartTime(context,syncType,Constants.EndSync,refGuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static void updateSyncHistory(ODataEntity oDataEntity, String syncTime,Context context) {
        UIListener uiListener = new UIListener() {
            @Override
            public void onRequestError(int i, Exception e) {

            }

            @Override
            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

            }
        };
        try {
            Thread.sleep(100);
            ODataPropMap oDataProperties;
            ODataProperty oDataProperty;
            Hashtable table = new Hashtable();
            if (oDataEntity != null) {
                oDataProperties = oDataEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.SyncHisGuid);
                //noinspection unchecked
                table.put(Constants.SyncHisGuid, oDataProperty.getValue());
                try {
                    ODataGuid oDataGuid = (ODataGuid) oDataProperty.getValue();

                    table.put(Constants.SetResourcePath, Constants.SyncHistorys + "(guid'" + oDataGuid.guidAsString36().toUpperCase() + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                oDataProperties = oDataEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.Collection);
                table.put(Constants.Collection, oDataProperty.getValue());

                oDataProperties = oDataEntity.getProperties();
                oDataProperty = oDataProperties.get(Constants.SyncApplication);
                table.put(Constants.SyncApplication, oDataProperty.getValue());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");
                String time = "";
                String strDate = "";
                String currentDateTimeString = "";
                try {
                    Date date = dateFormat.parse(syncTime);
                    strDate = dateFormat.format(date);
                    time = timeFormat.format(date.parse(syncTime));

                } catch (ParseException ex) {
                    ex.printStackTrace();
                    Log.v("Exception", ex.getLocalizedMessage());
                }
                try {
                    ODataDuration startDuration = UtilConstants.getTimeAsODataDuration(time);
                    table.put(Constants.SyncTime, startDuration);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                table.put(Constants.SyncDate, strDate);

               /* final Calendar calCurrentTime = Calendar.getInstance();
                int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                int minute = calCurrentTime.get(Calendar.MINUTE);
                int second = calCurrentTime.get(Calendar.SECOND);
                ODataDuration oDataDuration = null;
                try {
                    oDataDuration = new ODataDurationDefaultImpl();
                    oDataDuration.setHours(hourOfDay);
                    oDataDuration.setMinutes(minute);
                    oDataDuration.setSeconds(BigDecimal.valueOf(second));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                try {
                    OfflineManager.updateSyncHistory(table, uiListener,context);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static Calendar convertDateFormat(String dateVal) {
        Date date = null;
        Calendar curCal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            date = format.parse(dateVal);
            curCal.setTime(date);
            System.out.println("Date" + curCal.getTime());
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return curCal;
    }

    public static boolean getSyncHistoryColl(Context context){
        boolean check = Arrays.asList(Constants.getDefinigReq(context)).contains(Constants.SyncHistorys);
        return check;
    }

    /*update sync time in sqlite db*/
    public static void updatingSyncTime(final Context mContext, final ArrayList<String> alAssignColl, final String syncType,final String refGuid, final RefreshListInterface listInterface) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncHistoryDB syncHistoryDB = new SyncHistoryDB(mContext);
                if (getOneRecord(syncHistoryDB).isEmpty()) {
                    initialInsert(mContext);
                }

                boolean checkSyncHistoryColl = getSyncHistoryColl(mContext);
                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    String strSPGUID = Constants.getSPGUID();
                    String StrSPGUID32 = "";
                    String parternTypeID = "";
                    if (!TextUtils.isEmpty(strSPGUID)) {
                        StrSPGUID32 = strSPGUID.replaceAll("-", "");
                        parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                    }
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                    String loginId = sharedPreferences.getString("username", "");
//                    parternTypeID = sharedPreferences.getString(Constants.USERPARTNERTYPE,"");;
                    final ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();
                    for (int incReq =0; incReq<alAssignColl.size();incReq++) {
                        String colName =  alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }
                        syncHistoryDB.updateRecord(colName, syncTime);
//                try {
//                    String collectionQry = Constants.SyncHistorys + "?$filter=" +Constants.Collection + " eq'" + colName +"'";
//                    ODataEntity entity = OfflineManager.checkCollectionIsExist(collectionQry);
//                        //                        ODataEntity oDataEntity = OfflineManager.getSyncHistroyByCollection(collection);
                        if(checkSyncHistoryColl) {
//                            SyncUtils.createSyncHistory(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId);
                            Hashtable hashtable = SyncUtils.createSyncHistoryBatch(colName, syncTime, syncType, StrSPGUID32, parternTypeID, loginId,refGuid);
                            ODataEntity channelPartnerEntity = null;
                            try {
                                Log.d("Sync History","insert RefGuid-::"+hashtable.get(Constants.RefGUID)+"--"+hashtable.get(Constants.Collection));

                                channelPartnerEntity = OfflineManager.createSyncHistroyEntity(hashtable);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            int id = incReq+1;
                            String contentId = String.valueOf(id);
                            ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();
                            // Create change set
                            batchItem.setPayload(channelPartnerEntity);
                            batchItem.setMode(ODataRequestParamSingle.Mode.Create);

                            batchItem.setResourcePath(Constants.SyncHistroy);
                            batchItem.setContentID(contentId);
                          /*  HashMap<String, String> map = new HashMap<>();
                            map.put("OfflineOData.RemoveAfterUpload", "true");
                            batchItem.setOptions(map);*/

                            Map<String, String> createHeaders = new HashMap<String, String>();
                            createHeaders.put("OfflineOData.RemoveAfterUpload", "true");
                            batchItem.getCustomHeaders().putAll(createHeaders);


                            ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();
                            changeSetItem.add(batchItem);
                            try {
                                requestParamBatch.add(changeSetItem);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }
                        }


//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    }
                    try {
                        OfflineManager.offlineStore.executeRequest(requestParamBatch);
                    } catch (Exception e) {
                        try {
                            throw new OfflineODataStoreException(e);
                        } catch (OfflineODataStoreException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (Exception exce) {
                    exce.printStackTrace();
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }
                try {
                    updatingSyncStartTime(mContext,syncType,Constants.EndSync,refGuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(listInterface != null){
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listInterface.refreshList();
                    }
                });
            }
            }
        }).start();
    }

    public static void updatingInitialSyncStartTime(final Context mContext, final String syncType, final String syncMsg,final String refGuid,final String initialSyncTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncHistoryDB syncHistoryDB = new SyncHistoryDB(mContext);
                if (getOneRecord(syncHistoryDB).isEmpty()) {
                    initialInsert(mContext);
                }

                boolean checkSyncHistoryColl = getSyncHistoryColl(mContext);
                try{
                    String strSPGUID = Constants.getSPGUID();
                    String StrSPGUID32 = "";
                    String parternTypeID = "";
                    if (!TextUtils.isEmpty(strSPGUID)) {
                        StrSPGUID32 = strSPGUID.replaceAll("-", "");
                        parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                    }
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                    String loginId = sharedPreferences.getString("username", "");
//                    parternTypeID = sharedPreferences.getString(Constants.USERPARTNERTYPE,"");;
                    String startColl = "";
                    String syncollType = "";

                    if (checkSyncHistoryColl) {
                        startColl = "Initial Sync Start";
                        syncollType = Constants.Sync_All;
                        SyncUtils.createSyncHistory(startColl, initialSyncTime, syncollType, StrSPGUID32, parternTypeID, loginId,refGuid,mContext);
                    }
                } catch (Exception exce) {
                    exce.printStackTrace();
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }
            }
        }).start();
    }


    public static void updatingSyncStartTime(final Context mContext, final String syncType, final String syncMsg,final String refGuid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncHistoryDB syncHistoryDB = new SyncHistoryDB(mContext);
                if (getOneRecord(syncHistoryDB).isEmpty()) {
                    initialInsert(mContext);
                }

                boolean checkSyncHistoryColl = getSyncHistoryColl(mContext);
                try {
                    String syncTime ="";
                    if(syncMsg.equalsIgnoreCase(Constants.StartSync)) {
                        syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    }else {
                        syncTime = Constants.getSyncHistoryddmmyyyyTimeDelay();
                    }
                    String strSPGUID = Constants.getSPGUID();
                    String StrSPGUID32 = "";
                    String parternTypeID = "";
                    if (!TextUtils.isEmpty(strSPGUID)) {
                        StrSPGUID32 = strSPGUID.replaceAll("-", "");
                        parternTypeID = OfflineManager.getPartnerTypeID(Constants.UserPartners + "?$filter= PartnerID eq'" + StrSPGUID32 + "'");
                    }
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                    String loginId = sharedPreferences.getString("username", "");
//                    parternTypeID = sharedPreferences.getString(Constants.USERPARTNERTYPE,"");;
                    String startColl = "";
                    String syncollType = "";

                    try {
                        if(syncMsg.equalsIgnoreCase(Constants.StartSync)) {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download Start";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download Start";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload Start";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync Start";
                                syncollType = Constants.Auto_Sync;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Beat_sync)) {
                                startColl = "Beat PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Retailers_sync)) {
                                startColl = "Retailers PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.VisualAids_sync)) {
                                startColl = "VisualAid PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "AdVst Ret PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.VisitMTD_sync)) {
                                startColl = "VisitMTD PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Schemes_sync)) {
                                startColl = "Schemes PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST Bkgnd Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ROPD_sync)) {
                                startColl = "RO PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ROPostPD_sync)) {
                                startColl = "RO POST PD Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MerchPD_sync)) {
                                startColl = "Merch PD Sync Start";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MerchPostPD_sync)) {
                                startColl = "Merch POST PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.OSPD_sync)) {
                                startColl = "OS PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SD_PD_sync)) {
                                startColl = "SD PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SDPostPD_sync)) {
                                startColl = "SD Post PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CI_PD_sync)) {
                                startColl = "Competitor PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CIPostPD_sync)) {
                                startColl = "Competitor POST PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.FB_PD_sync)) {
                                startColl = "Feedback PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.FBPostPD_sync)) {
                                startColl = "Feedback POST PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Collection_PD_sync)) {
                                startColl = "Collection PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CollectionPostPD_sync)) {
                                startColl = "Collection POST PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.InvoicePD_sync)) {
                                startColl = "Invoice PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync Start";
                                syncollType = Constants.DownLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Dashboard_sync)) {
                                startColl = "DashBoard PD Sync Start";
                                syncollType = Constants.DownLoad;
                            }  else if (syncType.equalsIgnoreCase(Constants.Initial_sync)) {
                                startColl = "Initial Sync Start";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_cancel_sync)) {
                                startColl = "All Download Cancel Man";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_cancel_sync)) {
                                startColl = "Upload Cancel Man";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_cancel_sync)) {
                                startColl = "Download Cancel Man";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_net_sync)) {
                                startColl = "Upload Cancel Net";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_net_sync)) {
                                startColl = "All Download Cancel Net";
                                syncollType = Constants.Sync_All;
                            }
                        }else {
                            if (syncType.equalsIgnoreCase(Constants.Sync_All)) {
                                startColl = "All Download End";
                                syncollType = Constants.Sync_All;
                            } else if (syncType.equalsIgnoreCase(Constants.DownLoad)) {
                                startColl = "Download End";
                                syncollType = Constants.DownLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.UpLoad)) {
                                startColl = "Upload End";
                                syncollType = Constants.UpLoad;
                            } else if (syncType.equalsIgnoreCase(Constants.Auto_Sync)) {
                                startColl = "Auto Sync End";
                                syncollType = Constants.Auto_Sync;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Attnd_sync)) {
                                startColl = "Attnd Start Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Beat_sync)) {
                                startColl = "Beat PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Retailers_sync)) {
                                startColl = "Retailers PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.VisualAids_sync)) {
                                startColl = "VisualAid PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.AdVst_sync)) {
                                startColl = "AdVst Ret PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Behav_sync)) {
                                startColl = "Behav PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.VisitMTD_sync)) {
                                startColl = "VisitMTD PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Schemes_sync)) {
                                startColl = "Schemes PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPD_sync)) {
                                startColl = "SO PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPOSTBG_sync)) {
                                startColl = "SO POST Bkgnd Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SOPostPD_sync)) {
                                startColl = "SO POST PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ROPD_sync)) {
                                startColl = "RO PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.ROPostPD_sync)) {
                                startColl = "RO POST PD Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MerchPD_sync)) {
                                startColl = "Merch PD Sync End";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.MerchPostPD_sync)) {
                                startColl = "Merch POST PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.OSPD_sync)) {
                                startColl = "OS PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SD_PD_sync)) {
                                startColl = "SD PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.SDPostPD_sync)) {
                                startColl = "SD Post PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CI_PD_sync)) {
                                startColl = "Competitor PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CIPostPD_sync)) {
                                startColl = "Competitor POST PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.FB_PD_sync)) {
                                startColl = "Feedback PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.FBPostPD_sync)) {
                                startColl = "Feedback POST PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.Collection_PD_sync)) {
                                startColl = "Collection PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.CollectionPostPD_sync)) {
                                startColl = "Collection POST PD Sync End";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.InvoicePD_sync)) {
                                startColl = "Invoice PD Sync End";
                                syncollType = Constants.DownLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Attnd_refresh_sync)) {
                                startColl = "Attnd Refresh Sync End";
                                syncollType = Constants.DownLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Dashboard_sync)) {
                                startColl = "DashBoard PD Sync End";
                                syncollType = Constants.DownLoad;
                            }
                            else if (syncType.equalsIgnoreCase(Constants.Initial_sync)) {
                                startColl = "Initial Sync End";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_cancel_sync)) {
                                startColl = "All Download Cancel Man";
                                syncollType = Constants.Sync_All;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_cancel_sync)) {
                                startColl = "Upload Cancel Man";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_cancel_sync)) {
                                startColl = "Download Cancel Man";
                                syncollType = Constants.DownLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.upload_net_sync)) {
                                startColl = "Upload Cancel Net";
                                syncollType = Constants.UpLoad;
                            }else if (syncType.equalsIgnoreCase(Constants.download_all_net_sync)) {
                                startColl = "All Download Cancel Net";
                                syncollType = Constants.Sync_All;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (checkSyncHistoryColl) {
                        SyncUtils.createSyncHistory(startColl, syncTime, syncollType, StrSPGUID32, parternTypeID, loginId,refGuid,mContext);
                    }
                } catch (Exception exce) {
                    exce.printStackTrace();
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }
            }
        }).start();
    }

        public static ArrayList<String> getComplintsList () {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(Constants.Complaints);
            return arrayList;
        }

    }
