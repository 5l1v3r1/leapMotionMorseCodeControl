
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.leapmotion.leap.*;

class SampleListener extends Listener {    
	ArrayList < JPanel > components = new ArrayList < JPanel >();
	private static final Map<String, Integer> morse = new HashMap<String, Integer>();

    private static final int SIZE = 13;
    private static final double Y_MIN = 80;
    private static final double Y_MAX = 160;
    private static final double X_MIN = -40;
    private static final double X_MAX = 40;

    private String str = "";
    private String state = "N";

	private Robot robot;
	
	public SampleListener() throws AWTException {
  	  MyFrame myframe = new MyFrame();
      myframe.setSize( 500, 500 );
      myframe.setResizable( true );
      myframe.setLocationRelativeTo( null );
      myframe.setLayout( new GridLayout(SIZE,SIZE) );

      Container container = myframe.getContentPane();
      JPanel temp = null;

      for ( int i = 0; i < SIZE; i++ ) {
    	  for (int j = 0; j < SIZE; j++) {
    		  temp = new JPanel();
    		  temp.setSize( 200,200 );
    		  components.add( temp );
    		  container.add(temp);
    	  }
      }

      myframe.pack();
      myframe.setVisible( true );
      
      robot = new Robot();
	}


	public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }
    
    static {
    	morse.put("1111", KeyEvent.VK_SPACE);
    	morse.put("1110", KeyEvent.VK_BACK_SPACE);
    	morse.put("01", KeyEvent.VK_A);
    	morse.put("1000", KeyEvent.VK_B);
    	morse.put("1010", KeyEvent.VK_C);
    	morse.put("100", KeyEvent.VK_D);
    	morse.put("0", KeyEvent.VK_E);
    	morse.put("0010", KeyEvent.VK_F);
    	morse.put("110", KeyEvent.VK_G);
    	morse.put("0000", KeyEvent.VK_H);
    	morse.put("00", KeyEvent.VK_I);
    	morse.put("0111", KeyEvent.VK_J);
    	morse.put("101", KeyEvent.VK_K);
    	morse.put("0100", KeyEvent.VK_L);
    	morse.put("11", KeyEvent.VK_M);
    	morse.put("10", KeyEvent.VK_N);
    	morse.put("111", KeyEvent.VK_O);
    	morse.put("0110", KeyEvent.VK_P);
    	morse.put("1101", KeyEvent.VK_Q);
    	morse.put("010", KeyEvent.VK_R);
    	morse.put("000", KeyEvent.VK_S);
    	morse.put("1", KeyEvent.VK_T);
    	morse.put("001", KeyEvent.VK_U);
    	morse.put("0001", KeyEvent.VK_V);
    	morse.put("1011", KeyEvent.VK_Y);
    	morse.put("011", KeyEvent.VK_W);
    	morse.put("1001", KeyEvent.VK_X);
    	morse.put("1100", KeyEvent.VK_Z);
    }

    private void reset() {
        for (int i = 0; i < SIZE*SIZE; i++) {
        	components.get(i).setBackground(Color.BLACK);
        }

    }
    
    private void setState(String s) {
    	state = s;
    }
    
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        
        if (frame.fingers().count() != 1) {
        	return;
        }

        final Finger finger = frame.fingers().get(0);
        final double fingerTipY = finger.tipPosition().getY();
        final double fingerTipX = finger.tipPosition().getX();

        
        double y = -1;
        if (fingerTipY > Y_MIN && fingerTipY < Y_MAX) {
        	y = 1 - (fingerTipY - Y_MIN)/(Y_MAX - Y_MIN);
        }
        
        double x = -1;
        if (fingerTipX > X_MIN && fingerTipX < X_MAX) {
        	x = (fingerTipX - X_MIN)/(X_MAX - X_MIN);
        }
        
        int xIndex = (int)(SIZE*x);
        int yIndex = (int)(SIZE*y);
        int index = xIndex + yIndex*SIZE;
        
        reset();
        components.get(SIZE/2 + SIZE/2*SIZE).setBackground(Color.GREEN);
        if (xIndex > 0 && yIndex > 0) {
        	components.get(index).setBackground(Color.BLUE);
        }
        
        int sleepTime = 5;
        try {
        	if (fingerTipY > Y_MAX) {
            	components.get( SIZE/2 ).setBackground( Color.RED );
            	if (state == "B") {return;}

            	if (str == "") {
            		str = "1110";
            		robot.keyPress(morse.get(str));
            		robot.keyRelease(morse.get(str));
            	}
            	str = "";
            	System.out.println("");
            	setState("B");
            	Thread.sleep(sleepTime);
            } else if (fingerTipY < Y_MIN) {
            	components.get( SIZE/2 + (SIZE - 1)*SIZE ).setBackground( Color.RED );
            	if (state == "N") {return;}
            	setState("N");
            	
            	if (str == "") {
            		str = "1111";
            	}
            	
            	if (morse.containsKey(str)) {
            		System.out.println(" " + KeyEvent.getKeyText(morse.get(str)));
            		robot.keyPress(morse.get(str));
            		robot.keyRelease(morse.get(str));
            	} else {
            		System.out.println("!");
            	}
            	str = "";
            	Thread.sleep(sleepTime);
	        } else if (fingerTipX < X_MIN) {
	        	components.get( SIZE*(SIZE/2) ).setBackground( Color.RED );
	        	if (state == "0") {return;}
	        	setState("0");
	        	str += "0";
	        	System.out.print("0");
			    Thread.sleep(sleepTime);
	        } else if (fingerTipX > X_MAX) {
	        	components.get( SIZE - 1 + SIZE*(SIZE/2) ).setBackground( Color.RED );
	        	if (state == "1") {return;}
	        	setState("1");
	        	System.out.print("1");
	        	str += "1";
	        	Thread.sleep(sleepTime);
	        } else {
	        	state = "M";
	        }
    } catch (InterruptedException e) {
    	// TODO Auto-generated catch block
    	e.printStackTrace();
    } 
        
    }
}

class Sample {
    public static void main(String[] args) throws AWTException {          
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();
        controller.addListener(listener);

        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }
}
