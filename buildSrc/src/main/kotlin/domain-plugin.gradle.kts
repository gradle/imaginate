import conf.build.DomainTask

tasks.register<DomainTask>("domainTask") {
    input.set("Hello, world!")
    output.set(layout.buildDirectory.file("domain-output.txt"))
}
