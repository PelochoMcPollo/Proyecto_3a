package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VerificarActivity extends AppCompatActivity {

    EditText inputcodigo;
    String url = "http://192.168.1.49/proyecto_3a/src/api/registro.php";
    String email, password, nombre, telefono, clave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verficar_registro);

        inputcodigo = findViewById(R.id.codigoinput);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        nombre = intent.getStringExtra("nombre");
        telefono = intent.getStringExtra("telefono");
        clave = intent.getIntExtra("clave", 0) + "";
        Log.d("CLAVE", clave);

    }

    public void Verificar(View view){
        String codigo = inputcodigo.getText().toString().trim();
        if (codigo.isEmpty()){
            inputcodigo.setError("Introduce el código");
        }else if (codigo.equals(clave)){
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(VerificarActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificarActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(VerificarActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> parametros = new HashMap<String,String>();
                    parametros.put("email",email);
                    parametros.put("contrasenya",password);
                    parametros.put("nombreyapellidos",nombre);
                    parametros.put("telefono",telefono);
                    return parametros;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }else{
            // Si el código no es correcto
            inputcodigo.setError("Código incorrecto");
        }
    }
}
