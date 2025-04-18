package com.fiap.safeville.service;

import com.fiap.safeville.model.Crime;
import com.fiap.safeville.repository.CrimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrimeService {

    @Autowired
    private CrimeRepository repository;

    public List<Crime> listarTodos() {
        return repository.findAll();
    }

    public Optional<Crime> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Crime criar(Crime crime) {
        return repository.save(crime);
    }

    public Crime atualizar(Long id, Crime crime) {
        crime.setId(id);
        return repository.save(crime);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
