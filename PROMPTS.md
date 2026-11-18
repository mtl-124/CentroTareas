# PROMPTS USADOS

## **1. Primer Prompt**
Contexto del proyecto: app Android llamada CentroTareas, paquete com.lajara.centrotareas, hecha con Kotlin, Jetpack Compose y Material 3 (sin XML). Usa arquitectura MVVM + Repository con una interface. Archivos existentes:
- model/Estados.kt: PrimosUi (Inicial, Calculando, Listo(cantidad, milisegundos), Cancelado) y CargaUi (Inicial, Cargando, Exito(modo, perfil, pedidos, milisegundos), Error(mensaje)).
- data/TareasRepository.kt: interface con funciones suspend para contar primos, cargar perfil y cargar pedidos (esta última recibe simularError: Boolean).
- data/TareasRepositoryLocal.kt: implementación con delay(2000) al cargar el perfil, delay(3000) al cargar los pedidos (lanza IOException("Sin conexión") si simularError es true) y un conteo de primos dentro de withContext(Dispatchers.Default) que revisa ensureActive().
- viewmodel/TareasViewModel.kt: expone StateFlow para mensaje, progreso, corriendo, primos, carga y simularError, con funciones para iniciar y cancelar la tarea con progreso, contar y cancelar primos, y cargar una por una o a la vez con async.
- viewmodel/TareasViewModelFactory.kt y ui/PantallaTareas.kt (tres tarjetas: "Tarea con progreso", "Contar números primos" y "Dos cargas") más ui/theme.

Reglas obligatorias que debes respetar siempre:
1. Mantener MVVM + Repository: la pantalla solo dibuja estado y envía eventos al ViewModel; el ViewModel no recibe Context ni conoce Compose; el trabajo pesado va en el Repository.
2. Prohibido GlobalScope, runBlocking y Thread.sleep.
3. Si capturas una excepción en una corrutina, relanza siempre CancellationException antes de manejar otras excepciones.
4. No crees archivos nuevos, no agregues librerías ni dependencias, y modifica solo los archivos que indique este prompt.
5. No cambies los textos visibles de la interfaz ni el comportamiento de las demás tarjetas, salvo que se indique.
6. Escribe el código en español para nombres de variables y textos, igual que el resto del proyecto, sin comentarios explicativos dentro del código.

Forma de trabajar: antes de escribir código, dime en 3 a 5 líneas tu plan y qué archivos vas a tocar. Luego aplica los cambios. Al final, lista cada archivo modificado con un resumen de qué cambió y confirma que el código compila. No hagas commit.

Tarea: mover la tarea simulada con progreso desde el ViewModel al Repository y comunicar el avance con un Flow.

Estado actual: en TareasViewModel, la función que inicia la tarea con progreso lanza una corrutina con viewModelScope.launch, entra a withContext(Dispatchers.Default), da 100 vueltas con delay(40) y en cada vuelta actualiza el StateFlow de progreso con i / 100f. Mantiene un Job para poder cancelar y un StateFlow "corriendo" que vuelve a false en un bloque finally. El mensaje pasa por "Listo", "Trabajando...", "Terminó" y "Cancelado".

Qué debes hacer:
1. En la interface TareasRepository agrega una función (no suspend) que devuelva Flow<Float> con el avance de la tarea, por ejemplo tareaConProgreso().
2. En TareasRepositoryLocal impleméntala con flow { ... emit(...) }: 100 pasos, con delay(40) entre pasos, emitiendo i / 100f (de 0.01f a 1f). Aplica flowOn(Dispatchers.Default) al flow completo; no uses withContext dentro del flow.
3. En TareasViewModel borra el ciclo y el withContext de la tarea y haz que recolecte el Flow del Repository dentro de viewModelScope.launch, actualizando el StateFlow de progreso con cada valor. Conserva exactamente el comportamiento actual: al iniciar, progreso en 0f y mensaje "Trabajando..."; al terminar, mensaje "Terminó"; no permitir iniciar si ya está corriendo; el Job guardado; cancelar() cancela el Job y pone el mensaje "Cancelado"; "corriendo" vuelve a false en un finally.
4. Elimina de TareasViewModel los imports que dejen de usarse (Dispatchers, withContext, delay y los de Log si los hubiera).
5. No modifiques PantallaTareas.kt ni Estados.kt: la pantalla debe seguir funcionando igual.

