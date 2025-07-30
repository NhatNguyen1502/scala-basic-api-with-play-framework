package constants

object RegexConstants {
  val EmailRegex: String = "^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$"
  val PhoneNumberRegex: String = "^\\+?[0-9]{10,15}$"
}
