package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilCorreo, tilContraseña;
    Button btlogin;

    // Método que se ejecuta cuando se crea la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el diseño de la interfaz de usuario para esta actividad
        setContentView(R.layout.login);
        // Asigna instancias de elementos de interfaz de usuario a variables
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_pass);
        btlogin = (Button) findViewById(R.id.btlogin);

        // Configura un escuchador para el botón de inicio de sesión
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama al método Validar con la URL del servidor como argumento
                Validar("http://192.168.1.49/proyecto_3a/src/api/validarusuario.php");
            }
        });
    }

    // Método para validar el inicio de sesión
    private void Validar(String URL) {
        // Obtiene el correo y la contraseña ingresados por el usuario
        String email = tilCorreo.getEditText().getText().toString().trim();
        String password = tilContraseña.getEditText().getText().toString().trim();

        // Crea una solicitud HTTP de tipo POST usando Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Maneja la respuesta del servidor

                // Si el correo no está vacío, muestra un mensaje de inicio de sesión correcto y redirige al usuario
                if (!email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    // Si el correo está vacío, muestra un mensaje de error en el TextInputLayout
                    tilCorreo.setError("Usuario o contraseña incorrectos");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Maneja errores en la solicitud HTTP
                // Muestra un mensaje de error en el TextInputLayout y registra un mensaje de error en el registro de Android
                tilCorreo.setError("Error al iniciar sesion");
                Log.e("TAG", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Define los parámetros que se enviarán en la solicitud HTTP (correo y contraseña)
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("email", email);
                parametros.put("password", password);
                return parametros;
            }
        };
        // Crea una cola de solicitudes Volley y agrega la solicitud para que se ejecute
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
