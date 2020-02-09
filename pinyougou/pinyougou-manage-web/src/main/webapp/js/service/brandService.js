app.service('brandService', function ($http) {
    //分页查询所有
    this.findAll = function (page, size, searchEntity) {
        //执行查询，获取返回结果
        return $http.post('/brand/list.shtml?page=' + page + '&size=' + size, searchEntity);
    }
    //增加
    this.add = function (entity) {
        return $http.post('/brand/add.shtml', entity);
    }
    //修改
    this.update = function (entity) {
        return $http.post('/brand/modify.shtml', entity);
    }
    //删除
    this.delete = function (ids) {
        return $http.post('/brand/delete.shtml', ids);
    }

    //根据id查询
    this.findOne = function (id) {
        return $http.get('/brand' + id + '.shtml');
    }
});