package org.example.eoliiri.proyecto_3a;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;

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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private SesionManager sesionManager;

    Switch baja, media, alta;
    Spinner spinner;
    ImageView menu;
    String[] opciones = {"O3", "CO","NO2"};
    RequestQueue requestQueue; // Cola de solicitudes para comunicación con el servidor.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        requestQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa2);
        mapFragment.getMapAsync(this);
        sesionManager = new SesionManager(this);
        baja = findViewById(R.id.switch1);
        media = findViewById(R.id.switch2);
        alta = findViewById(R.id.switch3);
        spinner = findViewById(R.id.spinner);
        menu = findViewById(R.id.puntosMenu);
        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_perfil, popup.getMenu());

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
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);
        setSwitchListener(baja, 1);
        setSwitchListener(media, 2);
        setSwitchListener(alta, 3);
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
                for (Medicion medicion : med) {
                    Log.d("tag", medicion.toString());
                    pintarCirculosEnMapa(medicion);
                }
                centrarMapa(mediciones);
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
        if (valor <= 180) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (180 < valor && valor <= 240) {
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

    public ArrayList<Medicion> comprobarRiesgo(ArrayList<Medicion> mediciones) {
        if (!baja.isChecked() && !media.isChecked() && !alta.isChecked()) {
            // Ninguna opción está seleccionada, devolver la lista original sin filtrar
            return mediciones;
        }

        ArrayList<Medicion> res = new ArrayList<>();
        for (Medicion medicion : mediciones) {
            int valor = Integer.parseInt(medicion.getValor());
            if (baja.isChecked() && valor <= 180) {
                res.add(medicion);
            } else if (media.isChecked() && valor > 180 && valor <= 240) {
                res.add(medicion);
            } else if (alta.isChecked() && valor > 240) {
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
            if (baja.isChecked() && valor <= 180) {
                res.add(medicion);
            } else if (media.isChecked() && valor > 180 && valor <= 240) {
                res.add(medicion);
            } else if (alta.isChecked() && valor > 240) {
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
        for (Medicion medicion : med) {
            pintarCirculosEnMapa(medicion);
        }
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
}

