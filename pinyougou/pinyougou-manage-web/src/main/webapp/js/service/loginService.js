/***
 * 创建一个服务层
 * */
app.service("loginService",function($http){

    //查询列表
    this.showName=function(){
        return $http.get("/login/name.shtml");
    }

});
