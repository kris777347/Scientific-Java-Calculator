import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

@SuppressWarnings("serial")
public class RomanDisplay extends Display {

	public RomanDisplay(){
		super();
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 26));
		g2.drawString(super.getNum(), 10, 33);
	}
}
