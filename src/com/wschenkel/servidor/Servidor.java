package com.wschenkel.servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.wschenkel.app.FileMessage;
import com.wschenkel.cliente.ObservaArquivos;

public class Servidor {
	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String, ObjectOutputStream> streamMap = new HashMap<String, ObjectOutputStream>();
	private String serverPath = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/server";
	
	
	public Servidor() {
		try {
			serverSocket = new ServerSocket(8080);
			System.out.println("Servidor online.. Aguardando conexoes");
			
			while(true) {
				socket = serverSocket.accept();
				new Thread(new ListenerSocket(socket)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class ListenerSocket implements Runnable {
		
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}

		@Override
		public void run() {
			FileMessage message = null;
			
			try {
				while((message = (FileMessage) inputStream.readObject()) != null) {
					if (!message.getCliente().isEmpty()) {
						streamMap.put(message.getCliente(), outputStream);
					}
					
					if (message.getComando().equals("MandaArquivos")) {
						SincronizarDiretorios(this.outputStream, message);
					} else {
						if (message.getFile() != null) {
							SalvarArquivoServer(message);
							for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
								kv.getValue().writeObject(new FileMessage(kv.getKey(), message.getFile(), "SalvarArquivos"));
							}
						} else {
							DeletarArquivoServer(message);
							for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
								System.out.println("Enviado pro socket deletarArquivos");
								System.out.println("Cliente: " + kv.getKey());
								kv.getValue().writeObject(new FileMessage(kv.getKey(), message.getArquivoDeletado(), "DeletarArquivos"));
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Cliente removido: " + message.getCliente());
				streamMap.remove(message.getCliente());
			} catch (ClassNotFoundException e) {
				System.out.println("Exception");
				 e.printStackTrace();
			}
		}	
	}
	
	public void SincronizarDiretorios(ObjectOutputStream outputStream, FileMessage message) throws IOException {
		File diretorio = new File(serverPath);
		File arrayFiles[] = diretorio.listFiles();
		int i = 0;
		for (int j = arrayFiles.length; i < j; i++) {
			outputStream.writeObject(new FileMessage(message.getCliente().toString(), arrayFiles[i], "SalvarArquivos"));
		}
	}
	
	public void SalvarArquivoServer(FileMessage message) throws IOException {
		String pathServer = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/server/";
		try {
			
			FileInputStream fileInputStream = new FileInputStream(message.getFile());
			FileOutputStream fileOutputStream = new FileOutputStream(pathServer + message.getFile().getName());
			
			FileChannel fin = fileInputStream.getChannel();
			FileChannel fout = fileOutputStream.getChannel();
			
			long size= fin.size();
			fin.transferTo(0, size, fout);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void DeletarArquivoServer(FileMessage message) {
		String pathFolders = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/server/";
				
		File diretorio = new File(pathFolders);
		File[] arquivosDiretorio = diretorio.listFiles();
		
		for(File f : arquivosDiretorio) {
			if (f.getName().equals(message.getArquivoDeletado())) {
				f.delete();
			}
		}
		
		System.out.println("Arquivo: " + message.getArquivoDeletado() + "  no server deletado!");
		
		// Deletar clientes - Provisório.
		/*for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
			pathFolders = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/" + kv.getKey() + "/";
			diretorio = new File(pathFolders);
			arquivosDiretorio = diretorio.listFiles();
			for(File f : arquivosDiretorio) {
				if (f.getName().equals(message.getArquivoDeletado())) {
					f.delete();
				}
			}
		}*/
		
	}
	
	public static void main(String[] args) {
		new Servidor();
	}
	
}
