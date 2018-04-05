package rhal95.opengl.knot;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class VisualizerGraph {
	public double X_CLOSENESS = 1;
	public double Y_CLOSENESS = 0.5;
	public float SCALE = 0.2f;
	public double X_LOOP_SIZE = 0.5;
	public double Y_LOOP_SIZE = 0.5;
	public int POINTS_PER_LOOP = 5;
	VisualizerGraphNode first;
	int length;
	int max_width;
	int max_height;

	public VisualizerGraph() {
		first = new VisualizerGraphNode();
	}

	public static VisualizerGraph createNewGraphFromTemplate(VisualizerTemplate template) {
		VisualizerGraph result = new VisualizerGraph();
		// TODO here code to generate a graph from given Template.
		// maybe two runs. one to create all the "next" and "previous" nodes and
		// one to
		// interconnect them with the top and bottom lists
		List<List<Stitches>> tmpl_li = template.template;
		VisualizerGraphNode current = result.first;
		for (Iterator<List<Stitches>> iterator = tmpl_li.iterator(); iterator.hasNext();) {
			List<Stitches> list = (List<Stitches>) iterator.next();
			for (Iterator<Stitches> iterator2 = list.iterator(); iterator2.hasNext();) {
				Stitches stitch = (Stitches) iterator2.next();
				// Create the current node
				current.type = stitch;
				current.nextNode = new VisualizerGraphNode(current);
				// advance to the next node
				// last node of a row is where the las top node is also the next
				if (!iterator2.hasNext() && iterator.hasNext()) {
					current.topNodes.add(current.nextNode);
				}
				current = current.nextNode;
			}
		}
		// the basic structure should be build now
		// no interconnections present yet

		// advance to end of row
		current = result.first;
		while (current.topNodes.size() == 0 || current.nextNode != ((LinkedList<?>) current.topNodes).getLast())
			current = current.nextNode;
		// take the last node of the first row and the first of the second row
		VisualizerGraphNode bottom_row = current;
		current = current.nextNode;

		// Now advance the bottom_row backwards and the current forwards

		while (current.nextNode != null) {

			if (current.bottomNodes.size() == current.type.bottomStitchesAmount())
				current = current.nextNode;
			else
				current.bottomNodes.add(bottom_row);
			if (bottom_row.topNodes.size() == bottom_row.type.topStitchesAmount())
				bottom_row = bottom_row.previousNode;
			else
				bottom_row.topNodes.add(current);
			if (current.topNodes.size() > 0 && current.nextNode == ((LinkedList<?>) current.topNodes).getLast()
					&& current.bottomNodes.size() == current.type.bottomStitchesAmount()) {
				bottom_row = current;
				current = current.nextNode;
			}
		}

		int max = 0;
		for (List<Stitches> list : tmpl_li) {
			if (max < list.size())
				max = list.size();
		}
		result.max_width = max;
		result.max_height = tmpl_li.size();
		result.length = result.first.length();
		return result;
	}

	public int length() {
		return length;
	}

	public void calculatePoints(FloatBuffer points) {
		VisualizerGraphNode current = first;
		int x_coord = 0;
		int y_coord = 0;
		boolean dir = true;
		while (current.nextNode != null) {
			double angle = 2 * Math.PI / POINTS_PER_LOOP;
			for (int i = 0; i < POINTS_PER_LOOP; i++) {
				for (int j = 0; j < 3; j++) {
					if (dir)
						points.put((float) ((x_coord * X_CLOSENESS - max_width * X_CLOSENESS / 2)
								- Math.sin((i + 0.5) * angle) * X_LOOP_SIZE) * SCALE)
								.put((float) ((y_coord * Y_CLOSENESS - max_height * Y_CLOSENESS / 2)
										- Math.cos((i + 0.5) * angle) * Y_LOOP_SIZE) * SCALE)
								.put(0.0f);
					else
						points.put((float) ((x_coord * X_CLOSENESS - max_width * X_CLOSENESS / 2)
								- Math.sin(-(i + 2) * angle) * X_LOOP_SIZE) * SCALE)
								.put((float) ((y_coord * Y_CLOSENESS - max_height * Y_CLOSENESS / 2)
										- Math.cos(-(i + 2) * angle) * Y_LOOP_SIZE) * SCALE)
								.put(0.0f);
				}
			}

			if (current.nextNode != null && current.topNodes.contains(current.nextNode)) {
				y_coord += 1;
				dir = !dir;
			} else if (dir) {
				x_coord += 1;
			} else
				x_coord -= 1;
			current = current.nextNode;
		}
		points.rewind();
	}
}
