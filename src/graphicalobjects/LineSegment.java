package graphicalobjects;

import java.util.List;
import java.util.Stack;

import graphics.AbstractGraphicalObject;
import graphics.GeometryUtil;
import graphics.GraphicalObject;
import graphics.Point;
import graphics.Rectangle;
import graphics.Renderer;

public class LineSegment extends AbstractGraphicalObject {
	
	public LineSegment() {
		super(new Point[] {new Point(0,0), new Point(30,30)});
	}
	
	public LineSegment(Point s, Point e) {
		super(new Point[] {s, e});
	}

	@Override
	public Rectangle getBoundingBox() {
		Point hps = getHotPoint(0);
		Point hpe = getHotPoint(1);
		int xs = hps.getX(), ys = hps.getY();
		int xe = hpe.getX(), ye = hpe.getY();
		return new Rectangle(Math.min(xs, xe), Math.min(ys, ye), Math.abs(xs - xe), Math.abs(ys - ye));
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		return GeometryUtil.distanceFromLineSegment(getHotPoint(0),	getHotPoint(1), mousePoint);
	}

	@Override
	public void render(Renderer r) {
		r.drawLine(getHotPoint(0), getHotPoint(1));
	}

	@Override
	public String getShapeName() {
		return "Linija";
	}

	@Override
	public GraphicalObject duplicate() {
		return new LineSegment(getHotPoint(0), getHotPoint(1));
	}

	@Override
	public String getShapeID() {
		return "@LINE";
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		String[] args = data.split("[ ]+");
		Point s = new Point(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		Point e = new Point(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		stack.push(new LineSegment(s, e));
	}

	@Override
	public void save(List<String> rows) {
		rows.add(getShapeID() + " " + getHotPoint(0).getX() + " " + getHotPoint(0).getY()
				+ " " + getHotPoint(1).getX() + " " + getHotPoint(1).getY());
	}

}
