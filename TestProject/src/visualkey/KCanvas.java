/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualkey;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 *
 * @author camilo
 */
public abstract class KCanvas extends KFrame {

	protected KPanel panelToDraw;
	protected ScrollPane scroll;

	public KCanvas(Dimension canvasSize) {
		super(Toolkit.getDefaultToolkit().getScreenSize());
		init(Toolkit.getDefaultToolkit().getScreenSize(), canvasSize);
		pack();
	}

	public KCanvas(Dimension screenSize, Dimension canvasSize) {
		super(screenSize);
		init(screenSize, canvasSize);
		pack();
	}

	private void init(Dimension screenSize, Dimension canvasSize) {

		scroll = new ScrollPane();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scroll.setPreferredSize(new Dimension((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8)));
		panelToDraw = new KPanel(canvasSize) {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintCanvas(g);
			}
		};
		scroll.add(panelToDraw);
		this.addComponent(scroll, 0, 0, 1, 4, 1, 1, false);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				scroll.setPreferredSize(new Dimension(getWindowWidth() - 25, getWindowHeight() - 35));
			}
		});
	}

	public void modifyCanvasSize(Dimension d) {
		panelToDraw.setPreferredSize(d);
		scroll.removeAll();
		scroll.add(panelToDraw);
	}

	public void repaint() {
		panelToDraw.repaint();
		super.repaint();
	}

	public int getWidth() {
		return panelToDraw.getWidth();
	}

	public int getHeight() {
		return panelToDraw.getHeight();
	}

	public int getWindowWidth() {
		return super.getWidth();
	}

	public int getWindowHeight() {
		return super.getHeight();
	}

	protected abstract void paintCanvas(Graphics g);

}
