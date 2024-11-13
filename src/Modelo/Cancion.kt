package modelo

data class Cancion(
    val songName: String,
    val genero: String,
    val danceability: Double,
    val duracion: Double,
    val energy: Double,
    val acousticness: Double
)