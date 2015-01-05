package org.hidetake.gradle.ssh.plugin

import org.hidetake.groovy.ssh.core.Remote
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.Use

@Use(RemoteContainerExtension)
class RemoteContainerExtensionSpec extends Specification {

    def "role() should return empty collection if empty one is given"() {
        given:
        def remotes = []

        when:
        Collection<Remote> associated = remotes.role('something')

        then:
        associated.empty
    }

    @Unroll
    def "role() should filter remotes by #roles"() {
        given:
        def remotes = [
                createRemote('remote1', 'roleA'),
                createRemote('remote2', 'roleA', 'roleB'),
                createRemote('remote3', 'roleB'),
                createRemote('remote4', 'roleC'),
                createRemote('remote5')]

        when:
        Collection<Remote> associated = remotes.role(roles as String[])
        def actualRemoteNames = associated.collect { it.name }

        then:
        actualRemoteNames.toSet() == expectedRemoteNames.toSet()

        where:
        roles                                | expectedRemoteNames
        ['roleA']                            | ['remote1', 'remote2']
        ['roleB']                            | ['remote2', 'remote3']
        ['roleC']                            | ['remote4']
        ['roleD']                            | []
        ['roleA', 'roleB']                   | ['remote1', 'remote2', 'remote3']
        ['roleB', 'roleC']                   | ['remote2', 'remote3', 'remote4']
        ['roleA', 'roleC']                   | ['remote1', 'remote2', 'remote4']
        ['roleA', 'roleD']                   | ['remote1', 'remote2']
        ['roleA', 'roleB', 'roleC']          | ['remote1', 'remote2', 'remote3', 'remote4']
        ['roleA', 'roleB', 'roleD']          | ['remote1', 'remote2', 'remote3']
        ['roleA', 'roleB', 'roleC', 'roleD'] | ['remote1', 'remote2', 'remote3', 'remote4']
    }

    def "role() should throw error if null is given"() {
        given:
        def remotes = []

        when:
        remotes.role(null)

        then:
        AssertionError e = thrown()
        e.message.contains('role')
    }

    def "role() should throw error if no argument is given"() {
        given:
        def remotes = []

        when:
        remotes.role()

        then:
        AssertionError e = thrown()
        e.message.contains('role')
    }

    def "adding extension to null object should cause error"() {
        when:
        RemoteContainerExtension.role(null, 'role1')

        then:
        AssertionError e = thrown()
        e.message.contains('remotes')
    }

    private static createRemote(String name, String... roles) {
        def remote = new Remote(name)
        remote.roles.addAll(roles)
        remote
    }

}
