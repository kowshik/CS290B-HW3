package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import api.Space;

/**
 * Computes an optimal solution to the Travelling Salesman Problem on a remote
 * machine and displays the solution in a GUI.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 */
public class TspClient {

	// Size of JFrame displayed on the screen
	private static int N_PIXELS = 500;

	// Input for the Travelling Salesman Problem
	private static double[][] CITIES = { { 1, 1 }, { 8, 1 }, { 8, 8 },
			{ 1, 8 }, { 2, 2 }, { 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 },
			{ 6, 3 }, { 6, 6 }, { 3, 6 } };

	public static void main(String[] args) throws Exception {

		String computeSpaceServer = args[0];
		TspJob job = new TspJob(CITIES);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {

			long jobStartTime = System.currentTimeMillis();
			Space space = (Space) Naming.lookup("//" + computeSpaceServer + "/"
					+ Space.SERVICE_NAME);
			
			// ------Generate tasks and execute them remotely
			job.generateTasks(space);
			job.collectResults(space);
			int[] tour = job.getAllResults();
			// -------------------------------------
			
			
			String tourStr="[ "+tour[0];
			for(int index=1;index<tour.length;index++){
				tourStr+=", "+tour[index];
			}
			tourStr+=" ]";
			System.out.println("\n\nMinimum cost cycle : "+tourStr+"\n");
			JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue(
					CITIES, tour);

			// display JLabels: graphic images
			JFrame frame = new JFrame("Result Visualizations");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container container = frame.getContentPane();
			container.setLayout(new BorderLayout());
			container
					.add(new JScrollPane(euclideanTspLabel), BorderLayout.EAST);
			frame.pack();
			frame.setVisible(true);
			long jobEndTime = System.currentTimeMillis();
			System.out.println("Client Job Start Time : " + jobStartTime
					+ " ms");
			System.out.println("Client Job End Time : " + jobEndTime + " ms");
			System.out.println("Client Job Elapsed Time : "
					+ (jobEndTime - jobStartTime) + " ms");
		} catch (RemoteException e) {
			System.err.println("MandelbrotSetClient exception : ");
			e.printStackTrace();

		} catch (MalformedURLException e) {
			System.err.println("MandelbrotSetClient exception : ");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("MandelbrotSetClient exception : ");
			e.printStackTrace();
		}
	}

	private static JLabel displayEuclideanTspTaskReturnValue(double[][] cities,
			int[] tour) {

		// ** display the graph
		// get minX, maxX, minY, maxY, assume: 0.0 <= mins
		double minX = cities[0][0], maxX = cities[0][0];
		double minY = cities[0][1], maxY = cities[0][1];
		for (int i = 0; i < cities.length; i++) {
			if (cities[i][0] < minX) {
				minX = cities[i][0];
			}
			if (cities[i][0] > maxX) {
				maxX = cities[i][0];
			}
			if (cities[i][1] < minY) {
				minY = cities[i][1];
			}
			if (cities[i][1] > maxY) {
				maxY = cities[i][1];
			}
		}

		// scale points to fit in unit square
		double side = Math.max(maxX - minX, maxY - minY);
		double[][] scaledCities = new double[cities.length][2];
		for (int i = 0; i < cities.length; i++) {
			scaledCities[i][0] = (cities[i][0] - minX) / side;
			scaledCities[i][1] = (cities[i][1] - minY) / side;
		}

		Image image = new BufferedImage(N_PIXELS, N_PIXELS,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		int margin = 10;
		int field = N_PIXELS - 2 * margin;
		// draw edges
		graphics.setColor(Color.BLUE);
		int x1, y1, x2, y2;
		int city1 = tour[0], city2;
		x1 = margin + (int) (scaledCities[city1][0] * field);
		y1 = margin + (int) (scaledCities[city1][1] * field);
		for (int i = 1; i < cities.length; i++) {
			city2 = tour[i];
			x2 = margin + (int) (scaledCities[city2][0] * field);
			y2 = margin + (int) (scaledCities[city2][1] * field);
			graphics.drawLine(x1, y1, x2, y2);
			x1 = x2;
			y1 = y2;
		}
		city2 = tour[0];
		x2 = margin + (int) (scaledCities[city2][0] * field);
		y2 = margin + (int) (scaledCities[city2][1] * field);
		graphics.drawLine(x1, y1, x2, y2);

		// draw vertices
		int VERTEX_DIAMETER = 6;
		graphics.setColor(Color.RED);
		for (int i = 0; i < cities.length; i++) {
			int x = margin + (int) (scaledCities[i][0] * field);
			int y = margin + (int) (scaledCities[i][1] * field);
			graphics.fillOval(x - VERTEX_DIAMETER / 2, y - VERTEX_DIAMETER / 2,
					VERTEX_DIAMETER, VERTEX_DIAMETER);
		}
		ImageIcon imageIcon = new ImageIcon(image);
		return new JLabel(imageIcon);
	}
}

