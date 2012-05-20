trait UpperLower[T] {

    val toLowerCase: T => T

    val alphabet: Set[T]

    lazy val toUpperMap: Map[T, T] = alphabet flatMap { mightBeUpper =>
        val definitelyLower = toLowerCase(mightBeUpper)
        mightBeUpper != definitelyLower match {
            case true  => Some(definitelyLower -> mightBeUpper)
            case false => None
        }
    } toMap

    val toUpperCase: T => T = upperOrLower => toUpperMap getOrElse (upperOrLower, upperOrLower)
}

object AsciiCharUpperLower extends UpperLower[Char] {

    override val toLowerCase = (_: Char) toLower

    override val alphabet = 'A' to 'z' toSet

}
