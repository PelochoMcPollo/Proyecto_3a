package org.example.eoliiri.proyecto_3a;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {
    Button confirmar;
    Button cancelar;
    TextInputLayout correo, contrasenya, nombre, telefono, confirmarContrasenya;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerr);
        confirmar = findViewById(R.id.confirmarr);
        //cancelar =findViewById(R.id.cancelar);
        correo = findViewById(R.id.til_correo2);
        contrasenya = findViewById(R.id.til_pass2);
        nombre = findViewById(R.id.til_nombre);
        telefono = findViewById(R.id.til_telefono);
        confirmarContrasenya = findViewById(R.id.til_confirmarcontrasenya);

        // Configura un escuchador para el botón de inicio de sesión
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llama al método Validar con la URL del servidor como argumento

                Validar("http://192.168.148.194/proyecto_3a/src/api/enviaremailregistroapp.php");
            }
        });

    }

    public void abrirTerminos(View view) {
        startActivity(new Intent(this, TerminosPrivacidad.class));
    }


    private void Validar(String URL) {
        // Obtiene los valores de los campos ingresados por el usuario
        String email = correo.getEditText().getText().toString().trim();
        String password = contrasenya.getEditText().getText().toString().trim();
        String confirmPassword = confirmarContrasenya.getEditText().getText().toString().trim();
        String name = nombre.getEditText().getText().toString().trim();
        String phone = telefono.getEditText().getText().toString().trim();
        Random random = new Random();
        int min = 10000000; // El menor número de 8 cifras
        int max = 99999999; // El mayor número de 8 cifras
        int clave = random.nextInt(max - min + 1) + min;
        String codigo = encriptar(String.valueOf(clave));

        // Verifica si algún campo está vacío
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            // Muestra un mensaje de error en el TextInputLayout correspondiente
            if (email.isEmpty()) {
                correo.setError("Campo de correo vacío");
            } else correo.setError(null);
            if (password.isEmpty()) {
                contrasenya.setError("Campo de contraseña vacío");
            } else contrasenya.setError(null);
            if (confirmPassword.isEmpty()) {
                confirmarContrasenya.setError("Campo de confirmación de contraseña vacío");
            } else confirmarContrasenya.setError(null);
            if (name.isEmpty()) {
                nombre.setError("Campo de nombre vacío");
            } else nombre.setError(null);
            if (phone.isEmpty()) {
                telefono.setError("Campo de teléfono vacío");
            } else telefono.setError(null);
        } else if (!password.equals(confirmPassword)) {
            // Si las contraseñas no coinciden, muestra un mensaje de error en el TextInputLayout correspondiente
            confirmarContrasenya.setError("Las contraseñas no coinciden");
        } else {

            // Si todos los campos están llenos, procede con la solicitud HTTP y el registro
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Maneja la respuesta del servidor
                    // Muestra un mensaje de registro exitoso y redirige al usuario
                    Toast.makeText(RegisterActivity.this, "Email de verificación enviado", Toast.LENGTH_SHORT).show();
                    lanzarVerificar(email, password, name, phone, clave);
                    //Toast.makeText(RegisterActivity.this, "Registro de sesión correcto", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Maneja errores en la solicitud HTTP
                    // Muestra un mensaje de error en el TextInputLayout y registra un mensaje de error en el registro de Android
                    Toast.makeText(RegisterActivity.this,"Error al mandar el email de verificación", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Define los parámetros que se enviarán en la solicitud HTTP
                    int numero = (int) (Math.random() * 1000000) + 1;
                    encriptar(String.valueOf(numero));
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("email", email);
                    parametros.put("codigo", codigo);
                    return parametros;
                }
            };
            // Crea una cola de solicitudes Volley y agrega la solicitud para que se ejecute
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    public void lanzarVerificar(String email, String password, String name, String phone, int clave) {
        Intent intent = new Intent(this, VerificarActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("nombre", name);
        intent.putExtra("telefono", phone);
        intent.putExtra("clave", clave);
        startActivity(intent);
    }

    public static String encriptar(String texto) {
        StringBuilder textoEncriptado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            int valorAscii = (int) texto.charAt(i);
            // Simplemente sumamos 1 al valor ASCII (puedes usar cualquier operación)
            textoEncriptado.append((char) (valorAscii + 1));
        }
        return textoEncriptado.toString();
    }
}