var exec = require('cordova/exec');

exports.bindAccount = function (account, success, error) {
    exec(success, error, 'XGPush', 'bindAccount', [account]);
};
exports.unBindAccount = function (account, success, error) {
	exec(success, error, 'XGPush', 'unBindAccount', [account]);
};
exports.listenNotifyClick = function (success, error) {
	exec(success, error, 'XGPush', 'listenNotifyClick', []);
};


