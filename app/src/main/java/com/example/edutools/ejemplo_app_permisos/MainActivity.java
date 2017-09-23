package com.example.edutools.ejemplo_app_permisos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    LinearLayout l;
    Button btn1, btn2;
    TextView tv1, tv2;
    GoogleApiClient cliente;
    Location miubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l = (LinearLayout) findViewById(R.id.miLayout);
        btn1 = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);
        tv1 = (TextView) findViewById(R.id.textView2);
        tv2 = (TextView) findViewById(R.id.textView3);
        cliente = new GoogleApiClient.Builder(this)
        .enableAutoManage(this,this)
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aqui va a cargar nuestra latitud y longitud.
                if(miubicacion != null)
                {
                    tv1.setText(String.valueOf(miubicacion.getLatitude()));
                    tv2.setText(String.valueOf(miubicacion.getLongitude()));
                }
                else
                {
                    tv1.setText("Latitud desconocida.");
                    tv2.setText("Longitud desconocida.");
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentomapa = new Intent(MainActivity.this, MapsActivity.class);
                if(miubicacion != null)
                {
                    intentomapa.putExtra("latitud",miubicacion.getLatitude());
                    intentomapa.putExtra("longitud",miubicacion.getLongitude());
                }
                startActivity(intentomapa);
            }
        });
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        if (verificarPermisos()) {
            cargarApp();
        } else {
            solicitarPermisos();
        }
    }

    // Es la funcion que va a solicitar los permisos
    private void solicitarPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(l, "Te has olvidado de los permisos.", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            CAMERA,
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION,
                            READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE
                    }, 100);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    CAMERA,
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    private void cargarApp() {
        Toast.makeText(getApplicationContext(), "Cargar Por completo la aplicacion", Toast.LENGTH_LONG).show();
        btn1.setEnabled(true);
        btn2.setEnabled(true);
    }

    private boolean verificarPermisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED
                        || grantResults[1] == PackageManager.PERMISSION_DENIED
                        || grantResults[2] == PackageManager.PERMISSION_DENIED
                        || grantResults[3] == PackageManager.PERMISSION_DENIED
                        || grantResults[4] == PackageManager.PERMISSION_DENIED) {
                    // Este codigo solo se activa si se ah rechazado alguna peticion
                    // Codigo Opcional para abrir la configuracion de la aplicacion
                    AlertDialog.Builder ventana = new AlertDialog.Builder(MainActivity.this);
                    ventana.setTitle("Permisos Negados");
                    ventana.setMessage("Necesitas otorgar los Permisos");
                    ventana.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent configuracion = new Intent();
                            configuracion.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri direccion = Uri.fromParts("package", getPackageName(), null);
                            configuracion.setData(direccion);
                            startActivity(configuracion);
                        }
                    });
                    ventana.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Esta es una tostada, la app se cerro", Toast.LENGTH_LONG).show();
                        }
                    });
                    ventana.show();
                } else {
                    cargarApp();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Conexion establecida con Google Maps.", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        miubicacion = LocationServices.FusedLocationApi.getLastLocation(cliente);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),"se corto la conexion con Google Maps.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"No se pudo establecer conexion con Google Maps.",Toast.LENGTH_SHORT).show();
    }
}
