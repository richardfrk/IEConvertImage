//
// IEConvertImage v0.5
// Richard Frank
// richardfrk@gmail.com
//

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.itextpdf.text.*;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;

public class Main {
	
	static String inputDir = new String("/Users/richardfrk/Downloads/IEConvertImage/inputDir/");
	static String outputDir = new String("/Users/richardfrk/Downloads/IEConvertImage/outputDir/");

	public static void main(String[] args) throws IOException, DocumentException {

		convertJPEG2TIFF();
		convertTIFF2JPEG();
		convertTIFF2PDF();
		        	    	
    }
    
    private static List<File> getFiles(String fileExtension) throws IOException {
    	
		String[] strArrayExtension = new String[] { fileExtension };
		
		File directory = new File(inputDir);
		        
		List<File> listFiles = (List<File>) FileUtils.listFiles(directory, strArrayExtension, true);
		
		if (listFiles.size() == 0) {
			
			System.out.println("No file(s) in Directory.");
		}
   
		return listFiles;
    }
    
    // JPEG >> TIFF
    public static void convertJPEG2TIFF() throws IOException {
    	
    	List<File> listFiles = getFiles("jpg");
    	
    	if (listFiles.size() > 0) {
    	
        	System.out.println(" -- LISTING FILES --");
    	}
    	
    	for (File file : listFiles) {
    		
    		String fileName = file.getName();
    		System.out.println(fileName);
    	}
    	
		for (File file : listFiles) {
						
			File newFile = new File(outputDir+file.getName().substring(0, file.getName().lastIndexOf("."))+".tiff");
			
			BufferedImage readFile = ImageIO.read(file);
			
			if (!ImageIO.write(readFile, "tiff", newFile)) {
				System.out.println("JPEG2TIFF Problem with:"+" "+newFile.getName());
	      	} else {
	      		System.out.println("SUCCESSFUL Convertion: "+"JPEG >> TIFF");
	      	}
		}
    }
    
    // TIFF >> JPEG
    public static void convertTIFF2JPEG() throws IOException {
    	
    	List<File> listFiles = getFiles("tiff");
    	
    	if (listFiles.size() > 0) {
    	
        	System.out.println(" -- LISTING FILES --");
    	}
    	
    	for (File file : listFiles) {
    		
    		String fileName = file.getName();
    		System.out.println(fileName);
    	}
    	
		for (File file : listFiles) {
			
			try (ImageInputStream imageInput = ImageIO.createImageInputStream(file)) {
	    	    ImageReader imageReader = ImageIO.getImageReaders(imageInput).next();
	    	    imageReader.setInput(imageInput);

	    	    ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("JPEG").next();

	    	    int pages = imageReader.getNumImages(true);
	    	    
	    	    for (int index = 0; index < pages; index++) {
	    	        BufferedImage bufferedImage = imageReader.read(index, null);
	    	        
	    	        String page = new String("");
	    	        
	    	        if (pages != 1) {
	    	        	page = "_page_"+index;
	    	        }

	    	        try (ImageOutputStream imageOutput = ImageIO.createImageOutputStream(new File(outputDir+file.getName().substring(0, file.getName().lastIndexOf("."))+page+".jpg"))) {
	    	        	
	    	        	imageWriter.setOutput(imageOutput);
	    	        	imageWriter.write(bufferedImage);
	    	        	
	    	      		System.out.println("SUCCESSFUL Convertion: "+"TIFF >> JPEG");

	    	        } catch(Exception ex) {
	    	        	
	    				System.out.println("TIFF2JPEG Problem with:"+" "+file.getName());
	    	        }
	    	    }

	    	    imageWriter.dispose();
	    	    imageReader.dispose();
			}
		}
    }
    
    // TIFF >> PDF
    public static void convertTIFF2PDF() throws IOException, DocumentException {
    	
    	List<File> listFiles = getFiles("tiff");
    	
    	if (listFiles.size() > 0) {
    	
        	System.out.println(" -- LISTING FILES --");
    	}
    	
    	for (File file : listFiles) {
    		
    		String fileName = file.getName();
    		System.out.println(fileName);
    	}
    	
		for (File file : listFiles) {
    	
			RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
			String fileName = file.getCanonicalPath();
			RandomAccessFileOrArray ra = new RandomAccessFileOrArray(factory.createBestSource(fileName));

			Image image = TiffImage.getTiffImage(ra, 1);
			int numberOfPages = TiffImage.getNumberOfPages(ra);
			Rectangle pageSize = new Rectangle(image.getWidth(), image.getHeight());
			Document document = new Document(pageSize);
						
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputDir+file.getName().substring(0, file.getName().lastIndexOf("."))+".pdf"));
        
			writer.setStrictImageSequence(true);
			document.open();
        
			for(int index = 1; index <= numberOfPages; index++) {
        	
				Image newImage = TiffImage.getTiffImage(ra, index);
				document.add(newImage);
				document.newPage();
			}

			document.close();
			System.out.println("SUCCESSFUL Convertion: "+"TIFF >> PDF");

		}        
    }
    	
    	
    

    
  
        	
        
	      
}