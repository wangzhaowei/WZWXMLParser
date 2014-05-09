package WZWXML;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

public class WZWXMLSerializer {
	
	protected WZWXMLNode _rootNode;
	
	public WZWXMLSerializer(WZWXMLNode rootNode){
		_rootNode = rootNode;
	}
	
 	static protected String serializeAttributes(Hashtable<String, String> attrHashtable) {
		StringBuffer attrStringBuffer = new StringBuffer();
		Set<String> allKeysSet = attrHashtable.keySet();
		for (String key : allKeysSet) {
			attrStringBuffer.append(key + "=" + "\"" + attrHashtable.get(key) + "\"" + " ");
		}
		
		attrStringBuffer.deleteCharAt(attrStringBuffer.length() - 1);
		
		return attrStringBuffer.toString();
	}
	
	static protected String serializeXMLNode(WZWXMLNode node) {
		StringBuffer xmlStringBuffer = new StringBuffer();
		xmlStringBuffer.append("<" + node.getKey());
		if (node._attributesHashtable != null) {
			xmlStringBuffer.append(" " + serializeAttributes(node._attributesHashtable));
		}
		xmlStringBuffer.append(">");
		if (node._contentString != null) {
			xmlStringBuffer.append(node._contentString);
		}
		WZWXMLNode[] children = node.getChildren();
		for (int i = 0; i < children.length; i++) {
			xmlStringBuffer.append(serializeXMLNode(children[i]));
		}
		xmlStringBuffer.append("</" + node.getKey() + ">");
		
		return xmlStringBuffer.toString();
	}
	
	public String serialize() {
		StringBuffer xmlStringBuffer = new StringBuffer();
		switch (_rootNode.getNodeType()) {
		case TYPE_ROOT:{
			xmlStringBuffer.append("<?" + _rootNode.getKey() + " " + serializeAttributes(_rootNode._attributesHashtable) + "?>");
			WZWXMLNode[] children = _rootNode.getChildren();
			for (int i = 0; i < children.length; i++) {
				xmlStringBuffer.append(serializeXMLNode(children[i]));
			}
			break;
		}
		case TYPE_NODE:{
			xmlStringBuffer.append(serializeXMLNode(_rootNode));
		}
		default:
			break;
		}
		
		return xmlStringBuffer.toString();
	}
	
	public void serializeToFile(String path) throws IOException {
		File xmlFile = new File(path);
		if (xmlFile != null) {
			FileWriter fw = new FileWriter(xmlFile);
			fw.write(serialize());
			fw.close();
		}		
	}
}
