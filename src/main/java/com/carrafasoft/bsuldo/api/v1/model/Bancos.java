package com.carrafasoft.bsuldo.api.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bancos")
public class Bancos {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banco_id")
    private Long bancoId;

    @Column(name = "codigo_banco", length = 36)
    private String codigoBanco;
    @NotBlank
    @Column(name = "nome_banco", length = 50)
    private String nomeBanco;

    private Boolean status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    @PrePersist
    public void aoCadastrar() {

        toUppercase();
        setStatus(true);
        setCodigoBanco(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void aoAtualizar() {

        toUppercase();
    }

    private void toUppercase() {

        if(StringUtils.hasLength(nomeBanco)) {
            nomeBanco = nomeBanco.trim().toUpperCase();
        }
    }

}


