/*
* 搜索Controller实现
* */
app.controller('searchController', function ($scope, searchService) {
    //定义一个数据，用以存储选择的筛选条件

    //多选就是将"keyword": ""换成"keyword": []
    $scope.searchMap = {"keyword": "", "category": "", "brand": "", spec: {},"price":""};

    //点击搜索条件时，将选中的分类记录
    $scope.addItemSearch = function (key, value) {
        if (key == "category" || key == "brand"|| key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //实现搜索
        $scope.search();
    }

    //搜索条件移除
    $scope.removeItemSearch = function (key) {
        if (key == "category" || key == "brand"|| key == "price") {
            $scope.searchMap[key] = "";
        } else {
            //将数据从Map结构中移除
            delete $scope.searchMap.spec[key];
        }

        //实现搜索
        $scope.search();
    }

    //搜索方法
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        })
    }


});

