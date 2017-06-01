package net.schoollms.schoollmsregistration;

import android.app.ActivityManager;
import android.app.AlertDialog;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);
        AssetManager assetManager = getAssets();
        AndroidNetworking.initialize(getApplicationContext());



        Log.d(TAG, "onCreate: lifexx" + getApplicationInfo().dataDir);
        //Yes the package is installed.

    }

}
