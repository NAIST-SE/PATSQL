package patsql.generator.sql.query;

public class QJoinSpec {
	private JoinType joinType;
	private QRelation relation;
	private QCondition condition;

	public QJoinSpec(JoinType t, QRelation r, QCondition c) {
		this.joinType = t;
		this.relation = r;
		this.condition = c;
	}

	public QRelation getRelation() {
		return relation;
	}

	public QCondition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return String.format("%sJOIN %s ON %s", joinType, relation, condition.toString(1000));
	}

	public enum JoinType {
		INNER(""), LEFT("LEFT ");

		private final String s;

		private JoinType(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return this.s;
		}
	}
}
