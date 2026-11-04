package com.uniajc;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;

import com.uniajc.db.DatabaseConexion;
import com.uniajc.vista.VistaEstudiante;
import com.uniajc.vista.VistaPrincipalSwing;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gestión Académica UNIAJC...");

        // Probar/inicializar conexión
        DatabaseConexion.getConnection();

        // Lanzar interfaz gráfica Swing
        SwingUtilities.invokeLater(() -> {
            configurarLookAndFeel();
            VistaPrincipalSwing ventana = new VistaPrincipalSwing();
            ventana.setVisible(true);
        });
    }

    private static void configurarLookAndFeel() {
        try {
            Color colorPrimario = new Color(41, 128, 185);
            UIManager.put("Label.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Button.font", new FontUIResource("Segoe UI", Font.BOLD, 12));
            UIManager.put("Table.font", new FontUIResource("Segoe UI", Font.PLAIN, 11));
        } catch (Exception ignored) {}
    }
}
