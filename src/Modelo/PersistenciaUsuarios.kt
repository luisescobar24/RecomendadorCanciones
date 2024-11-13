package modelo

import java.util.prefs.Preferences

private val prefs = Preferences.userRoot().node("usuariosApp")

fun cargarUsuarios(): MutableMap<String, Usuario> {
    val usuarios = mutableMapOf<String, Usuario>()
    val usuariosRegistrados = prefs.keys()
    for (idUsuario in usuariosRegistrados) {
        usuarios[idUsuario] = Usuario(idUsuario)
    }
    return usuarios
}

// Guardar un usuario en Preferences
fun guardarUsuario(usuario: Usuario) {
    prefs.put(usuario.id, "registered")
}
