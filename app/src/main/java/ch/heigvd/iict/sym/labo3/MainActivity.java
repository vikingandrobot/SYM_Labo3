package ch.heigvd.iict.sym.labo3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button nfcActivity;
    private Button codesBarresActivity;
    private Button iBeaconActivity;
    private Button capteursActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcActivity = (Button) findViewById(R.id.nfcButton);
        nfcActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NfcActivity.class);
                startActivity(intent);
            }
        });

        codesBarresActivity = (Button) findViewById(R.id.codesBarresActivity);
        codesBarresActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CodesBarresActivity.class);
                startActivity(intent);
            }
        });

        iBeaconActivity = (Button) findViewById(R.id.iBeaconActivity);
        iBeaconActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), IBeaconActivity.class);
                startActivity(intent);
            }
        });

        capteursActivity = (Button) findViewById(R.id.capteursActivity);
        capteursActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CapteursActivity.class);
                startActivity(intent);
            }
        });



    }

}
