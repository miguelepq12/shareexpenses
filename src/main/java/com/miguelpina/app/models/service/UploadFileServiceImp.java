package com.miguelpina.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImp implements IUploadFileSevice{
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final static String UPLOADS_FOLDER = "uploads";

	@Override
	public Resource load(String filename,String folder) throws MalformedURLException {

		Path path = getPath(filename,folder);

		log.info("pathFoto: " + path);

		Resource resource= new UrlResource(path.toUri());;

		if (!resource.exists() || !resource.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen: " + path.toString());
		}

		return resource;
	}

	@Override
	public String copy(MultipartFile file,String folder) throws IOException {
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

		Path rootPath = getPath(uniqueFilename,folder);

		log.info("rootPath: " + rootPath);

		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename,String folder) {
		Path rootPath= getPath(filename,folder);
		File file =rootPath.toFile();
		
		if(file.exists()&& file.canRead()) {
			if(file.delete()) {
				return true;
			}
		}
		
		return false;
	}

	Path getPath(String filename,String folder) {
		return Paths.get(UPLOADS_FOLDER).resolve(folder).resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	@Override
	public void init() throws IOException {
		if(!Files.exists(Paths.get(UPLOADS_FOLDER))) {
			Files.createDirectory(Paths.get(UPLOADS_FOLDER));
			Files.createDirectory(Paths.get(UPLOADS_FOLDER,EVENT_IMAGE));
			Files.createDirectory(Paths.get(UPLOADS_FOLDER,USER_IMAGE));
		}
	}
}
