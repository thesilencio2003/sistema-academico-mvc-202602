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
import java.util.ArrayList;
import java.util.List;

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

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

public class VistaDocenteSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO   = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO     = new Color(231, 76, 60);
    private static final Color COLOR_EXITO      = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID", "Nombre", "Especialidad", "Documento", "Email", "Teléfono", "Título", "Contrato"};

    private final DocenteService docenteService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField textoBusqueda;
    private JButton botonBuscar, botonLimpiarBusqueda;
    private JButton botonRegistrar, botonEditar, botonEliminar, botonActualizar;

    private JDialog dialogoFormulario;
    private JTextField campoNombre;
    private JTextField campoEspecialidad;
    private JTextField campoDocumento;
    private JTextField campoEmail;
    private JTextField campoTelefono;
    private JComboBox<String> comboTituloAcademico;
    private JComboBox<String> comboTipoContrato;
    private JButton botonGuardar;
    private boolean modoEdicion;
    private Docente docenteSeleccionado;

    public VistaDocenteSwing() {
        this(new DocenteService(new DocenteDao()));
    }

    public VistaDocenteSwing(DocenteService docenteService) {
        this.docenteService = docenteService;
        initComponents();
        cargarDocentes();
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

        JLabel label = new JLabel("Buscar (ID / Nombre / Especialidad / Doc.):");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(20);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.setPreferredSize(new Dimension(200, 30));
        textoBusqueda.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) buscarDocentes();
            }
        });

        botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarDocentes());

        botonLimpiarBusqueda = crearBoton("Limpiar", new Color(149, 165, 166));
        botonLimpiarBusqueda.addActionListener(e -> { textoBusqueda.setText(""); cargarDocentes(); });

        panel.add(label);
        panel.add(textoBusqueda);
        panel.add(botonBuscar);
        panel.add(botonLimpiarBusqueda);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_SECUNDARIO);

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
            if (tabla.getSelectedRow() >= 0) mostrarDialogoRegistro(true);
            else JOptionPane.showMessageDialog(this, "Seleccione un docente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        });

        botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarDocente());

        botonActualizar = crearBoton("Actualizar", new Color(149, 165, 166));
        botonActualizar.addActionListener(e -> cargarDocentes());

        panel.add(botonRegistrar);
        panel.add(botonEditar);
        panel.add(botonEliminar);
        panel.add(botonActualizar);
        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(110, 35));
        return b;
    }

    public void cargarDocentes() {
        modeloTabla.setRowCount(0);
        try {
            List<Docente> docentes = docenteService.obtenerTodosLosDocentes();
            for (Docente d : docentes) {
                modeloTabla.addRow(new Object[]{
                    d.getIdDocente(), 
                    d.getNombre(), 
                    d.getEspecialidad(), 
                    d.getDocumento(), 
                    d.getEmail(), 
                    d.getTelefono(), 
                    d.getTituloAcademico(), 
                    d.getTipoContrato()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar docentes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarDocentes() {
        String criterio = textoBusqueda.getText().trim();
        if (criterio.isEmpty()) { cargarDocentes(); return; }

        modeloTabla.setRowCount(0);
        try {
            List<Docente> resultados = new ArrayList<>();
            if (criterio.matches("^\\d+$")) {
                docenteService.buscarDocentePorId(Integer.parseInt(criterio)).ifPresent(resultados::add);
            }
            for (Docente d : docenteService.buscarDocentesPorNombre(criterio)) {
                if (resultados.stream().noneMatch(e -> e.getIdDocente() == d.getIdDocente())) resultados.add(d);
            }
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron docentes para: \"" + criterio + "\"", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                cargarDocentes();
            } else {
                for (Docente d : resultados) {
                    modeloTabla.addRow(new Object[]{
                        d.getIdDocente(), 
                        d.getNombre(), 
                        d.getEspecialidad(), 
                        d.getDocumento(), 
                        d.getEmail(), 
                        d.getTelefono(), 
                        d.getTituloAcademico(), 
                        d.getTipoContrato()
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
                docenteSeleccionado = docenteService.buscarDocentePorId(id).orElse(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            docenteSeleccionado = new Docente();
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogoFormulario = new JDialog(parent, esEdicion ? "Editar Docente" : "Registrar Docente", true);
        dialogoFormulario.setSize(420, 480);
        dialogoFormulario.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titulo = new JLabel(esEdicion ? "Editar Docente" : "Registrar Docente");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_PRIMARIO);
        form.add(titulo, gbc);

        gbc.gridwidth = 1;

        // Campos del formulario
        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Nombre:"), gbc);
        campoNombre = new JTextField(18);
        gbc.gridx = 1; form.add(campoNombre, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Especialidad:"), gbc);
        campoEspecialidad = new JTextField(18);
        gbc.gridx = 1; form.add(campoEspecialidad, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Documento:"), gbc);
        campoDocumento = new JTextField(18);
        gbc.gridx = 1; form.add(campoDocumento, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Email:"), gbc);
        campoEmail = new JTextField(18);
        gbc.gridx = 1; form.add(campoEmail, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Teléfono:"), gbc);
        campoTelefono = new JTextField(18);
        gbc.gridx = 1; form.add(campoTelefono, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Título Académico:"), gbc);
        comboTituloAcademico = new JComboBox<>(new String[]{"Pregrado", "Especialización", "Maestría", "Doctorado"});
        gbc.gridx = 1; form.add(comboTituloAcademico, gbc);

        gbc.gridy++; gbc.gridx = 0; form.add(new JLabel("Tipo Contrato:"), gbc);
        comboTipoContrato = new JComboBox<>(new String[]{"Tiempo Completo", "Medio Tiempo", "Cátedra", "Ocasional"});
        gbc.gridx = 1; form.add(comboTipoContrato, gbc);

        // Botones de acción
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botones.setBackground(Color.WHITE);

        botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarDocente());
        JButton botonCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        botonCancelar.addActionListener(e -> dialogoFormulario.dispose());

        botones.add(botonGuardar);
        botones.add(botonCancelar);
        form.add(botones, gbc);

        if (esEdicion && docenteSeleccionado != null) {
            campoNombre.setText(docenteSeleccionado.getNombre());
            campoEspecialidad.setText(docenteSeleccionado.getEspecialidad());
            campoDocumento.setText(docenteSeleccionado.getDocumento());
            campoEmail.setText(docenteSeleccionado.getEmail());
            campoTelefono.setText(docenteSeleccionado.getTelefono());
            if (docenteSeleccionado.getTituloAcademico() != null) {
                comboTituloAcademico.setSelectedItem(docenteSeleccionado.getTituloAcademico());
            }
            if (docenteSeleccionado.getTipoContrato() != null) {
                comboTipoContrato.setSelectedItem(docenteSeleccionado.getTipoContrato());
            }
        }

        dialogoFormulario.add(form);
        dialogoFormulario.setVisible(true);
    }

    private void guardarDocente() {
        try {
            String nombre = campoNombre.getText().trim();
            String especialidad = campoEspecialidad.getText().trim();
            String documento = campoDocumento.getText().trim();
            String email = campoEmail.getText().trim();
            String telefono = campoTelefono.getText().trim();
            String tituloAcademico = (String) comboTituloAcademico.getSelectedItem();
            String tipoContrato = (String) comboTipoContrato.getSelectedItem();

            if (nombre.isEmpty() || especialidad.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(dialogoFormulario, "Nombre, Especialidad y Documento son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            docenteSeleccionado.setNombre(nombre);
            docenteSeleccionado.setEspecialidad(especialidad);
            docenteSeleccionado.setDocumento(documento);
            docenteSeleccionado.setEmail(email);
            docenteSeleccionado.setTelefono(telefono);
            docenteSeleccionado.setTituloAcademico(tituloAcademico);
            docenteSeleccionado.setTipoContrato(tipoContrato);

            if (modoEdicion) {
                docenteService.actualizarDocente(docenteSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Docente actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                docenteService.registrarDocente(docenteSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Docente registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dialogoFormulario.dispose();
            cargarDocentes();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarDocente() {
        if (tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un docente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar el docente seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                docenteService.eliminarDocente(id);
                JOptionPane.showMessageDialog(this, "Docente eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDocentes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}