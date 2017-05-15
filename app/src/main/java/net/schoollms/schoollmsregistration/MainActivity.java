package net.schoollms.schoollmsregistration;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private String[] ids;
    private String schoolName;
    private final static ArrayList<String> stringArrayList = new ArrayList<>();
    private final static ArrayList<String> stringSchools = new ArrayList<>();
    private Uri mUri;
    private int userId ;
    private Toast toast;
    private TedBottomPicker tedBottomPicker;
    private final Timer learnerTimer = new Timer();
    private int selectedSchoolID;
    private int yearId;
    private int sIp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner spRoles = (Spinner) findViewById(R.id.spinner_roles);
        final AutoCompleteTextView spSchoolName = (AutoCompleteTextView) findViewById(R.id.et_school_name);
        TextView tvSchoolName = (TextView) findViewById(R.id.txt_school_name);
        TextView tvId = (TextView) findViewById(R.id.txt_ID);
        final EditText etID = (EditText) findViewById(R.id.editText_ID);
        TextView tvSurname = (TextView) findViewById(R.id.txt_name);
        final TextView tvName = (TextView) findViewById(R.id.editText_name);
        final EditText etSurname = (EditText) findViewById(R.id.editText_surname);
        Button btnYes = (Button) findViewById(R.id.btn_yes);
        Button btnNo = (Button) findViewById(R.id.btn_no);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_confirm_details);
        final Button btnDone = (Button) findViewById(R.id.btn_done);
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ImageView imageView = (ImageView) findViewById(R.id.image_profile);
        final Button btnSend = (Button) findViewById(R.id.btn_send);

        SharedPreferences main = getSharedPreferences("main", MODE_PRIVATE);

        final String[] token = {""};

        String[] roles = {"Learner", "Support", "Teacher"};
        final String[] schools = {"School A", "School B", "School C"};
        ids = new String[]{"5678", "1234", "3456"};

        AndroidNetworking.post("http://www.schoollms.net/services/session/token")
                .setPriority(Priority.MEDIUM)
