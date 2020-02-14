/*****
 * 定义一个控制层 controller
 * 发送HTTP请求从后台获取数据
 ****/
app.controller("goodsEditController", function ($scope, $http, $controller, $location,goodsService, uploadService, itemCatService, typeTemplateService) {

    //继承父控制器
    $controller("baseController", {$scope: $scope});


    //定义状态集合     0         1          2         3
    $scope.status=["未审核","审核通过","审核不通过","关闭"];

    //商品分类集合,以map形式储存
    $scope.itemCatShowList=[];

    $scope.getItemShowList=function(){
        itemCatService.findAllList().success(function (response) {
            //迭代集合，组装数据
            for(var i=0;i<response.length;i++){
                var key = response[i].id;
                var value=response[i].name;
                $scope.itemCatShowList[key]=value;
            }
        })
    }

    //定义一个数组用于存储所有上传的文件
    //$scope.entity.goodsDesc.itemImages=[];
    //$scope.entity.goodsDesc={itemImages:[]};
    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    //其实每次就是把上次组合的商品集合数据传入，然后与当前规格名字和选项组合即可
    addColumn = function (itemsList, attributeValue, attributeName) {
        var items = [];
        for (var i = 0; i < itemsList.length; i++) {
            //循环跟上一次重组的商品结果集再次重组
            for (var j = 0; j < attributeValue.length; j++) {
                var newItem = angular.copy(itemsList[i]);
                newItem.spec[attributeName] = attributeValue[j];
                items.push(newItem);
            }
        }
        return items;
    }

    //SKU重组
    $scope.createItems = function () {
        var item = {"price": 0, "num": 0, "status": 1, "isDefault": "0", spec: {}};
        $scope.entity.items = [item];
        //循环规格，挨个重组
        for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i++) {
            var attributeValue = $scope.entity.goodsDesc.specificationItems[i].attributeValue;
            var attributeName = $scope.entity.goodsDesc.specificationItems[i].attributeName;
            $scope.entity.items = addColumn($scope.entity.items, attributeValue, attributeName);
        }
    }


    //SKU重组
    $scope.createItemsbk001 = function () {
        var item = {"price": 0, "num": 0, "status": 1, "isDefault": "1", spec: {}};
        var items0 = [item];

        //选中第1行的规格
        var items1 = [];
        if ($scope.entity.goodsDesc.specificationItems.length >= 1) {
            var attributeValue = $scope.entity.goodsDesc.specificationItems[0].attributeValue;
            var attributeName = $scope.entity.goodsDesc.specificationItems[0].attributeName;
            items1 = addColumn(items0, attributeValue, attributeName);
        }

        //选中第2行的规格
        var items2 = [];
        if ($scope.entity.goodsDesc.specificationItems.length >= 2) {
            var attributeValue = $scope.entity.goodsDesc.specificationItems[1].attributeValue;
            var attributeName = $scope.entity.goodsDesc.specificationItems[1].attributeName;
            items2 = addColumn(items1, attributeValue, attributeName);
        }

        //选中第3行的规格
        var items3 = [];
        if ($scope.entity.goodsDesc.specificationItems.length >= 3) {
            var attributeValue = $scope.entity.goodsDesc.specificationItems[2].attributeValue;
            var attributeName = $scope.entity.goodsDesc.specificationItems[2].attributeName;
            items3 = addColumn(items2, attributeValue, attributeName);
        }
    }


    //存储选中的规格数据
    // [
    //     {"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
    //     {"attributeName":"屏幕尺寸","attributeValue":["5.5寸","5寸"]}
    // ]
    $scope.updateSpecAttribute = function ($event, attributeName, attributeValue) {
        //判断当前规格名字，在集合中是否存在，比如attributeName传入参数为屏幕尺寸
        var result = searchObjectByKey($scope.entity.goodsDesc.specificationItems, attributeName);
        //如果存在，则直接将该对象返回过来
        //将勾选中的规格选项attributeValue加入到result的attributeValue属性中
        if (result != null) {
            //先判断是否是勾选
            if ($event.target.checked) {
                //规格选项加入属性
                result.attributeValue.push(attributeValue);
            } else {
                //规格选项移出属性
                var valueIndex = result.attributeValue.indexOf(attributeValue);
                result.attributeValue.splice(valueIndex, 1);
                //如果规格选项长度为零，直接移除该规格
                if (result.attributeValue.length <= 0) {
                    var nameIndex = $scope.entity.goodsDesc.specificationItems.indexOf(result);
                    $scope.entity.goodsDesc.specificationItems.splice(nameIndex, 1);
                }
            }


        } else {
            //如果不存在，就构建一条数据加入集合
            var newSpec = {"attributeName": attributeName, "attributeValue": [attributeValue]};
            $scope.entity.goodsDesc.specificationItems.push(newSpec);
        }

    }
    /**
     *
     * @param list=specificationItems:[]
     * @param attributeName
     */
    searchObjectByKey = function (list, attributeName) {
        for (var i = 0; i < list.length; i++) {
            if (list[i]["attributeName"] == attributeName) {
                return list[i];
            }
        }
    }

    //查询第一级分类
    $scope.findItemCat1List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat1List = response;
        });
        //清空二级、三级分类
        $scope.itemCat2List = null;
        $scope.itemCat3List = null;
        $scope.entity.typeTemplateId = null;
    }
    //查询第二级分类
    $scope.findItemCat2List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat2List = response;
        });
        //清空三级分类
        $scope.itemCat3List = null;
        $scope.entity.typeTemplateId = null;
    }
    //查询第三级分类
    $scope.findItemCat3List = function (id) {
        itemCatService.findByParentId(id).success(function (response) {
            $scope.itemCat3List = response;
        });
    }

    //第三级发生变化，根据id查询分类信息
    $scope.getTypeId = function (id) {
        itemCatService.findOne(id).success(function (response) {
            $scope.entity.typeTemplateId = response.typeId;
        });
    }

    //监控entity.typeTemplateId的变化
    //newValue:被监控数据变化前
    //oldValue:被监控数据变化后
    $scope.$watch('entity.typeTemplateId', function (newValue, oldValue) {
        /**
         * isNaN(newValue)==true,newValue是非数字类型
         * isNaN(newValue)==false,newValue是数字类型
         */
        if (!isNaN(newValue)) {
            typeTemplateService.findOne(newValue).success(function (response) {
                //获取品牌信息
                //不太好的写法
                //$scope.brandList=JSON.parse(response.brandIds);

                //较好的写法
                $scope.brandList = angular.fromJson(response.brandIds);

                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems = angular.fromJson(response.customAttributeItems);

                //获取规格选项
                // $scope.specList=angular.fromJson(response.specIds);

                //获取规格选项
                typeTemplateService.getOptionByTypeId($scope.entity.typeTemplateId).success(function (response) {
                    $scope.specList = response;
                })
            });
        }
    });


    //移除一张图片
    $scope.remove_image_entity = function (index) {
        //$scope.imagslist.splice(index,1);
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //往集合中添加一张图片
    $scope.add_image_entity = function () {
        //$scope.imagslist.push($scope.image_entity);
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //文件上传
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                //获取文件上传后的回显url
                $scope.image_entity.url = response.message;
            }
        });
    }

    //获取所有的Goods信息
    $scope.getPage = function (page, size) {
        //发送请求获取数据
        goodsService.findAll(page, size, $scope.searchEntity).success(function (response) {
            //集合数据
            $scope.list = response.list;
            //分页数据
            $scope.paginationConf.totalItems = response.total;
        });
    }

    //添加或者修改方法
    $scope.save = function () {
        //文本编辑器对象.html()表示获取文本编辑器内容
        $scope.entity.goodsDesc.introduction = editor.html();
        var result = null;
        if ($scope.entity.id != null) {
            //执行修改数据
            result = goodsService.update($scope.entity);
        } else {
            //增加操作
            result = goodsService.add($scope.entity);
        }
        //判断操作流程
        result.success(function (response) {
            //判断执行状态
            if (response.success) {
                //重新加载新的数据
                //$scope.reloadList();
                //alert("恭喜你增加成功！")
                //$scope.entity = {};
                //文本编辑器内容赋值  文本编辑器对象.html("");
                //editor.html("");

                //跳转到列表页
                location.href="/admin/goods.html";

            } else {
                //打印错误消息
                alert("增加失败");
            }
        });
    }

    //根据ID查询信息
    $scope.getById = function (id) {
        goodsService.findOne(id).success(function (response) {
            //将后台的数据绑定到前台
            $scope.entity = response;
        });
    }

    //获取url参数
    $scope.getUrlParam=function(){
        //获取id变量
        var id=$location.search()['id'];
        if(id!=null){
            $scope.getById(id);
        }
    }

    //批量删除
    $scope.delete = function () {
        goodsService.delete($scope.selectids).success(function (response) {
            //判断删除状态
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }
});
