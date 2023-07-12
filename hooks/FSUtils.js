"use strict";

var fs = require("fs");

var FSUtils = (function () {
    var api = {};

    api.exists = function (path) {
        try {
            return fs.existsSync(path);
        } catch (err) {
            /*NOPE*/
        }
        return false;
    };

    api.readFile = function (path, encoding) {
        return fs.readFileSync(path, encoding);
    };

    api.writeFile = function (path, content) {
        fs.writeFileSync(path, content);
    };

    return api;
})();

module.exports = FSUtils;
