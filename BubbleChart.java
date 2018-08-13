/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmaker;

import java.util.LinkedList;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Andrew Nelson
 */
public class BubbleChart extends Graph {
    
    //private TableView table;
    //COMMON
    //private String fileName;
    
    public BubbleChart(TableView table, String fileName) {
        super(table, fileName);
    }
    public Pane BubblePlot() {
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

}
