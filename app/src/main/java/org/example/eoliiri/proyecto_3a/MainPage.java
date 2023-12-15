package org.example.eoliiri.proyecto_3a;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {
    TextView co2,temperatura,riesgo;
    ImageView co2Image;
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
}