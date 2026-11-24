package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VistaPrincipalSwing extends JFrame {

    private static final Color COLOR_HEADER = new Color(41, 128, 185);
    private static final Color COLOR_FONDO_MENU = new Color(226, 226, 226);
    private static final Color COLOR_HOVER = new Color(210, 210, 210);
    private static final Color COLOR_TEXTO_OSCURO = new Color(25, 25, 25);

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final MenuLateralDrawer menuLateral;

    public VistaPrincipalSwing() {
        setTitle("Sistema academico UNIAJC");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de bienvenida
        JPanel panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBackground(new Color(236, 240, 241));
        JLabel labelBienvenida = new JLabel(
                "<html><center><h1>Bienvenido al Sistema academico UNIAJC</h1><p>Abre el menú en la esquina superior izquierda para navegar entre módulos</p></center></html>",
                SwingConstants.CENTER
        );
        labelBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelBienvenida.setForeground(COLOR_HEADER);
        panelBienvenida.add(labelBienvenida, BorderLayout.CENTER);

        // Módulos Swing
        VistaEstudianteSwing vistaEstudianteSwing = new VistaEstudianteSwing();
        VistaDocenteSwing vistaDocenteSwing = new VistaDocenteSwing();
        VistaMateriaSwing vistaMateriaSwing = new VistaMateriaSwing();
        VistaGrupoSwing vistaGrupoSwing = new VistaGrupoSwing();
        VistaInscripcionSwing vistaInscripcionSwing = new VistaInscripcionSwing();
        VistaCalificacionSwing vistaCalificacionSwing = new VistaCalificacionSwing();

        mainPanel.add(panelBienvenida, "INICIO");
        mainPanel.add(vistaEstudianteSwing, "ESTUDIANTES");
        mainPanel.add(vistaDocenteSwing, "DOCENTES");
        mainPanel.add(vistaMateriaSwing, "MATERIAS");
        mainPanel.add(vistaGrupoSwing, "GRUPOS");
        mainPanel.add(vistaInscripcionSwing, "INSCRIPCIONES");
        mainPanel.add(vistaCalificacionSwing, "CALIFICACIONES");

        // Menú lateral desplegable
        menuLateral = new MenuLateralDrawer(
                this::navegarAModulo,
                () -> alternarMenuLateral(false)
        );
        menuLateral.setVisible(false);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(menuLateral, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel crearBarraSuperior() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_HEADER);
        topBar.setPreferredSize(new Dimension(0, 52));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        izquierda.setOpaque(false);

        JButton btnHamburguesa = new JButton();
        btnHamburguesa.setPreferredSize(new Dimension(32, 32));
        btnHamburguesa.setFocusPainted(false);
        btnHamburguesa.setContentAreaFilled(false);
        btnHamburguesa.setBorderPainted(false);
        btnHamburguesa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHamburguesa.setToolTipText("Abrir o cerrar menú");

        URL menuIconUrl = getClass().getResource("/recursos/menu.png");
        if (menuIconUrl != null) {
            ImageIcon rawMenuIcon = new ImageIcon(menuIconUrl);
            Image scaledMenu = rawMenuIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnHamburguesa.setIcon(new ImageIcon(scaledMenu));
        } else {
            btnHamburguesa.setText("☰");
            btnHamburguesa.setFont(new Font("Segoe UI", Font.BOLD, 20));
            btnHamburguesa.setForeground(Color.WHITE);
        }
        btnHamburguesa.addActionListener(e -> alternarMenuLateral(!menuLateral.isVisible()));

        JLabel lblTitulo = new JLabel("Sistema academico UNIAJC");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        izquierda.add(btnHamburguesa);
        izquierda.add(lblTitulo);

        topBar.add(izquierda, BorderLayout.WEST);
        return topBar;
    }

    private void alternarMenuLateral(boolean mostrar) {
        menuLateral.setVisible(mostrar);
        revalidate();
        repaint();
    }

    private void navegarAModulo(String opcion) {
        switch (opcion) {
            case "Inicio" -> cardLayout.show(mainPanel, "INICIO");
            case "Estudiantes" -> cardLayout.show(mainPanel, "ESTUDIANTES");
            case "Docentes" -> cardLayout.show(mainPanel, "DOCENTES");
            case "Materias" -> cardLayout.show(mainPanel, "MATERIAS");
            case "Grupos" -> cardLayout.show(mainPanel, "GRUPOS");
            case "Inscripciones" -> cardLayout.show(mainPanel, "INSCRIPCIONES");
            case "Calificaciones" -> cardLayout.show(mainPanel, "CALIFICACIONES");
            case "Cerrar sesión" -> cerrarSesion();
            default -> cardLayout.show(mainPanel, "INICIO");
        }
        if (!"Cerrar sesión".equals(opcion)) {
            alternarMenuLateral(false);
        }
    }

    private ImageIcon obtenerIconoCerrarSesion() {
        URL iconUrl = getClass().getResource("/recursos/cerrar-sesion.png");
        if (iconUrl != null) {
            ImageIcon rawIcon = new ImageIcon(iconUrl);
            Image scaledImage = rawIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return null;
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                VistaLogin login = new VistaLogin();
                login.mostrarVentana();
            });
        }
    }

    private static class MenuLateralDrawer extends JPanel {

        private final Consumer<String> onSeleccionar;
        private final Runnable onCerrar;

        MenuLateralDrawer(Consumer<String> onSeleccionar, Runnable onCerrar) {
            this.onSeleccionar = onSeleccionar;
            this.onCerrar = onCerrar;
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(280, 600));
            setBackground(COLOR_FONDO_MENU);
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 180, 180)));

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(COLOR_HEADER);
            header.setPreferredSize(new Dimension(280, 52));
            header.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));

            JLabel lblTitulo = new JLabel("Sistema academico UNIAJC");
            lblTitulo.setForeground(Color.WHITE);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

            JButton btnCerrar = new JButton("X");
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnCerrar.setFocusPainted(false);
            btnCerrar.setBorderPainted(false);
            btnCerrar.setContentAreaFilled(false);
            btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCerrar.addActionListener(e -> {
                if (onCerrar != null) {
                    onCerrar.run();
                }
            });

            header.add(lblTitulo, BorderLayout.WEST);
            header.add(btnCerrar, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            JPanel lista = new JPanel();
            lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
            lista.setBackground(COLOR_FONDO_MENU);

            String[] opciones = {
                "Inicio",
                "Estudiantes",
                "Docentes",
                "Materias",
                "Grupos",
                "Inscripciones",
                "Calificaciones",
                "Cerrar sesión"
            };

            for (String opcion : opciones) {
                lista.add(crearFila(opcion));
                lista.add(new JSeparator());
            }
            lista.add(Box.createVerticalGlue());

            JScrollPane scroll = new JScrollPane(lista);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            add(scroll, BorderLayout.CENTER);
        }

        private JPanel crearFila(String texto) {
            JPanel fila = new JPanel(new BorderLayout());
            fila.setBackground(COLOR_FONDO_MENU);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            fila.setPreferredSize(new Dimension(260, 46));
            fila.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            fila.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boolean esCerrarSesion = "Cerrar sesión".equals(texto);

            JLabel lblTexto = new JLabel(texto, SwingConstants.LEFT);
            lblTexto.setFont(new Font("Segoe UI", esCerrarSesion ? Font.BOLD : Font.PLAIN, 13));
            lblTexto.setForeground(esCerrarSesion ? new Color(192, 57, 43) : COLOR_TEXTO_OSCURO);

            if (esCerrarSesion) {
                URL iconUrl = getClass().getResource("/recursos/cerrar-sesion.png");
                if (iconUrl != null) {
                    ImageIcon rawIcon = new ImageIcon(iconUrl);
                    Image scaledImage = rawIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    lblTexto.setIcon(new ImageIcon(scaledImage));
                    lblTexto.setIconTextGap(8);
                }
            }

            JLabel lblFlecha = new JLabel(">");
            lblFlecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblFlecha.setForeground(esCerrarSesion ? new Color(192, 57, 43) : COLOR_TEXTO_OSCURO);

            fila.add(lblTexto, BorderLayout.WEST);
            fila.add(lblFlecha, BorderLayout.EAST);

            fila.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    fila.setBackground(COLOR_HOVER);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    fila.setBackground(COLOR_FONDO_MENU);
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (onSeleccionar != null) {
                        onSeleccionar.accept(texto);
                    }
                }
            });

            return fila;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaPrincipalSwing frame = new VistaPrincipalSwing();
            frame.setVisible(true);
        });
    }
}