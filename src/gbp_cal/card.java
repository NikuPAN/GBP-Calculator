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
public class card {
    private static int ID = 0;
    private int id;
    private String cardName;
    private String band;
    private String charaName;
    private int rarity;
    private String type;
    private int pow;
    private int tech;
    private int vis;
    private int total;
    private String skillType;
    private int scorePerc;
    private float skillLastS;
    private float skillLastL;
    //private ImageIcon img;
    private String imgPath;
    public card(String cardName, String band, String charaName, int rarity, String type, int pow, int tech, int vis, String imgPath, String skillType) {
        this.id = ID;
        this.cardName = cardName;
        this.band = band;
        this.charaName = charaName;
        this.rarity = rarity;
        this.type = type;
        this.pow = pow;
        this.tech = tech;
        this.vis = vis;
        this.total = (this.pow+this.tech+this.vis);
        this.imgPath = ""+this.getClass().getResource("/cardIcons/card0.png");
        this.skillType = skillType;
        this.scorePerc = 0;
        this.skillLastS = 0.0f;
        this.skillLastL = 0.0f;
        this.ID++;
    }
    public card(String cardName, String band, String charaName, int rarity, String type, int pow, int tech, int vis, String skillType, int scorePerc, float skillLastL, String imgPath) {
        this.id = ID;
        this.cardName = cardName;
        this.band = band;
        this.charaName = charaName;
        this.rarity = rarity;
        this.type = type;
        this.pow = pow;
        this.tech = tech;
        this.vis = vis;
        this.total = (this.pow+this.tech+this.vis);
        this.skillType = skillType;
        this.scorePerc = scorePerc;
        this.skillLastS = 5.0f;
        this.skillLastL = skillLastL;
        this.imgPath = ("https://i.imgur.com/"+imgPath);
        //this.img = null;
        /*try {
            URL url = new URL("https://i.imgur.com/"+imgPath);
            Image imag = ImageIO.read(url);
            this.img = new ImageIcon(imag);
        } catch (IOException ex) {
            Logger.getLogger(card.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        this.ID++;
    }
    /*public ImageIcon getIMG() {
        return this.img;
    }*/
    public int getCardID() {
        return this.id;
    }
    public String getCardName() {
        return this.cardName;
    }
    public String getBand() {
        if(this.band.equalsIgnoreCase("Poppin'Party"))
            return "Poppin' Party";
        return this.band;
    }
    public String getCharaName() {
        return this.charaName;
    }
    public int getRarity() {
        return this.rarity;
    }
    public String getType() {
        return this.type;
    }
    public int getPow() {
        return this.pow;
    }
    public int getTech() {
        return this.tech;
    }
    public int getVis() {
        return this.vis;
    }
    public int getTotal() {
        return this.total;
    }
    public String getSkillType() {
        return this.skillType;
    }
    public int getScorePerc() {
        return this.scorePerc;
    }
    public float getSkillLastShort() {
        return this.skillLastS;
    }
    public float getSkillLastLong() {
        return this.skillLastL;
    }
    public int[] getSpec() {
        int[] spec = {this.pow, this.tech, this.vis};
        return spec;
    }
    public String getImgPath() {
        return this.imgPath;
    }
}
