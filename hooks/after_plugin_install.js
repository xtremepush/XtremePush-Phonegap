"use strict";

var FSUtils = require("./FSUtils");

var ROOT_BUILD_GRADLE_FILE = "platforms/android/build.gradle";
var ROOT_REPOSITORIES_GRADLE_FILE = "platforms/android/repositories.gradle";
var APP_REPOSITORIES_GRADLE_FILE = "platforms/android/app/repositories.gradle";
var COMMENT = "//xtremepush HUAWEI plugin dep";
var NEW_LINE = "\n";

module.exports = function (context) {
    if (!FSUtils.exists(ROOT_BUILD_GRADLE_FILE)) {
        console.log("root gradle file does not exist. after_plugin_install script wont be executed.");
    }

    var rootGradleContent = FSUtils.readFile(ROOT_BUILD_GRADLE_FILE, "UTF-8");
    var lines = rootGradleContent.split(NEW_LINE);

    var depAddedLines = addAGConnectDependency(lines);
    FSUtils.writeFile(ROOT_BUILD_GRADLE_FILE, depAddedLines.join(NEW_LINE));
};

function addAGConnectDependency(lines) {
    var AG_CONNECT_DEPENDENCY = "classpath 'com.huawei.agconnect:agcp:1.9.0.300' " + COMMENT;
    var pattern = /(\s*)classpath(\s+)[\',\"]com.android.tools.build:gradle.*[^\]\n]/m;
    var index;

    for (var i = 0; i < lines.length; i++) {
        var line = lines[i];
        if (pattern.test(line)) {
            index = i;
            break;
        }
    }

    lines.splice(index + 1, 0, AG_CONNECT_DEPENDENCY);
    return lines;
}

