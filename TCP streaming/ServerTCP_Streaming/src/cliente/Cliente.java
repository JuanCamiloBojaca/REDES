package cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Cliente 
{
	//puerto por el que se va a recibir
	private static final int puerto = 8080;
	private static final String IP = "localhost";
	//tamaño del buffer del cliente
	private static final int tamBuffer = 1024;
	
	public Cliente() 
	{
		
		try
		( 
			//inicio del proceso de recepción
			Socket s = new Socket(IP, puerto);
			PrintStream ps = new PrintStream(s.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			InputStream is = s.getInputStream();
		)
		{
			byte[] buffer = new byte[tamBuffer];
			int total = 0;
			for(int i = is.read(buffer); i > 0; i = is.read(buffer))
			{
				System.out.println("Cant: "+i);
				total += i;
			}
			System.out.println("Peso: "+total + "bytes");
		} 
		catch (Exception e) 
		{
			System.out.println("-------------------------------");
			System.out.println("ERROR CLIENTE: ");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println("-------------------------------");
		}
		
	}
	
	public static void main(String[] args) {
		new Cliente();
	}

}
