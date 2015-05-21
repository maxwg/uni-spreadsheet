package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import junit.*;

import expressions.Expression;

/**
 * 
 * SpreadsheetTest - This is a simple integration test. We basically set some
 * text within cells of the spread sheet and check they evaluate correctly.
 * 
 * @author Eric McCreath
 * 
 */

public class ExpressionTest {

	private static double ACCEPTABLEPRECISIONFACTOR = 0.00000000001;

	private String generateRandomExpression() {
		Random rng = new Random();
		String[] validSymbols = new String[] { "+", "-", "*", "/" };
		int terms = rng.nextInt(100);
		ArrayList<Object> expr = new ArrayList<Object>();
		for (int i = 0; i < terms; i++) {
			if (i % 2 == 0)
				expr.add(rng.nextDouble() * 1000+0.1);
			else
				expr.add(validSymbols[rng.nextInt(validSymbols.length)]);
		}
		if (terms % 2 == 0)
			expr.add(rng.nextDouble() * 1000);

		String exp = "";
		for (Object o : expr)
			exp += o.toString() + " ";

		return exp;
	}

	@Test
	public void testExpressionCalAuto() throws ScriptException {
		for (int i = 0; i < 10000; i++) {
			String expr = generateRandomExpression();
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			Object javResult = engine.eval(expr);
			try {
				double expResult = Expression.calculate(expr, null).evaluate();
				if (!(Math.abs(expResult
						- Double.parseDouble(javResult.toString())) < Math
						.abs(expResult) * ACCEPTABLEPRECISIONFACTOR)) {
					System.err.println("INVALID PARSING: " + expr
							+ "\nCalculated: " + expResult + "\nExpected "
							+ javResult);
					fail();
				}
			} catch (Exception e) {
				fail();
				e.printStackTrace();
			}
		}
	}
}
