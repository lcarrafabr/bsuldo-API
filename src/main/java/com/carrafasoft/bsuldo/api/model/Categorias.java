package com.carrafasoft.bsuldo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter

@Entity
@Table(name = "categorias")
public class Categorias {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoria_id")
	private Long categoriaId;

	@NotNull
	@Column(name = "nome_categoria", length = 100)
	private String nomeCategoria;

	private Boolean status;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoas pessoa;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoriaId == null) ? 0 : categoriaId.hashCode());
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
		Categorias other = (Categorias) obj;
		if (categoriaId == null) {
			if (other.categoriaId != null)
				return false;
		} else if (!categoriaId.equals(other.categoriaId))
			return false;
		return true;
	}

	@PrePersist
	public void aoCadastrar() {

		status = true;
		toUpperCase();
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomeCategoria = nomeCategoria.trim().toUpperCase();
	}

}
