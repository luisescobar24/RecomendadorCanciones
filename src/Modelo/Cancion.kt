package modelo

data class Cancion(
    val songName: String,      // Nombre de la canción
    val genero: String,        // Género de la canción
    val danceability: Double,  // Danceability de la canción
    val duracion: Double,      // Duración de la canción en milisegundos
    val energy: Double,        // Energía de la canción
    val acousticness: Double    // Acústica de la canción
)