package com.example.livros.service;

import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @InjectMocks
    private LivroService service;

    private Livro livroMock;

    @BeforeEach
    void setUp() {
        // 👇 Adicionado um link fictício como 4º parâmetro
        livroMock = new Livro(1L, "O Senhor dos Anéis", "J.R.R. Tolkien", "http://imagem.com/capa.jpg");
    }

    @Test
    void deveAdicionarLivroComSucesso() {
        when(repository.save(any(Livro.class))).thenReturn(livroMock);

        // 👇 Adicionado null como 4º parâmetro
        Livro salvo = service.adicionar(new Livro(null, "O Senhor dos Anéis", "J.R.R. Tolkien", null));

        assertNotNull(salvo.getId());
        assertEquals("O Senhor dos Anéis", salvo.getTitulo());
        verify(repository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveEditarLivroComSucesso() {
        // 👇 Adicionado um link fictício como 4º parâmetro
        Livro livroAtualizado = new Livro(1L, "O Hobbit", "J.R.R. Tolkien", "http://imagem.com/hobbit.jpg");
        
        when(repository.findById(1L)).thenReturn(Optional.of(livroMock));
        when(repository.save(any(Livro.class))).thenReturn(livroAtualizado);

        Livro resultado = service.editar(1L, livroAtualizado);

        assertEquals("O Hobbit", resultado.getTitulo());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Livro.class));
    }

    @Test
    void deveApagarLivroComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.apagar(1L));

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}