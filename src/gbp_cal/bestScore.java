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
public class bestScore {
    private int maxScore;
    private final int numOfKeys = 6;
    private float skillLastL[] = new float[numOfKeys];
    private int scoreUpPerc[] = new int[numOfKeys];
    public bestScore() {
        this.maxScore = 0;
    }
    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
    public int getMaxScore() {
        return this.maxScore;
    }
    public void setSkillLastL(float[] lastL) {
        if(lastL.length != 6)
            return;
        this.skillLastL = lastL;
    }
    public void setSkillLastL(int index, float LastL) {
        if(index < 0 || index >= this.numOfKeys)
            return;
        this.skillLastL[index] = LastL;
    }
    public float getSkillLastL(int index) {
        if(index < 0 || index >= this.numOfKeys)
            return 0.0f;
        return this.skillLastL[index];
    }
    public void setScoreUpPerc(int[] Perc) {
        if(Perc.length != 6)
            return;
        this.scoreUpPerc = Perc;
    }
    public void setScoreUpPerc(int index, int Perc) {
        if(index < 0 || index >= this.numOfKeys)
            return;
        this.scoreUpPerc[index] = Perc;
    }
    public int getScoreUpPerc(int index) {
        if(index < 0 || index >= this.numOfKeys)
            return 0;
        return this.scoreUpPerc[index];
    }
}
