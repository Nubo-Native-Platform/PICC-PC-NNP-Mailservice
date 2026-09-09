package com.nubons.nnpmailservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nubons.nnpmailservice.entity.EmailAttachment;

public interface EmailAttachmentRepo extends JpaRepository<EmailAttachment, Long> {

}
