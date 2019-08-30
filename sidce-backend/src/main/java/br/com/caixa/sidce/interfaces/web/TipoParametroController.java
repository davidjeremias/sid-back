package br.com.caixa.sidce.interfaces.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.TipoParametro;
import br.com.caixa.sidce.domain.service.TipoParametroService;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("api/tipo-parametros")
@CrossOrigin(origins = "*")
@Api(value = "Tipos de Parâmetros")
public class TipoParametroController extends RestFullEndpoint<TipoParametro, Integer> {

	@Autowired
	public TipoParametroController(TipoParametroService service) {
		super(service);
	}

}
