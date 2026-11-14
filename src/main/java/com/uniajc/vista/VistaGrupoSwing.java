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
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.MateriaService;

public class VistaGrupoSwing extends JPanel implements ActionListener {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);

    private static final String[] COLUMNAS = {"ID Grupo", "ID Materia", "ID Docente", "Aula", "Horario"};

    private final GrupoService grupoService;
    private final MateriaService materiaService;
    private final DocenteService docenteService;

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
    private JComboBox<MateriaItem> comboMaterias;
    private JComboBox<DocenteItem> comboDocentes;
    private JTextField campoAula;
    private JTextField campoHorario;
    private JButton botonGuardar;
    private boolean modoEdicion;
    private Grupo grupoSeleccionado;

    // Helper classes para comboboxes
    private static class MateriaItem {
        final int id;
        final String nombre;
        MateriaItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return id + " - " + nombre; }
    }

    private static class DocenteItem {
        final int id;
        final String nombre;
        DocenteItem(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return id + " - " + nombre; }
    }

    public VistaGrupoSwing() {
        this.grupoService = new GrupoService(new GrupoDao(), new MateriaDao(), new DocenteDao());
        this.materiaService = new MateriaService(new MateriaDao());
        this.docenteService = new DocenteService(new DocenteDao());
        initComponents();
        cargarGrupos();
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

        JLabel labelBusqueda = new JLabel("Buscar Grupo (ID / Aula):");
        labelBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelBusqueda.setForeground(COLOR_PRIMARIO);

        textoBusqueda = new JTextField(22);
        textoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textoBusqueda.setPreferredSize(new Dimension(220, 30));
        textoBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarGrupos();
                }
            }
        });

        botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscarGrupos());

        botonLimpiarBusqueda = crearBoton("Limpiar", new Color(149, 165, 166));
        botonLimpiarBusqueda.addActionListener(e -> {
            textoBusqueda.setText("");
            cargarGrupos();
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
                JOptionPane.showMessageDialog(this, "Seleccione un grupo de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        botonEliminar = crearBoton("Eliminar", COLOR_ACENTO);
        botonEliminar.addActionListener(e -> eliminarGrupo());

        botonActualizar = crearBoton("Actualizar", new Color(149, 165, 166));
        botonActualizar.addActionListener(e -> cargarGrupos());

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

    public void cargarGrupos() {
        modeloTabla.setRowCount(0);
        try {
            List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
            for (Grupo g : grupos) {
                modeloTabla.addRow(new Object[]{g.getId(), g.getIdMateria(), g.getIdDocente(), g.getClassroom(), g.getSchedule()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarGrupos() {
        String criterio = textoBusqueda.getText().trim();
        if (criterio.isEmpty()) {
            cargarGrupos();
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<Grupo> resultados = new ArrayList<>();
            if (criterio.matches("^\\d+$")) {
                int id = Integer.parseInt(criterio);
                Optional<Grupo> porId = grupoService.buscarGrupoPorId(id);
                porId.ifPresent(resultados::add);
            }

            List<Grupo> porAula = grupoService.buscarGruposPorAula(criterio);
            for (Grupo g : porAula) {
                if (resultados.stream().noneMatch(existente -> existente.getId() == g.getId())) {
                    resultados.add(g);
                }
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron grupos para: \"" + criterio + "\"", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                cargarGrupos();
            } else {
                for (Grupo g : resultados) {
                    modeloTabla.addRow(new Object[]{g.getId(), g.getIdMateria(), g.getIdDocente(), g.getClassroom(), g.getSchedule()});
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
                grupoSeleccionado = grupoService.buscarGrupoPorId(id).orElse(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            grupoSeleccionado = new Grupo();
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        dialogoFormulario = new JDialog(parentFrame, esEdicion ? "Editar Grupo" : "Registrar Grupo", true);
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
        JLabel labelTitulo = new JLabel(esEdicion ? "Editar Grupo" : "Registrar Grupo");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTitulo.setForeground(COLOR_PRIMARIO);
        panelFormulario.add(labelTitulo, gbc);

        gbc.gridwidth = 1;

        // Materia
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Materia:"), gbc);
        comboMaterias = new JComboBox<>();
        List<Materia> materias = materiaService.obtenerTodasLasMaterias();
        for (Materia m : materias) {
            comboMaterias.addItem(new MateriaItem(m.getId(), m.getName()));
        }
        gbc.gridx = 1;
        panelFormulario.add(comboMaterias, gbc);

        // Docente
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Docente:"), gbc);
        comboDocentes = new JComboBox<>();
        List<Docente> docentes = docenteService.obtenerTodosLosDocentes();
        for (Docente d : docentes) {
            comboDocentes.addItem(new DocenteItem(d.getIdDocente(), d.getNombre() + " (" + d.getEspecialidad() + ")"));
        }
        gbc.gridx = 1;
        panelFormulario.add(comboDocentes, gbc);

        // Aula
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Aula:"), gbc);
        campoAula = new JTextField(18);
        gbc.gridx = 1;
        panelFormulario.add(campoAula, gbc);

        // Horario
        gbc.gridy++;
        gbc.gridx = 0;
        panelFormulario.add(new JLabel("Horario:"), gbc);
        campoHorario = new JTextField(18);
        gbc.gridx = 1;
        panelFormulario.add(campoHorario, gbc);

        // Botones formulario
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonesForm.setBackground(Color.WHITE);

        botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        botonGuardar.addActionListener(e -> guardarGrupo());

        JButton botonCancelar = crearBoton("Cancelar", new Color(149, 165, 166));
        botonCancelar.addActionListener(e -> dialogoFormulario.dispose());

        panelBotonesForm.add(botonGuardar);
        panelBotonesForm.add(botonCancelar);
        panelFormulario.add(panelBotonesForm, gbc);

        if (esEdicion && grupoSeleccionado != null) {
            for (int i = 0; i < comboMaterias.getItemCount(); i++) {
                if (comboMaterias.getItemAt(i).id == grupoSeleccionado.getIdMateria()) {
                    comboMaterias.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < comboDocentes.getItemCount(); i++) {
                if (comboDocentes.getItemAt(i).id == grupoSeleccionado.getIdDocente()) {
                    comboDocentes.setSelectedIndex(i);
                    break;
                }
            }
            campoAula.setText(grupoSeleccionado.getClassroom());
            campoHorario.setText(grupoSeleccionado.getSchedule());
        }

        dialogoFormulario.add(panelFormulario);
        dialogoFormulario.setVisible(true);
    }

    private void guardarGrupo() {
        try {
            MateriaItem materiaSel = (MateriaItem) comboMaterias.getSelectedItem();
            DocenteItem docenteSel = (DocenteItem) comboDocentes.getSelectedItem();
            String aula = campoAula.getText().trim();
            String horario = campoHorario.getText().trim();

            if (materiaSel == null || docenteSel == null || aula.isEmpty() || horario.isEmpty()) {
                JOptionPane.showMessageDialog(dialogoFormulario, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            grupoSeleccionado.setIdMateria(materiaSel.id);
            grupoSeleccionado.setIdDocente(docenteSel.id);
            grupoSeleccionado.setClassroom(aula);
            grupoSeleccionado.setSchedule(horario);

            if (modoEdicion) {
                grupoService.actualizarGrupo(grupoSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Grupo actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                grupoService.registrarGrupo(grupoSeleccionado);
                JOptionPane.showMessageDialog(dialogoFormulario, "Grupo registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            dialogoFormulario.dispose();
            cargarGrupos();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogoFormulario, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarGrupo() {
        if (tabla.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el grupo seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabla.getValueAt(tabla.getSelectedRow(), 0);
                grupoService.eliminarGrupo(id);
                JOptionPane.showMessageDialog(this, "Grupo eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarGrupos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}