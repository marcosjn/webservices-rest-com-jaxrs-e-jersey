package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ClienteTest {
	
	private Client client;
	private WebTarget target;
	
	@Before
	public void Before() {
		Servidor.startaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());		
		this.client = ClientBuilder.newClient(config); 			
		this.target = this.client.target("http://localhost:8080");
	}
	
	@After
	public void mataServidor() {
		Servidor.mataServidor();
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		String conteudo = this.target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());		
	}
	
	@Test
	public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {		
		String conteudo = this.target.path("/projetos/1").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		Assert.assertEquals("Minha loja", projeto.getNome());		
	}
	
	@Test
	public void testaPostDeCarrinho() {
		Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        String xml = carrinho.toXML();
        
        Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);

        Response response = this.target.path("/carrinhos").request().post(entity);
        
        Assert.assertEquals(201, response.getStatus());
//        String location = response.getHeaderString("Location");
//        String conteudo = client.target(location).request().get(String.class);
//        Assert.assertTrue(conteudo.contains("Tablet"));
	}
}
