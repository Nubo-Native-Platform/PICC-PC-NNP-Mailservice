package com.nubons.nnpmailservice.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nnp_email")
public class Email {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "email_id")
	private Long emailId ;
	@NotNull
	@Column(name = "email_from")
	private String from;
	private String fromName;
	@NotNull
	@Column(name = "email_to")
	private String to;
	private String toName;
	@Column(name = "email_cc")
	private String cc;
	@Column(name = "email_cc_name")
	private String ccName;
	@NotNull
	private String subject;
	private String text ;
	private String templateName ;
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private String templateDataMap ;
	
	@OneToMany(mappedBy = "email", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();
	
	
	private String emailSendStatus ;
	
	private Date lastUpdatedDate ;
}
