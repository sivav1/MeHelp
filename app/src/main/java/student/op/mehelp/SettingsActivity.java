package student.op.mehelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Context context = this;
        /*ImageView menuBtn=(ImageView)findViewById(R.id.menubutton);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });
  */
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

        ImageView homeBtn = (ImageView) findViewById(R.id.homebutton);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        Button aboutMeBtn = (Button) findViewById(R.id.btnAboutMe);
        aboutMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AboutMeActivity.class);
                startActivity(intent);
            }
        });

        ImageView addContactBtn = (ImageView) findViewById(R.id.btnAddContact);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddContactActivity.class);
                startActivity(intent);
            }
        });

        ImageView helpBtn = (ImageView) findViewById(R.id.helpbutton);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowToUseActivity.showPopupHelp(SettingsActivity.this);
            }
        });

        ListView lstContacts = (ListView) findViewById(R.id.lstContacts);
        final ArrayList<Contact> contacts = MainActivity.db.getAllCotacts();
        ContactsAdapter contactsAdapter = new ContactsAdapter(this, contacts, ContactsAdapter.LayoutType.ContactEdit);
        lstContacts.setAdapter(contactsAdapter);
    }
}
