package com.carrafasoft.bsuldo.api.model.rendavariavel;

import com.carrafasoft.bsuldo.api.model.Pessoas;
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
@Table(name = "segmentos")
public class Segmentos {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "segmento_id")
    private Long segmentoId;

    @Column(name = "codigo_segmento", length = 36)
    private String codigoSegmento;

    @NotNull
    @Column(name = "nome_segmento", length = 100)
    private String nomeSegmento;

    private Boolean status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @PrePersist
    public void aoCadastrar() {
        toUpperCase();
        setCodigoSegmento(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void aoAtualizar() {
        toUpperCase();
    }

    private void toUpperCase() {

        if(StringUtils.hasLength(nomeSegmento)) {
            nomeSegmento = nomeSegmento.toUpperCase().trim();
        }
    }
}
