package org.example.eoliiri.proyecto_3a;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    static final String CANAL_ID = "Alertas";
    static final int NOTIFICACION_ID = 1;

    //----------------detectar cambios en el textView para alertas----
    private int scanResultCount = 0;
    private int toastQR = 0;
    private int valoresco2;
    private FusedLocationProviderClient fusedLocationClient;
    int c = 0;

    private Map<String, Long> lastExecutionTime = new HashMap<>();

    TextView co2, temp, dis; // Declaración de TextViews para mostrar datos.
    String co2p = "0", tempp = "0"; // Variables para almacenar valores de CO2 y temperatura.
    RequestQueue requestQueue; // Cola de solicitudes para comunicación con el servidor.
    SesionManager sesionManager; // Gestor de sesiones para almacenar credenciales de usuario.

    double latitude;
    double longitude;

    private static final int MAX_VALORES_CO2 = 10; // Cambia el tamaño según tu preferencia
    private String[] ultimosValoresCO2 = new String[MAX_VALORES_CO2];
    private int indiceValoresCO2 = 0; // Índice para controlar la posición en el array

    // Etiquetas para mensajes de registro.
    private static final String ETIQUETA_LOG = ">>>>";

    // Código de solicitud de permisos para Bluetooth y ubicación.
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    private static final long TIEMPO_ENTRE_EJECUCIONES = 30_000;

    // Escáner de dispositivos Bluetooth LE y callback para el escaneo.
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;

    // Método para buscar todos los dispositivos Bluetooth LE.
    @SuppressLint("MissingPermission")
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empieza");

        // Configuración del callback para el escaneo de dispositivos.
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanResult()");

                // Procesar y mostrar información del dispositivo Bluetooth LE.
                mostrarInformacionDispositivoBTLE(resultado);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onBatchScanResults()");
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): onScanFailed()");
            }
        };

        Log.d(ETIQUETA_LOG, "buscarTodosLosDispositivosBTLE(): empezamos a escanear");

        // Inicia el escaneo de dispositivos Bluetooth LE.
        this.elEscanner.startScan(this.callbackDelEscaneo);
    } // ()


    // --------------------------------------------------------------
    //              ScanResult ---> mostrarInformacionDispositivoBTLE()
    // --------------------------------------------------------------
    // Método para mostrar información de un dispositivo Bluetooth LE escaneado.
    @SuppressLint("MissingPermission")
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        // Obtener información del resultado del escaneo.
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        // Imprimir información en el registro de eventos.
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        // Opcionalmente, puedes imprimir información adicional, como UUID y otros datos.

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        // Parsear los bytes escaneados como trama iBeacon y mostrar los detalles.
        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");
    }


    // --------------------------------------------------------------
    // Método para buscar un dispositivo Bluetooth LE específico por su nombre.    @SuppressLint("MissingPermission")
    // --------------------------------------------------------------
    //                     String ---> buscarEsteDispositivoBTLE()
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");

        final MainActivity activity = this; // Almacena la referencia a MainActivity

        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        // Configurar un ScanCallback para escanear dispositivos Bluetooth LE.
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);


                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanResult()");
                Log.d("cishu", String.valueOf(scanResultCount));

                String beaconActual = resultado.getDevice().getAddress(); // Cambiar esto por el identificador único del beacon
                if (dispositivoBuscado.equals(resultado.getDevice().getName()) &&
                        esPermitidoEjecutar(beaconActual)) {
                    if (toastQR == 0) {
                        Toast.makeText(getApplicationContext(), "Conectado correctamente",
                                Toast.LENGTH_LONG).show();
                        toastQR = 1;
                        Server.guardarSonda(requestQueue);
                        Server.guardarUsuarioSonda(requestQueue, sesionManager.getEmail());
                    }

                    mostrarInformacionDispositivoBTLE(resultado);
                    byte[] bytes = resultado.getScanRecord().getBytes();

                    TramaIBeacon tib = new TramaIBeacon(bytes);

                    scanResultCount = 0;

                    double distancia = calcularDistancia(tib.getTxPower(), resultado.getRssi());
                    double d = cDistancia(tib.getTxPower(), resultado.getRssi(), 2);
                    mostrarDistancia(d);
                    Log.d("Pelochas", "distancia: " + String.valueOf(d));

                    int valor = Utilidades.bytesToInt(tib.getMajor());
                    if (valor < 0) {
                        valor = valor * -1;
                    }
                    co2p = String.valueOf(valor);
                    tempp = String.valueOf(Utilidades.bytesToInt(tib.getMinor()));

                    if (Integer.parseInt(co2p) > 180 && Integer.parseInt(co2p) <= 240) {
                        lanzarNoti(1);
                    } else if (Integer.parseInt(co2p) > 240) {
                        lanzarNoti(2);
                    }

                    co2.setText(co2p);
                    temp.setText(tempp);

                    ultimosValoresCO2[indiceValoresCO2] = co2p;

                    //Hora
                    Instant instanteActual = Instant.now();
                    ZonedDateTime zonedDateTime = instanteActual.atZone(ZoneId.of("Europe/Madrid"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String fechaFormateada = formatter.format(zonedDateTime);

                    //Coordenadas

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();

                                        // Utiliza latitude y longitude aquí
                                        Log.d("Pelochas", "Latitud: " + latitude + ", Longitud: " + longitude);

                                        // Aquí puedes realizar acciones con la ubicación obtenida
                                    }
                                }
                            });


                    Server.guardarMedicion(fechaFormateada, String.valueOf(latitude), String.valueOf(longitude), co2p, String.valueOf(1), requestQueue, new MedicionRecuperadoListener() {
                        @Override
                        public void medicionGuardada() {
                            Server.setSondaRecuperadoListener(new SondaRecuperadoListener() {
                                @Override
                                public void onSondaRecuperado() {
                                    Server.setUltimaMedicionListener(new UltimaMedicionRecuperadoListener() {
                                        @Override
                                        public void onUltimaMedicionListener() {
                                            Server.guardarSondaMedidion(requestQueue, Server.sonda, Server.idmedicion);
                                            Server.guardarUsuarioMedicion(requestQueue, sesionManager.getEmail(), Server.idmedicion);
                                        }
                                    });
                                    Server.recuperarUltimaMedicion(requestQueue);
                                }
                            });
                            Server.recuperarSonda(requestQueue);
                        }
                    });

                    lastExecutionTime.put(beaconActual, System.currentTimeMillis());

                } else {
                    scanResultCount++;
                }

                if (scanResultCount >= 500) {
                    NotificationHelper.mostrarNotificacion(MainActivity.this, "Alertas!", "sensor dañado o que hace lecturas erróneas o que no envía datos al móvil");
                    scanResultCount = 0;
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onBatchScanResults()");
            }


            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): onScanFailed()");
            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();
        Log.d("Hola", sf.toString());

        Log.d(ETIQUETA_LOG, "buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        this.elEscanner.startScan(this.callbackDelEscaneo);
    }

    public interface MedicionCallback {
        void medicionGuardada();
    }


    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);}


    private boolean esPermitidoEjecutar(String beaconActual) {
        long tiempoActual = System.currentTimeMillis();

        // Obtiene el tiempo de la última ejecución para el beacon actual
        Long ultimoTiempoEjecucion = lastExecutionTime.get(beaconActual);

        // Si es la primera vez que se detecta el beacon o ha pasado suficiente tiempo desde la última ejecución
        return ultimoTiempoEjecucion == null || tiempoActual - ultimoTiempoEjecucion > TIEMPO_ENTRE_EJECUCIONES;
    }

    // 计算距离的方法 Cómo calcular la distancia
    private double calcularDistancia(int txPower, int rssi) {
        // 根据信号强度衰减模型计算距离
        return Math.pow(10d, ((double) (txPower - rssi)) / (90 * 4));
    }

    public double cDistancia(int txPower, int rssi, double n) {
        if (rssi == 0) {
            return -1.0; // Valor no válido, la señal no se detecta
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {

            double distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return distance;
        }
    }

    // 显示距离的方法 Cómo mostrar la distancia
    private void mostrarDistancia(double distancia) {
        // 将距离显示在 distanciavalue 的 TextView 上    Mostrar distancia en TextView de distanciavalue
        if (distancia < 2) {
            dis.setText("Estas al lado del sensor");

        } else if (distancia >= 2 && distancia <= 5) {
            dis.setText("Estas cerca del sensor");
        } else if (distancia > 5) {
            dis.setText("Estas lejos");
        }
        //dis.setText(String.valueOf("%.2f",distancia)+" metreos");

    }

    // --------------------------------------------------------------
    // Detiene la búsqueda de dispositivos Bluetooth LE.
    // --------------------------------------------------------------
    //              detenerBusquedaDispositivosBTLE()
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void detenerBusquedaDispositivosBTLE() {
        // Comprueba si el callback de escaneo está nulo y, en ese caso, no realiza ninguna acción.
        if (this.callbackDelEscaneo == null) {
            return;
        }

        // Detiene el escaneo utilizando el escáner Bluetooth LE y establece el callback en nulo.
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo = null;
    } // ()

    // Método llamado al pulsar el botón "Buscar Dispositivos BTLE" en la interfaz.
    public void botonBuscarDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton buscar dispositivos BTLE Pulsado");
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // Método llamado al pulsar el botón "Buscar Nuestro Dispositivo BTLE" en la interfaz.
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        // Inicia la búsqueda de un dispositivo Bluetooth LE específico.
        this.buscarEsteDispositivoBTLE("PER PUIGDEMOOONT");
    } // ()

    //--------------------version 2 de :botonBuscarNuestroDispositivoBTLEPulsado----------------------
    //-------------se llama sin button -----------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsadoV2(String nombre) {
        // Inicia la búsqueda de un dispositivo Bluetooth LE específico.
        this.buscarEsteDispositivoBTLE(nombre);
    }


    // Método llamado al pulsar el botón "Detener Búsqueda de Dispositivos BTLE" en la interfaz.
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        Log.d(ETIQUETA_LOG, "boton detener búsqueda dispositivos BTLE Pulsado");
        this.detenerBusquedaDispositivosBTLE(); // Detiene la búsqueda de dispositivos BTLE.
    } // ()


    // --------------------------------------------------------------
    // Inicializa el Bluetooth y solicita los permisos necesarios.
    // --------------------------------------------------------------
    //                inicializarBlueTooth()
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");

        // Obtiene el adaptador Bluetooth predeterminado.
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT");

        // Habilita el adaptador Bluetooth.
        bta.enable();

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado = " + bta.isEnabled());
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): estado = " + bta.getState());

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escáner Bluetooth LE");

        // Obtiene el escáner Bluetooth LE a partir del adaptador Bluetooth.
        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): Socorro: NO hemos obtenido escáner Bluetooth LE  !!!!");
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): vamos a solicitar permisos (si no los tenemos) !!!!");

        // Verifica si los permisos necesarios para el Bluetooth están otorgados.
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si los permisos no están otorgados, solicita los permisos necesarios.
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        } else {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");
        }
    } // ()

    // Método llamado al crear la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Servicio de Música",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sesionManager = new SesionManager(this);

        // Inicializa la cola de solicitudes de Volley para realizar peticiones HTTP.
        requestQueue = Volley.newRequestQueue(this);

        Log.d(ETIQUETA_LOG, "onCreate(): comienza");

        // Inicializa el Bluetooth y solicita permisos si es necesario.
        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        // Asigna los TextView de la interfaz a las variables co2 y temp.
        co2 = findViewById(R.id.CO2); //
        temp = findViewById(R.id.Temp);
        //dis = findViewById(R.id.textView11);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            // Logic to handle location object
                        }
                    }
                });


        //alerta cuando cambia el textView de co2

    } // onCreate()


    // Método llamado cuando se otorgan o deniegan permisos solicitados por la aplicación.
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // Si la solicitud se cancela, los resultados están vacíos.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Los permisos se otorgan. Continuar la acción o flujo de trabajo en la aplicación.
                } else {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");
                }
                return;
        }
        // Otras líneas 'case' para verificar otros permisos que la aplicación podría solicitar.
    } // ()

    public void lanzarLogin(View view) {

        if (sesionManager.SesionIniciada()) {
            try {
                startActivity(new Intent(this, Mapa.class));
            } catch (Exception e) {
                Log.d("TAG", e.toString());
            }
        } else
            try {
                startActivity(new Intent(this, LoginActivity.class));
            } catch (Exception e) {
                Log.d("TAG", e.toString());
            }
    }

    public void lanzarMapa(View view) {
        Intent intent = new Intent(this, MapaPublico.class);
        startActivity(intent);
    }

    public void onInformacion(View view) {
        // 在这里编写在按钮被点击时要执行的操作
        Intent intent = new Intent(this, informacion.class);
        startActivity(intent);
    }

    public void lanzarRegistrate(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void lanzarScanner(View view) {
        // Ejemplo utilizando ZXing
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanea el código QR del sensor");
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
        //------handle the respuesta
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String qrData = result.getContents();
                // Process the scanned QR code data
                // ...
                Log.e("escaneo correcto", qrData);
                botonBuscarNuestroDispositivoBTLEPulsadoV2(qrData);
            } else {
                // Handle the case where scanning was canceled or failed
                // ...
                Log.e("escaneo correcto", "MAAAAAAAL");

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void lanzarNoti(int caso) {
        if (caso == 1) {
            NotificationHelper.mostrarNotificacion(this, "Alerta", "El nivel de riesgo de calidad del aire es moderado");
        } else {
            NotificationHelper.mostrarNotificacion(this, "Alerta", "El nivel de riesgo de calidad del aire es alto");
        }
    }

    public void mainPage(View view) {
        startActivity(new Intent(this, Mapa.class));
    }

    public void generar(View view) {
        Log.d("pelochas", "hola");

        Random random = new Random();

        // Array de correos electrónicos
        String[] correos = {"alba@gmail.com", "alex@gmail.com", "eduard01kate@gmail.com", "pepe@gmail.com"};

        for (int i = 1; i <= 20; i++) {

            LocalDateTime fechaEspecifica = LocalDateTime.of(2023, 12, 14, 0, 0, 0);
            fechaEspecifica = fechaEspecifica.plusHours(i);

            // Definir un formato de fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Convertir LocalDateTime a String
            String fechaComoString = fechaEspecifica.format(formatter);
            Log.d("pelochas", "fecha generar: " + fechaComoString);

            int numeroAleatorio = random.nextInt(4) + 2; // Generar número aleatorio entre 2 y 5
            int numeroAleatorio2  = random.nextInt(301);
            int numeroAleatorio3 = random.nextInt(3) + 1;
            double latitud = 38.99610528500254000000;
            double longitud = -0.16569078810570842000;

            double variacionLatitud = Math.random() * 0.02 - 0.01; // Variación de +/- 0.01 para la latitud
            double variacionLongitud = Math.random() * 0.02 - 0.01;

            double latitudNueva = latitud + variacionLatitud;
            double longitudNueva = longitud + variacionLongitud;

            String emailAleatorio = correos[random.nextInt(correos.length)]; // Seleccionar un email aleatorio
            Log.d("Pelochas",emailAleatorio);

           Server.guardarMedicion(fechaComoString, String.valueOf(latitudNueva), String.valueOf(longitudNueva), String.valueOf(numeroAleatorio2), String.valueOf(numeroAleatorio3), requestQueue, new MedicionRecuperadoListener() {
                @Override
                public void medicionGuardada() {
                    Server.setUltimaMedicionListener(new UltimaMedicionRecuperadoListener() {
                        @Override
                        public void onUltimaMedicionListener() {
                            Server.guardarSondaMedidion(requestQueue, String.valueOf(numeroAleatorio), Server.idmedicion); // Usar el número aleatorio en lugar de Server.sonda
                            Server.guardarUsuarioMedicion(requestQueue, emailAleatorio, Server.idmedicion);
                        }
                    });
                    Server.recuperarUltimaMedicion(requestQueue);

                }
            });
        }
    }

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