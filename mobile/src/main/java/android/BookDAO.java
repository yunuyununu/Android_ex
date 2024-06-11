package android;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sqlmap.MybatisManager;

public class BookDAO {
	public List<BookDTO> list() {
		SqlSession session = MybatisManager.getInstance().openSession();
		List<BookDTO> items = session.selectList("book.list");
		session.close();
		return items;
	}
	
	public String xml() {
		Element root = new Element("books");
		Document doc = new Document(root);
		doc.setRootElement(root);
		List<BookDTO> items = list();
		for (BookDTO dto : items) {
			Element data = new Element("book");
			Element book_code = new Element("book_code");
			book_code.setText(dto.getBook_code() + "");
			Element book_name = new Element("book_name");
			book_name.setText(dto.getBook_name());
			Element press = new Element("press");
			press.setText(dto.getPress());
			Element price = new Element("price");
			price.setText(dto.getPrice() + "");
			Element amount = new Element("amount");
			amount.setText(dto.getAmount() + "");
			data.addContent(book_code);
			data.addContent(book_name);
			data.addContent(press);
			data.addContent(price);
			data.addContent(amount);
			root.addContent(data);
		}
		XMLOutputter xout = new XMLOutputter();
		Format f = xout.getFormat();
		f.setEncoding("utf-8");
		f.setIndent("\t");
		f.setLineSeparator("\r\n");
		f.setTextMode(Format.TextMode.TRIM);
		xout.setFormat(f);
		return xout.outputString(doc);
	}
	
	public String json() {
		List<BookDTO> items = list();
		JSONObject jsonMain = new JSONObject();
		JSONArray jArray = new JSONArray();
		int count = 0;
		for (BookDTO dto : items) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("book_code", dto.getBook_code());
			jsonObj.put("book_name", dto.getBook_name());
			jsonObj.put("press", dto.getPress());
			jsonObj.put("price", dto.getPrice());
			jsonObj.put("amount", dto.getAmount());
			jArray.add(count, jsonObj);
			count++;
		}
		jsonMain.put("sendData", jArray);
		return jsonMain.toString();

	}
}
