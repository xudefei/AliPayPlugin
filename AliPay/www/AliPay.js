var exec = require('cordova/exec');
var alipay = function(){};

alipay.prototype.payV2 = function(arg,success,error) {
  exec(success, error, "AliPay", "payV2", [arg]);
};

alipay.prototype.authV2 = function(arg,success,error) {
  exec(success, error, "AliPay", "authV2", [arg]);
};

alipay.prototype.getSDKVersion = function(success,error) {
  exec(success, error, "AliPay", "getSDKVersion", []);
};

alipay.prototype.h5Pay = function(success,error) {
  exec(success, error, "AliPay", "h5Pay", []);
};

var pay = new alipay();
module.exports = pay;
