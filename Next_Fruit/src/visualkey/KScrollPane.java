package visualkey;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class KScrollPane extends KPanel implements MouseWheelListener, MouseListener {

	private int selected; // Muestra el indice del objeto seleccionado, -1 si no ha sido seleccionado ninguno o se ha seleccionado un espacio en blanco
	private boolean kind; // Representa si es de tipo imagen o texto
	private Image[] icons; // Contiene las imagenes que se van a mostrar
	private String[] list; // Contiene la lista que se va a mostrar
	private int position; // Representa en que posicion del scroll se encuentra el panel
	private int size; // Representa el tamanio nxn de la imagen o el tamanio la letra si es una lista de texto
	private int margin;

	public KScrollPane(Image[] icons, int iconSize, Dimension dimension, int margin){
		super(dimension);
		this.position = 0;
		this.size = iconSize;
		this.margin = margin;
		this.selected = -1;
		this.icons = icons;
		this.kind = true;
	}

	public KScrollPane(String[] list, int fontSize, Dimension dimension){
		super(dimension);
		this.position = 0;
		this.size = fontSize;
		this.selected = -1;
		this.list = list;
		this.kind = false;
	}

	public int getSelected() {
		return selected;
	}

	public String getKind() {
		if(kind)
			return "Image";
		else
			return "String";
	}

	public Image[] getIcons() {
		return icons;
	}

	public void setIcons(Image[] icons) {
		this.kind = true;
		this.icons = icons;
	}

	public String[] getList() {
		return list;
	}

	public void setList(String[] list) {
		this.kind = false;
		this.list = list;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		position -= e.getUnitsToScroll();
		repaint();
		System.out.println("Scroll");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		calculateSelected(e.getX(),e.getY());
		System.out.println("Clic");
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		// Dibujando las imagenes
		int posicion[] = new int[2];
		posicion[0] = margin;
		posicion[1] = margin;
		for(int h=0;h<icons.length;h++){
			g.drawImage(icons[h], posicion[0], posicion[1], size, size, this);
			posicion[0]+=size+margin;
			if(posicion[0]>(this.getWidth()-margin)){
				posicion[1]+=size+margin;
				posicion[0]=margin;
			}
		}
	}

	private void calculateSelected(int x, int y){
		if(kind){
			// Calculando la posicion real
			int[] posicionReal = new int[2];
			posicionReal[0] = x-margin;
			posicionReal[1] = y-margin;

			if(posicionReal[0]>0&&posicionReal[1]>0&&posicionReal[0]<401){

				// Calculando la posicion matricial
				int[] posicionMatricial = new int[2];
				posicionMatricial[0] = (int)Math.floor(posicionReal[1]/60);
				posicionMatricial[1] = (int)Math.floor(posicionReal[0]/60);

				// Determinando el producto al que le corresponda la posicion matricial
				selected = (posicionMatricial[0]*7)+posicionMatricial[1];
			}
		}
	}
}