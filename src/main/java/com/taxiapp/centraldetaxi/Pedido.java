package com.taxiapp.centraldetaxi;

import java.util.Date;

public class Pedido {
	private Taxi taxi;
	private Gps gps;
	private Cliente cliente;
	private Date dataHora;
	private boolean atendido = false;
	
	public Pedido(Cliente cliente, Gps gps){
		this.cliente = cliente;
		this.gps = gps;
		this.dataHora = new Date();
	}
	
	public void atualizarHorarioAtendimento(Date dataHora){
		this.dataHora = dataHora;
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
	
	public Date getDataHora(){
		return this.dataHora;
	}
}
