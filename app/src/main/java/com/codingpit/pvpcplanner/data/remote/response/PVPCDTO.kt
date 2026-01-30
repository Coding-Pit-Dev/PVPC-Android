package com.codingpit.pvpcplanner.data.remote.response

import com.squareup.moshi.Json

data class PVPCDTO(
    @param:Json(name = "Dia") val day: String,
    @param:Json(name = "Hora") val hour: String,
    @param:Json(name = "PCB") val pcb: String,
    @param:Json(name = "CYM") val cym: String,
    @param:Json(name = "COF2TD") val cof2td: String?,
    @param:Json(name = "PMHPCB") val pmhpcb: String?,
    @param:Json(name = "PMHCYM") val pmhcyM: String?,
    @param:Json(name = "SAHPCB") val sahpcb: String?,
    @param:Json(name = "SAHCYM") val sahcym: String?,
    @param:Json(name = "FOMPCB") val fompcb: String?,
    @param:Json(name = "FOMCYM") val fomcym: String?,
    @param:Json(name = "FOSPCB") val fospcb: String?,
    @param:Json(name = "FOSCYM") val foscyM: String?,
    @param:Json(name = "INTPCB") val intpcb: String?,
    @param:Json(name = "INTCYM") val intcym: String?,
    @param:Json(name = "PCAPPCB") val cappcb: String?,
    @param:Json(name = "PCAPCYM") val capcym: String?,
    @param:Json(name = "TEUPCB") val teupcb: String?,
    @param:Json(name = "TEUCYM") val teucym: String?,
    @param:Json(name = "CCVPCB") val ccvpcb: String?,
    @param:Json(name = "CCVCYM") val ccvcyM: String?,
    @param:Json(name = "EDSRPCB") val edsrpcb: String?,
    @param:Json(name = "EDSRCYM") val edsrcym: String?,
    @param:Json(name = "TAHPCB") val tahpcb: String?,
    @param:Json(name = "TAHCYM") val tahcym: String?,
)
