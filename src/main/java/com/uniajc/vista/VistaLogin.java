package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Estudiante;

public class VistaLogin extends JFrame {
    private final EstudianteDao estudianteDao;
    private final DocenteDao docenteDao;

    private JTextField txtIdentificador; 
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JLabel lblMensaje;

    public VistaLogin() {
        this(new EstudianteDao(), new DocenteDao());
    }

    public VistaLogin(EstudianteDao estudianteDao, DocenteDao docenteDao) {
        this.estudianteDao = estudianteDao;
        this.docenteDao = docenteDao;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Práctica - Inicio de Sesión");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // Encabezado
        JLabel lblTitulo = new JLabel("Sistema Práctica", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 43, 54));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 1, 8, 8));
        panelFormulario.setOpaque(false);

        JLabel lblUsuario = new JLabel("Correo o ID de Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtIdentificador = new JTextField();

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContrasena = new JPasswordField();

        panelFormulario.add(lblUsuario);
        panelFormulario.add(txtIdentificador);
        panelFormulario.add(lblContrasena);
        panelFormulario.add(txtContrasena);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

     
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
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

      
        btnIngresar.addActionListener(e -> capturarCredenciales());
        txtContrasena.addActionListener(e -> capturarCredenciales());
    }

    public void capturarCredenciales() {
        String identificador = txtIdentificador.getText().trim();
        String pass = new String(txtContrasena.getPassword());

        if (identificador.isEmpty() || pass.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña");
            return;
        }

        Optional<Estudiante> optEstudiante = estudianteDao.buscarPorEmail(identificador);
        if (optEstudiante.isPresent()) {
            Estudiante estudiante = optEstudiante.get();
            mostrarExito("¡Bienvenido Estudiante " + estudiante.getName() + "!");
            dispose();
            return;
        } 

        
        try {
            int idDocente = Integer.parseInt(identificador);
            Optional<Docente> optDocente = docenteDao.buscarPorId(idDocente);
            if (optDocente.isPresent()) {
                Docente docente = optDocente.get();
                mostrarExito("¡Bienvenido Docente " + docente.getName() + "!");
                dispose();
                return;
            }
        } catch (NumberFormatException ignored) {
           
        }

        mostrarError("Credenciales inválidas o usuario no registrado");
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