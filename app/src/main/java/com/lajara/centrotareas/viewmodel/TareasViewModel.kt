package com.lajara.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lajara.centrotareas.data.TareasRepository
import com.lajara.centrotareas.model.CargaUi
import com.lajara.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TiemposCarga(
    val unaPorUna: List<Long> = emptyList(),
    val aLaVez: List<Long> = emptyList()
) {
    val promedioUnaPorUna: Long?
        get() = if (unaPorUna.isNotEmpty()) unaPorUna.average().toLong() else null

    val promedioALaVez: Long?
        get() = if (aLaVez.isNotEmpty()) aLaVez.average().toLong() else null

    val diferencia: Long?
        get() {
            val p1 = promedioUnaPorUna
            val p2 = promedioALaVez
            return if (p1 != null && p2 != null) p1 - p2 else null
        }
}

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private var trabajo: Job? = null

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    fun tareaLarga() {
        if (_corriendo.value) return
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                repo.tareaConProgreso().collect { valor ->
                    _progreso.value = valor
                }
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private val _avancePrimos = MutableStateFlow(0f)
    val avancePrimos: StateFlow<Float> = _avancePrimos.asStateFlow()

    private var trabajoPrimos: Job? = null

    fun contar(hasta: Int) {
        trabajoPrimos = viewModelScope.launch {
            _avancePrimos.value = 0f
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta) { avance ->
                    _avancePrimos.value = avance
                }
                val milisegundos = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, milisegundos)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _tiempos = MutableStateFlow(TiemposCarga())
    val tiempos: StateFlow<TiemposCarga> = _tiempos.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cargarUnaPorUna() {
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val perfil = repo.cargarPerfil()
                val pedidos = repo.cargarPedidos(_simularError.value)
                val milisegundos = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, milisegundos)
                _tiempos.update { actual ->
                    actual.copy(unaPorUna = actual.unaPorUna + milisegundos)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: " + (e.message ?: "error desconocido"))
            }
        }
    }

    fun cargarALaVez() {
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = coroutineScope {
                    val a = async { repo.cargarPerfil() }
                    val b = async { repo.cargarPedidos(_simularError.value) }
                    Pair(a.await(), b.await())
                }
                val milisegundos = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, milisegundos)
                _tiempos.update { actual ->
                    actual.copy(aLaVez = actual.aLaVez + milisegundos)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: " + (e.message ?: "error desconocido"))
            }
        }
    }
}