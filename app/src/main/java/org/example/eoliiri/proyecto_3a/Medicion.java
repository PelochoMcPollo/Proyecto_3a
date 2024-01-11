package org.example.eoliiri.proyecto_3a;

public class Medicion {
    private String idMedicion;
    private String instante;
    private String latitud;
    private String longitud;
    private String valor;
    private String idContaminante;

    public Medicion(String idMedicion, String instante, String latitud, String longitud, String valor, String idContaminante) {
        this.idMedicion = idMedicion;
        this.instante = instante;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.idContaminante = idContaminante;
    }

    // Getters y setters para cada campo

    public String getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(String idMedicion) {
        this.idMedicion = idMedicion;
    }

    public String getInstante() {
        return instante;
    }

    public void setInstante(String instante) {
        this.instante = instante;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getIdContaminante() {
        return idContaminante;
    }

    public void setIdContaminante(String idContaminante) {
        this.idContaminante = idContaminante;
    }
}
