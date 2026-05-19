package com.example.livros.controller;

import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LivroControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/livros → 200 OK lista vazia")
    void deveRetornar200EListaVazia() {
        ResponseEntity<Livro[]> response =
                restTemplate.getForEntity("/api/livros", Livro[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/livros → 200 OK com livro criado")
    void deveCriarLivro() {
        Livro payload = new Livro(null, "Clean Code", "Robert Martin", null);
        ResponseEntity<Livro> response =
                restTemplate.postForEntity("/api/livros", payload, Livro.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("Clean Code", response.getBody().getTitulo());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/livros → lista com livros após inserção")
    void deveListarLivrosAposInsercao() {
        restTemplate.postForEntity("/api/livros",
                new Livro(null, "Livro 1", "Autor 1", null), Livro.class);
        restTemplate.postForEntity("/api/livros",
                new Livro(null, "Livro 2", "Autor 2", null), Livro.class);

        ResponseEntity<Livro[]> response =
                restTemplate.getForEntity("/api/livros", Livro[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/livros/{id} → 200 OK com livro atualizado")
    void deveEditarLivro() {
        Livro criado = restTemplate.postForEntity("/api/livros",
                new Livro(null, "Original", "Autor", null), Livro.class).getBody();

        HttpEntity<Livro> request = new HttpEntity<>(
                new Livro(null, "Atualizado", "Novo Autor", null));
        ResponseEntity<Livro> response = restTemplate.exchange(
                "/api/livros/" + criado.getId(),
                HttpMethod.PUT, request, Livro.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Atualizado", response.getBody().getTitulo());
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/livros/{id} → 204 No Content")
    void deveApagarLivro() {
        Livro criado = restTemplate.postForEntity("/api/livros",
                new Livro(null, "Para Deletar", "Autor", null), Livro.class).getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/livros/" + criado.getId(),
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/livros/99999 → 500 livro inexistente")
    void deveRetornarErroAoEditarInexistente() {
        HttpEntity<Livro> request = new HttpEntity<>(new Livro(null, "X", "Y", null));
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/livros/99999", HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /api/livros/99999 → 500 livro inexistente")
    void deveRetornarErroAoDeletarInexistente() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/livros/99999", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}