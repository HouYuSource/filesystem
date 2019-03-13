$(function () {
    //初始化Table
    var oTable = new TableInit();
    oTable.Init();
});

var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#table').bootstrapTable({
            url: ctx + '/file/page', //请求后台的URL（*）
            method: 'get', //请求方式（*）
            toolbar: '#toolbar', //工具按钮用哪个容器
            striped: true, //是否显示行间隔色
            cache: false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true, //是否显示分页（*）
            sortable: false, //是否启用排序
            sortOrder: "asc", //排序方式
            queryParams: oTableInit.queryParams, //传递参数（*）
            sidePagination: "server", //分页方式：client客户端分页，server服务端分页（*）
            pageNumber: 1, //初始化加载第一页，默认第一页
            pageSize: 5, //每页的记录行数（*）
            pageList: [5, 7, 10, 15], //可供选择的每页的行数（*）
            search: true, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            contentType: "application/x-www-form-urlencoded",
            strictSearch: true,
            showColumns: true, //是否显示所有的列
            showRefresh: true, //是否显示刷新按钮
            minimumCountColumns: 2, //最少允许的列数
            // clickToSelect: true, //是否启用点击选中行
            //height: 700,//行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: "name", //每一行的唯一标识，一般为主键列
            showToggle: false, //是否显示详细视图和列表视图的切换按钮
            cardView: false, //是否显示详细视图
            detailView: false, //是否显示父子表
            responseHandler: function (res) {
                return {
                    "total": res.data.totalElements,//总页数
                    "rows": res.data.content   //数据
                };
            },
            columns: [{
                align: 'center',
                checkbox: true, // 显示复选框
            }, {
                field: 'id',
                title: 'ID',
            }, {
                field: 'name',
                title: '文件名称'
            // }, {
            //     field: 'mapping',
            //     title: '匹配名称'
            }, {
                field: 'size',
                title: '文件大小',
                formatter: bytesToSize
            }, {
                field: 'type',
                title: '文件类型'

            }, {
                field: 'date',
                title: '上传日期',
                formatter: operateFormatter
            }, {
                // field: '操作',
                title: '操作',
                formatter: handle
            },
            ],
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            pageSize: params.limit, //页面大小
            pageIndex: (params.offset / params.limit), //页码
            name: params.search
        };
        return temp;
    };

    function operateFormatter(value, row, index) {//赋予的参数
        var date = formatData(row.date)
        return date
    }

    function handle(value, row, index) {//赋予的参数
        var handleString = '<div style="min-width: 70px"><a href="'+ row.mapping +'" target="_blank" style="margin-right: 10px;"><span class="glyphicon glyphicon-eye-open"></span></a>' +
            '<a href="'+ row.mapping.replace('view', 'download') +'" target="_blank"  style="margin-right: 10px"><span class="glyphicon glyphicon-cloud-download"></span></a>' +
            '<a href="javascript:void(0);" onclick="deleteByName(\''+ row.name +'\')"><span class="glyphicon glyphicon-trash"></span></a></div>'
        return handleString
    }

    function bytesToSize(value, row, index) {
        var bytes = value
        if (bytes === 0) return '0 B';
        var k = 1024, sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'], i = Math.floor(Math.log(bytes) / Math.log(k));
        return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }

    return oTableInit;
};

function deleteByName(name) {
    $.ajax({
        url: ctx + "/file/delete/" + name,
        type: "delete",
        success: function (r) {
            if (r.status == 200) {
                reLoad();
            } else {
                confirm(r.msg);
            }
        }
    });
}

/*删除多条信息*/
function removeMuch() {
    var rows = $('#table').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
    if (rows.length == 0) {
        confirm("请选择要删除的数据");
        return;
    }
    var r = confirm("确认要删除选中的'" + rows.length + "'条数据吗?");
    if (r == true) {
        var ids = new Array();
        // 遍历所有选择的行数据，取每条数据对应的ID
        $.each(rows, function (i, row) {
            ids[i] = row['name'];
        });
        var data = {
            "names": ids
        }

        $.ajax({
            url: ctx + "/file/delete",
            type: "POST",
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: 'application/json',
            success: function (r) {
                if (r.status == 200) {
                    reLoad();
                } else {
                    confirm(r.msg);
                }
            }
        });
    }
}

/*刷新table*/
function reLoad() {
    $('#table').bootstrapTable('refresh');
}

/*上传文件*/
$(function () {
    $("#saveEAP").click(function () {
        var fileObj = document.getElementById("FileUpload").files[0]; // js 获取文件对象
        if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
            alert("请选择文件");
            return;
        }
        var formFile = new FormData();
        formFile.append("action", "UploadVMKImagePath");
        formFile.append("file", fileObj); //加入文件对象
        var data = formFile;
        $.ajax({
            url: ctx + "/file/upload",
            data: data,
            type: "Post",
            dataType: "json",
            cache: false, //上传文件无需缓存
            processData: false, //用于对data参数进行序列化处理 这里必须false
            contentType: false, //必须

            success: function (data) {
                if (data.status == 200) {
                    $('#modal_user').modal('hide');
                    reLoad();
                } else {
                    confirm(data.msg);
                }
            },
            error: function (data) {
                confirm('上传失败：' + JSON.stringify(data));
            },
        });

    });
});

//打开模态框
function openModal() {
    $('#modal_user').modal({
        backdrop: "static"
    });
}

/**
 * js时间戳转为特定时间
 */
function formatData(timeMillis) {
    // var date = new Date(parseInt(timeMillis));
    var date = new Date(timeMillis);
    var y = date.getFullYear().toString();
    var m = (date.getMonth() + 1).toString();
    var d = date.getDate().toString();
    var h = date.getHours().toString();
    var mm = date.getMinutes().toString();
    var s = date.getSeconds().toString();
    return y + '-' + (m.length == 1 ? '0' + m : m) + '-' + (d.length == 1 ? '0' + d : d) + ' ' + (h.length == 1 ? '0' + h : h) + ':' + (mm.length == 1 ? '0' + mm : mm) + ':' + (s.length == 1 ? '0' + s : s)
}