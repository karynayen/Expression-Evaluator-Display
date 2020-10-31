import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class ExpressionEvaluator.
 */
public class ExpressionEvaluator extends Application {

	/** The data stack. */
	private GenericStack<Double> dataStack;

	/** The operator stack. */
	private GenericStack<String>  operStack;

	/** The main. */
	private BorderPane main = new BorderPane();

	/** The solution label. */
	private Label solutionLabel;

	/** The tokens. */
	private String[] tokens; //array or arrayList

	/** The input tf. */
	private TextField inputTf;

	/**
	 * Instantiates a new expression evaluator.
	 */
	public ExpressionEvaluator() {
		dataStack = new GenericStack<Double>();
		operStack = new GenericStack<String>();

	}
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		solutionLabel = new Label("");
		primaryStage.setTitle("Expression Evaluator");	
		Scene scene = new Scene(main, 500,150);

		VBox input = new VBox(3); 
		Label inputLabel = new Label("Enter an Expression: ");
		inputTf = new TextField();
		input.getChildren().addAll(inputLabel, inputTf, new Label (""));
		VBox.setMargin(inputLabel, new Insets( 0, 20, 0, 20 ));
		VBox.setMargin(inputTf, new Insets( 0, 20, 0, 20 ));

		main.setCenter(solutionLabel);

		VBox buttonBox = new VBox(5);
		Button evaluate = new Button("Evaluate");
		buttonBox.getChildren().addAll(evaluate, new Label(""));
		buttonBox.setAlignment(Pos.CENTER);

		main.setBottom(buttonBox);
		main.setTop(input);

		evaluate.setOnAction(e -> { 
			errorCheckAndRun();
		});

