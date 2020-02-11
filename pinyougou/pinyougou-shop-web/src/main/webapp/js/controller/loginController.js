/*
* 登录Controller
* */
app.controller('loginController', function ($scope, loginService) {


    //实现登录
    $scope.login = function () {
        $scope.msg = "正在登录....";
        //$scope.username,$scope.password
        loginService.login($scope.username, $scope.password).success(function (response) {
            //success   message
            if (response.success) {
                //登陆成功跳转到后台主页
                //location.href='/admin/index.html';
                location.href = response.message;
            } else {
                //登录失败
                //alert(response.message);
                $scope.msg = response.message;
            }
        });
    }
    //获取用户名信息
    $scope.showName=function(){
        //发送请求获取数据
        loginService.showName().success(function(response){
            //数据
            $scope.userName = response.userName;
        });
    }
});