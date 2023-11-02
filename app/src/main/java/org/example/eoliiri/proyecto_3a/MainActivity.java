package org.example.eoliiri.proyecto_3a;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

public class MainActivity extends AppCompatActivity {

    //----------------detectar cambios en el textView para alertas----

    private CheckTextViewValue checker;

    TextView co2, temp; // Declaración de TextViews para mostrar datos.
    String co2p = "0", tempp = "0"; // Variables para almacenar valores de CO2 y temperatura.
    RequestQueue requestQueue; // Cola de solicitudes para comunicación con el servidor.

    SesionManager sesionManager; // Gestor de sesiones para almacenar credenciales de usuario.

    // Etiquetas para mensajes de registro.
    private static final String ETIQUETA_LOG = ">>>>";

    // Código de solicitud de permisos para Bluetooth y ubicación.
    private static final int CODIGO_PETICION_PERMISOS = 11223344;

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

        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        // Configurar un ScanCallback para escanear dispositivos Bluetooth LE.
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                // Comprobar si el dispositivo escaneado coincide con el dispositivo buscado por nombre.
                if (dispositivoBuscado.equals(resultado.getDevice().getName())) {
                    mostrarInformacionDispositivoBTLE(resultado);
                    byte[] bytes = resultado.getScanRecord().getBytes();
                    TramaIBeacon tib = new TramaIBeacon(bytes);

                    // Verificar si el valor CO2 ha cambiado y actualizar la vista y la base de datos.
                    if (!co2p.equals(String.valueOf(Utilidades.bytesToInt(tib.getMajor()))))
                    {
                        Log.d("Pelochas",co2p);
                        Log.d("Pelochas",tempp);

                        co2p = String.valueOf(Utilidades.bytesToInt(tib.getMajor()));
                        tempp = String.valueOf(Utilidades.bytesToInt(tib.getMinor()));

                        co2.setText(co2p);
                        temp.setText(tempp);

                        Server.crearPrueba(co2p, tempp,requestQueue);
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        // Crear un filtro para buscar dispositivos con un nombre específico.
        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();
        Log.d("Hola", sf.toString());

        // Iniciar el escaneo para buscar el dispositivo específico por nombre.
        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado);
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );
        this.elEscanner.startScan(this.callbackDelEscaneo);
    } // ()

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

        sesionManager = new SesionManager(this);

        // Inicializa la cola de solicitudes de Volley para realizar peticiones HTTP.
        requestQueue = Volley.newRequestQueue(this);

        Log.d(ETIQUETA_LOG, "onCreate(): comienza");

        // Inicializa el Bluetooth y solicita permisos si es necesario.
        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, "onCreate(): termina");

        // Asigna los TextView de la interfaz a las variables co2 y temp.

        co2 = findViewById(R.id.CO2); //
        temp =findViewById(R.id.Temp);
        //alerta cuando cambia el textView de co2
        checker = new CheckTextViewValue(co2);
        checker.startChecking();
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

        if (sesionManager.SesionIniciada()){
            try {
                startActivity(new Intent(this, Perfil.class));
            }
            catch (Exception e){
                Log.d("TAG", e.toString());
            }
        }
        else
            try {
                startActivity(new Intent(this, LoginActivity.class));
            }
            catch (Exception e){
                Log.d("TAG", e.toString());
            }
    }

    public void onInformacion(View view) {
        // 在这里编写在按钮被点击时要执行的操作
        Intent intent = new Intent(this, informacion.class);
        startActivity(intent);
    }

    public void lanzarRegistrate(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

}