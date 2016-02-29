package visualkey;

import java.awt.Graphics;

/**
 * This class provide the estructure to make the game with layers, and make it easy the disposition of the
 * elements on the canvas
 * @author Juan Vicente Pradilla
 */
public abstract class Layer {

	protected int vHeight;
	protected int vWidth;
	protected  int vPositionX;
	protected int vPositionY;
	private boolean vVisible;
	private boolean vAutoHide;

	/**
	 * Gets the heigth of the object layer
	 * @return the corresponding heigh value of the layer
	 */    
	public int getHeight() {
		return this.vHeight;
	}

	/**
	 * Gets the width of the object layer
	 * @return the corresponding width value of the layer
	 */
	public int getWidth() {
		return this.vWidth;
	}

	/**
	 * Gets the X position of the object layer
	 * @return the corresponging x position value of the layer
	 */
	public int getPositionX() {
		return this.vPositionX;
	}

	/**
	 * Changed the x position of the object layer
	 * @param pPositionX the corresponging x value of the layer
	 */
	public void setPositionX(int pPositionX) {
		this.vPositionX = pPositionX;
	}

	/**
	 * Gets the y position of the object layer
	 * @return the corresponding y position value of the layer
	 */    
	public int getPositionY() {
		return this.vPositionY;
	}

	/**
	 * Changed the y position of the object layer 
	 * @param pPositionY the corresponging y value of the layer
	 */    
	public void setPositionY(int pPositionY) {
		this.vPositionY = pPositionY;
	}

	/**
	 * Determinates if the object layer is visible or not
	 * @return true if the object layer is visible, false in other case
	 */ 
	public boolean isVisible() {
		return this.vVisible;
	}

	/**
	 * Change de visibility of the object layer 
	 * @param pVisible true to make it visible, false in other case
	 */
	public void setVisible(boolean pVisible) {
		this.vVisible = pVisible;
	}

	/**
	 * Determinates if the object layer is autohide or not
	 * @return true if the object layer is autohide, false in other case
	 */
	public boolean isAutoHide() {
		return this.vAutoHide;
	}

	/**
	 * Change de autohide of the object layer
	 * @param pVisible true to make it autohide, false in other case
	 */
	public void setAutoHide(boolean vAutoHide) {
		this.vAutoHide = vAutoHide;
	}

	/**
	 * Move the object layer on the x-axis acording pMoveX and the y-axis acording pMoveY
	 * @param pMoveX value to move the object layer on the x-axis
	 * @param pMoveY value to move the object layer on the  y-axis
	 */
	public void move(int pMoveX, int pMoveY) {
		this.setPositionX(this.vPositionX + pMoveX);
		this.setPositionY(this.vPositionY + pMoveY);
	}

	/**
	 * Draw the layer in the canvas
	 * @param pGraphics objecto to proceed to paint 
	 */
	public abstract void draw(Graphics pGraphics);

}