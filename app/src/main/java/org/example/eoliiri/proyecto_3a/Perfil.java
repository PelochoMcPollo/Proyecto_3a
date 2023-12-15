package org.example.eoliiri.proyecto_3a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Perfil extends AppCompatActivity {
    TextView texto;
    Button btlogout;

    private SesionManager sesionManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        sesionManager = new SesionManager(this);

        texto = findViewById(R.id.textView2);
        btlogout = findViewById(R.id.btlogout);
        String email = sesionManager.getEmail();
        String password = sesionManager.getPassword();
        texto.setText(email + " " + password);
    }

    public void cerrarSesion(View view) {
        sesionManager.cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void lanzarEditar(View view) {
        Intent intent = new Intent(this, EditarPerfil.class);
        startActivity(intent);
    }
}
