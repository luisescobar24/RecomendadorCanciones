package modelo

data class Usuario(
    val id: String,
    private val playlist: MutableSet<Cancion> = mutableSetOf()
) {
    fun agregarCancion(cancion: Cancion): Boolean {
        return if (playlist.contains(cancion)) {
            false
        } else {
            playlist.add(cancion)
            true
        }
    }

    fun obtenerPlaylist(): List<Cancion> {
        return playlist.toList()
    }
}
