<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>日志记录</title>
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

    <script src="${ctx}/static/js/log/index.js" type="text/javascript" charset="utf-8"></script>
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
        <h1 style="cursor:pointer;" onclick="javascrtpt:window.location.href='${ctx!''}/file'">文件管理系统</h1><span style="color: #CCC;">power by houyu [ shaines.cn ]</span>
        <div class="row">
            <h2>
                日志列表
            </h2>
        </div>
        <table id="table" class="table table-bordered table-hover">
        </table>
    </div>

</div>
</body>

</html>