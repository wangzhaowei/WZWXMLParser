package WZWXML;
import java.util.Hashtable;

public class WZWXMLParser {
	public enum XMLNodeState{
		ST_BEGIN,
		ST_COMMENT,
		ST_TAG_START,
		ST_TAG_ATTRIBUTE_NAME_START,
		ST_TAG_ATTRIBUTE_NAME_END,
		ST_TAG_ATTRIBUTE_VALUE_START,
		ST_TAG_ATTRIBUTE_VALUE_END,
		ST_TAG,
		ST_TAG_END,
		ST_CONTENT_START,
		ST_CONTENT,
		ST_CHILD,
		ST_END_TAG_START,
		ST_END_TAG,
	}
	
	public enum XMLNodeType{
		TYPE_ROOT,
		TYPE_NODE,
		TYPE_COMMENT
	}
			
	protected char[] _XMLContent = null;
	protected Hashtable<String, String> _XMLHash;
	protected WZWXMLNode _xmlRootNode = new WZWXMLNode(XMLNodeType.TYPE_ROOT);
	
	private int _currentIndex = 0;
	
	public WZWXMLParser(char[] XMLContent){
		_XMLContent = XMLContent;
	}
	
	protected void setNodeKey(WZWXMLNode node, int beginIndex, int endIndex) {
		String key = new String(_XMLContent, beginIndex, endIndex - beginIndex + 1);
		if (key != null && key.length() > 0) {
			node._keyString = key;
		}
	}
	
