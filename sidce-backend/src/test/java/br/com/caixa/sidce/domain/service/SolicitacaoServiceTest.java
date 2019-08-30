package br.com.caixa.sidce.domain.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.testng.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.caixa.sidce.domain.model.AgendamentoETL;
import br.com.caixa.sidce.domain.model.CodigoSolicitacao;
import br.com.caixa.sidce.domain.model.SituacaoSolicitacao;
import br.com.caixa.sidce.domain.model.Solicitacao;
import br.com.caixa.sidce.domain.model.SolicitacaoConta;
import br.com.caixa.sidce.domain.model.enums.SituacaoSolicitacaoEnum;
import br.com.caixa.sidce.domain.model.enums.TipoSolicitacaoEnum;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SituacaoSolicitacaoRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoContaRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoCustomRepository;
import br.com.caixa.sidce.infraestructure.persistence.jpa.SolicitacaoRepository;
import br.com.caixa.sidce.interfaces.web.dto.ContratosDTO;
import br.com.caixa.sidce.interfaces.web.dto.SolicitacaoDTO;
import br.com.caixa.sidce.util.infraestructure.auditoria.AuditoriaAspecto;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.exception.PeriodoDatasException;

@RunWith(SpringRunner.class)
public class SolicitacaoServiceTest {

	@Spy
	@InjectMocks
	SolicitacaoService service;

	@Mock
	SituacaoSolicitacaoRepository mockSituacaoSolicitacaoRepository;

	@Mock
	SolicitacaoRepository mockSolicitacaoRepository;

	@Mock
	SolicitacaoContaRepository mockSolicitacaoContaRepository;

	@Mock
	SituacaoSolicitacaoRepository situacaoSolicitacaoRepository;

	@Mock
	private AfastamentoService afastamentoService;

	@Mock
	private AgendamentoETLService agendamentoETLService;

	@Mock
	private ParametroService parametroService;

	@Mock
	private SolicitacaoCustomRepository mockCrepo;

	@Mock
	private AuditoriaAspecto auditoria;

	@Mock
	private CodigoSolicitacaoService codigoSolicitacaoService;

	@Mock
	private SIICOService siicoService;
	
