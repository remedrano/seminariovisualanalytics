package com.va;

import java.util.List;

public class Grupo {

	public String nombre;
	public List<Frutales> frutales;

	public Grupo(String nombre, List<Frutales> frutales) {
		super();
		this.nombre = nombre;
		this.frutales = frutales;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Frutales> getFrutales() {
		return frutales;
	}
	public void setFrutales(List<Frutales> frutales) {
		this.frutales = frutales;
	}
	
	
}
