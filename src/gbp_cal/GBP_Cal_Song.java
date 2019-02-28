/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gbp_cal;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import static gbp_cal.GBP_Cal_Deck.getSheetsService;
import gbp_cal.endec.endec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author YAMATO
 */
public class GBP_Cal_Song {
    private static String API_version = "Build 8";
    private ArrayList<song> songList = new ArrayList<song>();
    private ArrayList<song> comboSongList = new ArrayList<song>();
    private final String[] filterList1 = {"<なし>", 
                                "オリジナル", "カバー"};
    private final String[] filterList2 = {"<なし>", 
                                "PPP", "AG", "P*P", "Roselia", "HHW", "Other", "GBP!スペシャルバンド"};
    private final String[] filterList3 = {"<なし>", 
                                "20", "21", "22", "23", "24", "25", "26", "27", "28"};
    private final String[] mode = {"AP","逆餡蜜"};
    private final String[] mode2 = {"協力","ソロ"};
    private endec e;
    private bestScore bs = new bestScore();
    
    public void getUpdateData() throws IOException {
        this.songList.clear(); // dump all old data
        this.comboSongList.clear();
        this.buildSongList();
    }
    private void buildSongList() throws IOException {
        songList.add( new song() );   // nashi
        //-------- add data here---------   
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        //String spreadsheetId = "1F9ECGyjbY-V2LzrIGQuRFTUVCTmmm6uC8F_EMn-h5Bg";
        String spreadsheetId = this.e.decrypt(Sets.r_v1k(), Sets.r_v1v(), Sets.r_v1());
        String range = "Songs!B3:AL";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (int i = 0; i < values.size(); i++) {
                if(values.size() < 37)  continue; // don't read if data less than 37
                songList.add( new song(
                        values.get(i).get(0).toString(),
                        values.get(i).get(1).toString(),
                        Boolean.parseBoolean(values.get(i).get(2).toString()),
                        Integer.parseInt(values.get(i).get(3).toString()),
                        Integer.parseInt(values.get(i).get(4).toString()),
                        new int[][]{
                            {Integer.parseInt(values.get(i).get(5).toString()),Integer.parseInt(values.get(i).get(6).toString()),Integer.parseInt(values.get(i).get(7).toString()),Integer.parseInt(values.get(i).get(8).toString())}, // s1
                            {Integer.parseInt(values.get(i).get(9).toString()),Integer.parseInt(values.get(i).get(10).toString()),Integer.parseInt(values.get(i).get(11).toString()),Integer.parseInt(values.get(i).get(12).toString())}, // s2
                            {Integer.parseInt(values.get(i).get(13).toString()),Integer.parseInt(values.get(i).get(14).toString()),Integer.parseInt(values.get(i).get(15).toString()),Integer.parseInt(values.get(i).get(16).toString())}, // s3
                            {Integer.parseInt(values.get(i).get(17).toString()),Integer.parseInt(values.get(i).get(18).toString()),Integer.parseInt(values.get(i).get(19).toString()),Integer.parseInt(values.get(i).get(20).toString())}, // s4
                            {Integer.parseInt(values.get(i).get(21).toString()),Integer.parseInt(values.get(i).get(22).toString()),Integer.parseInt(values.get(i).get(23).toString()),Integer.parseInt(values.get(i).get(24).toString())}, // s5
                            {Integer.parseInt(values.get(i).get(25).toString()),Integer.parseInt(values.get(i).get(26).toString()),Integer.parseInt(values.get(i).get(27).toString()),Integer.parseInt(values.get(i).get(28).toString())}  // s6
                        },
                        Integer.parseInt(values.get(i).get(29).toString()),
                        Integer.parseInt(values.get(i).get(30).toString()),
                        new int[]{ // invert beats 逆餡蜜
                            Integer.parseInt(values.get(i).get(31).toString()),
                            Integer.parseInt(values.get(i).get(32).toString()),
                            Integer.parseInt(values.get(i).get(33).toString()),
                            Integer.parseInt(values.get(i).get(34).toString()),
                            Integer.parseInt(values.get(i).get(35).toString()),
                            Integer.parseInt(values.get(i).get(36).toString())
                        }
                ) );
            }
        }
        System.out.println("Song List Created. There are "+(songList.size()-1)+" in total.");
        this.comboSongList = this.songList;
    }
    /*public void setSongSkills() {
        for (int i = 0; i < this.songList.size(); i++) 
            this.songList.get(i)
                    .setSkills(this.skills[i]);
    }*/
    public GBP_Cal_Song() throws IOException {
        this.e = new endec();
        Sets.s_v1k(this.e.getKey());
        Sets.s_v1v(this.e.getBytes());
        Sets.s_v1( this.e.encrypt( ("1F9ECGyjbY-V2LzrIGQu"+Sets.r_v1()) ) );
        buildSongList();
    }
    public String[] returnFilter() {
        return this.filterList1;
    }
    public String[] returnFilter2() {
        return this.filterList2;
    }
    public String[] returnFilter3() {
        return this.filterList3;
    }
    public String[] returnMode() {
        return this.mode;
    }
    public String[] returnMode2() {
        return this.mode2;
    }
    public ArrayList<song> updateFilteredSong(boolean filter1, int type_1, String cont_1
            , boolean filter2, int type_2, String cont_2, boolean filter3, int type_3, String cont_3) {
        ArrayList<song> ID = new ArrayList<>();
        for(int i = 0; i < songList.size(); i++) //  assign all songs first
            ID.add(songList.get(i));
        if(filter1) {
            ID = returnSong_ByFilter(ID, filter1, type_1, cont_1);
        }
        if(filter2) {
            ID = returnSong_ByFilter2(ID, filter2, type_2, cont_2);
        }
        if(filter3) {
            ID = returnSong_ByFilter3(ID, filter3, type_3, cont_3);
        }
        if(filter1 || filter2 || filter3) // add nashi
            ID.add(0, songList.get(0));
        return ID;
    }
    public ArrayList<song> returnSong_ByFilter(ArrayList<song> origin, boolean filter, int type, String cont) {
        ArrayList<song> ID = new ArrayList<>();
        if(filter) {
            boolean Origin = (type==1);
            //ID.add(cardList.get(0)); // add "nashi"
            switch(type) {
                case 1: case 2: { // is original
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getOriginal() == Origin && i != 0)
                            ID.add(origin.get(i));
                    }
                    break;
                }
                default: return null;
            }
        }
        return ID;
    }
    public ArrayList<song> returnSong_ByFilter2(ArrayList<song> origin, boolean filter, int type, String cont) {
        ArrayList<song> ID = new ArrayList<>();
        if(filter) {
            switch(type) {
                case 1: case 2: case 3: case 4: case 5: case 7: { 
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getBand().equalsIgnoreCase(GBP_Cal_Deck.shortToFull(cont)) 
                                || origin.get(i).getBand().equalsIgnoreCase(cont))
                            ID.add(origin.get(i));
                    }
                    break;
                }
                case 6: { 
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getBand().equalsIgnoreCase("Other") 
                                || origin.get(i).getBand().equalsIgnoreCase("Glitter*Green"))
                            ID.add(origin.get(i));
                    }
                    break;
                }
                default: return null;
            }
        }
        return ID;
    }
    public ArrayList<song> returnSong_ByFilter3(ArrayList<song> origin, boolean filter, int type, String cont) {
        ArrayList<song> ID = new ArrayList<>();
        if(filter) {
            for(int i = 0; i < origin.size(); i++) {
                if(origin.get(i).getSongLevel() == Integer.parseInt(cont))
                    ID.add(origin.get(i));
            }
        }
        return ID;
    }
    public void setSongList(ArrayList<song> songs) {
        this.comboSongList = songs;
    }
    public ArrayList<song> returnSongList() {
        return this.songList;
    }
    public song returnSong(int index) { // return specific card object
        if(index < 0 || index >= this.comboSongList.size()) 
            return null;
        return this.comboSongList.get(index);
    }
    public String[] returnSongName() {
        String[] songNames = new String[this.songList.size()];
        //for(card name : cardList) {  // for each loop
        for(int i = 0; i < songNames.length; i++) {
            songNames[i] = songList.get(i).getSongName();
        }
        return songNames;
    }
    public String[] returnFilteredSongName() {
        String[] songNames = new String[this.comboSongList.size()];
        //for(card name : cardList) {  // for each loop
        for(int i = 0; i < songNames.length; i++) {
            songNames[i] = comboSongList.get(i).getSongName();
        }
        return songNames;
    }
    private void permute(int[] arr, float[] arr2, int k, 
            int deckScore, song Song, boolean fever, boolean invert) {
        int highScore = 0, tmp1;
        float tmp2;
        for(int i = k; i < arr.length; i++) {
            if(i < arr.length -1 && arr[i] == arr[i+1]) // prevent repeat
                continue;
            tmp1 = arr[i];
            arr[i] = arr[k];
            arr[k] = tmp1;
            tmp2 = arr2[i];
            arr2[i] = arr2[k];
            arr2[k] = tmp2;
            permute(arr, arr2, k+1, deckScore, Song, fever, invert);
            tmp1 = arr[k];
            arr[k] = arr[i];
            arr[i] = tmp1;
            tmp2 = arr2[k];
            arr2[k] = arr2[i];
            arr2[i] = tmp2;
        }
        if (k == arr.length -1) {    
            int newScore = calSongScore(deckScore, Song, arr2, arr, fever, invert);
            if(newScore > highScore) {
                highScore = newScore;
                bs.setMaxScore(highScore);
                bs.setScoreUpPerc(arr);
                bs.setSkillLastL(arr2);
            }
        }
    }
    public bestScore getBestScore(int deckScore, song Song, float skillLastL[], int scoreUpPerc[], boolean fever, boolean invert) {
        permute(scoreUpPerc, skillLastL, 0, deckScore, Song, fever, invert);
        return bs;
    }
    public int calSongScore(int deckScore, song Song, float skillLastL[], int scoreUpPerc[], boolean fever, boolean invert) {
        if(skillLastL.length != 6 || scoreUpPerc.length != 6) return 0;
        int score = 0; float amt = 0;
        int start, over;
        for(int i = 1, j; i <= Song.getSongNotes(); i++) { // i = note number, j = song skill

            for (j = 0; j < 6; j++) {
                start = Song.getKey_Start(j);
                if(invert)
                    if(Song.getInvertBeat(j) == 1) 
                        start++;
                over = Song.getKeyBySec(j, skillLastL[j]);
                //if(i < Song.getSongNotes()) over++; // late skills(?
                amt = getComplexScore(deckScore, Song, i);
                
                if (i > start && i <= over) {  // it is in skill region, change it
                    amt = getSkilledScore(getComplexScore(deckScore, Song, i), scoreUpPerc[j]);
                    break;
                }
            }
            if(fever) {
                amt *= (i > Song.getFeverStart() && i <= Song.getFeverEnd()? 2.0f: 1.0f);
            }
            score += amt;
        }
        return score;
    }
    public float getSkilledScore(int score, int skillPercent) {
        //return (score + score * (skillPercent / 100.0000f));
        return (int)(score * ((100 + skillPercent) / 100.0f));
    }
    public int getComplexScore(int deckScore, song Song, int combo) {
        return (int)(this.getBasicScore(deckScore, Song) 
                * this.perfectBouns() 
                * this.comboBouns(combo)); // type casting
    }
    private float getBasicScore(int deckScore, song Song) {
        return ( (deckScore * 3 * (1 + (Song.getSongLevel() - 5) * 0.01f)) / Song.getSongNotes());
    }

    private float perfectBouns() {
        /*switch(grade) {
            case 1: return 1.1f;    // Perfect
            case 2: return 0.8f;    // Great
            case 3: return 0.5f;    // Good
            default: return 0.0f;   // Bad / Miss / Other Unknown
        }*/
        return 1.1f; 
    }
    private float perfectBouns(int grade) {
        switch(grade) {
            case 1: return 1.1f;    // Perfect
            case 2: return 0.8f;    // Great
            case 3: return 0.5f;    // Good
            default: return 0.0f;   // Bad / Miss / Other Unknown
        }
    }
    private float comboBouns(int combo) { // return percentage
        int i = 0;
        float bouns = 1.0f;
        while(i < combo) {
            i++;
            if(i == 21 || i == 51 || i == 101 || i == 151 || i == 201 || i == 251 
                    || i == 301 || i == 401 || i == 501 || i == 601 || i == 701)
                bouns += 0.01;
        }
        return bouns;
    }
    /*public float comboBouns(int combo) {  // return percentage
        if(combo >= 1 && combo <= 20) return 1.0;
        else if(combo >= 21 && combo <= 50) return 1.01;
        else if(combo >= 51 && combo <= 100) return 1.02;
        else if(combo >= 101 && combo <= 150) return 1.03;
        else if(combo >= 151 && combo <= 200) return 1.04;
        else if(combo >= 201 && combo <= 250) return 1.05;
        else if(combo >= 251 && combo <= 300) return 1.06;
        else if(combo >= 301 && combo <= 400) return 1.07;
        else if(combo >= 401 && combo <= 500) return 1.08;
        else if(combo >= 501 && combo <= 600) return 1.09;
        else if(combo >= 601 && combo <= 700) return 1.10;
        else if(combo >= 701 && combo <= 1081) return 1.11;
        else return 1.0;
    }*/
}
