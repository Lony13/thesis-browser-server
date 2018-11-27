package com.koczy.kurek.mizera.thesisbrowser.downloader.Parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.PARSED_PDF_FILE;

@Component
public class PdfParser {

    private static final Logger logger = Logger.getLogger(PdfParser.class.getName());


    public PdfParser() {
    }

    public void parseToTxt(InputStream inputstream, String txtName) {
        OutputStream out = getFileOutputStream(txtName);
        if(Objects.nonNull(out)){
            BodyContentHandler handler = new BodyContentHandler(out);
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            parseDocument(inputstream, handler, metadata, pcontext);
            closeStreams(inputstream, out);
            logger.info("File with name " + txtName + " was parsed");
        } else {
            logger.log(Level.WARNING, "Couldn't parse file: " + txtName + " to txt");
        }
    }

    private OutputStream getFileOutputStream(String txtName) {
        try {
            return new FileOutputStream(PARSED_PDF_FILE + txtName);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File with name " + txtName + " was not found");
        }
        return null;
    }

    private void parseDocument(InputStream inputstream, BodyContentHandler handler, Metadata metadata, ParseContext pcontext) {
        PDFParser pdfparser = new PDFParser();
        try {
            pdfparser.parse(inputstream, handler, metadata, pcontext);
        } catch (IOException | SAXException | TikaException e) {
            logger.log(Level.WARNING, "Exception occured during parse document");
        }
    }

    private void closeStreams(InputStream inputstream, OutputStream out) {
        try {
            inputstream.close();
            out.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException while closing streams");
        }
    }
}
