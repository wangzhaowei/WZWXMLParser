import java.util.LinkedList;

public class WZWXMLNode {
	
	public String _keyString;
	
	protected String _contentString = "";
	protected WZWXMLParser.XMLNodeType _nodeType;
	protected LinkedList<WZWXMLNode> _childrenNodeList = new LinkedList<WZWXMLNode>();
	protected WZWXMLNode _parentNode = null;
	
	public WZWXMLNode(WZWXMLParser.XMLNodeType nodeType){
		_nodeType = nodeType;
	}
	
	public WZWXMLNode(WZWXMLParser.XMLNodeType nodeType, WZWXMLNode parentNode){
		_nodeType = nodeType;
		_parentNode = parentNode;
	}
	
	public boolean addChild(WZWXMLNode child) {
		if (child == null) {
			return false;
		}
		child._parentNode = this;
		return _childrenNodeList.add(child);
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
}
