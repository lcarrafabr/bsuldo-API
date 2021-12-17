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
@Table(name = "permissao")
public class Permissao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permissao_id")
	private Long permissaoId;

	@NotNull
	private String descricao;

	public Long getPermissaoId() {
		return permissaoId;
	}

	public void setPermissaoId(Long permissaoId) {
		this.permissaoId = permissaoId;
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
		result = prime * result + ((permissaoId == null) ? 0 : permissaoId.hashCode());
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
		Permissao other = (Permissao) obj;
		if (permissaoId == null) {
			if (other.permissaoId != null)
				return false;
		} else if (!permissaoId.equals(other.permissaoId))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "Permissao [permissaoId=" + permissaoId + ", descricao=" + descricao + "]";
	}

	@PrePersist
	public void aocadastrar() {

		formatarDescricao();
	}

	@PreUpdate
	public void aoAtualizar() {

		formatarDescricao();
	}

	private void formatarDescricao() {

		String role = "ROLE_";

		descricao = role + descricao.toUpperCase().trim();
	}

}
