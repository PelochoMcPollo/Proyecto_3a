package org.example.eoliiri.proyecto_3a;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

public class EditarPerfil extends AppCompatActivity {
    RequestQueue requestQueue;
    TextInputEditText nombre,contrasenya,telefono;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);
        requestQueue = Volley.newRequestQueue(this);
        nombre = (TextInputEditText) findViewById(R.id.nombre);
        contrasenya = (TextInputEditText) findViewById(R.id.contrasenya);
        telefono = (TextInputEditText) findViewById(R.id.telefono);
        Server.recuperarUsuario(requestQueue,nombre,contrasenya);
        Server.recuperarTelefono(requestQueue,telefono);
        //nombre.setText(Server.getNombre());
        //contrasenya.setText(Server.getContrasenya());
    }
}
