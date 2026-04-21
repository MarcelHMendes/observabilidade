package com.exemplo.controller;

import com.exemplo.model.Usuario;
import com.exemplo.service.UsuarioService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class ExemploController {

    private static final Logger logger = LoggerFactory.getLogger(ExemploController.class);

    @Autowired
    private UsuarioService usuarioService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @GetMapping("/hello")
    public String hello() {
        logger.info("Requisição recebida em /hello");
        return "Hello World com OpenTelemetry Tracing!";
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Usuario> getUser(@PathVariable String id) {
        logger.info("Buscando usuário: {}", id);

        // Obtém o span atual
        Span span = Span.current();

        if (id == null || id.isEmpty()) {
            logger.warn("ID inválido");
            span.setAttribute("error", true);
            span.setStatus(StatusCode.ERROR, "ID inválido");
            return ResponseEntity.badRequest().build();
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            logger.info("Usuário encontrado: {}", usuario.getNome());

            // Adiciona atributos de sucesso
            span.setAttribute("user.id", id);
            span.setAttribute("user.name", usuario.getNome());
            span.setStatus(StatusCode.OK);

            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário: {}", id, e);

            // Marca o span como erro
            span.setAttribute("error", true);
            span.setAttribute("error.message", e.getMessage());
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);

            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
        logger.info("Criando novo usuário: {}", usuario.getNome());

        Span span = Span.current();

        try {
            Usuario novo = usuarioService.criarUsuario(usuario.getNome(), usuario.getEmail());
            span.setAttribute("user.created", true);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(novo);
        } catch (Exception e) {
            span.setAttribute("error", true);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/users/{id}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String id) {
        logger.info("Buscando detalhes do usuário: {}", id);

        Span span = Span.current();

        try {
            Usuario usuario = usuarioService.buscarPorId(id);

            Map<String, Object> detalhes = new java.util.HashMap<>();
            detalhes.put("usuario", usuario);

            try {
                String externalUrl = "https://jsonplaceholder.typicode.com/users/" + id;
                String externalResponse = restTemplate.getForObject(externalUrl, String.class);
                detalhes.put("dadosExternos", externalResponse);
                span.setAttribute("external.call.success", true);
            } catch (Exception e) {
                logger.warn("Erro na chamada externa: {}", e.getMessage());
                detalhes.put("dadosExternos", "Não disponível");
                span.setAttribute("external.call.error", true);
                span.setAttribute("external.call.error.message", e.getMessage());
            }

            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(detalhes);

        } catch (Exception e) {
            span.setAttribute("error", true);
            span.setAttribute("error.message", e.getMessage());
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/slow")
    public String slowOperation() {
        int delay = 1000 + random.nextInt(2000);
        logger.info("Iniciando operação lenta ({}ms)", delay);

        Span span = Span.current();
        span.setAttribute("operation.delay_ms", delay);
        span.setAttribute("operation.type", "slow_operation");


        try {
            Thread.sleep(delay);
            logger.info("Operação lenta concluída");
            span.setStatus(StatusCode.OK);
            return "Operação concluída em " + delay + "ms";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            span.setAttribute("error", true);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            return "Operação interrompida";
        }
    }

    @GetMapping("/unstable")
    public String unstable() {
        Span span = Span.current();

        if (random.nextBoolean()) {
            logger.error("Erro aleatório!");
            String errorMsg = "Erro aleatório simulando falha";

            // Marca o span como erro
            span.setAttribute("error", true);
            span.setAttribute("error.type", "random_failure");
            span.setAttribute("error.message", errorMsg);
            span.setStatus(StatusCode.ERROR, errorMsg);
            span.recordException(new RuntimeException(errorMsg));

            throw new RuntimeException(errorMsg);
        }

        span.setStatus(StatusCode.OK);
        return "Sucesso!";
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        logger.info("Gerando dashboard");

        Span span = Span.current();

        Map<String, Object> dashboard = new java.util.HashMap<>();

        try {
            int totalUsuarios = usuarioService.listarTodos().size();
            dashboard.put("usuarios", totalUsuarios);
            dashboard.put("status", "online");
            dashboard.put("timestamp", System.currentTimeMillis());

            span.setAttribute("dashboard.users_count", totalUsuarios);
            span.setStatus(StatusCode.OK);

            // Simula processamento adicional
            Thread.sleep(200);

        } catch (Exception e) {
            span.setAttribute("error", true);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            dashboard.put("status", "error");
        }

        return dashboard;
    }
}
