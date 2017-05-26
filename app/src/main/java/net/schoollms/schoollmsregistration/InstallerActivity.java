package net.schoollms.schoollmsregistration;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import model.Roles;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InstallerActivity extends AppCompatActivity {

    private String TAG = "InstallerActivity";
    private SharedPreferences sharedPreferences;
    private final ArrayList<Roles> mRoles = new ArrayList<>();
    public static final String GNU_PACKAGE = "com.gnuroot.debian";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AssetManager assetManager = getAssets();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installer);

        final Button btnBase = (Button) findViewById(R.id.btn_base);
        final Button btnCore = (Button) findViewById(R.id.btn_install_core);
        final Button btnStart = (Button) findViewById(R.id.btn_start_lms);
        sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean started = sharedPreferences.getBoolean("started", false);
        boolean coreInstalled = sharedPreferences.getBoolean("coreInstalled", false);
        boolean baseInstalled = sharedPreferences.getBoolean("baseInstalled", false);

        if(baseInstalled) {
            btnCore.setEnabled(false);
            btnBase.setEnabled(true);
            btnStart.setEnabled(false);
        } else if(coreInstalled) {
            btnCore.setEnabled(true);
            btnBase.setEnabled(false);
            btnStart.setEnabled(false);
        } else if (started) {
            btnCore.setEnabled(false);
            btnBase.setEnabled(false);
            btnStart.setEnabled(true);
        }
        //TODO: comment me out! please!
//        btnCore.setEnabled(true);
//        btnBase.setEnabled(true);
//        btnStart.setEnabled(true);

        btnBase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("started", false);
                editor.putBoolean("coreInstalled", true);
                editor.putBoolean("baseInstalled", false);
                editor.commit();


                try {

                    //check space first
                    //should only be done one in a life time
                    if(!sharedPreferences.getBoolean("hasItWritten", false)) {
                        copyAssets("net.schoollms.schoollmsregistration");
                    }

                    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                    long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
                    long megAvailable = bytesAvailable / (1024 * 1024);

                    if (megAvailable < 500L) {
                        new AlertDialog.Builder(InstallerActivity.this)
                                .setTitle("Not Enough Space")
                                .setMessage("There is not enough space")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish(); // application ends here
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;   //ExIT IF NOT ENOUGH SPACE!!!!
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
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("started", false);
                editor.putBoolean("coreInstalled", false);
                editor.putBoolean("baseInstalled", false);
                editor.commit();
                Intent in = new Intent(InstallerActivity.this, WebViewActivity.class);
                startActivity(in);
            }
        });
        btnCore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.putBoolean("started", true);
                editor.putBoolean("coreInstalled", false);
                editor.putBoolean("baseInstalled", false);
                editor.commit();

                if (isPackageInstalled(GNU_PACKAGE, InstallerActivity.this.getPackageManager())) {
                    Log.d(TAG, "onCreate: GNU is installed!");
                    Log.d(TAG, "onCreate: is Running!!!");
                    startGNUApk();  // tries to access GNURoot! watch out
                }  else {
                    Log.d(TAG, "onClick: aggg: the gnu pagae is not installed");
                    Toast.makeText(InstallerActivity.this, "Please complete the step above before continueing", Toast.LENGTH_LONG).show();
                    editor.putBoolean("started", false);
                    editor.putBoolean("coreInstalled", false);
                    editor.putBoolean("baseInstalled", true);
                    editor.commit();

                    boolean started = sharedPreferences.getBoolean("started", false);
                    boolean coreInstalled = sharedPreferences.getBoolean("coreInstalled", false);
                    boolean baseInstalled = sharedPreferences.getBoolean("baseInstalled", false);

                    if(baseInstalled) {
                        btnCore.setEnabled(false);
                        btnBase.setEnabled(true);
                        btnStart.setEnabled(false);
                    } else if(coreInstalled) {
                        btnCore.setEnabled(true);
                        btnBase.setEnabled(false);
                        btnStart.setEnabled(false);
                    } else if (started) {
                        btnCore.setEnabled(false);
                        btnBase.setEnabled(false);
                        btnStart.setEnabled(true);
                    }
                }
            }
        });
    }

    private void startFormActivity(AssetManager assetManager, File sdcardAndroidDir) throws IOException {
        if (!sharedPreferences.getBoolean("hasItWrittenGUNFILES", false)) {
            InputStream inputStream = assetManager.open("main.10.com.gnuroot.debian.obb");
            Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
            coppGNUOBB(sdcardAndroidDir, inputStream);
            copyAssetTrackerFile(assetManager);
            copyAssetGNUToFile(assetManager);
            sharedPreferences.edit().putBoolean("hasItWrittenGUNFILES", true).commit();
        }
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
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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

    private void startGNUApk() {
            //I must rename the package
            Intent intent = getLaunchIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        // startActivityForResult(fmIntent, 600);
    }


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
    private void copyAssets(String packageName) throws IOException {
        File sdcardAndroidDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath());
        InputStream inputStream = getAssets().open("install_core.sh");
        Log.d(TAG, "onCreate: onassetmanO open " + inputStream.available());
        File f = new File(sdcardAndroidDir.getAbsolutePath() + "/GNURoot/home");
        boolean mkdirs = f.mkdirs();//please make the directories if the don`t exit
        Log.d(TAG, "copyAssets: did we mk the dir?: " + mkdirs);
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
        cp(sdcardAndroidDir);
        sharedPreferences.edit().putBoolean("hasItWritten", true).commit();
    }

    private void cp(File sdcardAndroidDir) {
        int r2;
        InputStream inputStream2 = null;
        try {
            inputStream2 = getAssets().open("install.mp3");
            Log.d(TAG, "onCreate: onassetmanO open oonnnnnnn " + inputStream2.available());
            FileOutputStream fileOutputStream2 = new FileOutputStream(sdcardAndroidDir.getAbsolutePath() + "/GNURoot/home/install.tar.gz");
            byte[] buffer2 = new byte[1024];
            while ((r2 = inputStream2.read(buffer2)) != -1) {
                fileOutputStream2.write(buffer2, 0, r2);
            }
            inputStream2.close();
            inputStream2 = null;

            // write the output file
            fileOutputStream2.flush();
            fileOutputStream2.close();
            fileOutputStream2 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: the result is: " + resultCode);
        Log.d(TAG, "onActivityResult: the request code is: " + requestCode);
        Log.d(TAG, "onActivityResult: the intent data is : " + data);
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

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("status", MODE_PRIVATE);
        boolean started = sharedPreferences.getBoolean("started", false);
        boolean coreInstalled = sharedPreferences.getBoolean("coreInstalled", false);
        boolean baseInstalled = sharedPreferences.getBoolean("baseInstalled", false);

        Button btnBase = (Button) findViewById(R.id.btn_base);
        Button btnCore = (Button) findViewById(R.id.btn_install_core);
        Button btnStart = (Button) findViewById(R.id.btn_start_lms);
        Log.d(TAG, "onStart: we have started man: ");

        if(baseInstalled) {
            btnCore.setEnabled(false);
            btnBase.setEnabled(true);
            btnStart.setEnabled(false);
        } else if(coreInstalled) {
            btnCore.setEnabled(true);
            btnBase.setEnabled(false);
            btnStart.setEnabled(false);
        } else if (started) {
            btnCore.setEnabled(false);
            btnBase.setEnabled(false);
            btnStart.setEnabled(true);
        }

//        if (started) {
//            Intent in = new Intent(this, WebViewActivity.class);
//            startActivity(in);
//        }
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



}
