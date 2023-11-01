package org.example.eoliiri.proyecto_3a;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class EditarPerfil extends AppCompatActivity {
    RequestQueue requestQueue;
    TextInputEditText nombre,contrasenya,telefono,email;
    String nombreguardar, contrasenyaguardar, telefonoguardar, emailguardar;
    Button guardar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);
        requestQueue = Volley.newRequestQueue(this);
        nombre = (TextInputEditText) findViewById(R.id.nombre);
        contrasenya = (TextInputEditText) findViewById(R.id.contrasenya);
        telefono = (TextInputEditText) findViewById(R.id.telefono);
        email = (TextInputEditText) findViewById(R.id.email);
        Server.modificarTextos(requestQueue,nombre,contrasenya,telefono,email);
        guardar = (Button) findViewById(R.id.botonguardar);
        //nombre.setText(Server.getNombre());
        //contrasenya.setText(Server.getContrasenya());

    }

    public void guardar(View v)
    {
        nombreguardar = nombre.getText().toString().trim();
        contrasenyaguardar = contrasenya.getText().toString().trim();
        telefonoguardar = telefono.getText().toString().trim();
        emailguardar = email.getText().toString().trim();
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿ Desa Confirmar estos cambios?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();
            }
        });
        dialogo1.show();
    }

    public void aceptar()
    {
        Server.actualizarusuario(requestQueue,nombreguardar,contrasenyaguardar,emailguardar);
        Server.actualizarTelefono(requestQueue,telefonoguardar);

    }

    public void cancelar()
    {
        //dialogo.show().dismiss();
    }


}

