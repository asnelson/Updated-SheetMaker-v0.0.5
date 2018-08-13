
package sheetmaker;

import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Andrew Nelson
 */
public class BarGraph {
    
    private ObservableList<Map<String, Object>> data;
    private String fileName;
    
    public BarGraph(ObservableList<Map<String, Object>> data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }
    
    public Pane BarChart()
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