Criterios de aceptación: al pulsar Iniciar la barra avanza de 0 a 100 % en unos 4 segundos; Cancelar a mitad la detiene y muestra "Cancelado"; después se puede iniciar de nuevo; Tócame sigue sumando mientras corre; el proyecto compila sin advertencias nuevas; el ciclo ya no existe en el ViewModel.

## **2. Segundo Prompt**
Contexto del proyecto: app Android llamada CentroTareas, paquete com.lajara.centrotareas, hecha con Kotlin, Jetpack Compose y Material 3 (sin XML). Usa arquitectura MVVM + Repository con una interface. Archivos existentes:
- model/Estados.kt: PrimosUi (Inicial, Calculando, Listo(cantidad, milisegundos), Cancelado) y CargaUi (Inicial, Cargando, Exito(modo, perfil, pedidos, milisegundos), Error(mensaje)).
- data/TareasRepository.kt: interface con funciones suspend para contar primos, cargar perfil y cargar pedidos (esta última recibe simularError: Boolean).
- data/TareasRepositoryLocal.kt: implementación con delay(2000) al cargar el perfil, delay(3000) al cargar los pedidos (lanza IOException("Sin conexión") si simularError es true) y un conteo de primos dentro de withContext(Dispatchers.Default) que revisa ensureActive().
- viewmodel/TareasViewModel.kt: expone StateFlow para mensaje, progreso, corriendo, primos, carga y simularError, con funciones para iniciar y cancelar la tarea con progreso, contar y cancelar primos, y cargar una por una o a la vez con async.
- viewmodel/TareasViewModelFactory.kt y ui/PantallaTareas.kt (tres tarjetas: "Tarea con progreso", "Contar números primos" y "Dos cargas") más ui/theme.

Reglas obligatorias que debes respetar siempre:
1. Mantener MVVM + Repository: la pantalla solo dibuja estado y envía eventos al ViewModel; el ViewModel no recibe Context ni conoce Compose; el trabajo pesado va en el Repository.
2. Prohibido GlobalScope, runBlocking y Thread.sleep.
3. Si capturas una excepción en una corrutina, relanza siempre CancellationException antes de manejar otras excepciones.
4. No crees archivos nuevos, no agregues librerías ni dependencias, y modifica solo los archivos que indique este prompt.
5. No cambies los textos visibles de la interfaz ni el comportamiento de las demás tarjetas, salvo que se indique.
6. Escribe el código en español para nombres de variables y textos, igual que el resto del proyecto, sin comentarios explicativos dentro del código.

Forma de trabajar: antes de escribir código, dime en 3 a 5 líneas tu plan y qué archivos vas a tocar. Luego aplica los cambios. Al final, lista cada archivo modificado con un resumen de qué cambió y confirma que el código compila. No hagas commit.

Tarea: mostrar el porcentaje de avance mientras se cuenta cuántos números primos hay hasta N.

Estado actual: la función de contar primos del Repository recorre los números de 2 hasta N dentro de withContext(Dispatchers.Default), revisa ensureActive() en cada vuelta y devuelve solo la cantidad final. El ViewModel la llama desde viewModelScope.launch, mide el tiempo con System.currentTimeMillis(), pasa el estado por Calculando, Listo(cantidad, milisegundos) o Cancelado (relanzando CancellationException). La pantalla, mientras está en Calculando, solo muestra el texto "Calculando...".

