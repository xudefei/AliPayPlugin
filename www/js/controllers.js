angular.module('starter.controllers', [])

  .controller('DashCtrl', function ($scope) {
    var success = function (response) {
      var json = JSON.parse(response);
      alert(json);
    };

    var error = function (response) {
      var json = JSON.parse(response);
      alert(json);
    };

    $scope.testAlipay = function () {
      alipay.payV2("app_id=2016072900115009&timestamp=2016-12-02+16%3A55%3A53&biz_content=%7B%22timeout_express%22%3A%2230m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%220.01%22%2C%22subject%22%3A%221%22%2C%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22out_trade_no%22%3A%22121511134515401%22%7D&method=alipay.trade.app.pay&charset=utf-8&version=1.0&sign_type=RSA&sign=pCNuPrQlfBVbe2GZPS1ikpDS613utsAdULTVXfzmx2ymS4ZvBqufuT15LV2OL1Rk9HWcIdJZDJxm30rWP4Jt0aaCYOYGhQ6%2Bcl6l3TdBoio6qDmheOczCfQ4hDAy9SoYb3ZpPC94BHsOJx4sEdfCKKZJlZdcb3lP0OVCx0to6%2Fc%3D",
        function (resultStatus, result) {
          // 成功
          alert(result);
        }, function (resultStatus, result) {
          // 错误
          alert(result);
        });
    }
  })

  .controller('ChatsCtrl', function ($scope, Chats) {
    // With the new view caching in Ionic, Controllers are only called
    // when they are recreated or on app start, instead of every page change.
    // To listen for when this page is active (for example, to refresh data),
    // listen for the $ionicView.enter event:
    //
    //$scope.$on('$ionicView.enter', function(e) {
    //});

    $scope.chats = Chats.all();
    $scope.remove = function (chat) {
      Chats.remove(chat);
    };
  })

  .controller('ChatDetailCtrl', function ($scope, $stateParams, Chats) {
    $scope.chat = Chats.get($stateParams.chatId);
  })

  .controller('AccountCtrl', function ($scope) {
    $scope.settings = {
      enableFriends: true
    };
  });
