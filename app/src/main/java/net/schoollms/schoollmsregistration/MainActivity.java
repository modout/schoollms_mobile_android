package net.schoollms.schoollmsregistration;

import android.Manifest;
import android.annotation.SuppressLint;
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
import services.FileSizeWatcherService;

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
    private  int counter = 0;
    SharedPreferences s;
    final String[] token = {""};
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> ad;
    private Spinner spRoles;
    private AutoCompleteTextView spSchoolName;
    private EditText etID;
    private TextView tvName;
    private EditText etSurname;
    private Button btnYes;
    private Button btnNo;
    private LinearLayout linearLayout;
    private Button btnDone;
    private ImageView imageView;
    private Button btnSend;
    private LinearLayout lnrOther;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spRoles = (Spinner) findViewById(R.id.spinner_roles);
        spSchoolName = (AutoCompleteTextView) findViewById(R.id.et_school_name);
        etID = (EditText) findViewById(R.id.editText_ID);
        tvName = (TextView) findViewById(R.id.editText_name);
        etSurname = (EditText) findViewById(R.id.editText_surname);
        btnYes = (Button) findViewById(R.id.btn_yes);
        btnNo = (Button) findViewById(R.id.btn_no);
        linearLayout = (LinearLayout) findViewById(R.id.layout_confirm_details);
        btnDone = (Button) findViewById(R.id.btn_done);
        ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        imageView = (ImageView) findViewById(R.id.image_profile);

        btnSend = (Button) findViewById(R.id.btn_send);
        lnrOther = (LinearLayout) findViewById(R.id.lnrOtherInput);
        final Button  btnInternet, btnNoInternet,btnIPSet;
        final LinearLayout containerLayout = (LinearLayout) findViewById(R.id.theOne);
        final EditText etIP = (EditText) findViewById(R.id.et_IP_input);
        s = getSharedPreferences("school_pref", MODE_PRIVATE);

        btnInternet = (Button) findViewById(R.id.reg_internet);
        btnNoInternet = (Button) findViewById(R.id.reg_no_internet);
        btnIPSet = (Button) findViewById(R.id.btn_IP_set);
        String url;

        ids = new String[]{"5678", "1234", "3456"};

        btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.schoollms.net";
                doNetworkCalls(url);
                containerLayout.setVisibility(View.VISIBLE);
                btnIPSet.setVisibility(View.GONE);
              //  btnInternet.setVisibility(View.GONE);
            }
        });
        btnNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                //todo: please add the URL here dude
                etIP.setVisibility(View.VISIBLE);

             //   btnInternet.setVisibility(View.GONE);
                btnIPSet.setVisibility(View.VISIBLE);
            }
        });
        btnIPSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etIP.getText().toString()))
                    etIP.setError("please input the IP before continuing ");
                else {
                    String url = etIP.getText().toString();
                    btnIPSet.setVisibility(View.GONE);
                    containerLayout.setVisibility(View.VISIBLE);
                    doNetworkCalls(url);
                }
            }
        });
    }

    private void doNetworkCalls(final String url) {

        AndroidNetworking.post(url + "/services/session/token")
                .setPriority(Priority.MEDIUM)
//                .add
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: hello there " + response);
                        token[0] = response;
                        s.edit().putString("token", token[0]).commit();
                        Log.d(TAG, "onResponse: Commit babe[token!!!]: " + token[0]);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onResponse: hello there ERR " + anError.getResponse());
                        Toast.makeText(MainActivity.this, "Internet Error, please check your connection", Toast.LENGTH_SHORT).show();
                    }
                });
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
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
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.LOW)
                .addHeaders("X-CSRF-Token", token[0].equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token[0])
                .addBodyParameter("message", "get:roles")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String string = response.getString(i + ""); // 1: 'Teacher', 2: Le
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
                //Todo: this scholl is probably important ma nikka
                schoolName = school;
            }
        });

        spRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(counter >= 1) {
                    lnrOther.setVisibility(View.VISIBLE);
                }
                ++counter;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Please select an option!", Toast.LENGTH_LONG).show();
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
                    getNameAndSurname(url, token, tvName, etSurname, etID);
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
                s.edit().putString("role", (String) spRoles.getSelectedItem()).commit();
                Log.d(TAG, "onResponse: selected role Commit babe[role]: " + spRoles.getSelectedItem());
                if(!tvName.getText().toString().equals("")) {
                    tedBottomPicker.show(getSupportFragmentManager());
                } else {
                    Toast.makeText(MainActivity.this, "Recheck your Form and press Submit again or press not me this incidence will be reported", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(View.GONE);
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
                        .setMessage("Please contact your school admin! OR try again")
                        .create();
                al.show();
                btnDone.setVisibility(View.VISIBLE);
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "onClick: seding img");
                    toast = Toast.makeText(MainActivity.this, "Uploading", Toast.LENGTH_LONG);
                    uploadThePicture(url, mUri, token, etID);
                    Intent intent = new Intent(MainActivity.this, FileSizeWatcherService.class);
                    startService(intent);
                    if(spRoles.getSelectedItem().equals("Learner")) {
                        learnerTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                checkTheUserHasRegistered(url, token);
                            }
                        }, 1000 );
                    } else {
                        startGNUInstaller();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void checkTheUserHasRegistered(String url, String[] token) {
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
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




    private void uploadThePicture(String url, Uri uri, String[] token, TextView etID) throws IOException {
        File file = new File(uri.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        toast.show(); // show that its uploading
        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.d(TAG, "uploadThePicture: the encoding" + encodedFile);
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
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


    private void getYearId(String url, String[] token) {
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
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
                            s.edit().putInt("yearId", yearId).commit();
                            Log.d(TAG, "onResponse: Commit [year ID:]" + yearId);
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

    @SuppressLint("WrongConstant")
    public int getYear() {
        Calendar now = Calendar.getInstance();   // Gets the current date and time
        return now.get(Calendar.YEAR);

    }

    private void getSchoolIP(String url, String token) {
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
                .setPriority(Priority.HIGH)
                .addHeaders("X-CSRF-Token", token.equals("") ? "oSJwTj-O1J99uiMvnvtNEQgV9uqoUjQJoct6WYytwbA" : token)
                .addBodyParameter("message", "get:school_ip:"+ selectedSchoolID )
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String schoolIP = response.getString("school_ip");
                            sIp = Integer.parseInt(schoolIP);
                            s.edit().putInt("sIp", sIp).commit();
                            Log.d(TAG, "onResponse: COMMIT MAN: " + sIp);
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
        s.edit().putInt("school_id", selectedSchoolID).commit();
        Log.d(TAG, "onItemClick: hello thete : " + selectedSchoolID);
        getSchoolIP(url, token[0]);
        getYearId(url, token);
    }
    private void getNameAndSurname(String url, String[] token, final TextView tvName,final TextView etSurname, TextView etID) {
        Log.d(TAG, "run: in here man:" );
        AndroidNetworking.post(url + "/drupalgap/school_lms_resources/user_details_service.json")
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
                            s.edit().putInt("userId", userId).commit();
                            Log.d(TAG, "onResponse: resposnse name:" + name + "surane: " + surname);
                            Log.d(TAG, "onResponse: userID: " + userId);
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


}
