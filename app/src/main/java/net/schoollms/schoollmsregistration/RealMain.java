package net.schoollms.schoollmsregistration;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.androidnetworking.AndroidNetworking;
import model.Roles;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RealMain extends AppCompatActivity {

    private static final String TAG = "RealMain";
    private final static String GNU_PACKAGE = "com.gnuroot.debian";
   private final ArrayList<Roles> mRoles = new ArrayList<>();
    private String tag_json_obj = "tag-json-obj";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        AssetManager assetManager = getAssets();
        AndroidNetworking.initialize(getApplicationContext());



        Intent intent = new Intent(this, WebViewActivity.class);
        Log.d(TAG, "onCreate: lifexx" + getApplicationInfo().dataDir);
        //Yes the package is installed.
        if (isPackageInstalled(GNU_PACKAGE, this.getPackageManager())) {
            Log.d(TAG, "onCreate: GNU is installed!");

//            intent.putExtra("isInstalled", true); // installed and running
            //open webview to local host
            Log.d(TAG, "onCreate: is Running!!!");
            startGNUApk();  // tries to access GNURoot! watch out
            //   finish(); // CLOSE THAT ACTIVITY
            startActivity(intent);

        } else {
            try {

                //check space first
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
                long megAvailable = bytesAvailable / (1024 * 1024);

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
                File sdcardAndroidDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/Android/obb/com.gnuroot.debian");
                boolean isMkFile = false;
                if(sdcardAndroidDir.exists()) {
                    startFormActivity(assetManager, sdcardAndroidDir);
                } else {
                    isMkFile = sdcardAndroidDir.mkdirs();
                    if (isMkFile) {
                        startFormActivity(assetManager, sdcardAndroidDir);
                    }
                }
                Log.d(TAG, "onCreate: make file: " + isMkFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startFormActivity(AssetManager assetManager, File sdcardAndroidDir) throws IOException {
        InputStream inputStream = assetManager.open("main.10.com.gnuroot.debian.obb");
        Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
        coppGNUOBB(sdcardAndroidDir, inputStream);
        copyAssetTrackerFile(assetManager);
        copyAssetGNUToFile(assetManager);
        Intent formIntent = new Intent(this, MainActivity.class);
        startActivity(formIntent);
    }

    private void coppGNUOBB(File sdcardAndroidDir, InputStream inputStream) throws IOException {
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

    private void copyAssetGNUToFile(AssetManager assetManager) throws IOException {
        InputStream open = assetManager.open("gnu_app.apk");

        byte[] b = new byte[open.available()];
        int read = open.read(b);
        Log.d(TAG, read + " byte read");

        //  String tempFileName = "gnu_app.apk";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/gnu_app.apk";
        FileOutputStream fout = new FileOutputStream(path);
        fout.write(b);
        fout.close();
        open.close();
    }

    private void copyAssetTrackerFile(AssetManager assetManager) throws IOException {
        InputStream open = assetManager.open("tracker_app.apk");

        byte[] b = new byte[open.available()];
        int read = open.read(b);
        Log.d(TAG, read + " byte read");

        //  String tempFileName = "gnu_app.apk";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/tracker_app.apk";
        FileOutputStream fout = new FileOutputStream(path);
        fout.write(b);
        fout.close();
        open.close();
    }




    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startGNUApk() {
//        Intent fmIntent = getPackageManager().getLaunchIntentForPackage(GNU_PACKAGE);
//        fmIntent.setAction("com.gnuroot.debian.LAUNCH");
//        fmIntent.putExtra("launchType", "launchTerm"); //
//        fmIntent.putExtra("command", "#!/bin/bash\n" +
//                "ls");

        try {
            copyAssets("net.schoollms.schoollmsregistration");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getLaunchIntent();
        startActivity(intent);
        // startActivityForResult(fmIntent, 600);
    }

    private static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    //    private static void copyFileUsingStream(File source, File dest) throws IOException {
//        InputStream is = null;
//        OutputStream os = null;
//        try {
//            is = new FileInputStream(source);
//            os = new FileOutputStream(dest);
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) > 0) {
//                os.write(buffer, 0, length);
//            }
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//            if (os != null) {
//                os.close();
//            }
//        }
//    }
    private Intent getLaunchIntent() {
        String command;
        Intent installIntent = new Intent("com.gnuroot.debian.LAUNCH");
        installIntent.setComponent(new ComponentName("com.gnuroot.debian", "com.gnuroot.debian.GNURootMain"));
        installIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installIntent.putExtra("launchType", "launchTerm");
        command =
                "#!/bin/bash\n" +
                        "    /support/blockingScript rs_script /sdcard/GNURoot/home/install_core.sh\n"

        ;
        installIntent.putExtra("command", command);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //    installIntent.setData(getTarUri());

        return installIntent;
    }

    /**
     * Returns a Uri for the custom tar placed in the project's assets directory.
     *
     * @return
     */
    private Uri getTarUri() {
        File fileHandle = new File(getFilesDir() + "/rs_custom.tar.gz");
        return FileProvider.getUriForFile(this, "net.schoollms.schoollmsregistration.fileprovider", fileHandle);
    }

    /**
     * Renames assets from .mp3 to .tar.gz.
     *
     * @param packageName
     */
    private void copyAssets(String packageName) throws IOException {
        File sdcardAndroidDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath());
        InputStream inputStream = getAssets().open("install_core.sh");
        Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
        FileOutputStream fileOutputStream = new FileOutputStream(sdcardAndroidDir.getAbsolutePath() + "/GNURoot/home/install_core.sh");
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

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }



//    private void restIt() {
//        ApiEndPoint client = ApiClient.getClient().create(ApiEndPoint.class);
//        final Call<Roles> roles = client.getRoles("get:roles");
//        roles.enqueue(new Callback<Roles>() {
//            @Override
//            public void onResponse(Call<Roles> call, Response<Roles> response) {
//               mRoles.add(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<Roles> call, Throwable t) {
//
//            }
//        });
//
//        }

}
