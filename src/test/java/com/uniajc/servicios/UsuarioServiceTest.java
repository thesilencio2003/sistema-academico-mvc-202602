package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.modelo.Usuario;

public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        usuarioService = new UsuarioService();
    }

    @Test
    public void testAutenticarCamposVacios() {
        assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar("", "123"));
        assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar("admin", ""));
        assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar(null, "123"));
    }

    @Test
    public void testAutenticarUsuarioInexistente() {
        assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar("usuario_inexistente_xyz_123", "clave"));
    }

    @Test
    public void testAutenticarExitosoTextoPlano() {
        Usuario usuario = usuarioService.autenticar("alejandro", "12345678");
        assertNotNull(usuario);
        assertEquals("alejandro", usuario.getUsername());
    }

    @Test
    public void testAutenticarExitosoMD5() {
        Usuario usuario = usuarioService.autenticar("admin", "admin123");
        assertNotNull(usuario);
        assertEquals("admin", usuario.getUsername());
    }

    @Test
    public void testAutenticarContrasenaErronea() {
        assertThrows(IllegalArgumentException.class, () -> usuarioService.autenticar("alejandro", "password_incorrecta"));
    }
}

