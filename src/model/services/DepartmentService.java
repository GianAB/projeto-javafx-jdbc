package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartmentService {
	
	public List<Departamento> findAll() {
		/*
		 * Por enquanto n�o iremos buscar os dados do BD, iremos somente MOCK'ar. 
		 * 
		 * MOCK: � o que chamamos em programa��o quando iremos retornar dados de mentirinha.
		 * 
		 * */
		
		List<Departamento> list = new ArrayList<>();
		list.add(new Departamento(1, "Livros"));
		list.add(new Departamento(2, "Computadores"));
		list.add(new Departamento(3, "Eletr�nicos"));
		return list;
		
	}
}
