package org.example.eoliiri.proyecto_3a;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import java.util.ArrayList;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private SesionManager sesionManager;

    LatLng ultimopunto;
    TextView ppm;
    private Handler handler = new Handler();
    private Runnable ppmRunnable;

    Switch baja, media, alta;
    Spinner spinner;
    ImageView menu,co2Image,emoticono;
    Toolbar toolbar;
    RequestQueue requestQueue; // Cola de solicitudes para comunicación con el servidor.
    //private final LatLng EPSG = new LatLng(38.99611917166694, -0.16607561670145485);
    HorizontalProgressBar progressBar;

    String[] opciones = {"O3", "CO", "NO2"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        requestQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa2);
        mapFragment.getMapAsync(this);
        sesionManager = new SesionManager(this);
        ppm = (TextView) findViewById(R.id.calidadAire);
        baja = findViewById(R.id.switch1);
        media = findViewById(R.id.switch2);
        alta = findViewById(R.id.switch3);
        spinner = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toolbar);
        co2Image=findViewById(R.id.colorCo2);
        emoticono=findViewById(R.id.emoticono);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);

        handler.postDelayed(ppmRunnable = new Runnable() {
            public void run() {
                // Llamar a la función Ppm para actualizar el TextView cada 40 segundos
                Ppm(requestQueue, sesionManager.getEmail());

                // Volver a programar la ejecución después de 40 segundos
                handler.postDelayed(this, 40000);
            }
        }, 0);

        menu = findViewById(R.id.puntosMenu);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.option1:
                        lanzarMasInfo(null);
                        return true;
                    case R.id.option2:
                        lanzarEditar(null);
                        return true;
                    case R.id.option3:
                        cerrarSesion(null);
                        return true;
                    default:
                        return false;
                }
            });

            popup.show();
      

        setSwitchListener(baja, 1);
        setSwitchListener(media, 2);
        setSwitchListener(alta, 3);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recibirTodosLosPuntos(requestQueue, new RecibirMedicionesCallback() {
                    @Override
                    public void onRecibirMediciones(ArrayList<Medicion> mediciones) {
                        actualizarMapaSegunFiltro(mediciones);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
        recibirTodosLosPuntos(requestQueue, new RecibirMedicionesCallback() {
            @Override
            public void onRecibirMediciones(ArrayList<Medicion> mediciones) {
                actualizarMapaSegunFiltro(mediciones);
            }
        });
    }

    private void setSwitchListener(CompoundButton switchButton, int riskLevel) {
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recibirTodosLosPuntos(requestQueue, new RecibirMedicionesCallback() {
                @Override
                public void onRecibirMediciones(ArrayList<Medicion> mediciones) {
                    actualizarMapaSegunFiltro(mediciones);
                }
            });
        });
    }


    public void centrarMapa(ArrayList<Medicion> mediciones) {

        //ultimopunto = new LatLng(Double.parseDouble(mediciones.get(mediciones.size() - 1).getLatitud()), Double.parseDouble(mediciones.get(mediciones.size() - 1).getLongitud()));
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Medicion medicion : mediciones) {
            LatLng posicion = new LatLng(Double.parseDouble(medicion.getLatitud()), Double.parseDouble(medicion.getLongitud()));
            builder.include(posicion);
        }
        LatLngBounds bounds = builder.build();
        mapa.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        Log.d("TAG", "CENTRAO");
        //mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ultimopunto, 13));
        //obtenerDatosOficiales(requestQueue);
    }

    public void recibirTodosLosPuntos(RequestQueue requestQueue, final RecibirMedicionesCallback callback) {
        Server.setMedicionesUsuarioRecuperadoListener(new MedicionesUsuarioRecuperadoListener() {
            @Override
            public void onMedicionesUsuarioRecuperado(ArrayList<Medicion> mediciones) {
                ArrayList<Medicion> med = comprobarRiesgo(mediciones);
                pintarMapaDeCalor(med);
                centrarMapa(med);
                callback.onRecibirMediciones(mediciones); // Notificar al callback
            }
        });

        Server.recuperarMedicionesUsuario(requestQueue, sesionManager.getEmail());
    }

    private void crearMarcadorEstacion(JSONObject estacion, String parametro, String snippet) throws JSONException {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(estacion.getString("location"));
        double latitud = estacion.getJSONObject("coordinates").getDouble("latitude");
        double longitud = estacion.getJSONObject("coordinates").getDouble("longitude");
        int valor;
        float color;
        markerOptions.position(new LatLng(latitud, longitud));
        for (int j = 0; j < estacion.getJSONArray("measurements").length(); j++) {
            JSONObject medicion = estacion.getJSONArray("measurements").getJSONObject(j);
            if (medicion.getString("parameter").equals(parametro)) {
                valor = medicion.getInt("value");
                markerOptions.snippet(snippet + ": " + valor);
                color = decidirColor(valor);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
            }
        }
        mapa.addMarker(markerOptions);
    }

    public void obtenerDatosOficiales(RequestQueue requestQueue) {
        String url = "https://api.openaq.org/v1/latest?limit=100&page=1&offset=0&sort=desc&parameter=no2&parameter=o3&radius=1000&location=ES1885A&location=ES1912A&location=ES1239A&location=ES1181A&location=ES1709A&order_by=lastUpdated&dump_raw=false";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                JSONObject estacion = response.getJSONArray("results").getJSONObject(i);
                                switch (spinner.getSelectedItemPosition()) {
                                    case 0:
                                        crearMarcadorEstacion(estacion, "o3", "O3");
                                        break;
                                    case 1:
                                        crearMarcadorEstacion(estacion, "co", "CO");
                                        break;
                                    case 2:
                                        crearMarcadorEstacion(estacion, "no2", "NO2");
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private float decidirColor(int valor) {
        if (valor <= 121) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (121 < valor && valor <= 180) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            return BitmapDescriptorFactory.HUE_RED;
        }
    }


    // Método para pintar círculos en el mapa basados en las mediciones
    private void pintarCirculosEnMapa(Medicion medicion) {
        // Obtener datos de la medición
        LatLng posicion = new LatLng(Double.parseDouble(medicion.getLatitud()), Double.parseDouble(medicion.getLongitud()));
        int valor = Integer.parseInt(medicion.getValor());
        int color = 0;
        int color2 = 0;
        if (valor <= 180) {
            color = Color.GREEN;
            color2 = Color.argb(70, 0, 255, 0);
        } else if (180 < valor && valor <= 240) {
            color = Color.YELLOW;
            color2 = Color.argb(70, 255, 255, 0);
        } else {
            color = Color.RED;
            color2 = Color.argb(70, 255, 0, 0);
        }

        // Crear un círculo en el mapa con los datos de la medición
        mapa.addCircle(new CircleOptions()
                .center(posicion)
                .radius(100) // Aquí puedes ajustar el radio del círculo según tus necesidades
                .strokeWidth(2)
                .strokeColor(color)
                .fillColor(color2));
    }

    public void pintarMapaDeCalor(ArrayList<Medicion> mediciones) {
        Log.d("TAG", "PINTAO");
        ArrayList<WeightedLatLng> heatmapDataGreen = new ArrayList<>();
        ArrayList<WeightedLatLng> heatmapDataYellow = new ArrayList<>();
        ArrayList<WeightedLatLng> heatmapDataRed = new ArrayList<>();

        for (Medicion medicion : mediciones) {
            LatLng latLng = new LatLng(Double.parseDouble(medicion.getLatitud()), Double.parseDouble(medicion.getLongitud()));
            WeightedLatLng weightedLatLng = new WeightedLatLng(latLng, Double.parseDouble(medicion.getValor()));
            int valor = Integer.parseInt(medicion.getValor());
            if (valor <= 121) {
                heatmapDataGreen.add(weightedLatLng);
            } else if (valor > 121 && valor <= 180) {
                heatmapDataYellow.add(weightedLatLng);
            } else if (valor > 180) {
                heatmapDataRed.add(weightedLatLng);
            }
        }

        if (heatmapDataRed.size() != 0) {
            HeatmapTileProvider providerRed = new HeatmapTileProvider.Builder()
                    .weightedData(heatmapDataRed)
                    .gradient(new Gradient(new int[]{Color.RED, Color.RED}, new float[]{0.2f, 1f}))
                    .radius(50)
                    .build();
            mapa.addTileOverlay(new TileOverlayOptions().tileProvider(providerRed));
        }

        if (heatmapDataYellow.size() != 0) {
            HeatmapTileProvider providerYellow = new HeatmapTileProvider.Builder()
                    .weightedData(heatmapDataYellow)
                    .gradient(new Gradient(new int[]{Color.YELLOW, Color.YELLOW}, new float[]{0.2f, 1f}))
                    .radius(50)
                    .build();
            mapa.addTileOverlay(new TileOverlayOptions().tileProvider(providerYellow));
        }

        if (heatmapDataGreen.size() != 0) {
            HeatmapTileProvider providerGreen = new HeatmapTileProvider.Builder()
                    .weightedData(heatmapDataGreen)
                    .gradient(new Gradient(new int[]{Color.GREEN, Color.GREEN}, new float[]{0.2f, 1f}))
                    .radius(50)
                    .build();
            mapa.addTileOverlay(new TileOverlayOptions().tileProvider(providerGreen));
        }
    }

    public ArrayList<Medicion> comprobarRiesgo(ArrayList<Medicion> mediciones) {
        if (!baja.isChecked() && !media.isChecked() && !alta.isChecked()) {
            // Ninguna opción está seleccionada, devolver la lista original sin filtrar
            return mediciones;
        }

        ArrayList<Medicion> res = new ArrayList<>();
        for (Medicion medicion : mediciones) {
            int valor = Integer.parseInt(medicion.getValor());
            if (baja.isChecked() && valor <= 121) {
                res.add(medicion);
            } else if (media.isChecked() && valor > 121 && valor <= 180) {
                res.add(medicion);
            } else if (alta.isChecked() && valor > 180) {
                res.add(medicion);
            }
        }
        return res;
    }

    public ArrayList<Medicion> filterByContaminant(ArrayList<Medicion> mediciones) {
        ArrayList<Medicion> res = new ArrayList<>();
        int contaminante = spinner.getSelectedItemPosition() + 1;
        for (Medicion medicion : mediciones) {
            if (Integer.parseInt(medicion.getIdContaminante()) == contaminante) {
                res.add(medicion);
            }
        }
        return res;
    }

    public ArrayList<Medicion> filterByValue(ArrayList<Medicion> mediciones) {

        if (!baja.isChecked() && !media.isChecked() && !alta.isChecked()) {
            // Ninguna opción está seleccionada, devolver la lista original sin filtrar
            return mediciones;
        }
        ArrayList<Medicion> res = new ArrayList<>();
        for (Medicion medicion : mediciones) {
            int valor = Integer.parseInt(medicion.getValor());
            if (baja.isChecked() && valor <= 121) {
                res.add(medicion);
            } else if (media.isChecked() && valor > 121 && valor <= 180) {
                res.add(medicion);
            } else if (alta.isChecked() && valor > 180) {
                res.add(medicion);
            }
        }
        return res;
    }

    private void actualizarMapaSegunFiltro(ArrayList<Medicion> mediciones) {
        ArrayList<Medicion> med = filterByContaminant(mediciones);
        med = filterByValue(med);
        Log.d("TAG", "ACTUALIZAO");
        mapa.clear();
        pintarMapaDeCalor(med);
        obtenerDatosOficiales(requestQueue);
    }

    public void cerrarSesion(View view) {
        sesionManager.cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void lanzarEditar(View view) {
        Intent intent = new Intent(this, EditarPerfil.class);
        startActivity(intent);
    }

    public void lanzarMasInfo(View view) {
        Intent intent = new Intent(this, informacion.class);
        startActivity(intent);
    }

    public void Ppm(RequestQueue requestQueue, String correo){
        Server.setUsuarioMedicionListener(new UsuarioMeidicionRecuperadoListener() {
            @Override
            public void onUsuarioMedicionListener() {
                Log.d("Pelochas",Server.idmedicion);
                Server.setMedicionListener(new MedicionRecuperadoListener() {
                    @Override
                    public void medicionGuardada() {
                        ppm.setText(Server.valor);
                        progressBar.setProgress(Integer.parseInt(Server.valor));
                        if (Integer.parseInt(Server.valor)<  180) {
                            co2Image.setImageResource(R.drawable.verde);
                            emoticono.setImageResource(R.drawable.contento_verde);
                        } else if (Integer.parseInt(Server.valor)>= 180 && Integer.parseInt(Server.valor) <= 240) { // Corregido para incluir valores entre 400 y 1000
                            co2Image.setImageResource(R.drawable.amarillo);
                            emoticono.setImageResource(R.drawable.emoti_2);
                        } else if (Integer.parseInt(Server.valor) >240) {
                            co2Image.setImageResource(R.drawable.rojo);
                            emoticono.setImageResource(R.drawable.triste_max_rojo);
                        }

                    }
                });
                Server.recuperarMedicion(requestQueue,Server.idmedicion);
            }
        });
        Server.recuperarUsuarioMedicion(requestQueue,correo);
    }

    @Override
    protected void onDestroy() {
        // Detener el Runnable cuando la actividad se destruye
        handler.removeCallbacks(ppmRunnable);
        super.onDestroy();
    }
    //---------------------------------------
    //---------------toolbar----------------

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acercaDe) {
            //lanzarAcercaDe(null);
            return true;
        }
        if (id == R.id.menu_perfil) {
            lanzarEditarPerfil(null);
            return true;
        }
        if(id == R.id.descubrir){
            lanzarEditarDescubrir(null);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public void lanzarEditarDescubrir(View view){
        Intent i = new Intent(this,informacion.class);
        startActivity(i);
    }

    public void lanzarEditarPerfil(View view){
        Intent i = new Intent(this,EditarPerfil.class);
        startActivity(i);
    }


}

