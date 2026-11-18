package com.lajara.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lajara.centrotareas.model.CargaUi
import com.lajara.centrotareas.model.PrimosUi
import com.lajara.centrotareas.viewmodel.TareasViewModel
import com.lajara.centrotareas.viewmodel.TareasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel = viewModel(factory = TareasViewModelFactory())
) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val avancePrimos by vm.avancePrimos.collectAsState()
    val carga by vm.carga.collectAsState()
    val tiempos by vm.tiempos.collectAsState()
    val simularError by vm.simularError.collectAsState()
    var toques by rememberSaveable { mutableIntStateOf(0) }
    var texto by rememberSaveable { mutableStateOf("500000") }
    var hastaUsado by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF2F6FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CentroTareas",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Panel de tareas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Cada tarea corre en segundo plano y la pantalla no se congela.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TarjetaTarea(
                numero = "1",
                titulo = "Tarea con progreso",
                descripcion = "Corre en segundo plano y se puede cancelar"
            ) {
                FilledTonalButton(
                    onClick = { toques++ },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFD6E6F5),
                        contentColor = Color(0xFF0B2F4A)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Tócame: $toques")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { vm.tareaLarga() },
                        enabled = !corriendo,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Iniciar")
                    }
                    OutlinedButton(
                        onClick = { vm.cancelar() },
                        enabled = corriendo,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    trackColor = Color(0xFFD6E6F5)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progreso * 100).toInt()} %",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD6E6F5)
                    ) {
                        Text(
                            text = mensaje,
                            color = Color(0xFF0B2F4A),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            TarjetaTarea(
                numero = "2",
                titulo = "Contar números primos",
                descripcion = "Cálculo pesado con progreso y cancelación"
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it.filter { c -> c.isDigit() } },
                    label = { Text("Hasta el número") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val n = texto.toIntOrNull()
                            if (n != null && n >= 2) {
                                hastaUsado = n
                                vm.contar(n)
                            }
                        },
                        enabled = primos !is PrimosUi.Calculando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Contar")
                    }
                    OutlinedButton(
                        onClick = { vm.cancelarPrimos() },
                        enabled = primos is PrimosUi.Calculando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                }
                when (val p = primos) {
                    is PrimosUi.Inicial -> {}
                    is PrimosUi.Calculando -> {
                        LinearProgressIndicator(
                            progress = { avancePrimos },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            trackColor = Color(0xFFD6E6F5)
                        )
                        Text(
                            text = "Calculando... ${(avancePrimos * 100).toInt()} %",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is PrimosUi.Cancelado -> Text(
                        text = "Cancelado",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    is PrimosUi.Listo -> RecuadroResultado(esError = false) {
                        Text(
                            text = "Primos hasta $hastaUsado: ${p.cantidad}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tiempo: ${p.milisegundos} ms",
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            TarjetaTarea(
                numero = "3",
                titulo = "Dos cargas",
                descripcion = "Una por una contra A la vez"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Switch(
                        checked = simularError,
                        onCheckedChange = { vm.cambiarSimularError(it) }
                    )
                    Text("Simular error")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { vm.cargarUnaPorUna() },
                        enabled = carga !is CargaUi.Cargando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Una por una")
                    }
                    Button(
                        onClick = { vm.cargarALaVez() },
                        enabled = carga !is CargaUi.Cargando,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("A la vez")
                    }
                }
                when (val c = carga) {
                    is CargaUi.Inicial -> {}
                    is CargaUi.Cargando -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is CargaUi.Exito -> RecuadroResultado(esError = false) {
                        Text(
                            text = "Modo: ${c.modo}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Perfil: ${c.perfil}",
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Pedidos: ${c.pedidos}",
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Tiempo: ${c.milisegundos} ms",
                            color = Color(0xFF1B5E20)
                        )
                    }
                    is CargaUi.Error -> {
                        RecuadroResultado(esError = true) {
                            Text(
                                text = c.mensaje,
                                color = Color(0xFFB3261E),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        OutlinedButton(
                            onClick = { vm.cargarALaVez() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }

                if (tiempos.unaPorUna.isNotEmpty() || tiempos.aLaVez.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF2F6FA),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Comparación de tiempos",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text("Modo", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                    Text("Veces", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text("Promedio", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    Text("Último", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                }

                                val pUna = tiempos.promedioUnaPorUna
                                val uUna = tiempos.unaPorUna.lastOrNull()
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text("Una por una", modifier = Modifier.weight(1.5f))
                                    Text("${tiempos.unaPorUna.size}", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(if (pUna != null) "$pUna ms" else "-", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    Text(if (uUna != null) "$uUna ms" else "-", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                }

                                val pVez = tiempos.promedioALaVez
                                val uVez = tiempos.aLaVez.lastOrNull()
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text("A la vez", modifier = Modifier.weight(1.5f))
                                    Text("${tiempos.aLaVez.size}", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                    Text(if (pVez != null) "$pVez ms" else "-", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                    Text(if (uVez != null) "$uVez ms" else "-", modifier = Modifier.weight(1.2f), textAlign = TextAlign.End)
                                }
                            }

                            val diff = tiempos.diferencia
                            if (diff != null) {
                                val textoDiff = if (diff >= 0) {
                                    "A la vez ahorra $diff ms en promedio"
                                } else {
                                    "Una por una fue ${-diff} ms más rápida en promedio"
                                }
                                Text(
                                    text = textoDiff,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTarea(
    numero: String,
    titulo: String,
    descripcion: String,
    contenido: @Composable ColumnScope.() -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = numero,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            contenido()
        }
    }
}

@Composable
private fun RecuadroResultado(
    esError: Boolean,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (esError) Color(0xFFFBE3E3) else Color(0xFFE3F1E0),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = contenido
        )
    }
}