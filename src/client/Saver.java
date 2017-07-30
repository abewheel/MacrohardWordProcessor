package client;

public class Saver extends Thread {

	private Client client;
	
	public Saver(Client client) {
		this.client = client;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(5000);
				client.save();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
