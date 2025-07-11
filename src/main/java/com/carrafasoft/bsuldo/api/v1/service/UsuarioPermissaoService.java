package com.carrafasoft.bsuldo.api.v1.service;

import java.util.ArrayList;
import java.util.List;

import com.carrafasoft.bsuldo.api.v1.model.Permissao;
import com.carrafasoft.bsuldo.api.v1.model.UsuarioPermissao;
import com.carrafasoft.bsuldo.api.v1.repository.PermissaoRepository;
import com.carrafasoft.bsuldo.api.v1.repository.UsuarioPermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UsuarioPermissaoService {
	
	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;
	
	@Autowired
	private PermissaoRepository permissaoRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	public List<Permissao> retornaPermissoesDisponiveis(Long codigo) {
		
		
		List<UsuarioPermissao> userPermissaoSalvo = usuarioPermissaoRepository.pesquisaPermissoesDoUsuario(codigo);
		List<Permissao> permissoesList = permissaoRepository.findAll();
		
		int qtdUserpermition =  userPermissaoSalvo.size();
		int qtdPermissoes = permissoesList.size();
		
		List<Permissao> permissoesDisponiveis = new ArrayList<Permissao>();
		List<Permissao> listToRemoves = new ArrayList<Permissao>();
		
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
						
						//permissoesDisponiveis.remove(permissoesList.get(i));
						listToRemoves.add(permissoesList.get(i));
					}	
				}				
			}
			
			permissoesDisponiveis.removeAll(listToRemoves);
		}	
		
		//System.out.println(qtdPermissoes + " - " + permissoesDisponiveis.size());		
		return permissoesDisponiveis;
	}
	
	
	public String gerenciarPermissoesUsuario(List<Permissao> listaRecebida, String codigousuario) {
		
		Long idUsuario = Long.parseLong(codigousuario);
		boolean deleteAll = false;	
		List<Permissao> userPermissaoSalvo = permissaoRepository.pegarPermissoesCadastradasDoUsuario(idUsuario);
		int qtdListarecebida = listaRecebida.size();
		String messageRetorno = "";
		

		if(qtdListarecebida == 0) {
			
			usuarioPermissaoRepository.deleteAllPermisstionByUser(idUsuario);
			deleteAll = true;
			messageRetorno = "OK";
		}
		
		if(qtdListarecebida < userPermissaoSalvo.size() && deleteAll == false) {
			
			messageRetorno = removerPermissao(listaRecebida, userPermissaoSalvo, idUsuario);
			
		}
		
		if(qtdListarecebida > userPermissaoSalvo.size()) {
			
			messageRetorno = cadastrarPermissoes(listaRecebida, userPermissaoSalvo, idUsuario);
		}
		return messageRetorno;
	}
	
	
	private String cadastrarPermissoes(List<Permissao> listaRecebida, List<Permissao> listaSalva, Long usuarioId) {
		
		String message = "";
		
		try {
			List<Permissao> permissoesParaCadastrar = new ArrayList<Permissao>();
			
			permissoesParaCadastrar = listaRecebida;
			permissoesParaCadastrar.removeAll(listaSalva);
			
			for (int i = 0; i < permissoesParaCadastrar.size(); i++) {
				
				UsuarioPermissao userPermition = new UsuarioPermissao();
				
				userPermition.setPermissaoId(permissoesParaCadastrar.get(i).getPermissaoId());
				userPermition.setUsuarioId(usuarioId);
				
				usuarioPermissaoRepository.save(userPermition);
			}
			message = "OK";
		} catch (Exception e) {
			message = "NOK";
		}
		return message;
		
	}
	
	
	private String removerPermissao(List<Permissao> listaRecebida, List<Permissao> listaSalva, Long usuarioId) {
		
		String message = "";
		try {
			List<Permissao> permissoesParaRemover = new ArrayList<Permissao>();
			
			permissoesParaRemover = listaSalva;
			permissoesParaRemover.removeAll(listaRecebida);
			
			if(permissoesParaRemover.size() > 0) {
				
				Long permissaoId = 0L;
				
				for (int i = 0; i < permissoesParaRemover.size(); i++) {
					
					permissaoId = permissoesParaRemover.get(i).getPermissaoId();
					
					usuarioPermissaoRepository.deletePermissaoByUserAndPermistion(usuarioId, permissaoId);
				}
			}
			
			message = "OK";
		} catch (Exception e) {
			message = "NOK";
		}
		return message;
	}

}
