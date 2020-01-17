package com.shyam.idcardgenerator;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class IdCardGeneratorApplication {

	public static final String THUMBNAIL_PNG = "thumbnail.png";
	public static final String OUTPUT = "OUTPUT";
	private static final int EMPTY_THRESHOLD = 3;

	public static void main(String[] args) throws Exception {

		System.out.println("Start - ID Card Generator");

		if (args.length < 3) {
			System.err.println("Invalid parameters, usage: excellocation counter workdir");
			System.exit(1);
		}

		File file = new File(args[2]);
		if (!file.exists()) {
			System.err.println("Work directory does not exist!!  " + args[2]);
			System.exit(1);
		}

		if (!file.isDirectory()) {
			System.err.println(args[2] + " is not a directory!!");
			System.exit(1);
		}

		if (!file.canWrite()) {
			System.err.println(args[2] + " is not writable!!");
			System.exit(1);
		}

		File outDir = new File(file, OUTPUT);
		if (!outDir.exists()) {
			if (!outDir.mkdir()){
				System.err.println(args[2] + " is not writable!!");
				System.exit(1);
			}
		}

		process(args[0], Integer.parseInt(args[1]), args[2]);

		System.out.println("Finished - ID Card Generator");
	}

	private static void process(String excelFileName, int counter, String workDir) throws Exception {

		final XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelFileName));

		final XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

		int emptyRowCount = 0;

		for (int i = 1; i < xssfSheet.getLastRowNum(); i++) {

			System.out.println("Printing row: " + i);
			try {

				XSSFRow xssfRow = xssfSheet.getRow(i);

				if (isRowEmpty(xssfRow)) {
					emptyRowCount++;

					System.err.println("Empty row: " + i + ", empty row count = " + emptyRowCount);
					if (emptyRowCount > EMPTY_THRESHOLD) {
						System.err.println("Stopping becasue mazimum empty row count reached");
						break;
					}

					continue;
				}

				emptyRowCount = 0;

				String name = getCellValue(xssfRow.getCell(1));
				String address = getCellValue(xssfRow.getCell(2));
				String mobileNumber = getCellValue(xssfRow.getCell(6));
				String idNo = getCellValue(xssfRow.getCell(8));
				String institution = getCellValue(xssfRow.getCell(9));
				String ownerName = getCellValue(xssfRow.getCell(12));
				String ownerMobileNo = getCellValue(xssfRow.getCell(14));
				String imageurl = getCellValue(xssfRow.getCell(17));

				downloadImage(imageurl, new File(workDir));

				File photo = new File(workDir, THUMBNAIL_PNG);
				URL urlPhoto = photo.toURI().toURL();

				IdCardGenerator idCardGenerator = new IdCardGenerator.Builder()
						.no(Integer.toString(counter++))
						.photo(urlPhoto.toString())
						.name(name)
						.nameOfInstitution(institution)
						.mobileNo(mobileNumber)
						.address(address)
						.idNo(idNo)
						.ownerName(ownerName)
						.ownerMobileNo(ownerMobileNo)
						.build();


				BufferedImage bufferedImage = idCardGenerator.generate();

				ImageIO.write(bufferedImage, "PNG", new File(new File(workDir, OUTPUT), name.replace(" ", "_") + idNo.replace(File.separator, "_") + ".png"));


			} catch (Exception e) {
				System.err.println("Error occurred while processing row:" + i + ", details:" + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private static void downloadImage(String imageurl, File file) throws IOException {

		URL url = new URL(imageurl);
		Image image = ImageIO.read(url);

		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		g.drawImage(image, 0, 0, null);
		ImageIO.write(bi, "PNG", new File(file, THUMBNAIL_PNG));

	}

	private static String getCellValue(XSSFCell cell) {
		if (null == cell) {
			return null;
		}
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(cell);
	}

	public static boolean isRowEmpty(XSSFRow row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			XSSFCell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != CellType.BLANK)
				return false;
		}
		return true;
	}

}
