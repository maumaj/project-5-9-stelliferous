package controller;

import static controller.Instrument.PIANO;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;

/**
 * A controller class for the menu bar that sets the actions for each button.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class MenuBarController  {
    
    //the main controller of the program
    private MainController mainController; 
    
    //allows for acces of the undo-redo controller
    private UndoRedoActions undoController;
    
    //allows for access of the redLine controller
    private RedLineController redLineController;
    
    //allows for access of the composition controller
    private CompositionController compositionController;
    
    //makes available menu items, that they may be enabled/disabled
    @FXML MenuItem undoAction;
    @FXML MenuItem redoAction;
    @FXML MenuItem selectAllAction;
    @FXML MenuItem deleteAction;
    @FXML MenuItem groupAction;
    @FXML MenuItem ungroupAction;
    @FXML MenuItem ungroupAllAction;
    @FXML MenuItem playButton;
    @FXML MenuItem stopButton;

    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     * @param aThat the controller for undo-redo
     * @param aRed the controller for the redline
     * @param aComp the controller for the composition
     */
    public void init(MainController aThis, UndoRedoActions aThat, RedLineController aRed, CompositionController aComp) {
        mainController = aThis; 
        undoController = aThat;
        redLineController = aRed;
        compositionController = aComp;
    }
    
     /**
     * Exits the program upon user clicking the X or exit button.
     * @param e on user click
     */
    @FXML
    private void handleExitAction(ActionEvent e){
        System.exit(0);
    }
                
    /**
     * Stops current playing composition, plays the composition from the
     * start and resets the red line to be visible and play from start of animation.
     * Note: alteration in MidiPlayer.java play() method makes playing from
     * the start in this manner possible.
     * @param e  on user click
     */
    @FXML
    private void handlePlayAction(ActionEvent e){
        //clears all current MidiPlayer events
        mainController.endcomp = 0;
        mainController.MidiComposition.clear();
        
        //build the MidiComposition based off of TuneRectangles
        mainController.buildMidiComposition();
     
        //defines end of the composition for the red line to stop at
        mainController.redLineController.lineTransition.setToX(mainController.endcomp);
        
        //convert endcomp from miliseconds to seconds and set it to be duration
        mainController.redLineController.lineTransition.setDuration(Duration.seconds(mainController.endcomp/100));
        
        //makes red line visible, starts MidiComposition notes, moves red line
        mainController.redLineController.redLine.setVisible(true);
        mainController.redLineController.redLine.toFront();
        mainController.MidiComposition.play();
        mainController.redLineController.lineTransition.playFromStart();
        stopButton.setDisable(false);
    }
    
     /**
     * Stops the player from playing, stops and sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        stopTune();
        stopButton.setDisable(true);
    }
    
    /**
     * Select all the rectangles created on the pane.
     * @param e  on user click
     */    
    @FXML
    private void handleSelectAllAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        stopTune();
        
        //clears currently selected notes, adds and 'highlights' all notes
        mainController.selectedNotes.clear();
        for (int i =0; i<mainController.rectList.size(); i++){
            mainController.selectedNotes.add(mainController.rectList.get(i));
        }   
        mainController.compositionController.selectRed();
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Delete all the selected rectangles.
     * @param e  on user click
     */        
    @FXML
    private void handleDeleteAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        stopTune();
        
        //removes selected notes from Pane and from list of Rectangles
        mainController.selectedNotes.forEach((NoteRectangle e1) -> {
            mainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
            mainController.rectList.remove(e1);
            for(int p = 0; p < mainController.gestureModelController.gestureNoteGroups.size();p++){
                if(mainController.gestureModelController.gestureNoteGroups.get(p).contains(e1)){
                    mainController.gestureModelController.gestureNoteGroups.remove(p);
                }
            }
        });
        //clears all selected notes from the list of selected notes
        mainController.selectedNotes.clear();
        
        //reset gesture rectangles
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        
                        mainController.undoRedoActions.undoableAction();

    }
    
    /**
     * Creates a new gesture based on the selected note rectangles.
     * @param e on grouping event
     */
    @FXML
    private void handleGroupAction(ActionEvent e){
        stopTune();
        if (mainController.selectedNotes.isEmpty()) {
            return;
        }
        ArrayList<NoteRectangle> newGesture = new ArrayList<>();
        mainController.selectedNotes.forEach((e1)-> {
            newGesture.add(e1);
        });
       
        mainController.gestureModelController.gestureNoteGroups.add(0,newGesture);
        mainController.undoRedoActions.undoableAction();
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        

    }
    
    /**
     * Ungroups the selected gesture. Removes the gesture rectangle.
     * @param e on ungrouping event
     */
    @FXML
    private void handleUngroupAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.remove(mainController.selectedNotes);
        mainController.compositionController.selectRed();
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        mainController.undoRedoActions.undoableAction();
    }  
    
    /**
     * Ungroups all groups of NoteRectangles. Returns all notes to
     * individual notes.
     * @param e on ungroup all event
     */
    @FXML
    private void handleUngroupAllAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.clear();
        mainController.gestureModelController.resetGestureRectangle(mainController.rectList);
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Undoes the most recent change to the composition.
     * @param e on undo event
     */
    @FXML
    private void handleUndoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.undoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    /**
     * Redoes the most recently undone change. Does not redo if the last event 
     * on the pane was not an undo event.
     * @param e 
     */
    @FXML
    private void handleRedoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.redoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    /**
     * Adds a beat to the composition.
     * @param e on beat addition event
     */
    @FXML
    private void handleBeat1Action(ActionEvent e){
        for (int b= 0; b < 2000; b += 120){
            NoteRectangle beat = new NoteRectangle(b, 100*Constants.HEIGHTRECTANGLE, PIANO ,100);
            mainController.compositionController.initializeNoteRectangle(beat);
            mainController.compositionController.rectAnchorPane.getChildren().add(beat.notes);  
            mainController.compositionController.rectList.add(beat);
        }
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Sets the buttons as enabled or disabled as appropriate.
     */
    protected void checkButtons() {
        if (mainController.rectList.isEmpty()) {
            selectAllAction.setDisable(true);
            playButton.setDisable(true);
        } else {
            selectAllAction.setDisable(false);
            playButton.setDisable(false);
        }
        if (mainController.selectedNotes.isEmpty()) {
            deleteAction.setDisable(true);
            groupAction.setDisable(true);
        } else {
            deleteAction.setDisable(false);
            groupAction.setDisable(false);
        }
        if (mainController.undoRedoActions.undoableStates.size()> 1 ){
            undoAction.setDisable(false);
        } else {
            undoAction.setDisable(true);
        }
        if (mainController.undoRedoActions.redoableStates.size()> 0 ){
            redoAction.setDisable(false);
        } else {
            redoAction.setDisable(true);
        }
        if (mainController.gestureModelController.gestureNoteGroups.isEmpty()) {
            ungroupAllAction.setDisable(true);
        } else {
            ungroupAllAction.setDisable(false);
        }
        if (mainController.gestureModelController.gestureNoteGroups.contains(mainController.selectedNotes)){
            ungroupAction.setDisable(false);
        } else {
            ungroupAction.setDisable(true);
        }
    }
    
    /**
     * Disables everything that should be disabled at the start of the program.
     */
    protected void everythingDisable() {
        redoAction.setDisable(true);
        selectAllAction.setDisable(true);
        deleteAction.setDisable(true);
        groupAction.setDisable(true);
        undoAction.setDisable(true);
        ungroupAction.setDisable(true);
        ungroupAllAction.setDisable(true);
        playButton.setDisable(true);
        stopButton.setDisable(true);
    }
    
    /**
     * Stops the midiplayer and redline from playing.
     */
    private void stopTune() {
        mainController.MidiComposition.stop();
        mainController.redLineController.lineTransition.stop();
        mainController.redLineController.redLine.setVisible(false);
    }
}
