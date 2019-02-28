/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gbp_cal;

/**
 *
 * @author YAMATO
 */
public class song {
    
    private static int ID = 1;
    private int songID = 0;
    private String songName;
    private String band;
    private boolean original;
    private int songLength;
    private int songLevel;
    private int songNotes;
    private final int numOfKeys = 6;
    private int[] key_start = new int[numOfKeys], 
            key_7_0 = new int[numOfKeys], 
            key_7_5 = new int[numOfKeys],
            key_8_0 = new int[numOfKeys];
    private int feverStart;
    private int feverEnd;
    private int[] invertBeat = new int[numOfKeys];
    public song() {
        this.songName = "<なし>";
        this.original = false;
        this.band = "N/A";
        this.songLevel = 0;
        this.songNotes = 0;
        for(int i = 0; i < this.numOfKeys; i++) {
            this.key_start[i] = 0;
            this.key_7_0[i] = 0;
            this.key_7_5[i] = 0;
            this.key_8_0[i] = 0;
            this.invertBeat[i] = 0;
        }
        this.feverStart = 0;
        this.feverEnd = 0;
        this.songID = ID;
        ID++;
    }
    public song(String songName, String band, boolean original, int songLevel, int songNotes, int keys[][], int feverStart, int feverEnd, int invertBeat[]) {
        if(keys.length != numOfKeys || invertBeat.length != numOfKeys) 
            throw new IllegalArgumentException("Failed to create object. Illegal number of arguments for class song.");
        for(int i = 0; i < 6; i++) { // Safe Check
            if(keys[i].length != 4)
                throw new IllegalArgumentException("Failed to create object. Illegal number of arguments for class song.");
        }
        this.songName = songName;
        this.band = band;
        this.original = original;
        this.songLevel = songLevel;
        this.songNotes = songNotes;
        for(int i = 0; i < this.numOfKeys; i++) {
            this.key_start[i] = keys[i][0];
            this.key_7_0[i] = keys[i][1];
            this.key_7_5[i] = keys[i][2];
            this.key_8_0[i] = keys[i][3];
            this.invertBeat[i] = invertBeat[i];
        }
        this.feverStart = feverStart;
        this.feverEnd = feverEnd;
        this.songID = ID;
        ID++;
    }
    public int getInvertBeat(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return 0;
        return this.invertBeat[index];
    }
    public int[] getInvertBeats() {
        return this.invertBeat;
    }
    public void setSkills(int keys[][]) {
        for(int i = 0; i < this.numOfKeys; i++) {
            this.key_start[i] = keys[i][0];
            this.key_7_0[i] = keys[i][1];
            this.key_7_5[i] = keys[i][2];
            this.key_8_0[i] = keys[i][3];
        }
    }
    public int getSongID() {
        return this.songID;
    }
    public String getSongName() {
        return this.songName;
    }
    public String getBand() {
        return this.band;
    }
    public boolean getOriginal() {
        return this.original;
    }
    public int getLength() {
        return this.songLength;
    }
    public int getSongLevel() {
        return this.songLevel;
    }
    public int getSongNotes() {
        return this.songNotes;
    }
    public int getKey_Start(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_start[index];
    }
    public int[] getKey_Starts() {
        return this.key_start;
    }
    /*public int getKey_Over(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_over[index];
    }*/
    public int getKeyBySec(int index, float sec) { // which skill's 
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        if(sec == 7.0) return this.key_7_0[index];
        else if(sec == 7.5) return this.key_7_5[index];
        else if(sec == 8.0) return this.key_8_0[index];
        else return -1;
    }
    public int getKey_7_0(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_7_0[index];
    }
    public int getKey_7_5(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_7_5[index];
    }
    public int getKey_8_0(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_8_0[index];
    }
    public int getFeverStart() {
        return this.feverStart;
    }
    public int getFeverEnd() {
        return this.feverEnd;
    }
    /*public int getKey_Check(int index) {
        if(index < 0 || index >= this.numOfKeys) 
            return -1;
        return this.key_check[index];
    }*/
}
