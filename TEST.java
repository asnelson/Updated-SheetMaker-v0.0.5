package sheetmaker;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;

/**
 *
 * @author jiaruishe, andrewnelson
 
 * 
 * Our visualization tool is dubbed "Sheet Maker" because it takes the data
 * from the CTSV file and creates a sheet out of the data listed.
 * 
 
 */
public class TEST extends Application {
    
    @Override
    
    public void start(Stage stage) throws IOException {
    
        VBox vb = new VBox();
        // ************** create tab pane, append new data to tab pane**********
        TabPane tabpane = new TabPane();
        MenuBar mbar = new MenuBar();
        Menu file = new Menu("File");
        
        MenuItem open = new MenuItem("Open File");
        open.setOnAction(new EventHandler<ActionEvent>() {
            String fileName;
            @Override
            public void handle(ActionEvent event)
            {
                try {
                    final FileChooser fileChooser = new FileChooser();
                    File selectedFile = fileChooser.showOpenDialog(stage);
                    fileName = selectedFile.getName();
                    ObservableList<Map<String, Object>> data = generateDataInMap(selectedFile);
                    VBox vbox = new VBox();
                    tabpane.getTabs().add(createTab(data, fileName));
                    
                } catch (IOException ex) {
                }
            }        
        });
        file.getItems().add(open);
        
        Menu graph = new Menu("Graph");
        
        MenuItem scatter = new MenuItem("Scatter Plot");
        scatter.setOnAction((ActionEvent event) -> {
            Tab tab = tabpane.getSelectionModel().getSelectedItem(); //currently selected tab
            TableView<Map<String, Object>> tv = (TableView<Map<String, Object>>) tab.getContent(); //node inside currently selected
            ObservableList<Map<String, Object>> data = tv.getItems(); // observable list of data
            String tabID = tab.getId();
            Scatter sc = new Scatter(tv, tabID);
            Pane scatterData = sc.ScatterPlot();
            scatterData.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            vb.setVgrow(scatterData, Priority.ALWAYS);
            vb.getChildren().add(scatterData);
        });
        
        MenuItem bubble = new MenuItem("Bubble Chart");
        bubble.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Tab tab = tabpane.getSelectionModel().getSelectedItem(); //currently selected tab
                TableView<Map<String, Object>> tv = (TableView<Map<String, Object>>) tab.getContent(); //node inside currently selected
                ObservableList<Map<String, Object>> data = tv.getItems(); // observable list of data
                String tabID = tab.getId();
                BubbleChart bubble = new BubbleChart(tv, tabID);
                Pane bubbleData = bubble.BubblePlot();
                bubbleData.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                vb.setVgrow(bubbleData, Priority.ALWAYS);
                vb.getChildren().add(bubbleData);
            }
        });
        
        MenuItem bar = new MenuItem("Bar Graph");
        bar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Tab tab = tabpane.getSelectionModel().getSelectedItem(); //currently selected tab
                TableView<Map<String, Object>> tv = (TableView<Map<String, Object>>) tab.getContent(); //node inside currently selected
                ObservableList<Map<String, Object>> data = tv.getItems(); // observable list of data
                String tabID = tab.getId();
                BarGraph bar = new BarGraph(data, tabID);
                Pane barData = bar.BarChart();
                barData.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                vb.setVgrow(barData, Priority.ALWAYS);
                vb.getChildren().add(barData);
            }
        
        });
        
        graph.getItems().addAll(scatter, bubble, bar);
        
        mbar.getMenus().addAll(file, graph);
        vb.getChildren().addAll(mbar, tabpane);
        
        Scene scene = new Scene(vb, 700, 550);
        stage.setTitle("Sheet Maker");
        stage.setScene(scene);
        stage.show();
    
    }
    
    public String setTitle(File fileName) {
        String title = fileName.getName(); 
        return title;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public Tab createTab(ObservableList<Map<String, Object>> data, String fileName)
    {
        Tab tab = new Tab();
        tab.setText(fileName);
        tab.setId(fileName);
        VBox vb = new VBox();
        
        TableView table = setTable(data);
        long i = data.size();
        Label l = new Label("Data Size: " + i);
        //MenuBar mnb = setMenu(data);
        vb.getChildren().addAll(table,l);
        //vbox.getChildren().addAll(table);
        tab.setContent(table);
        return tab;
 
        
    }
    
    public TableView setTable(ObservableList<Map<String, Object>> data) 
    {
        Map<String,Object>first = data.get(0);
        
        TableView table= new TableView(data);
        for(String temp: first.keySet())
        {   
            TableColumn<Map, String> DataColumn = new TableColumn<>(temp);
            DataColumn.setCellValueFactory(new MapValueFactory(temp));
            DataColumn.setMinWidth(80);
            
            Callback<TableColumn<Map, String>, TableCell<Map, String>>
            cellFactoryForMap = (TableColumn<Map, String> p) -> 
            new TextFieldTableCell(new StringConverter() {
                @Override
                    public String toString(Object t) {
                    return t.toString();
                }
                @Override
                public Object fromString(String string) {
                    return string;
                }
            });
            DataColumn.setCellFactory(cellFactoryForMap);
            table.getColumns().add(DataColumn);
        }
        return table;
    }
    
    public ObservableList<Map<String, Object>> generateDataInMap(File fileName) throws IOException
    {
        ObservableList<Map<String, Object>> allData = FXCollections.observableArrayList();
        String title = setTitle(fileName);
        FileReader fr = new FileReader(fileName);
	BufferedReader br = new BufferedReader(fr);
        boolean p = true;
        String[] header = new String[0];
	try {
	    String line;
	    while ((line = br.readLine()) != null) {
	       //list.add(line); 
               line=line.replace("  ",",");
               String[] tempLine = line.split(",");
               if (p)
               {
                   header = tempLine;
                   p = false;
               }
               else
               {
                   HashMap row = new HashMap();
                   int i = 0;
                   for (String temp : tempLine) {
                        try {
                            double dtemp = Double.parseDouble(temp);
                            row.put(header[i], dtemp);
                        } catch (Exception ex) {
                            row.put(header[i], temp);
                        }
                        i++;
                    }
                   allData.add(row);
               }
	    }
	}
        catch(Exception error) {
            VBox v = new VBox();
            Label errorLabel = new Label("Error: Please select a Comma-Separated File (.csv or .txt)");    
            Button b = new Button();
            b.setText("Ok");
            v.getChildren().addAll(errorLabel, b);
            v.setAlignment(Pos.CENTER);
            v.setPadding(new Insets(15));
            Scene scene = new Scene(v, 350, 70);
            Stage errorStage = new Stage();
                errorStage.setScene(scene);
                errorStage.setTitle("Error");
                errorStage.show();
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    errorStage.close();
                }
            });
            
        }
        finally{  
		br.close();
	}
        return allData;
        
    }

}