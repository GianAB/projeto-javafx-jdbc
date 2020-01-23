package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.Listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Vendedor entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Departamento> comboBoxDepartment;

	@FXML
	private Label lblErrorName;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private Label lblErrorBirthDate;

	@FXML
	private Label lblErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Departamento> obsList;

	public void setSeller(Vendedor entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;

	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessage(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	// Pega os dados que foram preenchidos no formulário e retorna no objeto obj
	private Vendedor getFormData() { 
		Vendedor obj = new Vendedor();

		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "Field can't be empty");
		}
		obj.setNome(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrors("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			exception.addErrors("birthDate", "Field can't be empty"); 
		}else {
			//Pegar a data escolhida horário do pc do usuário e joga-la em uma data independente de localidade 
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataAniversario(Date.from(instant));
		}
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addErrors("baseSalary", "Field can't be empty");
		}
		obj.setSalario(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartamento(comboBoxDepartment.getValue());
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getNome());
		txtEmail.setText(entity.getEmail());
		if (entity.getDataAniversario() != null) {
			// Converter a data vinda do BD para o formato local, ou seja, do pc do usuário.
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getDataAniversario().toInstant(), ZoneId.systemDefault()));
		}
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getSalario()));

		// Se o dpt do meu vendedor for nulo ... 
		if (entity.getDepartamento() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {// Caso já tenha um dpt associado aí sim eu seleciono este departamento
			comboBoxDepartment.setValue(entity.getDepartamento());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null!");
		}
		List<Departamento> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		lblErrorName.setText(fields.contains("name")? errors.get("name") : "");
		lblErrorEmail.setText(fields.contains("email")? errors.get("email") : "");
		lblErrorBirthDate.setText(fields.contains("birthDate")? errors.get("birthDate") : "");
		lblErrorBaseSalary.setText(fields.contains("baseSalary")? errors.get("baseSalary") : "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
