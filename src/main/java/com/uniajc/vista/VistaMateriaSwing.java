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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

public class VistaMateriaSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID", "Nombre de Materia", "Créditos"};

    private final MateriaService materiaService;
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
    private JSpinner spinnerCreditos;
    private JButton botonGuardar;
    private boolean modoEdicion;
    private Materia materiaSeleccionada;

    public VistaMateriaSwing() {
        this(new MateriaService(new MateriaDao()));
    }

    public VistaMateriaSwing(MateriaService materiaService) {
        this.materiaService = materiaService;
        initComponents();
        cargarMaterias();
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

        JLabel labelBusqueda = new JLabel("Buscar Materia (ID / Nombre):");
        labelBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelBusqueda.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(22);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.setPreferredSize(new Dimension(220, 30));
        textoBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarMaterias();
                }
            }
        });

        botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarMaterias());

        botonLimpiarBusqueda = crearBoton("Limpiar", new Color(149, 165, 166));
        botonLimpiarBusqueda.addActionListener(e -> {
            textoBusqueda.setText("");
            cargarMaterias();
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
                JOptionPane.showMessageDialog(this, "Seleccione una materia de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarMateria());

        botonActualizar = crearBoton("Actualizar", new Color(149, 165, 166));
        botonActualizar.addActionListener(e -> cargarMaterias());

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

    public void cargarMaterias() {
        modeloTabla.setRowCount(0);
        try {
            List<Materia> materias = materiaService.obtenerTodasLasMaterias();
            for (Materia m : materias) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getName(), m.getCredits()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar materias: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarMaterias() {
        String criterio = textoBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            cargarMaterias();
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<Materia> resultados = new ArrayList<>();
            if (criterio.matches("^\\d+$")) {
                int id = Integer.parseInt(criterio);
                Optional<Materia> porId = materiaService.buscarMateriaPorId(id);
                porId.ifPresent(resultados::add);
            }

            List<Materia> porNombre = materiaService.buscarMateriasPorNombre(criterio);
            for (Materia m : porNombre) {
                if (resultados.stream().noneMatch(existente -> existente.getId() == m.getId())) {
                    resultados.add(m);
                }
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron materias para: \"" + criterio + "\"", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Materia m : resultados) {
                    modeloTabla.addRow(new Object[]{m.getId(), m.getName(), m.getCredits()});
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
                materiaSeleccionada = materiaService.buscarMateriaPorId(id).orElse(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            materiaSeleccionada = new Materia();
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogoFormulario = new JDialog(parentFrame, esEdicion ? "Editar Materia" : "Registrar Materia", true);
        dialogoFormulario.setSize(380, 240);
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
        JLabel labelTitulo = new JLabel(esEdicion ? "Editar Materia" : "Registrar Materia");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        panelFormulario.add(labelTitulo, gbc);

        gbc.gridwidth = 1;

        // Nombre
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        campoNombre = new JTextField(18);
        gbc.gridx = 1;
        panelFormulario.add(campoNombre, gbc);

        // Créditos
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Créditos (1-10):"), gbc);
        spinnerCreditos = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        gbc.gridx = 1;
        panelFormulario.add(spinnerCreditos, gbc);

        // Botones formulario
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonesForm.setBackground(Color.WHITE);

        botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarMateria());

        JButton botonCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        botonCancelar.addActionListener(e -> dialogoFormulario.dispose());

        panelBotonesForm.add(botonGuardar);
        panelBotonesForm.add(botonCancelar);
        panelFormulario.add(panelBotonesForm, gbc);

        if (esEdicion && materiaSeleccionada != null) {
            campoNombre.setText(materiaSeleccionada.getName());
            spinnerCreditos.setValue(materiaSeleccionada.getCredits());
        }

        dialogoFormulario.add(panelFormulario);
        dialogoFormulario.setVisible(true);
    }

    private void guardarMateria() {
        try {
            String nombre = campoNombre.getText().trim();
            int creditos = (int) spinnerCreditos.getValue();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialogoFormulario, "El nombre de la materia es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            materiaSeleccionada.setName(nombre);
            materiaSeleccionada.setCredits(creditos);

            if (modoEdicion) {
                materiaService.actualizarMateria(materiaSeleccionada);
                JOptionPane.showMessageDialog(dialogoFormulario, "Materia actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                materiaService.registrarMateria(materiaSeleccionada);
                JOptionPane.showMessageDialog(dialogoFormulario, "Materia registrada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dialogoFormulario.dispose();
            cargarMaterias();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMateria() {
        if (tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la materia seleccionada?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                materiaService.eliminarMateria(id);
                JOptionPane.showMessageDialog(this, "Materia eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarMaterias();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}

