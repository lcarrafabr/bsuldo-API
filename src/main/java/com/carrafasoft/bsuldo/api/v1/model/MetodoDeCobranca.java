package com.carrafasoft.bsuldo.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "metodo_de_cobranca")
public class MetodoDeCobranca {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metodo_de_cobranca_id")
	private Long metodoCobrancaId;

	@Column(name = "codigo_metodo_cobranca", length = 36)
	private String codigoMetodoCobranca;

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

	@PrePersist
	public void aoCadastrar() {

		status = true;
		toUpperCase();
		setCodigoMetodoCobranca(UUID.randomUUID().toString());
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomeMetodoCob = nomeMetodoCob.toUpperCase();
	}

}
