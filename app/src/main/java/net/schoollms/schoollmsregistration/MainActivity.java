package net.schoollms.schoollmsregistration;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import gun0912.tedbottompicker.TedBottomPicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private String[] ids;
    private String schoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] roles = {"Learner", "Support", "Teacher"};
        String[] schools = {"School A", "School B", "School C"};
        ids = new String[]{"5678", "1234", "3456"};

        Spinner spRoles = (Spinner) findViewById(R.id.spinner_roles);
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
        final Button btnUpload = (Button) findViewById(R.id.btn_upload);

        btnDone.setVisibility(View.VISIBLE);

        String role = (String) spRoles.getSelectedItem();
        //etSurname.editab
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schools);
        spSchoolName.setAdapter(adapter);
        spSchoolName.setOnItemClickListener(this);

        final String userId = etID.getText().toString();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(schoolName == null || TextUtils.isEmpty(schoolName)) {
                    spSchoolName.setError("Please enter a school to continue");
                } else if (TextUtils.isEmpty(etID.getText().toString())) {
                    etID.setError("Please enter your ID to continue");
                } else {
                    tvName.setText("John");
                    etSurname.setText("Smith");
                    Toast.makeText(MainActivity.this, "Scroll down now to validate you infomation", Toast.LENGTH_LONG).show();
                    btnDone.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });




    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        schoolName = (String) parent.getItemAtPosition(position);
    }
}