	protected WZWXMLNode loadNode(char[] XMLContent) throws Exception {
		int beginIndex = 0;
		int endIndex = 0;
		XMLNodeState parserState = XMLNodeState.ST_BEGIN;
		boolean isContinue = true;
		WZWXMLNode node = null;
		String attrKey = null;
		while (XMLContent[_currentIndex] != '\0' && isContinue) {
			switch (parserState) {
			case ST_BEGIN:{
				if (XMLContent[_currentIndex] == '<') {
					if (XMLContent[_currentIndex + 1] == '?') {
						_currentIndex++;
						parserState = XMLNodeState.ST_TAG_START;
						node = _xmlRootNode;
					}
					else if (XMLContent[_currentIndex + 1] == '!' && XMLContent[_currentIndex + 2] == '-' && XMLContent[_currentIndex + 3] == '-' ) {
						_currentIndex += 3;
						parserState = XMLNodeState.ST_COMMENT;
					}
					else{
						parserState = XMLNodeState.ST_TAG_START;
						node = new WZWXMLNode(XMLNodeType.TYPE_NODE);
					}
				}
				break;
			}
			case ST_COMMENT:{
				if (_XMLContent[_currentIndex] == '-' && _XMLContent[_currentIndex + 1] == '-' && _XMLContent[_currentIndex + 2] == '>') {
					_currentIndex += 2;
				}
				break;
			}
			case ST_TAG_START:{
				if (XMLContent[_currentIndex] != ' ') {
					parserState = XMLNodeState.ST_TAG;
					beginIndex = _currentIndex;
					endIndex = _currentIndex;
					_currentIndex--;
				}
				break;
			}
			case ST_TAG:{
				if (XMLContent[_currentIndex] == ' ') {
					parserState = XMLNodeState.ST_TAG_ATTRIBUTE_NAME_START;
					endIndex = _currentIndex - 1;
					setNodeKey(node, beginIndex, endIndex);
				}
				else if (XMLContent[_currentIndex] == '?' && XMLContent[_currentIndex + 1] == '>' && node.getNodeType() == XMLNodeType.TYPE_ROOT) {
					parserState = XMLNodeState.ST_TAG_END;
					endIndex = _currentIndex - 1;
					setNodeKey(node, beginIndex, endIndex);
					_currentIndex++;
				}
				else if (XMLContent[_currentIndex] == '>' && node.getNodeType() == XMLNodeType.TYPE_NODE) {
					parserState = XMLNodeState.ST_TAG_END;
					endIndex = _currentIndex - 1;
					setNodeKey(node, beginIndex, endIndex);
				}
				break;
			}
			case ST_TAG_ATTRIBUTE_NAME_START:{
				if (_XMLContent[_currentIndex] != ' ') {
					if (_XMLContent[_currentIndex] == '>' && node.getNodeType() == XMLNodeType.TYPE_NODE) {
						parserState = XMLNodeState.ST_TAG_END;
					}
					else if (_XMLContent[_currentIndex] == '?' && _XMLContent[_currentIndex + 1] == '>' && node.getNodeType() == XMLNodeType.TYPE_ROOT) {
						parserState = XMLNodeState.ST_TAG_END;
						_currentIndex++;
					}
					else {
						beginIndex = _currentIndex;
						endIndex = _currentIndex;
						parserState = XMLNodeState.ST_TAG_ATTRIBUTE_NAME_END;
						_currentIndex--;
					}
				}
				break;
			}
			case ST_TAG_ATTRIBUTE_NAME_END:{
				if (_XMLContent[_currentIndex] == '=') {
					endIndex = _currentIndex - 1;
					attrKey = new String(_XMLContent, beginIndex, endIndex - beginIndex + 1);
					parserState = XMLNodeState.ST_TAG_ATTRIBUTE_VALUE_START;
				}
				break;
			}
			case ST_TAG_ATTRIBUTE_VALUE_START:{
				if (_XMLContent[_currentIndex] == '"') {
					beginIndex = _currentIndex + 1;
					endIndex = _currentIndex + 1;
					parserState = XMLNodeState.ST_TAG_ATTRIBUTE_VALUE_END;
				}
				break;
			}
			case ST_TAG_ATTRIBUTE_VALUE_END:{
				if (_XMLContent[_currentIndex] == '"') {
					endIndex = _currentIndex - 1;
					node.addAttribute(attrKey, new String(_XMLContent, beginIndex, endIndex - beginIndex + 1));
					parserState = XMLNodeState.ST_TAG_ATTRIBUTE_NAME_START;
				}
				break;
			}
			case ST_TAG_END:{
				parserState = WZWXMLParser.XMLNodeState.ST_CONTENT_START;
				_currentIndex--;
				break;
			}
			case ST_CONTENT_START:{
				beginIndex = _currentIndex;
				endIndex = _currentIndex;
				parserState = XMLNodeState.ST_CONTENT;
				_currentIndex--;
				break;
			}
			case ST_CONTENT:{
				if (XMLContent[_currentIndex] == '<' ) {
					endIndex = _currentIndex - 1;
					String textStr = new String(XMLContent, beginIndex, endIndex - beginIndex + 1);
					if (textStr != null && textStr.length() > 0) {
						node.appendingString(textStr);
					}
					
					if (XMLContent[_currentIndex + 1] == '/' && node._nodeType == XMLNodeType.TYPE_NODE) {
						parserState = XMLNodeState.ST_END_TAG_START;
						_currentIndex++;
					}
					else {
						parserState = XMLNodeState.ST_CHILD;
						_currentIndex--;
					}
				}
				break;
			}
			case ST_CHILD:{
				WZWXMLNode child = loadNode(XMLContent);
				node.addChild(child);
				_currentIndex--;
				parserState = XMLNodeState.ST_CONTENT_START;
				break;
			}
			case ST_END_TAG_START:{
				beginIndex = _currentIndex;
				endIndex = _currentIndex;
				parserState = XMLNodeState.ST_END_TAG;
				_currentIndex--;
				break;
			}
			case ST_END_TAG:{
				if ('>' == XMLContent[_currentIndex]) {
					endIndex = _currentIndex - 1;
					String endTagString = new String(XMLContent, beginIndex, endIndex - beginIndex + 1);
					if (endTagString.equals(node._keyString)) {
						isContinue = false;
					}
				}
				break;
			}
			default:
				break;
			}
			_currentIndex++;
		}
		
		return node;
	}
	
	public WZWXMLNode parse() throws Exception {
		_currentIndex = 0;
		loadNode(_XMLContent);
		return _xmlRootNode;
	}
}
