package graphics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SVGRendererImpl implements Renderer {
	
	private List<String> lines = new ArrayList<>();
	private String fileName;
	
	public SVGRendererImpl(String fileName) {
		super();
		this.fileName = fileName;
		lines.add("<svg xlmns=\"http://www.w3.org/2000/svg\"");
		lines.add("xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
		lines.add("viewBox=\"-10 -10 800 600\">");
	}

	@Override
	public void drawLine(Point s, Point e) {
		lines.add("<line x1=\"" + s.getX() + "\" y1=\"" + s.getY()
				+ "\" x2=\"" + e.getX() + "\" y2=\"" + e.getY()
				+ "\" style=\"stroke:#0000ff\"/>");
	}

	@Override
	public void fillPolygon(Point[] points) {
		StringBuilder sb = new StringBuilder();
		sb.append("<polygon points=\"");
		for (Point p : points) {
			sb.append(p.getX()).append(',').append(p.getY()).append(' ');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\" style=\"stroke:#ff0000; fill:#0000ff;\"/>");
		lines.add(sb.toString());
	}
	
	public void close() throws IOException {
		lines.add("</svg>");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			for (String s : lines) {
				writer.write(s);
				writer.write('\n');
			}
		} 
	}

}
