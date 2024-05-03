package ija.tool.common;


public interface Observable {

	void addObserver(Observer o);
	void removeObserver(Observer o);
	void notifyObservers();

	public static interface Observer {
	
		void update(Observable o);
	}
}
