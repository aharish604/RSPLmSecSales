package com.arteriatech.ss.msecsales.rspl.registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.login.LoginActivity;
import com.arteriatech.mutils.login.PasscodeLoginActivity;
import com.arteriatech.mutils.registration.MainMenuBean;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.security.AppLockManager;
import com.arteriatech.mutils.support.PasscodeActivity;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.WaitTillStoreOpenActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.MSFAApplication;
import com.arteriatech.ss.msecsales.rspl.log.LogActivity;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {
    private boolean isNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegistrationModel registrationModel = new RegistrationModel();
        Intent intentExtra = getIntent();

        registrationModel.setAppID(Configuration.APP_ID);
        registrationModel.setHttps(Configuration.IS_HTTPS);
        registrationModel.setPort(Configuration.port_Text);
        registrationModel.setSecConfig(Configuration.secConfig_Text);
        registrationModel.setServerText(Configuration.server_Text);
        registrationModel.setShredPrefKey(Constants.PREFS_NAME);
        registrationModel.setFormID(Configuration.farm_ID);
        registrationModel.setSuffix(Configuration.suffix);

        registrationModel.setDataVaultFileName(Constants.DataVaultFileName);
        registrationModel.setOfflineDBPath(Constants.offlineDBPath);
        registrationModel.setOfflineReqDBPath(Constants.offlineReqDBPath);
        registrationModel.setIcurrentUDBPath(Constants.icurrentUDBPath);
        registrationModel.setIbackupUDBPath(Constants.ibackupUDBPath);
        registrationModel.setIcurrentRqDBPath(Constants.icurrentRqDBPath);
        registrationModel.setIbackupRqDBPath(Constants.ibackupRqDBPath);

        registrationModel.setLogInActivity(LoginActivity.class);
        registrationModel.setAppActionBarIcon(R.mipmap.ic_action_bar_logo);
        registrationModel.setAppLogo(R.drawable.arteria_new_logo_transparent);
        registrationModel.setAppVersionName(BuildConfig.VERSION_NAME);
        registrationModel.setAppName(getString(R.string.app_name));
        registrationModel.setEmainId(getString(R.string.register_support_email));
        registrationModel.setPhoneNo(getString(R.string.register_support_phone));
        registrationModel.setEmailSubject("");//getString(R.string.email_subject)
        registrationModel.setMainActivity(WaitTillStoreOpenActivity.class);
        registrationModel.setRegisterActivity(RegistrationActivity.class);
        registrationModel.setRegisterSuccessActivity(PasscodeActivity.class);
        registrationModel.setIDPURL(Configuration.IDPURL);
        registrationModel.setExternalTUserName(Configuration.IDPTUSRNAME);
        registrationModel.setExternalTPWD(Configuration.IDPTUSRPWD);
        /*security passcode*/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.getString(UtilConstants.QUICK_PIN, "");
        String permission = sharedPreferences.getString(UtilConstants.QUICK_PIN_ACCESS, "");
        String enablePermission = sharedPreferences.getString(UtilConstants.ENABLE_ACCESS, "");
        if (UtilConstants.SECURITY_ON.equalsIgnoreCase(permission) && UtilConstants.SECURITY_ON.equalsIgnoreCase(enablePermission)) {
            registrationModel.setPasscodeLoginActivity(PasscodeLoginActivity.class);
        }
        ArrayList<MainMenuBean> mainMenuBeanArrayList = new ArrayList<>();
        MainMenuBean mainMenuBean = new MainMenuBean();
        mainMenuBean.setActivityRedirect(LogActivity.class);
        mainMenuBean.setMenuImage(0);
        mainMenuBean.setMenuName("View");
        mainMenuBeanArrayList.add(mainMenuBean);
        registrationModel.setMenuBeen(mainMenuBeanArrayList);
//        registrationModel.setMainMenuActivity(LoginActivity.class);

        MSFAApplication msfaApplication = (MSFAApplication) getApplication();
        if (intentExtra != null) {
            if (intentExtra.getAction() != null && intentExtra.getAction().equalsIgnoreCase(BuildConfig.APPLICATION_ID)) {
                registrationModel.setBackToStartActivity(true);
                registrationModel.setLoginSuccessActivity(WaitTillStoreOpenActivity.class);
            }
        }
        sharedPreferences = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        String strPref = sharedPreferences.getString("username", (String) null);
        try {
            if (TextUtils.isEmpty(strPref)) {
                LogManager.writeLogInfo("Registration -UserName Empty");
                String userNameExtra = sharedPreferences.getString("usernameExtra", "");
                if (!TextUtils.isEmpty(userNameExtra)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", userNameExtra);
                    editor.apply();
                    strPref = sharedPreferences.getString("username",(String)null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(strPref)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                editor.putInt(Constants.INTIALIZEDB, Constants.IntializeDBVersion);
                editor.putBoolean(Constants.DataVaultUpdate, true);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(strPref) && (sharedPreferences.getInt(Constants.CURRENT_VERSION_CODE, 0) == Constants.NewDefingRequestVersion)) {
            MSFAApplication mApplication = (MSFAApplication) getApplicationContext();
            mApplication.startService(getApplicationContext(), false);
        }
        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(msfaApplication, registrationModel);
        Intent intent = new Intent(RegistrationActivity.this, UtilRegistrationActivity.class);
        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
        startActivity(intent);
        finish();
    }

}
