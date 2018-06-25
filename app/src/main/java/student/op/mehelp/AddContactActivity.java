package student.op.mehelp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;


public class AddContactActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 2;
    ImageView contactImg;
    Uri selectedImage;
    DbHelper db;
    boolean isEditing = false;
    int id = 0;
    ImageManager imgManager;
    Uri mUri;
    private LruCache<String, Bitmap> mMemoryCache;
    private String TAG = "Contacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        id = 0;

        db = new DbHelper(this);
        Intent intent = getIntent();
        imgManager = new ImageManager();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

// Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


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

        contactImg = (ImageView) findViewById(R.id.contactImg);
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
                HowToUseActivity.showPopupHelp(AddContactActivity.this);
            }
        });

        contactImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveContact();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calcelContactIntent = new Intent(context, SaveCancelActivity.class);
                startActivity(calcelContactIntent);
            }
        });

        String idStr = intent.getStringExtra(DbHelper.CONTACTS_COLUMN_ID);
        if (idStr != null && idStr != "") {
            id = Integer.valueOf(intent.getStringExtra(DbHelper.CONTACTS_COLUMN_ID));
            Contact contact = MainActivity.db.getContact(id);
            //String name=intent.getStringExtra(DbHelper.CONTACTS_COLUMN_NAME);
            if (contact.name != null && contact.name != "") {
                isEditing = true;
                EditText txtName = (EditText) findViewById(R.id.txtName);
                //String idStr=intent.getStringExtra(DbHelper.CONTACTS_COLUMN_ID);
                //Toast.makeText(this,idStr,Toast.LENGTH_LONG).show();
                //id = Integer.valueOf(idStr);
                txtName.setText(contact.name);
            }
            //String phone=intent.getStringExtra(DbHelper.CONTACTS_COLUMN_PHONE);
            if (contact.phoneNumber != null && contact.phoneNumber != "") {
                isEditing = true;
                EditText txtPhone = (EditText) findViewById(R.id.txtPhone);
                txtPhone.setText(contact.phoneNumber);
            }
            //String relation=intent.getStringExtra(DbHelper.CONTACTS_COLUMN_RELATION);
            if (contact.relation != null && contact.relation != "") {
                isEditing = true;
                EditText txtRelation = (EditText) findViewById(R.id.txtRelation);
                txtRelation.setText(contact.relation);
            }
            byte[] imgArray = contact.image;//intent.getByteArrayExtra(DbHelper.CONTACTS_COLUMN_IMAGE);
            if (imgArray != null) {
                Bitmap bmp = ImageManager.getImage(imgArray);
                isEditing = true;
                contactImg.setImageBitmap(bmp);
            }
        }

        ImageView btnContactList = (ImageView) findViewById(R.id.btnContactList);
        btnContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSingleContact();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImage = mUri;//imageReturnedIntent.getData();
                    Glide.with(this).load(selectedImage).override(200, 200).into(contactImg);
                    //contactImg.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    //selectedImage = imageReturnedIntent.getData();
                    selectedImage = imageReturnedIntent.getData();
                    Glide.with(this).load(selectedImage).override(200, 200).into(contactImg);
                    //contactImg.setImageURI(selectedImage);
                }
                break;
            case RESULT_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    contactPicked(imageReturnedIntent, this);
                }
                break;
        }
    }

    void saveContact() {
        boolean isError = false;
        EditText txtName = (EditText) findViewById(R.id.txtName);
        String name = txtName.getText().toString();
        if (name.length() == 0) {
            txtName.setError("Name is required..!!");
            isError = true;
        }

        EditText txtPhone = (EditText) findViewById(R.id.txtPhone);
        String phone = txtPhone.getText().toString();
        if (phone.length() == 0) {
            txtPhone.setError("Phone is required..!!");
            isError = true;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            txtPhone.setError("Phone number is not in correct format..!!");
            isError = true;
        }

        EditText txtRelation = (EditText) findViewById(R.id.txtRelation);
        String relation = txtRelation.getText().toString();
        if (relation.length() == 0) {
            txtRelation.setError("Relation is required..!!");
            isError = true;
        }

        if (!isError) {
            ImageView imageView = (ImageView) findViewById(R.id.contactImg);
            byte[] contactImage = ImageManager.getByteArray(R.id.contactImg, imageView, this);//.getByteArray(selectedImage,this);
            //Toast.makeText(this, String.valueOf(contactImage.length), Toast.LENGTH_LONG).show();

            //Toast.makeText(this, selectedImage.toString(),Toast.LENGTH_LONG).show();
            boolean result = false;
            if (isEditing)
                result = db.updateContact(id, name, phone, relation, contactImage);
            else
                result = db.insertContact(name, phone, relation, contactImage);
            if (result) {
                Intent savedContactIntent = new Intent(this, SaveSuccessActivity.class);
                startActivity(savedContactIntent);
            } else
                Toast.makeText(this, "Contact Save Failed...!!", Toast.LENGTH_LONG).show();
        }
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void selectSingleContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    private void contactPicked(Intent data, Context context) {

        Uri uri = data.getData();
        Log.i(TAG, "contactPicked() uri " + uri.toString());
        Cursor cursor;
        ContentResolver cr = this.getContentResolver();

        try {
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (null != cur && cur.getCount() > 0) {
                cur.moveToFirst();
                for (String column : cur.getColumnNames()) {
                    Log.i(TAG, "contactPicked() Contacts column " + column + " : " + cur.getString(cur.getColumnIndex(column)));
                }
            }

            if (cur.getCount() > 0) {
                //Query the content uri
                cursor = this.getContentResolver().query(uri, null, null, null, null);

                if (null != cursor && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (String column : cursor.getColumnNames()) {
                        Log.i(TAG, "contactPicked() uri column " + column + " : " + cursor.getString(cursor.getColumnIndex(column)));
                    }
                }

                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                EditText txtName = (EditText) findViewById(R.id.txtName);
                txtName.setText(name);
                EditText txtPhone = (EditText) findViewById(R.id.txtPhone);
                txtPhone.setText(phoneNo);
            } else {

            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
