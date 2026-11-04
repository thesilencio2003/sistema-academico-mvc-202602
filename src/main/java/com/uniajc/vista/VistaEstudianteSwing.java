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
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

public class VistaEstudianteSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID", "Nombre", "Apellido", "Correo"};

    private final EstudianteService estudianteService;
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
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoEmail;
    private JButton botonGuardar;
    private boolean modoEdicion;
    private Estudiante estudianteSeleccionado;

    public VistaEstudianteSwing() {
        this(new EstudianteService(new EstudianteDao()));
    }

    public VistaEstudianteSwing(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
        initComponents();
        cargarEstudiantes();
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

        JLabel labelBusqueda = new JLabel("Buscar por ID o Nombre:");
        labelBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelBusqueda.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(25);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.setPreferredSize(new Dimension(220, 30));
        textoBusqueda.setToolTipText("Ingrese ID o parte del nombre/apellido");
        textoBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarEstudiantes();
                }
            }
        });

        botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarEstudiantes());

        botonLimpiarBusqueda = crearBoton("Limpiar", new Color(149, 165, 166));
        botonLimpiarBusqueda.addActionListener(e -> {
            textoBusqueda.setText("");
            cargarEstudiantes();
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
                JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarEstudiante());

        botonActualizar = crearBoton("Actualizar", new Color(149, 165, 166));
        botonActualizar.addActionListener(e -> cargarEstudiantes());

        panel.add(botonRegistrar);
        panel.add(botonEditar);
        panel.add(botonEliminar);
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

    public void cargarEstudiantes() {
        modeloTabla.setRowCount(0);
        try {
            List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
            for (Estudiante e : estudiantes) {
                modeloTabla.addRow(new Object[]{e.getId(), e.getName(), e.getLastname(), e.getEmail()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarEstudiantes() {
        String criterio = textoBusqueda.getText().trim();

        if (criterio.isEmpty()) {
            cargarEstudiantes();
            return;
        }

        modeloTabla.setRowCount(0);

        try {
            List<Estudiante> resultados = new ArrayList<>();

            // 1. Si es numérico, intentamos buscar primero por ID exacto
            if (criterio.matches("^\\d+$")) {
                int id = Integer.parseInt(criterio);
                Optional<Estudiante> porId = estudianteService.buscarEstudiantePorId(id);
                porId.ifPresent(resultados::add);
            }

            // 2. Buscamos por coincidencia en Nombre / Apellido en la base de datos
            List<Estudiante> porNombre = estudianteService.buscarEstudiantesPorNombre(criterio);
            for (Estudiante e : porNombre) {
                if (resultados.stream().noneMatch(existente -> existente.getId() == e.getId())) {
                    resultados.add(e);
                }
            }

            // 3. Renderizar resultados en la tabla
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron estudiantes para: \"" + criterio + "\"", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Estudiante e : resultados) {
                    modeloTabla.addRow(new Object[]{e.getId(), e.getName(), e.getLastname(), e.getEmail()});
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
                estudianteSeleccionado = estudianteService.buscarEstudiantePorId(id).orElse(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            estudianteSeleccionado = new Estudiante();
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogoFormulario = new JDialog(parentFrame, esEdicion ? "Editar Estudiante" : "Registrar Estudiante", true);
        dialogoFormulario.setSize(420, 280);
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
        JLabel labelTitulo = new JLabel(esEdicion ? "Editar Estudiante" : "Registrar Estudiante");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        panelFormulario.add(labelTitulo, gbc);

        gbc.gridwidth = 1;

        // Nombre
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        campoNombre = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(campoNombre, gbc);

        // Apellido
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Apellido:"), gbc);
        campoApellido = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(campoApellido, gbc);

        // Email
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Correo Electrónico:"), gbc);
        campoEmail = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(campoEmail, gbc);

        // Botones formulario
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonesForm.setBackground(Color.WHITE);

        botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarEstudiante());

        JButton botonCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        botonCancelar.addActionListener(e -> dialogoFormulario.dispose());

        panelBotonesForm.add(botonGuardar);
        panelBotonesForm.add(botonCancelar);
        panelFormulario.add(panelBotonesForm, gbc);

        if (esEdicion && estudianteSeleccionado != null) {
            campoNombre.setText(estudianteSeleccionado.getName());
            campoApellido.setText(estudianteSeleccionado.getLastname());
            campoEmail.setText(estudianteSeleccionado.getEmail());
        }

        dialogoFormulario.add(panelFormulario);
        dialogoFormulario.setVisible(true);
    }

    private void guardarEstudiante() {
        try {
            String nombre = campoNombre.getText().trim();
            String apellido = campoApellido.getText().trim();
            String email = campoEmail.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialogoFormulario, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            estudianteSeleccionado.setName(nombre);
            estudianteSeleccionado.setLastname(apellido);
            estudianteSeleccionado.setEmail(email);

            if (modoEdicion) {
                estudianteService.actualizarEstudiante(estudianteSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Estudiante actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                estudianteService.registrarEstudiante(estudianteSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Estudiante registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dialogoFormulario.dispose();
            cargarEstudiantes();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante() {
        if (tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el estudiante seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                estudianteService.eliminarEstudiante(id);
                JOptionPane.showMessageDialog(this, "Estudiante eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarEstudiantes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}