Qué debes hacer:
1. En el Repository (interface e implementación) cambia la función de contar primos para que también reporte el avance como un número entre 0f y 1f. Usa la solución más simple: un parámetro de función (por ejemplo alAvanzar: (Float) -> Unit) y que la función siga devolviendo la cantidad como Int. No cambies PrimosUi.
2. En la implementación, reporta el avance solo unas 100 veces como máximo (no en cada número) y reporta 1f al terminar. Mantén ensureActive() en cada vuelta, de modo que cancelar siga funcionando de inmediato. La función debe seguir corriendo en Dispatchers.Default.
3. En TareasViewModel agrega un StateFlow<Float> de solo lectura (por ejemplo avancePrimos), que se reinicie a 0f al empezar a contar y se actualice con lo que reporte el Repository. No cambies cómo se manejan los estados Calculando, Listo y Cancelado ni el relanzado de CancellationException.
4. En PantallaTareas, dentro de la tarjeta "Contar números primos", cuando el estado sea Calculando muestra un LinearProgressIndicator de ancho completo con el avance y, debajo, el texto "Calculando... NN %" (porcentaje entero) centrado. Los demás estados (Inicial, Cancelado, Listo) y los botones Contar y Cancelar no cambian.
5. No toques la tarjeta "Tarea con progreso", la tarjeta "Dos cargas", el Factory ni el tema.

Criterios de aceptación: con N = 500000 se ve la barra y el porcentaje subiendo de 0 a 100 %; Cancelar a mitad detiene el cálculo y muestra "Cancelado"; al terminar aparece el recuadro verde con el resultado (por ejemplo, 41538 primos hasta 500000); para N = 1000 el resultado sigue siendo 168; la pantalla no se congela mientras calcula.

## **3. Tercero Prompt**
Contexto del proyecto: app Android llamada CentroTareas, paquete com.lajara.centrotareas, hecha con Kotlin, Jetpack Compose y Material 3 (sin XML). Usa arquitectura MVVM + Repository con una interface. Archivos existentes:
- model/Estados.kt: PrimosUi (Inicial, Calculando, Listo(cantidad, milisegundos), Cancelado) y CargaUi (Inicial, Cargando, Exito(modo, perfil, pedidos, milisegundos), Error(mensaje)).
- data/TareasRepository.kt: interface con funciones suspend para contar primos, cargar perfil y cargar pedidos (esta última recibe simularError: Boolean).
- data/TareasRepositoryLocal.kt: implementación con delay(2000) al cargar el perfil, delay(3000) al cargar los pedidos (lanza IOException("Sin conexión") si simularError es true) y un conteo de primos dentro de withContext(Dispatchers.Default) que revisa ensureActive().
- viewmodel/TareasViewModel.kt: expone StateFlow para mensaje, progreso, corriendo, primos, carga y simularError, con funciones para iniciar y cancelar la tarea con progreso, contar y cancelar primos, y cargar una por una o a la vez con async.
- viewmodel/TareasViewModelFactory.kt y ui/PantallaTareas.kt (tres tarjetas: "Tarea con progreso", "Contar números primos" y "Dos cargas") más ui/theme.

Reglas obligatorias que debes respetar siempre:
1. Mantener MVVM + Repository: la pantalla solo dibuja estado y envía eventos al ViewModel; el ViewModel no recibe Context ni conoce Compose; el trabajo pesado va en el Repository.
2. Prohibido GlobalScope, runBlocking y Thread.sleep.
3. Si capturas una excepción en una corrutina, relanza siempre CancellationException antes de manejar otras excepciones.
4. No crees archivos nuevos, no agregues librerías ni dependencias, y modifica solo los archivos que indique este prompt.
5. No cambies los textos visibles de la interfaz ni el comportamiento de las demás tarjetas, salvo que se indique.
6. Escribe el código en español para nombres de variables y textos, igual que el resto del proyecto, sin comentarios explicativos dentro del código.

Forma de trabajar: antes de escribir código, dime en 3 a 5 líneas tu plan y qué archivos vas a tocar. Luego aplica los cambios. Al final, lista cada archivo modificado con un resumen de qué cambió y confirma que el código compila. No hagas commit.

Tarea: acumular los tiempos de las dos formas de cargar ("Una por una" y "A la vez") y mostrar una tabla para ver la diferencia. Esta mejora cambia solo el ViewModel y la pantalla; el Repository no se toca.

Estado actual: las funciones que cargan una por una y a la vez miden el tiempo con System.currentTimeMillis() y, si todo sale bien, publican CargaUi.Exito(modo, perfil, pedidos, milisegundos), con modo "Una por una" o "A la vez". Si falla, publican CargaUi.Error. La tarjeta "Dos cargas" solo muestra el último resultado.

