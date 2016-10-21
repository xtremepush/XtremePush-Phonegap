#!/usr/bin/env node
 
var fs = require('fs');
var xml2js = require('xml2js');

var beacon = true;
var geo = true;
var attrib = false;

function getParamsFromXmlFile() {
    var parser = new xml2js.Parser();
    fs.readFile('platforms/android/xp-config.xml', function(err, data) {
        parser.parseString(data, function (err, result) {
            console.dir(result);
        });
    });
}

function replaceStringInFile(filename, to_replace, replace_with) {
    if (fs.existsSync(filename)) {
        var data = fs.readFileSync(filename, 'utf8');
        var result = data.replace(new RegExp(to_replace, "g"), replace_with);
        fs.writeFileSync(filename, result, 'utf8');
    } else {
        console.log("File missing: " + filename);
    }
}

getParamsFromXmlFile();

var gradleExtrasPath = "platforms/android/com.xtreme.plugins.XtremePush/cordova-xtremepush_lib/build-extras.gradle";

// Add geo library
replaceStringInFile(gradleExtrasPath, 
    "/\\*XP_DEP_AUTO\\*/", 
    "/\\*XP_DEP_AUTO\\*/\n    compile 'com.google.android.gms:play-services-location:+'");
// Todo add geo permissions and classes to manifest 

// Add ads library
replaceStringInFile(gradleExtrasPath, 
    "/\\*XP_DEP_AUTO\\*/", 
    "/\\*XP_DEP_AUTO\\*/\n    compile 'com.google.android.gms:play-services-ads:+'");
// Todo add attributions permissions and classes to manifest 

console.log("Configuration complete!");