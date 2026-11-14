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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.uniajc.controlador.ControladorCalificacion;
import com.uniajc.modelo.Calificacion;
import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.servicios.InscripcionCursoService;

public class VistaCalificacionSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID", "Inscripción", "Corte / Evaluación", "Porcentaje (%)", "Nota", "Observación"};
    private static final String[] CORTES_PREDEFINIDOS = {"Corte 1 (30%)", "Corte 2 (35%)", "Corte 3 (35%)", "Examen Final", "Laboratorio", "Taller", "Otro"};

    private final ControladorCalificacion controladorCalificacion;
    private final InscripcionCursoService inscripcionService;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFiltroInscripcion;
    private JLabel lblPorcentajeTotal;
    private JLabel lblNotaAcumulada;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnRefrescar;

    public VistaCalificacionSwing() {
        this.controladorCalificacion = new ControladorCalificacion();
        this.inscripcionService = new InscripcionCursoService();
        inicializarUI();
        cargarComboInscripciones();
        cargarCalificaciones();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Panel Superior (Título y Filtro de Inscripción)
        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBackground(COLOR_SECUNDARIO);

        JLabel titulo = new JLabel("Gestión de Calificaciones por Corte");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(COLOR_PRIMARIO);

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setBackground(COLOR_SECUNDARIO);

        JLabel lblInscripcion = new JLabel("Seleccionar Inscripción:");
        lblInscripcion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        comboFiltroInscripcion = new JComboBox<>();
        comboFiltroInscripcion.setPreferredSize(new Dimension(300, 28));
        comboFiltroInscripcion.addActionListener(e -> cargarCalificaciones());

        btnRefrescar = crearBoton("Refrescar", COLOR_PRIMARIO);
        btnRefrescar.addActionListener(this);

        panelFiltro.add(lblInscripcion);
        panelFiltro.add(comboFiltroInscripcion);
        panelFiltro.add(btnRefrescar);

        panelNorte.add(titulo, BorderLayout.NORTH);
        panelNorte.add(panelFiltro, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // 2. Panel Central (Tabla)
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
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollTabla = new JScrollPane(tabla);
        add(scrollTabla, BorderLayout.CENTER);

        // 3. Panel Inferior (Resumen de Notas y Botones de Acción)
        JPanel panelSur = new JPanel(new BorderLayout(10, 10));
        panelSur.setBackground(COLOR_SECUNDARIO);

        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelResumen.setBackground(new Color(220, 230, 242));
        panelResumen.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 220), 1));

        lblPorcentajeTotal = new JLabel("Porcentaje Acumulado: 0.0%");
        lblPorcentajeTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPorcentajeTotal.setForeground(new Color(44, 62, 80));

        lblNotaAcumulada = new JLabel("Nota Final Ponderada: 0.00 / 5.0");
        lblNotaAcumulada.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNotaAcumulada.setForeground(COLOR_PRIMARIO);

        panelResumen.add(lblPorcentajeTotal);
        panelResumen.add(new JLabel("|"));
        panelResumen.add(lblNotaAcumulada);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(COLOR_SECUNDARIO);

        btnNuevo = crearBoton("Nueva Calificación", COLOR_EXITO);
        btnEditar = crearBoton("Modificar", COLOR_PRIMARIO);
        btnEliminar = crearBoton("Eliminar", COLOR_ACENTO);

        btnNuevo.addActionListener(this);
        btnEditar.addActionListener(this);
        btnEliminar.addActionListener(this);

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelSur.add(panelResumen, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        add(panelSur, BorderLayout.SOUTH);
    }

    public void cargarComboInscripciones() {
        comboFiltroInscripcion.removeAllItems();
        comboFiltroInscripcion.addItem("Todas las inscripciones");

        try {
            List<InscripcionCurso> lista = inscripcionService.obtenerTodasLasInscripciones();
            for (InscripcionCurso ins : lista) {
                comboFiltroInscripcion.addItem(String.format("Inscripción #%d (Estudiante: %d | Grupo: %d)",
                        ins.getId(), ins.getIdEstudiante(), ins.getIdGrupo()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar inscripciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void seleccionarInscripcion(int idInscripcion) {
        for (int i = 0; i < comboFiltroInscripcion.getItemCount(); i++) {
            String item = comboFiltroInscripcion.getItemAt(i);
            if (item.startsWith("Inscripción #" + idInscripcion + " ")) {
                comboFiltroInscripcion.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargarCalificaciones() {
        modeloTabla.setRowCount(0);
        int inscripcionId = obtenerInscripcionSeleccionada();

        try {
            List<Calificacion> lista;
            if (inscripcionId > 0) {
                lista = controladorCalificacion.listarPorInscripcion(inscripcionId);
            } else {
                lista = controladorCalificacion.listarTodas();
            }

            BigDecimal sumaPorcentaje = BigDecimal.ZERO;
            BigDecimal sumaPonderada = BigDecimal.ZERO;

            for (Calificacion c : lista) {
                modeloTabla.addRow(new Object[]{
                        c.getId(),
                        c.getInscripcionId(),
                        c.getCorte(),
                        c.getPorcentaje() != null ? c.getPorcentaje() + "%" : "0%",
                        c.getNota(),
                        c.getObservacion() != null ? c.getObservacion() : ""
                });

                if (c.getPorcentaje() != null && c.getNota() != null) {
                    sumaPorcentaje = sumaPorcentaje.add(c.getPorcentaje());
                    BigDecimal aporte = c.getNota().multiply(c.getPorcentaje()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    sumaPonderada = sumaPonderada.add(aporte);
                }
            }

            if (inscripcionId > 0) {
                lblPorcentajeTotal.setText(String.format("Porcentaje Acumulado: %.1f%% / 100%%", sumaPorcentaje));
                BigDecimal notaFinal = sumaPonderada.setScale(2, RoundingMode.HALF_UP);
                String estado = (sumaPorcentaje.compareTo(new BigDecimal("100.00")) >= 0)
                        ? (notaFinal.compareTo(new BigDecimal("3.00")) >= 0 ? " [APROBADO]" : " [REPROBADO]")
                        : " [EN CURSO]";
                lblNotaAcumulada.setText(String.format("Nota Final Ponderada: %.2f / 5.0 %s", notaFinal, estado));
            } else {
                lblPorcentajeTotal.setText("Total registros: " + lista.size());
                lblNotaAcumulada.setText("Seleccione una inscripción para ver el consolidado");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar calificaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerInscripcionSeleccionada() {
        if (comboFiltroInscripcion == null || comboFiltroInscripcion.getSelectedIndex() <= 0) {
            return -1;
        }
        String texto = (String) comboFiltroInscripcion.getSelectedItem();
        try {
            int inicio = texto.indexOf('#') + 1;
            int fin = texto.indexOf(' ', inicio);
            return Integer.parseInt(texto.substring(inicio, fin));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefrescar) {
            cargarComboInscripciones();
            cargarCalificaciones();
        } else if (e.getSource() == btnNuevo) {
            mostrarDialogoFormulario(null);
        } else if (e.getSource() == btnEditar) {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una calificación para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloTabla.getValueAt(fila, 0);
            Optional<Calificacion> opt = controladorCalificacion.buscarPorId(id);
            if (opt.isPresent()) {
                mostrarDialogoFormulario(opt.get());
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la calificación seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnEliminar) {
            eliminarCalificacionSeleccionada();
        }
    }

    private void eliminarCalificacionSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una calificación para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String corte = (String) modeloTabla.getValueAt(fila, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la calificación del " + corte + " (ID: " + id + ")?\nLa nota final se recalculará automáticamente.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controladorCalificacion.eliminarCalificacion(id);
                JOptionPane.showMessageDialog(this, "Calificación eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCalificaciones();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoFormulario(Calificacion existente) {
        boolean esNuevo = (existente == null);
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                esNuevo ? "Registrar Nueva Calificación" : "Modificar Calificación", true);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Campos
        JComboBox<String> comboInscripcionModal = new JComboBox<>();
        try {
            List<InscripcionCurso> inscripciones = inscripcionService.obtenerTodasLasInscripciones();
            for (InscripcionCurso ins : inscripciones) {
                comboInscripcionModal.addItem("Inscripción #" + ins.getId() + " (Estudiante: " + ins.getIdEstudiante() + " | Grupo: " + ins.getIdGrupo() + ")");
            }
        } catch (Exception ignored) {}

        // Preseleccionar la inscripción activa si hay filtro
        int inscripcionActiva = obtenerInscripcionSeleccionada();
        if (inscripcionActiva > 0) {
            for (int i = 0; i < comboInscripcionModal.getItemCount(); i++) {
                if (comboInscripcionModal.getItemAt(i).startsWith("Inscripción #" + inscripcionActiva + " ")) {
                    comboInscripcionModal.setSelectedIndex(i);
                    break;
                }
            }
        }

        JComboBox<String> comboCorte = new JComboBox<>(CORTES_PREDEFINIDOS);
        comboCorte.setEditable(true);

        JTextField txtPorcentaje = new JTextField();
        JTextField txtNota = new JTextField();
        JTextField txtObservacion = new JTextField();

        if (!esNuevo) {
            for (int i = 0; i < comboInscripcionModal.getItemCount(); i++) {
                if (comboInscripcionModal.getItemAt(i).startsWith("Inscripción #" + existente.getInscripcionId() + " ")) {
                    comboInscripcionModal.setSelectedIndex(i);
                    break;
                }
            }
            comboInscripcionModal.setEnabled(false);
            comboCorte.setSelectedItem(existente.getCorte());
            txtPorcentaje.setText(existente.getPorcentaje() != null ? existente.getPorcentaje().toString() : "");
            txtNota.setText(existente.getNota() != null ? existente.getNota().toString() : "");
            txtObservacion.setText(existente.getObservacion() != null ? existente.getObservacion() : "");
        }

        // Fila 0: Inscripción
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelForm.add(new JLabel("Inscripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelForm.add(comboInscripcionModal, gbc);

        // Fila 1: Corte
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelForm.add(new JLabel("Corte / Evaluación:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelForm.add(comboCorte, gbc);

        // Fila 2: Porcentaje
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panelForm.add(new JLabel("Porcentaje (%):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelForm.add(txtPorcentaje, gbc);

        // Fila 3: Nota
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panelForm.add(new JLabel("Nota (0.0 - 5.0):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelForm.add(txtNota, gbc);

        // Fila 4: Observación
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panelForm.add(new JLabel("Observación:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelForm.add(txtObservacion, gbc);

        // Botones del modal
        JPanel panelBotonesModal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGuardar = crearBoton("Guardar", COLOR_EXITO);
        JButton btnCancelar = crearBoton("Cancelar", Color.GRAY);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                String itemIns = (String) comboInscripcionModal.getSelectedItem();
                if (itemIns == null) throw new IllegalArgumentException("Seleccione una inscripción.");
                int idIns = Integer.parseInt(itemIns.substring(itemIns.indexOf('#') + 1, itemIns.indexOf(' ')));

                String corte = comboCorte.getSelectedItem() != null ? comboCorte.getSelectedItem().toString().trim() : "";
                if (corte.isEmpty()) throw new IllegalArgumentException("El corte o evaluación no puede estar vacío.");

                String strPorcentaje = txtPorcentaje.getText().trim().replace(',', '.');
                String strNota = txtNota.getText().trim().replace(',', '.');

                BigDecimal porcentaje = new BigDecimal(strPorcentaje);
                BigDecimal nota = new BigDecimal(strNota);
                String observacion = txtObservacion.getText().trim();

                if (esNuevo) {
                    controladorCalificacion.registrarCalificacion(idIns, corte, porcentaje, nota, observacion);
                    JOptionPane.showMessageDialog(dialog, "Calificación registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controladorCalificacion.actualizarCalificacion(existente.getId(), existente.getInscripcionId(), corte, porcentaje, nota, observacion);
                    JOptionPane.showMessageDialog(dialog, "Calificación actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                cargarCalificaciones();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese valores numéricos válidos en porcentaje y nota (ej. 30 y 4.5).", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotonesModal.add(btnGuardar);
        panelBotonesModal.add(btnCancelar);

        dialog.add(panelForm, BorderLayout.CENTER);
        dialog.add(panelBotonesModal, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}

