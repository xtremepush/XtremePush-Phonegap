package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.util.SharedPrefUtils;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public final class LocationDialogActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.location_dialog, null);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.ask_location);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Locations are disabled")
                .setCancelable(false)
                .setView(view)
                .setPositiveButton(R.string.location_providers_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, PushConnector.START_LOCATION_ACTIVITY_CODE);
                        finish();
                    }
                })
                .setNegativeButton(R.string.location_providers_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPrefUtils.setPromptTurnLocation(LocationDialogActivity.this, !checkBox.isChecked());
                        finish();
                    }
                })
                .create();

        dialog.show();
	}
}
