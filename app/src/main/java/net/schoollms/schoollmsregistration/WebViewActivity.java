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
import android.view.View;
import android.view.Window;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;

import java.io.*;
import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private static final String PREF_NAME = "BrowserHistory";

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

        final SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        try {
            makeInstallerAvailable();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String androidId = Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
        final String url[] = {"http://timetable.schoollms.net/"};

        Log.d(TAG, "onCreate: in Here ");

        Intent fmIntent = getPackageManager().getLaunchIntentForPackage(RealMain.GNU_PACKAGE);
        fmIntent.setAction("com.gnuroot.debian.LAUNCH");
        fmIntent.putExtra("launchType", "launchTerm"); //
        File installer = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/Android/obb/com.gnuroot.debian/installer.sh");
        fmIntent.putExtra("command", installer.getAbsolutePath());
        //  Gdx.files.internal("level1.xml").readString();
        Log.d(TAG, "onCreate: webView" + installer.length());
        fmIntent.setData(Uri.fromFile(installer));
        startActivity(fmIntent);

        String prefString = pref.getString("url", "http://localhost:2080/");

        if (intent.getBooleanExtra("isInstalled", false)) {
            if(prefString.equals("http://localhost:2080/"))
                url[0] = "http://localhost:2080/";
            else
                url[0] = pref.getString("url", "");
        }
        //Setting webview [ohh no!!!]
        WebView view = (WebView) findViewById(R.id.activity_web_view);
        WebSettings viewSettings = view.getSettings();
//        viewSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
       // viewSettings.setBuiltInZoomControls(true);
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


        view.loadUrl(url[0]);
        CookieManager.getInstance().setAcceptCookie(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                if(!urlx.equals(url[0]))
                     pref.edit().putString("url", urlx).commit();
                viewx.loadUrl(pref.getString("url", "http://localhost:2080/"));
                return true;
            }
        });
    }
    private void makeInstallerAvailable() throws IOException {
        InputStream inputStream = getAssets().open("install_schoollms_core.sh");
        Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
        File sdcardAndroidDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/Android/obb/com.gnuroot.debian");
        FileOutputStream fileOutputStream = new FileOutputStream(sdcardAndroidDir.getAbsolutePath() + "/installer.sh");
        //     byte[] buffer = new byte[inputStream.available()];
        byte[] buffer = new byte[1024];
        int r;
        while ((r = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, r);
        }
        inputStream.close();
        inputStream = null;

        // write the output file
        fileOutputStream.flush();
        fileOutputStream.close();
        fileOutputStream = null;
    }



    private Intent getLaunchIntent() {
        String command;
        Intent installIntent = new Intent("com.gnuroot.debian.LAUNCH");
        installIntent.setComponent(new ComponentName("com.gnuroot.debian", "com.gnuroot.debian.GNURootMain"));
        installIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installIntent.putExtra("launchType", "launchXTerm");
        command =
                "#!/bin/bash\n" +
                        "if [ ! -f /support/.rs_custom_passed ]; then\n" +
                        "  /support/untargz rs_custom /support/rs_custom.tar.gz\n" +
                        "fi\n" +
                        "if [ -f /support/.rs_custom_passed ]; then\n" +
                        "  if [ ! -f /support/.rs_script_passed ]; then\n" +
                        "    /support/blockingScript rs_script /support/oldschoolrs_install.sh\n" +
                        "  fi\n" +
                        "  if [ -f /support/.rs_script_passed ]; then\n" +
                        "    /usr/bin/oldschoolrs\n" +
                        "  fi\n" +
                        "fi\n";
        installIntent.putExtra("command", command);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setData(getTarUri());
        return installIntent;
    }

    /**
     * Returns a Uri for the custom tar placed in the project's assets directory.
     * @return
     */
    private Uri getTarUri() {
        File fileHandle = new File(getFilesDir() + "/rs_custom.tar.gz");
        return FileProvider.getUriForFile(this, "com.gnuroot.rsinstaller.fileprovider", fileHandle);
    }

    /**
     * Renames assets from .mp3 to .tar.gz.
     * @param packageName
     */
    private void copyAssets(String packageName) {
        Context friendContext = null;
        try {
            friendContext = this.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e1) {
            return;
        }
        AssetManager assetManager = this.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                filename = filename.replace(".mp3", ".tar.gz");
                out = openFileOutput(filename,MODE_PRIVATE);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
