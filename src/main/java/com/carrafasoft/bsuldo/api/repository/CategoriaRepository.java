package com.carrafasoft.bsuldo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Categorias;

@Repository
public interface CategoriaRepository extends JpaRepository<Categorias, Long>{

}
