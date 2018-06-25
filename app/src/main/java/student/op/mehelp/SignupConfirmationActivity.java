package student.op.mehelp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class SignupConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirmation);

        final Context context = this;

        EditText txtVerificationCode = (EditText) findViewById(R.id.txtVerificationCode);
        txtVerificationCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0 && s.length() >= 6) {
                    Intent intent = getIntent();
                    String code = intent.getStringExtra("code");
                    Toast.makeText(context, code + " " + s.toString(), Toast.LENGTH_LONG).show();
                    if (code.compareTo(s.toString()) == 0) {
                        String name = intent.getStringExtra(DbHelper.PERSONAL_COLUMN_NAME);
                        String address = intent.getStringExtra(DbHelper.PERSONAL_COLUMN_ADDRESS);
                        String phone = intent.getStringExtra(DbHelper.PERSONAL_COLUMN_PHONE);
                        String blood = intent.getStringExtra(DbHelper.PERSONAL_COLUMN_BLOOD);
                        String foodRisk = intent.getStringExtra(DbHelper.PERSONAL_COLUMN_FOODRISK);
                        byte[] imgArray = intent.getByteArrayExtra(DbHelper.PERSONAL_COLUMN_IMAGE);
                        //ImageManager imgMgr=new ImageManager();
                        //String path=imgMgr.compressImage(Uri.parse(imgArray),context);
                        //Toast.makeText(context, String.valueOf(imgArray.length), Toast.LENGTH_LONG).show();

                        MainActivity.db.insertPersonalInfo(name, address, phone, blood, foodRisk, imgArray);
                        Toast.makeText(context, "Registration Successful...!!", Toast.LENGTH_SHORT).show();

                        Intent mainActIntent = new Intent(context, MainActivity.class);
                        startActivity(mainActIntent
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    } else {
                        Toast.makeText(context, "Verification Code doesn't match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
