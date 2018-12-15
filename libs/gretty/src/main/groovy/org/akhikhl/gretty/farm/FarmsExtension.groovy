/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty.farm

import org.akhikhl.gretty.FarmConfig
import org.akhikhl.gretty.FarmsConfig
import org.gradle.api.Project

class FarmsExtension extends FarmsConfig {

    final Project project

    FarmsExtension(Project project) {
        this.project = project
    }

    @Override
    FarmConfig createFarm() {
        new FarmExtension(project)
    }
}
