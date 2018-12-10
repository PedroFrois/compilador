package Lexical;

public class SymbolTableElement {
	private Tag tag;
	private int memory;
	private IdentifierType type;
	private int address;
	
	public SymbolTableElement(Tag tag) {
		super();
		this.tag = tag;
		this.memory = -1;
		this.type = IdentifierType.UNDEFINED;
		address = -1;
	}

	public SymbolTableElement(Tag tag, int memory, IdentifierType type) {
		super();
		this.tag = tag;
		this.memory = memory;
		this.type = type;
	}

	public Tag getTag() {
		return tag;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public IdentifierType getType() {
		return type;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public void setType(IdentifierType type) {
		this.type = type;
	}
	
	
}
