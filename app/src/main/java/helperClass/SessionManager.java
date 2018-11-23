package helperClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by rio.issac on 5/7/2015.
 */
public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "IncidentalExpense";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_REGISTRATION_ID = "registrationID";
    private static String TAG = SessionManager.class.getSimpleName();

    private static final String KEY_IS_UPDATED= "IsUpdated";
    // Shared Preferences
    SharedPreferences pref;
    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    public int getUserID() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public void setUserID(int EmpID) {

        editor.putInt(KEY_USER_ID, EmpID);

        // commit changes
        editor.commit();

    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Administrator");
    }

    public void setUserName(String EmpName) {

        editor.putString(KEY_USER_NAME, EmpName);

        // commit changes
        editor.commit();

    }


    public String getDeviceID() {
        return pref.getString(KEY_DEVICE_ID, "0");
    }

    public void setDeviceID(String DeviceID) {

        editor.putString(KEY_DEVICE_ID, DeviceID);

        // commit changes
        editor.commit();
    }

    public String getRegistrationId() {
        return pref.getString(KEY_REGISTRATION_ID, "0");
    }

    public void setRegistrationId(String RegistrationID) {

        editor.putString(KEY_REGISTRATION_ID, RegistrationID);

        // commit changes
        editor.commit();

    }

    public void clearSessionVariables(){

        editor.clear();
        editor.commit();
    }
}
