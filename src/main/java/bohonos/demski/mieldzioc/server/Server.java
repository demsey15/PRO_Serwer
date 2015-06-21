package bohonos.demski.mieldzioc.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import bohonos.demski.mieldzioc.repositories.SurveyHandler;
import bohonos.demski.mieldzioc.repositories.SurveysRepository;
import bohonos.demski.mieldzioc.repositories.Workers;

public class Server {
	public final static int PORT = 8046;
	
	
	private Workers workers = new Workers();
	private SurveyHandler surveyHandler = new SurveyHandler();
	private SurveysRepository surveysRepository = new SurveysRepository();
	
	public SurveysRepository getSurveysRepository() {
		return surveysRepository;
	}

	public SurveyHandler getSurveyHandler() {
		return surveyHandler;
	}

	public Workers getWorkers() {
		return workers;
	}
	
	

	public static void main(String[] args) {
		Server server = new Server();
		try {
			
			ServerSocket s = new ServerSocket(Server.PORT);
			System.out.println(s.getInetAddress());
			System.out.println(InetAddress.getLocalHost());
			
			while(true){
				Socket incoming = s.accept();
				System.out.println("Po³¹czono z: " + incoming.getInetAddress().getHostAddress());
				Thread t = new Thread(new ConnectionHandler(incoming,
						server.getWorkers(), server.getSurveyHandler(), server.getSurveysRepository()));
				t.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
			
		
		
	}

}
