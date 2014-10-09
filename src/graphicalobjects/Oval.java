package graphicalobjects;

import java.util.List;
import java.util.Stack;

import math.geom2d.Point2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.line.LineSegment2D;
import graphics.AbstractGraphicalObject;
import graphics.GraphicalObject;
import graphics.Point;
import graphics.Rectangle;
import graphics.Renderer;

public class Oval extends AbstractGraphicalObject {
	
	private static final int EDGE_COUNT = 10;
	private Point center;
	
	public Oval() {
		super(new Point[] {new Point(10, 0), new Point(0, 10)});
		center = new Point(0, 0);
	}
	
	public Oval(Point right, Point bottom) {
		super(new Point[] {right, bottom});
		center = new Point(bottom.getX(), right.getY());
	}

	@Override
	public Rectangle getBoundingBox() {
		int rx = getHotPoint(0).getX();
		int by = getHotPoint(1).getY();
		int width = (rx - center.getX()) * 2;
		int height = (by - center.getY()) * 2;
		return new Rectangle(rx - width, by - height, width, height);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		int x0 = center.getX();
		int y0 = center.getY();
		int a = getHotPoint(0).getX() - x0;
		int b = getHotPoint(1).getY() - y0;
		Ellipse2D ellipse = new Ellipse2D(x0, y0, a, b);
		if (ellipse.isInside(new Point2D(mousePoint.getX(), mousePoint.getY()))) {
			return 0.0;
		}
		return ellipse.distance(mousePoint.getX(), mousePoint.getY());
	}

	@Override
	public void render(Renderer r) {
		int x0 = center.getX();
		int y0 = center.getY();
		int a = getHotPoint(0).getX() - x0;
		int b = getHotPoint(1).getY() - y0;
		Ellipse2D ellipse = new Ellipse2D(x0, y0, a, b);
		Point[] points = new Point[EDGE_COUNT];
		int i = 0;
		for (LineSegment2D s : ellipse.asPolyline(EDGE_COUNT).edges()) {
			Point2D p = s.firstPoint();
			points[i] = new Point((int)p.getX(), (int)p.getY());
			i++;
		}
		r.fillPolygon(points);
	}

	@Override
	public String getShapeName() {
		return "Oval";
	}
	
	@Override
	public void setHotPoint(int index, Point point) {
		if (index == 0 && (point.getX() < getHotPoint(1).getX() || point.getY() > getHotPoint(1).getY())) {
			return;
		}
		if (index == 1 && (point.getY() < getHotPoint(0).getY() || point.getX() > getHotPoint(0).getX())) {
			return;
		}
		super.setHotPoint(index, point);
		center = new Point(getHotPoint(1).getX(), getHotPoint(0).getY());
	}
	
	@Override
	public void translate(Point delta) {
		center = center.translate(delta);
		super.translate(delta);
	}

	@Override
	public GraphicalObject duplicate() {
		return new Oval(getHotPoint(0), getHotPoint(1));
	}

	@Override
	public String getShapeID() {
		return "@OVAL";
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		String[] args = data.split("[ ]+");
		Point right = new Point(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		Point bottom = new Point(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		stack.push(new Oval(right, bottom));
	}

	@Override
	public void save(List<String> rows) {
		rows.add(getShapeID() + " " + getHotPoint(0).getX() + " " + getHotPoint(0).getY()
				+ " " + getHotPoint(1).getX() + " " + getHotPoint(1).getY());
	}

}
