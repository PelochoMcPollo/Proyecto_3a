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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mapa;
    private ArrayList<Medicion> listaMediciones;
    private SesionManager sesionManager;
    LatLng ultimopunto;

    Switch baja, media, alta;
    Spinner spinner;
    ImageView menu;
    String[] opciones = {"Co2", "Ozono"};
    RequestQueue requestQueue; // Cola de solicitudes para comunicación con el servidor.
    //private final LatLng EPSG = new LatLng(38.99611917166694, -0.16607561670145485);

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
                Log.d("TAG", "onMapReady: ");
                ultimopunto = new LatLng(Double.parseDouble(listaMediciones.get(listaMediciones.size() - 1).getLatitud()), Double.parseDouble(listaMediciones.get(listaMediciones.size() - 1).getLongitud()));
                mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mapa.getUiSettings().setZoomControlsEnabled(false);
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ultimopunto, 15));
            }
        });
    }

    public void recibirTodosLosPuntos(RequestQueue requestQueue, final RecibirMedicionesCallback callback) {
        Server.setMedicionesUsuarioRecuperadoListener(new MedicionesUsuarioRecuperadoListener() {
            @Override
            public void onMedicionesUsuarioRecuperado(ArrayList<Medicion> mediciones) {
                listaMediciones = mediciones;
                ArrayList<Medicion> med = comprobarRiesgo(mediciones);
                for (Medicion medicion : med) {
                    Log.d("tag", medicion.toString());
                    pintarCirculosEnMapa(medicion);
                }
                callback.onRecibirMediciones(mediciones); // Notificar al callback
            }
        });

        Server.recuperarMedicionesUsuario(requestQueue, sesionManager.getEmail());
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
        Log.d("tag", med.toString());
        mapa.clear();
        for (Medicion medicion : med) {
            pintarCirculosEnMapa(medicion);
        }
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

