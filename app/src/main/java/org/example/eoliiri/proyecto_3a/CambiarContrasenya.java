package org.example.eoliiri.proyecto_3a;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class CambiarContrasenya extends AppCompatActivity {
    String contrasenyaactual,nuevacontrasenya,confirmarcontrasenya;
    TextInputLayout inputcontrasenyaactual, inputnuevacontrasenya, inputconfirmarcontrasenya;
    RequestQueue requestQueue;
    private SesionManager sesionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_contrasenya);
        requestQueue = Volley.newRequestQueue(this);
        sesionManager = new SesionManager(this);
        inputcontrasenyaactual = (TextInputLayout) findViewById(R.id.contrasenyaactual);
        inputnuevacontrasenya = (TextInputLayout) findViewById(R.id.nuevacontrasenya);
        inputconfirmarcontrasenya = (TextInputLayout) findViewById(R.id.confirmcontrasenya);
    }

    public void  guardar(View view){
        if(Validar())
        {
            Server.cambiarContrasenya(requestQueue,contrasenyaactual,nuevacontrasenya,confirmarcontrasenya,sesionManager.getEmail());
            sesionManager.setPassword(nuevacontrasenya);
            Toast.makeText(this, "Contraseña Guardada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CambiarContrasenya.this,Perfil.class));
            finish();
        }
    }

    public void  cancelar(View view){
        startActivity(new Intent(CambiarContrasenya.this,EditarPerfil.class));
        finish();
    }

    boolean Validar()
    {
        contrasenyaactual = inputcontrasenyaactual.getEditText().getText().toString();
        nuevacontrasenya = inputnuevacontrasenya.getEditText().getText().toString();
        confirmarcontrasenya = inputconfirmarcontrasenya.getEditText().getText().toString();
        boolean s = true;
        if(contrasenyaactual.isEmpty() || nuevacontrasenya.isEmpty() || confirmarcontrasenya.isEmpty())
        {
            if(contrasenyaactual.isEmpty())
            {
                inputcontrasenyaactual.setError("Complete este campo");
                s=false;
            }else{
                inputcontrasenyaactual.setError(null);
            }
            if(nuevacontrasenya.isEmpty())
            {
                inputnuevacontrasenya.setError("Complete este campo");
                s=false;
            } else{
                inputnuevacontrasenya.setError(null);
            }
            if(confirmarcontrasenya.isEmpty())
            {
                inputconfirmarcontrasenya.setError("Complete este campo");
                s=false;
            }else{
                inputconfirmarcontrasenya.setError(null);
            }
        }
        else
        {
            inputcontrasenyaactual.setError(null);
            inputnuevacontrasenya.setError(null);
            inputconfirmarcontrasenya.setError(null);
            if(nuevacontrasenya.equals(contrasenyaactual))
            {
                inputnuevacontrasenya.setError("La conraseña no puede ser la mimsa que la anterior");
                s=false;
            }else{
                inputnuevacontrasenya.setError(null);
            }
            if(!nuevacontrasenya.equals(confirmarcontrasenya) && !confirmarcontrasenya.isEmpty())
            {
                inputconfirmarcontrasenya.setError("No coincide con la nueva contraseña");
                s=false;
            }else{
                inputconfirmarcontrasenya.setError(null);
            }
        }
        return s;
    }
}
