package onegame.modello.net.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
	/**
	 * Genera un hash sicuro della password usando PBKDF2 con HMAC SHA-256.
	 * @param password La password in chiaro
	 * @return Stringa Base64 contenente salt + hash
	 */
	public static String hashPassword(String password) {
		try {
			// Salt casuale di 16 byte
			byte[] salt = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(salt);

			// Parametri PBKDF2
			int iterations = 65536;
			int keyLength = 256;

			// Derivazione della chiave
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hash = skf.generateSecret(spec).getEncoded();

			// Combina salt + hash
			byte[] combined = new byte[salt.length + hash.length];
			System.arraycopy(salt, 0, combined, 0, salt.length);
			System.arraycopy(hash, 0, combined, salt.length, hash.length);

			// Codifica in Base64 per memorizzazione
			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new RuntimeException("Errore nell'hash della password", e);
		}
	}

	/**
	 * Verifica se la password in chiaro corrisponde all'hash memorizzato.
	 * @param password La password in chiaro
	 * @param stored L'hash memorizzato (Base64 salt + hash)
	 * @return true se la password è corretta, false altrimenti
	 */
	public static boolean verificaPassword(String password, String stored) {
		try {
			byte[] combined = Base64.getDecoder().decode(stored);

			// Estrai salt e hash
			byte[] salt = Arrays.copyOfRange(combined, 0, 16);
			byte[] hashFromDb = Arrays.copyOfRange(combined, 16, combined.length);

			// Parametri PBKDF2 (devono essere identici)
			int iterations = 65536;
			int keyLength = 256;

			// Deriva hash dalla password fornita
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hashedInput = skf.generateSecret(spec).getEncoded();

			// Confronto sicuro
			return MessageDigest.isEqual(hashFromDb, hashedInput);
		} catch (Exception e) {
			throw new RuntimeException("Errore nella verifica della password", e);
		}
	}
}
