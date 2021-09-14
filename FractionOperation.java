/**
 * @author      Gene Kostruba <genekostruba@comcast.net>
 * @version     1.0
 * 
 * Assumptions:
 *
 * The input arguments are properly formatted.  There are two unsigned operands, separated by
 * the operator, with one or more spaces separating the operator from each operand.  Each operand
 * is in one of the following 3 formats:
 *
 *     whole
 *     numerator/denominator
 *     whole_numerator/denominator
 *
 * where whole, numerator, and denominator are digits, and there is no embedded whitespace.
 * A negative result from the operation is possible if the operation is a subtraction, and
 * the second operand is larger than the first operand.
 *
 * Errors are thrown if:
 *
 * - there are not exactly 3 arguments, proper format is:
 *
 *           > java FractionOperation operand1 operator operand2
 *
 * - whole, numerator, and denominator are not all digits
 * - denominator is zero
 * - operator is not one of * / + -
 *
 * Note that if * is the desired operator, it must be specified in double quotes on the command
 * line, not to be treated as a file wildcard character:
 *
 * Incorrect:  > java FractionOperation 1_1/2 * 3_3/4
 * Correct:    > java FractionOperation 1_1/2 "*" 3_3/4 
 */
public class FractionOperation {
	private static String operator;
	private static Operand operand1;
	private static Operand operand2;
	private static Result result;
	
	private static class Operand {	
		private int whole;
		private boolean wholePartPresent;
		private int numerator;
		private int denominator;
		private boolean fractionalPartPresent;
		private int computationalNumerator;
		private int computationalDenominator;
		
		int getWhole() { return whole; }
		void setWhole(int whole) { this.whole = whole; }
		boolean getWholePartPresent() { return wholePartPresent; }
		void setWholePartPresent(boolean wholePartPresent) {
			this.wholePartPresent = wholePartPresent;
		}
		int getNumerator() { return numerator; }
		void setNumerator(int numerator) { this.numerator = numerator; }
		int getDenominator() { return denominator; }
		void setDenominator(int denominator) { this.denominator = denominator; }
		boolean getFractionalPartPresent() { return fractionalPartPresent; }
		void setFractionalPartPresent(boolean fractionalPartPresent) {
			this.fractionalPartPresent = fractionalPartPresent;
		}
		int getComputationalNumerator() { return computationalNumerator;}
		void setComputationalNumerator(int computationalNumerator) {
			this.computationalNumerator = computationalNumerator;
		}
		int getComputationalDenominator() { return computationalDenominator; }
		void setComputationalDenominator(int computationalDenominator) {
			this.computationalDenominator = computationalDenominator;
		}
	}
	
	private static class Result {
		private int whole;
		private boolean wholePartPresent;
		private int numerator;
		private int denominator;
		private boolean fractionalPartPresent;
		
		int getWhole() { return whole; }
		void setWhole(int whole) { this.whole = whole; }
		boolean getWholePartPresent() { return wholePartPresent; }
		void setWholePartPresent(boolean wholePartPresent) {
			this.wholePartPresent = wholePartPresent;
		}
		int getNumerator() { return numerator; }
		void setNumerator(int numerator) { this.numerator = numerator; }
		int getDenominator() { return denominator; }
		void setDenominator(int denominator) { this.denominator = denominator; }
		boolean getFractionalPartPresent() { return fractionalPartPresent; }
		void setFractionalPartPresent(boolean fractionalPartPresent) {
			this.fractionalPartPresent = fractionalPartPresent;
		}
	}
	
	public static void main(String[] args) {
		parseInput(args);
		setComputationalFraction(operand1);
		setComputationalFraction(operand2);
		switch (operator) {
			case "*":
				doMultiply();
				break;
			case "/":
				doDivide();
				break;
			case "+":
				doAdd();
				break;
			case "-":
				doSubtract();
				break;
			default:
				System.out.println(String.format(
					"Invalid operator '%s', expecting one of * / + -", operator));
				System.exit(1);	
		}
		
		reduceResultWithProperFraction();
		printResult();
		
		System.exit(0);
	}
	
	private static void parseInput(String[] args) {
		if (args.length != 3) {
			System.out.println(String.format(
				"Improper number of arguments %d, expecting 'operand1 operator operand2'", args.length));
			System.exit(1);
		}
		
		// args[1] is the operator
		operator = args[1];
		
		// args[0] and args[2] are the operands
		operand1 = parseOperand(args[0]);
		operand2 = parseOperand(args[2]);
	}
	
	private static Operand parseOperand (String input) {
		Operand operand = new Operand();
		
		if (input.contains("_")) {
			// input includes a whole part and a fractional part
			String[] parts = input.split("_");
			operand.setWhole(getInteger(parts[0]));
			operand.setWholePartPresent(true);
			String[] fractionalParts = parts[1].split("/");
			operand.setNumerator(getInteger(fractionalParts[0]));
			operand.setDenominator(getInteger(fractionalParts[1]));
			checkDenominator(operand.getDenominator());
			operand.setFractionalPartPresent(true);
		} else if (input.contains("/")) {
			// input is fraction only, no whole part
			operand.setWholePartPresent(false);
			String[] fractionalParts = input.split("/");
			operand.setNumerator(getInteger(fractionalParts[0]));
			operand.setDenominator(getInteger(fractionalParts[1]));
			checkDenominator(operand.getDenominator());
			operand.setFractionalPartPresent(true);
		} else {
			// input is whole only, no fractional part
			operand.setWhole(getInteger(input));
			operand.setWholePartPresent(true);
			operand.setFractionalPartPresent(false);
		}
		
		return operand;
	}
	
