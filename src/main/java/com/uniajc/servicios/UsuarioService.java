package com.uniajc.servicios;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import com.uniajc.dao.UsuarioDao;
import com.uniajc.modelo.Usuario;

public class UsuarioService {

    private final UsuarioDao usuarioDao;

    public UsuarioService() {
        this.usuarioDao = new UsuarioDao();
    }

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Autentica un usuario verificando username y password contra la tabla usuarios.
     * Soporta tanto contraseñas en texto plano como hashes MD5.
     *
     * @param username Nombre de usuario
     * @param password Contraseña ingresada
     * @return El objeto Usuario autenticado
     * @throws IllegalArgumentException Si las credenciales son inválidas o incompletas
     * @throws IllegalStateException Si el usuario existe pero está inactivo
     */
    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        Optional<Usuario> optUsuario = usuarioDao.buscarPorUsername(username.trim());
        if (optUsuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }

        Usuario usuario = optUsuario.get();

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario '" + usuario.getUsername() + "' está inactivo. Contacte al administrador.");
        }

        if (!verificarContrasena(password.trim(), usuario.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }

        // Actualizar último acceso del usuario autenticado
        try {
            usuarioDao.actualizarUltimoAcceso(usuario.getId());
            usuario.setUltimoAcceso(new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (Exception ignored) {}

        return usuario;
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio.");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            usuario.setRol("USUARIO");
        }

        Optional<Usuario> existente = usuarioDao.buscarPorUsername(usuario.getUsername());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el username: " + usuario.getUsername());
        }

        usuarioDao.guardar(usuario);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioDao.buscarPorUsername(username.trim());
    }

    public Optional<Usuario> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return usuarioDao.buscarPorId(id);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioDao.obtenerTodos();
    }

    private boolean verificarContrasena(String passwordIngresada, String passwordAlmacenada) {
        if (passwordAlmacenada == null || passwordIngresada == null) {
            return false;
        }

        // Comparación directa (texto plano)
        if (passwordAlmacenada.equals(passwordIngresada)) {
            return true;
        }

        // Comparación con hash MD5
        String hashMd5 = calcularMD5(passwordIngresada);
        if (hashMd5 != null && passwordAlmacenada.equalsIgnoreCase(hashMd5)) {
            return true;
        }

        // Comparación con hash SHA-256 por compatibilidad futura
        String hashSha256 = calcularSHA256(passwordIngresada);
        return hashSha256 != null && passwordAlmacenada.equalsIgnoreCase(hashSha256);
    }

    private String calcularMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String calcularSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}

