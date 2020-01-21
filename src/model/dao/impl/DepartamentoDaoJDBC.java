package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {
	
	private Connection conn;
	
	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Departamento obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO department (DepName) " + 
									   "VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			
			int linhaAfetada = st.executeUpdate();
			
			if (linhaAfetada > 0) {
				ResultSet rs = st.getGeneratedKeys();
				
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
				
			}else {
				throw new DbException("Nenhuma linha foi afetada!");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Departamento obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE department " + 
										"SET DepName = ? " + 
										"WHERE id = ?");

			st.setString(1, obj.getNome());
			st.setInt(2, obj.getId());
			
			int linhaAfetada = st.executeUpdate();
			
			if (linhaAfetada == 0) {
				throw new DbException("Id inexistente!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM department " + 
									   "WHERE id = ?");
			st.setInt(1, id);
			
			int linhaAfetada = st.executeUpdate();
			
			if (linhaAfetada == 0) {
				throw new DbException("Id inexistente!");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Departamento findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * " + 
									   "FROM department " + 
									   "WHERE Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			Departamento dep = new Departamento();
			
			if(rs.next()) {
				dep.setId(rs.getInt(1));
				dep.setNome(rs.getString(2));
			}
			
			return dep;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Departamento> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM department");
			rs = st.executeQuery();
			
			List<Departamento> dep = new ArrayList<>();
			
			while (rs.next()) {
				int id = rs.getInt(1);
				String nome = rs.getString(2);
				
				dep.add(new Departamento(id, nome));
			}
			return dep;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

}
