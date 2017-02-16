package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ModuroXmlHelperImpl implements ModuroXmlHelper {

   private DocumentBuilderFactory documentBuilderFactory;

   public ModuroXmlHelperImpl() {
      this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
   }

   /***
    * Generates a .cc3dFile that links to the ModuroPythonFile. Also will append a tag to the cc3d file (as child of the
    * simulation tag) <ModelJsonPath></ModelJsonPath>
    *
    * @param cc3dFile
    * @param modelJsonFile
    * @param moduroRunJsonPythonScriptPath
    * @return
    * @throws ParserConfigurationException
    */
   @Override
   public void generateCompucellSimulationFile(File cc3dFile, File modelJsonFile, File moduroRunJsonPythonScriptPath) { // https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/

      try {
         final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         final InputStream simTemplate = this.getClass().getClassLoader().getResourceAsStream("simulationFileTemplate.cc3d");
         final Document cc3dFileTemplateDoc = documentBuilder.parse(simTemplate);
         final NodeList simulationNodeList = cc3dFileTemplateDoc.getElementsByTagName("Simulation");

         if (simulationNodeList.getLength() == 0) {
            throw new RuntimeException(
                  "Invalid simulationFileTemplate.cc3d content. Nodelist of <Simulation>-Tag is empty");
         }

         // Yes, we could just add the ModelJsonPath tag to the simulationFileTemplate.cc3d but in case one
         // Changes the template for any reasons we better append it. This way it would be easier to replace the
         // cc3d-Template in the future.
         final Element modelJsonElement = cc3dFileTemplateDoc.createElement("ModelJsonPath");
         modelJsonElement.setNodeValue(modelJsonFile.getAbsolutePath());

         // There can be only one node
         final NodeList pythonScriptNodes = cc3dFileTemplateDoc.getElementsByTagName("PythonScript");
         if (pythonScriptNodes.getLength() != 1) {
            throw new RuntimeException("Invalid Nodecount for Tag <Simulation>: " + pythonScriptNodes.getLength()
                  + ". Required Count is : 1");
         }

         final Node pythonScriptNode = pythonScriptNodes.item(0);
         pythonScriptNode.setTextContent(moduroRunJsonPythonScriptPath.getAbsolutePath());
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         final Transformer transformer = transformerFactory.newTransformer();
         DOMSource domSource = new DOMSource(cc3dFileTemplateDoc);
         final StreamResult streamResult = new StreamResult(cc3dFile);
         transformer.transform(domSource, streamResult);
         System.out.println("cc3d File saved: " + cc3dFile.getAbsolutePath());

      } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

}
