//package helpers;
//
//import com.lowagie.text.DocumentException;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * Created by boris.tomic on 13/10/15.
// */
//public class SaveFile {
//
//    public static void save() throws IOException, DocumentException {
//
//        String inputFile = "seller/statistics";
//        String url = new File(inputFile).toURI().toURL().toString();
//        String outputFile = "firstdoc.pdf";
//        OutputStream os = new FileOutputStream(outputFile);
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocument(url);
//        renderer.layout();
//        renderer.createPDF(os);
//
//        os.close();
//    }
//
//}
