package main.java.app.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.java.gEngine.view.PaintingCanvas;

public class FrameKeyListener implements KeyListener{
	private PaintingCanvas canvas;
	public FrameKeyListener(PaintingCanvas canvas)
	{
		this.canvas=canvas;
	}
	@Override
	public void keyTyped(KeyEvent e) 
	{
		
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
    	if(e.getKeyCode()==KeyEvent.VK_A){
    		canvas.setRecurringXOffset(1);
        }
    	if(e.getKeyCode()==KeyEvent.VK_D){
    		canvas.setRecurringXOffset(-1);
        }
    	if(e.getKeyCode()==KeyEvent.VK_W){
    		canvas.setRecurringYOffset(1);
        }
    	if(e.getKeyCode()==KeyEvent.VK_S){
    		canvas.setRecurringYOffset(-1);
        }
    } 

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode()==KeyEvent.VK_A){
            canvas.setRecurringXOffset(0);
        }
    	if(e.getKeyCode()==KeyEvent.VK_D){
    		canvas.setRecurringXOffset(0);
        }
    	if(e.getKeyCode()==KeyEvent.VK_W){
    		canvas.setRecurringYOffset(0);
        }
    	if(e.getKeyCode()==KeyEvent.VK_S){
    		canvas.setRecurringYOffset(0);
        }
	}
	
}
