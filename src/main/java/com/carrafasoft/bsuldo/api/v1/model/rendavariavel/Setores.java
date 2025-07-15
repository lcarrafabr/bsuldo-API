package com.carrafasoft.bsuldo.api.v1.model.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "setores")
public class Setores {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setor_id")
    private Long setorId;

    @Column(name = "codigo_setor", length = 36)
    private String codigoSetor;

    @NotNull
    @Column(name = "nome_setor", length = 150)
    private String nomeSetor;

    private Boolean status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @PrePersist
    public void aoCadastrar() {
        toUpperCase();
        setCodigoSetor(UUID.randomUUID().toString());
        setStatus(true);
    }

    @PreUpdate
    public void aoAtualizar() {
        toUpperCase();
    }

    private void toUpperCase() {

        if(StringUtils.hasLength(nomeSetor)) {
            nomeSetor = nomeSetor.toUpperCase().trim();
        }
    }
}
