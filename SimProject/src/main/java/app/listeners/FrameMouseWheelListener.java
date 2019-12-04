package main.java.app.listeners;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import main.java.gEngine.view.PaintingCanvas;

public class FrameMouseWheelListener implements MouseWheelListener{

	private PaintingCanvas canvas;
	public FrameMouseWheelListener(PaintingCanvas canvas)
	{
		this.canvas = canvas;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		canvas.addScale(-e.getWheelRotation());
	}

}
