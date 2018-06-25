package student.op.mehelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        ImageView menuBtn = (ImageView) findViewById(R.id.menubutton);
        final Context context = this;
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.homebutton);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageView helpBtn = (ImageView) findViewById(R.id.helpbutton);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowToUseActivity.showPopupHelp(AboutMeActivity.this);
            }
        });

        Button btnCall111 = (Button) findViewById(R.id.btnCall111);
        btnCall111.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "calling 111", Toast.LENGTH_LONG).show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:111"));
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        ArrayList<PersonalInfo> info = MainActivity.db.getPersonalInfo();
        if (!info.isEmpty()) {
            TextView txtName = (TextView) findViewById(R.id.txtName);
            TextView txtAddress = (TextView) findViewById(R.id.txtAddress);
            TextView txtMobile = (TextView) findViewById(R.id.txtMobileNo);
            TextView txtBlood = (TextView) findViewById(R.id.txtBlood);
            TextView txtFoodRisk = (TextView) findViewById(R.id.txtFoodRisk);
            ImageView contactImg = (ImageView) findViewById(R.id.contactImg);

            PersonalInfo pInfo = info.get(0);
            txtName.setText(pInfo.name);
            txtAddress.setText(pInfo.address);
            txtMobile.setText(pInfo.phone);
            txtBlood.setText(pInfo.blood);
            txtFoodRisk.setText(pInfo.foodrisk);
            //Toast.makeText(this, String.valueOf(pInfo.image.length), Toast.LENGTH_LONG).show();
            Bitmap bmp = ImageManager.getImage(pInfo.image);
            //Uri imgUri=Uri.parse(pInfo.image);
            //Toast.makeText(this, String.valueOf(pInfo.image.length), Toast.LENGTH_LONG).show();
            contactImg.setImageBitmap(bmp);
        }
    }
}
