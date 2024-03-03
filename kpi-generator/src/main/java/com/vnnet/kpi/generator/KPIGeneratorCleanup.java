package com.vnnet.kpi.generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class KPIGeneratorCleanup {
    public void cleanup() {
        try {
            URL url = getClass().getClassLoader().getResource("generatorConfig.xml");

            System.out.println("cleanup confif file: " + url);

            File file = new File(url.getFile());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            Path root = Paths.get(".").normalize().toAbsolutePath();
            String xml_mapper_path = root.toAbsolutePath() + "\\kpi-common\\src\\main\\java\\com\\vnnet\\kpi\\web\\persistence\\";

            System.out.println("----------------------------" + root.toAbsolutePath());

            NodeList nList = doc.getElementsByTagName("table");

            System.out.println("--------------------------------------------------------------------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    System.out.println("----------------------------");
                    Element eElement = (Element) nNode;
                    String domainObjectName = eElement.getAttribute("domainObjectName");
                    System.out.println("domainObjectName : " + domainObjectName);
                    String xml_mapper;
                    xml_mapper = xml_mapper_path + domainObjectName + "Mapper.xml";
                    System.out.println("Delete file:" + xml_mapper);
                    boolean result = Files.deleteIfExists(Paths.get(xml_mapper));
                    if (result) {
                        System.out.println("Delete ok");
                    } else {
                        System.out.println("Delete failed");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("============KPIGeneratorCleanup================");
        KPIGeneratorCleanup kpiGeneratorCleanup = new KPIGeneratorCleanup();
        kpiGeneratorCleanup.cleanup();
    }
}
