package student.op.mehelp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

public class HowToUseActivity extends AppCompatActivity {

    public static void showPopupHelp(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
// Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
// Inflate and set the layout for the dialog
// Pass null as the parent view because its going in the dialog
// layout
        builder.setView(inflater.inflate(R.layout.activity_how_to_use, null));
        AlertDialog ad = builder.create();
        ad.setTitle("MeHelp How to Use");
        ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        ad.show();
        TextViewWithImages txtView = (TextViewWithImages) ad.findViewById(R.id.textView);
        txtView.setText(R.string.text_how_to_use);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
    }
}
