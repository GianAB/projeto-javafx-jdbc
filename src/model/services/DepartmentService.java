package model.services;

import java.util.List;

import model.dao.DaoFabrica;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartmentService {
	
	private DepartamentoDao dao = DaoFabrica.criarDepartamentoDao();
	
	public List<Departamento> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Departamento obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
	
	public void remove(Departamento obj) {
		dao.deleteById(obj.getId());
	}
	
}