Qué debes hacer:
1. En TareasViewModel.kt declara, en el mismo archivo y fuera de la clase (no crees archivos nuevos), una data class con dos listas de Long: los tiempos de "Una por una" y los de "A la vez", vacías por defecto. Dentro de esa clase agrega propiedades calculadas: promedio de cada modo (null si la lista está vacía) y la diferencia entre promedios (promedio de Una por una menos promedio de A la vez, o null si falta alguno de los dos).
2. En el ViewModel agrega un StateFlow de solo lectura con esa clase. En cada carga exitosa agrega el tiempo medido a la lista del modo correspondiente, actualizando el estado de forma segura (por ejemplo con update). Las cargas con error no se acumulan. No cambies cómo se manejan las excepciones ni el relanzado de CancellationException.
3. En PantallaTareas, dentro de la tarjeta "Dos cargas" y debajo del resultado, muestra la tabla solo cuando exista al menos un tiempo acumulado. Título "Comparación de tiempos". Columnas: Modo, Veces, Promedio y Último. Una fila de encabezado en negrita y una fila por modo ("Una por una" y "A la vez"). Si un modo aún no tiene datos, muestra "-" en Promedio y Último y 0 en Veces. Los tiempos se muestran con el sufijo " ms". Cuando existan datos de ambos modos, agrega debajo una línea: si la diferencia es positiva, "A la vez ahorra X ms en promedio"; si es negativa, "Una por una fue X ms más rápida en promedio".
4. Usa solo Material 3 y componentes de Compose (Row, Column, Text con pesos), sin librerías de tablas.
5. No cambies el Repository, el Factory, el modelo ni las otras dos tarjetas.

Criterios de aceptación: al pulsar "Una por una" y "A la vez" varias veces, la tabla acumula los tiempos (Una por una ≈ 5000 ms y A la vez ≈ 3000 ms) y el promedio y el último se actualizan; la línea de diferencia aparece cuando hay datos de ambos; con "Simular error" activado y una carga fallida, la tabla no suma ese intento; al rotar la pantalla la tabla conserva sus datos.

## **4. Cuarto Prompt**
Contexto del proyecto: app Android llamada CentroTareas, paquete com.lajara.centrotareas, hecha con Kotlin, Jetpack Compose y Material 3 (sin XML). Usa arquitectura MVVM + Repository con una interface. Archivos existentes:
- model/Estados.kt: PrimosUi (Inicial, Calculando, Listo(cantidad, milisegundos), Cancelado) y CargaUi (Inicial, Cargando, Exito(modo, perfil, pedidos, milisegundos), Error(mensaje)).
- data/TareasRepository.kt: interface con funciones suspend para contar primos, cargar perfil y cargar pedidos (esta última recibe simularError: Boolean).
- data/TareasRepositoryLocal.kt: implementación con delay(2000) al cargar el perfil, delay(3000) al cargar los pedidos (lanza IOException("Sin conexión") si simularError es true) y un conteo de primos dentro de withContext(Dispatchers.Default) que revisa ensureActive().
- viewmodel/TareasViewModel.kt: expone StateFlow para mensaje, progreso, corriendo, primos, carga y simularError, con funciones para iniciar y cancelar la tarea con progreso, contar y cancelar primos, y cargar una por una o a la vez con async.
- viewmodel/TareasViewModelFactory.kt y ui/PantallaTareas.kt (tres tarjetas: "Tarea con progreso", "Contar números primos" y "Dos cargas") más ui/theme.

Reglas obligatorias que debes respetar siempre:
1. Mantener MVVM + Repository: la pantalla solo dibuja estado y envía eventos al ViewModel; el ViewModel no recibe Context ni conoce Compose; el trabajo pesado va en el Repository.
2. Prohibido GlobalScope, runBlocking y Thread.sleep.
3. Si capturas una excepción en una corrutina, relanza siempre CancellationException antes de manejar otras excepciones.
4. No crees archivos nuevos, no agregues librerías ni dependencias, y modifica solo los archivos que indique este prompt.
5. No cambies los textos visibles de la interfaz ni el comportamiento de las demás tarjetas, salvo que se indique.
6. Escribe el código en español para nombres de variables y textos, igual que el resto del proyecto, sin comentarios explicativos dentro del código.

