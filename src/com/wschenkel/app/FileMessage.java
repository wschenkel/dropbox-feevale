package com.wschenkel.app;

import java.io.File;
import java.io.Serializable;

public class FileMessage implements Serializable {
	private String cliente;
	private File file;
	private String comando;
	private String arquivoDeletado;
	
	
	public FileMessage(String cliente, File file, String comando) {
		this.cliente = cliente;
		this.file = file;
		this.comando = comando;
	}
	 
	public FileMessage(String cliente, String arquivoDeletado, String comando) {
		this.cliente = cliente;
		this.arquivoDeletado = arquivoDeletado;
		this.comando = comando;
	}
	
	public FileMessage(String cliente, String comando) {
		this.cliente = cliente;
		this.comando = comando;
	}
	
	public FileMessage() {};
	
	public String getCliente() {
		return cliente;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getComando() {
		return comando;
	}
	
	public String getArquivoDeletado() {
		return arquivoDeletado;
	}
}
