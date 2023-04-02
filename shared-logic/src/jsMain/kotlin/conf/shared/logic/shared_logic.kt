package conf.shared.logic

actual fun sharedLogic(input: String): String {
    return conf.domain.domainFunction(input.uppercase())
}
