package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {

	private Connection conn;

	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Vendedor obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO seller" + 
									   "(Name, Email, BirthDate, BaseSalary, DepartmentId) " + 
									   "VALUES " + "(?, ?, ?, ?, ?)", 
									   Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataAniversario().getTime()));
			st.setDouble(4, obj.getSalario());
			st.setInt(5, obj.getDepartamento().getId());

			int linhasAfetadas = st.executeUpdate();

			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);

			} else {
				throw new DbException("Erro insperado: nenhuma linha afetada!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Vendedor obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE seller " + 
					"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + 
					"WHERE Id = ?");

			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataAniversario().getTime()));
			st.setDouble(4, obj.getSalario());
			st.setInt(5, obj.getDepartamento().getId());
			st.setInt(6, obj.getId());
			
			int linha = st.executeUpdate();
			
			if (linha == 0) {
				throw new DbException("Id requisitado não existe!");
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
			st = conn.prepareStatement("DELETE FROM seller " + 
									   "WHERE id = ?");
			st.setInt(1, id);
			
			int linha = st.executeUpdate();
			
			if (linha == 0) {
				throw new DbException("Id requisitado não existe!");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Vendedor findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT seller.*, department.DepName " 
									+ "FROM seller INNER JOIN department "
									+ "ON seller.DepartmentId = department.Id "
									+ "WHERE seller.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Departamento dep = instanciandoDepartment(rs);

				Vendedor obj = instanciandoVendedor(rs, dep);

				return obj;
			}
			return null;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {

			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	private Vendedor instanciandoVendedor(ResultSet rs, Departamento dep) throws SQLException {
		Vendedor obj = new Vendedor();
		obj.setId(rs.getInt("Id"));
		obj.setNome(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setSalario(rs.getDouble("BaseSalary"));
		obj.setDataAniversario(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		obj.setDepartamento(dep);

		return obj;
	}

	private Departamento instanciandoDepartment(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setNome(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Vendedor> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.DepName " 
									+ "FROM seller INNER JOIN department "
									+ "ON seller.DepartmentId = department.Id "
									+ "ORDER BY Name");

			rs = st.executeQuery();

			List<Vendedor> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();

			while (rs.next()) {
				Departamento dep = map.get(rs.getInt("DepartmentId")); // Para não instanciar duas vezes o mesmo
																		// departamento

				if (dep == null) {
					dep = instanciandoDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Vendedor obj = instanciandoVendedor(rs, dep);
				list.add(obj);

			}
			return list;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {

			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Vendedor> findByDepartment(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.DepName " 
									+ "FROM seller INNER JOIN department "
									+ "ON seller.DepartmentId = department.Id " 
									+ "WHERE seller.DepartmentId = ? "
									+ "ORDER BY Name;");

			st.setInt(1, departamento.getId());
			rs = st.executeQuery();

			List<Vendedor> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();

			while (rs.next()) {
				Departamento dep = map.get(rs.getInt("DepartmentId")); // Para não instanciar duas vezes o mesmo
																		// departamento

				if (dep == null) {
					dep = instanciandoDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Vendedor obj = instanciandoVendedor(rs, dep);
				list.add(obj);

			}
			return list;

		} catch (SQLException e) {

			throw new DbException(e.getMessage());

		} finally {

			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

}
