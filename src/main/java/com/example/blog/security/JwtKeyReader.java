package com.example.blog.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/*
	I need to know if there is a better way to use a secret key
	than directly reading it from the local directory.
*/

/*
	I tried to bring the file that contains the secret key with a relative path.
	But it seems that you cannot 

*/

@Component
@AllArgsConstructor
public class JwtKeyReader {
	
	private static final String FILEPATH = 
			"." + File.separator +
			"src" + File.separator +
			"main" + File.separator +
			"java" + File.separator +
			"com" + File.separator + 
			"example" + File.separator +
			"blog" + File.separator +
			"security" + File.separator +
			"secret-key.txt";
	
	public static String readKey() throws IOException {
		Path p = Paths.get(FILEPATH);
		byte[] keyBytes = Files.readAllBytes(p);
		return new String(keyBytes, "UTF-8");
	}

	// for key generation
	/*
	public static SecretKey generate() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		KeyGenerator generator = KeyGenerator.getInstance("HmacSHA512");
		generator.init(secureRandom);
		SecretKey key = generator.generateKey();
		return key;
	}
	
	public static void main(String[] args) {
		try {
			byte[] keyBytes = generate().getEncoded();
			System.out.println("sususus");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}*/
}
