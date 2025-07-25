package com.carrafasoft.bsuldo.api.v1.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.carrafasoft.bsuldo.api.v1.enums.SituacaoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoLancamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "lancamentos")
public class Lancamentos {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lancamento_id")
	private Long lancamentoId;

	@Column(name = "codigo_lancamento", length = 36)
	private String codigoLancamento;

	@NotNull
	@PositiveOrZero
	private BigDecimal valor;

	@NotNull
	@Column(name = "data_vencimento")
	private LocalDate datavencimento;

	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;

	@NotBlank
	@Column(length = 200)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private SituacaoEnum situacao;

	@NotNull
	private Boolean parcelado;

	@NotNull
	@Column(name = "quantidade_de_parcelas")
	private Integer quantidadeParcelas;

	@NotNull
	@Column(name = "numero_parcela")
	private Integer numeroParcela;

	@Column(name = "chave_pesquisa", length = 20, updatable = false)
	private String chavePesquisa;

	@NotNull
	@Column(name = "lancamento_recorrente")
	private Boolean lancRecorrente;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private TipoLancamento tipoLancamento;


	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoas pessoa;


	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categorias categoria;


	@ManyToOne
	@JoinColumn(name = "metodo_de_cobranca_id")
	private MetodoDeCobranca metodoDeCobranca;

	@ManyToOne
	@JoinColumn(name = "banco_id")
	private Bancos banco;

	@PrePersist
	public void aoCadastrar() {

		toUpperCase();
		if (SituacaoEnum.PAGO == situacao) {
			situacao = SituacaoEnum.PAGO;
		} else if (SituacaoEnum.VENCIDO == situacao) {
			situacao = SituacaoEnum.VENCIDO;
		} else if(SituacaoEnum.RECEBIDO == situacao) {
			situacao = SituacaoEnum.RECEBIDO;
		} else {
			situacao = SituacaoEnum.PENDENTE;
		}
		setCodigoLancamento(UUID.randomUUID().toString());

	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		descricao = descricao.trim().toUpperCase();
	}

}
