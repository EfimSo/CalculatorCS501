package com.example.worksheet2_q3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.worksheet2_q3.ui.theme.Worksheet2Q3Theme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Worksheet2Q3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    val pattern = remember { Regex("^[\\dp+\\-/*.=s]+\$") }
    var fieldText by remember { mutableStateOf("") }
    var operand1: Float? by remember { mutableStateOf(null) }
    var operator: String by remember { mutableStateOf("") }
    var errorText: String by remember { mutableStateOf("") }

    // Handles the main logic of the calculator
    val OnFieldTextChange: (String) -> Unit =
        { it ->
            if (it.isEmpty()) {
                fieldText = ""
                errorText = ""      // C button clears error message
            } else if (it.matches(pattern)) {
                val lastChar = it.last()
                when {
                    lastChar.isDigit() -> {
                        fieldText = it
                    }

                    lastChar == '.' -> {
                        val textBeforeLastChar = it.dropLast(1)
                        if (!textBeforeLastChar.contains('.')) {
                            fieldText = it
                        }
                    }

                    lastChar in listOf('+', '-', '*', '/') -> {
                        if (operand1 == null) {
                            operand1 = fieldText.toFloatOrNull()
                            if (operand1 != null) {
                                operator = lastChar.toString()
                                fieldText = ""
                            } else {
                                fieldText = ""
                                errorText = "Error: Invalid Number Entered"
                            }
                        } else {
                            val operand2 = fieldText.toFloatOrNull()
                            if (operand2 != null) {
                                operand1 = executeOperation(operator, operand1!!, operand2,
                                    handleErrorText = {text -> errorText = text})
                                if (operand1 == null) // Computation error
                                {
                                    fieldText = ""
                                }
                                else{
                                    fieldText = operand1.toString()
                                    operand1 = null
                                }
                                operator = ""
                            } else {
                                fieldText = ""
                                errorText = "Error: Invalid Second Operand"
                            }
                        }
                    }

                    lastChar == '=' -> {
                        if (operand1 != null && operator.isNotEmpty()) {
                            val operand2 = fieldText.toFloatOrNull()
                            if (operand2 != null) {
                                operand1 = executeOperation(operator, operand1!!, operand2,
                                    handleErrorText = {text -> errorText = text})
                                if (operand1 == null) // Computation error
                                {
                                    fieldText = ""
                                }
                                else{
                                    fieldText = operand1.toString()
                                    operand1 = null
                                }
                                operator = ""
                            } else {
                                fieldText = ""
                                errorText = "Error: Invalid Second Operand"
                            }
                        }
                    }

                    lastChar == 's' -> {
                        val currentOperand = it.dropLast(1).toFloatOrNull()
                        if (currentOperand != null) {
                            operand1 = kotlin.math.sqrt(currentOperand)
                            fieldText = operand1.toString()
                            operator = ""
                            operand1 = null
                        } else {
                            fieldText = ""
                            errorText = "Error: Invalid Number Entered"
                        }
                    }

                    else -> {
                        errorText = "Invalid Character Entered"
                        fieldText = ""
                    }
                }
            }
        }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = fieldText,
            onValueChange = {
                OnFieldTextChange(it)
            },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontSize = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(60.dp)
        )
        CalculatorButtonLayout(fieldText, onFieldTextChange = {
            OnFieldTextChange(it)
        })
        Spacer(modifier = Modifier.height(10.dp))
        Text(errorText, fontSize = 20.sp, color = Color.Red, textAlign = TextAlign.Center,)
    }

}


@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    Worksheet2Q3Theme {
        Calculator()
    }
}

@Composable
fun CalculatorButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CalculatorButtonLayout(fieldText: String, onFieldTextChange: (String) -> Unit) {

    fun handleDigit(digit: Int){
        onFieldTextChange(fieldText + digit.toString())
    }

    fun handleOperator(operator: String){
        onFieldTextChange(fieldText + operator)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val buttonModifier = Modifier.weight(1f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("1", onClick = {handleDigit(1)}, buttonModifier)
            CalculatorButton("2", onClick = {handleDigit(2)}, buttonModifier)
            CalculatorButton("3", onClick = {handleDigit(3)},  buttonModifier)
            CalculatorButton("+", onClick = {handleOperator("+")}, buttonModifier)
            CalculatorButton("*", onClick = {handleOperator("*")}, buttonModifier)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("4", onClick = {handleDigit(4)},  buttonModifier)
            CalculatorButton("5", onClick = {handleDigit(5)},  buttonModifier)
            CalculatorButton("6", onClick = {handleDigit(6)}, buttonModifier)
            CalculatorButton("-", onClick = {handleOperator("-")}, buttonModifier)
            CalculatorButton("/", onClick = {handleOperator("/")},  buttonModifier)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("7",  onClick = {handleDigit(7)}, buttonModifier)
            CalculatorButton("8",  onClick = {handleDigit(8)}, buttonModifier)
            CalculatorButton("9",  onClick = {handleDigit(9)}, buttonModifier)
            CalculatorButton("sqrt", onClick = {handleOperator("s")}, modifier = Modifier.weight(2f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton("0", onClick = {handleDigit(0)}, buttonModifier)
            CalculatorButton(".", onClick = {handleOperator(".")}, buttonModifier)
            CalculatorButton("C", onClick = {onFieldTextChange("")}, buttonModifier)
            CalculatorButton("=", onClick = {handleOperator("=")}, modifier = Modifier.weight(2f))
        }
    }
}




fun executeOperation(operator: String, number1: Float, number2: Float, handleErrorText: (String) -> Unit): Float? {

    when (operator) {
        "+" -> return (number1 + number2)
        "-" -> return (number1 - number2)
        "*" -> return(number1 * number2)
        "/" -> {
            if (number2 == 0f) {
                handleErrorText("Error: Division by zero is undefined")
                return null
            } else {
                return (number1 / number2)
            }
        }
        else -> {
            handleErrorText("Error: Unknown operator")
            return null
        }
    }
}

