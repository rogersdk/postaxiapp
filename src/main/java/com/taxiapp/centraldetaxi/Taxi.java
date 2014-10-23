package com.taxiapp.centraldetaxi;

import java.util.Observable;
import java.util.Observer;

public class Taxi implements Observer {
	private Observable centralDeTaxi;
	private int numero;
	private Gps gps;

	public Taxi(int numero) {
		this.numero = numero;
	}

	public int getNumero() {
		return this.numero;
	}

	public Gps getGps() {
		return gps;
	}

	private void setGps(Gps gps) {
		this.gps = gps;
	}

	public void atualizarLocalizacao(Gps gps) {
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
		// double distancia = this.gps.calculaDistancia((Gps) arg1);

		Pedido pedido = (Pedido) arg1;

		if (!pedido.isAtendido()) {
			double distancia = this.gps.calculaDistancia(pedido.getCliente()
					.getGps());

			if (distancia <= CentralDeTaxi.RAIO && !pedido.isAtendido()) {
				System.out.println("Existe um pedido de taxi para "
						+ pedido.getCliente().getNome() + ", está a cerca de "
						+ distancia + " metros. Deseja atendê-lo "
						+ this.getNumero() + "?");
			}
		}

		if (pedido.isAtendido() && !pedido.getTaxi().equals(this)) {
			System.out.println("Aviso ao taxi " + this.getNumero()
					+ ", o pedido do(a) " + pedido.getCliente().getNome()
					+ " já foi atendido.");
		}

		/*
		 * if(distancia <= CentralDeTaxi.RAIO && pedido.isAtendido()){
		 * System.out.println("asaoisaoi"); }
		 * 
		 * if(pedido.isAtendido() && !pedido.isRecebimento()){
		 * System.out.println("éagora"); }
		 */

		/*
		 * if (centralDeTaxi instanceof CentralDeTaxi) {
		 * if(this.gps.calculaDistancia( (Gps) arg1) <= CentralDeTaxi.RAIO){
		 * System.out.println("Taxi "+this.numero+
		 * " recebe notificação de novo pedido de taxi registrado."); }
		 * CentralDeTaxi centralDeTaxi = (CentralDeTaxi) centralInfSubject; }
		 */

	}

	public void setCentralDeTaxi(Observable centralDeTaxi) {
		this.centralDeTaxi = centralDeTaxi;
	}

}
