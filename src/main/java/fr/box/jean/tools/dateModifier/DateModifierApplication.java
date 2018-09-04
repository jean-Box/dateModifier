package fr.box.jean.tools.dateModifier;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.drew.imaging.ImageProcessingException;

import fr.box.jean.tools.dateModifier.controler.FiltreFichier;

@SpringBootApplication
public class DateModifierApplication {
	
	private static FiltreFichier fichier = new FiltreFichier();

	public static void main(String[] args) {
		SpringApplication.run(DateModifierApplication.class, args);
		try {
			fichier.manipulateFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
