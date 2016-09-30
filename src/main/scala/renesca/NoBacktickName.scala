package renesca

//: ----------------------------------------------------------------------------------
//: Copyright © 2016 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

trait NonBacktickName {
  def name: String
  require(name.matches("^[^`]*$"), "Backticks are not allowed in label names")

  override def toString = name
}
