package patsql.generator.sql.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import patsql.entity.synth.NamedTable;
import patsql.entity.table.Cell;
import patsql.entity.table.ColSchema;
import patsql.entity.table.Column;
import patsql.entity.table.DateValue;
import patsql.entity.table.Table;
import patsql.entity.table.Type;
import patsql.generator.sql.SQLUtil;
import patsql.ra.operator.BaseTable;

public class Util {

	public static void assertEqualsIgnoringExtraWhitespaces(CharSequence expected, CharSequence actual) {
		String expected2 = SQLUtil.removeExtraWhitespaces(expected.toString());
		String actual2 = SQLUtil.removeExtraWhitespaces(actual.toString());
		Assertions.assertEquals(expected2, actual2);
	}

	public static String toSQLExpression(Table t) {
		List<String> values = new ArrayList<>();
		for (int i = 0; i < t.height(); i++) {
			List<String> value = new ArrayList<>();
			for (Cell cell : t.row(i)) {
				if (cell.type == Type.Str) {
					value.add(String.format("'%s'", cell.value.replaceAll("'", "''")));
				} else if (cell.type == Type.Date) {
					value.add(Utils.dateToLiteral(DateValue.parse(cell.value).date));
				} else {
					value.add(cell.value);
				}
			}
			values.add(String.join(", ", value));
		}
		return "VALUES (" + String.join("), (", values) + ")";
	}

	public static String toDDL(NamedTable t) {
		return toDDLSub(t.name, t.table, false);
	}

	public static String toDDLNumberingCols(String name, Table t) {
		return toDDLSub(name, t, true);
	}

	static String toDDLSub(String name, Table t, boolean colNumbering) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("CREATE TABLE %s (%n", name));

		int colNum = 1;
		List<String> typeLines = new ArrayList<>();
		for (int i = 0; i < t.columns.length; i++) {
			Column col = t.columns[i];
			String colName;
			if (colNumbering) {
				colName = "col" + colNum++;
			} else {
				colName = col.schema.name;
			}
			String colType;

			switch (col.schema.type) {
			case Dbl:
				colType = "double precision";
				break;
			case Int:
				colType = "int";
				break;
			case Date:
				colType = "date";
				break;
			case Str:
				colType = "text";
				break;
			case Null: // fall through
			default:
				throw new RuntimeException();
			}
			typeLines.add("\t" + colName + " " + colType);
		}
		sb.append(String.join(",\n", typeLines));

		sb.append(")");
		return sb.toString();
	}

	public static BaseTable toBaseTable(NamedTable t) {
		BaseTable ret = new BaseTable(t.name);

		final Column[] cols = t.table.columns;
		ColSchema[] schemas = new ColSchema[cols.length];
		for (int i = 0; i < cols.length; i++) {
			schemas[i] = cols[i].schema;
		}
		ret.renamedCols = schemas;
		return ret;
	}

	/**
	 * @throws RuntimeException when check failed
	 */
	public static void checkSQLSyntax(String generatedStm, NamedTable... ins) {
		try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:")) {
			conn.setAutoCommit(false);
			for (NamedTable inTbl : ins) {
				String ddl = toDDL(inTbl);
				try (Statement s = conn.createStatement()) {
					s.executeUpdate(ddl);
				}
			}

			try (PreparedStatement ps = conn.prepareStatement(generatedStm); ResultSet rs = ps.executeQuery()) {
			}
			conn.commit();
		} catch (SQLException e) {
			System.err.println("## input tables");
			for (NamedTable nt : ins) {
				String ddl = toDDL(nt);
				System.err.println(ddl);
			}
			System.err.println("## query");
			System.err.println(generatedStm);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Execute the generated statement using h2 on-memory database and 
	 * compare the actual result with expected.    
	 * @throws RuntimeException when check failed
	 */
	public static void checkSQL(String generatedStm, Table outTbl, NamedTable... ins) {
		try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:")) {
			conn.setAutoCommit(false);

			// create inputs
			for (NamedTable inTbl : ins) {
				String ddl = toDDL(inTbl);
				try (Statement s = conn.createStatement()) {
					// System.out.println(ddl);
					s.executeUpdate(ddl);
				}
				String values = toSQLExpression(inTbl.table);
				String insert = String.format("INSERT INTO %s %s", inTbl.name, values);
				try (Statement s = conn.createStatement()) {
					// System.out.println(insert);
					s.executeUpdate(insert);
				}
			}

			// create output
			String outTblName = "output";
			{
				String ddl = toDDLNumberingCols(outTblName, outTbl);
				try (Statement s = conn.createStatement()) {
					s.executeUpdate(ddl);
				}
				String values = toSQLExpression(outTbl);
				String insert = String.format("INSERT INTO %s %s", outTblName, values);
				try (Statement s = conn.createStatement()) {
					s.executeUpdate(insert);
				}
			}

			// create actual
			String actualTblName = "actual";
			{
				String ddl = toDDLNumberingCols(actualTblName, outTbl);
				try (Statement s = conn.createStatement()) {
					s.executeUpdate(ddl);
				}
				String insert = String.format("INSERT INTO %s %s", actualTblName, generatedStm);
				try (Statement s = conn.createStatement()) {
					s.executeUpdate(insert);
				}
			}

			// compare
			String outputQuery = "SELECT * FROM " + outTblName;
			String actualQuery = "SELECT * FROM " + actualTblName;

			int count1 = 0;
			String sql1 = String.format("%s%nEXCEPT%n%s", actualQuery, outputQuery);
			try (PreparedStatement ps = conn.prepareStatement(sql1); ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					count1++;
				}
			}

			int count2 = 0;
			String sql2 = String.format("%s%nEXCEPT%n%s", outputQuery, actualQuery);
			try (PreparedStatement ps = conn.prepareStatement(sql2); ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					count2++;
				}
			}

			int count = count1 + count2;
			if (count != 0) {
				try (PreparedStatement ps = conn.prepareStatement(generatedStm); ResultSet rs = ps.executeQuery()) {
					System.err.println("## actual");
					while (rs.next()) {
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							System.err.print(rs.getString(i) + "\t");
						}
						System.err.println();
					}
				}
				try (PreparedStatement ps = conn.prepareStatement(outputQuery); ResultSet rs = ps.executeQuery()) {
					System.err.println("## expected");
					while (rs.next()) {
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							System.err.print(rs.getString(i) + "\t");
						}
						System.err.println();
					}
				}
				throw new RuntimeException("different from expectation. difference: " + count);
			}
			conn.commit();

		} catch (Exception e) {
			System.err.println("## query");
			System.err.println(generatedStm);
			System.err.println("## input data");
			for (NamedTable nt : ins) {
				String ddl = toDDL(nt);
				System.err.println(ddl);
				String values = toSQLExpression(nt.table);
				System.err.println(String.format("INSERT INTO %s %s", nt.name, values));
			}
			throw new RuntimeException(e);
		}

		// OK
	}

}
