package com.taxiapp.centraldetaxitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Observable;

import org.junit.Before;
import org.junit.Test;

import com.taxiapp.centraldetaxi.CentralDeTaxi;
import com.taxiapp.centraldetaxi.Cliente;
import com.taxiapp.centraldetaxi.Gps;
import com.taxiapp.centraldetaxi.MockDataHora;
import com.taxiapp.centraldetaxi.MockGps;
import com.taxiapp.centraldetaxi.Pedido;
import com.taxiapp.centraldetaxi.Taxi;
import com.taxiapp.exceptions.PedidoEmAbertoException;
import com.taxiapp.exceptions.TaxiJaAtendendoPedidoException;
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
		Cliente cliente = this.criarClienteSimples();
		central.cadastrarCliente(cliente);
		assertEquals(1, central.getClientes().size());
	}
	
	@Test(expected = PedidoEmAbertoException.class)
	public void testaClienteSolicitaTaxiComOutroHaPedidoAberto(){
		Cliente cliente = this.criarClienteSimples();
		
		central.cadastrarPedidoCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
	}
	
	/**
	 * 3 - Quando um cliente solicita um taxi, informa sua localizacao
	 * */
	@Test
	public void testaClienteSolicitarTaxi(){
		Cliente cliente = this.criarClienteSimples();
		Taxi taxi = this.criaTaxiSimples(1, new MockGps(3,4));
		
		central.cadastrarCliente(cliente);
		central.cadastrarTaxi(taxi);
		
		central.cadastrarPedidoCliente(cliente);
		
		assertEquals(cliente,central.getPedidoEmAbertoDoCliente(cliente).getCliente());
	}
	
	/**
	 * 4 - Quando um taxi for solicitado o tempo estimado para chegar deve ser calculado.
	 * Velocidade media 40km/h ou =~11,1m/s.
	 * */
	@Test
	public void testaCalculaTempoEstimadoParaChegarTaxiAteCliente(){		
		Cliente cliente = this.criarClienteSimples();

		central.cadastrarCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
		
		Taxi taxi = this.criaTaxiSimples(1, new MockGps(3,4));
		
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		
		central.taxiAceitaPedido(taxi,pedido);
		
		assertEquals(5.0, central.getDistancia(cliente, taxi), 0.001);
		assertEquals(0.450, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido),0.1);
		
		taxi.atualizarLocalizacao(new MockGps(30,40));
		assertEquals(4.504, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido),0.1);
	}
	
	/**
	 * 5 - Se o taxista demorar mais de 2x o tempo estimado inicialmente, outro taxi dever ser encaminhado;
	 * */
	@Test
	public void testaTaxistaDemora2xMaisOTempoEstimadoEnviarOutroTaxi(){
		Cliente cliente = this.criarClienteSimples();
		
		central.cadastrarCliente(cliente);
		
		Taxi taxiUm = this.criaTaxiSimples(1, new MockGps(30,40));
		Taxi taxiDois = this.criaTaxiSimples(1, new MockGps(60,80));
		
		central.cadastrarTaxi(taxiUm);
		central.cadastrarTaxi(taxiDois);
		
		central.cadastrarPedidoCliente(cliente);
		
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		MockDataHora dataHoraSistema = new MockDataHora(pedido.getDataHora());
		
		central.taxiAceitaPedido(taxiUm, pedido);
		assertEquals(4.504, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido),0.001);
		assertFalse(central.verificarDemoraAtendimento(pedido));
		
		dataHoraSistema.atrasarSegundos(60);
		pedido.atualizarHorarioAtendimento(dataHoraSistema.getDate());
		
		assertTrue(central.verificarDemoraAtendimento(pedido));
		
		central.encaminharNovoTaxi(pedido, taxiDois);
		
		assertEquals(taxiDois, pedido.getTaxi());
		assertEquals(9.0, central.getTempoDeAtendimentoDoTaxiAteCliente(pedido),0.1);
	}
	
	/**
	 * 6 - o taxista pode cancelar uma corrida antes de chegar, neste caso outro taxi sera encaminhado;
	 * */
	@Test
	public void testaTaxiCancelaCorridaEnviarOutro(){
		Cliente cliente = this.criarClienteSimples();
		
		central.cadastrarCliente(cliente);
		
		Taxi taxi = this.criaTaxiSimples(1, new MockGps(30,40));
		Taxi outroTaxi = this.criaTaxiSimples(2, new MockGps(100,100));
		
		central.cadastrarTaxi(taxi);
		central.cadastrarTaxi(outroTaxi);
		
		central.cadastrarPedidoCliente(cliente);
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		
		central.taxiAceitaPedido(taxi, pedido);
		assertEquals(taxi, pedido.getTaxi());
		
		central.taxiCancelaPedido(taxi);
		assertEquals(null, pedido.getTaxi());
		
		central.taxiAceitaPedido(outroTaxi,pedido);
		assertEquals(outroTaxi, pedido.getTaxi());
	}
	
	@Test(expected=TaxiJaAtendendoPedidoException.class)
	public void testaTaxiAceitaPedidoPoremAindaEstaAtendendoOutroPedido(){
		Cliente cliente = this.criarClienteSimples();
		Cliente clienteDois = this.criarClienteSimplesDois();
		
		central.cadastrarCliente(cliente);
		central.cadastrarCliente(clienteDois);
		
		Taxi taxi = this.criaTaxiSimples(1, new MockGps(30,40));
		
		central.cadastrarTaxi(taxi);
		
		central.cadastrarPedidoCliente(cliente);
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		
		central.cadastrarPedidoCliente(clienteDois);
		Pedido pedidoDois = central.getPedidoEmAbertoDoCliente(clienteDois);
		
		central.taxiAceitaPedido(taxi, pedido);
		central.taxiAceitaPedido(taxi, pedidoDois);
		assertEquals(taxi, pedido.getTaxi());
		
		central.taxiCancelaPedido(taxi);
		assertEquals(null, pedido.getTaxi());

	}
	
	/**
	 * 7 - ao receber o cliente o taxista deve informar;
	 * */
	@Test
	public void testaTaxiInformaRecebimentoCliente(){
		Cliente cliente = this.criarClienteSimples();
		
		central.cadastrarCliente(cliente);
		central.cadastrarPedidoCliente(cliente);
		
		Taxi taxi = this.criaTaxiSimples(1, new MockGps(30,40));

		central.cadastrarTaxi(taxi);
		
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		assertFalse(pedido.isRecebimento());
		
		central.taxiAceitaPedido(taxi, pedido);
		assertEquals(taxi, pedido.getTaxi());
		
		central.taxiInformaRecebimentoCliente(taxi);
		assertTrue(pedido.isRecebimento());
	}
	
	/**
	 * Todos os taxis num raio de 5km ou 5000m devem ser notificados;
	 * */
	@Test
	public void testaSomenteTaxiNoRaioDeCincoMilMetrosRecebeNotificacao(){
		Cliente cliente = this.criarClienteSimples();
		Taxi taxi = this.criaTaxiSimples(777, new MockGps(300,400));
		Taxi taxiDois = this.criaTaxiSimples(999, new MockGps(3001,4000));
		
		central.cadastrarCliente(cliente);
		central.cadastrarTaxi(taxi);
		central.cadastrarTaxi(taxiDois);
		
		central.cadastrarPedidoCliente(cliente);
	}
	
	
	public Cliente criarClienteSimples(){
		Cliente cliente = new Cliente("NomeDoCliente");
		Gps localizacaoCliente = new MockGps(0.0, 0.0);
		cliente.atualizarLocalizacao(localizacaoCliente);
		return cliente;
	}
	
	/**
	 * Testa o fluxo desejado
	 * */
	@Test
	public void testaFluxoDesajado(){
		Cliente cliente = this.criarClienteSimples();
		Taxi taxiUm = this.criaTaxiSimples(1, new MockGps(30,40));
		Taxi taxiDois = this.criaTaxiSimples(2232, new MockGps(300,400));
		Taxi taxiTres = this.criaTaxiSimples(3, new MockGps(4000,8000));
		
		central.cadastrarCliente(cliente);
		central.cadastrarTaxi(taxiUm);
		central.cadastrarTaxi(taxiDois);
		central.cadastrarTaxi(taxiTres);
		
		central.cadastrarPedidoCliente(cliente);
		Pedido pedido = central.getPedidoEmAbertoDoCliente(cliente);
		
		central.taxiAceitaPedido(taxiUm, pedido);
		
	}
	
	public Cliente criarClienteSimplesDois(){
		Cliente cliente = new Cliente("NomeDoClienteDois");
		Gps localizacaoCliente = new MockGps(0.0, 0.0);
		cliente.atualizarLocalizacao(localizacaoCliente);
		return cliente;
	}
	
	public Taxi criaTaxiSimples(int id, Gps gps){
		Taxi taxi = new Taxi(id);
		taxi.atualizarLocalizacao(gps);
		return taxi;
	}
	
	
}