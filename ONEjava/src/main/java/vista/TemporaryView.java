package vista;

import java.util.Scanner;

import modello.carte.Colore;

public class TemporaryView {

	private Scanner sc;
	
	public TemporaryView() {
		sc=new Scanner(System.in);
	}
	
	public void closeScanner() {
		sc.close();
	}
	
	public void printMessage(String s) {
		System.out.println(s);
	}
	
	public String chooseName(String message) {
		this.printMessage(message);
		return sc.nextLine();
	}
	
	public int chooseBetweenTwo(String message, String optionZero, String optionOne) {
		int answer = -1;
		do { 
			try {
				System.out.print(message + ": "+optionZero+"->0 , "+optionOne+"->1");
				answer = Integer.parseInt(sc.nextLine());
			}
			catch(NumberFormatException exception) {
				this.printMessage("Valore non valido! Inserisci 0 o 1!");
			}
		} while (answer < 0 || answer > 1);

		return answer;
	}
	
	public int chooseBetweenN(String message, int minValue, int maxValue) {
		int answer = 0;
		boolean loop = true;
		
		do {
			loop = false;
			try {
				System.out.print(message + " (" + minValue + "-" + maxValue + "): ");
				answer = Integer.parseInt(sc.nextLine());
				
				if(answer < minValue || answer > maxValue) {
					this.printMessage("Valore non valido! Inserisci un valore compreso tra " + minValue + " e " + maxValue + "!");
					loop = true;
				}
			}
			catch(NumberFormatException exception) {
				this.printMessage("Valore non valido! Inserisci un valore compreso tra " + minValue + " e " + maxValue + "!");
				loop = true;
			}
		}while(loop);
		
		return answer;
	}
	
	public Colore chooseColor() {
		int index = chooseBetweenN("Scegli un colore: 0->Rosso, 1->Blu, 2->Giallo, 3->Verde", 0, 3);
		return Colore.values()[index];
	}
}
