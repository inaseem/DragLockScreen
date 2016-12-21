
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tony Stark
 */
public class AndroidDragLockScreen extends JPanel implements Runnable{

    Graphics2D g;
    Thread th;
    int startx,starty;
    boolean drawing=false,animate=false,unlocked=false;
    Rectangle r,r1;
    int width=40,dotwidth=60;
    int arcwidth=width/2-5,fixed=arcwidth;
    Point p,pt[]=new Point[2];
    double d1,d2;
    float fl=1.0f,df= -0.01f;
    ActionListener al;
    Timer timer;
  public AndroidDragLockScreen(){
      th = new Thread(this);
      th.start();
      setOpaque(true);
      JLabel lb= new JLabel();
      lb.setIcon(new ImageIcon("rahul.jpg"));
      this.setLayout(new BorderLayout());
      this.add(lb);

      //Listener Starts
      al = new ActionListener(){
          public void actionPerformed(ActionEvent ae){
              fl+=df;
              if(fl<=0.01f)
              {
                  df*=-1;
              }
              if(fl>=1.00f)
              {
                  df*=-1;
              }
          }
      };
      timer = new Timer(5,al);
      MouseListener ml = new MouseAdapter(){
          @Override
          public void mousePressed(MouseEvent me){
              
              Point p1= me.getPoint();
              r1= new Rectangle(p1.x-dotwidth/2,p1.y-dotwidth/2,dotwidth,dotwidth);
              r= new Rectangle(p1.x-width/2,p1.y-width/2,width,width);
              p= new Point((int)r.getCenterX(),(int)r.getCenterY());
              pt[0]=p1;
              drawing=true;
              animate=true;
              timer.start();
          }
          @Override
          public void mouseReleased(MouseEvent me){
              drawing=false;
              reset();
              animate=false;
              arcwidth=fixed;
              unlocked=false;
              timer.stop();
              
          }
      };
      this.addMouseListener(ml);
      
      MouseMotionListener mll= new MouseAdapter(){
          @Override
          public void mouseDragged(MouseEvent me){
              Point p1= me.getPoint();
              pt[1]=p1;
              d1=getDistance(pt[0],p);
              d2=getDistance(pt[1],p);
              pt[0]=pt[1];
              if(unlocked==false)
              if(d2>d1)
              {
                  arcwidth--;
              }
              if(d2<d1)
              {
                  arcwidth++;
                  if(arcwidth>=fixed)
                  {
                      arcwidth=fixed;
                  }
              }
          }
      };
      this.addMouseMotionListener(mll);
  }
  
  public void reset(){
      fl=1.0f;
      df= -0.01f;
  }
  
    @Override
  public void paint(Graphics g2){
      g= (Graphics2D)g2;
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
      super.paint(g);
      g.setColor(Color.BLACK);
      if(unlocked!=true){
      if(drawing==true)
      {
          g.setStroke(new BasicStroke(2,BasicStroke.JOIN_ROUND,BasicStroke.CAP_ROUND));
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
          g.fillRect(0, 0, getWidth(), getHeight());
          g.setColor(Color.WHITE);
          g.setComposite(AlphaComposite.SrcOver);
        /////////////////////////////////////////////////////
        g.drawOval(r1.x, r1.y, r1.width, r1.height);
        if(animate==true)
        {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fl));
        }
        g.drawArc(p.x+r.width*2, p.y-r.height,  r.width/2, r.height*2, 90, -180);
        g.drawArc(p.x-r.width*2-r.width/2, p.y-r.height,  r.width/2, r.height*2, 90, 180);
        g.drawArc(p.x-r.width, p.y-r.height*2-r.height/2,  r.width*2, r.height/2, 0, 180);
        g.drawArc(p.x-r.width,p.y+r.height*2,  r.width*2, r.height/2, 0, -180);
        //////////////////////////////////////////////////////
        g.drawOval(p.x+r.width*2+r.width/2 -5, p.y-5, 10, 10);
        g.drawOval(p.x-r.width*2-r.width/2 -5, p.y-5, 10, 10);
        g.drawOval(p.x-5, p.y-r.height*2-r.height/2-5, 10, 10);
        g.drawOval(p.x-5, p.y+r.height*2+r.height/2-5, 10, 10);
        //////////////////////////////////////////////////////
        g.setComposite(AlphaComposite.SrcOver);
        g.fillRoundRect(p.x-width/4,p.y, width/2, width/3,3,3);
        g.setStroke(new BasicStroke(1.5f));
        if(arcwidth>=0)
        {
        g.drawArc(p.x-width/4+2, p.y-width/4, arcwidth, width/2, 0, 180);  
        }  
        if(arcwidth<0)
        {
        g.drawArc(p.x-width/4+2+arcwidth, p.y-width/4,-arcwidth, width/2, 0, 180);
        if(arcwidth<= -fixed)
        {
            unlocked=true;
            arcwidth=fixed;
            reset();
            timer.stop();
        }
        }
      }
      }
      if(unlocked==true)
      {
          g.setFont(new Font("arial",Font.BOLD,30));
          FontMetrics fm=g.getFontMetrics();
          String str="Unlocked";
          int w=fm.stringWidth(str);
          g.setColor(Color.WHITE);
          g.drawString(str, getWidth()/2-w/2, getHeight()/2);
      }
  }
  
  public double getDistance(Point p1,Point p2){
      return Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2));
  }
  
    @Override
  public void run(){
      try{
          while(true)
          {
              Thread.sleep(15);
              repaint();
          }
      }catch(InterruptedException e)
      {
          System.out.println(e);
      }
  }
  
  public static void main(String args[]){
      JFrame jfm= new JFrame();
      jfm.getContentPane().setBackground(Color.WHITE);
      jfm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jfm.setSize(600, 400);
      jfm.setLayout(new BorderLayout());
      jfm.add(new AndroidDragLockScreen());
      jfm.setVisible(true);
  }
}
