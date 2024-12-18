package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.model.exceptionmodel.CategoriaNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.entidadeException.EntidadeEmUsoException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

	private static final String CATEGORIA_EM_USO = "A categoria de código %d não pode ser removida, pois está em uso.";
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Transactional
	public Categorias atualizaCategoria(Long codigo, Categorias categoria) {
		
		Categorias categoriaSalva = buscaPorId(codigo);
		BeanUtils.copyProperties(categoria, categoriaSalva, "categoriaId");
		return categoriaRepository.save(categoriaSalva);
	}

	@Transactional
	public void remover(Long codigo) {

		try {
			categoriaRepository.deleteById(codigo);
		} catch (EmptyResultDataAccessException e) {
			throw new CategoriaNaoEncontradaException(codigo);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(CATEGORIA_EM_USO, codigo));
		}
	}

	@Transactional
	public void atualizarStatusAtivo(Long codigo, Boolean ativo) {
		
		Categorias categoriaSalva = buscaPorId(codigo);
		categoriaSalva.setStatus(ativo);

		/**Como tem a anotação @Transactional. Qualquer alteração será atualizado quando o @Transactionl finalizar o gerenciamento
		 * Seendo assim não precisa chaamr o repository.save()**/
		//categoriaRepository.save(categoriaSalva);

	}
	
	public Categorias buscaPorId(Long codigo) {

		return categoriaRepository.findById(codigo).orElseThrow(() -> new CategoriaNaoEncontradaException(codigo));
	}
}
