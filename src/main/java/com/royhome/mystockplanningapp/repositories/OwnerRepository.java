package com.royhome.mystockplanningapp.repositories;

import com.royhome.mystockplanningapp.models.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    public Optional<Owner> findOwnerByName(String name);
}
