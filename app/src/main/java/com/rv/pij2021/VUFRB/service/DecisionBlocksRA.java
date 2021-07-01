package com.rv.pij2021.VUFRB.service;

import android.util.Log;

import com.rv.pij2021.VUFRB.R;
import com.rv.pij2021.VUFRB.model.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class DecisionBlocksRA {

    List<BlocoRA> blocoList = new ArrayList<>();

    public DecisionBlocksRA(){

        // adicionar os dados já ordenados
        blocoList.add(new BlocoRA(new Point(-12.729291486317871f, -39.184233657393776f), "Sapeaçu", R.drawable.reitoria));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f), "REITORIA UFRB", R.drawable.reitoria));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"PAV I", R.drawable.pav_i));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"BIBLIOTECA", R.drawable.biblioteca));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"PAV II", R.drawable.pav_ii));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"BOTANICA", R.drawable.antiga_biblioteca));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"PAV ENG", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"CETEC", R.drawable.cetec));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"PREDIO SOLOS", R.drawable.predio_solos));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"LAB 1", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"LAB 2", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"LAB 3", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"LAB 4", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA(new Point(-12.659766398168875f,-39.08881134641973f),"LAB 5", R.drawable.campus_inteligente));

    }

    public class BlocoRA {
        public Point p0;
        public int imgId;
        public String titulo;

        public BlocoRA(Point p0, String titulo, int imgId){
            this.p0 = p0;
            this.titulo = titulo;
            this.imgId = imgId;
        }

        public BlocoRA(){

        }
    }

    // o angulo phi é em relação ao eixo y
    // é positivo no sentido antihorário
    // o intervalo dele é entre 0 a 2PI
    private double phi(float x, double x0, float y, double y0){

        double phi = Math.atan(Math.abs(x-x0)/Math.abs(y-y0));

        if(x-x0 > 0 && y-y0 > 0){
            return 2*Math.PI-phi;
        }else if (x-x0 < 0 && y-y0 > 0){
            return phi;
        }else if(x-x0 > 0 && y-y0 < 0){
            return Math.PI + phi;
        }

        return Math.PI - phi;
    }

    public BlocoRA bloco(SensorService sensorService){

        double theta = sensorService.getTheta();

        Log.i("RAUFRB", "theta = "+(theta*180.f/Math.PI));

        for (BlocoRA blocoRA : blocoList){

            // a priori, sabe-se que aresta1 <= aresta2, sempre!
            if (Math.pow(blocoRA.p0.getLon() - sensorService.longitude,2) + Math.pow(blocoRA.p0.getLat() - sensorService.latitude, 2) - 1 < 0){
                //Log.i("RAUFRB", "Dentro da área. r^2 = 0.0011191911348022736");

                double phi = phi(blocoRA.p0.getLon(), sensorService.longitude, blocoRA.p0.getLat(), sensorService.latitude);
                Log.i("RAUFRB", "phi = "+(phi*180.f/Math.PI));

                if (Math.abs(phi-theta) < 0.18){ // aproximadamente 10° de tolerancia = 0.18
                    return blocoRA;
                }
                break;
            }

        }

        return new BlocoRA(null,"Desconhecido",R.drawable.campus_inteligente);

    }

    public BlocoRA bloco(double longitude, double latitude, double theta){

        for (BlocoRA blocoRA : blocoList){

            // a priori, sabe-se que aresta1 <= aresta2, sempre!
            if (Math.pow(blocoRA.p0.getLon() - longitude,2) + Math.pow(blocoRA.p0.getLat() - latitude, 2) - 1 < 0){

                double phi = Math.atan((blocoRA.p0.getLat()- latitude)/(blocoRA.p0.getLon()- longitude));

                if (Math.abs(phi-theta) < 0.18){ // aproximadamente 10° de tolerancia
                    return blocoRA;
                }
            }

        }

        return new BlocoRA(null,"Desconhecido",R.drawable.campus_inteligente);
    }

}
