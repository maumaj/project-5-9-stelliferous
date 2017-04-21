/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author mauletj
 */
public class CopyCompositionActions {
    
    MainController mainController;
    
    CopyCompositionActions(MainController givenMainController){
        this.mainController = givenMainController;
        System.out.println(mainController);
    }
    
    /**
     * Copies the specified NoteRectangles and the gestures that contain them
     * into a string to be placed on the clipboard. 
     * @param copiedNotes
     * @param shift
     * @return 
     */
    protected String notesToString(ArrayList<NoteRectangle> copiedNotes, Boolean shift){
        
        //initalize the strings used to store the composition data
        String noteString = "";
        String gestureString = "";
        
        //if told to shift notes to the right, specify the distance
        double shiftNoteByX = 0;
        if (shift) {
            shiftNoteByX = 4;
        }
        
        ArrayList<ArrayList<NoteRectangle>> copiedGestureList = new ArrayList<>();
        for(int w = 0; w < copiedNotes.size(); w++){
            
            //adds to string based on the attributes of the NoteRectangle
            NoteRectangle currentRect = copiedNotes.get(w);
            noteString += (currentRect.getX()+shiftNoteByX) + ";";
            noteString += currentRect.getY() + ";";
            noteString += currentRect.getWidth() + ";";
            noteString += currentRect.getInstrument();
                        noteString += "&";

            //find which gestures contain this note, keep track of the index
            //of all notes in those gestures
            for (int g = 0; g < mainController.gestureModelController.gestureNoteGroups.size(); g++){
                System.out.println("looking at a gesture");
                ArrayList<NoteRectangle> currentGesture = mainController.gestureModelController.gestureNoteGroups.get(g);
                if (currentGesture.contains(currentRect) && !copiedGestureList.contains(currentGesture)){
                    System.out.println("found a gesture");
                    copiedGestureList.add(currentGesture);
                    for(int p=0; p < currentGesture.size();p++){
                        System.out.println("looking THROUGH a gesture");
                        gestureString += copiedNotes.indexOf(currentGesture.get(p)) +"&";
                    }
                    gestureString += "@";
                }
                }
            }
        
        //combine and return the strings with NoteRectangle and gesture data
        noteString +=  "--"  + gestureString;
        return noteString;
    }
    
    /**
     * Translates notes from a string into the composition of NoteRectangles 
     * and their gestures
     * @param noteString takes a string of composition notes
     */
    protected void notesFromString(String noteString){
       String[] notesAndGestures = noteString.split("--");
       String[] individualNoteArray = (notesAndGestures[0]).split("&");

       ArrayList<NoteRectangle> pastedNotes = translatePastedNoteRectangles(individualNoteArray);
       
       initializePastedNotes(pastedNotes);

       //adds any gestures
       if(notesAndGestures.length > 1){
            initializePastedGestures(notesAndGestures, pastedNotes);
       }
    }
    
    private ArrayList<NoteRectangle> translatePastedNoteRectangles(String[] individualNoteArray){
       ArrayList<NoteRectangle> pastedNotes = new ArrayList<>();
       
       //translates list of NoteRectangles
       for (int j = 0; j < individualNoteArray.length; j++){
           String[] noteAttributes = individualNoteArray[j].split(";");
           System.out.println("Notes: "+Arrays.toString(individualNoteArray));
           System.out.println("Note Attributes: "+Arrays.toString(noteAttributes));
           double xLocation = Double.parseDouble(noteAttributes[0]);
           double yLocation = Double.parseDouble(noteAttributes[1]);
           double width = Double.parseDouble(noteAttributes[2]);
           String instrumentString = noteAttributes[3];
           Instrument instrument = Instrument.valueOf(instrumentString);
           pastedNotes.add(new NoteRectangle(xLocation,yLocation,instrument, width, mainController));
       }
        return pastedNotes;
    }
    
    /**
     * Initializes and adds pasted NoteRectangles
     * @param pastedNotes list of notes to initialize
     */
    private void initializePastedNotes(ArrayList<NoteRectangle> pastedNotes){
       for (int o = 0; o < pastedNotes.size(); o++){
           NoteRectangle note = pastedNotes.get(o);
           mainController.rectList.add(note);
           mainController.compositionController.rectAnchorPane.getChildren().add(note.notes);
       }
    }
    
    /**
     * Initializes and adds pasted gestures.
     * @param notesAndGestures a string of notes and gestures to pasted
     * @param pastedNotes an ArrayList of NoteRectangles to paste to 
     */
    private void initializePastedGestures(String[] notesAndGestures, ArrayList<NoteRectangle> pastedNotes){
        ArrayList<ArrayList<NoteRectangle>> pastedGestures = new ArrayList<>();
           String[] individualGestureArray = (notesAndGestures[1]).split("@");
           for (int g = 0; g < individualGestureArray.length; g++){
               ArrayList<NoteRectangle> notesInGesture = new ArrayList<>();
               String[] gestureIndices = individualGestureArray[g].split("&");
               for (int q = 0; q < gestureIndices.length; q++){
                   notesInGesture.add(pastedNotes.get(q));
               }
               pastedGestures.add(notesInGesture);
               mainController.gestureModelController.gestureNoteGroups.add(notesInGesture);
               mainController.gestureModelController.resetGestureRectangle(notesInGesture);
               mainController.gestureModelController.updateGestureRectangle(notesInGesture, "red");
           }
    }
    
    
    /**
     * Allows the user to select a txt file from which to copy notes into
     * their composition.
     * @return a string describing the notes
     * @throws FileNotFoundException 
     */
    protected String readFile() throws FileNotFoundException{
        String noteString = "";
        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(fileStage);
        fileStage.show();
        if (selectedFile != null) {
            Scanner scanner = new Scanner(selectedFile);
            while (scanner.hasNext()){
                noteString += scanner.next();
            }
        }
        fileStage.close();
        return noteString;
    }
    
    /**
     * Chooses a txt file to which to copy the composition's notes.
     * Note: The txt file must be preexisting.
     * @throws IOException 
     */
    protected void copySelectedNotesToFile() throws IOException{
        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose txt file to Save To");
        File selectedFile = fileChooser.showOpenDialog(fileStage);
        fileStage.show();
        if (selectedFile != null) {
            saveFile(notesToString(mainController.selectedNotes,false),selectedFile);
        }
        fileStage.close();
    }
    
    /**
     * Allows the user to write/copy selected notes to a txt file in the proper
     * syntax.
     * @param noteString a string representing the current composition
     * @param file a file to save the string to 
     */
    protected void saveFile(String noteString, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(noteString);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
}
