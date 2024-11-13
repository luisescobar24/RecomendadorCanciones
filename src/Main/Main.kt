package Main

import ui.crearInterfazInicioSesion
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        crearInterfazInicioSesion() // Inicia la interfaz de inicio de sesión
    }
}
