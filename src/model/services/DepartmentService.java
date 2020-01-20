package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartmentService {
	
	public List<Departamento> findAll() {
		/*
		 * Por enquanto não iremos buscar os dados do BD, iremos somente MOCK'ar. 
		 * 
		 * MOCK: É o que chamamos em programação quando iremos retornar dados de mentirinha.
		 * 
		 * */
		
		List<Departamento> list = new ArrayList<>();
		list.add(new Departamento(1, "Livros"));
		list.add(new Departamento(2, "Computadores"));
		list.add(new Departamento(3, "Eletrônicos"));
		return list;
		
	}
}
