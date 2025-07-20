package Exceptions

sealed trait AppException {
  def message: String
}