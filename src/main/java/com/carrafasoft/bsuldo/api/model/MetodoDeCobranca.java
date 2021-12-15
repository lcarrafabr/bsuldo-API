package com.carrafasoft.bsuldo.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "metodo_de_cobranca")
public class MetodoDeCobranca {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metodo_de_cobranca_id")
	private Long metodoCobrancaId;

	@NotNull
	@Column(name = "nome_metodo_cobranca", length = 100)
	private String nomeMetodoCob;

	@NotNull
	private Boolean status;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	public Long getMetodoCobrancaId() {
		return metodoCobrancaId;
	}

	public void setMetodoCobrancaId(Long metodoCobrancaId) {
		this.metodoCobrancaId = metodoCobrancaId;
	}

	public String getNomeMetodoCob() {
		return nomeMetodoCob;
	}

	public void setNomeMetodoCob(String nomeMetodoCob) {
		this.nomeMetodoCob = nomeMetodoCob;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metodoCobrancaId == null) ? 0 : metodoCobrancaId.hashCode());
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
		MetodoDeCobranca other = (MetodoDeCobranca) obj;
		if (metodoCobrancaId == null) {
			if (other.metodoCobrancaId != null)
				return false;
		} else if (!metodoCobrancaId.equals(other.metodoCobrancaId))
			return false;
		return true;
	}

	@PrePersist
	public void aoCadastrar() {

		toUpperCase();
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomeMetodoCob = nomeMetodoCob.toUpperCase();
	}

}
