package com.example.livros.controller;

import com.example.livros.model.Livro;
import com.example.livros.service.LivroService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Livro> listar() {
        return service.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Livro> adicionar(@RequestBody Livro livro) {
        return ResponseEntity.ok(service.adicionar(livro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> editar(@PathVariable Long id, @RequestBody Livro livro) {
        return ResponseEntity.ok(service.editar(id, livro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}