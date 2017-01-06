package edu;

//
//Copyright 1998 by Lee Wittenberg.
//This software may be freely used for noncommercial purposes only.
//Any commercial use requires the author's permission.
//
import java.awt.*;
import java.awt.event.*;;
public class ssem1 extends Thread {
	public static ssem1 machine;
	public static ssem_Interface iface;
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d") || args[i].equals("-demo"))
				load_demo = true;;
		};
		ssem1.machine = new ssem1();
		ssem1.machine.setPriority(Thread.NORM_PRIORITY-1);
		ssem1.iface = new ssem_Interface();
	}
	public ssem1() {
		if (load_demo) {
			{
				int[] factor_pgm = {
								   	0x0,
								   	0x4018, 0x601a, 0x401a, 0x601b, 0x4017, 0x801b,
								   	0xc000, 0x2014, 0x801a, 0x6019, 0x4019, 0xc000,
								   	0xe000, 0x401a, 0x8015, 0x601b, 0x401b, 0x601a,
								   	0x0016, -0x3, 0x1, 0x4, -0x40000, 0x3ffff
								   };

				for (int i = 0; i < factor_pgm.length; i++)
					s_tube.set_Line_Value(i, factor_pgm[i]);
			};
		};
		start();
	}
	public final static int STORE_SIZE = 32;
	private Williams_Tube a_tube = new Williams_Tube(1);
	private Williams_Tube c_tube = new Williams_Tube(2);
	private Williams_Tube s_tube = new Williams_Tube(STORE_SIZE);
	private final static int CI = 0;
	private final static int PI = 1;
	public Williams_Tube get_A_Tube() {
		return a_tube;
	}
	public Williams_Tube get_C_Tube() {
		return c_tube;
	}
	public Williams_Tube get_S_Tube() {
		return s_tube;
	}
	public final static int FUNC_BITS = 3;
	public final static int FUNC_MASK = ~(~0 << FUNC_BITS);
	public final static int ADDR_BITS = 13;
	public final static int UNUSED_ADDR_BITS = 8;
	public final static int ADDR_MASK =
		~(~0 << (ADDR_BITS-UNUSED_ADDR_BITS));
	private boolean test_flip_flop = false;
	private int staticisor = 0;
	private boolean pre_pulse = false;
	public void run() {
		suspend();
		while (true) {
			do {
				{
					int n = test_flip_flop ? +2 : +1;
					c_tube.set_Line_Value(CI, c_tube.get_Line_Value(CI) + n);
					test_flip_flop = false;
					ssem1.iface.update_C_Display(CI);
				};
				c_tube.set_Line_Value(PI,
					s_tube.get_Line_Value(c_tube.get_Line_Value(CI) & ADDR_MASK));
				ssem1.iface.update_C_Display(PI);;
				staticisor = get_Manual() ? Line.ALL_ONES : c_tube.get_Line_Value(PI);
				staticisor &= get_Stat_Switches();;
				{
					int op = (staticisor >> ADDR_BITS) & FUNC_MASK;
					int addr = staticisor & ADDR_MASK;
					switch (op) {
					case 0:
						c_tube.set_Line_Value(CI, s_tube.get_Line_Value(addr));
						ssem1.iface.update_C_Display(CI);
						break;
					case 1:
						c_tube.set_Line_Value(CI, c_tube.get_Line_Value(CI) +
							s_tube.get_Line_Value(addr));
						ssem1.iface.update_C_Display(CI);
						break;
					case 2:
						a_tube.set_Line_Value(0, -s_tube.get_Line_Value(addr));
						ssem1.iface.update_A_Display(0);
						break;
					case 3:
						s_tube.set_Line_Value(addr, a_tube.get_Line_Value(0));
						ssem1.iface.update_S_Display(addr);
						break;
					case 4:
					case 5:
						a_tube.set_Line_Value(0, a_tube.get_Line_Value(0)
							- s_tube.get_Line_Value(addr));
						ssem1.iface.update_A_Display(0);
						break;
					case 6:
						if (a_tube.get_Line_Value(0) < 0) {
							test_flip_flop = true;
						}
						break;
					case 7:
						set_Prepulse(false);
						ssem1.iface.get_Lamp().illuminate(true);;
						break;
					}
				};;
				yield();
			} while (pre_pulse);
			suspend();
		}
	}
	synchronized public void set_Prepulse(boolean b) {
		pre_pulse = b;
	}
	private static boolean load_demo = false;
	private boolean manual_mode = true;
	synchronized public void set_Manual(boolean b) {
		manual_mode = b;
	}
	synchronized public boolean get_Manual() {
		return manual_mode;
	}
	private int staticisor_switches = Line.ALL_ONES;
	synchronized public int get_Stat_Switches() {
		return staticisor_switches;
	}
	synchronized public void set_Stat_Switch_Bit(int n) {
		staticisor_switches |= (0x1 << n);
	}
	synchronized public void reset_Stat_Switch_Bit(int n) {
		staticisor_switches &= ~(0x1 << n);
	}
	private boolean write_mode = true;
	public void set_Write_Flag(boolean b) {
		write_mode = b;
	}
	public boolean get_Write_Flag() {
		return write_mode;
	};
}
class Williams_Tube {
	private Line[] lines;
	public Williams_Tube(int n) {
		lines = new Line[n];
		for (int i = 0; i < n; i++)
			lines[i] = new Line(0);
	}
	public int get_Line_Value(int i) {
		return lines[i].get_Value();
	}
	public void set_Line_Value(int i, int n) {
		lines[i].set_Value(n);
	}
	public int get_Num_Lines() {
		return lines.length;
	}
	public void clear_Tube() {
		for (int i = 0; i < lines.length; i++)
			lines[i].set_Value(0);
	};
}
class Line {
	public final static int BITS_PER_LINE = 32;
	public final static int ALL_ONES = ~0;
	private int value;
	public Line(int n) {
		value = n;
	}
	public int get_Value() {
		return value;
	}
	public void set_Value(int n) {
		value = n;
	};
}
class Manual_Mode_Callback extends Callback {
	private boolean val;
	public Manual_Mode_Callback(boolean b) {
		val = b;
	}
	public void func(AWTEvent e) {
		ssem1.machine.set_Manual(val);
	}
}
class KSP_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.iface.get_Lamp().illuminate(false);;
		ssem1.machine.resume();
	}
}
class Stat_Switch_Callback extends Callback {
	private int bit;
	private boolean val;
	public Stat_Switch_Callback(int n, boolean b) {
		bit = n;
		val = b;
	}
	public void func(AWTEvent e) {
		if (val)
			ssem1.machine.set_Stat_Switch_Bit(bit);
		else
			ssem1.machine.reset_Stat_Switch_Bit(bit);
	}
}
class Start_Exec_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.set_Prepulse(true);
		ssem1.iface.get_Lamp().illuminate(false);;
		ssem1.machine.resume();
	}
}
class Stop_Exec_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.set_Prepulse(false);
	}
}
class KSC_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.get_S_Tube().clear_Tube();
		ssem1.iface.update_S_Display();
	}
}
class KCC_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.get_A_Tube().clear_Tube();
		ssem1.machine.get_C_Tube().clear_Tube();
		ssem1.iface.update_A_Display();
		ssem1.iface.update_C_Display();
	}
}
class KLC_Callback extends Callback {
	public void func(AWTEvent e) {
		int n = ssem1.machine.get_Stat_Switches() & ssem1.ADDR_MASK;
		ssem1.machine.get_S_Tube().set_Line_Value(n, 0);
		ssem1.iface.update_S_Display(n);
	}
}
class KAC_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.get_A_Tube().clear_Tube();
		ssem1.iface.update_A_Display();
	}
}
class KEC_Callback extends Callback {
	public void func(AWTEvent e) {
		ssem1.machine.get_A_Tube().clear_Tube();
		ssem1.machine.get_C_Tube().clear_Tube();
		ssem1.machine.get_S_Tube().clear_Tube();
		ssem1.iface.update_A_Display();
		ssem1.iface.update_C_Display();
		ssem1.iface.update_S_Display();
	}
}
class Write_Flag_Callback extends Callback {
	private boolean val;
	public Write_Flag_Callback(boolean b) {
		val = b;
	}
	public void func(AWTEvent e) {
		ssem1.machine.set_Write_Flag(val);
	}
}
class Typewriter_Callback extends Callback {
	private int mask;
	public Typewriter_Callback(int n) {
		mask = 0x1 << n;
	}
	public void func(AWTEvent e) {
		int i = ssem1.machine.get_Stat_Switches() & ssem1.ADDR_MASK;
		int n = ssem1.machine.get_S_Tube().get_Line_Value(i);
		if (ssem1.machine.get_Write_Flag())
			n |= mask;
		else
			n &= ~mask;
		ssem1.machine.get_S_Tube().set_Line_Value(i, n);
		ssem1.iface.update_S_Display(i);
	}
}
class ssem_Interface extends Frame {
	public ssem_Interface() {
		setTitle("SSEM Simulator");
		addWindowListener(new ProgramCloser());;
		{
			GridBagLayout gbl = new GridBagLayout();
			setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;;
			gbc.gridx = 0; gbc.gridwidth = gbc.gridheight = 1;
			Component x;
			Display_Panel d;
			x = d = new Display_Panel();
			a_display = d.get_A_Display();
			c_display = d.get_C_Display();
			s_display = d.get_S_Display();
			gbc.gridy = 0;
			gbl.setConstraints(x, gbc);
			add(x);;
			x = new Typewriter_Panel();
			gbc.gridy = 1;
			gbl.setConstraints(x, gbc);
			add(x);;
			x = new Stat_Panel();
			gbc.gridy = 2;
			gbl.setConstraints(x, gbc);
			add(x);;
			Control_Panel p;
			x = p = new Control_Panel();
			lamp = p.get_Lamp();
			gbc.gridy = 3;
			gbl.setConstraints(x, gbc);
			add(x);;
			pack();
			d.adjust();
		};
		pack();
		setResizable(false);
		setVisible(true);
	}
	private class ProgramCloser extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	private Stop_Lamp lamp;
	public Stop_Lamp get_Lamp() {
		return lamp;
	}
	private Display_Tube a_display;
	private Display_Tube c_display;
	private Display_Tube s_display;
	public void update_A_Display() {
		a_display.repaint();
	}
	public void update_A_Display(int n) {
		a_display.repaint(n);
	}
	public void update_C_Display() {
		c_display.repaint();
	}
	public void update_C_Display(int n) {
		c_display.repaint(n);
	}
	public void update_S_Display() {
		s_display.repaint();
	}
	public void update_S_Display(int n) {
		s_display.repaint(n);
	};
}
abstract class Callback {
	public abstract void func(AWTEvent e);
}
class CB_Button extends Button implements ActionListener {
	private Callback cb;
	public void actionPerformed(ActionEvent e) {
		cb.func(e);
	}
	public CB_Button(String s, Callback c) {
		super(s);
		cb = c;
		addActionListener(this);
	};
}
class CB_Checkbox extends Checkbox implements ItemListener {
	private Callback cb;
	public void itemStateChanged(ItemEvent e) {
		cb.func(e);
	}
	public CB_Checkbox(boolean b, Callback c) {
		super("", null, b);
		cb = c;
		addItemListener(this);
	}
	public CB_Checkbox(String s, CheckboxGroup g, boolean b, Callback c) {
		super(s,g,b);
		cb = c;
		addItemListener(this);
	};
}
class Display_Panel extends Panel {
	private Component[] parts;
	private final static int A_LABEL = 0;
	private final static int A_BOX = 1;
	private final static int A_TUBE = 2;
	private final static int C_LABEL = 3;
	private final static int C_BOX = 4;
	private final static int C_TUBE = 5;
	private final static int S_LABEL = 6;
	private final static int S_BOX = 7;
	private final static int S_TUBE = 8;
	private final static int NUM_PARTS = S_TUBE + 1;;
	public Display_Panel() {
		setLayout(null); 
		parts = new Component[NUM_PARTS];
		parts[A_TUBE] = new Display_Tube(ssem1.machine.get_A_Tube());
		parts[C_TUBE] = new Display_Tube(ssem1.machine.get_C_Tube());
		parts[S_TUBE] = new Display_Tube(ssem1.machine.get_S_Tube());
		add(parts[A_LABEL] = new Label("A:"));
		add(parts[A_BOX] = new CB_Checkbox(true, new
			Tube_Show_Callback(parts[A_TUBE])));
		add(parts[A_TUBE]);
		add(parts[C_LABEL] = new Label("C:"));
		add(parts[C_BOX] = new CB_Checkbox(true, new
			Tube_Show_Callback(parts[C_TUBE])));
		add(parts[C_TUBE]);
		add(parts[S_LABEL] = new Label("S:"));
		add(parts[S_BOX] = new CB_Checkbox(true, new
			Tube_Show_Callback(parts[S_TUBE])));
		add(parts[S_TUBE]);
	}
	public void adjust() {
		{
			int p_width, p_height;
			int margin = 8, extra_gap = 10*margin;
			{
				p_width = p_height = margin;
				for (int i = A_LABEL; i <= C_TUBE; i++) {
					Dimension d = parts[i].getPreferredSize();
					p_width += d.width;
					p_height = Math.max(p_height, d.height);

				}
				p_width += margin + extra_gap;
				p_height += margin;
			};
			{
				int x_pos = margin;
				{
					Dimension d;
					d = parts[A_LABEL].getPreferredSize();
					parts[A_LABEL].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
					d = parts[A_BOX].getPreferredSize();
					parts[A_BOX].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
					d = parts[A_TUBE].getPreferredSize();
					parts[A_TUBE].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
				};
				x_pos += extra_gap;
				{
					Dimension d;
					d = parts[C_LABEL].getPreferredSize();
					parts[C_LABEL].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
					d = parts[C_BOX].getPreferredSize();
					parts[C_BOX].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
					d = parts[C_TUBE].getPreferredSize();
					parts[C_TUBE].setBounds(x_pos,
											(p_height-d.height+1)/2,
											d.width,
											d.height);
					x_pos += d.width;
				};
			};
			{
				p_height += margin;
				Dimension d = parts[S_TUBE].getPreferredSize();
				int x_pos = (p_width - d.width+1)/2;
				int y_pos = p_height;
				p_height += d.height;
				parts[S_TUBE].setBounds(x_pos, y_pos, d.width, d.height);
				d = parts[S_LABEL].getPreferredSize();
				x_pos -= parts[S_BOX].getPreferredSize().width;
				parts[S_LABEL].setBounds(x_pos-d.width, y_pos, d.width, d.height);
				y_pos += (d.height+1)/2;
				d = parts[S_BOX].getPreferredSize();
				parts[S_BOX].setBounds(x_pos, y_pos-(d.height+1)/2, d.width, d.height);
				p_height += margin;
			};
			setSize(p_width, p_height);
		};
	}
	public Display_Tube get_A_Display() {
		return (Display_Tube) parts[A_TUBE];
	}
	public Display_Tube get_C_Display() {
		return (Display_Tube) parts[C_TUBE];
	}
	public Display_Tube get_S_Display() {
		return (Display_Tube) parts[S_TUBE];
	};
}
class Tube_Show_Callback extends Callback {
	private Display_Tube tube;
	public Tube_Show_Callback(Component t) {
		tube = (Display_Tube)t;
	}
	public void func(AWTEvent e) {
		tube.setEnabled(((Checkbox)e.getSource()).getState());
		tube.repaint();
	}
}
class Display_Tube extends Canvas {
	public Dimension getMinimumSize() {
		return new Dimension(Width, Height);
	}
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	public Dimension getSize() {
		return getMinimumSize();
	};
	private final static int LINE_HEIGHT = 8;
	private final static int BIT_WIDTH = 8;
	private final static int BIT_HEIGHT = 2;
	private final static int DASH_WIDTH = 5;
	private final static int DOT_WIDTH = 2;;
	private final static int Width = BIT_WIDTH * Line.BITS_PER_LINE;
	private int Height;
	private Williams_Tube actual_tube;
	private int num_lines;
	public Display_Tube(Williams_Tube t) {
		setBackground(Color.black);
		actual_tube = t;
		num_lines = t.get_Num_Lines();
		Height = num_lines * LINE_HEIGHT;
		setEnabled(true);
	}
	public void paint(Graphics g) {
		if (is_enabled) {
			for (int i = 0; i < num_lines; i++) {
				int n = actual_tube.get_Line_Value(i);
				for (int j = 0, mask = 0x1; j < Line.BITS_PER_LINE ; j++, mask <<= 1) {
					blob(g, i, j, (n & mask) != 0);
				}
			}
		} else {
			g.setColor(Color.black);
			g.fillRect(0, 0, Line.BITS_PER_LINE*BIT_WIDTH, num_lines*LINE_HEIGHT);
		}
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void repaint(int n) {
		if (is_enabled)
			repaint(0, n*LINE_HEIGHT, Line.BITS_PER_LINE*BIT_WIDTH, LINE_HEIGHT);
	}
	private static void blob(Graphics g, int row, int col, boolean val) {
		g.setColor(Color.white);
		g.fillRect(2+col*BIT_WIDTH, 4+row*LINE_HEIGHT, DOT_WIDTH, 
			BIT_HEIGHT);
		if (!val)
			g.setColor(Color.black);
		g.fillRect(2+col*BIT_WIDTH+DOT_WIDTH, 4+row*LINE_HEIGHT,
			DASH_WIDTH-DOT_WIDTH, BIT_HEIGHT);

	}
	private boolean is_enabled;
	public void setEnabled(boolean b) {
		is_enabled = b;
	};
}
class Typewriter_Panel extends Panel {
	public Typewriter_Panel() {
		Panel p = new Panel();
		add(p);
		p.setLayout(new GridLayout(5,8));
		for (int i = 0; i <5; i++) {
			for (int j = 0; j < 8; j++) {
				int n = i + (j*5);
				Button b = new CB_Button(""+n, new Typewriter_Callback(n));
				p.add(b);
				if (n >= Line.BITS_PER_LINE)
					b.setEnabled(false);
			}
		};
	}
}
class Stat_Panel extends Panel {
	public Stat_Panel() {
		{
			Panel p = new Panel();
			p.add(stat_switches(0,4,true));
			p.add(stat_switches(5,12,false));
			p.add(stat_switches(13,15,true));
			add(p);
		};
		{
			Panel p = new Panel();
			p.setLayout(new GridLayout(2,1));
			CheckboxGroup g = new CheckboxGroup();
			p.add(new CB_Checkbox("Automatic", g, false, new
				Manual_Mode_Callback(false)));
			p.add(new CB_Checkbox("Manual", g, true, new
				Manual_Mode_Callback(true)));
			ssem1.machine.set_Manual(true);
			add(p);
		};
	}
	static Outlined_Panel stat_switches(int first, int last, boolean working) {
		Stat_Switch s;
		Outlined_Panel p = new Outlined_Panel();
		for (int i = first; i <= last; i++) {
			p.add(s = new Stat_Switch(i));
			if (!working)
				s.setEnabled(false);
		}
		return p;
	};
}
class Stat_Switch extends Panel {
	public Stat_Switch(int switch_num) {
		setLayout(new GridLayout(3,1));
		Label l = new Label("S" + switch_num);
		l.setAlignment(Label.LEFT);
		add(l);
		CheckboxGroup g = new CheckboxGroup();
		add(new CB_Checkbox("", g, true, new
			Stat_Switch_Callback(switch_num, true)));
		add(new CB_Checkbox("", g, false, new
			Stat_Switch_Callback(switch_num, false)));
		ssem1.machine.set_Stat_Switch_Bit(switch_num);
	}
}
class Control_Panel extends Panel {
	public Control_Panel() {
		{
			Panel p = new Panel();
			p.setLayout(new GridLayout(3,1));
			p.add(new Label(" CS"));
			CheckboxGroup g = new CheckboxGroup();
			p.add(new CB_Checkbox("Run", g, false,
					new Start_Exec_Callback()));
			p.add(new CB_Checkbox("Stop", g, true,
					new Stop_Exec_Callback()));
			ssem1.machine.set_Prepulse(false);
			add(p);
		};
		add(new CB_Button("KC", new KSP_Callback()));;
		add(new CB_Button("KLC", new KLC_Callback()));;
		add(new CB_Button("KSC", new KSC_Callback()));;
		add(new CB_Button("KAC", new KAC_Callback()));;
		add(new CB_Button("KCC", new KCC_Callback()));;
		add(new CB_Button("KEC", new KEC_Callback()));;
		{
			Panel p = new Panel();
			p.setLayout(new GridLayout(3,1));
			p.add(new Label("Write/Erase"));
			CheckboxGroup g = new CheckboxGroup();
			p.add(new CB_Checkbox("Write", g, true, new
				Write_Flag_Callback(true)));
			p.add(new CB_Checkbox("Erase", g, false, new
				Write_Flag_Callback(false)));
			ssem1.machine.set_Write_Flag(true);
			add(p);
		};
		add(lamp = new Stop_Lamp());;
	}
	private Stop_Lamp lamp;
	public Stop_Lamp get_Lamp() {
		return lamp;
	};
}
class Stop_Lamp extends Canvas {
	private boolean lamp_is_on;
	public void paint(Graphics g) {
		if (lamp_is_on)
			g.setColor(Color.red);
		else
			g.setColor(Color.black);
		g.fillOval(0,0,Width,Width);
	}
	public void illuminate(boolean b) {
		lamp_is_on = b;
		repaint();
	};
	private final static int Width = 30;
	private final static int Height = 30;
	public Dimension getMinimumSize() {
		return new Dimension(Width, Height);
	}
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	public Dimension getSize() {
		return getMinimumSize();
	};
}
class Outlined_Panel extends Panel {
	public void paint(Graphics g) {
		super.paint(g);
		Dimension d = getSize();
		g.setColor(Color.black);
		g.drawRect(0,0,d.width-1,d.height-1);
	}
	public Insets getInsets() {
		return new Insets(1,1,1,1);
	};
}