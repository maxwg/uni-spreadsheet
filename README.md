# uni-spreadsheet
An experimental spreadsheet program with real-time latex rendering 


![fff](https://gitlab.cecs.anu.edu.au/uploads/u5584091/spreadsheet/1fb9452705/fff.png)

The program was designed to be extremely intuitive to use effectively. The syntax for expressions is as expected from most mathematics programs - such as Wolfram Alpha, with a very intuitive real-time expression display (running on a secondary thread). Expressions can include the expected symbols, *, ^, /, -, +, any decimal number, constants (pi, e), and even functions (predefined like sin, or defined through a function editor)

The function editor uses a simple python-like language (which also supports advanced expression parsing) to handle numerical processing. The syntax is as follows: 
```
A function is declared with:
[FUNCTION_NAME]([number] [PARAMETER NAME]){
 [CODE]
}

Assignments are done as such:
[VARIABLE NAME] = [EXPRESSION]; Where expression is any valid mathematical expression, and can include functions defined in the file
with arrays accessed like:
[MAINPARAMETER][n]
and lengths obtained with
#[MAINPARAMETER]

If and while statements are as expected:
if/while([EXPRESSION] [booleanOperator] [EXPRESSION]){
[CODE]
}
```
Overall, this results in a highly intuitive and extremely basic programming language that is turing complete.


![UML](https://gitlab.cecs.anu.edu.au/uploads/u5584091/spreadsheet/18e788863a/UML.png)


## Testing
Integration tests were already provided - and these were modified to handle the modifications to the GUI - automatic expression evaluation and new function syntax. Overall these tests are fairly comprehensive - testing Expression parsing and the functional language effectively. The only issue with these tests is that they are not automatic - and hence will not test every possible case (or at least a significant subset given automatic test case generation).

As a result, an automatic test added to manage expression calculation (this runs 10,000 times) - one which would almost perfectly verify that expressions are 100% working. A random expression consisting of valid operators was generated. A JavaScript engine was intitiated, and the random expression evaluated. The Grammar Expression parser was then used to evaluate the expression, and it is asserted that the returns must be within a range (to account for double precision errors, from different parse orders).

The biggest difficulty with testing the functional language automatically is that it is almost impossible to verify correctness - without having to verify the correctness of the test code!! Small parts (such as variable mappings/boolean expression evaluations) can be tested, however these are minor in the long run, with all of them needing to be perfectly correct as well as the entirety of its framework for validity. Hence, the language could only be tested manually. It is somewhat likely that there is indeed a flaw with the language - although there is no evidence yet to suggest this after programming numerous default funcitons.

The automatic equation renderer could also only be tested visually - as programmi ng a test which is capable of reading an interpreting the rendered equation is more difficult than the entirity of the project itself.

Overall, I am fairly happy with the robustness of the code.

## Extensions
* Intuitive input (Can now type directly in cells, which shifts focus. Arrow keys can unshift focus if the caret is at the start/end/top/bottom of text respective to the arrow key).
* Proper Expression rendering (Through latex)
* Real-Time calculation and expression rendering in a background thread such that performance is not affected
* Rendered expressions act as an easy way for the user to tell what they are typing wrong, as it is shown in real time. Rendered expressions also add helpful brackets for operators whose orders of operations are not commonly known (e.g, %, ^).
* Implicit multiplication
* Advanced cell calculations (no need to press calculate!). Yes, this does indeed handle updates to referenced cells!
* More operators (^,%), constants (pi, e), functions (sin, log, etc).
* Advanced Automated expression grammar - To create a new Function, simply implement a new FunctionOp, and append the function's class to fexp. New binary/unary operators can be similarly added.
* Effective python like language - which is interpreted during calculation, allowing the use of other functions defined in the file in any order (unlike C)
* Retheme - looks much nicer.

### Interesting Psychology Observation
Look at the menu bar (the bar containing the save/load/etc functions). Look at the cell labels (A-Z, 1-100). After careful (visual/programmatical) observation, note that they are not the same color. However, if they were set to the same colour, the menu bar looks much brighter, because of the surrounding colors. Some sort of optical illusion.
