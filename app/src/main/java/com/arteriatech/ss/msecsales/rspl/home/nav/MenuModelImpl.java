package com.arteriatech.ss.msecsales.rspl.home.nav;

import android.content.Context;
import android.content.SharedPreferences;

import com.arteriatech.mutils.registration.MainMenuBean;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;

import java.util.ArrayList;

public class MenuModelImpl implements MenuModel {

    @Override
    public void findItems(Context mContext, OnFinishedListener listener, int viewType) {
        ArrayList<MainMenuBean> mainMenuBeenList = new ArrayList<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        MainMenuBean mainMenuBean;
        if (viewType == 1) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName("Home");
            mainMenuBean.setId(1);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_home_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isAdhocVisitEnabled, "").equalsIgnoreCase(Constants.isAdhocVistTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_un_plan_adhoc));
            mainMenuBean.setId(2);
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setItemType("Today");
            mainMenuBean.setMenuImage(R.drawable.ic_adhoc_visit_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isMyTargetsEnabled, "").equalsIgnoreCase(Constants.isMyTargetsTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_my_targets));
            mainMenuBean.setId(3);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isRouteEnabled, "").equalsIgnoreCase(Constants.isRoutePlaneTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_beat_plan));
            mainMenuBean.setId(4);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_place_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
     /* if (sharedPreferences.getString(Constants.isDBStockEnabled, "").equalsIgnoreCase(Constants.isDBStockTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_db_stock));
            mainMenuBean.setId(4);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }*/
        if (sharedPreferences.getString(Constants.isBehaviourEnabled, "").equalsIgnoreCase(Constants.isBehaviourTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_behaviour));
            mainMenuBean.setId(5);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isDBStockEnabled, "").equalsIgnoreCase(Constants.isDBStockTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.db_stock));
            mainMenuBean.setId(6);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_shopping_basket_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
       /*    if (sharedPreferences.getString(Constants.isSchemeEnabled, "").equalsIgnoreCase(Constants.isSchemeTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_schemes));
            mainMenuBean.setId(6);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isVisualAidEnabled, "").equalsIgnoreCase(Constants.isVisualAidTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_visual_aid));
            mainMenuBean.setId(7);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }
        if (sharedPreferences.getString(Constants.isDigitalProductEntryEnabled, "").equalsIgnoreCase(Constants.isDigitalProductEntryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_digital_product));
            mainMenuBean.setId(8);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }*/

        if (sharedPreferences.getString(Constants.isRetailerListEnabled, "").equalsIgnoreCase(Constants.isRetailerListTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.menu_retailers));
            mainMenuBean.setId(9);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_store_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isVisualAidEnabled, "").equalsIgnoreCase(Constants.isVisualAidTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.visual_aidl_title));
            mainMenuBean.setId(10);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_visibility_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isDigitalProductEntryEnabled, "").equalsIgnoreCase(Constants.isDigitalProductEntryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.digital_product_title));
            mainMenuBean.setId(11);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_photo_album_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isVisitSummaryKey, "").equalsIgnoreCase(Constants.isVisitSummaryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_visit_summary));
            mainMenuBean.setId(12);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_my_targets_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isSchemeEnabled, "").equalsIgnoreCase(Constants.isSchemeTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_schemes));
            mainMenuBean.setId(13);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_card_giftcard_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isCreateRetailerKey, "").equalsIgnoreCase(Constants.isCreateRetailerTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_retailer_create));
            mainMenuBean.setId(14);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_person_add_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }

        if (sharedPreferences.getString(Constants.isExpenseEntryKey, "").equalsIgnoreCase(Constants.isExpenseEntryTcode)) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_expense_entry));
            mainMenuBean.setId(20);
            mainMenuBean.setItemType("Today");
            mainMenuBean.setTitleFlag("app");
            mainMenuBean.setMenuImage(R.drawable.ic_attach_money_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
        }


        if (sharedPreferences.getString(Constants.isExpenseListKey, "").equalsIgnoreCase(Constants.isExpenseListTcode))
            {
                mainMenuBean = new MainMenuBean();
                mainMenuBean.setMenuName(mContext.getString(R.string.title_expense_list));
                mainMenuBean.setId(21);
                mainMenuBean.setItemType("Today");
                mainMenuBean.setTitleFlag("app");
                mainMenuBean.setMenuImage(R.drawable.ic_attach_money_black_24dp);
                mainMenuBeenList.add(mainMenuBean);

            }

        if (sharedPreferences.getString(Constants.isisVSTSUMKey, "").equalsIgnoreCase(Constants.isVSTSUMTcode))
            {
                mainMenuBean = new MainMenuBean();
                mainMenuBean.setMenuName(mContext.getString(R.string.title_visit_summary_report_list));
                mainMenuBean.setId(22);
                mainMenuBean.setItemType("Today");
                mainMenuBean.setTitleFlag("app");
                mainMenuBean.setMenuImage(R.drawable.start);
                mainMenuBeenList.add(mainMenuBean);

            }
        if (viewType == 1) {
            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.sync_menu));
            mainMenuBean.setId(16);
            mainMenuBean.setItemType("Admin");
            mainMenuBean.setMenuImage(R.drawable.ic_sync_black_24dp);
            mainMenuBeenList.add(mainMenuBean);

            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.settings));
            mainMenuBean.setId(18);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_settings_black_24dp);
            mainMenuBeenList.add(mainMenuBean);

            mainMenuBean = new MainMenuBean();
            mainMenuBean.setMenuName(mContext.getString(R.string.title_support));
            mainMenuBean.setId(19);
            mainMenuBean.setItemType("");
            mainMenuBean.setTitleFlag("");
            mainMenuBean.setMenuImage(R.drawable.ic_help_black_24dp);
            mainMenuBeenList.add(mainMenuBean);
            listener.onFinished(mainMenuBeenList);
        }
    }
}
