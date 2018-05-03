/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author jemar
 */

package herokutest01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
/**
 *
 * @author joseph.martin
 */
public class HerokuTest01  extends Application {
    private Button calbtn;
    private Button savbtn;
    private Button resbtn;
    
    private RadioButton chocbtn;
    private RadioButton vanbtn;
    private RadioButton strawbtn;
    
    private CheckBox nuts;
    private CheckBox cherries;
    
    private final double FLAVORS = 2.25; 
    private final double MITAX = .06;
    
    private String orderFilename = "orders.order";
    
    @Override
    public void start(Stage primaryStage) {
        //Button btn = new Button();
        //btn.setText("Say 'Hello World'");

        final double TOPPINGS = 0.50;      

        ToggleGroup groupBtn = new ToggleGroup();
        chocbtn = new RadioButton("Chocolate");
        chocbtn.setToggleGroup(groupBtn);
        chocbtn.setSelected(true);
        
        vanbtn = new RadioButton("Vanilla");
        vanbtn.setToggleGroup(groupBtn);
        
        strawbtn = new RadioButton("Strawberry");
        strawbtn.setToggleGroup(groupBtn);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(chocbtn, vanbtn, strawbtn);  //Put Flavors in one column.

        TitledPane flavorsTitledPane = new TitledPane("Flavors", hbox);    
        //Starting the Checkbox section.
        nuts = new CheckBox("Nuts") ;
        cherries = new CheckBox("Cherrries");

        HBox cbhbox = new HBox();
        cbhbox.getChildren().addAll(nuts, cherries);  //Put toppings together.
        TitledPane toppingsTitledPane = new TitledPane("Toppings", cbhbox);       

        //Adding the Calculate and save buttons from here.
        Handler h = new Handler();
        calbtn = new Button("Calculate Cost");
        calbtn.onActionProperty().set(h);
        
        savbtn = new Button("Save");
        savbtn.onActionProperty().set(h);
        
        resbtn = new Button("Restore");
        resbtn.onActionProperty().set(h);

        HBox calhbox = new HBox();
        calhbox.getChildren().addAll(calbtn,savbtn, resbtn);  //Put Order together.
        TitledPane orderTitledPane = new TitledPane("Order", calhbox);

        HBox totalhbox = new HBox ();
        totalhbox.getChildren().addAll (flavorsTitledPane, toppingsTitledPane, orderTitledPane);


        StackPane root = new StackPane();
        root.getChildren().add(totalhbox);
        //root.getChildren().addAll(flavorsTitledPane, toppingsTitledPane, orderTitledPane);
        Scene scene = new Scene(root, 700, 100);

        primaryStage.setTitle("Joseph Martin - Ice Cream");
        primaryStage.setScene(scene);
        primaryStage.show();        
    } 
    public static void main(String[] args) {
        launch(args);
    }
    private class Handler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if(event.getSource() == calbtn) {
                double toppingCost = 0.0;
                if(cherries.isSelected())
                    toppingCost += .50;
                if(nuts.isSelected())
                    toppingCost += .50;
                double subTotal = FLAVORS + toppingCost;
                double taxed = subTotal * MITAX;
                double totaled = subTotal + taxed;   
                
                String title = "Your Order";
                String header = String.format("Total: $%,.2f", subTotal);
                String content = String.format("Order: $%,.2f\nTax: $%,.2f\nTotal:  $%,.2f", 
                        subTotal, taxed, totaled);
                
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setTitle(title);
                a.setHeaderText(header);
                a.setContentText(content);
                a.show();                   
            }
            else if(event.getSource() == savbtn) {
                FileWriter fw = null;
                PrintWriter pw = null;
                try {            
                    fw = new FileWriter(new File(orderFilename));
                    pw = new PrintWriter(fw);
                    String flavor;
                    if(chocbtn.isSelected())
                        flavor = chocbtn.getText();
                    else if(vanbtn.isSelected())
                        flavor = vanbtn.getText();
                    else
                        flavor = strawbtn.getText();    
                    String n = (nuts.isSelected()) ? "With_Nuts" : "Without_Nuts";
                    String c = (cherries.isSelected()) ? "With_Cherries" : "Without_Cherries";     
                    pw.println(flavor);
                    pw.println(n);
                    pw.println(c);
                } 
                catch(FileNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } 
                catch (IOException ex) {
                    Logger.getLogger(IceCreamOrder.class.getName()).log(Level.SEVERE, null, ex);
                }         
                finally {
                    try {
                        if(pw != null)
                            pw.close();
                        if(fw != null)
                            fw.close();
                    } 
                    catch (IOException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }                
            }
            else { //restore if not anything else
                Scanner reader = null;
                try {
                    File file = new File(orderFilename);
                    reader = new Scanner(file);            
                    String t = reader.nextLine();
                    switch(t) {
                        case "Chocalate":
                            chocbtn.setSelected(true);
                            break;
                        case "Vanilla":
                            vanbtn.setSelected(true);
                        default:
                            strawbtn.setSelected(true);;
                    }
                    boolean hasNuts = !(reader.nextLine().contains("Without"));
                    nuts.setSelected(hasNuts);
                    boolean hasCherries = !(reader.nextLine().contains("Without"));
                    cherries.setSelected(hasCherries);
                }
                catch(FileNotFoundException e) {
                    Alert a = new Alert(AlertType.ERROR);
                    a.setTitle("Error Restoring File");
                    a.setHeaderText("Error Restoring File");
                    a.setContentText( e.getMessage());
                    a.show();    
                }     
                finally {
                    if(reader != null)
                        reader.close();
                }                
            }            
        }
    }    
}