package main.java.app;

import java.awt.Canvas;

import javax.swing.JFrame;

import main.java.gEngine.adapters.Adpt_Model2View;

public class M2V_A implements Adpt_Model2View{
	JFrame frame;
	public M2V_A(JFrame frame)
	{
		this.frame = frame;
	}

	public void update() {
		frame.repaint();
	}

}
