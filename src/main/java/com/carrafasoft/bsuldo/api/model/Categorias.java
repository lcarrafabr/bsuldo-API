package com.carrafasoft.bsuldo.api.model;

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
@Table(name = "categorias")
public class Categorias {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "categoria_id")
	private Long categoriaId;

	@Column(name = "codigo_categoria", length = 36)
	private String codigoCategoria;

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

	@PrePersist
	public void aoCadastrar() {

		status = true;
		toUpperCase();
		setCodigoCategoria(UUID.randomUUID().toString());
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		nomeCategoria = nomeCategoria.trim().toUpperCase();
	}

}
