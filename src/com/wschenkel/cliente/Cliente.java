package com.wschenkel.cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.wschenkel.app.FileMessage;

public class Cliente {
	private Socket socket;
	private ObjectOutputStream outputStream;
	private String Savepath;
	
	public Cliente() throws IOException {
		
		this.socket = new Socket("localhost", 8080);
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		
		new Thread(new ListenerSocket(socket)).start();
		
		Menu();
	}
	
	private class ListenerSocket implements Runnable {
		
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}

		@Override
		public void run() {
			FileMessage message;
			
			try {
				while((message = (FileMessage) inputStream.readObject()) != null) {
					if (message.getComando().equals("SalvarArquivos")) {
						SalvarCliente(message);
					} else if (message.getComando().equals("DeletarArquivos")) {
						DeletarCliente(message);						
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void SalvarCliente(FileMessage message) throws IOException, InterruptedException {
		
		Savepath = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/" + message.getCliente().toString() + "/";
			
		try {
			
			FileInputStream fileInputStream = new FileInputStream(message.getFile());
			FileOutputStream fileOutputStream = new FileOutputStream(Savepath + message.getFile().getName());
			
			FileChannel fin = fileInputStream.getChannel();
			FileChannel fout = fileOutputStream.getChannel();
			
			long size= fin.size();
			fin.transferTo(0, size, fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
		
	private void DeletarCliente(FileMessage message) {
		
		String pathFolders = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/" + message.getCliente().toString() + "/";
		System.out.println("Path: " + pathFolders);
		
		File diretorio = new File(pathFolders);
		File[] arquivosDiretorio = diretorio.listFiles();
		
		for(File f : arquivosDiretorio) {
			if (f.getName().equals(message.getArquivoDeletado())) {
				System.out.println("Arquivo cliente deletado: " + f.getName());
				f.delete();
			}
		}
	}
	
	
	public void Menu() throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite o seu nome:"); 
		String nome = scanner.nextLine();
		Savepath = "/home/wschenkel/eclipse-workspace/Dropbox/src/share/";
		
		try {
			File pasta = new File(Savepath + nome);
			pasta.mkdir();
			
			outputStream.writeObject(new FileMessage(nome, "MandaArquivos"));
			
			new Thread(new ObservaArquivos(nome)).start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		int option = 0;
		while (option != -1) {
			System.out.println("Digite 1 para sair e 2 para enviar.");
			option = scanner.nextInt();
			
			if (option == 2) {
				EnviarArquivo(nome);
			} else {
				System.exit(0);
			}
		}
	} 
	
	public void EnviarArquivo(String nome) throws IOException {
		JFileChooser jfile = new JFileChooser();
		int opt = jfile.showOpenDialog(null);
		
		if (opt == JFileChooser.APPROVE_OPTION) {
			File file = jfile.getSelectedFile();
			outputStream.writeObject(new FileMessage(nome, file, "SalvarArquivos"));
		}
	}	
	
	public static void main(String[] args) throws IOException {
		new Cliente();
	}
}