	@Mock
	private FactoryEmail email;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = spy(service);
	}

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	public List<SolicitacaoConta> contas() {

		List<SolicitacaoConta> listaContas = new ArrayList<>();

		AtomicLong cont = new AtomicLong();

		SolicitacaoConta conta1 = SolicitacaoConta.builder().id(cont.getAndIncrement()).cpfCNPJ("33299020005")
				.numeroAgencia(12354).numeroConta("54321").digitoConta(1).numeroOperacao(13).situacao("Ativo")
				.dataAbertura(LocalDate.now().minusDays(50)).fimPeriodo(LocalDate.now())
				.inicioPeriodo(LocalDate.now().minusMonths(3)).build();

		SolicitacaoConta conta2 = conta1.toBuilder().id(cont.getAndIncrement()).numeroAgencia(45784)
				.numeroConta("12457").digitoConta(7).situacao("Ativo").build();

		SolicitacaoConta conta3 = conta1.toBuilder().id(cont.getAndIncrement()).numeroAgencia(231).numeroConta("5675")
				.digitoConta(2).build();

		listaContas.add(conta1);
		listaContas.add(conta2);
		listaContas.add(conta3);

		return listaContas;
	}

	@Test
	public void buscaContasPorCpfCnpjExceptionTest() {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] cpfCNPJ = { "54.131.351/5135-13" };
		String[] myStringArrayPage = { "1" };
		String[] myStringArrayLimit = { "1" };
		parameterMap.put("page", myStringArrayPage);
		parameterMap.put("limit", myStringArrayLimit);
		cpfCNPJ = parameterMap.put("cpfCNPJ", cpfCNPJ);

		doReturn("l74e1145a3a4444dce8967412002eff1f6").when(parametroService).buscarPorChave(any());
		doReturn("http://api.des.caixa:8080/cadastro/v1/clientes").when(parametroService).buscarPorChave(any());

		service.buscaContasPorCpfCnpj(parameterMap, "teste");
	}

	@Test
	public void criarListaSolicitacaoContaTest() {
		ContratosDTO contratosDTO1 = ContratosDTO.builder().coIdentificacao("0647003000021236").dtInicio(new Date())
				.nuProduto("3").sgSistema("SIDEC").dtFim(new Date()).nuUnidade("647")
				.noRdzdoContrato("COMPANHIA MUNICIPAL DE ADMINISTR").icSituacao("1").build();

		ContratosDTO contratosDTO2 = ContratosDTO.builder().coIdentificacao("0004000037977").dtInicio(new Date())
				.nuProduto("1292").sgSistema("SID01").dtFim(null).nuUnidade("682").noRdzdoContrato(null)
				.icSituacao(null).build();

		List<ContratosDTO> contratosDTOs = new ArrayList<>();
		contratosDTOs.add(contratosDTO1);
		contratosDTOs.add(contratosDTO2);

		assertNotNull(service.criarListaSolicitacaoConta(contratosDTOs, "12372207000176"));
	}

	@Test
	public void buscaSituacoesTest() {
		SituacaoSolicitacao s1 = new SituacaoSolicitacao();
		SituacaoSolicitacao s2 = new SituacaoSolicitacao();
		SituacaoSolicitacao s3 = new SituacaoSolicitacao();
		SituacaoSolicitacao s4 = new SituacaoSolicitacao();
		SituacaoSolicitacao s5 = new SituacaoSolicitacao();
		SituacaoSolicitacao s6 = new SituacaoSolicitacao();
		SituacaoSolicitacao s7 = new SituacaoSolicitacao();
		SituacaoSolicitacao s8 = new SituacaoSolicitacao();
		s1.setNomeSituacao(SituacaoSolicitacaoEnum.EM_CRIACAO);
		s2.setNomeSituacao(SituacaoSolicitacaoEnum.ENVIADO_PARA_APROVACAO);
		s3.setNomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO);
		s4.setNomeSituacao(SituacaoSolicitacaoEnum.RECUSADO);
		s5.setNomeSituacao(SituacaoSolicitacaoEnum.AFASTAMENTO_EM_CRIACAO);
		s6.setNomeSituacao(SituacaoSolicitacaoEnum.GERADO);
		s7.setNomeSituacao(SituacaoSolicitacaoEnum.TRANSMITIDO);
		s8.setNomeSituacao(SituacaoSolicitacaoEnum.REJEITADO);
		List<SituacaoSolicitacao> lista = new ArrayList<SituacaoSolicitacao>();
		lista.add(s1);
		lista.add(s2);
		lista.add(s3);
		lista.add(s4);
		lista.add(s5);
		lista.add(s6);
		lista.add(s7);
		lista.add(s8);

		doReturn(lista).when(situacaoSolicitacaoRepository).findAll();

		assertFalse(service.buscaSituacoes(true, false).isEmpty());
		assertFalse(service.buscaSituacoes(false, true).isEmpty());
		assertFalse(service.buscaSituacoes(false, false).isEmpty());
	}

	@Test
	public void testaSalvarSolicitacao() throws Exception {
		//
		doNothing().when(mockSolicitacaoContaRepository).deleteAll();
		Solicitacao a = Solicitacao.builder().id(1).matricula("teste").build();
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().prefixo("Teste").numero(1).codigo("Teste1")
				.build();
		doReturn(a).when(service).addOrUpdate(any());
		doReturn(codigoSolicitacao).when(codigoSolicitacaoService).addOrUpdate(any());
		// rascunho false
		Solicitacao test1 = service.salvarSolicitacao(montarDTO(), "testUser", "teste");
		assertEquals(test1, a);
		// rascunho false e afastamento true
		Solicitacao test5 = service.salvarSolicitacao(montarDTO().toBuilder().afastamento(true).build(), "testUser",
				"teste");
		assertEquals(test5, a);
		// rascunho true
		Solicitacao test2 = service.salvarSolicitacao(
				montarDTO().toBuilder().rascunho(false).afastamento(false).build(), "testUser", "teste");
		assertEquals(test2, a);
		// rascunho true e afastamento true
		Solicitacao test4 = service.salvarSolicitacao(montarDTO().toBuilder().rascunho(false).afastamento(true).build(),
				"testUser", "teste");
		assertEquals(test4, a);
		// id not null
		Solicitacao test3 = service.salvarSolicitacao(montarDTO().toBuilder().id(1).build(), "testUser", "teste");
		assertEquals(test3, a);

		Solicitacao test6 = service.salvarSolicitacao(
				montarDTO2().toBuilder().id(1).codigoSolicitacao(new CodigoSolicitacao()).build(), "testUser", "teste");
		assertEquals(test6, a);

		Solicitacao test7 = service.salvarSolicitacao(montarDTO2().toBuilder().id(1).build(), "testUser", "teste");
		assertEquals(test7, a);

		Solicitacao test8 = service.salvarSolicitacao(
				montarDTO2().toBuilder().rascunho(false).afastamento(false).build(), "testUser", "teste");
		assertEquals(test8, a);
	}

	@Test
	public void testaConsultaSalvarSolicitacaoExceptions01() throws NegocioException {
		SolicitacaoDTO dto = montarDTO();
		dto.setRascunho(false);
		dto.setContas(null);
		dto.setNomeArquivo("nomeTeste");
		exception.expect(NegocioException.class);
		service.salvarSolicitacao(dto, "testUser", "teste");
	}

	@Test
	public void testaConsultaSalvarSolicitacaoExceptions02() throws NegocioException {
		SolicitacaoDTO dto = montarDTO();
		dto.setRascunho(false);
		dto.setOficio(null);
		exception.expect(NegocioException.class);
		service.salvarSolicitacao(dto, "testUser", "teste");
	}

	@Test
	public void testaConsultaSalvarSolicitacaoExceptions03() throws NegocioException {
		SolicitacaoDTO dto = montarDTO();
		dto.setRascunho(false);
		dto.getContas().get(0).setInicioPeriodo(LocalDate.now());
		dto.getContas().get(0).setFimPeriodo(LocalDate.now().minusDays(10));
		exception.expect(NegocioException.class);
		service.salvarSolicitacao(dto, "testUser", "teste");
	}

	@Test
	public void testaConsultaSolicitacaoPaginadoComFiltro() throws NegocioException {

		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams();
		assertNotNull(service.consultaSolicitacaoPaginadoComFiltro(params));

		params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		assertNotNull(service.consultaSolicitacaoPaginadoComFiltro(params));
	}
	
	@Test
	public void consultaSolicitacaoPaginadoComFiltroTest() throws NegocioException {
		
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams();
		assertNotNull(service.consultaSolicitacaoPaginadoComFiltro(params));
		
		params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		Mockito.doThrow(NegocioException.class).when(siicoService).consultarUnidade(any());
		assertNotNull(service.consultaSolicitacaoPaginadoComFiltro(params));
	}

	@Test(expected = PeriodoDatasException.class)
	public void testaConsultaSolicitacaoPaginadoComFiltro2() throws NegocioException {

		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams2();
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

	@Test
	public void testaConsultaSolicitacaoPaginadoComFiltro4() throws NegocioException {
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1)
				.situacaoSolicitacao(SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.GERADO).build())
				.build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

	@Test
	public void testaConsultaSolicitacaoPaginadoComFiltro5() throws NegocioException, ParseException {
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1)
				.situacaoSolicitacao(
						SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.TRANSMITIDO).build())
				.build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		lista.add(AgendamentoETL.builder().dtHrProcessamento(dateFormat.parse("11-11-2012")).build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

	@Test
	public void testaConsultaSolicitacaoPaginadoComFiltro6() throws NegocioException {
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).situacaoSolicitacao(
				SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.REJEITADO).build()).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

	@Test
	public void testaConsultaSolicitacaoPaginadoComFiltro7() throws NegocioException {
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).situacaoSolicitacao(
				SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO).build()).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams3();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		service.consultaSolicitacaoPaginadoComFiltro(params);

		params = montarParams5();
		service.consultaSolicitacaoPaginadoComFiltro(params);

		params = montarParams6();
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

	@Test(expected = NegocioException.class)
	public void testaConsultaSolicitacaoPaginadoComFiltro8() throws NegocioException {
		List<Solicitacao> list = new ArrayList<>();
		Solicitacao sol = Solicitacao.builder().id(1).situacaoSolicitacao(
				SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO).build()).build();
		list.add(sol);
		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
		Map<String, String[]> params = montarParams4();
		List<AgendamentoETL> lista = new ArrayList<>();
		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
		service.consultaSolicitacaoPaginadoComFiltro(params);
	}

