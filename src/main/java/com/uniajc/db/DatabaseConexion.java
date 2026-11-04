package com.uniajc.db;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConexion {
    private static Connection connection = null;

    /**
     * Obtiene la conexión a la base de datos.
     * Si no existe o se encuentra cerrada, crea una nueva conexión usando config.properties.
     *
     * @return Objeto Connection listo para usar.
     */
    public static Connection getConnection() {
        Properties properties = new Properties();

        try {
            if (connection == null || connection.isClosed()) {
                properties.load(new FileInputStream(new File("config.properties")));

                String url = properties.get("URL").toString();
                String user = properties.get("USER").toString();
                String password = properties.get("PASSWORD").toString();

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conexion exitosa.");
            }
        } catch (SQLException error) {
            System.out.println("Failed to establish connection. " + error.getMessage());
            throw new RuntimeException("Error conectando a la base de datos: " + error.getMessage(), error);
        } catch (FileNotFoundException e) {
            System.out.println("config.properties file not found.");
            throw new RuntimeException("Archivo config.properties no encontrado.", e);
        } catch (IOException e) {
            System.out.println("Error reading config.properties file.");
            throw new RuntimeException("Error al leer config.properties.", e);
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed successfully.");
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection. " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
