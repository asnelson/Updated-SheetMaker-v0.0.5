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
public class SheetMaker extends Application {
    
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
            Pane scatterData = ScatterPlot(tv, tabID);
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
                Pane bubbleData = BubblePlot(tv, tabID);
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
                Pane barData = BarChart(data, tabID);
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
        //ArrayList<HashMap> list = new ArrayList<HashMap>(); 
        ObservableList<Map<String, Object>> allData = FXCollections.observableArrayList();
        //HashMap[] list = new HashMap[0];
        String title = setTitle(fileName);
        //String title = fileName.getName();
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
    
    public Pane ScatterPlot(TableView table, String fileName)
    {
        ObservableList<Map<String, Object>> data = table.getItems();
        Pane plot = new Pane();
        final Stage dialog = new Stage();
        dialog.setTitle(fileName + " Scatter Plot");
        dialog.initModality(Modality.APPLICATION_MODAL);
                //dialog.initOwner();
        VBox dialogVbox = new VBox(20);
        Map<String,Object>first = data.get(0);
        LinkedList<String> choices = new LinkedList<>();
        choices.addAll(first.keySet());
        ObservableList<String> model = FXCollections.observableList(choices);
        
        ArrayList<Double[]> points = new ArrayList<Double[]>(); 
                
        HBox xaxis = new HBox();
        HBox yaxis = new HBox();
        Label xprompt = new Label("x-axis");
        Label yprompt = new Label("y-axis");
        ChoiceBox<String> xchoice = new ChoiceBox<String>(model);
        ChoiceBox<String> ychoice = new ChoiceBox<String>(model);
                
        xaxis.getChildren().addAll(xprompt,xchoice);
        yaxis.getChildren().addAll(yprompt,ychoice);
                
        //dialogVbox.getChildren().add(new Text("Scatter plot"));
        Button plotBtn = new Button("Plot");
        EventHandler<ActionEvent> plotButtonPushed = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                final Stage popup = new Stage();
                Pane newHb = new Pane();
                newHb.setPrefSize(400, 400);
                String xKey = xchoice.getValue();
                String yKey = ychoice.getValue();
                ScatterPlotter(table, newHb, xKey, yKey);

                ChangeListener<Number> listenerX = new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        newHb.getChildren().clear();
                        ScatterPlotter(table,newHb, xKey, yKey);
                    }
                };
                newHb.widthProperty().addListener(listenerX);
                newHb.heightProperty().addListener(listenerX);
                dialog.close();
                
                Scene scatterScene = new Scene(newHb,400,400);
                popup.setScene(scatterScene);
                popup.setTitle(fileName + " Scatter Plot");
                popup.show();
            }
        };
        plotBtn.setOnAction(plotButtonPushed);
        
        dialogVbox.getChildren().addAll(xaxis,yaxis,plotBtn);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
        
        return plot;
    }
    public void ScatterPlotter( TableView table, Pane p, String xKey, String yKey) {
        ObservableList<Map<String, Object>> data = table.getItems();
        long lines = data.size();
        double width = p.getWidth() - 10;
        double height = p.getHeight() - 10;
        Double[] dian = new Double[2];
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        double xMin = 0;
        double yMin = 0;

        for (int i = 0; i < lines; ++i) {
            Map<String, Object> hsh = data.get(i);
            double xcord = (Double) hsh.get(xKey);
            double ycord = (Double) hsh.get(yKey);
            if (xcord > xMax) {
                xMax = xcord;
            }
            if (ycord > yMax) {
                yMax = ycord;
            }
            if (xcord < xMin) {
                xMin = xcord;
            }
            if (ycord < yMin) {
                yMin = ycord;
            }
        }
        double xOrigin = (-xMin / (xMax - xMin));
        double yOrigin = (yMax / (yMax - yMin));
        for (int i = 0; i < lines; ++i) {
            Map<String, Object> hsh = data.get(i);
            Double xcord = (Double) hsh.get(xKey);
            Double ycord = (Double) hsh.get(yKey);
            dian[0] = xcord;
            dian[1] = ycord;
            Circle c = new Circle(5);
            
            c.setCenterX((xOrigin + (xcord / (xMax - xMin))) * width);
            
            c.setCenterY((yOrigin - (ycord / (yMax - yMin))) * height);
            
            p.getChildren().add(c);

            final int selectedLine = i; //this is the line in the table
            c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    table.getSelectionModel().select(selectedLine);
                  
                }
            } );
        }
        Line x = new Line(0, height, width, height);//Line(double startX, double startY, double endX, double endY)
        Line y = new Line(0, 0, 0, height);
        if (xMin != 0) {
            y.setEndX((-xMin / (xMax - xMin)) * width);
            y.setStartX((-xMin / (xMax - xMin)) * width);
        }
        if (yMin != 0) {
            x.setEndY((yMax / (yMax - yMin)) * height);
            x.setStartY((yMax / (yMax - yMin)) * height);
        }
        p.getChildren().addAll(x, y);
    }
 
    
    public Pane BubblePlot(TableView table, String fileName) {
        ObservableList<Map<String, Object>> data = table.getItems();
        Pane plot = new Pane();
        final Stage dialog = new Stage();
        dialog.setTitle(fileName + " Bubble Chart");
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        Map<String, Object> first = data.get(0);
        LinkedList<String> choices = new LinkedList<>();
        choices.addAll(first.keySet());
        ObservableList<String> model = FXCollections.observableList(choices);

        HBox xaxis = new HBox();
        HBox yaxis = new HBox();
        HBox zaxis = new HBox();
        Label xprompt = new Label("x-axis");
        Label yprompt = new Label("y-axis");
        Label size = new Label("size");
        ChoiceBox<String> xchoice = new ChoiceBox<String>(model);
        ChoiceBox<String> ychoice = new ChoiceBox<String>(model);
        ChoiceBox<String> zchoice = new ChoiceBox<String>(model);

        xaxis.getChildren().addAll(xprompt, xchoice);
        yaxis.getChildren().addAll(yprompt, ychoice);
        zaxis.getChildren().addAll(size, zchoice);

        //dialogVbox.getChildren().add(new Text("Bubble plot"));
        Button plotBtn = new Button("Plot");
        EventHandler<ActionEvent> plotButtonPushed = (ActionEvent event) -> {
            final Stage popup = new Stage();
            Pane newPane = new Pane();
            newPane.setPrefSize(400, 400);
            String xKey = xchoice.getValue();
            String yKey = ychoice.getValue();
            String zKey = zchoice.getValue();
            BubbleGrapher(table, newPane, xKey, yKey, zKey);
            
            ChangeListener<Number> dataListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                newPane.getChildren().clear();
                BubbleGrapher(table, newPane, xKey, yKey, zKey);
            };
            newPane.widthProperty().addListener(dataListener);
            newPane.heightProperty().addListener(dataListener);
            dialog.close();
            
            Scene BubbleChart = new Scene(newPane,400,400);
            popup.setScene(BubbleChart);
            popup.setTitle(fileName + " Bubble Chart");
            popup.show();
        };
        plotBtn.setOnAction(plotButtonPushed);

        dialogVbox.getChildren().addAll(xaxis, yaxis, zaxis, plotBtn);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();

        return plot;
    }
    
    public void BubbleGrapher(TableView table, Pane p, String xKey, String yKey, String size) {
        ObservableList<Map<String, Object>> data = table.getItems();
        long lines = data.size();
        double width = p.getWidth();
        double height = p.getHeight();

        Double[] dian = new Double[3];
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        double xMin = 0;
        double yMin = 0;
        double rMax = 0;
        for (int i = 0; i < lines; ++i) {
            Map<String, Object> hsh = data.get(i);
            double xcord = (Double) hsh.get(xKey);
            double ycord = (Double) hsh.get(yKey);
            double radius = (Double) hsh.get(size);
            if (xcord > xMax) {
                xMax = xcord;
            }
            if (ycord > yMax) {
                yMax = ycord;
            }
            if (xcord < xMin) {
                xMin = xcord;
            }
            if (ycord < yMin) {
                yMin = ycord;
            }
            if (radius > rMax){
                rMax = radius;
            }
        }
        double xOrigin = (-xMin / (xMax - xMin));
        double yOrigin = (yMax / (yMax - yMin));
        for (int i = 0; i < lines; ++i) {
            Map<String, Object> hsh = data.get(i);
            double xcord = (Double) hsh.get(xKey);
            double ycord = (Double) hsh.get(yKey);
            double radius = (Double) hsh.get(size);
            dian[0] = xcord;
            dian[1] = ycord;
            Circle c = new Circle(1);
            c.setRadius(radius/rMax * width/ 15);
            
            c.setCenterX((xOrigin + (xcord / (xMax - xMin))) * width);
            
            c.setCenterY((yOrigin - (ycord / (yMax - yMin))) * height);
            
            p.getChildren().add(c);
            final int selectedLine = i;
            c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    table.getSelectionModel().select(selectedLine); //this selects the correct line from the selected point                  
                }
            } );
        }
        Line x = new Line(10, (height - 10), (width - 10), (height - 10));//Line(double startX, double startY, double endX, double endY)
        Line y = new Line(10, 10, 10, (height - 10));
        if (xMin != 0) {
            y.setEndX((-xMin / (xMax - xMin)) * width);
            y.setStartX((-xMin / (xMax - xMin)) * width);
        }
        if (yMin != 0) {
            x.setEndY((yMax / (yMax - yMin)) * height);
            x.setStartY((yMax / (yMax - yMin)) * height);
        }
        p.getChildren().addAll(x, y);
    }

  
    public Pane BarChart(ObservableList<Map<String, Object>> data, String fileName)
    {
        Pane plot = new Pane();
        final Stage dialog = new Stage();
        dialog.setTitle(fileName + " Bar Graph");
        dialog.initModality(Modality.APPLICATION_MODAL);
                //dialog.initOwner();
        VBox dialogVbox = new VBox(20);
        Map<String,Object>first = data.get(0);
        LinkedList<String> choices = new LinkedList<>();
        choices.addAll(first.keySet());
        ObservableList<String> model = FXCollections.observableList(choices);
        
        ArrayList<Double[]> points = new ArrayList<Double[]>(); 
                
        HBox xaxis = new HBox();
        HBox yaxis = new HBox();
        Label xprompt = new Label("Please select a category");
        //Label yprompt = new Label("y-axis");
        ChoiceBox<String> xchoice = new ChoiceBox<String>(model);
        //ChoiceBox<String> ychoice = new ChoiceBox<String>(model);
                
        xaxis.getChildren().addAll(xprompt,xchoice);
        //yaxis.getChildren().addAll(yprompt,ychoice);
                
        //dialogVbox.getChildren().add(new Text("Bar Graph"));
        Button plotBtn = new Button("Plot");
        EventHandler<ActionEvent> plotButtonPushed = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                final Stage popup = new Stage();
                Pane newPane = new Pane();
                HBox hb = new HBox();
                
                
                Scene barScene = new Scene(newPane,400,400);
                
                newPane.setPrefSize(400,400);// DO THIS IN SCENE
                Line x = new Line(10,380,380,380);//Line(double startX, double startY, double endX, double endY)
                Line y = new Line(10, 10, 10, 380);
                newPane.getChildren().addAll(x,y);
                
                System.out.println(xchoice.getValue());
                
                long lines = data.size();
                String xKey = xchoice.getValue();
                //String yKey = ychoice.getValue();
                Double[]dian = new Double[2];
                double xMax = Double.MIN_VALUE;
                double yMax = Double.MIN_VALUE;
                HashMap<Object, Integer> counters = new HashMap<>();
                
                for (int i = 0; i < lines; ++i) {
                    Map<String, Object> hsh = data.get(i);
                    Object key =  hsh.get(xKey);
                    
                    if(counters.containsKey(key) == false) {
                         counters.put(key,1);
                    }
                    else
                    {
                        int count = counters.get(key)+1;
                        counters.replace(key, count);
                    }
                }
                BarGrapher(counters,newPane, barScene);
                ChangeListener<Number> dataListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    newPane.getChildren().clear();
                    BarGrapher(counters, newPane, barScene);
                };
                
                newPane.widthProperty().addListener(dataListener);
                newPane.heightProperty().addListener(dataListener);
                
                dialog.close();
                popup.setScene(barScene);
                popup.setTitle(fileName + " " + xKey + " Bar Graph");
                popup.show();
                
                
            }
        };
        plotBtn.setOnAction(plotButtonPushed);
        
        dialogVbox.getChildren().addAll(xaxis,yaxis,plotBtn);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
        
        return plot;
    
    }
    
    public void BarGrapher(HashMap<Object, Integer> map, Pane p, Scene scene) {
    
        double width = p.getWidth() - 10;
        double height = p.getHeight() - 10;
        

        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        double xMin = 0;
        double yMin = 0;
        
        double widthOfBars = width/map.size() - 4;
        double space = 0;
        // point (0, 0) = (380, 380) on default window
        
        int totalCategories = 0;
        for(Map.Entry<Object, Integer> category: map.entrySet()) {  //****THIS COUNTS HOW MANY KEY VALUE PAIRS THERE ARE****
            totalCategories = totalCategories + category.getValue();
        }
        
        for(Map.Entry<Object, Integer> category: map.entrySet()) {
            double yVal = ((double)category.getValue())/totalCategories;
            double tall = (height - 10) * yVal;
            double yStart = (height - 10) - tall;
            
            Rectangle r = new Rectangle(10 + space, yStart, widthOfBars - 2, tall);
                   
            space = space + widthOfBars; 
            
            p.getChildren().add(r);
            
        }
        
        double xOrigin = (-xMin / (xMax - xMin));
        double yOrigin = (yMax / (yMax - yMin));
        
        Line x = new Line(10, (height - 10), (width - 10), (height - 10));//Line(double startX, double startY, double endX, double endY)
        Line y = new Line(10, 10, 10, (height - 10));
        if (xMin != 0) {
            y.setEndX((-xMin / (xMax - xMin)) * width);
            y.setStartX((-xMin / (xMax - xMin)) * width);
        }
        if (yMin != 0) {
            x.setEndY((yMax / (yMax - yMin)) * height);
            x.setStartY((yMax / (yMax - yMin)) * height);
        }
        p.getChildren().addAll(x, y);
        
    }
}