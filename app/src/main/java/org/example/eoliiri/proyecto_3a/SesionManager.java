package org.example.eoliiri.proyecto_3a;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionManager {
    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_NAME = "preferenciasLogin";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public SesionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void guardarCredenciales(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public boolean SesionIniciada() {
        // Verifica si el campo de correo electrónico no está vacío
        return !getEmail().isEmpty();
    }

    public void cerrarSesion() {
        // Borra las credenciales almacenadas
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }
}