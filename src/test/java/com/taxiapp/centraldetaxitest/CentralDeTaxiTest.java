package com.taxiapp.centraldetaxitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.taxiapp.centraldetaxi.CentralDeTaxi;
import com.taxiapp.centraldetaxi.Cliente;
import com.taxiapp.centraldetaxi.Gps;
import com.taxiapp.centraldetaxi.MockGps;
import com.taxiapp.centraldetaxi.Pedido;
import com.taxiapp.centraldetaxi.Taxi;
import com.taxiapp.exceptions.PedidoEmAbertoException;
import com.taxiapp.exceptions.TaxiJaCadastradoException;

public class CentralDeTaxiTest {
	private CentralDeTaxi central;

	@Before
	public void criarCentralDeTaxi() {
		central = new CentralDeTaxi();
	}
	
	/**
	 * 1 - Taxi identificado pelo número
	 * */
	@Test
	public void ehPossivelCadastrarUmTaxi() {
		Taxi taxi = new Taxi(123);
		central.cadastrarTaxi(taxi);
	}

	@Test(expected = TaxiJaCadastradoException.class)
	public void naoEhPossivelCadastrarOMesmoTaxiDuasVezes() {
		Taxi taxi = new Taxi(123);
		central.cadastrarTaxi(taxi);
		central.cadastrarTaxi(taxi);
	}

	/**
	 * 2 - Cada taxi possui sua localizacao
	 * */
	@Test
	public void taxiAtualizouALocalizacao() {
		Taxi taxi = new Taxi(123);
		Gps gps = new MockGps(11.0, 14.5);
		taxi.atualizarLocalizacao(gps);
		central.cadastrarTaxi(taxi);
		Gps novaLocalizacaoTaxi = new MockGps(10.2,10.8);
		assertEquals(gps.getLatitude(), central.buscarTaxi(123).getGps().getLatitude(),0.001);
		
		taxi.atualizarLocalizacao(novaLocalizacaoTaxi);
		
		assertEquals(novaLocalizacaoTaxi.getLatitude(), central.buscarTaxi(123).getGps().getLatitude(),0.001);
	}

	@Test
	public void carregaTaxisMaisProximos() {
		Taxi tA = new Taxi(123);
		Gps gA = new MockGps(10.5, 11.2);
		tA.atualizarLocalizacao(gA);

		Taxi tB = new Taxi(456);
		Gps gB = new MockGps(5.5, 5.2);
		tB.atualizarLocalizacao(gB);

		Taxi tC = new Taxi(789);
		Gps gC = new MockGps(50.5, 50.2);
		tC.atualizarLocalizacao(gC);
		central.cadastrarTaxi(tA);
		central.cadastrarTaxi(tB);
		central.cadastrarTaxi(tC);

		List<Taxi> taxisProximos = central.getTaxiProximos(0.0, 0.0, 20.0);
		assertTrue(taxisProximos.contains(tA));
		assertTrue(taxisProximos.contains(tB));
		assertFalse(taxisProximos.contains(tC));
	}

	@Test
	public void testaCalculaDistancia() {
		Taxi tA = new Taxi(123);
		Gps gA = new MockGps(4.0, 3.0);
		tA.atualizarLocalizacao(gA);

		Taxi tB = new Taxi(1234);
		Gps gB = new MockGps(-4.0, -3.0);
		tB.atualizarLocalizacao(gB);

		assertEquals(5.0, central.getDistancia(0.0, 0.0, tA), 0.001);
		assertEquals(5.0, central.getDistancia(0.0, 0.0, tB), 0.001);
	}
	
	@Test
	public void ehPossivelCadastrarCliente(){
		Cliente cliente = new Cliente("NomeDoCliente");	
		central.cadastrarCliente(cliente);
		assertEquals(1, central.getClientes().size());
	}
	
	@Test(expected = PedidoEmAbertoException.class)
	public void testaClienteSolicitaTaxiComOutroHaPedidoAberto(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps gps = new MockGps(4.0,3.0);
		cliente.atualizarLocalizacao(gps);
		
		central.cadastrarPedidoCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
	}
	
	/**
	 * 3 - Quando um cliente solicita um taxi, informa sua localizacao
	 * */
	@Test
	public void testaClienteSolicitarTaxi(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps localizacaoCliente = new MockGps(4.0, 3.0);
		cliente.atualizarLocalizacao(localizacaoCliente);
		
		central.cadastrarCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
		
		assertEquals(cliente,central.getPedidoEmAbertoDoCliente(cliente).getCliente());
	}
	
	/**
	 * 4 - Quando um taxi for solicitado o tempo estimado para chegar deve ser calculado.
	 * Velocidade media 40km/h ou =~11m/s.
	 * Levando em consideracao que a cada 1 ponto de unidade de distancia equivale a 100 metros
	 * o resultado sera dado em segundos
	 * */
	@Test
	public void testaCalculaTempoEstimadoParaChegarTaxiAteCliente(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps localizacaoCliente = new MockGps(0.0, 0.0);
		cliente.atualizarLocalizacao(localizacaoCliente);
		
		central.cadastrarCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
		
		Taxi taxi = new Taxi(1);
		Gps localizacaoTaxi = new MockGps(9.219,6.1);
		taxi.atualizarLocalizacao(localizacaoTaxi);
		
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		
		central.taxiAceitaPedido(taxi,pedido);
		
		assertEquals(11.0, central.getDistancia(cliente, taxi), 0.1);
		//assertEquals(1, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido));
		assertEquals(10, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido));
	}
	
	
	/*
	@Test
	public void testaTaxisReceberemNotificacao(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps gpsCliente = new MockGps(4.0,3.0);
		
		Taxi taxi = new Taxi(1);
		Gps gpsTaxi = new MockGps(10.5,4.0);
		taxi.setGps(gpsTaxi);
		
		central.cadastrarCliente(cliente);
		central.cadastrarTaxi(taxi);
		
	}
	*/

	@Test
	public void testaEnviarNotificacaoTodosTaxisComRaioDefinido(){
		
	}
	
}