package co.edu.icesi.nextfruit.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import co.edu.icesi.nextfruit.views.CalibrationResultsWindow;

public class CalibrationResultsController implements Initializable, ActionListener {

	private static final String SAVE_SETTINGS = "SaveSettings";
	
	private Model model;
	private CalibrationResultsWindow view;

	
	@Override
	public void init(Attachable model, Updateable view) {
		this.model = (Model) model;
		this.view = (CalibrationResultsWindow) view;
		addListeners();
	}

	
	private void addListeners() {
		
		view.getBtSaveSettings().setActionCommand(SAVE_SETTINGS);
		view.getBtSaveSettings().addActionListener(this);
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("actionPerformed: "+e.getActionCommand());
		switch (e.getActionCommand()) {
			
		case SAVE_SETTINGS:
			//
			//	Insert code for saving settings to a file in disk.
			//
			break;
		
		}
	}

}