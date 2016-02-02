package visualkey;

import java.awt.*;

import javax.swing.*;

import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class KPanel extends JPanel {

	// Constantes
	final boolean CENTRADO = true;
	final boolean HORIZONTAL = false;

	// Constructores
	public KPanel(){
		this.setLayout(layout);
	}

	public KPanel(Dimension dimension){
		this.setPreferredSize(dimension);
		this.setLayout(layout);
	}

	// Mueve objetos en el plano
	public int[] move(int[] array,int increment){
		for(int h=0;h<array.length;h++){
			array[h]+=increment;
		}
		return array;
	}

	public Image getImage(String path){
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
	}

	public ImageIcon getImageIcon(String path){
		return new ImageIcon(this.getImage(path));
	}

	// Labels
	private JLabel label;

	// Grid
	private GridBagConstraints restriction = new GridBagConstraints();

	// Layout
	private GridBagLayout layout = new GridBagLayout();

	public void addLabel(String text, int fila, int columna, int ancho, int alto, int wightx, int weightx, int weighty, boolean centrado) {
		label = new JLabel(text);
		setRestriction(fila, columna, ancho, alto, wightx, weightx, centrado);
		layout.setConstraints(label, restriction);
		this.add(label);
	}

	public void addComponent(Component component, int fila, int columna, int ancho, int alto, int nweightx, int nweighty, boolean centrado){
		setRestriction(fila, columna, ancho, alto, nweighty, nweightx, centrado);
		layout.setConstraints(component, restriction);
		this.add(component);
	}

	public void setRestriction(int fila, int columna, int ancho, int alto, int nweightx, int nweighty, boolean centrado) {
		restriction.gridx = columna;
		restriction.gridy = fila;
		restriction.gridwidth = ancho;
		restriction.gridheight = alto;
		restriction.weightx = nweightx;
		restriction.weighty = nweighty;
		if(centrado){
			restriction.fill = restriction.CENTER;
		}else{
			restriction.fill = restriction.HORIZONTAL;
		}
	}

	public void showMessage(String msj){
		JOptionPane.showMessageDialog(this,msj);
	}

	public String playSound(String sound){
		String respuesta = "OK";
		try {
			Clip sonido = AudioSystem.getClip();
			sonido.open(AudioSystem.getAudioInputStream(getClass().getResource(sound)));
			sonido.start();
		} catch (LineUnavailableException e) {
			respuesta = "IMPOSIBLE TO PLAY THIS SOUND: "+sound;
			e.printStackTrace();
		} catch (IOException e) {
			respuesta = "FILE NOT FOUND: "+sound;
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			respuesta = "UNSUPPOTED AUDIO INPUT: "+sound;
			e.printStackTrace();
		}
		return respuesta;
	}
}