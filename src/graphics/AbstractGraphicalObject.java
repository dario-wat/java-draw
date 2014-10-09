package graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGraphicalObject implements GraphicalObject {

	private Point[] hotPoints;
	private boolean[] hotPointSelected;
	private boolean selected;
	protected List<GraphicalObjectListener> listeners = new ArrayList<GraphicalObjectListener>();
	
	protected AbstractGraphicalObject(Point[] hotPoints) {
		super();
		this.hotPoints = Arrays.copyOf(hotPoints, hotPoints.length);
		this.hotPointSelected = new boolean[hotPoints.length];
		this.selected = false;
	}
	
	private void checkHotPointIndex(int index) {
		if (index < 0 || index >= hotPoints.length) {
			throw new IllegalArgumentException("Index out of range!");
		}
	}
	
	@Override
	public Point getHotPoint(int index) {
		checkHotPointIndex(index);
		return hotPoints[index];
	}
	
	@Override
	public void setHotPoint(int index, Point point) {
		checkHotPointIndex(index);
		hotPoints[index] = point;
		notifyListeners();
	}
	
	@Override
	public int getNumberOfHotPoints() {
		return hotPoints.length;
	}
	
	@Override
	public double getHotPointDistance(int index, Point mousePoint) {
		checkHotPointIndex(index);
		return GeometryUtil.distanceFromPoint(mousePoint, hotPoints[index]);
	}
	
	@Override
	public boolean isHotPointSelected(int index) {
		checkHotPointIndex(index);
		return hotPointSelected[index];
	}
	
	@Override
	public void setHotPointSelected(int index, boolean selected) {
		checkHotPointIndex(index);
		if (hotPointSelected[index] != selected) {
			hotPointSelected[index] = selected;
			notifySelectionListeners();
		}
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
	public void translate(Point delta) {
		for (int i = 0; i < hotPoints.length; i++) {
			hotPoints[i] = hotPoints[i].translate(delta);
		}
		notifyListeners();
	}
	
	@Override
	public void addGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.add(l);
	}
	
	@Override
	public void removeGraphicalObjectListener(GraphicalObjectListener l) {
		listeners.remove(l);
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
