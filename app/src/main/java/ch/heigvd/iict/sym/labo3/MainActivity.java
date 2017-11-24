package ch.heigvd.iict.sym.labo3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button nfcActivity;
    private Button codeBarresActivity;
    private Button iBeaconActivity;
    private Button capteursActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcActivity = (Button) findViewById(R.id.nfcButton);
        codeBarresActivity = (Button) findViewById(R.id.codeBarresActivity);
        iBeaconActivity = (Button) findViewById(R.id.iBeaconActivity);
        capteursActivity = (Button) findViewById(R.id.capteursActivity);

    }

}
