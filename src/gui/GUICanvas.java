package gui;

import graphics.G2DRendererImpl;
import graphics.GraphicalObject;
import graphics.Renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

import model.DocumentModel;
import model.DocumentModelListener;

public class GUICanvas extends JComponent {

	private static final long serialVersionUID = -6779354896673578331L;

	private DocumentModel model;
	private GUI gui;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;
	
	public GUICanvas(DocumentModel model, GUI gui) {
		super();
		this.model = model;
		this.gui = gui;
		
		setFocusable(true);
		initKeyListener();
		initMouseListener();
		
		model.addDocumentModelListener(new DocumentModelListener() {
			@Override
			public void documentChange() {
				repaint();
			}
		});
	}
	
	private void initKeyListener() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					ctrlDown = true;
				} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					shiftDown = true;
				}
				gui.getCurrentState().keyPressed(e.getKeyCode());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					ctrlDown = false;
				} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					shiftDown = false;
				}
			}
		});
	}
	
	private void initMouseListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
				Point p = e.getPoint();
				gui.getCurrentState().mouseDown(new graphics.Point(p.x, p.y),  shiftDown, ctrlDown);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point p = e.getPoint();
				gui.getCurrentState().mouseUp(new graphics.Point(p.x, p.y),  shiftDown, ctrlDown);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocus();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				gui.getCurrentState().mouseDragged(new graphics.Point(p.x, p.y));
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Renderer r = new G2DRendererImpl(g2d);
		for (GraphicalObject o : model.list()) {
			o.render(r);
			gui.getCurrentState().afterDraw(r, o);
		}
		gui.getCurrentState().afterDraw(r);
	}
}
