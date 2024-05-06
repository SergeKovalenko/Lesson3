package Lesson3;

import lombok.ToString;

@ToString
public class Fraction implements Fractionable{
    private int num;
    private int denum;
    private String stateKey;

    public Fraction(int num, int denum) {
        this.num = num;
        this.denum = denum;
        setStateKey();
    }
    @Override
    @Cache(timeValue=1000)
    public double doubleValue() {
        return (double) num/denum;
    }
    @Override
    public void setNum(int num) {
        this.num = num;
    }

   @Override
    public void setDenum(int denum) {
        this.denum = denum;
    }
    private void setStateKey(){
        stateKey = String.valueOf(this.num) + '#' + this.denum;
    }
    public String getStateKey() {
        return stateKey;
    }
}
