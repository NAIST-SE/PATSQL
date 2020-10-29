package patsql.ra.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import patsql.entity.synth.Example;
import patsql.entity.synth.NamedTable;
import patsql.entity.synth.SynthOption;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

public class EvaluationUtil {

	public static void writeResultToFile(File file, String sql, long synthesisTime, Example ex, SynthOption opt) {
		synthesisTime = (synthesisTime == 0) ? 1 : synthesisTime;

		String name = file.getParentFile().getName() + "_" + file.getName();
		name = name.replace(".md", "");
		if (sql == null)
			name = name + "X";

		String description = "(nothing)";
		try {
			description = Files.lines(file.toPath(), StandardCharsets.UTF_8) //
					.map(line -> line.replaceAll(" ", ""))//
					.filter(line -> line.startsWith("//http"))//
					.map(line -> line.replaceFirst("//", ""))//
					.map(line -> "<a href=\"" + line + "\" target=\"blank\"> URL </a>")//
					.collect(Collectors.joining("<br>"))//
					+ "<br>" + //
					Files.lines(file.toPath(), StandardCharsets.UTF_8) //
							.filter(line -> line.startsWith("//"))//
							.filter(line -> !line.replaceAll(" ", "").startsWith("//http"))
							.map(line -> line.replaceFirst("//", ""))//
							.map(line -> "<span>" + line + "</span>")//
							.collect(Collectors.joining("<br>"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();

		// DESCRIPTION
		sb.append("<h2>Description</h2>");
		sb.append(description);

		// header
		sb.append("<!DOCTYPE html>" + "<html lang=\"en\">" + "" + "<head>" + "<meta charset=\"UTF-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" + "<title>");
		sb.append(name);
		sb.append("</title>" + "<link rel=\"stylesheet\" href=\"../css/result.css\">"
				+ "<link rel=\"stylesheet\" href=\"../css/idea.css\">"
				+ "<script type=\"text/javascript\" src=\"../js/lib/highlight.pack.js\"></script>"
				+ "<script>hljs.initHighlightingOnLoad();</script>" + "</head>" + "" + "<body>"
				+ "<h2>I/O Example</h2>");

		// INPUTS
		for (NamedTable inTbl : ex.inputs) {
			sb.append("<h3>INPUT: ");
			sb.append(inTbl.name);
			sb.append("</h3>");

			sb.append("<table class=\"in_table mono\">");
			loadTable(sb, inTbl.table);
			sb.append("</table>");
		}

		// OUTPUT
		sb.append("<h3>OUTPUT</h3>");
		sb.append("<table class=\"out_table mono\">");
		loadTable(sb, ex.output);
		sb.append("</table>");

		// HINTS
		sb.append("  <h3>Hints</h3>" + "<div>" + "<ul class=\"mono\">");
		if (opt.extCells.length == 0) {
			sb.append("<li>(empty)</li>");
		}
		for (Cell ext : opt.extCells) {
			sb.append("<li>");
			sb.append(ext.value);
			sb.append("<span class=\"type\">:" + ext.type + "</span>");
			sb.append("</li>");
		}
		sb.append("</ul>" + "</div>");

		// SOLUTION
		sb.append("<h2>Our Solution</h2>");
		if (sql != null) {
			sb.append("<pre>" + "<code class=\"sql\">");
			sb.append(sql);
			sb.append("</code>" + "</pre>");
		} else {
			sb.append("(fail)");
		}

		// TIME
		sb.append("  <h2>Synthesis Time</h2>" + "<div>");
		if (sql == null) {
			sb.append("(timeout)&nbsp;");
			sb.append("&nbsp;milliseconds " + "</div>");
		} else {
			sb.append(synthesisTime);
			sb.append("&nbsp;milliseconds" + "</div>");
		}

		// trailer
		sb.append("</body>" + "" + "</html>");

		try {
			File outf = new File("html/results/" + name + ".html");
			outf.createNewFile();

			try (FileWriter writer = new FileWriter(outf)) {
				writer.write(sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void loadTable(StringBuilder sb, Table table) {
		sb.append("<tr>");
		for (ColSchema sc : table.schema()) {
			sb.append("<th>");
			sb.append(sc.name);
			sb.append("<span class=\"type\">:" + sc.type + "</span>");
			sb.append("</th>");
		}
		sb.append("</tr>");

		// rows
		for (int i = 0; i < table.height(); i++) {
			sb.append("<tr>");
			for (Cell c : table.row(i)) {
				if (c.type == Type.Null) {
					sb.append("<td><span class= \"null\">" + c.value + "</span></td>");
				} else {
					sb.append("<td>" + c.value.trim() + "</td>");
				}
			}
			sb.append("</tr>");
		}
	}
}
