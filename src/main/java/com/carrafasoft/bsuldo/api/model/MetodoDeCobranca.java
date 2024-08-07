package com.carrafasoft.bsuldo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
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

		status = true;
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
