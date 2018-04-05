package rhal95.opengl.knot;

import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import rhal95.opengl.Vec3f;
import rhal95.opengl.Vec4f;

public class VisualizerGraph implements Iterable<VisualizerGraphNode> {
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

	public Vec4f[] calculatePoints() {
		List<Vec4f> result = new LinkedList<>();
		float number = 0;
		float row = 1;
		result.add(new Vec4f(0, 0, 0));
		for (VisualizerGraphNode n : this) {
			if (number % 2 == 0)
				number += 2;
			else
				number -= 2;
			if (n.bottomNodes.contains(n.previousNode)) {
				result.add(new Vec4f(number-max_width/2, row-max_height/2, 0, 3));

			}
			result.add(new Vec4f(number-max_width/2, row-max_height/2, 0, 3));
			if (row % 2 == 0) {
				result.add(new Vec4f(number - 0.5f-max_width/2, row-max_height/2 + 1f, 0, 1));
				result.add(new Vec4f(number + 0.5f-max_width/2, row-max_height/2 + 1f, 0, 1));
			} else {
				result.add(new Vec4f(number + 0.5f-max_width/2, row-max_height/2 + 1f, 0, 1));
				result.add(new Vec4f(number - 0.5f-max_width/2, row-max_height/2 + 1f, 0, 1));

			}
			result.add(new Vec4f(number-max_width/2, row-max_height/2, 0, 3));
		}
		Vec4f[] l = new Vec4f[result.size()];
		result.toArray(l);
		return l;
	}

	@Override
	public Iterator<VisualizerGraphNode> iterator() {
		return new Iterator<VisualizerGraphNode>() {
			VisualizerGraphNode next = first;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public VisualizerGraphNode next() {
				VisualizerGraphNode result = next;
				next = next.nextNode;
				return result;
			}

		};
	}
}
