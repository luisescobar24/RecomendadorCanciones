package modelo

import java.util.prefs.Preferences

// Acceso a la instancia de Preferences para almacenar los usuarios
private val prefs = Preferences.userRoot().node("usuariosApp")

// Cargar usuarios desde Preferences
fun cargarUsuarios(): MutableMap<String, Usuario> {
    val usuarios = mutableMapOf<String, Usuario>()
    val usuariosRegistrados = prefs.keys()
    for (idUsuario in usuariosRegistrados) {
        usuarios[idUsuario] = Usuario(idUsuario) // Crea un usuario con ID
    }
    return usuarios
}

// Guardar un usuario en Preferences
fun guardarUsuario(usuario: Usuario) {
    prefs.put(usuario.id, "registered") // Guarda el ID del usuario como "registered"
}
