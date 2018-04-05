package rhal95.opengl.knot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.LinkedList;
import java.util.List;

public class VisualizerTemplate {
	List<List<Stitches>> template;

	private VisualizerTemplate() {
		template = new LinkedList<>();
	}

	public static VisualizerTemplate buildTemplate(File template_File) throws IOException {
		VisualizerTemplate result = null;

		BufferedReader reader = new BufferedReader(new FileReader(template_File));
		result = new VisualizerTemplate();
		List<Stitches> row = new LinkedList<>(); // create first row with
													// cast on stitches
		String line = reader.readLine();
		if (line.toUpperCase().startsWith("CAST ON:")) {
			int cast_on_stitches = Integer.parseInt(line.substring(8).trim());
			for (int i = 0; i < cast_on_stitches; i++) {
				row.add(Stitches.cast_on);
			}
		} else
			throw new RuntimeException("First row must start with \"cast on: NUMBER\"");
		result.template.add(row);
		for (int i = 1; reader.ready(); i++) {
			line = reader.readLine();

			int split_index;
			if ((split_index = line.indexOf(":")) > 0) {
				int line_number = Integer.parseInt(line.substring(0, split_index));
				// System.out.println(line);
			} else
				continue; // if illegal line to to the next
			row = new LinkedList<>();
			line = line.substring(split_index + 1);
			String[] rules = line.split(",");
			for (String rule : rules) {
				rule = rule.trim();
				int amount;
				Stitches to_add;
				if (rule.startsWith("k")) {
					to_add = Stitches.right;
				} else if (rule.startsWith("p")) {
					to_add = Stitches.left;
				} else
					throw new RuntimeException("Stitch type not supported yet");
				split_index = 0;
				while (split_index < rule.length() && !Character.isDigit(rule.charAt(split_index)))
					split_index++;
				// System.out.println(rule.substring(split_index));
				amount = Integer.parseInt(rule.substring(split_index));
				for (int j = 0; j < amount; j++) {
					row.add(to_add);
				}
			}
			result.template.add(row);
		}

		return result;
	}

	List<Stitches> getRow(int i) {
		if (0 <= i && i < template.size())
			return template.get(i);
		else
			throw new IndexOutOfBoundsException();
	}

	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[\n");
		for (List<Stitches> list : template) {
			builder.append("[");
			for (Stitches stiches : list) {
				builder.append(stiches);
				builder.append(" ");
			}
			builder.append("]\n");
		}
		builder.append("]\n");
		return builder.toString();
	}
}
