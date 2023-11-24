package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnviarMailContrasenya extends AppCompatActivity {
    String correo;
    TextInputLayout inputcorreo;
    Random random = new Random();
    int min = 10000000; // El menor número de 8 cifras
    int max = 99999999; // El mayor número de 8 cifras
    int numeroAleatorio = random.nextInt(max - min + 1) + min;
    String codigo;

    String URL = "http://192.168.148.194/proyecto_3a/src/api/enviaremailcontrasenyaapp.php";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.envair_mail_contrasenya);
        inputcorreo = (TextInputLayout) findViewById(R.id.correo);
    }

    public void cancelar(View view){
        startActivity(new Intent(EnviarMailContrasenya.this,LoginActivity.class));
        finish();
    }
    public void enviar(View view){
        inputcorreo.setError(null);
        correo = inputcorreo.getEditText().getText().toString();
        codigo = encriptar(String.valueOf(numeroAleatorio));
        if(!correo.isEmpty())
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("hola",response.toString());
                    if (!response.equals("El correo no tiene una cuenta creada")) {
                        Toast.makeText(EnviarMailContrasenya.this,"Se ha enviado un correo a " + correo,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EnviarMailContrasenya.this, CodigoContrasenya.class);
                        intent.putExtra("email",correo);
                        intent.putExtra("codigo",String.valueOf(numeroAleatorio));
                        startActivity(intent);
                        //finish();
                    } else {
                        // Si el correo o la contraseña son incorrectos, muestra un mensaje de error en el TextInputLayout
                        inputcorreo.setError("Este usuario no existe");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(EnviarMailContrasenya.this,"Error al enviar correo",Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Define los parámetros que se enviarán en la solicitud HTTP (correo y contraseña)
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("email", correo);
                    parametros.put("codigo", codigo);
                    return parametros;
                }
            };

            // Crea una cola de solicitudes Volley y agrega la solicitud para que se ejecute
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            }
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

