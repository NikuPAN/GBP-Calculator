/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gbp_cal;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import gbp_cal.endec.endec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author YAMATO
 */
public class GBP_Cal_Deck {

    /**
     * @param args the command line arguments
     */
    private static String API_version = "Build 34";
    private ArrayList<card> cardList = new ArrayList<card>();
    private ArrayList<card> comboList1 = new ArrayList<card>();
    private ArrayList<card> comboList2 = new ArrayList<card>();
    private ArrayList<card> comboList3 = new ArrayList<card>();
    private ArrayList<card> comboList4 = new ArrayList<card>();
    private ArrayList<card> comboList5 = new ArrayList<card>();
    private final String[] bandString = {"Poppin' Party", "Afterglow", 
        "Pastel*Palettes", "Roselia", "Hello, Happy World!"};
    private final String[] buffnames = {"<なし>", 
                                    "PPP-戸山 香澄","PPP-花園 たえ","PPP-牛込 りみ","PPP-山吹 沙綾","PPP-市ヶ谷 有咲",
                                    "AG-美竹 蘭","AG-青葉 モカ","AG-上原 ひまり","AG-宇田川 巴","AG-羽沢 つぐみ",
                                    "P*P-丸山 彩","P*P-氷川 日菜","P*P-白鷺 千聖","P*P-大和 麻弥","P*P-若宮 イヴ",
                                    "R-湊 友希那","R-氷川 紗夜","R-今井 リサ","R-宇田川 あこ","R-白金 燐子",
                                    "HHW-弦巻 こころ","HHW-瀬田 薫","HHW-北沢 はぐみ","HHW-松原 花音","HHW-奥沢 美咲"};
    private final String[] fullnames = {"<なし>", 
                                    "戸山 香澄","花園 たえ","牛込 りみ","山吹 沙綾","市ヶ谷 有咲",
                                    "美竹 蘭","青葉 モカ","上原 ひまり","宇田川 巴","羽沢 つぐみ",
                                    "丸山 彩","氷川 日菜","白鷺 千聖","大和 麻弥","若宮 イヴ",
                                    "湊 友希那","氷川 紗夜","今井 リサ","宇田川 あこ","白金 燐子",
                                    "弦巻 こころ","瀬田 薫","北沢 はぐみ","松原 花音","奥沢 美咲",};
    private final String[] filterList = {"<なし>", 
                                "PPP", "AG", "P*P", "Roselia", "HHW"};
    private final String[] filterList2 = {"<なし>", 
                                "★1","★2","★3","★4","★3↑"};
    private final String[] filterList3 = {"<なし>", 
                                "パワフル","クール","ピュア","ハッピー"};
    private int[][] memberValues = {{0,0,0}, {0,0,0}, {0,0,0}, {0,0,0}, {0,0,0}}; // Perf, Tech & Vis
    private int[][] memberBandTypes = {{-1,-1}, {-1,-1}, {-1,-1}, {-1,-1}, {-1,-1}};  // Band & Type
    private String[] memberNames = {"N/A", "N/A", "N/A", "N/A", "N/A"};
    private final double[] mi_effect = {4.5, 4.0, 3.5, 3.0, 2.5, 2.0, 0.0}; // music instruments
    private final double[] pf_effect = {10.0, 9.0, 8.0, 7.0, 6.0, 0.0};     // poster & fryer
    private final double[] fd_effect = {10.0, 7.0, 5.0, 3.0, 1.0, 0.0};     // food 
    private final double[] ryu_effect = {10.0, 7.0, 5.0, 3.0, 1.0, 0.0};    // ryuusei
    
    private final String[] mi_itemLevel = {"6","5","4","3","2","1","0"};    // Music Instruments
    private final String[] pf_itemLevel = {"5","4","3","2","1","0"};        // Poster & Fryer
    private final String[] fd_itemLevel = {"5","4","3","2","1","0"};        // Food
    private final String[] ryu_itemLevel = {"5","4","3","2","1","0"};       // Ryuusei
    
    private final String[] auto_itemLevel = {"MAX","5","4","3","2","1","0"};       // Auto
    
