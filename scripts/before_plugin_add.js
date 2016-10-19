#!/usr/bin/env node
 
var fs = require('fs');

function createXmlFile() {
    var filename = 'platforms/android/xp-config.xml';
    if (!fs.existsSync(filename)) {
        var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n<config>\n</config>';
        fs.writeFileSync(filename, xml, 'utf8');
        console.log("Created XML Config File");
    }
}

createXmlFile("");