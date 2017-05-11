package net.schoollms.schoollmsregistration;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class RealMain extends AppCompatActivity {

    private static final String TAG = "RealMain" ;
    final static String GNU_PACKAGE = "com.gnuroot.debian";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        AssetManager assetManager = getAssets();

        Intent intent = new Intent(this, WebViewActivity.class);
        File f = new File("data");

        Log.d(TAG, "onCreate: lifexx" +   getApplicationInfo().dataDir);
        //Yes the package is installed.
        if(isPackageInstalled(GNU_PACKAGE, this.getPackageManager())) {
            Log.d(TAG, "onCreate: " +   f.getAbsolutePath());

//            intent.putExtra("isInstalled", true); // installed and running
            //open webview to local host
            Log.d(TAG, "onCreate: is Running!!!");
            // now that it is installed check if its running
                //check Lms is installed #
                startGNUApk();
                finish(); // CLOSE THAT ACTIVITY
                startActivity(intent);

        } else {
            try {
                InputStream open = assetManager.open("gnu_app.apk");

                byte[] b = new byte[open.available()];
                int read = open.read(b);
                Log.d(TAG,read + " byte read");

              //  String tempFileName = "gnu_app.apk";
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/gnu_app.apk";
                FileOutputStream fout = new FileOutputStream(path);
                fout.write(b);
                fout.close();
//                fout.flush();
                open.close();

               File tempFile = Environment.getExternalStoragePublicDirectory(
                       Environment.DIRECTORY_DOWNLOADS + "/gnu_app.apk");

                Log.d(TAG, "onCreate: length o " + tempFile.length());

                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
                long megAvailable = bytesAvailable / (1024 * 1024);
                double gigs = megAvailable / 1024;

                if (megAvailable < 500L) {
                    new AlertDialog.Builder(this)
                            .setTitle("Not Enough Space")
                            .setMessage("There is not enough space")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                  finish(); // application ends here
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;   //ED+XIT IF NOT ENOUGH SPACE!!!!
                }
                //CReate OBB foldert!
                File sdcardAndroidDir =new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/Android/obb/com.gnuroot.debian");
                boolean isMkFile = sdcardAndroidDir.mkdir();
                if(isMkFile) {
                    InputStream inputStream = assetManager.open("main.10.com.gnuroot.debian.obb");
                    Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
                    FileOutputStream fileOutputStream = new FileOutputStream(sdcardAndroidDir.getAbsolutePath() + "/main.10.com.gnuroot.debian.obb");
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
                Intent in = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(tempFile);
                Log.d(TAG, "onCreate: patho " + uri.getPath());
                in.setDataAndType(uri, "application/vnd.android.package-archive").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                this.startActivity(in);

                //startGNUApk();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       finish();
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private  void startGNUApk() {
        Intent fmIntent = getPackageManager().getLaunchIntentForPackage(GNU_PACKAGE);
        fmIntent.setAction("com.gnuroot.debian.LAUNCH");
//                fmIntent.setData(null);
        fmIntent.putExtra("launchType", "launchTerm"); //
        fmIntent.putExtra("command", "ls");
        startActivityForResult(fmIntent, 600);
    }

    private static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
