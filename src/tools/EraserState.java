package tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.DocumentModel;
import graphicalobjects.LineSegment;
import graphics.GraphicalObject;
import graphics.Point;
import graphics.Renderer;

public class EraserState implements State {

	private Set<GraphicalObject> forDeletion = new HashSet<>();
	private DocumentModel model;
	private List<GraphicalObject> lines = new ArrayList<>();
	private Point last = null;
	
	public EraserState(DocumentModel model) {
		super();
		this.model = model;
	}
	
	@Override
	public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		last = mousePoint;
	}

	@Override
	public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
		for (GraphicalObject o : forDeletion) {
			model.removeGraphicalObject(o);
		}
		for (GraphicalObject o : lines) {
			model.removeGraphicalObject(o);
		}
	}

	@Override
	public void mouseDragged(Point mousePoint) {
		GraphicalObject selected = model.findSelectedGraphicalObject(mousePoint);
		if (selected != null) {
			forDeletion.add(selected);
		}
		
		GraphicalObject line = new LineSegment(mousePoint, last);
		model.addGraphicalObject(line);
		lines.add(line);
		last = mousePoint;
	}

	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void afterDraw(Renderer r, GraphicalObject go) {
	}

	@Override
	public void afterDraw(Renderer r) {
	}

	@Override
	public void onLeaving() {
	}

}
