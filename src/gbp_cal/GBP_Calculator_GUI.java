/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gbp_cal;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
//import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
//import javax.imageio.IIOImage;
//import javax.imageio.ImageWriteParam;
//import javax.imageio.ImageWriter;
//import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
//mport javax.imageio.stream.FileImageOutputStream;
import javax.swing.JComboBox;
//import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author YAMATO
 */
public class GBP_Calculator_GUI extends JFrame {

    /**
     * Creates new form GBP_Calculator_GUI
     */
    /*class ImagePanel extends JComponent {
        private Image image;
        public ImagePanel(javax.swing.ImageIcon image) {
            this.image = ScaledImage(image, 1920, 1080);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
        private Image ScaledImage(javax.swing.ImageIcon img, int w, int h){
            BufferedImage resizedImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = resizedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(img.getImage(), 0,0,w,h,null);
            g2.dispose();
            return resizedImage;
        }
    }*/
    private static final String backgrPath = "/bkgr/bg4.jpg";
    private static final Color backgrColor = new Color(45,45,45);
    private static final Color fontColor = new Color(210,210,210);
    private static final Color comboColor = new Color(30,30,30);
    private static final Color specFontColor = new Color(210,210,0);
    private static final Color specFontColor2 = new Color(0,210,210);
    
    private static final String title = "バンドリ！ガールズバンドパーティ！バンド総合力電卓";
    private static final String shortTitle = "バンドリ！ガルパ - バンド総合力電卓";
    private static final String version = "2.0.22 R1";
    private static final String author = "肉パン";
    private static final String[] contributor = {"katian","abc1234586(a.k.a ABC)", "血球@bloodball","山吹夏海"};
    private GBP_Cal_Deck GBP_API;
    private GBP_Cal_Song SONG_API;
    private final int bandMembers = 5;
    private final int areaItems = 14;
    private int[][] itemEffects = new int[areaItems][2]; // 0= PPP, 1=AG, 2=P*P, 3=Roselia, 4=HHW || Level 5-0
    private double[] member = new double[bandMembers];
    private double[] member_challenge = new double[bandMembers];
    private double[] challenge_buff = new double[bandMembers];
    private int basicDeckScore = 0;
    private boolean resultOnly;
    private boolean isAuto, isCalculated;
    
