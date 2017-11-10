package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mbreen on 28/10/2016.
 */

public class OnclickPushActivity extends Activity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        Intent i = getIntent().getParcelableExtra("mIntent");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            finishAndRemoveTask();
        }
        else
        {
            finish();
        }
    }

}
