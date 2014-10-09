package gui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import tools.AddShapeState;
import tools.EraserState;
import tools.IdleState;
import tools.SelectShapeState;
import tools.State;
import model.DocumentModel;
import graphicalobjects.CompositShape;
import graphics.GraphicalObject;
import graphics.SVGRendererImpl;

public class GUI extends JFrame {

	private static final long serialVersionUID = -4733038019191739491L;
	
	private List<GraphicalObject> objects = new ArrayList<>();
	private State currentState = new IdleState();
	private DocumentModel model = new DocumentModel();
	
	public GUI(List<GraphicalObject> objects) {
		super();		
		this.objects = objects;
		GUICanvas canvas = new GUICanvas(model, this);
		
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		
		initIdleStateListener();
		initToolbar();
		
		setSize(600, 400);
		setLocation(200, 100);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		super.setVisible(true);
	}
	
	private void initIdleStateListener() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						currentState.onLeaving();
						currentState = new IdleState();
					}
				}
				return false;
			}
		});
	}
	
	@SuppressWarnings("serial")
	private void initToolbar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		add(toolBar, BorderLayout.NORTH);
		
		for (final GraphicalObject o : objects) {
			JButton button = new JButton();
			button.setAction(new AbstractAction(o.getShapeName()) {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentState.onLeaving();
					currentState = new AddShapeState(model, o);
				}
			});
			toolBar.add(button);
		}
		
		JButton selButton = new JButton();
		selButton.setAction(new AbstractAction("Selektiraj") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new SelectShapeState(model);
			}
		});
		toolBar.add(selButton);
		
		JButton eraseButton = new JButton();
		eraseButton.setAction(new AbstractAction("Brisalo") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				currentState.onLeaving();
				currentState = new EraserState(model);
			}
		});
		toolBar.add(eraseButton);
		
		JButton svgExport = new JButton();
		svgExport.setAction(new AbstractAction("SVG Export") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int retVal = chooser.showSaveDialog(GUI.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					if (!fileName.endsWith(".svg")) {
						fileName += ".svg";
					}
					SVGRendererImpl r = new SVGRendererImpl(fileName);
					for (GraphicalObject o : model.list()) {
						o.render(r);
					}
					try {
						r.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(
								GUI.this,
								"Pogrešak u exportanju.",
								"SVG Export",
								JOptionPane.ERROR_MESSAGE
						);
					}
				}
			}
		});
		toolBar.add(svgExport);
		
		JButton saveButton = new JButton();
		saveButton.setAction(new AbstractAction("Save") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int retVal = chooser.showSaveDialog(GUI.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
					List<String> rows = new ArrayList<>();
					for (GraphicalObject o : model.list()) {
						o.save(rows);
					}
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
						for (String s : rows) {
							writer.write(s);
							writer.write('\n');
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(
								GUI.this,
								"Pogrešak u spremanju.",
								"Save",
								JOptionPane.ERROR_MESSAGE
						);
					}
				}
			}
		});
		toolBar.add(saveButton);
		
		JButton loadButton = new JButton();
		loadButton.setAction(new AbstractAction("Load") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int retVal = chooser.showOpenDialog(GUI.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					List<String> rows = new ArrayList<>();
					try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						while (true) {
							String line = reader.readLine();
							if (line == null || line.isEmpty()) {
								break;
							}
							rows.add(line);
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(
								GUI.this,
								"Pogrešak u učitavanju datoteke.",
								"Load",
								JOptionPane.ERROR_MESSAGE
						);
					}
					
					Map<String, GraphicalObject> objectMap = new HashMap<>();
					CompositShape prototype = new CompositShape();	//TODO ovo nije dobro
					objectMap.put(prototype.getShapeID(), prototype);
					for (GraphicalObject o : objects) {
						objectMap.put(o.getShapeID(), o);
					}
					
					Stack<GraphicalObject> stack = new Stack<>();
					for (String s : rows) {
						String id = s.substring(0, s.indexOf(' '));
						String data = s.substring(id.length() + 1);
						objectMap.get(id).load(stack, data);
					}
					
					model.clear();
					for (GraphicalObject o : stack) {
						model.addGraphicalObject(o);
					}
				}
			}
		});
		toolBar.add(loadButton);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
	
	public State getCurrentState() {
		return currentState;
	}
}
