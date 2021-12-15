package com.carrafasoft.bsuldo.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	
	public Categorias atualizaCategoria(Long codigo, Categorias categoria) {
		
		Categorias categoriaSalva = buscaPorId(codigo);
		BeanUtils.copyProperties(categoria, categoriaSalva, "categoriaId");
		return categoriaRepository.save(categoriaSalva);
	}
	
	public void atualizarStatusAtivo(Long codigo, Boolean ativo) {
		
		Categorias categoriaSalva = buscaPorId(codigo);
		categoriaSalva.setStatus(ativo);
		categoriaRepository.save(categoriaSalva);
	}
	
	private Categorias buscaPorId(Long codigo) {
		
		Categorias categoriaSalva = categoriaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		return categoriaSalva;
	}

}
