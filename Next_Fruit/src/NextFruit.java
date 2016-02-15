
import co.edu.icesi.nextfruit.modules.Model;
import co.edu.icesi.nextfruit.views.MainMenuWindow;

public class NextFruit {

	public static void main(String[] args) {

		// Starting model
		Model model = new Model();

		// Starting first view
		MainMenuWindow menu = new MainMenuWindow();
		menu.init(model, null);
	}

}
