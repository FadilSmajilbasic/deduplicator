package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;

/**
 * La classe GlobalPath descrive un percorso inserito dall'utente per la
 * scansione o per essere escluso dalla scansione.
 */
@Entity
public class GlobalPath {
	/**
	 * L'attributo path contiene il percorso assoluto del file o cartella. Utilizza
	 * l'annotazione @Id per indicare che è una chiave primaria della tabella.
	 */
	@Id
	@Column()
	private String path;

	/**
	 * L'attributo file indica se il percorso {@link GlobalPath#path} sia un file oppure una directory.
	 */
	private Boolean file;

	/**
	 * L'attributo ignoreFile indica se l'oggetto è da ignorare o se deve essere preso in considerazione durante la scansione.
	 */
	private Boolean ignoreFile;

	/**
	 * L'attributo date indica la data d'aggiunta in formato timestamp.
	 */
	private Long date;

	/**
	 * Costruttore vuoto.
	 */
	public GlobalPath() {
	}

	/**
	 * Costruttore che accetta il percorso dell'oggetto e se è da ignorare oppure no.
	 * @param path il percorso del oggetto.
	 * @param ignoreFile se è da ignorare o no.
	 */
	public GlobalPath(String path, boolean ignoreFile) {
		this.setPath(path);
		this.setignoreFile(ignoreFile);
		this.setDate(System.currentTimeMillis());
	}

	/**
	 * Metodo getter per la variabile path..
	 * @return il percorso del oggetto.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Metodo setter per la variabile path.
	 * Se il percorso non è valido viene tirata una RuntimeException.
	 * @param path il percorso da impostare.
	 */
	private void setPath(String path) {
		PathType type = Validator.getPathType(path);
		if (type == PathType.File) {
			this.path = path;
			this.setFile(true);
		} else if (type == PathType.Directory) {
			this.path = path;
			this.setFile(false);
		} else {
			throw new RuntimeException("Invalid path: " + path);
		}
	}

	/**
	 * Metodo getter per la variabile file.
	 * @return true se il percorso impostato è un file, false se è una cartella.
	 */
	public boolean isFile() {
		return file;
	}

	/**
	 * Metodo setter per la variabile file.
	 * @param file true se il percorso impostato è un file, false altrimenti.
	 */
	private void setFile(boolean file) {
		this.file = file;
	}

	/**
	 * Metodo getter per la variabile ignoreFile.
	 * @return true se l'oggetto è impostato per essere ignorato, false altrimenti.
	 */
	public boolean isignoreFile() {
		return ignoreFile;
	}

	/**
	 * Metodo setter per la variabile ignoreFile
	 * @param ignoreFile true se il l'oggetto deve essere ignorato, false se deve essere preso in considerazione durante la scansione.
	 */
	public void setignoreFile(boolean ignoreFile) {
		this.ignoreFile = ignoreFile;
	}

	/**
	 * Metodo getter per la variabile date.
	 * @return la data in formato timestamp.
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * Metodo setter per la variabile date.
	 * @param date la data da impostare in formato timestamp.
	 */
	private void setDate(Long date) {
		this.date = date;
	}
}
