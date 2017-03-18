import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Bar extends JComponent{
	private Color color;
	private Line2D.Double line;

	public Bar(int state, int x1, int y1, int x2, int y2){
		if(state == 1)
			color = Color.RED;
		else
			color = Color.GRAY.brighter().brighter().brighter().brighter();

		line = new Line2D.Double(x1, y1, x2, y2);
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.draw(line);
	}
}
