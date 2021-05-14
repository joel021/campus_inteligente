package com.rv.pij2021.VUFRB;

public class Ponto {
    public double lat, lon;

    public Ponto(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
    public Ponto(){

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
