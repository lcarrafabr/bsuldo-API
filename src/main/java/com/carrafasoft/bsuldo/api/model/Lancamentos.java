package com.carrafasoft.bsuldo.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import javax.validation.constraints.NotNull;

import com.carrafasoft.bsuldo.api.enums.SituacaoEnum;

@Entity
@Table(name = "lancamentos")
public class Lancamentos {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lancamento_id")
	private Long lancamentoId;

	@NotNull
	private BigDecimal valor;

	@NotNull
	@Column(name = "data_vencimento")
	private LocalDate datavencimento;

	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;

	@NotNull
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
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoas pessoa;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categorias categoria;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "metodo_de_cobranca_id")
	private MetodoDeCobranca metodoDeCobranca;

	public Long getLancamentoId() {
		return lancamentoId;
	}

	public void setLancamentoId(Long lancamentoId) {
		this.lancamentoId = lancamentoId;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDate getDatavencimento() {
		return datavencimento;
	}

	public void setDatavencimento(LocalDate datavencimento) {
		this.datavencimento = datavencimento;
	}

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public SituacaoEnum getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoEnum situacao) {
		this.situacao = situacao;
	}

	public Boolean getParcelado() {
		return parcelado;
	}

	public void setParcelado(Boolean parcelado) {
		this.parcelado = parcelado;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public Integer getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public String getChavePesquisa() {
		return chavePesquisa;
	}

	public void setChavePesquisa(String chavePesquisa) {
		this.chavePesquisa = chavePesquisa;
	}

	public Pessoas getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoas pessoa) {
		this.pessoa = pessoa;
	}

	public Categorias getCategoria() {
		return categoria;
	}

	public void setCategoria(Categorias categoria) {
		this.categoria = categoria;
	}

	public MetodoDeCobranca getMetodoDeCobranca() {
		return metodoDeCobranca;
	}

	public void setMetodoDeCobranca(MetodoDeCobranca metodoDeCobranca) {
		this.metodoDeCobranca = metodoDeCobranca;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lancamentoId == null) ? 0 : lancamentoId.hashCode());
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
		Lancamentos other = (Lancamentos) obj;
		if (lancamentoId == null) {
			if (other.lancamentoId != null)
				return false;
		} else if (!lancamentoId.equals(other.lancamentoId))
			return false;
		return true;
	}

	@PrePersist
	public void aoCadastrar() {

		toUpperCase();
		if(SituacaoEnum.PAGO == situacao) {
			situacao = SituacaoEnum.PAGO;
		} else if(SituacaoEnum.VENCIDO == situacao) {
			situacao = SituacaoEnum.VENCIDO;
		} else {
			situacao = SituacaoEnum.PENDENTE;
		}
		
	}

	@PreUpdate
	public void aoAtualizar() {

		toUpperCase();
	}

	private void toUpperCase() {

		descricao = descricao.trim().toUpperCase();
	}

}
