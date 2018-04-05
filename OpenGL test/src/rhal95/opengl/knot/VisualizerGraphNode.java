package rhal95.opengl.knot;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VisualizerGraphNode {
	List<VisualizerGraphNode> topNodes;
	VisualizerGraphNode nextNode;
	Stitches type;
	VisualizerGraphNode previousNode;
	List<VisualizerGraphNode> bottomNodes;

	public VisualizerGraphNode() {
		this.topNodes = new LinkedList<>();
		this.nextNode = null;
		this.previousNode = null;
		this.bottomNodes = new LinkedList<>();
		type = null;
	}
	
	public VisualizerGraphNode(Stitches type){
		this();
		this.type = type;
	}
	public VisualizerGraphNode(VisualizerGraphNode previous) {
		this();
		this.previousNode = previous;
	}
	public VisualizerGraphNode(Collection<VisualizerGraphNode> bottom) {
		this();
		this.bottomNodes.addAll(bottom);
	}
	
	public int length() {
		if(nextNode == null) return 0;
		else return nextNode.length() + 1;
	}
}
