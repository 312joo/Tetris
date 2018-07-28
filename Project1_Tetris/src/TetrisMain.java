import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;


public class TetrisMain extends Canvas implements Runnable, KeyListener, ActionListener{
	//�̹��� ���� �ҷ�����
	java.awt.Image img1=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\1.png");
	java.awt.Image img2=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\2.png");
	java.awt.Image img3=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\3.png");
	java.awt.Image img4=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\4.png");
	java.awt.Image img5=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\5.png");
	java.awt.Image img6=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\6.png");
	java.awt.Image img7=Toolkit.getDefaultToolkit().getImage("C:\\Users\\�ֿ���\\Desktop\\BigData\\Tetris\\src\\7.png");
	
	java.awt.Image[] imgs=new java.awt.Image[] {img1,img2,img3,img4,img5,img6,img7};
	
	public static final int WIDTH=475, HEIGHT=670, BLOCK=25;	//�� ��� ũ��: 25
	int presW, presH, nextW, nextH, startW=250, startH=50;	
	int cns=0, i, time=1000, score=0, level=1;
	boolean start=false, now=true, stop=false, end=false;
	boolean[][] store,temp;
	int[][][] shape;
	public int[] moveW= {-1,1,0}, moveH= {0,0,1};	//��,��,�Ʒ�
	
	private JButton btn_start=new JButton("New Game Start!");
	private JButton btn_stop=new JButton("STOP");
	private JButton btn_levelup=new JButton("��");
	private JButton btn_leveldown=new JButton("��");
	private JFrame frame=new JFrame("Tetris");
	private BufferStrategy buf;
	
	public Vector<Pos> vec=new Vector<Pos>();
	public List<int[][][]> list=new ArrayList<int[][][]>();
	
