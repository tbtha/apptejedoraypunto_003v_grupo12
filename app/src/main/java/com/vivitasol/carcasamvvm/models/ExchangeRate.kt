package com.vivitasol.carcasamvvm.models

import kotlinx.serialization.Serializable

// Respuesta de la API Cambista.cl
@Serializable
data class CambistaResponse(
    val meta: CambistaMeta,
    val data: List<CambistaData>
)

@Serializable
data class CambistaMeta(
    val tz: String,
    val codes: List<String>,
    val carried_forward: Boolean,
    val date: String
)

@Serializable
data class CambistaData(
    val date: String,
    val rates: Map<String, Double>
)
