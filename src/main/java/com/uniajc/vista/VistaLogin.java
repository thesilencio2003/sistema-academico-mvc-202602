package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.uniajc.controlador.ControladorLogin;
import com.uniajc.modelo.Usuario;

public class VistaLogin extends JFrame {

    private final ControladorLogin controladorLogin;

    private JTextField txtUsername;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JLabel lblMensaje;

    public VistaLogin() {
        this(new ControladorLogin());
    }

    public VistaLogin(ControladorLogin controladorLogin) {
        this.controladorLogin = controladorLogin;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sistema de Gestión Académica - Inicio de Sesión");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // Encabezado
        JLabel lblTitulo = new JLabel("Sistema Académico UNIAJC", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 43, 54));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 1, 8, 8));
        panelFormulario.setOpaque(false);

        JLabel lblUsuario = new JLabel("Nombre de Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContrasena = new JPasswordField();
        txtContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelFormulario.add(lblUsuario);
        panelFormulario.add(txtUsername);
        panelFormulario.add(lblContrasena);
        panelFormulario.add(txtContrasena);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Panel inferior: mensaje y botón
        JPanel panelInferior = new JPanel(new BorderLayout(8, 8));
        panelInferior.setOpaque(false);

        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(37, 99, 235));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(Color.RED);

        panelInferior.add(lblMensaje, BorderLayout.NORTH);
        panelInferior.add(btnIngresar, BorderLayout.SOUTH);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Eventos
        btnIngresar.addActionListener(e -> capturarCredenciales());
        txtUsername.addActionListener(e -> txtContrasena.requestFocusInWindow());
        txtContrasena.addActionListener(e -> capturarCredenciales());
    }

    public void capturarCredenciales() {
        String username = txtUsername.getText().trim();
        String password = new String(txtContrasena.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña.");
            return;
        }

        try {
            btnIngresar.setEnabled(false);
            btnIngresar.setText("Verificando...");

            Usuario usuario = controladorLogin.iniciarSesion(username, password);

            mostrarExito("¡Bienvenido, " + usuario.getNombre() + " (" + usuario.getRol() + ")!");

            // Abrir la ventana principal de la aplicación y cerrar login
            SwingUtilities.invokeLater(() -> {
                VistaPrincipalSwing vistaPrincipal = new VistaPrincipalSwing();
                vistaPrincipal.setVisible(true);
                dispose();
            });

        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error al iniciar sesión: " + ex.getMessage());
        } finally {
            btnIngresar.setEnabled(true);
            btnIngresar.setText("Iniciar Sesión");
        }
    }

    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(new Color(220, 38, 38));
        lblMensaje.setText(mensaje);
    }

    public void mostrarExito(String mensaje) {
        lblMensaje.setForeground(new Color(22, 163, 74));
        lblMensaje.setText(mensaje);
    }

    public void mostrarVentana() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    public static void main(String[] args) {
        new VistaLogin().mostrarVentana();
    }
}
