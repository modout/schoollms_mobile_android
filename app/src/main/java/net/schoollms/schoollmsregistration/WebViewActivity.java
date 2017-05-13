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

public class WebViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "WebViewActivity";
    private static final String PREF_NAME = "BrowserHistory";
    private SharedPreferences pref;


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

        if((pref.getString("url", "http://timetable.schoollms.net/").equals("http://timetable.schoollms.net/"))) {
            btn.setVisibility(View.VISIBLE);
            showPopup(btn);
        }

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(execCmd()) {
                    t.cancel();
                    Intent in = new Intent(getApplicationContext(), WebViewActivity.class);
                    startActivity(in);
                }
            }
        }, 60);
//        String androidId = Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
        final String url[] = {"http://timetable.schoollms.net/"};
        String prefString = pref.getString("url", "http://timetable.schoollms.net/");

        if (intent.getBooleanExtra("isInstalled", false)) {
            if(prefString.equals("http://timetable.schoollms.net/"))
                url[0] = "http://timetable.schoollms.net/";
            else
                url[0] = pref.getString("url", "");
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


        view.loadUrl(pref.getString("url", "http://timetable.schoollms.net/"));
        CookieManager.getInstance().setAcceptCookie(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                if(!urlx.equals(url[0]))
                     pref.edit().putString("url", urlx).commit();
                viewx.loadUrl(pref.getString("url", "http://timetable.schoollms.net/"));
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
            if (s.contains("auto")) {
                return true ;
            } else return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s.contains("auto")) {
            return true ;
        } else return false;
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
                pref.edit().putString("url", "http://timetable.schoollms.net/").commit();
                view.loadUrl(pref.getString("url", "http://timetable.schoollms.net/"));
                break;
        }
        return false;
    }
}
