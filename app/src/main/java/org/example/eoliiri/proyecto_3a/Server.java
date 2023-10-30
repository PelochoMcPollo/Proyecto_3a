package org.example.eoliiri.proyecto_3a;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * La clase `Server` se encarga de la comunicación con el servidor remoto para enviar
 * mediciones de CO2 y temperatura.
 */
public class Server {

    // URL del servidor al que se enviarán las mediciones.
    private static final String Url1 = "http://192.168.148.194/proyecto_3a/src/api/guardarprueba.php";


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
}
