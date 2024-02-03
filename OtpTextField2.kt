
@OptIn(ExperimentalFoundationApi::class)
object DigitsOnlyTransformation : InputTransformation {
  override val keyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Number,
    imeAction = ImeAction.Done
  )

  @RequiresApi(Build.VERSION_CODES.N)
  override fun transformInput(
    originalValue: TextFieldCharSequence,
    valueWithChanges: TextFieldBuffer
  ) {
    if (valueWithChanges.asCharSequence().contains("""\d""".toRegex()).not()) {
      valueWithChanges.revertAllChanges()
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OtpTextField(
  modifier: Modifier = Modifier,
  otpCount: Int = 6,
) {
  val textFieldState = rememberTextFieldState()

  BasicTextField2(
    modifier = modifier,
    state = textFieldState,
    inputTransformation = InputTransformation.maxLengthInChars(otpCount)
      .then(DigitsOnlyTransformation),
    decorator = {
      Row(horizontalArrangement = Arrangement.Center) {
        repeat(otpCount) { index ->
          CharView(
            index = index,
            text = textFieldState.text.toString()
          )
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
    }
  )
}

@Composable
private fun CharView(
  index: Int,
  text: String
) {
  val isFocused = text.length == index
  val char = when {
    index >= text.length -> ""
    else -> text[index].toString()
  }
  Text(
    modifier = Modifier
      .width(40.dp)
      .border(
        1.dp, when {
          isFocused -> GreyDark
          else -> GreyLight
        }, RoundedCornerShape(8.dp)
      )
      .padding(2.dp),
    text = char,
    style = MaterialTheme.typography.h4,
    color = if (isFocused) {
      GreyLight
    } else {
      GreyDark
    },
    textAlign = TextAlign.Center
  )
}

val GreyLight = Color(0xFFB5B6BA)
val GreyDark = Color(0xFF60626C)
