package test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.JsonUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Proj2a implements EntryPoint, ClickHandler
	{
		VerticalPanel mainPanel = new VerticalPanel();
		String baseURL = "http://localhost:3000";
		ArrayList<MyStudent> students;
		JsArray<Student> jsonData;
		Button loginButton = new Button("Login");
		Button addButton = new Button("Add");
		Button deleteButton = new Button("Delete");
		Button editButton = new Button("Edit");
		MyStudent selectedStudent = null;
		Button addStudentButton = new Button("Add Student");
		Button editStudentButton = new Button("Save Student");
		TextBox fnBox = new TextBox();
		TextBox lnBox = new TextBox();
		TextBox majBox = new TextBox();
		TextBox userBox = new TextBox();
		TextBox passBox = new TextBox();
		
		private static class MyStudent
		{
			private int id;
			private String first_name;
			private String last_name;
			private String major;
			
			public MyStudent(int id, String fn, String ln, String maj)
			{
				this.id = id;
				this.first_name = fn;
				this.last_name = ln;
				this.major = maj;
			}
		}
		public void onModuleLoad()
		{
			login();
			loginButton.addClickHandler(this);
			addButton.addClickHandler(this);
			deleteButton.addClickHandler(this);
			editButton.addClickHandler(this);
			addStudentButton.addClickHandler(this);
			editStudentButton.addClickHandler(this);
			RootPanel.get().add(mainPanel);
			//setupAddStudent();
		}
		
		public void onClick(ClickEvent e)
		{
			Object source = e.getSource();
			if (source == addStudentButton) {
				String url = baseURL + "/students/createStudent";
				String postData = URL.encode("first_name") + "=" +
					URL.encode(fnBox.getText().trim()) + "&" +
					URL.encode("last_name") + "=" +
					URL.encode(lnBox.getText().trim()) + "&" +
					URL.encode("major") + "=" +
					URL.encode(majBox.getText().trim());
				fnBox.setText("");
				lnBox.setText("");
				majBox.setText("");
				postRequest(url,postData,"postStudent");
			}
			else if (source == addButton) {
				setupAddStudent();
			}
			else if (source == deleteButton) {
				String url = baseURL + "/students/deleteStudent";
				String postData = URL.encode("student_id") + "=" +
				URL.encode("" + selectedStudent.id);
				postRequest(url,postData,"deleteStudent");
			}
			else if (source == editStudentButton) {
				String url = baseURL + "/students/editStudent";
				String postData = URL.encode("student_id") + "=" +
						URL.encode("" + selectedStudent.id) + "&" +
					URL.encode("first_name") + "=" +
					URL.encode(fnBox.getText().trim()) + "&" +
					URL.encode("last_name") + "=" +
					URL.encode(lnBox.getText().trim()) + "&" +
					URL.encode("major") + "=" +
					URL.encode(majBox.getText().trim());
				fnBox.setText("");
				lnBox.setText("");
				majBox.setText("");
				postRequest(url,postData,"editStudent");
			}
			else if (source == editButton) {
				setupEditStudent();
			}
			else if (source == loginButton) {
				String url = baseURL + "/admins/login";
				String postData = URL.encode("username") + "=" +
				   URL.encode(userBox.getText().trim()) + "&" +
				   URL.encode("password") + "=" +
				   URL.encode(passBox.getText().trim());
				userBox.setText("");
				passBox.setText("");
				postRequest(url,postData,"loginAdmin");
				
			}
		}
		public void getRequest(String url, final String getType) {
			final RequestBuilder rb = new
				RequestBuilder(RequestBuilder.GET,url);
			try {
				rb.sendRequest(null, new RequestCallback()
				{
					public void onError(final Request request,
							final Throwable exception)
					{
						Window.alert(exception.getMessage());
					}
					public void onResponseReceived(final Request request,
							final Response response)
					{
						if (getType.equals("getStudents")) {
							showStudents(response.getText());
						}
					}
				});
			}
			catch (final Exception e) {
				Window.alert(e.getMessage());
			}
		} // end getRequest()
		public void postRequest(String url, String data,
				final String postType)
		{
			final RequestBuilder rb = new
					RequestBuilder(RequestBuilder.POST,url);
			rb.setHeader("Content-type",
					"application/x-www-form-urlencoded");
		try {
			rb.sendRequest(data, new RequestCallback()
			{
				public void onError(final Request request,
						final Throwable exception)
				{
					Window.alert(exception.getMessage());
				}
				public void onResponseReceived(final Request request,
						final Response response)
				{
					if (postType.equals("postStudent") ||
						postType.equals("deleteStudent") || postType.equals("editStudent"))	{
						mainPanel.clear();
						String url = baseURL + "/students/index.json";
						getRequest(url,"getStudents");
					}
					else if (postType.equals("loginAdmin"))	{
						if (response.getText() == "1")	{
							mainPanel.clear();
							String url = baseURL + "/students/index.json";
							getRequest(url,"getStudents");
						}
						else
							login();
						
					}
				}
			});
		}
		catch (Exception e) {
			Window.alert(e.getMessage());
		}
	} // end postRequest()

	private void showStudents(String responseText)
	{
		//mainPanel.clear();
		jsonData = getData(responseText);
		students = new ArrayList<MyStudent>();
		Student student = null;
		for (int i = 0; i < jsonData.length(); i++) {
			student = jsonData.get(i);
			students.add(new MyStudent(student.getID(),
				student.getFirstName(), student.getLastName(),
				student.getMajor()));
		}
		CellTable<MyStudent> table = new CellTable<MyStudent>();
		
		TextColumn<MyStudent> fnameCol =
			new TextColumn<MyStudent>()
			{
				@Override
				public String getValue(MyStudent student)
				{
					return student.first_name;
				}
			};

		//Make first name sortable	
		fnameCol.setSortable(true);
		
		
		
		TextColumn<MyStudent> lnameCol =
			new TextColumn<MyStudent>()
			{
				@Override
				public String getValue(MyStudent student)
				{
					return student.last_name;
				}
			};
		
		//Make last name sortable	
		lnameCol.setSortable(true);
		
		TextColumn<MyStudent> majorCol =
			new TextColumn<MyStudent>()
			{						
				@Override
				public String getValue(MyStudent student)
				{
					return student.major;
				}
			};
		//Make major sortable	
		majorCol.setSortable(true);

	    // Create a data provider.
	    ListDataProvider<MyStudent> dataProvider = new ListDataProvider<MyStudent>();
	    
	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(table);


	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<MyStudent> list = dataProvider.getList();
	    for (MyStudent stud : students) {
		      list.add(stud);
		    }

	    ListHandler<MyStudent> columnSortHandler = new ListHandler<MyStudent>(list);
	    columnSortHandler.setComparator(fnameCol, new Comparator<MyStudent>() {
	              public int compare(MyStudent o1, MyStudent o2) {
	                if (o1 == o2) {
	                  return 0;
	                }

	                // Compare the name columns.
	                if (o1 != null) {
	                  return (o2 != null) ? o1.first_name.compareTo(o2.first_name) : 1;
	                }
	                return -1;
	              }
	            });
	    columnSortHandler.setComparator(lnameCol, new Comparator<MyStudent>() {
            public int compare(MyStudent o1, MyStudent o2) {
              if (o1 == o2) {
                return 0;
              }

              // Compare the name columns.
              if (o1 != null) {
                return (o2 != null) ? o1.last_name.compareTo(o2.last_name) : 1;
              }
              return -1;
            }
          });
	    columnSortHandler.setComparator(majorCol, new Comparator<MyStudent>() {
            public int compare(MyStudent o1, MyStudent o2) {
              if (o1 == o2) {
                return 0;
              }

              // Compare the name columns.
              if (o1 != null) {
                return (o2 != null) ? o1.major.compareTo(o2.major) : 1;
              }
              return -1;
            }
          });
	        table.addColumnSortHandler(columnSortHandler);

	        // We know that the data is sorted alphabetically by default.
	        table.getColumnSortList().push(fnameCol);
	        table.getColumnSortList().push(lnameCol);
	        table.getColumnSortList().push(majorCol);
	       
	        // Add it to the root panel.
	        RootPanel.get().add(table);

		
		
		//majorCol.setSortable(true);
	
			
		final SingleSelectionModel<MyStudent> selectionModel =
			new SingleSelectionModel<MyStudent>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(
			new SelectionChangeEvent.Handler()
			{
				public void onSelectionChange(SelectionChangeEvent e)
				{
					MyStudent selected = selectionModel.getSelectedObject();
					if (selected != null) {
						selectedStudent = selected;
				}
			}
		});
		table.addColumn(fnameCol, "First Name");
		table.addColumn(lnameCol, "Last Name");
		table.addColumn(majorCol, "Major");
		table.setRowCount(students.size(),true);
		table.setRowData(0,students);
		HorizontalPanel buttonRow = new HorizontalPanel();
		buttonRow.add(addButton);
		buttonRow.add(deleteButton);
		buttonRow.add(editButton);
		mainPanel.add(buttonRow);
		mainPanel.add(table);
	} // end showStudents()
	
	private void setupAddStudent()
	{
		mainPanel.clear();
		VerticalPanel addStudentPanel = new VerticalPanel();
		Label fnLabel = new Label("First Name");
		HorizontalPanel fnRow = new HorizontalPanel();
		fnRow.add(fnLabel);
		fnRow.add(fnBox);
		addStudentPanel.add(fnRow);
		Label lnLabel = new Label("Last Name");
		HorizontalPanel lnRow = new HorizontalPanel();
		lnRow.add(lnLabel);
		lnRow.add(lnBox);
		addStudentPanel.add(lnRow);
		Label majLabel = new Label("Major");
		HorizontalPanel majRow = new HorizontalPanel();
		majRow.add(majLabel);
		majRow.add(majBox);
		addStudentPanel.add(majRow);
		addStudentPanel.add(addStudentButton);
		mainPanel.add(addStudentPanel);
	}
	private void setupEditStudent()
	{
		mainPanel.clear();
		VerticalPanel editStudentPanel = new VerticalPanel();
		Label fnLabel = new Label("First Name");
		HorizontalPanel fnRow = new HorizontalPanel();
		fnRow.add(fnLabel);
		fnRow.add(fnBox);
		editStudentPanel.add(fnRow);
		Label lnLabel = new Label("Last Name");
		HorizontalPanel lnRow = new HorizontalPanel();
		lnRow.add(lnLabel);
		lnRow.add(lnBox);
		editStudentPanel.add(lnRow);
		Label majLabel = new Label("Major");
		HorizontalPanel majRow = new HorizontalPanel();
		majRow.add(majLabel);
		majRow.add(majBox);
		editStudentPanel.add(majRow);
		editStudentPanel.add(editStudentButton);
		mainPanel.add(editStudentPanel);
		fnBox.setText(selectedStudent.first_name);
		lnBox.setText(selectedStudent.last_name);
		majBox.setText(selectedStudent.major);
	}
	private void login()
	{
		mainPanel.clear();
		VerticalPanel loginPanel = new VerticalPanel();
		Label userLabel = new Label("Username");
		HorizontalPanel userRow = new HorizontalPanel();
		userRow.add(userLabel);
		userRow.add(userBox);
		loginPanel.add(userRow);
		Label passwordLabel = new Label("Password");
		HorizontalPanel passwordRow = new HorizontalPanel();
		passwordRow.add(passwordLabel);
		passwordRow.add(passBox);
		loginPanel.add(passwordRow);
		loginPanel.add(loginButton);
		mainPanel.add(loginPanel);
	}
	private JsArray<Student> getData(String json)
	{
		return JsonUtils.safeEval(json);
	}
}