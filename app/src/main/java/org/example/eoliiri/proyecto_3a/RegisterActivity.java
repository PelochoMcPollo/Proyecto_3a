package org.example.eoliiri.proyecto_3a;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {
    Button confirmar;
    Button cancelar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        confirmar=findViewById(R.id.confirmar);
        cancelar =findViewById(R.id.cancelar);
    }
}