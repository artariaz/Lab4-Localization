package localization;

public class USLocalizer extends Thread {
		
		Navigator nav;
	public USLocalizer(Navigator nav){
		this.nav = nav;
	}
	
	public void run(){
		nav.start();
		nav.state = Navigator.State.ROTATE;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.state = Navigator.State.IDLE;
	
	}
}
