package powermock.examples.spring;

import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedList;
import java.util.List;

@Repository
public class CompanyRepository {

    public String[] getAllEmployees() {
        try {
            final List<String> employees = new LinkedList<String>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(getClass().getResourceAsStream("/employees.xml"));
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("employee");
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("firstname");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();
                    final String firstName = fstNm.item(0).getNodeValue();
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("lastname");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();
                    final String lastName = lstNm.item(0).getNodeValue();
                    employees.add(String.format("%s %s", firstName, lastName));
                }
            }
            return employees.toArray(new String[employees.size()]);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
