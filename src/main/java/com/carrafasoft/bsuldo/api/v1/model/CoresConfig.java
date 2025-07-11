package com.carrafasoft.bsuldo.api.v1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Entity
@Table(name = "cores_config")
public class CoresConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cores_config_id")
	private Long coresConfigId;
	
	@NotNull
	@Column(name = "cor_principal", length = 10)
	private String corPrincipal;
	
	@NotNull
	@Column(name = "cor_secundaria", length = 10)
	private String corSecundaria;

	@NotNull
	@Column(name = "usar_cores_padrao")
	private Boolean usarCoresPadrao;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoas pessoa;
	
	@PrePersist
	public void aoCadastrar() {
		toUpperCase();
	}
	
	@PreUpdate
	public void aoAtualizar() {
		toUpperCase();
	}
	
	private void toUpperCase() {
		
		if(StringUtils.hasLength(corPrincipal) && StringUtils.hasLength(corSecundaria)) {
			corPrincipal = corPrincipal.toUpperCase().trim();
			corSecundaria = corSecundaria.toUpperCase().trim();
		}
	}

}
