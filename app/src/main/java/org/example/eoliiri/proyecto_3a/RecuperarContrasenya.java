package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

public class RecuperarContrasenya extends AppCompatActivity {
    String contrasenya,cofirmarcontrasenya,correo;
    RequestQueue requestQueue;
    TextInputLayout contrasenyainput,confirmarcontrasenyainput;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_contrasenya);
        contrasenyainput = (TextInputLayout) findViewById(R.id.contrasenya);
        confirmarcontrasenyainput   = (TextInputLayout) findViewById(R.id.confirmarcontrasenya);
        requestQueue = Volley.newRequestQueue(this);
        Intent intent  = getIntent();
        correo = intent.getStringExtra("email");
    }

    public void enviar(View view){
        if(validar())
        {
            Server.recuperContrasenya(requestQueue,contrasenya,cofirmarcontrasenya,correo);
            Toast.makeText(RecuperarContrasenya.this,"Contrasenya Actualizada",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RecuperarContrasenya.this,LoginActivity.class));
            finish();
        }
    }
    public  void cancelar(View view)
    {
        startActivity(new Intent(RecuperarContrasenya.this,LoginActivity.class));
        finish();
    }
    public  boolean validar()
    {
        contrasenyainput.setError(null);
        confirmarcontrasenyainput.setError(null);
        boolean s = true;
        contrasenya = contrasenyainput.getEditText().getText().toString();
        cofirmarcontrasenya = confirmarcontrasenyainput.getEditText().getText().toString();
        if(contrasenya.isEmpty() || cofirmarcontrasenya.isEmpty())
        {
            if(contrasenya.isEmpty())
            {
                contrasenyainput.setError("Complete este campo");
                s=false;
            }else
            {
                contrasenyainput.setError(null);
            }

            if(cofirmarcontrasenya.isEmpty())
            {
                confirmarcontrasenyainput.setError("Complete este campo");
                s=false;
            }else
            {
                confirmarcontrasenyainput.setError(null);
            }

        }
        else
        {
            if(!contrasenya.equals(cofirmarcontrasenya))
            {
                confirmarcontrasenyainput.setError("La contraseña no coincide");
                s=false;
            }
        }

        return s;
    }
}
