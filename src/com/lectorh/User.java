package com.lectorh;

public class User {
	
	int _id;
	String _name;
	byte[] _huella;
	
	public User(){
		
	}
	
	public User(int id, String name, byte[] huella){
		this._id = id;
		this._name = name;
		this._huella = huella;
	}
	
	public User(String name, byte[] huella){
		this._name = name;
		this._huella = huella;
	}
	
// =============  getters ===============================================
	public int getID(){
		return this._id;
	}
	
	public String getName(){
		return this._name;
	}
	
	public byte[] getHuella(){
		return this._huella;
	}

// =========== setters ==================================================
	
	public void setID(int id){
		this._id = id;
	}
	
	public void setName(String name){
		this._name = name;
	}
	
	public void setHuella(byte[] huella){
		this._huella = huella;
	}
}
