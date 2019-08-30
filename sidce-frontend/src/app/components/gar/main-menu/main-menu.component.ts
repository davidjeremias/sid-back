import { Component, isDevMode } from '@angular/core';
import { KeycloakService } from 'src/app/auth/keycloak.service';
import { Globals } from 'src/app/entidade/parametros/globals';
import { System } from 'src/app/entidade/parametros/system';

/**
 * Componente de menu criado para atender demanda inicial SIDCE
 * @author allef.garug
 * @version 1.2.2
 */
@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent {

  isAberto: boolean;
  isOver: boolean;
  roles: Array<any>;
  menu: Array<any>;
  globals: Globals;
  private itemAberto: HTMLElement;

  constructor() {
    // Instancia e recupera menu de componente Globals
    this.globals = System.getInstance();
    this.verificarAutorizacao(this.globals.menu);

    // Valores iniciais do componente
    this.isAberto = true;
  }

  /**
   * Persiste menu aberto ou abre apenas com mouse sobre itens do menu
   */
  toggleMenu() {
    this.isAberto = (this.isAberto) ? false : true;
  }

  /**
   * Controle de toogle de todos os submenus
   * 
   * @param {HTMLElement} item Item que terá seus items expandidos ou retraidos
   */
  toggleSubmenus(item) {
    const itensIguais = item === this.itemAberto;

    if (!this.itemAberto) {
      // Caso não possua nenhum item atualmente aberto, item aberto 
      this.itemAberto = item;
      this.toggleItem();
    } else if (itensIguais) {
      // Caso itens forem iguais, fecha o item e atribui null indicando que não há item aberto
      this.toggleItem();
      this.itemAberto = null;
    } else {
      // Caso forem diferentes, toggle nos dois itens e atribui o novo ao item aberto
      this.toggleItem();
      this.toggleItem(item);
      this.itemAberto = item;
    } 
  }

  /**
   * Expande items de um item de menu
   * 
   * @param item Item que terá seus items expandidos ou retraidos, this.itemAberto caso não seja informado
   */
  private toggleItem(item?) {
    const activeItem = item ? item : this.itemAberto;
    this.isVirada(activeItem) ? activeItem.classList.remove('isVisible') : activeItem.classList.add('isVisible');
  }

  /**
   * Responsável por indicar se elemento está ou não com subitems expandidos
   * 
   * @return {boolean} Retorno boleano
   * @param item Submenu a ser expandido, this.itemAberto caso não seja informado
   */
  isVirada(item?) {
    const element: HTMLElement = item ? item : this.itemAberto;
    return element.classList.contains('isVisible') ? true : false;
  }

  /**
   * Verifica as autorizações do usuário e altera o menu para um novo menu após verificações
   * Recebe um menu no formato json e após validações monta o menu da aplicação com base 
   * nos parâmetros de role no app.component
   * 
   * @param menu Menu setado no menu do sistema após validações
   * @return void
   */
  public verificarAutorizacao(menu) {
    this.roles = KeycloakService.keyCloak.realmAccess.roles;
    let menuAtual = new Array();
    menu.forEach(e => {
      if (this.eTratado(e)) menuAtual.push(this.eTratado(e));
    })
    this.menu = menuAtual;
  }

  /**
   * Trata um item de menu, verificando se usuário possuirá acesso a determinado item
   * 
   * @private
   * @param  {MenuItem} item Item de entrada, sendo do tipo MenuItem com adição de parâmetro roles 
   * @return {MenuItem} Item único tratado ou null
   */
  private eTratado(item) {
    // Caso esteja em modo de desenvolvimento, retorna o item
    // if (isDevMode()){
    //   return item;
    // } 

    // Se o item for capaz de ser validado sozinho, já retorna o próprio item
    if (this.validaRoles(item)) {
      return this.validaRoles(item);
      // Caso contrário, validará cada item individualmente para validação
    } else if (item.items) {
      const newItem = this.globals.copy(item);
      newItem.items = new Array();
      item.items.forEach(subitem => {
        if (this.validaRoles(subitem)) newItem.items.push(subitem);
      });
      // Se ao menos um dos itens forem exibididos, o item será retornado
      return newItem.items.length >= 1 ? newItem : null;
    }

    return null;
  }

  /**
   * Verifica se determinado item será mantido no menu, analisando as roles de permissão do usuário e as permissões
   * necessárias para visualização do item
   * 
   * @private 
   * @param  {MenuItem} item Item de entrada, sendo do tipo MenuItem com adição de parâmetro roles 
   */
  private validaRoles(item) {
    return item.roles ? item.roles.some(e => this.roles.includes(e)) ? item : null : null;
  }
}
