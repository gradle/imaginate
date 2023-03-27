package conf.common

import conf.domain.domainFunction
import java.util.*

fun commonLogic(input: String): String =
    domainFunction(input.reversed()).uppercase(Locale.getDefault())
