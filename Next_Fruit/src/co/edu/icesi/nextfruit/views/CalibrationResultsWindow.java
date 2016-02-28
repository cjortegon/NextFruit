package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import co.edu.icesi.nextfruit.controller.CalibrationResultsController;
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.modules.callibrator.ColorChecker;
import co.edu.icesi.nextfruit.mvc.interfaces.Attachable;
import co.edu.icesi.nextfruit.mvc.interfaces.Initializable;
import co.edu.icesi.nextfruit.mvc.interfaces.Updateable;
import visualkey.KFrame;

public class CalibrationResultsWindow extends KFrame implements Initializable, Updateable {

	private static final long serialVersionUID = 1L;
	private Model model;
	private JTable colorSpacesGrid;
	private JLabel lbPixelsXCm;
	private JLabel lbColorCal;
	private JLabel lbPixelsXCmValue;
	private JButton btSaveSettings;

	
	
	@Override
	public void init(Attachable model, Updateable view) {

		// Initializing objects
		lbPixelsXCm = new JLabel("", SwingConstants.CENTER);
		lbColorCal = new JLabel(" Color Calibration ", SwingConstants.CENTER);
		lbPixelsXCmValue = new JLabel("");
		lbPixelsXCm.setPreferredSize(new Dimension(500, 30));
		lbColorCal.setPreferredSize(new Dimension(500, 30));
		lbPixelsXCmValue.setPreferredSize(new Dimension(500, 30));
		btSaveSettings = new JButton(" Save This Camera Settings ");

		lbPixelsXCm.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		lbColorCal.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		
		// Attaching to model
		this.model = (Model) model;
		model.attach(this);

		
		//Label
		String pixelsXCm = this.model.getSizeCalibrator().getPixelsForCentimeter() + "";
		lbPixelsXCm.setText(" Simple Spatial Calibration ");
		lbPixelsXCmValue.setText(" There are " + pixelsXCm + " pixels per centimeter. ");

		lbPixelsXCmValue.setFont(lbPixelsXCmValue.getFont().deriveFont(Font.PLAIN));
		
		
		//Grid
		ColorChecker cC = this.model.getColorChecker();

		Object[] columnNames = {"", "", "", "", "", ""};
		Object[][] rowData = new Object[4][6];
		
		double[][][] rgbValues = cC.getReadRGB();
		double[][][] labValues = cC.getReadLAB();
		
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 6; j++){
				
				rowData[i][j] = "<html><b><center>" + "RGB: " + (int)rgbValues[i][j][0] +
						", " + (int)rgbValues[i][j][1] + ", " + (int)rgbValues[i][j][2] + "<br>" +
						"Lab: " + (int)labValues[i][j][0] + ", " + (int)labValues[i][j][1] + ", " +
						(int)labValues[i][j][2] + "</center></b></html>";
				
			}
		}

		
		colorSpacesGrid = new JTable(rowData, columnNames);	
		
		colorSpacesGrid.setRowHeight(60);
		colorSpacesGrid.setBorder(new LineBorder(Color.BLACK));
		colorSpacesGrid.setGridColor(Color.BLACK);
		
		colorSpacesGrid.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
		colorSpacesGrid.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
		colorSpacesGrid.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
		colorSpacesGrid.getColumnModel().getColumn(3).setCellRenderer(new CustomRenderer());
		colorSpacesGrid.getColumnModel().getColumn(4).setCellRenderer(new CustomRenderer());
		colorSpacesGrid.getColumnModel().getColumn(5).setCellRenderer(new CustomRenderer());
		
		setTableWidth();
		


		//Adding objects to window
		addComponent(lbPixelsXCm, 1, 0, 1, 1, false);
		addComponent(lbPixelsXCmValue, 2, 0, 1, 1, false);
		addComponent(lbColorCal, 3, 0, 1, 1, false);
		addComponent(colorSpacesGrid, 4, 0, 1, 1, true);
		addComponent(btSaveSettings, 5, 0, 1, 1, true);
		

		
		// Starting controller
		new CalibrationResultsController().init(model, this);

		// Ending initialization
		pack();
		setResizable(false);
		
	}

	
	@Override
	public void update() {
		if(isVisible()) {
			// Repainting components
			repaint();
		}
	}
	
	
	private void setTableWidth(){

		JTableHeader header = colorSpacesGrid.getTableHeader();

		TableCellRenderer defaultHeaderRenderer = null;

		if (header != null)
			defaultHeaderRenderer = header.getDefaultRenderer();

		TableColumnModel columns = colorSpacesGrid.getColumnModel();
		TableModel data = colorSpacesGrid.getModel();

		int margin = columns.getColumnMargin(); // only JDK1.3

		int rowCount = data.getRowCount();

		//int totalWidth = 0;

		for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
			TableColumn column = columns.getColumn(i);

			int columnIndex = column.getModelIndex();

			int width = -1;

			TableCellRenderer h = column.getHeaderRenderer();

			if (h == null){
				h = defaultHeaderRenderer;
			}

			if (h != null) // Not explicitly impossible
			{
				Component c = h.getTableCellRendererComponent(colorSpacesGrid, column.getHeaderValue(), false, false,
						-1, i);

				width = c.getPreferredSize().width;
			}

			for (int row = rowCount - 1; row >= 0; --row) {
				TableCellRenderer r = colorSpacesGrid.getCellRenderer(row, i);

				Component c = r.getTableCellRendererComponent(colorSpacesGrid, data.getValueAt(row, columnIndex), false,
						false, row, i);

				width = Math.max(width, c.getPreferredSize().width);
			}

			if (width >= 0){
				column.setPreferredWidth(width + margin); // <1.3: without margin
			}

			//totalWidth += column.getPreferredWidth();

		}

	}


	public JButton getBtSaveSettings() {
		return btSaveSettings;
	}


	public void setBtSaveSettings(JButton btSaveSettings) {
		this.btSaveSettings = btSaveSettings;
	}

}
