package br.com.caixa.sidce.interfaces.util;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.caixa.sidce.domain.model.enums.PerfilEnum;

/**
 * 
 * @author p577455, p583410
 *
 */
public class UsuarioLogadoUtil {
	
	
	
	/**
	 * Retorna todas as roles do usuário logado.
	 * @return List de strings das roles;
	 */
	public static String usuarioLogado(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	/**
	 * Retorna todas as roles do usuário logado, com exceção de uma_authorization
	 * @return List de strings das roles;
	 */
	public static List<String> buscarRoles(){
		
		List<String> roles = new ArrayList<>();
		KeycloakAuthenticationToken kat = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		kat.getAuthorities().stream()
			.map(e -> e.getAuthority().substring(9))
			.filter(e -> !"authorization".equals(e))
			.filter(e -> !"ine_access".equals(e))
			.forEach(roles::add);
		return roles;
	}
	
	/**
	 * Retorna se o usuário possui o parâmetro perfil
	 * @param perfil a ser verificado
	 * @return true se possuir, false caso contrário
	 */
	public static Boolean possuiPerfil(PerfilEnum perfil) {
		return buscarRoles().contains(perfil.getDescricao());
	}
	
	/**
	 * Verifica se o usuário possui APENAS o parâmetro perfil
	 * @param perfil a ser verificado
	 * @return true caso possua apenas perfil, false caso contrário
	 */
	public static Boolean possuiApenasPerfil(PerfilEnum perfil) {
		return (possuiPerfil(perfil) && buscarRoles().size() == 1);
	}
	
	/**
	 * Verifica se o usuário logado possui permissão de acordo com as ROLES do usuário
	 * @param lista com as permissões (ROLES), que caso o usuário possua pelo menos uma, significa que tem permissão
	 * @return true caso possua permissão, false caso contrário
	 */
	public static boolean verificaPermissao( List<String> permissoes) {
		boolean isPermitido = false;
		List<String> roles = buscarRoles();
		for (int i = 0; i < permissoes.size(); i++) {
			if(roles.contains(permissoes.get(i))) {
				isPermitido = true;
			}
		}
		return isPermitido;
	}

}
