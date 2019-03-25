package server;

import java.io.*;
import java.net.*;

public class Transmisor implements Runnable 
{

	private Socket cliente;
	//descripcion del archivo a transmitir
	private static final String RUTA = "data/Auxilio Me Desmayo, Profe Paseme El Año.mp4";
	//descripcion del tamaño del buffer que tiene el transmisor
	private static final int tamBuffer = 1024;
	
	//inicializacion del transmision son el socket cliente
	public Transmisor(Socket cliente) {
		this.cliente = cliente;
	}

	@Override
	public void run() {

		try 
		(
			//inicio del proceso de transmisión
			Socket cliente = this.cliente;
			PrintStream ps = new PrintStream(cliente.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			FileInputStream fis = new FileInputStream( new File( RUTA ) )
		)
		{
			byte[] buffer = new byte[tamBuffer];
			for(int i = fis.read(buffer); i > 0; i = fis.read(buffer))
				ps.write(buffer, 0, i);
		}
		catch (Exception e) 
		{
			System.out.println("-------------------------------");
			System.out.println("ERROR TRANSMISOR:");
			System.out.println(e.getMessage());
			System.out.println(e.getClass());
			System.out.println("-------------------------------");
		}

	}

}
