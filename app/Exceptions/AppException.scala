package Exceptions

sealed trait AppError {
  def message: String
}