    private final String[] types = {"パワフル","クール","ピュア","ハッピー"};
    private final String[] eventTypes = {"<なし>","パワフル","クール","ピュア","ハッピー"};
    private final String[] bounsTypes = {"<なし>","パフォー","テクニック","ビジュアル"};
    // event attributes
    private int eventType = -1;
    private int bonusType = -1;
    private String[] eventBuffMember = {"N/A", "N/A", "N/A", "N/A", "N/A"};
    private endec e;
    public static String getAPI_Version() {
        return API_version;
    }
    //------------------------ Beta ----------------------------
    /** Application name. */
    private static final String APPLICATION_NAME = "GBP Calculator";
    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/gbp_cal");
    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            GBP_Cal_Deck.class.getResourceAsStream("client_secret2.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public void getUpdateData() throws IOException {
        this.cardList.clear(); // dump all old data
        this.comboList1.clear();
        this.comboList2.clear();
        this.comboList3.clear();
        this.comboList4.clear();
        this.comboList5.clear();
        this.buildCardList();
    }
    private void buildCardList() throws IOException {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        //String spreadsheetId = "1F9ECGyjbY-V2LzrIGQuRFTUVCTmmm6uC8F_EMn-h5Bg";
        String spreadsheetId = this.e.decrypt(Sets.r_v2k(), Sets.r_v2v(), Sets.r_v2());
        String range = "Member(Dev)!A3:O";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        
    	cardList.add( new card("<なし>", "N/A", "N/A", 0, "N/A", 0, 0, 0, "4ia63Ib.png", "N/A") );
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            int cardID, rarity;
            String pic;
            for (int i = 0; i < values.size(); i++) {
                cardID = Integer.parseInt(values.get(i).get(0).toString());
                rarity = Integer.parseInt(values.get(i).get(4).toString());
                if(values.get(i).size() < 14) 
                    pic = "4ia63Ib.png"; // 沒圖片不幫你們讀取(?
                else {
                    pic = ( (values.get(i).size() > 14) ? values.get(i).get(14) : values.get(i).get(13) ).toString();
                }
                //System.out.println(pic);
                cardList.add( new card(
                        values.get(i).get(1).toString()
                      , values.get(i).get(2).toString()
                      , values.get(i).get(3).toString()
                      , rarity
                      , values.get(i).get(5).toString()
                      , Integer.parseInt(values.get(i).get(6).toString())
                      , Integer.parseInt(values.get(i).get(7).toString())
                      , Integer.parseInt(values.get(i).get(8).toString())
                      , values.get(i).get(9).toString()
                      , Integer.parseInt(values.get(i).get(10).toString())
                      , Float.parseFloat(values.get(i).get(11).toString())
                      , pic
                    ) 
                );
            }
        }
        System.out.println("Card List Created. There are "+(cardList.size()-1)+" cards in total.");
        this.comboList1 = this.cardList;
        this.comboList2 = this.cardList;
        this.comboList3 = this.cardList;
        this.comboList4 = this.cardList;
        this.comboList5 = this.cardList;
    }
    
    public GBP_Cal_Deck() throws IOException {
        this.e = new endec();
        Sets.s_v2k(this.e.getKey());
        Sets.s_v2v(this.e.getBytes());
        Sets.s_v2( this.e.encrypt( (Sets.r_v2()+"RFTUVCTmmm6uC8F_EMn-h5Bg") ) );
        buildCardList();
        /*System.out.println("ArrayList<card> cardList = new ArrayList<card>();");
        for(int i = 0; i < this.charaString.length; i++) {
            System.out.println("\tcardList.add( new card(\""+charaString[i]+"\", \""
                    +returnCharaBand(charaString[i])+"\", \""
                    +returnCharaFullname(charaString[i])+"\", "
                    +cardDetail[i][0].substring(1)+", \""
                    +cardDetail[i][1]+"\", "
                    +cardData[i][0]+", "
                    +cardData[i][1]+", "
                    +cardData[i][2]+", \""
                    //+(cardData[i][0]+cardData[i][1]+cardData[i][2])+", \""
                    +"/cardIcons/card"+i+".png"
                    +"\") );");
        }*/
    }
    public ArrayList<card> returnComboList1() {
        return this.comboList1;
    }
    public ArrayList<card> returnComboList2() {
        return this.comboList2;
    }
    public ArrayList<card> returnComboList3() {
        return this.comboList3;
    }
    public ArrayList<card> returnComboList4() {
        return this.comboList4;
    }
    public ArrayList<card> returnComboList5() {
        return this.comboList5;
    }
    public void setComboList1(ArrayList<card> cards) {
        this.comboList1 = cards;
    }
    public void setComboList2(ArrayList<card> cards) {
        this.comboList2 = cards;
    }
    public void setComboList3(ArrayList<card> cards) {
        this.comboList3 = cards;
    }
    public void setComboList4(ArrayList<card> cards) {
        this.comboList4 = cards;
    }
    public void setComboList5(ArrayList<card> cards) {
        this.comboList5 = cards;
    }
    public String[] returnFilter() {
        return this.filterList;
    }
    public String[] returnFilter2() {
        return this.filterList2;
    }
    public String[] returnFilter3() {
        return this.filterList3;
    }
    public String[] getItemLevels(int type) {
        switch(type) {
            case 1: return this.mi_itemLevel;   // Music instruments
            case 2: return this.pf_itemLevel;   // Poster & Fryer
            case 3: return this.fd_itemLevel;   // Food (canteen)
            case 4: return this.ryu_itemLevel;   // Ryuusei
            case 5: return this.auto_itemLevel;   // --AUTO--
            default: return null;
        }
    }
    public int bandToNumber(String band) {
        switch(band) {
            case "Poppin' Party": case "Poppin'Party": return 0;
            case "Afterglow": return 1;
            case "Pastel*Palettes": case "Pastel* Palettes": return 2;
            case "Roselia": return 3;
            case "Hello, Happy World!": case "Hello,Happy World!": return 4;
            default: return -1;
        }
    }
    public int typeToNumber(String type) {
        switch(type) {
            case "パワフル": return 0;
            case "クール": return 1;
            case "ピュア": return 2;
            case "ハッピー": return 3;
            default: return -1;
        }
    }
    public double getEffectByLevel(int type, int level) {
        if(level < 0) 
            return 0.0;
        switch(type) {
            case 1: {   // Music instruments
                if(level >= this.mi_effect.length)
                    return 0.0;
                return this.mi_effect[level];
            }
            case 2: {   // Poster & fryer
                if(level >= this.pf_effect.length)
                    return 0.0;
                return this.pf_effect[level];
            }
            case 3: {   // Food (canteen)
                if(level >= this.fd_effect.length)
                    return 0.0;
                return this.fd_effect[level];
            } 
            case 4: {   // Ryuusei
                if(level >= this.ryu_effect.length)
                    return 0.0;
                return this.ryu_effect[level];
            }
            default: return 0.0;
        }
    }
    public void setEventType(int type) {
        if(type >= 1 && type <= 4) {
            this.eventType = type;
        } else {
            this.eventType = -1;
        }
    }
    public int getEventType() {
        return this.eventType;
    }
    public void setBonusType(int type) {
        if(type >= 1 && type <= 3) {
            this.bonusType = type;
        } else {
            this.bonusType = -1;
        }
    }
    public int getBonusType() {
        return this.bonusType;
    }
    public void setEventBuffMember(int index, String name) {
        if(index >= 0 && index < this.eventBuffMember.length) {
            this.eventBuffMember[index] = name;
        }
    }
    public String getEventBuffMember(int index) {
        if(index < 0 || index >= this.eventBuffMember.length) 
            return "N/A";
        return this.eventBuffMember[index];
    }
    public void setMemberName(int index, String name) {
        if(index >= 0 && index < this.memberNames.length) {
            this.memberNames[index] = name;
        }
    }
    public String getMemberName(int index) {
        if(index < 0 || index >= this.memberNames.length) 
            return "N/A";
        return this.memberNames[index];
    }
    public void setMemberBand(int index, int Band) {
        if(index >= 0 && index < this.memberBandTypes.length && Band >= 0 && Band < 5) {
            this.memberBandTypes[index][0] = Band;
        }
    }
    public int getMemberBand(int index) {
        if(index >= 0 && index < this.memberBandTypes.length) {
            return this.memberBandTypes[index][0];
        }
        return -1;
    }
    public void setMemberType(int index, int Type) {
        if(index >= 0 && index < this.memberBandTypes.length && Type >= 0 && Type < 4) {
            this.memberBandTypes[index][1] = Type;
        }
    }
    public int getMemberType(int index) {
        if(index >= 0 && index < this.memberBandTypes.length) {
            return this.memberBandTypes[index][1];
        }
        return -1;
    }
    public void setMemberValue(int index, int[] value) {
        if(index >= 0 && index < this.memberValues.length 
                && value.length == this.memberValues[index].length) {
            this.memberValues[index] = value;
        }
    }
    public int getMemberValue(int index, int value) {
        if(index >= 0 && index < this.memberValues.length 
                && value >= 0 && value < this.memberValues[index].length) {
            return this.memberValues[index][value];
        }
        return -1;
    }
    public int getMemberTotal(int index) {
        if(index < 0 || index >= this.memberValues.length)
            return -1;
        int total = 0;
        for (int i = 0; i < this.memberValues[index].length; i++)
            total += this.memberValues[index][i];
        return total;
    }
    public int getAllMemberTotal() {
        int sum = 0;
        for(int i = 0,j; i < this.memberValues.length; i++) {
            for(j = 0; j < this.memberValues[i].length; j++) 
               sum += this.memberValues[i][j];
        }
        return sum;
    }
    public String returnCharaBand(String chara) {
        int index; // search index
        for(int i = 0; i < this.fullnames.length; i++) {
            index = chara.indexOf(this.fullnames[i]);
            if(index != -1) {
                if(i > 0 && i <= 5) return "Poppin' Party";
                else if(i > 5 && i <= 10) return "Afterglow";
                else if(i > 10 && i <= 15) return "Hello, Happy World!";
                else if(i > 15 && i <= 20) return "Pastel*Palettes";
                else if(i > 20 && i <= 25) return "Roselia";
            }
        }
        return "N/A";
    }
    public static String shortToFull(String band) {
        switch(band) {
            case "PPP": return "Poppin' Party";
            case "AG": return "Afterglow";
            case "P*P": return "Pastel*Palettes";
            case "Roselia": case "Rose": return "Roselia";
            case "HHW": return "Hello, Happy World!";
            default: return "N/A";
        }
    }
    public String returnCharaFullname(String chara) {
        int index; // search index
        for(int i = 1; i < this.fullnames.length; i++) {
            index = chara.indexOf(this.fullnames[i]);
            if(index > -1) 
                return this.fullnames[i];
        }
        return this.fullnames[0];
    }
    public String returnCharaFullname(int index) {
        if(index < 0 || index >= this.fullnames.length)
            return this.fullnames[0];
        return this.fullnames[index];
    }
    public String[] returnAllChaBuffName() {
        return this.buffnames;
    }
    public String[] returnAllChaFullName() {
        return this.fullnames;
    }
    public String[] returnEventType() {
        return this.eventTypes;
    }
    public String[] returnBounsType() {
        return this.bounsTypes;
    }
    public String[] returnType() {
        return this.types;
    }
    public String returnType(int index) {
        if(index < 0 || index >= this.types.length) 
            return null;
        return this.types[index];
    }
    public String[] returnBand() {
        return this.bandString;
    }
    public String returnBand(int index) {
        if(index < 0 || index >= this.bandString.length) 
            return null;
        return this.bandString[index];
    }
    public ArrayList<card> updateFilteredCard(boolean filter1, int type_1, String cont_1
            , boolean filter2, int type_2, String cont_2, boolean filter3, int type_3, String cont_3) {
        ArrayList<card> ID = new ArrayList<>();
        for(int i = 0; i < cardList.size(); i++) //  assign all cards first
            ID.add(cardList.get(i));
        if(filter1) {
            ID = returnCard_ByFilter(ID, filter1, type_1, cont_1);
        }
        if(filter2) {
            ID = returnCard_ByFilter2(ID, filter2, type_2, cont_2);
        }
        if(filter3) {
            ID = returnCard_ByFilter3(ID, filter3, type_3, cont_3);
        }
        if(filter1 || filter2 || filter3) // add nashi
            ID.add(0, cardList.get(0));
        return ID;
    }
    public ArrayList<card> returnCard_ByFilter(ArrayList<card> origin, boolean filter, int type, String cont) {
        ArrayList<card> ID = new ArrayList<>();
        if(filter) {
            //ID.add(cardList.get(0)); // add "nashi"
            switch(type) {
                case 1: case 2: case 3: case 4: case 5: { // filter by rarity
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getBand().equals(shortToFull(cont)))
                            ID.add(origin.get(i));
                    }
                    break;
                }
                default: return null;
            }
        }
        return ID;
    }
    public ArrayList<card> returnCard_ByFilter2(ArrayList<card> origin, boolean filter, int type, String cont) {
        ArrayList<card> ID = new ArrayList<>();
        if(filter) {
            //ID.add(cardList.get(0)); // add "nashi"
            switch(type) {
                case 1: case 2: case 3: case 4: { // filter by rarity
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getRarity() == Integer.parseInt(cont.substring(1)))
                            ID.add(origin.get(i));
                    }
                    break;
                } 
                case 5: {
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getRarity() >= 3 && origin.get(i).getRarity() <= 4)
                            ID.add(origin.get(i));
                    }
                    break;
                } 
                default: return null;
            }
        }
        return ID;
    }
    public ArrayList<card> returnCard_ByFilter3(ArrayList<card> origin, boolean filter, int type, String cont) {
        ArrayList<card> ID = new ArrayList<>();
        if(filter) {
            //ID.add(cardList.get(0)); // add "nashi"
            switch(type) {
                case 1: case 2: case 3: case 4: { // filter by type
                    for(int i = 0; i < origin.size(); i++) {
                        if(origin.get(i).getType().equals(cont))
                            ID.add(origin.get(i));
                    }
                    break;
                }
                /*case 14: { // filter by character name
                    for(int i = 0; i < cardList.size(); i++) {
                        if(cardList.get(i).getCharaName().equals(cont))
                            ID.add(cardList.get(i));
                    }
                    break;
                }*/
                default: return null;
            }
        }
        return ID;
    }
    /*public ArrayList<card> returnCard_ByFilter(boolean filter, int type, String cont) { // return card object
        ArrayList<Integer> id = this.returnCard_IDByFilter(filter, type, cont);
        ArrayList<card> cards = new ArrayList<card>();
        for(int i = 0; i < id.size(); i++) {
            cards.add(this.cardList.get(
                    id.get(i)
            )); 
        }
        return cards;
    }*/
    public ArrayList<card> returnCard() { // return card object
        return this.cardList;
    }
    public card returnCard(int index) { // return specific card object
        if(index < 0 || index >= this.cardList.size()) 
            return null;
        return this.cardList.get(index);
    }
    public String[] returnCardName() {
        String[] cardNames = new String[this.cardList.size()];
        //for(card name : cardList) {  // for each loop
        for(int i = 0; i < cardNames.length; i++) {
            cardNames[i] = cardList.get(i).getCardName();
        }
        return cardNames;
    }
    public String[] returnCardName(int ID) {
        String[] cardNames;
        switch(ID) {
            case 1: { 
                cardNames = new String[this.comboList1.size()]; 
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = comboList1.get(i).getCardName();
                break; 
            }
            case 2: {
                cardNames = new String[this.comboList2.size()]; 
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = comboList2.get(i).getCardName();
                break;
            }
            case 3: { 
                cardNames = new String[this.comboList3.size()];
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = comboList3.get(i).getCardName();
                 break;
            }
            case 4: {
                cardNames = new String[this.comboList4.size()];
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = comboList4.get(i).getCardName();
                 break;
            }
            case 5: {
                cardNames = new String[this.comboList5.size()];
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = comboList5.get(i).getCardName();
                 break;
            }
            default: {
                cardNames = new String[this.cardList.size()];
                for(int i = 0; i < cardNames.length; i++) 
                    cardNames[i] = cardList.get(i).getCardName();
            }
        }
        return cardNames;
    }
    public int[] returnCardData(int index) { // return whole line of data
        if(index < 0 || index >= this.cardList.size())
            return null;
        int[] data = new int[3];
        data[0] = this.cardList.get(index).getPow();
        data[1] = this.cardList.get(index).getTech();
        data[2] = this.cardList.get(index).getVis();
        return data;
    }
    public int returnCardData(int index, int index2) { // returns pow, tech, vis specifically
        if(index < 0 || index >= this.cardList.size())
            return -1;
        // means index no problem
        if(index2 < 0 || index2 >= 3)
            return -1;
        switch(index2) {
            case 0: return this.cardList.get(index).getPow();
            case 1: return this.cardList.get(index).getTech();
            case 2: return this.cardList.get(index).getVis();
            default: return -1;
        }
    }
    public String returnCardDetail(int index, int index2) { // returns 
        if(index < 0 || index >= this.cardList.size())
            return "Unknown";
        if(index2 < 0 || index2 >= 2)
            return "Unknown";
        switch(index2) {
            case 0: return "★"+this.cardList.get(index).getRarity(); // cast to string, smart!
            case 1: return this.cardList.get(index).getType();  // It's a string
            default: return "Unknown";
        }
    }
    //public static void main(String[] args) {
        // TODO code application logic here
    //}
}
