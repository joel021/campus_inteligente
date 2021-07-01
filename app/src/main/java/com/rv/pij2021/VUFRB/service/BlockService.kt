package com.rv.pij2021.VUFRB.service

import java.util.*

class BlockService : DecisionBlocksRA() {
    @JvmField
    var infos: HashMap<String, BlocoInfo>

    inner class BlocoInfo(var resumo: String, var status: String, var abertura: String, var fechamento: String)

    init {
        infos = HashMap()
        infos["REITORIA UFRB"] = BlocoInfo("Local dos administradores.", "Fechada", "7:00h", "18:00h")
        infos["PAV I"] = BlocoInfo("Pavilhão de aulas de disciplinas majoritariamente de exatas.", "Fechada", "7:00h", "18:00h")
        infos["BIBLIOTECA"] = BlocoInfo("Local para leitura, estudos e obtenção de livros.", "Fechada", "7:00h", "18:00h")
        infos["PAV II"] = BlocoInfo("Pavilhão de aulas de disciplinas majoritariamente de humanas.", "Fechada", "7:00h", "18:00h")
        infos["ANTIGA BIBLIOTECA"] = BlocoInfo("Local para estudos, tem laboratórios: informática, química e biologia.", "Fechada", "7:00h", "18:00h")
        infos["PREDIO SOLOS"] = BlocoInfo("Local para estudos, tem laboratórios: biologia, engenharia sanitária.", "Fechada", "7:00h", "18:00h")
        infos["CETEC"] = BlocoInfo("Centro de Ciências Exatas e Tecnológicas, local de administradores do centro. Onde se faz matrícula e setor de professores de exatas.", "Fechada", "7:00h", "18:00h")
        infos["Desconhecido"] = BlocoInfo("Não se tem informação a respeito,", "?", "?h", "?h")
    }
}