//	@Test
//	public void testaConsultaSolicitacaoPaginadoComFiltro9() throws NegocioException {
//		List<Solicitacao> list = new ArrayList<>();
//		Solicitacao sol = Solicitacao.builder().id(1).situacaoSolicitacao(
//				SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO).build()).build();
//		list.add(sol);
//		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
//		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
//		Map<String, String[]> params = montarParams5();
//		List<AgendamentoETL> lista = new ArrayList<>();
//		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
//		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
//		service.consultaSolicitacaoPaginadoComFiltro(params);
//	}

//	@Test
//	public void testaConsultaSolicitacaoPaginadoComFiltro10() throws NegocioException {
//		List<Solicitacao> list = new ArrayList<>();
//		Solicitacao sol = Solicitacao.builder().id(1).situacaoSolicitacao(
//				SituacaoSolicitacao.builder().nomeSituacao(SituacaoSolicitacaoEnum.CONFIRMADO).build()).build();
//		list.add(sol);
//		PageImpl<Solicitacao> pgImpl = new PageImpl<>(list);
//		doReturn(pgImpl).when(mockCrepo).buscaSolicitacoesPaginado(any(), any());
//		Map<String, String[]> params = montarParams6();
//		List<AgendamentoETL> lista = new ArrayList<>();
//		lista.add(AgendamentoETL.builder().dtHrProcessamento(new Date()).build());
//		doReturn(lista).when(agendamentoETLService).buscaAgendamentoPorSolicitacaoAll(any());
//		service.consultaSolicitacaoPaginadoComFiltro(params);
//	}

	@Test
	public void salvaSolicitacaoPreAprovadoTest() throws NegocioException {
		doNothing().when(mockSolicitacaoContaRepository).deleteAll();
		Solicitacao s = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.E)
				.isPreAprovado(true).build();
		Optional<Solicitacao> opt = Optional.of(Solicitacao.builder().build());
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().prefixo("Teste").numero(1).codigo("Teste1")
				.build();
		doReturn(opt).when(service).findOne(any());
		doReturn(s).when(service).addOrUpdate(any());
		doReturn(codigoSolicitacao).when(codigoSolicitacaoService).addOrUpdate(any());

		Solicitacao test1 = service.salvaSolicitacaoPreAprovado(montarDTO(), "testUser", "1.1.1.1");
		assertEquals(test1, s);
	}

	@Test
	public void salvaSolicitacaoPreAprovado2Test() throws NegocioException {
		doNothing().when(mockSolicitacaoContaRepository).deleteAll();
		Solicitacao s = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.G)
				.isPreAprovado(true).build();
		Solicitacao s1 = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.E)
				.isPreAprovado(true).build();
		Optional<Solicitacao> opt = Optional.of(Solicitacao.builder().build());
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().prefixo("Teste").numero(1).codigo("Teste1")
				.build();
		doReturn(opt).when(service).findOne(any());
		doReturn(s).when(service).addOrUpdate(any());
		doReturn(codigoSolicitacao).when(codigoSolicitacaoService).addOrUpdate(any());

		Solicitacao test1 = service.salvaSolicitacaoPreAprovado(
				SolicitacaoDTO.builder().matricula("1234").contas(contas()).oficio("testeOficio").rascunho(false)
						.contas(contas()).tipoSolicitacao(TipoSolicitacaoEnum.E).afastamento(true).build(),
				"testUser", "1.1.1.1");
		assertEquals(test1, s);

		test1 = service.salvaSolicitacaoPreAprovado(
				SolicitacaoDTO.builder().id(1).matricula("1234").contas(contas()).oficio("testeOficio").rascunho(false)
						.contas(contas()).tipoSolicitacao(TipoSolicitacaoEnum.E).afastamento(true).build(),
				"testUser", "1.1.1.1");
		assertEquals(test1, s);

		doReturn(s1).when(service).addOrUpdate(any());
		test1 = service.salvaSolicitacaoPreAprovado(
				SolicitacaoDTO.builder().id(1).matricula("1234").contas(contas()).oficio("testeOficio").rascunho(false)
						.contas(contas()).tipoSolicitacao(TipoSolicitacaoEnum.G).afastamento(true).build(),
				"testUser", "1.1.1.1");
		assertEquals(test1, s1);
	}

	@Test
	public void excluirAfastamentoTest() throws NegocioException {
		CodigoSolicitacao codigoSolicitacao = CodigoSolicitacao.builder().prefixo("Teste").numero(1).codigo("Teste1")
				.build();
		Solicitacao s = Solicitacao.builder().id(1).tipoSolicitacao(TipoSolicitacaoEnum.G)
				.codigoSolicitacao(codigoSolicitacao).build();
		Solicitacao s1 = Solicitacao.builder().id(1).tipoSolicitacao(TipoSolicitacaoEnum.E)
				.codigoSolicitacao(codigoSolicitacao).build();
		service.excluirAfastamento(s, "testUser", "1.1.1.1");
		service.excluirAfastamento(s1, "testUser", "1.1.1.1");
	}

	@Test
	public void aprovarSolicitacaoTest() throws NegocioException {
		Solicitacao s = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.E)
				.isPreAprovado(false).build();
		Solicitacao s1 = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.G)
				.isPreAprovado(false).build();
		Optional<Solicitacao> opt = Optional.of(Solicitacao.builder().build());
		doReturn(opt).when(service).findOne(any());
		doReturn(s).when(service).addOrUpdate(any());

		Solicitacao test1 = service.aprovarSolicitacao(s, "testUser", "1.1.1.1");
		assertEquals(test1, s);

		doReturn(s1).when(service).addOrUpdate(any());
		test1 = service.aprovarSolicitacao(s1, "testUser", "1.1.1.1");
		assertEquals(test1, s1);
	}

	@Test
	public void rejeitarSolicitacaoTest() throws NegocioException {
		Solicitacao s = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.G)
				.isPreAprovado(true).build();
		Solicitacao s1 = Solicitacao.builder().id(1).matricula("teste").tipoSolicitacao(TipoSolicitacaoEnum.G)
				.isPreAprovado(true).build();
		Optional<Solicitacao> opt = Optional
				.of(Solicitacao.builder().tipoSolicitacao(TipoSolicitacaoEnum.G).isPreAprovado(false).build());
		Optional<Solicitacao> opt1 = Optional
				.of(Solicitacao.builder().tipoSolicitacao(TipoSolicitacaoEnum.G).isPreAprovado(true).build());
		doReturn(opt).when(service).findOne(any());
		doReturn(s).when(service).addOrUpdate(any());

		Solicitacao test1 = service.rejeitarSolicitacao(s, "testUser", "1.1.1.1");
		assertEquals(test1, s);

		doReturn(opt1).when(service).findOne(any());
		test1 = service.rejeitarSolicitacao(s1, "testUser", "1.1.1.1");
		assertEquals(test1, s1);
	}

	@Test(expected = NegocioException.class)
	public void rejeitarSolicitacao2Test() throws NegocioException {
		Solicitacao s = Solicitacao.builder().id(1).matricula("teste").build();
		doReturn(s).when(service).addOrUpdate(any());
		service.rejeitarSolicitacao(s, "testUser", "1.1.1.1");
	}

