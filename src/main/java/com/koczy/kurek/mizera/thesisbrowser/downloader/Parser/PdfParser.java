package com.koczy.kurek.mizera.thesisbrowser.downloader.Parser;

import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PdfParser {

    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());

    private static final String SAVE_DIRECTORY = "parsedPDF/";

    public PdfParser() {
    }

    public void parseToTxt(InputStream inputstream, String txtName) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(SAVE_DIRECTORY + txtName);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.toString());
        }

        BodyContentHandler handler = new BodyContentHandler(out);
        Metadata metadata = new Metadata();
        ParseContext pcontext = new ParseContext();

        parseDocument(inputstream, handler, metadata, pcontext);

        try {
            inputstream.close();
            out.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }
    }

    private void parseDocument(InputStream inputstream, BodyContentHandler handler, Metadata metadata, ParseContext pcontext) {
        PDFParser pdfparser = new PDFParser();
        try {
            pdfparser.parse(inputstream, handler, metadata, pcontext);
        } catch (IOException | SAXException | TikaException e) {
            logger.log(Level.WARNING, e.toString());
        }
    }
}
