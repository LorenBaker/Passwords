package lbconsulting.com.passwords.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PasswordsIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PASSWORD_LONGEVITY = "lbconsulting.com.passwords.services.ACTION_PASSWORD_LONGEVITY";
    //private static final String ACTION_BAZ = "lbconsulting.com.passwords.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_LONGEVITY_MIN = "lbconsulting.com.passwords.services.EXTRA_LONGEVITY_MIN";
    //private static final String EXTRA_PARAM2 = "lbconsulting.com.passwords.services.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPasswordLongevity(Context context, int longevityMin) {
        Intent intent = new Intent(context, PasswordsIntentService.class);
        intent.setAction(ACTION_PASSWORD_LONGEVITY);
        intent.putExtra(EXTRA_LONGEVITY_MIN, longevityMin);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
/*    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, PasswordsIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/
    public PasswordsIntentService() {
        super("PasswordsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PASSWORD_LONGEVITY.equals(action)) {
                final int longevityMin = intent.getIntExtra(EXTRA_LONGEVITY_MIN, MySettings.DEFAULT_LONGEVITY_MIN);
                handleActionPasswordLongevity(longevityMin);
            }
/*            else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPasswordLongevity(int longevityMin) {
       // Toast.makeText(this, "PasswordsIntentService started.", Toast.LENGTH_SHORT).show();
        MyLog.d("PasswordsIntentService", "handleActionPasswordLongevity Started.");
        int waitPeriodMs = longevityMin * 60000;
        try {
            Thread.sleep(waitPeriodMs);
            MyLog.d("PasswordsIntentService", "handleActionPasswordLongevity. " + longevityMin + " min elapsed.");
            //Toast.makeText(this, "PasswordsIntentService. " + longevityMin + " min has elapsed.", Toast.LENGTH_SHORT).show();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
/*    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }*/
}
