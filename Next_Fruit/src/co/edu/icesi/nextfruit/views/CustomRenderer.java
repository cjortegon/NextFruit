package co.edu.icesi.nextfruit.views;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 * @author JuanD
 * Custom renderer to align and paint every cell of a different colour.
 */
@SuppressWarnings("serial")
public class CustomRenderer extends DefaultTableCellRenderer{

		
	/**
	 * Method to center the text and paint the background of every cell,
	 * from a given column in a JTable
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column){
		
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		String rgb = table.getValueAt(row, column).toString();
		
		String[] t1 = rgb.split("<br>");
		String[] t2 = t1[0].split(":");
		String[] values = t2[1].split(",");
		
		int[] components = new int[3];
		
		components[0] = Integer.parseInt(values[0].trim());
		components[1] = Integer.parseInt(values[1].trim());
		components[2] = Integer.parseInt(values[2].trim());

		cellComponent.setBackground(new Color(components[0], components[1], components[2]));
		cellComponent.setForeground(new Color(255, 255, 255));
		
		if(row == 3 && column == 0 || row == 3 && column == 1 || 
				row == 3 && column == 2 || row == 2 && column == 3){
			cellComponent.setForeground(new Color(0, 0, 0));
		}
		
		this.setHorizontalAlignment(JLabel.CENTER);

		return cellComponent;
		
	}
	
}
