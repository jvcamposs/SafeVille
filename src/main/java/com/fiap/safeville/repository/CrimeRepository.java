package com.fiap.safeville.repository;

import com.fiap.safeville.model.Crime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrimeRepository extends JpaRepository<Crime, Long> {}
