package onegame.client.controllore.utils;

import onegame.client.controllore.utils.Wrappers.IntegerAndBooleanWrapper;
import onegame.client.controllore.utils.Wrappers.StringWrapper;
import onegame.modello.net.GiocatoreDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * classe di utility per ricreare mappa di giocatori per GUI
 */
public class MappaUtils {

	/**
	 * metodo che, partendo da una lista di giocatoriDTO, indice di posizione corrente e indice di posizione assoluta
	 * genera la mappa utilizzata dalla gui per renderizzare i giocatori avversari
	 * 
	 * @param giocatori, tutti i giocatori della partita e le relative informazioni utili per la gui
	 * @param posizioneCorrente, giocatore corrente (serve per impostare il flag di giocatore corrente)
	 * @param posizioneAssoluta, posizione assoluta del giocatore di cui si deve calcolare la mappa
	 * @return
	 */
	public static Map<StringWrapper, IntegerAndBooleanWrapper> creaMappa(List<GiocatoreDTO> giocatori, int posizioneCorrente, int posizioneAssoluta){
		LinkedHashMap<StringWrapper, IntegerAndBooleanWrapper> mappaGiocatoriDTO = new LinkedHashMap<>();
		int i=0;
		for (GiocatoreDTO gtdo : giocatori) {
			String nickname = gtdo.nickname;
			boolean flag = (i==posizioneCorrente);
			Wrappers.StringWrapper sr=new Wrappers.StringWrapper(nickname,i);
			Wrappers.IntegerAndBooleanWrapper info=new Wrappers.IntegerAndBooleanWrapper(gtdo.numeroCarteInMano,flag);
			mappaGiocatoriDTO.put(sr, info);
			i++;
		}
//		System.out.println("PRIMA DELLA ROTAZIONE " + posizioneAssoluta);
//		for(Map.Entry<StringWrapper, IntegerAndBooleanWrapper> entry : mappaGiocatoriDTO.entrySet()) {
//			System.out.println(entry.getKey().getValue());
//		}
		mappaGiocatoriDTO = ruotaMappa(mappaGiocatoriDTO, posizioneAssoluta);
//		System.out.println("PRIMA DELLA DELETE");
//		for(Map.Entry<StringWrapper, IntegerAndBooleanWrapper> entry : mappaGiocatoriDTO.entrySet()) {
//			System.out.println(entry.getKey().getValue());
//		}
		rimuoviPrimoDaMappa(mappaGiocatoriDTO);
//		System.out.println("PRIMA DELLA return");
//		for(Map.Entry<StringWrapper, IntegerAndBooleanWrapper> entry : mappaGiocatoriDTO.entrySet()) {
//			System.out.println(entry.getKey().getValue());
//		}
		return mappaGiocatoriDTO;
	}
	
	private static LinkedHashMap<StringWrapper, IntegerAndBooleanWrapper> ruotaMappa(LinkedHashMap<StringWrapper, IntegerAndBooleanWrapper> map, int offset) {
        if (map == null || map.isEmpty()) {
            return new LinkedHashMap<>();
        }

        //converto le entry in lista
        List<Map.Entry<StringWrapper, IntegerAndBooleanWrapper>> entries = new ArrayList<>(map.entrySet());

        //normalizzo l'offset (può essere > size o negativo)
        int size = entries.size();
        int shift = ((offset % size) + size) % size;

        //ruoto la lista in senso antiorario
        Collections.rotate(entries, -shift);

        //ricostruisco la LinkedHashMap con il nuovo ordine
        LinkedHashMap<StringWrapper, IntegerAndBooleanWrapper> rotated = new LinkedHashMap<>();
        for (Map.Entry<StringWrapper, IntegerAndBooleanWrapper> e : entries) {
            rotated.put(e.getKey(), e.getValue());
        }

        return rotated;
    }
	
	private static void rimuoviPrimoDaMappa (LinkedHashMap<StringWrapper, IntegerAndBooleanWrapper> map) {
		Iterator<StringWrapper> it = map.keySet().iterator();
        if (it.hasNext()) {
            StringWrapper firstKey = it.next();
            map.remove(firstKey);
        }
	}

}
