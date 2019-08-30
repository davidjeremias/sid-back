package br.com.caixa.sidce.infraestructure.persistence.jpa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.com.caixa.sidce.config.ConfigDataSource;
import br.com.caixa.sidce.config.DataSourceIntegracaoEnum;
import br.com.caixa.sidce.interfaces.web.dto.EmailDTO;
import br.com.caixa.sidce.interfaces.web.dto.UnidadeDTO;
import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;

@Repository
public class SIICOCustomImplRepository implements SIICOCustomRepository {

	@Autowired
	private ConfigDataSource confiDS;

	@Override
	public UnidadeDTO consultarUnidade(Integer unidade) throws NegocioException {

		String sql = " SELECT NO_UNIDADE, NU_UNIDADE, SG_UNIDADE FROM ICOSM001.ICOTBU24_UNIDADE "
				+ "WHERE NU_UNIDADE = ? AND IC_ULTIMA_SITUACAO = 'AT' ";

		UnidadeDTO unidadeSiicoDTO = new UnidadeDTO();

		try (Connection con = confiDS.getConnection(DataSourceIntegracaoEnum.SIICO);
				PreparedStatement ps = con.prepareStatement(sql);) {

			ps.setInt(1, unidade);

			try (ResultSet rs = ps.executeQuery();) {
				unidadeSiicoDTO = criarUnidadeSiicoDTO(rs);
			}

			return unidadeSiicoDTO;

		} catch (SQLException e) {
			throw new NegocioException("problemas-conexao-siico", e);
		}
	}

	private UnidadeDTO criarUnidadeSiicoDTO(ResultSet rs) throws SQLException {
		UnidadeDTO unidadeSiicoDTO = new UnidadeDTO();
		while (rs.next()) {
			unidadeSiicoDTO = UnidadeDTO.builder().nomeUnidade(rs.getString("NO_UNIDADE").trim())
					.unidade(rs.getInt("NU_UNIDADE"))
					.siglaUnidade((rs.getString("SG_UNIDADE") != null ? rs.getString("SG_UNIDADE") : "").trim())
					.build();
		}
		return unidadeSiicoDTO;
	}
	
	@Override
	public PageImpl<UnidadeDTO> buscaUnidades(Pageable pageRequest, Integer numeroUnidade) throws NegocioException {
		Integer offset = pageRequest.getPageNumber() * 10;
		String query = "SELECT U.NO_UNIDADE, U.NU_UNIDADE, E.NO_EMAIL2 FROM ICOSM001.TB001_UNIDADE_EMAIL E "
				+ "INNER JOIN ICOSM001.ICOTBU24_UNIDADE U "
				+ "ON E.NU_UNIDADE = U.NU_UNIDADE "
				+ "WHERE CAST(U.NU_UNIDADE AS VARCHAR(4)) LIKE '"+numeroUnidade+"%' AND U.IC_ULTIMA_SITUACAO = 'AT' "
				+ "ORDER BY U.NU_UNIDADE "
				+ "LIMIT 10 OFFSET "+offset.toString();
		
		String count = "SELECT COUNT(NU_UNIDADE) FROM ICOSM001.ICOTBU24_UNIDADE WHERE CAST(NU_UNIDADE AS VARCHAR(4)) LIKE '"+numeroUnidade+"%' AND IC_ULTIMA_SITUACAO = 'AT'";
		
		List<UnidadeDTO> listaUnidadeSiicoDTO = new ArrayList<>();
		long total = 0;
		try (Connection con = confiDS.getConnection(DataSourceIntegracaoEnum.SIICO);
				PreparedStatement ps = con.prepareStatement(query);
				PreparedStatement pCount = con.prepareStatement(count);) {
			try (ResultSet rs = ps.executeQuery(); ResultSet rCount = pCount.executeQuery();) {
				listaUnidadeSiicoDTO = criarListaUnidadeSiicoDTO(rs);
				total = countUnidade(rCount);
			}
			return new PageImpl<>(listaUnidadeSiicoDTO, pageRequest, total);
		} catch (SQLException e) {
			throw new NegocioException("problemas-conexao-siico", e);
		}
	}
	
	private List<UnidadeDTO> criarListaUnidadeSiicoDTO(ResultSet rs) throws SQLException {
		UnidadeDTO unidadeSiicoDTO = null;
		List<UnidadeDTO> listaUnidadeSiicoDTO = new ArrayList<>();
		while (rs.next()) {
			unidadeSiicoDTO = UnidadeDTO.builder()
					.unidade(rs.getInt("NU_UNIDADE"))
					.nomeUnidade(rs.getString("NO_UNIDADE").trim())
					.emailUnidade(rs.getString("NO_EMAIL2").toLowerCase())
					.build();
			listaUnidadeSiicoDTO.add(unidadeSiicoDTO);
		}
		return listaUnidadeSiicoDTO;
	}
	
	private int countUnidade(ResultSet rs) throws SQLException {
		int numberOfRows = 0;
		if (rs.next()) {
			numberOfRows = rs.getInt(1);
	    }
		return numberOfRows;
	}

	@Override
	public EmailDTO buscaEmailUnidade(String matricula) throws NegocioException {
		String sql = "SELECT E.NO_EMAIL2 FROM ICOSM001.ICOTBH01_EMPRO_CXA EMP "
				+ "INNER JOIN ICOSM001.TB001_UNIDADE_EMAIL E "
				+ "ON EMP.NU_UNIDADE_U24 = E.NU_UNIDADE "
				+ "WHERE EMP.NU_MATRICULA = '"+matricula+"'";
		
		EmailDTO email = new EmailDTO();

		try (Connection con = confiDS.getConnection(DataSourceIntegracaoEnum.SIICO);
				PreparedStatement ps = con.prepareStatement(sql);) {

			try (ResultSet rs = ps.executeQuery();) {
				email = criarEmailDTO(rs);
			}
			return email;
		} catch (SQLException e) {
			throw new NegocioException("problemas-conexao-siico", e);
		}
	}
	
	private EmailDTO criarEmailDTO(ResultSet rs) throws SQLException {
		EmailDTO email = new EmailDTO();
		while (rs.next()) {
			email = EmailDTO.builder()
			.email(rs.getString("NO_EMAIL2").trim()).build();
		}
		return email;
	}

	

}
