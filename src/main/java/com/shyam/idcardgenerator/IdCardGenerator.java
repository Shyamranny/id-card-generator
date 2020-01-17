package com.shyam.idcardgenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class IdCardGenerator {

    private final String no;
    private final String name;
    private final String nameOfInstitution;
    private final String mobileNo;
    private final String address;
    private final String idNo;
    private final String ownerName;
    private final String ownerMobileNo;
    private final String photo;

    private IdCardGenerator(Builder builder) {
        this.no = builder.no;
        this.name = builder.name;
        this.nameOfInstitution = builder.nameOfInstitution;
        this.mobileNo = builder.mobileNo;
        this.address = builder.address;
        this.idNo = builder.idNo;
        this.ownerName = builder.ownerName;
        this.ownerMobileNo = builder.ownerMobileNo;
        this.photo = builder.photo;
    }

    public BufferedImage generate() throws Exception {

        BufferedImage bufferedImage = new BufferedImage(650, 1016,
                BufferedImage.TYPE_INT_RGB);

        // Create a graphics contents on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        File background = new File("template.png");
        URL url = background.toURI().toURL();

        File file = new File("template.html");

        String html = usingBufferedReader(file);

        html = html.replace("BACKGROUND", url.toString())
                .replace("PHOTO", photo)
                .replace("NUMBER", no)
                .replace("NAME", name)
                .replace("INSTITUTION", nameOfInstitution)
                .replace("MOBILE_NO", mobileNo)
                .replace("ADDREDD", address)
                .replace("ID_NO", idNo)
                .replace("OWNER_NAM", ownerName)
                .replace("OWNER_MOBILE", ownerMobileNo);


        JEditorPane jep = new JEditorPane("text/html", html);
        jep.setSize(650, 1016);
        jep.print(g2d);

        return bufferedImage;

    }



    private  String usingBufferedReader(File filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static class Builder {
        private String no;
        private String name;
        private String nameOfInstitution;
        private String mobileNo;
        private String address;
        private String idNo;
        private String ownerName;
        private String ownerMobileNo;
        private String photo;

        public Builder no(String no) { this.no = no; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder nameOfInstitution(String nameOfInstitution) { this.nameOfInstitution = nameOfInstitution; return this; }
        public Builder mobileNo(String mobileNo) { this.mobileNo = mobileNo; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder idNo(String idNo) { this.idNo = idNo; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder ownerMobileNo(String ownerMobileNo) { this.ownerMobileNo = ownerMobileNo; return this; }
        public Builder photo(String photo) { this.photo = photo; return this; }

        public IdCardGenerator build() throws Exception {

            validate();

            return new IdCardGenerator(this);
        }

        private void validate() throws Exception {

            if (null == no || no.isEmpty()) {
                throw new Exception("Card number is empty");
            }

            if (null == name || name.isEmpty()) {
                throw new Exception("Name is empty");
            }

            if (null == nameOfInstitution || nameOfInstitution.isEmpty()) {
                throw new Exception("NameOfInstitution is empty");
            }

            if (null == mobileNo || mobileNo.isEmpty()) {
                throw new Exception("MobileNo is empty");
            }

            if (null == address || address.isEmpty()) {
                throw new Exception("Address is empty");
            }
            if (null == idNo || idNo.isEmpty()) {
                throw new Exception("IdNo is empty");
            }

            if (null == ownerName || ownerName.isEmpty()) {
                throw new Exception("OwnerName is empty");
            }

            if (null == ownerMobileNo || ownerMobileNo.isEmpty()) {
                throw new Exception("OwnerMobileNo is empty");
            }

            if (null == photo || photo.isEmpty()) {
                throw new Exception("Photo is empty");
            }
        }

    }
}
