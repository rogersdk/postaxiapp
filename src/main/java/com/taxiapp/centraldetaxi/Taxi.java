package com.taxiapp.centraldetaxi;

import java.util.Observable;
import java.util.Observer;

public class Taxi implements Observer{
	private Observable centralDeTaxi;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gps == null) ? 0 : gps.hashCode());
		result = prime * result + numero;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Taxi other = (Taxi) obj;
		if (gps == null) {
			if (other.gps != null)
				return false;
		} else if (!gps.equals(other.gps))
			return false;
		if (numero != other.numero)
			return false;
		return true;
	}

	@Override
	public void update(Observable centralInfSubject, Object arg1) {
		// TODO Auto-generated method stub
		if (centralDeTaxi instanceof CentralDeTaxi) {
			CentralDeTaxi centralDeTaxi = (CentralDeTaxi) centralInfSubject;
			System.out.println("Taxi "+this.numero+" recebe notificação de novo pedido de taxi registrado.");
		}
	}
	
	public void setCentralDeTaxi(Observable centralDeTaxi){
		this.centralDeTaxi = centralDeTaxi;
	}
	
}
