package com.nubons.nnpmailservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nubons.nnpmailservice.entity.Email;

public interface EmailRepo extends JpaRepository<Email, Long> {

	Email findByEmailId(long email_id);

}
