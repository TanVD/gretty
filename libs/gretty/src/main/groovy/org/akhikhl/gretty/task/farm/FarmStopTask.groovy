/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty.task.farm

class FarmStopTask extends FarmServiceTask {

    @Override
    String getCommand() {
        'stop'
    }
}
