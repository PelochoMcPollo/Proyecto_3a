package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_pass);
        btlogin = (Button) findViewById(R.id.btlogin);

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validar("http://192.168.148.194/proyecto_3a/src/api/validarusuario.php");
            }
        });
    }

    private void Validar(String URL){
        String email = tilCorreo.getEditText().getText().toString().trim();
        String password = tilContraseña.getEditText().getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                if(!email.isEmpty()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    tilCorreo.setError("Usuario o contraseña incorrectos");
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                tilCorreo.setError("Error al iniciar sesion");
                Log.e("TAG", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("email", email);
                parametros.put("password", password);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
