package com.rv.pij2021.VUFRB;

import java.util.ArrayList;
import java.util.List;

public class DecisaoBlocosRA {

    List<BlocoRA> blocoList = new ArrayList<>();

    public DecisaoBlocosRA(){

        // adicionar os dados já ordenados
        blocoList.add(new BlocoRA( new Ponto(-12.659766398168875,-39.08881134641973), new Ponto(-12.658567831849457,-39.08881134641973), "REITORIA UFRB", R.drawable.reitoria));
        blocoList.add(new BlocoRA( new Ponto(-12.658984821019407,-39.08901645008407), new Ponto(-12.658163195577893,-39.08901645008407), "PAV I", R.drawable.pav_i));
        blocoList.add(new BlocoRA( new Ponto(-12.658468433245261,-39.09138730199762), new Ponto(-12.657759826859012,-39.09138730199762), "BIBLIOTECA", R.drawable.biblioteca));
        blocoList.add(new BlocoRA( new Ponto(-12.657893226725244,-39.09221725932677), new Ponto(-12.657893226725244,-39.09221725932677), "PAV II", R.drawable.pav_ii));
        blocoList.add(new BlocoRA( new Ponto(-12.657991329712273,-39.09324480997324), new Ponto(-12.657031847991465,-39.09324480997324), "BOTANICA", R.drawable.antiga_biblioteca));
        blocoList.add(new BlocoRA( new Ponto(-12.65761722029418,-39.09497474768018), new Ponto(-12.657038449940227,-39.09497474768018), "PAV ENG", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA( new Ponto(-12.657695739379028,-39.09071846736173), new Ponto(-12.657129481429301,-39.09071846736173), "CETEC", R.drawable.cetec));
        blocoList.add(new BlocoRA( new Ponto(-12.65846381653017,-39.0864038081611), new Ponto(-12.65846381653017,-39.0864038081611), "PREDIO SOLOS", R.drawable.predio_solos));
        blocoList.add(new BlocoRA( new Ponto(-12.657848817426004,-39.08554818345819), new Ponto(-12.657550476903356,-39.08554818345819), "LAB 1", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA( new Ponto(-12.658298944571804,-39.08532019570634), new Ponto(-12.657683945075043,-39.08532019570634), "LAB 2", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA( new Ponto(-12.657757221688618,-39.08522095397907), new Ponto(-12.657757221688618,-39.08522095397907), "LAB 3", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA( new Ponto(-12.658424561307958,-39.084722063133846), new Ponto(-12.657817413176897,-39.084722063133846), "LAB 4", R.drawable.campus_inteligente));
        blocoList.add(new BlocoRA( new Ponto(-12.658510393877538,-39.08415810360615), new Ponto(-12.657903774852652,-39.08415810360615), "LAB 5", R.drawable.campus_inteligente));

    }

    public class BlocoRA {
        public Ponto p1, p2;
        int imgId;
        String titulo;

        public BlocoRA(Ponto p1, Ponto p2, String titulo, int imgId){
            this.p1 = p1;
            this.p2 = p2;
            this.titulo = titulo;
            this.imgId = imgId;
        }

        public BlocoRA(){

        }
    }

    public BlocoRA bloco(double lat, double lon){
        for (BlocoRA blocoRA : blocoList){

            // a priori, sabe-se que P1 <= P2, sempre!
            if (blocoRA.p1.lat <= lat && blocoRA.p2.lat > lat && blocoRA.p1.lon <= lon && blocoRA.p2.lon >= lon){
                return blocoRA;
            }

        }
        return new BlocoRA(null,null,"Desconhecido",R.drawable.campus_inteligente);
    }

    public int imgId(double lat, double lon){
        for (BlocoRA blocoRA : blocoList){

            // a priori, sabe-se que P1 <= P2, sempre!
            if (blocoRA.p1.lat <= lat && blocoRA.p2.lat > lat && blocoRA.p1.lon <= lon && blocoRA.p2.lon >= lon){
                return blocoRA.imgId;
            }

        }
        return R.drawable.campus_inteligente;
    }
    public String titulo(double lat, double lon){

        for (BlocoRA blocoRA : blocoList){

            // a priori, sabe-se que P1 <= P2, sempre!
            if (blocoRA.p1.lat <= lat && blocoRA.p2.lat > lat && blocoRA.p1.lon <= lon && blocoRA.p2.lon >= lon){
                return blocoRA.titulo;
            }

        }
        return "Desconhecido";
    }

}
