package com.exemplo.service;

import com.exemplo.model.Usuario;
import io.opentelemetry.api.trace.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final Map<String, Usuario> bancoSimulado = new HashMap<>();
    private final Random random = new Random();

    public UsuarioService() {
        bancoSimulado.put("1", new Usuario("1", "João Silva", "joao@email.com"));
        bancoSimulado.put("2", new Usuario("2", "Maria Santos", "maria@email.com"));
        bancoSimulado.put("3", new Usuario("3", "Pedro Costa", "pedro@email.com"));
    }

    public Usuario buscarPorId(String id) {
        Span span = Span.current();

        // Simula latência
        try {
            Thread.sleep(50 + random.nextInt(100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        span.setAttribute("db.query", "SELECT * FROM users WHERE id = ?");
        span.setAttribute("db.user_id", id);

        Usuario usuario = bancoSimulado.get(id);

        if (usuario == null) {
            String errorMsg = "Usuário não encontrado: " + id;
            logger.error(errorMsg);

            // Marca erro no span
            span.setAttribute("error", true);
            span.setAttribute("error.message", errorMsg);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, errorMsg);

            throw new RuntimeException(errorMsg);
        }

        span.setAttribute("user.found", true);
        return usuario;
    }

    public Usuario criarUsuario(String nome, String email) {
        Span span = Span.current();

        try {
            Thread.sleep(100);

            String id = String.valueOf(bancoSimulado.size() + 1);
            Usuario novo = new Usuario(id, nome, email);
            bancoSimulado.put(id, novo);

            span.setAttribute("user.created", true);
            span.setAttribute("user.id", id);

            return novo;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            span.setAttribute("error", true);
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, e.getMessage());
            throw new RuntimeException("Erro ao criar usuário", e);
        }
    }

    public Map<String, Usuario> listarTodos() {
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return bancoSimulado;
    }

    public void deletarPorId(String id) {
        Span span = Span.current();

        try {
            Thread.sleep(10);
            bancoSimulado.remove(id);
            span.setAttribute("user.deleted", true);
            span.setAttribute("user.id", id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            span.setAttribute("error", true);
            span.setAttribute("error.message", e.getMessage());
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }
}
