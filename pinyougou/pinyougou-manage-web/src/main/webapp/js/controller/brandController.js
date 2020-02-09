app.controller('brandController', function ($scope,$controller, $http,brandService) {

    //让该controller继承另一个controller
    $controller('baseController',{$scope:$scope});

    //2.3）创建方法实现后台查询，返回JSON的pageinfo
    $scope.getPage = function (page, size) {

        //执行查询，获取返回结果
        brandService.findAll(page,size,$scope.searchEntity).success(function (response) {
            //获取相应数据，先接收集合数据，
            $scope.list = response.list;
            //给分页参数赋值
            $scope.paginationConf.totalItems = response.total;
        });
    };

    //创建save()方法
    $scope.save = function () {
        var result=null;
        //如果id!=null，则执行修改
        if ($scope.entity.id != null) {
            result=brandService.update($scope.entity);
        }else{
            result=brandService.add($scope.entity);
        }
        //发送请求，第一个是url，第二个表示提交的数据
        result.success(function (response) {
            if (response.success) {
                //增加成功，刷新页面即可
                $scope.getPage(1, 10);
            } else {
                //增加失败
                alert(response.message);
            }
        });
    };
    //根据id查询id:品牌ID
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    //删除方法
    $scope.delete = function () {
        var url = '/brand/delete.shtml';
        brandService.delete($scope.selectIds).success(function (response) {
            if (response.success) {
                //删除成功，刷新页面即可
                $scope.getPage(1, 10);
            } else {
                //删除失败
                alert(response.message);
            }
        });
    };



});