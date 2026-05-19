package com.example.livros.service;

import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LivroServiceTest {

    @Autowired
    private LivroService service;

    @Autowired
    private LivroRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Integração: deve adicionar livro com sucesso")
    void deveAdicionarLivroComSucesso() {
        Livro livro = new Livro(null, "Dom Casmurro", "Machado de Assis", null);
        Livro salvo = service.adicionar(livro);

        assertNotNull(salvo.getId());
        assertEquals("Dom Casmurro", salvo.getTitulo());
        assertEquals("Machado de Assis", salvo.getAutor());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: deve listar todos os livros")
    void deveListarTodosOsLivros() {
        service.adicionar(new Livro(null, "Livro A", "Autor A", null));
        service.adicionar(new Livro(null, "Livro B", "Autor B", null));

        List<Livro> livros = service.listarTodos();
        assertEquals(2, livros.size());
    }

    @Test
    @Order(3)
    @DisplayName("Integração: deve editar livro com sucesso")
    void deveEditarLivroComSucesso() {
        Livro salvo = service.adicionar(new Livro(null, "Titulo Original", "Autor Original", null));
        Livro novo = new Livro(null, "Titulo Atualizado", "Autor Atualizado", "http://capa.jpg");

        Livro resultado = service.editar(salvo.getId(), novo);

        assertEquals("Titulo Atualizado", resultado.getTitulo());
        assertEquals("Autor Atualizado", resultado.getAutor());
    }

    @Test
    @Order(4)
    @DisplayName("Integração: deve apagar livro com sucesso")
    void deveApagarLivroComSucesso() {
        Livro salvo = service.adicionar(new Livro(null, "Para Apagar", "Autor X", null));
        assertDoesNotThrow(() -> service.apagar(salvo.getId()));
        assertEquals(0, repository.count());
    }

    @Test
    @Order(5)
    @DisplayName("Caixa Branca: editar livro inexistente lança exceção")
    void deveLancarExcecaoAoEditarLivroInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.editar(999L, new Livro(null, "X", "Y", null)));
        assertEquals("Livro não encontrado", ex.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Caixa Branca: apagar livro inexistente lança exceção")
    void deveLancarExcecaoAoApagarLivroInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.apagar(999L));
        assertEquals("Livro não encontrado", ex.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Caixa Branca: listar retorna lista vazia quando banco vazio")
    void deveRetornarListaVaziaQuandoNaoHaLivros() {
        List<Livro> livros = service.listarTodos();
        assertNotNull(livros);
        assertTrue(livros.isEmpty());
    }

    @ParameterizedTest(name = "[{index}] Adicionar: {0} de {1}")
    @CsvSource({
            "O Pequeno Príncipe, Antoine de Saint-Exupéry, http://img.com/1.jpg",
            "1984, George Orwell, http://img.com/2.jpg",
            "A Revolução dos Bichos, George Orwell, ''",
            "Sapiens, Yuval Noah Harari, http://img.com/4.jpg",
            "O Alquimista, Paulo Coelho, ''"
    })
    @Order(8)
    @DisplayName("Parametrizado: deve adicionar livros com dados variados")
    void deveAdicionarVariosLivros(String titulo, String autor, String urlImagem) {
        String url = (urlImagem == null || urlImagem.isBlank()) ? null : urlImagem;
        Livro salvo = service.adicionar(new Livro(null, titulo, autor, url));

        assertNotNull(salvo.getId());
        assertEquals(titulo, salvo.getTitulo());
    }

    @ParameterizedTest(name = "[{index}] Editar titulo para: {1}")
    @CsvSource({
            "Titulo A, Titulo Editado A",
            "Titulo B, Titulo Editado B",
            "Titulo C, Titulo Editado C"
    })
    @Order(9)
    @DisplayName("Parametrizado: deve editar títulos com dados variados")
    void deveEditarTituloComDadosDiferentes(String tituloOriginal, String tituloNovo) {
        Livro salvo = service.adicionar(new Livro(null, tituloOriginal, "Autor", null));
        Livro resultado = service.editar(salvo.getId(),
                new Livro(null, tituloNovo, "Autor", null));
        assertEquals(tituloNovo, resultado.getTitulo());
    }
}