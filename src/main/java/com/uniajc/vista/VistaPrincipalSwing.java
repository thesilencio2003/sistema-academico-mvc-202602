package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
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
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de bienvenida
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBackground(new Color(236, 240, 241));
        JLabel labelBienvenida = new JLabel("Bienvenido al Sistema de Gestión Académica UNIAJC", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelBienvenida.setForeground(new Color(41, 128, 185));
        panelBienvenida.add(labelBienvenida, BorderLayout.CENTER);

        // Panel de Estudiantes
        VistaEstudianteSwing vistaEstudianteSwing = new VistaEstudianteSwing();

        mainPanel.add(panelBienvenida, "INICIO");
        mainPanel.add(vistaEstudianteSwing, "ESTUDIANTES");

        setJMenuBar(crearMenu());
        add(mainPanel);
    }

    private JMenuBar crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuNavegacion = new JMenu("Módulos");
        menuNavegacion.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JMenuItem itemInicio = new JMenuItem("Inicio");
        itemInicio.addActionListener(e -> cardLayout.show(mainPanel, "INICIO"));

        JMenuItem itemEstudiantes = new JMenuItem("Estudiantes");
        itemEstudiantes.addActionListener(e -> cardLayout.show(mainPanel, "ESTUDIANTES"));

        menuNavegacion.add(itemInicio);
        menuNavegacion.add(itemEstudiantes);

        JMenu menuSalir = new JMenu("Archivo");
        menuSalir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuSalir.add(itemSalir);

        menuBar.add(menuSalir);
        menuBar.add(menuNavegacion);

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaPrincipalSwing frame = new VistaPrincipalSwing();
            frame.setVisible(true);
        });
    }
}

