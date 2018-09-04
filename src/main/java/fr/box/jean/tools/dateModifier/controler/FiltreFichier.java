package fr.box.jean.tools.dateModifier.controler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import fr.box.jean.tools.dateModifier.model.Fichier;
import fr.box.jean.tools.dateModifier.utils.DateUtils;

/**
 * @author dlafossa
 *
 */
public class FiltreFichier implements FilenameFilter {
	
	private final static Logger LOGGER = Logger.getLogger(FiltreFichier.class.getName());

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	private LocalDateTime dateViser = LocalDateTime.of(2018, 7, 1, 15, 12, 8);

	public void manipulateFiles() throws IOException, ImageProcessingException {

//1 le répertoir 
		File f = new File("D:/Users/dlafossa/Pictures/Vacance Juillet 2018/").getAbsoluteFile();
		FilenameFilter filter = new FiltreFichier();
		List<Fichier> fichiers = new ArrayList<Fichier>();
		// 2 filtre des fichier .JPG
		for (String nom : f.list(filter)) {
			fichiers.add(new Fichier(nom, f.getPath()));
		}
		// tris sur le nom
		Collections.sort(fichiers);

		Long timebetween = differenceBetween(
				getMetadataAttributes(new File(fichiers.get(0).getPath() + "/" + fichiers.get(0).getName())),
				dateViser);
//		showMeTimeBetween(getMetadataAttributes(new File(fichiers.get(0).getPath() + "/" + fichiers.get(0).getName())),
//				dateViser);
//		Long timebetween = differenceBetween(
//				getLastModifiedTime(new File(fichiers.get(0).getPath() + "/" + fichiers.get(0).getName())), dateViser);

		for (Fichier fichier : fichiers) {
			File fileToWork = new File(fichier.getPath() + "/" + fichier.getName());
			LocalDateTime dateCreation = getMetadataAttributes(fileToWork);

			LocalDateTime ldt = addTime(timebetween, dateCreation);
//			ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
			LOGGER.info("\t" + "" + fichier.getName() + "\t" + dateCreation.format(formatter) + "=>" + ldt.format(formatter));
			
			// long millis = zdt.toInstant().toEpochMilli();
			updateMatadetaFileDate(fileToWork, DateUtils.convertToDateViaInstant(ldt));
			getMetadataAttributes(fileToWork);
			
//			if (fileToWork.setLastModified(millis)) {
//				System.out.println("\t ok " + "" + fichier.getName() + "\t" + dateCreation.format(formatter) + "=>"
//						+ getLastModifiedTime(fileToWork).format(formatter));
//			} else {
//				System.out.println("\t KO " + "" + fichier.getName() + "\t" + dateCreation.format(formatter) + "=>"
//						+ getLastModifiedTime(fileToWork).format(formatter));
//			}
		}

	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static LocalDateTime getLastModifiedTime(File file) throws IOException {
		BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		return attr.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws ImageProcessingException
	 */
	public static LocalDateTime getMetadataAttributes(File file) throws IOException, ImageProcessingException {
		Date datetime = null;
		Metadata metadata = null;
		metadata = ImageMetadataReader.readMetadata(file);

		if (metadata != null) {
			// obtain the Exif IFD0 directory
			ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			// query the datetime tag's value
			datetime = exifIFD0.getDate(ExifIFD0Directory.TAG_DATETIME);
		}
		return DateUtils.convertToLocalDateTimeViaSqlTimestamp(datetime);
	}

	private void updateMatadetaFileDate(File file, Date datetime) throws ImageProcessingException, IOException {
		Metadata metadata = null;
		metadata = ImageMetadataReader.readMetadata(file);
		file.setWritable(true);
		if (metadata != null) {
			// obtain the Exif SubIFD directory
			ExifSubIFDDirectory exifSubIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			// query the datetime tag's value
			exifSubIFD.setDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, datetime);
			exifSubIFD.setDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED, datetime);

			// obtain the Exif IFD0 directory
			ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			// query the datetime tag's value
			LOGGER.info("\t"+exifIFD0.getDate(ExifIFD0Directory.TAG_DATETIME));
			exifIFD0.setDate(ExifIFD0Directory.TAG_DATETIME, datetime);
			LOGGER.info("\t"+exifIFD0.getDate(ExifIFD0Directory.TAG_DATETIME));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		return (name.endsWith(".JPG"));
	}

	/**
	 * Recherche du temps passer entre les deux date en sec
	 * 
	 * @param fromDateTime {@link LocalDateTime}
	 * @param toDateTime   {@link LocalDateTime}
	 * @return un maps
	 */
	public Long differenceBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return ChronoUnit.SECONDS.between(fromDateTime, toDateTime);
	}

	public LocalDateTime addTime(Long timebetween, LocalDateTime fromDateTime) {
		return fromDateTime.plusSeconds(timebetween);
	}

	private void showMeTimeBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime) {

		LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

		long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears(years);

		long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths(months);

		long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays(days);

		long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours(hours);

		long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes(minutes);

		long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

		LOGGER.info(years + " years " + months + " months " + days + " days " + hours + " hours " + minutes
				+ " minutes " + seconds + " seconds.");
	}

}