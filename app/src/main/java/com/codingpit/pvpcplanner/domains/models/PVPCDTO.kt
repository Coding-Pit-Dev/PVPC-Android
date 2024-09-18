package com.codingpit.pvpcplanner.domains.models

import com.squareup.moshi.Json

data class PVPCDTO(
    @Json(name = "Dia") val dia: String?,
    @Json(name = "Hora") val hora: String?,
    @Json(name = "PCB") val pcb: String?,
    @Json(name = "CYM") val cym: String?,
    @Json(name = "COF2TD") val cof2td: String?,
    @Json(name = "PMHPCB") val pmhpcb: String?,
    @Json(name = "PMHCYM") val pmhcyM: String?,
    @Json(name = "SAHPCB") val sahpcb: String?,
    @Json(name = "SAHCYM") val sahcym: String?,
    @Json(name = "FOMPCB") val fompcb: String?,
    @Json(name = "FOMCYM") val fomcym: String?,
    @Json(name = "FOSPCB") val fospcb: String?,
    @Json(name = "FOSCYM") val foscyM: String?,
    @Json(name = "INTPCB") val intpcb: String?,
    @Json(name = "INTCYM") val intcym: String?,
    @Json(name = "PCAPPCB") val cappcb: String?,
    @Json(name = "PCAPCYM") val capcym: String?,
    @Json(name = "TEUPCB") val teupcb: String?,
    @Json(name = "TEUCYM") val teucym: String?,
    @Json(name = "CCVPCB") val ccvpcb: String?,
    @Json(name = "CCVCYM") val ccvcyM: String?,
    @Json(name = "EDSRPCB") val edsrpcb: String?,
    @Json(name = "EDSRCYM") val edsrcym: String?,
    @Json(name = "TAHPCB") val tahpcb: String?,
    @Json(name = "TAHCYM") val tahcym: String?
)
