package ch.heigvd.iict.sym.labo3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CapteursActivity extends AppCompatActivity {

    //opengl
    private OpenGLRenderer  opglr           = null;
    private GLSurfaceView m3DView         = null;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    // Sensor data to use
    private float[] gravity;
    private float[] geomagnetic;

    // Rotation matrix
    private float[] lastRotationMatrix;

    // The two listeners to use to receive data from sensors
    private SensorEventListener accelerometerListener;
    private SensorEventListener magnetometerListener;

    public CapteursActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // we need fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capteurs);


        // link to GUI
        this.m3DView = findViewById(R.id.compass_opengl);

        //we create the 3D renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravity = null;
        geomagnetic = null;
        lastRotationMatrix = new float[16];

        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                gravity = sensorEvent.values;
                if (gravity != null && geomagnetic != null)
                    computeMatrix();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        magnetometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                geomagnetic = sensorEvent.values;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };



    }

    private void computeMatrix() {
        boolean success = SensorManager.getRotationMatrix(lastRotationMatrix, null, gravity, geomagnetic);
        if (success)
            lastRotationMatrix = opglr.swapRotMatrix(lastRotationMatrix);
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(accelerometerListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(magnetometerListener, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(accelerometerListener);
        mSensorManager.unregisterListener(magnetometerListener);
    }


    /* TODO */
    // your activity need to register accelerometer and magnetometer sensors' updates
    // then you may want to call
    //  this.opglr.swapRotMatrix()
    // with the 4x4 rotation matrix, everytime a new matrix is computed
    // more information on rotation matrix can be found on-line:
    // https://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[],%20float[],%20float[],%20float[])


}
