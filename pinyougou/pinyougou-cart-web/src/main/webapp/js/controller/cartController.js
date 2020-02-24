/*
* 购物车Controller
* */
app.controller('cartController',function ($scope,cartService) {

    //查询所有购物车数据
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;

            //计算金额和总数量
            sum($scope.cartList);
        })
    }


    //总金额和总件数计算
    sum=function (cartList) {
        //定义一个参数，用于存储总数量和总价格
        $scope.totalValue={totalNum:0,totalMoney:0.0};

        for(var i=0;i<cartList.length;i++){
            //cart对象
            var cart=cartList[i];

            //获取购物车商品明细列表
            var itemlist = cart.orderItemList;

            //循环商品明细
            for(var j=0;j<itemlist.length;j++){
                //总数量
                $scope.totalValue.totalNum+=itemlist[j].num;
                //总金额
                $scope.totalValue.totalMoney+=itemlist[j].totalFee;
            }

        }

    }

    //添加购物车
    $scope.add=function (itemid,num) {
        cartService.add(itemid,num).success(function (response) {
            if(response.success){
                //刷新页面
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    }

})