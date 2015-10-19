//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.
// 

package analyzer;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.AggregateNode;
import com.akiban.sql.parser.AndNode;
import com.akiban.sql.parser.BinaryArithmeticOperatorNode;
import com.akiban.sql.parser.BinaryRelationalOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.FromList;
import com.akiban.sql.parser.FromTable;
import com.akiban.sql.parser.GroupByColumn;
import com.akiban.sql.parser.GroupByList;
import com.akiban.sql.parser.NotNode;
import com.akiban.sql.parser.NumericConstantNode;
import com.akiban.sql.parser.OrNode;
import com.akiban.sql.parser.ResultColumn;
import com.akiban.sql.parser.ResultColumnList;
import com.akiban.sql.parser.SelectNode;
import com.akiban.sql.parser.ValueNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

// TODO: Auto-generated Javadoc
/**
 * The Class HadoopVisitor.
 */
public class HadoopVisitor implements Visitor {

	/** The subcommands generated for each part of the query */
	private String select, from, where, groupby;
	
	/**
	 * Gets the generated command.
	 *
	 * @return the generated command
	 */
	public String getGeneratedCommand(){
		if( groupby == null )return "";
		
		String ret = 
				"COUNT=function(x){return (dim(x));}; AVG=function(x){return (mean(x));}; MIN=function(x){return (min(x));}; MAX=function(x){return (max(x));};\n" +
				"map <- function(k,v){\n" +
				"  key = v["+ groupby +"];\n" +
				"  val = v["+ select.substring(select.indexOf('(')+2, select.indexOf(')')) +"];\n" +
				"  return (keyval(key,val));\n" +
				"}\n" +
				"reduce <- function(key,vals){\n" +
				"  return (keyval(key,"+ select.substring(0, select.indexOf('(')) +  "(vals)));\n" +
				"}\n"+
				"source = \""+from+"\";\n"
				;
		return ret;
	}
	
	public boolean skipChildren(Visitable v) throws StandardException {
		if(v instanceof ResultColumnList || v instanceof FromList ||  v instanceof GroupByList )return true;
		return false;
	}

	public boolean stopTraversal() {
		return false;
	}

	public Visitable visit(Visitable v) throws StandardException {
		if(v instanceof ResultColumnList)visit((ResultColumnList)v);
		else if(v instanceof FromList)visit((FromList)v);
		else if(v instanceof GroupByList)visit((GroupByList)v);
		else if(v instanceof ResultColumn)visit((ResultColumn)v);
		else if(v instanceof SelectNode){
			SelectNode sn = (SelectNode)v;
			ValueNode vn = sn.getWhereClause();
			if( vn != null ){
				ExpressionVisitor ev = new ExpressionVisitor(false);
				vn.accept(ev);
				where = ev.getGeneratedCommand();
			}
		}
		else System.out.println(v.getClass().toString());
		return null;
	}
	
	/**
	 * Visit the selected columns generating, in case, subexpressions.
	 *
	 * @param rcl the list of selected columns
	 * @throws StandardException the standard exception
	 */
	public void visit(ResultColumnList rcl) throws StandardException {
		select = "";
		boolean first = true;
		for( ResultColumn rc : rcl ){
			if(!first)select += ",";
			else first = false;
			ValueNode expr = rc.getExpression();
			ExpressionVisitor v = new ExpressionVisitor(false);
			expr.accept(v);
			select += v.getGeneratedCommand() + " as "+rc.getName().toUpperCase();
			
		}
	}
	
	/**
	 * It saves the source table name.
	 *
	 * @param fl the list of sources
	 * @throws StandardException the standard exception
	 */
	public void visit(FromList fl) throws StandardException {
		from = "";
		boolean first = true;
		for( FromTable ft : fl ){
			if(!first)from += ",";
			else first = false;
			from +=  ft.getTableName();
		}
	}
	
