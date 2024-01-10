package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        String fileNameXml = "data.xml";
        List<Employee> listXml = parseXML(fileNameXml);
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "data2.json");
    }

    private static List parseXML(String fileNameXml) throws SAXException, ParserConfigurationException {
        List<Employee> listPerson = new ArrayList<Employee>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileNameXml));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Employee person = new Employee();
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    person.id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    person.firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    person.lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    person.country = element.getElementsByTagName("country").item(0).getTextContent();
                    person.age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    listPerson.add(person);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listPerson;
    }

    private static void writeString(String json, String nameFileOut) {
        try (FileWriter file = new FileWriter(nameFileOut)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List parseCSV(String[] columnMapping, String fileName) {
        List listPerson = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            listPerson = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listPerson;
    }
}