package com.example.simactivation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.simactivation.entity.SimActivation;

@Repository
public interface SimActivationRepository extends JpaRepository<SimActivation, Long> {
}