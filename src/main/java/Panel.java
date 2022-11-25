import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
public class Panel extends JPanel implements ActionListener {
    private int w,h;
    private JSlider slider;
    private JSlider freq;
    private Timer timer;
    private boolean animating;
    private int oldN ;
    private int oldF;
    private float t = 0f;
    private ArrayList<Pointf> actualFunc;
    private ArrayList<Pointf> taylorAppx;
    private ArrayList<ArrayList<Pointf>> animation = new ArrayList<>();
    private int frame;
    public Panel(){
        /*======================================*/
        w = 800;//width of the window
        h = 800;//height of the window
        setPreferredSize(new Dimension(w,h));//applying panel dimensions to the frame
        setBackground(Color.black);//setting background color
        /*====================================*/
        freq = new JSlider();//frequency slider
        slider = new JSlider();// number of Terms slider
        freq.setValue(2);//initial value
        freq.setMinimum(1);//minimum value
        freq.setMaximum(10);//maximum value 10 > could be higher
        slider.setMinimum(2);//initial value
        slider.setValue(1);//minimum value
        slider.setMaximum(10);// maximum because any larger number than it will lead in high number Factorial that surpasses double limit
        add(freq);//adding freq slider to the JPanel
        add(slider);//adding the slider to the JPanel
        taylorAppx = calcTaylor(slider.getValue(),freq.getValue());//calculate taylor points
        actualFunc = calcSin(slider.getValue(),freq.getValue());
         animating = false;//to keep track of animation periods
         oldN = slider.getValue()-1;
         oldF = freq.getValue()-1;
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                taylorAppx.clear();
                actualFunc.clear();
                ArrayList<Pointf> taylorOld = calcTaylor(oldN,freq.getValue());
                actualFunc = calcSin(slider.getValue(),freq.getValue());
                taylorAppx = calcTaylor(slider.getValue(),freq.getValue());
                animation = calcAni(taylorOld,taylorAppx);
                animating = true;
                repaint();
            }
        });
        freq.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                taylorAppx.clear();
                actualFunc.clear();
                ArrayList<Pointf> taylorOld = calcTaylor(oldN,oldF);
                actualFunc = calcSin(slider.getValue(),freq.getValue());
                taylorAppx = calcTaylor(slider.getValue(),freq.getValue());
                animation = calcAni(taylorOld,taylorAppx);
                animating = true;
                repaint();
            }
        });
        timer = new Timer(10,this); // Runs <ActionPreformed> method every x MilliSeconds
        timer.start();//start <line above>
    }
    public class Pointf{
        float x1,y1,x2,y2;
        public Pointf(float x,float y){
            this.x1 = x; this.y1 = y;
        }
        public Pointf(float x1,float y1,float x2,float y2){
            this.x1 = x1; this.y1 = y1;
            this.x2 = x2; this.y2 = y2;
        }
        public String toString(){
            return "("+x1+","+y1+")"+",("+x2+","+y2+")";
        }
    }
    private class XYs{
        int[] x1,y1,x2,y2;
        public XYs(int[]x1,int[]y1,int[]x2,int[]y2){
            this.x1 = x1;this.y1 = y1;
            this.x2 = x2;this.y2 = y2;
        }
        public XYs(int[]x1,int[]y1){
            this.x1 = x1;this.y1 = y1;
        }
    }
    @Override //this method will be invoked evey time the frame is modified or repaint is invoked
    public void paint(Graphics g){
        super.paint(g); // paints background
        g.setFont(new Font("",0,12));//scaling up the size of the font
        g.setColor(Color.red);
        g.drawString("RED (Actual Sine wave)",0,30);
        g.setColor(Color.orange);
        g.drawString("ORANGE (TaylorApproximation)",0,45);
        g.setColor(Color.white);
        /*Typing Sliders Info*/
        g.setFont(new Font("",0,24));//scaling up the size of the font
        g.drawString("n : "+(slider.getValue()*2-1)+" ",slider.getX(),40);
        g.drawString("freq : "+freq.getValue()+" ",freq.getX(),40);
        /*===================*/
        /*drawing axis*/
        g.drawLine(w/2,h,w/2,0);//y axis
        g.drawLine(w,h/2,0,h/2);//x axis
        g.translate(w/2,h/2);// middle screen is (0,0) instead of top right corner
        /*===================*/
        if(frame == animation.size()){
            animating = false;
            animation.clear();
            frame = 0;
        }
        draw(g);//draw taylor points
        oldN = slider.getValue();
        oldF = freq.getValue();
    }
    private ArrayList<Pointf> calcSin(int n,int freq) {
        ArrayList<Pointf> sinwave = new ArrayList<>();
        int amp = 100;//amplitude of the sine wave
        double y;
        int a = 0;
        for(int x = 0 ; x < w ; x++){
               //=================================================//
            float yac = (float)sin(a);//actual sin<using Math.sin>
            sinwave.add(new Pointf(x,yac*amp,-x,-yac*amp));
               //=================================================//
            a+=freq;
        }
        return sinwave;
    }
    private ArrayList<Pointf> calcTaylor(int n,int freq) {
        ArrayList<Pointf> taylorAppx = new ArrayList<>();
        int amp = 100;//amplitude of the sine wave
        double y;
        int a = 0;
           for(int x = 0 ; x < w ; x++){
               y = (Taylor.sin(a*Math.PI/180,n));
               taylorAppx.add(new Pointf(x,(float)y*amp,-x,(float)-y*amp));
                a+=freq;
               }
        return taylorAppx;
    }
    private void draw(Graphics g){
        /*Turning Arraylists to regular Arrays in order to use g::drawPolyline method*/
        {
        XYs xy = getXYfromArr(actualFunc);
        int[] xs1 = xy.x1;
        int[] ys1 = xy.y1;
        int[] xs2 = xy.x2;
        int[] ys2 = xy.y2;
        g.setColor(Color.red);
        g.drawPolyline(xs1,ys1,actualFunc.size()-1);
        g.drawPolyline(xs2,ys2,actualFunc.size()-1);
        }//to isolate XYs obj
        if(animating){
            drawAnimation(g,frame);
        }else{
            XYs xy = getXYfromArr(taylorAppx);
            int[] xst1 = xy.x1;
            int[] yst1 = xy.y1;
            int[] xst2 = xy.x2;
            int[] yst2 = xy.y2;
        g.setColor(Color.orange);
        g.drawPolyline(xst1,yst1,taylorAppx.size()-1);
        g.drawPolyline(xst2,yst2,taylorAppx.size()-1);
        }
        /*==============================================================================*/
    }
    private void drawAnimation(Graphics g,int frame) {
        XYs xy = getXYfromArr(animation.get(frame));
        int[] xst1 = xy.x1;
        int[] yst1 = xy.y1;
        int[] xst2 = xy.x2;
        int[] yst2 = xy.y2;
        g.setColor(Color.orange);
        g.drawPolyline(xst1,yst1,animation.get(frame).size()-1);
        g.drawPolyline(xst2,yst2,animation.get(frame).size()-1);
        this.frame++;
    }
    private ArrayList<ArrayList<Pointf>> calcAni(ArrayList<Pointf> a , ArrayList<Pointf> b){
        ArrayList<ArrayList<Pointf>> animation = new ArrayList<>();
        t = 0;
        float inc = 0.05f;
        while(t <= 1){
            ArrayList<Pointf> tmp = new ArrayList<>();
            for(int x = 0 ; x < w ; x++){
            Pointf pa = a.get(x);
            Pointf pb = b.get(x);
            Pointf c = lerp(pa,pb,t);
            tmp.add(c);
            }
            animation.add(tmp);
            t+= inc;
        }
        return animation;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    public XYs getXYfromArr(ArrayList<Pointf> arr){
        int[] x1 = new int[arr.size()]
                ,y1 = new int[arr.size()]
                ,x2 = new int[arr.size()]
                ,y2 = new int[arr.size()];
        XYs xy;
        for(int i = 0 ; i < arr.size() ;i++){
            Pointf tmp = arr.get(i);
            x1[i] = Math.round(tmp.x1);y1[i] = Math.round(tmp.y1);
            x2[i] = Math.round(tmp.x2);y2[i] = Math.round(tmp.y2);
        }
        return new XYs(x1,y1,x2,y2);
    }
    public Pointf lerp(Pointf current, Pointf target, float increment) {
        float x,y,x2,y2;
        x = lerp(current.x1,target.x1,increment);
        y = lerp(current.y1,target.y1,increment);
        x2 = lerp(current.x2,target.x2,increment);
        y2 = lerp(current.y2,target.y2,increment);
        return new Pointf(x,y,x2,y2);
    }
    public float lerp(float current, float target, float increment) {
        return current + (target - current) * increment;
    }
    public double sin(double a){ return Math.sin(a*Math.PI/180);}
}
