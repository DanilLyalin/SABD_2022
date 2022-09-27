package main.java.obfuscation;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.XMLConstants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static main.java.obfuscation.Obfuscator.deobfuscate;
import static main.java.obfuscation.Obfuscator.obfuscate;

/**
 Главный класс программы. Аргумент "obfusc" - режим обфускирования, а аргумент "deobfusc" - режим деобфускирования
 */

public class Main {

    private static final String ORIGINAL_FILEPATH = "lab1/XML_files/initial.xml";
    private static final String OBFUSCATED_FILEPATH = "lab1/XML_files/obfuscated.xml";
    private static final String DEOBFUSCATED_FILEPATH = "lab1/XML_files/deobfuscated.xml";
    private static final String[] SENSITIVE_FIELDS = {"age", "firstName", "lastName", "location"};

    public static void main(String[] args) {

        if (args.length > 0) {
            // Режим обфускации
            if (Objects.equals(args[0], "obfusc")) {
                try {
                    //Проверка наличия файла
                    if (Files.notExists(Path.of(ORIGINAL_FILEPATH))) {
                        System.out.printf("Initial XML file not found! Exiting.");
                        return ;
                    }

                    //Чтение XML файла
                    SAXBuilder saxBuilder = new SAXBuilder();
                    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

                    Document doc = saxBuilder.build(new File(ORIGINAL_FILEPATH));

                    Element root = doc.getRootElement();
                    List<Element> list = root.getChildren("employee");

                    //Вызов функции обфускации для выбранных полей данных
                    for (Element target : list) {
                        for (int i = 0; i < SENSITIVE_FIELDS.length; i++) {
                            String value = target.getChildText(SENSITIVE_FIELDS[i]);
                            String obfuscatedValue = obfuscate(value);
                            target.getChild(SENSITIVE_FIELDS[i]).setText(obfuscatedValue);
                        }
                    }

                    //Сохранение нового XML файла
                    FileWriter fileWriter = new FileWriter(OBFUSCATED_FILEPATH);

                    XMLOutputter xmlOutputter = new XMLOutputter();
                    xmlOutputter.setFormat(Format.getPrettyFormat().setEncoding("ISO-8859-1"));
                    xmlOutputter.output(doc, fileWriter);

                } catch (IOException | JDOMException e) {
                    e.printStackTrace();
                }
            // Режим де-обфускации
            }else if (Objects.equals(args[0], "deobfusc")) {
                try {
                    //Проверка наличия файла, прошедшего обфускацию
                    if (Files.notExists(Path.of(OBFUSCATED_FILEPATH))) {
                        System.out.printf("Obfuscated file not found! Exiting.");
                        return ;
                    }

                    //Чтение XML файла
                    SAXBuilder saxBuilder = new SAXBuilder();
                    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                    saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

                    Document doc = saxBuilder.build(new File(OBFUSCATED_FILEPATH));

                    Element root = doc.getRootElement();
                    List<Element> list = root.getChildren("employee");

                    //Вызов функции деобфускации для выбранных полей данных
                    for (Element target : list) {
                        for (int i = 0; i < SENSITIVE_FIELDS.length; i++) {
                            String value = target.getChildText(SENSITIVE_FIELDS[i]);
                            String deobfuscatedValue = deobfuscate(value);
                            target.getChild(SENSITIVE_FIELDS[i]).setText(deobfuscatedValue);
                        }
                    }

                    //Сохранение нового XML файла, но с исходным содержанием
                    FileWriter fileWriter = new FileWriter(DEOBFUSCATED_FILEPATH);

                    XMLOutputter xmlOutputter = new XMLOutputter();
                    xmlOutputter.setFormat(Format.getPrettyFormat().setEncoding("ISO-8859-1"));
                    xmlOutputter.output(doc, fileWriter);

                } catch (IOException | JDOMException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("Wrong argument");
            }
        } else {
            System.out.println("No arguments found");
        }
    }
}