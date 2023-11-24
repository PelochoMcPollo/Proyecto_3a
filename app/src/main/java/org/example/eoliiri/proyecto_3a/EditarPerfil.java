package org.example.eoliiri.proyecto_3a;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class EditarPerfil extends AppCompatActivity {
    RequestQueue requestQueue;  // Cola de solicitudes para comunicarse con el servidor
    TextInputEditText nombre, telefono, email;  // Campos de entrada para el nombre, teléfono y correo electrónico
    String nombreguardar, telefonoguardar, emailguardar;  // Variables para guardar los valores editados
    Button guardar;  // Botón para guardar los cambios

    private SesionManager sesionManager;  // Maneja la información de la sesión del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);  // Establece la vista de la actividad
        sesionManager = new SesionManager(this);  // Inicializa el gestor de sesión
        requestQueue = Volley.newRequestQueue(this);  // Inicializa la cola de solicitudes para Volley
        nombre = findViewById(R.id.nombre);  // Obtiene la referencia al campo de nombre desde la vista
        telefono = findViewById(R.id.telefono);  // Obtiene la referencia al campo de teléfono desde la vista
        email = findViewById(R.id.email);  // Obtiene la referencia al campo de correo electrónico desde la vista
        Server.modificarTextos(requestQueue, nombre, telefono, email, sesionManager.getEmail());  // Carga los datos del perfil del usuario en la interfaz de usuario
        guardar = findViewById(R.id.botonguardar);  // Obtiene la referencia al botón "Guardar" desde la vista
        Log.d("Hola",sesionManager.getEmail());
    }

    public void guardar(View v) {
        nombreguardar = nombre.getText().toString().trim();  // Obtiene el nombre editado
        telefonoguardar = telefono.getText().toString().trim();  // Obtiene el teléfono editado
        emailguardar = email.getText().toString().trim();  // Obtiene el correo electrónico editado
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  // Crea un cuadro de diálogo de confirmación
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Desa Confirmar estos cambios?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                aceptar();  // Si se confirma, llama al método "aceptar" para guardar los cambios
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                cancelar();  // Si se cancela, llama al método "cancelar" (actualmente comentado)
            }
        });
        dialogo1.show();  // Muestra el cuadro de diálogo de confirmación
    }

    public void aceptar() {
        Server.actualizarusuario(requestQueue, nombreguardar, emailguardar, sesionManager.getEmail());  // Actualiza la información del usuario en el servidor
        Server.actualizarTelefono(requestQueue, telefonoguardar, sesionManager.getEmail());  // Actualiza el teléfono del usuario en el servidor
        sesionManager.setEmail(emailguardar);
        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();  // Muestra un mensaje de "Cambios guardados"
        finish();  // Finaliza la actividad y regresa a la pantalla anterior
    }

    public void cancelar() {
        //dialogo.show().dismiss();
    }

    public void Volver(View v) {
        finish();  // Finaliza la actividad y regresa a la pantalla anterior
    }
    public void cambiarcontrasenya(View v) {
        startActivity(new Intent(EditarPerfil.this, CambiarContrasenya.class));
        finish();
    }
}
