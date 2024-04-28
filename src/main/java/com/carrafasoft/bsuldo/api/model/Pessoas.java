package com.carrafasoft.bsuldo.api.model;

import com.carrafasoft.bsuldo.api.utils.FuncoesUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "pessoas")
public class Pessoas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pessoa_id")
	private Long pessoaID;

	@JsonIgnore
	@Column(name = "pessoa_id_token", updatable = false)
	private String pessoIdToken;

	@NotNull
	@Column(name = "nome_pessoa", length = 255)
	private String nomePessoa;

	@Column(name = "data_cadastro", updatable = false)
	private LocalDate dataCadastro;

	@NotNull
	@Email
	@Column(name = "email", length = 150)
	private String email;

	@PrePersist
	public void aoCadastrarPessoa() {

		dataCadastro = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
		toUpperCase();
		pessoIdToken = FuncoesUtils.gerarUUID();
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomePessoa = nomePessoa.toUpperCase();
	}

}
