package com.carrafasoft.bsuldo.api.model;

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

@Entity
@Table(name = "pessoas")
public class Pessoas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pessoa_id")
	private Long pessoaID;

	@NotNull
	@Column(name = "nome_pessoa", length = 255)
	private String nomePessoa;

	@Column(name = "data_cadastro", updatable = false)
	private LocalDate dataCadastro;

	@NotNull
	@Email
	@Column(name = "email", length = 150)
	private String email;

	public Long getPessoaID() {
		return pessoaID;
	}

	public void setPessoaID(Long pessoaID) {
		this.pessoaID = pessoaID;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public LocalDate getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDate dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pessoaID == null) ? 0 : pessoaID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoas other = (Pessoas) obj;
		if (pessoaID == null) {
			if (other.pessoaID != null)
				return false;
		} else if (!pessoaID.equals(other.pessoaID))
			return false;
		return true;
	}

	@PrePersist
	public void aoCadastrarPessoa() {

		dataCadastro = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
		toUpperCase();
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomePessoa = nomePessoa.toUpperCase();
	}

}