//	@Test
//	public void buscarUnidadeTest() throws NegocioException {
//		doReturn(new UnidadeDTO()).when(siicoService).consultarUnidade(any());
//		service.buscarUnidade(1);
//
//		Mockito.doThrow(NegocioException.class).when(siicoService).consultarUnidade(any());
//		service.buscarUnidade(1);
//	}

	private Map<String, String[]> montarParams() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("fimPeriodo", new String[] { "2019-04-01T03:00:00.000Z" });
		params.put("iniPeriodo", new String[] { "2018-04-01T03:00:00.000Z" });
		params.put("tipo", new String[] { "G" });
		params.put("situacao", new String[] { "1" });
		params.put("numeroUnidade", new String[] { "1" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		return params;
	}

	private Map<String, String[]> montarParams2() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("fimPeriodo", new String[] { "2018-04-01T03:00:00.000Z" });
		params.put("iniPeriodo", new String[] { "2018-05-01T03:00:00.000Z" });
		params.put("tipo", new String[] { "G" });
		params.put("situacao", new String[] { "1" });
		params.put("numeroUnidade", new String[] { "1" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		return params;
	}

	private Map<String, String[]> montarParams3() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("fimPeriodo", new String[] { "2018-04-01T03:00:00.000Z" });
		params.put("tipo", new String[] { "G" });
		params.put("situacao", new String[] { "1" });
		params.put("numeroUnidade", new String[] { "1" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		return params;
	}

	private Map<String, String[]> montarParams4() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("numeroUnidade", new String[] { "1" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		return params;
	}

	private Map<String, String[]> montarParams5() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("tipo", new String[] { "G" });
		params.put("numeroUnidade", new String[] { "1" });
		params.put("isAfastamento", new String[] { "false" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		return params;
	}

	private Map<String, String[]> montarParams6() {
		Map<String, String[]> params = new HashMap<>();
		params.put("matricula", new String[] { "teste" });
		params.put("tipo", new String[] { "G" });
		params.put("unidade", new String[] { "1" });
		params.put("isAfastamento", new String[] { "true" });
		params.put("cpfCNPJ", new String[] { "12345678910" });
		params.put("situacao[]", new String[] { "1" });
		return params;
	}

	private SolicitacaoDTO montarDTO() {
		return SolicitacaoDTO.builder().matricula("1234").contas(contas()).oficio("testeOficio").rascunho(true)
				.afastamento(false).tipoSolicitacao(TipoSolicitacaoEnum.E).build();
	}

	private SolicitacaoDTO montarDTO2() {
		return SolicitacaoDTO.builder().matricula("1234").contas(contas()).oficio("testeOficio").rascunho(true)
				.afastamento(false).tipoSolicitacao(TipoSolicitacaoEnum.G).build();
	}

}
