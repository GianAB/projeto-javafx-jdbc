package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	/*
	 * Ao invés de instanciar um new Dep... e gerar um acoplamento forte eu irei
	 * realizar uma inversão de controle através de uma injeção de dependência com
	 * um método.
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
	private TableColumn<Departamento, Departamento> tableColumnEDIT;

	@FXML
	TableColumn<Departamento, Departamento> tableColumnREMOVE;

	@FXML
	private Button btNew;

	/*
	 * Passo 2 Associar o obsList com o atributo tableViewDepartment e assim os
	 * departamentos irão aparecer na tela.
	 */
	private ObservableList<Departamento> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Departamento obj = new Departamento();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {

		// Este comnado é um padrão do JavaFx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("nome"));

		// Este é um macete para fazer a tabela acompanhar o tamanho da tela
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Fazer um downCating
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());

	}

	/*
	 * Passo 1 Este método a baixo será responsável por: acessar o serviço; carregar
	 * os departamentos; jogar os departamentos no atributo obsList.
	 */
	public void updateTableView() {
		// Este if será necessário somente pelo fato de não estarmos utilizando um
		// framework
		if (service == null) {
			throw new IllegalStateException("O service estava nulo!");
		}
		List<Departamento> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Departamento obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));

			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);

			// Carregar janela para adicionar um novo departamento
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter department data");
			dialogStage.setScene(new Scene(pane)); // Definindo a cena como sendo uma nova cena
			dialogStage.setResizable(false); // Pode ou não redimencionar a janela
			dialogStage.initOwner(parentStage); // Quem é o stage pai desta janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // Defini se será uma janela modal ou terá outro
																// comportamento
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Departamento obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confimation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			
			try {
				service.remove(obj);
				updateTableView();
				
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}
}
