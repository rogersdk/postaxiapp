package com.taxiapp.centraldetaxi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import com.taxiapp.exceptions.PedidoEmAbertoException;
import com.taxiapp.exceptions.TaxiJaAtendendoPedidoException;
import com.taxiapp.exceptions.TaxiJaCadastradoException;

public class CentralDeTaxi extends Observable{
	private List<Observer> taxis = new ArrayList<Observer>();
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private Map<Cliente, Pedido> pedidos = new HashMap<Cliente, Pedido>();

	public void cadastrarTaxi(Taxi taxi) {
		if (taxis.contains(taxi)) {
			throw new TaxiJaCadastradoException();
		}
		this.taxis.add(taxi);
		this.addObserver(taxi);
		taxi.setCentralDeTaxi(this);
	}
	
	public List getClientes(){
		return this.clientes;
	}

	public void cadastrarCliente(Cliente cliente) {
		clientes.add(cliente);
	}
	
	public Taxi buscarTaxi(int id){
		Iterator iterator = taxis.iterator();
		while(iterator.hasNext()){
			Taxi taxi = (Taxi) iterator.next();
			if(taxi.getNumero() == id){
				return taxi;
			}
		}
		
		return null;
	}

	public List<Taxi> getTaxiProximos(double latitude, double longitude,
			double raio) {
		List<Taxi> taxisProximos = new ArrayList<Taxi>();
		
		Iterator iterator = taxis.iterator();
		while(iterator.hasNext()){
			Taxi taxi = (Taxi) iterator.next();
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
		setChanged();
		notifyObservers();
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
	
	public Pedido getPedidoEmAtendimentoDoTaxi(Taxi taxi){
		Iterator<Entry<Cliente, Pedido>> entries = pedidos.entrySet().iterator();
		while(entries.hasNext()){
			Entry<Cliente, Pedido> entry = (Entry) entries.next();
			Pedido pedido = entry.getValue();
			if( pedido.isAtendido() && pedido.getTaxi().equals(taxi)){
				return pedido;
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
		if(getPedidoEmAtendimentoDoTaxi(taxi) != null){
			throw new TaxiJaAtendendoPedidoException();
		}
		pedido.atender(taxi);
	}
	
	public void taxiCancelaPedido(Taxi taxi){
		Pedido pedido = this.getPedidoEmAtendimentoDoTaxi(taxi);
		pedido.cancelarTaxi();
	}
	
	public double getTempoDeAtendimentoDoTaxiAteCliente(Pedido pedido){
		if(!pedido.isAtendido()){
			throw new PedidoEmAbertoException();
		}
		double velocidadeMedia = 11.1;
		double distancia = this.getDistancia(pedido.getCliente(), pedido.getTaxi());
		
		return distancia / velocidadeMedia;
	}

	public boolean verificarDemoraAtendimento(Pedido pedido){
		long tempoMaximoAtendimento = new Double(getTempoDeAtendimentoDoTaxiAteCliente(pedido)).longValue() * 2;
		long horaPedidoMaisTempoDeAtendimento = pedido.getDataHora().getTime() + (tempoMaximoAtendimento*1000);
		long horaAtual = new Date().getTime();
		
		if(horaPedidoMaisTempoDeAtendimento < horaAtual){
			return true;
		}
		
		return false;
	}
	
	public void encaminharNovoTaxi(Pedido pedido, Taxi taxi){
		pedido.setTaxi(taxi);
	}
	
	public void taxiInformaRecebimentoCliente(Taxi taxi){
		Pedido pedido = this.getPedidoEmAtendimentoDoTaxi(taxi);
		pedido.informarRecebimento();
	}
	
}
