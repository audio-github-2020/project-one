app.service('seckillGoodsService',function ($http) {

    //下单
    this.add=function (id) {
        // Jmeter测试版
        // return $http.get('/seckill/order/add.shtml?id='+id+'&username='+username);
        return $http.get('/seckill/order/add.shtml?id='+id);

    }

    //根据ID查询商品详情
    this.getOne=function (id) {
        return $http.get('/seckill/goods/one.shtml?id='+id);
    }

    //秒杀列表
    this.list=function () {
        return $http.get('/seckill/goods/list.shtml');
    }

})