package com.fiap.safeville.controller;

import com.fiap.safeville.model.Crime;
import com.fiap.safeville.service.CrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/crimes")
public class CrimeApiController {

    @Autowired
    private CrimeService service;

    @GetMapping
    public List<Crime> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Crime> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Crime criar(@RequestBody Crime crime) {
        return service.criar(crime);
    }

    @PutMapping("/{id}")
    public Crime atualizar(@PathVariable Long id, @RequestBody Crime crime) {
        return service.atualizar(id, crime);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
