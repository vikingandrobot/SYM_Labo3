package ch.heigvd.iict.sym.labo3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * This Activity provides an authentication with different modes
 *      - Password only
 *      - Tag NCF only
 *      - Password AND Tag NCF
 *
 * By default password or NFC is required. And a switch allows a secure mode where both password
 * and password is required to log in.
 *
 * The implementation of this class is heavily inspired by
 * https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 */
public class NfcActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private boolean secureMode;

    // UI elements
    private EditText passwordEditText;
    private Switch secureSwitch;
    private Button loginButton;

    // This is bad
    private final String PASSWORD = "mypassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get the UI elements
        this.passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        this.secureSwitch = (Switch) findViewById(R.id.secureSwitch);
        this.loginButton = (Button) findViewById(R.id.loginButton);

        // By default secure mode is disabled
        this.secureMode = false;


        // Get NFC and check device support
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(null == nfcAdapter) {
            Toast.makeText(this, "This device doesn't support NFC", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        /**
         * Listener for secure mode switch.
         *
         * If the switch is checked, hide the login button.
         * Otherwise, display the login button.
         */
        secureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Check if the NFC adapter is enabled
                if (!nfcAdapter.isEnabled()) {
                    Toast.makeText(NfcActivity.this, "Please enable the NFC first", Toast.LENGTH_LONG).show();
                    buttonView.setChecked(false);
                    loginButton.setVisibility(View.VISIBLE);
                    return;
                }

                if(isChecked) {
                    loginButton.setVisibility(View.GONE);
                }
                else {
                    loginButton.setVisibility(View.VISIBLE);
                }

                secureMode = isChecked;
            }
        });


        /**
         * Listener for the login button (Authentication without NFC).
         *
         * When the button is clicked, we check the password and login in.
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!secureMode && passwordEditText.getText().toString().equals(PASSWORD)) {
                    System.out.println("Logged with normal mode");

                    login();
                }
                else {
                    Toast.makeText(NfcActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        // Deregister
        stopForegroundDispatch(this, nfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handle when the user attaches a Tag to the device.
     *
     * @param intent the new intent to handle
     */
    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        // Check for NDEF type
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            // Get the type
            String type = intent.getType();

            if("text/plain".equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            }
            else {
                Log.d("Error log", "Wrong mime type: " + type);
            }
        }
    }

    /**
     * Register the activity to receive the tag.
     *
     * @param activity The activity requesting the forground dispatch.
     * @param adapter The adapter used for the forground dispatch.
     */
    private void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        Intent intent = new Intent(
            activity,
            activity.getClass()
        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Populates with the details of the tag
        PendingIntent pendingIntent = PendingIntent.getActivity(
            activity.getApplicationContext(), 0, intent, 0
        );

        // Intent filters to handle the intents that we want to intercept
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            filter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        IntentFilter[] filters = new IntentFilter[] {filter};

        // Tech that we want to handle
        String[][] techList = new String[][] { new String[] { Ndef.class.getName() } };

        // Set all the settings
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * Unregister the activity to receive the tag.
     *
     * @param activity The activity requesting the forground dispatch.
     * @param adapter The adapter used for the forground dispatch.
     */
    private void stopForegroundDispatch(Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Handle the login activity
     */
    private void login() {
        Intent intent = new Intent(NfcActivity.this, AuthenticatedActivity.class);
        intent.putExtra("level", AuthenticatedActivity.Level.AUTHENTICATE_MAX.level());
        startActivity(intent);
    }

    /**
     * Inner class NdefReaderTask
     *
     * Allow to reading the data in background.
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            // NDEF is not supported by this Tag.
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e("Error log", "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {

            // Check the correct content of the tag
            if (result != null && result.equals("test")) {
                System.out.println("Read content: " + result);

                // Check for secure mode
                if(secureMode) {

                    // Check the password
                    if(passwordEditText.getText().toString().equals(PASSWORD)) {
                        System.out.println("Logged with secure mode");

                        login();
                    }
                    else {
                        Toast.makeText(NfcActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    System.out.println("Logged with normal mode with NFC only");

                    login();
                }
            }
            else {
                Toast.makeText(NfcActivity.this, "Wrong NFC", Toast.LENGTH_LONG).show();
            }
        }
    }
}
