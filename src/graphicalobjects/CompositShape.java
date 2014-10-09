package graphicalobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import graphics.GraphicalObject;
import graphics.GraphicalObjectListener;
import graphics.Point;
import graphics.Rectangle;
import graphics.Renderer;

public class CompositShape implements GraphicalObject {

	private List<GraphicalObject> children;
	private boolean selected = false;
	private List<GraphicalObjectListener> listeners = new ArrayList<>();
	
	public CompositShape() {
		super();
	}
	
	public CompositShape(List<GraphicalObject> children) {
		super();
		this.children = new ArrayList<>(children);
	}
	
	public List<GraphicalObject> getChildren() {
		return children;
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			notifySelectionListeners();
		}
	}

	@Override
	public int getNumberOfHotPoints() {
		return 0;
	}

	@Override
	public Point getHotPoint(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHotPoint(int index, Point point) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isHotPointSelected(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHotPointSelected(int index, boolean selected) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getHotPointDistance(int index, Point mousePoint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(Point delta) {
		for (GraphicalObject o : children) {
			o.translate(delta);
		}
		notifyListeners();
	}

	@Override
	public Rectangle getBoundingBox() {
		Rectangle tmp = children.get(0).getBoundingBox();
		int x0 = tmp.getX(), y0 = tmp.getY();
		int x1 = tmp.getWidth() + x0, y1 = tmp.getHeight() + y0;
		for (GraphicalObject o : children) {
			Rectangle r = o.getBoundingBox();
			int xr = r.getX(), yr = r.getY(), wr = r.getWidth(), hr = r.getHeight();
			if (xr < x0) 	  { x0 = xr; }
			if (yr < y0) 	  { y0 = yr; }
			if (xr + wr > x1) { x1 = xr + wr; }
			if (yr + hr > y1) { y1 = yr + hr; }
		}
		return new Rectangle(x0, y0, x1 - x0, y1 - y0);
	}

	@Override
	public double selectionDistance(Point mousePoint) {
		double min = children.get(0).selectionDistance(mousePoint);
		for (GraphicalObject o : children) {
			double dist = o.selectionDistance(mousePoint);
			if (dist < min) {
				min = dist;
			}
		}
		return min;
	}

	@Override
	public void render(Renderer r) {
		for (GraphicalObject o : children) {
			o.render(r);
		}
	}

	@Override
	public void addGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);
	}

	@Override
	public void removeGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.remove(l);
	}

	@Override
	public String getShapeName() {
		return "Kompozit";
	}

	@Override
	public GraphicalObject duplicate() {
		return new CompositShape(children);
	}

	@Override
	public String getShapeID() {
		return "@COMP";
	}

	@Override
	public void load(Stack<GraphicalObject> stack, String data) {
		int n = Integer.parseInt(data.trim());
		List<GraphicalObject> objects = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			objects.add(stack.pop());
		}
		stack.push(new CompositShape(objects));
	}

	@Override
	public void save(List<String> rows) {
		for (GraphicalObject o : children) {
			o.save(rows);
		}
		rows.add(getShapeID() + " " + children.size());
	}
	
	protected void notifyListeners() {
		for (GraphicalObjectListener gol : listeners) {
			gol.graphicalObjectChanged(this);
		}
	}
	
	protected void notifySelectionListeners() {
		for (GraphicalObjectListener gol : listeners) {
			gol.graphicalObjectSelectionChanged(this);
		}
	}
}
