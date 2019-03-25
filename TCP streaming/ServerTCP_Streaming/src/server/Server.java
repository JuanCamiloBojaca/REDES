package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
	private final int puerto;
	
	public Server() 
	{
		//puerto por el que se va a transmitir
		puerto = 8080;
		//inicialización de un nuevo server socket
		try (ServerSocket ss = new ServerSocket(puerto))
		{
			System.out.println("Servidor iniciado en puerto: "+puerto);
			while(true)
			{	//inicialización de un nuevo hilo en el socket
				Socket s = ss.accept();
				new Thread(new Transmisor(s)).start();;
			}
			
		}
		catch (Exception e) 
		{
			System.out.println("-------------------------------");
			System.out.println("ERROR SERVIDOR: ");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println("-------------------------------");
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
	

}
