/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taxiapp.centraldetaxi;

/**
 *
 * @author oswaldolinhares
 */
public class Taxi {
    private int numero;
    private Gps gps;
    public Taxi(int numero) {
        this.numero = numero;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }
    
    
}
