package net.schoollms.schoollmsregistration;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;

import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
         String androidId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final String url[] = {"http://timetable.schoollms.net/"};


        //Permission Listener
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(WebViewActivity.this, "Permission Granted",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(WebViewActivity.this, "Permission Denied\n" + deniedPermissions.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        final TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(WebViewActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        Log.d(TAG, "onImageSelected: " + uri.getPath());
                    }
                })
                .create();


        tedBottomPicker.show(getSupportFragmentManager());

//
//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        // not ins
        if(intent.getBooleanExtra("isInstalled", false)) {
            url[0] = "http://localhost:2080/";
        }
                WebView view = (WebView) findViewById(R.id.activity_web_view);
                view.loadUrl(url[0]);
    }
}
