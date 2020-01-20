package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{

	/*
	 * Ao inv�s de instanciar um new Dep... e gerar um acoplamento forte 
	 * eu irei realizar uma invers�o de controle atrav�s de uma inje��o de depend�ncia com um m�todo.
	 * 
	 */
	
	private DepartmentService service;
	
	@FXML
	private TableView<Departamento> tableViewDepartment;
		
	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Departamento, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	
	/*Passo 2
	 * Associar o obsList com o atributo tableViewDepartment e assim os departamentos ir�o aparecer na tela.
	 * */
	private ObservableList<Departamento> obsList;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	public void setDepartmentservice(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
		
	}

	private void initializeNodes() {
		
		//Este comnado � um padr�o do JavaFx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		
		//Este � um macete para fazer a tabela acompanhar o tamanho da tela
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Fazer um downCating
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		
		
	}
	
	
	/* Passo 1
	 * Este m�todo a baixo ser� respons�vel por:
	 * 	acessar o servi�o;
	 *  carregar os departamentos;
	 *  jogar os departamentos no atributo obsList.
	 * */
	public void updatetableView() {
		//Este if ser� necess�rio somente pelo fato de n�o estarmos utilizando um framework 
		if(service == null) {
			throw new IllegalStateException("O service estava nulo!");
		}
		List<Departamento> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
}
