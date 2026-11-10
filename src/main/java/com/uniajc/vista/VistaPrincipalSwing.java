package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VistaPrincipalSwing extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public VistaPrincipalSwing() {
        setTitle("Sistema de Gestión Académica - UNIAJC");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de bienvenida
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBackground(new Color(236, 240, 241));
        JLabel labelBienvenida = new JLabel("<html><center><h1>Bienvenido al Sistema Académico UNIAJC</h1><p>Seleccione un módulo desde el menú superior para gestionar el ecosistema académico</p></center></html>", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelBienvenida.setForeground(new Color(41, 128, 185));
        panelBienvenida.add(labelBienvenida, BorderLayout.CENTER);

        // Módulos Swing
        VistaEstudianteSwing vistaEstudianteSwing = new VistaEstudianteSwing();
        VistaDocenteSwing vistaDocenteSwing = new VistaDocenteSwing();
        VistaMateriaSwing vistaMateriaSwing = new VistaMateriaSwing();
        VistaGrupoSwing vistaGrupoSwing = new VistaGrupoSwing();
        VistaInscripcionSwing vistaInscripcionSwing = new VistaInscripcionSwing();

        mainPanel.add(panelBienvenida, "INICIO");
        mainPanel.add(vistaEstudianteSwing, "ESTUDIANTES");
        mainPanel.add(vistaDocenteSwing, "DOCENTES");
        mainPanel.add(vistaMateriaSwing, "MATERIAS");
        mainPanel.add(vistaGrupoSwing, "GRUPOS");
        mainPanel.add(vistaInscripcionSwing, "INSCRIPCIONES");

        setJMenuBar(crearMenu());
        add(mainPanel);
    }

    private JMenuBar crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JMenuItem itemInicio = new JMenuItem("Inicio");
        itemInicio.addActionListener(e -> cardLayout.show(mainPanel, "INICIO"));
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemInicio);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuModulos = new JMenu("Módulos");
        menuModulos.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JMenuItem itemEstudiantes = new JMenuItem("Estudiantes");
        itemEstudiantes.addActionListener(e -> cardLayout.show(mainPanel, "ESTUDIANTES"));

        JMenuItem itemDocentes = new JMenuItem("Docentes");
        itemDocentes.addActionListener(e -> cardLayout.show(mainPanel, "DOCENTES"));

        JMenuItem itemMaterias = new JMenuItem("Materias");
        itemMaterias.addActionListener(e -> cardLayout.show(mainPanel, "MATERIAS"));

        JMenuItem itemGrupos = new JMenuItem("Grupos");
        itemGrupos.addActionListener(e -> cardLayout.show(mainPanel, "GRUPOS"));

        JMenuItem itemInscripciones = new JMenuItem("Inscripciones");
        itemInscripciones.addActionListener(e -> cardLayout.show(mainPanel, "INSCRIPCIONES"));

        menuModulos.add(itemEstudiantes);
        menuModulos.add(itemDocentes);
        menuModulos.add(itemMaterias);
        menuModulos.add(itemGrupos);
        menuModulos.add(itemInscripciones);

        menuBar.add(menuArchivo);
        menuBar.add(menuModulos);

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaPrincipalSwing frame = new VistaPrincipalSwing();
            frame.setVisible(true);
        });
    }
}
