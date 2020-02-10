/*****
 * 定义一个控制层 controller
 ****/
app.controller("indexController",function($scope,loginService){
    //获取所有的Address信息
    $scope.showName=function(page,size){
        //发送请求获取数据
        loginService.showName().success(function(response){
            //数据
            $scope.loginName = response.loginName;

        });
    }


});
