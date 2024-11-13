package funciones

import modelo.Cancion
import java.io.File

fun cargarArchivoCSV(archivoPath: String): Pair<List<Cancion>, List<String>> {
    val canciones = mutableListOf<Cancion>()
    val generos = mutableSetOf<String>()
    val archivo = File(archivoPath)

    if (archivo.exists()) {

        val lineas = archivo.readLines()
        for (i in lineas.indices) {
            val line = lineas[i]

            if (i == 0) continue


            val lineaLimpia = line.replace(Regex(",+\\s*$"), "").replace(",,", ",")


            val datos = lineaLimpia.split(",").map { it.trim() }


            val songName = if (datos.isNotEmpty()) datos.last() else ""


            if (datos.size >= 20 && songName.isNotEmpty()) {

                val cancion = Cancion(
                    danceability = datos[0].toDoubleOrNull() ?: 0.0,
                    duracion = datos[16].toDoubleOrNull() ?: 0.0,
                    genero = datos[18],
                    songName = songName,
                    energy = datos[1].toDoubleOrNull() ?: 0.0,
                    acousticness = datos[6].toDoubleOrNull() ?: 0.0
                )
                canciones.add(cancion)
                generos.add(cancion.genero)
            } else {
                println("Línea con formato incorrecto o sin nombre de canción: $line")
            }
        }
    } else {
        println("El archivo no existe en la ruta proporcionada.")
    }

    println("Número de canciones cargadas: ${canciones.size}")
    println("Géneros cargados: ${generos.toList()}")

    return Pair(canciones, generos.toList())
}

fun filtrarCanciones(
    canciones: List<Cancion>,
    danceabilityMin: Double,
    danceabilityMax: Double,
    genero: String,
    duracionMin: Double,
    duracionMax: Double
): List<Cancion> {
    return canciones.filter { cancion ->

        val danceabilityFilter = cancion.danceability in danceabilityMin..danceabilityMax

        val duracionFilter = cancion.duracion in duracionMin..duracionMax

        val generoFilter = genero.isEmpty() || cancion.genero.equals(genero, ignoreCase = true)

        danceabilityFilter && duracionFilter && generoFilter
    }
}

fun recomendarCancionesCercanas(
    canciones: List<Cancion>,
    danceabilityMin: Double,
    danceabilityMax: Double,
    duracionMin: Double,
    duracionMax: Double
): List<Cancion> {

    println("Rangos de preferencia - Danceability: [$danceabilityMin, $danceabilityMax]")
    println("Rangos de preferencia - Duración: [$duracionMin, $duracionMax]")

    val umbralMinDanceability = danceabilityMin * 0.10
    val umbralMaxDanceability = danceabilityMax * 0.10

    val nuevoRangoDanceabilityMin = (danceabilityMin - umbralMinDanceability).coerceAtLeast(0.0)  // No puede ser negativo
    val nuevoRangoDanceabilityMax = (danceabilityMax + umbralMaxDanceability).coerceAtMost(1.0)  // No puede exceder 1.0

    val umbralMinDuracion = duracionMin * 0.10
    val umbralMaxDuracion = duracionMax * 0.10

    val nuevoRangoDuracionMin = (duracionMin - umbralMinDuracion).coerceAtLeast(0.0)
    val nuevoRangoDuracionMax = duracionMax + umbralMaxDuracion

    println("Rangos de preferencia con nueva brecha:")
    println("Danceability: [$nuevoRangoDanceabilityMin, $nuevoRangoDanceabilityMax]")
    println("Duración: [$nuevoRangoDuracionMin, $nuevoRangoDuracionMax]")

    val cancionesRecomendadas = canciones.filter { cancion ->
        cancion.danceability in nuevoRangoDanceabilityMin..nuevoRangoDanceabilityMax &&
                cancion.duracion in nuevoRangoDuracionMin..nuevoRangoDuracionMax
    }

    println("Número de canciones recomendadas: ${cancionesRecomendadas.size}")

    return cancionesRecomendadas
}
