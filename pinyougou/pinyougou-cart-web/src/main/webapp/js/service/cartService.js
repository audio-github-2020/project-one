/*
* 购物车Service
* */
app.service('cartService',function ($http) {

    //查询购物车数据
    this.findCartList=function () {
        return $http.get('/cart/list.shtml');
    }

    //加入购物车
    this.add=function (itemid,num) {
        return $http.get('/cart/add.shtml?itemid='+itemid+'&num='+num);
    }

})