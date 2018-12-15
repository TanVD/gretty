package org.akhikhl.gretty.task.app

/** @author sala */
class AppRedeployTask extends AppServiceTask {
    List webapps = []

    def webapp(String webapp) {
        webapps.add(webapp)
    }

    @Override
    String getCommand() {
        return "redeploy ${webapps.join(' ')}"
    }
}
