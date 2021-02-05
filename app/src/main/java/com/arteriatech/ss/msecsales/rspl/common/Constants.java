package com.arteriatech.ss.msecsales.rspl.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.system.ErrnoException;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.support.SecuritySettingActivity;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.alerts.NotificationSetClass;
import com.arteriatech.ss.msecsales.rspl.customers.NotPostedRetailerActivity;
import com.arteriatech.ss.msecsales.rspl.home.dashboard.BrandProductiveBean;
import com.arteriatech.ss.msecsales.rspl.home.dashboard.VisitedBeatBean;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;
import com.arteriatech.ss.msecsales.rspl.mbo.Config;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.MTPRoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RemarkReasonBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeCalcuBean;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.scheme.SchemeItemListBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncSelectionActivity;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.RefreshListInterface;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.sap.client.odata.v4.core.CharBuffer;
import com.sap.client.odata.v4.core.GUID;
import com.sap.client.odata.v4.core.StringFunction;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.offline.ODataOfflineException;
import com.sap.smp.client.odata.offline.ODataOfflineStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.arteriatech.ss.msecsales.rspl.store.OfflineManager.offlineStore;

/**
 * Created by e10769 on 12-Apr-18.
 */

public class Constants {
    public static final String PASSWORD_LOCKED = "PASSWORD_LOCKED";
    public static final String Unauthorized = "Unauthorized";
    public static final String PASSWORD_DISABLED = "PASSWORD_DISABLED";
    public static final String USER_INACTIVE = "USER_INACTIVE";
    public static final String PASSWORD_RESET_REQUIRED = "PASSWORD_RESET_REQUIRED";
    public static final String PASSWORD_CHANGE_REQUIRED = "PASSWORD_CHANGE_REQUIRED";
    public static String PasswordExpiredMsg = "User is locked or password expired. Click on Settings to change password or Please Contact Channel team";
    public static final int TAKE_PICTURE = 190;
    public static final String SOS_SO_TASK_ENTITY = ".Task";
    public static final String[][] billAges = {{"00", "01", "02", "03", "04"}, {"All", "0 - 30 Days", "31 - 60 Days", "61 - 90 Days", "> 90 Days"}};
    public static final String InvList = "InvList";
    public static final String RTGS = "RTGS";
    public static final String NEFT = "NEFT";
    public static final String DD = "DD";
    public static final String Cheque = "Cheque";
    public static String REPEATABLE_REQUEST_ID = "";
    public static final String RetailerChange = "RetailerChange";
    public static final String offlineDBPath = "/data/com.arteriatech.ss.msecsales.rspl/files/mSecSales_Offline.udb";
    public static final String offlineReqDBPath = "/data/com.arteriatech.ss.msecsales.rspl/files/mSecSales_Offline.rq.udb";
    public static final String icurrentUDBPath = "/data/com.arteriatech.ss.msecsales.rspl/files/mSecSales_Offline.udb";
    public static final String ibackupUDBPath = "mSecSales_Offline.udb";
    public static final String icurrentRqDBPath = "/data/com.arteriatech.ss.msecsales.rspl/files/mSecSales_Offline.rq.udb";
    public static final String ibackupRqDBPath = "mSecSales_Offline.rq.udb";
    /*property name*/
    public static final String MobileNoSales = "MobileNoSales";
    public static final String AuthOrgValue = "AuthOrgValue";
    public static final String RoleID = "RoleID";
    public static final String AuthOrgTypeID = "AuthOrgTypeID";
    public static final String KPIGUID = "KPIGUID";
    public static final String HTTP_HEADER_SUP_APPCID = "X-SUP-APPCID";
    public static final String HTTP_HEADER_SMP_APPCID = "X-SMP-APPCID";
    public static final String EXTRA_SO_DETAIL = "openSODetails";
    public static final String EXTRA_PENDING_VIEWCOUNT = "viewCount";
    public static final String DELIVERY_STATUS = "DeliveryStatus";
    public static final String RefdocItmGUID = "RefdocItmGUID";
    public static final String entityType = "EntityType";
    public static final String BeatGUID = "BeatGUID";
    public static final String H = "H";
    public static final String SalesDistDesc = "SalesDistDesc";
    public static final String MatGroupDesc = "MatGroupDesc";
    public static final String ONETIMESHIPTO = "OneTimeShipTo";
    public static final String RegionID = "RegionID";
    public static final String CUSTOMERS = "Customers";
    public static final String CustomerNo = "CustomerNo";
    public static final String CustomerPO = "CustomerPO";
    public static final String CustomerName = "CustomerName";
    public static final String CustomerPODate = "CustomerPODate";
    public static final String SalesArea = "SalesArea";
    public static final String AmtPastDue = "AmtPastDue";
    public static final String AmtCurrentDue = "AmtCurrentDue";
    public static final String Amt31To60 = "Amt31To60";
    public static final String Amt61To90 = "Amt61To90";
    public static final String Amt91To120 = "Amt91To120";
    public static final String AmtOver120 = "AmtOver120";
    public static final String VisitCatID = "VisitCatID";
    public static final String PROP_MER_TYPE = "RVWTYP";
    public static final String DESCRIPTION = "Description";
    public static final String EntityType = "EntityType";
    public static final String IsDefault = "IsDefault";
    public static final String PropName = "PropName";
    public static final String ID = "ID";
    public static final String CustomerCreditLimits = "CustomerCreditLimits";
    public static final String DSPPRCNO0 = "DSPPRCNO0";
    public static final String DSPMATNO = "DSPMATNO";
    public static final String EVLTYP = "EVLTYP";
    public static final String TypeValue = "TypeValue";
    public static final String Typeset = "Typeset";
    public static final String Types = "Types";
    public static final String SS = "SS";
    public static final String MAXEXPALWM = "MAXEXPALWM";
    public static final String MAXEXPALWD = "MAXEXPALWD";
    public static final String AUTOSYNC = "AUTOSYNC";
    public static final String DATEFORMAT = "DATEFORMAT";
    public static final String SMINVITMNO = "SMINVITMNO";
    public static final String NOITMZEROS = "NOITMZEROS";
    public static final String TypesName = "TypesName";
    public static final String Typesname = "Typesname";
    public static final String PROP_ATTTYP = "ATTTYP";
    public static final String PROP_ACTTYP = "ACTTYP";
    public static final String VisitSeqId = "VisitSeqId";
    public static final String City = "City";
    public static final String SalesDistrictCode = "SalesDistrict";
    public static final String SalesDistrictDesc = "SalesDistrictDesc";
    public static final String CPUID = "CPUID";
    public static final String Address = "Address";
    public static final String ParentId = "ParentId";
    public static final String WeeklyOff = "WeeklyOff";
    public static final String CvgValue = "CvgValue";
    public static final String UOMNO0 = "UOMNO0";
    public static final String Material = "Material";
    public static final String Device = "DeviceData";
    public static final String NonDevice = "NonDeviceData";
    public static final String ExpenseFreq = "ExpenseFreq";
    public static final String ExpenseDaily = "000010";
    public static final String ExpenseMonthly = "000030";
    public static final String ExpenseType = "ExpenseType";
    public static final String LocationDesc = "LocationDesc";
    public static final String ExpenseTypeDesc = "ExpenseTypeDesc";
    public static final String ExpenseItemType = "ExpenseItemType";
    public static final String ExpenseItemTypeDesc = "ExpenseItemTypeDesc";
    public static final String ExpenseFreqDesc = "ExpenseFreqDesc";
    public static final String ExpenseItemCat = "ExpenseItemCat";
    public static final String ExpenseItemCatDesc = "ExpenseItemCatDesc";
    public static final String DefaultItemCat = "DefaultItemCat";
    public static final String DefaultItemCatDesc = "DefaultItemCatDesc";
    public static final String AmountCategory = "AmountCategory";
    public static final String AmountCategoryDesc = "AmountCategoryDesc";
    public static final String MaxAllowancePer = "MaxAllowancePer";
    public static final String ExpenseQuantityUom = "ExpenseQuantityUom";
    public static final String ItemFieldSet = "ItemFieldSet";
    public static final String ItemFieldSetDesc = "ItemFieldSetDesc";
    public static final String Allowance = "Allowance";
    public static final String IsSupportDocReq = "IsSupportDocReq";
    public static final String IsRemarksReq = "IsRemarksReq";
    public static final String ExpenseGUID = "ExpenseGUID";
    public static final String FiscalYear = "FiscalYear";
    public static final String ExpenseNo = "ExpenseNo";
    public static final String ExpenseDate = "ExpenseDate";
    public static final String ExpenseItemGUID = "ExpenseItemGUID";
    public static final String ExpeseItemNo = "ExpeseItemNo";
    public static final String ConvenyanceMode = "ConvenyanceMode";
    public static final String ConvenyanceModeDs = "ConvenyanceModeDs";
    public static final String Distance = "Distance";
    public static final String BeatDistance = "BeatDistance";
    public static final String ConveyanceAmt = "ConveyanceAmt";
    public static final String ExpenseDocumentID = "ExpenseDocumentID";
    // SO Create Properties
    public static final String TestRun_Text = "M";
    public static final String ItemCatID = "ItemCatID";
    public static final String str_0 = "0";
    public static final String INVOICE_ITEM = "InvoiceItems";
    public static final String SSInvoice = "SSInvoice";
    public static final String SSSO = "SSSO";
    public static final String DocumentTypeID = "DocumentTypeID";
    public static final String DocumentTypeDesc = "DocumentTypeDesc";
    public static final String DocumentStatusID = "DocumentStatusID";
    public static final String DocumentStatusDesc = "DocumentStatusDesc";
    public static final String DocumentMimeType = "DocumentMimeType";
    public static final String DocumentSize = "DocumentSize";
    public static final String red_hex_color_code = "#2cb037";
    public static final String BirthdayAlertsCount = "BirthdayAlertsCount";
    public static final String TextAlertsCount = "TextAlertsCount";
    public static final String AppointmentAlertsCount = "AppointmentAlertsCount";
    /*mime type*/
    public static final String MimeTypePDF = "application/pdf";
    public static final String MimeTypeXLS = "application/vnd.ms-excel";
    public static final String MimeTypeXLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MimeTypeMP4 = "application/octet-stream";
    public static final String MimeTypevideomp4 = "video/mp4";
    public static final String MimeTypePPT = "application/ppt";
    public static final String MimeTypePPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String MimeTypeDocx = "application/docx";
    public static final String MimeTypevndmspowerpoint = "application/vnd.ms-powerpoint";
    public static final String MimeTypeMsword = "application/msword";
    public static final String MimeTypeDOCx = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MimeTypePng = "image/png";
    public static final String MimeTypeJpg = "image/jpg";
    public static final String MimeTypeJpeg = "image/jpeg";
    public static final String MimeTypeUrl = "text/url";
    /*app preference*/
    public static final String KEY_FIRST_TIME_RUN = "firstTimeRun";
    public static final String NOT_POSTED_RETAILERS = "notPostedRetailers";
    public static final String NOT_POSTED_RETAILERS_ERROR_MGS = "notPostedRetailersErrorMsg";
    public static final String PREFS_NAME = "mSFAPreference";
    public static final String LOGPREFS_NAME = "mSFALogPreference";
    public static final String isReIntilizeDB = "isReIntilizeDB";
    public static final String USERROLE = "UserRole";
    public static final String USERROLELOGINID = "USERROLELOGINID";
    public static final String USERPARTNERTYPE = "USERPARTNERTYPE";
    public static final String isRollResponseGot = "isRollResponseGot";
    public static final String isUserPartnerResponseGot = "isUserPartnerResponseGot";
    /*key extra*/
    public static final String EXTRA_COME_FROM = "comeFrom";
    public static final String EXTRA_BEAN = "onBean";
    public static final String EXTRA_POS = "extraPOS";
    public static final String EXTRA_BEAN_LIST = "onBeanList";
    public static final String isMaterialEnabled = "isMaterialEnabled";
    public static final String EXTRA_SO_HEADER = "Header";
    /*store*/
    public static final String EncryptKey = "welcome1";
    public static final String STORE_NAME = "mSecSales_Offline";
    public static final String backupDBPath = "mSecSales_Offline.udb";
    public static final String backuprqDBPath = "mSecSales_Offline.rq.udb";
    public static final String arteria_dayfilter = "x-arteria-daysfilter";
    public static final String arteria_attfilter = "x-arteria-vst";
    public static final String NO_OF_DAYS = "0";
    public static final String arteria_session_header = "x-arteria-loginid";
    public static final String SampleDisbursementDesc = "Sample Disbursement";
    public static final String SampleDisbursement = "SampleDisbursement";
    public static final String FinancialPostings = "FinancialPostings";
    public static final String FinancialPostingItemDetails = "FinancialPostingItemDetails";
    public static final String ReturnOrderCreate = "Return Order Create";
    public static final String SOItemDetails = "SOItemDetails";
    public static final String SOItems = "SOItems";
    public static final String SOTexts = "SOTexts";
    public static final String UserSalesPersons = "UserSalesPersons";
    public static final String KPISet = "KPISet";
    public static final String Customers = "Customers";
    public static final String Targets = "Targets";
    public static final String TargetItems = "TargetItems";
    public static final String SyncHistorys = "SyncHistorys";
    public static final String UserPartners = "UserPartners";
    public static final String SyncHistorysENTITY = ".SyncHistory";
    public static final String KPIItems = "KPIItems";
    public static final String UserCustomers = "UserCustomers";
    public static final String CompetitorMasters = "CompetitorMasters";
    public static final String CompetitorInfos = "CompetitorInfos";
    public static final String PendingCompetitorInfos = "Competitor Infos";
    public static final String SSOutstandingInvoices = "SSOutstandingInvoices";
    public static final String SSOutstandingInvoiceItemDetails = "SSOutstandingInvoiceItemDetails";
    public static final String PricingConditions = "PricingConditions";
    public static final String Claims = "Claims";
    public static final String ClaimItemDetails = "ClaimItemDetails";
    public static final String ClaimDocuments = "ClaimDocuments";
    public static final String ComplaintDocuments = "ComplaintDocuments";
    public static final String TextCategorySet = "TextCategorySet";
    public static final String MerchReviewImages = "MerchReviewImages";
    public static final String ExpenseDocuments = "ExpenseDocuments";
    public static final String ExpenseAllowances = "ExpenseAllowances";
    public static final String Expenses = "Expenses";
    public static final String ExpenseItemDetails = "ExpenseItemDetails";
    public static final String SchemeCPDocuments = "SchemeCPDocuments";
    public static final String SalesPersons = "SalesPersons";
    public static final String UserProfileAuthSet = "UserProfileAuthSet";
    public static final String UserLogins = "UserLogins";
    public static final String UserProfiles = "UserProfiles";
    public static final String Attendances = "Attendances";
    public static final String Visits = "Visits";
    public static final String VisitActivities = "VisitActivities";
    public static final String PendingVisitActivities = "Visit Activities";
    public static final String RoutePlans = "RoutePlans";
    public static final String RouteSchedules = "RouteSchedules";
    public static final String RouteSchedulePlans = "RouteSchedulePlans";
    public static final String ChannelPartners = "ChannelPartners";
    public static final String CPDMSDivisons = "CPDMSDivisons";
    public static final String ValueHelps = "ValueHelps";
    public static final String ExpenseConfigs = "ExpenseConfigs";
    public static final String ConfigTypsetTypeValues = "ConfigTypsetTypeValues";
    public static final String MerchReviews = "MerchReviews";
    public static final String PendingMerchReviews = "Merchandising Reviews";
    public static final String ConfigTypesetTypes = "ConfigTypesetTypes";
    public static final String CPDMSDivisions = "CPDMSDivisions";
    public static final String CPPartnerFunctions = "CPPartnerFunctions";
    public static final String SSSOs = "SSSOs";
    public static final String SSSOItemDetails = "SSSOItemDetails";
    public static final String Documents = "Documents";
    public static final String SSSoItemDetails = "SSSOItemDetails";
    public static final String Performances = "Performances";
    public static final String SSINVOICES = "SSInvoices";
    public static final String SSInvoiceItemDetails = "SSInvoiceItemDetails";
    public static final String CPStockItemDetails = "CPStockItemDetails";
    public static final String CPStockItemSnos = "CPStockItemSnos";
    public static final String CPStockItems = "CPStockItems";
    public static final String SegmentedMaterials = "SegmentedMaterials";
    public static final String SSInvoiceTypes = "SSInvoiceTypes";
    public static final String Schemes = "Schemes";
    /*log msg*/
    public static final String OfflineStoreOpenFailed = "offlineStoreOpenFailed";
    public static final String OfflineStoreOpenedFailed = "Offline store opened failed";
    public static final String OfflineStoreStateChanged = "offlineStoreStateChanged";
    public static final String OfflineStoreOpenFinished = "offlineStoreOpenFinished";
    public static final String RequestFlushResponse = "requestFlushResponse - status code ";
    public static final String OfflineStoreRequestFailed = "offlineStoreRequestFailed";
    public static final String PostedSuccessfully = "posted successfully";
    public static final String SynchronizationCompletedSuccessfully = "Synchronization completed successfully";
    public static final String OfflineStoreFlushStarted = "offlineStoreFlushStarted";
    public static final String OfflineStoreFlushFinished = "offlineStoreFlushFinished";
    public static final String OfflineStoreFlushSucceeded = "offlineStoreFlushSucceeded";
    public static final String OfflineStoreFlushFailed = "offlineStoreFlushFailed";
    public static final String FlushListenerNotifyError = "FlushListener::notifyError";
    public static final String offlineStoreRequestFailed = "offlineStoreRequestFailed";
    public static final String offline_store_not_closed = "Offline store not closed: ";
    public static final String invalid_payload_entityset_expected = "Invalid payload:EntitySet expected but got ";
    public static final String RequestCacheResponse = "requestCacheResponse";
    public static final String RequestFailed = "requestFailed";
    public static final String Status_message = "status message";
    public static final String Status_code = "status code";
    public static final String RequestFinished = "requestFinished";
    public static final String RequestServerResponse = "requestServerResponse";
    public static final String BeforeReadRequestServerResponse = "Before Read requestServerResponse";
    public static final String BeforeReadentity = "Before Read entity";
    public static final String AfterReadentity = "After Read entity";
    public static final String RequestStarted = "requestStarted";
    public static final String OfflineRequestListenerNotifyError = "OfflineRequestListener::notifyError";
    public static final String ErrorWhileRequest = "Error while request";
    public static final String error_txt = "Error :";
    public static final String OfflineStoreRefreshStarted = "OfflineStoreRefreshStarted";
    public static final String OfflineStoreRefreshSucceeded = "OfflineStoreRefreshSucceeded";
    public static final String OfflineStoreRefreshFailed = "OfflineStoreRefreshFailed";
    public static final String SyncOnRequestSuccess = "Sync::onRequestSuccess";
    public static final String ERROR_ARCHIVE_COLLECTION = "ErrorArchive";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_METHOD = "RequestMethod";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_BODY = "RequestBody";
    public static final String ERROR_ARCHIVE_ENTRY_HTTP_CODE = "HTTPStatusCode";
    public static final String ERROR_ARCHIVE_ENTRY_MESSAGE = "Message";
    public static final String ERROR_ARCHIVE_ENTRY_CUSTOM_TAG = "CustomTag";
    public static final String ERROR_ARCHIVE_ENTRY_REQUEST_URL = "RequestURL";
    public static final String error = "error";
    public static final String message = "message";
    public static final String value = "value";
    public static final String error_txt1 = "Error";
    public static final String error_archive_called_txt = "Error Arcive is called";
    public static final String Periodicity = "Periodicity";
    /*tcode*/
    public static final String isAdhocVisitEnabled = "isAdhocVisitEnabled";
    public static final String isAdhocVistTcode = "/ARTEC/ADHOC_VST";
    public static final String isTCPCEnabled = "isTCPCEnabled";
    public static final String isTCPCTcode = "/ARTEC/SS_DAYSM_TCPC";
    public static final String isBeatEnabled = "isBeatEnabled";
    public static final String isBeatTcode = "/ARTEC/SS_DAYSM_BEAT";
    public static final String isBrandEnabled = "isBrandEnabled";
    public static final String isBrandTcode = "/ARTEC/SS_DAYSM_BND";
    public static final String isMyTargetsEnabled = "isMyTargetsEnabled";
    public static final String isMyTargetsTcode = "/ARTEC/SS_MYTRGTS";
    public static final String isBehaviourEnabled = "isBehaviourEnabled";
    public static final String isBehaviourTcode = "/ARTEC/SS_SPCP_EVAL";
    public static final String isSchemeEnabled = "isSchemeEnabled";
    public static final String isSchemeTcode = "/ARTEC/SS_SCHEMES";
    public static final String isVisualAidEnabled = "isVisualAidEnabled";
    public static final String isVisualAidTcode = "/ARTEC/SS_VSLADS";
    public static final String isDigitalProductEntryEnabled = "isDigitalProductEntryEnabled";
    public static final String isDigitalProductEntryTcode = "/ARTEC/SS_DGTPRD";
    public static final String isDBStockEnabled = "isDBStockEnabled";
    public static final String isDBStockTcode = "/ARTEC/SS_DBSTK";
    public static final String isSOCreateKey = "isSOCreate";
    public static final String isSOCreateTcode = "/ARTEC/SS_SOCRT";
    public static final String isCollCreateEnabledKey = "isCollCreateEnabled";
    public static final String isCollCreateTcode = "/ARTEC/SS_FICRT";
    public static final String isSampleDisbursmentEnabledKey = "isSampleDisbursmentCreateEnabled";
    public static final String isSampleDisbursmentCreateTcode = "/ARTEC/SS_SAMPCRT";
    public static final String isCustomerComplaintEnabledKey = "isCustomerComplaintCreateEnabled";
    public static final String isCustomerComplaintCreateTcode = "/ARTEC/SF_CUSTCOMPCRT";
    public static final String isMerchReviewKey = "isMerCreateEnabled";
    public static final String isMerchReviewTcode = "/ARTEC/SS_MERRVW";
    public static final String isMerchReviewListKey = "isMerCreateListEnabled";
    public static final String isMerchReviewListTcode = "/ARTEC/SS_MERRVWLST";
    public static final String isStockListKey = "isStocksListEnabled";
    public static final String isStockListTcode = "/ARTEC/SS_CPSTKLIST";
    public static final String isFeedBackListKey = "isFeedBackListEnabled";
    public static final String isFeedBackListTcode = "/ARTEC/SF_FDBKLIST";
    public static final String isSecondarySalesListKey = "isSecondarySalesListEnabled";
    public static final String isSecondarySalesListTcode = "/ARTEC/SS_SOLIST";
    public static final String isSecondaryInvoiceListTcode = "/ARTEC/SS_INVHIS";
    public static final String isSecondaryInvoiceListKey = "isSecondaryInvoiceListEnabled";
    public static final String isCollListTcode = "/ARTEC/SS_COLLHIS";
    public static final String isCollListKey = "isCollListEnabled";
    public static final String isOutstandingListTcode = "/ARTEC/SS_OUTSTND";
    public static final String isOutstandingListKey = "isOutstandingListEnabled";
    public static final String isMustSellKey = "isMustSellEnabled";
    public static final String isMustSellTcode = "/ARTEC/MC_MSTSELL";
    public static final String isFocusedProductKey = "isFocusedProductEnabled";
    public static final String isFocusedProductTcode = "/ARTEC/SS_FOCPROD";
    public static final String isNewProductKey = "isNewProductEnabled";
    public static final String isNewProductTcode = "/ARTEC/SS_NEWPROD";
    public static final String isCompInfoEnabled = "isCompInfoEnabled";
    public static final String isCompInfoTcode = "/ARTEC/SS_COMPINFO";
    public static final String isCompetitorListKey = "isCompetitorListEnabled";
    public static final String isCompetitorListTcode = "/ARTEC/SF_COMPINFOLST";
    public static final String isFeedbackCreateKey = "isFeedbackCreateEnabled";
    public static final String isFeedbackTcode = "/ARTEC/SS_FDBKCRT";
    public static final String isInvoiceCreateKey = "isCreateInvoiceEnabled";
    public static final String isInvoiceTcode = "/ARTEC/SS_INVCRT";
    public static final String isReturnOrderCreateEnabled = "isReturnOrderCreateEnabled";
    public static final String isReturnOrderTcode = "/ARTEC/SS_RETURNORDER";
    public static final String isDaySummaryKey = "isDaySummaryEnabled";
    public static final String isDaySummaryTcode = "/ARTEC/SS_DAYSMRY";
    public static final String isDlrBehaviourKey = "isBehaviourEnabled";
    public static final String isDlrBehaviourTcode = "/ARTEC/SS_SPCP_EVAL";
    public static final String isRetailerStockKey = "isRetailerStock";
    public static final String isRetailerStockTcode = "/ARTEC/SS_CPSTK";
    public static final String isVisitSummaryKey = "isVisitSummaryEnabled";
    public static final String isVisitSummaryTcode = "/ARTEC/SS_VISTSMRY";
    public static final String isExpenseEntryKey = "isExpenseEntryEnabled";
    public static final String isExpenseEntryTcode = "/ARTEC/SS_EXP_ENTRY";
    public static final String isExpenseListKey = "isExpenseListEnabled";
    public static final String isisVSTSUMKey = "isisVSTSUMEnabled";
    public static final String isExpenseListTcode = "/ARTEC/SS_EXPLIST";
    public static final String isVSTSUMTcode = "/ARTEC/SS_VST_SUMY";
    public static final String isReturnOrderListKey = "isReturnOrderListEnabled";
    public static final String isReturnOrderListTcode = "/ARTEC/SS_ROLIST";
    public static final String isSampleDisbursmentListKey = "isSampleDisbursmentListEnabled";
    public static final String isSampleDisbursmentListTcode = "/ARTEC/SS_SMPLST";
    public static final String isCustomerComplaintListKey = "isCustomerComplaintListEnabled";
    public static final String isCustomerComplaintListTcode = "/ARTEC/SS_CUSTCOMPLST";
    public static final String isRetailerListEnabled = "isRetailerListEnabled";
    public static final String isRetailerListTcode = "/ARTEC/SS_CP_GETLST";
    public static final String isRouteEnabled = "isRouteEnabled";
    public static final String isRoutePlaneTcode = "/ARTEC/SS_ROUTPLAN";
    public static final String isCreateRetailerKey = "isCreateRetailerEnabled";
    public static final String isCreateRetailerTcode = "/ARTEC/SS_CP_CRT";
    public static final String isRetailerTrendKey = "isRetailerTendEnabled";
    public static final String isRetailerTrendTcode = "/ARTEC/SS_TRENDS";
    /*store related error*/
    public static final String Error = "Error";
    public static final String Error_code_missing = "";
    public static final String Fresh = "Fresh";
    public static final String MerchList = "MerchList";
    public static final String Merchendising_Snap = "Merchendising Snapshot";
    public static final String BeatPlan = "BeatPlan";
    public static final String AdhocVisitCatID = "02";
    public static final String BeatVisitCatID = "01";
    public static final String OtherBeatVisitCatID = "02";
    public static final String X = "X";
    public static final String isLocalFilterQry = "?$filter= sap.islocal() ";
    public static final String isNonLocalFilterQry = "?$filter= not sap.islocal() ";
    public static final String device_reg_failed_txt = "Device registration failed";
    public static final String SHOWNOTIFICATION = "SHOWNOTIFICATION";
    public static final String timeStamp = "TimeStamp";
    public static final String sync_table_history_txt = "Sync table(History)";
    public static final String str_00 = "00";
    public static final String str_01 = "01";
    public static final String str_2 = "2";
    public static final String str_04 = "04";
    public static final String str_03 = "03";
    public static final String str_3 = "3";
    public static final String str_1 = "1";
    public static final String str_06 = "06";
    public static final String str_05 = "05";
    public static final String str_20 = "20";
    public static final String SecondarySOCreate = "Secondary SO Create";
    public static final String SecondarySOCreateTemp = "Secondary SO Create Temp";
    public static final String RetailerStock = "RetailerStock";
    public static final String PendingRetailerStock = "Retailer Stock";
    public static final String username = "username";
    public static final String usernameExtra = "usernameExtra";

    public static final String soRefrenceNoToRemove = "soRefrenceNoToRemove";
    public static final String ITEM_TXT = "ITEMS";
    public static final String Requestsuccess_status_message_key = "requestsuccess - status message key";
    public static final String RequestFailed_status_message = "requestFailed - status message ";
    public static final String RequestServerResponseStatusCode = "requestServerResponse - status code";
    public static final String OnlineRequestListenerNotifyError = "OnlineRequestListener::notifyError";
    public static final String FeedbackCreated = "Feedback created";
    public static final String ExpenseCreated = "Expense created";
    public static final String RequestsuccessStatusMessageBeforeSuccess = "requestsuccess - status message before success";
    public static final String ORG_MONTHS[] = {"Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String NEW_MONTHSCODE[] = {"11", "12", "01", "02",
            "03", "04", "05", "06", "07", "08", "09", "10"};
    public static final String RequestID = "RequestID";
    public static final String RepeatabilityCreation = "RepeatabilityCreation";
    public static final String T = "T";
    public static final String APP_UPGRADE_TYPESET_VALUE = "MSEC";
    // Default Names
    public static final String RetailerApprovalList = "RetailerApprovalList";
    public static final String RetailerList = "RetailerList";
    public static final String ItemList = "ItemList";
    public static final String Retailer = "Retailer";
    public static final String NotPostedRetailer = "NotPostedRetailer";
    public static final String NAVFROM = "NAVFROM";
    public static final String AdhocList = "AdhocList";
    public static final String comingFrom = "ComingFrom";
    public static final String VisitType = "VisitType";
    public static final String OtherRouteGUID = "OtherRouteGUID";
    public static final String DistubutorID = "DistubutorID";
    public static final String OtherRouteName = "OtherRouteName";
    public static final String BeatType = "BeatType";
    public static final String EXTRA_TITLE = "extraTitle";
    public static final String MTPList = "MTPList";
    public static final String tel_txt = "tel:";
    //    public static String ComingFromCreateSenarios = "";
    public static final String Visit = "Visit";
    public static final String Reports = "Reports";
    public static final String Summary = "Summary";
    public static final String RetName = "RetName";
    public static final String TimeStamp = "TimeStamp";
    public static final String SyncTableHistory = "Sync table(History)";
    public static final String delete_from = "DELETE FROM ";
    public static final String default_value_double = "0.0";
    public static final String default_value_int = "0";
    public static final String create_table = "create table IF NOT EXISTS ";
    public static final String EventsData = "EventsData";
    public static final String on_Create = "onCreate:";
    public static final String[] reportsArray = {"SOs", "ROs", "Invoices", "Collections", "Merchandising", "Feedbacks", "Outstandings", "Competitor Info", "Complaints", "Retailer Trends", "Sample Disbursement"};
    public static final String whatsapp_packagename = "com.whatsapp";
    public static final String whatsapp_conv_packagename = "com.whatsapp.Conversation";
    public static final String whatsapp_domainname = "@s.whatsapp.net";
    public static final String jid = "jid";
    public static final String sms_txt = "sms:";
    public static final String plain_text = "plain/text";
    public static final String send_email = "Send your email in:";
    public static final String PartnerGUID = "PartnerGUID";
    public static final String PartnerNo = "PartnerNo";
    public static final String TargetQty = "TargetQty";
    public static final String ActualQty = "ActualQty";
    public static final String TargetValue = "TargetValue";
    public static final String ActualValue = "ActualValue";
    public static final String TargetGUID = "TargetGUID";
    public static final String MaterialNo = "MaterialNo";
    public static final String HigherLevelItemNo = "HigherLevelItemNo";
    public static final String OrdMatGrp = "OrdMatGrp";
    public static final String MaterialGroup = "MaterialGroup";
    public static final String MaterialGrpDesc = "MaterialGrpDesc";
    public static final String KPICode = "KPICode";
    public static final String KPIName = "Name";
    public static final String KPIFor = "KPIFor";
    public static final String CalculationSource = "CalculationSource";
    public static final String CalculationBase = "CalculationBase";
    public static final String KPICategory = "KPICategory";
    public static final String SPChannelEvaluationList = "SPChannelEvaluationList";
    public static final String Month = "Month";
    public static final String Year = "Year";
    public static final String Period = "Period";
    public static final String RollUpTo = "RollUpTo";
    public static final String RollupStatus = "RollupStatus";
    public static final String RollupStatusDesc = "RollupStatusDesc";
    public static final String QtyMonth1PrevPerf = "QtyMonth1PrevPerf";
    public static final String QtyMonth2PrevPerf = "QtyMonth2PrevPerf";
    public static final String QtyMonth3PrevPerf = "QtyMonth3PrevPerf";
    public static final String ReportOnID = "ReportOnID";
    public static final String AlternativeUOM1 = "AlternativeUOM1";
    public static final String AlternativeUOM2 = "AlternativeUOM2";
    /*bundle*/
    public static final String BUNDLE_RESOURCE_PATH = "resourcePath";
    public static final String BUNDLE_OPERATION = "operationBundle";
    public static final String BUNDLE_REQUEST_CODE = "requestCodeBundle";
    public static final String BUNDLE_SESSION_ID = "sessionIdBundle";
    public static final String BUNDLE_SESSION_REQUIRED = "isSessionRequired";
    public static final String BUNDLE_SESSION_URL_REQUIRED = "isSessionTOUrlRequired";
    public static final String BUNDLE_SESSION_TYPE = "sessionTypeBundle";
    public static final String BUNDLE_IS_BATCH_REQUEST = "isBatchRequestBundle";
    public static final String EXTRA_SESSION_REQUIRED = "isSessionRequired";
    public static final String Tasks = "Tasks";
    public static final String CustomerCreditLimitDocs = "CustomerCreditLimitDocs";
    public static final String TaskHistorys = "TaskHistorys";
    public static final String GRStatusID = "GRStatusID";
    public static final String StorLoc = "StorLoc";
    public static final String Plant = "Plant";
    public static final String PlantDesc = "PlantDesc";
    public static final String DelvQty = "DelvQty";
    public static final String SalesAreaDesc = "SalesAreaDesc";
    public static final String UnloadingPoint = "UnloadingPoint";
    public static final String ReceivingPoint = "ReceivingPoint";
    public static final String PaytermDesc = "PaytermDesc";
    public static final String Payterm = "Payterm";
    public static final String Incoterm1 = "Incoterm1";
    public static final String Incoterm2 = "Incoterm2";
    public static final String Incoterm1Desc = "Incoterm1Desc";
    public static final String ShippingTypeID = "ShippingTypeID";
    public static final String SalesGroup = "SalesGroup";
    public static final String MeansOfTranstyp = "MeansOfTranstyp";
    public static final String MeansOfTranstypDesc = "MeansOfTranstypDesc";
    public static final String SOPartnerFunctions = "SOPartnerFunctions";
    public static final String PartnerFunctionID = "PartnerFunctionID";
    public static final String RegionDesc = "RegionDesc";
    public static final String CountryDesc = "CountryDesc";
    public static final String PartnerFunctionDesc = "PartnerFunctionDesc";
    public static final String VendorNo = "VendorNo";
    public static final String VendorName = "VendorName";
    public static final String PersonnelName = "PersonnelName";
    public static final String PersonnelNo = "PersonnelNo";
    public static final String SALESORDERITEMS = "SalesOrderItems";
    public static final String ItemCategory = "ItemCategory";
    public static final String DepotStock = "DepotStock";
    public static final String DeviceNo = "DeviceNo";
    public static final String ORDER_TYPE = "ORDERTYPE";
    public static final String ORDER_TYPE_DESC = "ORDERTYPE_DESC";
    public static final String SALESAREA = "SALESAREA";
    public static final String SALESAREA_DESC = "SALESAREADESC";
    public static final String SOLDTO = "SOLDTO";
    public static final String SOLDTONAME = "SOLDTONAME";
    public static final String SHIPPINTPOINT = "SHIPPINTPOINT";
    public static final String SHIPPINTPOINTDESC = "SHIPPINTPOINTDESC";
    public static final String SHIPTO = "SHIPTO";
    public static final String SHIPTONAME = "SHIPTONAME";
    public static final String FORWARDINGAGENT = "FORWARDINGAGENT";
    public static final String FORWARDINGAGENTNAME = "FORWARDINGAGENTNAME";
    public static final String PLANT = "PLANT";
    public static final String PLANTDESC = "PLANTDSEC";
    public static final String INCOTERM1 = "INCOTERM1";
    public static final String INCOTERM1DESC = "INCOTERM1DESC";
    public static final String INCOTERM2 = "INCOTERM2";
    public static final String SALESDISTRICT = "SALESDISTRICT";
    public static final String SALESDISTRICTDESC = "SALESDISTRICTDESC";
    public static final String ROUTE = "ROUTE";
    public static final String ROUTEDESC = "ROUTEDESC";
    public static final String MEANSOFTRANSPORT = "MEANSOFTRANSPORT";
    public static final String MEANSOFTRANSPORTDESC = "MEANSOFTRANSPORTDESC";
    public static final String STORAGELOC = "STORAGELOC";
    public static final String CUSTOMERPO = "CUSTOMERPO";
    public static final String CUSTOMERPODATE = "CUSTOMERPODATE";
    public static final String SalesOfficeDesc = "SalesOfficeDesc";
    public static final String SalesOrderItems = "SalesOrderItems";
    public static final String StorLocDesc = "StorLocDesc";
    public static final String StatusUpdate = "StatusUpdate";
    public static final String OrderTypeText = "OrderTypeText";
    public static final int PERMISSION_REQUEST_CODE = 110;
    public static final int STORAGE_PERMISSION = 891;
    public final static String ExpenseListBean = "ExpenseListBean";
    /*permission Request*/
    public static final int CAMERA_PERMISSION_CONSTANT = 890;
    public static final String SSRO = "SSSO";
    public static final String CustomerComplaintsCreate = "Consumer Complaints Create";
    public static final String str_false = "false";
    public static final String Complaints = "Complaints";
    public static final String COMPLAINTLISTMODEL = "complaintlistmodel";
    public static final String OtherRouteList = "OtherRouteList";
    public static final String SYNC_TABLE = "SyncTable";
    public static final String CPList = "CPList";
    public static final String StateDesc = "StateDesc";
    public static String SchemeCPsEntity = ".SchemeCP";
    public static String ClaimsEntity = ".Claim";
    public static String ComingFromCreateSenarios = "";
    public static String OtherRouteNameVal = "";
    public static String OtherRouteGUIDVal = "";
    public static String Conv_Mode_Type_Other = "0000000001";
    public static boolean FLAG = false;
    public static String WindowDisplayID = "11";
    public static String WindowDisplayClaimID = "13";
    public static String WindowDisplayValueHelp = "WindowDisplay";
    public static String EXTRA_ARRAY_LIST = "arrayList";
    public static String A = "A";
    public static String SchemeCPDocumentID = "SchemeCPDocumentID";
    public static String WindowHeight = "WindowHeight";
    public static String WindowBreadth = "WindowBreadth";
    public static String WindowLength = "WindowLength";
    public static String SchemeCPGUID = "SchemeCPGUID";
    public static String SchemeCPDocType = "SchemeCPDocType";
    public static String WindowSizeUOM = "WindowUOM";
    public static String DecisionKey = "DecisionKey";
    public static String Comments = "Comments";
    public static String RejectStatus = "02";
    public static String ApprovalStatus01 = "01";
    public static int LAST_SEL_VAL = 0;
    public static boolean Exportdbflag = false;
    /*sync Selection*/
    public static String duplicateCPList = "duplicateCPList";
    public static String ErrorList = "errorList";
    /*datavalt export*/
    public static String KeyNo = "KeyNo";
    public static String KeyValue = "KeyValue";
    public static String KeyType = "KeyType";
    public static String DataVaultData = "DataVaultData";
    public static String DataVaultFileName = "mSecSalesDataVault.txt";
    public static String MRP = "MRP";
    public static String EXTRA_SSRO_GUID = "extraSSROguid";
    public static String PERCENTAGE = "%";
    public static String EXTRA_ORDER_DATE = "extraDate";
    public static String EXTRA_ORDER_IDS = "extraIDS";
    public static String EXTRA_ORDER_AMOUNT = "extraAmount";
    public static String EXTRA_ORDER_SATUS = "extraStatus";
    public static String EXTRA_ORDER_CURRENCY = "extraCurrency";
    public static String EXTRA_CPGUID = "extraCPGUID";
    public static String EXTRA_STOCK_BEAN = "extraStockBean";
    public static String EXTRA_STOCK_OWNER = "extraStockOwner";
    public static String EXTRA_SEARCH_HINT = "extraSearchHint";
    public static String EXTRA_SCHEME_GUID = "extraSchemeGUID";
    public static String AttendanceGUID = "AttendanceGUID";
    public static String StartDate = "StartDate";
    public static String StartTime = "StartTime";
    public static String EndTime = "EndTime";
    public static String StartLat = "StartLat";
    public static String StartLong = "StartLong";
    public static String EndDate = "EndDate";
    public static String EndLat = "EndLat";
    public static String EndLong = "EndLong";
    public static String Etag = "Etag";
    public static String MediaLink = "mediaLink";
    public static String TextCategoryID = "TextCategoryID";
    public static String TextCategoryTypeID = "TextCategoryTypeID";
    public static String TextCategoryDesc = "TextCategoryDesc";
    public static String TextCategoryTypeDesc = "TextCategoryTypeDesc";
    public static String Text = "Text";
    public static String InvoiceHisMatNo = "MaterialNo";
    public static String InvoiceHisMatDesc = "MaterialDesc";
    public static String ZZIsBomMaterial = "ZZIsBomMaterial";
    public static String HigherLevelItm = "HigherLevelItm";
    public static String InvoiceHisAmount = "GrossAmount";
    public static String InvoiceHisQty = "Quantity";
    public static String CompName = "CompName";
    public static String CompGUID = "CompGUID";
    public static String CompInfoGUID = "CompInfoGUID";
    public static String Earnings = "Earnings";
    public static String SchemeAmount = "SchemeAmount";
    public static String SchemeName = "SchemeName";
    public static String SchemeGUID = "SchemeGUID";
    public static String ValidFromDate = "ValidFromDate";
    public static String ValidToDate = "ValidToDate";
    public static String MatGrp1Amount = "MatGrp1Amount";
    public static String MatGrp2Amount = "MatGrp2Amount";
    public static String MatGrp3Amount = "MatGrp3Amount";
    public static String MatGrp4Amount = "MatGrp4Amount";
    public static String UpdatedOn = "UpdatedOn";
    public static String PurchaseQty = "PurchaseQty";
    public static String PurchaseAmount = "PurchaseAmount";
    public static String DealerName = "DealerName";
    public static String DealerCode = "DealerCode";
    public static String DealerType = "DealerType";
    public static String MTDValue = "MTDValue";
    public static String OrderToRecivive = "OrderToRecivive";
    public static String DateofDispatch = "DateofDispatch";
    public static String TradeDate = "TradeDate";
    public static String PriceDate = "PriceDate";
    public static String BrandName = "BrandName";
    public static String HDPE = "HDPE";
    public static String PaperBag = "PaperBag";
    public static String PriceType = "PriceType";
    public static String diaryCheck = "diaryCheck";
    public static String chitPadCheck = "chitPadCheck";
    public static String bannerCheck = "bannerCheck";
    public static String AmountOne = "AmountOne";
    public static String DateOne = "DateOne";
    public static String AmountTwo = "AmountTwo";
    public static String DateTwo = "DateTwo";
    public static String AmountThree = "AmountThree";
    public static String DateThree = "DateThree";
    public static String AmountFour = "AmountFour";
    public static String DateFour = "DateFour";
    public static String CPNo = "CPNo";
    public static String FromCPTypID = "FromCPTypID";
    public static String FromCPTypeDesc = "FromCPTypeDesc";
    public static String RetailerName = "Name";
    public static String Address2 = "Address2";
    public static String Address3 = "Address3";
    public static String Address4 = "Address4";
    public static String ApprovedAt = "ApprovedAt";
    public static String ApprovedOn = "ApprovedOn";
    public static String ApprvlStatusDesc = "ApprvlStatusDesc";
    public static String BlockID = "BlockID";
    public static String BlockName = "BlockName";
    public static String BuiltupArea = "BuiltupArea";
    public static String BuiltupAreaUom = "BuiltupAreaUom";
    public static String BusinessID1 = "BusinessID1";
    public static String BusinessID2 = "BusinessID2";
    public static String Landline = "Landline";
    public static String FaxNo = "FaxNo";
    public static String ID1 = "ID1";
    public static String ID2 = "ID2";
    public static String IsKeyCP = "IsKeyCP";
    public static String ConstructionStageDs = "ConstructionStageDs";
    public static String ConstructionStageID = "ConstructionStageID";
    public static String ApprovedBy = "ApprovedBy";
    public static String TownDesc = "TownDesc";
    public static String ParentID = "ParentID";
    public static String ParentName = "ParentName";
    public static String ParentTypDesc = "ParentTypDesc";
    public static String ParentTypeID = "ParentTypeID";
    public static String ParentTypeDesc = "ParentTypeDesc";
    public static String PartnerMgrName = "PartnerMgrName";
    public static String PartnerMgrNo = "PartnerMgrNo";
    public static String CPTypeDesc = "CPTypeDesc";
    public static String StatusID = "StatusID";
    public static String StatusIdRetailer = "01";
    public static String VisitSeq = "VisitSeq";
    public static String ZZDEVICE_ID = "ZZDeviceID";
    public static String ZZPARENT = "ZZParent";
    public static String Description = "Description";
    public static String EXTRA_COMPLAINT_BEAN = "ExtraComplaintBean";
    public static String CategoryId = "CategoryId";
    public static String VoiceBalance = "VoiceBalance";
    public static String DataBalance = "DataBalance";
    public static String Last111Date = "Last111Date";
    public static String OutstandingAmt = "OutstandingAmt";
    public static String LastInvAmt = "LastInvAmt";
    public static String NewLaunchedProduct = "New Launched Product";
    public static String MustSellProduct = "Must Sell Product";
    public static String FocusedProduct = "Focused Product";
    public static String SalesOrderCreate = "Sales Order Create";
    public static String StockGuid = "StockGuid";
    public static String MerchReviewGUID = "MerchReviewGUID";
    public static String SPNo = "SPNo";
    public static String SPName = "SPName";
    public static String SPGUID = "SPGUID";
    public static String MerchReviewType = "MerchReviewType";
    public static String MerchReviewTypeDesc = "MerchReviewTypeDesc";
    public static String MerchReviewTime = "MerchReviewTime";
    public static String CreatedBy = "CreatedBy";
    public static String AccountGrp = "AccountGrp";
    public static String History = "History";
    public static String Pending = "Pending Sync";
    public static String ActivationDate = "ActivationDate";
    public static String CreatedOn = "CreatedOn";
    public static String CreatedAt = "CreatedAt";
    public static String ChangedBy = "ChangedBy";
    public static String TestRun = "TestRun";
    public static String SPCategoryDesc = "SPCategoryDesc";
    public static String FeedbackNo = "FeedbackNo";
    public static String FeedbackDesc = "FeedbackDesc";
    public static String DeviceStatus = "DeviceStatus";
    public static String FeedBackGuid = "FeedBackGuid";
    public static String FeebackGUID = "FeebackGUID";
    public static String FeedbackType = "FeedbackType";
    public static String FeedbackTypeDesc = "FeedbackTypeDesc";
    public static String FeedbackSubTypeID = "FeedbackSubTypeID";
    public static String FeedbackSubTypeDesc = "FeedbackSubTypeDesc";
    public static String FeedbackDate = "FeedbackDate";
    public static String FeedbackSubType = "FeedbackSubType";
    public static String SPCategoryID = "SPCategoryID";
    public static String Location = "Location";
    public static String Location1 = "Location1";
    public static String BTSID = "BTSID";
    public static String Testrun = "Testrun";
    public static String FeebackItemGUID = "FeebackItemGUID";
    public static String MerchReviewDate = "MerchReviewDate";
    public static String MerchReviewLat = "MerchReviewLat";
    public static String MerchReviewLong = "MerchReviewLong";
    public static String MerchImageGUID = "MerchImageGUID";
    public static String ImageMimeType = "ImageMimeType";
    public static String ImageSize = "ImageSize";
    public static String Image = "Image";
    public static String ImagePath = "ImagePath";
    public static String ImageByteArray = "ImageByteArray";
    public static String DocumentStore = "DocumentStore";
    public static String FileName = "FileName";
    public static String PlannedDate = "PlannedDate";
    public static String PlannedStartTime = "PlannedStartTime";
    public static String PlannedEndTime = "PlannedEndTime";
    public static String VisitTypeID = "VisitTypeID";
    public static String VisitTypeDesc = "VisitTypeDesc";
    public static String VisitDate = "VisitDate";
    public static String RschGUID = "RschGUID";
    public static String VisitSummaryGUID = "VisitSummaryGUID";
    public static String VisitSummarySet = "VisitSummarySet";
    public static String NonProdNoOrder = "NonProdNoOrder";
    public static String NonProductive = "NonProductive";
    public static String ParntName = "ParntName";
    public static String ParntNo = "ParntNo";
    public static String ParntCPTypDesc = "ParntCPTypDesc";
    public static String ParntCPType = "ParntCPType";
    public static String Productive = "Productive";
    public static String ProposedRoute = "ProposedRoute";
    public static String ApprovedRoute = "ApprovedRoute";
    public static String RouteID = "RouteID";
    public static String RouteGUID = "RouteGUID";
    public static String RouteDesc = "RouteDesc";
    public static String RoutID = "RoutID";
    public static String SpName = "SpName";
    public static String SpNo = "SpNo";
    public static String TotalRetailers = "TotalRetailers";
    public static String VisitedRetailers = "VisitedRetailers";
    public static String VisitedRetailersMTD = "VisitedRetailersMTD";
    public static String RoutePlanKey = "RoutePlanKey";
    public static String PaymentStatusID = "PaymentStatusID";
    public static String PaymentModeID = "PaymentModeID";
    public static String PaymentMode = "PaymentMode";
    public static String PaymentModeDesc = "PaymentModeDesc";
    public static String PaymetModeDesc = "PaymetModeDesc";
    public static String BranchName = "BranchName";
    public static String InstrumentNo = "InstrumentNo";
    public static String InstrumentDate = "InstrumentDate";
    public static String ReferenceTypeID = "ReferenceTypeID";
    public static String ReferenceTypeDesc = "ReferenceTypeDesc";
    public static String Source = "Source";
    public static String Mobile = "MOBILE";
    public static String Source_SFA = "SFA";
    public static String BankID = "BankID";
    public static String BankName = "BankName";
    public static String Remarks = "Remarks";
    public static String Currency = "Currency";
    public static String RefDocItmGUID = "RefDocItmGUID";
    public static String Status = "Status";
    public static String ItemStatus = "ItemStatus";
    public static String RejectionReasonDesc = "RejectionReasonDesc";
    public static String RejectionReasonID = "RejectionReasonID";
    public static String RefDocNo = "RefDocNo";
    public static String OrderNo = "OrderNo";
    public static String SSSOGuid = "SSSOGuid";
    public static String SSROGUID = "SSROGUID";
    public static String SSROItemGUID = "SSROItemGUID";
    public static String NetPrice = "NetPrice";
    public static String Banner = "Banner";
    public static String NetAmount = "NetAmount";
    public static String OrderDate = "OrderDate";
    public static String Amount = "Amount";
    public static String FIPGUID = "FIPGUID";
    public static String ExtRefID = "ExtRefID";
    public static String FIPDocType = "FIPDocType";
    public static String FIPDocType1 = "FIPDocType1";
    public static String FIPDate = "FIPDate";
    public static String FIPDocNo = "FIPDocNo";
    public static String FIPAmount = "FIPAmount";
    public static String DebitCredit = "DebitCredit";
    public static String ParentNo = "ParentNo";
    public static String SPFirstName = "SPFirstName";
    public static String SalesDist = "SalesDist";
    public static String Route = "Route";
    public static String SplProcessing = "SplProcessing";
    public static String SplProcessingDesc = "SplProcessingDs";
    public static String MatFrgtGrp = "MatFrgtGrp";
    public static String MatFrgtGrpDesc = "MatFrgtGrpDs";
    public static String FIPItemGUID = "FIPItemGUID";
    public static String ReferenceID = "ReferenceID";
    public static String ReferenceDate = "ReferenceDate";
    public static String BalanceAmount = "BalanceAmount";
    public static String ClearedAmount = "ClearedAmount";
    public static String FIPItemNo = "FIPItemNo";
    public static String FirstName = "FirstName";
    public static String SalesOffice = "SalesOffice";
    public static String LastName = "LastName";
    public static String AttendanceTypeH1 = "AttendanceTypeH1";
    public static String AttendanceTypeH2 = "AttendanceTypeH2";
    public static String AutoClosed = "AutoClosed";
    public static String PerformanceOnIDDesc = "PerformanceOnIDDesc";
    public static String MaterialGroupID = "MaterialGroupID";
    public static String MaterialGroupDesc = "MaterialGroupDesc";
    public static String Material_Catgeory = "MaterialCategory";
    public static String DbBatch = "Batch";
    public static String ManufacturingDate = "ManufacturingDate";
    public static String Material_No = "MaterialNo";
    public static String Material_Desc = "MaterialDesc";
    public static String BaseUom = "BaseUom";
    public static String BasePrice = "BasePrice";
    public static String VisitActivityGUID = "VisitActivityGUID";
    public static String VisitGUID = "VisitGUID";
    public static String ActivityType = "ActivityType";
    public static String ActivityTypeDesc = "ActivityTypeDesc";
    public static String ActivityRefID = "ActivityRefID";
    public static boolean flagforexportDB;
    public static String Validity = "Validity";
    public static String Benefits = "Benefits";
    public static String Price = "Price";
    public static String ItemNo = "ItemNo";
    public static String SchemeDesc = "SchemeDesc";
    public static String ReviewDate = "ReviewDate";
    public static String CPTypeID = "CPTypeID";
    public static String SPGuid = "SPGuid";
    public static String SoldToCPGUID = "SoldToCPGUID";
    public static String BeatGuid = "BeatGuid";
    public static String BillToCPGUID = "BillToCPGUID";
    public static String ShipToCPGUID = "ShipToCPGUID";
    public static String BillToGuid = "BillToGuid";
    public static String SoldToTypeID = "SoldToTypeID";
    public static String SoldToTypDesc = "SoldToTypDesc";
    public static String ShipToTypeID = "ShipToTypeID";
    public static String CPName = "CPName";
    public static String Address1 = "Address1";
    public static String CountryID = "CountryID";
    //	public static String Country = "Country";
    public static String BTSCircle = "BTSCircle";
    public static String DesignationID = "DesignationID";
    public static String DesignationDesc = "DesignationDesc";
    //	public static String ParentTypeID = "ParentTypeID";
    public static String DistrictDesc = "DistrictDesc";
    public static String CityDesc = "CityDesc";
    public static String CityID = "CityID";
    public static String DistrictID = "DistrictID";
    public static String VisitNavigationFrom = "";
    public static String DBStockKey = "DBStockKey";
    public static String DBStockKeyDate = "DBStockKeyDate";
    public static String District = "District";
    public static String StateID = "StateID";
    public static String Region = "Region";
    public static String Country = "Country";
    public static String Landmark = "Landmark";
    public static String PostalCode = "PostalCode";
    public static String SalesPersonMobileNo = "MobileNo";
    public static String MobileNo = "Mobile1";
    public static String MobileNo1 = "MobileNo";
    public static String MobileNo2 = "Mobile1";
    public static String CPMobileNo = "CPMobileNo";
    public static String EmailID = "EmailID";
    public static String DOB = "DOB";
    public static String Anniversary = "Anniversary";
    public static String PAN = "PAN";
    public static String VATNo = "VATNo";
    public static String TIN = "TIN";
    public static String OwnerName = "OwnerName";
    public static String ZZVisitFlag = "ZZVisitFlag";
    public static String OutletName = "Name";
    public static String RetailerProfile = "Group1";
    public static String RetailerProfileText = "RetailerProfile";
    public static String CPGRP5 = "CPGRP5";
    public static String CPGRP4 = "CPGRP4";
    public static String CPGRP2 = "CPGRP2";
    public static String CPGRP3 = "CPGRP3";
    public static String Group2 = "Group2";
    public static String Group1 = "Group1";
    public static String Group3 = "Group3";
    public static String Group3Desc = "Group3Desc";
    public static String Group4 = "Group4";
    public static String Group5 = "Group5";
    public static String Group4Desc = "Group4Desc";
    public static String Latitude = "Latitude";
    public static String Longitude = "Longitude";
    public static String SetResourcePath = "SetResourcePath";
    public static String PartnerMgrGUID = "PartnerMgrGUID";
    public static String OtherCustGuid = "OtherCustGuid";
    public static String CPGUID32 = "CPGUID32";
    public static String CPGUID = "CPGUID";
    public static String PFGUID = "PFGUID";
    public static String PartnarCPGUID = "PartnarCPGUID";
    public static String PartnerCPNo = "PartnerCPNo";
    public static String PartnarName = "PartnarName";
    public static String PartnerMobileNo = "PartnerMobileNo";
    public static String PartnerFunction = "PartnerFunction";
    public static String CP1GUID = "CP1GUID";
    public static String PerformanceTypeID = "PerformanceTypeID";
    public static String AsOnDate = "AsOnDate";
    public static String CPGuid = "CPGuid";
    public static String OINVAG = "OINVAG";
    public static String COLLECTIONHDRS = "CollectionHdrs";
    public static String COLLECTIONITEMS = "CollectionItems";
    public static String OPEN_INVOICE_LIST = "OpenInvList";
    public static String INVOICES = "Invoices";
    public static String VISITACTIVITIES = "VisitActivities";
    public static String INVOICESSERIALNUMS = "InvoiceItmSerNumList";
    public static String ENDLONGITUDE = "EndLongitude";
    public static String REMARKS = "Remarks";
    public static String VISITKEY = "VisitGUID";
    public static String ROUTEPLANKEY = "RoutePlanGUID";
    public static String LOGINID = "LoginID";
    public static String DATE = "Date";
    public static String VISITTYPE = "VisitType";
    public static String CUSTOMERNO = "CustomerNo";
    public static String REASON = "Reason";
    public static String STARTDATE = "StartDate";
    public static String STARTTIME = "StartTime";
    public static String STARTLATITUDE = "StartLatitude";
    public static String STARTLONGITUDE = "StartLongitude";
    public static String ENDTIME = "EndTime";
    public static String ENDDATE = "EndDate";
    public static String ENDLATITUDE = "EndLatitude";
    public static String ETAG = "ETAG";
    public static String RschGuid = "RschGuid";
    public static String RouteSchGUID = "RouteSchGUID";
    public static String VisitCPGUID = "VisitCPGUID";
    public static String ViisitCPNo = "ViisitCPNo";
    public static String VisitCPName = "VisitCPName";
    public static String SalesPersonID = "SalesPersonID";
    public static String SalesPersonName = "SalesPersonName";
    public static String ShortName = "ShortName";
    public static String RoutId = "RoutId";
    public static String SequenceNo = "SequenceNo";
    public static String DayOfWeek = "DayOfWeek";
    public static String DayOfMonth = "DayOfMonth";
    public static String DOW = "DOW";
    public static String DOM = "DOM";
    public static String ComplaintCategory = "ComplaintCategory";
    public static String TypesValue = "TypeValue";
    public static String Name = "Name";
    public static String Mobile1 = "Mobile1";
    public static String WeeklyOffDesc = "WeeklyOffDesc";
    public static String TaxRegStatusDesc = "TaxRegStatusDesc";
    public static String TaxRegStatus = "TaxRegStatus";
    public static String TaxRegStatusDone = "0000000001";
    public static String TaxRegStatusNotDone = "0000000002";
    public static String Tax1 = "Tax1";
    public static String OrderType = "OrderType";
    public static String ApprvlStatusID = "ApprvlStatusID";
    public static String ApprovalStatusID = "ApprovalStatus";
    public static String ChangedAt = "ChangedAt";
    public static String ChangedOn = "ChangedOn";
    public static String RoutSchScope = "RoutSchScope";
    public static String DMSDivisionID = "DMSDivisionID";
    public static String DmsDivsionDesc = "DmsDivsionDesc";
    public static String DMSDivision = "DMSDivision";
    public static String DMSDivisionDesc = "DMSDivisionDesc";
    public static String SoldToId = "SoldToId";
    public static String SoldToName = "SoldToName";
    public static String SoldToID = "SoldToID";
    public static String ApprovalStatusDs = "ApprovalStatusDs";
    public static String ApprovalStatus = "ApprovalStatus";
    public static String FromCPGUID = "FromCPGUID";
    public static String FromCPNo = "FromCPNo";
    public static String FromCPTypeID = "FromCPTypeID";
    public static String ConditionPricingDate = "ConditionPricingDate";
    public static String SONo = "SONo";
    public static String DelvStatusID = "DelvStatusID";
    public static String Quantity = "Quantity";
    public static String SOUpdate = "SOUpdate";
    public static String SOCancel = "SOCancel";
    public static String TotalAmount = "TotalAmount";
    public static String InvoiceNo = "InvoiceNo";
    public static String ISFreeGoodsItem = "ISFreeGoodsItem";
    public static String InvoiceHisNo = "InvoiceNo";
    public static String InvoiceDate = "InvoiceDate";
    public static String InvoiceAmount = "InvoiceAmount";
    public static String InvoiceAmount1 = "GrossAmount";
    public static String InvoiceStatus = "StatusID";
    public static String InvoiceGUID = "InvoiceGUID";
    public static String StockOwner = "StockOwner";
    public static String ParentType = "ParentType";
    public static String StockTypeID = "StockTypeID";
    public static String UnrestrictedQty = "UnrestrictedQty";
    public static String SlabTypeID = "SlabTypeID";
    public static String SlabTypeDesc = "SlabTypeDesc";
    public static String IsIncludingPrimary = "IsIncludingPrimary";
    public static String SchemeCatID = "SchemeCatID";
    public static String SchemeCatDesc = "SchemeCatDesc";
    public static String OnSaleOfCatID = "OnSaleOfCatID";
    public static String OnSaleOfItemUOMDesc = "OnSaleOfItemUOMDesc";
    public static String OnSaleOfItemUOMID = "OnSaleOfItemUOMID";
    public static String OnSaleOfCatIDOrderMatGrp = "000005";
    public static String OnSaleOfCatIDMaterial = "000005";
    public static Boolean BoolMatWiseSchemeAvalible = false;
    public static Boolean BoolMatWiseQPSSchemeAvalible = false;
    public static String SchemeTypeID = "SchemeTypeID";
    public static String SchemeTypeDesc = "SchemeTypeDesc";
    public static String SchemeID = "SchemeID";
    public static String ValidFrom = "ValidFrom";
    public static String SKUGroupID = "SKUGroupID";
    public static String SKUGroupDesc = "SKUGroupDesc";
    public static String PriDiscountPer = "PriDiscountPer";
    public static String InvoiceTypeID = "InvoiceTypeID";
    public static String InvoiceTypeDesc = "InvoiceTypeDesc";
    public static String IsLatLongUpdate = "IsLatLongUpdate";
    public static String CreditDays = "CreditDays";
    public static String CreditBills = "CreditBills";
    public static String CreditLimit = "CreditLimit";
    public static String CreditPeriod = "CreditPeriod";
    public static String GrossAmt = "GrossAmt";
    public static String TAX = "TAX";
    public static String CPType = "CPType";
    public static String SecDiscount = "SecDiscount";
    public static String PriDiscount = "PriDiscount";
    public static String CashDiscount = "CashDiscount";
    public static String CashDiscountAmount = "CashDiscountAmount";
    public static String CashDiscountPercentage = "CashDiscountPercentage";
    public static String SecondaryDiscountPerc = "SecondaryDiscountPerc";
    public static String PrimaryDiscountPerc = "PrimaryDiscountPerc";
    public static String CashDiscountPerc = "CashDiscountPerc";
    public static String MFD = "MFD";
    public static String PONo = "PONo";
    public static String PODate = "PODate";
    public static String FromCPName = "FromCPName";
    public static String FromCPTypId = "FromCPTypId";
    public static String FromCPTypDs = "FromCPTypID";
    public static String SoldToUID = "SoldToUID";
    public static String SoldToDesc = "SoldToDesc";
    public static String SoldToType = "SoldToType";
    public static String SoldToTypDs = "SoldToTypDs";
    public static String SSSOItemGUID = "SSSOItemGUID";
    public static String OrderMatGrp = "OrderMatGrp";
    public static String OrderMatGrpDesc = "OrderMatGrpDesc";
    public static String OrdMatGrpDesc = "OrdMatGrpDesc";
    public static String ShipToName = "ShipToName";
    public static String OpenAdvanceAmt = "OpenAdvanceAmt";

    //SyncHistory
    public static String SyncHisGuid = "SyncHisGuid";
    public static String Collection = "Collection";
    public static String SyncDate = "SyncDate";
    public static String SyncTime = "SyncTime";
    public static String SyncTypeDesc = "SyncTypeDesc";
    public static String SyncType = "SyncType";
    public static String Application = "Application";
    public static String SyncHistroy = "SyncHistorys";
    public static String PartnerId = "PartnerId";
    public static String PartnerTypeID = "PartnerTypeID";
    public static String Sync_All = "000001";
    //    public static String All_DownLoad = "000002";
    public static String DownLoad = "000002";
    public static String UpLoad = "000003";
    public static String Auto_Sync = "000004";

    public static String Attnd_sync = "000005";
    public static String Attnd_refresh_sync = "000030";
    public static String Beat_sync = "000006";
    public static String Retailers_sync = "000007";
    public static String VisualAids_sync = "000008";
    public static String AdVst_sync = "000009";
    public static String Behav_sync = "000010";
    public static String DigitalPrd_sync = "000011";
    public static String VisitMTD_sync = "000012";
    public static String Schemes_sync = "000013";
    public static String SOPD_sync = "000014";
    public static String SOPOSTBG_sync = "000015";
    public static String SOPostPD_sync = "000016";
    public static String ROPD_sync = "000017";
    public static String ROPostPD_sync = "000018";
    public static String MerchPD_sync = "000019";
    public static String MerchPostPD_sync = "000020";
    public static String OSPD_sync = "000021";
    public static String SD_PD_sync = "000022";
    public static String SDPostPD_sync = "000023";
    public static String CI_PD_sync = "000024";
    public static String CIPostPD_sync = "000025";
    public static String FB_PD_sync = "000026";
    public static String FBPostPD_sync = "000027";
    public static String Collection_PD_sync = "000028";
    public static String CollectionPostPD_sync = "000029";
    public static String InvoicePD_sync = "000031";
    public static String Dashboard_sync = "000032";
    public static String Initial_sync = "000033";
    public static String download_all_cancel_sync = "000034";
    public static String download_all_net_sync = "000038";
    public static String download_cancel_sync = "000036";
    public static String upload_cancel_sync = "000035";
    public static String upload_net_sync = "000037";


    public static String EndSync = "End";
    public static String StartSync = "Start";
    // Schemes Properties
    public static String GeographyScopeID = "GeographyScopeID";
    public static String GeographyScopeDesc = "GeographyScopeDesc";
    public static String GeographyLevelID = "GeographyLevelID";
    public static String GeographyLevelDesc = "GeographyLevelDesc";
    public static String GeographyTypeID = "GeographyTypeID";
    public static String GeographyTypeDesc = "GeographyTypeDesc";
    public static String GeographyValueID = "GeographyValueID";
    public static String GeographyValueDesc = "GeographyValueDesc";
    public static String SchemeItemGUID = "SchemeItemGUID";
    public static String FromQty = "FromQty";
    public static String ToQty = "ToQty";
    public static String FromValue = "FromValue";
    public static String ToValue = "ToValue";
    public static String PayoutPerc = "PayoutPerc";
    public static String PayoutAmount = "PayoutAmount";
    public static String SlabRuleID = "SlabRuleID";
    public static String SlabRuleDesc = "SlabRuleDesc";
    public static String SaleUnitID = "SaleUnitID";
    public static String FreeQty = "FreeQty";
    public static String FreeArticle = "FreeArticle";
    public static String FreeArticleDesc = "FreeArticleDesc";
    public static String NoOfCards = "NoOfCards";
    public static String CardTitle = "CardTitle";
    public static String ScratchCardDesc = "ScratchCardDesc";
    public static String SalesAreaID = "SalesAreaID";
    public static String SlabGUID = "SlabGUID";
    public static String SaleUnitIDAmountWise = "000002";
    public static String TargetBasedID = "TargetBasedID";
    public static String BrandsCategories = "BrandsCategories";
    public static String OrderMaterialGroups = "OrderMaterialGroups";
    public static String Brands = "Brands";
    public static String MaterialCategories = "MaterialCategories";
    public static String BrandID = "BrandID";
    public static String BrandDesc = "BrandDesc";
    public static String MaterialCategoryID = "MaterialCategoryID";
    public static String MaterialCategoryDesc = "MaterialCategoryDesc";
    public static String FreeQuantity = "FreeQuantity";
    public static String Category = "Category";
    public static String CRS_SKU_GROUP = "CRS SKU Group";
    public static String BannerID = "BannerID";
    public static String ProductCatID = "ProductCatID";
    public static String SchemeSaleUnitIDCBB = "000004";
    public static String SchemeSaleUnitIDBAG = "000006";
    public static String SchemeFreeProdSeq = "000001";
    public static String SchemeFreeProdLowMRP = "000002";
    public static String DmsDivision = "DmsDivision";
    public static String DmsDivisionDesc = "DmsDivisionDesc";
    //    public static HashMap<String, String> Map_Must_Sell_Mat = new HashMap<>();
    public static String IS_TRUE = "true";
    public static String IsHeaderBasedSlab = "IsHeaderBasedSlab";
    public static String SchFreeMatGrpGUID = "SchFreeMatGrpGUID";
    public static String FreeQtyUOM = "FreeQtyUOM";
    public static String TransRefTypeID = "TransRefTypeID";
    public static String TransRefNo = "TransRefNo";
    public static String TransRefItemNo = "TransRefItemNo";
    public static String AlternativeUOM1Num = "AlternativeUOM1Num";
    public static String AltUomNum = "AltUomNum";
    public static String AltUomDen = "AltUomDen";
    public static String AlternativeUOM1Den = "AlternativeUOM1Den";
    public static String IntermUnitPrice = "IntermUnitPrice";
    public static String MaterialGrp = "MaterialGrp";
    public static String OrgScopeID = "OrgScopeID";
    public static String OrgScopeDesc = "OrgScopeDesc";
    public static String IsExcluded = "IsExcluded";
    public static String SchemeItemMinSaleUnitIDPPacket = "000001";
    public static String SchemeItemMinSaleUnitIDAmount = "000002";
    public static String SchemeItemMinSaleUnitIDKG = "000003";
    public static String SchemeItemMinSaleUnitIDPercentage = "000004";
    public static String SchemeItemMinSaleUnitIDCBB = "000005";
    public static String SchemeItemMinSaleUnitIDTon = "000006";
    public static String SchemeItemMinSaleUnitIDBag = "000007";
    public static String Tax2 = "Tax2";
    public static String Tax3 = "Tax3";
    public static String Tax4 = "Tax4";
    public static String Tax5 = "Tax5";
    public static String Tax6 = "Tax6";
    public static String Tax7 = "Tax7";
    public static String Tax8 = "Tax8";
    public static String Tax9 = "Tax9";
    public static String Tax10 = "Tax10";
    public static String ConditionTypeID = "ConditionTypeID";
    public static String ConditionTypeDesc = "ConditionTypeDesc";
    public static String ReferenceTaxFieldID = "ReferenceTaxFieldID";
    public static String ReferenceTaxFieldDesc = "ReferenceTaxFieldDesc";
    public static String FormulaID = "FormulaID";
    public static String FormulaDesc = "FormulaDesc";
    public static String CalcOnID = "CalcOnID";
    public static String CalcOnDesc = "CalcOnDesc";
    public static String ApplicableOnID = "ApplicableOnID";
    public static String ApplicableOnDesc = "ApplicableOnDesc";
    public static String CalcOnConditionTypeID = "CalcOnConditionTypeID";
    public static String CalcOnConditionTypeDesc = "CalcOnConditionTypeDesc";
    public static String BatchOrSerial = "BatchOrSerial";
    public static String EXTRA_OPEN_NOTIFICATION = "openNotification";
    public static String RatioSchNum = "RatioSchNum";
    public static String RatioSchDen = "RatioSchDen";
    public static String FreeMaterialNo = "FreeMaterialNo";
    public static String FreeTypeID = "FreeTypeID";
    public static String MSEC = "MSEC";
    public static String DAYSFLT = "DAYSFLT";
    public static String SKUGRP = "SKUGRP";
    public static String CPSTKUOM = "CPSTKUOM";
    public static String SKUGROUP = "SKU GROUP";
    public static String CRSSKUGROUP = "CRS SKU GROUP";
    public static String SchemeFreeProduct = "000001";
    public static String SchemeFreeSKUGroup = "000007";
    public static String SchemeFreeCRSSKUGroup = "000002";
    public static String SchemeFreeDiscountPercentage = "000003";
    public static String SchemeFreeDiscountAmount = "000004";
    public static String SchemeFreeScratchCard = "000005";
    public static String SchemeFreeFreeArticle = "000006";
    public static String OnSaleOfBanner = "000001";
    public static String OnSaleOfBrand = "000002";
    public static String OnSaleOfProdCat = "000003";
    public static String OnSaleOfOrderMatGrp = "000004";
    public static String OnSaleOfMat = "000005";
    public static String OnSaleOfSchemeMatGrp = "000006";
    // Invoices Properties
    public static String isInvoiceItemsEnabled = "isInvoiceItemsEnabled";
    public static String CollectionAmount = "CollectionAmount";
    /*  alerts */
    public static String PartnerType = "PartnerType";
    public static String AlertGUID = "AlertGUID";
    public static String AlertText = "AlertText";
    public static String AlertHistory = "AlertHistory";
    public static String Alerts = "Alerts";
    public static String ALERT_ENTITY = ".Alert";
    public static String ExpenseEntity = ".Expense";
    public static String ExpenseItemEntity = ".ExpenseItemDetail";
    public static String PartnerID = "PartnerID";
    public static String AlertTypeDesc = "AlertTypeDesc";
    public static String AlertType = "AlertType";
    public static String ObjectType = "ObjectType";
    public static String ObjectID = "ObjectID";
    public static String ObjectSubID = "ObjectSubID";
    public static String ConfirmedBy = "ConfirmedBy";
    public static String SyncApplication = "Application";
    public static String DocumentID = "DocumentID";
    public static String DocumentSt = "DocumentStore";
    public static String DocumentLink = "DocumentLink";
    public static String DocumentName = "FileName";
    public static String FolderName = "VisualVid";
    public static String ConfirmedOn = "ConfirmedOn";
    public static String BirthDayAlertsTempKey = "BirthDayAlertsTempKey";
    public static String AlertsTempKey = "AlertsTempKey";
    public static String BirthDayAlertsKey = "BirthDayAlertsKey";
    public static String BirthDayAlertsDate = "BirthDayAlertsDate";
    public static String AlertsDataKey = "AlertsDataKey";
    public static Boolean IsOnlineStoreFailed = false;
    public static Boolean IsOnlineStoreStarted = false;
    /*entity name */
    public static String FeedbackEntity = ".Feedback";
    public static String VISITACTIVITYENTITY = ".VisitActivity";
    public static String FeedbackItemDetailEntity = ".FeedbackItemDetail";
    public static String VISITENTITY = ".Visit";
    public static String CUSTOMERENTITY = ".Customer";
    public static String ATTENDANCEENTITY = ".Attendance";
    public static String MERCHINDISINGENTITY = ".MerchReview";
    public static String MERCHINDISINGITEMENTITY = ".MerchReviewImage";
    public static String ChannelPartnerEntity = ".ChannelPartner";
    public static String InvoiceEntity = ".SSInvoice";
    public static String InvoiceItemEntity = ".SSInvoiceItemDetail";
    public static String InvoiceSerialNoEntity = ".SSInvoiceItemSerialNo";
    public static String FinancialPostingsEntity = ".FinancialPosting";
    public static String FinancialPostingsItemEntity = ".FinancialPostingItemDetail";
    public static String CompetitorInfoEntity = ".CompetitorInfo";
    public static String SPStockSNosEntity = ".SPStockItemSNo";
    public static String CPStockItemEntity = ".CPStockItem";
    public static String ComplaintEntity = ".Complaint";
    public static String STOCK_ENTITY = ".Stock";
    public static String RouteScheduleEntity = ".RouteSchedule";
    public static String RouteSchedulePlanEntity = ".RouteSchedulePlan";
    public static String RouteScheduleSPEntity = ".RouteScheduleSP";
    public static String SalesOrderEntity = ".SSSO";
    public static String ReturnOrderEntity = ".SSRO";
    public static String ReturnOrderItemEntity = ".SSROItemDetail";
    public static String SalesOrderItemEntity = ".SSSOItemDetail";
    public static String AlertEntity = ".Alert";
    public static String CPDMSDivisionEntity = ".CPDMSDivision";
    public static String CPPartnerFunctionEntity = ".CPPartnerFunction";
    /*collection list*/
    public static String SoldToTypeDesc = "SoldToTypeDesc";
    public static String DeletionInd = "DeletionInd";
    public static String Division = "Division";
    public static String InvoiceItemGUID = "InvoiceItemGUID";
    public static String SOs = "SOs";
    public static String Collections = "Collections";
    public static String CustomerSalesAreas = "CustomerSalesAreas";
    public static String CPSPRelations = "CPSPRelations";
    public static String Feedbacks = "Feedbacks";
    public static String FeedbackItemDetails = "FeedbackItemDetails";
    public static String SchemeFreeMatGrpMaterials = "SchemeFreeMatGrpMaterials";
    public static String SchemeItems = "SchemeItems";
    public static String SchemeItemDetails = "SchemeItemDetails";
    public static String SchemeSlabs = "SchemeSlabs";
    public static String SchemeGeographies = "SchemeGeographies";
    public static String SchemeCPs = "SchemeCPs";
    public static String SchemeSalesAreas = "SchemeSalesAreas";
    public static String SchemeCostObjects = "SchemeCostObjects";
    public static String GoodsIssueFromID = "GoodsIssueFromID";
    public static String ItemMin = "ItemMin";
    public static String CPGroup2Desc = "CPGroup2Desc";
    public static String SalesAreaGUID = "SalesAreaGUID";
    public static String CPGroup1ID = "CPGroup1ID";
    public static String CPGroup1Desc = "CPGroup1Desc";
    public static String CPGroup3Desc = "CPGroup3Desc";
    public static String DivisionID = "DivisionID";
    public static String CPGroup4Desc = "CPGroup4Desc";
    public static String DistributionChannelID = "DistributionChannelID";
    public static String CPGroup3ID = "CPGroup3ID";
    public static String SalesOrgDesc = "SalesOrgDesc";
    public static String DistributionChannelDesc = "DistributionChannelDesc";
    public static String DivisionDesc = "DivisionDesc";
    public static String CPGroup4ID = "CPGroup4ID";
    public static String SalesOrgID = "SalesOrgID";
    public static String CPGroup2ID = "CPGroup2ID";
    public static String ChannelPartner = "ChannelPartner";
    public static String isWindowDisplayKey = "isWindowDisplayEnabled";
    public static String isWindowDisplayTcode = "/ARTEC/SS_WIN_DISPLAY";
    public static String isStartCloseEnabled = "isStartCloseEnabled";
    public static String isStartCloseTcode = "/ARTEC/MC_ATTND";
    public static String isRetailerApprovalKey = "isRetailerApprovalKey";
    public static String isRetailerApprovalTcode = "/ARTEC/SS_RET_APRL";
    public static String Error_Msg = "";
    /*others*/
    public static String x_csrf_token = "";
    public static int ErrorCode = 0;
    public static int ErrorNo = 0;
    public static int ErrorNoSyncLog = 0;
    public static int ErrorNoTechincalCache = 0;
    public static int ErrorNo_Get_Token = 0;
    public static String ErrorName = "";
    public static String NetworkError_Name = "NetworkError";
    public static String Comm_error_name = "Communication error";
    public static String Network_Name = "Network";
    public static String Unothorized_Error_Name = "401";
    public static String Max_restart_reached = "Maximum restarts reached";
    public static int Network_Error_Code = 101;
    public static int Comm_Error_Code = 110;
    public static int UnAuthorized_Error_Code = 401;
    public static int UnAuthorized_Error_Code_Offline = -10207;
    public static int Network_Error_Code_Offline = -10205;
    public static int Unable_to_reach_server_offline = -10208;
    public static int Resource_not_found = -10210;
    public static int Unable_to_reach_server_failed_offline = -10204;
    public static int Build_Database_Failed_Error_Code1 = -100036;
    public static int Build_Database_Failed_Error_Code2 = -100097;
    public static int Build_Database_Failed_Error_Code3 = -10214;
    public static int Database_Transction_Failed_Error_Code = -10104;
    public static String Invalid_input_Error_Code = "-10133";
    public static String Executing_SQL_Commnd_Error = "10001";
    public static int Execu_SQL_Error_Code = -10001;
    public static int Store_Def_Not_matched_Code = -10247;
    public static String Store_Defining_Req_Not_Matched = "10247";
    public static String RFC_ERROR_CODE_100027 = "100027";
    public static String RFC_ERROR_CODE_100029 = "100029";
    public static String Invalid_Store_Option_Value = "InvalidStoreOptionValue";
    public static HashMap<String, Object> MapEntityVal = new HashMap<String, Object>();
    public static ArrayList<String> AL_ERROR_MSG = new ArrayList<>();
    public static Set<String> Entity_Set = new HashSet<>();
    public static String CreateOperation = "Create";
    public static String ReadOperation = "Read";
    public static String UpdateOperation = "Update";
    public static String DeleteOperation = "Delete";
    public static String QueryOperation = "Query";
    public static boolean isPullDownSync = false;
    /*default value*/
    public static boolean mBoolIsReqResAval = false;
    public static boolean mBoolIsNetWorkNotAval = false;
    public static String Route_Plan_No = "";
    public static String Route_Plan_Desc = "";
    public static String Route_Plan_Key = "";
    public static String Route_Schudle_GUID = "";
    public static boolean isSync = false;
    public static boolean isBackGroundSync = false;
    public static String ALL = "ALL";
    public static String All = "All";
    public static String None = "None";
    public static String yes = "Yes";
    public static String No = "No";
    public static String SOITST = "SOITST";
    public static String DELVST = "DELVST";
    public static String ClosingeDay = "ClosingeDay";
    public static String Today = "Today";
    public static String PreviousDay = "PreviousDay";
    public static String ClosingeDayType = "ClosingeDayType";
    public static boolean isStoreClosed = false;
    public static String str_02 = "02";
    public static String TLSD = "TLSD";
    public static String IsfreeGoodsItem = "IsfreeGoodsItem";
    public static String SOCreateID = "06";
    public static String CompetitorId = "08";
    public static String CustomerCompCreateID = "10";
    public static String RetailerStockID = "07";
    public static String CollCreateID = "02";
    public static String MerchReviewCreateID = "01";
    public static String CompInfoCreateID = "04";
    public static String FeedbackID = "03";
    public static String SampleDisbursementID = "09";
    public static String ROCreateID = "08";
    public static String DSTSTKVIEW = "DSTSTKVIEW";
    public static String CPSTKAUOM1 = "CPSTKAUOM1";
    public static String PRICALBTC = "PRICALBTC";
    public static String DBSTKTYPE = "DBSTKType";
    public static String MSLInd = "MSLInd";
    public static String FocussedInd = "FocussedInd";
    public static String CrossSell = "CrossSell";
    public static String UPSell = "UPSell";
    public static String SOQ = "SOQ";
    public static String SellIndicator = "SellIndicator";
    public static String ROList = "ROList";
    public static String DR = "DR";
    public static String CS = "CS";
    public static String US = "US";
    public static ODataDuration mStartTimeDuration = null;
    public static double DoubGetRunningSlabPer = 0.0;
    public static String SchemeCatIDInstantScheme = "000002";
    public static String BasketCatID = "000002";
    public static String SchemeCatIDQPSScheme = "000001";
    public static String SchemeTypeIDBasketScheme = "000008";
    public static String SalesDistrictID = "SalesDistrictID";
    public static MSFAApplication mApplication = null;
    public static Parser parser = null;
    // Data base
    public static String DATABASE_NAME = "mSecSales.db";
    public static boolean devicelogflag = false;
    public static boolean importdbflag = false;
    public static boolean FlagForUpdate = false;
    public static boolean FlagForSecurConnection = false;
    public static String DATABASE_REGISTRATION_TABLE = "registrationtable";
    public static int[] IconVisibiltyReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static int[] IconPositionReportFragment = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static boolean iSAutoSync = false;
    public static String ValidTo = "ValidTo";
    public static String strErrorWithColon = "Error : ";
    public static String MaterialDesc = "MaterialDesc";
    public static String HigherLevelItemno = "HigherLevelItemno";
    public static String OrderMaterialGroupDesc = "OrderMaterialGroupDesc";
    public static String OrderMaterialGroupID = "OrderMaterialGroupID";
    public static String SegmentId = "SegmentId";
    public static String OrderMaterialGroup = "OrderMaterialGroup";
    /*auto sync*/
    public static boolean isDayStartSyncEnbled = false;
    public static int mErrorCount = 0;
    public static String EvaluationTypeID = "EvaluationTypeID";
    public static String UOM = "UOM";
    public static String isSOApprovalKey = "isSOApprovalEnabled";
    public static String isSOApprovalTcode = "/ARTEC/SF_SOAPRL";
    public static String isMtpApprovalkey = "isMtpApprovalEnabled";
    public static String isMtpApprovalTcode = "/ARTEC/SF_MTP_CRT";
    public static String isContractApprovalkey = "isContractApprovalEnabled";
    public static String isContractApprovalTcode = "/ARTEC/SF_CNTR_APPRL";
    public static String isCreditLimitApprkey = "isCCreditLimitApprEnabled";
    public static String isCreditLimitApprTcode = "/ARTEC/SF_CRD_LMT_APPRL";
    public static String isPendingDispatchkey = "isPendingDispatchEnabled";
    public static String isPendingDispatchTcode = "/ARTEC/SF_PND_DISP";
    public static ArrayList<MTPRoutePlanBean> alTodayBeatCustomers = new ArrayList<>();
    public static String SO_Cust_QRY = "";
    public static ArrayList<String> alCustomers = new ArrayList<>();
    public static String InstanceID = "InstanceID";
    public static String SSROs = "SSROs";
    public static String SSROItemDetails = "SSROItemDetails";
    public static String ReturnOrders = "ReturnOrders";
    public static String ReturnOrderItems = "ReturnOrderItems";
    public static String ReturnOrderItemDetails = "ReturnOrderItemDetails";
    public static String RetOrdNo = "RetOrdNo";
    public static String InvoiceQty = "InvoiceQty";
    public static String UnitPrice = "UnitPrice";
    public static String OpenQty = "OpenQty";
    public static String OrderTypeDesc = "OrderTypeDesc";
    public static String ShipToPartyName = "ShipToPartyName";
    public static String ShipToParty = "ShipToParty";
    public static String TransporterID = "TransporterID";
    public static String TransporterName = "TransporterName";
    public static String ShippingTypeDesc = "ShippingTypeDesc";
    public static String SaleOffDesc = "SaleOffDesc";
    public static String SaleGrpDesc = "SaleGrpDesc";
    public static String DelvStatus = "DelvStatus";
    public static String SOConditions = "SOConditions";
    public static String CondCurrency = "CondCurrency";
    public static String ConditionAmount = "ConditionAmount";
    public static String ConditionAmtPer = "ConditionAmtPer";
    public static String ConditionValue = "ConditionValue";
    public static String Mobile2 = "Mobile2";
    public static String RegionIDRegionID = "RegionIDRegionID";
    public static String PaymentMethod = "";
    public static String IssuingBank = "";
    public static String GrossAmount = "GrossAmount";
    public static String Freight = "Freight";
    public static String Tax = "Tax";
    public static String Discount = "Discount";
    public static String TaxAmount = "TaxAmount";
    public static String StatusDesc = "StatusDesc";
    public static String DelvStatusDesc = "DelvStatusDesc";
    public static String ItemCatDesc = "ItemCatDesc";
    public static String DiscountPer = "DiscountPer";
    public static String OwnStock = "OwnStock";
    public static String RejReason = "RejReason";
    public static String RejReasonDesc = "RejReasonDesc";
    public static String QAQty = "QAQty";
    public static String EXTRA_TAB_POS = "extraTabPos";
    public static String comingFromChange = "comingFromChange";
    public static String SalesGrpDesc = "SalesGrpDesc";
    public static String SalesOffDesc = "SalesOffDesc";
    public static String SalesOfficeID = "SalesOfficeID";
    public static String SalesGroupID = "SalesGroupID";
    public static Bundle SOBundleExtras = null;
    public static String isSOCancelEnabled = "isSOCancelEnabled";
    public static String isSOChangeEnabled = "isSOChangeEnabled";
    public static String LoginID = "LoginID";
    public static String LoginId = "LoginId";
    public static String RefGUID = "RefGUID";
    public static String REJRSN = "REJRSN";
    public static String Uom = "Uom";
    public static String CPStockItemGUID = "CPStockItemGUID";
    public static String StockValue = "StockValue";
    public static String LandingPrice = "LandingPrice";
    public static String WholeSalesLandingPrice = "WholeSalesLandingPrice";
    public static String Margin = "Margin";
    public static String ConsumerOffer = "ConsumerOffer";
    public static String ShelfLife = "ShelfLife";
    public static String TradeOffer = "TradeOffer";
    public static String Batch = "Batch";
    public static String StockRefGUID = "StockRefGUID";
    public static String RoutDesc = "RoutDesc";
    public static String Scheme = "RoutDesc";
    public static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public static String Brand = "Brand";
    public static String InvoiceList = "InvoiceList";
    public static String RegSchemeCat = "RegSchemeCat";
    public static String WDSPINVDTR = "WDSPINVDTR";
    public static String SC = "SC";
    public static String EXTRA_SCHEME_NAME = "extraSchemeName";
    public static String EXTRA_SCHEME_IS_SECONDTIME = "isSecondTime";
    public static String EXTRA_SCHEME_TYPE_ID = "extraSchemeTypeIds";
    public static String EXTRA_INVOICE_DATE = "invoiceDate";
    public static String EXTRA_SCHEME_ID = "schemeIds";
    public static String EXTRA_IS_OFFLINE = "isOffline";
    public static ArrayList<RetailerBean> alTodayBeatRet = new ArrayList<>();
    public static HashMap<String, String> mMapCPSeqNo = new HashMap<>();
    public static HashSet<String> mSetTodayRouteSch = new HashSet<>();
    public static String SS_INV_RET_QRY = "";
    public static ArrayList<String> alRetailers = new ArrayList<>();
    public static ArrayList<String> alRetailersGuid = new ArrayList<>();
    public static ArrayList<String> alRetailersGuid36 = new ArrayList<>();
    public static ArrayList<String> alVisitedGuid36 = new ArrayList<>();
    public static ArrayList<String> alRouteGuid36 = new ArrayList<>();
    public static ArrayList<String> alVisitGuid36 = new ArrayList<>();
    public static HashMap<String, Set<String>> visitRetailersMap = new HashMap<>();
    public static ArrayList<String> alRetailersCount = new ArrayList<>();
    public static HashMap<String, String> MAPQPSSCHGuidByMaterial = new HashMap<>();
    public static HashMap<String, ArrayList<String>> MAPSCHGuidByMaterial = new HashMap<>();//TODO need to change
    public static HashMap<String, String> MAPORDQtyByCrsSkuGrp = new HashMap<>();
    public static HashMap<String, ArrayList<String>> MAPSCHGuidByCrsSkuGrp = new HashMap<>();// TODO need to change
    public static HashMap<String, ArrayList<SKUGroupBean>> HashMapSubMaterials = new HashMap<>();
    public static ArrayList<SKUGroupBean> selectedSOItems = new ArrayList<>();
    public static HashMap<String, SKUGroupBean> hashMapCpStockDataByMaterial = new HashMap<>();
    public static HashMap<String, SchemeBean> hashMapCpStockItemGuidQtyValByMaterial = new HashMap<>();
    public static HashMap<String, SchemeBean> hashMapCpStockItemGuidQtyValByOrderMatGrp = new HashMap<>();
    public static HashMap<String, Set> hashMapMaterialValByOrdMatGrp = new HashMap<>();
    public static String SchemeTypeNormal = "NormalScheme";
    public static String SchemeTypeBasket = "BasketScheme";
    public static String EmptyGUIDVal = "00000000-0000-0000-0000-000000000000";
    public static String BasketHeadingName = "Basket(Min)";
    public static HashMap<String, SchemeBean> HashMapSchemeValuesBySchemeGuid = new HashMap<>();
    public static HashMap<String, String> HashMapSchemeIsInstantOrQPS = new HashMap<>();
    public static HashMap<String, ArrayList<String>> HashMapSchemeListByMaterial = new HashMap<>();
    public static String SchemeQRY = "";
    public static String CPGUIDVAL = "";
    public static String YES = "YES";
    public static String Y = "Y";
    public static String N = "N";
    public static String EntityKeyID = "EntityKeyID";
    public static String EntityKeyDesc = "EntityKeyDesc";
    public static String EntityAttribute1 = "EntityAttribute1";
    public static String EntityKey = "EntityKey";
    // constants after merge
    public static String StatusId = "StatusID";
    public static String ComplaintType = "ComplaintType";
    public static String ComplaintDate = "ComplaintDate";
    public static String ComplaintStatusID = "ComplaintStatusID";
    public static String ComplaintStatusDesc = "ComplaintStatusDesc";
    public static String ComplaintNo = "ComplaintNo";
    public static String ComplaintGUID = "ComplaintGUID";
    public static String ComplaintPriorityID = "ComplaintPriorityID";
    public static String ComplaintPriorityDesc = "ComplaintPriorityDesc";
    public static String ComplaintTypeDesc = "ComplaintTypeDesc";
    public static String ComplaintTypeID = "ComplaintTypeID";
    public static String ComplainCategoryDesc = "ComplainCategoryDesc";
    public static String ComplaintCategoryID = "ComplaintCategoryID";
    public static String PerformanceOnID = "PerformanceOnID";
    public static String PerformanceGUID = "PerformanceGUID";
    public static String AmtTarget = "AmtTarget";
    public static String AmtLMTD = "AmtLMTD";
    public static String AmtMTD = "AmtMTD";
    public static String AmtMonth1PrevPerf = "AmtMonth1PrevPerf";
    public static String AmtMonthlyGrowth = "AmtMonthlyGrowth";
    public static String AmtMonth2PrevPerf = "AmtMonth2PrevPerf";
    public static String AmtMonth3PrevPerf = "AmtMonth3PrevPerf";
    public static String AmtLastYearMTD = "AmtLastYearMTD";
    public static String QtyLastYearMTD = "QtyLastYearMTD";
    public static String QtyTarget = "QtyTarget";
    public static String QtyLMTD = "QtyLMTD";
    public static String QtyMTD = "QtyMTD";
    public static String QtyMonthlyGrowth = "QtyMonthlyGrowth";
    public static SQLiteDatabase EventUserHandler;
    public static boolean checkSearch = false;
    public static boolean addedSeaerch = false;
    public static final String CURRENT_VERSION_CODE = "currentVersionCode";
    public static final String INTIALIZEDB = "intializedb";
    public static final String DataVaultUpdate = "DataVaultUpdate";


    public static int NewDefingRequestVersion = 29;
    public static int IntializeDBVersion = 3;

    public static String APKVersion = "APKVersion";
    public static String APKVersionCode = "APKVersionCode";

    /*defining request for open store*/
    public static String[] getDefinigReq(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginId = sharedPreferences.getString("username", "");

        if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 24) {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", //Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules", UserSalesPersons,
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems,/*SyncHistorys,UserPartners,*/
                    CompetitorMasters, CompetitorInfos, TextCategorySet,
                    "Visits", "Attendances", "VisitActivities",
                    SSOutstandingInvoices, SSOutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
                    "Alerts?$filter=Application eq 'MSEC'",
//                        "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    SSSOs, SSSoItemDetails, SSROs, SSROItemDetails,
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'", "ComplaintDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'CPGRP2' or Typeset eq 'CPGRP3'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'EVLTYP' or Typeset eq 'DELVST' or Typeset eq 'SOITST' or Typeset eq 'CPGRP4' or Typeset eq 'CPGRP5'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
                            ")"};
//                        ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 26) {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", UserCustomers,//Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules", UserSalesPersons,
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                    CompetitorMasters, CompetitorInfos, TextCategorySet,
                    "Visits", "Attendances", "VisitActivities",
                    SSOutstandingInvoices, SSOutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
                    "Alerts?$filter=Application eq 'MSEC'",
//                        "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    SSSOs, SSSoItemDetails, SSROs, SSROItemDetails,
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'", "ComplaintDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'CPGRP2' or Typeset eq 'CPGRP3'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'EVLTYP' or Typeset eq 'DELVST' or Typeset eq 'SOITST' or Typeset eq 'CPGRP4' or Typeset eq 'CPGRP5'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
                            ")"};
//                        ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        } else if (sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <= 27) {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", UserCustomers,//Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules", UserSalesPersons,
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                    CompetitorMasters, CompetitorInfos, TextCategorySet,
                    "Visits", "Attendances", "VisitActivities",
                    SSOutstandingInvoices, SSOutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages",
//                "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
                    "Alerts?$filter=Application eq 'MSEC'",
//                        "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    SSSOs, SSSoItemDetails, SSROs, SSROItemDetails,
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'", "ComplaintDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'CPGRP2' or Typeset eq 'CPGRP3' or Typeset eq 'ZDOCTY'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'EVLTYP' or Typeset eq 'DELVST' or Typeset eq 'SOITST' or Typeset eq 'CPGRP4' or Typeset eq 'CPGRP5'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
                            ")"};
//                        ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        }/*else if(sharedPreferences.getInt(CURRENT_VERSION_CODE, 0) <=28){
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", UserCustomers,//Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules", UserSalesPersons,
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems,SyncHistorys,*//*UserPartners,*//*
                    CompetitorMasters, CompetitorInfos, TextCategorySet,
                    "Visits", "Attendances", "VisitActivities",
                    SSOutstandingInvoices, SSOutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages","VisitSummarySet",
//                "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
                    "Alerts?$filter=Application eq 'MSEC'",
//                        "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    SSSOs, SSSoItemDetails, SSROs, SSROItemDetails,
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'", "ComplaintDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'CPGRP2' or Typeset eq 'CPGRP3' or Typeset eq 'ZDOCTY'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'EVLTYP' or Typeset eq 'DELVST' or Typeset eq 'SOITST' or Typeset eq 'CPGRP4' or Typeset eq 'CPGRP5'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
                            ")"};
//                        ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        }*/ else {
            String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners", UserCustomers,//Customers,
                    "SSInvoices",
                    "SSInvoiceItemDetails",
                    "RoutePlans", "RouteSchedulePlans", "RouteSchedules", UserSalesPersons,
                    "SPStockItems", "SPStockItemSNos",
                    "Feedbacks", "FeedbackItemDetails",
                    KPISet, Targets, TargetItems, KPIItems, SyncHistorys, UserPartners,
                    CompetitorMasters, CompetitorInfos, TextCategorySet,
                    "Visits", "Attendances", "VisitActivities",
                    SSOutstandingInvoices, SSOutstandingInvoiceItemDetails,
                    "CPStockItems?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "CPStockItemSnos?$filter=(StockOwner eq '01' or StockOwner eq '02')",
                    "SegmentedMaterials", PricingConditions,
                    "Brands",
                    "MaterialCategories",
                    "OrderMaterialGroups", "BrandsCategories",
                    "MerchReviews",
                    "MerchReviewImages", "VisitSummarySet",
//                "Alerts?$filter=Application eq 'MSEC' and LoginID eq " +
                    "Alerts?$filter=Application eq 'MSEC'",
//                        "'" + loginId.toUpperCase() + "'",
                    Schemes, SchemeItemDetails, SchemeSlabs, SchemeGeographies, SchemeCPs, SchemeSalesAreas, SchemeCostObjects, SchemeFreeMatGrpMaterials, SchemeCPDocuments + "?$filter= DocumentStore eq 'A'",
                    Claims,
                    ClaimItemDetails, ClaimDocuments + "?$filter= DocumentStore eq 'A'",
                    "Complaints",
                    "CPGeoClassifications",
                    "FinancialPostingItemDetails", "FinancialPostings",
                    "Performances?$filter= PerformanceTypeID eq '000002' and AggregationLevelID eq '01'",
                    "SPChannelEvaluationList",
                    "CPDMSDivisions",
                    "UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27",
                    SSSOs, SSSoItemDetails, SSROs, SSROItemDetails,
                    "ExpenseConfigs", "Expenses", "ExpenseItemDetails", "ExpenseAllowances", "ExpenseDocuments?$filter= DocumentStore eq 'A'", "ComplaintDocuments?$filter= DocumentStore eq 'A'",
                    "Documents?$filter=DocumentStore eq 'A' and Application eq 'PD'",
                    "ConfigTypsetTypeValues?$filter=Typeset eq 'MSEC' or Typeset eq 'ATTTYP' or Typeset eq 'RVWTYP' or Typeset eq 'FIPRTY' or Typeset eq 'ACTTYP' or Typeset eq 'SF' or Typeset eq 'SC' or Typeset eq 'SS' or Typeset eq 'MC' or Typeset eq 'SSCP' or Typeset eq 'SSROUT' or Typeset eq 'SSSO' or Typeset eq 'CPGRP2' or Typeset eq 'CPGRP3' or Typeset eq 'ZDOCTY'",
                    "ConfigTypesetTypes?$filter=Typeset eq 'APNRMD' or Typeset eq 'SCGOTY' or Typeset eq 'UOMNO0' or Typeset eq 'EVLTYP' or Typeset eq 'DELVST' or Typeset eq 'SOITST' or Typeset eq 'CPGRP4' or Typeset eq 'CPGRP5'",
                    "SSInvoiceTypes",
                    "ValueHelps?$filter= ModelID eq 'SSGW_ALL' and (EntityType eq 'Attendance' or EntityType eq 'FinancialPosting' or EntityType eq 'FinancialPostingItemDetail' or EntityType eq 'SSInvoice' or EntityType eq 'MerchReview' or EntityType eq 'SegmentedMaterial' or EntityType eq 'ChannelPartner' or EntityType eq 'Feedback' or EntityType eq 'Performance' or EntityType eq 'Evaluation' or EntityType eq 'Target' or EntityType eq 'Visit' or EntityType eq  'SSSOItemDetail' or EntityType eq  'SSSO' or EntityType eq 'SSRO' or EntityType eq  'Complaints' or EntityType eq 'ExpenseConfig' or EntityType eq 'ExpenseItemDetail' or EntityType eq 'Scheme' or EntityType eq 'SchemeSalesArea' or EntityType eq 'SchemeGeo' or EntityType eq 'SchemeCostObject' or EntityType eq 'SchemeSlab' or EntityType eq 'SchemeCPDoc' or EntityType eq 'SchemeCP'" +
                            ")"};
//                        ") and LoginID eq '" + loginId.toUpperCase() + "'"};


            return DEFINGREQARRAY;
        }

//        String[] DEFINGREQARRAY = {"SalesPersons", "CPSPRelations", "ChannelPartners",Customers,"Attendances","SSSOs",SSSoItemDetails};
//        return DEFINGREQARRAY;
    }

    public static final void updateTCodeToSharedPreference(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, ArrayList<Config> authList) {
        if (authList != null && authList.size() > 0) {
            if (sharedPreferences.contains(isStartCloseEnabled)) {
                editor.remove(isStartCloseEnabled);
            }
            if (sharedPreferences.contains(isRetailerListEnabled)) {
                editor.remove(isRetailerListEnabled);
            }
            if (sharedPreferences.contains("isRetailerUpdate")) {
                editor.remove("isRetailerUpdate");
            }
            if (sharedPreferences.contains(isCreateRetailerKey)) {
                editor.remove(isCreateRetailerKey);
            }
            if (sharedPreferences.contains("isHelpLine")) {
                editor.remove("isHelpLine");
            }
            if (sharedPreferences.contains("isMyStock")) {
                editor.remove("isMyStock");
            }
            if (sharedPreferences.contains("isVisitCreate")) {
                editor.remove("isVisitCreate");
            }
            if (sharedPreferences.contains(isRetailerTrendKey)) {
                editor.remove(isRetailerTrendKey);
            }
            if (sharedPreferences.contains("isRetailerStock")) {
                editor.remove("isRetailerStock");
            }
            if (sharedPreferences.contains(isCollListKey)) {
                editor.remove(isCollListKey);
            }
            if (sharedPreferences.contains(isSecondaryInvoiceListKey)) {
                editor.remove(isSecondaryInvoiceListKey);
            }
            if (sharedPreferences.contains(isRouteEnabled)) {
                editor.remove(isRouteEnabled);
            }
            if (sharedPreferences.contains(isAdhocVisitEnabled)) {
                editor.remove(isAdhocVisitEnabled);
            }
            if (sharedPreferences.contains("isTariffEnabled")) {
                editor.remove("isTariffEnabled");
            }
            if (sharedPreferences.contains(isSchemeEnabled)) {
                editor.remove(isSchemeEnabled);
            }
            if (sharedPreferences.contains(isBehaviourEnabled)) {
                editor.remove(isBehaviourEnabled);
            }
            if (sharedPreferences.contains(isMyTargetsEnabled)) {
                editor.remove(isMyTargetsEnabled);
            }
            if (sharedPreferences.contains("isMyPerformanceEnabled")) {
                editor.remove("isMyPerformanceEnabled");
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains(isNewProductKey)) {
                editor.remove(isNewProductKey);
            }
            if (sharedPreferences.contains(isFocusedProductKey)) {
                editor.remove(isFocusedProductKey);
            }
            if (sharedPreferences.contains(isOutstandingListKey)) {
                editor.remove(isOutstandingListKey);
            }
            if (sharedPreferences.contains(isDBStockEnabled)) {
                editor.remove(isDBStockEnabled);
            }
            if (sharedPreferences.contains(isVisualAidEnabled)) {
                editor.remove(isVisualAidEnabled);
            }
            if (sharedPreferences.contains(isRetailerStockKey)) {
                editor.remove(isRetailerStockKey);
            }
            if (sharedPreferences.contains(isDaySummaryKey)) {
                editor.remove(isDaySummaryKey);
            }
            if (sharedPreferences.contains(isDlrBehaviourKey)) {
                editor.remove(isDlrBehaviourKey);
            }
            if (sharedPreferences.contains("isActStatusEnabled")) {
                editor.remove("isActStatusEnabled");
            }
            if (sharedPreferences.contains(isMerchReviewKey)) {
                editor.remove(isMerchReviewKey);
            }
            if (sharedPreferences.contains(isMerchReviewListKey)) {
                editor.remove(isMerchReviewListKey);
            }
            if (sharedPreferences.contains(isSOCreateKey)) {
                editor.remove(isSOCreateKey);
            }
            if (sharedPreferences.contains(isVisitSummaryKey)) {
                editor.remove(isVisitSummaryKey);
            }
            if (sharedPreferences.contains(isInvoiceCreateKey)) {
                editor.remove(isInvoiceCreateKey);
            }
            if (sharedPreferences.contains(isCollCreateEnabledKey)) {
                editor.remove(isCollCreateEnabledKey);
            }
            if (sharedPreferences.contains(isFeedbackCreateKey)) {
                editor.remove(isFeedbackCreateKey);
            }
            if (sharedPreferences.contains("isCompInfoEnabled")) {
                editor.remove("isCompInfoEnabled");
            }
            if (sharedPreferences.contains(isStockListKey)) {
                editor.remove(isStockListKey);
            }
            if (sharedPreferences.contains(isFeedBackListKey)) {
                editor.remove(isFeedBackListKey);
            }
            if (sharedPreferences.contains(isCompetitorListKey)) {
                editor.remove(isCompetitorListKey);
            }
            if (sharedPreferences.contains(isReturnOrderListKey)) {
                editor.remove(isReturnOrderListKey);
            }
            if (sharedPreferences.contains(isSecondarySalesListKey)) {
                editor.remove(isSecondarySalesListKey);
            }
            if (sharedPreferences.contains(isSampleDisbursmentEnabledKey)) {
                editor.remove(isSampleDisbursmentEnabledKey);
            }
            if (sharedPreferences.contains(isCustomerComplaintEnabledKey)) {
                editor.remove(isCustomerComplaintEnabledKey);
            }
            if (sharedPreferences.contains(isReturnOrderCreateEnabled)) {
                editor.remove(isReturnOrderCreateEnabled);
            }
            if (sharedPreferences.contains(isWindowDisplayKey)) {
                editor.remove(isWindowDisplayKey);
            }
            if (sharedPreferences.contains(isSampleDisbursmentListKey)) {
                editor.remove(isSampleDisbursmentListKey);
            }
            if (sharedPreferences.contains(isCustomerComplaintListKey)) {
                editor.remove(isCustomerComplaintListKey);
            }
            if (sharedPreferences.contains(isExpenseEntryKey)) {
                editor.remove(isExpenseEntryKey);
            }
            if (sharedPreferences.contains(isDigitalProductEntryEnabled)) {
                editor.remove(isDigitalProductEntryEnabled);
            }
            if (sharedPreferences.contains(isCompInfoEnabled)) {
                editor.remove(isCompInfoEnabled);
            }
            if (sharedPreferences.contains(isCompetitorListKey)) {
                editor.remove(isCompetitorListKey);
            }
            if (sharedPreferences.contains(isRetailerApprovalKey)) {
                editor.remove(isRetailerApprovalKey);
            }
            if (sharedPreferences.contains(isExpenseListKey)) {
                editor.remove(isExpenseListKey);
            }
            if (sharedPreferences.contains(isisVSTSUMKey)) {
                editor.remove(isisVSTSUMKey);
            }
            if (sharedPreferences.contains(isTCPCEnabled)) {
                editor.remove(isTCPCEnabled);
            }
            if (sharedPreferences.contains(isBeatEnabled)) {
                editor.remove(isBeatEnabled);
            }
            if (sharedPreferences.contains(isBrandEnabled)) {
                editor.remove(isBrandEnabled);
            }
            editor.commit();
            for (int incVal = 0; incVal < authList.size(); incVal++) {
                if (authList.get(incVal).getFeature().equalsIgnoreCase(isStartCloseTcode)) {
                    editor.putString(isStartCloseEnabled, isStartCloseTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRetailerListTcode)) {
                    editor.putString(isRetailerListEnabled, isRetailerListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CP_CHG")) {
                    editor.putString("isRetailerUpdate", "/ARTEC/SS_CP_CHG");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCreateRetailerTcode)) {
                    editor.putString(isCreateRetailerKey, isCreateRetailerTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_HELPLINE")) {
                    editor.putString("isHelpLine", "/ARTEC/SF_HELPLINE");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
                    editor.putString("isMyStock", "/ARTEC/SF_MYSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SF_VST")) {
                    editor.putString("isVisitCreate", "/ARTEC/SF_VST");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRetailerTrendTcode)) {
                    editor.putString(isRetailerTrendKey, isRetailerTrendTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_CPSTK")) {
                    editor.putString(isRetailerStockKey, "/ARTEC/SS_CPSTK");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCollListTcode)) {
                    editor.putString(isCollListKey, isCollListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSecondaryInvoiceListTcode)) {
                    editor.putString(isSecondaryInvoiceListKey, isSecondaryInvoiceListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRoutePlaneTcode)) {
                    editor.putString(isRouteEnabled, isRoutePlaneTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isAdhocVistTcode)) {
                    editor.putString(isAdhocVisitEnabled, isAdhocVistTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_TARIFF")) {
                    editor.putString("isTariffEnabled", "/ARTEC/SS_TARIFF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSchemeTcode)) {
                    editor.putString(isSchemeEnabled, isSchemeTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isBehaviourTcode)) {
                    editor.putString(isBehaviourEnabled, isBehaviourTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMyTargetsTcode)) {
                    editor.putString(isMyTargetsEnabled, isMyTargetsTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_MYPERF")) {
                    editor.putString("isMyPerformanceEnabled", "/ARTEC/SS_MYPERF");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFocusedProductTcode)) {
                    editor.putString(isFocusedProductKey, isFocusedProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isNewProductTcode)) {
                    editor.putString(isNewProductKey, isNewProductTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMustSellTcode)) {
                    editor.putString(isMustSellKey, isMustSellTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_ACTSTS")) {
                    editor.putString("isActStatusEnabled", "/ARTEC/SS_ACTSTS");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewTcode)) {
                    editor.putString(isMerchReviewKey, isMerchReviewTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isInvoiceTcode)) {
                    editor.putString(isInvoiceCreateKey, isInvoiceTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCollCreateTcode)) {
                    editor.putString(isCollCreateEnabledKey, isCollCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFeedbackTcode)) {
                    editor.putString(isFeedbackCreateKey, isFeedbackTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase("/ARTEC/SS_COMPINFO")) {
                    editor.putString("isCompInfoEnabled", "/ARTEC/SS_COMPINFO");
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isOutstandingListTcode)) {
                    editor.putString(isOutstandingListKey, isOutstandingListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isMerchReviewListTcode)) {
                    editor.putString(isMerchReviewListKey, isMerchReviewListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSOCreateTcode)) {
                    editor.putString(isSOCreateKey, isSOCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDBStockTcode)) {
                    editor.putString(isDBStockEnabled, isDBStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDaySummaryTcode)) {
                    editor.putString(isDaySummaryKey, isDaySummaryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDlrBehaviourTcode)) {
                    editor.putString(isDlrBehaviourKey, isDlrBehaviourTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRetailerStockTcode)) {
                    editor.putString(isRetailerStockKey, isRetailerStockTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isVisitSummaryTcode)) {
                    editor.putString(isVisitSummaryKey, isVisitSummaryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isVisualAidTcode)) {
                    editor.putString(isVisualAidEnabled, isVisualAidTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isStockListTcode)) {
                    editor.putString(isStockListKey, isStockListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isFeedBackListTcode)) {
                    editor.putString(isFeedBackListKey, isFeedBackListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isReturnOrderListTcode)) {
                    editor.putString(isReturnOrderListKey, isReturnOrderListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSecondarySalesListTcode)) {
                    editor.putString(isSecondarySalesListKey, isSecondarySalesListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSampleDisbursmentCreateTcode)) {
                    editor.putString(isSampleDisbursmentEnabledKey, isSampleDisbursmentCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCustomerComplaintCreateTcode)) {
                    editor.putString(isCustomerComplaintEnabledKey, isCustomerComplaintCreateTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isReturnOrderTcode)) {
                    editor.putString(isReturnOrderCreateEnabled, isReturnOrderTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isWindowDisplayTcode)) {
                    editor.putString(isWindowDisplayKey, isWindowDisplayTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSampleDisbursmentListTcode)) {
                    editor.putString(isSampleDisbursmentListKey, isSampleDisbursmentListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCustomerComplaintListTcode)) {
                    editor.putString(isCustomerComplaintListKey, isCustomerComplaintListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isExpenseEntryTcode)) {
                    editor.putString(isExpenseEntryKey, isExpenseEntryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isDigitalProductEntryTcode)) {
                    editor.putString(isDigitalProductEntryEnabled, isDigitalProductEntryTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isSchemeTcode)) {
                    editor.putString(isSchemeEnabled, isSchemeTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCompInfoTcode)) {
                    editor.putString(isCompInfoEnabled, isCompInfoTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isCompetitorListTcode)) {
                    editor.putString(isCompetitorListKey, isCompetitorListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isRetailerApprovalTcode)) {
                    editor.putString(isRetailerApprovalKey, isRetailerApprovalTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isExpenseListTcode)) {
                    editor.putString(isExpenseListKey, isExpenseListTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isTCPCTcode)) {
                    editor.putString(isTCPCEnabled, isTCPCTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isBeatTcode)) {
                    editor.putString(isBeatEnabled, isBeatTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isBrandTcode)) {
                    editor.putString(isBrandEnabled, isBrandTcode);
                } else if (authList.get(incVal).getFeature().equalsIgnoreCase(isVSTSUMTcode)) {
                    editor.putString(isisVSTSUMKey, isVSTSUMTcode);
                }

                editor.commit();
            }
        }
    }

    public static final void updateLogTCodeToSharedPreference(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, ArrayList<Config> authList) {
        if (authList != null && authList.size() > 0) {
            for (int incVal = 0; incVal < authList.size(); incVal++) {
                if (authList.get(incVal).getFeature().equalsIgnoreCase("ENABLEDBG")) {
                    editor.putBoolean("writeDBGLog", true);
                }
                editor.commit();
            }
        }

        if (sharedPreferences.getBoolean("writeDBGLog", false)) {
            Constants.writeDebug = sharedPreferences.getBoolean("writeDBGLog", false);
        }
    }

    public static String getSPGUID() {
        final String[] spGuid = {""};
        Thread thread = null;
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        spGuid[0] = OfflineManager.getGuidValueByColumnName(Constants.UserSalesPersons + "?$select=" + Constants.SPGUID, Constants.SPGUID);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        }
        return spGuid[0];
    }

    public static String[] getSPGUIDName() {
        String spGuid[] = new String[2];
        try {
            spGuid = OfflineManager.getSPGuidNameValue(Constants.UserSalesPersons + "?$filter=" + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spGuid;
    }

    public static void storeInDataVault(String docNo, String jsonHeaderObjectAsString, Context context) {
        try {
            ConstantsUtils.storeInDataVault(docNo, jsonHeaderObjectAsString, context);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
    }

    public static ErrorBean getErrorCode(int operation, Exception exception, Context context) {
        ErrorBean errorBean = new ErrorBean();
        try {
            int errorCode = 0;
            boolean hasNoError = true;
            if ((operation == Operation.Create.getValue())) {

                try {
                    // below error code getting from online manger (While posting data vault data)
//                    errorCode = ((ErrnoException) ((ODataNetworkException) exception).getCause().getCause()).errno;

                    if ((exception instanceof OnlineODataStoreException) && exception.getMessage().startsWith("Retailer already exist")) {

                    } else {
                        Throwable throwables = (((ODataNetworkException) exception).getCause()).getCause().getCause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (throwables instanceof ErrnoException) {
                                errorCode = ((ErrnoException) throwables).errno;
                            } else {
                                if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                    errorCode = Constants.UnAuthorized_Error_Code;
                                    hasNoError = false;
                                } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Comm_Error_Code;
                                } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                    hasNoError = false;
                                    errorCode = Constants.Network_Error_Code;
                                } else {
                                    Constants.ErrorNo = 0;
                                }
                            }
                        } else {
                            try {
                                if (exception.getMessage() != null) {
                                    if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                                        errorCode = Constants.UnAuthorized_Error_Code;
                                        hasNoError = false;
                                    } else if (exception.getMessage().contains(Constants.Comm_error_name)) {
                                        hasNoError = false;
                                        errorCode = Constants.Comm_Error_Code;
                                    } else if (exception.getMessage().contains(Constants.Network_Name)) {
                                        hasNoError = false;
                                        errorCode = Constants.Network_Error_Code;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        if (errorCode != Constants.UnAuthorized_Error_Code) {
                            if (errorCode == Constants.Network_Error_Code || errorCode == Constants.Comm_Error_Code) {
                                hasNoError = false;
                            } else {
                                hasNoError = true;
                            }
                        }
                    }
                } catch (Exception e1) {
                    if (exception.getMessage().contains(Constants.Unothorized_Error_Name)) {
                        errorCode = Constants.UnAuthorized_Error_Code;
                        hasNoError = false;
                    } else {
                        Constants.ErrorNo = 0;
                    }
                }
                LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());

            } else if (operation == Operation.OfflineFlush.getValue() || operation == Operation.OfflineRefresh.getValue() || operation == Operation.GetRequest.getValue()) {
                try {
                    // below error code getting from offline manger (While posting flush and refresh collection)
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }

                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Executing_SQL_Commnd_Error)) {
                            hasNoError = false;
                            errorCode = -10001;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                if (errorCode != 0) {
                    LogManager.writeLogError("Error : [" + errorCode + "]" + exception.getMessage());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                // below error code getting from offline manger (While posting flush and refresh collection)
                try {
                    errorCode = ((ODataOfflineException) ((ODataNetworkException) exception).getCause()).getCode();

                    // Display popup for Communication and Unauthorized errors
                    if (errorCode == Constants.Network_Error_Code_Offline
                            || errorCode == Constants.UnAuthorized_Error_Code_Offline
                            || errorCode == Constants.Unable_to_reach_server_offline
                            || errorCode == Constants.Resource_not_found
                            || errorCode == Constants.Unable_to_reach_server_failed_offline) {

                        hasNoError = false;
                    } else {
                        hasNoError = true;
                    }
                } catch (Exception e) {
                    try {
                        String mStrErrMsg = exception.getCause().getLocalizedMessage();
                        if (mStrErrMsg.contains(Store_Defining_Req_Not_Matched)) {
                            hasNoError = false;
                            errorCode = -10247;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            errorBean.setErrorCode(errorCode);
            if (exception.getMessage() != null && !exception.getMessage().equalsIgnoreCase("")) {
                errorBean.setErrorMsg(exception.getMessage());
            } else {
                errorBean.setErrorMsg(context.getString(R.string.unknown_error));
            }

            errorBean.setHasNoError(hasNoError);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code1 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code2 + "")
                || errorBean.getErrorMsg().contains(Constants.Build_Database_Failed_Error_Code3 + "")
                || errorBean.getErrorCode() == Constants.Execu_SQL_Error_Code
                || errorBean.getErrorCode() == Constants.Store_Def_Not_matched_Code) {
            if (errorBean.getErrorMsg().contains("500")
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100029)
                    || errorBean.getErrorMsg().contains(Constants.RFC_ERROR_CODE_100027)) {
                errorBean.setStoreFailed(false);
            } else {
                errorBean.setStoreFailed(true);
            }

        } else {
            errorBean.setStoreFailed(false);
        }


//        }
        if (errorBean.isStoreFailed()) {
            try {
                UtilConstants.closeStore(context,
                        OfflineManager.options, errorBean.getErrorMsg() + "",
                        offlineStore, Constants.PREFS_NAME, errorBean.getErrorCode() + "");

            } catch (Exception e) {
                e.printStackTrace();
            }
//            Constants.Entity_Set.clear();
//            Constants.AL_ERROR_MSG.clear();
            offlineStore = null;
            OfflineManager.options = null;
        }


        return errorBean;
    }

    public static void displayErrorDialog(Context context, String error_msg) {
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            UtilConstants.showAlert(error_msg, context);
        } else {
            Constants.customAlertDialogWithScroll(context, mErrorMsg);
        }
    }

    public static void displayMsgReqError(int errorCode, Context context) {
        if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
            if (errorCode == Constants.UnAuthorized_Error_Code_Offline) {
                String errorMessage = "Authorization failed,Your Password is expired. To change password go to Setting and click on Change Password";
                UtilConstants.showAlert(errorMessage, context);
            } else {
                UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin, errorCode + ""), context);
            }

        } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        } else if (errorCode == Constants.Resource_not_found) {
            UtilConstants.showAlert(context.getString(R.string.techincal_error_plz_contact, errorCode + ""), context);
        } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
            UtilConstants.showAlert(context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + ""), context);
        } else {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + ""), context);
        }
    }

    public static String makeMsgReqError(int errorCode, Context context, boolean isInvError) {
        String mStrErrorMsg = "";

        if (!isInvError) {
            if (errorCode == Constants.UnAuthorized_Error_Code || errorCode == Constants.UnAuthorized_Error_Code_Offline) {
//                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, errorCode + "");
                mStrErrorMsg = "Authorization failed,Your Password is expired. To change password go to Setting and click on Change Password";
            } else if (errorCode == Constants.Unable_to_reach_server_offline || errorCode == Constants.Network_Error_Code_Offline) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            } else if (errorCode == Constants.Resource_not_found) {
                mStrErrorMsg = context.getString(R.string.techincal_error_plz_contact, errorCode + "");
            } else if (errorCode == Constants.Unable_to_reach_server_failed_offline) {
                mStrErrorMsg = context.getString(R.string.comm_error_server_failed_plz_contact, errorCode + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, errorCode + "");
            }
        } else {
            if (errorCode == 4) {
                mStrErrorMsg = context.getString(R.string.auth_fail_plz_contact_admin, Constants.UnAuthorized_Error_Code + "");
            } else if (errorCode == 3) {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            } else {
                mStrErrorMsg = context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + "");
            }
        }

        return mStrErrorMsg;
    }

    public static void setSyncTime(Context context, String syncType) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        if (settings.getBoolean(Constants.isReIntilizeDB, false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isReIntilizeDB, false);
            editor.apply();
            try {
                try {
                    SyncUtils.initialInsert(context);  // create sync history table
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  SyncUtils.updateAllSyncHistory(context,syncType);
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
            }
        }
    }

    /**
     * SHOW PROGRESS DIALOG
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = null;
        try {
            progressDialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
            progressDialog.setMessage(message);
            progressDialog.setTitle(title);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }

    public static void getLocation(Activity mActivity, final LocationInterface locationInterface) {
        UtilConstants.latitude = 0.0;
        UtilConstants.longitude = 0.0;
        LocationUtils.getCustomLocation(mActivity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                if (status) {
                    android.location.Location location = locationModel.getLocation();
                    try {
                        try {
                            UtilConstants.latitude = UtilConstants.round(location.getLatitude(), 12);
                        } catch (Exception e) {
                            UtilConstants.latitude = location.getLatitude();
                            e.printStackTrace();
                        }
                        try {
                            UtilConstants.longitude = UtilConstants.round(location.getLongitude(), 12);
                        } catch (Exception e) {
                            UtilConstants.longitude = location.getLongitude();
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("LocationUtils", "location: " + locationModel.getLocationFrom());
                }
                if (locationInterface != null) {
                    locationInterface.location(status, locationModel, errorMsg, errorCode);
                }
            }
        });
    }

    public static String convertStrGUID32to36(String strGUID32) {
        return CharBuffer.join9(StringFunction.substring(strGUID32, 0, 8), "-", StringFunction.substring(strGUID32, 8, 12), "-", StringFunction.substring(strGUID32, 12, 16), "-", StringFunction.substring(strGUID32, 16, 20), "-", StringFunction.substring(strGUID32, 20, 32));
    }

    public static ArrayList<String> getPendingList() {
        ArrayList<String> alFlushColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alFlushColl.add(Constants.MerchReviews);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alFlushColl;
    }

    public static ArrayList<String> getRefreshList() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        try {
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.Visits);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.VisitActivities);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + Constants.isLocalFilterQry)) {
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return alAssignColl;
    }

    public static String getConcatinatinFlushCollectios(ArrayList<String> alFlushColl) {
        String concatFlushCollStr = "";
        for (int incVal = 0; incVal < alFlushColl.size(); incVal++) {
            if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else if (incVal == 0) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            } else if (incVal == alFlushColl.size() - 1) {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
            } else {
                concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
            }
        }

        return concatFlushCollStr;
    }

    @SuppressLint("NewApi")
    public static String getSyncType(Context context, String collectionName, String operation) {
        String mStrSyncType = "4";
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(collectionName, "");
        if (!sharedVal.equalsIgnoreCase("")) {
            if (operation.equalsIgnoreCase(CreateOperation)) {
                if (sharedVal.substring(0, 1).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(0, 1).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(ReadOperation)) {
                if (sharedVal.substring(1, 2).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(1, 2).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }

            } else if (operation.equalsIgnoreCase(UpdateOperation)) {
                if (sharedVal.substring(2, 3).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(2, 3).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(DeleteOperation)) {
                if (sharedVal.substring(3, 4).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(3, 4).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            } else if (operation.equalsIgnoreCase(QueryOperation)) {
                if (sharedVal.substring(4, 5).equalsIgnoreCase("0")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("1")) {
                    mStrSyncType = "1";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("2")) {
                    mStrSyncType = "2";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("3")) {
                    mStrSyncType = "3";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("4")) {
                    mStrSyncType = "4";
                } else if (sharedVal.substring(4, 5).equalsIgnoreCase("5")) {
                    mStrSyncType = "5";
                }
            }
        } else {
            mStrSyncType = "4";
        }
        return mStrSyncType;
    }

    public static String convertALBussinessMsgToString(ArrayList<String> arrayList) {
        String mErrorMsg = "";
        if (arrayList != null && arrayList.size() > 0) {
            for (String errMsg : arrayList) {
                if (mErrorMsg.length() == 0) {
                    mErrorMsg = mErrorMsg + errMsg;
                } else {
                    mErrorMsg = mErrorMsg + "\n" + errMsg;
                }
            }
        }
        return mErrorMsg;
    }

    public static void customAlertDialogWithScroll(final Context context, final String mErrTxt) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_scroll, null);

        String mStrErrorEntity = getErrorEntityName();

        TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);

        textview.setText(context.getString(R.string.msg_error_occured_during_sync_except) + " " + mStrErrorEntity + " \n" + mErrTxt);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyTheme);

        alertDialog.setCancelable(false)
                .setPositiveButton(context.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        copyMessageToClipBoard(context, mErrTxt);
                    }
                });

        alertDialog.setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public static void customAlertDialogWithScrollError(final Context context, final String mErrTxt, final Activity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.custom_dialog_scroll, null);

                    String mStrErrorEntity = getErrorEntityName();

                    TextView textview = (TextView) view.findViewById(R.id.tv_err_msg);
                    final TextView tvdetailmsg = (TextView) view.findViewById(R.id.tv_detail_msg);

                    if (mErrTxt.contains("invalid authentication") || mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
                        textview.setText(Constants.PasswordExpiredMsg);
                        tvdetailmsg.setText(mErrTxt);
                    } else {
                        textview.setText(context.getString(R.string.msg_error_occured_during_sync_except) + " " + mStrErrorEntity + " \n" + mErrTxt);
                    }

                    if (mErrTxt.contains("invalid authentication") || mErrTxt.contains("HTTP Status 401 ? Unauthorized")) {
                        final AlertDialog dialog = new AlertDialog.Builder(context)
                                .setView(view)
                                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                                .setNeutralButton("Show Details", null)
                                .setNegativeButton("Settings", null)
                                .create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                b.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // TODO Do something
                                        dialog.dismiss();
                                    }
                                });

                                final Button mesg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                                mesg.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // TODO Do something


                                        if (mesg.getText().equals("Hide Details")) {
                                            tvdetailmsg.setVisibility(View.GONE);
                                            mesg.setText("Show Details");
                                        } else {
                                            tvdetailmsg.setVisibility(View.VISIBLE);
                                            mesg.setText("Hide Details");
                                        }


                                        // dialog.dismiss();
                                    }
                                });
                                Button change = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                change.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        // TODO Do something
                                        RegistrationModel<Serializable> registrationModel = new RegistrationModel<>();
                                        Intent intent = new Intent(context, com.arteriatech.mutils.support.SecuritySettingActivity.class);
                                        registrationModel.setExtenndPwdReq(true);
                                        registrationModel.setUpdateAsPortalPwdReq(true);
                                        registrationModel.setIDPURL(Configuration.IDPURL);
                                        registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                                        registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                                        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
                                        //context.startActivityForResult(intent, 350);
                                        context.startActivity(intent);
                                        // dialog.dismiss();
                                    }
                                });

                            }
                        });
                        dialog.show();
                    } else {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.MyTheme);
                        if (mErrTxt.contains("Retailer already exist with mobile no")) {
                            alertDialog.setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.msg_retailer_error), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            openErrorRetailerActivity(context);
                                            activity.finish();
                                        }
                                    });
                        } else {
                            alertDialog.setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            copyMessageToClipBoard(context, mErrTxt);
                                        }
                                    });
                        }
                        alertDialog.setView(view);
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                }
            });


        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static void openErrorRetailerActivity(Context context) {
        Intent intent = new Intent(context, NotPostedRetailerActivity.class);
        context.startActivity(intent);
    }


    public static void copyMessageToClipBoard(Context context, String message) {
        ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error Message", message);
        clipboard.setPrimaryClip(clip);
        UtilConstants.showAlert(context.getString(R.string.issue_copied_to_clipboard_send_to_chnnel_team), context);
    }

    public static String getErrorEntityName() {
        String mEntityName = "";

        try {
            if (Constants.Entity_Set != null && Constants.Entity_Set.size() > 0) {

                if (Constants.Entity_Set != null && !Constants.Entity_Set.isEmpty()) {
                    Iterator itr = Constants.Entity_Set.iterator();
                    while (itr.hasNext()) {
                        if (mEntityName.length() == 0) {
                            mEntityName = mEntityName + itr.next().toString();
                        } else {
                            mEntityName = mEntityName + "," + itr.next().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            mEntityName = "";
        }

        return mEntityName;
    }

    public static void removeDeviceDocNoFromSharedPref(Context context, String createType, String refDocNo, SharedPreferences sharedPreferences, boolean isRemoveAllData) {
        if (!isRemoveAllData) {
            Set<String> set = new HashSet<>();
            set = sharedPreferences.getStringSet(createType, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.remove(refDocNo);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(createType, setTemp);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.contains(createType)) {
                editor.remove(createType);
                editor.commit();
            }
        }
    }

    public static ArrayList<String> removeDataValtFromSharedPref(Context context, String createType, String refDocNo, boolean isRemoveAllData) {
        ArrayList<String> dataValtCollectionName = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (createType.equalsIgnoreCase(Constants.SecondarySOCreate) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.SecondarySOCreate, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getSOsCollection());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.SecondarySOCreateTemp) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.SecondarySOCreateTemp, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getSOsCollection());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.Feedbacks) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.Feedbacks, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getFeedBack());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.FinancialPostings) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.FinancialPostings, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getFIPCollection());
                dataValtCollectionName.addAll(SyncUtils.getInvoice());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.SampleDisbursement) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.SampleDisbursement, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getInvoice());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.CPList) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.CPList, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getFOS());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.ROList) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.ROList, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getROsCollection());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.NOT_POSTED_RETAILERS) || isRemoveAllData) {
            sharedPreferences = context.getSharedPreferences(Constants.NOT_POSTED_RETAILERS, 0);
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.duplicateCPList, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getFOS());
                return dataValtCollectionName;
            }
        }
        if (createType.equalsIgnoreCase(Constants.Expenses) || isRemoveAllData) {
            Constants.removeDeviceDocNoFromSharedPref(context, Constants.Expenses, refDocNo, sharedPreferences, isRemoveAllData);
            if (!isRemoveAllData) {
                dataValtCollectionName.addAll(SyncUtils.getExpenseListCollection());
                return dataValtCollectionName;
            }
        }
        return dataValtCollectionName;
    }

    public static void updateLastSyncTimeToTable(Context mContext, ArrayList<String> alAssignColl, String syncType, String refguid) {
        SyncUtils.updatingSyncTime(mContext, alAssignColl, syncType, refguid, null);
    }

    public static void updateLastSyncTimeToTable(Context mContext, ArrayList<String> alAssignColl, String syncType, RefreshListInterface listInterface, String refGuid) {
        SyncUtils.updatingSyncTime(mContext, alAssignColl, syncType, refGuid, listInterface);
    }


    public static final void createTable(SQLiteDatabase db, String tableName, String clumsname) {
        try {
            String sql = Constants.create_table + tableName
                    + " ( " + clumsname + ", Status text );";
            Log.d(Constants.EventsData, Constants.on_Create + sql);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            String delSql = Constants.delete_from + tableName;
            db.execSQL(delSql);

        } catch (Exception e) {
            System.out.println("createTableKey(EventDataSqlHelper): " + e.getMessage());
        }
    }

    public static final void insertHistoryDB(SQLiteDatabase db, String tblName, String clmname, String value) {
        String sql = "INSERT INTO " + tblName + "( " + clmname + ") VALUES('"
                + value + "') ;";
        db.execSQL(sql);
    }

    public static final void updateStatus(SQLiteDatabase db, String tblName, String clmname, String value, String inspectionLot) {
        String sql = "UPDATE " + tblName + " SET  " + clmname + "='" + value
                + "' Where Collections = '" + inspectionLot + "';";
        db.execSQL(sql);
    }

    public static final void createDB(SQLiteDatabase db) {
        String sql = "create table if not exists "
                + Constants.DATABASE_REGISTRATION_TABLE
                + "( username  text, password   text,repassword text,themeId text,mainView text);";
        Log.d("EventsData", "onCreate: " + sql);
        db.execSQL(sql);
    }

    public static final void setIconVisibiltyReports(SharedPreferences sharedPreferences, int[] mArrIntReportsOriginalStatus) {
        String sharedVal = sharedPreferences.getString(isSecondarySalesListKey, "");
        if (sharedVal.equalsIgnoreCase(isSecondarySalesListTcode)) {
            mArrIntReportsOriginalStatus[0] = 1;
        } else {
            mArrIntReportsOriginalStatus[0] = 0;
        }
        sharedVal = sharedPreferences.getString(isReturnOrderListKey, "");
        if (sharedVal.equalsIgnoreCase(isReturnOrderListTcode)) {
            mArrIntReportsOriginalStatus[1] = 1;
        } else {
            mArrIntReportsOriginalStatus[1] = 0;
        }

        sharedVal = sharedPreferences.getString(isSecondaryInvoiceListKey, "");
        if (sharedVal.equalsIgnoreCase(isSecondaryInvoiceListTcode)) {
            mArrIntReportsOriginalStatus[2] = 1;
        } else {
            mArrIntReportsOriginalStatus[2] = 0;
        }

        sharedVal = sharedPreferences.getString(isCollListKey, "");
        if (sharedVal.equalsIgnoreCase(isCollListTcode)) {
            mArrIntReportsOriginalStatus[3] = 1;
        } else {
            mArrIntReportsOriginalStatus[3] = 0;
        }
        sharedVal = sharedPreferences.getString(isMerchReviewListKey, "");
        if (sharedVal.equalsIgnoreCase(isMerchReviewListTcode)) {
            mArrIntReportsOriginalStatus[4] = 1;
        } else {
            mArrIntReportsOriginalStatus[4] = 0;
        }

        sharedVal = sharedPreferences.getString(isFeedBackListKey, "");
        if (sharedVal.equalsIgnoreCase(isFeedBackListTcode)) {
            mArrIntReportsOriginalStatus[5] = 1;
        } else {
            mArrIntReportsOriginalStatus[5] = 0;
        }

        sharedVal = sharedPreferences.getString(isOutstandingListKey, "");
        if (sharedVal.equalsIgnoreCase(isOutstandingListTcode)) {
            mArrIntReportsOriginalStatus[6] = 1;
        } else {
            mArrIntReportsOriginalStatus[6] = 0;
        }

        sharedVal = sharedPreferences.getString(isCompetitorListKey, "");
        if (sharedVal.equalsIgnoreCase(isCompetitorListTcode)) {
            mArrIntReportsOriginalStatus[7] = 1;
        } else {
            mArrIntReportsOriginalStatus[7] = 0;
        }

        sharedVal = sharedPreferences.getString(isCustomerComplaintListKey, "");
        if (sharedVal.equalsIgnoreCase(isCustomerComplaintListTcode)) {
            mArrIntReportsOriginalStatus[8] = 1;
        } else {
            mArrIntReportsOriginalStatus[8] = 0;
        }

        sharedVal = sharedPreferences.getString(isRetailerTrendKey, "");
        if (sharedVal.equalsIgnoreCase(isRetailerTrendTcode)) {
            mArrIntReportsOriginalStatus[9] = 1;
        } else {
            mArrIntReportsOriginalStatus[9] = 0;
        }
        sharedVal = sharedPreferences.getString(isSampleDisbursmentListKey, "");

        if (sharedVal.equalsIgnoreCase(isSampleDisbursmentListTcode)) {
            mArrIntReportsOriginalStatus[10] = 1;
        } else {
            mArrIntReportsOriginalStatus[10] = 0;
        }

    }

    public static String getDistNameFromCPDMSDIV(String mStrCPGUID, String mStrSPGUID) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String selParentName = "";
        try {
            selParentName = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.ParentName + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ", Constants.ParentName);

        } catch (OfflineODataStoreException e) {
            selParentName = "";
            e.printStackTrace();
        }
        return selParentName;
    }

    public static void displayPieChart(String targetPer, PieChart pieChart, Context context, float textSize, String mStrValue) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        //spacing between graph and margin
        //mChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setExtraOffsets(-5, -5, -5, -5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
//        pieChart.setCenterText(Constants.generateCenterSpannableText(ConstantsUtils.decimalZeroBasedOnValue(targetPer) + "%"));
        pieChart.setCenterTextSize(textSize);
        pieChart.setCenterText(Constants.generateCenterSpannableText(mStrValue));

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        Constants.setPieChartData(targetPer, pieChart, context);
        pieChart.animateXY(1500, 1500);
//        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // entry label styling
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

//        pieChart.getTransformer().prepareMatrixValuePx(chart);
//        pieChart.mat().prepareMatrixOffset(chart);

//        pieChart.getContentRect().set(0, 0, pieChart.getWidth(), pieChart.getHeight());
        pieChart.invalidate();
    }

    public static void setPieChartData(String totalPercent, PieChart pieChart, Context context) {

        String remainingPercent = "0";
        try {
            remainingPercent = String.valueOf(100 - Integer.parseInt(totalPercent.split("\\.")[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        float flTotalPercent = Float.parseFloat(totalPercent);
        float flRemainingPer = Float.parseFloat(remainingPercent);

        List<PieEntry> entries = new ArrayList<>();
        if (flTotalPercent != 0f)
            entries.add(new PieEntry(flTotalPercent, ""));
        if (flRemainingPer != 0f)
            entries.add(new PieEntry(Float.parseFloat(remainingPercent), ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0f);

        //pie chart background color start
        ArrayList<Integer> colors = new ArrayList<Integer>();
        if (flTotalPercent != 0f)
            colors.add(ColorTemplate.rgb(String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(context, R.color.primaryColor)))));
        if (flRemainingPer != 0f)
            colors.add(Color.rgb(238, 238, 238));
        dataSet.setColors(colors);
        //pie chart background color end
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setDrawValues(false);
        pieChart.setData(data);
//        pieChart.highlightValue(0, 0, false);
        pieChart.highlightValue(null);
    }

    public static SpannableString generateCenterSpannableText(String totalPercent) {

        SpannableString s = new SpannableString(totalPercent);
        s.setSpan(new RelativeSizeSpan(2.5f), 0, s.length(), 0);
        return s;
    }

    public static String getTotalOrderValueByCurrentMonth(String mStrFirstDateMonth, String cpQry, String mStrCPDMSDIVQry) {

        double totalOrderVal = 0.0;

       /* String mStrOrderVal = "0.0";
        try {
            if(cpQry.equalsIgnoreCase("")){
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                        "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " ge datetime'" + mStrFirstDateMonth + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and "+mStrCPDMSDIVQry+" ", Constants.NetAmount);
            }else{
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoices +
                        "?$select=" + Constants.NetAmount + " &$filter=" + Constants.InvoiceDate + " ge datetime'" + mStrFirstDateMonth + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and ("+cpQry+") and "+mStrCPDMSDIVQry+" ", Constants.NetAmount);
            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        double mdouDevOrderVal = 0.0;

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;*/

        return totalOrderVal + "";
    }

    public static String getLastDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        Date lastDayOfMonth = cal.getTime();
        String currentDateTimeString1 = (String) DateFormat.format("yyyy-MM-dd", lastDayOfMonth);
        return getTimeformat2(currentDateTimeString1, (String) null);
    }

    public static String getTimeformat2(String date, String time) {
        String datefrt = "";
        datefrt = "00:00:00";
        String currentDateTimeString = date + "T" + datefrt;
        return currentDateTimeString;
    }

    public static String getFirstDateOfCurrentMonth() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static String getSPGUID(String columnName) {
        String spGuid = "";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.UserSalesPersons + "?$select=" + columnName, columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spGuid;
    }

    public static String getSOOrderType() {
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$top=1 &$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSSO' and  " +
                    "" + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000010' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return ordettype;
    }

    public static String getReturnOrderType() {
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$top=1 &$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSRO' and  " +
                    "" + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000020' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return ordettype;
    }

    public static String getVisitTargetForToday() {
        alTodayBeatRet.clear();
        String count = "0";
        ArrayList<RetailerBean> alRetailerList = new ArrayList<>();
        alRetailerList = getTodaysBeatRetailers();
        alTodayBeatRet.addAll(alRetailerList);
        count = (alRetailerList.size() > 0) ? String.valueOf(alRetailerList.size()) : "0";
        return count;
    }

    public static ArrayList<RetailerBean> getTodaysBeatRetailers() {
        ArrayList<RetailerBean> alRetailerList = new ArrayList<>();
        ArrayList<RetailerBean> alRSCHList = getTodayRoutePlan();
        if (alRSCHList != null && alRSCHList.size() > 0) {
            String mCPGuidQry = getCPFromRouteSchPlan(alRSCHList);
            try {
                if (!mCPGuidQry.equalsIgnoreCase("")) {
                    List<RetailerBean> listRetailers = OfflineManager.getTodayBeatRetailer(mCPGuidQry, Constants.mMapCPSeqNo);
                    alRetailerList = (ArrayList<RetailerBean>) listRetailers;
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        return alRetailerList;

    }

    public static ArrayList<RetailerBean> getTodayRoutePlan() {
        String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
        ArrayList<RetailerBean> alRSCHList = null;
        try {
            alRSCHList = OfflineManager.getTodayRoutes1(routeQry, "");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return alRSCHList;
    }

    public static String getCPFromRouteSchPlan(ArrayList<RetailerBean> alRouteList) {
        String mCPGuidQry = "", qryForTodaysBeat = "";
        if (alRouteList != null && alRouteList.size() > 0) {
            String mRSCHQry = "";
            // Routescope ID will be same for all the routes planned for the day hence first record scope is used to decide
            String routeSchopeVal = alRouteList.get(0).getRoutSchScope();
            if (alRouteList.size() > 1) {

                if (routeSchopeVal.equalsIgnoreCase("000001")) {
                    for (RetailerBean routeList : alRouteList) {
                        if (mRSCHQry.length() == 0)
                            mRSCHQry += " guid'" + routeList.getRschGuid().toUpperCase() + "'";
                        else
                            mRSCHQry += " or " + Constants.RouteSchGUID + " eq guid'" + routeList.getRschGuid().toUpperCase() + "'";

                    }

                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans

                }

            } else {


                if (routeSchopeVal.equalsIgnoreCase("000001")) {

                    mRSCHQry = "guid'" + alRouteList.get(0).getRschGuid().toUpperCase() + "'";


                } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                    // Get the list of retailers from RoutePlans
                }

            }
            qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=(" +
                    Constants.RouteSchGUID + " eq " + mRSCHQry + ") &$orderby=" + Constants.SequenceNo + "";

            try {
                // Prepare Today's beat Retailer Query
                mCPGuidQry = OfflineManager.getBeatList(qryForTodaysBeat);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        return mCPGuidQry;
    }

    /*returns total number of retailers visited(Route plan)*/
    public static String getVisitedRetailerCount() {
        String mTodayBeatVisitCount = "0";
        try {
            if (alTodayBeatRet != null && alTodayBeatRet.size() > 0) {
                String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' " + "and (" + Constants.VisitCatID + " eq '" + Constants.str_01 + "' ) and StatusID ne '02' and " + Constants.CPTypeID + " eq '" + Constants.str_02 + "'";
                Set<String> retList = new HashSet<>();
                try {
                    retList = OfflineManager.getUniqueOutVisitFromVisit(mVisitQry);
                    mTodayBeatVisitCount = retList.size() + "";
                } catch (Exception e) {
                    mTodayBeatVisitCount = "0";
                }

            } else {
                mTodayBeatVisitCount = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTodayBeatVisitCount;
    }

    public static String getVisitedCount() {
        String mTodayBeatVisitCount = "0";
        try {
            String qry = Constants.Visits + "?$filter=" + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.CPTypeID + " eq '" + Constants.str_02 + "'";
            List<String> retList = new ArrayList<>();
            try {
                mTodayBeatVisitCount = OfflineManager.getUniqueVisitCount(qry) + "";
//                    mTodayBeatVisitCount = retList.size() + "";
            } catch (Exception e) {
                mTodayBeatVisitCount = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mTodayBeatVisitCount;
    }

    public static DmsDivQryBean getDMSDIV(String mStrParentID) {
        DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
        try {
            if (mStrParentID.equalsIgnoreCase("")) {
                dmsDivQryBean = OfflineManager.getDMSDIVQry(Constants.CPSPRelations);
            } else {
                dmsDivQryBean = OfflineManager.getDMSDIVQry(Constants.CPSPRelations + "?$filter=" + Constants.CPGUID + " eq '" + mStrParentID + "'");
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return dmsDivQryBean;
    }

    public static DmsDivQryBean getDMSDIVForSchemes(String mStrParentID) {
        DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
        try {
            if (mStrParentID.equalsIgnoreCase("")) {
                dmsDivQryBean = OfflineManager.getDMSDIVQryForSchemes(Constants.CPSPRelations);
            } else {
                dmsDivQryBean = OfflineManager.getDMSDIVQryForSchemes(Constants.CPSPRelations + "?$filter=" + Constants.CPGUID + " eq '" + mStrParentID + "'");
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return dmsDivQryBean;
    }

    public static int getBalanceVisit(ArrayList<RetailerBean> alTodaysRetailers) {
        int mIntBalVisitRet = 0;
        String mStrRetQry = "";
        if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
            for (int i = 0; i < alTodaysRetailers.size(); i++) {
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.VisitCPGUID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "(" + Constants.VisitCPGUID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.VisitCPGUID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20" + Constants.VisitCPGUID + "%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "'";
                }
            }
        }

        if (!mStrRetQry.equalsIgnoreCase("")) {
            String mStrBalVisitQry = Constants.RouteSchedulePlans + "?$filter = " + mStrRetQry + " ";
            try {
                mIntBalVisitRet = OfflineManager.getBalanceRetVisitRoute(mStrBalVisitQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else {
            mIntBalVisitRet = 0;
        }

        return mIntBalVisitRet;
    }

    public static String getTotalOrderValue(Context context, String mStrCurrentDate,
                                            ArrayList<RetailerBean> alTodaysRetailers) {

        String mSOOrderType = getSOOrderType();
        double totalOrderVal = 0.0;

        String mStrRetQry = "", ssINVRetQry = "";
        boolean isReadAllRetail = true;
        if (isReadAllRetail) {
            List<ODataEntity> entities = null;
            try {
//                entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, "CPDMSDivisions?$filter=StatusID eq '01' and ApprvlStatusID eq '03' and DMSDivision ne ''");
                if (entities != null) {
                    int i = 0;
                    int totalSize = entities.size();
                    for (ODataEntity entity : entities) {
                        ODataPropMap properties = entity.getProperties();
                        ODataProperty property = properties.get(Constants.CPNo);
                        String cpNo = (String) property.getValue();
                        property = properties.get(Constants.CPGUID);
                        ODataGuid mCpGuid = null;
                        String cpguid = "";
                        try {
                            mCpGuid = (ODataGuid) property.getValue();
//                            retBean.setCPGUID(mCpGuid.guidAsString36().toUpperCase());
                            cpguid = mCpGuid.guidAsString32().toUpperCase() + "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (i == 0 && i == totalSize - 1) {
                            mStrRetQry = mStrRetQry
                                    + "(" + Constants.SoldToId + "%20eq%20'"
                                    + cpNo + "')";

                            ssINVRetQry = ssINVRetQry
                                    + "(" + Constants.SoldToID + "%20eq%20'"
                                    + cpNo + "')";

                        } else if (i == 0) {
                            mStrRetQry = mStrRetQry
                                    + "(" + Constants.SoldToId + "%20eq%20'"
                                    + cpNo + "'";

                            ssINVRetQry = ssINVRetQry
                                    + "(" + Constants.SoldToID + "%20eq%20'"
                                    + cpNo + "'";

                        } else if (i == totalSize - 1) {
                            mStrRetQry = mStrRetQry
                                    + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                                    + cpNo + "')";

                            ssINVRetQry = ssINVRetQry
                                    + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                                    + cpNo + "')";
                        } else {
                            mStrRetQry = mStrRetQry
                                    + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                                    + cpNo + "'";

                            ssINVRetQry = ssINVRetQry
                                    + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                                    + cpNo + "'";
                        }
                        if (!alRetailers.contains(cpNo)) {
                            alRetailers.add(cpNo);
                            alRetailersGuid.add(cpguid);
                        }
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (alTodaysRetailers != null && alTodaysRetailers.size() > 0) {
                for (int i = 0; i < alTodaysRetailers.size(); i++) {
                    if (i == 0 && i == alTodaysRetailers.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToId + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "')";

                        ssINVRetQry = ssINVRetQry
                                + "(" + Constants.SoldToID + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "')";

                    } else if (i == 0) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToId + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "'";

                        ssINVRetQry = ssINVRetQry
                                + "(" + Constants.SoldToID + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "'";

                    } else if (i == alTodaysRetailers.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "')";

                        ssINVRetQry = ssINVRetQry
                                + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "')";
                    } else {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToId + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "'";

                        ssINVRetQry = ssINVRetQry
                                + "%20or%20" + Constants.SoldToID + "%20eq%20'"
                                + alTodaysRetailers.get(i).getCPNo() + "'";
                    }

                    if (!alRetailers.contains(alTodaysRetailers.get(i).getCPNo())) {
                        alRetailers.add(alTodaysRetailers.get(i).getCPNo());
                        alRetailersGuid.add(alTodaysRetailers.get(i).getCpGuidStringFormat());
                        alRetailersGuid36.add(alTodaysRetailers.get(i).getCPGUID().toUpperCase());
                    }
                }
            }
        }
        Constants.SS_INV_RET_QRY = mStrRetQry;
        String mStrOrderVal = "0.0";
//        if (alRetailers.size() > 0) {
        try {
            if (!mStrRetQry.equalsIgnoreCase("")) {
//                    mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
//                            "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and Status ne '000004' and " + mStrRetQry + " ", Constants.NetPrice);
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                        "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and Status ne '000004'", Constants.NetPrice);
            } else {
                mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                        "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and Status ne '000004'", Constants.NetPrice);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        }
        double mdouDevOrderVal = 0.0;

//        if (alRetailers.size() > 0) {
        try {
            mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmt(Constants.SecondarySOCreateTemp, context, mStrCurrentDate, alRetailers);
        } catch (Exception e) {
            mdouDevOrderVal = 0.0;
        }
//        }

        totalOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return totalOrderVal + "";
    }

    public static String getyourOrderValue(Context context, String mStrCurrentDate) {

        String mSOOrderType = getSOOrderType();
        double yourOrderVal = 0.0;


        String mStrOrderVal = "0.0";
        String spGuid = Constants.getSPGUID();
//        if (alRetailers.size() > 0) {
        try {

            mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                    "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and " + Constants.SPGUID + " eq guid'" + spGuid + "' and Status ne '000004'", Constants.NetPrice);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        }
        double mdouDevOrderVal = 0.0;

//        if (alRetailers.size() > 0) {
        try {
            mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmt(Constants.SecondarySOCreateTemp, context, mStrCurrentDate, alRetailers);
        } catch (Exception e) {
            mdouDevOrderVal = 0.0;
        }
//        }

        yourOrderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return yourOrderVal + "";
    }

    public static String getDeviceTLSD(ArrayList<String> alTodaysRetailers, String entityType, Context mContext) {

        double mDoubleDevTLSD = 0.0;
        if (alTodaysRetailers.size() > 0) {
            try {
                mDoubleDevTLSD = OfflineManager.getTLSD(entityType, mContext,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mDoubleDevTLSD = 0.0;
                e.printStackTrace();
            }
        }
        return mDoubleDevTLSD + "";

    }

    public static String getDeviceTLSDOffline(String mStrSoldToID) {
        String mStrQry = "", mStrOfflineTLSD = "0";
        if (!Constants.SS_INV_RET_QRY.equalsIgnoreCase("")) {
            try {
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSSOs + "?$filter= " + Constants.OrderDate +
                        " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and " + Constants.SS_INV_RET_QRY + " ", Constants.SSSOGuid);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        if (!mStrQry.equalsIgnoreCase("")) {
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SSSoItemDetails + "?$filter=" + Constants.IsfreeGoodsItem + " ne '" + Constants.X + "' and " + mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }


        Double mDouTotalTLSD = Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD + "";
    }

    public static String getDeviceBillCut(ArrayList<String> alTodaysRetailers, String entityType, Context mContext) {

        int mIntDevBillCut = 0;
        if (alTodaysRetailers.size() > 0) {
            try {
                String mQry = Constants.SSSOs + "?$select=" + Constants.SoldToId + " &$filter= " + Constants.OrderDate + " eq datetime'"
                        + UtilConstants.getNewDate() + "' and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and " + Constants.SS_INV_RET_QRY + " ";
                mIntDevBillCut = OfflineManager.getUniqueBillCut(entityType, mContext,
                        UtilConstants.getNewDate(), alTodaysRetailers, mQry, Constants.SoldToId);
            } catch (Exception e) {
                mIntDevBillCut = 0;
                e.printStackTrace();
            }
        }
        return mIntDevBillCut + "";
    }

    public static void createVisit(Map<String, String> parameterMap, String cpGuid, Context context, UIListener listener) {

        try {
            Thread.sleep(100);

            GUID guid = GUID.newRandom();

            Hashtable table = new Hashtable();
            //noinspection unchecked
            table.put(Constants.CPNo, UtilConstants.removeLeadingZeros(parameterMap.get(Constants.CPNo)));

            table.put(Constants.CPName, parameterMap.get(Constants.CPName));
            //noinspection unchecked
            table.put(Constants.STARTDATE, UtilConstants.getNewDateTimeFormat());

            final Calendar calCurrentTime = Calendar.getInstance();
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
            }

            table.put(Constants.STARTTIME, oDataDuration);

            try {
                //noinspection unchecked
                table.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                //noinspection unchecked
                table.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //noinspection unchecked
            table.put(Constants.EndLat, "");
            //noinspection unchecked
            table.put(Constants.EndLong, "");
            //noinspection unchecked
            table.put(Constants.ENDDATE, "");
            //noinspection unchecked
            table.put(Constants.ENDTIME, "");
            //noinspection unchecked
            table.put(Constants.VISITKEY, guid.toString().toUpperCase());

            table.put(Constants.StatusID, parameterMap.get(Constants.StatusID));
            if (!TextUtils.isEmpty(parameterMap.get(Constants.BeatGUID))) {
                table.put(Constants.BeatGUID, parameterMap.get(Constants.BeatGUID));
            }

            table.put(Constants.VisitCatID, parameterMap.get(Constants.VisitCatID));
            try {
                table.put(Constants.ZZPARENT, parameterMap.get(Constants.ZZPARENT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                table.put(Constants.ZZDEVICE_ID, parameterMap.get(Constants.ZZDEVICE_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }

            table.put(Constants.CPTypeID, parameterMap.get(Constants.CPTypeID));

            if (parameterMap.get(Constants.PlannedDate) != null) {
                table.put(Constants.PlannedDate, parameterMap.get(Constants.PlannedDate));
            } else {
                table.put(Constants.PlannedDate, "");
            }

            if (parameterMap.get(Constants.PlannedStartTime) != null) {
                ODataDuration startDuration = UtilConstants.getTimeAsODataDuration(parameterMap.get(Constants.PlannedStartTime));
                table.put(Constants.PlannedStartTime, startDuration);
            } else {
                table.put(Constants.PlannedStartTime, "");
            }

            if (parameterMap.get(Constants.PlannedEndTime) != null) {
                ODataDuration endDuration = UtilConstants.getTimeAsODataDuration(parameterMap.get(Constants.PlannedEndTime));
                table.put(Constants.PlannedEndTime, endDuration);
            } else {
                table.put(Constants.PlannedEndTime, "");
            }

            //noinspection unchecked
            if (parameterMap.get(Constants.Remarks) != null) {
                table.put(Constants.Remarks, parameterMap.get(Constants.Remarks));
            }

            table.put(Constants.VisitTypeID, parameterMap.get(Constants.VisitTypeID));
            table.put(Constants.VisitTypeDesc, parameterMap.get(Constants.VisitTypeDesc));


            if (parameterMap.get(Constants.VisitDate) != null) {
                table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
            } else {
                table.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
            }

            table.put(Constants.CPGUID, cpGuid.toUpperCase());

//            String[][] mArraySPValues = getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(cpGuid.guidAsString36().toUpperCase());
//
//            try {
//                table.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
//            } catch (Exception e) {
            table.put(Constants.SPGUID, Constants.getSPGUID());
//            }

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);

//            int sharedVal = sharedPreferences.getInt("VisitSeqId", 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            table.put(Constants.LOGINID, loginIdVal);

            try {
                table.put(Constants.VisitSeq, parameterMap.get(Constants.VisitSeq));
            } catch (Exception e) {
                table.put(Constants.VisitSeq, "");
                e.printStackTrace();
            }

           /* sharedVal++;

            SharedPreferences sharedPreferencesVal = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, sharedVal);
            editor.commit();*/


            String mStrRoutePlanKey = Constants.Route_Plan_Key;
            if (!mStrRoutePlanKey.equalsIgnoreCase("")) {
                String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRoutePlanKey, 0, 8), "-", StringFunction.substring(mStrRoutePlanKey, 8, 12), "-", StringFunction.substring(mStrRoutePlanKey, 12, 16), "-", StringFunction.substring(mStrRoutePlanKey, 16, 20), "-", StringFunction.substring(mStrRoutePlanKey, 20, 32));
                //noinspection unchecked
                table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
            } else {
                String mStrRouteKey = getRouteNo(cpGuid);
                if (mStrRouteKey.equalsIgnoreCase("")) {
                    table.put(Constants.ROUTEPLANKEY, "");
                } else {
                    String mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRouteKey, 0, 8), "-", StringFunction.substring(mStrRouteKey, 8, 12), "-", StringFunction.substring(mStrRouteKey, 12, 16), "-", StringFunction.substring(mStrRouteKey, 16, 20), "-", StringFunction.substring(mStrRouteKey, 20, 32));
                    table.put(Constants.ROUTEPLANKEY, mStrRouteGuidFormat.toUpperCase());
                }

            }

            try {
                //noinspection unchecked
                OfflineManager.createVisit(table, listener,context);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (InterruptedException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    private static String getRouteNo(String mCpGuid) {

        String mStrRouteKey = "";
        String qryStr = Constants.RouteSchedulePlans + "?$filter=" + Constants.VisitCPGUID + " eq '" + mCpGuid.toUpperCase() + "' ";
        try {
            mStrRouteKey = OfflineManager.getRoutePlanKeyNew(qryStr);

        } catch (OfflineODataStoreException e) {
            mStrRouteKey = "";
            e.printStackTrace();
        }
        return mStrRouteKey;
    }

    public static void printLog(String message) {
        Log.d("OnlineStore", "error : " + message);
        LogManager.writeLogError("error : " + message);
    }

    public static void printLogInfo(String message) {
        Log.d("OnlineStore", "info : " + message);
        LogManager.writeLogInfo("info : " + message);
    }

    public static InputFilter getNumberAlphabetOnly() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9 ]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static InputFilter getNumberAlphabet() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static InputFilter getNumberAlphabetCaps() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[A-Z0-9]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static InputFilter getNumberOnly() {
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[0-9]*")) //put your constraints here
                {
                    return source;
                }
                return "";
            }
        };
        return filter;
    }

    public static InputFilter[] getAlphabetOnly(int maxLength) {
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        return FilterArray;
    }

    public static void saveDeviceDocNoToSharedPref(Context context, String createType, String refDocNo) {
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);

        HashSet<String> setTemp = new HashSet<>();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                setTemp.add(itr.next().toString());
            }
        }
        setTemp.add(refDocNo);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(createType, setTemp);
        editor.commit();
    }

    public static void onVisitActivityUpdate(Context mContext, String mStrBundleCPGUID32,
                                             String visitActRefID, String vistActType,
                                             String visitActTypeDesc, ODataDuration mStartTimeDuration) {
        try {
            //========>Start VisitActivity
            Hashtable visitActivityTable = new Hashtable();
            String mStrSPGUID = Constants.getSPGUID();
            String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + mStrSPGUID + "'";
            ODataGuid mGuidVisitId = null;
            try {
                mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            if (mGuidVisitId != null) {
                GUID mStrGuide = GUID.newRandom();
                visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
                visitActivityTable.put(Constants.LOGINID, loginIdVal);
                visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
                visitActivityTable.put(Constants.ActivityType, vistActType);
                visitActivityTable.put(Constants.ActivityTypeDesc, visitActTypeDesc);
                visitActivityTable.put(Constants.ActivityRefID, visitActRefID);
                try {
                    visitActivityTable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                    visitActivityTable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                visitActivityTable.put(Constants.StartTime, mStartTimeDuration);
                visitActivityTable.put(Constants.EndTime, UtilConstants.getOdataDuration());

                try {
                    OfflineManager.createVisitActivity(visitActivityTable);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //========>End VisitActivity
    }

    public static String[][] getDistributorsByCPGUID(Context mContext, String mStrCPGUID) {
        String mDBStkType = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.DSTSTKVIEW);
        String spGuid = "";
        String qryStr = "";
        String[][] mArrayDistributors = null;

        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.DSTSTKVIEW + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                if (mDBStkType.equalsIgnoreCase(Constants.str_01)) {
                    try {
                        spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                    if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
                    } else {
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "'";
                    }
                } else {
                    qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
                }
            } else {
                mDBStkType = "";
                qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mArrayDistributors = OfflineManager.getDistributorListByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[11][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
            mArrayDistributors[10][0] = "";
        } else {
            try {
                if (mArrayDistributors[4][0] != null) {

                }
            } catch (Exception e) {
                mArrayDistributors = new String[11][1];
                mArrayDistributors[0][0] = "";
                mArrayDistributors[1][0] = "";
                mArrayDistributors[2][0] = "";
                mArrayDistributors[3][0] = "";
                mArrayDistributors[4][0] = "";
                mArrayDistributors[5][0] = "";
                mArrayDistributors[6][0] = "";
                mArrayDistributors[7][0] = "";
                mArrayDistributors[8][0] = "";
                mArrayDistributors[9][0] = "";
                mArrayDistributors[10][0] = "";
            }
        }

        return mArrayDistributors;
    }

    public static String[][] getDistributorsByCPGUID(Context mContext, String mStrCPGUID, String parentID) {
        String mDBStkType = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.DSTSTKVIEW);
        String spGuid = "";
        String qryStr = "";
        String[][] mArrayDistributors = null;
        String tempParentID = "";
        if (!TextUtils.isEmpty(parentID)) {
            tempParentID = String.valueOf(Integer.parseInt(parentID));
            tempParentID = UtilConstants.addZerosBeforeNumber(tempParentID, 10);
        }

        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.DSTSTKVIEW + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                if (mDBStkType.equalsIgnoreCase(Constants.str_01)) {
                    try {
                        spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                    if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' and " + Constants.ParentID + " eq '" + tempParentID + "'";
                    } else {
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.ParentID + " eq '" + tempParentID + "'";
                    }
                } else {
//                    String tempParentID = "";
                    if (!TextUtils.isEmpty(parentID)) {
                        tempParentID = String.valueOf(Integer.parseInt(parentID));
                    }
                    qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.ParentID + " eq '" + tempParentID + "'";
                }
            } else {
//                String tempParentID = "";
                if (!TextUtils.isEmpty(parentID)) {
                    tempParentID = String.valueOf(Integer.parseInt(parentID));
                }
                mDBStkType = "";
                qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.ParentID + " eq '" + tempParentID + "'";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mArrayDistributors = OfflineManager.getDistributorListByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[11][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
            mArrayDistributors[10][0] = "";
        } else {
            try {
                if (mArrayDistributors[4][0] != null) {

                }
            } catch (Exception e) {
                mArrayDistributors = new String[11][1];
                mArrayDistributors[0][0] = "";
                mArrayDistributors[1][0] = "";
                mArrayDistributors[2][0] = "";
                mArrayDistributors[3][0] = "";
                mArrayDistributors[4][0] = "";
                mArrayDistributors[5][0] = "";
                mArrayDistributors[6][0] = "";
                mArrayDistributors[7][0] = "";
                mArrayDistributors[8][0] = "";
                mArrayDistributors[9][0] = "";
                mArrayDistributors[10][0] = "";
            }
        }

        return mArrayDistributors;
    }

    public static String[][] getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(String mStrCPGUID, Context mContext) {
        String spGuid = Constants.getSPGUID(Constants.SPGUID);
        String selCPDMSDIV = "";
        try {
            if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                selCPDMSDIV = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.DMSDivision + " &$filter="
                        + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ", Constants.DMSDivision);
            } else {
                selCPDMSDIV = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.DMSDivision + " &$filter="
                        + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ", Constants.DMSDivision);
            }
//            selCPDMSDIV = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.DMSDivision + " &$filter="
//                    + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "+Constants.PartnerMgrGUID+" eq guid'"+spGuid.toUpperCase()+"' ", Constants.DMSDivision);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String[][] mArraySPValues = null;
        String qryStr = "";
        if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
            qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "
                    + Constants.DMSDivision + " eq '" + selCPDMSDIV + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
        } else {
            qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "
                    + Constants.DMSDivision + " eq '" + selCPDMSDIV + "' ";
        }
//        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and "
//                + Constants.DMSDivision + " eq '" + selCPDMSDIV + "' and "+Constants.PartnerMgrGUID+" eq guid'"+spGuid.toUpperCase()+"'";
        try {
            mArraySPValues = OfflineManager.getSPValuesByCPGUIDAndDMSDivision(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


        if (mArraySPValues == null) {
            mArraySPValues = new String[12][1];
            mArraySPValues[0][0] = "";
            mArraySPValues[1][0] = "";
            mArraySPValues[2][0] = "";
            mArraySPValues[3][0] = "";
            mArraySPValues[4][0] = "";
            mArraySPValues[5][0] = "";
            mArraySPValues[6][0] = "";
            mArraySPValues[7][0] = "";
            mArraySPValues[8][0] = "";
            mArraySPValues[9][0] = "";
            mArraySPValues[10][0] = "";
            mArraySPValues[11][0] = "";
        } else {
            try {
                if (mArraySPValues[4][0] != null) {

                }
            } catch (Exception e) {
                mArraySPValues = new String[12][1];
                mArraySPValues[0][0] = "";
                mArraySPValues[1][0] = "";
                mArraySPValues[2][0] = "";
                mArraySPValues[3][0] = "";
                mArraySPValues[4][0] = "";
                mArraySPValues[5][0] = "";
                mArraySPValues[6][0] = "";
                mArraySPValues[7][0] = "";
                mArraySPValues[8][0] = "";
                mArraySPValues[9][0] = "";
                mArraySPValues[10][0] = "";
                mArraySPValues[11][0] = "";
            }
        }

        return mArraySPValues;
    }

    public static String getName(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static void displayMsgINet(int errCode, Context context) {
        if (errCode == 4) {
            UtilConstants.showAlert(context.getString(R.string.auth_fail_plz_contact_admin, Constants.UnAuthorized_Error_Code + ""), context);
        } else if (errCode == 3) {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + ""), context);
        } else {
            UtilConstants.showAlert(context.getString(R.string.data_conn_lost_during_sync_error_code, Constants.Network_Error_Code + ""), context);
        }
    }

    public static Hashtable getFeedbackHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.FeebackGUID, fetchJsonHeaderObject.getString(Constants.FeebackGUID));
            //noinspection unchecked
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            //noinspection unchecked
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));

            //noinspection unchecked
            dbHeadTable.put(Constants.FeedbackType, fetchJsonHeaderObject.getString(Constants.FeedbackType));
            dbHeadTable.put(Constants.FeedbackTypeDesc, fetchJsonHeaderObject.getString(Constants.FeedbackTypeDesc));

            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));


            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));

            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));

            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));

            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));

            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentTypDesc, fetchJsonHeaderObject.getString(Constants.ParentTypDesc));
            dbHeadTable.put(Constants.FeedbackDate, fetchJsonHeaderObject.optString(Constants.FeedbackDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static Hashtable getSOHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.SSSOGuid, fetchJsonHeaderObject.getString(Constants.SSSOGuid));
            dbHeadTable.put(Constants.BeatGuid, fetchJsonHeaderObject.getString(Constants.BeatGuid));
            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));

//            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PONo, getStartTime());
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypId, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToId, fetchJsonHeaderObject.getString(Constants.SoldToId));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToType, fetchJsonHeaderObject.getString(Constants.SoldToType));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.GrossAmt, fetchJsonHeaderObject.getString(Constants.GrossAmt));
            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    private static String getStartTime() {
        String startTime = "";
        try {
            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
            String time = localDateFormat.format(new Date());
            startTime = time.replaceAll(":", "");
        } catch (Exception e) {
            startTime = "";
            e.printStackTrace();
        }
        try {
            if (Constants.iSAutoSync) {
                startTime = "A" + startTime;
            } else if (Constants.isBackGroundSync) {
                startTime = "B" + startTime;
            } else if (Constants.isPullDownSync) {
                startTime = "P" + startTime;
            } else {
                startTime = "U" + startTime;
            }
        } catch (Exception e) {
            startTime = "";
            e.printStackTrace();
        }
        return startTime;
    }

    public static Hashtable getCollHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {

        Hashtable dbHeadTable = new Hashtable();

        try {

            dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.BankID, fetchJsonHeaderObject.getString(Constants.BankID));
            dbHeadTable.put(Constants.BankName, fetchJsonHeaderObject.getString(Constants.BankName));
            dbHeadTable.put(Constants.BranchName, fetchJsonHeaderObject.getString(Constants.BranchName));
            dbHeadTable.put(Constants.InstrumentNo, fetchJsonHeaderObject.getString(Constants.InstrumentNo));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            dbHeadTable.put(Constants.FIPDocType, fetchJsonHeaderObject.getString(Constants.FIPDocType));
            dbHeadTable.put(Constants.PaymentModeID, fetchJsonHeaderObject.getString(Constants.PaymentModeID));
            dbHeadTable.put(Constants.FIPDate, fetchJsonHeaderObject.getString(Constants.FIPDate));
            dbHeadTable.put(Constants.InstrumentDate, fetchJsonHeaderObject.getString(Constants.InstrumentDate));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.FIPGUID, fetchJsonHeaderObject.getString(Constants.FIPGUID));
            dbHeadTable.put(Constants.SPGuid, fetchJsonHeaderObject.getString(Constants.SPGuid));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.ParentNo, fetchJsonHeaderObject.getString(Constants.ParentNo));
            try {
                dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPFirstName, fetchJsonHeaderObject.getString(Constants.SPFirstName));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Source, fetchJsonHeaderObject.getString(Constants.Source));
            dbHeadTable.put(Constants.DMSDivision, fetchJsonHeaderObject.getString(Constants.DMSDivision));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static String formatValue(String key, String value) {

        String formattedValue = "";
        if (!key.equalsIgnoreCase("") && !value.equalsIgnoreCase("")) {

            formattedValue = value + " - " + key;

        } else {
            if (!key.equalsIgnoreCase("") || !value.equalsIgnoreCase("")) {
                formattedValue = value + " - " + key;

            }

        }

        return formattedValue;

    }

    public static boolean restartApp(Activity activity) {
        /*LogonCoreContext lgCtx1 = null;
        try {
            lgCtx1 = LogonCore.getInstance().getLogonContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lgCtx1 == null) {

            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAppRestart", true);
            editor.commit();
            Log.e("Restart", "Called");
            activity.finishAffinity();
            Intent dialogIntent = new Intent(activity, RegistrationActivity.class);
            activity.startActivity(dialogIntent);
        } else {
            return false;

        }*/
        return false;
    }

    public static String getConfigTypeIndicator(String collName, String columnName, String whereColumnn, String whereColval, String propertyColumn, String propVal) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "' and " + propertyColumn + " eq '" + propVal + "' ", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static void setPermissionStatus(Context mContext, String key, boolean value) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = permissionStatus.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPermissionStatus(Context mContext, String key) {
        SharedPreferences permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        return permissionStatus.getBoolean(key, false);
    }

    public static void navigateToAppSettingsScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static TextView setFontSizeByMaxText(TextView textView) {
        try {
            int lineCount = textView.getText().length();

            if (lineCount < 20) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else if (lineCount < 35) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            } else if (lineCount < 50) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            } else if (lineCount < 70) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            } else if (lineCount < 85) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            } else {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        return textView;
    }

    public static int dpToPx(int i, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = i * ((int) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static String convertHashMapToString(HashMap<String, String> hashMap, String propertyName) {
        String mStrQry = "";
        if (hashMap != null && !hashMap.isEmpty()) {
            Iterator iterator = hashMap.keySet().iterator();
            int incVal = 0;
            while (iterator.hasNext()) {
                if (incVal == 0 && incVal == hashMap.size() - 1) {
                    mStrQry = mStrQry
                            + "(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "')";

                } else if (incVal == 0) {
                    mStrQry = mStrQry
                            + "((" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "')";

                } else if (incVal == hashMap.size() - 1) {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "'))";
                } else {
                    mStrQry = mStrQry
                            + "%20or%20(" + propertyName + "%20eq%20'"
                            + iterator.next().toString() + "') ";
                }
                incVal++;
            }
        }

        return mStrQry;
    }

    public static String getOrderQtyByRetiler(String mStrRetNo, String mStrCurrentDate, Context context, String ssoQry) {
        String mStrOrderQty = "0.0";
        double orderQty = 0.0;
        try {
            if (!ssoQry.equalsIgnoreCase("")) {
                mStrOrderQty = OfflineManager.getTotalSumByCondition("" + Constants.SSSoItemDetails +
                        "?$select=" + Constants.Quantity + " &$filter= " + ssoQry + " ", Constants.Quantity);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        double mdouDevOrderQty = 0.0;
        try {
            mdouDevOrderQty = OfflineManager.getDeviceTotalOrderQtyByRetailer(Constants.SSSOs, context, mStrCurrentDate, mStrRetNo, Constants.Quantity);
        } catch (Exception e) {
            mdouDevOrderQty = 0.0;
        }

        orderQty = Double.parseDouble(mStrOrderQty) + mdouDevOrderQty;

        return orderQty + "";
    }

    public static String getInvQtyByInvQry(String invQry) {
        String mStInvQty = "0.0";
        try {
            if (!invQry.equalsIgnoreCase("")) {
                mStInvQty = OfflineManager.getTotalSumByCondition("" + Constants.SSInvoiceItemDetails +
                        "?$select=" + Constants.Quantity + " &$filter= " + invQry + " ", Constants.Quantity);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStInvQty + "";
    }

    public static String getNoOfDaysBefore(int days) {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        return simpleDateFormat.format(cal.getTime()) + "T00:00:00";
    }

    public static boolean isSchemeBasketOrNot(String mStrSchemeGuid) {
        boolean mBoolHeadWiseScheme = false;
        String mStrSchemeQry = Constants.Schemes + "?$filter= " + Constants.SchemeGUID +
                " eq guid'" + mStrSchemeGuid + "' and  " + Constants.SchemeTypeID + " eq '" + Constants.SchemeTypeIDBasketScheme + "' ";
        try {
            mBoolHeadWiseScheme = OfflineManager.getVisitStatusForCustomer(mStrSchemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mBoolHeadWiseScheme;
    }

    public static String trimQtyDecimalUpvalue(String qty) {
        String roundQry = "";
        try {
            if (qty.contains(".")) {
                String[] arrQty = qty.split("\\.");
                double roundValue = Double.parseDouble(arrQty[0]);
                double roundDecimal = Double.parseDouble(arrQty[1]);
                if (roundDecimal > 0) {
                    roundValue = roundValue + 1;
                }
                roundQry = roundValue + "";
                return roundQry.substring(0, roundQry.indexOf(".")) + "";
            } else
                return qty;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roundQry;
    }

    public static void showCustomKeyboard(View v, KeyboardView keyboardView, Context context) {
        if (v != null) {
            ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);

    }

    public static void hideCustomKeyboard(KeyboardView keyboardView) {
        try {
            keyboardView.setVisibility(View.GONE);
            keyboardView.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decrementEditTextVal(EditText editText, String mStrDotAval) {
        BigDecimal mDouAmountVal = new BigDecimal("0");
        String et_text = editText.getText().toString();
        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;

        }

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger subtactVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue() + decimal.doubleValue());
                } else {
                    mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
                }
            } else {
                mDouAmountVal = BigDecimal.valueOf(integer.subtract(subtactVal).doubleValue());
            }

        } else {
            mDouAmountVal = BigDecimal.valueOf(Double.parseDouble(total) - 1);
        }
        int res = mDouAmountVal.compareTo(new BigDecimal("0"));

        if (res <= 0) {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText("0.0");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal("0"));
                }
            } else {
                editText.setText("0");
            }
            setCursorPos(editText);
        } else {
            if (mStrDotAval.equalsIgnoreCase("Y")) {
                setCursorPos(editText);
                if (et_text.contains(".")) {
                    editText.setText(mDouAmountVal + "");
                } else {
                    editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
                }
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(mDouAmountVal + ""));
            }
            setCursorPos(editText);
        }

    }

    public static void incrementTextValues(EditText editText, String mStrDotAval) {
//        double sPrice = 0;
        BigDecimal sPrice = new BigDecimal("0");
        String et_text = editText.getText().toString();

        String total = "0.0";
        if (!et_text.isEmpty()) {
            total = et_text;
        }
//        sPrice = Double.parseDouble(total);
//        sPrice++;

        if (total.contains(".")) {
//            double number = Double.parseDouble(total);
            BigDecimal number = new BigDecimal(total);
//            int integer = (int)number;
            BigInteger integer = new BigDecimal(number.doubleValue()).toBigInteger();
            String[] splitNumber = total.split("\\.");
            BigDecimal decimal = new BigDecimal("0.0");
            BigInteger incrementVal = new BigInteger("1");
            if (splitNumber.length > 1) {
                if (!splitNumber[1].equalsIgnoreCase("")) {
//                    decimal = Double.parseDouble("."+splitNumber[1]);
                    decimal = BigDecimal.valueOf(Double.parseDouble("." + splitNumber[1]));
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue() + decimal.doubleValue());
                } else {
                    sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
                }
            } else {
                sPrice = BigDecimal.valueOf(integer.add(incrementVal).doubleValue());
            }

        } else {
            sPrice = BigDecimal.valueOf(Double.parseDouble(total) + 1);
        }
        if (mStrDotAval.equalsIgnoreCase("Y")) {
            setCursorPos(editText);
            if (et_text.contains(".")) {
                editText.setText(sPrice + "");
            } else {
                editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
            }
        } else {
            editText.setText(UtilConstants.removeLeadingZeroVal(sPrice + ""));
        }
        setCursorPos(editText);

    }

    private static void setCursorPos(EditText editText) {
        int position = 0;
        try {
            position = editText.getText().toString().length();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            editText.setSelection(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String calculatePrimaryDiscount(String mStrPriDis, String mStrNetAmt) {
        String mDouPriSchDis = "0.0";

        try {
            if (Double.parseDouble(mStrPriDis) > 0 && Double.parseDouble(mStrNetAmt) > 0) {
                mDouPriSchDis = Constants.formulaOneCalculation(mStrPriDis, mStrNetAmt);
            } else {
                mDouPriSchDis = "0.0";
            }
        } catch (NumberFormatException e) {
            mDouPriSchDis = "0.0";
        }
        return mDouPriSchDis;
    }

    public static String formulaOneCalculation(String taxPer, String applyColumn) {
        Double mCaluclateVal = 0.0;
        try {
            mCaluclateVal = (Double.parseDouble(applyColumn) * Double.parseDouble(taxPer)) / 100;
        } catch (NumberFormatException e) {
            mCaluclateVal = 0.0;
        }

        if (mCaluclateVal.isInfinite() || mCaluclateVal.isNaN()) {
            mCaluclateVal = 0.0;
        }
        return mCaluclateVal + "";

    }

    public static int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    public static String makeCPQry(ArrayList<String> alRetailers, String columnName) {
        String mCPQry = "";
        for (String cpNo : alRetailers) {
            if (mCPQry.length() == 0)
                mCPQry += " " + columnName + " eq '" + cpNo + "'";
            else
                mCPQry += " or " + columnName + " eq '" + cpNo + "'";

        }

        return mCPQry;
    }

    public static String getTaxAmount(String mStrAfterPriDisAmount, String mStrSecDisAmt, ODataEntity oDataEntity, String mStrOrderQty) {
        String mStrAfterSecAmt = (Double.parseDouble(mStrAfterPriDisAmount) - Double.parseDouble(mStrSecDisAmt)) + "";
        Double mStrNetAmtPerQty = Double.parseDouble(mStrAfterSecAmt) / Double.parseDouble(mStrOrderQty);
        String mStrTaxAmt = "0";
        try {
            mStrTaxAmt = OfflineManager.getPriceOnFieldByMatBatchAfterPrimarySecDiscount(oDataEntity, mStrNetAmtPerQty + "", mStrOrderQty);
        } catch (OfflineODataStoreException e) {
            mStrTaxAmt = "0";
        }

        return mStrTaxAmt;
    }

    public static String getCalculateColumn(String mStrApplicableOnID) {
        String mStrCalColumn = "";

        if (mStrApplicableOnID.equalsIgnoreCase("01")) {
//            mStrCalColumn = Constants.UnitPrice;
            mStrCalColumn = Constants.IntermUnitPrice;
        } else if (mStrApplicableOnID.equalsIgnoreCase("02")) {
            mStrCalColumn = Constants.LandingPrice;
        } else if (mStrApplicableOnID.equalsIgnoreCase("03")) {
            mStrCalColumn = Constants.MRP;
        } else if (mStrApplicableOnID.equalsIgnoreCase("04")) {
//            mStrCalColumn = Constants.UnitPrice;
            mStrCalColumn = Constants.IntermUnitPrice;
        }

        return mStrCalColumn;
    }

    public static ArrayList<SchemeBean> removeDuplicateScheme(ArrayList<SchemeBean> schPerCalBeanList) {
        ArrayList<SchemeBean> schPerCalBeanListFinal = new ArrayList<>();
        ArrayList<String> schemeIdList = new ArrayList<>();
        if (schPerCalBeanList != null) {
            for (SchemeBean schemeBean : schPerCalBeanList) {
                if (!schemeIdList.contains(schemeBean.getSchemeGuid())) {
                    schPerCalBeanListFinal.add(schemeBean);
                    schemeIdList.add(schemeBean.getSchemeGuid());
                }
            }
        }
        return schPerCalBeanListFinal;
    }

    public static double getSecDiscAmtPer(double calSecPer, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        double values = 0.0;
        for (SchemeCalcuBean schemeCalcuBean : schemeCalcuBeanArrayList) {
            values = values + schemeCalcuBean.getmDouSecDiscount();
        }
        values = values + calSecPer;
        return values;
    }

    public static double getSecSchemeAmt(double mDouSecAmt, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        double values = 0.0;
        for (SchemeCalcuBean schemeCalcuBean : schemeCalcuBeanArrayList) {
            values = values + schemeCalcuBean.getmDouSecAmt();
        }
        values = mDouSecAmt + values;
        return values;
    }

    public static SchemeBean getPrimaryTaxValByMaterial(String cPStockItemGUID, String mStrMatNo,
                                                        String mStrOrderQty, boolean ratioSchemeCal, SKUGroupBean skuGroupBean, SOCreateBean soCreateBean) {

        SchemeBean mStrNetAmount = null;
        try {

            String mStockSnoQry = "";
            if (soCreateBean.getPriceBatchCalType().equalsIgnoreCase(Constants.X)) {
                mStockSnoQry = Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                        + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' and "
                        + Constants.StockTypeID + " eq '" + Constants.str_1 +
                        "'  &$orderby=" + Constants.ManufacturingDate + "%20desc ";
            } else {
                mStockSnoQry = Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                        + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' and "
                        + Constants.StockTypeID + " eq '" + Constants.str_1 +
                        "'  &$orderby=" + Constants.ManufacturingDate + "%20asc ";
            }
            mStrNetAmount = OfflineManager.getNetAmount(mStockSnoQry, mStrOrderQty, mStrMatNo, ratioSchemeCal, skuGroupBean, soCreateBean.getPriceBatchCalType());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrNetAmount;
    }

    public static String trimQtyDecimalPlace(String qty) {
        if (qty.contains("."))
            return qty.substring(0, qty.indexOf("."));
        else
            return qty;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static String getTypesetValueForSkugrp(Context ctx) {
        String typeValues = "";
        try {
            typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.MSEC + "' and " + Constants.Types + " eq '" + Constants.SKUGRP + "'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (typeValues.equalsIgnoreCase("X")) {
            return ctx.getString(R.string.lbl_sku_group);
        } else {
            return ctx.getString(R.string.lbl_crs_sku_group);
        }
    }

    public static String getPRICALBATCHType() {
        String mStrPRICALBTC = "";
        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.PRICALBTC + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                mStrPRICALBTC = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.PRICALBTC);
            }
        } catch (OfflineODataStoreException e) {
            mStrPRICALBTC = "";
            e.printStackTrace();
        }
        return mStrPRICALBTC;
    }

    public static String[][] getDMSDivisionByCPGUID(String mStrCPGUID, Context mContext) {
        String qryStr = "";
        String[][] mArrayCPDMSDivisions = null;
        if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
            String spGuid = "";
            try {
                spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "' ";
        } else {
            qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "'";
        }

        try {
            mArrayCPDMSDivisions = OfflineManager.getDMSDivisionByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayCPDMSDivisions == null) {
            mArrayCPDMSDivisions = new String[2][1];
            mArrayCPDMSDivisions[0][0] = "";
            mArrayCPDMSDivisions[1][0] = "";
        }

        return mArrayCPDMSDivisions;
    }

    public static final String getSOSuccessMsg(String fipDocNo) {
        return "SO # " + fipDocNo + " created";
    }

    public static final String getCollectionSuccessMsg(String fipDocNo) {
        return "Collection # " + fipDocNo + " created";
    }

    public static final String getROSuccessMsg(String fipDocNo) {
        return "RO # " + fipDocNo + " created";
    }

    public static final String getExpenseSuccessMsg(String fipDocNo) {
        return "Expense # " + fipDocNo + " created";
    }

    public static String getDueDateStatus(Calendar todayCalenderDate, String strInvoiceDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date invoiceDate = null;
            invoiceDate = formatter.parse(strInvoiceDate);
            Calendar oldDay = Calendar.getInstance();
            oldDay.setTime(invoiceDate);
            long diff = todayCalenderDate.getTimeInMillis() - oldDay.getTimeInMillis(); //result in millis
            long days = diff / (24 * 60 * 60 * 1000);
            if (days > 0) {
                return "C";//over due
            } else if (days < -6) {
                return "B";//not due
            } else {
                return "A";//near due
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void updateBirthdayAlertsStatus(String keyVal, Context context) {
        String store = null;
        try {
            store = ConstantsUtils.getFromDataVault(keyVal, context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (store != null && !store.equalsIgnoreCase("")) {
            HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
            if (!hashMap.isEmpty()) {
                Iterator mapSelctedValues = hashMap.keySet()
                        .iterator();
                while (mapSelctedValues.hasNext()) {
                    String Key = (String) mapSelctedValues.next();
                    hashMap.put(Key, Constants.Y);
                }
            }
            setBirthdayToDataVault(hashMap, keyVal, context);

        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            setBirthdayToDataVault(hashMap, keyVal, context);
        }
    }

    public static HashMap<String, String> getBirthdayListFromDataValt(String mStrKeyVal) {
        HashMap hashMap = new HashMap();
        //Fetch object from data vault
        try {
            JSONObject fetchJsonHeaderObject = new JSONObject(mStrKeyVal);

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

            hashMap = convertStringToMap(itemsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static HashMap<String, String> convertStringToMap(String jsonString) {
        HashMap myHashMap = new HashMap();
        try {
            JSONArray e = new JSONArray("[" + jsonString + "]");
            JSONObject jObject = null;
            String keyString = null;

            for (int i = 0; i < e.length(); ++i) {
                jObject = e.getJSONObject(i);
                for (int k = 0; k < jObject.length(); ++k) {
                    keyString = (String) jObject.names().get(k);
                    myHashMap.put(keyString, jObject.getString(keyString));
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return myHashMap;
    }

    public static void setBirthdayToDataVault(HashMap<String, String> hashMap, String alertsKey, Context context) {

        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();
        try {
            String jsonFromMap = gson.toJson(hashMap);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(alertsKey, jsonHeaderObject.toString(), context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BirthdaysBean> getAlertsValuesFromDataVault(Context context) {
        ArrayList<BirthdaysBean> alDataValutAlertList = new ArrayList<>();
        ArrayList<BirthdaysBean> alDataValutAlertHistList = new ArrayList<>();
        JSONObject fetchJsonHeaderObject = null;
        String store = null;
        try {
            store = ConstantsUtils.getFromDataVault(AlertsDataKey, context);
        } catch (Throwable e) {
            store = "";
        }

        if (store != null && !store.equalsIgnoreCase("")) {
            try {
                fetchJsonHeaderObject = new JSONObject(store);
                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                alDataValutAlertList = Constants.convertToBirthDayArryList(itemsString);
                if (alDataValutAlertList != null && alDataValutAlertList.size() > 0) {
                    for (BirthdaysBean birthdaysBean : alDataValutAlertList) {
                        if (birthdaysBean.getStatus().equalsIgnoreCase(Constants.Y)) {
                            alDataValutAlertHistList.add(birthdaysBean);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return alDataValutAlertHistList;
    }

    public static ArrayList<BirthdaysBean> convertToBirthDayArryList(String jsonString) {
        ArrayList<BirthdaysBean> alBirthDayList = null;
        try {
            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<ArrayList<BirthdaysBean>>() {
            }.getType();
            alBirthDayList = gson.fromJson(jsonString, stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alBirthDayList;
    }

    public static void setAlertsValToDataVault(ArrayList<BirthdaysBean> alAlerts, String alertsKey, Context context) {

        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();
        try {
            String jsonFromMap = gson.toJson(alAlerts);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(alertsKey, jsonHeaderObject.toString(), context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setBirthdayListToDataValut(Context context) {
        String splitDayMonth[] = new String[0];
        try {
            String[][] oneWeekDay;
            oneWeekDay = UtilConstants.getOneweekValues(1);
            splitDayMonth = oneWeekDay[0][0].split("-");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        ArrayList<BirthdaysBean> alRetBirthDayTempList = new ArrayList<>();
        ArrayList<BirthdaysBean> alDataVaultList = new ArrayList<>();
        ArrayList<BirthdaysBean> alRetBirthDayList = getTodayBirthDayList();

        try {
            SharedPreferences settings = null;
            if (context != null) {
                settings = context.getSharedPreferences(Constants.PREFS_NAME,
                        0);
            }
            String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

            if (UtilConstants.getDate1() != null && mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {

                String store = null;
                try {
                    store = ConstantsUtils.getFromDataVault(Constants.BirthDayAlertsKey, context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (store != null && !store.equalsIgnoreCase("")) {
                    alDataVaultList = Constants.getBirthdayListFromDataVault(store);


                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            boolean mBoolIsRecordExists = false;

                            if (alDataVaultList != null && alDataVaultList.size() > 0) {

                                // Loop arrayList1 items
                                for (BirthdaysBean secondBeanAL : alDataVaultList) {
                                    if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID()) && (!firstBeanAL.getAppointmentAlert()
                                            && !secondBeanAL.getAppointmentAlert())) {

                                        if ((secondBeanAL.getDOB().equalsIgnoreCase(firstBeanAL.getDOB())
                                                || (secondBeanAL.getAnniversary().equalsIgnoreCase(firstBeanAL.getAnniversary())))) {

                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (firstBeanAL.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getDOBStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setDOBStatus("");
                                            else
                                                birthdaysBean.setDOBStatus(Constants.X);

                                            if (firstBeanAL.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]) && secondBeanAL.getAnniversaryStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAnniversaryStatus("");
                                            else
                                                birthdaysBean.setAnniversaryStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setDOB(firstBeanAL.getDOB());
                                            birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }

                                    } else {
                                        if (firstBeanAL.getCPUID().toUpperCase().equalsIgnoreCase(secondBeanAL.getCPUID())
                                                && (firstBeanAL.getAppointmentAlert()
                                                && secondBeanAL.getAppointmentAlert())) {
                                            BirthdaysBean birthdaysBean = new BirthdaysBean();
                                            birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                            if (secondBeanAL.getAppointmentStatus().equalsIgnoreCase(""))
                                                birthdaysBean.setAppointmentStatus("");
                                            else
                                                birthdaysBean.setAppointmentStatus(Constants.X);

                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                            birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                            birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                            birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                            birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                            birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                            birthdaysBean.setAppointmentAlert(true);
                                            alRetBirthDayTempList.add(birthdaysBean);
                                            mBoolIsRecordExists = true;
                                            break;
                                        }
                                    }
                                }

                                if (!mBoolIsRecordExists) {
                                    BirthdaysBean birthdaysBean = new BirthdaysBean();
                                    if (!firstBeanAL.getAppointmentAlert()) {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setDOBStatus(firstBeanAL.getDOBStatus());
                                        birthdaysBean.setAnniversaryStatus(firstBeanAL.getAnniversaryStatus());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setDOB(firstBeanAL.getDOB());
                                        birthdaysBean.setAnniversary(firstBeanAL.getAnniversary());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    } else {
                                        birthdaysBean.setCPUID(firstBeanAL.getCPUID());
                                        birthdaysBean.setAppointmentStatus("");
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setOwnerName(firstBeanAL.getOwnerName());
                                        birthdaysBean.setRetailerName(firstBeanAL.getRetailerName());
                                        birthdaysBean.setMobileNo(firstBeanAL.getMobileNo());
                                        birthdaysBean.setAppointmentTime(firstBeanAL.getAppointmentTime());
                                        birthdaysBean.setAppointmentEndTime(firstBeanAL.getAppointmentEndTime());
                                        birthdaysBean.setAppointmentType(firstBeanAL.getAppointmentType());
                                        birthdaysBean.setAppointmentAlert(true);
                                        alRetBirthDayTempList.add(birthdaysBean);
                                    }
                                }

                            }


                        }

                    }


                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayTempList != null && alRetBirthDayTempList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayTempList, context);
                    } else {
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            assignValuesIntoDataVault(alRetBirthDayList, context);
                        } else {
                            assignEmptyValuesIntoDataVault(context);
                        }
                    }

                } else {
                    setCurrentDateTOSharedPerf(context);
                    // TODO add values into data vault
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        assignValuesIntoDataVault(alRetBirthDayList, context);
                    } else {
                        assignEmptyValuesIntoDataVault(context);
                    }
                }


            } else {
                assignEmptyValuesIntoDataVault(context);
                setCurrentDateTOSharedPerf(context);
                // TODO add values into data vault
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    assignValuesIntoDataVault(alRetBirthDayList, context);
                } else {
                    assignEmptyValuesIntoDataVault(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static int setBirthDayRecordsToDataValut(Context context) {
        int mIntBirthdaycount = 0;
        try {
            mIntBirthdaycount = 0;
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                    0);
            ArrayList<BirthdaysBean> alRetBirthDayList = getTodayBirthDayListOnly();
            try {

                String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

                if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {
                    String store = null;
                    try {
                        store = ConstantsUtils.getFromDataVault(Constants.BirthDayAlertsTempKey, context);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (store != null && !store.equalsIgnoreCase("")) {
                        HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
                        if (!hashMap.isEmpty()) {
                            if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                                for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                                    if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                        String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                        if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                            hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                        } else {
                                            hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                            mIntBirthdaycount++;
                                        }
                                    } else {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                        mIntBirthdaycount++;
                                    }
                                }
                            } else {
                                mIntBirthdaycount = 0;
                            }
                        } else {
                            hashMap = new HashMap<>();
                            if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                                for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                                    if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                        String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                        if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                            hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                        } else {
                                            hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                            mIntBirthdaycount++;
                                        }
                                    } else {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                        mIntBirthdaycount++;
                                    }
                                }
                            } else {
                                mIntBirthdaycount = 0;
                            }
                        }
                        setCurrentDateTOSharedPerf(context);
                        if (hashMap == null)
                            hashMap = new HashMap<>();
                        setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey, context);

                    } else {
                        HashMap<String, String> hashMap = new HashMap<>();
                        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                            for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                                if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                    String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                    if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                    } else {
                                        hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                        mIntBirthdaycount++;
                                    }
                                } else {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                    mIntBirthdaycount++;
                                }
                            }
                        } else {
                            mIntBirthdaycount = 0;
                        }

                        setCurrentDateTOSharedPerf(context);
                        setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey, context);
                    }
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getCPUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getCPUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                    mIntBirthdaycount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getCPUID(), Constants.N);
                                mIntBirthdaycount++;
                            }
                        }
                    } else {
                        mIntBirthdaycount = 0;
                    }

                    setCurrentDateTOSharedPerf(context);
                    setBirthdayToDataVault(hashMap, Constants.BirthDayAlertsTempKey, context);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Constants.BirthdayAlertsCount, mIntBirthdaycount);
            editor.apply();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return mIntBirthdaycount;

    }

    public static int setAlertsRecordsToDataValut(Context context) {
        int mIntTextAlertcount = 0;
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME,
                0);

        ArrayList<BirthdaysBean> arrayList = new ArrayList<>();
        ArrayList<BirthdaysBean> alRetBirthDayList = OfflineManager.getAlertsFromLocalDB(arrayList);
        try {

            String store = null;
            try {
                store = ConstantsUtils.getFromDataVault(Constants.AlertsTempKey, context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (store != null && !store.equalsIgnoreCase("")) {
                HashMap<String, String> hashMap = Constants.getBirthdayListFromDataValt(store);
                if (!hashMap.isEmpty()) {
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                    mIntTextAlertcount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        }
                    } else {
                        mIntTextAlertcount = 0;
                    }
                } else {
                    hashMap = new HashMap<>();
                    if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                        for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                            if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                                String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                                if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                                } else {
                                    hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                    mIntTextAlertcount++;
                                }
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        }
                    } else {
                        mIntTextAlertcount = 0;
                    }
                }

                setCurrentDateTOSharedPerf(context);
                setBirthdayToDataVault(hashMap, Constants.AlertsTempKey, context);

            } else {

                HashMap<String, String> hashMap = new HashMap<>();
                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    for (BirthdaysBean firstBeanAL : alRetBirthDayList) {
                        if (hashMap.containsKey(firstBeanAL.getAlertGUID())) {
                            String mStrVal = hashMap.get(firstBeanAL.getAlertGUID());
                            if (mStrVal.equalsIgnoreCase(Constants.Y)) {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.Y);
                            } else {
                                hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                                mIntTextAlertcount++;
                            }
                        } else {
                            hashMap.put(firstBeanAL.getAlertGUID(), Constants.N);
                            mIntTextAlertcount++;
                        }
                    }
                } else {
                    mIntTextAlertcount = 0;
                }

                setCurrentDateTOSharedPerf(context);
                setBirthdayToDataVault(hashMap, Constants.AlertsTempKey, context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.TextAlertsCount, mIntTextAlertcount);
        editor.commit();

        return mIntTextAlertcount;

    }

    public static ArrayList<BirthdaysBean> getTodayBirthDayListOnly() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        String[][] oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {
                String[] splitDayMonth = oneWeekDay[0][i].split("-");
                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        }

        return alRetBirthDayList;
    }

    public static ArrayList<BirthdaysBean> getTodayBirthDayList() {
        ArrayList<BirthdaysBean> alRetBirthDayList = null;
        ArrayList<BirthdaysBean> alAppointmentList = null;
        String[][] oneWeekDay = UtilConstants.getOneweekValues(1);
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                String[] splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00' and " + Constants.CPTypeID + " eq '" + Constants.str_02 + "' and (month%28" + Constants.PlannedDate + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.PlannedDate + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ")";
                try {
                    alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alRetBirthDayList.size(), alAppointmentList);
                    }
                } else {
                    alRetBirthDayList = new ArrayList<>();
                    if (alAppointmentList != null && alAppointmentList.size() > 0) {
                        alRetBirthDayList.addAll(alAppointmentList);
                    }
                }

            }
        }

        return alRetBirthDayList;
    }

    public static ArrayList<BirthdaysBean> getBirthdayListFromDataVault(String mStrKeyVal) {

        ArrayList<BirthdaysBean> beanArrayList = null;
        //Fetch object from data vault
        try {

            JSONObject fetchJsonHeaderObject = new JSONObject(mStrKeyVal);

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

            beanArrayList = convertToBirthDayArryList(itemsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beanArrayList;
    }

    public static void setCurrentDateTOSharedPerf(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
        editor.commit();

    }

    public static void assignValuesIntoDataVault(ArrayList<BirthdaysBean> alRetBirthDayList, Context context) {

        Gson gson = new Gson();
        Hashtable dbHeaderTable = new Hashtable();
        try {
            String jsonFromMap = gson.toJson(alRetBirthDayList);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey, jsonHeaderObject.toString(), context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void assignEmptyValuesIntoDataVault(Context context) {
        try {
            //noinspection deprecation
            ConstantsUtils.storeInDataVault(Constants.BirthDayAlertsKey, "", context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setAppointmentNotification(Context context) {
        new NotificationSetClass(context);

    }

    private static SharedPreferences getAppointmentSharedPrefer(Context context) {
        return context.getSharedPreferences("appointmentPref", Context.MODE_PRIVATE);
    }

    public static void saveSharedPref(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void removeAlarmIdFromSharedPref(Context context, String key) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        if (sharedPreferences.contains(key)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public static int getSharedPref(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = getAppointmentSharedPrefer(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static String getNameByCPGUID(String collName, String columnName, String whereColumnn, String whereColval) {
        String colmnVal = "";
        try {
            colmnVal = OfflineManager.getValueByColumnName(collName + "?$select=" + columnName + " &$filter = " + whereColumnn + " eq guid'" + whereColval + "'", columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colmnVal;
    }

    public static Set<String> getOrderMatGrpByBrandAndCategory(String mStrCatID, String mStrBrandID, String mStrDMSDivisionQry) {
        Set<String> mSetOrderMatGrp = new HashSet<>();
        try {

            if (!mStrCatID.equalsIgnoreCase("") && mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and " + mStrDMSDivisionQry + "  ", Constants.OrderMaterialGroupID);
            } else if (mStrCatID.equalsIgnoreCase("") && !mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and " + mStrDMSDivisionQry + " ", Constants.OrderMaterialGroupID);
            } else {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and " + mStrDMSDivisionQry + " ", Constants.OrderMaterialGroupID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSetOrderMatGrp;
    }

    public static boolean isDBStockCategoryDisplay(Context mContext) {
        return false;
    }

    public static Locale getLocalFromISO(String iso4217code) {
        Locale toReturn = null;
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).
                    getCurrency().getCurrencyCode();
            if (iso4217code.equals(code)) {
                toReturn = locale;
                break;
            }
        }
        return toReturn;
    }

    public static String getCurrencyPattren(String currencyCode, String mStrAmount) {

        String mStrConAmount = "";

        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            if (!TextUtils.isEmpty(currencyCode))
                format.setCurrency(java.util.Currency.getInstance(currencyCode));
            if (!TextUtils.isEmpty(mStrAmount))
                mStrConAmount = format.format(new BigDecimal(mStrAmount));
            else
                mStrConAmount = format.format(new BigDecimal(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStrConAmount;
    }

    public static String appendPrecedingZeros(String mStrInputNo, int stringLength) {


        String mfinalString = Constants.trimLeadingZeros(mStrInputNo);
       /* try {
            if(mStrInputNo!=null && !mStrInputNo.equalsIgnoreCase("")) {
                try {
                    int numberOfDigits = mStrInputNo.length();
                    int numberOfLeadingZeroes = stringLength - numberOfDigits;
                    StringBuilder sb = new StringBuilder();
                    if (numberOfLeadingZeroes > 0) {
                        for (int i = 0; i < numberOfLeadingZeroes; i++) {
                            sb.append("0");
                        }
                    }
                    sb.append(mStrInputNo);
                    mfinalString = sb.toString();
                } catch (Exception e) {
                    mfinalString = mStrInputNo;
                    e.printStackTrace();
                }
            }else{
                mfinalString = "";
            }
        } catch (Exception e) {
            mfinalString = "";
            e.printStackTrace();
        }*/

        return mfinalString;
    }

    public static String trimLeadingZeros(String source) {
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c != '0') {
                return source.substring(i);
            }
        }
        return ""; // or return "0";
    }

    public static File SaveImageInDevice(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, filename + ".jpg");
        if (filename.contains(".")) {
            file = new File(extStorageDirectory, filename);
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    public static boolean checkPermission(Context context) {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int resultCore = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED && resultCore == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        UtilConstants.dialogBoxWithCallBack(context, title, message, positiveButton, negativeButton, false, dialogCallBack);
    }

    public static String getRouteSchGUID(String collName, String columnName, String whereColumnn, String whereColval, String cpTypeID) {
        String mStrRouteSchGUID = "";
        if (cpTypeID.equalsIgnoreCase(Constants.str_01)) {
            try {
                mStrRouteSchGUID = OfflineManager.getGuidValueByColumnName(collName + "?$top=1 &$select=" + columnName + " &$filter = " + whereColumnn + " eq '" + whereColval + "'", columnName);
            } catch (Exception e) {
                mStrRouteSchGUID = "";
            }
        } else {
            // future will use ful
        }
        return mStrRouteSchGUID;
    }

    public static String getValueFromDataVault(String key, Context context) {
        String store = null;
        try {
            store = ConstantsUtils.getFromDataVault(key, context);
        } catch (Throwable e) {
            store = "";
        }

        return store;
    }

    public static void setCursorPostion(EditText editText, View view, MotionEvent motionEvent) {
        EditText edText = (EditText) view;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int touchPosition = editText.getOffsetForPosition(x, y);
        if (touchPosition >= 0) {
            editText.setSelection(touchPosition);
        }
    }

    public static String getCurrencySymbol(String currency, String amount) {
        return UtilConstants.getCurrencyFormat(currency, amount);
    }

    public static ArrayList<String> getDefinigReqList(Context mContext) {
        ArrayList<String> alAssignColl = new ArrayList<>();
        String[] DEFINGREQARRAY = getDefinigReq(mContext);
        for (String collectionName : DEFINGREQARRAY) {
            if (collectionName.contains("?")) {
                String splitCollName[] = collectionName.split("\\?");
                collectionName = splitCollName[0];
            }
            alAssignColl.add(collectionName);
        }
        return alAssignColl;
    }

    /**
     * Delete visual vid product
     */
    public static void deleteFolder(Activity activity) {
        if (Build.VERSION_CODES.M <= android.os.Build.VERSION.SDK_INT) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                if (myDirectory.exists()) {
                    myDirectory.delete();
                }
            }
        } else {
            File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
            if (myDirectory.exists()) {
                myDirectory.delete();
            }
        }
    }

    public static void openImageInGallery(Context mContext, String file) {
//        String videoResource = file.getPath();
        Uri intentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(file));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(intentUri, "image/jpeg");
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Instruct the user to install a PDF reader here, or something
            Toast.makeText(mContext, mContext.getString(R.string.visual_aidl_app_not_found), Toast.LENGTH_LONG).show();
        }
    }

    public static String makePendingDataToJsonString(Context context) {
        String mStrJson = "";
        ArrayList<Object> objectArrayList = SyncSelectionActivity.getPendingCollList(context, false);
        if (!objectArrayList.isEmpty()) {
            String[][] invKeyValues = (String[][]) objectArrayList.get(1);
            JSONArray jsonArray = new JSONArray();
            for (int k = 0; k < invKeyValues.length; k++) {
                JSONObject jsonObject = new JSONObject();
                String store = "";
                try {
                    store = ConstantsUtils.getFromDataVault(invKeyValues[k][0].toString(), context);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                try {
                    // Add the values to the jsonObject
                    jsonObject.put(Constants.KeyNo, invKeyValues[k][0]);
                    jsonObject.put(Constants.KeyType, invKeyValues[k][1]);
                    jsonObject.put(Constants.KeyValue, store);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(DataVaultData, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mStrJson = jsonObj.toString();
        }
        return mStrJson;
    }

    public static boolean isFileExits(String fileName) {
        boolean isFileExits = false;
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            // Get The Text file
            File txtFile = new File(sdCardDir, fileName);
            // Read the file Contents in a StringBuilder Object
            if (txtFile.exists()) {
                isFileExits = true;
            } else {
                isFileExits = false;
            }
        } catch (Exception e) {
            isFileExits = false;
            e.printStackTrace();
            LogManager.writeLogError("isFileExits() : " + e.getMessage());
        }
        return isFileExits;
    }

    public static String getTextFileData(String fileName) {
        // Get the dir of SD Card
        File sdCardDir = Environment.getExternalStorageDirectory();
        // Get The Text file
        File txtFile = new File(sdCardDir, fileName);
        // Read the file Contents in a StringBuilder Object
        StringBuilder text = new StringBuilder();
        if (txtFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(txtFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("C2c", "Error occured while reading text file!!");
                LogManager.writeLogError("getTextFileData() : (IOException)" + e.getMessage());
            }
        } else {
            text.append("");
        }
        return text.toString();
    }

    public static void setJsonStringDataToDataVault(String mJsonString, Context context) {
        try {
            JSONObject jsonObj = new JSONObject(mJsonString);
            // Getting data JSON Array nodes
            JSONArray jsonArray = jsonObj.getJSONArray(DataVaultData);
            for (int incVal = 0; incVal < jsonArray.length(); incVal++) {
                JSONObject jsonObject = jsonArray.getJSONObject(incVal);
                String mStrKeyNo = jsonObject.getString(KeyNo);
                String mStrKeyKeyType = jsonObject.getString(KeyType);
                String mStrKeyValue = jsonObject.getString(KeyValue);
                Constants.saveDeviceDocNoToSharedPref(context, mStrKeyKeyType, mStrKeyNo);
                ConstantsUtils.storeInDataVault(mStrKeyNo, mStrKeyValue, context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String[][] getDealer(String closingDate) {
        String retList[][] = null;

        try {
            String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + closingDate + "'";

            ArrayList<RetailerBean> alRSCHList = null;
            try {
                alRSCHList = OfflineManager.getTodayRoutes1(routeQry, "");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if (alRSCHList != null && alRSCHList.size() > 0) {
                String routeSchopeVal = alRSCHList.get(0).getRoutSchScope();
                if (alRSCHList.size() > 1) {
                    String mRSCHQry = "";
                    if (routeSchopeVal.equalsIgnoreCase("000001")) {
                        for (RetailerBean routeList : alRSCHList) {
                            if (mRSCHQry.length() == 0)
                                mRSCHQry += " guid'" + routeList.getRschGuid().toUpperCase() + "'";
                            else
                                mRSCHQry += " or " + Constants.RouteSchGUID + " eq guid'" + routeList.getRschGuid().toUpperCase() + "'";

                        }

                        String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=(" +
                                Constants.RouteSchGUID + " eq " + mRSCHQry + ") &$orderby=" + Constants.SequenceNo + "";

                        retList = OfflineManager.getNotVisitedRetailerList(qryForTodaysBeat, closingDate);


                    } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                        // Get the list of retailers from RoutePlans
                    }


                } else {


                    if (routeSchopeVal.equalsIgnoreCase("000001")) {
                        String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'"
                                + alRSCHList.get(0).getRschGuid().toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";

                        retList = OfflineManager.getNotVisitedRetailerList(qryForTodaysBeat, closingDate);

                    } else if (routeSchopeVal.equalsIgnoreCase("000002")) {
                        // Get the list of retailers from RoutePlans
                    }

                }

            } else {
                retList = null;
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        return retList;

    }

    public static boolean isEndateAndEndTimeValid(String mStrStartDate, String mStrStartTime) {

        boolean isValidEndDateAndTime = false;
        try {
            String mStrTime = UtilConstants.convertTimeOnly(mStrStartTime);
            String mStrCurrentTime = UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date startDate = null;
            Date endDate = null;

            try {
                startDate = format.parse(mStrStartDate);
                endDate = format.parse(UtilConstants.getNewDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date startTimeDate = null;
            Date endTimeDate = null;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                startTimeDate = simpleDateFormat.parse(mStrTime);
                endTimeDate = simpleDateFormat.parse(mStrCurrentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            isValidEndDateAndTime = false;

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            int mDay = cal.get(Calendar.DAY_OF_MONTH);

            Calendar calCurrent = Calendar.getInstance();
            calCurrent.setTime(endDate);
            int mYearCurrent = calCurrent.get(Calendar.YEAR);
            int mMonthCurrent = calCurrent.get(Calendar.MONTH);
            int mDayCurrent = calCurrent.get(Calendar.DAY_OF_MONTH);

            if (mYear == mYearCurrent && mMonth == mMonthCurrent && mDay == mDayCurrent) {
                if (endTimeDate.before(startTimeDate)) {
                    isValidEndDateAndTime = false;
                } else {
                    isValidEndDateAndTime = true;
                }
            } else {
                if (startDate.compareTo(endDate) <= 0) {
                    isValidEndDateAndTime = true;
                } else {
                    isValidEndDateAndTime = false;
                }

            }

        } catch (Exception e) {
            isValidEndDateAndTime = false;
        }
        return isValidEndDateAndTime;
    }

    public static ArrayList<RemarkReasonBean> getReasonValues(ArrayList<RemarkReasonBean> alReason) {
        alReason.clear();
        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "Reason" + "' and EntityType eq 'Visit' &$orderby=" + Constants.ID + "";
        try {
            alReason.addAll(OfflineManager.getRemarksReason(query));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return alReason;
    }

    public static ArrayList<RetailerBean> getVisitedRetFromVisit() {
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' " + "and StatusID ne '02' and " + Constants.CPTypeID + " eq '" + Constants.str_02 + "' and (" + Constants.VisitCatID + " eq '" + Constants.str_01 + "' or " + Constants.VisitCatID + " eq '" + Constants.str_02 + "') &$orderby=" + Constants.EndTime + "%20desc ";

        ArrayList<RetailerBean> retList = OfflineManager.getCPListFromVisit(mVisitQry);
        return retList;
    }

    public static String getOrderValByRetiler(String mStrRetNo, String mStrCurrentDate, String mSOOrderType, Context context, String mStrBeatGuid) {
        String mStrOrderVal = "0.0";
        double orderVal = 0.0;
        try {
            mStrOrderVal = OfflineManager.getTotalSumByCondition("" + Constants.SSSOs +
                    "?$select=" + Constants.NetPrice + " &$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate +
                    "' and " + Constants.SoldToId + " eq '" + mStrRetNo + "' and " + Constants.OrderType + " eq '" + mSOOrderType + "' and "
                    + Constants.SPGUID + " eq guid'" + Constants.getSPGUID() + "' and "
                    + Constants.BeatGuid + " eq guid'" + mStrBeatGuid + "'", Constants.NetPrice);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        double mdouDevOrderVal = 0.0;
        try {
            mdouDevOrderVal = OfflineManager.getDeviceTotalOrderAmtByRetailer(Constants.SecondarySOCreate, context, mStrCurrentDate, mStrRetNo, mStrBeatGuid);
        } catch (Exception e) {
            mdouDevOrderVal = 0.0;
        }

        orderVal = Double.parseDouble(mStrOrderVal) + mdouDevOrderVal;

        return orderVal + "";
    }

    public static String getDeviceAndDataVaultTLSD(String mStrSoldToID, String mStrCPDMSDIVQry, String beatGuid) {
        String parentID = OfflineManager.getParentID(Constants.CPDMSDivisions + "?$select=" + Constants.ParentID + " &$filter="
                + Constants.CPNo + " eq '" + mStrSoldToID + "' and " + Constants.RouteGUID + " eq guid'" + beatGuid + "'");
        String tempParentID = "";
        if (!TextUtils.isEmpty(parentID)) {
            tempParentID = String.valueOf(Integer.parseInt(parentID));
        }
        String mStrQry = "", mStrOfflineTLSD = "0";
        try {
            if (mStrSoldToID.equalsIgnoreCase("")) {
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSINVOICES + "?$filter= " + Constants.InvoiceDate +
                        " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and " + Constants.InvoiceDate + " lt datetime'" + UtilConstants.getNewDate() + "' and " + mStrCPDMSDIVQry + " and " + Constants.InvoiceTypeID + " ne '" + Constants.getSampleInvoiceTypeID() + "' and " + Constants.CPGUID + " eq '" + tempParentID + "' ", Constants.InvoiceGUID);
            } else {
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSINVOICES + "?$filter= " + Constants.InvoiceDate +
                        " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and " + Constants.InvoiceDate + " lt datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SoldToID + " eq '" + mStrSoldToID + "' and " + mStrCPDMSDIVQry + " and " + Constants.InvoiceTypeID + " ne '" + Constants.getSampleInvoiceTypeID() + "' and " + Constants.CPGUID + " eq '" + tempParentID + "' ", Constants.InvoiceGUID);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (!mStrQry.equalsIgnoreCase("")) {
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SSInvoiceItemDetails + "?$filter= " + mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        Double mDouDeviceTLSD = 0.0;

        Double mDouTotalTLSD = mDouDeviceTLSD + Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD + "";
    }

    public static String getSampleInvoiceTypeID() {
        String InvoiceTypeID = "";
        try {
            InvoiceTypeID = OfflineManager.getValueByColumnName(Constants.SSInvoiceTypes + "?$top=1 &$select=" + Constants.InvoiceTypeID + " " +
                    "&$filter=" + Constants.GoodsIssueFromID + " eq '000002' and GoodsIssueCatID eq '000002'", Constants.InvoiceTypeID);
        } catch (OfflineODataStoreException e) {
            InvoiceTypeID = "";
        }
        return InvoiceTypeID;
    }

    public static String getGUIDEditResourcePath(String collection, String key) {
        return new String(collection + "(guid'" + key + "')");
    }

    public static String[][] CheckForOtherInConfigValue(String[][] configValues) {
        for (int i = 0; i < configValues[0].length; i++) {
            if (configValues[1][i].equalsIgnoreCase("Others")) {
                String[] temp = new String[configValues.length];
                for (int k = 0; k < configValues.length; k++) {
                    temp[k] = configValues[k][i];
                }
                for (int j = i; j < configValues[0].length - 1; j++) {
                    for (int k = 0; k < configValues.length; k++) {
                        configValues[k][j] = configValues[k][j + 1];
                    }
                }
                for (int k = 0; k < configValues.length; k++) {
                    configValues[k][configValues[0].length - 1] = temp[k];
                }
                break;
            }
        }
        return configValues;
    }

    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());

        java.util.Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K, V> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static String[][] getDistributorsByCPGUID(String mStrCPGUID, Context mContext) {
        String mDBStkType = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.DSTSTKVIEW);
        String spGuid = "";
        String qryStr = "";
        String[][] mArrayDistributors = null;

        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Types + " eq '" + Constants.DSTSTKVIEW + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                if (mDBStkType.equalsIgnoreCase(Constants.str_01)) {
                    if (ConstantsUtils.getRollInformation(mContext).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                        try {
                            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID + " ", Constants.SPGUID);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' and " + Constants.PartnerMgrGUID + " eq guid'" + spGuid.toUpperCase() + "'";
                    } else {
                        qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "'";
                    }

                } else {
                    qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
                }
            } else {
                mDBStkType = "";
                qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrCPGUID.toUpperCase() + "' ";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mArrayDistributors = OfflineManager.getDistributorListByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[11][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
            mArrayDistributors[10][0] = "";
        } else {
            try {
                if (mArrayDistributors[4][0] != null) {

                }
            } catch (Exception e) {
                mArrayDistributors = new String[11][1];
                mArrayDistributors[0][0] = "";
                mArrayDistributors[1][0] = "";
                mArrayDistributors[2][0] = "";
                mArrayDistributors[3][0] = "";
                mArrayDistributors[4][0] = "";
                mArrayDistributors[5][0] = "";
                mArrayDistributors[6][0] = "";
                mArrayDistributors[7][0] = "";
                mArrayDistributors[8][0] = "";
                mArrayDistributors[9][0] = "";
                mArrayDistributors[10][0] = "";
            }
        }

        return mArrayDistributors;
    }

    public static void onVisitActivityUpdate(String mStrBundleCPGUID32, String loginIdVal,
                                             String visitActRefID, String vistActType, String visitActTypeDesc) {
        //========>Start VisitActivity
        try {
            Hashtable visitActivityTable = new Hashtable();
            String getVisitGuidQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
            ODataGuid mGuidVisitId = null;
            try {
                mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            GUID mStrGuide = GUID.newRandom();
            visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
            visitActivityTable.put(Constants.LOGINID, loginIdVal);
            visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
            visitActivityTable.put(Constants.ActivityType, vistActType);
            visitActivityTable.put(Constants.ActivityTypeDesc, visitActTypeDesc);
            visitActivityTable.put(Constants.ActivityRefID, visitActRefID);
//            visitActivityTable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.latitude));
//            visitActivityTable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.longitude));

            try {
                visitActivityTable.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.latitude, 12)));
                visitActivityTable.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.round(UtilConstants.longitude, 12)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            visitActivityTable.put(Constants.StartTime, mStartTimeDuration);
            visitActivityTable.put(Constants.EndTime, UtilConstants.getOdataDuration());


            try {
                OfflineManager.createVisitActivity(visitActivityTable);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //========>End VisitActivity
    }

    public static Hashtable getSSInvoiceHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {
            try {
                dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            } catch (JSONException e) {
                dbHeadTable.put(Constants.BeatGUID, "");
            }
            dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.InvoiceNo, fetchJsonHeaderObject.getString(Constants.InvoiceNo));
            dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
            dbHeadTable.put(Constants.InvoiceTypeDesc, fetchJsonHeaderObject.getString(Constants.InvoiceTypeDesc));
            dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.BillToGuid, fetchJsonHeaderObject.getString(Constants.BillToGuid));
            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
            dbHeadTable.put(Constants.SoldToName, fetchJsonHeaderObject.getString(Constants.SoldToName));
            dbHeadTable.put(Constants.ShipToCPGUID, fetchJsonHeaderObject.getString(Constants.ShipToCPGUID));
            dbHeadTable.put(Constants.Source, fetchJsonHeaderObject.getString(Constants.Source));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypeDesc, fetchJsonHeaderObject.getString(Constants.SoldToTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
//            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    /*public static String getNameSpaceOnline(OnlineODataStore oDataOfflineStore) {
        String mStrNameSpace = "";
        ODataMetadata oDataMetadata = null;

        try {
            oDataMetadata = oDataOfflineStore.getMetadata();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        Set set = oDataMetadata.getMetaNamespaces();
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();

            while (itr.hasNext()) {
                String tempNameSpace = itr.next().toString();
                if (!tempNameSpace.equalsIgnoreCase("OfflineOData")) {
                    mStrNameSpace = tempNameSpace;
                }
            }
        }

        return mStrNameSpace;
    }*/

    public static final String getLastSyncTimeStamp(String tableName, String columnName, String columnValue) {
        return "select *  from  " + tableName + " Where " + columnName + "='" + columnValue + "'  ;";

    }

    public static String getOnSaleOfDescConcatString(ArrayList<SchemeItemListBean> alSchemeItemList) {
        String mStrOnSaleDesc = "";
        if (alSchemeItemList != null && alSchemeItemList.size() > 0) {
            if (alSchemeItemList.size() > 2) {
                mStrOnSaleDesc = alSchemeItemList.get(0).getOnSalesDesc() + "\n" + alSchemeItemList.get(1).getOnSalesDesc() + "\n ...";
            } else if (alSchemeItemList.size() == 2) {
                mStrOnSaleDesc = alSchemeItemList.get(0).getOnSalesDesc() + "\n" + alSchemeItemList.get(1).getOnSalesDesc();
            } else {
                mStrOnSaleDesc = alSchemeItemList.get(0).getOnSalesDesc();
            }
        }
        return mStrOnSaleDesc;
    }

    public static ArrayList<String> getPendingMerchList(Context context, String createType) {
        ArrayList<String> devMerList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(createType, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                devMerList.add(itr.next().toString());
            }
        }

        return devMerList;
    }

    public static void deleteDeviceMerchansisingFromDataVault(Context context) {
        ArrayList<String> alDeviceMerList = Constants.getPendingMerchList(context, Constants.MerchList);
        if (alDeviceMerList != null && alDeviceMerList.size() > 0) {
            for (int incVal = 0; incVal < alDeviceMerList.size(); incVal++) {
                try {
                    if (!OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews +
                            "?$filter=sap.islocal() and " + Constants.MerchReviewGUID + " eq guid'" + alDeviceMerList.get(incVal) + "'")) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                        Constants.removeDeviceDocNoFromSharedPref(context, Constants.MerchList, alDeviceMerList.get(incVal), sharedPreferences, false);
                        storeInDataVault(alDeviceMerList.get(incVal), "", context);
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static Hashtable getCPHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            //noinspection unchecked
            dbHeadTable.put(Constants.Address1, fetchJsonHeaderObject.getString(Constants.Address1));
            dbHeadTable.put(Constants.Address2, fetchJsonHeaderObject.getString(Constants.Address2));
            dbHeadTable.put(Constants.Address3, fetchJsonHeaderObject.getString(Constants.Address3));
            dbHeadTable.put(Constants.Address4, fetchJsonHeaderObject.getString(Constants.Address4));
            //noinspection unchecked
            dbHeadTable.put(Constants.Country, fetchJsonHeaderObject.getString(Constants.Country));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictDesc, fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictID, fetchJsonHeaderObject.getString(Constants.DistrictID));
            dbHeadTable.put(Constants.StateID, fetchJsonHeaderObject.getString(Constants.StateID));
            dbHeadTable.put(Constants.StateDesc, fetchJsonHeaderObject.getString(Constants.StateDesc));
            dbHeadTable.put(Constants.CityID, fetchJsonHeaderObject.getString(Constants.CityID));
            dbHeadTable.put(Constants.CityDesc, fetchJsonHeaderObject.getString(Constants.CityDesc));
            dbHeadTable.put(Constants.Landmark, fetchJsonHeaderObject.getString(Constants.Landmark));
            dbHeadTable.put(Constants.PostalCode, fetchJsonHeaderObject.getString(Constants.PostalCode));
            dbHeadTable.put(Constants.MobileNo, fetchJsonHeaderObject.getString(Constants.MobileNo));
            dbHeadTable.put(Constants.Mobile2, fetchJsonHeaderObject.getString(Constants.Mobile2));
            dbHeadTable.put(Constants.Landline, fetchJsonHeaderObject.getString(Constants.Landline));
            dbHeadTable.put(Constants.EmailID, fetchJsonHeaderObject.getString(Constants.EmailID));
            dbHeadTable.put(Constants.PAN, fetchJsonHeaderObject.getString(Constants.PAN));
            dbHeadTable.put(Constants.VATNo, fetchJsonHeaderObject.getString(Constants.VATNo));
            dbHeadTable.put(Constants.OutletName, fetchJsonHeaderObject.getString(Constants.OutletName));
            dbHeadTable.put(Constants.OwnerName, fetchJsonHeaderObject.getString(Constants.OwnerName));

            dbHeadTable.put(Constants.DOB, fetchJsonHeaderObject.getString(Constants.DOB));
            dbHeadTable.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            dbHeadTable.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));
            dbHeadTable.put(Constants.PartnerMgrGUID, fetchJsonHeaderObject.getString(Constants.PartnerMgrGUID));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Anniversary, fetchJsonHeaderObject.getString(Constants.Anniversary));
            dbHeadTable.put(Constants.WeeklyOff, fetchJsonHeaderObject.getString(Constants.WeeklyOff));
            dbHeadTable.put(Constants.CPUID, fetchJsonHeaderObject.getString(Constants.CPUID));
            dbHeadTable.put(Constants.TaxRegStatus, fetchJsonHeaderObject.getString(Constants.TaxRegStatus));
            dbHeadTable.put(Constants.Group4, fetchJsonHeaderObject.getString(Constants.Group4));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));

            dbHeadTable.put(Constants.RouteID, fetchJsonHeaderObject.getString(Constants.RouteID));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));

            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static String getTypesetValueForRetUOM(Context ctx) {
        String typeValues = "";
        try {
            typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SS + "' and " + Constants.Types + " eq '" + Constants.CPSTKUOM + "'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return typeValues;
    }

    public static ODataDuration getOdataDuration() {
        final Calendar calCurrentTime = Calendar.getInstance();
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
        }

        return oDataDuration;
    }

    public static String convertArrListToGsonString(ArrayList<HashMap<String, String>> arrtable) {
        String convertGsonString = "";
        Gson gson = new Gson();
        try {
            convertGsonString = gson.toJson(arrtable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertGsonString;
    }

    public static Hashtable getROHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.SSROGUID, fetchJsonHeaderObject.getString(Constants.SSROGUID));
            try {
                dbHeadTable.put(Constants.BeatGuid, fetchJsonHeaderObject.getString(Constants.BeatGuid));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
           /* if(fetchJsonHeaderObject.getString(Constants.DmsDivision)!=null && !TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.DmsDivision)))
                dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));*/
//            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypId, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToId, fetchJsonHeaderObject.getString(Constants.SoldToId));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypDesc, fetchJsonHeaderObject.getString(Constants.SoldToTypDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.StatusID, fetchJsonHeaderObject.getString(Constants.StatusID));
            dbHeadTable.put(Constants.ApprovalStatusID, fetchJsonHeaderObject.getString(Constants.ApprovalStatusID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static double SkuQntyCar=0.0;

    public static String getCBBQty(SKUGroupBean skuGroupBean) {
        double doublAltUom1Num = 0.0;
        double doublAltUom1Den = 0.0;
        Double doublCBBQty = 0.0;
        Double mDoubOrderQty = 0.0;
        Double doublCalQty = 0.0;
        String alternativeUOM = "";
        String selectedUOM = "";



        try {
            alternativeUOM = skuGroupBean.getAlternativeUOM1();
            selectedUOM = skuGroupBean.getSelectedUOM();
            try {
                mDoubOrderQty =mDoubOrderQty+Double.parseDouble(skuGroupBean.getEtQty());
            } catch (NumberFormatException e) {
                mDoubOrderQty = 0.0;
                e.printStackTrace();
            }

            try {
                doublAltUom1Num = Double.parseDouble(skuGroupBean.getAlternativeUOM1Num());
            } catch (NumberFormatException e) {
                doublAltUom1Num = 0.0;
                e.printStackTrace();
            }

            try {
                doublAltUom1Den = Double.parseDouble(skuGroupBean.getAlternativeUOM1Den());
            } catch (NumberFormatException e) {
                doublAltUom1Den = 0.0;
                e.printStackTrace();
            }


            if (doublAltUom1Num > 0) {
                if (doublAltUom1Num <= doublAltUom1Den) { // Emami and Pal Case
                    if (alternativeUOM.equalsIgnoreCase(selectedUOM)) {
                        try {
                            doublCalQty = mDoubOrderQty / doublAltUom1Num;
                        } catch (Exception e) {
                            doublCalQty = 0.0;
                        }
                        if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                            doublCalQty = 0.0;
                        }

                        doublCBBQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublCalQty), 3).doubleValue();
                    } else {
                        try {
                            doublCalQty = mDoubOrderQty / doublAltUom1Den;
                        } catch (Exception e) {
                            doublCalQty = 0.0;
                        }
                        if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                            doublCalQty = 0.0;
                        }

                        try {
                            doublCBBQty = doublCalQty * doublAltUom1Num;
                        } catch (Exception e) {
                            doublCBBQty = 0.0;
                        }

                        if (doublCBBQty.isNaN() || doublCBBQty.isInfinite()) {
                            doublCBBQty = 0.0;
                        }

                        doublCBBQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublCBBQty), 3).doubleValue();
                    }


                } else { // RSPL

                    if (!selectedUOM.equalsIgnoreCase("")) {
                        if (alternativeUOM.equalsIgnoreCase(selectedUOM)) {

                            try {
                                doublCalQty = mDoubOrderQty / doublAltUom1Num;
                            } catch (Exception e) {
                                doublCalQty = 0.0;
                            }
                            if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                                doublCalQty = 0.0;
                            }

                            doublCBBQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublCalQty), 3).doubleValue();
                        } else {
                            try {
                                doublCalQty = mDoubOrderQty / doublAltUom1Den;
                            } catch (Exception e) {
                                doublCalQty = 0.0;
                            }
                            if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                                doublCalQty = 0.0;
                            }

                            try {
                                doublCBBQty = doublCalQty * doublAltUom1Den;
                            } catch (Exception e) {
                                doublCBBQty = 0.0;
                            }

                            if (doublCBBQty.isNaN() || doublCBBQty.isInfinite()) {
                                doublCBBQty = 0.0;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            doublCBBQty = 0.0;
            e.printStackTrace();
        }

      //  SkuQntyCar=SkuQntyCar+doublCBBQty;
      //  System.out.println("Sku Qnty: "+SkuQntyCar+"doublCBBQty "+doublCBBQty);

        return doublCBBQty+"";

    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ODataEntity> getListEntities(String mStrQry, ODataOfflineStore oDataOfflineStore) {
        List<ODataEntity> entities = null;
        try {
            entities = UtilOfflineManager.getEntities(oDataOfflineStore, mStrQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public static boolean isCBBOrBag(String onSaleUnitID) {
        boolean isCBB = false;
        if (onSaleUnitID != null && !onSaleUnitID.equalsIgnoreCase("")) {
            if (onSaleUnitID.equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB) || onSaleUnitID.equalsIgnoreCase(Constants.SchemeSaleUnitIDBAG)) {
                isCBB = true;
            }
        } else {
            isCBB = false;
        }
        return isCBB;
    }


    public static String getTotalPieceFromCBB(SKUGroupBean skuGroupBean) {
        double doublAltUom1Num = 0.0;
        double doublAltUom1Den = 0.0;
        Double doublPCQty = 0.0;
        Double doublCalQty = 0.0;
        Double mDoubleCBBQty = 0.0;
        try {
            try {
                doublAltUom1Num = Double.parseDouble(skuGroupBean.getAlternativeUOM1Num());
            } catch (NumberFormatException e) {
                doublAltUom1Num = 0.0;
                e.printStackTrace();
            }

            try {
                doublAltUom1Den = Double.parseDouble(skuGroupBean.getAlternativeUOM1Den());
            } catch (NumberFormatException e) {
                doublAltUom1Den = 0.0;
                e.printStackTrace();
            }
            try {
                mDoubleCBBQty = Double.parseDouble(skuGroupBean.getCBBQty());
            } catch (NumberFormatException e) {
                mDoubleCBBQty = 0.0;
                e.printStackTrace();
            }


            if (doublAltUom1Num > 0) {
                if (doublAltUom1Num <= doublAltUom1Den) { // Emami and Pal Case
                    try {
                        doublCalQty = mDoubleCBBQty * doublAltUom1Den;
                    } catch (Exception e) {
                        doublCalQty = 0.0;
                    }
                    if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                        doublCalQty = 0.0;
                    }

                    try {
                        doublPCQty = doublCalQty * doublAltUom1Num;
                    } catch (Exception e) {
                        doublPCQty = 0.0;
                    }

                    if (doublPCQty.isNaN() || doublPCQty.isInfinite()) {
                        doublPCQty = 0.0;
                    }

                    doublPCQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublPCQty), 3).doubleValue();
                } else { // RSPL
                    if (!skuGroupBean.getSelectedUOM().equalsIgnoreCase(skuGroupBean.getUOM()) && !skuGroupBean.getSelectedUOM().equalsIgnoreCase("")) {
                        try {
                            doublCalQty = mDoubleCBBQty * doublAltUom1Num;
                        } catch (Exception e) {
                            doublCalQty = 0.0;
                        }
                        if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                            doublCalQty = 0.0;
                        }

                        try {
                            doublPCQty = doublCalQty;
                        } catch (Exception e) {
                            doublPCQty = 0.0;
                        }

                        if (doublPCQty.isNaN() || doublPCQty.isInfinite()) {
                            doublPCQty = 0.0;
                        }
                    } else {
                        try {
                            doublCalQty = mDoubleCBBQty * doublAltUom1Num;
                        } catch (Exception e) {
                            doublCalQty = 0.0;
                        }
                        if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                            doublCalQty = 0.0;
                        }

                        try {
                            doublPCQty = doublCalQty;
                        } catch (Exception e) {
                            doublPCQty = 0.0;
                        }

                        if (doublPCQty.isNaN() || doublPCQty.isInfinite()) {
                            doublPCQty = 0.0;
                        }
                    }


                    doublPCQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublPCQty), 3).doubleValue();
                }
            }
        } catch (Exception e) {
            doublPCQty = 0.0;
            e.printStackTrace();
        }
        return doublPCQty + "";

    }

    public static boolean compareTwoDates(String mStrDate, String mStrDateTwo) {
        boolean mBoolValid = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-mm-dd");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(mStrDate);
            convertedDate2 = dateFormat.parse(mStrDateTwo);

            Calendar cal2 = Calendar.getInstance();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(convertedDate);
            cal2.setTime(convertedDate2);

            if (cal1.after(cal2)) {
                mBoolValid = true;
                System.out.println("Date1 is after Date2");
            } else if (cal1.before(cal2)) {
                mBoolValid = false;
                System.out.println("Date1 is before Date2");
            } else if (cal1.equals(cal2)) {
                mBoolValid = true;
                System.out.println("Date1 is before Date2");
            } else {
                mBoolValid = false;
                System.out.println("Date1 is equal to Date2");
            }


           /* if (convertedDate.compareTo(convertedDate2) > 0) {
                mBoolValid = true;
                System.out.println("Date1 is after Date2");
            } else if (convertedDate.compareTo(convertedDate2) < 0) {
                mBoolValid = false;
                System.out.println("Date1 is before Date2");
            } else if (convertedDate.compareTo(convertedDate2) == 0) {
                mBoolValid = false;
                System.out.println("Date1 is equal to Date2");
            }*/


        } catch (ParseException e) {
            mBoolValid = false;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mBoolValid;
    }

    public static String[][] getDistributors() {
        String[][] mArrayDistributors = null;
        String qryStr = Constants.SalesPersons;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[10][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
            mArrayDistributors[7][0] = "";
            mArrayDistributors[8][0] = "";
            mArrayDistributors[9][0] = "";
        }

        return mArrayDistributors;
    }

    public static Date convertStringToDate(String dates) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.parse(dates);
    }

    public static Hashtable getExpenseHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        try {

            dbHeadTable.put(Constants.ExpenseGUID, fetchJsonHeaderObject.getString(Constants.ExpenseGUID));
            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.ExpenseNo, fetchJsonHeaderObject.getString(Constants.ExpenseNo));
            dbHeadTable.put(Constants.FiscalYear, fetchJsonHeaderObject.getString(Constants.FiscalYear));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.ExpenseType, fetchJsonHeaderObject.getString(Constants.ExpenseType));
            dbHeadTable.put(Constants.ExpenseTypeDesc, fetchJsonHeaderObject.getString(Constants.ExpenseTypeDesc));
            dbHeadTable.put(Constants.ExpenseDate, fetchJsonHeaderObject.getString(Constants.ExpenseDate));
            dbHeadTable.put(Constants.Status, fetchJsonHeaderObject.getString(Constants.Status));
            dbHeadTable.put(Constants.StatusDesc, fetchJsonHeaderObject.getString(Constants.StatusDesc));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static String makeInvoiceListQry(ArrayList<InvoiceListBean> ssInvArrayList, String guidColumn) {
        String ssINVListQry = "";
        if (ssInvArrayList != null && ssInvArrayList.size() > 0) {
            for (int incVal = 0; incVal < ssInvArrayList.size(); incVal++) {
                if (incVal == 0 && incVal == ssInvArrayList.size() - 1) {
                    ssINVListQry = ssINVListQry
                            + "(" + guidColumn + "%20eq%20guid'"
                            + ssInvArrayList.get(incVal).getInvoiceGuid().toUpperCase() + "')";

                } else if (incVal == 0) {
                    ssINVListQry = ssINVListQry
                            + "(" + guidColumn + "%20eq%20guid'"
                            + ssInvArrayList.get(incVal).getInvoiceGuid().toUpperCase() + "'";

                } else if (incVal == ssInvArrayList.size() - 1) {
                    ssINVListQry = ssINVListQry
                            + "%20or%20" + guidColumn + "%20eq%20guid'"
                            + ssInvArrayList.get(incVal).getInvoiceGuid().toUpperCase() + "')";
                } else {
                    ssINVListQry = ssINVListQry
                            + "%20or%20" + guidColumn + "%20eq%20guid'"
                            + ssInvArrayList.get(incVal).getInvoiceGuid().toUpperCase() + "'";
                }
            }
        } else {
            ssINVListQry = "";
        }
        return ssINVListQry;
    }

    public static boolean isCBBOrBagItemMinmum(String onSaleUnitID) {
        boolean isCBB = false;
        if (onSaleUnitID != null && !onSaleUnitID.equalsIgnoreCase("")) {
            if (onSaleUnitID.equalsIgnoreCase(Constants.SchemeItemMinSaleUnitIDCBB) || onSaleUnitID.equalsIgnoreCase(Constants.SchemeItemMinSaleUnitIDBag)) {
                isCBB = true;
            }
        } else {
            isCBB = false;
        }
        return isCBB;
    }

    public static boolean isCarOrBag(String onSaleUnitID) {
        boolean isCBB = false;
        if (onSaleUnitID != null && !onSaleUnitID.equalsIgnoreCase("")) {
            if (onSaleUnitID.equalsIgnoreCase("car") || onSaleUnitID.equalsIgnoreCase("Bag")) {
                isCBB = true;
            }
        } else {
            isCBB = false;
        }
        return isCBB;
    }

    public static String getCalInvoiceAlterQty(ReturnOrderBean skuGroupBean) {
        double doublAltUom1Num = 0.0;
        double doublAltUom1Den = 0.0;
        Double doublCBBQty = 0.0;
        Double doublCalQty = 0.0;

        try {

            try {
                doublAltUom1Num = Double.parseDouble(skuGroupBean.getAlternativeUOM1Num());
            } catch (NumberFormatException e) {
                doublAltUom1Num = 0.0;
                e.printStackTrace();
            }

            try {
                doublAltUom1Den = Double.parseDouble(skuGroupBean.getAlternativeUOM1Den());
            } catch (NumberFormatException e) {
                doublAltUom1Den = 0.0;
                e.printStackTrace();
            }


            if (doublAltUom1Num > 0) {
                if (doublAltUom1Num <= doublAltUom1Den) { // Emami and Pal Case
                    try {
                        doublCalQty = doublAltUom1Num / doublAltUom1Den;
                    } catch (Exception e) {
                        doublCalQty = 0.0;
                    }
                    if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                        doublCalQty = 0.0;
                    }


                    doublCBBQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublCalQty), 3).doubleValue();


                } else { // RSPL

                    try {
                        doublCalQty = doublAltUom1Den / doublAltUom1Num;
                    } catch (Exception e) {
                        doublCalQty = 0.0;
                    }
                    if (doublCalQty.isNaN() || doublCalQty.isInfinite()) {
                        doublCalQty = 0.0;
                    }
                    doublCBBQty = ConstantsUtils.decimalRoundOff(new BigDecimal(doublCalQty), 3).doubleValue();

                }
            }
        } catch (Exception e) {
            doublCBBQty = 0.0;
            e.printStackTrace();
        }
        return doublCBBQty + "";

    }

    public static ArrayList<String> getEntityNames() {
        ArrayList<String> alEntityList = new ArrayList<>();
        alEntityList.add(Constants.Feedbacks);
        alEntityList.add(Constants.SecondarySOCreate);
        alEntityList.add(Constants.FinancialPostings);
        alEntityList.add(Constants.SampleDisbursement);
        alEntityList.add(Constants.CPList);
        alEntityList.add(Constants.ROList);
        alEntityList.add(Constants.Expenses);
        return alEntityList;
    }

    public static String get12HoursFromat(String mStrTime) {
        String mStrTimeFormat = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date dateObj = sdf.parse(mStrTime);
            mStrTimeFormat = new SimpleDateFormat("h:mm aa").format(dateObj);
        } catch (final ParseException e) {
            mStrTimeFormat = "00:00";
        }
        return mStrTimeFormat;
    }

    /*public static String getVisitedRetCountFromVisits(){
        String mVistCount = "0";
        String mVisitQry = Constants.Visits + "?$filter= " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.ENDDATE + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' " +"and StatusID ne '02' and ("+Constants.VisitCatID+" eq '"+Constants.str_01+"' or "+Constants.VisitCatID+" eq '"+Constants.str_02+"') &$orderby=" + Constants.EndTime+"%20desc ";
        List<ODataEntity> entities = null;
        try {
            entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mVisitQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            if(entities!=null && entities.size()>0){
                mVistCount = entities.size()+"";
            }else {
                mVistCount = "0";
            }
        } catch (Exception e) {
            mVistCount = "0";
            e.printStackTrace();
        }
        return mVistCount;
    }*/

    public static String getOrderCountFromSSSOs(Context context, String mStrCurrentDate) {

        String mSOOrderType = getSOOrderType();
        String mStrRetQry = "";
        int totalOrderCount = 0;
        alRetailersCount.clear();
        String mOfflineOrderCount = "0", mDataVaultOrderCount = "0";
        try {
            if (Constants.alRetailersGuid36 != null && Constants.alRetailersGuid36.size() > 0) {
                for (int i = 0; i < Constants.alRetailersGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alRetailersGuid36.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToCPGUID + "%20eq%20 guid'"
                                + Constants.alRetailersGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alRetailersGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alRetailersGuid36.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alRetailersGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alRetailersGuid36.get(i).toUpperCase() + "'";

                    }


                }


                String mStrRetTotalQry = Constants.SSSOs + " ?$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' " +
                        "and " + Constants.OrderType + " eq '" + mSOOrderType + "' and Status ne '000004' and " + mStrRetQry + " ";

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStrRetTotalQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (ODataEntity oDataEntity : entities) {
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.SoldToCPGUID);
                            ODataGuid mGUIDVal = null;
                            String cpGUID = "";
                            try {
                                mGUIDVal = (ODataGuid) property.getValue();
                                cpGUID = mGUIDVal.guidAsString36().toUpperCase();
                            } catch (Exception e) {
                                cpGUID = "";
                                e.printStackTrace();
                            }
                            if (!alRetailersCount.contains(cpGUID.toUpperCase())) {
                                alRetailersCount.add(cpGUID.toUpperCase());
                            }
                        }
                        mOfflineOrderCount = entities.size() + "";
                    } else {
                        mOfflineOrderCount = "0";
                    }
                } catch (Exception e) {
                    mOfflineOrderCount = "0";
                    e.printStackTrace();
                }

                try {
                    mDataVaultOrderCount = OfflineManager.getDeviceOrderCount(Constants.SecondarySOCreate, context, mStrCurrentDate, Constants.alRetailersGuid36);
                } catch (Exception e) {
                    mDataVaultOrderCount = "0";
                }
                try {
                    totalOrderCount = Constants.alRetailersCount.size();
                } catch (NumberFormatException e) {
                    totalOrderCount = 0;
                    e.printStackTrace();
                }
            } else {
                totalOrderCount = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return totalOrderCount + "";
    }

    public static String getVisitedOrderCount(Context context, String mStrCurrentDate) {

        String mStrRetQry = "";
        String mStrBeatQry = "";
        int totalOrderCount = 0;
        int mDataVaultOrderCount = 0;
        Set<String> alTemp = new HashSet<>();
        HashMap<String, Set<String>> distMap = new HashMap<>();
        ArrayList<String> alVisitedSOCount = new ArrayList<>();
        ArrayList<String> alBeatSOCount = new ArrayList<>();
        ArrayList<SOCreateBean> alVisitedSOCountDataVolt = new ArrayList<>();
        try {
            if (Constants.alVisitedGuid36 != null && Constants.alVisitedGuid36.size() > 0) {
                for (int i = 0; i < Constants.alVisitedGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alVisitedGuid36.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToCPGUID + "%20eq%20 guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrRetQry = mStrRetQry
                                + "(" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alVisitedGuid36.size() - 1) {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrRetQry = mStrRetQry
                                + "%20or%20" + Constants.SoldToCPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "'";

                    }


                }

                for (int i = 0; i < Constants.alRouteGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alRouteGuid36.size() - 1) {
                        mStrBeatQry = mStrBeatQry
                                + "(" + Constants.BeatGuid + "%20eq%20 guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrBeatQry = mStrBeatQry
                                + "(" + Constants.BeatGuid + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alRouteGuid36.size() - 1) {
                        mStrBeatQry = mStrBeatQry
                                + "%20or%20" + Constants.BeatGuid + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrBeatQry = mStrBeatQry
                                + "%20or%20" + Constants.BeatGuid + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "'";

                    }


                }

                String mStrRetTotalQry = Constants.SSSOs + " ?$filter=" + Constants.OrderDate + " eq datetime'" + mStrCurrentDate + "' " +
                        "and " + mStrRetQry + " and " + mStrBeatQry + " and SPGUID eq guid'" + Constants.getSPGUID() + "'";

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStrRetTotalQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (ODataEntity oDataEntity : entities) {
                            alTemp = new HashSet<>();
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.SoldToCPGUID);
                            ODataGuid mGUIDVal = null;
                            String cpGUID = "";
                            try {
                                mGUIDVal = (ODataGuid) property.getValue();
                                cpGUID = mGUIDVal.guidAsString36().toUpperCase();
                            } catch (Exception e) {
                                cpGUID = "";
                                e.printStackTrace();
                            }

                            property = properties.get(Constants.BeatGuid);
                            ODataGuid mGUIDBeat = null;
                            String beatGUID = "";
                            try {
                                mGUIDBeat = (ODataGuid) property.getValue();
                                beatGUID = mGUIDBeat.guidAsString36().toUpperCase();
                            } catch (Exception e) {
                                cpGUID = "";
                                e.printStackTrace();
                            }
                            if (!alVisitedSOCount.contains(cpGUID.toUpperCase())) {
                                alVisitedSOCount.add(cpGUID.toUpperCase());
                            }
                            if (distMap.containsKey(cpGUID)) {
                                Set<String> beatList = distMap.get(cpGUID);
                                beatList.add(beatGUID);
                                distMap.put(cpGUID, beatList);
                            } else {
                                alTemp.add(beatGUID);
                                distMap.put(cpGUID, alTemp);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    alVisitedSOCountDataVolt = OfflineManager.getDeviceSOCount(Constants.SecondarySOCreateTemp, context, mStrCurrentDate);
                    for (int i = 0; i < alVisitedSOCountDataVolt.size(); i++) {
                        alTemp = new HashSet<>();
                        SOCreateBean soCreateBean = alVisitedSOCountDataVolt.get(i);
//                        if(!alVisitedSOCount.contains(alVisitedSOCountDataVolt.get(i))){
//                            alVisitedSOCount.add(alVisitedSOCountDataVolt.get(i));
//                        }
                        if (distMap.containsKey(soCreateBean.getCPGUID())) {
                            Set<String> beatList = distMap.get(soCreateBean.getCPGUID());
                            beatList.add(soCreateBean.getBeatGUID());
                            distMap.put(soCreateBean.getCPGUID(), beatList);
                        } else {
                            alTemp.add(soCreateBean.getBeatGUID());
                            distMap.put(soCreateBean.getCPGUID(), alTemp);

                        }
                    }

                } catch (Exception e) {
                    mDataVaultOrderCount = 0;
                }
                int totalVisit = 0;
                try {
                    Set keys = distMap.keySet();
                    Iterator itr = keys.iterator();
                    String key;
                    Set<String> value;

                    while (itr.hasNext()) {
                        key = (String) itr.next();
                        value = distMap.get(key);
                        totalVisit = totalVisit + value.size();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
//                    totalOrderCount = alVisitedSOCount.size();
                    totalOrderCount = totalVisit;
                } catch (NumberFormatException e) {
                    totalOrderCount = 0;
                    e.printStackTrace();
                }
            } else {
                totalOrderCount = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalOrderCount + "";
    }

    public static ArrayList<VisitedBeatBean> getBeatCount(Context context) {

        String mStrBeatQry = "";
        String mStrRouteQry = "";
        int totalOrderCount = 0;
        ArrayList<String> alBeatCount = new ArrayList<>();
        ArrayList<String> alCPno = new ArrayList<>();
        ArrayList<VisitedBeatBean> alBeatList = new ArrayList<>();
        VisitedBeatBean visitedBeatBean = null;
        String mStrGuid = "";
        String desc = "";
        String cpno = "";
        String parentID = "";
        int alValue = 0;
        int visitCount = 0;
        try {
            if (Constants.visitRetailersMap != null && Constants.visitRetailersMap.size() > 0) {
//            if (Constants.alVisitedGuid36 != null && Constants.alVisitedGuid36.size() > 0) {
                /*for (int i = 0; i < Constants.alVisitedGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alVisitedGuid36.size() - 1) {
                        mStrBeatQry = mStrBeatQry
                                + "(" + Constants.CPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrBeatQry = mStrBeatQry
                                + "(" + Constants.CPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alVisitedGuid36.size() - 1) {
                        mStrBeatQry = mStrBeatQry
                                + "%20or%20" + Constants.CPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrBeatQry = mStrBeatQry
                                + "%20or%20" + Constants.CPGUID + "%20eq%20guid'"
                                + Constants.alVisitedGuid36.get(i).toUpperCase() + "'";

                    }


                }

                for (int i = 0; i < Constants.alRouteGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alRouteGuid36.size() - 1) {
                        mStrRouteQry = mStrRouteQry
                                + "(" + Constants.RouteGUID + "%20eq%20 guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrRouteQry = mStrRouteQry
                                + "(" + Constants.RouteGUID + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alRouteGuid36.size() - 1) {
                        mStrRouteQry = mStrRouteQry
                                + "%20or%20" + Constants.RouteGUID + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrRouteQry = mStrRouteQry
                                + "%20or%20" + Constants.RouteGUID + "%20eq%20guid'"
                                + Constants.alRouteGuid36.get(i).toUpperCase() + "'";

                    }


                }*/

                Set keys = Constants.visitRetailersMap.keySet();
                Iterator itr = keys.iterator();
                String key;
                Set<String> value;
                int count = 0;
                int totalCount = keys.size();
                while (itr.hasNext()) {
                    key = (String) itr.next();
                    value = Constants.visitRetailersMap.get(key);
                    if (!TextUtils.isEmpty(key)) {
                        String cpGuidQry = "(" + Constants.CPGUID + "%20eq%20 guid'"
                                + Constants.convertStrGUID32to36(key).toUpperCase() + "' and ";
                        if (value != null) {
                            ArrayList<String> valueAl = new ArrayList<>(value);
                            for (int i = 0; i < valueAl.size(); i++) {
                                if (i == valueAl.size() - 1) {
                                    mStrRouteQry = mStrRouteQry + cpGuidQry + Constants.RouteGUID + "%20eq%20guid'"
                                            + valueAl.get(i).toUpperCase() + "')";

                                } else {
                                    mStrRouteQry = mStrRouteQry + cpGuidQry
                                            + Constants.RouteGUID + "%20eq%20guid'"
                                            + valueAl.get(i).toUpperCase() + "') or ";

                                }
                            }
                        }
                    }
                    if ((totalCount - 1) == count) {
                        mStrRouteQry = mStrRouteQry + "";
                    } else {
                        mStrRouteQry = mStrRouteQry + " or ";
                    }
                    count++;
                }

                ArrayList<String> divisionList = null;
                try {
                    divisionList = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27" + " &$orderby=AuthOrgTypeID asc");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String stringDivision = "";
                if (divisionList != null && !divisionList.isEmpty()) {
                    for (int i = 0; i < divisionList.size(); i++) {
                        if (i == divisionList.size() - 1) {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i) + "'";
                        } else {
                            stringDivision = stringDivision + "DMSDivision eq '" + divisionList.get(i) + "' or ";
                        }
                    }
                }

//                String mStrRetTotalQry = Constants.CPDMSDivisions + "?$filter=" +mStrBeatQry+" and "+mStrRouteQry + " &$orderby=" + Constants.RouteDesc + " desc";
                String mStrRetTotalQry = Constants.CPDMSDivisions + "?$filter=" + mStrRouteQry + " &$orderby=" + Constants.RouteDesc + " desc";

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStrRetTotalQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < entities.size(); i++) {
                            ODataEntity oDataEntity = entities.get(i);
                            visitedBeatBean = new VisitedBeatBean();
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.RouteGUID);
                            ODataGuid mGUIDVal = null;
                            try {
                                mGUIDVal = (ODataGuid) property.getValue();
                                mStrGuid = mGUIDVal.guidAsString36();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            property = properties.get(Constants.RouteDesc);
                            desc = (String) property.getValue();
                            property = properties.get(Constants.CPNo);
                            cpno = (String) property.getValue();
                            property = properties.get(Constants.ParentID);
                            parentID = (String) property.getValue();

                            property = properties.get(Constants.CPGUID);
                            ODataGuid cpGuidVal = null;
                            String cpGUIDTemp = "";
                            cpGuidVal = (ODataGuid) property.getValue();
                            cpGUIDTemp = cpGuidVal.guidAsString36().toUpperCase();
                            // Log.d("Beat_Count","Diff"+cpGUIDTemp+"--"+cpno+"--"+mStrGuid);

                            /*if (!alCPno.contains(cpGUIDTemp)) {
                                if (!alBeatCount.contains(mStrGuid)) {
                                    alValue = alValue + 1;
                                    visitCount = 1;
                                    alBeatCount.add(mStrGuid);
                                    visitedBeatBean.setBeatName(desc);
                                    visitedBeatBean.setTotalVisitedRetailers(String.valueOf(visitCount));
                                    visitedBeatBean.setRouteGUID(mStrGuid);
                                    if(!TextUtils.isEmpty(stringDivision)) {
                                        visitedBeatBean.setTotalBeatRetailers(OfflineManager.getTotalRetailerCount(Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + mStrGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ParentID eq '" + parentID + "' and ("+stringDivision+")"));
                                    }else {
                                        visitedBeatBean.setTotalBeatRetailers(OfflineManager.getTotalRetailerCount(Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + mStrGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ParentID eq '" + parentID + "'"));
                                    }
                                    visitedBeatBean.setVisitedStatus(OfflineManager.getRoutePlanStatus(Constants.RoutePlans + "?$filter=" + Constants.RschGuid + " eq guid'" + mStrGuid + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGuid + " eq guid'" + Constants.getSPGUID() + "'"));
                                    alBeatList.add(visitedBeatBean);
                                } else {
                                    try {
                                    *//*int tempValue = alValue-1;
                                    visitCount =visitCount+1;
                                    VisitedBeatBean visitedBeatBean1 = alBeatList.get(tempValue);
                                    visitedBeatBean1.setTotalVisitedRetailers(String.valueOf(visitCount));
                                    alBeatList.set(tempValue,visitedBeatBean1);*//*
                                        int j = 0;
                                        for (VisitedBeatBean beatBean : alBeatList) {
                                            if (mStrGuid.equalsIgnoreCase(beatBean.getRouteGUID())) {
                                                beatBean.setTotalVisitedRetailers(String.valueOf(Integer.parseInt(beatBean.getTotalVisitedRetailers()) + 1));
                                                alBeatList.set(j, beatBean);
                                            }
                                            j++;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                alCPno.add(cpGUIDTemp);
                            }*/


                            alCPno = new ArrayList<>();
                            if (!alBeatCount.contains(mStrGuid)) {
                                if (!alCPno.contains(cpGUIDTemp)) {
                                    alCPno.add(cpGUIDTemp);
                                    alValue = alValue + 1;
                                    visitCount = 1;
                                    alBeatCount.add(mStrGuid);
                                    visitedBeatBean.setBeatName(desc);
                                    visitedBeatBean.setTotalVisitedRetailers(String.valueOf(visitCount));
                                    visitedBeatBean.setRouteGUID(mStrGuid);
                                    if (!TextUtils.isEmpty(stringDivision)) {
                                        visitedBeatBean.setTotalBeatRetailers(OfflineManager.getTotalRetailerCount(Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + mStrGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ParentID eq '" + parentID + "' and (" + stringDivision + ")"));
                                    } else {
                                        visitedBeatBean.setTotalBeatRetailers(OfflineManager.getTotalRetailerCount(Constants.CPDMSDivisions + "?$filter=" + Constants.RouteGUID + " eq guid'" + mStrGuid + "' and StatusID eq '01' and ApprvlStatusID eq '03' and ParentID eq '" + parentID + "'"));
                                    }
                                    visitedBeatBean.setVisitedStatus(OfflineManager.getRoutePlanStatus(Constants.RoutePlans + "?$filter=" + Constants.RschGuid + " eq guid'" + mStrGuid + "' and " + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SPGuid + " eq guid'" + Constants.getSPGUID() + "'"));
                                    visitedBeatBean.setAlCPGuid(alCPno);
                                    alBeatList.add(visitedBeatBean);
                                }
                            } else {
                                try {
                                    /*int tempValue = alValue-1;
                                    visitCount =visitCount+1;
                                    VisitedBeatBean visitedBeatBean1 = alBeatList.get(tempValue);
                                    visitedBeatBean1.setTotalVisitedRetailers(String.valueOf(visitCount));
                                    alBeatList.set(tempValue,visitedBeatBean1);*/
                                    int j = 0;
                                    for (VisitedBeatBean beatBean : alBeatList) {
                                        ArrayList<String> alCpGuidTemp = beatBean.getAlCPGuid();
                                        if (mStrGuid.equalsIgnoreCase(beatBean.getRouteGUID())) {
                                            if (!alCpGuidTemp.contains(cpGUIDTemp)) {
                                                alCpGuidTemp.add(cpGUIDTemp);
                                                beatBean.setTotalVisitedRetailers(String.valueOf(Integer.parseInt(beatBean.getTotalVisitedRetailers()) + 1));
                                                beatBean.setAlCPGuid(alCpGuidTemp);
                                                alBeatList.set(j, beatBean);
                                            }
                                        }
                                        j++;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Test", "getBeatCount");
        return alBeatList;
    }

    public static ArrayList<BrandProductiveBean> getBrandDetails(Context context) {

        String mStrVisitQry = "";
        int totalOrderCount = 0;
        ArrayList<String> alVisitAct = new ArrayList<>();
        ArrayList<String> alSSS0Guid = new ArrayList<>();
        ArrayList<BrandProductiveBean> alSSS0Item = new ArrayList<>();
        VisitedBeatBean visitedBeatBean = null;
        String mStrGuid = "";
        ODataGuid oDataGuid = null;
        int alValue = 0;
        int visitCount = 0;
        try {
            if (Constants.alVisitGuid36 != null && Constants.alVisitGuid36.size() > 0) {
                for (int i = 0; i < Constants.alVisitGuid36.size(); i++) {
                    if (i == 0 && i == Constants.alVisitGuid36.size() - 1) {
                        mStrVisitQry = mStrVisitQry
                                + "(" + Constants.VisitGUID + "%20eq%20guid'"
                                + Constants.alVisitGuid36.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrVisitQry = mStrVisitQry
                                + "(" + Constants.VisitGUID + "%20eq%20guid'"
                                + Constants.alVisitGuid36.get(i).toUpperCase() + "'";


                    } else if (i == Constants.alVisitGuid36.size() - 1) {
                        mStrVisitQry = mStrVisitQry
                                + "%20or%20" + Constants.VisitGUID + "%20eq%20guid'"
                                + Constants.alVisitGuid36.get(i).toUpperCase() + "')";

                    } else {
                        mStrVisitQry = mStrVisitQry
                                + "%20or%20" + Constants.VisitGUID + "%20eq%20guid'"
                                + Constants.alVisitGuid36.get(i).toUpperCase() + "'";

                    }


                }

                String mStrVisitActQry = Constants.VisitActivities + "?$filter=" + mStrVisitQry;

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStrVisitActQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (ODataEntity oDataEntity : entities) {
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.ActivityRefID);
                            oDataGuid = (ODataGuid) property.getValue();
                            if (oDataGuid != null) {
                                mStrGuid = oDataGuid.guidAsString36();
                                alVisitAct.add(mStrGuid);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    alSSS0Guid = Constants.getSSSOGuid(alVisitAct);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    alSSS0Item = Constants.getSSSOiteMDeatils(alSSS0Guid, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Test", "getBrand");
        return alSSS0Item;
    }

    private static ArrayList<BrandProductiveBean> getSSSOiteMDeatils(ArrayList<String> alSSS0Guid, Context context) {
        String mItemQry = "";
        ArrayList<BrandProductiveBean> alBrandProductive = new ArrayList<>();
        ArrayList<BrandProductiveBean> alBrandProductiveTemp = new ArrayList<>();
        ArrayList<BrandProductiveBean> alBrandProductiveDatavolt = new ArrayList<>();
        ArrayList<String> alBrand = new ArrayList<>();
        ArrayList<String> alItem = new ArrayList<>();
        ArrayList<String> alRetailerCount = new ArrayList<>();
        BrandProductiveBean brandProductiveBean = null;
        int alValue = 0;
        int visitCount = 0;
        String brandName = "";
        String itemName = "";
        String price = "";
        String banner = "";
        String isfreeGoodsItem = "";
        ODataGuid sssoGuid = null;
//        String cpGuid = "";
        String UOM = "";
        String HigherLevelItemno = "";
        String sssoGuidStr = "";
        String quantity = "";
        int quantityValue = 0;
        int pcValue = 0;
        int bagValue = 0;
        int carValue = 0;
        double netPrice = 0.0;
        try {
            if (alSSS0Guid != null && alSSS0Guid.size() > 0) {
                for (int i = 0; i < alSSS0Guid.size(); i++) {
                    if (i == 0 && i == alSSS0Guid.size() - 1) {
                        mItemQry = mItemQry
                                + "(" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alSSS0Guid.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mItemQry = mItemQry
                                + "(" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alSSS0Guid.get(i).toUpperCase() + "'";


                    } else if (i == alSSS0Guid.size() - 1) {
                        mItemQry = mItemQry
                                + "%20or%20" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alSSS0Guid.get(i).toUpperCase() + "')";

                    } else {
                        mItemQry = mItemQry
                                + "%20or%20" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alSSS0Guid.get(i).toUpperCase() + "'";

                    }


                }

                String mStrItemQry = Constants.SSSOItemDetails + "?$filter=" + mItemQry + " &$orderby=" + Constants.OrderMatGrpDesc + " desc";

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStrItemQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < entities.size(); i++) {
                            ODataEntity oDataEntity = entities.get(i);
                            brandProductiveBean = new BrandProductiveBean();
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.OrderMatGrpDesc);
                            brandName = (String) property.getValue();
                            property = properties.get(Constants.Quantity);
                            quantity = property.getValue().toString();
                            property = properties.get(Constants.MaterialDesc);
                            itemName = (String) property.getValue();
                            property = properties.get(Constants.NetPrice);
                            price = property.getValue().toString();
                            property = properties.get(Constants.Brand);
                            banner = property.getValue().toString();
                            property = properties.get(Constants.IsfreeGoodsItem);
                            isfreeGoodsItem = property.getValue().toString();
                            property = properties.get(Constants.Uom);
                            UOM = property.getValue().toString();
                            property = properties.get(Constants.HigherLevelItemno);
                            HigherLevelItemno = property.getValue().toString();
                            SOCreateBean soCreateBean = new SOCreateBean();
                            try {
                                property = properties.get(Constants.SSSOGuid);
                                sssoGuid = (ODataGuid) property.getValue();
                                sssoGuidStr = sssoGuid.guidAsString36();
                                soCreateBean = OfflineManager.getCPGuid(Constants.SSSOs + "?$filter=" + Constants.SSSOGuid + " eq guid'" + sssoGuidStr + "'");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!isfreeGoodsItem.equalsIgnoreCase("X")) {
                                if (HigherLevelItemno.equalsIgnoreCase("000000")) {
                                    if (!alBrand.contains(banner)) {
                                        alItem = new ArrayList<>();
                                        alRetailerCount = new ArrayList<>();
                                        quantityValue = 0;
                                        netPrice = 0.0;
                                        pcValue = 0;
                                        bagValue = 0;
                                        carValue = 0;
                                        alBrand.add(banner);
                                        alValue = alValue + 1;
                                        if (!TextUtils.isEmpty(price)) {
                                            netPrice = netPrice + Double.parseDouble(price);
                                        }
                                        if (!TextUtils.isEmpty(quantity)) {
                                            quantityValue = quantityValue + (int) Float.parseFloat(quantity);
                                        }
                                        if (UOM.equalsIgnoreCase("pc")) {
                                            if (!TextUtils.isEmpty(quantity)) {
                                                pcValue = pcValue + (int) Float.parseFloat(quantity);
                                            }
                                        }
                                        if (UOM.equalsIgnoreCase("bag")) {
                                            if (!TextUtils.isEmpty(quantity)) {
                                                bagValue = bagValue + (int) Float.parseFloat(quantity);
                                            }
                                        }
                                        if (UOM.equalsIgnoreCase("car")) {
                                            if (!TextUtils.isEmpty(quantity)) {
                                                carValue = carValue + (int) Float.parseFloat(quantity);
                                            }
                                        }
                                        if (!alRetailerCount.contains(soCreateBean.getCPGUID36() + ":" + soCreateBean.getBeatGUID())) {
                                            alRetailerCount.add(soCreateBean.getCPGUID36() + ":" + soCreateBean.getBeatGUID());
                                        }
                                        if (!alItem.contains(itemName)) {
                                            alItem.add(itemName);
                                        }
                                        brandProductiveBean.setOrderMatGrpDesc(brandName);
                                        brandProductiveBean.setQuantity(String.valueOf(quantityValue));
                                        brandProductiveBean.setMaterialItemDesc(String.valueOf(alItem.size()));
                                        brandProductiveBean.setRetailerCount(String.valueOf(alRetailerCount.size()));
                                        brandProductiveBean.setToatlPrice(String.valueOf(netPrice));
                                        brandProductiveBean.setMaterialItemList(alItem);
                                        brandProductiveBean.setRetailerCountList(alRetailerCount);
                                        brandProductiveBean.setBrandID(banner);
                                        brandProductiveBean.setPcValue(String.valueOf(pcValue));
                                        brandProductiveBean.setBagValue(String.valueOf(bagValue));
                                        brandProductiveBean.setCarValue(String.valueOf(carValue));
                                        alBrandProductive.add(brandProductiveBean);
                                    } else {
                                        int j = 0;
                                        for (BrandProductiveBean brandProductiveBean1 : alBrandProductive) {
                                            int qty = (int) Float.parseFloat(brandProductiveBean1.getQuantity());
                                            int pcVal = (int) Float.parseFloat(brandProductiveBean1.getPcValue());
                                            int bagVal = (int) Float.parseFloat(brandProductiveBean1.getBagValue());
                                            int carVal = (int) Float.parseFloat(brandProductiveBean1.getCarValue());
                                            double totlePrice = Double.parseDouble(brandProductiveBean1.getToatlPrice());
                                            ArrayList<String> alItem1 = brandProductiveBean1.getMaterialItemList();
                                            ArrayList<String> alRetailerCountList = brandProductiveBean1.getRetailerCountList();
                                            if (banner.equalsIgnoreCase(brandProductiveBean1.getBrandID())) {
                                                if (!TextUtils.isEmpty(quantity)) {
                                                    qty = qty + (int) Float.parseFloat(quantity);
                                                }
                                                if (!TextUtils.isEmpty(price)) {
                                                    totlePrice = totlePrice + Double.parseDouble(price);
                                                }
                                                if (!alItem1.contains(itemName)) {
                                                    alItem1.add(itemName);
                                                }
                                                if (!alRetailerCountList.contains(soCreateBean.getCPGUID36() + ":" + soCreateBean.getBeatGUID())) {
                                                    alRetailerCountList.add(soCreateBean.getCPGUID36() + ":" + soCreateBean.getBeatGUID());
                                                }
                                                if (UOM.equalsIgnoreCase("pc")) {
                                                    if (!TextUtils.isEmpty(quantity)) {
                                                        pcVal = pcVal + (int) Float.parseFloat(quantity);
                                                    }
                                                }
                                                if (UOM.equalsIgnoreCase("bag")) {
                                                    if (!TextUtils.isEmpty(quantity)) {
                                                        bagVal = bagVal + (int) Float.parseFloat(quantity);
                                                    }
                                                }
                                                if (UOM.equalsIgnoreCase("car")) {
                                                    if (!TextUtils.isEmpty(quantity)) {
                                                        carVal = carVal + (int) Float.parseFloat(quantity);
                                                    }
                                                }
                                                brandProductiveBean.setRetailerCountList(alRetailerCountList);
                                                brandProductiveBean1.setMaterialItemDesc(String.valueOf(alItem1.size()));
                                                brandProductiveBean1.setRetailerCount(String.valueOf(alRetailerCountList.size()));
                                                brandProductiveBean1.setQuantity(String.valueOf(qty));
                                                brandProductiveBean1.setToatlPrice(String.valueOf(totlePrice));
                                                brandProductiveBean1.setMaterialItemList(alItem1);
                                                brandProductiveBean1.setPcValue(String.valueOf(pcVal));
                                                brandProductiveBean1.setBagValue(String.valueOf(bagVal));
                                                brandProductiveBean1.setCarValue(String.valueOf(carVal));
                                                alBrandProductive.set(j, brandProductiveBean1);
                                                break;
                                            }
                                            j++;
                                        }

                                    /*int temp = alValue - 1;
                                    BrandProductiveBean brandProductiveBean1 = alBrandProductive.get(temp);
                                    if(!TextUtils.isEmpty(quantity)) {
                                        quantityValue = quantityValue + (int)Float.parseFloat(quantity);
                                    }
                                    if(!TextUtils.isEmpty(price)) {
                                        netPrice = netPrice + Double.parseDouble(price);
                                    }
                                    if(!alItem.contains(itemName)){
                                        alItem.add(itemName);
                                    }
                                    if(!alRetailerCount.contains(cpGuid)){
                                        alRetailerCount.add(cpGuid);
                                    }
                                    if(UOM.equalsIgnoreCase("pc")) {
                                        if (!TextUtils.isEmpty(quantity)) {
                                            pcValue = pcValue + (int) Float.parseFloat(quantity);
                                        }
                                    }
                                    if(UOM.equalsIgnoreCase("bag")) {
                                        if (!TextUtils.isEmpty(quantity)) {
                                            bagValue = bagValue + (int) Float.parseFloat(quantity);
                                        }
                                    }
                                    if(UOM.equalsIgnoreCase("car")) {
                                        if (!TextUtils.isEmpty(quantity)) {
                                            carValue = carValue + (int) Float.parseFloat(quantity);
                                        }
                                    }
                                    brandProductiveBean1.setMaterialItemDesc(String.valueOf(alItem.size()));
                                    brandProductiveBean1.setRetailerCount(String.valueOf(alRetailerCount.size()));
                                    brandProductiveBean1.setQuantity(String.valueOf(quantityValue));
                                    brandProductiveBean1.setToatlPrice(String.valueOf(netPrice));
                                    brandProductiveBean1.setMaterialItemList(alItem);
                                    brandProductiveBean1.setRetailerCountList(alRetailerCount);
                                    brandProductiveBean1.setPcValue(String.valueOf(pcValue));
                                    brandProductiveBean1.setBagValue(String.valueOf(bagValue));
                                    brandProductiveBean1.setCarValue(String.valueOf(carValue));
                                    alBrandProductive.set(temp,brandProductiveBean1);*/
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            try {
                alBrandProductiveDatavolt = OfflineManager.getDeviceSSSoDetails(Constants.SecondarySOCreateTemp, context, UtilConstants.getNewDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
            int quantityValueTemp = 0;
            int pcValueTemp = 0;
            int bagValueTemp = 0;
            int carValueTemp = 0;
            double netPriceTemp = 0.0;
            try {
                if (alBrandProductive != null && alBrandProductive.size() > 0) {
                    int i = 0;
                    for (BrandProductiveBean brandProductiveTemp : alBrandProductive) {
                        quantityValueTemp = 0;
                        pcValueTemp = 0;
                        bagValueTemp = 0;
                        carValueTemp = 0;
                        netPriceTemp = 0.0;
//                        BrandProductiveBean brandProductiveTemp = alBrandProductive.get(i);
                        String bannerID = brandProductiveTemp.getBrandID();
                        ArrayList<String> itemList = brandProductiveTemp.getMaterialItemList();
                        ArrayList<String> retailerList = brandProductiveTemp.getRetailerCountList();

                        for (int j = 0; j < alBrandProductiveDatavolt.size(); j++) {
                            BrandProductiveBean brandProductiveDataVoltTemp = alBrandProductiveDatavolt.get(j);
                            String bannerDataVoltID = brandProductiveDataVoltTemp.getBrandID();
                            String bannerDataVoltItem = brandProductiveDataVoltTemp.getMaterialItemDesc();
                            ArrayList<String> itemListDataVolt = brandProductiveDataVoltTemp.getMaterialItemList();
                            ArrayList<String> retailerListDataVolt = brandProductiveDataVoltTemp.getRetailerCountList();
                            if (bannerID.equalsIgnoreCase(bannerDataVoltID)) {
                                if (!brandProductiveTemp.getQuantity().isEmpty()) {
                                    quantityValueTemp = quantityValueTemp + (int) Float.parseFloat(brandProductiveTemp.getQuantity());
                                }

                                if (!brandProductiveTemp.getPcValue().isEmpty()) {
                                    pcValueTemp = pcValueTemp + (int) Float.parseFloat(brandProductiveTemp.getPcValue());
                                }

                                if (!brandProductiveTemp.getBagValue().isEmpty()) {
                                    bagValueTemp = bagValueTemp + (int) Float.parseFloat(brandProductiveTemp.getBagValue());
                                }
                                if (!brandProductiveTemp.getCarValue().isEmpty()) {
                                    carValueTemp = carValueTemp + (int) Float.parseFloat(brandProductiveTemp.getCarValue());
                                }
                                if (!brandProductiveTemp.getToatlPrice().isEmpty()) {
                                    netPriceTemp = netPriceTemp + Double.parseDouble(brandProductiveTemp.getToatlPrice());
                                }
                                for (int k = 0; k < itemListDataVolt.size(); k++) {
                                    if (!itemList.contains(itemListDataVolt.get(k))) {
                                        itemList.add(itemListDataVolt.get(k));
                                    }
                                }
                                for (int k = 0; k < retailerListDataVolt.size(); k++) {
                                    if (!retailerList.contains(retailerListDataVolt.get(k))) {
                                        retailerList.add(retailerListDataVolt.get(k));
                                    }
                                }
                                if (!brandProductiveDataVoltTemp.getQuantity().isEmpty()) {
                                    quantityValueTemp = quantityValueTemp + (int) Float.parseFloat(brandProductiveDataVoltTemp.getQuantity());
                                }

                                if (!brandProductiveDataVoltTemp.getPcValue().isEmpty()) {
                                    pcValueTemp = pcValueTemp + (int) Float.parseFloat(brandProductiveDataVoltTemp.getPcValue());
                                }

                                if (!brandProductiveDataVoltTemp.getBagValue().isEmpty()) {
                                    bagValueTemp = bagValueTemp + (int) Float.parseFloat(brandProductiveDataVoltTemp.getBagValue());
                                }
                                if (!brandProductiveDataVoltTemp.getCarValue().isEmpty()) {
                                    carValueTemp = carValueTemp + (int) Float.parseFloat(brandProductiveDataVoltTemp.getCarValue());
                                }

                                if (!brandProductiveDataVoltTemp.getToatlPrice().isEmpty()) {
                                    netPriceTemp = netPriceTemp + Double.parseDouble(brandProductiveDataVoltTemp.getToatlPrice());
                                }
                                brandProductiveTemp.setToatlPrice(String.valueOf(netPriceTemp));
                                brandProductiveTemp.setMaterialItemDesc(String.valueOf(itemList.size()));
                                brandProductiveTemp.setRetailerCount(String.valueOf(retailerList.size()));
                                brandProductiveTemp.setMaterialItemList(itemList);
                                brandProductiveTemp.setRetailerCountList(retailerList);
                                brandProductiveTemp.setQuantity(String.valueOf(quantityValueTemp));
                                brandProductiveTemp.setPcValue(String.valueOf(pcValueTemp));
                                brandProductiveTemp.setBagValue(String.valueOf(bagValueTemp));
                                brandProductiveTemp.setCarValue(String.valueOf(carValueTemp));
                                alBrandProductiveDatavolt.remove(j);
                                break;
                            }
                        }
                        alBrandProductive.set(i, brandProductiveTemp);
                        i++;
                    }
                    for (int j = 0; j < alBrandProductiveDatavolt.size(); j++) {
                        BrandProductiveBean brandProductiveDataVoltTemp = alBrandProductiveDatavolt.get(j);
                        BrandProductiveBean brandProductivetemp = new BrandProductiveBean();
                        brandProductivetemp.setOrderMatGrpDesc(brandProductiveDataVoltTemp.getOrderMatGrpDesc());
                        brandProductivetemp.setQuantity(brandProductiveDataVoltTemp.getQuantity());
                        brandProductivetemp.setMaterialItemDesc(brandProductiveDataVoltTemp.getMaterialItemDesc());
                        brandProductivetemp.setToatlPrice(brandProductiveDataVoltTemp.getToatlPrice());
                        brandProductivetemp.setBrandID(brandProductiveDataVoltTemp.getBrandID());
                        brandProductivetemp.setMaterialItemList(brandProductiveDataVoltTemp.getMaterialItemList());
                        brandProductivetemp.setRetailerCountList(brandProductiveDataVoltTemp.getRetailerCountList());
                        brandProductivetemp.setRetailerCount(brandProductiveDataVoltTemp.getRetailerCount());
                        brandProductivetemp.setPcValue(brandProductiveDataVoltTemp.getPcValue());
                        brandProductivetemp.setBagValue(brandProductiveDataVoltTemp.getBagValue());
                        brandProductivetemp.setCarValue(brandProductiveDataVoltTemp.getCarValue());
                        alBrandProductiveTemp.add(brandProductivetemp);
                    }
                    alBrandProductive.addAll(alBrandProductiveTemp);
                } else {
                    alBrandProductive.addAll(alBrandProductiveDatavolt);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (alBrandProductive != null && alBrandProductive.size() > 0) {
            java.util.Collections.sort(alBrandProductive, new Comparator<BrandProductiveBean>() {
                public int compare(BrandProductiveBean one, BrandProductiveBean other) {
                    BigInteger i1 = null;
                    BigInteger i2 = null;
                    try {
                        i1 = new BigInteger(one.getBrandID());
                    } catch (NumberFormatException e) {
                    }

                    try {
                        i2 = new BigInteger(other.getBrandID());
                    } catch (NumberFormatException e) {
                    }

                    if (i1 != null && i2 != null) {
                        return i1.compareTo(i2);
                    } else {
                        return one.getBrandID().compareTo(other.getBrandID());
                    }


                }
            });
        }
        Log.d("Test", "getSSSOItemDetails");
        return alBrandProductive;
    }

    private static ArrayList<String> getSSSOGuid(ArrayList<String> alVisitAct) {
        String mStrSSSoQry = "";
        String mStrGuid = "";
        ODataGuid oDataGuid = null;
        ArrayList<String> alSSS0Guid = new ArrayList<>();
        try {
            if (alVisitAct != null && alVisitAct.size() > 0) {
                for (int i = 0; i < alVisitAct.size(); i++) {
                    if (i == 0 && i == alVisitAct.size() - 1) {
                        mStrSSSoQry = mStrSSSoQry
                                + "(" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alVisitAct.get(i).toUpperCase() + "')";


                    } else if (i == 0) {
                        mStrSSSoQry = mStrSSSoQry
                                + "(" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alVisitAct.get(i).toUpperCase() + "'";


                    } else if (i == alVisitAct.size() - 1) {
                        mStrSSSoQry = mStrSSSoQry
                                + "%20or%20" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alVisitAct.get(i).toUpperCase() + "')";

                    } else {
                        mStrSSSoQry = mStrSSSoQry
                                + "%20or%20" + Constants.SSSOGuid + "%20eq%20guid'"
                                + alVisitAct.get(i).toUpperCase() + "'";
                    }


                }

                String mStSSSOlistQry = Constants.SSSOs + "?$filter=" + mStrSSSoQry;

                List<ODataEntity> entities = null;
                try {
                    entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, mStSSSOlistQry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                try {
                    ODataProperty property;
                    ODataPropMap properties;
                    if (entities != null && entities.size() > 0) {
                        for (ODataEntity oDataEntity : entities) {
                            properties = oDataEntity.getProperties();
                            property = properties.get(Constants.SSSOGuid);
                            oDataGuid = (ODataGuid) property.getValue();
                            if (oDataGuid != null) {
                                mStrGuid = oDataGuid.guidAsString36();
                                alSSS0Guid.add(mStrGuid);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Test", "getSSSODetails");
        return alSSS0Guid;
    }

    public static String getCountryCode(Context mContext) {
        String mConCode = "91";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
            mConCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso) + "";
        } catch (Exception e) {
            mConCode = "91";
            e.printStackTrace();
        }
        return mConCode;
    }

    public static String getCurrencyFromSP() {
        String mStrCurrency = "";
        try {
            mStrCurrency = OfflineManager.getValueByColumnName(Constants.SalesPersons + "?$top=1 &$select=" + Constants.Currency, Constants.Currency);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrCurrency;
    }

    public static RetailerBean getCPValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        Hashtable dbHeadTable = new Hashtable();
        RetailerBean retailerBean = new RetailerBean();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            //noinspection unchecked
            dbHeadTable.put(Constants.Address1, fetchJsonHeaderObject.getString(Constants.Address1));
            dbHeadTable.put(Constants.Address2, fetchJsonHeaderObject.getString(Constants.Address2));
            dbHeadTable.put(Constants.Address3, fetchJsonHeaderObject.getString(Constants.Address3));
            dbHeadTable.put(Constants.Address4, fetchJsonHeaderObject.getString(Constants.Address4));
            //noinspection unchecked
            dbHeadTable.put(Constants.Country, fetchJsonHeaderObject.getString(Constants.Country));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictDesc, fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictID, fetchJsonHeaderObject.getString(Constants.DistrictID));
            dbHeadTable.put(Constants.StateID, fetchJsonHeaderObject.getString(Constants.StateID));
            dbHeadTable.put(Constants.StateDesc, fetchJsonHeaderObject.getString(Constants.StateDesc));
            dbHeadTable.put(Constants.CityID, fetchJsonHeaderObject.getString(Constants.CityID));
            dbHeadTable.put(Constants.CityDesc, fetchJsonHeaderObject.getString(Constants.CityDesc));
            dbHeadTable.put(Constants.Landmark, fetchJsonHeaderObject.getString(Constants.Landmark));
            dbHeadTable.put(Constants.PostalCode, fetchJsonHeaderObject.getString(Constants.PostalCode));
            dbHeadTable.put(Constants.MobileNo, fetchJsonHeaderObject.getString(Constants.MobileNo));
            dbHeadTable.put(Constants.Mobile2, fetchJsonHeaderObject.getString(Constants.Mobile2));
            dbHeadTable.put(Constants.Landline, fetchJsonHeaderObject.getString(Constants.Landline));
            dbHeadTable.put(Constants.EmailID, fetchJsonHeaderObject.getString(Constants.EmailID));
            dbHeadTable.put(Constants.PAN, fetchJsonHeaderObject.getString(Constants.PAN));
            dbHeadTable.put(Constants.VATNo, fetchJsonHeaderObject.getString(Constants.VATNo));
            dbHeadTable.put(Constants.OutletName, fetchJsonHeaderObject.getString(Constants.OutletName));
            dbHeadTable.put(Constants.OwnerName, fetchJsonHeaderObject.getString(Constants.OwnerName));

            dbHeadTable.put(Constants.DOB, fetchJsonHeaderObject.getString(Constants.DOB));
            dbHeadTable.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            dbHeadTable.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));
            dbHeadTable.put(Constants.PartnerMgrGUID, fetchJsonHeaderObject.getString(Constants.PartnerMgrGUID));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Anniversary, fetchJsonHeaderObject.getString(Constants.Anniversary));
            dbHeadTable.put(Constants.WeeklyOff, fetchJsonHeaderObject.getString(Constants.WeeklyOff));
            dbHeadTable.put(Constants.CPUID, fetchJsonHeaderObject.getString(Constants.CPUID));
            dbHeadTable.put(Constants.TaxRegStatus, fetchJsonHeaderObject.getString(Constants.TaxRegStatus));
            dbHeadTable.put(Constants.Group4, fetchJsonHeaderObject.getString(Constants.Group4));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));

            dbHeadTable.put(Constants.RouteID, fetchJsonHeaderObject.getString(Constants.RouteID));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));

            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.ITEM_TXT, fetchJsonHeaderObject.getString(Constants.ITEM_TXT));


            retailerBean.setCPGUID(fetchJsonHeaderObject.getString(Constants.CPGUID));
            retailerBean.setAddress1(fetchJsonHeaderObject.getString(Constants.Address1));
            retailerBean.setAddress2(fetchJsonHeaderObject.getString(Constants.Address2));
            retailerBean.setAddress3(fetchJsonHeaderObject.getString(Constants.Address3));
            retailerBean.setAddress4(fetchJsonHeaderObject.getString(Constants.Address4));
            retailerBean.setCountry(fetchJsonHeaderObject.getString(Constants.Country));
            retailerBean.setDistrictDesc(fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            retailerBean.setState(fetchJsonHeaderObject.getString(Constants.StateDesc));
            retailerBean.setCity(fetchJsonHeaderObject.getString(Constants.CityDesc));
            retailerBean.setLandMark(fetchJsonHeaderObject.getString(Constants.Landmark));
            retailerBean.setPostalCode(fetchJsonHeaderObject.getString(Constants.PostalCode));
            retailerBean.setMobile1(fetchJsonHeaderObject.getString(Constants.MobileNo));

            retailerBean.setEmailId(fetchJsonHeaderObject.getString(Constants.EmailID));
            retailerBean.setOutletName(fetchJsonHeaderObject.getString(Constants.OutletName));
            retailerBean.setRetailerName(fetchJsonHeaderObject.getString(Constants.OwnerName));
            retailerBean.setLatVal(Double.parseDouble(fetchJsonHeaderObject.getString(Constants.Latitude)));
            retailerBean.setLongVal(Double.parseDouble(fetchJsonHeaderObject.getString(Constants.Longitude)));

            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
            ArrayList<HashMap<String, String>> arrtable = UtilConstants.convertToArrayListMap(itemsString);
            String group3ID = "";
            String group4ID = "";
            String weeklyOffID = "";
            if (arrtable != null && arrtable.size() > 0) {
                HashMap<String, String> mapItemVal = arrtable.get(0);
                group3ID = mapItemVal.get(Constants.Group3);
                group4ID = mapItemVal.get(Constants.Group4);


            }
            weeklyOffID = fetchJsonHeaderObject.getString(Constants.WeeklyOff);
            try {
                String CPGrup3Desc = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$select=" + Constants.Typesname + " &$filter=" + Constants.Typeset + " eq 'CPGRP3' and " + Constants.Types + " eq '" + group3ID + "'", Constants.Typesname);
                retailerBean.setGroup3Desc(CPGrup3Desc);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            try {
                String WeeklyDes = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'WeeklyOff' and " + Constants.EntityType + " eq '" + Constants.ChannelPartner + "' and " + Constants.ID + " eq '" + weeklyOffID + "'", Constants.Description);
                retailerBean.setWeeklyOffDesc(WeeklyDes);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {
                String CPGrup4Desc = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$select=" + Constants.Typesname + " &$filter=" + Constants.Typeset + " eq 'CPGRP4' and " + Constants.Types + " eq '" + group4ID + "'", Constants.Typesname);
                retailerBean.setGroup4Desc(CPGrup4Desc);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            retailerBean.setFetchJsonHeaderObject(String.valueOf(fetchJsonHeaderObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retailerBean;
    }

    public static String getAddress(JSONObject fetchJsonHeaderObject) {
        String address = "";
        try {
            if (!fetchJsonHeaderObject.getString(Constants.Address1).isEmpty()) {
                address = address + fetchJsonHeaderObject.getString(Constants.Address1);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.Address2).isEmpty()) {
                address = address + "\n" + fetchJsonHeaderObject.getString(Constants.Address2);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.Address3).isEmpty()) {
                address = address + "\n" + fetchJsonHeaderObject.getString(Constants.Address3);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.Address4).isEmpty()) {
                address = address + "\n" + fetchJsonHeaderObject.getString(Constants.Address4);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.DistrictDesc).isEmpty()) {
                address = address + "\n" + fetchJsonHeaderObject.getString(Constants.DistrictDesc);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.Country).isEmpty()) {
                address = address + "\n" + fetchJsonHeaderObject.getString(Constants.Country);
            } else {
                address = address;
            }

            if (!fetchJsonHeaderObject.getString(Constants.PostalCode).isEmpty()) {
                address = address + "-" + fetchJsonHeaderObject.getString(Constants.PostalCode);
            } else {
                address = address;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static void removeFromSharKey(String key, String error, Context context, boolean isDelete) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.NOT_POSTED_RETAILERS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isDelete) {
            editor.remove(key);
        } else {
            editor.putString(key, error);
        }
        editor.commit();
    }

    public static void closeStore(Context context) {
        try {
            UtilConstants.closeStore(context,
                    OfflineManager.options, "-100036",
                    offlineStore, Constants.PREFS_NAME, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
//            Constants.Entity_Set.clear();
//            Constants.AL_ERROR_MSG.clear();
        offlineStore = null;
        OfflineManager.options = null;
    }

    public static int quantityLength() {
        String maxStrLength = "";
        int maxLength = 0;
        try {
            maxStrLength = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SS' and Types eq 'ALWLENQTY'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            maxStrLength = "";
        }
        if (!TextUtils.isEmpty(maxStrLength)) {
            maxLength = Integer.parseInt(maxStrLength);
        } else {
            maxLength = 9;
        }
        return maxLength;
    }

    public static String getRatioScheme() {
        String ratioSheme = "";
        try {
            ratioSheme = OfflineManager.getValueByColumnName("ConfigTypsetTypeValues?$filter=Typeset eq 'SS' and Types eq 'ZRATIOSCH'", Constants.TypeValue);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ratioSheme = "";
        }
        return ratioSheme;
    }

    public static ODataDuration getTimeAsODataDurationConvertion(String timeString) {
        List<String> timeDuration = Arrays.asList(timeString.split("-"));
        int hour = Integer.parseInt((String) timeDuration.get(0));
        int minute = Integer.parseInt((String) timeDuration.get(1));
        int seconds = Integer.parseInt((String) timeDuration.get(2));
        ODataDurationDefaultImpl oDataDuration = null;

        try {
            oDataDuration = new ODataDurationDefaultImpl();
            oDataDuration.setHours(hour);
            oDataDuration.setMinutes(minute);
            oDataDuration.setSeconds(BigDecimal.valueOf((long) seconds));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return oDataDuration;
    }

    public static String getSyncHistoryddmmyyyyTimeDelay() {
        String currentDateTimeString1 = (String) android.text.format.DateFormat.format("dd/MM/yyyy", new Date());
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, 2);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String currentDateTimeString2 = dateFormat.format(date);

        String currentDateTimeString = currentDateTimeString1 + "T" + currentDateTimeString2;
        return currentDateTimeString1 + " " + currentDateTimeString2;
    }

    public static void writeDebugLog(Context mcontext, String message) {
        SharedPreferences sha = mcontext.getSharedPreferences(Constants.LOGPREFS_NAME, 0);
        if (sha.getBoolean("writeDBGLog", false)) {
            LogManager.writeLogDebug(message);
        }


    }

    public static boolean writeDebug = false;

    public static String checkUnknownNetworkerror(String errorMsg, Context mcontext) {
        String customErrorMsg = "";
        if (!TextUtils.isEmpty(errorMsg)) {
            if (errorMsg.contains("10346"))
                customErrorMsg = mcontext.getString(R.string.error_10346);
            else if (errorMsg.contains("10349"))
                customErrorMsg = mcontext.getString(R.string.error_10349);
            else if (errorMsg.contains("10348"))
                customErrorMsg = mcontext.getString(R.string.error_10348);
            else if (errorMsg.contains("10345"))
                customErrorMsg = mcontext.getString(R.string.error_10345);
            else if (errorMsg.contains("10065"))
                customErrorMsg = mcontext.getString(R.string.error_10065);
            else if (errorMsg.contains("10058"))
                customErrorMsg = mcontext.getString(R.string.error_10058);
        }
        return customErrorMsg;
    }

    public static String removeLeadingZero(String cpguid) {
        String resultData = "";
        if (!TextUtils.isEmpty(cpguid))
            resultData = cpguid.replaceFirst("^0+(?!$)", "");

        return resultData;
    }

    public static JSONObject getFeedbackJSONHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            //noinspection unchecked
            if (fetchJsonHeaderObject.has(Constants.FeebackGUID)) {
                dbHeadTable.put(Constants.FeebackGUID, fetchJsonHeaderObject.getString(Constants.FeebackGUID));
                Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.FeebackGUID).replaceAll("-", "");
            }
            //noinspection unchecked
            if (fetchJsonHeaderObject.has(Constants.Remarks)) {
                dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            }
            //noinspection unchecked
            if (fetchJsonHeaderObject.has(Constants.CPNo)) {
                dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            }
            //noinspection unchecked
            if (fetchJsonHeaderObject.has(Constants.CPGUID)) {
                dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            }

            //noinspection unchecked
            if (fetchJsonHeaderObject.has(Constants.FeedbackType)) {
                dbHeadTable.put(Constants.FeedbackType, fetchJsonHeaderObject.getString(Constants.FeedbackType));
            }
            if (fetchJsonHeaderObject.has(Constants.FeedbackTypeDesc)) {
                dbHeadTable.put(Constants.FeedbackTypeDesc, fetchJsonHeaderObject.getString(Constants.FeedbackTypeDesc));
            }

//            if(fetchJsonHeaderObject.has(Constants.LOGINID)) {
//                dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
//            }

            if (fetchJsonHeaderObject.has(Constants.CPTypeID)) {
                dbHeadTable.put(Constants.FromCPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            }

            if (fetchJsonHeaderObject.has(Constants.SPGUID)) {
                dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            }

            if (fetchJsonHeaderObject.has(Constants.SPNo)) {
                dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            }

//            if(fetchJsonHeaderObject.has(Constants.CreatedOn)) {
//                dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
//            }
//
//            if(fetchJsonHeaderObject.has(Constants.CreatedAt)) {
//                dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
//            }
            if (fetchJsonHeaderObject.has(Constants.ParentID)) {
                dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            }
            if (fetchJsonHeaderObject.has(Constants.ParentName)) {
                dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));
            }
            if (fetchJsonHeaderObject.has(Constants.ParentTypeID)) {
                dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            }
            if (fetchJsonHeaderObject.has(Constants.ParentTypDesc)) {
                dbHeadTable.put(Constants.ParentTypDesc, fetchJsonHeaderObject.getString(Constants.ParentTypDesc));
            }
            if (fetchJsonHeaderObject.has(Constants.FeedbackDate)) {
                if (!TextUtils.isEmpty(Constants.FeedbackDate)) {
                    dbHeadTable.put(Constants.FeedbackDate, fetchJsonHeaderObject.optString(Constants.FeedbackDate));
                }
            }

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.Remarks, singleRow.optString(Constants.Remarks));
                itemObject.put(Constants.FeebackGUID, singleRow.optString(Constants.FeebackGUID));
                itemObject.put(Constants.FeebackItemGUID, singleRow.optString(Constants.FeebackItemGUID));
                itemObject.put(Constants.FeedbackType, singleRow.optString(Constants.FeedbackType));
                itemObject.put(Constants.FeedbackTypeDesc, singleRow.optString(Constants.FeedbackTypeDesc));
                itemObject.put(Constants.FeedbackSubTypeID, singleRow.optString(Constants.FeedbackSubTypeID));
                itemObject.put(Constants.FeedbackSubTypeDesc, singleRow.optString(Constants.FeedbackSubTypeDesc));
                itemObject.put(Constants.ParentID, fetchJsonHeaderObject.optString(Constants.ParentID));
                itemObject.put(Constants.ParentName, fetchJsonHeaderObject.optString(Constants.ParentName));
                itemObject.put(Constants.ParentTypDesc, fetchJsonHeaderObject.optString(Constants.ParentTypDesc));
                itemObject.put(Constants.ParentTypeID, fetchJsonHeaderObject.optString(Constants.ParentTypeID));
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.FeedbackItemDetails, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getSOHeaderJSONValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            dbHeadTable.put(Constants.SSSOGuid, fetchJsonHeaderObject.getString(Constants.SSSOGuid));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.SSSOGuid).replaceAll("-", "");
            dbHeadTable.put(Constants.BeatGuid, fetchJsonHeaderObject.getString(Constants.BeatGuid));
//            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));

//            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PONo, getStartTime());
//            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
//            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypId, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
//            dbHeadTable.put(Constants.FromCPTypDs, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToId, fetchJsonHeaderObject.getString(Constants.SoldToId));
            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
//            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToType, fetchJsonHeaderObject.getString(Constants.SoldToType));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
//            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
            dbHeadTable.put(Constants.GrossAmt, fetchJsonHeaderObject.getString(Constants.GrossAmt));
//            dbHeadTable.put(Constants.NetPrice, fetchJsonHeaderObject.getString(Constants.NetPrice));
            dbHeadTable.put(Constants.NetPrice, "0");
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.Source, Constants.Mobile);
//            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
//            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));


            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {

                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                if (!singleRow.getString(Constants.IsfreeGoodsItem).equalsIgnoreCase("X")) {
                    JSONObject itemObject = new JSONObject();
                    itemObject.put(Constants.SSSOItemGUID, singleRow.get(Constants.SSSOItemGUID));
                    itemObject.put(Constants.SSSOGuid, singleRow.get(Constants.SSSOGuid));
                    itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                    itemObject.put(Constants.MaterialNo, singleRow.get(Constants.MaterialNo));
                    itemObject.put(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc));
                    itemObject.put(Constants.OrderMatGrp, singleRow.get(Constants.OrderMatGrp));
                    itemObject.put(Constants.OrderMatGrpDesc, singleRow.get(Constants.OrderMatGrpDesc));
                    itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                    itemObject.put(Constants.Uom, singleRow.get(Constants.Uom));
//                    itemObject.put(Constants.NetPrice, singleRow.get(Constants.NetPrice));
                    itemObject.put(Constants.NetPrice, "0");
                    itemObject.put(Constants.MRP, singleRow.get(Constants.MRP));
                    itemObject.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));
                    itemObject.put(Constants.Quantity, singleRow.get(Constants.Quantity));
                    itemObject.put(Constants.PriDiscount, singleRow.get(Constants.PriDiscount));
                    itemObject.put(Constants.SecDiscount, singleRow.get(Constants.SecDiscount));
                    itemObject.put(Constants.CashDiscount, singleRow.get(Constants.CashDiscount));
                    itemObject.put(Constants.PrimaryDiscountPerc, singleRow.get(Constants.PrimaryDiscountPerc));
//                    itemObject.put(Constants.SecondaryDiscountPerc, singleRow.get(Constants.SecondaryDiscountPerc));
                    itemObject.put(Constants.CashDiscountPerc, singleRow.get(Constants.CashDiscountPerc));
                    itemObject.put(Constants.TAX, "0");
                    if (!TextUtils.isEmpty(singleRow.getString(Constants.MFD)))
                        itemObject.put(Constants.MFD, singleRow.get(Constants.MFD));
                    itemObject.put(Constants.IsfreeGoodsItem, singleRow.get(Constants.IsfreeGoodsItem));
                    itemObject.put(Constants.Batch, singleRow.get(Constants.Batch));
                    itemObject.put(Constants.TransRefTypeID, singleRow.get(Constants.TransRefTypeID));
                    itemObject.put(Constants.TransRefNo, singleRow.get(Constants.TransRefNo));
                    itemObject.put(Constants.TransRefItemNo, singleRow.get(Constants.TransRefItemNo));
                    jsonArray.put(itemObject);
                }
            }
            dbHeadTable.put(Constants.SSSoItemDetails, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getCollHeaderJSONValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {

        JSONObject dbHeadTable = new JSONObject();

        try {

            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.BeatGUID))) {
                dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
            }
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.BankID))) {
                dbHeadTable.put(Constants.BankID, fetchJsonHeaderObject.getString(Constants.BankID));
                dbHeadTable.put(Constants.BankName, fetchJsonHeaderObject.getString(Constants.BankName));
            }

            dbHeadTable.put(Constants.BranchName, fetchJsonHeaderObject.getString(Constants.BranchName));
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.InstrumentNo))) {
                dbHeadTable.put(Constants.InstrumentNo, fetchJsonHeaderObject.getString(Constants.InstrumentNo));
            }
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.Remarks))) {
                dbHeadTable.put(Constants.Remarks, fetchJsonHeaderObject.getString(Constants.Remarks));
            }
            dbHeadTable.put(Constants.FIPDocType, fetchJsonHeaderObject.getString(Constants.FIPDocType));
            dbHeadTable.put(Constants.PaymentModeID, fetchJsonHeaderObject.getString(Constants.PaymentModeID));
            dbHeadTable.put(Constants.FIPDate, fetchJsonHeaderObject.getString(Constants.FIPDate) + "T00:00:00");
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.InstrumentDate))) {
                dbHeadTable.put(Constants.InstrumentDate, fetchJsonHeaderObject.getString(Constants.InstrumentDate) + "T00:00:00");
            }
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
//            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));
            dbHeadTable.put(Constants.FIPGUID, fetchJsonHeaderObject.getString(Constants.FIPGUID));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.FIPGUID).replaceAll("-", "");
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGuid));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.ParentNo, fetchJsonHeaderObject.getString(Constants.ParentNo));
            try {
                dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPFirstName, fetchJsonHeaderObject.getString(Constants.SPFirstName));
//            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
//            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.Source, /*fetchJsonHeaderObject.getString(*/Constants.Mobile)/*)*/;
            dbHeadTable.put(Constants.DMSDivision, fetchJsonHeaderObject.getString(Constants.DMSDivision));

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {

                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.FIPItemGUID, singleRow.optString(Constants.FIPItemGUID));
                itemObject.put(Constants.FIPGUID, singleRow.optString(Constants.FIPGUID));
                itemObject.put(Constants.ReferenceTypeID, singleRow.optString(Constants.ReferenceTypeID));
                itemObject.put(Constants.FIPItemNo, String.valueOf(incrementVal + 1));
                if (!singleRow.optString(Constants.ReferenceID).equalsIgnoreCase("")) {
                    itemObject.put(Constants.FIPAmount, singleRow.optString(Constants.FIPAmount));
                    itemObject.put(Constants.ReferenceID, singleRow.optString(Constants.ReferenceID).toUpperCase());
                    itemObject.put(Constants.Amount, singleRow.optString(Constants.Amount));
                    if (singleRow.optString(Constants.CashDiscountPercentage) != null && !TextUtils.isEmpty(singleRow.optString(Constants.CashDiscountPercentage))) {
                        itemObject.put(Constants.CashDiscountPercentage, new BigDecimal(singleRow.optString(Constants.CashDiscountPercentage)));
                    }
//                    itemObject.put(Constants.CashDiscount, singleRow.optString(Constants.CashDiscount));
                    if (singleRow.optString(Constants.CashDiscountPercentage) != null && !TextUtils.isEmpty(singleRow.optString(Constants.CashDiscountPercentage))) {
                        itemObject.put(Constants.CashDiscountPercentage, singleRow.optString(Constants.CashDiscountPercentage));
                    }
                    if (singleRow.optString(Constants.CashDiscount) != null && !TextUtils.isEmpty(singleRow.optString(Constants.CashDiscount))) {
                        itemObject.put(Constants.CashDiscount, singleRow.optString(Constants.CashDiscount));
                    }
                } else {
                    itemObject.put(Constants.FIPAmount, singleRow.optString(Constants.FIPAmount));
                }
                if (!TextUtils.isEmpty(singleRow.optString(Constants.InstrumentDate))) {
                    itemObject.put(Constants.InstrumentDate, singleRow.optString(Constants.InstrumentDate));
                }
                itemObject.put(Constants.Currency, singleRow.optString(Constants.Currency));
                itemObject.put(Constants.InstrumentNo, singleRow.optString(Constants.InstrumentNo));
                itemObject.put(Constants.PaymentMode, singleRow.optString(Constants.PaymentModeID));
                itemObject.put(Constants.PaymetModeDesc, singleRow.optString(Constants.PaymetModeDesc));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.BeatGUID))) {
                    itemObject.put(Constants.BeatGUID, singleRow.optString(Constants.BeatGUID));
                }
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.FinancialPostingItemDetails, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getSSInvoiceJSONHeaderValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {
            try {
                if (fetchJsonHeaderObject.has(Constants.BeatGUID) && !TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.BeatGUID))) {
                    dbHeadTable.put(Constants.BeatGUID, fetchJsonHeaderObject.getString(Constants.BeatGUID));
                } else {
                    dbHeadTable.put(Constants.BeatGUID, "00000000-0000-0000-0000-000000000000");
                }
            } catch (JSONException e) {
                dbHeadTable.put(Constants.BeatGUID, "00000000-0000-0000-0000-000000000000");
            }
            dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.InvoiceGUID).replaceAll("-", "");
//            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.InvoiceNo, fetchJsonHeaderObject.getString(Constants.InvoiceNo));
            dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
            dbHeadTable.put(Constants.InvoiceTypeDesc, fetchJsonHeaderObject.getString(Constants.InvoiceTypeDesc));
            dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
            dbHeadTable.put(Constants.PONo, fetchJsonHeaderObject.getString(Constants.PONo));
            dbHeadTable.put(Constants.PODate, fetchJsonHeaderObject.getString(Constants.PODate));
            dbHeadTable.put(Constants.BillToGuid, fetchJsonHeaderObject.getString(Constants.BillToGuid));
            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
            dbHeadTable.put(Constants.SoldToName, fetchJsonHeaderObject.getString(Constants.SoldToName));
            dbHeadTable.put(Constants.ShipToCPGUID, fetchJsonHeaderObject.getString(Constants.ShipToCPGUID));
            dbHeadTable.put(Constants.Source, fetchJsonHeaderObject.getString(Constants.Source));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypeDesc, fetchJsonHeaderObject.getString(Constants.SoldToTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));
//            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.InvoiceItemGUID, singleRow.get(Constants.InvoiceItemGUID));
                itemObject.put(Constants.InvoiceGUID, singleRow.get(Constants.InvoiceGUID));
                itemObject.put(Constants.StockGuid, singleRow.get(Constants.StockGuid));
                itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                itemObject.put(Constants.InvoiceNo, singleRow.get(Constants.InvoiceNo));
                itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                itemObject.put(Constants.Quantity, singleRow.get(Constants.Quantity));
                itemObject.put(Constants.MaterialNo, singleRow.get(Constants.MaterialNo));
                itemObject.put(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc));
                itemObject.put(Constants.UOM, singleRow.get(Constants.UOM));
                itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                itemObject.put(Constants.InvoiceDate, singleRow.get(Constants.InvoiceDate));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.BeatGUID))) {
                    itemObject.put(Constants.BeatGUID, singleRow.get(Constants.BeatGUID));
                }
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.SSInvoiceItemDetails, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getCPHeaderJSONValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            //noinspection unchecked
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.CPGUID).replaceAll("-", "");
            //noinspection unchecked
            dbHeadTable.put(Constants.Address1, fetchJsonHeaderObject.getString(Constants.Address1));
            dbHeadTable.put(Constants.Address2, fetchJsonHeaderObject.getString(Constants.Address2));
            dbHeadTable.put(Constants.Address3, fetchJsonHeaderObject.getString(Constants.Address3));
            dbHeadTable.put(Constants.Address4, fetchJsonHeaderObject.getString(Constants.Address4));
            //noinspection unchecked
            dbHeadTable.put(Constants.Country, fetchJsonHeaderObject.getString(Constants.Country));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictDesc, fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            //noinspection unchecked
            dbHeadTable.put(Constants.DistrictID, fetchJsonHeaderObject.getString(Constants.DistrictID));
            dbHeadTable.put(Constants.StateID, fetchJsonHeaderObject.getString(Constants.StateID));
            dbHeadTable.put(Constants.StateDesc, fetchJsonHeaderObject.getString(Constants.StateDesc));
            dbHeadTable.put(Constants.CityID, fetchJsonHeaderObject.getString(Constants.CityID));
            dbHeadTable.put(Constants.CityDesc, fetchJsonHeaderObject.getString(Constants.CityDesc));
            dbHeadTable.put(Constants.Landmark, fetchJsonHeaderObject.getString(Constants.Landmark));
            dbHeadTable.put(Constants.PostalCode, fetchJsonHeaderObject.getString(Constants.PostalCode));
            dbHeadTable.put(Constants.MobileNo, fetchJsonHeaderObject.getString(Constants.MobileNo));
            dbHeadTable.put(Constants.Mobile2, fetchJsonHeaderObject.getString(Constants.Mobile2));
            dbHeadTable.put(Constants.Landline, fetchJsonHeaderObject.getString(Constants.Landline));
            dbHeadTable.put(Constants.EmailID, fetchJsonHeaderObject.getString(Constants.EmailID));
            dbHeadTable.put(Constants.PAN, fetchJsonHeaderObject.getString(Constants.PAN));
            dbHeadTable.put(Constants.VATNo, fetchJsonHeaderObject.getString(Constants.VATNo));
            dbHeadTable.put(Constants.OutletName, fetchJsonHeaderObject.getString(Constants.OutletName));
            dbHeadTable.put(Constants.OwnerName, fetchJsonHeaderObject.getString(Constants.OwnerName));

            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.DOB))) {
                dbHeadTable.put(Constants.DOB, fetchJsonHeaderObject.getString(Constants.DOB) + "T00:00:00");
            }
            try {
                dbHeadTable.put(Constants.Source, Constants.Mobile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.optString(Constants.Latitude))) {
                dbHeadTable.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            }
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.optString(Constants.Longitude))) {
                dbHeadTable.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));
            }
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.optString(Constants.PartnerMgrGUID))) {
                dbHeadTable.put(Constants.PartnerMgrGUID, fetchJsonHeaderObject.getString(Constants.PartnerMgrGUID));
            }
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.optString(Constants.Anniversary))) {
                dbHeadTable.put(Constants.Anniversary, fetchJsonHeaderObject.getString(Constants.Anniversary) + "T00:00:00");
            }
            dbHeadTable.put(Constants.WeeklyOff, fetchJsonHeaderObject.getString(Constants.WeeklyOff));
            dbHeadTable.put(Constants.CPUID, fetchJsonHeaderObject.getString(Constants.CPUID));
            dbHeadTable.put(Constants.TaxRegStatus, fetchJsonHeaderObject.getString(Constants.TaxRegStatus));
//            dbHeadTable.put(Constants.Group4, fetchJsonHeaderObject.getString(Constants.Group4));
//            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

//            dbHeadTable.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
//            dbHeadTable.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
//            dbHeadTable.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
//            dbHeadTable.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
//            dbHeadTable.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
//            dbHeadTable.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
//            dbHeadTable.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
//            dbHeadTable.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));
//            dbHeadTable.put(Constants.IsKeyCP, fetchJsonHeaderObject.getString(Constants.IsKeyCP));

            dbHeadTable.put(Constants.RouteID, fetchJsonHeaderObject.getString(Constants.RouteID));
            dbHeadTable.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            dbHeadTable.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            dbHeadTable.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));

            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
//            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
//            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));


            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.CPGUID, fetchJsonHeaderObject.optString(Constants.CPGUID));
                itemObject.put(Constants.CP1GUID, singleRow.get(Constants.CP1GUID));
                try {
                    itemObject.put(Constants.PartnerMgrGUID, singleRow.get(Constants.PartnerMgrGUID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(singleRow.optString(Constants.RouteGUID))) {
                    itemObject.put(Constants.RouteGUID, singleRow.optString(Constants.RouteGUID));
                }
                itemObject.put(Constants.DMSDivision, singleRow.get(Constants.DMSDivision));
                itemObject.put(Constants.StatusID, singleRow.get(Constants.StatusID));
                itemObject.put(Constants.PartnerMgrNo, singleRow.get(Constants.PartnerMgrNo));
                itemObject.put(Constants.ParentID, singleRow.get(Constants.ParentID));
                itemObject.put(Constants.ParentTypeID, singleRow.get(Constants.ParentTypeID));
                itemObject.put(Constants.ParentName, singleRow.get(Constants.ParentName));
                itemObject.put(Constants.Group1, singleRow.get(Constants.Group1));
                itemObject.put(Constants.Group2, singleRow.get(Constants.Group2));
                itemObject.put(Constants.Group3, singleRow.get(Constants.Group3));
                itemObject.put(Constants.Group4, singleRow.get(Constants.Group4));
                itemObject.put(Constants.Group5, singleRow.get(Constants.Group5));
                itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                itemObject.put(Constants.RouteID, singleRow.get(Constants.RouteID));
                itemObject.put(Constants.RouteDesc, singleRow.get(Constants.RouteDesc));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.DiscountPer)))
                    itemObject.put(Constants.DiscountPer, singleRow.optString(Constants.DiscountPer));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.CreditLimit)))
                    itemObject.put(Constants.CreditLimit, singleRow.get(Constants.CreditLimit));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.CreditDays)))
                    itemObject.put(Constants.CreditLimit, singleRow.get(Constants.CreditDays));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.CreditBills)))
                    itemObject.put(Constants.CreditLimit, singleRow.get(Constants.CreditBills));
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.CPDMSDivisions, jsonArray);

            //hardcoded partner function
            jsonArray = new JSONArray();
            JSONObject itemObject = new JSONObject();
            itemObject.put(Constants.CPGUID, fetchJsonHeaderObject.optString(Constants.CPGUID));
            itemObject.put(Constants.PFGUID, GUID.newRandom().toString36().toUpperCase());
            itemObject.put(Constants.PartnerFunction, "01");
            itemObject.put(Constants.PartnarName, fetchJsonHeaderObject.optString(Constants.OutletName));
            itemObject.put(Constants.PartnerMobileNo, fetchJsonHeaderObject.optString(Constants.MobileNo));
            itemObject.put(Constants.PartnarCPGUID, fetchJsonHeaderObject.optString(Constants.CPGUID).replace("-", "").toUpperCase());
            itemObject.put(Constants.StatusID, Constants.str_01);
            jsonArray.put(itemObject);
            itemObject = new JSONObject();
            itemObject.put(Constants.CPGUID, fetchJsonHeaderObject.optString(Constants.CPGUID));
            itemObject.put(Constants.PFGUID, GUID.newRandom().toString36().toUpperCase());
            itemObject.put(Constants.PartnerFunction, "02");
            itemObject.put(Constants.PartnarName, fetchJsonHeaderObject.optString(Constants.OutletName));
            itemObject.put(Constants.PartnerMobileNo, fetchJsonHeaderObject.optString(Constants.MobileNo));
            itemObject.put(Constants.StatusID, Constants.str_01);
            itemObject.put(Constants.PartnarCPGUID, fetchJsonHeaderObject.optString(Constants.CPGUID).replace("-", "").toUpperCase());
            jsonArray.put(itemObject);
            dbHeadTable.put(Constants.CPPartnerFunctions, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getROHeaderJSONValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            dbHeadTable.put(Constants.SSROGUID, fetchJsonHeaderObject.getString(Constants.SSROGUID));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.SSROGUID).replaceAll("-", "");
            /*try {
                dbHeadTable.put(Constants.BeatGuid, fetchJsonHeaderObject.getString(Constants.BeatGuid));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
//            dbHeadTable.put(Constants.OrderNo, fetchJsonHeaderObject.getString(Constants.OrderNo));
            dbHeadTable.put(Constants.OrderType, fetchJsonHeaderObject.getString(Constants.OrderType));
            dbHeadTable.put(Constants.OrderDate, fetchJsonHeaderObject.getString(Constants.OrderDate));
            dbHeadTable.put(Constants.OrderTypeDesc, fetchJsonHeaderObject.getString(Constants.OrderTypeDesc));
           /* if(fetchJsonHeaderObject.getString(Constants.DmsDivision)!=null && !TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.DmsDivision)))
                dbHeadTable.put(Constants.DmsDivision, fetchJsonHeaderObject.getString(Constants.DmsDivision));*/
//            dbHeadTable.put(Constants.DmsDivisionDesc, fetchJsonHeaderObject.getString(Constants.DmsDivisionDesc));
            dbHeadTable.put(Constants.FromCPGUID, fetchJsonHeaderObject.getString(Constants.FromCPGUID));
//            dbHeadTable.put(Constants.FromCPNo, fetchJsonHeaderObject.getString(Constants.FromCPNo));
            dbHeadTable.put(Constants.FromCPName, fetchJsonHeaderObject.getString(Constants.FromCPName));
            dbHeadTable.put(Constants.FromCPTypID, fetchJsonHeaderObject.getString(Constants.FromCPTypId));
            dbHeadTable.put(Constants.FromCPTypeDesc, fetchJsonHeaderObject.getString(Constants.FromCPTypDs));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToId));
//            dbHeadTable.put(Constants.SoldToUID, fetchJsonHeaderObject.getString(Constants.SoldToUID));
            dbHeadTable.put(Constants.SoldToDesc, fetchJsonHeaderObject.getString(Constants.SoldToDesc));
            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
            dbHeadTable.put(Constants.SoldToTypDs, fetchJsonHeaderObject.getString(Constants.SoldToTypDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.FirstName, fetchJsonHeaderObject.getString(Constants.FirstName));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
//            dbHeadTable.put(Constants.CreatedOn, fetchJsonHeaderObject.getString(Constants.CreatedOn));
//            dbHeadTable.put(Constants.CreatedAt, fetchJsonHeaderObject.getString(Constants.CreatedAt));
            dbHeadTable.put(Constants.StatusID, fetchJsonHeaderObject.getString(Constants.StatusID));
            dbHeadTable.put(Constants.ApprovalStatusID, fetchJsonHeaderObject.getString(Constants.ApprovalStatusID));
            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));
//            dbHeadTable.put(Constants.LOGINID, fetchJsonHeaderObject.getString(Constants.LOGINID));


            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.SSROItemGUID, singleRow.get(Constants.SSROItemGUID));
                itemObject.put(Constants.SSROGUID, singleRow.get(Constants.SSROGUID));
                try {
                    itemObject.put(Constants.StockRefGUID, singleRow.get(Constants.StockRefGUID));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                itemObject.put(Constants.ItemNo, singleRow.get(Constants.ItemNo));
                itemObject.put(Constants.MaterialNo, singleRow.get(Constants.MaterialNo));
                itemObject.put(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc));
                try {
                    itemObject.put(Constants.OrdMatGrp, singleRow.get(Constants.OrdMatGrp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    itemObject.put(Constants.OrdMatGrpDesc, singleRow.get(Constants.OrdMatGrpDesc));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemObject.put(Constants.Quantity, singleRow.get(Constants.Quantity));
                itemObject.put(Constants.MRP, singleRow.get(Constants.MRP));
                itemObject.put(Constants.UnitPrice, singleRow.get(Constants.UnitPrice));
                itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                try {
                    itemObject.put(Constants.UOM, singleRow.get(Constants.Uom));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemObject.put(Constants.Batch, singleRow.get(Constants.Batch));
                itemObject.put(Constants.RejectionReasonID, singleRow.get(Constants.RejectionReasonID));
                itemObject.put(Constants.RefDocNo, singleRow.get(Constants.RefDocNo));
                itemObject.put(Constants.RejectionReasonDesc, singleRow.get(Constants.RejectionReasonDesc));
                itemObject.put(Constants.RefdocItmGUID, singleRow.get(Constants.RefdocItmGUID));
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.SSROItemDetails, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static JSONObject getExpenseHeaderJSONValuesFromJsonObject(JSONObject fetchJsonHeaderObject) {
        JSONObject dbHeadTable = new JSONObject();
        try {

            dbHeadTable.put(Constants.ExpenseGUID, fetchJsonHeaderObject.getString(Constants.ExpenseGUID));
            Constants.REPEATABLE_REQUEST_ID = fetchJsonHeaderObject.getString(Constants.ExpenseGUID).replaceAll("-", "");
//            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
            dbHeadTable.put(Constants.ExpenseNo, fetchJsonHeaderObject.getString(Constants.ExpenseNo));
            dbHeadTable.put(Constants.FiscalYear, fetchJsonHeaderObject.getString(Constants.FiscalYear));
            dbHeadTable.put(Constants.CPName, fetchJsonHeaderObject.getString(Constants.CPName));
            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
            dbHeadTable.put(Constants.CPType, fetchJsonHeaderObject.getString(Constants.CPType));
            dbHeadTable.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));
            dbHeadTable.put(Constants.SPGUID, fetchJsonHeaderObject.getString(Constants.SPGUID));
            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));
            dbHeadTable.put(Constants.SPName, fetchJsonHeaderObject.getString(Constants.SPName));
            dbHeadTable.put(Constants.ExpenseType, fetchJsonHeaderObject.getString(Constants.ExpenseType));

            dbHeadTable.put(Constants.ExpenseTypeDesc, fetchJsonHeaderObject.getString(Constants.ExpenseTypeDesc));
            if (!TextUtils.isEmpty(fetchJsonHeaderObject.getString(Constants.ExpenseDate))) {
                dbHeadTable.put(Constants.ExpenseDate, fetchJsonHeaderObject.getString(Constants.ExpenseDate));
            }
            dbHeadTable.put(Constants.Status, fetchJsonHeaderObject.getString(Constants.Status));
            dbHeadTable.put(Constants.StatusDesc, fetchJsonHeaderObject.getString(Constants.StatusDesc));
            dbHeadTable.put(Constants.Amount, fetchJsonHeaderObject.getString(Constants.Amount));
            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

            JSONArray itemsArray = new JSONArray(fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            JSONArray jsonArray = new JSONArray();
            for (int incrementVal = 0; incrementVal < itemsArray.length(); incrementVal++) {
                JSONObject singleRow = itemsArray.getJSONObject(incrementVal);
                JSONObject itemObject = new JSONObject();
                itemObject.put(Constants.ExpenseItemGUID, singleRow.get(Constants.ExpenseItemGUID));
                itemObject.put(Constants.ExpenseGUID, singleRow.get(Constants.ExpenseGUID));
                itemObject.put(Constants.ExpeseItemNo, singleRow.get(Constants.ExpeseItemNo));
                itemObject.put(Constants.ExpenseItemType, singleRow.get(Constants.ExpenseItemType));
                itemObject.put(Constants.ExpenseItemTypeDesc, singleRow.get(Constants.ExpenseItemTypeDesc));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.BeatGUID))) {
                    itemObject.put(Constants.BeatGUID, singleRow.get(Constants.BeatGUID));
                }
                itemObject.put(Constants.Location, singleRow.get(Constants.Location));
//                itemObject.put(Constants.ExpenseDate, singleRow.get(Constants.ExpenseDate));
//                itemObject.put(Constants.FiscalYear, fetchJsonHeaderObject.getString(Constants.FiscalYear));
//                itemObject.put(Constants.Month, fetchJsonHeaderObject.getString(Constants.Month));
                if (!TextUtils.isEmpty(singleRow.optString(Constants.ConvenyanceMode))) {
                    itemObject.put(Constants.ConvenyanceMode, singleRow.get(Constants.ConvenyanceMode));
                    itemObject.put(Constants.ConvenyanceModeDs, singleRow.get(Constants.ConvenyanceModeDs));
                }

                if (!TextUtils.isEmpty(singleRow.optString(Constants.BeatDistance))) {
                    itemObject.put(Constants.BeatDistance, singleRow.optString(Constants.BeatDistance));
                }
                if (!TextUtils.isEmpty(singleRow.optString(Constants.Amount))) {
                    itemObject.put(Constants.Amount, singleRow.get(Constants.Amount));
                }
                itemObject.put(Constants.UOM, singleRow.get(Constants.UOM));
                itemObject.put(Constants.Currency, singleRow.get(Constants.Currency));
                itemObject.put(Constants.Remarks, singleRow.get(Constants.Remarks));
                jsonArray.put(itemObject);
            }
            dbHeadTable.put(Constants.ExpenseItemDetails, jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dbHeadTable;
    }

    public static void passwordStatusErrorMessage(final Context context, final JSONObject jsonObject, String loginUser) {
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    String buttonMSG = "";
                    String errortext = "";
                    int code = 0;
                    if (jsonObject != null) {
                        if (jsonObject.has("code")) {
                            code = jsonObject.optInt("code");
                        }

                        if (jsonObject.has("message")) {
                            message = jsonObject.optString("message");
                        }
                        if (code != 200 && code != 0) {
                            if (message.equalsIgnoreCase(Constants.PASSWORD_LOCKED)) {
                                errortext = context.getString(R.string.password_lock_error_message);
//                                com.arteriatech.mutils.common.UtilConstants.getUserlockMessage(context,loginUser, Configuration.IDPURL,Configuration.IDPTUSRNAME,Configuration.IDPTUSRPWD,Configuration.APP_ID);
                            } else if (message.equalsIgnoreCase(Constants.USER_INACTIVE)) {
                                errortext = context.getString(R.string.user_inactive_error_message);
                            } else if (message.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED) || message.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED)) {
                                errortext = context.getString(R.string.password_change_error_message);
                            } else if (message.equalsIgnoreCase(Constants.PASSWORD_DISABLED)) {
                                errortext = context.getString(R.string.password_disable_error_message);
                            } else {
                                errortext = context.getString(R.string.unauthorized_error_message);
                            }

                            if (message.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED) || message.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED)) {
                                buttonMSG = context.getString(R.string.settings_extend_password);
                            } else {
                                buttonMSG = context.getString(R.string.ok);
                            }
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.UtilsDialogTheme);
                                builder.setCancelable(false);

                                final String finalMessage = message;
                                builder.setMessage(errortext).setCancelable(false).setPositiveButton(buttonMSG, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (finalMessage.equalsIgnoreCase(Constants.PASSWORD_RESET_REQUIRED) || finalMessage.equalsIgnoreCase(Constants.PASSWORD_CHANGE_REQUIRED)) {
                                            SharedPreferences sharedPerf = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                                            String userName = sharedPerf.getString("username", "");
                                            Intent intentSetting = new Intent(context, SecuritySettingActivity.class);
                                            RegistrationModel registrationModel = new RegistrationModel();
                                            registrationModel.setExtenndPwdReq(true);
                                            registrationModel.setUpdateAsPortalPwdReq(true);
                                            registrationModel.setIDPURL(Configuration.IDPURL);
                                            registrationModel.setUserName(userName);
                                            registrationModel.setShredPrefKey(Constants.PREFS_NAME);
                                            registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
                                            registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
                                            intentSetting.putExtra(UtilConstants.RegIntentKey, registrationModel);
                                            context.startActivity(intentSetting);
                                        }
                                    }
                                });
                                builder.show();
                            } catch (Exception var8) {
                                var8.printStackTrace();
                            }
                        }
                    }
                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getErrorMessage(IReceiveEvent event, Context context) {
        String errorMsg = "";

        try {
            if (event.getReader() != null) {
                String responseBody = IReceiveEvent.Util.getResponseBody(event.getReader());
                if (event.getResponseStatusCode() != 401) {
                    try {
                        if (!responseBody.contains("html")) {
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(responseBody);
                                JSONObject errorObject = jsonObject.getJSONObject("error");
                                JSONObject erMesgObject = errorObject.getJSONObject("message");
                                errorMsg = erMesgObject.optString("value");
                            } catch (JSONException var7) {
                                var7.printStackTrace();
                                errorMsg = var7.getMessage();
                            }
                        } else {
                            errorMsg = responseBody;
                        }
                    } catch (Exception var8) {
                        var8.printStackTrace();
                        errorMsg = var8.getMessage();
                    }
                } else {
                    errorMsg = responseBody;
                }
            } else {
                errorMsg = context.getString(R.string.error_bad_req);
            }
        } catch (Throwable var9) {
            errorMsg = var9.getMessage();
            var9.printStackTrace();
        }

        return errorMsg;
    }

}
