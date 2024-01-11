package org.example.eoliiri.proyecto_3a;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainPage extends AppCompatActivity {
    TextView co2,temperatura,riesgo;
    ImageView co2Image;
    HorizontalProgressBar progressBar;
    private double co2Valor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        co2=findViewById(R.id.CO2);
        temperatura=findViewById(R.id.Temp);
        riesgo=findViewById(R.id.nivelRiesgo);
        co2Image=findViewById(R.id.colorCo2);
        progressBar=findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar.setProgress(1400);
        /*co2.setText(co2p);
        temperatura.setText(tempp);
        riesgo.setText();*/

        if(co2Valor<400){
            co2Image.setImageResource(R.drawable.verde);
        } else if (co2Valor>400&&co2Valor<100) {
            co2Image.setImageResource(R.drawable.amarillo);
        } else if (co2Valor>1000) {
            co2Image.setImageResource(R.drawable.rojo);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acercaDe) {
            //lanzarAcercaDe(null);
            return true;
        }
        if (id == R.id.menu_perfil) {
            //lanzarEditarPerfil(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

}