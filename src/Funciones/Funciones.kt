package funciones

import modelo.Cancion
import java.io.File

fun cargarArchivoCSV(archivoPath: String): Pair<List<Cancion>, List<String>> {
    val canciones = mutableListOf<Cancion>()
    val generos = mutableSetOf<String>()
    val archivo = File(archivoPath)

    if (archivo.exists()) {
        // Leer todas las líneas del archivo
        val lineas = archivo.readLines()
        for (i in lineas.indices) {
            val line = lineas[i]
            // Ignorar la primera línea (cabecera)
            if (i == 0) continue

            // Eliminar comas dobles y espacios en blanco al final
            val lineaLimpia = line.replace(Regex(",+\\s*$"), "").replace(",,", ",")

            // Separar la línea en columnas
            val datos = lineaLimpia.split(",").map { it.trim() }

            // Extraer el nombre de la canción como el último campo no vacío
            val songName = if (datos.isNotEmpty()) datos.last() else ""

            // Verificar que hay suficientes columnas (20 columnas)
            if (datos.size >= 20 && songName.isNotEmpty()) {
                // Crear la canción
                val cancion = Cancion(
                    danceability = datos[0].toDoubleOrNull() ?: 0.0,
                    duracion = datos[16].toDoubleOrNull() ?: 0.0, // Duración en milisegundos
                    genero = datos[18], // Género de la canción
                    songName = songName, // Nombre de la canción
                    energy = datos[1].toDoubleOrNull() ?: 0.0, // Energía de la canción
                    acousticness = datos[6].toDoubleOrNull() ?: 0.0 // Acústica de la canción
                )
                canciones.add(cancion)
                generos.add(cancion.genero) // Agregar el género a la lista de géneros únicos
            } else {
                println("Línea con formato incorrecto o sin nombre de canción: $line")
            }
        }
    } else {
        println("El archivo no existe en la ruta proporcionada.")
    }

    // Mostrar estadísticas de carga
    println("Número de canciones cargadas: ${canciones.size}")
    println("Géneros cargados: ${generos.toList()}")

    return Pair(canciones, generos.toList()) // Devuelve las canciones y los géneros únicos
}

// Función para filtrar canciones según los criterios de búsqueda
fun filtrarCanciones(
    canciones: List<Cancion>,
    danceabilityMin: Double,
    danceabilityMax: Double,
    genero: String,
    duracionMin: Double,
    duracionMax: Double
): List<Cancion> {
    return canciones.filter { cancion ->
        // Filtrar por danceability
        val danceabilityFilter = cancion.danceability in danceabilityMin..danceabilityMax
        // Filtrar por duración
        val duracionFilter = cancion.duracion in duracionMin..duracionMax
        // Filtrar por género si no está vacío
        val generoFilter = genero.isEmpty() || cancion.genero.equals(genero, ignoreCase = true)

        // Retornar la canción solo si todos los filtros son true
        danceabilityFilter && duracionFilter && generoFilter
    }
}

// Función recursiva para agregar canciones a un playlist
fun agregarCancionesRecursivamente(
    canciones: List<Cancion>,
    playlist: MutableList<Cancion>,
    indice: Int = 0
): MutableList<Cancion> {
    if (indice >= canciones.size) {
        return playlist
    }

    val cancion = canciones[indice]
    if (cancion !in playlist) {
        playlist.add(cancion)
    }

    return agregarCancionesRecursivamente(canciones, playlist, indice + 1)
}

// Función para recomendar canciones cercanas
fun recomendarCancionesCercanas(
    canciones: List<Cancion>,
    danceabilityMin: Double,
    danceabilityMax: Double,
    duracionMin: Double,
    duracionMax: Double
): List<Cancion> {
    // Imprimir los valores de danceabilityMin, danceabilityMax, duracionMin y duracionMax
    println("Rangos de preferencia - Danceability: [$danceabilityMin, $danceabilityMax]")
    println("Rangos de preferencia - Duración: [$duracionMin, $duracionMax]")

    // Calcular el nuevo rango para danceability con un umbral del 10%
    val umbralMinDanceability = danceabilityMin * 0.10
    val umbralMaxDanceability = danceabilityMax * 0.10

    val nuevoRangoDanceabilityMin = (danceabilityMin - umbralMinDanceability).coerceAtLeast(0.0)  // No puede ser negativo
    val nuevoRangoDanceabilityMax = (danceabilityMax + umbralMaxDanceability).coerceAtMost(1.0)  // No puede exceder 1.0

    // Calcular el nuevo rango para duración con un umbral del 10%
    val umbralMinDuracion = duracionMin * 0.10
    val umbralMaxDuracion = duracionMax * 0.10

    val nuevoRangoDuracionMin = (duracionMin - umbralMinDuracion).coerceAtLeast(0.0)
    val nuevoRangoDuracionMax = duracionMax + umbralMaxDuracion

    // Imprimir el nuevo rango ajustado
    println("Rangos de preferencia con nueva brecha:")
    println("Danceability: [$nuevoRangoDanceabilityMin, $nuevoRangoDanceabilityMax]")
    println("Duración: [$nuevoRangoDuracionMin, $nuevoRangoDuracionMax]")

    // Filtrar las canciones que cumplan ambos criterios simultáneamente
    val cancionesRecomendadas = canciones.filter { cancion ->
        cancion.danceability in nuevoRangoDanceabilityMin..nuevoRangoDanceabilityMax &&
                cancion.duracion in nuevoRangoDuracionMin..nuevoRangoDuracionMax
    }

    // Imprimir el número de canciones recomendadas
    println("Número de canciones recomendadas: ${cancionesRecomendadas.size}")

    return cancionesRecomendadas
}