	private static int getInteger(String part) {
		int x = 0;
		try {
			x = Integer.parseInt(part);
		} catch (NumberFormatException nfe) {
			System.out.println(String.format("Input part '%s' is not a number", part));
			System.exit(1);
		}
		
		return x;
	}
	
	private static void checkDenominator(int denominator) {
		if (denominator == 0) {
			System.out.println("Denominator of fraction is zero");
			System.exit(1);					
		}
	}

	private static void setComputationalFraction(Operand operand) {
		// Computational fraction is the same as the input operand if input operand has no whole
		// part.  If the input operand has a whole part, the computational fraction is the operand
		// as an improper fraction (or whole/1 if there is no fractional part).  The operation is
		// executed on operands as computational fractions.
		int whole = operand.getWhole();
		int numerator = operand.getNumerator();
		int denominator = operand.getDenominator();
		
		if (operand.getWholePartPresent() == false) {
			// Operand only has fractional part
			operand.setComputationalNumerator(numerator);
			operand.setComputationalDenominator(denominator);
		} else if (operand.getFractionalPartPresent() == false) {
			// Operand only has whole part
			operand.setComputationalNumerator(whole);
			operand.setComputationalDenominator(1);			
		} else {
			// Operand has whole and fractional parts
			if (whole == 0) {
				operand.setComputationalNumerator(numerator);
				operand.setComputationalDenominator(denominator);				
			} else {
				operand.setComputationalNumerator(whole * denominator + numerator);
				operand.setComputationalDenominator(denominator);				
			}
		}
	}
	
	private static void doMultiply() {
		result = new Result();
		
		result.setNumerator(operand1.getComputationalNumerator() * operand2.getComputationalNumerator());
		result.setDenominator(operand1.getComputationalDenominator() * operand2.getComputationalDenominator());
	}
	
	private static void doDivide() {
		result = new Result();
		
		checkDivideByZero();
		int invertedNumerator = operand2.getComputationalDenominator();
		int invertedDenominator = operand2.getComputationalNumerator();
		result.setNumerator(operand1.getComputationalNumerator() * invertedNumerator);
		result.setDenominator(operand1.getComputationalDenominator() * invertedDenominator);
	}
	
	private static void doAdd() {
		result = new Result();
		
		int firstNumerator = operand1.getComputationalNumerator() * operand2.getComputationalDenominator();
		int secondNumerator = operand1.getComputationalDenominator() * operand2.getComputationalNumerator();
		result.setNumerator(firstNumerator + secondNumerator);
		result.setDenominator(operand1.getComputationalDenominator() * operand2.getComputationalDenominator());
	}
	
	private static void doSubtract() {
		result = new Result();
		
		int firstNumerator = operand1.getComputationalNumerator() * operand2.getComputationalDenominator();
		int secondNumerator = operand1.getComputationalDenominator() * operand2.getComputationalNumerator();
		result.setNumerator(firstNumerator - secondNumerator);
		result.setDenominator(operand1.getComputationalDenominator() * operand2.getComputationalDenominator());
	}
	
	private static void checkDivideByZero() {
		if (operand2.getComputationalNumerator() == 0) {
			System.out.println("Operation is division and second operand is zero");
			System.exit(1);	
		}
	}
	
	private static void reduceResultWithProperFraction() {
		// The result of the operation is a (possilby improper) fraction.  Compute its whole part,
		// then reduce any remaining fractional part.
		setResultWholeAndFractionalParts();
		reduceResultFractionalPart();
	}
	
	private static void setResultWholeAndFractionalParts() {
		int numerator = result.getNumerator();
		int denominator = result.getDenominator();
		
		// Check zero result
		if (numerator == 0) {
			result.setWhole(0);
			result.setWholePartPresent(true);
			result.setFractionalPartPresent(false);
			return;
		}
		// numerator could be negative as the result of a subtraction
		int whole = numerator / denominator;
		if (whole == 0) {
			result.setWholePartPresent(false);
			result.setFractionalPartPresent(true);
		} else {
			// if numerator is negative, whole will carry the sign
			result.setWhole(whole);
			result.setWholePartPresent(true);
			int remainder = numerator % denominator;
			if (remainder == 0) {
				result.setFractionalPartPresent(false);
			} else {
				result.setFractionalPartPresent(true);
				if (remainder < 0) {
					remainder = -remainder;
				}
				result.setNumerator(remainder);
			}
		}
		
		return;
	}
		
	private static void reduceResultFractionalPart() {
		if (result.getFractionalPartPresent() == false) {
			return;
		}
		int numerator = result.getNumerator();
		int denominator = result.getDenominator();
		int sign = 1;
		// If there is no whole part, numerator will carry the sign
		if (numerator < 0) {
			sign = -1;
			numerator = -numerator;
		}
		int factor = gcd(numerator, denominator);
		result.setNumerator(sign * numerator / factor);
		result.setDenominator(denominator / factor);
		
		return;
	}
	
	private static int gcd(int a, int b) {
		// Compute greatest common divisor for reduction
		int t = 0;
		while (b != 0) {
			t = b;
			b = a % b;
			a = t;
		}
		
		return a;
	}
	
	private static void printResult() {
		if (result.getWholePartPresent() == true) {
			System.out.print(result.getWhole());
			if (result.getFractionalPartPresent() == true) {
				System.out.print("_");
			}
		}
		if (result.getFractionalPartPresent() == true) {
			System.out.print(result.getNumerator());
			System.out.print("/");
			System.out.print(result.getDenominator());
		}
		System.out.println();
	}
}
