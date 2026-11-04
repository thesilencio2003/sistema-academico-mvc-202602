package com.uniajc.vista;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

public class VistaEstudiante {

    private final ControladorEstudiante controlador;
    private final Scanner scanner;

    public VistaEstudiante() {
        this.controlador = new ControladorEstudiante();
        this.scanner = new Scanner(System.in);
    }

    public void iniciarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n===== GESTIÓN DE ESTUDIANTES (CONSOLA) =====");
            System.out.println("1. Listar Estudiantes");
            System.out.println("2. Registrar Estudiante");
            System.out.println("3. Buscar Estudiante por ID");
            System.out.println("4. Buscar Estudiante por Nombre");
            System.out.println("5. Actualizar Estudiante");
            System.out.println("6. Eliminar Estudiante");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
                continue;
            }

            switch (opcion) {
                case 1 -> listarEstudiantes();
                case 2 -> registrarEstudiante();
                case 3 -> buscarPorId();
                case 4 -> buscarPorNombre();
                case 5 -> actualizarEstudiante();
                case 6 -> eliminarEstudiante();
                case 0 -> System.out.println("Saliendo del menú de estudiantes...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void listarEstudiantes() {
        System.out.println("\n--- Lista de Estudiantes ---");
        try {
            List<Estudiante> lista = controlador.listarEstudiantes();
            if (lista.isEmpty()) {
                System.out.println("No hay estudiantes registrados.");
                return;
            }
            for (Estudiante e : lista) {
                System.out.printf("ID: %-4d | Nombre: %-15s | Apellido: %-15s | Correo: %s%n",
                        e.getId(), e.getName(), e.getLastname(), e.getEmail());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarEstudiante() {
        System.out.println("\n--- Registrar Estudiante ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Correo: ");
        String email = scanner.nextLine().trim();

        try {
            controlador.registrarEstudiante(nombre, apellido, email);
            System.out.println("¡Estudiante registrado exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        System.out.println("\n--- Buscar por ID ---");
        System.out.print("Ingrese ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<Estudiante> estudiante = controlador.buscarEstudiantePorId(id);
            if (estudiante.isPresent()) {
                Estudiante e = estudiante.get();
                System.out.printf("Encontrado: ID=%d, Nombre=%s %s, Correo=%s%n",
                        e.getId(), e.getName(), e.getLastname(), e.getEmail());
            } else {
                System.out.println("No se encontró ningún estudiante con ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido: debe ser un número entero.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorNombre() {
        System.out.println("\n--- Buscar por Nombre ---");
        System.out.print("Ingrese nombre o apellido a buscar: ");
        String nombre = scanner.nextLine().trim();
        try {
            List<Estudiante> resultados = controlador.buscarEstudiantesPorNombre(nombre);
            if (resultados.isEmpty()) {
                System.out.println("No se encontraron estudiantes que coincidan con: " + nombre);
                return;
            }
            System.out.println("Resultados encontrados: " + resultados.size());
            for (Estudiante e : resultados) {
                System.out.printf("ID: %-4d | Nombre: %-15s | Apellido: %-15s | Correo: %s%n",
                        e.getId(), e.getName(), e.getLastname(), e.getEmail());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarEstudiante() {
        System.out.println("\n--- Actualizar Estudiante ---");
        System.out.print("Ingrese ID del estudiante: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Nuevo Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Nuevo Apellido: ");
            String apellido = scanner.nextLine().trim();
            System.out.print("Nuevo Correo: ");
            String email = scanner.nextLine().trim();

            controlador.actualizarEstudiante(id, nombre, apellido, email);
            System.out.println("¡Estudiante actualizado exitosamente!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido: debe ser un número entero.");
        } catch (Exception e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }

    private void eliminarEstudiante() {
        System.out.println("\n--- Eliminar Estudiante ---");
        System.out.print("Ingrese ID del estudiante a eliminar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            controlador.eliminarEstudiante(id);
            System.out.println("¡Estudiante eliminado exitosamente!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido: debe ser un número entero.");
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
}
