package com.carrafasoft.bsuldo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Pessoas;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoas, Long>{

}