		inputTf.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER){
				errorCheckAndRun();
			}
		});
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage

	}

	/**
	 * Error check and run.
	 */
	private void errorCheckAndRun() {
		if(inputTf.getText().trim().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText("Evaluate Failed!");
			alert.setContentText("Enter an expression");
			alert.showAndWait();
		}else {
			solutionLabel.setText(evaluateExpression(inputTf.getText()));
		}
	}

	/**
	 * Evaluates the expression by first massaging the string, and then splitting it
	 * into "tokens" that are either operations or data operands. 
	 *
	 * @param str this is the string that the user typed in the text field
	 * @return the string that is the result of the evaluation. It should include the original
	 *         expression and the value that it equals, or indicate if some error occurred.
	 */
	protected String evaluateExpression(String str) { 
		String originalStr = str;
		dataStack = new GenericStack<Double>();
		operStack = new GenericStack<String>();
		str = str.replaceAll("(\\+|-|/|\\(|\\)|\\*)", " $1 ");  //adds spaces to help with tokenization later
		tokens = str.trim().split("\\s+");
		str = "";
		
		//checks start and end for extra operations
		String prevToken = tokens[0];
		if (tokens[0].equals("+")||tokens[0].equals("*")||tokens[0].equals("/")){
			return "Op Error: Starting with operation " + tokens[0];
		}
		if (tokens[tokens.length-1].equals("+")||tokens[tokens.length-1].equals("/") ||tokens[tokens.length-1].equals("*")||tokens[tokens.length-1].equals("/")){
			return "Op Error: Ending with operation " + tokens[tokens.length-1];
		}
		
		//checks paren and data count, modifies expression for implicit multiplication
		int leftParen = 0; 
		int rightParen = 0;
		int dataCount = 0;
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].equals("(") && !(prevToken.equals("+")||prevToken.equals("-")
					||prevToken.equals("*")||prevToken.equals("/")|| prevToken.equals("("))) {
					str += "* ";
					str += tokens[i] + " "; 
			}else if (prevToken.equals("(") && (tokens[i].equals("+")||tokens[i].equals("*")||tokens[i].equals("/"))) {
				return "Op Error: " + prevToken + tokens[i];
			}
			else if (prevToken.equals(")") && !(tokens[i].equals("+")||tokens[i].equals("-")
					||tokens[i].equals("*")||tokens[i].equals("/")|| tokens[i].equals("(") || tokens[i].equals(")"))) {
				str += "* ";
				str += tokens[i] + " "; 
			}
			else {
				str += tokens[i] + " "; 
			}
			if(tokens[i].equals(")")) {
				rightParen++;
			}else if(tokens[i].equals("(")) {
				leftParen++;
			}else if(!(tokens[i].equals("+")||tokens[i].equals("-")
					||tokens[i].equals("*")||tokens[i].equals("/")|| tokens[i].equals("("))) {
				dataCount ++;
			}
			prevToken = tokens[i];
		}

		tokens = str.trim().split("\\s+");
		if(leftParen != rightParen) {
			return "Paren Error: Uneven count";
		}
		if(dataCount == 0) {
			return "Op Error: Must Add Data";
		}
		
		//checks for stand-alone negatives, modifies, and adds to array
		String [] temp = new String [tokens.length];
		int j = 0; 
		int newSize = 0;
		prevToken = tokens[0];
		for (int i = 0; i < tokens.length; i ++) {
			if (prevToken.equals("(") && tokens[i].equals("-")) {
				temp[j] = "-"+tokens[i+1];
				i++;
				newSize++;
			}else {
				temp[j] = tokens[i];
				newSize++;
			}
			j++;
			prevToken = tokens[i];
			
		}
		
		//insert new values into an array with proper size. 
		//Checks for double numbers and three in a row operations (not including paren)
		tokens = new String [newSize];
		int multOp = 0;
		int multData = 0;
		for(int i = 0; i < tokens.length; i++) {
			tokens[i] = temp[i];
			if (temp[i].equals("+") || temp[i].equals("-") || temp[i].equals("/") || temp[i].equals("*")){
				multOp ++;
				multData = 0; 
			}else if(!temp[i].equals("(") && !temp[i].equals(")")) {
				multOp = 0; 
				multData ++; 
			}else {
				multOp = 0;
				multData = 0;
			}
			if (multData > 1) {
				return "Data Error: Double Numbers";
			}
			if (multOp > 2) {
				return "Op Error: Too Many Operations";
			}
		}
		
		//begins the algorithm
		int index = 0;
		int operInARow = 0;
		Boolean negative = false;
		while (index < tokens.length) {
			//check if data or operation with parseDouble
			try {
				//checks for extraneous characters
				String test = tokens[index].replaceAll("(\\D+|\\d*\\.?\\d+)", " $1 "); 
				String [] testParse = test.trim().split("\\s+");
				if(testParse.length > 1 && !testParse[0].equals("-")) {
					return "Data Error: Invalid Input " + tokens[index];
				}
				double data = Double.parseDouble(tokens[index]);
				
				//negative number boolean
				if(negative) {
					negative = false;
					data = (-1) * data;
				}
				
				dataStack.push(data);
				operInARow = 0;
		
			}catch(NumberFormatException e) {
				
				operInARow ++;
				String currOpp = tokens[index];
				
				//if data is anything other than an operation
				if(!(currOpp.equals("+") || currOpp.equals("-") || currOpp.equals("/") || currOpp.equals("*") || currOpp.equals("(") || currOpp.equals(")"))){
					return "Data Error: Invalid Input";
				}
				
				//checks for valid two in a row scenarios
				if (operInARow > 1) {
					if(currOpp.equals("-")) {
						negative = true;
						index ++; //make sure to skip because never reached!!!
						continue;
					} else {
						//operation does not count if "(" - no need to check ")" because this is never added to operStack
						if(!currOpp.equals("(") ) {
							return "Op Error: " + currOpp + " " +  operStack.peek();
						}
					}
				}

				if (operStack.isEmpty()) {
					if(currOpp.equals("(")) {
						operInARow = 0;
						operStack.push(currOpp);
					}else if(currOpp.equals("-")){ 
						operStack.push(currOpp);
					}else {
						operStack.push(currOpp);
					}
				}else {
					
					String topStackOpp = operStack.peek();
					if(currOpp.equals("(")) {
						operInARow = 0;
						if(operStack.peek().equals("-")) {
							negative = false;
						}
						operStack.push(currOpp);
					} else if(currOpp.equals(")")){
						operInARow = 0;
						while(!operStack.peek().equals("(")) {
							if (solve().contains("Div0 Error:")) {
								return "Div0 Error:";
							}
							operInARow = 0;
						}
						operStack.pop();
					} 
					else if((currOpp.equals("+")||currOpp.equals("-"))&&
							(topStackOpp.equals("*")||topStackOpp.equals("/"))) {
						while(operStack.getSize() > 1 && !operStack.peek().equals("(")) {
							if (solve().contains("Div0 Error:")) {
								return "Div0 Error:";
							}
						}
						if(!operStack.peek().equals("(") && operStack.getSize() > 0) {
							if (solve().contains("Div0 Error:")) {
								return "Div0 Error:";
							}
						}
						operStack.push(currOpp);
					}
					else if ((currOpp.equals("*")||currOpp.equals("/"))&&
							(topStackOpp.equals("+")||topStackOpp.equals("-"))){
						//currOpp > topStack
						operStack.push(currOpp);
					} 
					else if((currOpp.equals("*")||currOpp.equals("/"))&&
							(topStackOpp.equals("*")||topStackOpp.equals("/"))) {
						//currOpp == topStack
						if (solve().contains("Div0 Error:")) {
							return "Div0 Error:";
						}
						operStack.push(currOpp);
					} else if((currOpp.equals("+")||currOpp.equals("-")) && 
							(topStackOpp.equals("+")||topStackOpp.equals("-"))) {
						solve();
						operStack.push(currOpp);
					}
					else {
						operStack.push(currOpp);
					}
				}
			}
			index ++;
		}
		while(operStack.getSize() > 0) {
			if (solve().contains("Div0 Error:")) {
				return "Div0 Error:";
			}
		}
		//System.out.println(originalStr+ " = " + dataStack.peek());
		return (originalStr + " = " + dataStack.peek());
		
	}

	/**
	 * Solve.
	 */
	private String solve() {
	    Double d1 = dataStack.pop();
        Double d2;
        String opp = operStack.pop();
        Double result = 0.0;
     
        if (dataStack.getSize() > 0) {
          d2 = dataStack.pop();
          if(opp.equals("*")) {
              result = d1 * d2; 
          } else if(opp.equals("/")) {
              if(d1 == 0) {
                  return "Div0 Error:";
              }
              result = d2 / d1; 
          } else if(opp.equals("-")) {
              result = d2 - d1; 
          } else if(opp.equals("+")) {
              result = d2 + d1; 
          }
      }else {
          if (opp.equals("-")) {
              result =  d1 * (-1);
          }
      }
         dataStack.push(result);
         return "";
         
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);

	}

}