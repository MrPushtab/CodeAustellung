package main.java.app;


import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import main.java.app.listeners.FrameKeyListener;
import main.java.app.listeners.FrameMouseWheelListener;
import main.java.gEngine.adapters.Adpt_Model2View;
import main.java.gEngine.adapters.Adpt_View2Model;
import main.java.gEngine.model.ModelController;
import main.java.gEngine.model.SpriteObj;
import main.java.gEngine.view.PaintingCanvas;

public class GraphicsController {

	private ModelController modelController;
	private Adpt_Model2View model2viewAdapter;
	private Adpt_View2Model	view2modelAdapter;
	private PaintingCanvas canvas;
	public GraphicsController()
	{
		canvas = new PaintingCanvas();
		modelController = new ModelController(100);
		
		JFrame frame = new JFrame() {
			public void paintComponent(Graphics g) {
	           super.paintComponents(g);  // clear the panel and redo the background
	           view2modelAdapter.paint(g);  // call back to the model to paint the sprites
	        }
		}
;
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
		
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		menubar.add(file);
		frame.add(canvas);
		//frame.setJMenuBar(menubar);
		frame.setResizable(false);
		
		
		
		frame.addKeyListener(new FrameKeyListener(canvas));
		frame.addMouseWheelListener(new FrameMouseWheelListener(canvas));
		
		
		
		AbstractAction Quit = new AbstractAction("Quit") {
			public void actionPerformed(ActionEvent e) 
			{
				//saveOld();
				System.exit(0);
			}

		};
		file.add(Quit);
		frame.setJMenuBar(menubar);
	    frame.pack();  
	    frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //frame.setFocusable(true);
	    frame.requestFocus();
	    frame.setFocusable(true);
	    
	    model2viewAdapter = new M2V_A(frame);
	    view2modelAdapter = new V2M_A(modelController);
	    canvas.setView2ModelAdapter(view2modelAdapter);
	    modelController.getTimer().createTimer((e) -> model2viewAdapter.update());
	    frame.setVisible(true);
	    frame.requestFocus();
	    //frame.setFocusable(true);
	}
	public ModelController getModelController()
	{
		return modelController;
	}
	public void addSprite(SpriteObj spr)
	{
		//spr.addPanel(canvas);
		modelController.addSprite(spr);
	}
	
	
}
