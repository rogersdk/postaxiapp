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
import com.taxiapp.centraldetaxi.Taxi;
import com.taxiapp.exceptions.PedidoEmAbertoException;
import com.taxiapp.exceptions.TaxiJaCadastradoException;

public class CentralDeTaxiTest {
	private CentralDeTaxi central;

	@Before
	public void criarCentralDeTaxi() {
		central = new CentralDeTaxi();
	}

	@Test
	public void ehPossivelCadastrarUmTaxi() {
		Taxi taxi = new Taxi(123);
		central.cadastrarTaxi(taxi);
	}

	@Test(expected = TaxiJaCadastradoException.class)
	public void naoEhPossivelCadastraroOMesmoTaxiDuasVezes() {
		Taxi taxi = new Taxi(123);
		central.cadastrarTaxi(taxi);
		central.cadastrarTaxi(taxi);
	}

	@Test
	public void taxiAtualizouALocalizacao() {
		Taxi taxi = new Taxi(123);
		central.cadastrarTaxi(taxi);
		central.atualizarLocalizacao(taxi);
	}

	@Test
	public void carregaTaxisMaisProximos() {
		Taxi tA = new Taxi(123);
		Gps gA = new MockGps(10.5, 11.2);
		tA.setGps(gA);

		Taxi tB = new Taxi(456);
		Gps gB = new MockGps(5.5, 5.2);
		tB.setGps(gB);

		Taxi tC = new Taxi(789);
		Gps gC = new MockGps(50.5, 50.2);
		tC.setGps(gC);
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
		tA.setGps(gA);

		Taxi tB = new Taxi(1234);
		Gps gB = new MockGps(-4.0, -3.0);
		tB.setGps(gB);

		assertEquals(5.0, central.getDistancia(0.0, 0.0, tA), 0.001);
		assertEquals(5.0, central.getDistancia(0.0, 0.0, tB), 0.001);
	}
	
	@Test
	public void ehPossivelCadastrarCliente(){
		Cliente cliente = new Cliente("NomeDoCliente");	
		central.cadastrarCliente(cliente);
	}
	
	@Test(expected = PedidoEmAbertoException.class)
	public void testaClienteSolicitaTaxiVariasComOutroHaPedidoAberto(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps gps = new MockGps(4.0,3.0);
		central.cadastrarPedidoCliente(cliente, gps);
	}
	
	
	@Test
	public void testaClienteSolicitarTaxi(){
		Cliente cliente = new Cliente("NomeDoCliente");
		central.cadastrarCliente(cliente);
		Gps localizacaoCliente = new MockGps(4.0, 3.0);
		
		central.cadastrarPedidoCliente(cliente, localizacaoCliente);
		assertEquals(cliente,central.getPedidoDoCliente(cliente).getCliente());
	}
		
}