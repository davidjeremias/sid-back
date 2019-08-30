package br.com.caixa.sidce.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import br.com.caixa.sidce.util.infraestructure.exception.InternalException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Configuration
public class ConfigDataSource {
	
	@Autowired
	private Environment env;
	
  	@Value("${spring.datasource.jndi-name}")
    private String jndiSIDCE;

    private JndiDataSourceLookup lookup = new JndiDataSourceLookup();

    @Primary
    @Bean
    public DataSource primarsyDs() {
        return lookup.getDataSource(jndiSIDCE);
    }
	
	public DataSource dataSource(DataSourceIntegracaoEnum ds) {
	    final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
	    dsLookup.setResourceRef(true);
	    return dsLookup.getDataSource(env.getProperty(ds.getSpringJNDI()));
	}
	
	public Connection getConnection(DataSourceIntegracaoEnum ds) {
	    Connection conn = null;
    	try{
    		Log.info(ConfigDataSource.class, "Abrindo conexão com: "+ds.getSigla());
	        conn = dataSource(ds).getConnection();
	    } catch (SQLException e) {
	    	 throw new InternalException("problemas-conexao-siico", e);
	    }
	    return conn;
	}
}