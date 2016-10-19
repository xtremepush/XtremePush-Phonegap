#!/usr/bin/env node
 
var fs = require('fs');
var xml2js = require('xml2js');

var beacon = true;
var geo = true;
var attrib = false;

function checkParameters() {
    if (process.env.XP_BEACON) {
        if (process.env.XP_BEACON == 0) {
            beacon = false;
        }
    } else {
        if (getParamFromFile("XP_BEACON") == 0)
            beacon = false
    }

    if (process.env.XP_GEO) {
        if (process.env.XP_GEO == 0) {
            geo = false;
        }
    } else {
        if (getParamFromFile("XP_GEO") == 0)
            geo = false
    }

    if (process.env.XP_ATTRIB) {
        if (process.env.XP_ATTRIB == 1) {
            attrib = true;
        }
    } else {
        if (getParamFromFile("XP_BEACON") == 1)
            attrib = true
    }
}

function getParamFromFile(keyName) {
    // var configobj = JSON.parse(fs.readFileSync(ourconfigfile, 'utf8'));
}

function getParamFromXmlFile(keyName) {
    var parser = new xml2js.Parser();
    fs.readFile(__dirname + '/mk.xml', function(err, data) {
        parser.parseString(data, function (err, result) {
            console.dir(result);
            console.log('Done');
        });
    });
    // var xml = '<?xml version="1.0" encoding="UTF-8" ?><business><company>Code Blog</company><owner>Nic Raboy</owner><employee><firstname>Nic</firstname><lastname>Raboy</lastname></employee><employee><firstname>Maria</firstname><lastname>Campos</lastname></employee></business>';
    // parseString(xml, function (err, result) {
    //     console.dir(JSON.stringify(result));
    // });
}

function replaceStringInFile(filename, to_replace, replace_with) {
    if (fs.existsSync(filename)) {
        var data = fs.readFileSync(filename, 'utf8');
        var result = data.replace(new RegExp(to_replace, "g"), replace_with);
        fs.writeFileSync(filename, result, 'utf8');
        console.log("Done: " + filename);
    } else {
        console.log("File missing: " + filename);
    }
}

getParamFromXmlFile("");
var gradleExtrasPath = "platforms/android/com.xtreme.plugins.XtremePush/cordova-xtremepush_lib/build-extras.gradle";
replaceStringInFile(gradleExtrasPath, 
    "/\\*DEP_AUTO\\*/", 
    "/\\*DEP_AUTO\\*/\n    compile 'com.google.android.gms:play-services-location:+'");