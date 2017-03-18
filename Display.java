import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class Display extends JPanel {

	private String num;

	public Display(){
		num = "";

		this.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.setBounds(30, 10, 255, 45);
	}

	public String getNum(){
		return num;
	}

	public void setNum(String number){
		num = number;
	}

	public void resetDisplay(){
		num = "";
	}

	public void addDigit(String d){
		num = num + d;
	}

	public void deleteDigit(){
		num = num.substring(0, num.length() - 1);
	}
}
