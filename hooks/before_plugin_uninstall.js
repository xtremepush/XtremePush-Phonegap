"use strict";

var FSUtils = require("./FSUtils");

var ROOT_GRADLE_FILE = "platforms/android/build.gradle";
var COMMENT = "//xtremepush HUAWEI plugin dep";
var NEW_LINE = "\n";

module.exports = function (context) {
    removeLinesFromGradle(ROOT_GRADLE_FILE);
};

function removeLinesFromGradle(repositoryPath) {
    if (!FSUtils.exists(repositoryPath)) {
        return;
    }

    var rootGradleContent = FSUtils.readFile(repositoryPath, "UTF-8");
    var lines = rootGradleContent.split(NEW_LINE);
    var linesAfterRemove = removeLinesAddedByPlugin(lines);
    FSUtils.writeFile(repositoryPath, linesAfterRemove.join(NEW_LINE));
}

function removeLinesAddedByPlugin(lines) {
    var indexList = [];
    for (var i = 0; i < lines.length; i++) {
        var line = lines[i];
        if (line.includes(COMMENT)) {
            indexList.push(i);
        }
    }

    for (var i = 0; i < indexList.length; i++) {
        lines.splice(indexList[i], 1);

        //if a line is removed, indexes are changed
        if (i !== indexList.length - 1) {
            for (var j = i + 1; j < indexList.length; j++) {
                indexList[j] = indexList[j] - 1;
            }
        }
    }

    return lines;
}
