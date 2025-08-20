package com.codingpit.pvpcplanner.data.remote.response

import com.squareup.moshi.Json

data class PVPCDTO(
    @get:Json(name = "Dia") val day: String,
    @get:Json(name = "Hora") val hour: String,
    @get:Json(name = "PCB") val pcb: String,
    @get:Json(name = "CYM") val cym: String,
    @get:Json(name = "COF2TD") val cof2td: String?,
    @get:Json(name = "PMHPCB") val pmhpcb: String?,
    @get:Json(name = "PMHCYM") val pmhcyM: String?,
    @get:Json(name = "SAHPCB") val sahpcb: String?,
    @get:Json(name = "SAHCYM") val sahcym: String?,
    @get:Json(name = "FOMPCB") val fompcb: String?,
    @get:Json(name = "FOMCYM") val fomcym: String?,
    @get:Json(name = "FOSPCB") val fospcb: String?,
    @get:Json(name = "FOSCYM") val foscyM: String?,
    @get:Json(name = "INTPCB") val intpcb: String?,
    @get:Json(name = "INTCYM") val intcym: String?,
    @get:Json(name = "PCAPPCB") val cappcb: String?,
    @get:Json(name = "PCAPCYM") val capcym: String?,
    @get:Json(name = "TEUPCB") val teupcb: String?,
    @get:Json(name = "TEUCYM") val teucym: String?,
    @get:Json(name = "CCVPCB") val ccvpcb: String?,
    @get:Json(name = "CCVCYM") val ccvcyM: String?,
    @get:Json(name = "EDSRPCB") val edsrpcb: String?,
    @get:Json(name = "EDSRCYM") val edsrcym: String?,
    @get:Json(name = "TAHPCB") val tahpcb: String?,
    @get:Json(name = "TAHCYM") val tahcym: String?,
)
