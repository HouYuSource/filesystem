<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>file</title>
    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- JQ -->
    <#--<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>-->
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>

    <script src="https://unpkg.com/bootstrap-table@1.14.1/dist/bootstrap-table.min.js"></script>

    <script type="text/javascript">
        var ctx = '${ctx!''}';
    </script>

    <script src="${ctx}/static/js/file/index.js" type="text/javascript" charset="utf-8"></script>
</head>
<style type="text/css">
    body {
        background-image: url(${ctx}/static/images/background.png);
        background-attachment: fixed;
    }

    .container h1 {
        text-align: center;
        color: white;
    }
    .row h2{
        float: left;
    }
    table th,
    td {
        text-align: center;
    }

    table {
        background: white;
    }

    .pull-left.pagination-detail {
        color: #FFFF;
        font-size: 18px;
    }
    .row {
        height: 80px;
        line-height: 60px;
        color: white;
    }
    .row h2 {
        margin-left: 15px;
    }

    .row div {
        float: right;
        margin-right: 15px;
        padding-top: 20px;
    }
    .columns.columns-right.btn-group.pull-right {
        margin-bottom: 10px;
    }
</style>

<body>
<div class="container" style="opacity:0.75;">

    <div class="starter-template">
        <h1 style="cursor:pointer;" onclick="javascrtpt:window.location.href='${ctx!''}/log'">文件管理系统</h1><span style="color: #CCC;">powered by houyu [ shaines.cn ]</span>
        <div class="row">
            <h2>
                文件列表
            </h2>
            <div>
                <#--或者使用data-target="#editRow"-->
                <button type="button" id="addModel" onclick="openModal();" class="btn btn-info">上传</button>&nbsp;&nbsp;&nbsp;
                <button type="button" onclick="removeMuch();" id="delete" class="btn btn-danger">删除</button>
            </div>
        </div>
        <table id="table" class="table table-bordered table-hover">
        </table>
    </div>

</div>
<!-- /.container -->
<div class="modal fade" id="modal_user" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content" style="margin-top: 30%;">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">上传</h4>
            </div>
            <div class="modal-body modal-li">
                <form id="selectEAP">
                    <ul>
                        <li>
                            <label for="addExpertName">上传文件:</label>
                            <input name="file" id="FileUpload" type="file" class="form-control">
                        </li>
                    </ul>
                </form>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                    </button>
                    <button type="button" class="btn btn-primary" id="saveEAP">保存</button>
                </div>
            </div>

        </div>
    </div>
</div>
</body>

</html>