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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * La clase `Server` se encarga de la comunicación con el servidor remoto para enviar
 * mediciones de CO2 y temperatura.
 */
public class Server {
    private static String ip = "192.168.1.49";

    // URL del servidor al que se enviarán las mediciones.
    private static final String Url1 = "http://"+ip+"/BiometriaBackendd/src/api/guardarprueba.php";
    private static final String UrlRecuperarUsuario = "http://"+ip+"/BiometriaBackendd/src/api/recuperarusuario.php?email=";
    private static final String  UrlRecuperarTelefono = "http://"+ip+"/BiometriaBackendd/src/api/recuperartelefono.php?email=";
    private  static  final  String UrlActualizarUsuario = "http://"+ip+"/BiometriaBackendd/src/api/actualizarusuario.php?email=";
    private  static  final  String UrlActualizarTelefono = "http://"+ip+"/BiometriaBackendd/src/api/actualizartelefono.php?email=";
    private  static  final  String UrlCambiarContrasenya = "http://"+ip+"/BiometriaBackendd/src/api/cambiarcontrasenyaapp.php?email=";
    private  static  final  String UrlRecuperarContrasenya = "http://"+ip+"/BiometriaBackendd/src/api/recuperarcontrasenya.php?email=";
    private  static  final  String UrlGuardarMediicion = "http://"+ip+"/BiometriaBackendd/src/api/guardarmedicion.php?email=";
    private  static  final  String UrlGuardarSonda = "http://"+ip+"/BiometriaBackendd/src/api/guardarsonda.php";
    private  static  final  String UrlGuardarUsuarioSonda = "http://"+ip+"/BiometriaBackendd/src/api/guardarusuariosonda.php?";
    private  static  final  String UrlRecuperarSonda = "http://"+ip+"/BiometriaBackendd/src/api/recuperarsonda.php?email=";
    private  static  final  String UrlGuardarSondaMedicion = "http://"+ip+"/BiometriaBackendd/src/api/guardarsondamedicion.php";
    private  static  final  String UrlRecupearUsuarioMedicion = "http://"+ip+"/BiometriaBackendd/src/api/recuperarusuariomedicion.php";
    private static  final  String UrlGuardarUsuarioMedicion = "http://"+ip+"/BiometriaBackendd/src/api/guardarusuariomedicion.php";

    private static String nombre,contrasenya,telefono,email;
    public static String  sonda,idmedicion;

    private  static  UsuarioRecuperadoListener usuarioRecuperadoListener;
    private  static  TelefonoRecuperadoListener telefonoRecuperadoListener;
    private  static  SondaRecuperadoListener sondaRecuperadoListener;
    private  static  UsuarioMeidicionRecuperadoListener usuarioMeidicionRecuperadoListener;




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

    public  static  void  guardarSondaMedidion(RequestQueue requestQueue, final String idsonda, final String idmedicion)
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlGuardarSondaMedicion,                // URL del servidor.
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
                params.put("idsonda",idsonda);
                params.put("idmedicion",idmedicion);
                return params;

            }
        };
        // Se agrega la solicitud a la cola de solicitudes para su procesamiento.
        requestQueue.add(stringRequest);
    }

    public  static  void guardarUsuarioMedicion(RequestQueue requestQueue,final String email, final  String idmedicion)
    {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlGuardarUsuarioMedicion,    // URL del servidor.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Llama al método medicionGuardada() del callback después de recibir la respuesta del servidor

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
                params.put("email", email);
                params.put("v", idmedicion);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void guardarMedicion(final String tiempo, final String latitud, final String longitud, final String valor, final String idcontaminante, RequestQueue requestQueue, final MedicionRecuperadoListener callback) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlGuardarMediicion,    // URL del servidor.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Llama al método medicionGuardada() del callback después de recibir la respuesta del servidor
                        callback.medicionGuardada();
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
                params.put("instante", tiempo);
                params.put("latitud", latitud);
                params.put("longitud", longitud);
                params.put("valor", valor);
                params.put("idcontaminante", idcontaminante);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public static void guardarSonda(RequestQueue requestQueue)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlGuardarSonda,
                response -> {
                    // Manejar la respuesta del servidor si es necesario
                    // Por ejemplo, mostrar un mensaje de éxito al usuario
                },
                error -> {
                    // Manejar errores de la solicitud al servidor
                }
        );

        requestQueue.add(stringRequest);
    }

    public static void guardarUsuarioSonda(RequestQueue requestQueue,final String email)
    {
        Server.setSondaRecuperadoListener(new SondaRecuperadoListener() {
            @Override
            public void onSondaRecuperado() {
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,    // Método HTTP (POST).
                        UrlGuardarUsuarioSonda,                // URL del servidor.
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("email",email);
                        params.put("idsonda",sonda);

                        return params;
                    }

                };

                requestQueue.add(stringRequest);
            }
        });
        Server.recuperarSonda(requestQueue);

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
    public  static  void recuperarUsuarioMedicion(RequestQueue requestQueue,final String correo){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecupearUsuarioMedicion.concat(correo),                   // URL del servidor.
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            idmedicion = response.getString("idmedicion");
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

    public static void recuperarSonda(RequestQueue requestQueue) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,    // Método HTTP (POST).
                UrlRecuperarSonda,                   // URL del servidor.
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            sonda = response.getString("idsonda");
                            if(sondaRecuperadoListener != null)
                            {
                                sondaRecuperadoListener.onSondaRecuperado();
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

    public static void cambiarContrasenya(RequestQueue requestQueue ,final String contrasenyaactual, final String nuevacontrasenya , final String confirmarcontrasenya ,final String correo){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlCambiarContrasenya.concat(correo),                   // URL del servidor.
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
                params.put("contrasenyaactual",contrasenyaactual);
                params.put("nuevacontrasenya",nuevacontrasenya);
                params.put("confirmarcontrasenya",confirmarcontrasenya);
                params.put("correo",correo);
                return params;
            }
        };
        // Se agrega la solicitud a la cola de solicitudes para su procesamiento.
        requestQueue.add(stringRequest);
    }

    public static void recuperContrasenya(RequestQueue requestQueue ,final String nuevacontrasenya , final String confirmarcontrasenya ,final String correo){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,    // Método HTTP (POST).
                UrlRecuperarContrasenya.concat(correo),                   // URL del servidor.
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
                params.put("nuevacontrasenya",nuevacontrasenya);
                params.put("confirmarcontrasenya",confirmarcontrasenya);
                params.put("email",correo);
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
    public  static  void  setSondaRecuperadoListener(SondaRecuperadoListener listener){
        sondaRecuperadoListener = listener;
    }

    public  static  void setUsuarioMedicionListener(UsuarioMeidicionRecuperadoListener listener)
    {
        usuarioMeidicionRecuperadoListener = listener;
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
