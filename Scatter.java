/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmaker;

import java.util.ArrayList;
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
public class Scatter extends Graph {
    
    public Scatter(TableView table, String fileName) {
        super(table, fileName);
    }
    
    
    public Pane ScatterPlot()
    {
        ObservableList<Map<String, Object>> data = table.getItems();
        Pane plot = new Pane();
        final Stage dialog = new Stage();
        dialog.setTitle(fileName + " Scatter Plot");
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        Map<String,Object>first = data.get(0);
        LinkedList<String> choices = new LinkedList<>();
        choices.addAll(first.keySet());
        ObservableList<String> model = FXCollections.observableList(choices);
        
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
    
    public void ScatterPlotter(TableView table, Pane p, String xKey, String yKey) {
        this.Grapher(table, p, xKey, yKey);
    }
}
