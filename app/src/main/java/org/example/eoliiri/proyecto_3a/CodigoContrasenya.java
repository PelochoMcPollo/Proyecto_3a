package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CodigoContrasenya extends AppCompatActivity {
    String correo,codigo,inputcodigo;
    EditText codigoinput;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codigo_contrasenya);

        codigoinput = (EditText) findViewById(R.id.codigoinput);

        Intent intent = getIntent();
        correo = intent.getStringExtra("email");
        codigo = intent.getStringExtra("codigo");
    }
    public  void Verificar(View view){
        inputcodigo = codigoinput.getText().toString();


        if(inputcodigo.equals(codigo))
        {
            Intent intent = new Intent(CodigoContrasenya.this, RecuperarContrasenya.class);
            intent.putExtra("email",correo);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(CodigoContrasenya.this,"El codigo no coincide",Toast.LENGTH_SHORT).show();
        }
    }

}
