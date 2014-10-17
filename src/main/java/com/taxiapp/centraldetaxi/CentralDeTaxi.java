package com.taxiapp.centraldetaxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.taxiapp.exceptions.PedidoEmAbertoException;
import com.taxiapp.exceptions.TaxiJaCadastradoException;

public class CentralDeTaxi {
	private List<Taxi> taxis = new ArrayList<Taxi>();
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private Map<Cliente, Pedido> pedidos = new HashMap<Cliente, Pedido>();

	// List usar tipo mais generico caso precise ser trocado;
	public void cadastrarTaxi(Taxi taxi) {
		if (taxis.contains(taxi)) {
			throw new TaxiJaCadastradoException();
		}
		taxis.add(taxi);
	}
	
	public List getClientes(){
		return this.clientes;
	}

	public void cadastrarCliente(Cliente cliente) {
		clientes.add(cliente);
	}
	
	public Taxi buscarTaxi(int id){
		for(Taxi taxi: taxis){
			if(taxi.getNumero() == id){
				return taxi;
			}
		}
		return null;
	}

	public List<Taxi> getTaxiProximos(double latitude, double longitude,
			double raio) {
		List<Taxi> taxisProximos = new ArrayList<Taxi>();
		for (Taxi taxi : taxis) {
			if (getDistancia(latitude, longitude, taxi) <= raio) {
				taxisProximos.add(taxi);
			}
		}
		return taxisProximos;
	}

	public double getDistancia(double latitude, double longitude, Taxi taxi) {
		return getDistanciaEntreDoisPontos(latitude, taxi.getGps().getLatitude(), longitude, taxi.getGps().getLongitude());
	}
	
	public double getDistancia(Cliente cliente, Taxi taxi) {
		return getDistanciaEntreDoisPontos(cliente.getGps().getLatitude(), taxi.getGps().getLatitude(), cliente.getGps().getLongitude(), taxi.getGps().getLongitude());
	}
	
	public double getDistanciaEntreDoisPontos(double xB, double xA, double yB, double yA){
		return Math.sqrt( Math.pow((xB-xA), 2) + Math.pow(yB-yA, 2) );
	}

	public void cadastrarPedidoCliente(Cliente cliente) {

		if(!verificaPedidoAbertoCliente(cliente)){
			throw new PedidoEmAbertoException();
		}
		
		Pedido pedido = new Pedido(cliente, cliente.getGps());
		pedidos.put(cliente, pedido);
	}

	public Pedido getPedidoEmAbertoDoCliente(Cliente cliente) {
		for (Entry<Cliente, Pedido> entry : pedidos.entrySet()) {
			Cliente clienteKey = entry.getKey();
			Pedido pedidoValue = entry.getValue();
			if(clienteKey.equals(cliente) && !pedidoValue.isAtendido()){
				return pedidoValue;
			}
		}
		return null;
	}

	public boolean verificaPedidoAbertoCliente(Cliente cliente) {
		for (Entry<Cliente, Pedido> entry : pedidos.entrySet()) {
			Cliente clienteKey = entry.getKey();
			Pedido pedidoValue = entry.getValue();
			if(clienteKey.equals(cliente) && !pedidoValue.isAtendido()){
				return false;
			}
		}
		return true;
	}
	
	public void taxiAceitaPedido(Taxi taxi,Pedido pedido){
		pedido.setTaxi(taxi);
	}
	
	public int getTempoDeAtendimentoDoTaxiAteCliente(Pedido pedido){
		Double distancia = this.getDistancia(pedido.getCliente(), pedido.getTaxi());
		
		return distancia.intValue() / 11;
	}
	
}
