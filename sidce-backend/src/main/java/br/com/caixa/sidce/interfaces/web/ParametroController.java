package br.com.caixa.sidce.interfaces.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.caixa.sidce.domain.model.Parametro;
import br.com.caixa.sidce.domain.service.ParametroService;
import br.com.caixa.sidce.util.infraestructure.web.RestFullEndpoint;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("api/parametros")
@CrossOrigin(origins = "*")
@Api(value = "Parametro")
public class ParametroController extends RestFullEndpoint<Parametro, Integer> {
	
	@Autowired
	public ParametroController(ParametroService service) {
		super(service);
	}
	
}
