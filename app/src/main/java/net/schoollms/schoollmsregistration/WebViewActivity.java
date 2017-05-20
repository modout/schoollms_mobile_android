package net.schoollms.schoollmsregistration;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.*;
import android.widget.*;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class WebViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "WebViewActivity";
    private static final String PREF_NAME = "BrowserHistory";
    private SharedPreferences pref;
    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;
    String u = null;
    private String ux;


    private    WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        CookieManager.getInstance().setAcceptCookie(true);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.webLoading);
        Intent intent = getIntent();
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Button btn = (Button) findViewById(R.id.button_menu);
        btn.setVisibility(View.GONE);

        Log.d(TAG, "onCreate: in here: webview");

        u = "localhost:2080";
        if (getConnectivityStatus(this) == TYPE_WIFI) {
            u = getIntent().getStringExtra("schoolIp") + "/vas/timetable";
        } else if(getConnectivityStatus(this) == TYPE_MOBILE) {
            u = "timetable.schoollms.net";
        }

        if((pref.getString("url", "http://timetable.schoollms.net/").equals("http://timetable.schoollms.net/"))) {
            btn.setVisibility(View.VISIBLE);
           // showPopup(btn);
        }

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(execCmd()) {
                    t.cancel();
                    Log.d(TAG, "the command has been executed");
                    Intent in = new Intent(getApplicationContext(), WebViewActivity.class);
                    startActivity(in);
                }
            }
        }, 60);
//        String androidId = Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
        ux = "http://"+ u +"/viewtimetable.php?school_id=" + getIntent().getStringExtra("schoolId")+ "&user_type="+getIntent().getStringExtra("role")+"&user_id=" + getIntent().getStringExtra("userID") + "&year_id="+ getIntent().getStringExtra("yearId");

        final String url[] = {ux};
        String prefString = pref.getString("url", ux);

        if (intent.getBooleanExtra("isInstalled", false)) {
            if(prefString.equals(ux))
                url[0] = ux;
            else
                url[0] = pref.getString("url", ux);
        }
        //Setting webview [ohh no!!!]
        view = (WebView) findViewById(R.id.activity_web_view);
        WebSettings viewSettings = view.getSettings();
        viewSettings.setJavaScriptEnabled(true);


        final Activity activity = this;
        view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
                if (progress == 100) {
                    bar.setVisibility(View.GONE);
                }
            }
        });
        view.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });


        view.loadUrl(pref.getString("url", ux   ));
        CookieManager.getInstance().setAcceptCookie(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                if(!urlx.equals(url[0]))
                     pref.edit().putString("url", urlx).commit();
                viewx.loadUrl(pref.getString("url", ux));
                return true;
            }
        });
    }

    private boolean execCmd() {
        String line;
        String s = "";
        Log.d(TAG, "execCmd: hello there111");

        try {
            Process process = Runtime.getRuntime().exec("cat /var/lib/dpkg/alternatives/awk");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Log.d(TAG, "execCmd: hello there");

            StringBuilder log = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
                Log.d(TAG, "execCmd: the value: " + line);
            }
            s = log.toString();
            return s.contains("auto");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.contains("auto");
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.action);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_timetable:
                pref.edit().putString("url", ux).commit();
                view.loadUrl(pref.getString("url", "http://timetable.schoollms.net/"));
                break;
        }
        return false;
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "wifi";
        } else if (conn == TYPE_MOBILE) {
            status = "mobile";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "none";
        }
        return status;
    }
}


