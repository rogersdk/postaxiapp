package com.taxiapp.centraldetaxi;

public class Taxi {
	private int numero;
	private Gps gps;

	public Taxi(int numero) {
		this.numero = numero;
	}
	
	public int getNumero(){
		return this.numero;
	}
	
	public Gps getGps() {
		return gps;
	}

	private void setGps(Gps gps) {
		this.gps = gps;
	}
	
	public void atualizarLocalizacao(Gps gps){
		this.setGps(gps);
	}
	
}
