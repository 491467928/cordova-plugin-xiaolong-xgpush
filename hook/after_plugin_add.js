module.exports = function(context) {
	var fs = require('fs');
	var searchFile = function (name, dir) {
		fs.readdir(dir, function (err, files) {
			if (err) {
				return console.error(err);
			} else {
				var tDir = dir;
				for (var i = 0; i < files.length; i++) {
					if (files[i] === name) {
						modifyIt(tDir + "/" + name);
					} else if (files[i].indexOf('.java') === -1) {
						searchFile(name, tDir + "/" + files[i]);
					}
				}

			}
		})
	}

	var modifyIt = function (file) {
		var ConfigParser = null;
		try {
			ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
		} catch(e) {
			// fallback
			ConfigParser = context.requireCordovaModule('cordova-lib/src/configparser/ConfigParser');
		}

		var config      = new ConfigParser(path.join(context.opts.projectRoot, "config.xml")),
			packageName = config.android_packageName() || config.packageName();

		// replace dash (-) with underscore (_)
		packageName = packageName.replace(/-/g , "_");
		var data= fs.readFileSync(file, 'utf-8');
		data = data.replace('ANDROID_ACCESS_ID', context.opts.cli_variables.ANDROID_ID);
		data = data.replace('ANDROID_ACCESS_KEY', context.opts.cli_variables.ANDROID_KEY);
		data = data.replace('HW_APPID', context.opts.cli_variables.HW_APPID);
		data = data.replace('PACKAGE_NAME', packageName);
		fs.writeFile(file, data, 'utf-8', function (err) {
			if (err) {
				return console.log("Insert android access_id and access_key error" + err);
			} else {
				return console.info('Insert android access_id and access_key success');
			}
		});
	}
	var getFileName=function () {
		var configStr=  fs.readFileSync("config.xml","utf-8");
		var tmpStr=configStr.match(/id=\s*([^;]*)/)[0];
		var strs=tmpStr.split(' ')[0].split(".");
		var str=strs[strs.length-1];
		return str.substr(0,str.length-1)+"-build-extras.gradle";
	}
	searchFile(getFileName(),'platforms/android/cordova-plugin-xiaolong-xgpush');
}
