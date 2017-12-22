package ch.heigvd.iict.sym.labo3;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Authentication page. This page provides some sensitive data (No data for this example).
 * A level of security is available that provide a timeout :
 *      AUTHENTICATE_MAX : 3 min (By default)
 *      AUTHENTICATE_MEDIUM : 5 min
 *      AUTHENTICATE_LOW : 10 min
 *
 * After that, the activity is destroyed and the data is no more available.
 */
public class AuthenticatedActivity extends AppCompatActivity {

    // Level available
    public enum Level {
        AUTHENTICATE_MAX("max", 3),
        AUTHENTICATE_MEDIUM("medium", 5),
        AUTHENTICATE_LOW("low", 10);

        private String level;
        private int time;

        private Level(String level, int time) {
            this.level = level;
            this.time = time;
        }

        public String level() {
            return level;
        }

        public int time() {
            return time;
        }
    }

    private TextView levelTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get the UI elements
        this.levelTextView = (TextView) findViewById(R.id.levelTextView);


        // Get the level (By default MAX if no level)
        Level level = Level.AUTHENTICATE_MAX;

        Bundle bd = getIntent().getExtras();
        if(bd != null) {
            String l = (String) bd.get("level");

            if(l.equals(Level.AUTHENTICATE_MEDIUM.level())) {
                level = Level.AUTHENTICATE_MEDIUM;
            }
            else if(l.equals(Level.AUTHENTICATE_LOW.level())) {
                level = Level.AUTHENTICATE_MEDIUM;
            }
        }


        // Display the level selected
        levelTextView.setText("Authentication level : " + level);


        // Set a timer to destroy the activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(AuthenticatedActivity.this, "Authentication Timeout", Toast.LENGTH_LONG).show();
                finish();
            }
        }, 60000 * level.time());
    }
}
