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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * La clase `Server` se encarga de la comunicación con el servidor remoto para enviar
 * mediciones de CO2 y temperatura.
 */
public class Server {
    private static String ip = "10.237.24.60";

    // URL del servidor al que se enviarán las mediciones.
    private static final String Url1 = "http://"+ip+"/proyecto_3a/src/api/guardarprueba.php";
    private static final String UrlRecuperarUsuario = "http://"+ip+"/proyecto_3a/src/api/recuperarusuario.php?email=";
    private static final String  UrlRecuperarTelefono = "http://"+ip+"/proyecto_3a/src/api/recuperartelefono.php?email=";
    private  static  final  String UrlActualizarUsuario = "http://"+ip+"/proyecto_3a/src/api/actualizarusuario.php?email=";
    private  static  final  String UrlActualizarTelefono = "http://"+ip+"/proyecto_3a/src/api/actualizartelefono.php?email=";

    private static String nombre,contrasenya,telefono,email;

    private  static  UsuarioRecuperadoListener usuarioRecuperadoListener;
    private  static  TelefonoRecuperadoListener telefonoRecuperadoListener;




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
    public  static void crearPrueba(final String co2bd, final String tempbd , RequestQueue requestQueue) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                Url1,                // URL del servidor.
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
     *
     *   -------------------------------------------------------------------------
     *    requestQueue --> recuperarUsuario() --> [nombre:text, contrasenya:text]
     *   -------------------------------------------------------------------------
     *
     * @param requestQueue
     */

    public static void recuperarUsuario(RequestQueue requestQueue,String correo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecuperarUsuario.concat(correo),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            nombre = response.getString("nombreyapellidos");
                            contrasenya = response.getString("contrasenya");
                            email = response.getString("email");

                            if (usuarioRecuperadoListener != null) {
                                usuarioRecuperadoListener.onUsuarioRecuperado();
                            }


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
     *
     *   -------------------------------------------------------------------------
     *    requestQueue --> recuperarTelefono() --> [telefono:R]
     *   -------------------------------------------------------------------------
     *
     * @param requestQueue
     */

    public static void recuperarTelefono(RequestQueue requestQueue, String correo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecuperarTelefono.concat(correo),                   // URL del servidor.
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            telefono = response.getString("telefono");
                            if(telefonoRecuperadoListener != null)
                            {
                                telefonoRecuperadoListener.onTelefonoRecuperado();
                            }
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
     *
     *   -------------------------------------------------------------------------
     *    requestQueue, nombre:text, contrasenya:text --> actualizarusuario()
     *   -------------------------------------------------------------------------
     *
     * @param requestQueue
     * @param nombreg
     */

    public static void actualizarusuario(RequestQueue requestQueue ,final String nombreg, final String emailg, final String correo)
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlActualizarUsuario.concat(correo),                   // URL del servidor.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Pelochas","Actualizado");
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
                params.put("email",emailg);
                params.put("emailantiguo",correo);
                params.put("nombreyapellidos",nombreg);
                return params;
            }
        };
        // Se agrega la solicitud a la cola de solicitudes para su procesamiento.
        requestQueue.add(stringRequest);
    }


    /**
     *
     *   -------------------------------------------------------------------------
     *    requestQueue, telefono:text --> actualizatelefono()
     *   -------------------------------------------------------------------------
     *
     * @param requestQueue
     * @param telefono
     */

    public static void actualizarTelefono(RequestQueue requestQueue ,final String telefono, final String correo){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlActualizarTelefono.concat(correo),                   // URL del servidor.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Pelochas","Actualizado");
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
                params.put("email",correo);
                params.put("telefono",telefono);
                return params;
            }
        };
        // Se agrega la solicitud a la cola de solicitudes para su procesamiento.
        requestQueue.add(stringRequest);
    }


    /**
     *
     * @param requestQueue
     * @param nombrei
     * @param telefonoi
     * @param emaili
     */
    public static void modificarTextos(RequestQueue requestQueue,TextInputEditText nombrei , TextInputEditText telefonoi, TextInputEditText emaili,String correo)
    {
        Server.setUsuarioRecuperadoListener(new UsuarioRecuperadoListener() {
            @Override
            public void onUsuarioRecuperado() {
                // Los datos del usuario han sido recuperados, ahora puedes actualizar los campos de texto
                nombrei.setText(nombre);
                emaili.setText(email);
            }
        });
        Server.setTelefonoRecuperadoListener(new TelefonoRecuperadoListener() {
            @Override
            public void onTelefonoRecuperado() {
                // Los datos del usuario han sido recuperados, ahora puedes actualizar los campos de texto
                telefonoi.setText(telefono);
            }
        });

        // Inicia la recuperación de datos del usuario
        Server.recuperarUsuario(requestQueue,correo);
        Server.recuperarTelefono(requestQueue,correo);



    }

    public static void setUsuarioRecuperadoListener(UsuarioRecuperadoListener listener) {
        usuarioRecuperadoListener = listener;
    }
    public static void setTelefonoRecuperadoListener(TelefonoRecuperadoListener listener){
        telefonoRecuperadoListener = listener;
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
    public  static String getUrl1()
    {
        return  Url1;
    }
    public  static String getUrlRecuperarUsuario()
    {
        return  UrlRecuperarUsuario;
    }
    public  static String getNombre() {return  nombre;}
    public  static String getContrasenya()
    {
        return  contrasenya;
    }
    public  static String getTelefono(){return telefono;}
}
