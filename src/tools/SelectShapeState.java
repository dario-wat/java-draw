package tools;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import model.DocumentModel;
import graphicalobjects.CompositShape;
import graphics.GraphicalObject;
import graphics.Point;
import graphics.Rectangle;
import graphics.Renderer;

public class SelectShapeState implements State {

	private DocumentModel model;
	private GraphicalObject currentObj = null;
	private int currentHP = -1;
	
	public SelectShapeState(DocumentModel model) {
		super();
		this.model = model;
	}
	
	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		GraphicalObject selected = model.findSelectedGraphicalObject(mousePoint);
		currentHP = -1;
		currentObj = null;
		if (selected != null) {
			int index = model.findSelectedHotPoint(selected, mousePoint);
			if (index != -1) {
				currentHP = index;
				currentObj = selected;
			}
		}
		
		if (ctrlDown) {
			if (selected == null) {
				return;
			}
			selected.setSelected(true);
			if (model.getSelectedObjects().size() >= 2) {
				currentHP = -1;
				currentObj = null;
			}
		} else {
			deselectAll();
			if (selected == null) {
				return;
			}
			
			int n = selected.getNumberOfHotPoints();
			for (int i = 0; i < n; i++) {
				selected.setHotPointSelected(i, true);
			}
			selected.setSelected(true);
		}
	}

	private void deselectAll() {
		List<GraphicalObject> objects = new ArrayList<>(model.getSelectedObjects());
		int size = objects.size();
		for (int j = 0; j < size; j++) {
			GraphicalObject o = objects.get(j);
			int n = o.getNumberOfHotPoints();
			for (int i = 0; i < n; i++) {
				o.setHotPointSelected(i, false);
			}
			o.setSelected(false);
		}
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		if (currentHP == -1) {
			return;
		}
		currentObj.setHotPoint(currentHP, mousePoint);
	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_UP) {
			translateAll(new Point(0, -1));
		} else if (keyCode == KeyEvent.VK_LEFT) {
			translateAll(new Point(-1, 0));
		} else if (keyCode == KeyEvent.VK_DOWN) {
			translateAll(new Point(0, 1));
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			translateAll(new Point(1, 0));
		} else if (keyCode == KeyEvent.VK_PLUS) {
			if (model.getSelectedObjects().size() != 1) {
				return;
			}
			GraphicalObject object = model.getSelectedObjects().get(0);
			model.increaseZ(object);
		} else if (keyCode == KeyEvent.VK_MINUS) {
			if (model.getSelectedObjects().size() != 1) {
				return;
			}
			GraphicalObject object = model.getSelectedObjects().get(0);
			model.decreaseZ(object);
		} else if (keyCode == KeyEvent.VK_G) {
			List<GraphicalObject> sel = new ArrayList<>(model.getSelectedObjects());
			if (sel.size() == 0) {
				return;
			}
			for (GraphicalObject o : sel) {
				model.removeGraphicalObject(o);
			}
			GraphicalObject composit = new CompositShape(sel);	// TODO
			model.addGraphicalObject(composit);
			composit.setSelected(true);
		} else if (keyCode == KeyEvent.VK_U) {
			List<GraphicalObject> sel = new ArrayList<>(model.getSelectedObjects());
			if (sel.size() == 1 && sel.get(0) instanceof CompositShape) {
				CompositShape obj = (CompositShape) sel.get(0);
				List<GraphicalObject> children = obj.getChildren();
				model.removeGraphicalObject(obj);
				
				for (GraphicalObject o : children) {
					model.addGraphicalObject(o);
					o.setSelected(true);
				}
			}
		}
	}
	
	private void translateAll(Point t) {
		for (GraphicalObject o : model.getSelectedObjects()) {
			o.translate(t);
		}
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
		if (!go.isSelected()) {
			return;
		}
		Rectangle rect = go.getBoundingBox();
		int x = rect.getX(), y = rect.getY(), w = rect.getWidth(), h = rect.getHeight();
		Point a = new Point(x, y);
		Point b = new Point(x + w, y);
		Point c = new Point(x + w, y + h);
		Point d = new Point(x, y + h);
		r.drawLine(a, b);
		r.drawLine(b, c);
		r.drawLine(c, d);
		r.drawLine(d, a);
	}

	@Override
	public void afterDraw(Renderer r) {
		if (model.getSelectedObjects().size() != 1) {
			return;
		}
		GraphicalObject obj = model.getSelectedObjects().get(0); 
		int n = obj.getNumberOfHotPoints();
		for (int i = 0; i < n; i++) {
			Point p = obj.getHotPoint(i);
			int x = p.getX(), y = p.getY();
			final int diff = 3;
			Point a = new Point(x - diff, y - diff);
			Point b = new Point(x - diff, y + diff);
			Point c = new Point(x + diff, y + diff);
			Point d = new Point(x + diff, y - diff);
			r.drawLine(a, b);
			r.drawLine(b, c);
			r.drawLine(c, d);
			r.drawLine(d, a);
		}
	}

	@Override
	public void onLeaving() {
		deselectAll();
	}

}
