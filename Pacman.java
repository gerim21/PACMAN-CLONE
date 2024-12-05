package pacman;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Pacman extends JFrame{

	public Pacman() {
		add(new Model());
	}
	
	
	public static void main(String[] args) {
		Pacman pac = new Pacman();
		pac.setVisible(true);
		pac.setTitle("Pacman");
		pac.setSize(700,900);
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pac.setLocationRelativeTo(null);
		
	}

}
