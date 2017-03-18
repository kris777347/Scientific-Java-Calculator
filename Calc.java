import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Calc {
	private JFrame frame;

	private LED panelLED = new LED();
	private RomanDisplay panelRomanDisplay = new RomanDisplay();

	private JPanel panelArabic = new JPanel();
	private JPanel panelHex = new JPanel();
	private JPanel panelRoman = new JPanel();
	private JPanel panelMode = new JPanel();

	private Rectangle rectArabic = new Rectangle(30, 140, 173, 133);
	private Rectangle rectHex = new Rectangle(30, 285, 165, 60);
	private Rectangle rectRoman = new Rectangle(30, 140, 173, 133);
	private Rectangle rectMode = new Rectangle(30, 60, 255, 30);

	private final int V_GRID_SPACE = 10;
	private final int H_GRID_SPACE = 10;

	private ButtonGroup groupMode = new ButtonGroup();
	private JRadioButton[] radioButtons;

	private String[] valuesArabic = { "7", "8", "9", "4", "5", "6", "1", "2", "3", "0" };
	private String[] valuesHex = { "A", "B", "C", "D", "E", "F" };
	private String[] valuesRoman = { "I", "V", "X", "L", "C", "D", "M" };
	private String[] valuesOperator = { "+", "-", "*", "/" };
	private String[] valuesMode = { "Dec", "Hex", "Bin", "Roman" };

	private boolean isRoman;
	private boolean isBinary;

	private boolean isDecimalEnabled;
	private boolean isHexadecimalEnabled;
	private boolean isDecimalVisible;
	private boolean isHexadecimalVisible;

	private boolean newCalculation;
	private boolean newValue;

	private String value1 = null;
	private String value2 = null;
	private String operator = null;

	private int[] numbersArabic = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
	private String[] numbersRoman = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

	private String lastMode;
	private String currentMode;

	private JRadioButton makeModeButton(String s, ButtonGroup bg, JPanel jp){
		JRadioButton radioButton = new JRadioButton(s);
		bg.add(radioButton);

		ItemListener listener = new ModeButtonListener();
		radioButton.addItemListener(listener);
		ActionListener listener2 = new ModeButtonListener();
		radioButton.addActionListener(listener2);

		return radioButton;
	}

	private void createModePanel(JPanel jp, ButtonGroup bg, String[] sArray, Rectangle r){
		jp.setBounds(r);
		jp.setLayout(new FlowLayout(FlowLayout.LEADING, H_GRID_SPACE, V_GRID_SPACE));

		for (String s: sArray)
			jp.add(makeModeButton(s, bg, jp));

		frame.getContentPane().add(jp);
		JRadioButton radioSelected = (JRadioButton) jp.getComponent(0);
		radioSelected.setSelected(true);
	}

	class ModeButtonListener implements ActionListener, ItemListener{

		public ModeButtonListener(){

		}

		public String convertToDecimal(){
			String value;

			if(lastMode.equals("Roman")) value = panelRomanDisplay.getNum();
			else value = panelLED.getNum().replaceAll("Z", "");

			if(!value.equals("") && !value.equals("Error")){
				switch (lastMode)
				{
				case "Hex": return hexToDec(value);
				case "Bin": return binToDec(value);
				case "Roman": return romToDec(value);

				default: return value;
				}
			}

			else return value;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JRadioButton[] radioButtons = new JRadioButton[valuesMode.length];
			String value = convertToDecimal();

			for (int i=0; i<radioButtons.length; i++) {
				radioButtons[i] = (JRadioButton) panelMode.getComponent(i);
				if(radioButtons[i].isSelected()){
					currentMode = radioButtons[i].getText();
				}
			}
			if(!value.equals("") && !value.equals("Error")){
				if(radioButtons[1].isSelected()) value = decToHex(value);
				else if(radioButtons[2].isSelected()) value = decToBin(value);
				else if(radioButtons[3].isSelected()) value = decToRom(value);
			}

			if(!isRoman) panelLED.setNum(value);
			else panelRomanDisplay.setNum(value);
		}

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			radioButtons = new JRadioButton[valuesMode.length];
			lastMode = currentMode;

			for (int i=0; i<radioButtons.length; i++)
				radioButtons[i] = (JRadioButton) panelMode.getComponent(i);

			if(radioButtons[3].isSelected()){
				isDecimalVisible = false;
				isHexadecimalVisible = false;
				isRoman = true;
			}
			else{
				isDecimalVisible = true;
				isHexadecimalVisible = true;
				isRoman = false;
			}

			if(radioButtons[0].isSelected() || radioButtons[1].isSelected())
				isDecimalEnabled = true;
			else isDecimalEnabled = false;

			if(radioButtons[1].isSelected()) isHexadecimalEnabled = true;
			else isHexadecimalEnabled = false;

			if(radioButtons[2].isSelected()) isBinary = true;
			else isBinary = false;

			panelArabic.setVisible(isDecimalVisible);
			panelHex.setVisible(isHexadecimalVisible);
			panelRoman.setVisible(isRoman);
			panelRomanDisplay.setVisible(isRoman);
			panelLED.setVisible(!isRoman);

			enableButtons(panelArabic, isDecimalEnabled);
			enableButtons(panelHex, isHexadecimalEnabled);

			if(radioButtons[2].isSelected()){
				enableSingleButton(panelArabic, isBinary, 6);
				enableSingleButton(panelArabic, isBinary, 9);
			}

			frame.repaint();
		}
	}

	private JButton makeDigitButton(String s){
		JButton button = new JButton(s);

		ActionListener	listener = new DigitButtonListener(s);
		button.addActionListener(listener);

		return button;
	}

	private void createButtonPanel(JPanel jp, String[] sArray, Rectangle r, int row, int column){
		jp.setBounds(r);
		jp.setLayout(new GridLayout(row, column, H_GRID_SPACE, V_GRID_SPACE));

		for (String s: sArray)
			jp.add(makeDigitButton(s));

		frame.getContentPane().add(jp);
	}

	class DigitButtonListener implements ActionListener{
		private String digit;

		public DigitButtonListener(String aDigit){
			digit = aDigit;
		}

		public void actionPerformed(ActionEvent event){
			Display d;

			if(isRoman) d = panelRomanDisplay;
			else d = panelLED;

			if(newValue) d.resetDisplay();

			d.addDigit(digit);
			frame.repaint();
			newValue = false;
		}
	}

	private JButton makeOpButton(String s){
		JButton button = new JButton(s);
		ActionListener listener = new OperatorButtonListener(s);
		button.addActionListener(listener);

		return button;
	}

	private void createOpButtonPanel(){
		JPanel operatorPanel = new JPanel();
		operatorPanel.setBounds(230, 140, 45, 130);
		operatorPanel.setLayout(new GridLayout(4, 1, 10, 10));

		for (String s: valuesOperator)
			operatorPanel.add(makeOpButton(s));

		frame.getContentPane().add(operatorPanel);
	}

	class OperatorButtonListener implements ActionListener{
		private String op;

		public OperatorButtonListener(String anOperator){
			op = anOperator;
		}

		public void actionPerformed(ActionEvent event){
			operator = op;

			if(newCalculation){
				if(isRoman) value1 = panelRomanDisplay.getNum();
				else value1 = panelLED.getNum();

				newCalculation = false;
				newValue = true;
			}
			else value1 = value2;

			if(isRoman){
				value1 = panelRomanDisplay.getNum();
				panelRomanDisplay.resetDisplay();
			}
			else{
				value1 = panelLED.getNum();
				panelLED.resetDisplay(); // M
			}

			frame.repaint();
		}
	}

	private void enableButtons(JPanel jp, boolean b){
		Component[] components = jp.getComponents();
		for(Component c: components) c.setEnabled(b);
	}

	private void enableSingleButton(JPanel jp, boolean b, int i){
		Component components = jp.getComponent(i);
		components.setEnabled(b);
	}

	private int calculate(String strValue1, String strValue2, String op){
		String strippedValue1 = strValue1.replaceAll("Z", "");
		String strippedValue2 = strValue2.replaceAll("Z", "");
		int value1;
		int value2;

		if(radioButtons[1].isSelected()){
			value1 = Integer.parseInt(strippedValue1, 16);
			value2 = Integer.parseInt(strippedValue2, 16);
		}

		else if(radioButtons[2].isSelected()){
			value1 = Integer.parseInt(strippedValue1, 2);
			value2 = Integer.parseInt(strippedValue2, 2);
		}

		else if(radioButtons[3].isSelected()){
			value1 = Integer.parseInt(romToDec(strippedValue1));
			value2 = Integer.parseInt(romToDec(strippedValue2));
		}

		else{
			value1 = Integer.parseInt(strippedValue1);
			value2 = Integer.parseInt(strippedValue2);
		}

		// System.out.println("value1: " + value1);
		// System.out.println("value2: " + value2);
		// System.out.println("operator: " + operator);

		if (op.equals("+"))	return value1 + value2;
		else if(op.equals("-")) return value1 - value2;
		else if(op.equals("*")) return value1 * value2;
		else if(op.equals("/")) return value1 / value2;
		else return value2;
	}

	private String decToHex(String s){
		int numberDecimal = Integer.parseInt(s);

		return (Integer.toHexString(numberDecimal)).toUpperCase();
	}

	private String hexToDec(String s){
		int numberDecimal = Integer.parseInt(s, 16);

		return String.valueOf(numberDecimal);
	}

	private String decToBin(String s){
		int numberDecimal = Integer.parseInt(s);

		if(numberDecimal > 0 && numberDecimal < 257){
			return (Integer.toBinaryString(numberDecimal));
		}

		else return "Error";
	}

	private String binToDec(String s){
		int intDecimal = Integer.parseInt(s, 2);

		return String.valueOf(intDecimal);
	}

	private String decToRom(String s){
		int numberDecimal = Integer.parseInt(s);
		String numberRoman = "";

		// System.out.println("numberDecimal: " + numberDecimal);

		if(numberDecimal>0 && numberDecimal<4000){
			for (int i=0; i<numbersArabic.length; i++) {
				while (numberDecimal >= numbersArabic[i]) {
					numberRoman += numbersRoman[i];
					numberDecimal -= numbersArabic[i];
				}
			}
			return numberRoman;
		}

		else return "Error";
	}

	private String romToDec(String s){
		String numberRoman = s;
		int numberDecimal = 0;

		for (int i=0; i<numbersArabic.length; i++){
			while (!numberRoman.equals("") && numberRoman.startsWith(numbersRoman[i])){
				numberDecimal += numbersArabic[i];
				numberRoman = numberRoman.replaceFirst(numbersRoman[i], "");
			}
		}
		return String.valueOf(numberDecimal);
	}

	public Calc(){
		initialize();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize(){
		newCalculation = true;
		newValue = true;
		currentMode = "Dec";

		frame = new JFrame();
		frame.setBounds(100, 100, 325, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		frame.getContentPane().add(panelLED);
		frame.getContentPane().add(panelRomanDisplay);
		panelRomanDisplay.setVisible(isRoman);

		createModePanel(panelMode, groupMode, valuesMode, rectMode);

		JRadioButton[] radioButtons = new JRadioButton[valuesMode.length];

		for (int i=0; i<radioButtons.length; i++)
			radioButtons[i] = (JRadioButton) panelMode.getComponent(i);

		JButton btnAC = new JButton("AC");
		btnAC.setBounds(30, 100, 55, 23);
		frame.getContentPane().add(btnAC);
		btnAC.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent me){
				if(isRoman) panelRomanDisplay.resetDisplay();
				else panelLED.resetDisplay();
				frame.repaint();

				value1 = null;
				value2 = null;
				operator = null;
				newCalculation = true;
				newValue = true;
			}
		});

		JButton btnDel = new JButton("Del");
		btnDel.setBounds(95, 100, 55, 23);
		frame.getContentPane().add(btnDel);
		btnDel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent me){

				if(isRoman) panelRomanDisplay.deleteDigit();
				else panelLED.deleteDigit();

				frame.repaint();
			}
		});

		createButtonPanel(panelArabic, valuesArabic, rectArabic, 4, 3);
		enableButtons(panelArabic, isDecimalEnabled);
		createButtonPanel(panelHex, valuesHex, rectHex, 2, 3);
		enableButtons(panelHex, isHexadecimalEnabled);
		createButtonPanel(panelRoman, valuesRoman, rectRoman, 4, 2);
		createOpButtonPanel();

		JButton btnEqual = new JButton("=");
		btnEqual.setBounds(230, 285, 45, 65);
		frame.getContentPane().add(btnEqual);

		btnEqual.addMouseListener(new MouseListener(){
			private int calculatedValue;

			@Override
			public void mouseClicked(MouseEvent me){
			}

			@Override
			public void mouseEntered(MouseEvent me){
			}

			@Override
			public void mouseExited(MouseEvent me){
			}

			@Override
			public void mousePressed(MouseEvent me){
				if(operator == null || value1 == null){
					value1 = "1";
					operator = "Q";
				}

				if(isRoman){
					value2 = panelRomanDisplay.getNum();
					panelRomanDisplay.resetDisplay();
				}
				else{
					value2 = panelLED.getNum();
					panelLED.resetDisplay();
				}
				calculatedValue = calculate(value1, value2, operator);

				if(calculatedValue < 0){
					if(isRoman) panelRomanDisplay.setNum("Error");
					else panelLED.setNum("Error");
				}
				else{
					if(radioButtons[1].isSelected()){
						panelLED.setNum((Integer.toHexString(calculatedValue)).toUpperCase());
					}

					else if(radioButtons[2].isSelected()){
						panelLED.setNum( Integer.toBinaryString(calculatedValue) );
					}

					else if(radioButtons[3].isSelected()){
						String tempString  = String.valueOf(calculatedValue);
						panelRomanDisplay.setNum(decToRom(tempString));
					}

					else{
						panelLED.setNum(String.valueOf(calculatedValue));
					}
				}

				value2 = String.valueOf(calculatedValue);
				value1 = null;
				operator = null;
				newValue = true;
			}

			@Override
			public void mouseReleased(MouseEvent me){
				panelLED.revalidate();
				frame.repaint();
			}
		});
	}

	public static void main(String[] args){

		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					Calc window = new Calc();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
