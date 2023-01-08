package net.vonforst.evmap.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Looper
import androidx.annotation.RequiresPermission

/**
 * Base class for [com.mapzen.android.lost.internal.FusionEngine].
 */
abstract class LocationEngine(protected val context: Context) {
    protected val requests: MutableList<LocationRequest> = mutableListOf()

    /**
     * Return most best recent location available.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    abstract fun getLastKnownLocation(): Location?

    /**
     * Enables the engine on receiving a valid location request.
     *
     * @param request Valid location request to enable.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocationUpdates(priority: Priority, intervalMs: Long, listener: LocationListener) {
        if (!requests.any { it.listener == listener }) {
            requests.add(LocationRequest(priority, intervalMs, listener))
        }
        enable()
    }

    /**
     * Disables the engine when no requests remain, otherwise updates the engine's configuration.
     *
     * @param requests Valid location request to enable.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun removeUpdates(listener: LocationListener) {
        this.requests.removeIf { it.listener == listener }
        disable()
        if (this.requests.isNotEmpty()) enable()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun removeAllRequests() {
        requests.clear()
        disable()
    }

    /**
     * Subclass should perform all operations required to enable the engine. (ex. Register for
     * location updates.)
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    protected abstract fun enable()

    /**
     * Subclass should perform all operations required to disable the engine. (ex. Remove location
     * updates.)
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    protected abstract fun disable()
    protected val looper: Looper
        get() = context.mainLooper

    interface Callback {
        fun reportLocation(location: Location)
    }
}

data class LocationRequest(
    val priority: Priority,
    val intervalMs: Long,
    val listener: LocationListener
)

enum class Priority {
    HIGH_ACCURACY,
    BALANCED_POWER_ACCURACY,
    LOW_POWER,
    NO_POWER
}