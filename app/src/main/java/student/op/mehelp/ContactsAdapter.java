package student.op.mehelp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by varun on 2017-09-18.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> implements ListAdapter {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    LocationManager locationManager;
    Context thisContext;
    LayoutType layoutType;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, LayoutType layoutType) {
        //int layout=(layoutType==LayoutType.ContactEdit)?R.layout.contact_card_layout:R.layout.contact_edit_card_layout;
        super(context, (layoutType == LayoutType.ContactView) ? R.layout.contact_card_layout : R.layout.contact_edit_card_layout, contacts);
        this.layoutType = layoutType;
        thisContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    (layoutType == LayoutType.ContactView) ? R.layout.contact_card_layout : R.layout.contact_edit_card_layout, parent, false);
        }

        final ImageView contactImg = (ImageView) convertView.findViewById(R.id.contactImg);
        Bitmap bmp = ImageManager.getImage(contact.image);
        contactImg.setImageBitmap(bmp);

        if (layoutType == LayoutType.ContactView) {
            ImageView btnCall = (ImageView) convertView.findViewById(R.id.btnCall);
            btnCall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "calling " + contact.phoneNumber, Toast.LENGTH_LONG).show();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + contact.phoneNumber));
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    v.getContext().startActivity(callIntent);
                }
            });

            ImageView btnMsg = (ImageView) convertView.findViewById(R.id.btnText);
            btnMsg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager = (LocationManager) thisContext.getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MINIMUM_TIME_BETWEEN_UPDATES,
                            MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                            new GPSLocationManager());
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    criteria.setPowerRequirement(Criteria.POWER_LOW);
                    String bestProvider = locationManager.getBestProvider(criteria, true);
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    if (location == null) {
                        Toast.makeText(thisContext, "GPS signal not found", Toast.LENGTH_SHORT).show();
                    } else {
                        String locationURL = "I'm in trouble, Please call me. It's urgent. I'm at (http://maps.google.com/maps?q=" + String.valueOf(location.getLatitude()) +
                                "," + String.valueOf(location.getLongitude()) + "&ll=" + String.valueOf(location.getLatitude()) +
                                "," + String.valueOf(location.getLongitude()) + "&z=14)";
                        sendSMSMessage(contact.phoneNumber, locationURL);
                    }
                }
            });
        }

        if (layoutType == LayoutType.ContactEdit) {
            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.editbtn);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent editIntent = new Intent(thisContext, AddContactActivity.class);
                    editIntent.putExtra(DbHelper.CONTACTS_COLUMN_ID, String.valueOf(contact.id));
                    /*editIntent.putExtra(DbHelper.CONTACTS_COLUMN_NAME, contact.name);
                    editIntent.putExtra(DbHelper.CONTACTS_COLUMN_PHONE, contact.phoneNumber);
                    editIntent.putExtra(DbHelper.CONTACTS_COLUMN_RELATION, contact.relation);
                    editIntent.putExtra(DbHelper.CONTACTS_COLUMN_IMAGE, contact.image);*/
                    v.getContext().startActivity(editIntent);
                }
            });

            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.deletebtn);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    new AlertDialog.Builder(thisContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete Contact..??")
                            .setMessage("Are you sure you want to delete this contact?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.db.deleteContact(contact.id);
                                    Intent deletedContactIntent = new Intent(v.getContext(), DeleteResultActivity.class);
                                    v.getContext().startActivity(deletedContactIntent);
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }
        return convertView;
    }

    protected void sendSMSMessage(String phoneNo, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(thisContext.getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }

    public enum LayoutType {
        ContactView,
        ContactEdit
    }

    public class GPSLocationManager implements LocationListener {

        public void onLocationChanged(Location loc) {
            double lat = loc.getLatitude();
            double longi = loc.getLongitude();
        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}

