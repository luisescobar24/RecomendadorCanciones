package ui

import modelo.Usuario
import modelo.cargarUsuarios
import modelo.guardarUsuario
import javax.swing.*
import java.awt.*

private val usuariosRegistrados = cargarUsuarios()

fun crearInterfazInicioSesion() {
    val frame = JFrame("Inicio de Sesión")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.size = Dimension(400, 300)
    frame.layout = GridLayout(4, 1)

    val etiquetaUsuario = JLabel("ID de Usuario:")
    val campoUsuario = JTextField()
    val botonIniciarSesion = JButton("Iniciar Sesión")
    val botonRegistrarse = JButton("Registrarse")

    val panelEntrada = JPanel().apply {
        layout = GridLayout(2, 1)
        add(etiquetaUsuario)
        add(campoUsuario)
    }

    val panelBotones = JPanel().apply {
        layout = FlowLayout()
        add(botonIniciarSesion)
        add(botonRegistrarse)
    }

    frame.add(panelEntrada)
    frame.add(panelBotones)
    frame.isVisible = true

    botonIniciarSesion.addActionListener {
        val idUsuario = campoUsuario.text.trim()
        if (idUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor, ingrese un ID de usuario.", "Error", JOptionPane.ERROR_MESSAGE)
        } else if (usuariosRegistrados.containsKey(idUsuario)) {
            JOptionPane.showMessageDialog(frame, "Inicio de sesión exitoso. Bienvenido, $idUsuario.", "Éxito", JOptionPane.INFORMATION_MESSAGE)
            frame.dispose() // Cierra la ventana de inicio de sesión
            ui.crearInterfazUsuario(idUsuario) // Llama a la función de interfaz de usuario desde el otro archivo
        } else {
            JOptionPane.showMessageDialog(frame, "Usuario no encontrado. Regístrese primero.", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    botonRegistrarse.addActionListener {
        val idUsuario = campoUsuario.text.trim()
        if (idUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor, ingrese un ID de usuario.", "Error", JOptionPane.ERROR_MESSAGE)
        } else if (usuariosRegistrados.containsKey(idUsuario)) {
            JOptionPane.showMessageDialog(frame, "El ID de usuario ya está registrado. Intente iniciar sesión.", "Error", JOptionPane.ERROR_MESSAGE)
        } else {
            val nuevoUsuario = Usuario(idUsuario)
            usuariosRegistrados[idUsuario] = nuevoUsuario
            guardarUsuario(nuevoUsuario) // Guarda el usuario registrado
            JOptionPane.showMessageDialog(frame, "Registro exitoso. Bienvenido, $idUsuario.", "Éxito", JOptionPane.INFORMATION_MESSAGE)
            frame.dispose()
            ui.crearInterfazUsuario(idUsuario) // Llama a la función de interfaz de usuario desde el otro archivo
        }
    }
}