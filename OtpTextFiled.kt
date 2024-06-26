
@Composable
fun OtpTextField(
  modifier: Modifier = Modifier,
  otpText: String,
  otpCount: Int = 6,
  onOtpTextChange: (String, Boolean) -> Unit
) {
  LaunchedEffect(Unit) {
    if (otpText.length > otpCount) {
      throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
    }
  }

  BasicTextField(
    modifier = modifier,
    value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
    onValueChange = {
      if (it.text.length <= otpCount) {
        onOtpTextChange.invoke(it.text, it.text.length == otpCount)
      }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    decorationBox = {
      Row(horizontalArrangement = Arrangement.Center) {
        repeat(otpCount) { index ->
          CharView(
            index = index,
            text = otpText
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
    index == text.length -> "0"
    index > text.length -> ""
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
