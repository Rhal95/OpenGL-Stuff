package rhal95.opengl.knot;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import rhal95.opengl.CurveDrawer;
import rhal95.opengl.Vec3f;
import rhal95.opengl.Vec4f;

public class Main {
	public static void main(String[] args) {
		try {
			VisualizerGraph g = VisualizerGraph
					.createNewGraphFromTemplate(VisualizerTemplate.buildTemplate(new File("Example.txt")));
			Vec4f[] pts = g.calculatePoints();

			CurveDrawer draw = new CurveDrawer(pts, IntStream.range(0, pts.length+3).toArray(), 2, 0.125f);
			draw.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
