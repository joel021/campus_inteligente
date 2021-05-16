package com.rv.pij2021.VUFRB;

import java.util.HashMap;

public class InfosDecisaoBlocos extends DecisaoBlocosRA {
    public HashMap<String, BlocoInfo> infos;

    public InfosDecisaoBlocos(){
        infos = new HashMap<>();
        infos.put("REITORIA UFRB", new BlocoInfo("Local dos administradores.", "Fechada", "7:00h", "18:00h"));
        infos.put("PAV I", new BlocoInfo("Pavilhão de aulas de disciplinas majoritariamente de exatas.", "Fechada", "7:00h", "18:00h"));
        infos.put("BIBLIOTECA", new BlocoInfo("Local para leitura, estudos e obtenção de livros.", "Fechada", "7:00h", "18:00h"));
        infos.put("PAV II", new BlocoInfo("Pavilhão de aulas de disciplinas majoritariamente de humanas.", "Fechada", "7:00h", "18:00h"));
        infos.put("ANTIGA BIBLIOTECA", new BlocoInfo("Local para estudos, tem laboratórios: informática, química e biologia.", "Fechada", "7:00h", "18:00h"));
        infos.put("PREDIO SOLOS", new BlocoInfo("Local para estudos, tem laboratórios: biologia, engenharia sanitária.", "Fechada", "7:00h", "18:00h"));

        infos.put("CETEC", new BlocoInfo("Centro de Ciências Exatas e Tecnológicas, local de administradores do centro. Onde se faz matrícula e setor de professores de exatas.", "Fechada", "7:00h", "18:00h"));
        infos.put("Desconhecido", new BlocoInfo("Não se tem informação a respeito,", "?", "?h", "?h"));
    }
    public class BlocoInfo {
        String resumo, status,abertura,fechamento;

        public BlocoInfo(String resumo, String status, String abertura, String fechamento){
            this.abertura = abertura;
            this.resumo = resumo;
            this.status = status;
            this.fechamento = fechamento;
        }
    }

}
