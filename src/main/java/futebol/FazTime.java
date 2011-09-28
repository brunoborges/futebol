package futebol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FazTime {

	static final int JOGADORES_POR_TIME = 2;
	static List<Jogador> jogadores = new ArrayList<Jogador>();
	static List<Time> times = new ArrayList<Time>();
	static char[] alfabeto = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static double forcaMedia;

	private static void inicializaJogadores() {
		jogadores.clear();

		jogadores.add(new Jogador("Alex Souza", 3));
		jogadores.add(new Jogador("André Guedes", 2));
		jogadores.add(new Jogador("Augusto Nesser (Caixetinha)", 3));
		jogadores.add(new Jogador("Bruno Borges", 4));
		jogadores.add(new Jogador("Diego Berardino", 3));
		jogadores.add(new Jogador("Diogo Gonçalves", 4));
		jogadores.add(new Jogador("Diogo Máximo", 4));
		jogadores.add(new Jogador("Felipe Magela", 4));
		jogadores.add(new Jogador("Guilhermo Reid", 3));
		jogadores.add(new Jogador("Jean Pereira", 3));
		jogadores.add(new Jogador("Juan Garay", 2));
		jogadores.add(new Jogador("Léo Soares", 4));
		jogadores.add(new Jogador("Leonardo Pinto", 3));
		jogadores.add(new Jogador("Lúcio Simões", 3));
		jogadores.add(new Jogador("Marcelo Behera", 3));
		jogadores.add(new Jogador("Pedrão Barros", 3));
		jogadores.add(new Jogador("Rafael Coutinho", 3));
		jogadores.add(new Jogador("Rodrigo Caixeta", 4));
		jogadores.add(new Jogador("Tiago Barros (pedrinho)", 3));
		jogadores.add(new Jogador("Tiago Carpanese", 2));

		calculaMedia();
	}

	private static void preparaTimes() {
		times.clear();
		int totalTimes = jogadores.size() / JOGADORES_POR_TIME;
		for (int i = 0; i < totalTimes; i++) {
			times.add(new Time("Time " + alfabeto[i], JOGADORES_POR_TIME));
		}
	}

	private static void calculaMedia() {
		double forcaTotal = 0;

		for (Jogador j : jogadores) {
			forcaTotal += j.getForca();
		}

		forcaMedia = forcaTotal / jogadores.size();
	}

	public static void main(String[] args) {
		do {
			inicializaJogadores();
			preparaTimes();
			montaTimes();
		} while (!calculaEquilibrio());

		imprimeTimes();

		System.out.println("##################");
		System.out.println("  FIM DO SORTEIO");
		System.out.println("##################");
	}

	private static void imprimeTimes() {
		for (Time time : times) {
			System.out.println(time);
			System.out.println("-----");

			try {
				Thread.sleep(new Random().nextInt(3000));
			} catch (InterruptedException e) {
			}
		}
	}

	private static boolean calculaEquilibrio() {
		int forcaMinima = Integer.MAX_VALUE;
		int forcaMaxima = Integer.MIN_VALUE;

		for (Time time : times) {
			forcaMinima = Math.min(time.getForca(), forcaMinima);
			forcaMaxima = Math.max(time.getForca(), forcaMaxima);
		}

		return !(forcaMinima < 0.7 * forcaMaxima);
	}

	private static void montaTimes() {
		// Randomiza a lista de jogadores
		Collections.shuffle(jogadores, new Random(System.currentTimeMillis()));
		Collections.shuffle(times, new Random(System.currentTimeMillis()));

		Iterator<Time> itTime = times.iterator();
		while (jogadores.size() > 0) {
			Time timeDaVez = itTime.next();
			if (timeDaVez.isCompleto()) {
				continue;
			}

			Jogador j = getJogador(timeDaVez);
			timeDaVez.addJogador(j);

			if (itTime.hasNext() == false) {
				itTime = times.iterator();
			}
		}
	}

	private static Jogador getJogador(int forca) {
		Jogador jj = null;
		if (jogadores.size() == 0)
			return null;
		else if (jogadores.size() == 1)
			return jogadores.remove(0);

		Iterator<Jogador> it = jogadores.iterator();
		while (it.hasNext()) {
			Jogador j = it.next();
			if (j.getForca() == forca) {
				jj = j;
				break;
			}
		}

		if (jj != null)
			jogadores.remove(jj);

		return jj;
	}

	private static Jogador getJogador(Time timeDaVez) {
		double peso = Math.random();

		if (timeDaVez.getForca() < forcaMedia) {
			peso += Math.random();
		} else {
			peso -= Math.random();
		}

		int forca = 0;
		if (peso >= 0 && peso < 0.10) {
			// 10%
			forca = 1;
		} else if (peso >= 0.10 && peso < 0.25) {
			// 15%
			forca = 2;
		} else if (peso >= 0.25 && peso < 0.65) {
			// 40%
			forca = 3;
		} else if (peso >= 0.65 && peso < 0.95) {
			// 30%
			forca = 4;
		} else if (peso >= 0.95) {
			// 5%
			forca = 5;
		}

		Jogador jj = getJogador(forca);
		while (jj == null && jogadores.size() > 0)
			jj = getJogador(timeDaVez);

		return jj;
	}

}