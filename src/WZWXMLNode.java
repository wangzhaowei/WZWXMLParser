import java.util.Hashtable;
import java.util.LinkedList;

public class WZWXMLNode {
	
	public String _keyString;
	
	protected String _contentString = "";
	protected WZWXMLParser.XMLNodeType _nodeType;
	protected Hashtable<String, String> _attributesHashtable;
	protected LinkedList<WZWXMLNode> _childrenNodeList = new LinkedList<WZWXMLNode>();
	protected WZWXMLNode _parentNode = null;
	
	protected void print(int indention) {
		for (int i = 0; i < indention; i++) {
			System.out.print(' ');
		}
		System.out.println(_keyString + " : " + _contentString);
		int size = _childrenNodeList.size();
		for (int i = 0; i < size; i++) {
			_childrenNodeList.get(i).print(indention + 1);
		}
	}
	
	public WZWXMLNode(WZWXMLParser.XMLNodeType nodeType){
		_nodeType = nodeType;
	}
	
	public boolean addChild(WZWXMLNode child) {
		if (child == null) {
			return false;
		}
		child._parentNode = this;
		return _childrenNodeList.add(child);
	}
	
	public String addAttribute(String key, String value) {
		if (_attributesHashtable == null) {
			_attributesHashtable = new Hashtable<String, String>();
		}
		
		return _attributesHashtable.put(key, value);
	}
	
	public String getAttributeWithKey(String key) {
		return _attributesHashtable.get(key);
	}
	
	public void appendingString(String str) {
		_contentString += str;
	}
	
	public WZWXMLParser.XMLNodeType getNodeType() {
		return _nodeType;
	}
	
	public WZWXMLNode[] getChildren() {
		return _childrenNodeList.toArray(new WZWXMLNode[0]);
	}
	
	public void printTree() {
		print(0);
	}
}
