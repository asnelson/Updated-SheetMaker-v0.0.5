/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmaker;

import java.util.Map;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Andrew Nelson
 */
public class Graph {
    TableView table;
    String fileName;
    
    public Graph(TableView table, String fileName) {
        this.table = table;
        this.fileName = fileName;
    }
    
    public void Grapher( TableView table, Pane p, String xKey, String yKey) {
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
 
    public void Grapher(TableView table, Pane p, String xKey, String yKey, String size) {
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