//                .add
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: hello there " + response);
                        token[0] = response;
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onResponse: hello there ERR " + anError.getResponse());
                    }
                });
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                        .setPriority(Priority.LOW)
                        .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                        .addBodyParameter("message", "get:schools")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Iterator<String> keys = response.keys();
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        String k = keys.next();
                                        String string = response.getString(k);
                                        stringSchools.add(k);
                                        adapter.add(string);
                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "onResponse: ressss: " + string);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d(TAG, "onError: error" + anError.toString());
                            }
                        });
            }
        });
        t.start();



        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.LOW)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:roles")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String string = response.getString(i + "");
                                string = string.toUpperCase();
                                ad.add(string);
                                ad.notifyDataSetChanged();
                                Log.d(TAG, "onResponse: ressss: " + string);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: error" + anError.toString());
                    }
                });


        Log.d(TAG, "onCreate: size of array" + stringArrayList.size());


        btnDone.setVisibility(View.VISIBLE);

        String[] mnx = new String[stringArrayList.size()];
        Log.d(TAG, "onCreate: the ssize of this i " + stringArrayList.size());
        for (int i = 0; i < stringArrayList.size(); i++) {
            mnx[i] = stringArrayList.get(i);
        }
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoles.setAdapter(ad);
        ad.notifyDataSetChanged();


        String role = (String) spRoles.getSelectedItem();
        spSchoolName.setAdapter(adapter);
        spSchoolName.setOnItemClickListener(this);
        spSchoolName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String school = (String)parent.getItemAtPosition(position);
                schoolName = school;
            }
        });
        final String userId = etID.getText().toString();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (schoolName == null || TextUtils.isEmpty(schoolName)) {
                     spSchoolName.setError("Please enter a school to continue");
                } else if (TextUtils.isEmpty(etID.getText().toString())) {
                    etID.setError("Please enter your ID to continue");
                } else {
                   // t2.start();
                    getNameAndSurname(token, tvName, etSurname, etID);
                    Toast.makeText(MainActivity.this, "Scroll down now to validate you infomation", Toast.LENGTH_LONG).show();
                    btnDone.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        //Permission Listener
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        tedBottomPicker = getTedBottomPicker(imageView);


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  t2.isAlive();
                Toast.makeText(getApplicationContext(), "Starting image picker", Toast.LENGTH_LONG);
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("schoolId", selectedSchoolID);
                intent.putExtra("role", (String) spRoles.getSelectedItem());
                intent.putExtra("userID", userId);
                intent.putExtra("yearId", yearId);
                intent.putExtra("schoolIp", sIp);

                if(!tvName.getText().toString().equals("")) {
                    tedBottomPicker.show(getSupportFragmentManager());
                } else {
                    Toast.makeText(MainActivity.this, "Recheck your Form and press Submit again or press not me this incidence will be reported", Toast.LENGTH_LONG).show();
                    btnDone.setVisibility(View.VISIBLE);
                   // uploadThePicture();
                }

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog al = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Please contact your school admin!")
                        .create();
                al.show();
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "onClick: seding img");
                    toast = Toast.makeText(MainActivity.this, "Uploading", Toast.LENGTH_LONG);
                    uploadThePicture(mUri, token, etID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(spRoles.getSelectedItem().equals("Learner")) {
                    learnerTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            checkTheUserHasRegistered(token);
                        }
                    }, 1000 );
                } else {
                    startGNUInstaller();
                }

            }
        });
    }

    private TedBottomPicker getTedBottomPicker(final ImageView imageView) {
        return new TedBottomPicker.Builder(MainActivity.this)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(Uri uri) {
                            Log.d(TAG, "onImageSelected: " + uri.getPath());
                            imageView.setImageURI(uri);
                            mUri = uri;
                        }
                    })
                    .create();
    }


    private void getYearId(String[] token) {
        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:year_id:" + getYear())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String yId = response.getString("year_id");
                            yearId = Integer.parseInt(yId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onResponse: the errroorr" + error.toString());

                    }
                });
    }

    private void getSchoolIP(String[] token) {
        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:school_ip:"+ selectedSchoolID )
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String schoolIP = response.getString("school_ip");
                            sIp = Integer.parseInt(schoolIP);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onResponse: the errroorr" + error.toString());

                    }
                });
    }

    private void checkTheUserHasRegistered(String[] token) {
        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:photo_confirm:" + userId)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("waiting")) {
                                // a counter should set timeout
                            } else if (status.equals("rejected")) {
                                toast.setText("please upload a proper school profile");
                                tedBottomPicker.show(getSupportFragmentManager());
                            } else if (status.equals("accepted")) {
                                learnerTimer.cancel();
                                startTrackerInstaller();
                                startGNUInstaller();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onResponse: the errroorr" + error.toString());

                    }
                });
    }




    private void uploadThePicture(Uri uri, String[] token, TextView etID) throws IOException {
        File file = new File(uri.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        toast.show(); // show that its uploading
        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.d(TAG, "uploadThePicture: the encoding" + encodedFile);
        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "send:photo:" + userId + ":" + encodedFile)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        toast.setText("Uploadeded");
                        toast.show();
                        Log.d(TAG, "onResponse: the resposesssss" + response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d(TAG, "onResponse: the errroorr" + error.toString());

                    }
                });
    }

    private void startGNUInstaller() {
        // launch nGNU installer
        File tempFile = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/gnu_app.apk");
        Log.d(TAG, "onCreate: length o " + tempFile.length());

        Intent in = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(tempFile);
        Log.d(TAG, "onCreate: patho " + uri.getPath());
        in.setDataAndType(uri, "application/vnd.android.package-archive").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(in);
    }

    private void startTrackerInstaller() {
        // launch nGNU installer
        File tempFile = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/tracker_app.apk");
        Log.d(TAG, "onCreate: length " + tempFile.length());

        Intent in = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(tempFile);
        Log.d(TAG, "onCreate: patho " + uri.getPath());
        in.setDataAndType(uri, "application/vnd.android.package-archive").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(in);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        schoolName = (String) parent.getItemAtPosition(position);
        selectedSchoolID = Integer.parseInt(stringSchools.get(position));
    }
    private void getNameAndSurname(String[] token, final TextView tvName,final TextView etSurname, TextView etID) {
        Log.d(TAG, "run: in here man:" );
        AndroidNetworking.post("http://www.schoollms.net/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:details:" + etID.getText().toString() + ":schoollms_schema_userdata_access_profile")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: response: " + response.toString());
                            final String name = response.getString("name");
                            final String surname = response.getString("surname");
                            userId = Integer.parseInt(response.getString("user_id"));
                            Log.d(TAG, "onResponse: resposnse name:" + name + "surane: " + surname);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvName.setText(name);
                                    etSurname.setText(surname);
                                }
                            });

                        } catch (JSONException e) {
                            Log.d("MainActivity","hello in here");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: erros is here man: " + anError.toString());
                    }
                });

    }

    public int getYear() {
        Calendar now = Calendar.getInstance();   // Gets the current date and time
        return now.get(Calendar.YEAR);

    }
}
