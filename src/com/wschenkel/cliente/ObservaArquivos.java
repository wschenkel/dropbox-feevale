package com.wschenkel.cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import com.wschenkel.app.FileMessage;

public class ObservaArquivos implements Runnable {
	
	private String nomeCliente;
	private Path caminhoPastaCliente;
	private Path nomeArquivoAlterado;
	private Socket socket;
	private ObjectOutputStream outputStream;
	
	public ObservaArquivos(String cliente) throws IOException {
		this.nomeCliente = cliente;
		AbreConexaoServer();
	}
	
	public void AbreConexaoServer() {
		try {
			this.socket = new Socket("localhost", 8080);
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			caminhoPastaCliente = Paths.get("/home/wschenkel/eclipse-workspace/Dropbox/src/share/" + nomeCliente);
			WatchService watchService =  caminhoPastaCliente.getFileSystem().newWatchService();
			
			caminhoPastaCliente.register(watchService, StandardWatchEventKinds.ENTRY_DELETE);
			
			WatchKey watchKey = null;
			while (true) {
			    watchKey = watchService.poll(10, TimeUnit.MINUTES);
			    
			    if (watchKey != null) {
			    	
			        watchKey.pollEvents().stream().forEach(event -> {
			        	
			        	if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			        		nomeArquivoAlterado = (Path) event.context();
			        		
			        		try {
			        			outputStream.writeObject(new FileMessage("", nomeArquivoAlterado.toString(), ""));
			        		} catch (Exception e) {
								e.printStackTrace();
							}
			        	}
			        });
			    }
			    
			    watchKey.reset();
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
