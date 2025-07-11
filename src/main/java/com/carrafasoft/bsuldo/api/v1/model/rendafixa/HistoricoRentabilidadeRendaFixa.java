package com.carrafasoft.bsuldo.api.v1.model.rendafixa;

import com.carrafasoft.bsuldo.api.v1.model.Pessoas;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "historico_rentabilidade_rf")
public class HistoricoRentabilidadeRendaFixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_rentabildiade_RF_id")
    private Long histRentabilidadeRFId;

    @NotNull
    @Column(name = "data_rentabilidade")
    private LocalDate dataRentabilidade;

    @Column(name = "dia_da_semana")
    private String diaDaSemana;

    @NotNull
    @Column(name = "valor_resgate_app")
    private BigDecimal valorResgateApp;

    private BigDecimal rendimento; //atualizado automaticamente pela API

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoas pessoa;

    public Long getHistRentabilidadeRFId() {
        return histRentabilidadeRFId;
    }

    public void setHistRentabilidadeRFId(Long histRentabilidadeRFId) {
        this.histRentabilidadeRFId = histRentabilidadeRFId;
    }

    public LocalDate getDataRentabilidade() {
        return dataRentabilidade;
    }

    public void setDataRentabilidade(LocalDate dataRentabilidade) {
        this.dataRentabilidade = dataRentabilidade;
    }

    public String getDiaDaSemana() {
        return diaDaSemana;
    }

    public void setDiaDaSemana(String diaDaSemana) {
        this.diaDaSemana = diaDaSemana;
    }

    public BigDecimal getValorResgateApp() {
        return valorResgateApp;
    }

    public void setValorResgateApp(BigDecimal valorResgateApp) {
        this.valorResgateApp = valorResgateApp;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }

    public Pessoas getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoas pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoricoRentabilidadeRendaFixa that = (HistoricoRentabilidadeRendaFixa) o;

        return histRentabilidadeRFId.equals(that.histRentabilidadeRFId);
    }

    @Override
    public int hashCode() {
        return histRentabilidadeRFId.hashCode();
    }
}
