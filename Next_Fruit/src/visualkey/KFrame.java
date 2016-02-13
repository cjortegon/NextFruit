package visualkey;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class KFrame extends JFrame {

	// Constantes
	final boolean CENTRADO = true;
	final boolean HORIZONTAL = false;

	// Labels
	private JLabel label;

	// Grid
	private GridBagConstraints restriction = new GridBagConstraints();

	// Layout
	private GridBagLayout layout = new GridBagLayout();

	// Constructors
	public KFrame(){
		this.setLayout(layout);
	}
	public KFrame(Dimension dimension){
		this.setLayout(layout);
		this.setSize(dimension);
	}

	public Image getImage(String path){
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
	}

	public ImageIcon getImageIcon(String path){
		return new ImageIcon(this.getImage(path));
	}

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