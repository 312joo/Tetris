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
	//이미지 파일 불러오기
	java.awt.Image img1=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\1.png");
	java.awt.Image img2=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\2.png");
	java.awt.Image img3=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\3.png");
	java.awt.Image img4=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\4.png");
	java.awt.Image img5=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\5.png");
	java.awt.Image img6=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\6.png");
	java.awt.Image img7=Toolkit.getDefaultToolkit().getImage("C:\\Users\\최영주\\Desktop\\BigData\\Tetris\\src\\7.png");
	
	java.awt.Image[] imgs=new java.awt.Image[] {img1,img2,img3,img4,img5,img6,img7};
	
	public static final int WIDTH=475, HEIGHT=670, BLOCK=25;	//한 블록 크기: 25
	int presW, presH, nextW, nextH, startW=250, startH=50;	
	int cns=0, i, time=1000, score=0, level=1;
	boolean start=false, now=true, stop=false, end=false;
	boolean[][] store,temp;
	int[][][] shape;
	public int[] moveW= {-1,1,0}, moveH= {0,0,1};	//좌,우,아래
	
	private JButton btn_start=new JButton("New Game Start!");
	private JButton btn_stop=new JButton("STOP");
	private JButton btn_levelup=new JButton("▲");
	private JButton btn_leveldown=new JButton("▼");
	private JFrame frame=new JFrame("Tetris");
	private BufferStrategy buf;
	
	public Vector<Pos> vec=new Vector<Pos>();
	public List<int[][][]> list=new ArrayList<int[][][]>();
	
	//블록형태별 각 방향별 x, y 위치값 설정
	int[][][] Lshape= {{{0,1},{0,0},{1,0},{2,0}},{{-1,0},{0,0},{0,1},{0,2}},{{-2,0},{-1,0},{0,0},{0,-1}},{{1,0},{0,0},{0,-1},{0,-2}}},
			Zshape={{{-1,-1},{0,-1},{0,0},{1,0}},{{0,1},{0,0},{1,0},{1,-1}},{{-1,0},{0,0},{0,1},{1,1}},{{-1,1},{-1,0},{0,0},{0,-1}}},
			MirLshape={{{0,-1},{0,0},{1,0},{2,0}},{{0,2},{0,1},{0,0},{1,0}},{{-2,0},{-1,0},{0,0},{0,1}},{{-1,0},{0,0},{0,-1},{0,-2}}},
			Sshape={{{-1,0},{0,0},{0,-1},{1,-1}},{{0,-1},{0,0},{1,0},{1,1}},{{-1,1},{0,1},{0,0},{1,0}},{{-1,-1},{-1,0},{0,0},{0,1}}},
			Lineshape={{{-2,0},{-1,0},{0,0},{1,0}},{{0,-2},{0,-1},{0,0},{0,1}},{{-1,0},{0,0},{1,0},{2,0}},{{0,-1},{0,0},{0,1},{0,2}}},
			Squrshape={{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}},{{1,0},{0,0},{0,-1},{1,-1}}},
			Tshape={{{-1,0},{0,0},{1,0},{0,-1}},{{0,1},{0,0},{0,-1},{1,0}},{{-1,0},{0,0},{0,1},{1,0}},{{-1,0},{0,0},{0,1},{0,-1}}};

	
	TetrisMain(){
		//ArrayList에 각 형태의 위치값 삽입
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
			
			//BufferStrategy 4장
			if(buf==null) {
				createBufferStrategy(4);
				continue;
			}

				//첫 장- 기본셋팅
			Graphics g1=(Graphics) buf.getDrawGraphics();
			render(g1);
			
			if(start) {
				
				if(!end) {
					//둘째 장- 현재 블럭
					Graphics g2=(Graphics) buf.getDrawGraphics();
					update(g2);
				}
				
				//셋째 장- 쌓인 블럭
			Graphics g3=(Graphics) buf.getDrawGraphics();
			stored(g3);
			
			}

			getScore();
			
				//넷째 장 - 시작점 막히면 종료글자 띄우기
			if(store[0][11]) {
				Graphics g4=(Graphics) buf.getDrawGraphics();
				//종료글자-PINK
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
		
		//위치값 한 칸 내리기
		if(stop) continue;
		presW+=moveW[2]*BLOCK;	presH+=moveH[2]*BLOCK;
		
			}
	}
	
	
	public void render(Graphics g) {
		
		//배경-BLACK
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH,HEIGHT);
		
		//글자-WHITE
		g.setFont(new Font("Ink Free",Font.PLAIN,22));
		g.setColor(Color.WHITE);
		g.drawString("*UP: Rotate  *SPACE: Put Down", 80, HEIGHT-40);
		g.drawLine(0,50, WIDTH, 50);
		g.drawLine(0, HEIGHT-70, WIDTH, HEIGHT-70);
		g.drawString("Your Score: "+score, 10, 80);
		g.drawString("Level: "+level, 360, 80);

	}
	
	
	public void update(Graphics g) {
		
		//새 블록 셋팅
		if(now) {
				i=(int)Math.floor(Math.random()*7);
				presW=startW;	presH=startH;
				shape=list.get(i);
		}
		
		now=false;
		for(int k=0;k<22;k++)
		Arrays.fill(temp[k], false);
		
		
		//그리기
		for(int j=0;j<4;j++){
			
			nextW=presW+shape[cns][j][0]*BLOCK;
			nextH=presH+shape[cns][j][1]*BLOCK;
			
			int a=(nextH-startH)/BLOCK;	int b=(nextW/BLOCK);
			
			if(a<0) continue;
			
			temp[a][b]=true;
			
			//걸리는지 확인 후
			if(a>=21) now=true;
			else if(store[a+1][b]) now=true;
						
			g.drawImage(imgs[i],nextW,nextH,BLOCK,BLOCK,this);
		}
		
		//걸린게 없으면 저장하지 않음
		if(!now) return ;
		
		
		//저장
		for(int j=0;j<22;j++) for(int k=0;k<20;k++) 
		if(temp[j][k]) {
			store[j][k]=true;
			vec.addElement(new Pos(k*BLOCK,j*BLOCK+startH,i));
		}
	}
		
	
	//이미 쌓여있는 블록들 그리기
	public void stored(Graphics g) {
		
		for(int j=0;j<vec.size();j++) {
			int i=vec.get(j).i;	int nextW=vec.get(j).x;	int nextH=vec.get(j).y;
			g.drawImage(imgs[i],nextW,nextH,BLOCK,BLOCK,this);
		}
	}
	
	
	//가득쌓인 줄이 있는지 확인 후 점수 올리고, 블록 위치값 재정리
	public void getScore() {
	
		boolean[][] temp_move=new boolean[22][20];
		boolean reset=false;
		
		for(int j=0;j<22;j++) {
			//초기화
			int cnt=0; 
			
			
			
			for(int k=0;k<20;k++) if(store[j][k]) cnt++;
			
			
			//한 줄이 전부 true이면 점수올림 & 재정리
			if(cnt==19) {
				for(int k=0;k<22;k++) 
					Arrays.fill(temp_move[k], false);
				
				score++; reset=true;
				
				//재정리
				for(int z=vec.size()-1;z>=0;z--) {
					int y=(vec.get(z).y-startH)/BLOCK;	int x=vec.get(z).x/BLOCK;	
					
					if(y==j) 	//지우는 줄은 삭제
						vec.removeElementAt(z);
					
					
					else if(y<j)	{	//윗줄은 위치조정
						vec.get(z).setY(y*BLOCK+startH+BLOCK);
						temp_move[y+1][x]=true;
					}
					
					else {	//아랫줄은 그대로
						temp_move[y][x]=true;
					}
		
				}
				cnt=0;
				
			}
			
		}
		
		//재정리한 데이터를 복사해줌
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
			
				//왼쪽 키보드
			if(KeyEvent.getKeyText(e.getKeyCode()).equals("Left")) {
				boolean doit=true;
				for(int j=0;j<4;j++) {
					int nextW=presW+(shape[cns][j][0]+moveW[0])*BLOCK;
					int nextH=presH+(shape[cns][j][1]+moveH[0])*BLOCK;
					
					if(nextW<0)	doit=false;	//왼쪽이 벽이라면 이동안함
					else if(nextH<startH) continue;	//시작포인트에서 블록이 다 나타나지 않은 상태에서 누른경우 해당 블록은 건너띰 
					else if(store[(presH-startH)/BLOCK][nextW/BLOCK]) doit=false;	//블록이 이미 쌓여있으면 이동안함
				}
				if(doit) { //블록 왼쪽으로 한 칸 이동
						presW+=moveW[0]*BLOCK;
						presH+=moveH[0]*BLOCK;
				}
				
				//오른쪽 키보드
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Right")) {
				boolean doit=true;
				for(int j=0;j<4;j++) {
					int nextW=presW+(shape[cns][j][0]+moveW[1])*BLOCK;
					int nextH=presH+(shape[cns][j][1]+moveH[1])*BLOCK;
					
					if(nextW>=475) doit=false;	//오른쪽이 벽이라면 이동안함
					else if(nextH<startH) continue;	//시작포인트에서 블록이 다 나타나지 않은 상태에서 누른경우 해당 블록은 건너띰
					else if(store[(nextH-startH)/BLOCK][nextW/BLOCK]) doit=false;	//블록이 이미 쌓여있으면 이동안함
				}
				if(doit) {	//블록 오른쪽으로 한 칸 이동
						presW+=moveW[1]*BLOCK;
						presH+=moveH[1]*BLOCK;
				}
	
					//아래쪽 키보드
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Down")) {
												//블록 아래쪽 한 칸 이동
						presW+=moveW[2]*BLOCK;
						presH+=moveH[2]*BLOCK;
				
						
					//윗쪽 키보드-방향변환
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Up")) {
				
				int cns=this.cns;
				for(int j=0;j<4;j++){
					
					cns++;
					if(cns==4) cns=0;
					
					int nextW=presW+shape[cns][j][0]*BLOCK;
					int nextH=presH+shape[cns][j][1]*BLOCK;
					
					if(nextW<0 | nextH<0 | nextW>=475 | nextH>=HEIGHT) return ;	//벽에 걸리는 경우 변환 안됨
					
					
				}	
						this.cns++;
						if(this.cns==4) this.cns=0;
			
						
					//스페이스바 키보드 -바닥에 곧바로 쌓기
			}else if(KeyEvent.getKeyText(e.getKeyCode()).equals("Space")) {
				
				boolean yn=false;
				
				//바닥이나 블록에 걸릴 때까지 아래로 이동
				while(true) {
				for(int k=0;k<22;k++)
					Arrays.fill(temp[k], false);
					
				presW+=moveW[2]*BLOCK;
				presH+=moveH[2]*BLOCK;
				
					//그리기
					for(int j=0;j<4;j++){
						nextW=presW+shape[cns][j][0]*BLOCK;
						nextH=presH+shape[cns][j][1]*BLOCK;
						
						int a=(nextH-startH)/BLOCK;	int b=(nextW/BLOCK);
					
						temp[a][b]=true;
						
						//걸리는지 확인 후
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
		
				//시작버튼 - 리셋
		if(e.getActionCommand().equals(btn_start.getActionCommand())){
			
			start=true; 
			end=false;
			vec.removeAllElements();
			now=true;
			score=0;
			for(int j=0;j<22;j++)
			Arrays.fill(store[j], false);
			
			
				//멈춤버튼
		}else if(e.getActionCommand().equals(btn_stop.getActionCommand())) {
			
			if(stop) stop=false;	
			else stop=true;
			
			
				//레벨조정
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