Forma de trabajar: antes de escribir código, dime en 3 a 5 líneas tu plan y qué archivos vas a tocar. Luego aplica los cambios. Al final, lista cada archivo modificado con un resumen de qué cambió y confirma que el código compila. No hagas commit.

Tarea: mejorar el diseño visual de PantallaTareas.kt con Material 3 y Jetpack Compose, sin cambiar ninguna lógica. Solo puedes modificar ui/PantallaTareas.kt. No toques el ViewModel, el Repository, Estados.kt, el Factory ni el tema. No uses librerías nuevas ni la librería de íconos extendida.

Debe conservarse sin cambios: todos los textos visibles (Tócame: n, Iniciar, Cancelar, Contar, Hasta el número, Una por una, A la vez, Simular error, Reintentar, los mensajes de estado y de error, la tabla de comparación), el orden de las tres tarjetas, qué botones se habilitan o deshabilitan en cada estado y todos los llamados al ViewModel.

Diseño que quiero:
1. Fondo de pantalla en un gris azulado muy claro (#F2F6FA). Barra superior azul con el título "CentroTareas" en negrita, como ahora.
2. Encabezado de página arriba de las tarjetas: título "Panel de tareas" en negrita y un subtítulo en gris: "Cada tarea corre en segundo plano y la pantalla no se congela."
3. Cada tarjeta es una ElevatedCard blanca con esquinas de 16 dp. Su encabezado lleva un círculo azul con el número de la tarjeta (1, 2 y 3) en blanco y negrita, a la izquierda, y a su derecha el título de la tarjeta en negrita y una descripción corta en gris: tarjeta 1 "Corre en segundo plano y se puede cancelar", tarjeta 2 "Cálculo pesado con progreso y cancelación", tarjeta 3 "Una por una contra A la vez".
4. Tarjeta 1: el botón Tócame centrado como FilledTonalButton en azul claro (#D6E6F5) con texto azul oscuro (#0B2F4A); una fila con Iniciar (relleno) y Cancelar (outlined), ambos del mismo ancho (weight 1f); la barra de progreso de ancho completo, 10 dp de alto y bordes redondeados, con pista azul claro; debajo, en una fila con espacio entre extremos, el porcentaje en negrita y azul a la izquierda y el mensaje de estado dentro de una "píldora" (Surface con esquinas totalmente redondeadas, fondo azul claro y texto azul oscuro) a la derecha.
5. Tarjeta 2: campo de texto con la etiqueta "Hasta el número" a ancho completo; una fila con Contar y Cancelar del mismo ancho; mientras calcula, barra redondeada igual a la de la tarjeta 1 con el texto "Calculando... NN %" centrado debajo; el estado Cancelado como texto centrado; el resultado dentro de un recuadro verde.
6. Tarjeta 3: el Switch "Simular error" alineado a la izquierda; una fila con Una por una (outlined) y A la vez (relleno) del mismo ancho; mientras carga, un CircularProgressIndicator centrado con el texto "Cargando..." debajo; el éxito en un recuadro verde; el error en un recuadro rojo con el texto centrado y, debajo, el botón Reintentar (outlined) centrado; la tabla "Comparación de tiempos" dentro de una superficie de esquinas de 12 dp con fondo gris azulado claro (#F2F6FA) y relleno de 12 dp.
7. Recuadros de resultado: crea un único composable privado reutilizable (dentro del mismo archivo) para los recuadros de color, con esquinas de 12 dp y relleno de 12 dp. Verde: fondo #E3F1E0 y texto #1B5E20. Rojo: fondo #FBE3E3 y texto #B3261E.
8. Espaciado consistente: 16 dp entre tarjetas y de margen de pantalla, 12 dp dentro de las tarjetas.

Criterios de aceptación: la app compila; todas las funciones siguen trabajando igual que antes (iniciar y cancelar la tarea, contar primos con progreso y cancelar, las dos cargas, el error simulado con Reintentar, la tabla); no cambió ninguna lógica ni otro archivo; la pantalla se ve bien en un celular normal y en rotación.