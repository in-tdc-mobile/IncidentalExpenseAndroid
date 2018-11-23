package helperClass;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.mariapps.incidentalexpenseandroid.LoginActivity;
import com.mariapps.incidentalexpenseandroid.R;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aruna.ramakrishnan on 11/23/2018.
 */
public class CommonFunctions {
    private static Context context;
    private SessionManager session;

    public CommonFunctions() {
    }

    public CommonFunctions(Context context) {
        this.context = context;
        session = new SessionManager(context);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isAuthenticateUser(JSONObject jObj) {
        try {
            if (!jObj.get("CommonEntity").equals(null)) {
                Log.e("notificationDatedown", String.valueOf(jObj.getJSONObject("CommonEntity")));
                session = new SessionManager(this.context);
                JSONObject objCommonEntity = jObj.getJSONObject("CommonEntity");
                if (objCommonEntity.getString("IsAuthourized").toString().matches("N")) {
                    Intent myIntent = new Intent(this.context, LoginActivity.class);
                    context.startActivity(myIntent);
                    session.setLogin(false);
                    Toast.makeText(this.context,
                            R.string.logged_out, Toast.LENGTH_LONG)
                            .show();
                    ((Activity) this.context).finish();
                } else {
                    if (objCommonEntity.getString("TransactionStatus").toString().matches("N") && !objCommonEntity.getString("Message").toString().equals("")) {
                        Toast.makeText(this.context,
                                objCommonEntity.getString("Message").toString(), Toast.LENGTH_LONG)
                                .show();

                        return false;
                    }
                }
            }

        } catch (Exception e) {
            // JSON error
            e.printStackTrace();
        }

        return true;
    }

    public void showSnackBar(View view, String text, String buttonText) {
        final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG).setActionTextColor(context.getResources().getColor(R.color.white));
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


        snackbar.setAction(buttonText, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    public Boolean isUserAuthenticate(JSONObject jObj) {
        try {
            if (!jObj.get("CommonEntity").equals(null)) {
                JSONObject objCommonEntity = jObj.getJSONObject("CommonEntity");
                if (objCommonEntity.getString("IsAuthourized").toString().matches("Y")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }



    public static void showHideDialog(ProgressDialog pDialog) {
        if (!pDialog.isShowing())
            pDialog.show();
        else
            pDialog.dismiss();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int desiredWidth = 0;
        if (listAdapter == null)
            return;
        desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +   (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

    }


    public static boolean checkAndRequestPermissions(Activity activity, String permission, int requestCode) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permission.equals(Manifest.permission.CAMERA)) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
            return false;
        }
        return true;
    }
}
