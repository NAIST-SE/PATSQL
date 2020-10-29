package patsql.entity.table;

public class DateFuncColSchema extends ColSchema {
	public final DateFunc func;
	public final ColSchema src;

	public DateFuncColSchema(DateFunc func, ColSchema src) {
		super("EXTRACT(" + func + " _FROM " + src.name + ")", Type.Int);
		this.func = func;
		this.src = src;
	}

}
