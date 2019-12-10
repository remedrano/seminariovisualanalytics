package com.va;

import java.util.List;

public class Departamento {

	public String nombre;
	public List<Grupo> grupos;
	
	
	public Departamento(String nombre, List<Grupo> grupos) {
		super();
		this.nombre = nombre;
		this.grupos = grupos;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Grupo> getGrupos() {
		return grupos;
	}
	public void setGrupos(List<Grupo> grupos) {
		this.grupos = grupos;
	}
	
	
}
