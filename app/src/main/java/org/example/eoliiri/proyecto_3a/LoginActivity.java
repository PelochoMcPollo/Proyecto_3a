package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilCorreo, tilContraseña;
    TextView registro, recuperarcontrasenya;
    MaterialButton btlogin;
    String email, password;
    private SesionManager sesionManager;

    // Método que se ejecuta cuando se crea la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el diseño de la interfaz de usuario para esta actividad
        setContentView(R.layout.login);

        sesionManager = new SesionManager(this);

        // Asigna instancias de elementos de interfaz de usuario a variables
        tilCorreo = findViewById(R.id.til_correo);
        tilContraseña = findViewById(R.id.til_pass);
        btlogin = findViewById(R.id.confirmarr);
        btlogin.setText("Iniciar sesión");
        registro = findViewById(R.id.textView4);
        recuperarcontrasenya = (TextView) findViewById(R.id.recuperarcontrasenya);

        // Configura un escuchador para el botón de inicio de sesión
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = tilCorreo.getEditText().getText().toString().trim();
                password = tilContraseña.getEditText().getText().toString().trim();
                // Llama al método Validar con la URL del servidor como argumento
                Validar("http://192.168.148.194/proyecto_3a/src/api/validarusuario.php");
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        recuperarcontrasenya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, EnviarMailContrasenya.class));
                finish();
            }
        });
    }

    // Método para validar el inicio de sesión

    /**
     * @param URL
     */
    private void Validar(String URL) {
        // Obtiene el correo y la contraseña ingresados por el usuario

        if (email.isEmpty()) {
            tilCorreo.setError("El correo no puede estar vacío");
        } else {
            tilCorreo.setError(null);
        }

        if (password.isEmpty()) {
            tilContraseña.setError("La contraseña no puede estar vacía");
        } else {
            tilContraseña.setError(null);
        }

        if (!email.isEmpty() && !password.isEmpty()) {

            // Crea una solicitud HTTP de tipo POST usando Volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Maneja la respuesta del servidor

                    Log.d("hola", response);
                    // Si el correo no está vacío, muestra un mensaje de inicio de sesión correcto y redirige al usuario
                    if (!response.isEmpty()) {
                        sesionManager.guardarCredenciales(email, password);
                        Toast.makeText(LoginActivity.this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Perfil.class));
                        finish();
                    } else {
                        // Si el correo o la contraseña son incorrectos, muestra un mensaje de error en el TextInputLayout
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
}
