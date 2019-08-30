package br.com.caixa.sidce.interfaces.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.caixa.sidce.util.infraestructure.exception.NegocioException;
import br.com.caixa.sidce.util.infraestructure.exception.NotFoundException;
import br.com.caixa.sidce.util.infraestructure.log.Log;

@Service("fileStorage")
public class FileStorage {

	private static final String ERRO_AO_DESCOMPACTAR_ARQUIVO = "Erro ao descompactar arquivo";
	private static final String COMPRESSION_FAILED = "compression-failed";
	private static final String FILE_NOT_FOUND = "file-not-found";

	/**
	 * Busca o arquivo para download
	 *
	 * @param fileName - Nome do arquivo
	 * @return Resource - Resource do Arquivo
	 */
	public Resource carregaResourceArquivo(String fileName, Path etlOutput) {

		try {
			Path filePath = etlOutput.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new NotFoundException("aquivo-nao-existe");
			}
		} catch (MalformedURLException ex) {
			throw new NotFoundException("aquivo-nao-existe", ex);
		}
	}

	/**
	 * Salva o arquivo no diretório definido
	 *
	 * @param MultipartFile file - Arquivo
	 * @param Path          targetLocation - Caminho onde será salvo
	 * @return fileName Nome do arquivo salvo
	 * @throws NegocioException
	 */
	public String salvarArquivo(MultipartFile file, Path fileStorageLocation) throws NegocioException {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			// Verifica se possui caminho inválido
			if (fileName.contains("..")) {
				throw new NegocioException("problemas-sequencia-arquivo" + fileName);
			}
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (IOException ex) {
			throw new NegocioException("problema-salvar-arquivo", ex);
		}
	}

	/**
	 * Compacta em formato zip os arquivos
	 * 
	 * @param srcFiles    - Array de Files para serem compactadas
	 * @param caminhoZip  - Diretório onde ficará o ZIP
	 * @param zipFileName - Nome do Arquivo Zip
	 * @throws NegocioException
	 */
	public void zipArquivos(File[] srcFiles, Path caminhoZip, String zipFileName) throws NegocioException {

		try (FileOutputStream fos = new FileOutputStream(caminhoZip.resolve(zipFileName).toFile());
				ZipOutputStream zipOut = new ZipOutputStream(fos);) {
			for (File fileToZip : srcFiles) {
				geraZip(fileToZip, zipOut);
			}
		} catch (IOException e) {
			throw new NegocioException(COMPRESSION_FAILED, e);
		}
	}

	public void unzipFile(ZipInputStream zipt, File destDir) throws NegocioException {
		ZipEntry zipEntry = null;
		byte[] buffer = new byte[1024];

		try {
			zipEntry = zipt.getNextEntry();
		} catch (IOException e1) {
			Log.error(this.getClass(), "Arquivo para extração não encontrado", e1);
			throw new NegocioException(FILE_NOT_FOUND, e1);
		}

		try (FileOutputStream fos = new FileOutputStream(newFile(destDir, zipEntry))) {
			while (zipEntry != null) {
				int len;
				while ((len = zipt.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				zipEntry = zipt.getNextEntry();
			}
		} catch (IOException e) {
			throw new NegocioException("uncompression-failed", e);
		}
	}

	public void unzipFile(Resource arquivo, File destDir) throws NegocioException {
		try {
			ZipInputStream zis = new ZipInputStream(arquivo.getInputStream());
			this.unzipFile(zis, destDir);
		} catch (IOException e) {
			Log.error(this.getClass(), ERRO_AO_DESCOMPACTAR_ARQUIVO, e);
			throw new NegocioException(FILE_NOT_FOUND, e);
		}
	}
 
	public void unzipFile(File arquivo, File destDir) throws NegocioException {
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(arquivo));
			this.unzipFile(zis, destDir);
		} catch (FileNotFoundException e) {
			Log.error(this.getClass(), ERRO_AO_DESCOMPACTAR_ARQUIVO, e);
			throw new NegocioException(FILE_NOT_FOUND, e);
		}
	}

	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private void geraZip(File fileToZip, ZipOutputStream zipOut) throws NegocioException {
		try (FileInputStream fis = new FileInputStream(fileToZip);) {
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}

		} catch (IOException e) {
			throw new NegocioException(COMPRESSION_FAILED, e);
		}
	}

	public void unzip(Resource zipFilePath, String destDir) throws NegocioException {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try (ZipInputStream zis = new ZipInputStream(zipFilePath.getInputStream());) {
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				escreveArquivoExtraido(buffer, zis, newFile);
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
		} catch (IOException e) {
			Log.error(this.getClass(), ERRO_AO_DESCOMPACTAR_ARQUIVO, e);
			throw new NegocioException("unzip-file-failed", e);
		}

	}

	private void escreveArquivoExtraido(byte[] buffer, ZipInputStream zis, File newFile) throws NegocioException {
		
		try (FileOutputStream fos = new FileOutputStream(newFile);){
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			throw new NegocioException("falha-no-arquivo", e);
		}
		
	}
}
