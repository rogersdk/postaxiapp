package com.taxiapp.centraldetaxi;

public class Pedido {
	private Taxi taxi;
	private Cliente cliente;
	private boolean atendido = false;
	
	public Pedido(Cliente cliente, Taxi taxi){
		this.cliente = cliente;
		this.taxi = taxi;
	}
	
	public boolean isAtendido(){
		return atendido;
	}
	
	public Cliente getCliente(){
		return this.cliente;
	}
}
