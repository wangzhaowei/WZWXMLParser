import java.util.Hashtable;


public class WZWXMLParser {
	public enum XMLNodeState{
		ST_BEGIN,
		ST_COMMENT,
		ST_TAG_START,
		ST_TAG_ATTRIBUTE,
		ST_TAG,
		ST_TAG_END,
		ST_CONTENT_START,
		ST_CONTENT,
		ST_CONTENT_END,
		ST_CHILD,
		ST_END_TAG_START,
		ST_END_TAG,
		ST_END_TAG_END,
		ST_PARSE_END
	}
	
	public enum XMLNodeType{
		TYPE_ROOT,
		TYPE_XML,
		TYPE_NODE,
		TYPE_COMMENT
	}
			
	protected char[] _XMLContent = null;
	protected Hashtable<String, String> _XMLHash;
	protected WZWXMLNode xmlRootNode = new WZWXMLNode(XMLNodeType.TYPE_ROOT);
	
	private int _currentIndex = 0;
	
	public WZWXMLParser(char[] XMLContent){
		_XMLContent = XMLContent;
	}
	
	protected WZWXMLNode loadNode(char[] XMLContent) throws Exception {
		int beginIndex = 0;
		int endIndex = 0;
		XMLNodeState parserState = XMLNodeState.ST_BEGIN;
		boolean isContinue = true;
		WZWXMLNode node = null;
		while (XMLContent[_currentIndex] != '\0' && isContinue) {
			switch (parserState) {
			case ST_BEGIN:{
				if (XMLContent[_currentIndex] == '<') {
					if (XMLContent[_currentIndex + 1] == '?') {
						_currentIndex++;
						parserState = XMLNodeState.ST_TAG_START;
						node = new WZWXMLNode(XMLNodeType.TYPE_XML);
					}
					else if (XMLContent[_currentIndex + 1] == '!' && XMLContent[_currentIndex + 2] == '-' && XMLContent[_currentIndex + 2] == '-' ) {
						_currentIndex += 3;
						parserState = XMLNodeState.ST_COMMENT;
						node = new WZWXMLNode(XMLNodeType.TYPE_COMMENT);
					}
					else{
						parserState = XMLNodeState.ST_TAG_START;
						node = new WZWXMLNode(XMLNodeType.TYPE_NODE);
					}
				}
				break;
			}
			case ST_COMMENT:{
				throw new Exception("ST_COMMENT DID NOT IMPLEMENTATION");
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
				if ((XMLContent[_currentIndex] == '?' && XMLContent[_currentIndex + 1] == '>' && node.getNodeType() == XMLNodeType.TYPE_XML) ||
						(XMLContent[_currentIndex] == '>' && node.getNodeType() == XMLNodeType.TYPE_NODE)) {
					parserState = XMLNodeState.ST_TAG_END;
					endIndex = _currentIndex - 1;
					node._keyString = new String(XMLContent, beginIndex, endIndex - beginIndex + 1);
				}
				break;
			}
			case ST_TAG_ATTRIBUTE:{
				throw new Exception("ST_TAG_ATTRIBUTE DID NOT IMPLEMENTATION");
//				if (_XMLContent[currentIndex] == '?' && _XMLContent[currentIndex + 1] == '>' && node.getNodeType() == XMLNodeType.TYPE_XML) {
//					nodeState = XMLNodeState.ST_TAG_END;
//					currentIndex++;
//				}
//				else if (_XMLContent[currentIndex] == '>' && node.getNodeType() == XMLNodeType.TYPE_NODE) {
//					nodeState = XMLNodeState.ST_TAG_END;
//				}
//				break;
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
						parserState = XMLNodeState.ST_CONTENT_END;
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
			case ST_CONTENT_END:{
				parserState = XMLNodeState.ST_END_TAG_START;
				_currentIndex--;
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
						parserState = XMLNodeState.ST_END_TAG_END;
					}
				}
				break;
			}
			case ST_END_TAG_END:{
				parserState = XMLNodeState.ST_PARSE_END;
				_currentIndex--;
				break;
			}
			case ST_PARSE_END:{
				isContinue = false;
				_currentIndex--;
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
		xmlRootNode = new WZWXMLNode(XMLNodeType.TYPE_ROOT);
		xmlRootNode.addChild(loadNode(_XMLContent));
		return xmlRootNode;
	}
}
