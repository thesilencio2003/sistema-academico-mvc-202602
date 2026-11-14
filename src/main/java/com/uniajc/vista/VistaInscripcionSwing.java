package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.InscripcionCursoService;

public class VistaInscripcionSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID Inscripción", "ID Estudiante", "ID Grupo", "Nota Final", "Estado"};
    private static final String[] ESTADOS = {"ACTIVO", "APROBADO", "REPROBADO", "CANCELADO"};

    private final InscripcionCursoService inscripcionService;
    private final EstudianteService estudianteService;
    private final GrupoService grupoService;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField textoBusqueda;
    private JButton botonBuscar;
    private JButton botonLimpiarBusqueda;
    private JButton botonRegistrar;
    private JButton botonEditar;
    private JButton botonEliminar;
    private JButton botonActualizar;

    private JDialog dialogoFormulario;
    private JComboBox<EstudianteItem> comboEstudiantes;
    private JComboBox<GrupoItem> comboGrupos;
    private JTextField campoNotaFinal;
    private JComboBox<String> comboEstado;
    private JButton botonGuardar;
    private boolean modoEdicion;
    private InscripcionCurso inscripcionSeleccionada;

    private static class EstudianteItem {
        final int id;
        final String nombre;
        EstudianteItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return id + " - " + nombre; }
    }

    private static class GrupoItem {
        final int id;
        final String info;
        GrupoItem(int id, String info) { this.id = id; this.info = info; }
        @Override public String toString() { return "Grupo #" + id + " (" + info + ")"; }
    }

    public VistaInscripcionSwing() {
        this.inscripcionService = new InscripcionCursoService();
        this.estudianteService = new EstudianteService(new EstudianteDao());
        this.grupoService = new GrupoService();
        initComponents();
        cargarInscripciones();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(COLOR_SECUNDARIO);

        JLabel labelBusqueda = new JLabel("Buscar por ID / Estudiante:");
        labelBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelBusqueda.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(22);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.setPreferredSize(new Dimension(220, 30));
        textoBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarInscripciones();
                }
            }
        });

        botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarInscripciones());

        botonLimpiarBusqueda = crearBoton("Limpiar", new Color(149, 165, 166));
        botonLimpiarBusqueda.addActionListener(e -> {
            textoBusqueda.setText("");
            cargarInscripciones();
        });

        panel.add(labelBusqueda);
        panel.add(textoBusqueda);
        panel.add(botonBuscar);
        panel.add(botonLimpiarBusqueda);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_SECUNDARIO);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setGridColor(new Color(189, 195, 199));
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(COLOR_PRIMARIO);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2));
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(COLOR_SECUNDARIO);

        botonRegistrar = crearBoton("Registrar", COLOR_EXITO);
        botonRegistrar.addActionListener(e -> mostrarDialogoRegistro(false));

        botonEditar = crearBoton("Editar", COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> {
            if (tabla.getSelectedRow() >= 0) {
                mostrarDialogoRegistro(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una inscripción de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarInscripcion());

        botonActualizar = crearBoton("Actualizar", new Color(149, 165, 166));
        botonActualizar.addActionListener(e -> cargarInscripciones());

        JButton botonCalificaciones = crearBoton("Gestionar Calificaciones", new Color(142, 68, 173));
        botonCalificaciones.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            int idIns = -1;
            if (fila >= 0) {
                idIns = (int) modeloTabla.getValueAt(fila, 0);
            }
            JDialog dialogCal = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Gestión de Calificaciones", true);
            dialogCal.setSize(800, 500);
            dialogCal.setLocationRelativeTo(this);
            VistaCalificacionSwing panelCal = new VistaCalificacionSwing();
            if (idIns > 0) {
                panelCal.seleccionarInscripcion(idIns);
            }
            dialogCal.add(panelCal);
            dialogCal.setVisible(true);
            cargarInscripciones();
        });

        panel.add(botonRegistrar);
        panel.add(botonEditar);
        panel.add(botonEliminar);
        panel.add(botonCalificaciones);
        panel.add(botonActualizar);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setPreferredSize(new Dimension(110, 35));
        return boton;
    }

    public void cargarInscripciones() {
        modeloTabla.setRowCount(0);
        try {
            List<InscripcionCurso> lista = inscripcionService.obtenerTodasLasInscripciones();
            for (InscripcionCurso i : lista) {
                modeloTabla.addRow(new Object[]{
                        i.getId(),
                        i.getIdEstudiante(),
                        i.getIdGrupo(),
                        i.getFinalGrade() != null ? i.getFinalGrade() : "Sin Calificar",
                        i.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar inscripciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarInscripciones() {
        String criterio = textoBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            cargarInscripciones();
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<InscripcionCurso> resultados = new ArrayList<>();
            if (criterio.matches("^\\d+$")) {
                int id = Integer.parseInt(criterio);
                Optional<InscripcionCurso> porId = inscripcionService.buscarInscripcionPorId(id);
                porId.ifPresent(resultados::add);

                List<InscripcionCurso> porEstudiante = inscripcionService.buscarInscripcionesPorEstudiante(id);
                for (InscripcionCurso i : porEstudiante) {
                    if (resultados.stream().noneMatch(existente -> existente.getId() == i.getId())) {
                        resultados.add(i);
                    }
                }
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron inscripciones para el criterio: \"" + criterio + "\"", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (InscripcionCurso i : resultados) {
                    modeloTabla.addRow(new Object[]{
                            i.getId(),
                            i.getIdEstudiante(),
                            i.getIdGrupo(),
                            i.getFinalGrade() != null ? i.getFinalGrade() : "Sin Calificar",
                            i.getStatus()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoRegistro(boolean esEdicion) {
        modoEdicion = esEdicion;

        if (esEdicion && tabla.getSelectedRow() >= 0) {
            int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
            try {
                inscripcionSeleccionada = inscripcionService.buscarInscripcionPorId(id).orElse(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            inscripcionSeleccionada = new InscripcionCurso();
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogoFormulario = new JDialog(parentFrame, esEdicion ? "Editar Inscripción" : "Registrar Inscripción", true);
        dialogoFormulario.setSize(440, 320);
        dialogoFormulario.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel labelTitulo = new JLabel(esEdicion ? "Editar Inscripción" : "Registrar Inscripción");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        panelFormulario.add(labelTitulo, gbc);

        gbc.gridwidth = 1;

        // Estudiante
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Estudiante:"), gbc);
        comboEstudiantes = new JComboBox<>();
        List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        for (Estudiante e : estudiantes) {
            comboEstudiantes.addItem(new EstudianteItem(e.getId(), e.getName() + " " + e.getLastname()));
        }
        gbc.gridx = 1;
        panelFormulario.add(comboEstudiantes, gbc);

        // Grupo
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Grupo:"), gbc);
        comboGrupos = new JComboBox<>();
        List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
        for (Grupo g : grupos) {
            comboGrupos.addItem(new GrupoItem(g.getId(), g.getClassroom() + " - " + g.getSchedule()));
        }
        gbc.gridx = 1;
        panelFormulario.add(comboGrupos, gbc);

        // Nota Final
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Nota Final (0.0 - 5.0):"), gbc);
        campoNotaFinal = new JTextField(18);
        gbc.gridx = 1;
        panelFormulario.add(campoNotaFinal, gbc);

        // Estado
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Estado:"), gbc);
        comboEstado = new JComboBox<>(ESTADOS);
        gbc.gridx = 1;
        panelFormulario.add(comboEstado, gbc);

        // Botones formulario
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonesForm.setBackground(Color.WHITE);

        botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarInscripcion());

        JButton botonCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        botonCancelar.addActionListener(e -> dialogoFormulario.dispose());

        panelBotonesForm.add(botonGuardar);
        panelBotonesForm.add(botonCancelar);
        panelFormulario.add(panelBotonesForm, gbc);

        if (esEdicion && inscripcionSeleccionada != null) {
            for (int i = 0; i < comboEstudiantes.getItemCount(); i++) {
                if (comboEstudiantes.getItemAt(i).id == inscripcionSeleccionada.getIdEstudiante()) {
                    comboEstudiantes.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < comboGrupos.getItemCount(); i++) {
                if (comboGrupos.getItemAt(i).id == inscripcionSeleccionada.getIdGrupo()) {
                    comboGrupos.setSelectedIndex(i);
                    break;
                }
            }
            campoNotaFinal.setText(inscripcionSeleccionada.getFinalGrade() != null ? inscripcionSeleccionada.getFinalGrade().toString() : "");
            comboEstado.setSelectedItem(inscripcionSeleccionada.getStatus());
        }

        dialogoFormulario.add(panelFormulario);
        dialogoFormulario.setVisible(true);
    }

    private void guardarInscripcion() {
        try {
            EstudianteItem estSel = (EstudianteItem) comboEstudiantes.getSelectedItem();
            GrupoItem grupoSel = (GrupoItem) comboGrupos.getSelectedItem();
            String strNota = campoNotaFinal.getText().trim();
            String estado = (String) comboEstado.getSelectedItem();

            if (estSel == null || grupoSel == null) {
                JOptionPane.showMessageDialog(dialogoFormulario, "Estudiante y Grupo son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal nota = null;
            if (!strNota.isEmpty()) {
                try {
                    nota = new BigDecimal(strNota);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogoFormulario, "La nota final debe ser un número decimal (ej. 4.5).", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            inscripcionSeleccionada.setIdEstudiante(estSel.id);
            inscripcionSeleccionada.setIdGrupo(grupoSel.id);
            inscripcionSeleccionada.setFinalGrade(nota);
            inscripcionSeleccionada.setStatus(estado != null ? estado : "ACTIVO");

            if (modoEdicion) {
                inscripcionService.actualizarInscripcion(inscripcionSeleccionada);
                JOptionPane.showMessageDialog(dialogoFormulario, "Inscripción actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                inscripcionService.registrarInscripcion(inscripcionSeleccionada);
                JOptionPane.showMessageDialog(dialogoFormulario, "Inscripción registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dialogoFormulario.dispose();
            cargarInscripciones();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarInscripcion() {
        if (tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la inscripción seleccionada?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                inscripcionService.eliminarInscripcion(id);
                JOptionPane.showMessageDialog(this, "Inscripción eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarInscripciones();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}

