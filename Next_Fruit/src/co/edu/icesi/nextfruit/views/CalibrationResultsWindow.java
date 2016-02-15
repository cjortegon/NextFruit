package co.edu.icesi.nextfruit.views;

import co.edu.icesi.nextfruit.controller.CalibrationResultsController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

public class CalibrationResultsWindow extends KFrame implements Initializable, Updateable {

	private Model model;

	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		/**
		 * Aqui creas los objetos de tu vista
		 */

		// Adding objects to window
		/**
		 * Aqui agregas los objetos a la ventana usando el metodo addComponent(objeto, fila, columna, filas_ocupadas, columnas_ocupadas, centrado?);
		 * Vas agregando los componentes como si se tratara de una cuadricula de excel, si es un componente grande puede ocupar mas de una fila o mas de una columna
		 * El tamano de filas y columnas se adapta al de los componentes
		 */

		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		// Starting controller
		new CalibrationResultsController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
	}

	@Override
	public void update() {
		if(isVisible()) {

			/**
			 * Aqui es donde se actualizan los componentes graficos llamando a los atributos del modelo
			 */

			// Repainting components
			repaint();
		}
	}

}
