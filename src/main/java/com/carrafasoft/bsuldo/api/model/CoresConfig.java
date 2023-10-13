package com.carrafasoft.bsuldo.api.model;

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

import org.springframework.util.StringUtils;

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
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoas pessoa;

	public Pessoas getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoas pessoa) {
		this.pessoa = pessoa;
	}

	public Long getCoresConfigId() {
		return coresConfigId;
	}

	public void setCoresConfigId(Long coresConfigId) {
		this.coresConfigId = coresConfigId;
	}

	public String getCorPrincipal() {
		return corPrincipal;
	}

	public void setCorPrincipal(String corPrincipal) {
		this.corPrincipal = corPrincipal;
	}

	public String getCorSecundaria() {
		return corSecundaria;
	}

	public void setCorSecundaria(String corSecundaria) {
		this.corSecundaria = corSecundaria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coresConfigId == null) ? 0 : coresConfigId.hashCode());
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
		CoresConfig other = (CoresConfig) obj;
		if (coresConfigId == null) {
			if (other.coresConfigId != null)
				return false;
		} else if (!coresConfigId.equals(other.coresConfigId))
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
		
		if(StringUtils.hasLength(corPrincipal) && StringUtils.hasLength(corSecundaria)) {
			corPrincipal = corPrincipal.toUpperCase().trim();
			corSecundaria = corSecundaria.toUpperCase().trim();
		}
	}

}
