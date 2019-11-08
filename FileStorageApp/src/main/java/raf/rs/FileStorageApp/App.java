package raf.rs.FileStorageApp;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import exceptions.DownloadException;
import framework.ExecuteOption;
import framework.Input;
import framework.Lifecycle;
import framework.Option;
import framework.Question;
import framework.Structure;
import raf.rs.FIleStorageSpi.User;
import raf.rs.FileStorageLocalImpl.model.FileStorageLocal;
import raf.rs.FileStorageLocalImpl.model.LocalDirectoryService;
import raf.rs.FileStorageLocalImpl.model.LocalFileService;
import raf.rs.FileStorageLocalImpl.model.LocalUser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	new Lifecycle(new DemoStructure()).run();
        
    }
    
    private static final class DemoStructure extends Structure {
    	
    	String str1 = "";
    	String str2 = "";
    	User admin = null;
    	User realAdmin = null;
    	LocalUser user = null;
    	FileStorageLocal storage = null;
    	LocalFileService localService = null;
    	LocalDirectoryService directioryService = null;
		
		@Override
		protected Question create() {
			final String rootpath = "";
			
			Question q1 = new Question ("Koju operaciju zelite da izvrsite?");
			
			
			Option opKreiranjeAdmin = new ExecuteOption("Kreiranje admina") {
				
				@Override
				public void execute() {
					String username = getInput("username?").getValue();
					String password = getInput("password?").getValue();
					String path = getInput("path do skladista?").getValue();
					admin = new LocalUser(username, password, true);
					try {
						storage = new FileStorageLocal(path, true, admin);
						localService = new LocalFileService(storage);
						directioryService = new LocalDirectoryService(storage);
						((LocalUser)admin).setFileStorage(storage);
						realAdmin = admin;
						((LocalUser)realAdmin).setFileStorage(storage);
					} catch (Exception e) {
						System.out.println("Nije bilo moguce napraviti skladiste!");
						e.printStackTrace();
					}
					
				}
			}.addInput(new Input("username?")).addInput(new Input("password?")).addInput(new Input("path do skladista?"));
			
			
			Option opKonekcija = new ExecuteOption("Konekcija na postojece skladiste") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Skladiste ne postoji!");
						return;
					}
					if(admin != null) {
						System.out.println("Skladiste trenutno koristi jedan korisnik!");
						return;
					}
					String username = getInput("username?").getValue();
					String password = getInput("password?").getValue();
					String path = getInput("path do skladista?").getValue();
					
					admin = new LocalUser(username, password, false);
					try {
						if(!admin.connectToFileStorage(storage.getRootDirPath())) {
							System.out.println("Greska prilikom konekovanja na storage!");
							return;
						}
					} catch (Exception e) {
						System.out.println("Greska prilikom konekovanja korisnika (" + admin + ") na skladiste!");
						e.printStackTrace();
					}
				}
			}.addInput(new Input("username?")).addInput(new Input("password?")).addInput(new Input("path do skladista?"));
			
			
			Option opDiskonekcija = new ExecuteOption("Diskonekcija sa skladista") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Skladiste ne postoji!");
						return;
					}
					if(admin == null) {
						System.out.println("Skladiste trenutno ne koristi niko!");
						return;
					}
					
					
				//	admin = new LocalUser(username, password, false);
					try {
						
						if(!((LocalUser)admin).disconnectFromFileStorage(storage.getRootDirPath())) {
							System.out.println("Greska prilikom diskonektovanja sa skladista!");
							return;
						}
						admin = null;
					} catch (Exception e) {
						System.out.println("Greska prilikom diskonektovanja korisnika (" + admin + ") sa skladista!");
						e.printStackTrace();
					}
				}
			};
			
			
			Option opKreiranjeKorisnika = new ExecuteOption("Kreiranje korisnika") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Skladiste ne postoji!");
						return;
					}
					if(admin == null) {
						System.out.println("Skladiste nema usera!");
						return;
					}
					if(!admin.isRootUser()) {
						System.out.println("Samo admin moze da pravi nove korisnike!");
						return;
					}
					String username = getInput("username?").getValue();
					String password = getInput("password?").getValue();
					System.out.println("CreateUser PRE: " + storage.getUsers());
					((LocalUser)admin).createNewUser(admin, username, password);
					System.out.println("CreateUser POSLE: " + storage.getUsers());
					
				}
			}.addInput(new Input("username?")).addInput(new Input("password?"));
			
			
			q1.addOption(opKreiranjeAdmin);
			q1.addOption(opKonekcija);
			q1.addOption(opDiskonekcija);
			q1.addOption(opKreiranjeKorisnika);
					
			Option opCreateFile = new ExecuteOption("Kreiranje praznog fajla") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String path = getInput("path").getValue();
					String filename = getInput("filename").getValue();
					try {
						localService.createEmptyFile(path, filename);
					} catch (Exception e) {
						System.out.println(e.getMessage());
				//		e.printStackTrace();
					}
					
				}
			}.addInput(new Input("path")).addInput(new Input("filename")); 
			
			Option opCreateMultipleFiles = new ExecuteOption("Kreiranje vise praznih fajlova") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String path = getInput("path").getValue();
					String filename = getInput("filename").getValue();
					int number = Integer.parseInt(getInput("number of files").getValue());
					try {
						localService.createMultipleFiles(path, filename, number);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					
				}
			}.addInput(new Input("path")).addInput(new Input("filename")).addInput(new Input("number of files"));
			
			Option opDeleteFile = new ExecuteOption("Brisanje fajla") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String path = getInput("path").getValue();
					String filename = getInput("filename").getValue();
					try {
						localService.delFile(path, filename);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					
				}
			}.addInput(new Input("path")).addInput(new Input("filename"));
			
			Option opUploadFile = new ExecuteOption("Upload fajla") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String pathSource = getInput("path source").getValue();
					String pathDestination = getInput("path destination").getValue();
					try {
						localService.uploadFile(pathSource, pathDestination);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					
				}
			}.addInput(new Input("path source")).addInput(new Input("path destination"));
			
			Option opDownloadFile = new ExecuteOption("Download fajla") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String pathSource = getInput("path source").getValue();
					String pathDestination = getInput("path destination").getValue();
					try {
						localService.downloadFile(pathSource, pathDestination);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}.addInput(new Input("path source")).addInput(new Input("path destination"));
			
			Option opUploadMultipleFiles = new ExecuteOption("Upload vise fajlova") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String pathDestination = getInput("path destination").getValue();
					System.out.println("!!! Fajlove navoditi sa * izmedju njih!!!");
					String allFiles = getInput("allFiles").getValue();
					List<File> files = new ArrayList<File>();
					
					String []parts = allFiles.split("&&&");
					for(String part : parts) {
						String path = FilenameUtils.separatorsToSystem(part);
						File f = new File(path);
						if(!f.exists()) {
							System.out.println("Fajl ne postoji {" + part + "}");
							return;
						}
						files.add(f);
					}
					
					if(files.isEmpty()) {
						System.out.println("Lista fajlova je prazna!");
						return;
					}
					try {
						localService.uploadMultipleFiles(pathDestination, files);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					
				}
			}.addInput(new Input("path destination")).addInput(new Input("allFiles"));
			
			Option opAddMataData = new ExecuteOption("Dodavanje meta podataka") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					System.out.println("Ukoliko se unosi jedan meta podataka, uneti u obliku key:value");
					System.out.println("Ukoliko se unosi vise meta podataka, uneti u obliku key:value-key:value");
					String metaFilePath = getInput("metaFilePath").getValue();
					String input = getInput("value(s)").getValue();
					String pairs[] = input.split("-");
					if(pairs.length == 0) {
						System.out.println("Nije unet nijedan metapodataka!");
						return;
					}
					Hashtable<String, String> table = new Hashtable<String, String>();
					for(String pair : pairs) {
						String parts[] = pair.split(":");
						if(parts.length != 2) {
							System.out.println("Nekorektno unet par vrednosti!");
							return;
						}
						table.put(parts[0], parts[1]);
					}
					localService.addMetaData(metaFilePath, table);
				}
			}.addInput(new Input("metaFilePath")).addInput(new Input("value(s)"));
			
			Option opCreateMetaData = new ExecuteOption("Kreiranje meta podataka") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					System.out.println("Ukoliko se unosi jedan meta podataka, uneti u obliku key:value");
					System.out.println("Ukoliko se unosi vise meta podataka, uneti u obliku key:value-key:value");
					String filepath = getInput("filepath").getValue();
					String input = getInput("value(s)").getValue();
					String pairs[] = input.split("-");
					if(pairs.length == 0) {
						System.out.println("Nije unet nijedan metapodataka!");
						return;
					}
					Hashtable<String, String> table = new Hashtable<String, String>();
					for(String pair : pairs) {
						String parts[] = pair.split(":");
						if(parts.length != 2) {
							System.out.println("Nekorektno unet par vrednosti!");
							return;
						}
						table.put(parts[0], parts[1]);
					}
					localService.createMetaDataFile(filepath, table);
				}
			}.addInput(new Input("filepath")).addInput(new Input("value(s)"));
			
			Option opUploadArhiveFile = new ExecuteOption("Upload arhiviranog file-a") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String pathSource = getInput("path source").getValue();
					String pathDestination = getInput("path destination").getValue();
					try {
						localService.uploadArchive(pathSource, pathDestination);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}.addInput(new Input("path source")).addInput(new Input("path destination"));
		
			
			q1.addOption(opCreateFile);
			q1.addOption(opCreateMultipleFiles);
			q1.addOption(opDeleteFile);
			q1.addOption(opUploadFile);
			q1.addOption(opDownloadFile);
			q1.addOption(opUploadMultipleFiles);
			q1.addOption(opCreateMetaData);
			q1.addOption(opAddMataData);
			q1.addOption(opUploadArhiveFile);
		
			
			Option opCreateDirectory = new ExecuteOption("Kreiranje direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String dirName = getInput("directory name").getValue();
					try {
						if(!directioryService.createEmptyDirectory(dirPath, dirName).exists()) {
							System.out.println("Nije kreiran novi direktorijum na putanji (" + dirPath + ")");
							return;
						}
					} catch (Exception e) {
						System.out.println("Nije bilo moguce kreirati novi direktorijum na putanji (" + dirPath + ")");
						e.printStackTrace();
					}					
				}
			}.addInput(new Input("directory path")).addInput(new Input("directory name"));
			
			
			Option opCreateMultipleDirectories = new ExecuteOption("Kreiranje vise direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String dirName = getInput("directory name").getValue();
					String number = getInput("number").getValue();
					try {
						if(!directioryService.createMultipleDirectories(dirPath, dirName, Integer.valueOf(number))) {
							System.out.println("Nije kreiran novi direktorijum na putanji (" + dirPath + ")");
							return;
						}
					} catch (Exception e) {
						System.out.println("Nije bilo moguce kreirati novi direktorijum na putanji (" + dirPath + ")");
						e.printStackTrace();
					}					
				}
			}.addInput(new Input("directory path")).addInput(new Input("directory name")).addInput(new Input("number"));
			
			
			Option opDeleteDirectory = new ExecuteOption("Brisanje direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String dirName = getInput("directory name").getValue();
					try {
						if(!directioryService.delDirectory(dirPath, dirName)) {
							System.out.println("Nije obrisan direktorijum sa putanje (" + dirPath + ")");
							return;
						}
					} catch (Exception e) {
						System.out.println("Nije bilo moguce obrisati direktorijum sa putanji (" + dirPath + ")");
						e.printStackTrace();
					}
				}
			}.addInput(new Input("directory path")).addInput(new Input("directory name"));
			
			
			Option opDownloadDirectory = new ExecuteOption("Skidanje direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String sourcePath = getInput("source path").getValue();
					String destnationPath = getInput("destination path").getValue();
					try {
						if(!directioryService.downloadDirectory(sourcePath, destnationPath)) {
							System.out.println("Greska prilikom skiranja direktorijuma(" + sourcePath + ")");
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Nije bilo moguce skinuti direktorijum sa putanje (" + sourcePath + ")");
					}
				}
			}.addInput(new Input("source path")).addInput(new Input("destination path"));
			
			
			Option opListDirectories = new ExecuteOption("Izlistaj direktorijume direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String result = directioryService.listDirectories(dirPath);
					System.out.println(result);					
				}
			}.addInput(new Input("directory path"));
			
			
			Option opListFiles = new ExecuteOption("Izlistaj fajlove direktorijuma") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String result = directioryService.listFiles(dirPath, false);
					System.out.println(result);					
				}
			}.addInput(new Input("directory path"));
			
			
			Option opFilesExtension = new ExecuteOption("Izlistaj fajlove sa ekstenzijom") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					String dirPath = getInput("directory path").getValue();
					String extension = getInput("extension").getValue();
					List<File> files = directioryService.getFilesWithExtension(dirPath, extension);
					if(files.isEmpty()) {
						System.out.println("Nema fajlova sa ekstenzijom (" + extension + ")");
						return;
					}
					System.out.println(files);					
				}
			}.addInput(new Input("directory path")).addInput(new Input("extension"));
			
			
			Option opAllFilesDirectory = new ExecuteOption("Izlistaj fajlove direktorijuma sortirano (ukoliko je prosledjen za prvi argument true)") {
				
				@Override
				public void execute() {
					if(storage == null) {
						System.out.println("Morate biti konektovani na storage!");
						return;
					}
					if(admin == null) {
						System.out.println("Niko nije konektovan na storage!");
						return;
					}
					boolean sorted = Boolean.getBoolean(getInput("sorted").getValue());
					String dirPath = getInput("directory path").getValue();
					List<String> files = new ArrayList<String>();
					try {
						files = directioryService.getAllFiles(sorted, dirPath);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Greska prilikom preuzimanja svih fajlova!");
					}
					if(files.isEmpty()) {
						System.out.println("Nema fajlova");
						return;
					}
					System.out.println(files);					
				}
			}.addInput(new Input("sorted")).addInput(new Input("directory path"));
			
			
			q1.addOption(opCreateDirectory);
			q1.addOption(opCreateMultipleDirectories);
			q1.addOption(opDeleteDirectory);
			q1.addOption(opDownloadDirectory);
			q1.addOption(opListDirectories);
			q1.addOption(opListFiles);
			q1.addOption(opFilesExtension);
			q1.addOption(opAllFilesDirectory);
			
		
			return q1;
		}
		
	}
}