	//������º� �� ���⺰ x, y ��ġ�� ����
	int[][][] Lshape= {{{0,1},{0,0},{1,0},{2,0}},{{-1,0},{0,0},{0,1},{0,2}},{{-2,0},{-1,0},{0,0},{0,-1}},{{1,0},{0,0},{0,-1},{0,-2}}},
			Zshape={{{-1,-1},{0,-1},{0,0},{1,0}},{{0,1},{0,0},{1,0},{1,-1}},{{-1,0},{0,0},{0,1},{1,1}},{{-1,1},{-1,0},{0,0},{0,-1}}},
			MirLshape={{{0,-1},{0,0},{1,0},{2,0}},{{0,2},{0,1},{0,0},{1,0}},{{-2,0},{-1,0},{0,0},{0,1}},{{-1,0},{0,0},{0,-1},{0,-2}}},
			Sshape={{{-1,0},{0,0},{0,-1},{1,-1}},{{0,-1},{0,0},{1,0},{1,1}},{{-1,1},{0,1},{0,0},{1,0}},{{-1,-1},{-1,0},{0,0},{0,1}}},
			Lineshape={{{-2,0},{-1,0},{0,0},{1,0}},{{0,-2},{0,-1},{0,0},{0,1}},{{-1,0},{0,0},{1,0},{2,0}},{{0,-1},{0,0},{0,1},{0,2}}},
			Squrshape={{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}}},
			Tshape={{{-1,0},{0,0},{1,0},{0,-1}},{{0,1},{0,0},{0,-1},{1,0}},{{-1,0},{0,0},{0,1},{1,0}},{{-1,0},{0,0},{0,1},{0,-1}}};

	
	TetrisMain(){
		//ArrayList�� �� ������ ��ġ�� ����
		list.add(Lshape);
		list.add(Zshape);
		list.add(MirLshape);
		list.add(Sshape);
		list.add(Lineshape);
		list.add(Squrshape);
		list.add(Tshape);
		
		store=new boolean[22][20];	temp=new boolean[22][20];
		
		//JFrame
		frame.setSize(WIDTH,HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		frame.add(btn_start);
		frame.add(btn_stop);
		frame.add(btn_levelup);
		frame.add(btn_leveldown);
		frame.add(this);
		frame.setVisible(true);
		
		this.init();
		this.start();
		
	}
		
	public void init() {
		
		//Button
		btn_start.setFont(new Font("Ink Free",Font.BOLD,20));
		btn_stop.setFont(new Font("Ink Free",Font.PLAIN,20));
		btn_start.setBounds(20,10,200,35);
		btn_stop.setBounds(240,10,90,35);
		btn_levelup.setBounds(350, 10, 50, 35);
		btn_leveldown.setBounds(410, 10, 50, 35);
		
	}
	
	public void start() {
		
		//Thread
		Thread t=new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
		
		//Event
		this.addKeyListener(this);
		btn_start.addActionListener(this);
		btn_stop.addActionListener(this);
		btn_levelup.addActionListener(this);
		btn_leveldown.addActionListener(this);
		
		while(true) {
			
			buf=getBufferStrategy();
			
			//BufferStrategy 4��
			if(buf==null) {
				createBufferStrategy(4);
				continue;
			}

				//ù ��- �⺻����
			Graphics g1=(Graphics) buf.getDrawGraphics();
			render(g1);
			
			if(start) {
				
				if(!end) {
					//��° ��- ���� ��
					Graphics g2=(Graphics) buf.getDrawGraphics();
					update(g2);
				}
				
				//��° ��- ���� ��
			Graphics g3=(Graphics) buf.getDrawGraphics();
			stored(g3);
			
			}

			getScore();
			
				//��° �� - ������ ������ ������� ����
			if(store[0][11]) {
				Graphics g4=(Graphics) buf.getDrawGraphics();
				//�������-PINK
				g4.setFont(new Font("Ink Free",Font.ROMAN_BASELINE,100));
				g4.setColor(Color.PINK);
				g4.drawString("The End", 40, 250);
				end=true;
			}
			
			buf.show();
		}
	}
	
	//Thread start
	public void run() {
				
		while(true) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��ġ�� �� ĭ ������
		if(stop) continue;
		presW+=moveW[2]*BLOCK;	presH+=moveH[2]*BLOCK;
		
			}
	}
	
	
	public void render(Graphics g) {
		
		//���-BLACK
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH,HEIGHT);
		
		//����-WHITE
		g.setFont(new Font("Ink Free",Font.PLAIN,22));
		g.setColor(Color.WHITE);
		g.drawString("*UP: Rotate  *SPACE: Put Down", 80, HEIGHT-40);
		g.drawLine(0,50, WIDTH, 50);
		g.drawLine(0, HEIGHT-70, WIDTH, HEIGHT-70);
		g.drawString("Your Score: "+score, 10, 80);
		g.drawString("Level: "+level, 360, 80);

	}
	
	
	public void update(Graphics g) {
		
		//�� ��� ����
		if(now) {
				i=(int)Math.floor(Math.random()*7);
				presW=startW;	presH=startH;
				shape=list.get(i);
		}
		
		now=false;
		for(int k=0;k<22;k++)
		Arrays.fill(temp[k], false);
		
		
		//�׸���
		for(int j=0;j<4;j++){
			
			nextW=presW+shape[cns][j][0]*BLOCK;
			nextH=presH+shape[cns][j][1]*BLOCK;
			
			int a=(nextH-startH)/BLOCK;	int b=(nextW/BLOCK);
			
			if(a<0) continue;
			
			temp[a][b]=true;
			
			//�ɸ����� Ȯ�� ��
			if(a>=21) now=true;
			else if(store[a+1][b]) now=true;
						
			g.drawImage(imgs[i],nextW,nextH,BLOCK,BLOCK,this);
		}
		
		//�ɸ��� ������ �������� ����
		if(!now) return ;
		
		
		//����
		for(int j=0;j<22;j++) for(int k=0;k<20;k++) 
		if(temp[j][k]) {
			store[j][k]=true;
			vec.addElement(new Pos(k*BLOCK,j*BLOCK+startH,i));
		}
	}
		
	
	//�̹� �׿��ִ� ��ϵ� �׸���
	public void stored(Graphics g) {
		
		for(int j=0;j<vec.size();j++) {
			int i=vec.get(j).i;	int nextW=vec.get(j).x;	int nextH=vec.get(j).y;
			g.drawImage(imgs[i],nextW,nextH,BLOCK,BLOCK,this);
		}
	}
	
	
	//������� ���� �ִ��� Ȯ�� �� ���� �ø���, ��� ��ġ�� ������
	public void getScore() {
	
		boolean[][] temp_move=new boolean[22][20];
		boolean reset=false;
		
		for(int j=0;j<22;j++) {
			//�ʱ�ȭ
			int cnt=0; 
			
			
			
			for(int k=0;k<20;k++) if(store[j][k]) cnt++;
			
			
			//�� ���� ���� true�̸� �����ø� & ������
			if(cnt==19) {
				for(int k=0;k<22;k++) 
					Arrays.fill(temp_move[k], false);
				
				score++; reset=true;
				
				//������
				for(int z=vec.size()-1;z>=0;z--) {
					int y=(vec.get(z).y-startH)/BLOCK;	int x=vec.get(z).x/BLOCK;	
					
					if(y==j) 	//����� ���� ����
						vec.removeElementAt(z);
					
					
					else if(y<j)	{	//������ ��ġ����
						vec.get(z).setY(y*BLOCK+startH+BLOCK);
						temp_move[y+1][x]=true;
					}
					
					else {	//�Ʒ����� �״��
						temp_move[y][x]=true;
					}
		
				}
				cnt=0;
				
			}
			
		}
		
		//�������� �����͸� ��������
		if(reset) {
			for(int k=0;k<22;k++) 
				Arrays.fill(store[k], false);
			for(int k=0;k<22;k++) 
				store[k]=Arrays.copyOf(temp_move[k],temp_move[k].length);
		}
		
		
		
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
			
				//���� Ű����
			if(KeyEvent.getKeyText(e.getKeyCode()).equals("Left")) {
				boolean doit=true;
				for(int j=0;j<4;j++) {
					int nextW=presW+(shape[cns][j][0]+moveW[0])*BLOCK;
					int nextH=presH+(shape[cns][j][1]+moveH[0])*BLOCK;
					
					if(nextW<0)	doit=false;	//������ ���̶�� �̵�����
					else if(nextH<startH) continue;	//��������Ʈ���� ����� �� ��Ÿ���� ���� ���¿��� ������� �ش� ����� �ǳʶ� 
					else if(store[(presH-startH)/BLOCK][nextW/BLOCK]) doit=false;	//����� �̹� �׿������� �̵�����
				}
				if(doit) { //��� �������� �� ĭ �̵�
						presW+=moveW[0]*BLOCK;
						presH+=moveH[0]*BLOCK;
				}
				
				//������ Ű����
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Right")) {
				boolean doit=true;
				for(int j=0;j<4;j++) {
					int nextW=presW+(shape[cns][j][0]+moveW[1])*BLOCK;
					int nextH=presH+(shape[cns][j][1]+moveH[1])*BLOCK;
					
					if(nextW>=475) doit=false;	//�������� ���̶�� �̵�����
					else if(nextH<startH) continue;	//��������Ʈ���� ����� �� ��Ÿ���� ���� ���¿��� ������� �ش� ����� �ǳʶ�
					else if(store[(nextH-startH)/BLOCK][nextW/BLOCK]) doit=false;	//����� �̹� �׿������� �̵�����
				}
				if(doit) {	//��� ���������� �� ĭ �̵�
						presW+=moveW[1]*BLOCK;
						presH+=moveH[1]*BLOCK;
				}
	
					//�Ʒ��� Ű����
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Down")) {
												//��� �Ʒ��� �� ĭ �̵�
						presW+=moveW[2]*BLOCK;
						presH+=moveH[2]*BLOCK;
				
						
					//���� Ű����-���⺯ȯ
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Up")) {
				
				int cns=this.cns;
				for(int j=0;j<4;j++){
					
					cns++;
					if(cns==4) cns=0;
					
					int nextW=presW+shape[cns][j][0]*BLOCK;
					int nextH=presH+shape[cns][j][1]*BLOCK;
					
					if(nextW<0 | nextH<0 | nextW>=475 | nextH>=HEIGHT) return ;	//���� �ɸ��� ��� ��ȯ �ȵ�
					
					
				}	
						this.cns++;
						if(this.cns==4) this.cns=0;
			
						
					//�����̽��� Ű���� -�ٴڿ� ��ٷ� �ױ�
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Space")) {
				
				boolean yn=false;
				
				//�ٴ��̳� ��Ͽ� �ɸ� ������ �Ʒ��� �̵�
				while(true) {
				for(int k=0;k<22;k++)
					Arrays.fill(temp[k], false);
					
				presW+=moveW[2]*BLOCK;
				presH+=moveH[2]*BLOCK;
				
					//�׸���
					for(int j=0;j<4;j++){
						nextW=presW+shape[cns][j][0]*BLOCK;
						nextH=presH+shape[cns][j][1]*BLOCK;
						
						int a=(nextH-startH)/BLOCK;	int b=(nextW/BLOCK);
					
						temp[a][b]=true;
						
						//�ɸ����� Ȯ�� ��
						if(a>=21) yn=true;
						else if(store[a+1][b]==true) yn=true;
														
					}
					
					if(!yn) continue ;
					return;	
				}
			}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
				//���۹�ư - ����
		if(e.getActionCommand().equals(btn_start.getActionCommand())){
			
			start=true; 
			end=false;
			vec.removeAllElements();
			now=true;
			score=0;
			for(int j=0;j<22;j++)
			Arrays.fill(store[j], false);
			
			
				//�����ư
		}else if(e.getActionCommand().equals(btn_stop.getActionCommand())) {
			
			if(stop) stop=false;	
			else stop=true;
			
			
				//��������
		}else if(e.getActionCommand().equals(btn_levelup.getActionCommand())) {
			//Level 1~5 
			
			if(time<250) return ;
			else if(time==250) {
				time=100;
				level++;
				return ;
			}
			time-=250; level++;
			
		}else if(e.getActionCommand().equals(btn_leveldown.getActionCommand())) {
			
			if(time>=1000) return ;
			else if(time==100) {
				time=250;
				level--;
				return ;
			}
			time+=250;	level--;
		}
	}

	public static void main(String[] ar) {
		
		new TetrisMain();
	}
	
}
