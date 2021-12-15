package com.carrafasoft.bsuldo.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Permissao;
import com.carrafasoft.bsuldo.api.model.UsuarioPermissao;
import com.carrafasoft.bsuldo.api.repository.PermissaoRepository;
import com.carrafasoft.bsuldo.api.repository.UsuarioPermissaoRepository;
import com.carrafasoft.bsuldo.api.repository.UsuarioRepository;

@Service
public class UsuarioPermissaoService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	
	public List<Permissao> retornaPermissoesDisponiveis(Long codigo) {
		
		
		List<UsuarioPermissao> userPermissaoSalvo = usuarioPermissaoRepository.pesquisaPermissoesDoUsuario(codigo);
		List<Permissao> permissoesList = permissaoRepository.findAll();
		
		int qtdUserpermition =  userPermissaoSalvo.size();
		int qtdPermissoes = permissoesList.size();
		
		List<Permissao> permissoesDisponiveis = new ArrayList<Permissao>();
		
		if(qtdUserpermition == 0) {
			
			//Mostrar todas as permissões disponíveis
			permissoesDisponiveis = permissoesList;
		}
		
		/*Separa os permissões que o usuario tem das que ele não tem e retorna somente o que ele tem disponivel para cadastrar**/
		if(qtdUserpermition > 0 && qtdUserpermition < qtdPermissoes) {
			
			permissoesDisponiveis = permissoesList;
			
			for (int i = 0; i < permissoesList.size(); i++) {
				
				Long codigoPermissao = permissoesList.get(i).getPermissaoId();
				//System.out.println("codigoPermissao: " + codigoPermissao);
				
				for (int j = 0; j < userPermissaoSalvo.size(); j++) {
					
					if(userPermissaoSalvo.get(j).getPermissaoId().equals(codigoPermissao)) {
						
						permissoesDisponiveis.remove(permissoesList.get(i));
					}
					
				}
				
			}
		}	
		
		//System.out.println(qtdPermissoes + " - " + permissoesDisponiveis.size());
		
		
		return permissoesDisponiveis;
	}
	
	
	public void cadastrarPermissoesUsuario(List<Permissao> listaRecebida, String codigousuario) {
		
		Long idUsuario = Long.parseLong(codigousuario);
		
		List<UsuarioPermissao> userPermissaoSalvo = usuarioPermissaoRepository.pesquisaPermissoesDoUsuario(idUsuario);
		List<Permissao> permissoesList = permissaoRepository.findAll();
		
		int qtdListarecebida = listaRecebida.size();
		int qtdPermissoes = permissoesList.size();
		

		if(qtdListarecebida == 0) {
			
			//TODO deletar todas as permissões
		}
		
		if(qtdListarecebida < userPermissaoSalvo.size()) {
			
			//TODO verificar qual permissão deve ser removida
		}
		
		if(qtdListarecebida > userPermissaoSalvo.size()) {
			
			//TODO verificar qual permissão deve ser adicionada
		}
	}

}
