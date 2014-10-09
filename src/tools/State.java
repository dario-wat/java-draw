package tools;

import graphics.GraphicalObject;
import graphics.Point;
import graphics.Renderer;

public interface State {

	void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown);
	void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown);
	void mouseDragged(Point mousePoint);
	void keyPressed(int keyCode);
	void afterDraw(Renderer r, GraphicalObject go);
	void afterDraw(Renderer r);
	void onLeaving();
}
