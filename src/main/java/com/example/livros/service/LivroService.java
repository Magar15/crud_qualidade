package com.example.livros.service;

// 👇 Estas são as duas linhas que estavam faltando!
import com.example.livros.model.Livro;
import com.example.livros.repository.LivroRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro adicionar(Livro livro) {
        return repository.save(livro);
    }

    public List<Livro> listarTodos() {
        return repository.findAll();
    }

    public Livro editar(Long id, Livro livroAtualizado) {
        return repository.findById(id).map(livro -> {
            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setUrlImagem(livroAtualizado.getUrlImagem()); // 📍 Linha adicionada
            return repository.save(livro);
        }).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    public void apagar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado");
        }
        repository.deleteById(id);
    }
}