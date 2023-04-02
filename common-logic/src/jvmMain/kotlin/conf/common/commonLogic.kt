package conf.common

actual fun commonLogic(input: String): String {
    return conf.domain.domainFunction(input.uppercase())
}
