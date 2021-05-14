package patsql.generator.sql.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import patsql.entity.table.window.WinFunc;

public class QWindowFuncColumn extends QColumn {

	public WinFunc func;
	public Optional<QColumn> src;
	public List<QColumn> partCols;
	public List<QSortSpec> sortSpecs;

	public QWindowFuncColumn(WinFunc func, Optional<QColumn> src, List<QColumn> partCols,
			List<QSortSpec> sortSpecs) {
		this.func = func;
		this.src = src;
		this.partCols = partCols;
		this.sortSpecs = sortSpecs;
	}

	@Override
	public QRelation getSrcRelation() {
		return src.orElseThrow(() -> new IllegalStateException()).getSrcRelation();
	}

	@Override
	public String referencedColName() {
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return toPostgreSQL();
	}

	@Override
	public String toStringAliased() {
		if (isAliased == false || !getColAlias().isPresent()) {
			return toPostgreSQL();
		} else {
			return toPostgreSQL() + " AS " + getColAlias().get();
		}
	}

	private String toPostgreSQL() {
		String windowFuncType;
		switch (func) {
		case RANK:
			assert !src.isPresent();
			windowFuncType = "rank()";
			break;
		case COUNT:
		case MAX:
		case MIN:
		case SUM:
		default:
			assert src.isPresent();
			windowFuncType = String.format("%s(%s)", func.toString().toLowerCase(), src.get().toString());
			break;
		}

		String partKeyStr;
		if (partCols.isEmpty()) {
			partKeyStr = "";
		} else {
			partKeyStr = partCols.stream() //
					.map(QColumn::toString) //
					.collect(Collectors.joining(", ", "PARTITION BY ", ""));
		}

		String sortKeyStr;
		if (sortSpecs.isEmpty()) {
			sortKeyStr = "";
		} else {
			sortKeyStr = sortSpecs.stream() //
					.map(QSortSpec::toString) //
					.collect(Collectors.joining(", ", "ORDER BY ", ""));
		}

		if ("".equals(partKeyStr) || "".equals(sortKeyStr)) {
			return windowFuncType + " OVER (" + partKeyStr + sortKeyStr + ")";
		} else {
			return windowFuncType + " OVER (" + partKeyStr + " " + sortKeyStr + ")";
		}
	}

	@Override
	public Optional<String> getColAlias() {
		String windowFuncType;
		switch (func) {
		case RANK:
			assert !src.isPresent();
			windowFuncType = "rank";
			break;
		case COUNT:
		case MAX:
		case MIN:
		case SUM:
		default:
			assert src.isPresent();
			windowFuncType = String.format("%s_%s", func.toString().toLowerCase(),
					src.get().toString().toLowerCase().replace('.', '_'));
			break;
		}

		String partKeyStr;
		if (partCols.isEmpty()) {
			partKeyStr = "";
		} else {
			partKeyStr = partCols.stream() //
					.map(QColumn::toString) //
					.map(String::toLowerCase) //
					.map(s -> s.replace('.', '_')) //
					.collect(Collectors.joining("_", "part_by_", ""));
		}

		String sortKeyStr;
		if (sortSpecs.isEmpty()) {
			sortKeyStr = "";
		} else {
			sortKeyStr = sortSpecs.stream() //
					.map(QSortSpec::toString).map(String::toLowerCase) //
					.map(s -> s.replace('.', '_')).map(s -> s.replace(' ', '_')) //
					.collect(Collectors.joining("_", "order_by_", ""));
		}

		if ("".equals(partKeyStr) && "".equals(sortKeyStr)) {
			return Optional.of(windowFuncType + "_over_nothing");
		} else if ("".equals(partKeyStr) || "".equals(sortKeyStr)) {
			return Optional.of(windowFuncType + "_over_" + partKeyStr + sortKeyStr);
		} else {
			return Optional.of(windowFuncType + "_over_" + partKeyStr + "_" + sortKeyStr);
		}
	}

	public List<QQualifiedCol> referredCols() {
		List<QQualifiedCol> ret = new ArrayList<>();
		if (src.isPresent()) {
			ret.add(new QQualifiedCol(src.get().getSrcRelation(), src.get().referencedColName()));
		}
		for (QColumn partCol : partCols) {
			ret.add(new QQualifiedCol(partCol.getSrcRelation(), partCol.referencedColName()));
		}
		for (QSortSpec qSortSpec : sortSpecs) {
			ret.add(new QQualifiedCol(qSortSpec.c.getSrcRelation(), qSortSpec.c.referencedColName()));
		}
		return ret;
	}

}
