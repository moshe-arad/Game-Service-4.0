package org.moshe.arad.backgammon.json;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BoardItemJson {

	private String sign;
	private int count;
	private int columnIndex;
	
	public BoardItemJson() {
	
	}

	public BoardItemJson(String sign, int count, int columnIndex) {
		super();
		this.sign = sign;
		this.count = count;
		this.columnIndex = columnIndex;
	}

	@Override
	public String toString() {
		return "BoardItem [sign=" + sign + ", count=" + count + ", columnIndex=" + columnIndex + "]";
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
}
