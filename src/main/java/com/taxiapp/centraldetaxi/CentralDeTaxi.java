package com.taxiapp.centraldetaxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taxiapp.exceptions.TaxiJaCadastradoException;

public class CentralDeTaxi {
	private List<Taxi> taxis = new ArrayList<Taxi>();
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private Map<Cliente,Pedido> pedidos = new HashMap<Cliente,Pedido>();
	
	
	// List usar tipo mais generico caso precise ser trocado;
	public void cadastrarTaxi(Taxi taxi) {
		if (taxis.contains(taxi)) {
			throw new TaxiJaCadastradoException();
		}
		taxis.add(taxi);
	}
	
	public void cadastrarCliente(Cliente cliente){
		clientes.add(cliente);
	}

	public void atualizarLocalizacao(Taxi taxi) {
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

	public double getDistancia(double latitude, double logintude, Taxi taxi) {
		double dx = latitude - taxi.getGps().getLatitude();
		double dy = logintude - taxi.getGps().getLongitude();
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public void cadastrarPedidoCliente(Cliente cliente, Gps gps){
		Pedido pedido = new Pedido(cliente, null);
		pedido.setGps(gps);
		pedidos.put(cliente, pedido);
	}
	
	public Pedido getPedidoDoCliente(Cliente cliente){
		return pedidos.get(cliente);
	}
	
}
