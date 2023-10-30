package org.example.eoliiri.proyecto_3a;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * La clase `Server` se encarga de la comunicación con el servidor remoto para enviar
 * mediciones de CO2 y temperatura.
 */
public class Server {

    // URL del servidor al que se enviarán las mediciones.
    private static final String Url1 = "http://192.168.148.194/proyecto_3a/src/api/guardarprueba.php";
    private static final String UrlRecuperarUsuario = "http://192.168.148.194/proyecto_3a/src/api/recuperarusuario.php?email=pepe@gmail.com";
    private static final String  UrlRecuperarTelefono = "http://192.168.148.194/proyecto_3a/src/api/recuperartelefono.php?email=pepe@gmail.com";

    private static String nombre,contrasenya,telefono;



    /**
     * crearPrueba()
     * Este método envía mediciones de CO2 y temperatura al servidor remoto.
     *
     * //-----------------------------------------------------------------------------------------------------------
     * //                String, String, RequestQueue ---> crearPrueba()
     * //-----------------------------------------------------------------------------------------------------------
     *
     * @param co2bd         Nivel de CO2 a enviar al servidor.
     * @param tempbd        Temperatura a enviar al servidor.
     * @param requestQueue  Cola de solicitudes Volley para gestionar la comunicación en red.
     */
    public static void crearPrueba(final String co2bd, final String tempbd , RequestQueue requestQueue) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                Url1,                   // URL del servidor.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Pelochas", error.toString());
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                // Se definen los parámetros a enviar en la solicitud (nivel de CO2 y temperatura).
                params.put("co2",co2bd);
                params.put("temp",tempbd);
                return params;

            }
        };
        // Se agrega la solicitud a la cola de solicitudes para su procesamiento.
        requestQueue.add(stringRequest);
    }



    public static void recuperarUsuario(RequestQueue requestQueue, TextInputEditText nombrei, TextInputEditText contrasenyai) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecuperarUsuario,                   // URL del servidor.
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            nombre = response.getString("nombreyapellidos");
                            contrasenya = response.getString("contrasenya");
                            nombrei.setText(nombre);
                            contrasenyai.setText(contrasenya);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }

    public static void recuperarTelefono(RequestQueue requestQueue, TextInputEditText telefonoi) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecuperarTelefono,                   // URL del servidor.
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            telefono = response.getString("telefono");
                            telefonoi.setText(telefono);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }
    /**
     * getUrl1()
     *
     * //-----------------------------------------------------------------------------------------------------------
     * //       getUrl1() ---> String
     * //-----------------------------------------------------------------------------------------------------------
     * Este método devuelve la URL del servidor al que se envían las mediciones.
     *
     * @return URL del servidor.
     */
    public static String getUrl1()
    {
        return  Url1;
    }
    public static String getUrlRecuperarUsuario()
    {
        return  UrlRecuperarUsuario;
    }
    public static String getNombre()
    {
        return  nombre;
    }
    public static String getContrasenya()
    {
        return  contrasenya;
    }
}
