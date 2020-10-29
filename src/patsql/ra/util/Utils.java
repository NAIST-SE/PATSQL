package patsql.ra.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.DateValue;
import patsql.entity.table.Table;
import patsql.entity.table.Type;

public class Utils {

	public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
		List<List<T>> result = new ArrayList<>();

		// base case: return a list containing a single empty list.
		if (lists.isEmpty()) {
			result.add(new ArrayList<>());
			return result;
		}

		for (T obj : lists.get(0)) {
			List<List<T>> tailLists = lists.subList(1, lists.size());
			for (List<T> tailProductMember : cartesianProduct(tailLists)) {
				List<T> resultElem = new ArrayList<>();
				resultElem.add(obj);
				resultElem.addAll(tailProductMember);
				result.add(resultElem);
			}
		}
		return result;
	}

	public static Table loadTableFromFile(String filePath) {
		return loadTableFromFile(new File(filePath));
	}

	public static Table loadTableFromFile(File input) {
		try {
			List<String> lines = Files.readAllLines(input.toPath(), StandardCharsets.UTF_8);
			return parseCsvTable(lines.toArray(new String[0]));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("file read error :" + input.getAbsolutePath());
		}
	}

	/**
	 * Try to dispatch a parser to parse the table, the formats we are able to
	 * support are only markdown style or csv table
	 */
	public static Table parseTable(String... lines) {
		if (lines[0].contains("|")) {
			return parseMarkdownTable(lines);
		} else {
			return parseCsvTable(lines);
		}
	}

	/**
	 * Parse a table that looks like following. <br>
	 * S_key:Int,C_name:Str <br>
	 * S1,class2 <br>
	 * S2,null <br>
	 * S4,class2<br>
	 */
	private static Table parseCsvTable(String[] lines) {
		String[] header = splitByComma(lines[0]);
		String[][] rows = new String[lines.length - 1][header.length];
		for (int i = 0; i < lines.length - 1; i++) {
			rows[i] = splitByComma(lines[i + 1]);
		}
		return buildTable(header, rows);
	}

	private static String[] splitByComma(String line) {
		String[] strs = line.split(",");
		return Arrays.stream(strs).map(String::trim).toArray(String[]::new);
	}

	/**
	 * split "age:Int" into "age" and "Int".
	 */
	private static String[] splitLabelStr(String labelStr) {
		String[] strs = labelStr.split(":");
		if (strs.length == 2) {
			return new String[] { //
					strs[0].trim(), // column name
					strs[1].trim() // column type
			};
		} else if (strs.length == 1) { // only column name (Scythe file)
			return new String[] { strs[0].trim() };
		} else {
			throw new IllegalStateException("The header cell " + labelStr + " is invalid.");
		}
	}

	public static void printCommentsinScytheFile(File input) {
		try {
			Files.lines(input.toPath(), StandardCharsets.UTF_8) //
					.filter(line -> line.startsWith("//"))//
					.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ScytheFileData loadFromScytheFile(File input) {
		String[] lines;
		List<NamedTable> inputs = new ArrayList<>();
		Table output = null;
		List<Cell> constVals = new ArrayList<>();

		try {
			lines = Files.lines(input.toPath(), StandardCharsets.UTF_8) //
					.filter(line -> !line.startsWith("//")) //
					.filter(line -> !line.trim().isEmpty()) //
					.toArray(String[]::new);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Scythe file read error :" + input.getAbsolutePath());
		}

		Set<String> usedInputTableNames = new HashSet<>();

		int cursor = 0;
		while (cursor < lines.length) {

			// skip lines that doesn't belong to any segments.
			if (!lines[cursor].startsWith("#")) {
				cursor++;
				continue;
			}

			String segmentName = lines[cursor].substring(1).trim();
			cursor++;

			// read lines within a segment.
			List<String> linesInSegment = new ArrayList<>();
			while (cursor < lines.length) {
				String line = lines[cursor].trim();
				if (line.startsWith("#"))
					break; // if it reaches the end of the segment.
				linesInSegment.add(line);
				cursor++;
			}

			if (segmentName.startsWith("input")) {
				// segName might be like "input:tablea"
				String tableName;
				String baseTableName;

				int id = 1;
				if (segmentName.contains(":")) {
					// if some name is specified, use it.
					int idx = segmentName.lastIndexOf(":");
					baseTableName = segmentName.substring(idx + 1);
					tableName = baseTableName;
				} else {
					// if no name is specified, set
					baseTableName = "input";
					tableName = baseTableName + id;
				}

				while (usedInputTableNames.contains(tableName)) {
					id++;
					tableName = baseTableName + id;
				}
				usedInputTableNames.add(tableName);

				Table table = parseTable(linesInSegment.toArray(new String[0]));
				inputs.add(new NamedTable(tableName, table));
			} else if (segmentName.startsWith("output")) {
				output = parseTable(linesInSegment.toArray(new String[0]));
			} else if (segmentName.startsWith("constraint")) {
				String json = String.join(" ", linesInSegment);
				constVals = parseConstraint(json);
			} else if (segmentName.startsWith("solution")) {
				// System.out.println("Stack Overflow Solution: " + segContent);
			}
		}

		assert output != null;
		return new ScytheFileData(inputs, output, constVals);
	}

	/**
	 * Parse a table that looks like the following.
	 * 
	 * <pre>
	 * | col1 | col2 |   col3   |
	 * |------------------------|
	 * |    3 | A    | A_data_3 |
	 * |    5 | B    | B_data_2 |
	 * |    6 | C    | C_data_1 |
	 * </pre>
	 */
	private static Table parseMarkdownTable(String[] lines) {
		// load a header
		String[] header = splitByBar(lines[0]);

		// load rows
		int startRow = 1;
		if (Pattern.matches("\\|[-|]*\\|", lines[startRow].trim()))
			startRow = 2;

		String[][] rows = new String[lines.length - startRow][];
		for (int i = startRow; i < lines.length; i++) {
			rows[i - startRow] = splitByBar(lines[i]);
		}

		assert Arrays.stream(rows).allMatch(row -> row != null);
		assert Arrays.stream(rows).flatMap(row -> Arrays.stream(row)).allMatch(cell -> cell != null);

		return buildTable(header, rows);
	}

	/**
	 * split a line separated by '|' into a list.
	 * 
	 * For example, converting "| col1 | col2 | col3 | " -> ["col1", "col2", "col3"]
	 */
	private static String[] splitByBar(String line) {
		String[] content = line.trim().split("\\|");
		String[] list = new String[content.length - 1];
		/* ignore i=0 (empty string) */
		for (int i = 1; i < content.length; i++)
			list[i - 1] = content[i].trim();
		return list;
	}

	private static Table buildTable(String[] schemaStrings, String[][] rows) {
		List<String> colNames = new ArrayList<>();
		for (String schemaString : schemaStrings) {
			colNames.add(splitLabelStr(schemaString)[0]);
		}

		// First, guess the column types using header row.
		List<GuessedType> guessedTypes = new ArrayList<>();
		for (String schemaString : schemaStrings) {
			String[] header = splitLabelStr(schemaString);

			GuessedType guessedType = null;
			if (header.length == 2 && "Number".equals(header[1])) {
				Type type = Type.Int; // If Number is specified, try treating as Int at first
				guessedType = new GuessedType(Sureness.GUESSED, type);
			} else if (header.length == 2) {
				// when the type is specified.
				Type type = specifierToType(header[1]);
				guessedType = new GuessedType(Sureness.SPECIFIED, type);
			} else {
				guessedType = new GuessedType(Sureness.NONE, Type.Str);
			}
			guessedTypes.add(guessedType);
		}

		// Next, guess the column types using rows.
		for (String[] row : rows) {
			for (int i = 0; i < row.length; i++) {
				GuessedType guessedType;
				try {
					guessedType = guessedTypes.get(i);
				} catch (IndexOutOfBoundsException e) {
					throw new RuntimeException("no matched guessed type for " + row[i], e);
				}
				switch (guessedType.sureness) {
				case NONE:
					// If the type is not guessed yet, guess and use it.
					guessedType.sureness = Sureness.GUESSED;
					guessedType.type = guessType(row[i]);
					break;
				case GUESSED:
					// If the type is already guessed, check it.
					if (guessedType.type == Type.Dbl && guessType(row[i]) == Type.Int) {
						guessedType.type = Type.Dbl;
					} else if (guessedType.type == Type.Int && guessType(row[i]) == Type.Dbl) {
						guessedType.type = Type.Dbl;
					} else if (guessedType.type != guessType(row[i])) {
						// If they are different, use String type.
						guessedType.type = Type.Str;
					}
					break;
				case SPECIFIED:
					break; // do nothing
				default:
					throw new RuntimeException("unexpected sureness: " + guessedType.sureness);
				}
			}
		}

		// Create Table with ColSchema with the guessed types.
		List<ColSchema> colSchemas = new ArrayList<>();
		for (int i = 0; i < colNames.size(); i++) {
			colSchemas.add(new ColSchema(colNames.get(i), guessedTypes.get(i).type));
		}
		Table table = new Table(colSchemas.toArray(new ColSchema[0]));

		// Load rows
		for (String[] rawRowData : rows) {
			Cell[] row = new Cell[rawRowData.length];
			for (int i = 0; i < rawRowData.length; i++) {
				String rawValue = rawRowData[i];
				Type type;
				if (rawValue.equals("NULL") || rawValue.startsWith("NULL[")) {
					type = Type.Null;
				} else if (rawValue.matches("\\s*")) {
					type = Type.Null;
				} else {
					type = table.columns[i].schema.type;
				}
				row[i] = new Cell(rawValue, type);
			}
			table.addRow(row);
		}

		return table;
	}

	private static Type specifierToType(String columnTypeStr) {
		switch (columnTypeStr) {
		case "String":
		case "Str":
			return Type.Str;
		case "Dbl":
			return Type.Dbl;
		case "Int":
			return Type.Int;
		case "Date":
			return Type.Date;
		case "Number": // Number should be handled before calling this method
		default:
			throw new RuntimeException(String.format("Type %s is illegal!", columnTypeStr));
		}
	}

	public static Type guessType(String raw) {
		try {
			Double.parseDouble(raw);
			if (raw.contains(".")) {
				return Type.Dbl;
			} else {
				try {
					Integer.parseInt(raw);
					return Type.Int;
				} catch (NumberFormatException ignore) {
					return Type.Dbl; // raw is something like "1e-1"
				}
			}
		} catch (NumberFormatException ignore) {
		}

		try {
			DateValue.parse(raw);
			return Type.Date;
		} catch (Exception ignore) {
		}

		// since we don't have null values for now, trying to keep it as it is
		if (raw.startsWith("NULL[")) {
			String typeRawString = raw.substring(raw.indexOf("[") + 1, raw.indexOf("]"));
			switch (typeRawString) {
			case "date":
			case "time":
				return Type.Date;
			case "num":
				return Type.Int; // try treating as Int
			case "str":
			default:
				return Type.Str;
			}
		}

		return Type.Str;
	}

	private static class GuessedType {
		Sureness sureness;
		Type type;

		GuessedType(Sureness sureness, Type type) {
			this.sureness = sureness;
			this.type = type;
		}
	}

	private static enum Sureness {
		NONE, GUESSED, SPECIFIED;
	}

	private static Optional<Cell> stripType(String raw) {
		if (raw.matches(".*:Str")) {
			return Optional.of(new Cell(raw.replace(":Str", ""), Type.Str));
		} else if (raw.matches(".*:Date")) {
			return Optional.of(new Cell(raw.replace(":Date", ""), Type.Date));
		} else if (raw.matches(".*:Int")) {
			return Optional.of(new Cell(raw.replace(":Int", ""), Type.Int));
		} else if (raw.matches(".*:Dbl")) {
			return Optional.of(new Cell(raw.replace(":Dbl", ""), Type.Dbl));
		} else {
			return Optional.empty();
		}
	}

	private static List<Cell> parseConstraint(String json) {
		List<Cell> ret = new ArrayList<>();
		JsonConstraint p = parseJSON(json);
		for (String constant : p.constants) {
			Optional<Cell> constCell = stripType(constant);
			ret.add(constCell.orElse(new Cell(constant, guessType(constant))));
		}

		// ignores aggregation function settings for now

		return ret;
	}

	private class JsonConstraint {
		List<String> constants;
		List<String> aggregation_functions;
	}

	private static JsonConstraint parseJSON(String fileContent) {
		Gson gson = new GsonBuilder().create();
		JsonConstraint p = gson.fromJson(fileContent, JsonConstraint.class);

		if (p.constants == null)
			p.constants = Collections.emptyList();
		if (p.aggregation_functions == null)
			p.aggregation_functions = Collections.emptyList();

		return p;
	}

}
