package com.taxiapp.centraldetaxi;

public class Pedido {
	private Taxi taxi;
	private Gps gps;
	private Cliente cliente;
	private boolean atendido = false;
	
	public Pedido(Cliente cliente, Gps gps){
		this.cliente = cliente;
		this.gps = gps;
	}
	
	public boolean isAtendido(){
		return atendido;
	}
	
	public Cliente getCliente(){
		return this.cliente;
	}
	
	public void setGps(Gps gps){
		this.gps = gps;
	}
	
	public void setTaxi(Taxi taxi){
		this.taxi = taxi;
	}
	
	public Taxi getTaxi(){
		return this.taxi;
	}
}
