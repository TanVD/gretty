package org.akhikhl.gretty.internal.integrationTests

import org.akhikhl.gretty.ServletContainerConfig
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FarmIntegrationTestPlugin extends BasePlugin {

  private static final Logger log = LoggerFactory.getLogger(FarmIntegrationTestPlugin)

  @Override
  protected void configureExtensions(Project project) {
    super.configureExtensions(project)

    project.ext.defineFarmIntegrationTestAllContainers = { Collection integrationTestContainers = null, Closure configureFarm ->

      def farmIntegrationTestAllContainersTask = project.tasks.findByName('farmIntegrationTestAllContainers')
      if (farmIntegrationTestAllContainersTask) {
        return farmIntegrationTestAllContainersTask
      }

      project.ext.defineIntegrationTestAllContainers(integrationTestContainers)

      farmIntegrationTestAllContainersTask = project.task('farmIntegrationTestAllContainers')

      if (!integrationTestContainers) {
        integrationTestContainers = ServletContainerConfig.getConfigNames().collect()
      } // returns immutable and we want to filter later

      if (project.hasProperty('testAllContainers') && project.testAllContainers) {
        integrationTestContainers.retainAll(Eval.me(project.testAllContainers))
      }

      // farmSecure tests not working on Jetty 9.3 and 9.4, see https://github.com/gretty-gradle-plugin/gretty/issues/67
      if (project.path.startsWith(':farmSecure')) {
        println "Excluding farmSecure tests from Jetty 9.3/9.4, see https://github.com/gretty-gradle-plugin/gretty/issues/67 ."
        integrationTestContainers -= ['jetty9.3', 'jetty9.4']
      }

      integrationTestContainers.each { container ->

        project.farms.farm container, {
          servletContainer = container
          configureFarm.delegate = delegate
          configureFarm()
        }

        project.tasks.matching { it.name in ['farmBeforeIntegrationTest' + container, 'farmAfterIntegrationTest' + container] }.all {
          integrationTestTask 'integrationTest_' + container
        }

        project.tasks.matching { it.name == 'farmIntegrationTest' + container }.all {
          integrationTestTask 'integrationTest_' + container
          farmIntegrationTestAllContainersTask.dependsOn it
        }
      }

      farmIntegrationTestAllContainersTask
    }
  }
}