	/**
	 * Visits the group by list.
	 *
	 * @param gbl the group by list
	 * @throws StandardException the standard exception
	 */
	public void visit(GroupByList gbl) throws StandardException {
		groupby = "";
		boolean first = true;
		for( GroupByColumn gbc : gbl ){
			if(!first)groupby += ",";
			else first = false;
			groupby +=" "+gbc.getColumnName().substring(1)+" ";
		}
	}
	
	public boolean visitChildrenFirst(Visitable arg0) {
		return false;
	}

	/**
	 * The Class ExpressionVisitor. Used to generate a R command from a math/bool expression
	 */
	public class ExpressionVisitor implements Visitor{
		
		private String result = "";
		
		private boolean skiproot;
		
		private boolean skippedroot1;
		
		private boolean skippedroot2;
		
		/**
		 * Instantiates a new expression visitor.
		 *
		 * @param skiproot specifies if the root of the tree must be skipped or not
		 */
		public ExpressionVisitor(boolean skiproot){
			this.skiproot = skiproot;
		}
		
		public boolean stopTraversal() {return false;}
		
		public boolean visitChildrenFirst(Visitable arg0) {return false;}
		
		public boolean skipChildren(Visitable arg0) throws StandardException {if( skiproot && !skippedroot2 ){ skippedroot2 = true; return false;}return true; }

		public Visitable visit(Visitable v) throws StandardException {
			if( skiproot && !skippedroot1 ){ skippedroot1 = true; return null; }
			if( v instanceof ColumnReference )result += ((ColumnReference)v).getColumnName();
			else if( v instanceof AggregateNode ){
				AggregateNode an = (AggregateNode)v;
				result += an.getAggregateName() + "(";
				ExpressionVisitor ev = new ExpressionVisitor(true);
				an.accept(ev);
				result += ev.getGeneratedCommand() + ")";
			}
			else if( v instanceof NotNode ){
				NotNode an = (NotNode)v;
				result += "! (";
				ExpressionVisitor ev = new ExpressionVisitor(true);
				an.accept(ev);
				result += ev.getGeneratedCommand() + ")";
			}
			else if( v instanceof BinaryArithmeticOperatorNode ){
				BinaryArithmeticOperatorNode bn = (BinaryArithmeticOperatorNode)v;
				result += "(";
				ExpressionVisitor ev = new ExpressionVisitor(false);
				bn.getLeftOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += bn.getOperator();
				ev = new ExpressionVisitor(false);
				bn.getRightOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += ")";
			}else if( v instanceof NumericConstantNode ){
				NumericConstantNode nn = (NumericConstantNode)v;
				result += nn.getValue();
			}
			else if( v instanceof BinaryRelationalOperatorNode ){
				BinaryRelationalOperatorNode bn = (BinaryRelationalOperatorNode)v;
				result += "(";
				ExpressionVisitor ev = new ExpressionVisitor(false);
				bn.getLeftOperand().accept(ev);
				result += ev.getGeneratedCommand();
				if( bn.getOperator().equals("="))
					result += "==";
				else
					result += bn.getOperator();
				ev = new ExpressionVisitor(false);
				bn.getRightOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += ")";
			}
			else if( v instanceof AndNode ){
				AndNode bn = (AndNode)v;
				result += "(";
				ExpressionVisitor ev = new ExpressionVisitor(false);
				bn.getLeftOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += " & ";
				ev = new ExpressionVisitor(false);
				bn.getRightOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += ")";
			}
			else if( v instanceof OrNode ){
				OrNode bn = (OrNode)v;
				result += "(";
				ExpressionVisitor ev = new ExpressionVisitor(false);
				bn.getLeftOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += " | ";
				ev = new ExpressionVisitor(false);
				bn.getRightOperand().accept(ev);
				result += ev.getGeneratedCommand();
				result += ")";
			}
			else System.out.println(v.getClass().toString());
			return null;
		}
		
		/**
		 * Gets the generated command.
		 *
		 * @return the generated command
		 */
		public String getGeneratedCommand(){
			return result;
		}
	}
	
}
