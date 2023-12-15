package org.example.eoliiri.proyecto_3a;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {
    TextView co2,temperatura,riesgo;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        co2=findViewById(R.id.CO2);
        temperatura=findViewById(R.id.Temp);
        riesgo=findViewById(R.id.nivelRiesgo);




        /*co2.setText(co2p);
        temperatura.setText(tempp);
        riesgo.setText();*/
    }
}