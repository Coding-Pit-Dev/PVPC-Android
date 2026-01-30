package com.codingpit.pvpcplanner.ui.components.graph

import com.codingpit.pvpcplanner.domain.models.PVPCModel
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener

class MarkerVisibilityListener(
    private val responseData: List<PVPCModel>,
    private val onMarkerChanged: (Int, Double) -> Unit,
) : CartesianMarkerVisibilityListener {
    override fun onShown(
        marker: CartesianMarker,
        targets: List<CartesianMarker.Target>,
    ) {
        super.onShown(marker, targets)
        val target = targets.firstOrNull() ?: return
        val price = responseData.firstOrNull { it.startHour == target.x.toInt() }?.pcb ?: return
        onMarkerChanged(
            target.x.toInt(),
            price,
        )
    }

    override fun onUpdated(
        marker: CartesianMarker,
        targets: List<CartesianMarker.Target>,
    ) {
        super.onUpdated(marker, targets)
        val target = targets.firstOrNull() ?: return
        val price = responseData.firstOrNull { it.startHour == target.x.toInt() }?.pcb ?: return
        onMarkerChanged(
            target.x.toInt(),
            price,
        )
    }
}
