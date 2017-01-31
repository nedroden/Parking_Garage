package Controller;

import Models.Location;
import Models.Simulator;
import Models.SimulatorView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

import java.util.Optional;

public class Controller {


    //make simulator object
	private Simulator sim;
    private SimulatorView simView;

    @FXML
    private Canvas _canvas;

    @FXML
    private Button button_operate1;

    @FXML
    private Button button_operate2;

    @FXML
    private Button button_operate3;

    @FXML
    private Button button_operate4;

    @FXML
    private Button button_operate5;

    @FXML
    private Button button_operate6;

    @FXML
    private TextArea textTarget;

    @FXML
    private Timeline timeline;

    public void initialize() {
        sim = new Simulator(_canvas);
        simView = sim.getSimulatorView();
    }

    @FXML
    private void closeApp(ActionEvent e) {
        System.exit(0);
    }

    @FXML
    private void tick1() {
        //call the simulator object to run for 1 tick
        tickFor(1);
    }

    @FXML
    private void tick50() {
        //call simulator object to run for 50 ticks
        tickFor(50);
    }

    @FXML
    private void tick1000() {
        //call the simulator object to run for 1000 ticks
        tickFor(1000);
    }

    @FXML
    private void tickFor(int ticks) {
        setText("I should be running for " + ticks + " ticks now");
        disableButtons(true);

        timeline = new Timeline();
        timeline.setCycleCount(ticks);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), e -> sim.tick()));

        timeline.play();
        timeline.setOnFinished(e -> disableButtons(false));
    }

    @FXML
    private void makePassHolderPlaces(){
        simView.makePassHolderPlaces();
    }

    @FXML
    private void MakePassHolderRows() {
        //setText("I should be opening a popup window now.");

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Number Input Dialog");
        dialog.setContentText("Number of rows:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String result2 = result.get();
            // Parses a integer from a String and tries to catch errors.
            int rowAmount = 0;
            try {
                rowAmount = Integer.parseInt(result2);
            } catch(NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if(rowAmount < 1) {
                    setText("Please enter an positive whole number!");
                } else {
                    simView.makePassHolderRows(rowAmount);
                }
            }
        }
    }

    @FXML
    private void makeReservationsAt() {
        // make reservations at a prompted location
        int floor = insertFloor();
        int row = insertRow();
        int place = insertPlace();

        // illegal answers return -1
        if (floor == -1 || row == -1 || place == -1) {
            setText("One or more arguments were not filled in correctly!");
        } else {
            simView.makeReservationsAt(new Location(floor, row, place));
        }
    }

    private int insertFloor() {
        // input a floor
        int floor = -1;

        TextInputDialog floorDialog = new TextInputDialog("0");
        floorDialog.setTitle("Floor Input Dialog");
        floorDialog.setHeaderText("Please enter the floor number for your reservation below. Between 0 and " + (simView.getNumberOfFloors() - 1));
        floorDialog.setContentText("Floor:");
        Optional<String> floorResult = floorDialog.showAndWait();

        if (floorResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String floorResult2 = floorResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                floor = Integer.parseInt(floorResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (floor < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (floor < simView.getNumberOfFloors()) {
                        return(floor);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(floor);
    }

    private int insertRow() {
        // input a row
        int row = -1;

        TextInputDialog rowDialog = new TextInputDialog("0");
        rowDialog.setTitle("Row Input Dialog");
        rowDialog.setHeaderText("Please enter the row number for your reservation below. Between 0 and " + (simView.getNumberOfRows() - 1));
        rowDialog.setContentText("Row:");
        Optional<String> rowResult = rowDialog.showAndWait();

        if (rowResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String rowResult2 = rowResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                row = Integer.parseInt(rowResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (row < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (row < simView.getNumberOfRows()) {
                        return(row);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(row);
    }

    private int insertPlace() {
        // input a place
        int place = -1;

        TextInputDialog placeDialog = new TextInputDialog("0");
        placeDialog.setTitle("Place Input Dialog");
        placeDialog.setHeaderText("Please enter the floor number for your reservation below. Between 0 and " + (simView.getNumberOfPlaces() - 1));
        placeDialog.setContentText("Place:");
        Optional<String> placeResult = placeDialog.showAndWait();

        if (placeResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String placeResult2 = placeResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                place = Integer.parseInt(placeResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (place < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (place < simView.getNumberOfPlaces()) {
                        return (place);
                    } else {
                        return (-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return (place);
    }

    @FXML
    private void submit() {
        // Opening a pop-up dialog window to ask for the amount of ticks, converting it to integer and calling on tickFor
        setText("I should be opening a popup window now.");

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Number Input Dialog");
        dialog.setHeaderText("Please enter the amount of ticks this program should be running for below.");
        dialog.setContentText("Number of ticks:");
        Optional<String> result = dialog.showAndWait();

        // Checking if something was filled in. No answer does nothing.
        if (result.isPresent()){
            // Turns Optional<String> into a normal String
            String result2 = result.get();
            // Parses a integer from a String and tries to catch errors.
            int ticksAmount = -1;
            try {
                ticksAmount = Integer.parseInt(result2);
            } catch(NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if(ticksAmount < 1) {
                    setText("Please enter an positive whole number!");
                } else {
                    tickFor(ticksAmount);
                }
            }
        }
    }
    @FXML
    private void getTime(){
        int[] time = sim.getTime();
        setText("Week " + time[3] + " Day " + time[2] + " Hour " + time[1] + " Minute " + time[0] );
    }

    @FXML
    private void reset() {
        // resets all parking spots to empty on click
        setText("I should be removing cars now.");
        sim.resetRevenue();
        sim.resetTime();
        simView.reset();
        setText("All cars should be gone now");
        button_operate6.setDisable(true);
    }

    @FXML
    private void getRevenue(){
        setText("The total revenue since the start is: €" + sim.getRevenue());
    }

    @FXML
    private void showAbout() {
        //show about information
        setText("Parking Simulator is a program that lets city parking Groningen see how some changes to their Parking Garage might affect business.");
    }

    private void setText(String txt) {
        textTarget.setText(txt);
    }

    private void disableButtons(boolean doDisable) {
        button_operate1.setDisable(doDisable);
        button_operate2.setDisable(doDisable);
        button_operate3.setDisable(doDisable);
        button_operate4.setDisable(doDisable);
        button_operate5.setDisable(!doDisable);
        button_operate6.setDisable(doDisable);
    }

    @FXML
    private void stop() {
        if (timeline != null) {
            timeline.stop();
            disableButtons(false);
        }
    }
}
