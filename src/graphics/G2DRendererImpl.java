package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

public class G2DRendererImpl implements Renderer {

	private Graphics2D g2d;
	
	public G2DRendererImpl(Graphics2D g2d) {
		super();
		this.g2d = g2d;
	}
	
	@Override
	public void drawLine(Point s, Point e) {
		g2d.setColor(Color.BLUE);
		g2d.drawLine(s.getX(), s.getY(), e.getX(), e.getY());
	}

	@Override
	public void fillPolygon(Point[] points) {
		int n = points.length;
		int[] xPoints = new int[n];
		int[] yPoints = new int[n];
		for (int i = 0; i < n; i++) {
			xPoints[i] = points[i].getX();
			yPoints[i] = points[i].getY();
		}
		g2d.setColor(Color.BLUE);
		g2d.fillPolygon(xPoints, yPoints, n);
		g2d.setColor(Color.RED);
		g2d.drawPolygon(xPoints, yPoints, n);
	}

}
