package localization;

public class USLocalizer extends Thread {
		
		Navigator nav;
	public USLocalizer(Navigator nav){
		this.nav = nav;
	}
	
	public void run(){
		nav.start();
		
		while(true){
		nav.setState(Navigator.State.ROTATE);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.setState(Navigator.State.IDLE);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
}
