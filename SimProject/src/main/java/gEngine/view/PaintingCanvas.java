package main.java.gEngine.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JPanel;

import main.java.gEngine.adapters.Adpt_View2Model;

public class PaintingCanvas extends JPanel/** implements Runnable*/{
	
	private Adpt_View2Model view2modelAdapter;
	
	private int C_HEIGHT, C_WIDTH;
	private int c_xOffset, c_yOffset;
	private int c_recurxOffset, c_recuryOffset;
	private long lastFrame;
	private int scale;
	
	//private BufferedImage image = new BufferedImage(C_WIDTH, C_HEIGHT, BufferedImage.TYPE_INT_RGB);
	//private int[] image_pixelMap = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	
	private Thread animator;
	
	public PaintingCanvas()
	{
		C_HEIGHT = 300;
		C_WIDTH = 300;
		 c_xOffset = 0;
		 c_yOffset = 0;
		 c_recuryOffset=0;
		 c_recuryOffset=0;
		 scale = 1;
		 lastFrame = System.currentTimeMillis();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(C_WIDTH, C_HEIGHT));
		setDoubleBuffered(true);
	}
	
	public void setView2ModelAdapter(Adpt_View2Model adpt)
	{
		view2modelAdapter = adpt;
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        long nowTime = System.currentTimeMillis();
        addOffset(c_recurxOffset*((nowTime-lastFrame)/scale),c_recuryOffset*((nowTime-lastFrame)/scale));
        
        //perform transformations of the Canvas
        ((Graphics2D)(g)).scale(scale,scale);
        g.translate(c_xOffset, c_yOffset);
        //g.translate(c_xOffset*scale, c_yOffset*scale);
        lastFrame=nowTime;
        view2modelAdapter.paint(g);
       // g.translate(c_xOffset, c_yOffset);
	}

	public void addOffset(long x, long y)
	{
		c_xOffset+=(int)x;
		c_yOffset+=(int)y;
	}
	public void setOffsets(int x, int y)
	{
		c_xOffset=x;
		c_yOffset=y;
	}
	public void setRecurringOffset(int x, int y)
	{
		c_recurxOffset=(int)x;
		c_recuryOffset=(int)y;
	}
	public void setRecurringXOffset(int x)
	{
		c_recurxOffset=(int)x;
	}
	public void setRecurringYOffset(int y)
	{
		c_recuryOffset=(int)y;
	}
	public void setScale(int x)
	{
		scale = x;
	}
	public void addScale(int x)
	{
		/**if(x>0)
		{
			scale*=2;
		}
		else
		{
			scale/=2;
		}*/
		int oldScale =scale;
		scale+=x;
		if(scale<1)
		{
			scale=1;
		}
		//change offset to 
		//	(screenwidth/2)==centerX
		//offset changed by centerX*(newScale/oldScale)
		//addOffset(((1920/2)*(scale-x))/(scale)*x,((1000/2)*(scale-x))/(scale))*x);
		//System.out.println(c_xOffset);
		//addOffset(-(((((1920/2)-c_xOffset))*(oldScale))/(scale)*x),(-(((((1000/2)-c_yOffset))*(oldScale))/(scale))*x));
		//addOffset(-500,-250);
	}
	// implements Runnable.run()
	/**public void run()
	{
		while(true)
		{
			repaint();
		}
	}*/
}
