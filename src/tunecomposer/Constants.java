package tunecomposer;

/**
 * Stores all the constant for TuneComposer.java to use.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class Constants {
    
    //sets volume for the MidiPlayer's notes
    protected static final int VOLUME = 120;
    
    //sets trackIndex for the MidiPlayer's notes
    protected static final int TRACK_INDEX = 1;
    
    //define the number of total pitches to be 127
    protected static final int PITCHTOTAL = 127;
    
    //sets constant height of each rectangle
    protected static final int HEIGHTRECTANGLE = 10;
    
    //the pixel length in which a user can click to stretch a NoteRectangle
    protected static final int STRETCHZONE = 5;
    
    //defines the padding area around gesture outlines
    protected static final double GESTURERECTPADDING = 5.0;
    
    //define the original width of the noterectangles
    protected static final int ORIGINALRECTWIDTH = 100;
}
