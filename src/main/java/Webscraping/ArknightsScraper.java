package Webscraping;

import com.opencsv.CSVWriter;
import org.w3c.dom.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArknightsScraper {
    private ArrayList<Operator> operators = new ArrayList<>();
    private ArrayList<String> listaop = new ArrayList<>();

    /**
     * Comienza el webscraping con este metodo
     * @throws InterruptedException por los Exception y los sleep
     */
    public void start() throws InterruptedException, ParserConfigurationException, TransformerException {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver");
        FirefoxOptions options = new FirefoxOptions();

        WebDriver driver = new FirefoxDriver(options);

        LinkOp(driver);
        SetOp(driver);
        driver.quit();
        crearCSV();
        crearXML();
    }

    /**
     * Metodo donde se guardara en la lista listaop los links de donde tendra que pillar los Operators
     * @param a
     * @throws InterruptedException
     */
    private void LinkOp(WebDriver a) throws InterruptedException {
        a.get("https://gamepress.gg/arknights/tools/interactive-operator-list#tags=null##stats");
        Thread.sleep(2000);
        List<WebElement> elementos = a.findElements(By.className("operators-row"));
        WebElement temp;
        for (WebElement elemento : elementos) {
            if (elemento.getAttribute("data-availserver").equals("na") && !elemento.getAttribute("data-name").equals("Tulip")) {
                temp = elemento.findElement(By.className("operator-title-actual"));
                String href = temp.getAttribute("href");
                listaop.add(href);
            }
        }
    }

    /**
     * Metodo donde creara los Operator que se añadira a la lista operator
     * @param driver es link donde pillara los atributos de los operator
     * @throws InterruptedException por los Exception y los sleep
     */
    private void SetOp(WebDriver driver) throws InterruptedException {
        for (String href : listaop) {
            String name, position, attack;
            boolean alter = false;
            WebElement temp;
            driver.get(href);
            Thread.sleep(4000);
            temp = driver.findElement(By.name("twitter:title"));
            name = temp.getAttribute("content");
            temp = driver.findElement(By.className("position-cell"));
            position = temp.findElement(By.tagName("a")).getText();
            temp = driver.findElement(By.className("traits-cell"));
            attack = temp.findElement(By.tagName("a")).getText();
            if (driver.findElement(By.className("main-title")).getText().equals("Alternative Forms")) alter = true;
            Operator operator = new Operator(name, position, attack, alter, SetSkill(driver), SetClass(driver));
            operators.add(operator);
        }
    }

    /**
     * Metodo para crear una lista de Skill
     * @param driver es link donde pillara los atributos de los skills
     * @return Una lista de Skill
     */
    private List<Skill> SetSkill(WebDriver driver) {
        List<Skill> lista = new ArrayList<>();
        List<WebElement> skills;
        WebElement skill;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            skill = driver.findElement(By.className("skill-section"));
            skills = skill.findElements(By.className("tab-link"));
            if (skills.get(2).getText().equals("Skill 3")) {
                for (int i = 0; i < 3; i++) {
                    if (i != 0) {
                        js.executeScript("arguments[0].click();", skills.get(i));
                    }
                    lista.add(pillarSkill(i, driver));
                }
            }
        } catch (java.lang.IndexOutOfBoundsException e) {
            try {
                skill = driver.findElement(By.className("skill-section"));
                skills = skill.findElements(By.className("tab-link"));
                if (skills.get(1).getText().equals("Skill 2")) {
                    for (int i = 0; i < 2; i++) {
                        if (i != 0) {
                            js.executeScript("arguments[0].click();", skills.get(i));
                        }
                        lista.add(pillarSkill(i, driver));
                    }
                }
            } catch (java.lang.IndexOutOfBoundsException e2) {
                int i = 0;
                lista.add(pillarSkill(i, driver));
            }
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
        }
        return lista;
    }
    /**
     * Metodo para recoger las  Skills
     * @param i es el numero del bucle en el que estan para poder localizar la posicion del skill
     * @param driver es link donde pillara los atributos de los skills
     * @return Returna una Skill que se guardara en el metodo anterior
     */
    private Skill pillarSkill(int i, WebDriver driver) {
        //Variables
        String name, charge, duration;
        boolean auto = false;
        int cost, initial;
        List<WebElement> templ;
        WebElement temp;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        temp = driver.findElement(By.id("skill-tab-" + (i + 1)));
        templ = temp.findElements(By.tagName("a"));
        name = templ.get(1).getText();
        templ = driver.findElements(By.className("sp-charge-type"));
        temp = templ.get(i);
        charge = temp.findElement(By.tagName("a")).getText();
        templ = driver.findElements(By.className("skill-activation"));
        temp = templ.get(i);
        if (!temp.findElement(By.tagName("a")).getText().equals("MANUAL")) auto = true;
        templ = driver.findElements(By.className("skill-upgrade-link"));
        try {
            js.executeScript("arguments[0].click();", templ.get(9));
            templ = driver.findElements(By.className("sp-cost"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            cost = Integer.parseInt(templ.get(9).getText());
            templ = driver.findElements(By.className("initial-sp"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            initial = Integer.parseInt(templ.get(9).getText());
            templ = driver.findElements(By.className("skill-duration"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            duration = templ.get(9).getText();
        } catch (java.lang.IndexOutOfBoundsException e) {
            js.executeScript("arguments[0].click();", templ.get(6));
            templ = driver.findElements(By.className("sp-cost"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            cost = Integer.parseInt(templ.get(6).getText());
            templ = driver.findElements(By.className("initial-sp"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            initial = Integer.parseInt(templ.get(6).getText());
            templ = driver.findElements(By.className("skill-duration"));
            temp = templ.get(i);
            templ = temp.findElements(By.className("effect-description"));
            duration = templ.get(6).getText();
        }
        return new Skill(name, charge, duration, cost, initial, auto);
    }
    /**
     * Metodo para recoger el Class
     * @param driver es link donde pillara los atributos de la class
     * @return Devuelve un Class
     */
    private Class SetClass(WebDriver driver) {
        String clas, subclass;
        List<WebElement> templ;
        templ = driver.findElements(By.className("profession-title"));
        clas = templ.get(0).getText();
        subclass = templ.get(1).getText();
        return new Class(clas, subclass);
    }

    /**
     * Metodo para crear el CSV
     */
    private void crearCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter("ArknightsScraperOperator.csv"))) {
            String[] info = {"Name", "Position", "Attact_Type", "Alter", "Class_Primary", "Class_Secondary"};
            writer.writeNext(info);
            for (Operator op : operators) {
                String[] operator = {
                        op.getName(),
                        op.getPosition(),
                        op.getAttack(),
                        String.valueOf(op.isAlter()),
                        op.getClase().getPrimary(),
                        op.getClase().getSecondary(),
                };
                writer.writeNext(operator);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("ArknightsScraperSkills.csv"))) {
            String[] info = {"Name", "Skill_Name", "Charge", "Duration", "Total_Cost", "Initial_Cost", "Auto"};
            writer.writeNext(info);
            for (Operator op : operators) {
                for (Skill skill : op.getSkill()) {
                    String[] skills = {
                            op.getName(),
                            skill.getName(),
                            skill.getCharge(),
                            skill.getDuration(),
                            String.valueOf(skill.getCost()),
                            String.valueOf(skill.getInitial()),
                            String.valueOf(skill.isAuto())
                    };
                    writer.writeNext(skills);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo para crear el XML
     * @throws ParserConfigurationException Por si hay un error en el builder
     * @throws TransformerException Por si hay un error en la tranformacion
     */
    private void crearXML() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.newDocument();

        Node rootNode = document.createElement("operators");
        document.appendChild(rootNode);

        for (Operator operator : operators) {
            Node operatorNode = document.createElement("operator");
            rootNode.appendChild(operatorNode);

            Node nameOperator = document.createElement("Name");
            nameOperator.appendChild(document.createTextNode(operator.getName()));
            operatorNode.appendChild(nameOperator);

            Node positionOperator = document.createElement("Position");
            positionOperator.appendChild(document.createTextNode(operator.getPosition()));
            operatorNode.appendChild(positionOperator);

            Node attackOperator = document.createElement("Attack");
            attackOperator.appendChild(document.createTextNode(operator.getAttack()));
            operatorNode.appendChild(attackOperator);

            Node alterOperator = document.createElement("Alter");
            alterOperator.appendChild(document.createTextNode(String.valueOf(operator.isAlter())));
            operatorNode.appendChild(alterOperator);

            Node skillsNode = document.createElement("Skills");
            operatorNode.appendChild(skillsNode);
            for (Skill skill : operator.getSkill()) {
                Node skillNode = document.createElement("Skill");
                skillsNode.appendChild(skillNode);

                Node nameSkill = document.createElement("Name");
                nameSkill.appendChild(document.createTextNode(skill.getName()));
                skillNode.appendChild(nameSkill);

                Node chargeSkill = document.createElement("Charge");
                chargeSkill.appendChild(document.createTextNode(skill.getCharge()));
                skillNode.appendChild(chargeSkill);

                Node durationSkill = document.createElement("Duration");
                durationSkill.appendChild(document.createTextNode(skill.getDuration()));
                skillNode.appendChild(durationSkill);

                Node costSkill = document.createElement("Cost");
                costSkill.appendChild(document.createTextNode(String.valueOf(skill.getCost())));
                skillNode.appendChild(costSkill);

                Node initialSkill = document.createElement("Initial");
                initialSkill.appendChild(document.createTextNode(String.valueOf(skill.getInitial())));
                skillNode.appendChild(initialSkill);

                Node autoSkill = document.createElement("Auto");
                autoSkill.appendChild(document.createTextNode(String.valueOf(skill.isAuto())));
                skillNode.appendChild(autoSkill);
            }
            Node classesNode = document.createElement("Classes");
            operatorNode.appendChild(classesNode);
            Node classNode = document.createElement("Class");
            operatorNode.appendChild(classNode);

            Node primaryClass = document.createElement("Primary");
            primaryClass.appendChild(document.createTextNode(String.valueOf(operator.getClase().getPrimary())));
            classNode.appendChild(primaryClass);

            Node secondaryClass = document.createElement("Secondary");
            secondaryClass.appendChild(document.createTextNode(String.valueOf(operator.getClase().getSecondary())));
            classNode.appendChild(secondaryClass);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("ArknightsScraper.xml"));
        transformer.transform(source, result);
    }
}