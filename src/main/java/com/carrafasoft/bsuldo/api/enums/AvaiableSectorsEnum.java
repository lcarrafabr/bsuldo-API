package com.carrafasoft.bsuldo.api.enums;

import org.apache.commons.lang3.StringUtils;

interface AvaiableSectors{String obterSetor();}
public enum AvaiableSectorsEnum implements AvaiableSectors{

    Retail_Trade("Retail Trade") {
        @Override
        public String obterSetor() {
            return "Comercio de varejo";
        }
    },
    Energy_Minerals("Energy Minerals") {
        @Override
        public String obterSetor() {
            return "Energia Minerais";
        }
    },
    Health_Services("Health Services") {
        @Override
        public String obterSetor() {
            return "Serviços de saúde";
        }
    },
    Utilities("Utilities") {
        @Override
        public String obterSetor() {
            return "Util. pública";
        }
    },
    Finance("Finance") {
        @Override
        public String obterSetor() {
            return "Finança";
        }
    },
    Consumer_Services("Consumer Services") {
        @Override
        public String obterSetor() {
            return "Serviços do Consumidor";
        }
    },
    Consumer_Non_Durables("Consumer Non-Durables"){
        @Override
        public String obterSetor() {
            return "Bens de cons. não duráveis";
        }
    },
    Non_Energy_Minerals("Non-Energy Minerals") {
        @Override
        public String obterSetor() {
            return "Minerais não energéticos";
        }
    },
    Commercial_Services("Commercial Services") {
        @Override
        public String obterSetor() {
            return "Serviços comerciais";
        }
    },
    Distribution_Services("Distribution Services") {
        @Override
        public String obterSetor() {
            return "Serviços de distribuição";
        }
    },
    Transportation("Transportation") {
        @Override
        public String obterSetor() {
            return "Transporte";
        }
    },
    Technology_Services("Technology Services") {
        @Override
        public String obterSetor() {
            return "Serviços de tecnologia";
        }
    },
    Process_Industries("Process Industries") {
        @Override
        public String obterSetor() {
            return "Bens industriais";
        }
    },
    Communications("Communications") {
        @Override
        public String obterSetor() {
            return "Comunicações";
        }
    },
    Producer_Manufacturing("Producer Manufacturing") {
        @Override
        public String obterSetor() {
            return "Bens industriais";
        }
    },
    Miscellaneous("Miscellaneous") {
        @Override
        public String obterSetor() {
            return "Outros";
        }
    },
    Electronic_Technology("Electronic Technology") {
        @Override
        public String obterSetor() {
            return "Tecnologia Eletrônica";
        }
    },
    Industrial_Services("Industrial Services") {
        @Override
        public String obterSetor() {
            return "Serviços Industriais";
        }
    },
    Health_Technology("Health Technology") {
        @Override
        public String obterSetor() {
            return "Tecnologia em Saúde";
        }
    },
    Consumer_Durables("Consumer Durables") {
        @Override
        public String obterSetor() {
            return "Bens de consumo duráveis";
        }
    },
    Real_Estate("Real Estate") {
        @Override
        public String obterSetor() {
            return "Imobiliário";
        }
    },
    consumer_defensive("consumer defensive") {
        @Override
        public String obterSetor() {
            return "Defesa do consumidor";
        }
    },
    Technology("Technology") {
        @Override
        public String obterSetor() {
            return "Tecnologia";
        }
    };


    private final String descricao;

    AvaiableSectorsEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static String getSetorTraduzido(String sector) {

        for (AvaiableSectorsEnum tipo: values()) {

            if(StringUtils.equals(tipo.getDescricao(), sector)) {
                return tipo.obterSetor();
            }
        }

        return "NÃO ESPECIFICADO";
    }
}
