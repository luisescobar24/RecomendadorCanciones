package ui

import funciones.cargarArchivoCSV
import funciones.filtrarCanciones
import funciones.recomendarCancionesCercanas
import modelo.Cancion
import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*

fun crearInterfazUsuario(idUsuario: String) {
    // Frame principal
    val frame = JFrame("Sistema de Recomendación de Canciones - Bienvenido, $idUsuario")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.size = Dimension(800, 600)
    frame.layout = BorderLayout()

    val panelBusqueda = JPanel()
    panelBusqueda.layout = GridBagLayout()
    panelBusqueda.border = BorderFactory.createTitledBorder("Criterios de Búsqueda")
    val gbc = GridBagConstraints().apply {
        fill = GridBagConstraints.HORIZONTAL
        insets = Insets(5, 5, 5, 5)
    }

    val labels = arrayOf(
        "Danceability Mínimo:",
        "Danceability Máximo:",
        "Duración Mínima (segundos):",
        "Duración Máxima (segundos):",
        "Género:"
    )
    val fields = arrayOf(JTextField("0.0"), JTextField("1.0"), JTextField("0.0"), JTextField("300.0"), JComboBox<String>())

    for (i in labels.indices) {
        gbc.gridx = 0
        gbc.gridy = i
        panelBusqueda.add(JLabel(labels[i]), gbc)

        gbc.gridx = 1
        panelBusqueda.add(fields[i], gbc)
    }

    val generoComboBox = fields[4] as JComboBox<String>

    val cargarButton = JButton("Cargar Archivo CSV")
    val canciones = mutableListOf<Cancion>()
    val tableModel = DefaultTableModel(arrayOf("Nombre", "Género", "Duración (segundos)", "Danceability", "Energy", "Acousticness"), 0)
    val resultadosTable = JTable(tableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        rowHeight = 25 // Altura de las filas
        selectionBackground = Color(220, 220, 220)
        selectionForeground = Color.BLACK
    }
    resultadosTable.tableHeader.background = Color(100, 100, 250)
    resultadosTable.tableHeader.foreground = Color.WHITE
    resultadosTable.tableHeader.font = Font("Arial", Font.BOLD, 14)
    val scrollPane = JScrollPane(resultadosTable)

    cargarButton.addActionListener {
        val archivo = JFileChooser()
        if (archivo.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            val archivoSeleccionado = archivo.selectedFile
            try {
                canciones.clear()
                val (cancionesCargadas, generosUnicos) = cargarArchivoCSV(archivoSeleccionado.absolutePath)
                canciones.addAll(cancionesCargadas)
                JOptionPane.showMessageDialog(frame, "Archivo cargado exitosamente.")


                generoComboBox.removeAllItems()
                generoComboBox.addItem("")
                generosUnicos.forEach {
                    generoComboBox.addItem(it)
                }


                actualizarTabla(canciones, tableModel)
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(frame, "Error al cargar el archivo: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

    gbc.gridx = 0
    gbc.gridy = labels.size
    gbc.gridwidth = 2
    panelBusqueda.add(cargarButton, gbc)

    val panelBotones = JPanel()

    val filtrarButton = JButton("Filtrar Canciones")
    filtrarButton.addActionListener {
        try {
            val danceabilityMin = (fields[0] as JTextField).text.toDoubleOrNull () ?: throw IllegalArgumentException("El valor de Danceability Mínimo no es válido.")
            val danceabilityMax = (fields[1] as JTextField).text.toDoubleOrNull() ?: throw IllegalArgumentException("El valor de Danceability Máximo no es válido.")
            val duracionMin = (fields[2] as JTextField).text.toDoubleOrNull()?.times(1000) ?: throw IllegalArgumentException("El valor de Duración Mínima no es válido.")
            val duracionMax = (fields[3] as JTextField).text.toDoubleOrNull()?.times(1000) ?: throw IllegalArgumentException("El valor de Duración Máxima no es válido.")
            val genero = generoComboBox.selectedItem as? String ?: ""

            if (danceabilityMin > danceabilityMax) throw IllegalArgumentException("Danceability Mínimo no puede ser mayor que Danceability Máximo.")
            if (duracionMin > duracionMax) throw IllegalArgumentException("Duración Mínima no puede ser mayor que Duración Máxima.")

            val cancionesFiltradas = filtrarCanciones(canciones, danceabilityMin, danceabilityMax, genero, duracionMin, duracionMax)
            tableModel.setRowCount(0)

            cancionesFiltradas.forEach { cancion ->
                val row = arrayOf(cancion.songName, cancion.genero, cancion.duracion / 1000, cancion.danceability, cancion.energy, cancion.acousticness)
                tableModel.addRow(row)
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(frame, "Error al filtrar canciones: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }
    panelBotones.add(filtrarButton)

    val recomendarButton = JButton("Recomendar Canciones Cercanas")
    recomendarButton.addActionListener {
        try {
            val danceabilityMin = (fields[0] as JTextField).text.toDoubleOrNull() ?: throw IllegalArgumentException("El valor de Danceability Mínimo no es válido.")
            val danceabilityMax = (fields[1] as JTextField).text.toDoubleOrNull() ?: throw IllegalArgumentException("El valor de Danceability Máximo no es válido.")
            val duracionMin = (fields[2] as JTextField).text.toDoubleOrNull()?.times(1000) ?: throw IllegalArgumentException("El valor de Duración Mínima no es válido.")
            val duracionMax = (fields[3] as JTextField).text.toDoubleOrNull()?.times(1000) ?: throw IllegalArgumentException("El valor de Duración Máxima no es válido.")

            if (danceabilityMin > danceabilityMax) throw IllegalArgumentException("Danceability Mínimo no puede ser mayor que Danceability Máximo.")
            if (duracionMin > duracionMax) throw IllegalArgumentException("Duración Mínima no puede ser mayor que Duración Máxima.")

            val cancionesRecomendadas = recomendarCancionesCercanas(canciones, danceabilityMin, danceabilityMax, duracionMin, duracionMax)
            tableModel.setRowCount(0)

            cancionesRecomendadas.forEach { cancion ->
                val row = arrayOf(cancion.songName, cancion.genero, cancion.duracion / 1000, cancion.danceability, cancion.energy, cancion.acousticness)
                tableModel.addRow(row)
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(frame, "Error al recomendar canciones: ${e.message}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }
    panelBotones.add(recomendarButton)

    frame.add(panelBusqueda, BorderLayout.NORTH)
    frame.add(scrollPane, BorderLayout.CENTER)
    frame.add(panelBotones, BorderLayout.SOUTH)

    frame.isVisible = true
}

fun actualizarTabla(canciones: List<Cancion>, tableModel: DefaultTableModel) {
    tableModel.setRowCount(0) 
    canciones.forEach { cancion ->
        val row = arrayOf(
            cancion.songName,
            cancion.genero,
            cancion.duracion / 1000,
            cancion.danceability,
            cancion.energy,
            cancion.acousticness
        )
        tableModel.addRow(row)
    }}