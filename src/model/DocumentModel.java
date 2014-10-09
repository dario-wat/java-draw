package model;

import graphics.GraphicalObject;
import graphics.GraphicalObjectListener;
import graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentModel {

	public static final double SELECTION_PROXIMITY = 10;
	
	private List<GraphicalObject> objects = new ArrayList<>();
	private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
	private List<DocumentModelListener> listeners = new ArrayList<>();
	private List<GraphicalObject> selectedObjects = new ArrayList<>();
	private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);
	
	private final GraphicalObjectListener goListener = new GraphicalObjectListener() {
		
		@Override
		public void graphicalObjectSelectionChanged(GraphicalObject go) {
			if (go.isSelected() && !selectedObjects.contains(go)) {
				selectedObjects.add(go);
			} else if (!go.isSelected() && selectedObjects.contains(go)) {
				selectedObjects.remove(go);
			}
			notifyListeners();
		}
		
		@Override
		public void graphicalObjectChanged(GraphicalObject go) {
			notifyListeners();
		}
	};
	
	public DocumentModel() {
		super();
	}
	
	public void clear() {
		for (GraphicalObject o : objects) {
			o.removeGraphicalObjectListener(goListener);
		}
		objects.clear();
		notifyListeners();
	}
	
	public void addGraphicalObject(GraphicalObject obj) {
		if (obj.isSelected()) {
			obj.setSelected(false);
		}
		obj.addGraphicalObjectListener(goListener);
		objects.add(obj);
		notifyListeners();
	}
	
	public void removeGraphicalObject(GraphicalObject obj) {
		if (obj.isSelected()) {
			obj.setSelected(false);
		}
		obj.removeGraphicalObjectListener(goListener);
		if (objects.remove(obj)) {
			notifyListeners();
		}
	}
	
	public List<GraphicalObject> list() {
		return roObjects;
	}
	
	public void addDocumentModelListener(DocumentModelListener l) {
		listeners.add(l);
	}
	
	public void removeDocumentModelListener(DocumentModelListener l) {
		listeners.remove(l);
	}
	
	public void notifyListeners() {
		for (DocumentModelListener l : listeners) {
			l.documentChange();
		}
	}
	
	public List<GraphicalObject> getSelectedObjects() {
		return roSelectedObjects;
	}
	
	public void increaseZ(GraphicalObject obj) {
		if (objects.contains(obj)) {
			int index = objects.indexOf(obj);
			if (index != objects.size()-1) {
				objects.remove(obj);
				objects.add(index+1, obj);
				notifyListeners();
			}
		}
	}
	
	public void decreaseZ(GraphicalObject obj) {
		if (objects.contains(obj)) {
			int index = objects.indexOf(obj);
			if (index != 0) {
				objects.remove(obj);
				objects.add(index-1, obj);
				notifyListeners();
			}
		}
	}
	
	public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
		if (objects.isEmpty()) {
			return null;
		}
		double minDist = objects.get(0).selectionDistance(mousePoint);
		GraphicalObject minObj = objects.get(0);
		for (GraphicalObject o : objects) {
			double dist = o.selectionDistance(mousePoint);
			if (dist <= minDist) {
				minDist = dist;
				minObj = o;
			}
		}
		if (minDist < SELECTION_PROXIMITY) {
			return minObj;
		}
		return null;
	}
	
	public int findSelectedHotPoint(GraphicalObject obj, Point mousePoint) {
		int n = obj.getNumberOfHotPoints(); 
		if (n == 0) {
			return -1;
		}
		double minDist = obj.getHotPointDistance(0, mousePoint);
		int minPoint = 0;
		for (int i = 0; i < n; i++) {
			double dist = obj.getHotPointDistance(i, mousePoint);
			if (dist < minDist) {
				minDist = dist;
				minPoint = i;
			}
		}
		if (minDist < SELECTION_PROXIMITY) {
			return minPoint;
		}
		return -1;
	}
}
