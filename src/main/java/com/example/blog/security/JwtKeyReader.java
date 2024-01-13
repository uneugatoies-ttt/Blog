package com.example.blog.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/*
	-> 이 class는 local에 저장된 text file로부터 JWT를 생성하는 데에 필요한 secret key를 읽는
	기능을 하는 static method를 정의하고 있다.

	-> 현재는 local directory에 secret key를 담는 text file (.gitignore에 포함)을 만들어,
	이것을 이 file에서 읽어오는 식으로 secret key를 사용하고 있지만, 더 좋은 방법이 있지 않을까 싶다.
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

}
