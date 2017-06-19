package com.ellzone.slotpuzzle2d.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.fatangare.logcatviewer.utils.LogcatViewer;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.v("App","Build Version Greater than or equal to M: " + Build.VERSION_CODES.M);
            checkDrawOverlayPermission();
        } else {
            Log.v("App","OS Version Less than M");
            //No need for Permission as less then M OS.
            startSlotPuzzle();
        }
    }

    public final static int REQUEST_CODE = -1010101;

    public void checkDrawOverlayPermission() {
        Log.v("App","Package Name: "+getApplicationContext().getPackageName());

        /** check if we already  have permission to draw over other apps**/
        if (!Settings.canDrawOverlays(this)) {
            Log.v("App","Requesting Permission"+Settings.canDrawOverlays(this));
            /** if not construct intent to request permission**/
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" +getApplicationContext().getPackageName()));
            /* request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE); //It will call onActivityResult Function After you press Yes/No and go Back after giving permission
        } else {
            Log.v("App","We already have permission for it.");
            // disablePullNotificationTouch();
            //Do your stuff, we got permission captain
            startSlotPuzzle();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        Log.v("App","OnActivity Result.");
        //check if received result code
        //  is equal our requested code for draw permission
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    //Permission Granted by Overlay!!!
                    //Do your Stuff
                    startSlotPuzzle();
                }
            }
        }
    }

    public boolean CheckPermission(Context context, String Permission) {
        return ContextCompat.checkSelfPermission(context, Permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void startSlotPuzzle() {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new SlotPuzzle(), config);
        LogcatViewer.showLogcatLoggerView(this);
    }
}
