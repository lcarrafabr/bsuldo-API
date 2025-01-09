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

	private static final String CATEGORIA_EM_USO = "A categoria de código %s não pode ser removida, pois está em uso.";
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private PessoaService pessoaService;
	
	@Transactional
	public Categorias atualizaCategoria(String codigo, Categorias categoria,String tokenId) {
		
		Categorias categoriaSalva = verificaCategoriaExistente(codigo, tokenId);
		BeanUtils.copyProperties(categoria, categoriaSalva, "categoriaId");
		return categoriaRepository.save(categoriaSalva);
	}

	@Transactional
	public void remover(String codigo) {

		try {
			categoriaRepository.deleteByCodigoCategoria(codigo);
		} catch (EmptyResultDataAccessException e) {
			throw new CategoriaNaoEncontradaException(codigo);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(String.format(CATEGORIA_EM_USO, codigo));
		}
	}

	@Transactional
	public void atualizarStatusAtivo(String codigo, Boolean ativo) {
		
		Categorias categoriaSalva = buscaPorCodigoUUID(codigo);
		categoriaSalva.setStatus(ativo);

		/**Como tem a anotação @Transactional. Qualquer alteração será atualizado quando o @Transactionl finalizar o gerenciamento
		 * Seendo assim não precisa chaamr o repository.save()**/
		//categoriaRepository.save(categoriaSalva);

	}
	
	public Categorias buscaPorId(Long codigo) {

		return categoriaRepository.findById(codigo).orElseThrow(() -> new CategoriaNaoEncontradaException(codigo.toString()));
	}

	public Categorias buscaPorCodigoUUID(String codigo) {

		return categoriaRepository.findByCodigoCategoria(codigo).orElseThrow(() -> new CategoriaNaoEncontradaException(codigo));
	}

	public Categorias verificaCategoriaExistente(String codigo, String tokenId) {

		return  categoriaRepository.findByIdAndPessoaId(codigo, pessoaService.recuperaIdPessoaByToken(tokenId))
				.orElseThrow(() -> new CategoriaNaoEncontradaException(codigo));
	}
}
