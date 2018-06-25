package student.op.mehelp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    public static final String MainPP_SP = "MainPP_data";
    public static final int R_PERM = 2822;
    private static final int REQUEST = 112;
    ImageView imageView;
    Uri selectedImage;
    ImageManager imgManager;
    Uri mUri;

    public static String random() {
        Random generator = new Random();
        int min = 100000;
        int max = 999999;
        int num = generator.nextInt(max - min + 1) + min;
        return String.valueOf(num);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Context context = this;
        imgManager = new ImageManager();

        SharedPreferences settings = getSharedPreferences(MainPP_SP, 0);
        HashMap<String, String> map = (HashMap<String, String>) settings.getAll();

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG", "@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS
            };


            if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("TAG", "@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
            } else {
                Log.d("TAG", "@@@ IN ELSE hasPermissions");
                //callNextActivity();
            }
        } else {
            Log.d("TAG", "@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }


        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtName = (EditText) findViewById(R.id.txtName);
                EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
                EditText txtPhone = (EditText) findViewById(R.id.txtMob);
                EditText txtBlood = (EditText) findViewById(R.id.txtBlood);
                EditText txtFoodRisk = (EditText) findViewById(R.id.txtFoodRisk);

                //String contactImage = "";
                //if(selectedImage!=null)
                byte[] contactImage = ImageManager.getByteArray(R.id.imageView, imageView, context);
                boolean isError = false;

                String name = txtName.getText().toString();
                String address = txtAddress.getText().toString();
                String phone = txtPhone.getText().toString();
                String blood = txtBlood.getText().toString();
                String foodRisk = txtFoodRisk.getText().toString();
                if (name.length() == 0) {
                    txtName.setError("Name is required..!!");
                    isError = true;
                }
                if (address.length() == 0) {
                    txtAddress.setError("Address is required..!!");
                    isError = true;
                }
                if (phone.length() == 0) {
                    txtPhone.setError("Phone is required..!!");
                    isError = true;
                } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
                    txtPhone.setError("Phone number is not in correct format..!!");
                    isError = true;
                }
                if (blood.length() == 0) {
                    txtBlood.setError("Blood Group is required");
                    isError = true;
                } else if (!isBloodGroupValid(blood, context)) {
                    txtBlood.setError("Enter Valid Blood Group");
                    isError = true;
                }

                sendSMSMessage(txtName.getText().toString(),
                        txtAddress.getText().toString(),
                        txtPhone.getText().toString(),
                        txtBlood.getText().toString(),
                        txtFoodRisk.getText().toString(),
                        contactImage, isError);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImage = mUri;//imageReturnedIntent.getData();
                    Glide.with(this).load(selectedImage).override(200, 200).into(imageView);
                    //imageView.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    //Toast.makeText(this, imageReturnedIntent.getDataString(), Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData(
                    );
                    //imageView.setImageURI(selectedImage);

                    Glide.with(this).load(selectedImage).override(200, 200).into(imageView);
                }
                break;
        }
    }

    protected void sendSMSMessage(String name, String address, String phone, String blood, String foodRisk, byte[] image, boolean isError) {
        Log.i("Send SMS", "");
        String message = "Your MeHelp verification code is ";
        String code = random();
        message += code;
        Log.i("SMS", message);
        try {
            if (!isError) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

                Intent confirmIntent = new Intent(getApplicationContext(), SignupConfirmationActivity.class);
                confirmIntent.putExtra("code", code);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_NAME, name);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_ADDRESS, address);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_PHONE, phone);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_BLOOD, blood);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_FOODRISK, foodRisk);
                confirmIntent.putExtra(DbHelper.PERSONAL_COLUMN_IMAGE, image);
                //Toast.makeText(this, String.valueOf(image.length), Toast.LENGTH_LONG).show();
                startActivity(confirmIntent
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "@@@ PERMISSIONS grant");
                    //callNextActivity();
                } else {
                    Log.d("TAG", "@@@ PERMISSIONS Denied");
                    Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean isBloodGroupValid(String bloodGroup, Context context) {
        if (bloodGroup != null) {
            String[] bloodGroups = context.getResources().getStringArray(R.array.blood_groups);
            int len = bloodGroups.length;
            for (int i = 0; i < len; i++) {
                if (bloodGroup.equalsIgnoreCase(bloodGroups[i]))
                    return true;
            }
        }
        return false;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, 1);
                                break;
                            case 1:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                                startActivityForResult(takePicture, 0);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

}