    public GBP_Calculator_GUI() throws AWTException, MalformedURLException, IOException {
        long start = System.currentTimeMillis();
        this.getContentPane().setBackground(backgrColor);
        //GBP_API = new GBP_Cal_Deck();
        //SONG_API = new GBP_Cal_Song();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    GBP_API = new GBP_Cal_Deck();
                } catch (IOException ex) {
                    Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    SONG_API = new GBP_Cal_Song();
                } catch (IOException ex) {
                    Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }
        //this.setContentPane(new ImagePanel(new javax.swing.ImageIcon(getClass().getResource(backgrPath))));
        initComponents();
        for(int i = 0, j; i < areaItems; i++) {
            for(j = 0; j < 2; j++)
                itemEffects[i][j] = 0;
        }
        this.loadLabel1.setVisible(false);
        updateTeamSumUp();
        /*if (SystemTray.isSupported()) {
            GBP_API.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }*/
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Calculator took "+elapsedTime+" ms to start up.");
    }
    private void updateTeamSumUp() {
        //float sum = 0.0f;
        float sum = 0, sum2 = 0;
        double addPercent;
        for(int i = 0; i < bandMembers; i++) {
            member_challenge[i] = member[i] = (float)GBP_API.getMemberTotal(i);
            challenge_buff[i] = 0.0;
        }
        for(int i = 0, j; i < bandMembers; i++) {
            if(member[i] <= 0.0f) continue; // skip the loop if unset
            if(GBP_API.getEventType() != -1) { // challenge event buff
                if(GBP_API.getMemberType(i)+1 == GBP_API.getEventType()) {  // type buff
                    challenge_buff[i] = (member_challenge[i] * 0.2);
                }
                for(int orz = 0; orz < 5; orz++) {   // member buff
                    if(GBP_API.getEventBuffMember(orz).equals("N/A")) continue;
                    if(GBP_API.getMemberName(i).equals(GBP_API.getEventBuffMember(orz))) {
                        //challenge_buff[i] += (member_challenge[i] * 0.1f);
                        challenge_buff[i] += (member_challenge[i] * 0.1);
                        if(GBP_API.getBonusType() != -1) { // Boost 50%
                            if(GBP_API.getMemberType(i)+1 == GBP_API.getEventType()) {
                                challenge_buff[i] += (float)(GBP_API.getMemberValue(i, GBP_API.getBonusType() -1) * 0.5);
                            }
                        }
                        break;
                    }
                }
                //challenge_buff[i] = Math.floor(challenge_buff[i]); // round it up.
            }
            j = 0; addPercent = 0.0; // reuse instead of create new
            while(j < areaItems) { // j from 0 to 13 represents 14 items.
                if(j >= 0 && j < 5) { // Music instruments
                    if(GBP_API.getMemberBand(i) == this.itemEffects[j][0]) { // item effects' band and types are the first number.
                        addPercent += GBP_API.getEffectByLevel(1,this.itemEffects[j][1]); // item effects' level are the second number.
                    }
                } else if (j == 5 || j == 9) { // Poster & Fryer
                    if(GBP_API.getMemberBand(i) == this.itemEffects[j][0]) { // item effects' band and types are the first number.
                        addPercent += GBP_API.getEffectByLevel(2,this.itemEffects[j][1]); // item effects' level are the second number.
                    }
                } else if (j == 11) { // Food
                    if(GBP_API.getMemberType(i) == this.itemEffects[j][0]) { // item effects' band and types are the first number.
                        addPercent += GBP_API.getEffectByLevel(3,this.itemEffects[j][1]); // item effects' level are the second number.
                    }
                } else if (j == 13) { // Ryuusei
                    if(GBP_API.getMemberType(i) == this.itemEffects[j][0]) { // item effects' band and types are the first number.
                        addPercent += GBP_API.getEffectByLevel(4,this.itemEffects[j][1]); // item effects' level are the second number.
                    }
                }
                j++; // this is important
            }
            member[i] = member[i] + (member[i] * (addPercent / 100.00));
            member_challenge[i] = (member_challenge[i] + challenge_buff[i] + (member_challenge[i] * (addPercent / 100.00)));
            sum += member[i];
            sum2 += member_challenge[i];
        }
        // System.out.println(sum);
        double res1 = Math.floor(sum);
        double res2 = Math.floor(sum2);
        //System.out.println("res1: "+res1+" res2: "+res2);
        String result;
        if(GBP_API.getEventType() == -1) {
            result = String.format("%,d", (int)res1);
            this.resultLabel5.setText("ソロ/協力ライブ:");
        } else {
            result = String.format("%,d", (int)res2);
            this.resultLabel5.setText("対バン/チャレンジ:");
        }
        //result = String.format("%,d (%,d)", sum, sum2);
        this.resultTextField.setText(""+result);
        updateResultColor();
        if(this.eventTypeCombo.getSelectedIndex() != 0) 
            basicDeckScore = (int)res2;
        else 
            basicDeckScore = (int)res1;
        this.updateSongTheoryScore();
    }
    public void updateInvertTexts(boolean set, int[] values, int[] starts) {
        if(set) {
            if(values.length != 6 || starts.length != 6) { // safe check
                updateInvertTexts(false, null, null);
                return;
            }
            int sMode = this.songCalMethod.getSelectedIndex();
            String[] str = new String[values.length];
            for(int i = 0; i < str.length; i++) 
                str[i] = (values[i]==2&&sMode==1?"逆餡蜜 ("+(starts[i]):"AP ("+(starts[i]+1)) +")";
            this.calMethodTxt1.setText(str[0]);
            this.calMethodTxt2.setText(str[1]);
            this.calMethodTxt3.setText(str[2]);
            this.calMethodTxt4.setText(str[3]);
            this.calMethodTxt5.setText(str[4]);
            this.calMethodTxt6.setText(str[5]);
        } else {
            this.calMethodTxt1.setText("特にない");
            this.calMethodTxt2.setText("特にない");
            this.calMethodTxt3.setText("特にない");
            this.calMethodTxt4.setText("特にない");
            this.calMethodTxt5.setText("特にない");
            this.calMethodTxt6.setText("特にない");
        }
    }
    //private void updateSongBestTheoryScore() {
        //bestScore bs = SONG_API.getBestScore(basicDeckScore, this.SONG_API.returnSong(SongID), skillLastL, scoreUpPerc, resultOnly, resultOnly);
        
    //}
    private void updateSongTheoryScore() {
        this.resultLabel6.setText(this.songCalMethod2.getSelectedIndex() == 0?"協力楽曲理論スコア:":"ソロ楽曲理論スコア:");
        int[] cardIDs = {
            this.memberCombo1.getSelectedIndex(),
            this.memberCombo2.getSelectedIndex(),
            this.memberCombo3.getSelectedIndex(),
            this.memberCombo4.getSelectedIndex(),
            this.memberCombo5.getSelectedIndex()
        };
        this.resultLabel7.setText("<曲名なし>");
        for(int i = 0; i < cardIDs.length; i++) 
            if(cardIDs[i] == 0) { 
                this.resultTextField2.setText("N/A"); 
                return; 
            }
        // don't calculate song score until all cards selected.
        int SongID = this.songNameCombo.getSelectedIndex();
        if(SongID <= 0) // not calculating until song are selected 
            return;
        this.resultLabel7.setText(this.songNameCombo.getSelectedItem().toString());
        float[] skillLastL = {
            Float.parseFloat(this.memSkillTimeCombo1.getSelectedItem().toString()),
            Float.parseFloat(this.memSkillTimeCombo2.getSelectedItem().toString()),
            Float.parseFloat(this.memSkillTimeCombo3.getSelectedItem().toString()),
            Float.parseFloat(this.memSkillTimeCombo4.getSelectedItem().toString()),
            Float.parseFloat(this.memSkillTimeCombo5.getSelectedItem().toString()),
            Float.parseFloat(this.memSkillTimeCombo6.getSelectedItem().toString()),
        };
        int[] scoreUpPerc = {
            this.memberSkillSlider1.getValue(),
            this.memberSkillSlider2.getValue(),
            this.memberSkillSlider3.getValue(),
            this.memberSkillSlider4.getValue(),
            this.memberSkillSlider5.getValue(),
            this.memberSkillSlider6.getValue()
        };
        updateInvertTexts(SongID!=0, this.SONG_API.returnSong(SongID).getInvertBeats()
            , this.SONG_API.returnSong(SongID).getKey_Starts());
        String result = String.format("%,d(%s)"
                , this.SONG_API.calSongScore(basicDeckScore, this.SONG_API.returnSong(SongID), skillLastL, scoreUpPerc
                        , (this.songCalMethod2.getSelectedIndex()==0)
                        , (this.songCalMethod.getSelectedIndex()==1))
                , this.songCalMethod.getSelectedItem().toString());

        if(isAuto && !isCalculated) { // automatically get best score
            bestScore bs = this.SONG_API.getBestScore(basicDeckScore, this.SONG_API.returnSong(SongID), skillLastL, scoreUpPerc
                        , (this.songCalMethod2.getSelectedIndex()==0)
                        , (this.songCalMethod.getSelectedIndex()==1));
            this.memSkillTimeCombo1.setSelectedItem(""+bs.getSkillLastL(0));
            this.memSkillTimeCombo2.setSelectedItem(""+bs.getSkillLastL(1));
            this.memSkillTimeCombo3.setSelectedItem(""+bs.getSkillLastL(2));
            this.memSkillTimeCombo4.setSelectedItem(""+bs.getSkillLastL(3));
            this.memSkillTimeCombo5.setSelectedItem(""+bs.getSkillLastL(4));
            this.memSkillTimeCombo6.setSelectedItem(""+bs.getSkillLastL(5));
            this.memberSkillSlider1.setValue(bs.getScoreUpPerc(0));
            this.memberSkillSlider2.setValue(bs.getScoreUpPerc(1));
            this.memberSkillSlider3.setValue(bs.getScoreUpPerc(2));
            this.memberSkillSlider4.setValue(bs.getScoreUpPerc(3));
            this.memberSkillSlider5.setValue(bs.getScoreUpPerc(4));
            this.memberSkillSlider6.setValue(bs.getScoreUpPerc(5));
            isCalculated = true;
        }
        resultTextField2.setText(result);
    }
    private void updateResultColor() {
        switch(this.eventTypeCombo.getSelectedIndex()) {
            case 1: {
                this.resultTextField.setForeground(new Color(255,50,50)); break; }
            case 2: {
                this.resultTextField.setForeground(new Color(45,45,255)); break; }
            case 3: {
                this.resultTextField.setForeground(new Color(0,205,0)); break; }
            case 4: {
                this.resultTextField.setForeground(new Color(255,175,0)); break; }
            default: {
                this.resultTextField.setForeground(this.fontColor);
                break; 
            }
        }
    }
    public void displayTray() throws AWTException, java.net.MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("/bandIcons/garupa_icon.png");
        //Image image = new javax.swing.ImageIcon(getClass().getResource("/bandIcons/garupa_icon.png"));
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        trayIcon.displayMessage(shortTitle+" "+version, "notification demo", TrayIcon.MessageType.INFO);
    }
    private void setBandIcon(javax.swing.JLabel label, String str) {
        if(!str.equals("N/A")) {
            label.setText("");
            label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bandIcons/band_"+(GBP_API.bandToNumber(str)+1)+".png")));
        } else {
            label.setIcon(null);
            label.setText(GBP_API.returnCharaBand(str));
        }
    }
    
    private void setTypeIcon(javax.swing.JLabel label, String str) {
        if(!str.equals("N/A")) {
            label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/typeIcons/type_"+(GBP_API.typeToNumber(str)+1)+".png")));
        } else {
            label.setIcon(null);
        }
        label.setText(str);
    }
    private void updateFilteredDeck(int updateID, JComboBox fCombo1, JComboBox fCombo2, JComboBox fCombo3, // fail
        ArrayList<card> comboList, JComboBox updateCombo) {
        boolean filter = false, filter2 = false, filter3 = false;
        int id = fCombo1.getSelectedIndex(),
            id2 = fCombo2.getSelectedIndex(),
            id3 = fCombo3.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        comboList = GBP_API
                .updateFilteredCard(filter, id, fCombo1.getSelectedItem().toString()
                        , filter2, id2, fCombo2.getSelectedItem().toString()
                        , filter3, id3, fCombo3.getSelectedItem().toString());
        updateCombo.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(updateID)
                )
        );
        updateCombo.setSelectedIndex(0);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        areaItemLabel1 = new javax.swing.JLabel();
        memberCombo1 = new javax.swing.JComboBox<String>();
        bandLabel = new javax.swing.JLabel();
        charaLabel = new javax.swing.JLabel();
        rareLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        powLabel = new javax.swing.JLabel();
        techLabel = new javax.swing.JLabel();
        visLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        memberBand1 = new javax.swing.JLabel();
        memberName1 = new javax.swing.JLabel();
        memberRare1 = new javax.swing.JLabel();
        memberType1 = new javax.swing.JLabel();
        memberPow1 = new javax.swing.JLabel();
        memberTech1 = new javax.swing.JLabel();
        memberVis1 = new javax.swing.JLabel();
        memberTotal1 = new javax.swing.JLabel();
        memberCombo2 = new javax.swing.JComboBox<String>();
        memberBand2 = new javax.swing.JLabel();
        memberName2 = new javax.swing.JLabel();
        memberRare2 = new javax.swing.JLabel();
        memberType2 = new javax.swing.JLabel();
        memberPow2 = new javax.swing.JLabel();
        memberTech2 = new javax.swing.JLabel();
        memberVis2 = new javax.swing.JLabel();
        memberTotal2 = new javax.swing.JLabel();
        memberCombo3 = new javax.swing.JComboBox<String>();
        memberBand3 = new javax.swing.JLabel();
        memberName3 = new javax.swing.JLabel();
        memberRare3 = new javax.swing.JLabel();
        memberType3 = new javax.swing.JLabel();
        memberPow3 = new javax.swing.JLabel();
        memberTech3 = new javax.swing.JLabel();
        memberVis3 = new javax.swing.JLabel();
        memberTotal3 = new javax.swing.JLabel();
        memberCombo4 = new javax.swing.JComboBox<String>();
        memberBand4 = new javax.swing.JLabel();
        memberName4 = new javax.swing.JLabel();
        memberRare4 = new javax.swing.JLabel();
        memberType4 = new javax.swing.JLabel();
        memberPow4 = new javax.swing.JLabel();
        memberTech4 = new javax.swing.JLabel();
        memberVis4 = new javax.swing.JLabel();
        memberTotal4 = new javax.swing.JLabel();
        memberCombo5 = new javax.swing.JComboBox<String>();
        memberBand5 = new javax.swing.JLabel();
        memberName5 = new javax.swing.JLabel();
        memberRare5 = new javax.swing.JLabel();
        memberType5 = new javax.swing.JLabel();
        memberPow5 = new javax.swing.JLabel();
        memberTech5 = new javax.swing.JLabel();
        memberVis5 = new javax.swing.JLabel();
        memberTotal5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        teamMemberLabel = new javax.swing.JLabel();
        areaLabel1 = new javax.swing.JLabel();
        areaLabel2 = new javax.swing.JLabel();
        areaLabel3 = new javax.swing.JLabel();
        areaLabel4 = new javax.swing.JLabel();
        areaLabel5 = new javax.swing.JLabel();
        areaLabel6 = new javax.swing.JLabel();
        areaLabel7 = new javax.swing.JLabel();
        areaItemLabel2 = new javax.swing.JLabel();
        areaItemLabel3 = new javax.swing.JLabel();
        areaItemCombo1 = new javax.swing.JComboBox<String>();
        areaItemCombo2 = new javax.swing.JComboBox<String>();
        areaItemCombo3 = new javax.swing.JComboBox<String>();
        areaItemCombo4 = new javax.swing.JComboBox<String>();
        areaItemCombo5 = new javax.swing.JComboBox<String>();
        areaItemCombo6 = new javax.swing.JComboBox<String>();
        areaItemCombo7 = new javax.swing.JComboBox<String>();
        areaItemLevel1 = new javax.swing.JComboBox<String>();
        areaItemLevel2 = new javax.swing.JComboBox<String>();
        areaItemLevel3 = new javax.swing.JComboBox<String>();
        areaItemLevel4 = new javax.swing.JComboBox<String>();
        areaItemLevel5 = new javax.swing.JComboBox<String>();
        areaItemLevel6 = new javax.swing.JComboBox<String>();
        areaItemLevel7 = new javax.swing.JComboBox<String>();
        areaLabel8 = new javax.swing.JLabel();
        areaLabel9 = new javax.swing.JLabel();
        areaLabel10 = new javax.swing.JLabel();
        areaLabel11 = new javax.swing.JLabel();
        areaLabel12 = new javax.swing.JLabel();
        areaLabel13 = new javax.swing.JLabel();
        areaLabel14 = new javax.swing.JLabel();
        areaItemCombo8 = new javax.swing.JComboBox<String>();
        areaItemCombo9 = new javax.swing.JComboBox<String>();
        areaItemCombo10 = new javax.swing.JComboBox<String>();
        areaItemCombo11 = new javax.swing.JComboBox<String>();
        areaItemCombo12 = new javax.swing.JComboBox<String>();
        areaItemCombo13 = new javax.swing.JComboBox<String>();
        areaItemCombo14 = new javax.swing.JComboBox<String>();
        areaItemLevel8 = new javax.swing.JComboBox<String>();
        areaItemLevel9 = new javax.swing.JComboBox<String>();
        areaItemLevel10 = new javax.swing.JComboBox<String>();
        areaItemLevel11 = new javax.swing.JComboBox<String>();
        areaItemLevel12 = new javax.swing.JComboBox<String>();
        areaItemLevel13 = new javax.swing.JComboBox<String>();
        areaItemLevel14 = new javax.swing.JComboBox<String>();
        jSeparator2 = new javax.swing.JSeparator();
        resultLabel3 = new javax.swing.JLabel();
        teamIcon1 = new javax.swing.JLabel();
        teamIcon2 = new javax.swing.JLabel();
        teamIcon3 = new javax.swing.JLabel();
        teamIcon4 = new javax.swing.JLabel();
        teamIcon5 = new javax.swing.JLabel();
        resultLabel4 = new javax.swing.JLabel();
        Title = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        resultTextField = new javax.swing.JTextField();
        resetMemberButton = new javax.swing.JButton();
        resetItemButton = new javax.swing.JButton();
        screenCapture = new javax.swing.JButton();
        resultTextField2 = new javax.swing.JTextField();
        resultLabel5 = new javax.swing.JLabel();
        resultLabel6 = new javax.swing.JLabel();
        teamMemberLabel1 = new javax.swing.JLabel();
        filterCombo1 = new javax.swing.JComboBox<String>();
        filterCombo2 = new javax.swing.JComboBox<String>();
        filterCombo3 = new javax.swing.JComboBox<String>();
        filterCombo4 = new javax.swing.JComboBox<String>();
        filterCombo5 = new javax.swing.JComboBox<String>();
        filterCombo6 = new javax.swing.JComboBox<String>();
        filterCombo7 = new javax.swing.JComboBox<String>();
        filterCombo8 = new javax.swing.JComboBox<String>();
        filterCombo9 = new javax.swing.JComboBox<String>();
        filterCombo10 = new javax.swing.JComboBox<String>();
        filterCombo11 = new javax.swing.JComboBox<String>();
        filterCombo12 = new javax.swing.JComboBox<String>();
        filterCombo13 = new javax.swing.JComboBox<String>();
        filterCombo14 = new javax.swing.JComboBox<String>();
        filterCombo15 = new javax.swing.JComboBox<String>();
        eventLabel = new javax.swing.JLabel();
        eventTypeLabel = new javax.swing.JLabel();
        eventBuffLabel = new javax.swing.JLabel();
        EventBuffMemberLabel1 = new javax.swing.JLabel();
        EventBuffMemberLabel2 = new javax.swing.JLabel();
        EventBuffMemberLabel3 = new javax.swing.JLabel();
        EventBuffMemberLabel4 = new javax.swing.JLabel();
        EventBuffMemberLabel5 = new javax.swing.JLabel();
        eventTypeCombo = new javax.swing.JComboBox<String>();
        eventBuffMemCombo1 = new javax.swing.JComboBox<String>();
        eventBuffMemCombo2 = new javax.swing.JComboBox<String>();
        eventBuffMemCombo3 = new javax.swing.JComboBox<String>();
        eventBuffMemCombo4 = new javax.swing.JComboBox<String>();
        eventBuffMemCombo5 = new javax.swing.JComboBox<String>();
        areaLabelAuto1 = new javax.swing.JLabel();
        areaItemAutoCombo1 = new javax.swing.JComboBox<String>();
        areaItemAutoLevel1 = new javax.swing.JComboBox<String>();
        areaLabelAuto2 = new javax.swing.JLabel();
        areaItemAutoCombo2 = new javax.swing.JComboBox<String>();
        areaItemAutoLevel2 = new javax.swing.JComboBox<String>();
        songLabel = new javax.swing.JLabel();
        songNameLabel = new javax.swing.JLabel();
        skillPercLabel1 = new javax.swing.JLabel();
        memberSkillPercLabel1 = new javax.swing.JLabel();
        memberSkillPercLabel2 = new javax.swing.JLabel();
        memberSkillPercLabel3 = new javax.swing.JLabel();
        memberSkillPercLabel4 = new javax.swing.JLabel();
        memberSkillPercLabel5 = new javax.swing.JLabel();
        songNameCombo = new javax.swing.JComboBox<String>();
        memberSkillPercLabel6 = new javax.swing.JLabel();
        memberSkillSlider1 = new javax.swing.JSlider();
        memberSkillSlider2 = new javax.swing.JSlider();
        memberSkillSlider3 = new javax.swing.JSlider();
        memberSkillSlider4 = new javax.swing.JSlider();
        memberSkillSlider5 = new javax.swing.JSlider();
        memberSkillSlider6 = new javax.swing.JSlider();
        skillLabel1 = new javax.swing.JLabel();
        memberSkill1 = new javax.swing.JLabel();
        memberSkill2 = new javax.swing.JLabel();
        memberSkill3 = new javax.swing.JLabel();
        memberSkill4 = new javax.swing.JLabel();
        memberSkill5 = new javax.swing.JLabel();
        memberSkillPerc1 = new javax.swing.JLabel();
        memberSkillPerc2 = new javax.swing.JLabel();
        memberSkillPerc3 = new javax.swing.JLabel();
        memberSkillPerc4 = new javax.swing.JLabel();
        memberSkillPerc5 = new javax.swing.JLabel();
        memberSkillPerc6 = new javax.swing.JLabel();
        resultOnlyCheckbox = new javax.swing.JCheckBox();
        songFilter1 = new javax.swing.JComboBox<String>();
        songFilter2 = new javax.swing.JComboBox<String>();
        memSkillTimeCombo1 = new javax.swing.JComboBox<String>();
        memberSkillTime1 = new javax.swing.JLabel();
        memSkillTimeCombo2 = new javax.swing.JComboBox<String>();
        memberSkillTime2 = new javax.swing.JLabel();
        memSkillTimeCombo3 = new javax.swing.JComboBox<String>();
        memberSkillTime3 = new javax.swing.JLabel();
        memSkillTimeCombo4 = new javax.swing.JComboBox<String>();
        memberSkillTime4 = new javax.swing.JLabel();
        memSkillTimeCombo5 = new javax.swing.JComboBox<String>();
        memberSkillTime5 = new javax.swing.JLabel();
        memSkillTimeCombo6 = new javax.swing.JComboBox<String>();
        memberSkillTime6 = new javax.swing.JLabel();
        songFilter3 = new javax.swing.JComboBox<String>();
        songCalMethod = new javax.swing.JComboBox<String>();
        filterAutoCombo1 = new javax.swing.JComboBox<String>();
        filterAutoCombo2 = new javax.swing.JComboBox<String>();
        filterAutoCombo3 = new javax.swing.JComboBox<String>();
        resultLabel7 = new javax.swing.JLabel();
        calMethodTxt1 = new javax.swing.JLabel();
        calMethodTxt2 = new javax.swing.JLabel();
        calMethodTxt3 = new javax.swing.JLabel();
        calMethodTxt4 = new javax.swing.JLabel();
        calMethodTxt5 = new javax.swing.JLabel();
        calMethodTxt6 = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        loadLabel1 = new javax.swing.JLabel();
        buffMemAutoCombo1 = new javax.swing.JComboBox<String>();
        songCalMethod2 = new javax.swing.JComboBox<String>();
        eventBounsLabel1 = new javax.swing.JLabel();
        eventBonusCombo = new javax.swing.JComboBox<String>();
        autoCheckbox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(title+" Ver"+version);
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(102, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.white);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(500, 380));
        setPreferredSize(new java.awt.Dimension(1090, 750));
        setResizable(false);

        areaItemLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLabel1.setForeground(this.fontColor);
        areaItemLabel1.setText("エリアアイテム");
        areaItemLabel1.setPreferredSize(new java.awt.Dimension(110, 20));

        memberCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        memberCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnCardName()));
        memberCombo1.setSelectedIndex(0);
        memberCombo1.setSelectedItem(0);
        memberCombo1.setPreferredSize(new java.awt.Dimension(245, 35));
        memberCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberCombo1ActionPerformed(evt);
            }
        });

        bandLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        bandLabel.setForeground(this.fontColor);
        bandLabel.setText("バンド");
        bandLabel.setPreferredSize(new java.awt.Dimension(93, 20));

        charaLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        charaLabel.setForeground(this.fontColor);
        charaLabel.setText("キャラ");
        charaLabel.setPreferredSize(new java.awt.Dimension(60, 20));

        rareLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        rareLabel.setForeground(this.fontColor);
        rareLabel.setText("レア");
        rareLabel.setPreferredSize(new java.awt.Dimension(20, 20));

        typeLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        typeLabel.setForeground(this.fontColor);
        typeLabel.setText("属性");
        typeLabel.setPreferredSize(new java.awt.Dimension(56, 20));

        powLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        powLabel.setForeground(this.fontColor);
        powLabel.setText("Perf.");
        powLabel.setPreferredSize(new java.awt.Dimension(40, 20));

        techLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        techLabel.setForeground(this.fontColor);
        techLabel.setText("Tech.");

        visLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        visLabel.setForeground(this.fontColor);
        visLabel.setText("Vis.");
        visLabel.setPreferredSize(new java.awt.Dimension(40, 20));

        totalLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        totalLabel.setForeground(this.fontColor);
        totalLabel.setText("総合力");
        totalLabel.setPreferredSize(new java.awt.Dimension(50, 20));

        memberBand1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberBand1.setForeground(this.fontColor);
        memberBand1.setText("N/A");
        memberBand1.setPreferredSize(new java.awt.Dimension(93, 35));

        memberName1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberName1.setForeground(this.fontColor);
        memberName1.setText("N/A");
        memberName1.setPreferredSize(new java.awt.Dimension(60, 35));

        memberRare1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberRare1.setForeground(this.fontColor);
        memberRare1.setText("★0");
        memberRare1.setPreferredSize(new java.awt.Dimension(20, 35));

        memberType1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberType1.setForeground(this.fontColor);
        memberType1.setText("N/A");
        memberType1.setPreferredSize(new java.awt.Dimension(56, 35));

        memberPow1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberPow1.setForeground(this.fontColor);
        memberPow1.setText("0");
        memberPow1.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTech1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTech1.setForeground(this.fontColor);
        memberTech1.setText("0");
        memberTech1.setPreferredSize(new java.awt.Dimension(40, 35));

        memberVis1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberVis1.setForeground(this.fontColor);
        memberVis1.setText("0");
        memberVis1.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTotal1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTotal1.setForeground(this.fontColor);
        memberTotal1.setText("0");
        memberTotal1.setPreferredSize(new java.awt.Dimension(50, 35));

        memberCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        memberCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnCardName()));
        memberCombo2.setSelectedIndex(0);
        memberCombo2.setSelectedItem(0);
        memberCombo2.setPreferredSize(new java.awt.Dimension(245, 35));
        memberCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberCombo2ActionPerformed(evt);
            }
        });

        memberBand2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberBand2.setForeground(this.fontColor);
        memberBand2.setText("N/A");
        memberBand2.setPreferredSize(new java.awt.Dimension(93, 35));

        memberName2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberName2.setForeground(this.fontColor);
        memberName2.setText("N/A");
        memberName2.setPreferredSize(new java.awt.Dimension(60, 35));

        memberRare2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberRare2.setForeground(this.fontColor);
        memberRare2.setText("★0");
        memberRare2.setPreferredSize(new java.awt.Dimension(20, 35));

        memberType2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberType2.setForeground(this.fontColor);
        memberType2.setText("N/A");
        memberType2.setPreferredSize(new java.awt.Dimension(56, 35));

        memberPow2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberPow2.setForeground(this.fontColor);
        memberPow2.setText("0");
        memberPow2.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTech2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTech2.setForeground(this.fontColor);
        memberTech2.setText("0");
        memberTech2.setPreferredSize(new java.awt.Dimension(40, 35));

        memberVis2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberVis2.setForeground(this.fontColor);
        memberVis2.setText("0");
        memberVis2.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTotal2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTotal2.setForeground(this.fontColor);
        memberTotal2.setText("0");
        memberTotal2.setPreferredSize(new java.awt.Dimension(50, 35));

        memberCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        memberCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnCardName()));
        memberCombo3.setSelectedIndex(0);
        memberCombo3.setSelectedItem(0);
        memberCombo3.setPreferredSize(new java.awt.Dimension(245, 35));
        memberCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberCombo3ActionPerformed(evt);
            }
        });

        memberBand3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberBand3.setForeground(this.fontColor);
        memberBand3.setText("N/A");
        memberBand3.setPreferredSize(new java.awt.Dimension(93, 35));

        memberName3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberName3.setForeground(this.fontColor);
        memberName3.setText("N/A");
        memberName3.setPreferredSize(new java.awt.Dimension(60, 35));

        memberRare3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberRare3.setForeground(this.fontColor);
        memberRare3.setText("★0");
        memberRare3.setPreferredSize(new java.awt.Dimension(20, 35));

        memberType3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberType3.setForeground(this.fontColor);
        memberType3.setText("N/A");
        memberType3.setPreferredSize(new java.awt.Dimension(56, 35));

        memberPow3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberPow3.setForeground(this.fontColor);
        memberPow3.setText("0");
        memberPow3.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTech3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTech3.setForeground(this.fontColor);
        memberTech3.setText("0");
        memberTech3.setPreferredSize(new java.awt.Dimension(40, 35));

        memberVis3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberVis3.setForeground(this.fontColor);
        memberVis3.setText("0");
        memberVis3.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTotal3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTotal3.setForeground(this.fontColor);
        memberTotal3.setText("0");
        memberTotal3.setPreferredSize(new java.awt.Dimension(50, 35));

        memberCombo4.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        memberCombo4.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnCardName()));
        memberCombo4.setSelectedIndex(0);
        memberCombo4.setSelectedItem(0);
        memberCombo4.setPreferredSize(new java.awt.Dimension(245, 35));
        memberCombo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberCombo4ActionPerformed(evt);
            }
        });

        memberBand4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberBand4.setForeground(this.fontColor);
        memberBand4.setText("N/A");
        memberBand4.setPreferredSize(new java.awt.Dimension(93, 35));

        memberName4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberName4.setForeground(this.fontColor);
        memberName4.setText("N/A");
        memberName4.setPreferredSize(new java.awt.Dimension(60, 35));

        memberRare4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberRare4.setForeground(this.fontColor);
        memberRare4.setText("★0");
        memberRare4.setPreferredSize(new java.awt.Dimension(20, 35));

        memberType4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberType4.setForeground(this.fontColor);
        memberType4.setText("N/A");
        memberType4.setPreferredSize(new java.awt.Dimension(56, 35));

        memberPow4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberPow4.setForeground(this.fontColor);
        memberPow4.setText("0");
        memberPow4.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTech4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTech4.setForeground(this.fontColor);
        memberTech4.setText("0");
        memberTech4.setPreferredSize(new java.awt.Dimension(40, 35));

        memberVis4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberVis4.setForeground(this.fontColor);
        memberVis4.setText("0");
        memberVis4.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTotal4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTotal4.setForeground(this.fontColor);
        memberTotal4.setText("0");
        memberTotal4.setPreferredSize(new java.awt.Dimension(50, 35));

        memberCombo5.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        memberCombo5.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnCardName()));
        memberCombo5.setSelectedIndex(0);
        memberCombo5.setSelectedItem(0);
        memberCombo5.setPreferredSize(new java.awt.Dimension(245, 35));
        memberCombo5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberCombo5ActionPerformed(evt);
            }
        });

        memberBand5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberBand5.setForeground(this.fontColor);
        memberBand5.setText("N/A");
        memberBand5.setPreferredSize(new java.awt.Dimension(93, 35));

        memberName5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberName5.setForeground(this.fontColor);
        memberName5.setText("N/A");
        memberName5.setPreferredSize(new java.awt.Dimension(60, 35));

        memberRare5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberRare5.setForeground(this.fontColor);
        memberRare5.setText("★0");
        memberRare5.setPreferredSize(new java.awt.Dimension(20, 35));

        memberType5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberType5.setForeground(this.fontColor);
        memberType5.setText("N/A");
        memberType5.setPreferredSize(new java.awt.Dimension(56, 35));

        memberPow5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberPow5.setForeground(this.fontColor);
        memberPow5.setText("0");
        memberPow5.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTech5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTech5.setForeground(this.fontColor);
        memberTech5.setText("0");
        memberTech5.setPreferredSize(new java.awt.Dimension(40, 35));

        memberVis5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberVis5.setForeground(this.fontColor);
        memberVis5.setText("0");
        memberVis5.setPreferredSize(new java.awt.Dimension(40, 35));

        memberTotal5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberTotal5.setForeground(this.fontColor);
        memberTotal5.setText("0");
        memberTotal5.setPreferredSize(new java.awt.Dimension(50, 35));

        teamMemberLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        teamMemberLabel.setForeground(this.fontColor);
        teamMemberLabel.setText("フィルタ(一括変更)");
        teamMemberLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        areaLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel1.setForeground(this.fontColor);
        areaLabel1.setText("スタジオ(マイク)");
        areaLabel1.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel2.setForeground(this.fontColor);
        areaLabel2.setText("スタジオ(ギター)");
        areaLabel2.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel3.setForeground(this.fontColor);
        areaLabel3.setText("スタジオ(ベース)");
        areaLabel3.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel4.setForeground(this.fontColor);
        areaLabel4.setText("スタジオ(ドラム)");
        areaLabel4.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel5.setForeground(this.fontColor);
        areaLabel5.setText("スタジオ(その他)");
        areaLabel5.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel6.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel6.setForeground(this.fontColor);
        areaLabel6.setText("ポスター");
        areaLabel6.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel7.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel7.setForeground(this.fontColor);
        areaLabel7.setText("カウンター");
        areaLabel7.setPreferredSize(new java.awt.Dimension(110, 25));

        areaItemLabel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLabel2.setForeground(this.fontColor);
        areaItemLabel2.setText("バンド/属性");
        areaItemLabel2.setPreferredSize(new java.awt.Dimension(180, 20));

        areaItemLabel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLabel3.setForeground(this.fontColor);
        areaItemLabel3.setText("レベル");
        areaItemLabel3.setPreferredSize(new java.awt.Dimension(245, 20));

        areaItemCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo1.setSelectedIndex(0);
        areaItemCombo1.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo1ActionPerformed(evt);
            }
        });

        areaItemCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo2.setSelectedIndex(0);
        areaItemCombo2.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo2ActionPerformed(evt);
            }
        });

        areaItemCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo3.setSelectedIndex(0);
        areaItemCombo3.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo3ActionPerformed(evt);
            }
        });

        areaItemCombo4.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo4.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo4.setSelectedIndex(0);
        areaItemCombo4.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo4ActionPerformed(evt);
            }
        });

        areaItemCombo5.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo5.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo5.setSelectedIndex(0);
        areaItemCombo5.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo5ActionPerformed(evt);
            }
        });

        areaItemCombo6.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo6.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo6.setSelectedIndex(0);
        areaItemCombo6.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo6ActionPerformed(evt);
            }
        });

        areaItemCombo7.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "なし" }));
        areaItemCombo7.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo7ActionPerformed(evt);
            }
        });

        areaItemLevel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(1)));
        areaItemLevel1.setSelectedIndex(0);
        areaItemLevel1.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel1ActionPerformed(evt);
            }
        });

        areaItemLevel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(1)));
        areaItemLevel2.setSelectedIndex(0);
        areaItemLevel2.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel2ActionPerformed(evt);
            }
        });

        areaItemLevel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(1)));
        areaItemLevel3.setSelectedIndex(0);
        areaItemLevel3.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel3ActionPerformed(evt);
            }
        });

        areaItemLevel4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel4.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(1)));
        areaItemLevel4.setSelectedIndex(0);
        areaItemLevel4.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel4ActionPerformed(evt);
            }
        });

        areaItemLevel5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel5.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(1)));
        areaItemLevel5.setSelectedIndex(0);
        areaItemLevel5.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel5ActionPerformed(evt);
            }
        });

        areaItemLevel6.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel6.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(2)));
        areaItemLevel6.setSelectedIndex(0);
        areaItemLevel6.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel6ActionPerformed(evt);
            }
        });

        areaItemLevel7.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        areaItemLevel7.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel7ActionPerformed(evt);
            }
        });

        areaLabel8.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel8.setForeground(this.fontColor);
        areaLabel8.setText("ミニテーブル");
        areaLabel8.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel9.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel9.setForeground(this.fontColor);
        areaLabel9.setText("マガジンラック");
        areaLabel9.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel10.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel10.setForeground(this.fontColor);
        areaLabel10.setText("入り口前");
        areaLabel10.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel11.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel11.setForeground(this.fontColor);
        areaLabel11.setText("看板");
        areaLabel11.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel12.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel12.setForeground(this.fontColor);
        areaLabel12.setText("センター");
        areaLabel12.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel13.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel13.setForeground(this.fontColor);
        areaLabel13.setText("中庭");
        areaLabel13.setPreferredSize(new java.awt.Dimension(110, 25));

        areaLabel14.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaLabel14.setForeground(this.fontColor);
        areaLabel14.setText("メニュー");
        areaLabel14.setPreferredSize(new java.awt.Dimension(110, 25));

        areaItemCombo8.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "なし" }));
        areaItemCombo8.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo8ActionPerformed(evt);
            }
        });

        areaItemCombo9.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "なし" }));
        areaItemCombo9.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo9ActionPerformed(evt);
            }
        });

        areaItemCombo10.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo10.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemCombo10.setSelectedIndex(0);
        areaItemCombo10.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo10ActionPerformed(evt);
            }
        });

        areaItemCombo11.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "なし" }));
        areaItemCombo11.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo11ActionPerformed(evt);
            }
        });

        areaItemCombo12.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo12.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnType()));
        areaItemCombo12.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo12ActionPerformed(evt);
            }
        });

        areaItemCombo13.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "なし" }));
        areaItemCombo13.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo13ActionPerformed(evt);
            }
        });

        areaItemCombo14.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemCombo14.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnType()));
        areaItemCombo14.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemCombo14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemCombo14ActionPerformed(evt);
            }
        });

        areaItemLevel8.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        areaItemLevel8.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel8ActionPerformed(evt);
            }
        });

        areaItemLevel9.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        areaItemLevel9.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel9ActionPerformed(evt);
            }
        });

        areaItemLevel10.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel10.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(2)));
        areaItemLevel10.setSelectedIndex(0);
        areaItemLevel10.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel10ActionPerformed(evt);
            }
        });

        areaItemLevel11.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        areaItemLevel11.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel11ActionPerformed(evt);
            }
        });

        areaItemLevel12.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel12.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(3)));
        areaItemLevel12.setSelectedIndex(0);
        areaItemLevel12.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel12ActionPerformed(evt);
            }
        });

        areaItemLevel13.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        areaItemLevel13.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel13ActionPerformed(evt);
            }
        });

        areaItemLevel14.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemLevel14.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(4)));
        areaItemLevel14.setSelectedIndex(0);
        areaItemLevel14.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemLevel14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemLevel14ActionPerformed(evt);
            }
        });

        resultLabel3.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        resultLabel3.setForeground(this.fontColor);
        resultLabel3.setText("結 果");
        resultLabel3.setPreferredSize(new java.awt.Dimension(110, 20));

        teamIcon1.setBackground(new java.awt.Color(153, 255, 255));
        teamIcon1.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        teamIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cardIcons/card0.png"))); // NOI18N
        teamIcon1.setText("icon1");

        teamIcon2.setBackground(new java.awt.Color(153, 255, 255));
        teamIcon2.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        teamIcon2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cardIcons/card0.png"))); // NOI18N
        teamIcon2.setText("icon2");

        teamIcon3.setBackground(new java.awt.Color(153, 255, 255));
        teamIcon3.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        teamIcon3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cardIcons/card0.png"))); // NOI18N
        teamIcon3.setText("icon3");

        teamIcon4.setBackground(new java.awt.Color(153, 255, 255));
        teamIcon4.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        teamIcon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cardIcons/card0.png"))); // NOI18N
        teamIcon4.setText("icon3");

        teamIcon5.setBackground(new java.awt.Color(153, 255, 255));
        teamIcon5.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        teamIcon5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cardIcons/card0.png"))); // NOI18N
        teamIcon5.setText("icon3");

        resultLabel4.setFont(new java.awt.Font("微軟正黑體", 3, 18)); // NOI18N
        resultLabel4.setForeground(this.fontColor);
        resultLabel4.setText("バンド総合力:");
        resultLabel4.setPreferredSize(new java.awt.Dimension(140, 20));

        Title.setFont(new java.awt.Font("微軟正黑體", 3, 25)); // NOI18N
        Title.setForeground(this.fontColor);
        Title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Title.setText("バンドリ！ガールズバンドパーティ！バンド総合力電卓");

        jLabel1.setFont(new java.awt.Font("新細明體", 2, 12)); // NOI18N
        jLabel1.setForeground(this.fontColor);
        jLabel1.setText("Author: "+this.author+", Contributor: "+this.contributor[3]+", Credits to: "+this.contributor[0]+" - song data & algorithms,  "+this.contributor[1]+" - card data,  "+this.contributor[2]+" - image format    version"+this.version+" - PRESENTED BY LZ★");

        resultTextField.setEditable(false);
        resultTextField.setBackground(new java.awt.Color(90, 90, 90));
        resultTextField.setFont(new java.awt.Font("微軟正黑體", 3, 28)); // NOI18N
        resultTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        resultTextField.setText("0");

        resetMemberButton.setFont(new java.awt.Font("微軟正黑體", 2, 12)); // NOI18N
        resetMemberButton.setText("リセット");
        resetMemberButton.setPreferredSize(new java.awt.Dimension(73, 20));
        resetMemberButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetMemberButtonMouseClicked(evt);
            }
        });

        resetItemButton.setFont(new java.awt.Font("微軟正黑體", 2, 12)); // NOI18N
        resetItemButton.setText("リセット");
        resetItemButton.setPreferredSize(new java.awt.Dimension(73, 20));
        resetItemButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetItemButtonMouseClicked(evt);
            }
        });

        screenCapture.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        screenCapture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/otherIcons/screen_capture.png"))); // NOI18N
        screenCapture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                screenCaptureMouseClicked(evt);
            }
        });

        resultTextField2.setEditable(false);
        resultTextField2.setBackground(new java.awt.Color(90, 90, 90));
        resultTextField2.setFont(new java.awt.Font("微軟正黑體", 3, 28)); // NOI18N
        resultTextField2.setForeground(this.fontColor);
        resultTextField2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        resultTextField2.setText("0");

        resultLabel5.setFont(new java.awt.Font("微軟正黑體", 3, 18)); // NOI18N
        resultLabel5.setForeground(this.fontColor);
        resultLabel5.setText("協力ライブ:");
        resultLabel5.setPreferredSize(new java.awt.Dimension(140, 20));

        resultLabel6.setFont(new java.awt.Font("微軟正黑體", 3, 18)); // NOI18N
        resultLabel6.setForeground(this.fontColor);
        resultLabel6.setText("楽曲理論スコア:");
        resultLabel6.setPreferredSize(new java.awt.Dimension(140, 20));
        resultLabel6.setRequestFocusEnabled(false);
        resultLabel6.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        teamMemberLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        teamMemberLabel1.setForeground(this.fontColor);
        teamMemberLabel1.setText("メンバー");
        teamMemberLabel1.setPreferredSize(new java.awt.Dimension(245, 20));

        filterCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo1.setMaximumRowCount(7);
        filterCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterCombo1.setSelectedIndex(0);
        filterCombo1.setSelectedItem(0);
        filterCombo1.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo1ActionPerformed(evt);
            }
        });

        filterCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo2.setMaximumRowCount(7);
        filterCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterCombo2.setSelectedIndex(0);
        filterCombo2.setSelectedItem(0);
        filterCombo2.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo2ActionPerformed(evt);
            }
        });

        filterCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo3.setMaximumRowCount(7);
        filterCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterCombo3.setSelectedIndex(0);
        filterCombo3.setSelectedItem(0);
        filterCombo3.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo3ActionPerformed(evt);
            }
        });

        filterCombo4.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo4.setMaximumRowCount(7);
        filterCombo4.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterCombo4.setSelectedIndex(0);
        filterCombo4.setSelectedItem(0);
        filterCombo4.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo4ActionPerformed(evt);
            }
        });

        filterCombo5.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo5.setMaximumRowCount(7);
        filterCombo5.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterCombo5.setSelectedIndex(0);
        filterCombo5.setSelectedItem(0);
        filterCombo5.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo5ActionPerformed(evt);
            }
        });

        filterCombo6.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo6.setMaximumRowCount(7);
        filterCombo6.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterCombo6.setSelectedIndex(0);
        filterCombo6.setSelectedItem(0);
        filterCombo6.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo6ActionPerformed(evt);
            }
        });

        filterCombo7.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo7.setMaximumRowCount(7);
        filterCombo7.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterCombo7.setSelectedIndex(0);
        filterCombo7.setSelectedItem(0);
        filterCombo7.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo7ActionPerformed(evt);
            }
        });

        filterCombo8.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo8.setMaximumRowCount(7);
        filterCombo8.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterCombo8.setSelectedIndex(0);
        filterCombo8.setSelectedItem(0);
        filterCombo8.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo8ActionPerformed(evt);
            }
        });

        filterCombo9.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo9.setMaximumRowCount(7);
        filterCombo9.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterCombo9.setSelectedIndex(0);
        filterCombo9.setSelectedItem(0);
        filterCombo9.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo9ActionPerformed(evt);
            }
        });

        filterCombo10.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo10.setMaximumRowCount(7);
        filterCombo10.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterCombo10.setSelectedIndex(0);
        filterCombo10.setSelectedItem(0);
        filterCombo10.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo10ActionPerformed(evt);
            }
        });

        filterCombo11.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo11.setMaximumRowCount(7);
        filterCombo11.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterCombo11.setSelectedIndex(0);
        filterCombo11.setSelectedItem(0);
        filterCombo11.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo11ActionPerformed(evt);
            }
        });

        filterCombo12.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo12.setMaximumRowCount(7);
        filterCombo12.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterCombo12.setSelectedIndex(0);
        filterCombo12.setSelectedItem(0);
        filterCombo12.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo12ActionPerformed(evt);
            }
        });

        filterCombo13.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo13.setMaximumRowCount(7);
        filterCombo13.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterCombo13.setSelectedIndex(0);
        filterCombo13.setSelectedItem(0);
        filterCombo13.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo13ActionPerformed(evt);
            }
        });

        filterCombo14.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo14.setMaximumRowCount(7);
        filterCombo14.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterCombo14.setSelectedIndex(0);
        filterCombo14.setSelectedItem(0);
        filterCombo14.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo14ActionPerformed(evt);
            }
        });

        filterCombo15.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterCombo15.setMaximumRowCount(7);
        filterCombo15.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterCombo15.setSelectedIndex(0);
        filterCombo15.setSelectedItem(0);
        filterCombo15.setPreferredSize(new java.awt.Dimension(245, 35));
        filterCombo15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterCombo15ActionPerformed(evt);
            }
        });

        eventLabel.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        eventLabel.setForeground(this.specFontColor2);
        eventLabel.setText("[チャレンジ/対バン]");
        eventLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        eventTypeLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        eventTypeLabel.setForeground(this.fontColor);
        eventTypeLabel.setText("タイプ:");
        eventTypeLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        eventBuffLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        eventBuffLabel.setForeground(this.fontColor);
        eventBuffLabel.setText("バフメンバー:");
        eventBuffLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        EventBuffMemberLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        EventBuffMemberLabel1.setForeground(this.fontColor);
        EventBuffMemberLabel1.setText("1.");
        EventBuffMemberLabel1.setPreferredSize(new java.awt.Dimension(245, 20));

        EventBuffMemberLabel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        EventBuffMemberLabel2.setForeground(this.fontColor);
        EventBuffMemberLabel2.setText("2.");
        EventBuffMemberLabel2.setPreferredSize(new java.awt.Dimension(245, 20));

        EventBuffMemberLabel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        EventBuffMemberLabel3.setForeground(this.fontColor);
        EventBuffMemberLabel3.setText("3.");
        EventBuffMemberLabel3.setPreferredSize(new java.awt.Dimension(245, 20));

        EventBuffMemberLabel4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        EventBuffMemberLabel4.setForeground(this.fontColor);
        EventBuffMemberLabel4.setText("4.");
        EventBuffMemberLabel4.setPreferredSize(new java.awt.Dimension(245, 20));

        EventBuffMemberLabel5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        EventBuffMemberLabel5.setForeground(this.fontColor);
        EventBuffMemberLabel5.setText("5.");
        EventBuffMemberLabel5.setPreferredSize(new java.awt.Dimension(245, 20));

        eventTypeCombo.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventTypeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnEventType()));
        eventTypeCombo.setSelectedIndex(0);
        eventTypeCombo.setSelectedItem(0);
        eventTypeCombo.setPreferredSize(new java.awt.Dimension(100, 25));
        eventTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventTypeComboActionPerformed(evt);
            }
        });

        eventBuffMemCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBuffMemCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnAllChaBuffName()));
        eventBuffMemCombo1.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBuffMemCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBuffMemCombo1ActionPerformed(evt);
            }
        });

        eventBuffMemCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBuffMemCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnAllChaBuffName()));
        eventBuffMemCombo2.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBuffMemCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBuffMemCombo2ActionPerformed(evt);
            }
        });

        eventBuffMemCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBuffMemCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnAllChaBuffName()));
        eventBuffMemCombo3.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBuffMemCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBuffMemCombo3ActionPerformed(evt);
            }
        });

        eventBuffMemCombo4.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBuffMemCombo4.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnAllChaBuffName()));
        eventBuffMemCombo4.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBuffMemCombo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBuffMemCombo4ActionPerformed(evt);
            }
        });

        eventBuffMemCombo5.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBuffMemCombo5.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnAllChaBuffName()));
        eventBuffMemCombo5.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBuffMemCombo5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBuffMemCombo5ActionPerformed(evt);
            }
        });

        areaLabelAuto1.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        areaLabelAuto1.setForeground(this.specFontColor);
        areaLabelAuto1.setText("一括変更(バンド)");
        areaLabelAuto1.setPreferredSize(new java.awt.Dimension(110, 25));

        areaItemAutoCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemAutoCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBand()));
        areaItemAutoCombo1.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemAutoCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemAutoCombo1ActionPerformed(evt);
            }
        });

        areaItemAutoLevel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemAutoLevel1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(5)));
        areaItemAutoLevel1.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemAutoLevel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemAutoLevel1ActionPerformed(evt);
            }
        });

        areaLabelAuto2.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        areaLabelAuto2.setForeground(this.specFontColor);
        areaLabelAuto2.setText("一括変更(タイプ)");
        areaLabelAuto2.setPreferredSize(new java.awt.Dimension(110, 25));

        areaItemAutoCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        areaItemAutoCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnType()));
        areaItemAutoCombo2.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemAutoCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemAutoCombo2ActionPerformed(evt);
            }
        });

        areaItemAutoLevel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        areaItemAutoLevel2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.getItemLevels(5)));
        areaItemAutoLevel2.setPreferredSize(new java.awt.Dimension(100, 25));
        areaItemAutoLevel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaItemAutoLevel2ActionPerformed(evt);
            }
        });

        songLabel.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        songLabel.setForeground(this.specFontColor2);
        songLabel.setText("[楽曲スコア理論值]");
        songLabel.setToolTipText("");
        songLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        songNameLabel.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songNameLabel.setForeground(this.fontColor);
        songNameLabel.setText("楽曲:");
        songNameLabel.setPreferredSize(new java.awt.Dimension(245, 20));

        skillPercLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        skillPercLabel1.setForeground(this.fontColor);
        skillPercLabel1.setText("スコアスキル(0-115%):");
        skillPercLabel1.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPercLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel1.setForeground(this.fontColor);
        memberSkillPercLabel1.setText("1.");
        memberSkillPercLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel1.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPercLabel2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel2.setForeground(this.fontColor);
        memberSkillPercLabel2.setText("2.");
        memberSkillPercLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel2.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPercLabel3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel3.setForeground(this.fontColor);
        memberSkillPercLabel3.setText("3.");
        memberSkillPercLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel3.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPercLabel4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel4.setForeground(this.fontColor);
        memberSkillPercLabel4.setText("4.");
        memberSkillPercLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel4.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPercLabel5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel5.setForeground(this.fontColor);
        memberSkillPercLabel5.setText("5.");
        memberSkillPercLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel5.setPreferredSize(new java.awt.Dimension(245, 20));

        songNameCombo.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        songNameCombo.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnSongName()));
        songNameCombo.setSelectedIndex(0);
        songNameCombo.setSelectedItem(0);
        songNameCombo.setPreferredSize(new java.awt.Dimension(100, 25));
        songNameCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songNameComboActionPerformed(evt);
            }
        });

        memberSkillPercLabel6.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkillPercLabel6.setForeground(this.fontColor);
        memberSkillPercLabel6.setText("6.");
        memberSkillPercLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPercLabel6.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillSlider1.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider1.setMajorTickSpacing(10);
        memberSkillSlider1.setMaximum(115);
        memberSkillSlider1.setMinorTickSpacing(5);
        memberSkillSlider1.setSnapToTicks(true);
        memberSkillSlider1.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider1.setValue(0);
        memberSkillSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider1StateChanged(evt);
            }
        });

        memberSkillSlider2.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider2.setMajorTickSpacing(10);
        memberSkillSlider2.setMaximum(115);
        memberSkillSlider2.setMinorTickSpacing(5);
        memberSkillSlider2.setSnapToTicks(true);
        memberSkillSlider2.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider2.setValue(0);
        memberSkillSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider2StateChanged(evt);
            }
        });

        memberSkillSlider3.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider3.setMajorTickSpacing(10);
        memberSkillSlider3.setMaximum(115);
        memberSkillSlider3.setMinorTickSpacing(5);
        memberSkillSlider3.setSnapToTicks(true);
        memberSkillSlider3.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider3.setValue(0);
        memberSkillSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider3StateChanged(evt);
            }
        });

        memberSkillSlider4.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider4.setMajorTickSpacing(10);
        memberSkillSlider4.setMaximum(115);
        memberSkillSlider4.setMinorTickSpacing(5);
        memberSkillSlider4.setSnapToTicks(true);
        memberSkillSlider4.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider4.setValue(0);
        memberSkillSlider4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider4StateChanged(evt);
            }
        });

        memberSkillSlider5.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider5.setMajorTickSpacing(10);
        memberSkillSlider5.setMaximum(115);
        memberSkillSlider5.setMinorTickSpacing(5);
        memberSkillSlider5.setSnapToTicks(true);
        memberSkillSlider5.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider5.setValue(0);
        memberSkillSlider5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider5StateChanged(evt);
            }
        });

        memberSkillSlider6.setFont(new java.awt.Font("微軟正黑體", 0, 12)); // NOI18N
        memberSkillSlider6.setMajorTickSpacing(10);
        memberSkillSlider6.setMaximum(115);
        memberSkillSlider6.setMinorTickSpacing(5);
        memberSkillSlider6.setSnapToTicks(true);
        memberSkillSlider6.setToolTipText("スコアアップスキル(min 0%, max 115%)");
        memberSkillSlider6.setValue(0);
        memberSkillSlider6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                memberSkillSlider6StateChanged(evt);
            }
        });

        skillLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        skillLabel1.setForeground(this.fontColor);
        skillLabel1.setText("スキル");
        skillLabel1.setPreferredSize(new java.awt.Dimension(50, 20));

        memberSkill1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkill1.setForeground(this.fontColor);
        memberSkill1.setText("N/A");
        memberSkill1.setPreferredSize(new java.awt.Dimension(56, 35));

        memberSkill2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkill2.setForeground(this.fontColor);
        memberSkill2.setText("N/A");
        memberSkill2.setPreferredSize(new java.awt.Dimension(56, 35));

        memberSkill3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkill3.setForeground(this.fontColor);
        memberSkill3.setText("N/A");
        memberSkill3.setPreferredSize(new java.awt.Dimension(56, 35));

        memberSkill4.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkill4.setForeground(this.fontColor);
        memberSkill4.setText("N/A");
        memberSkill4.setPreferredSize(new java.awt.Dimension(56, 35));

        memberSkill5.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        memberSkill5.setForeground(this.fontColor);
        memberSkill5.setText("N/A");
        memberSkill5.setPreferredSize(new java.awt.Dimension(56, 35));

        memberSkillPerc1.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc1.setForeground(this.fontColor);
        memberSkillPerc1.setText(this.memberSkillSlider1.getValue()+"%");
        memberSkillPerc1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc1.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPerc2.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc2.setForeground(this.fontColor);
        memberSkillPerc2.setText(this.memberSkillSlider2.getValue()+"%");
        memberSkillPerc2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc2.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPerc3.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc3.setForeground(this.fontColor);
        memberSkillPerc3.setText(this.memberSkillSlider3.getValue()+"%");
        memberSkillPerc3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc3.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPerc4.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc4.setForeground(this.fontColor);
        memberSkillPerc4.setText(this.memberSkillSlider4.getValue()+"%");
        memberSkillPerc4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc4.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPerc5.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc5.setForeground(this.fontColor);
        memberSkillPerc5.setText(this.memberSkillSlider5.getValue()+"%");
        memberSkillPerc5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc5.setPreferredSize(new java.awt.Dimension(245, 20));

        memberSkillPerc6.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillPerc6.setForeground(this.fontColor);
        memberSkillPerc6.setText(this.memberSkillSlider6.getValue()+"%");
        memberSkillPerc6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillPerc6.setPreferredSize(new java.awt.Dimension(245, 20));

        resultOnlyCheckbox.setFont(new java.awt.Font("微軟正黑體", 3, 18)); // NOI18N
        resultOnlyCheckbox.setForeground(this.fontColor);
        resultOnlyCheckbox.setText("結果の画面のみ");
        resultOnlyCheckbox.setIconTextGap(10);
        resultOnlyCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                resultOnlyCheckboxStateChanged(evt);
            }
        });

        songFilter1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songFilter1.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnFilter()));
        songFilter1.setSelectedIndex(0);
        songFilter1.setPreferredSize(new java.awt.Dimension(100, 25));
        songFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songFilter1ActionPerformed(evt);
            }
        });

        songFilter2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songFilter2.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnFilter2()));
        songFilter2.setSelectedIndex(0);
        songFilter2.setPreferredSize(new java.awt.Dimension(100, 25));
        songFilter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songFilter2ActionPerformed(evt);
            }
        });

        memSkillTimeCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo1.setMaximumRowCount(3);
        memSkillTimeCombo1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo1ActionPerformed(evt);
            }
        });

        memberSkillTime1.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime1.setForeground(this.fontColor);
        memberSkillTime1.setText("秒");
        memberSkillTime1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime1.setPreferredSize(new java.awt.Dimension(245, 20));

        memSkillTimeCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo2.setMaximumRowCount(3);
        memSkillTimeCombo2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo2ActionPerformed(evt);
            }
        });

        memberSkillTime2.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime2.setForeground(this.fontColor);
        memberSkillTime2.setText("秒");
        memberSkillTime2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime2.setPreferredSize(new java.awt.Dimension(245, 20));

        memSkillTimeCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo3.setMaximumRowCount(3);
        memSkillTimeCombo3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo3ActionPerformed(evt);
            }
        });

        memberSkillTime3.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime3.setForeground(this.fontColor);
        memberSkillTime3.setText("秒");
        memberSkillTime3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime3.setPreferredSize(new java.awt.Dimension(245, 20));

        memSkillTimeCombo4.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo4.setMaximumRowCount(3);
        memSkillTimeCombo4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo4ActionPerformed(evt);
            }
        });

        memberSkillTime4.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime4.setForeground(this.fontColor);
        memberSkillTime4.setText("秒");
        memberSkillTime4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime4.setPreferredSize(new java.awt.Dimension(245, 20));

        memSkillTimeCombo5.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo5.setMaximumRowCount(3);
        memSkillTimeCombo5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo5ActionPerformed(evt);
            }
        });

        memberSkillTime5.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime5.setForeground(this.fontColor);
        memberSkillTime5.setText("秒");
        memberSkillTime5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime5.setPreferredSize(new java.awt.Dimension(245, 20));

        memSkillTimeCombo6.setFont(new java.awt.Font("微軟正黑體", 0, 13)); // NOI18N
        memSkillTimeCombo6.setMaximumRowCount(3);
        memSkillTimeCombo6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7.0", "7.5", "8.0" }));
        memSkillTimeCombo6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memSkillTimeCombo6ActionPerformed(evt);
            }
        });

        memberSkillTime6.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        memberSkillTime6.setForeground(this.fontColor);
        memberSkillTime6.setText("秒");
        memberSkillTime6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        memberSkillTime6.setPreferredSize(new java.awt.Dimension(245, 20));

        songFilter3.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songFilter3.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnFilter3()));
        songFilter3.setSelectedIndex(0);
        songFilter3.setPreferredSize(new java.awt.Dimension(100, 25));
        songFilter3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songFilter3ActionPerformed(evt);
            }
        });

        songCalMethod.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songCalMethod.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnMode()));
        songCalMethod.setSelectedIndex(0);
        songCalMethod.setPreferredSize(new java.awt.Dimension(100, 25));
        songCalMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songCalMethodActionPerformed(evt);
            }
        });

        filterAutoCombo1.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterAutoCombo1.setMaximumRowCount(7);
        filterAutoCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        filterAutoCombo1.setSelectedIndex(0);
        filterAutoCombo1.setSelectedItem(0);
        filterAutoCombo1.setPreferredSize(new java.awt.Dimension(245, 35));
        filterAutoCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterAutoCombo1ActionPerformed(evt);
            }
        });

        filterAutoCombo2.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterAutoCombo2.setMaximumRowCount(7);
        filterAutoCombo2.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter2()));
        filterAutoCombo2.setSelectedIndex(0);
        filterAutoCombo2.setSelectedItem(0);
        filterAutoCombo2.setPreferredSize(new java.awt.Dimension(245, 35));
        filterAutoCombo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterAutoCombo2ActionPerformed(evt);
            }
        });

        filterAutoCombo3.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        filterAutoCombo3.setMaximumRowCount(7);
        filterAutoCombo3.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter3()));
        filterAutoCombo3.setSelectedIndex(0);
        filterAutoCombo3.setSelectedItem(0);
        filterAutoCombo3.setPreferredSize(new java.awt.Dimension(245, 35));
        filterAutoCombo3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterAutoCombo3ActionPerformed(evt);
            }
        });

        resultLabel7.setFont(new java.awt.Font("微軟正黑體", 2, 14)); // NOI18N
        resultLabel7.setForeground(this.fontColor);
        resultLabel7.setText("<曲名なし>");
        resultLabel7.setPreferredSize(new java.awt.Dimension(140, 20));

        calMethodTxt1.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt1.setForeground(this.fontColor);
        calMethodTxt1.setText("特にない");
        calMethodTxt1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt1.setPreferredSize(new java.awt.Dimension(245, 20));

        calMethodTxt2.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt2.setForeground(this.fontColor);
        calMethodTxt2.setText("特にない");
        calMethodTxt2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt2.setPreferredSize(new java.awt.Dimension(245, 20));

        calMethodTxt3.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt3.setForeground(this.fontColor);
        calMethodTxt3.setText("特にない");
        calMethodTxt3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt3.setPreferredSize(new java.awt.Dimension(245, 20));

        calMethodTxt4.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt4.setForeground(this.fontColor);
        calMethodTxt4.setText("特にない");
        calMethodTxt4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt4.setPreferredSize(new java.awt.Dimension(245, 20));

        calMethodTxt5.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt5.setForeground(this.fontColor);
        calMethodTxt5.setText("特にない");
        calMethodTxt5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt5.setPreferredSize(new java.awt.Dimension(245, 20));

        calMethodTxt6.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        calMethodTxt6.setForeground(this.fontColor);
        calMethodTxt6.setText("特にない");
        calMethodTxt6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        calMethodTxt6.setPreferredSize(new java.awt.Dimension(245, 20));

        updateButton.setFont(new java.awt.Font("微軟正黑體", 2, 12)); // NOI18N
        updateButton.setText("データを更新する");
        updateButton.setPreferredSize(new java.awt.Dimension(73, 20));
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateButtonMouseClicked(evt);
            }
        });

        loadLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        loadLabel1.setForeground(this.fontColor);
        loadLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/otherIcons/load.gif"))); // NOI18N
        loadLabel1.setPreferredSize(new java.awt.Dimension(20, 20));

        buffMemAutoCombo1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        buffMemAutoCombo1.setMaximumRowCount(6);
        buffMemAutoCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnFilter()));
        buffMemAutoCombo1.setSelectedIndex(0);
        buffMemAutoCombo1.setPreferredSize(new java.awt.Dimension(100, 25));
        buffMemAutoCombo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buffMemAutoCombo1ActionPerformed(evt);
            }
        });

        songCalMethod2.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        songCalMethod2.setModel(new javax.swing.DefaultComboBoxModel<>(SONG_API.returnMode2()));
        songCalMethod2.setSelectedIndex(0);
        songCalMethod2.setPreferredSize(new java.awt.Dimension(100, 25));
        songCalMethod2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                songCalMethod2ActionPerformed(evt);
            }
        });

        eventBounsLabel1.setFont(new java.awt.Font("微軟正黑體", 1, 14)); // NOI18N
        eventBounsLabel1.setForeground(this.fontColor);
        eventBounsLabel1.setText("ボーナス:");
        eventBounsLabel1.setPreferredSize(new java.awt.Dimension(245, 20));

        eventBonusCombo.setFont(new java.awt.Font("微軟正黑體", 0, 14)); // NOI18N
        eventBonusCombo.setMaximumRowCount(5);
        eventBonusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(GBP_API.returnBounsType()));
        eventBonusCombo.setSelectedIndex(0);
        eventBonusCombo.setSelectedItem(0);
        eventBonusCombo.setPreferredSize(new java.awt.Dimension(100, 25));
        eventBonusCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventBonusComboActionPerformed(evt);
            }
        });

        autoCheckbox.setFont(new java.awt.Font("微軟正黑體", 3, 14)); // NOI18N
        autoCheckbox.setForeground(this.fontColor);
        autoCheckbox.setText("X");
        autoCheckbox.setEnabled(false);
        autoCheckbox.setIconTextGap(10);
        autoCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                autoCheckboxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(areaLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel14, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel9, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel10, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel11, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(areaItemLevel12, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemCombo13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLevel13, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaLabelAuto1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemAutoLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaLabelAuto2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(areaItemLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resetItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(areaItemAutoCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(areaItemAutoLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(eventTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eventTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(eventBounsLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(eventBonusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(eventBuffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(2, 2, 2)
                                    .addComponent(buffMemAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(EventBuffMemberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(EventBuffMemberLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(EventBuffMemberLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(EventBuffMemberLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(EventBuffMemberLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(eventBuffMemCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBuffMemCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBuffMemCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBuffMemCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBuffMemCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(eventLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(songLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(memberSkillPercLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(memberSkillSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(memberSkillPerc6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(memSkillTimeCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(memberSkillTime6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(calMethodTxt6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(memberSkillPercLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(memberSkillPercLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(memberSkillPercLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(memberSkillSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberSkillSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberSkillSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(memberSkillPercLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberSkillPercLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(memberSkillSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberSkillSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberSkillPerc3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(memSkillTimeCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(memberSkillTime3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(calMethodTxt3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberSkillPerc4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(memSkillTimeCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(memberSkillTime4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(calMethodTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberSkillPerc5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(memSkillTimeCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(memberSkillTime5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(calMethodTxt5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(memberSkillPerc1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberSkillPerc2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memSkillTimeCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(memberSkillTime1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memSkillTimeCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(memberSkillTime2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(calMethodTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(calMethodTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(songNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(songFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(songFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(songNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(skillPercLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(autoCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(songCalMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(songFilter3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(songCalMethod2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(filterCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(filterCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(filterCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(filterCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(filterCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, 0)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(filterCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(filterCombo8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(filterCombo7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(filterCombo9, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, 0)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(filterCombo11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(filterCombo12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(filterCombo13, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(filterCombo14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(filterCombo10, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, 0)
                                                .addComponent(filterCombo15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(filterAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(filterAutoCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(filterAutoCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(memberCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(memberCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(memberCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(memberCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(memberCombo1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(teamMemberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(94, 94, 94)
                                        .addComponent(resetMemberButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(memberBand2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberBand1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(bandLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(memberName1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberName2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(charaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(rareLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memberRare1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(memberType1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memberRare2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(memberType2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(memberPow1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(memberPow2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(powLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memberTech2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(memberVis2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memberTech1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(memberVis1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(techLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(visLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memberTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(memberSkill2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(memberTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(memberSkill1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(skillLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberBand3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(memberName3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberRare3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberType3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberPow3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTech3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberVis3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTotal3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberSkill3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberBand4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(memberName4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberRare4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberType4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberPow4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTech4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberVis4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTotal4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberSkill4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(memberBand5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(memberName5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberRare5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberType5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberPow5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTech5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberVis5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(memberTotal5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(memberSkill5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(teamMemberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(loadLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1035, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1035, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1055, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(teamIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(15, 15, 15)
                                        .addComponent(teamIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(15, 15, 15)
                                        .addComponent(teamIcon3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(15, 15, 15)
                                        .addComponent(teamIcon4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(15, 15, 15)
                                        .addComponent(teamIcon5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(resultLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resultLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(resultLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(resultLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(resultLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(resultOnlyCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(screenCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(resultTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(resultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loadLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addComponent(teamMemberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(teamMemberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterAutoCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterAutoCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bandLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(charaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rareLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(powLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(techLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(visLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(resetMemberButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(skillLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memberCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberBand1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberName1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberRare1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberType1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberPow1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTech1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberVis1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberSkill1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memberCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberBand2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberName2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberRare2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberType2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberPow2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTech2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberVis2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberSkill2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memberCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberBand3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberName3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberRare3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberType3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberPow3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTech3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberVis3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTotal3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterCombo13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberSkill3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memberCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberBand4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberName4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberRare4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberType4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberPow4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTech4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberVis4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTotal4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberSkill4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(memberCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberBand5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberName5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberRare5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberType5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberPow5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTech5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberVis5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberTotal5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(memberSkill5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(areaItemLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(areaItemLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(resetItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(areaItemLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(eventLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(songLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(eventTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(areaLabelAuto1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(areaItemAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(areaItemAutoLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(songNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(songFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(songFilter2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(songFilter3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(eventTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(areaLabelAuto2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(areaItemAutoCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(areaItemAutoLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(areaLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemCombo8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(areaItemLevel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(areaLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(areaItemLevel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaItemCombo9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(areaItemLevel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaItemCombo10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(areaItemLevel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaItemCombo11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(35, 35, 35)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(areaItemCombo13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaItemLevel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(areaItemCombo14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(areaItemLevel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(songNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(songCalMethod2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBonusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(eventBounsLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(skillPercLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(songCalMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(autoCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(5, 5, 5)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(memberSkillPercLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memberSkillSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(2, 2, 2)
                                                .addComponent(memberSkillPercLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(memberSkillPerc1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memberSkillTime1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memSkillTimeCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(calMethodTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(2, 2, 2)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(memberSkillTime2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(memSkillTimeCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(calMethodTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(memberSkillPerc2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addComponent(memberSkillSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(memberSkillPercLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memberSkillSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(memberSkillTime3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(memSkillTimeCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(calMethodTxt3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(memberSkillPerc3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(2, 2, 2)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(memberSkillPercLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(memberSkillTime4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(memSkillTimeCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(calMethodTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(memberSkillPerc4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addComponent(memberSkillSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(memberSkillPercLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(memberSkillTime5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(memSkillTimeCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(calMethodTxt5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(memberSkillPerc5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(memberSkillSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(memberSkillPercLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(memberSkillSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(memberSkillTime6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(memSkillTimeCombo6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(calMethodTxt6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(memberSkillPerc6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(3, 3, 3)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(teamIcon1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(teamIcon2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(teamIcon3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(teamIcon4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(teamIcon5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(resultLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(resultOnlyCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(screenCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(resultLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(resultTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(resultLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(resultLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(resultLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EventBuffMemberLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventBuffMemCombo4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EventBuffMemberLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventBuffMemCombo5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventBuffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buffMemAutoCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EventBuffMemberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventBuffMemCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EventBuffMemberLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventBuffMemCombo2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EventBuffMemberLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventBuffMemCombo3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void areaItemCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo1ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[0][0] = this.areaItemCombo1.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo1ActionPerformed

    private void areaItemCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo2ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[1][0] = this.areaItemCombo2.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo2ActionPerformed

    private void areaItemCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo3ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[2][0] = this.areaItemCombo3.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo3ActionPerformed

    private void areaItemCombo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo4ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[3][0] = this.areaItemCombo4.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo4ActionPerformed

    private void areaItemCombo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo5ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[4][0] = this.areaItemCombo5.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo5ActionPerformed

    private void areaItemCombo6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo6ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[5][0] = this.areaItemCombo6.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo6ActionPerformed

    private void areaItemCombo7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo7ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo7ActionPerformed

    private void areaItemLevel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel1ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[0][1] = this.areaItemLevel1.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel1ActionPerformed

    private void areaItemLevel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel2ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[1][1] = this.areaItemLevel2.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel2ActionPerformed

    private void areaItemLevel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel3ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[2][1] = this.areaItemLevel3.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel3ActionPerformed

    private void areaItemLevel4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel4ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[3][1] = this.areaItemLevel4.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel4ActionPerformed

    private void areaItemLevel5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel5ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[4][1] = this.areaItemLevel5.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel5ActionPerformed

    private void areaItemLevel6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel6ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[5][1] = this.areaItemLevel6.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel6ActionPerformed

    private void areaItemLevel7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel7ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel7ActionPerformed

    private void areaItemCombo8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo8ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo8ActionPerformed

    private void areaItemCombo9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo9ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo9ActionPerformed

    private void areaItemCombo10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo10ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[9][0] = this.areaItemCombo10.getSelectedIndex(); // assign band
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo10ActionPerformed

    private void areaItemCombo11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo11ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo11ActionPerformed

    private void areaItemCombo12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo12ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[11][0] = this.areaItemCombo12.getSelectedIndex(); // assign type
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo12ActionPerformed

    private void areaItemCombo13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo13ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo13ActionPerformed

    private void areaItemCombo14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemCombo14ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[13][0] = this.areaItemCombo14.getSelectedIndex(); // assign type
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemCombo14ActionPerformed

    private void areaItemLevel8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel8ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel8ActionPerformed

    private void areaItemLevel9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel9ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel9ActionPerformed

    private void areaItemLevel10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel10ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[9][1] = this.areaItemLevel10.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel10ActionPerformed

    private void areaItemLevel11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel11ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel11ActionPerformed

    private void areaItemLevel12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel12ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[11][1] = this.areaItemLevel12.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel12ActionPerformed

    private void areaItemLevel13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel13ActionPerformed
        // TODO add your handling code here:
        //updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel13ActionPerformed

    private void areaItemLevel14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemLevel14ActionPerformed
        // TODO add your handling code here:
        this.itemEffects[13][1] = this.areaItemLevel14.getSelectedIndex(); // assign level
        updateTeamSumUp();
    }//GEN-LAST:event_areaItemLevel14ActionPerformed

    private void memberCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberCombo1ActionPerformed
        // TODO add your handling code here:
        //String str = memberCombo1.getSelectedItem().toString();
        int cardID = memberCombo1.getSelectedIndex();
        if(cardID == 0) {
            this.memSkillTimeCombo1.setSelectedIndex(0);
            this.memSkillTimeCombo6.setSelectedIndex(0);
        }
        String band = GBP_API.returnComboList1().get(cardID).getBand();
        String type = GBP_API.returnComboList1().get(cardID).getType();
        
        this.setBandIcon(this.memberBand1, band);
        this.setTypeIcon(this.memberType1, type);
        
        this.memberName1.setText(GBP_API.returnComboList1().get(cardID).getCharaName());
        this.memberRare1.setText("★"+GBP_API.returnComboList1().get(cardID).getRarity());
        try {
            URL url = new URL(GBP_API.returnComboList1().get(cardID).getImgPath());
            //Image imag = ImageIO.read(url);
            Image imag = Toolkit.getDefaultToolkit().createImage(url);
            this.teamIcon1.setIcon( new ImageIcon(imag) ); 
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.memberPow1.setText(""+GBP_API.returnComboList1().get(cardID).getPow());
        this.memberTech1.setText(""+GBP_API.returnComboList1().get(cardID).getTech());
        this.memberVis1.setText(""+GBP_API.returnComboList1().get(cardID).getVis());
        this.memberTotal1.setText(""+GBP_API.returnComboList1().get(cardID).getTotal());
        
        this.memberSkill1.setText(GBP_API.returnComboList1().get(cardID).getSkillType());
        this.memberSkillSlider1.setValue(GBP_API.returnComboList1().get(cardID).getScorePerc());
        this.memberSkillSlider6.setValue(GBP_API.returnComboList1().get(cardID).getScorePerc());
        this.memSkillTimeCombo1.setSelectedItem(""+GBP_API.returnComboList1().get(cardID).getSkillLastLong());
        this.memSkillTimeCombo6.setSelectedItem(""+GBP_API.returnComboList1().get(cardID).getSkillLastLong());
        
        GBP_API.setMemberName(0, GBP_API.returnComboList1().get(cardID).getCharaName());
        GBP_API.setMemberBand(0, GBP_API.bandToNumber(band));   // temporary
        GBP_API.setMemberType(0, GBP_API.typeToNumber(type));   // temporary
        GBP_API.setMemberValue(0, GBP_API.returnComboList1().get(cardID).getSpec());
        updateTeamSumUp();
    }//GEN-LAST:event_memberCombo1ActionPerformed

    private void memberCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberCombo2ActionPerformed
        // TODO add your handling code here:
        //String str = memberCombo2.getSelectedItem().toString();
        int cardID = memberCombo2.getSelectedIndex();
        if(cardID == 0) 
            this.memSkillTimeCombo2.setSelectedIndex(0);
        String band = GBP_API.returnComboList2().get(cardID).getBand();
        String type = GBP_API.returnComboList2().get(cardID).getType();
        
        this.setBandIcon(this.memberBand2, band);
        this.setTypeIcon(this.memberType2, type);
        
        this.memberName2.setText(GBP_API.returnComboList2().get(cardID).getCharaName());
        this.memberRare2.setText("★"+GBP_API.returnComboList2().get(cardID).getRarity());
        try {
            URL url = new URL(GBP_API.returnComboList2().get(cardID).getImgPath());
            //Image imag = ImageIO.read(url);
            Image imag = Toolkit.getDefaultToolkit().createImage(url);
            this.teamIcon2.setIcon( new ImageIcon(imag) ); 
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.memberPow2.setText(""+GBP_API.returnComboList2().get(cardID).getPow());
        this.memberTech2.setText(""+GBP_API.returnComboList2().get(cardID).getTech());
        this.memberVis2.setText(""+GBP_API.returnComboList2().get(cardID).getVis());
        this.memberTotal2.setText(""+GBP_API.returnComboList2().get(cardID).getTotal());
        
        this.memberSkill2.setText(GBP_API.returnComboList2().get(cardID).getSkillType());
        this.memberSkillSlider2.setValue(GBP_API.returnComboList2().get(cardID).getScorePerc());
        this.memSkillTimeCombo2.setSelectedItem(""+GBP_API.returnComboList2().get(cardID).getSkillLastLong());
        
        GBP_API.setMemberName(1, GBP_API.returnComboList2().get(cardID).getCharaName());
        GBP_API.setMemberBand(1, GBP_API.bandToNumber(band));   // temporary
        GBP_API.setMemberType(1, GBP_API.typeToNumber(type));   // temporary
        GBP_API.setMemberValue(1, GBP_API.returnComboList2().get(cardID).getSpec());
        updateTeamSumUp();
    }//GEN-LAST:event_memberCombo2ActionPerformed

    private void memberCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberCombo3ActionPerformed
        // TODO add your handling code here:
        //String str = memberCombo3.getSelectedItem().toString();
        int cardID = memberCombo3.getSelectedIndex();
        if(cardID == 0) 
            this.memSkillTimeCombo3.setSelectedIndex(0);
        String band = GBP_API.returnComboList3().get(cardID).getBand();
        String type = GBP_API.returnComboList3().get(cardID).getType();
        
        this.setBandIcon(this.memberBand3, band);
        this.setTypeIcon(this.memberType3, type);
        
        this.memberName3.setText(GBP_API.returnComboList3().get(cardID).getCharaName());
        this.memberRare3.setText("★"+GBP_API.returnComboList3().get(cardID).getRarity());
        try {
            URL url = new URL(GBP_API.returnComboList3().get(cardID).getImgPath());
            //Image imag = ImageIO.read(url);
            Image imag = Toolkit.getDefaultToolkit().createImage(url);
            this.teamIcon3.setIcon( new ImageIcon(imag) ); 
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.memberPow3.setText(""+GBP_API.returnComboList3().get(cardID).getPow());
        this.memberTech3.setText(""+GBP_API.returnComboList3().get(cardID).getTech());
        this.memberVis3.setText(""+GBP_API.returnComboList3().get(cardID).getVis());
        this.memberTotal3.setText(""+GBP_API.returnComboList3().get(cardID).getTotal());
        
        this.memberSkill3.setText(GBP_API.returnComboList3().get(cardID).getSkillType());
        this.memberSkillSlider3.setValue(GBP_API.returnComboList3().get(cardID).getScorePerc());
        this.memSkillTimeCombo3.setSelectedItem(""+GBP_API.returnComboList3().get(cardID).getSkillLastLong());
        
        GBP_API.setMemberName(2, GBP_API.returnComboList3().get(cardID).getCharaName());
        GBP_API.setMemberBand(2, GBP_API.bandToNumber(band));   // temporary
        GBP_API.setMemberType(2, GBP_API.typeToNumber(type));   // temporary
        GBP_API.setMemberValue(2, GBP_API.returnComboList3().get(cardID).getSpec());
        updateTeamSumUp();
    }//GEN-LAST:event_memberCombo3ActionPerformed

    private void memberCombo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberCombo4ActionPerformed
        // TODO add your handling code here:
        //String str = memberCombo4.getSelectedItem().toString();
        int cardID = memberCombo4.getSelectedIndex();
        if(cardID == 0) 
            this.memSkillTimeCombo4.setSelectedIndex(0);
        String band = GBP_API.returnComboList4().get(cardID).getBand();
        String type = GBP_API.returnComboList4().get(cardID).getType();
        
        this.setBandIcon(this.memberBand4, band);
        this.setTypeIcon(this.memberType4, type);
        
        this.memberName4.setText(GBP_API.returnComboList4().get(cardID).getCharaName());
        this.memberRare4.setText("★"+GBP_API.returnComboList4().get(cardID).getRarity());
        try {
            URL url = new URL(GBP_API.returnComboList4().get(cardID).getImgPath());
            //Image imag = ImageIO.read(url);
            Image imag = Toolkit.getDefaultToolkit().createImage(url);
            this.teamIcon4.setIcon( new ImageIcon(imag) ); 
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.memberPow4.setText(""+GBP_API.returnComboList4().get(cardID).getPow());
        this.memberTech4.setText(""+GBP_API.returnComboList4().get(cardID).getTech());
        this.memberVis4.setText(""+GBP_API.returnComboList4().get(cardID).getVis());
        this.memberTotal4.setText(""+GBP_API.returnComboList4().get(cardID).getTotal());
        
        this.memberSkill4.setText(GBP_API.returnComboList4().get(cardID).getSkillType());
        this.memberSkillSlider4.setValue(GBP_API.returnComboList4().get(cardID).getScorePerc());
        this.memSkillTimeCombo4.setSelectedItem(""+GBP_API.returnComboList4().get(cardID).getSkillLastLong());
        
        GBP_API.setMemberName(3, GBP_API.returnComboList4().get(cardID).getCharaName());
        GBP_API.setMemberBand(3, GBP_API.bandToNumber(band));   // temporary
        GBP_API.setMemberType(3, GBP_API.typeToNumber(type));   // temporary
        GBP_API.setMemberValue(3, GBP_API.returnComboList4().get(cardID).getSpec());
        updateTeamSumUp();
    }//GEN-LAST:event_memberCombo4ActionPerformed

    private void memberCombo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberCombo5ActionPerformed
        // TODO add your handling code here:
        //String str = memberCombo5.getSelectedItem().toString();
        int cardID = memberCombo5.getSelectedIndex();
        if(cardID == 0) 
            this.memSkillTimeCombo5.setSelectedIndex(0);
        String band = GBP_API.returnComboList5().get(cardID).getBand();
        String type = GBP_API.returnComboList5().get(cardID).getType();
        
        this.setBandIcon(this.memberBand5, band);
        this.setTypeIcon(this.memberType5, type);
        
        this.memberName5.setText(GBP_API.returnComboList5().get(cardID).getCharaName());
        this.memberRare5.setText("★"+GBP_API.returnComboList5().get(cardID).getRarity());
        try {
            URL url = new URL(GBP_API.returnComboList5().get(cardID).getImgPath());
            //Image imag = ImageIO.read(url);
            Image imag = Toolkit.getDefaultToolkit().createImage(url);
            this.teamIcon5.setIcon( new ImageIcon(imag) ); 
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.memberPow5.setText(""+GBP_API.returnComboList5().get(cardID).getPow());
        this.memberTech5.setText(""+GBP_API.returnComboList5().get(cardID).getTech());
        this.memberVis5.setText(""+GBP_API.returnComboList5().get(cardID).getVis());
        this.memberTotal5.setText(""+GBP_API.returnComboList5().get(cardID).getTotal());
        
        this.memberSkill5.setText(GBP_API.returnComboList5().get(cardID).getSkillType());
        this.memberSkillSlider5.setValue(GBP_API.returnComboList5().get(cardID).getScorePerc());
        this.memSkillTimeCombo5.setSelectedItem(""+GBP_API.returnComboList5().get(cardID).getSkillLastLong());
        
        GBP_API.setMemberName(4, GBP_API.returnComboList5().get(cardID).getCharaName());
        GBP_API.setMemberBand(4, GBP_API.bandToNumber(band));   // temporary
        GBP_API.setMemberType(4, GBP_API.typeToNumber(type));   // temporary
        GBP_API.setMemberValue(4, GBP_API.returnComboList5().get(cardID).getSpec());
        updateTeamSumUp();
    }//GEN-LAST:event_memberCombo5ActionPerformed

    private void resetMemberButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetMemberButtonMouseClicked
        // TODO add your handling code here:
        this.filterAutoCombo1.setSelectedIndex(0); 
        this.filterAutoCombo2.setSelectedIndex(0); 
        this.filterAutoCombo3.setSelectedIndex(0); 
        this.filterCombo1.setSelectedIndex(0); this.filterCombo6.setSelectedIndex(0); 
            this.filterCombo11.setSelectedIndex(0); this.memberCombo1.setSelectedIndex(0); // reset
        this.filterCombo2.setSelectedIndex(0); this.filterCombo7.setSelectedIndex(0); 
            this.filterCombo12.setSelectedIndex(0); this.memberCombo2.setSelectedIndex(0); // reset
        this.filterCombo3.setSelectedIndex(0); this.filterCombo8.setSelectedIndex(0); 
            this.filterCombo13.setSelectedIndex(0); this.memberCombo3.setSelectedIndex(0); // reset
        this.filterCombo4.setSelectedIndex(0); this.filterCombo9.setSelectedIndex(0); 
            this.filterCombo14.setSelectedIndex(0); this.memberCombo4.setSelectedIndex(0); // reset
        this.filterCombo5.setSelectedIndex(0); this.filterCombo10.setSelectedIndex(0); 
            this.filterCombo15.setSelectedIndex(0); this.memberCombo5.setSelectedIndex(0); // reset
            
        this.memSkillTimeCombo1.setSelectedIndex(0); this.memSkillTimeCombo2.setSelectedIndex(0);
        this.memSkillTimeCombo3.setSelectedIndex(0); this.memSkillTimeCombo4.setSelectedIndex(0);
        this.memSkillTimeCombo5.setSelectedIndex(0); this.memSkillTimeCombo6.setSelectedIndex(0);
    }//GEN-LAST:event_resetMemberButtonMouseClicked

    private void resetItemButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetItemButtonMouseClicked
        // TODO add your handling code here:
        this.areaItemCombo1.setSelectedIndex(0); this.areaItemLevel1.setSelectedIndex(0);
        this.areaItemCombo2.setSelectedIndex(0); this.areaItemLevel2.setSelectedIndex(0);
        this.areaItemCombo3.setSelectedIndex(0); this.areaItemLevel3.setSelectedIndex(0);
        this.areaItemCombo4.setSelectedIndex(0); this.areaItemLevel4.setSelectedIndex(0);
        this.areaItemCombo5.setSelectedIndex(0); this.areaItemLevel5.setSelectedIndex(0);
        this.areaItemCombo6.setSelectedIndex(0); this.areaItemLevel6.setSelectedIndex(0);
        this.areaItemCombo7.setSelectedIndex(0); this.areaItemLevel7.setSelectedIndex(0);
        this.areaItemCombo8.setSelectedIndex(0); this.areaItemLevel8.setSelectedIndex(0);
        this.areaItemCombo9.setSelectedIndex(0); this.areaItemLevel9.setSelectedIndex(0);
        this.areaItemCombo10.setSelectedIndex(0); this.areaItemLevel10.setSelectedIndex(0);
        this.areaItemCombo11.setSelectedIndex(0); this.areaItemLevel11.setSelectedIndex(0);
        this.areaItemCombo12.setSelectedIndex(0); this.areaItemLevel12.setSelectedIndex(0);
        this.areaItemCombo13.setSelectedIndex(0); this.areaItemLevel13.setSelectedIndex(0);
        this.areaItemCombo14.setSelectedIndex(0); this.areaItemLevel14.setSelectedIndex(0);
        this.areaItemAutoCombo1.setSelectedIndex(0); this.areaItemAutoLevel1.setSelectedIndex(0);
        this.areaItemAutoCombo2.setSelectedIndex(0); this.areaItemAutoLevel2.setSelectedIndex(0);
    }//GEN-LAST:event_resetItemButtonMouseClicked

    private void screenCaptureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_screenCaptureMouseClicked
        // TODO add your handling code here:
        Dimension dim = this.getContentPane().getSize();
        BufferedImage bufferedImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        Graphics result = bufferedImage.createGraphics();
        result.setClip(0, (this.resultOnly?dim.height-160 : 0), dim.width, (this.resultOnly?160 : dim.height));
        //result.drawImage(bufferedImage, 0, (this.resultOnly?dim.height-160 : 0), dim.width, (this.resultOnly?160 : dim.height), null);
        System.out.println("width: "+result.getClipBounds().width+", height: "+result.getClipBounds().height);
        Rectangle screenRect = new Rectangle(
                (int)this.getContentPane().getLocationOnScreen().x,
                (int)this.getContentPane().getLocationOnScreen().y+(this.resultOnly?dim.height-160 : 0), 
                result.getClipBounds().width, 
                result.getClipBounds().height
        );
        //this.getContentPane().paint(result);
        try {
            BufferedImage output = new Robot().createScreenCapture(screenRect);
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(Calendar.getInstance().getTime());
            File f = new File("Screenshot_"+timeStamp+".png");
            ImageIO.write(output, "png", f);
            /*JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(1.0f);
            
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            // specifies where the jpg image has to be written
            writer.setOutput(new FileImageOutputStream(
                    new File("Screenshot_"+timeStamp+".png")));
            writer.write(null, new IIOImage(output, null, null), jpegParams);
            writer.dispose();*/
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (AWTException ex) {
            Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_screenCaptureMouseClicked

    private void filterCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo1ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.filterCombo1.getSelectedIndex(),
            id2 = filterCombo6.getSelectedIndex(),
            id3 = filterCombo11.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        
        GBP_API.setComboList1(GBP_API
                .updateFilteredCard(filter, id, filterCombo1.getSelectedItem().toString()
                        , filter2, id2, filterCombo6.getSelectedItem().toString()
                        , filter3, id3, filterCombo11.getSelectedItem().toString())
        );
        this.memberCombo1.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(1)
                )
        );
        this.memberCombo1.setSelectedIndex(0);
    }//GEN-LAST:event_filterCombo1ActionPerformed

    private void filterCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo2ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.filterCombo2.getSelectedIndex(),
            id2 = filterCombo7.getSelectedIndex(),
            id3 = filterCombo12.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        GBP_API.setComboList2(GBP_API
                .updateFilteredCard(filter, id, filterCombo2.getSelectedItem().toString()
                        , filter2, id2, filterCombo7.getSelectedItem().toString()
                        , filter3, id3, filterCombo12.getSelectedItem().toString())
        );
        this.memberCombo2.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(2)
                )
        );
        this.memberCombo2.setSelectedIndex(0);
    }//GEN-LAST:event_filterCombo2ActionPerformed

    private void filterCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo3ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.filterCombo3.getSelectedIndex(),
            id2 = filterCombo8.getSelectedIndex(),
            id3 = filterCombo13.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        GBP_API.setComboList3(GBP_API
                .updateFilteredCard(filter, id, filterCombo3.getSelectedItem().toString()
                        , filter2, id2, filterCombo8.getSelectedItem().toString()
                        , filter3, id3, filterCombo13.getSelectedItem().toString())
        );
        this.memberCombo3.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(3)
                )
        );
        this.memberCombo3.setSelectedIndex(0);
    }//GEN-LAST:event_filterCombo3ActionPerformed

    private void filterCombo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo4ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.filterCombo4.getSelectedIndex(),
            id2 = filterCombo9.getSelectedIndex(),
            id3 = filterCombo14.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        GBP_API.setComboList4(GBP_API
                .updateFilteredCard(filter, id, filterCombo4.getSelectedItem().toString()
                        , filter2, id2, filterCombo9.getSelectedItem().toString()
                        , filter3, id3, filterCombo14.getSelectedItem().toString())
        );
        this.memberCombo4.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(4)
                )
        );
        this.memberCombo4.setSelectedIndex(0);
    }//GEN-LAST:event_filterCombo4ActionPerformed

    private void filterCombo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo5ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.filterCombo5.getSelectedIndex(),
            id2 = filterCombo10.getSelectedIndex(),
            id3 = filterCombo15.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        GBP_API.setComboList5(GBP_API
                .updateFilteredCard(filter, id, filterCombo5.getSelectedItem().toString()
                        , filter2, id2, filterCombo10.getSelectedItem().toString()
                        , filter3, id3, filterCombo15.getSelectedItem().toString())
        );
        this.memberCombo5.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        GBP_API.returnCardName(5)
                )
        );
        this.memberCombo5.setSelectedIndex(0);
    }//GEN-LAST:event_filterCombo5ActionPerformed

    private void eventBuffMemCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBuffMemCombo1ActionPerformed
        // TODO add your handling code here:
        if(this.eventBuffMemCombo1.getSelectedIndex() != 0) {
            GBP_API.setEventBuffMember(0, this.GBP_API.returnCharaFullname(this.eventBuffMemCombo1.getSelectedIndex()));
        } else {
            GBP_API.setEventBuffMember(0, "N/A");
        }
        updateTeamSumUp();
    }//GEN-LAST:event_eventBuffMemCombo1ActionPerformed

    private void eventBuffMemCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBuffMemCombo2ActionPerformed
        // TODO add your handling code here:
        if(this.eventBuffMemCombo2.getSelectedIndex() != 0) {
            GBP_API.setEventBuffMember(1, this.GBP_API.returnCharaFullname(this.eventBuffMemCombo2.getSelectedIndex()));
        } else {
            GBP_API.setEventBuffMember(1, "N/A");
        }
        updateTeamSumUp();
    }//GEN-LAST:event_eventBuffMemCombo2ActionPerformed

    private void eventBuffMemCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBuffMemCombo3ActionPerformed
        // TODO add your handling code here:
        if(this.eventBuffMemCombo3.getSelectedIndex() != 0) {
            GBP_API.setEventBuffMember(2, this.GBP_API.returnCharaFullname(this.eventBuffMemCombo3.getSelectedIndex()));
        } else {
            GBP_API.setEventBuffMember(2, "N/A");
        }
        updateTeamSumUp();
    }//GEN-LAST:event_eventBuffMemCombo3ActionPerformed

    private void eventBuffMemCombo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBuffMemCombo4ActionPerformed
        // TODO add your handling code here:
        if(this.eventBuffMemCombo4.getSelectedIndex() != 0) {
            GBP_API.setEventBuffMember(3, this.GBP_API.returnCharaFullname(this.eventBuffMemCombo4.getSelectedIndex()));
        } else {
            GBP_API.setEventBuffMember(3, "N/A");
        }
        updateTeamSumUp();
    }//GEN-LAST:event_eventBuffMemCombo4ActionPerformed

    private void eventBuffMemCombo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBuffMemCombo5ActionPerformed
        // TODO add your handling code here:
        if(this.eventBuffMemCombo5.getSelectedIndex() != 0) {
            GBP_API.setEventBuffMember(4, this.GBP_API.returnCharaFullname(this.eventBuffMemCombo5.getSelectedIndex()));
        } else {
            GBP_API.setEventBuffMember(4, "N/A");
        }
        updateTeamSumUp();
    }//GEN-LAST:event_eventBuffMemCombo5ActionPerformed

    private void eventTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventTypeComboActionPerformed
        // TODO add your handling code here:
        GBP_API.setEventType(this.eventTypeCombo.getSelectedIndex());
        updateTeamSumUp();
    }//GEN-LAST:event_eventTypeComboActionPerformed

    private void areaItemAutoCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemAutoCombo1ActionPerformed
        // TODO add your handling code here:
        int bandID = areaItemAutoCombo1.getSelectedIndex();
        areaItemCombo1.setSelectedIndex(bandID); 
        areaItemCombo2.setSelectedIndex(bandID);
        areaItemCombo3.setSelectedIndex(bandID); 
        areaItemCombo4.setSelectedIndex(bandID);
        areaItemCombo5.setSelectedIndex(bandID);
        areaItemCombo6.setSelectedIndex(bandID);
        areaItemCombo10.setSelectedIndex(bandID);
    }//GEN-LAST:event_areaItemAutoCombo1ActionPerformed

    private void areaItemAutoLevel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemAutoLevel1ActionPerformed
        // TODO add your handling code here:
        int bandLevel = areaItemAutoLevel1.getSelectedIndex();
        areaItemLevel1.setSelectedIndex(bandLevel); 
        areaItemLevel2.setSelectedIndex(bandLevel);
        areaItemLevel3.setSelectedIndex(bandLevel); 
        areaItemLevel4.setSelectedIndex(bandLevel);
        areaItemLevel5.setSelectedIndex(bandLevel);
        if(bandLevel > 0) {
            areaItemLevel6.setSelectedIndex(bandLevel-1);
            areaItemLevel10.setSelectedIndex(bandLevel-1);
        } else {
            areaItemLevel6.setSelectedIndex(bandLevel);
            areaItemLevel10.setSelectedIndex(bandLevel);
        }
    }//GEN-LAST:event_areaItemAutoLevel1ActionPerformed

    private void areaItemAutoCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemAutoCombo2ActionPerformed
        // TODO add your handling code here:
        int typeID = areaItemAutoCombo2.getSelectedIndex();
        areaItemCombo12.setSelectedIndex(typeID);
        areaItemCombo14.setSelectedIndex(typeID);
    }//GEN-LAST:event_areaItemAutoCombo2ActionPerformed

    private void areaItemAutoLevel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaItemAutoLevel2ActionPerformed
        // TODO add your handling code here:
        int typeLevel = areaItemAutoLevel2.getSelectedIndex();
        if(typeLevel > 0) {
            areaItemLevel12.setSelectedIndex(typeLevel-1);
            areaItemLevel14.setSelectedIndex(typeLevel-1);
        } else {
            areaItemLevel12.setSelectedIndex(typeLevel);
            areaItemLevel14.setSelectedIndex(typeLevel);
        }
    }//GEN-LAST:event_areaItemAutoLevel2ActionPerformed

    private void filterCombo6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo6ActionPerformed
        // TODO add your handling code here:
        this.filterCombo1ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo6ActionPerformed

    private void filterCombo7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo7ActionPerformed
        // TODO add your handling code here:
        this.filterCombo2ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo7ActionPerformed

    private void filterCombo8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo8ActionPerformed
        // TODO add your handling code here:
        this.filterCombo3ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo8ActionPerformed

    private void filterCombo9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo9ActionPerformed
        // TODO add your handling code here:
        this.filterCombo4ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo9ActionPerformed

    private void filterCombo10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo10ActionPerformed
        // TODO add your handling code here:
        this.filterCombo5ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo10ActionPerformed

    private void filterCombo11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo11ActionPerformed
        // TODO add your handling code here:
        this.filterCombo1ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo11ActionPerformed

    private void filterCombo12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo12ActionPerformed
        // TODO add your handling code here:
        this.filterCombo2ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo12ActionPerformed

    private void filterCombo13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo13ActionPerformed
        // TODO add your handling code here:
        this.filterCombo3ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo13ActionPerformed

    private void filterCombo14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo14ActionPerformed
        // TODO add your handling code here:
        this.filterCombo4ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo14ActionPerformed

    private void filterCombo15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCombo15ActionPerformed
        // TODO add your handling code here:
        this.filterCombo5ActionPerformed(evt);
    }//GEN-LAST:event_filterCombo15ActionPerformed

    private void songNameComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songNameComboActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_songNameComboActionPerformed

    private void memberSkillSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider2StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc2.setText(this.memberSkillSlider2.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider2StateChanged

    private void memberSkillSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider3StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc3.setText(this.memberSkillSlider3.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider3StateChanged

    private void memberSkillSlider4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider4StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc4.setText(this.memberSkillSlider4.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider4StateChanged

    private void memberSkillSlider5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider5StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc5.setText(this.memberSkillSlider5.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider5StateChanged

    private void memberSkillSlider6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider6StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc6.setText(this.memberSkillSlider6.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider6StateChanged

    private void resultOnlyCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_resultOnlyCheckboxStateChanged
        // TODO add your handling code here:
        this.resultOnly = this.resultOnlyCheckbox.isSelected();
    }//GEN-LAST:event_resultOnlyCheckboxStateChanged

    private void songFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songFilter1ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.songFilter1.getSelectedIndex(),
            id2 = songFilter2.getSelectedIndex(),
            id3 = songFilter3.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        SONG_API.setSongList(SONG_API
                .updateFilteredSong(filter, id, songFilter1.getSelectedItem().toString()
                        , filter2, id2, songFilter2.getSelectedItem().toString()
                        , filter3, id3, songFilter3.getSelectedItem().toString())
        );
        this.songNameCombo.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        SONG_API.returnFilteredSongName()
                )
        );
        this.songNameCombo.setSelectedIndex(0);
    }//GEN-LAST:event_songFilter1ActionPerformed

    private void songFilter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songFilter2ActionPerformed
        // TODO add your handling code here:
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.songFilter1.getSelectedIndex(),
            id2 = songFilter2.getSelectedIndex(),
            id3 = songFilter3.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        SONG_API.setSongList(SONG_API
                .updateFilteredSong(filter, id, songFilter1.getSelectedItem().toString()
                        , filter2, id2, songFilter2.getSelectedItem().toString()
                        , filter3, id3, songFilter3.getSelectedItem().toString())
        );
        this.songNameCombo.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        SONG_API.returnFilteredSongName()
                )
        );
        this.songNameCombo.setSelectedIndex(0);
    }//GEN-LAST:event_songFilter2ActionPerformed

    private void memberSkillSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memberSkillSlider1StateChanged
        // TODO add your handling code here:
        this.memberSkillPerc1.setText(this.memberSkillSlider1.getValue()+"%");
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memberSkillSlider1StateChanged

    private void memSkillTimeCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo1ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo1ActionPerformed

    private void memSkillTimeCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo2ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo2ActionPerformed

    private void memSkillTimeCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo3ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo3ActionPerformed

    private void memSkillTimeCombo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo4ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo4ActionPerformed

    private void memSkillTimeCombo5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo5ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo5ActionPerformed

    private void memSkillTimeCombo6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memSkillTimeCombo6ActionPerformed
        // TODO add your handling code here:
        this.updateSongTheoryScore();
    }//GEN-LAST:event_memSkillTimeCombo6ActionPerformed

    private void songFilter3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songFilter3ActionPerformed
        boolean filter = false, filter2 = false, filter3 = false;
        int id = this.songFilter1.getSelectedIndex(),
            id2 = songFilter2.getSelectedIndex(),
            id3 = songFilter3.getSelectedIndex();
        if(id > 0) filter = true;
        if(id2 > 0) filter2 = true;
        if(id3 > 0) filter3 = true;
        SONG_API.setSongList(SONG_API
                .updateFilteredSong(filter, id, songFilter1.getSelectedItem().toString()
                        , filter2, id2, songFilter2.getSelectedItem().toString()
                        , filter3, id3, songFilter3.getSelectedItem().toString())
        );
        this.songNameCombo.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        SONG_API.returnFilteredSongName()
                )
        );
        this.songNameCombo.setSelectedIndex(0);
    }//GEN-LAST:event_songFilter3ActionPerformed

    private void songCalMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songCalMethodActionPerformed
        // TODO add your handling code here:
        this.updateTeamSumUp();
    }//GEN-LAST:event_songCalMethodActionPerformed

    private void filterAutoCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterAutoCombo1ActionPerformed
        // TODO add your handling code here:
        int band = this.filterAutoCombo1.getSelectedIndex();
        this.filterCombo1.setSelectedIndex(band);
        this.filterCombo2.setSelectedIndex(band);
        this.filterCombo3.setSelectedIndex(band);
        this.filterCombo4.setSelectedIndex(band);
        this.filterCombo5.setSelectedIndex(band);
    }//GEN-LAST:event_filterAutoCombo1ActionPerformed

    private void filterAutoCombo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterAutoCombo2ActionPerformed
        // TODO add your handling code here:
        int rare = this.filterAutoCombo2.getSelectedIndex();
        this.filterCombo6.setSelectedIndex(rare);
        this.filterCombo7.setSelectedIndex(rare);
        this.filterCombo8.setSelectedIndex(rare);
        this.filterCombo9.setSelectedIndex(rare);
        this.filterCombo10.setSelectedIndex(rare);
    }//GEN-LAST:event_filterAutoCombo2ActionPerformed

    private void filterAutoCombo3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterAutoCombo3ActionPerformed
        // TODO add your handling code here:
        int type = this.filterAutoCombo3.getSelectedIndex();
        this.filterCombo11.setSelectedIndex(type);
        this.filterCombo12.setSelectedIndex(type);
        this.filterCombo13.setSelectedIndex(type);
        this.filterCombo14.setSelectedIndex(type);
        this.filterCombo15.setSelectedIndex(type);
    }//GEN-LAST:event_filterAutoCombo3ActionPerformed

    private void buffMemAutoCombo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buffMemAutoCombo1ActionPerformed
        // TODO add your handling code here:
        int selID = buffMemAutoCombo1.getSelectedIndex();
        if(selID == 0) {
            eventBuffMemCombo1.setSelectedIndex(selID);
            eventBuffMemCombo2.setSelectedIndex(selID);
            eventBuffMemCombo3.setSelectedIndex(selID);
            eventBuffMemCombo4.setSelectedIndex(selID);
            eventBuffMemCombo5.setSelectedIndex(selID);
        } else {
            eventBuffMemCombo1.setSelectedIndex( ((selID-1)*5) +1 );
            eventBuffMemCombo2.setSelectedIndex( ((selID-1)*5) +2 );
            eventBuffMemCombo3.setSelectedIndex( ((selID-1)*5) +3 );
            eventBuffMemCombo4.setSelectedIndex( ((selID-1)*5) +4 );
            eventBuffMemCombo5.setSelectedIndex( ((selID-1)*5) +5 );
        }
    }//GEN-LAST:event_buffMemAutoCombo1ActionPerformed

    private void songCalMethod2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_songCalMethod2ActionPerformed
        // TODO add your handling code here:
        this.updateTeamSumUp();
    }//GEN-LAST:event_songCalMethod2ActionPerformed

    private void eventBonusComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventBonusComboActionPerformed
        // TODO add your handling code here:
        GBP_API.setBonusType(this.eventBonusCombo.getSelectedIndex());
        updateTeamSumUp();
    }//GEN-LAST:event_eventBonusComboActionPerformed

    private void autoCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_autoCheckboxStateChanged
        // TODO add your handling code here:
        this.isAuto = this.autoCheckbox.isSelected();
        isCalculated = false;
        updateSongTheoryScore();
    }//GEN-LAST:event_autoCheckboxStateChanged

    private void updateButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateButtonMouseClicked
        // TODO add your handling code here:
        this.loadLabel1.setVisible(true); // loading
        Thread t1 = new Thread(() -> {
            try {
                GBP_API.getUpdateData();
            } catch (IOException ex) {
                Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                SONG_API.getUpdateData();
            } catch (IOException ex) {
                Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }
        this.loadLabel1.setVisible(false); // finish loading 
        this.resetMemberButtonMouseClicked(evt);
    }//GEN-LAST:event_updateButtonMouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new GBP_Calculator_GUI().setVisible(true);
            } catch (AWTException | MalformedURLException ex) {
                Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GBP_Calculator_GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EventBuffMemberLabel1;
    private javax.swing.JLabel EventBuffMemberLabel2;
    private javax.swing.JLabel EventBuffMemberLabel3;
    private javax.swing.JLabel EventBuffMemberLabel4;
    private javax.swing.JLabel EventBuffMemberLabel5;
    private javax.swing.JLabel Title;
    private javax.swing.JComboBox<String> areaItemAutoCombo1;
    private javax.swing.JComboBox<String> areaItemAutoCombo2;
    private javax.swing.JComboBox<String> areaItemAutoLevel1;
    private javax.swing.JComboBox<String> areaItemAutoLevel2;
    private javax.swing.JComboBox<String> areaItemCombo1;
    private javax.swing.JComboBox<String> areaItemCombo10;
    private javax.swing.JComboBox<String> areaItemCombo11;
    private javax.swing.JComboBox<String> areaItemCombo12;
    private javax.swing.JComboBox<String> areaItemCombo13;
    private javax.swing.JComboBox<String> areaItemCombo14;
    private javax.swing.JComboBox<String> areaItemCombo2;
    private javax.swing.JComboBox<String> areaItemCombo3;
    private javax.swing.JComboBox<String> areaItemCombo4;
    private javax.swing.JComboBox<String> areaItemCombo5;
    private javax.swing.JComboBox<String> areaItemCombo6;
    private javax.swing.JComboBox<String> areaItemCombo7;
    private javax.swing.JComboBox<String> areaItemCombo8;
    private javax.swing.JComboBox<String> areaItemCombo9;
    private javax.swing.JLabel areaItemLabel1;
    private javax.swing.JLabel areaItemLabel2;
    private javax.swing.JLabel areaItemLabel3;
    private javax.swing.JComboBox<String> areaItemLevel1;
    private javax.swing.JComboBox<String> areaItemLevel10;
    private javax.swing.JComboBox<String> areaItemLevel11;
    private javax.swing.JComboBox<String> areaItemLevel12;
    private javax.swing.JComboBox<String> areaItemLevel13;
    private javax.swing.JComboBox<String> areaItemLevel14;
    private javax.swing.JComboBox<String> areaItemLevel2;
    private javax.swing.JComboBox<String> areaItemLevel3;
    private javax.swing.JComboBox<String> areaItemLevel4;
    private javax.swing.JComboBox<String> areaItemLevel5;
    private javax.swing.JComboBox<String> areaItemLevel6;
    private javax.swing.JComboBox<String> areaItemLevel7;
    private javax.swing.JComboBox<String> areaItemLevel8;
    private javax.swing.JComboBox<String> areaItemLevel9;
    private javax.swing.JLabel areaLabel1;
    private javax.swing.JLabel areaLabel10;
    private javax.swing.JLabel areaLabel11;
    private javax.swing.JLabel areaLabel12;
    private javax.swing.JLabel areaLabel13;
    private javax.swing.JLabel areaLabel14;
    private javax.swing.JLabel areaLabel2;
    private javax.swing.JLabel areaLabel3;
    private javax.swing.JLabel areaLabel4;
    private javax.swing.JLabel areaLabel5;
    private javax.swing.JLabel areaLabel6;
    private javax.swing.JLabel areaLabel7;
    private javax.swing.JLabel areaLabel8;
    private javax.swing.JLabel areaLabel9;
    private javax.swing.JLabel areaLabelAuto1;
    private javax.swing.JLabel areaLabelAuto2;
    private javax.swing.JCheckBox autoCheckbox;
    private javax.swing.JLabel bandLabel;
    private javax.swing.JComboBox<String> buffMemAutoCombo1;
    private javax.swing.JLabel calMethodTxt1;
    private javax.swing.JLabel calMethodTxt2;
    private javax.swing.JLabel calMethodTxt3;
    private javax.swing.JLabel calMethodTxt4;
    private javax.swing.JLabel calMethodTxt5;
    private javax.swing.JLabel calMethodTxt6;
    private javax.swing.JLabel charaLabel;
    private javax.swing.JComboBox<String> eventBonusCombo;
    private javax.swing.JLabel eventBounsLabel1;
    private javax.swing.JLabel eventBuffLabel;
    private javax.swing.JComboBox<String> eventBuffMemCombo1;
    private javax.swing.JComboBox<String> eventBuffMemCombo2;
    private javax.swing.JComboBox<String> eventBuffMemCombo3;
    private javax.swing.JComboBox<String> eventBuffMemCombo4;
    private javax.swing.JComboBox<String> eventBuffMemCombo5;
    private javax.swing.JLabel eventLabel;
    private javax.swing.JComboBox<String> eventTypeCombo;
    private javax.swing.JLabel eventTypeLabel;
    private javax.swing.JComboBox<String> filterAutoCombo1;
    private javax.swing.JComboBox<String> filterAutoCombo2;
    private javax.swing.JComboBox<String> filterAutoCombo3;
    private javax.swing.JComboBox<String> filterCombo1;
    private javax.swing.JComboBox<String> filterCombo10;
    private javax.swing.JComboBox<String> filterCombo11;
    private javax.swing.JComboBox<String> filterCombo12;
    private javax.swing.JComboBox<String> filterCombo13;
    private javax.swing.JComboBox<String> filterCombo14;
    private javax.swing.JComboBox<String> filterCombo15;
    private javax.swing.JComboBox<String> filterCombo2;
    private javax.swing.JComboBox<String> filterCombo3;
    private javax.swing.JComboBox<String> filterCombo4;
    private javax.swing.JComboBox<String> filterCombo5;
    private javax.swing.JComboBox<String> filterCombo6;
    private javax.swing.JComboBox<String> filterCombo7;
    private javax.swing.JComboBox<String> filterCombo8;
    private javax.swing.JComboBox<String> filterCombo9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel loadLabel1;
    private javax.swing.JComboBox<String> memSkillTimeCombo1;
    private javax.swing.JComboBox<String> memSkillTimeCombo2;
    private javax.swing.JComboBox<String> memSkillTimeCombo3;
    private javax.swing.JComboBox<String> memSkillTimeCombo4;
    private javax.swing.JComboBox<String> memSkillTimeCombo5;
    private javax.swing.JComboBox<String> memSkillTimeCombo6;
    private javax.swing.JLabel memberBand1;
    private javax.swing.JLabel memberBand2;
    private javax.swing.JLabel memberBand3;
    private javax.swing.JLabel memberBand4;
    private javax.swing.JLabel memberBand5;
    private javax.swing.JComboBox<String> memberCombo1;
    private javax.swing.JComboBox<String> memberCombo2;
    private javax.swing.JComboBox<String> memberCombo3;
    private javax.swing.JComboBox<String> memberCombo4;
    private javax.swing.JComboBox<String> memberCombo5;
    private javax.swing.JLabel memberName1;
    private javax.swing.JLabel memberName2;
    private javax.swing.JLabel memberName3;
    private javax.swing.JLabel memberName4;
    private javax.swing.JLabel memberName5;
    private javax.swing.JLabel memberPow1;
    private javax.swing.JLabel memberPow2;
    private javax.swing.JLabel memberPow3;
    private javax.swing.JLabel memberPow4;
    private javax.swing.JLabel memberPow5;
    private javax.swing.JLabel memberRare1;
    private javax.swing.JLabel memberRare2;
    private javax.swing.JLabel memberRare3;
    private javax.swing.JLabel memberRare4;
    private javax.swing.JLabel memberRare5;
    private javax.swing.JLabel memberSkill1;
    private javax.swing.JLabel memberSkill2;
    private javax.swing.JLabel memberSkill3;
    private javax.swing.JLabel memberSkill4;
    private javax.swing.JLabel memberSkill5;
    private javax.swing.JLabel memberSkillPerc1;
    private javax.swing.JLabel memberSkillPerc2;
    private javax.swing.JLabel memberSkillPerc3;
    private javax.swing.JLabel memberSkillPerc4;
    private javax.swing.JLabel memberSkillPerc5;
    private javax.swing.JLabel memberSkillPerc6;
    private javax.swing.JLabel memberSkillPercLabel1;
    private javax.swing.JLabel memberSkillPercLabel2;
    private javax.swing.JLabel memberSkillPercLabel3;
    private javax.swing.JLabel memberSkillPercLabel4;
    private javax.swing.JLabel memberSkillPercLabel5;
    private javax.swing.JLabel memberSkillPercLabel6;
    private javax.swing.JSlider memberSkillSlider1;
    private javax.swing.JSlider memberSkillSlider2;
    private javax.swing.JSlider memberSkillSlider3;
    private javax.swing.JSlider memberSkillSlider4;
    private javax.swing.JSlider memberSkillSlider5;
    private javax.swing.JSlider memberSkillSlider6;
    private javax.swing.JLabel memberSkillTime1;
    private javax.swing.JLabel memberSkillTime2;
    private javax.swing.JLabel memberSkillTime3;
    private javax.swing.JLabel memberSkillTime4;
    private javax.swing.JLabel memberSkillTime5;
    private javax.swing.JLabel memberSkillTime6;
    private javax.swing.JLabel memberTech1;
    private javax.swing.JLabel memberTech2;
    private javax.swing.JLabel memberTech3;
    private javax.swing.JLabel memberTech4;
    private javax.swing.JLabel memberTech5;
    private javax.swing.JLabel memberTotal1;
    private javax.swing.JLabel memberTotal2;
    private javax.swing.JLabel memberTotal3;
    private javax.swing.JLabel memberTotal4;
    private javax.swing.JLabel memberTotal5;
    private javax.swing.JLabel memberType1;
    private javax.swing.JLabel memberType2;
    private javax.swing.JLabel memberType3;
    private javax.swing.JLabel memberType4;
    private javax.swing.JLabel memberType5;
    private javax.swing.JLabel memberVis1;
    private javax.swing.JLabel memberVis2;
    private javax.swing.JLabel memberVis3;
    private javax.swing.JLabel memberVis4;
    private javax.swing.JLabel memberVis5;
    private javax.swing.JLabel powLabel;
    private javax.swing.JLabel rareLabel;
    private javax.swing.JButton resetItemButton;
    private javax.swing.JButton resetMemberButton;
    private javax.swing.JLabel resultLabel3;
    private javax.swing.JLabel resultLabel4;
    private javax.swing.JLabel resultLabel5;
    private javax.swing.JLabel resultLabel6;
    private javax.swing.JLabel resultLabel7;
    private javax.swing.JCheckBox resultOnlyCheckbox;
    private javax.swing.JTextField resultTextField;
    private javax.swing.JTextField resultTextField2;
    private javax.swing.JButton screenCapture;
    private javax.swing.JLabel skillLabel1;
    private javax.swing.JLabel skillPercLabel1;
    private javax.swing.JComboBox<String> songCalMethod;
    private javax.swing.JComboBox<String> songCalMethod2;
    private javax.swing.JComboBox<String> songFilter1;
    private javax.swing.JComboBox<String> songFilter2;
    private javax.swing.JComboBox<String> songFilter3;
    private javax.swing.JLabel songLabel;
    private javax.swing.JComboBox<String> songNameCombo;
    private javax.swing.JLabel songNameLabel;
    private javax.swing.JLabel teamIcon1;
    private javax.swing.JLabel teamIcon2;
    private javax.swing.JLabel teamIcon3;
    private javax.swing.JLabel teamIcon4;
    private javax.swing.JLabel teamIcon5;
    private javax.swing.JLabel teamMemberLabel;
    private javax.swing.JLabel teamMemberLabel1;
    private javax.swing.JLabel techLabel;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel visLabel;
    // End of variables declaration//GEN-END:variables
}
