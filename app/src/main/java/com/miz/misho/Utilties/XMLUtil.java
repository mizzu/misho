package com.miz.misho.Utilties;

import android.content.Context;

import com.miz.misho.BuildConfig;
import com.miz.misho.Objects.VocabList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLUtil {
    private String XML_PATH;
    private final String XML_DEF_NAME = "DefaultSettingsXML.xml";
    private final String XML_NAME = "Settings.xml";
    private Context mContext;
    private FileUtil fileUtil;

    public XMLUtil(Context context) throws IOException{
        mContext = context;
        XML_PATH = mContext.getFilesDir().getPath() + File.pathSeparator+ BuildConfig.APPLICATION_ID + File.separator + "settings";
        fileUtil = new FileUtil(mContext);
        createSettingsIfNotExists();
    }

    public void createSettingsIfNotExists() throws IOException {
        File f = new File(XML_PATH + File.separator + XML_NAME);
        if(!f.exists()) {
            File fdir = new File(XML_PATH);
            fdir.mkdirs();
            InputStream dbi = mContext.getAssets().open(XML_DEF_NAME);
            String out = XML_PATH + File.separator + XML_NAME;
            OutputStream outs = new FileOutputStream(out);
            byte[] buff = new byte[1024];
            int length;
            while ((length = dbi.read(buff)) > 0) {
                outs.write(buff, 0, length);
            }

            outs.flush();
            outs.close();
            dbi.close();
        }
    }

    public ArrayList<VocabList> getQuickAddList() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException{
        File f = new File(XML_PATH + File.separator + XML_NAME);
        if(!f.exists())
            throw new FileNotFoundException();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db =  dbf.newDocumentBuilder();
        Document settings = db.parse(f);
        settings.getDocumentElement().normalize();

        ArrayList<VocabList> vl = new ArrayList<>();

        ArrayList<Element> toRemove = new ArrayList<>();
        Node quickadd = settings.getElementsByTagName("quickadd").item(0);
        NodeList qa = quickadd.getChildNodes();
        for(int i = 0; i < qa.getLength(); i++) {
            Node n = qa.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if(n.getNodeValue() != null)
                    if(n.getParentNode().getTextContent().trim().length() == 0)
                        continue;

                Element e = (Element) n;
                VocabList vlt = new VocabList(e.getElementsByTagName("name").item(0).getTextContent(),
                        e.getElementsByTagName("path").item(0).getTextContent());
                if(!new File(vlt.getPath()+fileUtil.getEntryend()).exists()) {
                    toRemove.add(e);
                    continue;
                }
                    vl.add(vlt);
                }
            }
            for(Element te : toRemove) {
                te.getParentNode().removeChild(te);
            }
            if(!toRemove.isEmpty()) {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.transform(new DOMSource(settings),
                        new StreamResult(new FileOutputStream(f)));
            }
        return  vl;
        }

    public boolean updateQuickAddNamePath(String  oldname, String oldpath, String newname, String newpath) throws ParserConfigurationException,  SAXException, IOException, TransformerException {
        File f = new File(XML_PATH + File.separator + XML_NAME);
        if(!f.exists())
            throw new FileNotFoundException();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db =  dbf.newDocumentBuilder();
        Document settings = db.parse(f);
        settings.getDocumentElement().normalize();
        boolean updated = false;

        Node quickadd = settings.getElementsByTagName("quickadd").item(0);
        NodeList qa = quickadd.getChildNodes();
        for(int i = 0; i < qa.getLength(); i++) {
            Node n = qa.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if(n.getNodeValue() != null)
                    if(n.getParentNode().getTextContent().trim().length() == 0)
                        continue;

                Element e = (Element) n;
                if (e.getElementsByTagName("name").item(0).getTextContent().equals(oldname) &&
                        e.getElementsByTagName("path").item(0).getTextContent().equals(oldpath)) {
                    e.getElementsByTagName("name").item(0).setTextContent(newname);
                    e.getElementsByTagName("path").item(0).setTextContent(newpath);
                    updated = true;
                }
            }

        }

        if(!updated) {
            if (qa.getLength() > 2) {
                quickadd.removeChild(quickadd.getFirstChild());
            }

            Element eqa = settings.createElement("qa");


            Element name = settings.createElement("name");
            name.appendChild(settings.createTextNode(newname));
            eqa.appendChild(name);

            Element path = settings.createElement("path");
            path.appendChild(settings.createTextNode(newpath));
            eqa.appendChild(path);

            quickadd.appendChild(eqa);
        }

        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.transform(new DOMSource(settings),
                new StreamResult(new FileOutputStream(f)));

            return true;
    }
}
